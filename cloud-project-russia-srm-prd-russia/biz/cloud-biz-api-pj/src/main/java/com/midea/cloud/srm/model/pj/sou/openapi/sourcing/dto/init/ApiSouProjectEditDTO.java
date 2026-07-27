package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.enums.SouRulesEnums;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderWayEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 寻源openAPI - 寻源单
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ApiSouProjectEditDTO extends BaseObjectX {

    /**
     * @see SouProject#getProjectId
     */
    @ApiModelProperty("ID")
    private Long projectId;

    /**
     * @see SouProject#getSouNo
     */
    @ApiModelProperty("寻源单号")
    private String souNo;

    /**
     * @see SouProject#getSouName
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "寻源单名称(长度限制100)", required = true)
    private String souName = "竞价" + System.currentTimeMillis();

    /**
     * @see SouProject#getProcessConfigId
     */
    @ApiModelProperty(value = "流程配置ID", required = true)
    private Long processConfigId;

    /**
     * @see SouProject#getScoreRuleType
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "评选方式(低价/高价/综合)【评分规则】", required = true)
    private SouScoreRuleTypeEnum scoreRuleType = SouScoreRuleTypeEnum.MIN_PRICE;

    /**
     * @see SouProject#getScoreTemplateId
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "评分模板ID(专用于综合评分)", required = true)
    private Long scoreTemplateId = 1L;

    /**
     * @see SouProject#getScoreTemplateName()
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "评分模板名称(专用于综合评分)", required = true)
    private String scoreTemplateName = "竞价";

    /**
     * @see SouProject#getStandardCurrency
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "本位币(长度限制20)", required = true)
    private String standardCurrency = "RMB";

    /**
     * @see SouProject#getPricePrecision
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "本位币价格精度", required = true)
    private Integer pricePrecision = 4;

    /**
     * @see SouProject#getNeedEncryptPrice
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("投标控制 -- 是否密封报价(如果密封报价后，则必须等到商务开标后才可)")
    private Enable needEncryptPrice = Enable.N;

    /**
     * @see SouProject#getOrderSite
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("预计报价地点(长度限制300)")
    private String orderSite = "长城";

    /**
     * 否
     * @see SouProject#getIsSyncToPriceLibrary
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("是否同步至价格库")
    private Enable isSyncToPriceLibrary = Enable.N;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("生成价格审批单方式")
    private String generatePriceApprovalType = "";

    /**
     * @see SouProject#getNeedPwdOperations
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("需要密码解密的操作")
    private String needPwdOperations = "";

    /**
     * @see SouProject#getAllowItemChange
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("是否允许物料变更")
    private Enable allowItemChange = Enable.N;

    /**
     * @see SouProject#getAllowNewVendors
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("是否允许中途追加供应商")
    private Enable allowNewVendors = Enable.N;

    /**
     * @see SouProject#getAllowProxyOrder
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("是否允许代理报价")
    private Enable allowProxyOrder = Enable.N;

    /**
     * 当前 时间
     * @see SouProject#getPriceStartTime
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("价格有效期从（原定价开始时间）")
    private Date priceStartTime = new Date();

    /**
     * 当前 时间
     * @see SouProject#getPriceEndTime
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("价格有效期到（原定价结束时间）")
    private Date priceEndTime = new Date();

    /**
     * @see SouProject#getSignUpStartTime
     */
    @ApiModelProperty("报名开始时间")
    private Date signUpStartTime;

    /**
     * @see SouProject#getSignUpEndTime
     */
    @ApiModelProperty("报名截止时间")
    private Date signUpEndTime;

    /**
     * @see SouProject#getOrderStartTime
     */
    @ApiModelProperty("报价开始时间")
    private Date orderStartTime;

    /**
     * @see SouProject#getOrderEndTime
     */
    @ApiModelProperty(value = "报价截止时间", required = true)
    private Date orderEndTime;

    /**
     * @see SouProject#getEarliestBusinessOpenTime
     */
    @ApiModelProperty("最早开标时间")
    private Date earliestBusinessOpenTime;

    // ---------------------------------------------------------- 供应商相关控制 -------------------------------------------------------
    /**
     * @see SouProject#getPublishScope
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "发布范围(邀请/公开)", required = true)
    private SouPublishScopeEnum publishScope = SouPublishScopeEnum.INVITE_TENDER;

    /**
     * @see SouProject#getOrderWay
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "报价方式(单项/组合)", required = true)
    private SouOrderWayEnum orderWay = SouOrderWayEnum.SINGLE;

    /**
     * @see SouProject#getOrderType
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "报价类型(普通/公式/模型/...)", required = true)
    private SouOrderTypeEnum orderType = SouOrderTypeEnum.SIMPLE;

    /**
     * @see SouProject#getAllowWithdraw
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("投标控制 -- 是否允许供应商撤回报价(Y/N)")
    private Enable allowWithdraw = Enable.N;

    /**
     * @see SouProject#getAllowPartPrice
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("投标控制 -- 是否允许供应商只对部分物料报价(Y/N)")
    private Enable allowPartPrice = Enable.N;

    /**
     * @see SouProject#getIsPriceNotax
     */
    @ApiModelProperty("Y-供应商报价时使用未税价/N-供应商报价时使用含税价")
    private Enable isPriceNotax;

    // --------------------------------------------------------- 采购商额外信息 --------------------------------------------------------
    /**
     * @see SouProject#getLinkman
     */
    @ApiModelProperty("联系人")
    private String linkman;

    /**
     * @see SouProject#getTel
     */
    @ApiModelProperty("电话")
    private String tel;

    /**
     * @see SouProject#getEmail
     */
    @ApiModelProperty("邮箱")
    private String email;

    /**
     * @see SouProject#getRemark
     */
    @ApiModelProperty("备注")
    private String remark;

    // ---------------------------------------------------------- 关联上游单据 ---------------------------------------------------------
    /**
     * @see SouProject#getSourceFromType
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty(value = "来源类型(长度限制20)", required = true)
    private String sourceFromType = "PURCHASE_REQ";

    /**
     * @see SouProject#getSourceFromId
     */
    @ApiModelProperty("来源单据ID")
    private Long sourceFromId;

    /**
     * @see SouProject#getSourceFromNo
     */
    @ApiModelProperty("来源单据号(长度限制80)")
    private String sourceFromNo;

    // ---------------------------------------------------------- 报价模板信息 ---------------------------------------------------------
    /**
     * @see SouProject#getQuoteTempId
     */
    @ApiModelProperty("报价模板id")
    protected Long quoteTempId;

    /**
     * @see SouProject#getQuoteTempName
     */
    @ApiModelProperty("报价模板名称")
    protected String quoteTempName;

    // ---------------------------------------------------------- 保证金信息 ---------------------------------------------------------
    /**
     * @see CompSouProject#getBondAmount
     */
    @ApiModelProperty("商务要求 -- 保证金金额")
    private BigDecimal bondAmount;

    /**
     * @see CompSouProject#getBondDesc
     */
    @ApiModelProperty("商务要求 -- 保证金说明")
    private String bondDesc;

    /**
     * @see CompSouProject#getBondMethod
     */
    @ApiModelProperty("商务要求 -- 保证金提交方式[字典值: BID_BOND_SUBMISSION]")
    private String bondMethod;

    /**
     * @see CompSouProject#getBondEndTime
     */
    @ApiModelProperty("商务要求 -- 保证金提交截止时间")
    private Date bondEndTime;

    /**
     * @see CompSouProject#getBankAccountNum
     */
    @ApiModelProperty("商务要求 -- 保证金缴纳账号")
    private String bankAccountNum;

    /**
     * @see CompSouProject#getBankAccountName
     */
    @ApiModelProperty("商务要求 -- 账户名称")
    private String bankAccountName;

    /**
     * @see CompSouProject#getBankBranchName
     */
    @ApiModelProperty("商务要求 -- 开户支行")
    private String bankBranchName;

    /**
     * 竞价规则
     */
    @ApiModelProperty("竞价规则")
    private SouRulesEnums souRules;

    @ApiModelProperty("公开规则")
    private String publicRules;

    @ApiModelProperty("每项物资报价次数")
    private Integer orderNum;

    @ApiModelProperty("报价上限")
    private Integer quoteCap;

}
