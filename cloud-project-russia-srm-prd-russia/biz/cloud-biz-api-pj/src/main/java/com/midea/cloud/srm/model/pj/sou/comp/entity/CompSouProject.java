package com.midea.cloud.srm.model.pj.sou.comp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.comp.entity.ExtCompSouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 竞价 - 寻源单
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_comp_project")
@ApiModel("竞价.寻源单")
public class CompSouProject extends ExtCompSouProject {

    @TableId("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @TableField("BUDGET_AMOUNT")
    @ApiModelProperty("预算金额")
    private BigDecimal budgetAmount;

    @TableField("MIN_PERCENT")
    @ApiModelProperty("最小涨/跌幅百分比")
    private BigDecimal minPercent;

    @TableField("MIN_AMOUNT")
    @ApiModelProperty("最小涨/跌金额")
    private BigDecimal minAmount;

    @TableField("EXTEND_TRIGGER")
    @ApiModelProperty("截止至前几分钟")
    private BigDecimal extendTrigger;

    @TableField("EXTEND_MINUTE")
    @ApiModelProperty("延长多少分钟")
    private BigDecimal extendMinute;

    @TableField("MAX_WIN_VENDOR_COUNT")
    @ApiModelProperty("中标供应商数量")
    private Integer maxWinVendorCount;

    @TableField("EXCHANGE_RATE_TYPE")
    @ApiModelProperty("汇率类型[字典值: EXCHANGE_RATE_TYPE]")
    private String exchangeRateType;

    @TableField("CURRENCY_EXCHANGE_DATE")
    @ApiModelProperty("币种转换日期")
    private Date currencyExchangeDate;

    @TableField("BOND_AMOUNT")
    @ApiModelProperty("商务要求 -- 保证金金额")
    private BigDecimal bondAmount;

    @TableField("BOND_DESC")
    @ApiModelProperty("商务要求 -- 保证金说明")
    private String bondDesc;

    @TableField("BOND_METHOD")
    @ApiModelProperty("商务要求 -- 保证金提交方式[字典值: BID_BOND_SUBMISSION]")
    private String bondMethod;

    @TableField("BOND_END_TIME")
    @ApiModelProperty("商务要求 -- 保证金提交截止时间")
    private Date bondEndTime;

    @TableField("BANK_ACCOUNT_NUM")
    @ApiModelProperty("商务要求 -- 保证金缴纳账号")
    private String bankAccountNum;

    @TableField("BANK_ACCOUNT_NAME")
    @ApiModelProperty("商务要求 -- 账户名称")
    private String bankAccountName;

    @TableField("BANK_BRANCH_NAME")
    @ApiModelProperty("商务要求 -- 开户支行")
    private String bankBranchName;

    @TableField("REQUIRE_DESC")
    @ApiModelProperty("需求简述")
    protected String requireDesc;

}
