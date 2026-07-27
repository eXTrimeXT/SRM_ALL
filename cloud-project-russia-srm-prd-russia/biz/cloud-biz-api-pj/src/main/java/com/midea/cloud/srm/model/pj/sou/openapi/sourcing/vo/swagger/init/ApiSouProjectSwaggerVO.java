package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.init;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProject;
import com.midea.cloud.srm.model.pj.sou.bid.enums.BidSouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouProject;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.pj.sou.inq.enums.InqSouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * 寻源单 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "寻源单")
@EqualsAndHashCode(callSuper = true)
public class ApiSouProjectSwaggerVO extends SouProject {

    /** @see InqSouProject#getExtProjectStatus */
    @ApiModelProperty("寻源状态(仅用于简易询价-inq)")
    private InqSouProjectStatusEnum extProjectStatus;

    /** @see InqSouProject#getInquiryType */
    @ApiModelProperty("询价类型(仅用于简易询价-inq)[字典值: SOU_INQUIRY_TYPE]")
    private String inquiryType;

    /**
     * @see InqSouProject#getExchangeRateType
     * @see BidSouProject#getExchangeRateType
     * @see BrgSouProject#getExchangeRateType
     * @see CompSouProject#getExchangeRateType
     */
    @ApiModelProperty("汇率类型(仅用于简易询价-inq/招投标-bid/项目式询价-brg/竞价-comp)[字典值: EXCHANGE_RATE_TYPE]")
    private String exchangeRateType;

    /**
     * @see InqSouProject#getCurrencyExchangeDate
     * @see BidSouProject#getCurrencyExchangeDate
     * @see BrgSouProject#getCurrencyExchangeDate
     * @see CompSouProject#getCurrencyExchangeDate
     */
    @ApiModelProperty("币种转换日期(仅用于简易询价-inq/招投标-bid/项目式询价-brg/竞价-comp)")
    private Date currencyExchangeDate;

    /** @see InqSouProject#getIsTargetPriceOk */
    @ApiModelProperty("是否已设定目标价(仅用于简易询价-inq)")
    private Enable isTargetPriceOk;

    /**
     * @see InqSouProject#getExcludeBlackVendors
     * @see BidSouProject#getExcludeBlackVendors
     * @see BrgSouProject#getExcludeBlackVendors
     */
    @ApiModelProperty("推荐控制 -- 是否排除黑名单供应商(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private Enable excludeBlackVendors;

    /**
     * @see InqSouProject#getExcludeNoCurrentOrgVendors
     * @see BidSouProject#getExcludeNoCurrentOrgVendors
     * @see BrgSouProject#getExcludeNoCurrentOrgVendors
     */
    @ApiModelProperty("推荐控制 -- 是否排除非本业务实体供应商(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private Enable excludeNoCurrentOrgVendors;

    /**
     * @see InqSouProject#getExcludeOrgQuitVendors
     * @see BidSouProject#getExcludeOrgQuitVendors
     * @see BrgSouProject#getExcludeOrgQuitVendors
     */
    @ApiModelProperty("推荐控制 -- 是否排除业务实体退出供应商(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private Enable excludeOrgQuitVendors;

    /**
     * @see InqSouProject#getExcludeOrgCategoryStatus
     * @see BidSouProject#getExcludeOrgCategoryStatus
     * @see BrgSouProject#getExcludeOrgCategoryStatus
     */
    @ApiModelProperty("推荐控制 -- 需要排除指定品类状态的供应商(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private String excludeOrgCategoryStatus;

    /**
     * @see BidSouProject#getBudgetAmount
     * @see BrgSouProject#getBudgetAmount
     * @see CompSouProject#getBudgetAmount
     */
    @ApiModelProperty("预算金额(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private BigDecimal budgetAmount;

    /**
     * @see BidSouProject#getBargainType
     * @see BrgSouProject#getBargainType
     */
    @ApiModelProperty("询价类型(仅用于招投标-bid/项目式询价-brg)(字典值: SOU_BID_TYPE)")
    private BidSouTypeEnum bargainType;

    /**
     * @see BidSouProject#getRequireDesc
     * @see BrgSouProject#getRequireDesc
     * @see CompSouProject#getRequireDesc
     */
    @ApiModelProperty("需求简述(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String requireDesc;

    /**
     * @see BidSouProject#getBondAmount
     * @see BrgSouProject#getBondAmount
     * @see CompSouProject#getBondAmount
     */
    @ApiModelProperty("商务要求 -- 保证金金额(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private BigDecimal bondAmount;

    /**
     * @see BidSouProject#getBondDesc
     * @see BrgSouProject#getBondDesc
     * @see CompSouProject#getBondDesc
     */
    @ApiModelProperty("商务要求 -- 保证金说明(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String bondDesc;

    /**
     * @see BidSouProject#getBondMethod
     * @see BrgSouProject#getBondMethod
     * @see CompSouProject#getBondMethod
     */
    @ApiModelProperty("商务要求 -- 保证金提交方式(仅用于招投标-bid/项目式询价-brg/竞价)[字典值: BID_BOND_SUBMISSION]")
    private String bondMethod;

    /**
     * @see BidSouProject#getBondEndTime
     * @see BrgSouProject#getBondEndTime
     * @see CompSouProject#getBondEndTime
     */
    @ApiModelProperty("商务要求 -- 保证金提交截止时间(仅用于招投标-bid/项目式询价-brg/竞价)")
    private Date bondEndTime;

    /**
     * @see BidSouProject#getBankAccountNum
     * @see BrgSouProject#getBankAccountNum
     * @see CompSouProject#getBankAccountNum
     */
    @ApiModelProperty("商务要求 -- 保证金缴纳账号(仅用于招投标-bid/项目式询价-brg/竞价)")
    private String bankAccountNum;

    /**
     * @see BidSouProject#getBankAccountName
     * @see BrgSouProject#getBankAccountName
     * @see CompSouProject#getBankAccountName
     */
    @ApiModelProperty("商务要求 -- 账户名称(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String bankAccountName;

    /**
     * @see BidSouProject#getBankBranchName
     * @see BrgSouProject#getBankBranchName
     * @see CompSouProject#getBankBranchName
     */
    @ApiModelProperty("商务要求 -- 开户支行(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String bankBranchName;

    /**
     * @see BidSouProject#getPublicLowestPrice
     * @see BrgSouProject#getPublicLowestPrice
     */
    @ApiModelProperty("投标控制 -- 是否向供应商公开上轮最低价(仅用于招投标-bid/项目式询价-brg)")
    private Enable publicLowestPrice;

    /**
     * @see BidSouProject#getPublicTotalRank
     * @see BrgSouProject#getPublicTotalRank
     */
    @ApiModelProperty("投标控制 -- 是否向供应商公开上轮排名结果(仅用于招投标-bid/项目式询价-brg)")
    private Enable publicTotalRank;

    /**
     * @see BidSouProject#getPublicTargetPrice
     * @see BrgSouProject#getPublicTargetPrice
     */
    @ApiModelProperty("投标控制 -- 是否向供应商公开拦标价(仅用于招投标-bid/项目式询价-brg)")
    private Enable publicTargetPrice;

    /**
     * @see BidSouProject#getVisibleRankResult
     * @see BrgSouProject#getVisibleRankResult
     */
    @ApiModelProperty("投标控制 -- 允许供应商查看最终排名结果(仅用于招投标-bid/项目式询价-brg)")
    private Enable visibleRankResult;

    /**
     * @see BidSouProject#getVisibleFinalPrice
     * @see BrgSouProject#getVisibleFinalPrice
     */
    @ApiModelProperty("投标控制 -- 允许供应商查看中标价(仅用于招投标-bid/项目式询价-brg)")
    private Enable visibleFinalPrice;

    /**
     * @see BidSouProject#getVisibleWinVendor
     * @see BrgSouProject#getVisibleWinVendor
     */
    @ApiModelProperty("投标控制 -- 允许供应商查看中标供应商(仅用于招投标-bid/项目式询价-brg)")
    private Enable visibleWinVendor;

    /** @see CompSouProject#getMinPercent */
    @ApiModelProperty("最小涨/跌幅百分比(仅用于竞价-comp)")
    private BigDecimal minPercent;

    /** @see CompSouProject#getMinAmount */
    @ApiModelProperty("最小涨/跌金额(仅用于竞价-comp)")
    private BigDecimal minAmount;

    /** @see CompSouProject#getExtendTrigger */
    @ApiModelProperty("截止至前几分钟(仅用于竞价-comp)")
    private BigDecimal extendTrigger;

    /** @see CompSouProject#getExtendMinute */
    @ApiModelProperty("延长多少分钟(仅用于竞价-comp)")
    private BigDecimal extendMinute;

    /** @see CompSouProject#getMaxWinVendorCount */
    @ApiModelProperty("中标供应商数量(仅用于竞价-comp)")
    private Integer maxWinVendorCount;

    public static PageInfo<ApiSouProjectSwaggerVO> convert(PageInfo<SouProject> page) {
        PageInfo<ApiSouProjectSwaggerVO> pageInfo = new PageInfo<>();
        pageInfo.setList(new ArrayList<>(page.getList().size())); {
            page.getList().forEach(souProject -> {
                pageInfo.getList().add(SouObjectXUtil.convertTargetObj(souProject, ApiSouProjectSwaggerVO.class));
            });
        }
        pageInfo.setPageNum(page.getPageNum());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

}
