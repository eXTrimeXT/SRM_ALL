package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class ExtSouOrderDto extends ExtSouOrder {

    @ApiModelProperty("具体的单据号生成规则由具体业务模块决定")
    private String souNo;

    @ApiModelProperty("寻源单名称")
    private String souName;

    @ApiModelProperty("寻源类型(字典:SOU_TYPE)")
    private String souType;

    @ApiModelProperty("项目状态")
    private String projectStatus;

    @ApiModelProperty("立项审核状态")
    private String createApprovalStatus;

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
     * 招标流程
     */
    private String extSouProcess;

    @ApiModelProperty("报价截止时间")
    private Date orderEndTime;

    @ApiModelProperty("保证金缴纳状态")
    private String marginStatus;

    @ApiModelProperty("处理方式, 字典：MARGIN_HANDER_MODE")
    private String handerMode;

    @ApiModelProperty("原因说明")
    private String causeDesc;

    /**
     * 主键
     */
    @ApiModelProperty("保证金缴纳表主键ID")
    private Long marginId;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("组织报价原因")
    private String extOrderReason;

    @ApiModelProperty("发布日期")
    private Date publishTime;

    @ApiModelProperty("项目创建时间")
    private Date projectCreationDate;

    @ApiModelProperty("招标类型")
    private String orderType;

    @ApiModelProperty("合并招标标识")
    private Boolean mergeFlag;

    @ApiModelProperty("当前轮次标识，Y时可根据投标状态控制投标或者撤回投标")
    private String currentRoundFlag;

    private Integer currentRound;

    @ApiModelProperty("招标联系人办公电话")
    private String tel;

    @ApiModelProperty("投标次数")
    private Integer tenderTimes;

    @ApiModelProperty("含税总价")
    private BigDecimal extTaxAmount;

    @ApiModelProperty("中/落标通知是否发送")
    private String isSend;
}
