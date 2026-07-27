package com.midea.cloud.srm.model.pj.sou.brg.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.brg.entity.ExtBrgSouProject;
import com.midea.cloud.srm.model.pj.sou.brg.enums.BrgSouTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目式询价基础信息表
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/21
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_brg_project")
@ApiModel("询价头信息")
public class BrgSouProject extends ExtBrgSouProject {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("寻源单ID")
    @TableId("PROJECT_ID")
    private Long projectId;

    @TableField("BUDGET_AMOUNT")
    @ApiModelProperty("预算金额")
    private BigDecimal budgetAmount;

    @TableField("BARGAIN_TYPE")
    @ApiModelProperty("询价类型(字典值: SOU_BRG_TYPE)")
    private BrgSouTypeEnum bargainType;

    @TableField("REQUIRE_DESC")
    @ApiModelProperty("需求简述")
    private String requireDesc;

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

    @TableField("PUBLIC_LOWEST_PRICE")
    @ApiModelProperty("投标控制 -- 是否向供应商公开上轮最低价")
    private Enable publicLowestPrice;

    @TableField("PUBLIC_TOTAL_RANK")
    @ApiModelProperty("投标控制 -- 是否向供应商公开上轮排名结果")
    private Enable publicTotalRank;

    @TableField("PUBLIC_TARGET_PRICE")
    @ApiModelProperty("投标控制 -- 是否向供应商公开拦标价")
    private Enable publicTargetPrice;

    @TableField("VISIBLE_RANK_RESULT")
    @ApiModelProperty("投标控制 -- 允许供应商查看最终排名结果")
    private Enable visibleRankResult;

    @TableField("VISIBLE_FINAL_PRICE")
    @ApiModelProperty("投标控制 -- 允许供应商查看中标价")
    private Enable visibleFinalPrice;

    @TableField("VISIBLE_WIN_VENDOR")
    @ApiModelProperty("投标控制 -- 允许供应商查看中标供应商")
    private Enable visibleWinVendor;

    @TableField("EXCLUDE_BLACK_VENDORS")
    @ApiModelProperty("推荐控制 -- 是否排除黑名单供应商")
    private Enable excludeBlackVendors;

    @TableField("EXCLUDE_NO_CURRENT_ORG_VENDORS")
    @ApiModelProperty("推荐控制 -- 排除非本业务实体供应商")
    private Enable excludeNoCurrentOrgVendors;

    @TableField("EXCLUDE_ORG_QUIT_VENDORS")
    @ApiModelProperty("推荐控制 -- 排除业务实体退出供应商")
    private Enable excludeOrgQuitVendors;

    @TableField("EXCLUDE_ORG_CATEGORY_STATUS")
    @ApiModelProperty("推荐控制 -- 需要排除指定品类状态的供应商")
    private String excludeOrgCategoryStatus;

    @TableField("EXCHANGE_RATE_TYPE")
    @ApiModelProperty("汇率类型[字典值: EXCHANGE_RATE_TYPE]")
    private String exchangeRateType;

    @TableField("CURRENCY_EXCHANGE_DATE")
    @ApiModelProperty("币种转换日期")
    private Date currencyExchangeDate;

}
