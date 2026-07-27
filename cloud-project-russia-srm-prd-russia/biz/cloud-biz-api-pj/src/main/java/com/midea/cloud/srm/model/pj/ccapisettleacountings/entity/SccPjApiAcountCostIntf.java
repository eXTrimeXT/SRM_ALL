package com.midea.cloud.srm.model.pj.ccapisettleacountings.entity;

import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/04/09/ $
 * @Description:
 */
@Data
@ApiModel("结算记账接口-成本结转")
public class SccPjApiAcountCostIntf extends BaseEntity {
    @ApiModelProperty("主键")
    private Long costIntfId;
    @ApiModelProperty("外键")
    private Long acountingIntfId;
    @ApiModelProperty("服务编码")
    private String serviceCode;
    @ApiModelProperty("结转费用分类编码")
    private String coExpenseClassifyCode;
    @ApiModelProperty("结转金额")
    private BigDecimal costAmount;
    @ApiModelProperty("成本中心编码")
    private String costCenterCode;
    @ApiModelProperty("成本中心名称")
    private String costCenterName;
    @ApiModelProperty("订单号")
    private String orderNo;
    @ApiModelProperty("订单名称")
    private String orderName;
    @ApiModelProperty("研发类型")
    private String devType;
    @ApiModelProperty("车型编码")
    private String carCode;
    @ApiModelProperty("投放形式编码")
    private String layCode;
    @ApiModelProperty("备注")
    private String remarks;
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
