package com.midea.cloud.srm.biz.pj.sou.metadata.strategy.entityextend;

import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.EntityExtendDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataDataDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.result.EntityExtendResult;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.vo.MetadataDataVO;
import com.midea.cloud.srm.biz.pj.sou.metadata.utils.EntityUtil;
import com.midea.cloud.srm.model.annonations.EntityExtend;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.ExtendHandlerType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 扩展数据操作策略
 * </pre>
 *
 * @author huangyq154@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/9/5 13:50
 *  修改内容:
 * </pre>
 */
@Component
@Slf4j
public class EntityExtendDataHandler {

    private Map<ExtendHandlerType, String> strategies;

    public EntityExtendDataHandler() {
        this.strategies = new HashMap<>(50);
        this.strategies.put(ExtendHandlerType.SRM, "srmEntityExtendDataStrategy");
    }

    public EntityExtendResult<List<MetadataDataVO>> query(EntityExtendDTO extendDto, List<Object> entityIds) {
        EntityExtendDataStrategy strategy = SpringContextHolder.getBean(strategies.get(extendDto.getExtendHandlerType()), EntityExtendDataStrategy.class);
        if (null == strategy) {
            EntityExtendResult<List<MetadataDataVO>> result = new EntityExtendResult<>();
            result.setData(new ArrayList<>());
            return result;
        }
        return strategy.query(extendDto, entityIds);
    }

    public EntityExtendDTO getExtendDto(Class<?> entityClass) throws SQLException {
        EntityExtend entityExtendAop = entityClass.getSuperclass().getAnnotation(EntityExtend.class);
        if (null == entityExtendAop) {
            return null;
        }
        TableName tableNameAop = entityClass.getAnnotation(TableName.class);
        if (null == tableNameAop) {
            return null;
        }

        ExtendHandlerType extendHandlerType = entityExtendAop.handler();
        EntityExtendDTO extendDto = new EntityExtendDTO();
        extendDto.setEntityClass((Class<? extends BaseEntity>) entityClass);
        extendDto.setExtendIdAttr(entityExtendAop.extendIdAttr());
        extendDto.setTableName(tableNameAop.value());
        extendDto.setExtendReferenceAttr(entityExtendAop.extendReferenceAttr());
        extendDto.setExtendAttr(entityExtendAop.entityExtendAttr());
        extendDto.setExtendHandlerType(extendHandlerType);
        //实体id列
        String entityIdAttr = EntityUtil.getEntityIdAttr(entityClass);
        extendDto.setEntityIdAttr(entityIdAttr);
        //获取当前实体属性列表
        List<String> entityAttrs = EntityUtil.getEntityAttrs(entityClass);
        extendDto.setEntityAttrs(entityAttrs);
        EntityExtendDataStrategy strategy = SpringContextHolder.getBean(strategies.get(extendDto.getExtendHandlerType()), EntityExtendDataStrategy.class);
        if (null == strategy) {
            return extendDto;
        }
        extendDto = strategy.setExtendDtoAdditionAttr(entityClass, entityExtendAop, tableNameAop, extendDto);
        return extendDto;
    }

    public EntityExtendResult<Long> insert(EntityExtendDTO extendDto, MetadataDataDTO dataDto) throws Exception {
        EntityExtendDataStrategy strategy = SpringContextHolder.getBean(strategies.get(extendDto.getExtendHandlerType()), EntityExtendDataStrategy.class);
        EntityExtendResult<Long> result = null;
        if (null != strategy && CollectionUtils.isNotEmpty(dataDto.getDetails())) {
            result = strategy.insert(extendDto, dataDto);
        } else {
            result = new EntityExtendResult<>();
        }
        return result;
    }

    public EntityExtendResult<Long> update(EntityExtendDTO extendDto, MetadataDataDTO dataDto) throws Exception {
        EntityExtendDataStrategy strategy = SpringContextHolder.getBean(strategies.get(extendDto.getExtendHandlerType()), EntityExtendDataStrategy.class);
        EntityExtendResult<Long> result = null;
        if (null != strategy && CollectionUtils.isNotEmpty(dataDto.getDetails())) {
            result = strategy.update(extendDto, dataDto);
        } else {
            result = new EntityExtendResult<>();
            result.setProcessedCount(0L);
        }
        return result;
    }

    public EntityExtendResult<Long> delete(EntityExtendDTO extendDto, MetadataDataDTO dataDto) throws Exception {
        EntityExtendDataStrategy strategy = SpringContextHolder.getBean(strategies.get(extendDto.getExtendHandlerType()), EntityExtendDataStrategy.class);
        EntityExtendResult<Long> result = null;
        if (null != strategy && CollectionUtils.isNotEmpty(dataDto.getConditions())) {
            result = strategy.delete(extendDto, dataDto);
        } else {
            result = new EntityExtendResult<>();
            result.setProcessedCount(0L);
        }
        return result;
    }


}
