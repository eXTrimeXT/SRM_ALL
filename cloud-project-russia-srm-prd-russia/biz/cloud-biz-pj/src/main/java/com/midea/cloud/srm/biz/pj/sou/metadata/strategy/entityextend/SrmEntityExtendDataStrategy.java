package com.midea.cloud.srm.biz.pj.sou.metadata.strategy.entityextend;

import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.biz.pj.sou.metadata.cache.MetadataCache;
import com.midea.cloud.srm.biz.pj.sou.metadata.constants.MetadataKey;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.ConditionType;
import com.midea.cloud.srm.biz.pj.sou.metadata.enums.MetadataType;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.EntityExtendDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataDataDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataQueryDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.result.EntityExtendResult;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.vo.MetadataDataVO;
import com.midea.cloud.srm.biz.pj.sou.metadata.service.MetadataDataService;
import com.midea.cloud.srm.biz.pj.sou.metadata.utils.EntityUtil;
import com.midea.cloud.srm.model.annonations.EntityExtend;
import com.midea.cloud.srm.model.base.metadata.vo.MetadataDetailVO;
import com.midea.cloud.srm.model.base.metadata.vo.MetadataVO;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

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
 *  修改日期: 2022/9/5 13:54
 *  修改内容:
 * </pre>
 */
@Component("srmEntityExtendDataStrategy")
public class SrmEntityExtendDataStrategy implements EntityExtendDataStrategy {

    @Override
    public EntityExtendDTO setExtendDtoAdditionAttr(Class<?> entityClass, EntityExtend entityExtendAop, TableName tableNameAop, EntityExtendDTO extendDto) throws SQLException {
        //检查扩展表名是否存在
        String tableName = tableNameAop.value() + "_" + MetadataType.EXT.toString().toLowerCase();
        extendDto.setExtendTableName(tableName);
        MetadataVO metadataVo = SpringContextHolder.getBean(MetadataCache.class).getByTableName(tableName);
        if (null == metadataVo) {
            return null;
        }
        List<MetadataDetailVO> metadataDetailVos = metadataVo.getDetails();
        //扩展表主键列是否存在
        boolean hasIdAttr = metadataDetailVos.stream().anyMatch(d -> d.getFieldAttr().equals(entityExtendAop.extendIdAttr()));
        //扩展表关联主表列是否存在
        boolean hasReferenceAttr = metadataDetailVos.stream().anyMatch(d -> d.getFieldAttr().equals(entityExtendAop.extendIdAttr()));
        //是否有扩展属性
        boolean hasExtendAttr = EntityUtil.hasExtendAttr(entityClass, entityExtendAop.entityExtendAttr());
        if (!hasIdAttr || !hasReferenceAttr || !hasExtendAttr || null == extendDto.getEntityIdAttr()) {
            return null;
        }
        return extendDto;
    }

    @Override
    public EntityExtendResult<List<MetadataDataVO>> query(EntityExtendDTO extendDto, List<Object> entityIds) {
        MetadataQueryDTO queryDto = buildExtendDataQueryDto(entityIds, extendDto);
        List<MetadataDataVO> dataVos = SpringContextHolder.getBean(MetadataDataService.class).getList(queryDto);
        EntityExtendResult<List<MetadataDataVO>> result = new EntityExtendResult<>();
        result.setData(dataVos);
        result.setProcessedCount((long) dataVos.size());
        return result;
    }

    @Override
    public EntityExtendResult<Long> insert(EntityExtendDTO extendDto, MetadataDataDTO dataDto) throws Exception {
        List<Object> ids = SpringContextHolder.getBean(MetadataDataService.class).batchAdd(dataDto);
        dataDto.getDetails().forEach(rowDto -> rowDto.remove(MetadataKey.KEY_ORI_OBJECT));
        EntityExtendResult<Long> result = new EntityExtendResult<>();
        result.setData((long) ids.size());
        result.setProcessedCount(result.getData());
        return result;
    }

    @Override
    public EntityExtendResult<Long> update(EntityExtendDTO extendDto, MetadataDataDTO dataDto) throws Exception {
        SpringContextHolder.getBean(MetadataDataService.class).batchUpdateById(dataDto);
        EntityExtendResult<Long> result = new EntityExtendResult<>();
        result.setData((long) dataDto.getDetails().size());
        result.setProcessedCount(result.getData());
        return result;
    }

    @Override
    public EntityExtendResult<Long> delete(EntityExtendDTO extendDto, MetadataDataDTO dataDto) throws Exception {
        SpringContextHolder.getBean(MetadataDataService.class).delete(dataDto);
        EntityExtendResult<Long> result = new EntityExtendResult<>();
        result.setData((long) dataDto.getDetails().size());
        result.setProcessedCount(result.getData());
        return result;
    }

    private MetadataQueryDTO buildExtendDataQueryDto(List<?> referenceValues, EntityExtendDTO extendDto) {
        MetadataQueryDTO queryDto = new MetadataQueryDTO();
        queryDto.setTableName(extendDto.getExtendTableName());
        queryDto.addCondition(extendDto.getExtendReferenceAttr(), ConditionType.IN, referenceValues, null);
        queryDto.addOrder(extendDto.getEntityIdAttr(), ConditionType.ASC);
        queryDto.addOrder("creationDate", ConditionType.DESC);
        return queryDto;
    }
}
