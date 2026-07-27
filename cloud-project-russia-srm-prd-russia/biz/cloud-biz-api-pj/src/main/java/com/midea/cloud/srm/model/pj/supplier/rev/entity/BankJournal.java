package com.midea.cloud.srm.model.pj.supplier.rev.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.meiql.api.annotation.QlMatchType;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @author huangbf3
 * 银行信息日志表
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("scc_sup_auth_bank_journal")
@ApiModel(description = "银行信息日志")
@QlMatchType("BankJournal")
public class BankJournal extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId("BANK_JOURNAL_ID")
    private Long bankJournalId;

    @ApiModelProperty(value = "单据ID")
    @TableField("FORM_ID")
    private Long formId;

    @ApiModelProperty(value = "单据类型")
    @TableField("FORM_TYPE")
    private String formType;

    @ApiModelProperty(value = "供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    @ApiModelProperty(value = "银行名称")
    @TableField("BANK_NAME")
    private String bankName;

    @ApiModelProperty(value = "银行代码")
    @NotEmpty(message = "银行代码不能为空")
    @TableField("BANK_CODE")
    private String bankCode;

    @ApiModelProperty(value = "开户行")
    @TableField("OPENING_BANK")
    private String openingBank;

    @ApiModelProperty(value = "联行编码")
    @TableField("UNION_CODE")
    private String unionCode;

    @ApiModelProperty(value = "SWIFT CODE")
    @TableField(value = "SWIFT_CODE")
    private String swiftCode;

    @ApiModelProperty(value = "银行账号")
    @NotEmpty(message = "银行账号不能为空")
    @TableField("BANK_ACCOUNT")
    private String bankAccount;

    @ApiModelProperty(value = "银行账户名")
    @NotEmpty(message = "银行账户名称不能为空")
    @TableField("BANK_ACCOUNT_NAME")
    private String bankAccountName;

    @ApiModelProperty(value = "币种")
    @NotEmpty(message = "银行信息的币种不能为空")
    @TableField("CURRENCY_CODE")
    private String currencyCode;

    @ApiModelProperty(value = "主账号")
    @TableField("CEEA_MAIN_ACCOUNT")
    private String ceeaMainAccount;

    @ApiModelProperty(value = "启用")
    @TableField("CEEA_ENABLED")
    private String ceeaEnabled;

    @ApiModelProperty(value = "账户类型")
    @TableField("ACCOUNT_TYPE")
    private String accountType;

    @ApiModelProperty(value = "生效日期(YYYY-MM-DD)")
    @TableField("START_DATE")
    private Date startDate;

    @ApiModelProperty(value = "失效日期(YYYY-MM-DD)")
    @TableField("END_DATE")
    private Date endDate;


    @ApiModelProperty(value = "银行信息Id")
    @TableField("CEEA_BANK_INFO_ID")
    private Long ceeaBankInfoId;

}
