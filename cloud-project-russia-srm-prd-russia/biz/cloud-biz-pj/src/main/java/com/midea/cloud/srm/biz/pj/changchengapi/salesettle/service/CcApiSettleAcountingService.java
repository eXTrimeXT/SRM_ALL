package com.midea.cloud.srm.biz.pj.changchengapi.salesettle.service;

import com.midea.cloud.srm.model.pj.ccapisettleacountings.dto.ApiSettleAcountingRequestDto;
import com.midea.cloud.srm.model.pj.ccapisettleacountings.dto.ApiSettleAcountingResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Author: panmq
 * @Date: 2024/04/10/ $
 * @Description: 结算记账 销售结算记账接口，完成会计记账
 */

@Api("结算记账 销售结算记账接口，完成会计记账")
public interface CcApiSettleAcountingService {

    /**
     * 结算记账 销售结算记账接口，完成会计记账
     * 文档：https://open.gwm.cn/goodsdetail?group_id=765bd9673d0a41459765f0b2d0031673&tabQueryType=0
     * @param requestDto
     * @return
     */
    @ApiOperation(("结算记账 销售结算记账接口，完成会计记账"))
    ApiSettleAcountingResponseDto accounting(ApiSettleAcountingRequestDto requestDto);
}
