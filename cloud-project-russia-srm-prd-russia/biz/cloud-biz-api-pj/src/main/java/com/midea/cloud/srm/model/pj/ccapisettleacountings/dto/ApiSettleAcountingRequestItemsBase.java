package com.midea.cloud.srm.model.pj.ccapisettleacountings.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: panmq
 * @Date: 2024/04/09/ $
 * @Description:
 */
@Data
@ApiModel("结算记账-结算行-基本信息")
public class ApiSettleAcountingRequestItemsBase {
    @ApiModelProperty("行号，一次请求不可重复")
    private Integer itemNo;
    @ApiModelProperty("客商类型（1-客户，2-供应商）")
    private String partnerType;
    @ApiModelProperty("客商编码")
    private String partnerCode;
    @ApiModelProperty("客商名称")
    private String partnerName;
    @ApiModelProperty("利润中心编码")
    private String profitCenterCode;
    @ApiModelProperty("利润中心名称")
    private String profitCenterName;
    @ApiModelProperty("成本中心编码")
    private String costCenterCode;
    @ApiModelProperty("成本中心名称")
    private String costCenterName;
    @ApiModelProperty("单据说明")
    private String documentExplain;
    @ApiModelProperty("是否含税（true-含税，false-不含税）")
    private Boolean containTax;
}
