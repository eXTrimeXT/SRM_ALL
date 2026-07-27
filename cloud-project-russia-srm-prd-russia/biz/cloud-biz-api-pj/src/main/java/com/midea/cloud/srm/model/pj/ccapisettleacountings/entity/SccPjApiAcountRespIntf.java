package com.midea.cloud.srm.model.pj.ccapisettleacountings.entity;

import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/04/10/ $
 * @Description:
 */
@Data
@ApiModel("结算记账接口-响应")
public class SccPjApiAcountRespIntf extends BaseEntity {

    @ApiModelProperty("主键")
    private Long respIntfId;
    @ApiModelProperty("外键")
    private Long acountingIntfId;
    @ApiModelProperty("系统编码")
    private String systemCode;
    @ApiModelProperty("业务单据号")
    private String businessNo;
    @ApiModelProperty("请求流水号")
    private String reqSn;
    @ApiModelProperty("结算单据编码")
    private String settleDocumentCode;
    @ApiModelProperty("行号，对应请求中baseInfo中的itemNo")
    private Integer itemNo;
    @ApiModelProperty("凭证号，多个凭证号以英文“,”分隔")
    private String voucherNo;
    @ApiModelProperty("凭证年")
    private String year;
    @ApiModelProperty("公司代码")
    private String companyCode;
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
