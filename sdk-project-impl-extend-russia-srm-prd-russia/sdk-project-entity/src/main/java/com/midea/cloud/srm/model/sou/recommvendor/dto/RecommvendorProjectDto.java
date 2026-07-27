package com.midea.cloud.srm.model.sou.recommvendor.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.BaseDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouDemand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel(description = "供应商推荐DTO")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class RecommvendorProjectDto extends BaseDTO {

    @ApiModelProperty("ID")
    private Long projectId;
    @ApiModelProperty("寻源单号")
    private String souNo;
    @ApiModelProperty("寻源名称")
    private String souName;
    @ApiModelProperty("寻源类型")
    private String souType;
    @ApiModelProperty("流程配置ID")
    private Long processConfigId;
    @ApiModelProperty("评选方式(高价/低价/综合)")
    private String scoreRuleType;
    @ApiModelProperty("评分模板ID")
    private Long scoreTemplateId;
    @ApiModelProperty("本位币")
    private String standardCurrency;
    @ApiModelProperty("价格精度")
    private Integer pricePrecision;
    @ApiModelProperty("项目状态")
    private String projectStatus;
    @ApiModelProperty("立项审核状态")
    private String createApprovalStatus;
    @ApiModelProperty("是否密封报价")
    private String needEncryptPrice;
    @ApiModelProperty("当前轮次")
    private Integer currentRound;
    @ApiModelProperty("预计报价地点")
    private String orderSite;
    @ApiModelProperty("是否同步至价格库")
    private String isSyncToPriceLibrary;
    @ApiModelProperty("生成价格审批单方式")
    private String generatePriceApprovalType;
    @ApiModelProperty("作废原因")
    private String cancelReason;
    @ApiModelProperty("需要密码解密的操作")
    private String needPwdOperations;
    @ApiModelProperty("是否允许物料变更")
    private String allowItemChange;
    @ApiModelProperty("是否允许中途追加供应商")
    private String allowNewVendors;
    @ApiModelProperty("是否允许代理报价")
    private String allowProxyOrder;
    @ApiModelProperty("价格有效期从")
    private Date priceStartTime;
    @ApiModelProperty("价格有效期到")
    private Date priceEndTime;
    @ApiModelProperty("发布时间")
    private Date publishTime;
    @ApiModelProperty("报名开始时间")
    private Date signUpStartTime;
    @ApiModelProperty("报名截止时间")
    private Date signUpEndTime;
    @ApiModelProperty("报价开始时间")
    private Date orderStartTime;
    @ApiModelProperty("报价截止时间")
    private Date orderEndTime;
    @ApiModelProperty("技术开标标识")
    private String techOpen;
    @ApiModelProperty("技术开标时间")
    private Date techOpenTime;
    @ApiModelProperty("最早开标时间")
    private Date earliestBusinessOpenTime;
    @ApiModelProperty("发布范围(邀请/公开)")
    private String publishScope;
    @ApiModelProperty("报价方式(单项/组合)")
    private String orderWay;
    @ApiModelProperty("报价类型(普通/公式/模型)")
    private String orderType;
    @ApiModelProperty("是否允许撤回报价")
    private String allowWithdraw;
    @ApiModelProperty("是否允许部分报价")
    private String allowPartPrice;
    @ApiModelProperty("是否使用未税价报价")
    private String isPriceNotax;
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
    @ApiModelProperty("来源单据编码")
    private String sourceFromNo;
    @ApiModelProperty("报价模板ID")
    private Long quoteTempId;
    @ApiModelProperty("报价模板名称")
    private String quoteTempName;
    @ApiModelProperty("品类ID")
    private Long extCategoryId;
    @ApiModelProperty("品类编码")
    private String extCategoryCode;
    @ApiModelProperty("品类")
    private String extCategoryName;
    @ApiModelProperty("是否缴纳保证金")
    private String extEarnestFlag;
    @ApiModelProperty("保证金缴纳金额（万元）")
    private BigDecimal extEarnestAmount;
    @ApiModelProperty("开户银行")
    private String extBankName;
    @ApiModelProperty("开户行号")
    private String extBankNumber;
    @ApiModelProperty("开户账号")
    private String extBankAccount;
    @ApiModelProperty("开户户名")
    private String extBankAccountName;
    @ApiModelProperty("推荐供应商单号")
    private String extRecommendNo;
    @ApiModelProperty("招标项目编号")
    private String extProjectNo;
    @ApiModelProperty("收标方式")
    private String extSouMode;
    @ApiModelProperty("评分规则（字典）")
    private String extScoreRule;
    @ApiModelProperty("隐藏评标关键信息")
    private String extHideKeyInfo;
    @ApiModelProperty("板块ID")
    private Long extOrgBuId;
    @ApiModelProperty("板块编码")
    private String extOrgBuCode;
    @ApiModelProperty("板块名称")
    private String extOrgBuName;
    @ApiModelProperty("公司ID")
    private Long extOrgOuId;
    @ApiModelProperty("公司编码")
    private String extOrgOuCode;
    @ApiModelProperty("公司名称")
    private String extOrgOuName;
    @ApiModelProperty("申请部门")
    private String extApplicantDepart;
    @ApiModelProperty("申请人")
    private String extApplicant;
    @ApiModelProperty("投资编号")
    private String extInvestNo;
    @ApiModelProperty("预算")
    private BigDecimal extBudget;
    @ApiModelProperty("规模数量")
    private String extScaleQuantity;
    @ApiModelProperty("是否指定评标人")
    private String extAssignEvaluator;
    @ApiModelProperty("技术负责人")
    private String extTechPrincipal;
    @ApiModelProperty("招标负责人")
    private String extSouPrincipal;
    @ApiModelProperty("招标流程")
    private String extSouProcess;
    @ApiModelProperty("评标总人数")
    private Integer extBidEvaluatorNum;
    @ApiModelProperty("要求高级专家人数")
    private Integer extAskSeniorExpertNum;
    @ApiModelProperty("专家抽取范围，字典：SOU_EXPERT_RANGE")
    private String extExpertRange;
    @ApiModelProperty("评分模板名称")
    private String scoreTemplateName;

    @ApiModelProperty("合并申请单号")
    private String applicantNo;

    @ApiModelProperty("合并申请单ID")
    private String applicantId;

    @ApiModelProperty("供应商推荐单供应商列表")
    private List<RecommvendorDto> recommvendorList;

    @ApiModelProperty("供应商推荐单扩展表")
    private RecommvendorProjectExtendDto recommvendorProjectExtend;

    @ApiModelProperty("供应商推荐单附件列表")
    private List<RecommvendorFileDto> recommvendorFileList;

    @ApiModelProperty("供应商推荐关联申请单号")
    private List<ExtSouDemand> recommvendorDemandList;
    @ApiModelProperty("bpm发起人账号")
    private String startBpmUsername;

    @ApiModelProperty("bpm发起人名称")
    private String startBpmNickname;
    @ApiModelProperty("是否部分取消")
    private String partCancle;

}
