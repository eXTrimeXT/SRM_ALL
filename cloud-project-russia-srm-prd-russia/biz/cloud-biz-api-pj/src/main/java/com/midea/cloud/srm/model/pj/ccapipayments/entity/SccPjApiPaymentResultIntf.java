package com.midea.cloud.srm.model.pj.ccapipayments.entity;

import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 批量付款及自动提交审批推送结果-接口表实体类
 */
@ApiModel("批量付款及自动提交审批推送结果-接口表实体类")
@Data
public class SccPjApiPaymentResultIntf extends BaseEntity {

    @ApiModelProperty("主键")
    private Long paymentResultIntfId;
    @ApiModelProperty("外键")
    private Long paymentIntfId;
    @ApiModelProperty("状态")
    private String status;
    @ApiModelProperty("是否成功")
    private String success;
    @ApiModelProperty("状态码")
    private String statusCode;
    @ApiModelProperty("消息")
    private String message;
    @ApiModelProperty("单据号")
    private String orderNo;
    @ApiModelProperty("支付行项目唯一标识")
    private String requestItemId;
    @ApiModelProperty("支付序号")
    private Integer itemNum;
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
