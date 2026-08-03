package com.midea.cloud.srm.biz.pj.sou.metadata.interceptor;

import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.biz.pj.sou.metadata.config.MetadataProperties;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.EntityExtendDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.result.EntityExtendResult;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.vo.MetadataDataVO;
import com.midea.cloud.srm.biz.pj.sou.metadata.strategy.entityextend.EntityExtendDataHandler;
import com.midea.cloud.srm.biz.pj.sou.metadata.utils.EntityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

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
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
@Slf4j
public class EntityExtendQueryInterceptor extends AbstractEntityExtendInterceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //执行查询
        Object obj = invocation.proceed();
        if (Boolean.FALSE.equals(SpringContextHolder.getBean(MetadataProperties.class).getEnableExtend())) {
            return obj;
        }
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        if (!SqlCommandType.SELECT.equals(ms.getSqlCommandType())) {
            return obj;
        }
        if (obj instanceof List && CollectionUtils.isNotEmpty((List<?>) obj)) {
            EntityExtendDataHandler dataHandler = SpringContextHolder.getBean(EntityExtendDataHandler.class);
            List<?> rows = (List<?>) obj;
            Object firstRow = rows.get(0);
            if (null == firstRow) {
                return obj;
            }
            Class<?> entityClass = firstRow.getClass();
            //从元数据配置中获取扩展字段配置
            EntityExtendDTO extendDto = dataHandler.getExtendDto(entityClass);
            if (null != extendDto) {
                List<Object> entityIds = rows.stream().map((Object row) -> getEntityId(row, extendDto.getEntityIdAttr())).collect(Collectors.toList());
                //去除空值
                entityIds.remove(null);
                EntityExtendResult<List<MetadataDataVO>> result = dataHandler.query(extendDto, entityIds);
                List<MetadataDataVO> extendData = result.getData();
                if (CollectionUtils.isNotEmpty(extendData)) {
                    for (Object row : rows) {
                        setExtend(extendDto, row, extendData);
                    }
                }
            }
        }
        return obj;
    }

    private void setExtend(EntityExtendDTO extendDto, Object row, List<MetadataDataVO> extendVos) {
        Object id = getEntityId(row, extendDto.getEntityIdAttr());
        if (null != id) {
            MetadataDataVO extendVo = extendVos.stream().filter(d -> id.equals(d.get(extendDto.getExtendReferenceAttr()))).findFirst().orElse(null);
            if (null != extendVo) {
                try {
                    extendVo.remove(extendDto.getExtendReferenceAttr());
                    Field extendField = EntityUtil.getExtendAttr(row.getClass(), extendDto.getExtendAttr());
                    ReflectionUtils.makeAccessible(extendField);
                    ReflectionUtils.setField(extendField, row, extendVo.buildExtensionMap());
                } catch (Exception e) {
                }
            }
        }
    }
}
