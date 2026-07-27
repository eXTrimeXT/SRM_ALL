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
@ApiModel("结算记账-请求头")
public class ApiSettleAcountingRequestHead {
    @ApiModelProperty("系统编码")
    private String systemCode;
    @ApiModelProperty("业务单据号")
    private String businessNo;
    @ApiModelProperty("请求流水号")
    private String reqSn;
    @ApiModelProperty("销售类型（01-服务销售，02-资产销售，10-其他）")
    private String saleType;
    @ApiModelProperty("销售组织编码")
    private String orgCode;
    @ApiModelProperty("经办部门编码")
    private String deptCode;
    @ApiModelProperty("经办部门名称")
    private String deptName;
    @ApiModelProperty("创建人工号")
    private String createUserNo;
    @ApiModelProperty("创建人姓名")
    private String createUserName;
    @ApiModelProperty("创建时间")
    private String createTime;
    @ApiModelProperty("结算说明")
    private String settleExplain;
    @ApiModelProperty("记账人工号")
    private String accountUserNo;
    @ApiModelProperty("记账人姓名")
    private String accountUserName;
    @ApiModelProperty("是否需审批")
    private Boolean needApprove;
}
