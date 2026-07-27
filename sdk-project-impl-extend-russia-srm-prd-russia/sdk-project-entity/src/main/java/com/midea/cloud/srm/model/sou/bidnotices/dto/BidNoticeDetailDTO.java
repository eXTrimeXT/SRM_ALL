package com.midea.cloud.srm.model.sou.bidnotices.dto;


import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;


/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/9 18:09:01
 *  修改内容:
 * </pre>
 */
@ApiModel(value = "BidNoticeDetailDTO", description = "中/落标通知明细表DTO对象")
@Data
@EqualsAndHashCode(callSuper = true)
public class BidNoticeDetailDTO extends BaseDTO {

    @ApiModelProperty(value = "中/落标通知明细ID", example = "1", required = true)
    private Long bidNoticeDetailId;

    @ApiModelProperty(value = "中/落标通知ID", example = "1", required = true)
    private Long bidNoticeId;

    @ApiModelProperty(value = "序号", example = "1")
    private Integer lineNum;

    @ApiModelProperty(value = "供应商ID", example = "1")
    private Long vendorId;

    @ApiModelProperty(value = "供应商编码", example = "001")
    private String vendorCode;

    @ApiModelProperty(value = "供应商名称", example = "供应商1")
    private String vendorName;

    @ApiModelProperty(value = "是否中标", example = "Y")
    private String isWin;

    @ApiModelProperty(value = "中标金额（元）", example = "1000.00")
    private BigDecimal winAmount;

    @ApiModelProperty(value = "合同签署单位名称集合,逗号分割", example = "公司A")
    private String contractSignUnit;

    @ApiModelProperty(value = "合同签署单位ID集合,逗号分割", example = "公司A")
    private String contractSignUnitId;

    @ApiModelProperty(value = "合同签署单位编码集合,逗号分割", example = "公司A")
    private String contractSignUnitCode;

    @ApiModelProperty(value = "合同签署单位统一社会信用代码集合,逗号分割", example = "公司A")
    private String contractSignUnitCredit;

    @ApiModelProperty(value = "合同周期", example = "2022-01-01至2023-01-01")
    private String contractPeriod;

    @ApiModelProperty(value = "是否履约评价", example = "是")
    private String isPerformanceEvaluated;

    @ApiModelProperty(value = "不履约评价的原因", example = "质量不合格")
    private String nonPerformanceReason;

    @ApiModelProperty(value = "是否现场考察", example = "是")
    private String isOnSiteInspected;

    @ApiModelProperty(value = "通知书附件ID", example = "1")
    private Long noticeAttachmentId;

    @ApiModelProperty(value = "通知书附件名称", example = "通知书1")
    private String noticeAttachmentName;

    @ApiModelProperty(value = "是否发送", example = "Y")
    private String isSend;

    @ApiModelProperty(value = "发送时间", example = "2022-01-01 12:00:00")
    private Date sendTime;

    @ApiModelProperty(value = "是否已盖章", example = "Y")
    private String isSign;

    @ApiModelProperty(value = "现场考察ID", example = "1")
    private Long inspectId;

}

