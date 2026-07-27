package com.midea.cloud.srm.model.pj.ccapisettleacountings.entity;

import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: panmq
 * @Date: 2024/04/09/ $
 * @Description:
 */
@Data
@ApiModel("结算记账接口-结算明细-基本信息")
public class SccPjApiAcountSetinfoIntf extends BaseEntity {
    @ApiModelProperty("主键")
    private Long settleInfoIntfId;
    @ApiModelProperty("外键-头")
    private Long acountingIntfId;
    @ApiModelProperty("外键-结算明细")
    private Long settleIntfId;
    @ApiModelProperty("业务编码")
    private String businessCode;
    @ApiModelProperty("规格型号")
    private String specsModel;
    @ApiModelProperty("结算数量")
    private BigDecimal settleNumber;
    @ApiModelProperty("单价")
    private BigDecimal price;
    @ApiModelProperty("单位")
    private String unit;
    @ApiModelProperty("折扣标识（true-折扣，false-不折扣）")
    private String discountFlag;
    @ApiModelProperty("折扣金额，折扣标识为true时，必填")
    private BigDecimal discountAmount;
    @ApiModelProperty("备注")
    private String remarks;


}
