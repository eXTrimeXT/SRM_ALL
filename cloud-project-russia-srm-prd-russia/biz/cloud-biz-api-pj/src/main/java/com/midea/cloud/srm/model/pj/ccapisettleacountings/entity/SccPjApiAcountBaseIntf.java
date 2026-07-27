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
@ApiModel("结算记账接口-行基本信息")
public class SccPjApiAcountBaseIntf extends BaseEntity {
    @ApiModelProperty("主键")
    private Long baseIntfId;
    @ApiModelProperty("外键")
    private Long acountingIntfId;
    @ApiModelProperty("行号，一次请求不可重复")
    private String itemNo;
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
    private String containTax;
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
