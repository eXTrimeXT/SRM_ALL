package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.score.entity.SouScoreTemplate;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 寻源核心-询价单信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_project")
@ApiModel(description = "寻源核心-询价单信息")
@SuppressWarnings("JavadocReference")
public class SouProject extends BaseEntity<SouProject> {

    @TableId("PROJECT_ID")
    @ApiModelProperty("ID")
    private Long projectId;

    /**
     * @see SequenceCodeConstant#SOU
     */
    @TableField("SOU_NO")
    @ApiModelProperty("具体的单据号生成规则由具体业务模块决定")
    private String souNo;

    //--------------- 基础信息 -----------------
    /**
     * 发布时间
     * PS: 如果预估报价开始时间早于发布时间，则实际报价开始时间是发布时间。
     * 如果预估报价开始时间晚于发布时间，则实际报价开始时间是预估报价开始时间。
     */
    @TableField("PUBLISH_TIME")
    @ApiModelProperty("发标时间")
    private Date publishTime;

    @TableField("SIGN_UP_START_TIME")
    @ApiModelProperty("报名开始时间")
    private Date signUpStartTime;

    @TableField("SIGN_UP_END_TIME")
    @ApiModelProperty("报名截止时间")
    private Date signUpEndTime;

    /**
     * @see SouRound#getOrderStartTime
     */
    @TableField("ORDER_START_TIME")
    @ApiModelProperty("报价开始时间(冗余字段)")
    private Date orderStartTime;

    /**
     * @see SouRound#getOrderEndTime
     */
    @TableField("ORDER_END_TIME")
    @ApiModelProperty("报价截止时间(冗余字段)")
    private Date orderEndTime;

    /** 竞价规则 */
    @TableField("SOU_RULES")
    @ApiModelProperty("竞价规则")
    private String souRules;

    @TableField("PUBLIC_RULES")
    @ApiModelProperty("公开规则")
    private String publicRules;

    @TableField("ORDER_NUM")
    @ApiModelProperty("每项物资报价次数")
    private Integer orderNum;

    @TableField("QUOTE_CAP")
    @ApiModelProperty("报价上限")
    private Integer quoteCap;

    @TableField("SOU_NAME")
    @ApiModelProperty("寻源单名称")
    private String souName;

    @ApiModelProperty(value = "板块")
    @TableField("ORG_BU_ID")
    private Long orgBuId;

    @ApiModelProperty(value = "板块编码")
    @TableField("ORG_BU_CODE")
    private String orgBuCode;

    @ApiModelProperty(value = "板块名称")
    @TableField("ORG_BU_NAME")
    private String orgBuName;

    @ApiModelProperty(value = "公司")
    @TableField("COMPANY_ID")
    private Long companyId;

    @ApiModelProperty(value = "公司编码")
    @TableField("COMPANY_CODE")
    private String companyCode;

    @ApiModelProperty(value = "公司名称")
    @TableField("COMPANY_NAME")
    private String COMPANY_NAME;

    @ApiModelProperty(value = "部门ID")
    @TableField("DEP_ID")
    private Long depId;

    @ApiModelProperty(value = "部门编码")
    @TableField("DEP_CODE")
    private String depCode;

    @ApiModelProperty(value = "部门名称")
    @TableField("DEP_NAME")
    private String depName;

    /**
     * @see SouTypeEnum
     */
    @TableField("SOU_TYPE")
    @ApiModelProperty("寻源类型(字典:SOU_TYPE)")
    private String souType;

    /**
     * @see SouProcessConfig#getProcessConfigId
     */
    @TableField("PROCESS_CONFIG_ID")
    @ApiModelProperty("流程配置ID")
    private Long processConfigId;

    @TableField("SCORE_RULE_TYPE")
    @ApiModelProperty("评选方式(低价/高价/综合)【评分规则】")
    private SouScoreRuleTypeEnum scoreRuleType;

    /**
     * @see SouScoreTemplate#getScoreTemplateId
     */
    @TableField("SCORE_TEMPLATE_ID")
    @ApiModelProperty("评分模板ID(专用于综合评分)")
    private Long scoreTemplateId;

    /**
     * @see SouScoreTemplate#getScoreTemplateName()
     */
    @TableField("SCORE_TEMPLATE_NAME")
    @ApiModelProperty("评分模板名称(专用于综合评分)")
    private String scoreTemplateName;

    @TableField("STANDARD_CURRENCY")
    @ApiModelProperty("本位币")
    private String standardCurrency;

    @TableField("PRICE_PRECISION")
    @ApiModelProperty("本位币价格精度")
    private Integer pricePrecision;

    @TableField("PROJECT_STATUS")
    @ApiModelProperty("项目状态")
    private SouProjectStatusEnum projectStatus;

    @TableField("CREATE_APPROVAL_STATUS")
    @ApiModelProperty("立项审核状态")
    private SouApprovalStatusEnum createApprovalStatus;

    /**
     * 投标控制 -- 是否密封报价
     * PS: 如果为"否"，则采购商可以实时看到供应商的报价信息
     * PS: 如果为"是"，则仅当本轮商务开标后，经过报价解密动作后，采购商才可查看供应商本轮报价信息
     * PS: 与 {@link #allowProxyOrder} 互斥
     */
    @TableField("NEED_ENCRYPT_PRICE")
    @ApiModelProperty("投标控制 -- 是否密封报价(如果密封报价后，则必须等到商务开标后才可)")
    private Enable needEncryptPrice;

    /**
     * @see SouRound#getRound
     */
    @TableField("CURRENT_ROUND")
    @ApiModelProperty("当前轮次(冗余字段)")
    private Integer currentRound;

    @TableField("ORDER_SITE")
    @ApiModelProperty("预计报价地点")
    private String orderSite;

    @TableField("IS_SYNC_TO_PRICE_LIBRARY")
    @ApiModelProperty("是否同步至价格库")
    private Enable isSyncToPriceLibrary;

    /**
     * @see SouGeneratePriceApprovalTypeEnum
     */
    @TableField("GENERATE_PRICE_APPROVAL_TYPE")
    @ApiModelProperty("生成价格审批单方式")
    private String generatePriceApprovalType;

    @TableField("CANCEL_REASON")
    @ApiModelProperty("作废原因")
    private String cancelReason;

    /**
     * {@link SouGroupOperateAuthEnum}的组合，逗号隔开
     *
     * @see SouGroup#getOperateAuth
     */
    @TableField("NEED_PWD_OPERATIONS")
    @ApiModelProperty("需要密码解密的操作(如技术标/商务标等)")
    private String needPwdOperations;

    /**
     * 寻源中途修改物料需求
     */
    @TableField("ALLOW_ITEM_CHANGE")
    @ApiModelProperty("是否允许物料变更")
    private Enable allowItemChange;

    /**
     * 寻源中途追加供应商
     */
    @TableField("ALLOW_NEW_VENDORS")
    @ApiModelProperty("是否允许追加供应商")
    private Enable allowNewVendors;

    /**
     * 该字段与 {@link #needEncryptPrice} 为互斥，密封报价时，是不允许代理报价的，不然采购商能够看到供应商报价信息，与密封报价不符
     */
    @TableField("ALLOW_PROXY_ORDER")
    @ApiModelProperty("是否允许代理报价")
    private Enable allowProxyOrder;

    /** 时间节点相关 */
    @TableField("PRICE_START_TIME")
    @ApiModelProperty("价格有效期从（原定价开始时间）")
    private Date priceStartTime;

    @TableField("PRICE_END_TIME")
    @ApiModelProperty("价格有效期到（原定价结束时间）")
    private Date priceEndTime;

    @TableField("TECH_OPEN")
    @ApiModelProperty("技术开标标识")
    private Enable techOpen;

    @TableField("TECH_OPEN_TIME")
    @ApiModelProperty("技术开标时间")
    private Date techOpenTime;

    @TableField("EARLIEST_BUSINESS_OPEN_TIME")
    @ApiModelProperty("最早开标时间")
    private Date earliestBusinessOpenTime;

    /** 供应商相关控制 */
    @TableField("PUBLISH_SCOPE")
    @ApiModelProperty("发布范围(邀请/公开)")
    private SouPublishScopeEnum publishScope;

    @TableField("ORDER_WAY")
    @ApiModelProperty("报价方式(单项/组合)")
    private SouOrderWayEnum orderWay;

    @TableField("ORDER_TYPE")
    @ApiModelProperty("报价类型(普通/公式/模型/...)")
    private SouOrderTypeEnum orderType;

    @TableField("ALLOW_WITHDRAW")
    @ApiModelProperty("投标控制 -- 是否允许供应商撤回报价(Y/N)")
    private Enable allowWithdraw;

    @TableField("ALLOW_PART_PRICE")
    @ApiModelProperty("投标控制 -- 是否允许供应商只对部分物料报价(Y/N)")
    private Enable allowPartPrice;

    @TableField("IS_PRICE_NOTAX")
    @ApiModelProperty("Y-供应商报价时使用未税价/N-供应商报价时使用含税价")
    private Enable isPriceNotax;

    @TableField("INVITE_COUNT")
    @ApiModelProperty("本轮应报价供应商数量")
    private Integer inviteCount;

    @TableField("ORDER_COUNT")
    @ApiModelProperty("本轮已报价供应商数量")
    private Integer orderCount;

    /** 采购商额外信息 */
    @TableField("LINKMAN")
    @ApiModelProperty("联系人")
    private String linkman;

    @TableField("TEL")
    @ApiModelProperty("电话")
    private String tel;

    @TableField("EMAIL")
    @ApiModelProperty("邮箱")
    private String email;

    @TableField("REMARK")
    @ApiModelProperty("备注")
    private String remark;

    // ---------------------------------------------------------- 关联上游单据 ---------------------------------------------------------
    /**
     * @see SouSourceFromTypeEnum
     */
    @ApiModelProperty("来源类型")
    @TableField("SOURCE_FROM_TYPE")
    private String sourceFromType;

    @ApiModelProperty("来源单据ID")
    @TableField("SOURCE_FROM_ID")
    private Long sourceFromId;

    @ApiModelProperty("来源单据号")
    @TableField("SOURCE_FROM_NO")
    private String sourceFromNo;

    // ---------------------------------------------------------- 报价模板信息 ---------------------------------------------------------
    /**
     * @see SouQuoteTemp#getTempId
     */
    @ApiModelProperty("报价模板ID")
    @TableField("QUOTE_TEMP_ID")
    private Long quoteTempId;

    /**
     * @see SouQuoteTemp#getTempName
     */
    @ApiModelProperty("报价模板名称")
    @TableField("QUOTE_TEMP_NAME")
    private String quoteTempName;


    @ApiModelProperty("招标编号")
    @TableField("ext_Project_No")
    private String extProjectNo;

}
