package com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.init;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProject;
import com.midea.cloud.srm.model.pj.sou.bid.enums.BidSouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectEditDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 招投标openAPI - 项目信息保存
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouProjectEditDTO extends ApiSouProjectEditDTO {

    /** @see BidSouProject#getBudgetAmount */
    @ApiModelProperty("预算金额")
    private BigDecimal budgetAmount;

    /** @see BidSouProject#getBargainType */
    @ApiModelProperty("询价类型")
    private BidSouTypeEnum bargainType;

    /** @see BidSouProject#getRequireDesc */
    @ApiModelProperty("需求简述")
    private String requireDesc;

    /** @see BidSouProject#getBondAmount */
    @ApiModelProperty("商务要求 -- 保证金金额")
    private BigDecimal bondAmount;

    /** @see BidSouProject#getBondDesc */
    @ApiModelProperty("商务要求 -- 保证金说明")
    private String bondDesc;

    /** @see BidSouProject#getBondMethod */
    @ApiModelProperty("商务要求 -- 保证金提交方式[字典值: BID_BOND_SUBMISSION]")
    private String bondMethod;

    /** @see BidSouProject#getBondEndTime */
    @ApiModelProperty("商务要求 -- 保证金提交截止时间")
    private Date bondEndTime;

    /** @see BidSouProject#getBudgetAmount */
    @ApiModelProperty("商务要求 -- 保证金缴纳账号")
    private String bankAccountNum;

    /** @see BidSouProject#getBankAccountName */
    @ApiModelProperty("商务要求 -- 账户名称")
    private String bankAccountName;

    /** @see BidSouProject#getBankBranchName */
    @ApiModelProperty("商务要求 -- 开户支行")
    private String bankBranchName;

    /** @see BidSouProject#getPublicLowestPrice */
    @ApiModelProperty("投标控制 -- 是否向供应商公开上轮最低价")
    private Enable publicLowestPrice;

    /** @see BidSouProject#getPublicTotalRank */
    @ApiModelProperty("投标控制 -- 是否向供应商公开上轮最低价")
    private Enable publicTotalRank;

    /** @see BidSouProject#getPublicTargetPrice */
    @ApiModelProperty("投标控制 -- 是否向供应商公开拦标价")
    private Enable publicTargetPrice;

    /** @see BidSouProject#getVisibleRankResult */
    @ApiModelProperty("投标控制 -- 允许供应商查看最终排名结果")
    private Enable visibleRankResult;

    /** @see BidSouProject#getVisibleFinalPrice */
    @ApiModelProperty("投标控制 -- 允许供应商查看中标价")
    private Enable visibleFinalPrice;

    /** @see BidSouProject#getVisibleWinVendor */
    @ApiModelProperty("投标控制 -- 允许供应商查看中标供应商")
    private Enable visibleWinVendor;

    /** @see BidSouProject#getExcludeBlackVendors */
    @ApiModelProperty("推荐控制 -- 是否排除黑名单供应商")
    private Enable excludeBlackVendors;

    /** @see BidSouProject#getExcludeNoCurrentOrgVendors */
    @ApiModelProperty("推荐控制 -- 排除非本业务实体供应商")
    private Enable excludeNoCurrentOrgVendors;

    /** @see BidSouProject#getExcludeOrgQuitVendors */
    @ApiModelProperty("推荐控制 -- 排除业务实体退出供应商")
    private Enable excludeOrgQuitVendors;

    /** @see BidSouProject#getExcludeOrgCategoryStatus */
    @ApiModelProperty("推荐控制 -- 需要排除指定品类状态的供应商")
    private String excludeOrgCategoryStatus;

    /** @see BidSouProject#getExchangeRateType */
    @ApiModelProperty("汇率类型[字典值: EXCHANGE_RATE_TYPE]")
    private String exchangeRateType;

    /** @see BidSouProject#getCurrencyExchangeDate */
    @ApiModelProperty("币种转换日期")
    private Date currencyExchangeDate;

}
