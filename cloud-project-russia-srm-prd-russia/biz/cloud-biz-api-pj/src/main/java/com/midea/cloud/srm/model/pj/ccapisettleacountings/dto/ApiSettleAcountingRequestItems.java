package com.midea.cloud.srm.model.pj.ccapisettleacountings.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/04/09/ $
 * @Description:
 */
@Data
@ApiModel("结算记账-结算行")
public class ApiSettleAcountingRequestItems {
    @ApiModelProperty("基本信息")
    private ApiSettleAcountingRequestItemsBase baseInfo;
    @ApiModelProperty("结算明细")
    private List<ApiSettleAcountingRequestItemsSettle> settleDetailList;
    @ApiModelProperty("成本结转")
    private List<ApiSettleAcountingRequestItemsCost> costInfoList;
    @ApiModelProperty("附件列表")
    private List<ApiSettleAcountingRequestItemsAttach> attachList;

}
