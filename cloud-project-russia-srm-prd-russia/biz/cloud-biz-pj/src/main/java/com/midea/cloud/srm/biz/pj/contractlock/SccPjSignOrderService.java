package com.midea.cloud.srm.biz.pj.contractlock;

import com.midea.cloud.srm.model.pj.sign.entity.SccPjSignOrder;
import io.swagger.annotations.ApiOperation;

import java.util.Collection;

/**
 * @author huangbf3
 */
public interface SccPjSignOrderService {
    /**
     * 备注
     * @param sccPjSignOrder
     * @return
     */
    @ApiOperation(value = "保存签署业务单据关系表")
    boolean save(SccPjSignOrder sccPjSignOrder);

    /**
     * 备注
     * @param entityList
     * @return
     */
    @ApiOperation(value = "批量保存签署业务单据关系表")
    boolean saveBatch(Collection<SccPjSignOrder> entityList);

}
