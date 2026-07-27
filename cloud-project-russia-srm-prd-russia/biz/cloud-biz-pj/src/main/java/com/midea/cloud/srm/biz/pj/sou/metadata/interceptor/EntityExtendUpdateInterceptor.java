package com.midea.cloud.srm.biz.pj.sou.metadata.interceptor;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.biz.pj.sou.metadata.config.MetadataProperties;
import com.midea.cloud.srm.biz.pj.sou.metadata.constants.MetadataKey;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.ConditionType;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.JoinType;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.EntityExtendDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataDataDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.result.EntityExtendResult;
import com.midea.cloud.srm.biz.pj.sou.metadata.strategy.entityextend.EntityExtendDataHandler;
import com.midea.cloud.srm.biz.pj.sou.metadata.utils.EntityUtil;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.mideacloud.common.id.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/7/21 19:30
 *  修改内容:
 * </pre>
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
@Slf4j
public class EntityExtendUpdateInterceptor extends AbstractEntityExtendInterceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (Boolean.FALSE.equals(SpringContextHolder.getBean(MetadataProperties.class).getEnableExtend())) {
            return invocation.proceed();
        }
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        Class<?> entityClass = getEntityClass(ms);
        List<Object> rows = getListParam(invocation.getArgs()[1]);
        EntityExtendResult<Long> entityExtendResult = null;
        if (!rows.isEmpty() && null != rows.get(0) && null != entityClass) {
            EntityExtendDataHandler dataHandler = SpringContextHolder.getBean(EntityExtendDataHandler.class);
            /*从元数据配置中获取扩展字段配置 */
            EntityExtendDTO extendDto = dataHandler.getExtendDto(entityClass);
            if (null != extendDto) {
                MetadataDataDTO dataDto = buildExtendDataDto(extendDto, rows, ms.getSqlCommandType());
                if (SqlCommandType.INSERT.equals(ms.getSqlCommandType()) && !dataDto.getDetails().isEmpty()) {
                    entityExtendResult = dataHandler.insert(extendDto, dataDto);
                } else if (SqlCommandType.UPDATE.equals(ms.getSqlCommandType()) && !dataDto.getDetails().isEmpty()) {
                    entityExtendResult = dataHandler.update(extendDto, dataDto);
                } else if (SqlCommandType.DELETE.equals(ms.getSqlCommandType()) && CollectionUtils.isNotEmpty(dataDto.getConditions())) {
                    entityExtendResult = dataHandler.delete(extendDto, dataDto);
                }
            }
        }
        if (null != entityExtendResult && !entityExtendResult.getProcessOri()) {
            if (null == entityExtendResult.getData()) {
                return 0;
            }
            return entityExtendResult.getData().intValue();
        }
        return invocation.proceed();
    }

    private MetadataDataDTO buildExtendDataDto(EntityExtendDTO extendDto, List<Object> rows, SqlCommandType sqlCommandType) {
        MetadataDataDTO dataDto = new MetadataDataDTO();
        dataDto.setTableName(extendDto.getExtendTableName());
        for (Object row : rows) {
            Object id = getEntityId(row, extendDto.getEntityIdAttr());
            /*如果是insert模式，id为空，则插入id */
            if(null == id && SqlCommandType.INSERT.equals(sqlCommandType)){
                id = IdGenerator.generate();
                Field idField = EntityUtil.getExtendAttr(row.getClass(), extendDto.getEntityIdAttr());
                ReflectionUtils.makeAccessible(idField);
                ReflectionUtils.setField(idField, row, id);
            }
            if (null != id) {
                if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                    MetadataDataDTO rowDto = getExtend(extendDto, row, id);
                    if (null != rowDto
//                    扩展表与实体表ID关联
                            && rowDto.get(extendDto.getExtendReferenceAttr()).equals(id)
                    ) {
                        dataDto.addDetail(rowDto);
                    }
                } else if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
                    MetadataDataDTO rowDto = getExtend(extendDto, row, id);
                    if (null != rowDto
//                    扩展表与实体表ID关联
                            && rowDto.get(extendDto.getExtendReferenceAttr()).equals(id)
//                    扩展表ID不为空
                            && null != rowDto.get(extendDto.getExtendIdAttr())
                    ) {
                        dataDto.addDetail(rowDto);
                    }
                } else if (SqlCommandType.DELETE.equals(sqlCommandType)) {
                    /*根据主表ID删除 */
                    dataDto.addCondition(extendDto.getExtendReferenceAttr(), ConditionType.EQ, id, JoinType.OR);
                }
            }
        }
        return dataDto;
    }


    private List<Object> getListParam(Object obj) {
        List<Object> rows = new ArrayList<>();
        if (obj instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap<Object> paramMap = (MapperMethod.ParamMap<Object>) obj;
            if (paramMap.containsKey(Constants.ENTITY) && null != paramMap.get(Constants.ENTITY)) {
                rows.add(paramMap.get(Constants.ENTITY));
            }
        } else if (obj instanceof BaseEntity) {
            rows.add(obj);
        } else if (obj instanceof Long || obj instanceof String) {
            rows.add(obj);
        }
        return rows;
    }

    private MetadataDataDTO getExtend(EntityExtendDTO extendDto, Object row, Object entityId) {
        try {
            Field extendField = EntityUtil.getExtendAttr(row.getClass(), extendDto.getExtendAttr());
            ReflectionUtils.makeAccessible(extendField);
            Map extend = (Map) ReflectionUtils.getField(extendField, row);
            MetadataDataDTO rowDto = new MetadataDataDTO();
            rowDto.put(MetadataKey.KEY_ORI_OBJECT, JSON.parseObject(JSONUtil.toJsonStr(row)));
            rowDto.put(extendDto.getExtendReferenceAttr(), entityId);
            if (null != extend && !extend.isEmpty()) {
                rowDto.putAll(extend);
            }
            return rowDto;
        } catch (Exception e) {
            /*没有扩展属性，返回null */
        }
        return null;
    }

    private Class getEntityClass(MappedStatement ms) throws ClassNotFoundException {
        String className = ms.getId().substring(0, ms.getId().lastIndexOf("."));
        Type[] mapperTypes = Class.forName(className).getGenericInterfaces();
        if (mapperTypes.length <= 0) {
            return null;
        }
        ParameterizedType paramType = (ParameterizedType) mapperTypes[0];
        Type[] actualParamTypes = paramType.getActualTypeArguments();
        if (actualParamTypes.length <= 0) {
            return null;
        }
        return (Class) actualParamTypes[0];
    }
}
