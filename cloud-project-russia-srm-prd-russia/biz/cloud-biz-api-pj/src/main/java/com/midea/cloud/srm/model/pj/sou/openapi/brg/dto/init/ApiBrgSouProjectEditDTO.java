package com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.init;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouProject;
import com.midea.cloud.srm.model.pj.sou.brg.enums.BrgSouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectEditDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目式询价openAPI - 项目信息保存
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouProjectEditDTO extends ApiSouProjectEditDTO {

    /** @see BrgSouProject#getBudgetAmount */
    @ApiModelProperty("预算金额")
    private BigDecimal budgetAmount;

    /** @see BrgSouProject#getBargainType */
    @ApiModelProperty("询价类型")
    private BrgSouTypeEnum bargainType;

    /** @see BrgSouProject#getRequireDesc */
    @ApiModelProperty("需求简述")
    private String requireDesc;

    /** @see BrgSouProject#getBondAmount */
    @ApiModelProperty("商务要求 -- 保证金金额")
    private BigDecimal bondAmount;

    /** @see BrgSouProject#getBondDesc */
    @ApiModelProperty("商务要求 -- 保证金说明")
    private String bondDesc;

    /** @see BrgSouProject#getBondMethod */
    @ApiModelProperty("商务要求 -- 保证金提交方式[字典值: BID_BOND_SUBMISSION]")
    private String bondMethod;

    /** @see BrgSouProject#getBondEndTime */
    @ApiModelProperty("商务要求 -- 保证金提交截止时间")
    private Date bondEndTime;

    /** @see BrgSouProject#getBudgetAmount */
    @ApiModelProperty("商务要求 -- 保证金缴纳账号")
    private String bankAccountNum;

    /** @see BrgSouProject#getBankAccountName */
    @ApiModelProperty("商务要求 -- 账户名称")
    private String bankAccountName;

    /** @see BrgSouProject#getBankBranchName */
    @ApiModelProperty("商务要求 -- 开户支行")
    private String bankBranchName;

    /** @see BrgSouProject#getPublicLowestPrice */
    @ApiModelProperty("投标控制 -- 是否向供应商公开上轮最低价")
    private Enable publicLowestPrice;

    /** @see BrgSouProject#getPublicTotalRank */
    @ApiModelProperty("投标控制 -- 是否向供应商公开上轮最低价")
    private Enable publicTotalRank;

    /** @see BrgSouProject#getPublicTargetPrice */
    @ApiModelProperty("投标控制 -- 是否向供应商公开拦标价")
    private Enable publicTargetPrice;

    /** @see BrgSouProject#getVisibleRankResult */
    @ApiModelProperty("投标控制 -- 允许供应商查看最终排名结果")
    private Enable visibleRankResult;

    /** @see BrgSouProject#getVisibleFinalPrice */
    @ApiModelProperty("投标控制 -- 允许供应商查看中标价")
    private Enable visibleFinalPrice;

    /** @see BrgSouProject#getVisibleWinVendor */
    @ApiModelProperty("投标控制 -- 允许供应商查看中标供应商")
    private Enable visibleWinVendor;

    /** @see BrgSouProject#getExcludeBlackVendors */
    @ApiModelProperty("推荐控制 -- 是否排除黑名单供应商")
    private Enable excludeBlackVendors;

    /** @see BrgSouProject#getExcludeNoCurrentOrgVendors */
    @ApiModelProperty("推荐控制 -- 排除非本业务实体供应商")
    private Enable excludeNoCurrentOrgVendors;

    /** @see BrgSouProject#getExcludeOrgQuitVendors */
    @ApiModelProperty("推荐控制 -- 排除业务实体退出供应商")
    private Enable excludeOrgQuitVendors;

    /** @see BrgSouProject#getExcludeOrgCategoryStatus */
    @ApiModelProperty("推荐控制 -- 需要排除指定品类状态的供应商")
    private String excludeOrgCategoryStatus;

    /** @see BrgSouProject#getExchangeRateType */
    @ApiModelProperty("汇率类型[字典值: EXCHANGE_RATE_TYPE]")
    private String exchangeRateType;

    /** @see BrgSouProject#getCurrencyExchangeDate */
    @ApiModelProperty("币种转换日期")
    private Date currencyExchangeDate;

}
