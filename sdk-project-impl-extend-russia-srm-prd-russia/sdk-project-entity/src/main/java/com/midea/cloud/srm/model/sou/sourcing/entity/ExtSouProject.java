package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.sourcing.enums.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
@TableName("scc_sou_project")
public class ExtSouProject extends BaseEntity {

    @TableId("project_id")
    @ApiModelProperty("ID")
    private Long projectId;
    @TableField("sou_no")
    @ApiModelProperty("具体的单据号生成规则由具体业务模块决定")
    private String souNo;
    @TableField("sou_name")
    @ApiModelProperty("寻源单名称")
    private String souName;
    @TableField("sou_type")
    @ApiModelProperty("寻源类型(字典:SOU_TYPE)")
    private String souType;
    @TableField("process_config_id")
    @ApiModelProperty("流程配置ID")
    private Long processConfigId;
    @TableField("score_rule_type")
    @ApiModelProperty("评选方式(低价/高价/综合)【评分规则】")
    private String scoreRuleType;
    @TableField("score_template_id")
    @ApiModelProperty("评分模板ID(专用于综合评分)")
    private Long scoreTemplateId;
    @TableField("score_template_name")
    @ApiModelProperty("评分模板名称(专用于综合评分)")
    private String scoreTemplateName;
    @TableField("standard_currency")
    @ApiModelProperty("本位币")
    private String standardCurrency;
    @TableField("price_precision")
    @ApiModelProperty("本位币价格精度")
    private Integer pricePrecision;
    @TableField("project_status")
    @ApiModelProperty("项目状态")
    private String projectStatus;
    @TableField("create_approval_status")
    @ApiModelProperty("立项审核状态")
    private String createApprovalStatus;
    @TableField("need_encrypt_price")
    @ApiModelProperty("投标控制 -- 是否密封报价(如果密封报价后，则必须等到商务开标后才可)")
    private Enable needEncryptPrice;
    @TableField("current_round")
    @ApiModelProperty("当前轮次(冗余字段)")
    private Integer currentRound;
    @TableField("order_site")
    @ApiModelProperty("预计报价地点")
    private String orderSite;
    @TableField("is_sync_to_price_library")
    @ApiModelProperty("是否同步至价格库")
    private Enable isSyncToPriceLibrary;
    @TableField("cancel_reason")
    @ApiModelProperty("作废原因")
    private String cancelReason;
    @TableField("need_pwd_operations")
    @ApiModelProperty("需要密码解密的操作(如技术标/商务标等)")
    private String needPwdOperations;
    @TableField("allow_item_change")
    @ApiModelProperty("是否允许物料变更")
    private Enable allowItemChange;
    @TableField("allow_new_vendors")
    @ApiModelProperty("是否允许追加供应商")
    private Enable allowNewVendors;
    @TableField("allow_proxy_order")
    @ApiModelProperty("是否允许代理报价")
    private Enable allowProxyOrder;
    @TableField("price_start_time")
    @ApiModelProperty("价格有效期从（原定价开始时间）")
    private Date priceStartTime;
    @TableField("price_end_time")
    @ApiModelProperty("价格有效期到（原定价结束时间）")
    private Date priceEndTime;
    @TableField("publish_time")
    @ApiModelProperty("发布时间")
    private Date publishTime;
    @TableField("sign_up_start_time")
    @ApiModelProperty("报名开始时间")
    private Date signUpStartTime;
    @TableField("sign_up_end_time")
    @ApiModelProperty("报名截止时间")
    private Date signUpEndTime;
    @TableField("order_start_time")
    @ApiModelProperty("报价开始时间(冗余字段)")
    private Date orderStartTime;
    @TableField("order_end_time")
    @ApiModelProperty("报价截止时间(冗余字段)")
    private Date orderEndTime;
    @TableField("tech_open")
    @ApiModelProperty("技术开标标识")
    private Enable techOpen;
    @TableField("tech_open_time")
    @ApiModelProperty("技术开标时间")
    private Date techOpenTime;
    @TableField("earliest_business_open_time")
    @ApiModelProperty("最早开标时间")
    private Date earliestBusinessOpenTime;
    @TableField("publish_scope")
    @ApiModelProperty("发布范围(邀请/公开)")
    private String publishScope;
    @TableField("order_way")
    @ApiModelProperty("报价方式(单项/组合)")
    private String orderWay;
    @TableField("order_type")
    @ApiModelProperty("报价类型(普通/公式/模型/...)")
    private String orderType;
    @TableField("allow_withdraw")
    @ApiModelProperty("投标控制 -- 是否允许供应商撤回报价(Y/N)")
    private Enable allowWithdraw;
    @TableField("allow_part_price")
    @ApiModelProperty("投标控制 -- 是否允许供应商只对部分物料报价(Y/N)")
    private Enable allowPartPrice;
    @TableField("is_price_notax")
    @ApiModelProperty("Y-供应商报价时使用未税价/N-供应商报价时使用含税价")
    private Enable isPriceNotax;
    @TableField("invite_count")
    @ApiModelProperty("本轮应报价供应商数量")
    private Integer inviteCount;
    @TableField("order_count")
    @ApiModelProperty("本轮已报价供应商数量")
    private Integer orderCount;
    @TableField("linkman")
    @ApiModelProperty("联系人")
    private String linkman;
    @TableField("tel")
    @ApiModelProperty("电话")
    private String tel;
    @TableField("email")
    @ApiModelProperty("邮箱")
    private String email;
    @TableField("remark")
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("来源类型")
    @TableField("source_from_type")
    private String sourceFromType;
    @ApiModelProperty("来源单据ID")
    @TableField("source_from_id")
    private Long sourceFromId;
    @ApiModelProperty("来源单据号")
    @TableField("source_from_no")
    private String sourceFromNo;
    @ApiModelProperty("报价模板ID")
    @TableField("quote_temp_id")
    private Long quoteTempId;
    @ApiModelProperty("报价模板名称")
    @TableField("quote_temp_name")
    private String quoteTempName;
    /**
     * 招标项目编号
     */
    private String extProjectNo;
    /**
     * 收标方式
     */
    private String extSouMode;
    /**
     * 评分规则（字典）
     */
    private String extScoreRule;
    /**
     * 品类ID
     */
    private Long extCategoryId;
    /**
     * 品类编码
     */
    private String extCategoryCode;
    /**
     * 品类
     */
    private String extCategoryName;
    /**
     * 是否缴纳保证金
     */
    private String extEarnestFlag;
    /**
     * 保证金缴纳金额（万元）
     */
    private BigDecimal extEarnestAmount;
    /**
     * 开户银行
     */
    private String extBankName;
    /**
     * 开户行号
     */
    private String extBankNumber;
    /**
     * 开户账号
     */
    private String extBankAccount;
    /**
     * 开户户名
     */
    private String extBankAccountName;
    /**
     * 推荐供应商单号
     */
    private String extRecommendNo;
    /**
     * 隐藏评标关键信息
     */
    private String extHideKeyInfo;
    /**
     * 板块ID
     */
    private Long extOrgBuId;
    /**
     * 板块编码
     */
    private String extOrgBuCode;
    /**
     * 板块名称
     */
    private String extOrgBuName;
    /**
     * 公司ID
     */
    private Long extOrgOuId;
    /**
     * 公司编码
     */
    private String extOrgOuCode;
    /**
     * 公司名称
     */
    private String extOrgOuName;
    /**
     * 申请部门
     */
    private String extApplicantDepart;
    /**
     * 申请人
     */
    private String extApplicant;
    /**
     * 投资编号
     */
    private String extInvestNo;
    /**
     * 预算
     */
    private BigDecimal extBudget;
    /**
     * 规模数量
     */
    private String extScaleQuantity;
    /**
     * 是否指定评标人
     */
    private String extAssignEvaluator;
    /**
     * 技术负责人
     */
    private String extTechPrincipal;
    /**
     * 招标负责人
     */
    private String extSouPrincipal;
    /**
     * 招标流程
     */
    private String extSouProcess;

    @ApiModelProperty("评标总人数")
    private Integer extBidEvaluatorNum;

    @ApiModelProperty("要求高级专家人数")
    private Integer extAskSeniorExpertNum;

    @ApiModelProperty("专家抽取范围，字典：SOU_EXPERT_RANGE")
    private String extExpertRange;

    @ApiModelProperty("技术负责人联系方式")
    private String extTechPrincipalTel;

}
