package com.midea.cloud.srm.model.pj.ccapisettleacountings.entity;

import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/04/09/ $
 * @Description:
 */
@Data
@ApiModel("结算记账接口-请求头")
public class SccPjApiAcountHeadIntf extends BaseEntity {
    @ApiModelProperty("主键")
    private Long acountingIntfId;
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
    @ApiModelProperty("是否需审批")
    private String needApprove;
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
    @ApiModelProperty("处理序号")
    private String processSerialNum;
    @ApiModelProperty("处理状态，PENDING：未处理，COMPLETED：处理完成，PROCESSING：处理中，ERROR：处理错误，RETRY：需重试")
    private String processStatus;
    @ApiModelProperty("处理信息")
    private String processMessage;
    @ApiModelProperty("处理时间")
    private Date processDate;
    @ApiModelProperty("处理批次号")
    private Long processGroupId;

}
