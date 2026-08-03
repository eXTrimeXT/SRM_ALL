package com.midea.cloud.srm.biz.pj.sou.metadata.strategy.entityextend;

import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.EntityExtendDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.dto.MetadataDataDTO;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.result.EntityExtendResult;
import com.midea.cloud.srm.biz.pj.sou.metadata.model.vo.MetadataDataVO;
import com.midea.cloud.srm.model.annonations.EntityExtend;

import java.sql.SQLException;
import java.util.List;

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
public interface EntityExtendDataStrategy {
    /**
     * 备注
     * @param entityClass
     * @param entityExtendAop
     * @param tableNameAop
     * @param extendDto
     * @return
     * @throws SQLException
     */
    EntityExtendDTO setExtendDtoAdditionAttr(Class<?> entityClass, EntityExtend entityExtendAop, TableName tableNameAop, EntityExtendDTO extendDto) throws SQLException;

    /**
     * 备注
     * @param extendDto
     * @param entityIds
     * @return
     */
    EntityExtendResult<List<MetadataDataVO>> query(EntityExtendDTO extendDto, List<Object> entityIds);

    /**
     * 备注
     * @param extendDto
     * @param dataDto
     * @return
     * @throws Exception
     */
    EntityExtendResult<Long> insert(EntityExtendDTO extendDto, MetadataDataDTO dataDto) throws Exception;

    /**
     * 备注
     * @param extendDto
     * @param dataDto
     * @return
     * @throws Exception
     */
    EntityExtendResult<Long> update(EntityExtendDTO extendDto, MetadataDataDTO dataDto) throws Exception;

    /**
     * 备注
     * @param extendDto
     * @param dataDto
     * @return
     * @throws Exception
     */
    EntityExtendResult<Long> delete(EntityExtendDTO extendDto, MetadataDataDTO dataDto) throws Exception;
}
