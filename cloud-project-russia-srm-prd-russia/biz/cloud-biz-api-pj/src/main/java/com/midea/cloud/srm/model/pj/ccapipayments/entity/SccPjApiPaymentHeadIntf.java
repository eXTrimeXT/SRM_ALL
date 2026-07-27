package com.midea.cloud.srm.model.pj.ccapipayments.entity;

import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 批量付款及自动提交审批推送头-接口表实体类
 */
@ApiModel("批量付款及自动提交审批推送头-接口表实体类")
@Data
public class SccPjApiPaymentHeadIntf extends BaseEntity {

    @ApiModelProperty("主键")
    private Long paymentIntfId;
    @ApiModelProperty("创建者工号")
    private String creator;
    @ApiModelProperty("系统标识")
    private String sysFlag;
    @ApiModelProperty("公司代码")
    private String corporationCode;
    @ApiModelProperty("公司名称")
    private String currencyName;
    @ApiModelProperty("时间")
    private String paymentDate;
    @ApiModelProperty("申请说明")
    private String requestNote;
    @ApiModelProperty("组织代码")
    private String organizationCode;
    @ApiModelProperty("业务货币代码")
    private String currencyCode;
    @ApiModelProperty("资金预算部门代码")
    private String budgetDepartmentCode;
    @ApiModelProperty("资金预算部门名称")
    private String budgetDepartmentName;
    @ApiModelProperty("对接文件")
    private String fileFlag;
    @ApiModelProperty("单据类型")
    private String paymentType;
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
