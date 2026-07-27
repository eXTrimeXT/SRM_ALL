package com.midea.cloud.srm.model.sou.report.souschedules.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.report.souschedules.entity.SccNpmSouSchedule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/03/13/ $
 * @Description:
 */
@Data
public class SccNpmSouScheduleReportDto extends SccNpmSouSchedule {

    @ApiModelProperty("具体的单据号生成规则由具体业务模块决定")
    private String souNo;
    @ApiModelProperty("寻源单名称")
    private String souName;
    @ApiModelProperty("寻源类型(字典:SOU_TYPE)")
    private String souType;
    @ApiModelProperty("流程配置ID")
    private Long processConfigId;
    @ApiModelProperty("评选方式(低价/高价/综合)【评分规则】")
    private String scoreRuleType;
    @ApiModelProperty("评分模板ID(专用于综合评分)")
    private Long scoreTemplateId;
    @ApiModelProperty("评分模板名称(专用于综合评分)")
    private String scoreTemplateName;
    @ApiModelProperty("本位币")
    private String standardCurrency;
    @ApiModelProperty("本位币价格精度")
    private Integer pricePrecision;
    @ApiModelProperty("项目状态")
    private String projectStatus;
    @ApiModelProperty("立项审核状态")
    private String createApprovalStatus;
    @ApiModelProperty("投标控制 -- 是否密封报价(如果密封报价后，则必须等到商务开标后才可)")
    private Enable needEncryptPrice;
    @ApiModelProperty("当前轮次(冗余字段)")
    private Integer currentRound;
    @ApiModelProperty("预计报价地点")
    private String orderSite;
    @ApiModelProperty("是否同步至价格库")
    private Enable isSyncToPriceLibrary;
    @ApiModelProperty("需要密码解密的操作(如技术标/商务标等)")
    private String needPwdOperations;
    @ApiModelProperty("是否允许物料变更")
    private Enable allowItemChange;
    @ApiModelProperty("是否允许追加供应商")
    private Enable allowNewVendors;
    @ApiModelProperty("是否允许代理报价")
    private Enable allowProxyOrder;
    @ApiModelProperty("价格有效期从（原定价开始时间）")
    private Date priceStartTime;
    @ApiModelProperty("价格有效期到（原定价结束时间）")
    private Date priceEndTime;
    @ApiModelProperty("报名开始时间")
    private Date signUpStartTime;
    @ApiModelProperty("报名截止时间")
    private Date signUpEndTime;
    @ApiModelProperty("报价开始时间(冗余字段)")
    private Date orderStartTime;
    @ApiModelProperty("报价截止时间(冗余字段)")
    private Date orderEndTime;
    @ApiModelProperty("技术开标标识")
    private Enable techOpen;
    @ApiModelProperty("技术开标时间")
    private Date techOpenTime;
    @ApiModelProperty("最早开标时间")
    private Date earliestBusinessOpenTime;
    @ApiModelProperty("发布范围(邀请/公开)")
    private String publishScope;
    @ApiModelProperty("报价方式(单项/组合)")
    private String orderWay;
    @ApiModelProperty("报价类型(普通/公式/模型/...)")
    private String orderType;
    @ApiModelProperty("投标控制 -- 是否允许供应商撤回报价(Y/N)")
    private Enable allowWithdraw;
    @ApiModelProperty("投标控制 -- 是否允许供应商只对部分物料报价(Y/N)")
    private Enable allowPartPrice;
    @ApiModelProperty("Y-供应商报价时使用未税价/N-供应商报价时使用含税价")
    private Enable isPriceNotax;
    @ApiModelProperty("本轮应报价供应商数量")
    private Integer inviteCount;
    @ApiModelProperty("本轮已报价供应商数量")
    private Integer orderCount;
    @ApiModelProperty("联系人")
    private String linkman;
    @ApiModelProperty("电话")
    private String tel;
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("来源类型")
    private String sourceFromType;
    @ApiModelProperty("来源单据ID")
    private Long sourceFromId;
    @ApiModelProperty("来源单据号")
    private String sourceFromNo;
    @ApiModelProperty("报价模板ID")
    private Long quoteTempId;
    @ApiModelProperty("报价模板名称")
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

    /**
     * 招标创建时间
     */
    private Date bidCreationDate;
}
