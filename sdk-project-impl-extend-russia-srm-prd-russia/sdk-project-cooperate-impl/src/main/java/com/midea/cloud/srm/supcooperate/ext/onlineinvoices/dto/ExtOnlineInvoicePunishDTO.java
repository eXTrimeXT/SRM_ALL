package com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Data
public class ExtOnlineInvoicePunishDTO extends BaseEntity {

    @ApiModelProperty("主键,扣罚返利明细ID")
    @TableId("INVOICE_PUNISH_ID")
    private Long invoicePunishId;
    @ApiModelProperty("开票通知ID")
    @TableField("INVOICE_NOTICE_ID")
    private Long invoiceNoticeId;
    @ApiModelProperty("供应商考核单ID")
    @TableField("VENDOR_ASSES_ID")
    private Long vendorAssesId;
    @ApiModelProperty("考核单号")
    @TableField("ASSESSMENT_NO")
    private String assessmentNo;
    @ApiModelProperty("考核时间")
    @TableField("ASSESSMENT_DATE")
    private LocalDateTime assessmentDate;
    @ApiModelProperty("考核类型")
    @TableField("ASSESSMENT_TYPE")
    private String assessmentType;
    @ApiModelProperty("实际考核金额(含税)")
    @TableField("ACTUAL_ASSESSMENT_AMOUNT_Y")
    private BigDecimal actualAssessmentAmountY;
    @ApiModelProperty("实际考核金额(不含税)")
    @TableField("ACTUAL_ASSESSMENT_AMOUNT_N")
    private BigDecimal actualAssessmentAmountN;
    @ApiModelProperty("税额")
    @TableField("TAX")
    private BigDecimal tax;
    @ApiModelProperty("币种名称")
    @TableField("CURRENCY_NAME")
    private String currencyName;
    @ApiModelProperty("币种编码")
    @TableField("CURRENCY_CODE")
    private String currencyCode;
    @ApiModelProperty("采购分类id(物料分类id)")
    @TableField("CATEGORY_ID")
    private Long categoryId;
    @ApiModelProperty("采购分类编码(物料分类编码)")
    @TableField("CATEGORY_CODE")
    private String categoryCode;
    @ApiModelProperty("采购分类名称(物料分类名称)")
    @TableField("CATEGORY_NAME")
    private String categoryName;
    @ApiModelProperty("物料编码")
    @TableField("ITEM_CODE")
    private String itemCode;
    @ApiModelProperty("物料名称")
    @TableField("ITEM_NAME")
    private String itemName;
    @ApiModelProperty("备注")
    @TableField(
            value = "COMMENT",
            keepGlobalFormat = true
    )
    private String comment;
    @ApiModelProperty("生效日期(YYYY-MM-DD)")
    @TableField("START_DATE")
    private LocalDate startDate;
    @ApiModelProperty("失效日期(YYYY-MM-DD)")
    @TableField("END_DATE")
    private LocalDate endDate;
    @ApiModelProperty("是否引用(是:Y,否:N)")
    @TableField("IF_QUOTE")
    private String ifQuote;
}
