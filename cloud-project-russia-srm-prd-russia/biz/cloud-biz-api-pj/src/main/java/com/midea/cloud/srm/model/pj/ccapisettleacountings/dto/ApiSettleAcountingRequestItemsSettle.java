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
@ApiModel("结算记账-结算行-结算明细列表")
public class ApiSettleAcountingRequestItemsSettle {
    @ApiModelProperty("服务编码，不同结算明细不能重复")
    private String serviceCode;
    @ApiModelProperty("税率")
    private String taxRate;
    @ApiModelProperty("税收分类编码")
    private String taxClassifyCode;
    @ApiModelProperty("科目编码")
    private String accSubjectCode;
    @ApiModelProperty("科目名称")
    private String accSubjectName;
    @ApiModelProperty("结算信息列表")
    private List<ApiSettleAcountingRequestItemsSettleInfo> settleInfoList;
}
