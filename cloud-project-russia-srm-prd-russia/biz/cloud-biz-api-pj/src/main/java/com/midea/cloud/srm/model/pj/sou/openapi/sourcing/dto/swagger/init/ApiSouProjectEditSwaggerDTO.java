package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.swagger.init;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.bid.enums.BidSouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.init.ApiBidSouProjectEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.init.ApiBrgSouProjectEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouProjectEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.init.ApiInqSouProjectEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectEditDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderWayEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 寻源单 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 * PS: 来源于 {@link ApiSouProjectEditDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "寻源单")
@EqualsAndHashCode(callSuper = true)
public class ApiSouProjectEditSwaggerDTO extends BaseObjectX {

    /** @see ApiSouProjectEditDTO#getProjectId */
    @ApiModelProperty("ID")
    private Long projectId;

    /** @see ApiSouProjectEditDTO#getSouNo */
    @ApiModelProperty("寻源单号")
    private String souNo;

    /** @see ApiSouProjectEditDTO#getSouName */
    @ApiModelProperty(value = "寻源单名称(长度限制100)", required = true)
    private String souName;

    /** @see ApiSouProjectEditDTO#getProcessConfigId */
    @ApiModelProperty(value = "流程配置ID", required = true)
    private Long processConfigId;

    /** @see ApiSouProjectEditDTO#getScoreRuleType */
    @ApiModelProperty(value = "评选方式(低价/高价/综合)【评分规则】", required = true)
    private SouScoreRuleTypeEnum scoreRuleType;

    /** @see ApiSouProjectEditDTO#getScoreTemplateId */
    @ApiModelProperty(value = "评分模板ID(专用于综合评分)", required = true)
    private Long scoreTemplateId;

    /** @see ApiSouProjectEditDTO#getScoreTemplateName()  */
    @ApiModelProperty(value = "评分模板名称(专用于综合评分)", required = true)
    private String scoreTemplateName;

    /** @see ApiSouProjectEditDTO#getStandardCurrency */
    @ApiModelProperty(value = "本位币(长度限制20)", required = true)
    private String standardCurrency;

    /** @see ApiSouProjectEditDTO#getPricePrecision */
    @ApiModelProperty(value = "本位币价格精度", required = true)
    private Integer pricePrecision;

    /** @see ApiSouProjectEditDTO#getNeedEncryptPrice */
    @ApiModelProperty("投标控制 -- 是否密封报价(如果密封报价后，则必须等到商务开标后才可)")
    private Enable needEncryptPrice;

    /** @see ApiSouProjectEditDTO#getOrderSite */
    @ApiModelProperty("预计报价地点(长度限制300)")
    private String orderSite;

    /** @see ApiSouProjectEditDTO#getIsSyncToPriceLibrary */
    @ApiModelProperty("是否同步至价格库")
    private Enable isSyncToPriceLibrary;

    /** @see ApiSouProjectEditDTO#getNeedPwdOperations */
    @ApiModelProperty("需要密码解密的操作")
    private String needPwdOperations;

    /** @see ApiSouProjectEditDTO#getAllowItemChange */
    @ApiModelProperty("是否允许物料变更")
    private Enable allowItemChange;

    /** @see ApiSouProjectEditDTO#getAllowNewVendors */
    @ApiModelProperty("是否允许中途追加供应商")
    private Enable allowNewVendors;

    /** @see ApiSouProjectEditDTO#getAllowProxyOrder */
    @ApiModelProperty("是否允许代理报价")
    private Enable allowProxyOrder;

    // ---------------------------------------------------------- 时间节点相关 ---------------------------------------------------------
    /** @see ApiSouProjectEditDTO#getPriceStartTime */
    @ApiModelProperty("价格有效期从（原定价开始时间）")
    private Date priceStartTime;

    /** @see ApiSouProjectEditDTO#getPriceEndTime */
    @ApiModelProperty("价格有效期到（原定价结束时间）")
    private Date priceEndTime;

    /** @see ApiSouProjectEditDTO#getSignUpStartTime */
    @ApiModelProperty("报名开始时间")
    private Date signUpStartTime;

    /** @see ApiSouProjectEditDTO#getSignUpEndTime */
    @ApiModelProperty("报名截止时间")
    private Date signUpEndTime;

    /** @see ApiSouProjectEditDTO#getOrderStartTime */
    @ApiModelProperty("报价开始时间")
    private Date orderStartTime;

    /** @see ApiSouProjectEditDTO#getOrderEndTime */
    @ApiModelProperty(value = "报价截止时间", required = true)
    private Date orderEndTime;

    /** @see ApiSouProjectEditDTO#getEarliestBusinessOpenTime */
    @ApiModelProperty("最早开标时间")
    private Date earliestBusinessOpenTime;

    // ---------------------------------------------------------- 供应商相关控制 -------------------------------------------------------
    /** @see ApiSouProjectEditDTO#getPublishScope */
    @ApiModelProperty(value = "发布范围(邀请/公开)", required = true)
    private SouPublishScopeEnum publishScope;

    /** @see ApiSouProjectEditDTO#getOrderWay */
    @ApiModelProperty(value = "报价方式(单项/组合)", required = true)
    private SouOrderWayEnum orderWay;

    /** @see ApiSouProjectEditDTO#getOrderType */
    @ApiModelProperty(value = "报价类型(普通/公式/模型/...)", required = true)
    private SouOrderTypeEnum orderType;

    /** @see ApiSouProjectEditDTO#getAllowWithdraw */
    @ApiModelProperty("投标控制 -- 是否允许供应商撤回报价(Y/N)")
    private Enable allowWithdraw;

    /** @see ApiSouProjectEditDTO#getAllowPartPrice */
    @ApiModelProperty("投标控制 -- 是否允许供应商只对部分物料报价(Y/N)")
    private Enable allowPartPrice;

    /** @see ApiSouProjectEditDTO#getIsPriceNotax */
    @ApiModelProperty("Y-供应商报价时使用未税价/N-供应商报价时使用含税价")
    private Enable isPriceNotax;

    // --------------------------------------------------------- 采购商额外信息 --------------------------------------------------------
    /** @see ApiSouProjectEditDTO#getLinkman */
    @ApiModelProperty("联系人")
    private String linkman;

    /** @see ApiSouProjectEditDTO#getTel */
    @ApiModelProperty("电话")
    private String tel;

    /** @see ApiSouProjectEditDTO#getEmail */
    @ApiModelProperty("邮箱")
    private String email;

    /** @see ApiSouProjectEditDTO#getRemark */
    @ApiModelProperty("备注")
    private String remark;

    // ---------------------------------------------------------- 关联上游单据 ---------------------------------------------------------
    /** @see ApiSouProjectEditDTO#getSourceFromType */
    @ApiModelProperty(value = "来源类型(长度限制20)", required = true)
    private String sourceFromType;

    /** @see ApiSouProjectEditDTO#getSourceFromId */
    @ApiModelProperty("来源单据ID")
    private Long sourceFromId;

    /** @see ApiSouProjectEditDTO#getSourceFromNo */
    @ApiModelProperty("来源单据号(长度限制80)")
    private String sourceFromNo;

    // ----------------------------------------------------------- 寻源场景专用 --------------------------------------------------------
    /** @see ApiInqSouProjectEditDTO#getInquiryType */
    @ApiModelProperty("询价类型(仅用于简易询价-inq)[字典值: SOU_INQUIRY_TYPE]")
    private String inquiryType;

    /** @see ApiInqSouProjectEditDTO#getExchangeRateType */
    @ApiModelProperty("汇率类型(仅用于简易询价-inq/招投标-bid/项目式询价-brg/竞价-comp)[字典值: EXCHANGE_RATE_TYPE]")
    private String exchangeRateType;

    /** @see ApiInqSouProjectEditDTO#getCurrencyExchangeDate */
    @ApiModelProperty("币种转换日期(仅用于简易询价-inq/招投标-bid/项目式询价-brg/竞价-comp)")
    private Date currencyExchangeDate;

    /** @see ApiInqSouProjectEditDTO#getIsTargetPriceOk */
    @ApiModelProperty("是否已设定目标价(仅用于简易询价-inq)")
    private Enable isTargetPriceOk;

    /** @see ApiInqSouProjectEditDTO#getExcludeBlackVendors */
    @ApiModelProperty("推荐控制 -- 是否排除黑名单供应商(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private Enable excludeBlackVendors;

    /** @see ApiInqSouProjectEditDTO#getExcludeNoCurrentOrgVendors */
    @ApiModelProperty("推荐控制 -- 是否排除非本业务实体供应商(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private Enable excludeNoCurrentOrgVendors;

    /** @see ApiInqSouProjectEditDTO#getExcludeOrgQuitVendors */
    @ApiModelProperty("推荐控制 -- 是否排除业务实体退出供应商(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private Enable excludeOrgQuitVendors;

    /** @see ApiInqSouProjectEditDTO#getExcludeOrgCategoryStatus */
    @ApiModelProperty("推荐控制 -- 需要排除指定品类状态的供应商(仅用于简易询价-inq/招投标-bid/项目式询价-brg)")
    private String excludeOrgCategoryStatus;

    /**
     * @see ApiBidSouProjectEditDTO#getBudgetAmount
     * @see ApiBrgSouProjectEditDTO#getBudgetAmount
     */
    @ApiModelProperty("预算金额(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private BigDecimal budgetAmount;

    /**
     * @see ApiBidSouProjectEditDTO#getBargainType
     * @see ApiBrgSouProjectEditDTO#getBargainType
     */
    @ApiModelProperty("询价类型(仅用于招投标-bid/项目式询价-brg)")
    private BidSouTypeEnum bargainType;

    /**
     * @see ApiBidSouProjectEditDTO#getRequireDesc
     * @see ApiBrgSouProjectEditDTO#getRequireDesc
     */
    @ApiModelProperty("需求简述(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String requireDesc;

    /**
     * @see ApiBidSouProjectEditDTO#getBondAmount
     * @see ApiBrgSouProjectEditDTO#getBondAmount
     */
    @ApiModelProperty("商务要求 -- 保证金金额(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private BigDecimal bondAmount;

    /**
     * @see ApiBidSouProjectEditDTO#getBondDesc
     * @see ApiBrgSouProjectEditDTO#getBondDesc
     */
    @ApiModelProperty("商务要求 -- 保证金说明(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String bondDesc;

    /**
     * @see ApiBidSouProjectEditDTO#getBondMethod
     * @see ApiBrgSouProjectEditDTO#getBondMethod
     */
    @ApiModelProperty("商务要求 -- 保证金提交方式(仅用于招投标-bid/项目式询价-brg/竞价-comp)[字典值: BID_BOND_SUBMISSION]")
    private String bondMethod;

    /**
     * @see ApiBidSouProjectEditDTO#getBondEndTime
     * @see ApiBrgSouProjectEditDTO#getBondEndTime
     */
    @ApiModelProperty("商务要求 -- 保证金提交截止时间(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private Date bondEndTime;

    /**
     * @see ApiBidSouProjectEditDTO#getBankAccountNum
     * @see ApiBrgSouProjectEditDTO#getBankAccountNum
     */
    @ApiModelProperty("商务要求 -- 保证金缴纳账号(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String bankAccountNum;

    /**
     * @see ApiBidSouProjectEditDTO#getBankAccountName
     * @see ApiBrgSouProjectEditDTO#getBankAccountName
     */
    @ApiModelProperty("商务要求 -- 账户名称(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String bankAccountName;

    /**
     * @see ApiBidSouProjectEditDTO#getBankBranchName
     * @see ApiBrgSouProjectEditDTO#getBankBranchName
     */
    @ApiModelProperty("商务要求 -- 开户支行(仅用于招投标-bid/项目式询价-brg/竞价-comp)")
    private String bankBranchName;

    /**
     * @see ApiBidSouProjectEditDTO#getPublicLowestPrice
     * @see ApiBrgSouProjectEditDTO#getPublicLowestPrice
     */
    @ApiModelProperty("投标控制 -- 是否向供应商公开上轮最低价(仅用于招投标-bid/项目式询价-brg)")
    private Enable publicLowestPrice;

    /**
     * @see ApiBidSouProjectEditDTO#getPublicTotalRank
     * @see ApiBrgSouProjectEditDTO#getPublicTotalRank
     */
    @ApiModelProperty("投标控制 -- 是否向供应商公开上轮最低价(仅用于招投标-bid/项目式询价-brg)")
    private Enable publicTotalRank;

    /**
     * @see ApiBidSouProjectEditDTO#getPublicTargetPrice
     * @see ApiBrgSouProjectEditDTO#getPublicTargetPrice
     */
    @ApiModelProperty("投标控制 -- 是否向供应商公开拦标价(仅用于招投标-bid/项目式询价-brg)")
    private Enable publicTargetPrice;

    /**
     * @see ApiBidSouProjectEditDTO#getVisibleRankResult
     * @see ApiBrgSouProjectEditDTO#getVisibleRankResult
     */
    @ApiModelProperty("投标控制 -- 允许供应商查看最终排名结果(仅用于招投标-bid/项目式询价-brg)")
    private Enable visibleRankResult;

    /**
     * @see ApiBidSouProjectEditDTO#getVisibleFinalPrice
     * @see ApiBrgSouProjectEditDTO#getVisibleFinalPrice
     */
    @ApiModelProperty("投标控制 -- 允许供应商查看中标价(仅用于招投标-bid/项目式询价-brg)")
    private Enable visibleFinalPrice;

    /**
     * @see ApiBidSouProjectEditDTO#getVisibleWinVendor
     * @see ApiBrgSouProjectEditDTO#getVisibleWinVendor
     */
    @ApiModelProperty("投标控制 -- 允许供应商查看中标供应商(仅用于招投标-bid/项目式询价-brg)")
    private Enable visibleWinVendor;

    // ------------------------
    /** @see ApiCompSouProjectEditDTO#getMinPercent */
    @ApiModelProperty("最小涨/跌幅百分比(仅用于竞价-comp)")
    private BigDecimal minPercent;

    /** @see ApiCompSouProjectEditDTO#getMinAmount */
    @ApiModelProperty("最小涨/跌金额(仅用于竞价-comp)")
    private BigDecimal minAmount;

    /** @see ApiCompSouProjectEditDTO#getExtendTrigger */
    @ApiModelProperty("截止至前几分钟(仅用于竞价-comp)")
    private BigDecimal extendTrigger;

    /** @see ApiCompSouProjectEditDTO#getExtendMinute */
    @ApiModelProperty("延长多少分钟(仅用于竞价-comp)")
    private BigDecimal extendMinute;

    /** @see ApiCompSouProjectEditDTO#getMaxWinVendorCount */
    @ApiModelProperty("中标供应商数量(仅用于竞价-comp)")
    private Integer maxWinVendorCount;

}
