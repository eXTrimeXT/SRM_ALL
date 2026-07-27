package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.sou.CustomDateTimeSerializer;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.*;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * (非材) 招标计划拓展表
 * PS: 是 {@link PrRequirementHead} 的拓展表
 * @author huangbf3
 */
@Data
@TableName("scc_npm_pr_require_head")
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementHead extends BaseEntity<ExtPrSouRequirementHead> {

    public static final String SCENE_TYPE = "SOU_PLAN";

    /** @see PrRequirementHead#getRequirementHeadId */
    @ApiModelProperty("主键ID")
    @TableId("REQUIREMENT_HEAD_ID")
    private Long requirementHeadId;

    @TableField("ORG_BU_ID")
    @ApiModelProperty("所属板块ID")
    private Long orgBuId;

    @TableField("ORG_BU_CODE")
    @ApiModelProperty("所属板块编码")
    private String orgBuCode;

    @TableField("ORG_BU_NAME")
    @ApiModelProperty("所属板块名称")
    private String orgBuName;

    /** @see PrSouRequirementFromEnum */
    @TableField("REQUIRE_FROM")
    @ApiModelProperty("需求来源")
    private String requireFrom;
    /**
     * 当需求来源"计划外"，则必填
     */
    @TableField("NO_REPORT_MONTH_PLAN_REASON")
    @ApiModelProperty("未报月度计划原因")
    private String noReportMonthPlanReason;
    /**
     * 必填
     */
    @TableField("PROJECT_NAME")
    @ApiModelProperty("项目名称")
    private String projectName;

    @TableField("PROJECT_MONTH")
    @ApiModelProperty("月份")
    private String projectMonth;

    @TableField("INVEST_NO")
    @ApiModelProperty("投资编号")
    private String investNo;
    /**
     * 必填
     */
    @TableField("REQUIRE_QUANTITY")
    @ApiModelProperty("数量/规模")
    private String requireQuantity;
    /**
     * 必填
     */
    @TableField("TOTAL_AMOUNT_BY_TEN_KILO")
    @ApiModelProperty("概算金额(万元)")
    private BigDecimal totalAmountByTenKilo;
    /**
     * 必填，当需求来源为"特殊招标"，则为否
     */
    @TableField("NEED_PUBLIC")
    @ApiModelProperty("是否公示")
    private Enable needPublic;
    /**
     * 当不公示时，必填
     */
    @TableField("NO_PUBLIC_REASON")
    @ApiModelProperty("不公示理由")
    private String noPublicReason;

    /** @see PrSouRequirementNoPublicEnum 当不公示时必填*/
    @TableField("NO_PUBLIC_REASON_CHOOSE")
    @ApiModelProperty("不公示原因选择")
    private String noPublicReasonChoose;
    /**
     * 若是否公示为是，则必须填写公示截止时间(年月日)
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss", serializeUsing = CustomDateTimeSerializer.class)
    @TableField("PUBLIC_END_TIME")
    @ApiModelProperty("公示截止时间")
    private Date publicEndTime;

    /** @see ExtPrSouRequirementHead#getPublicEndTime */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss", serializeUsing = CustomDateTimeSerializer.class)
    @TableField("EXT_PUBLIC_END_TIME")
    @ApiModelProperty("调整公示截止时间")
    private Date extPublicEndTime;
    /**
     * 必填
     */
    @TableField("PROJECT_ADDRESS")
    @ApiModelProperty("项目所在地")
    private String projectAddress;

    /**
     * 必填，当需求来源为"特殊招标"/"计划外"，则为否，如果无前置技术交流，则公示截止时间为递交招标资料时间
     */
    @TableField("PREFIX_TECH_DISCUSSION")
    @ApiModelProperty("前置技术交流意向")
    private Enable prefixTechDiscussion;

    /**
     * 有前置技术交流时才显示且必填(年月日)
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    @TableField("SEND_SOU_PROFILE_END_DATE")
    @ApiModelProperty("递交招标资料时间")
    private LocalDate sendSouProfileEndDate;

    /**
     * 若需求类型选择月度，计划外，则必填；其他情况默认为否
     */
    @TableField("IF_APPOINT_BRAND")
    @ApiModelProperty("是否指定品牌")
    private Enable ifAppointBrand;

    @TableField("APPOINT_BRAND_FILE_ID")
    @ApiModelProperty("指定品牌文件ID")
    private Long appointBrandFileId;

    @TableField("APPOINT_BRAND_FILE_NAME")
    @ApiModelProperty("指定品牌文件名称")
    private String appointBrandFileName;

    /**
     * 若需求类型选择月度，计划外，则必填；其他情况默认为否
     */
    @TableField("IF_QUALIFY_UNIT")
    @ApiModelProperty("是否限定单位")
    private Enable ifQualifyUnit;

    @TableField("QUALIFY_UNIT_FILE_ID")
    @ApiModelProperty("限定单位文件ID")
    private Long qualifyUnitFileId;

    @TableField("QUALIFY_UNIT_FILE_NAME")
    @ApiModelProperty("限定单位文件名称")
    private String qualifyUnitFileName;

    /** {@link ExtPrSouProjectPlan#getProjectPlanId} */
    @TableField("PROJECT_PLAN_ID")
    @ApiModelProperty("项目计划ID")
    private Long projectPlanId;

    /** {@link ExtPrSouProjectPlan#getPlanNo} */
    @TableField("PLAN_NO")
    @ApiModelProperty("项目计划编号")
    private String planNo;

    /** {@link PrSouRequirementSpecialSouTypeEnum} 当需求来源"特殊招标"，则必填*/
    @TableField("SPECIAL_SOU_TYPE")
    @ApiModelProperty("特殊招标类型")
    private String specialSouType;

    /**
     *  {@link PrSouRequirementSpecialReasonEnum}
     * 若特殊招标类型选择“特定原因导致目标供应商唯一”，则必填
     */
    @TableField("SPECIAL_REASON")
    @ApiModelProperty("特定原因")
    private String specialReason;

    /**
     * 若特殊招标类型选择“时间紧急”，则显示，且必填(年月日)
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    @TableField("REQUIRE_PRODUCT_DATE")
    @ApiModelProperty("需求产生时间")
    private LocalDate requireProductDate;

    @TableField("REQUIRE_PRODUCT_FILE_ID")
    @ApiModelProperty("需求产生时间附件ID")
    private Long requireProductFileId;

    @TableField("REQUIRE_PRODUCT_FILE_NAME")
    @ApiModelProperty("需求产生时间附件名称")
    private String requireProductFileName;

    /**
     * 若特殊招标类型选择“时间紧急”，则显示，且必，只能填数字填
     */
    @TableField("DELIVERY_DAY")
    @ApiModelProperty("工期交货期")
    private BigDecimal deliveryDay;

    @TableField("DELIVERY_DAY_FILE_ID")
    @ApiModelProperty("工期交货期附件ID")
    private Long deliveryDayFileId;

    @TableField("DELIVERY_DAY_FILE_NAME")
    @ApiModelProperty("工期交货期附件名称")
    private String deliveryDayFileName;

    /**
     * 若特殊招标类型选择“时间紧急”，则显示，且必填，只能填数字
     */
    @TableField("SIGN_CONTRACT_DAY")
    @ApiModelProperty("签合同用时")
    private BigDecimal signContractDay;

    /**
     * 若特殊招标类型选择“时间紧急”，则显示，且必填(年月日)
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    @TableField("PUT_INTO_USE_DATE")
    @ApiModelProperty("投入使用时间")
    private LocalDate putIntoUseDate;

    @TableField("PUT_INTO_USE_DATE_FILE_ID")
    @ApiModelProperty("投入使用时间附件ID")
    private Long putIntoUseDateFileId;

    @TableField("PUT_INTO_USE_DATE_FILE_NAME")
    @ApiModelProperty("投入使用时间附件名称")
    private String putIntoUseDateFileName;

    /**
     * 若招标类型选择特殊招标时显示
     */
    @TableField("OTHER_SPECIAL_REASON")
    @ApiModelProperty("其他特殊原因补充")
    private String otherSpecialReason;

    @TableField("REMAINING_DAY")
    @ApiModelProperty("剩余时间")
    private BigDecimal remainingDay;

    /**
     * 必填
     */
    @TableField("PROJECT_OVERVIEW")
    @ApiModelProperty("项目概况及范围")
    private String projectOverview;

    /**
     * 必填
     */
    @TableField("TECH_REQUIRE")
    @ApiModelProperty("技术要求")
    private String techRequire;

    /**
     * 必填
     */
    @TableField("PERFORMANCE_REQUIRE")
    @ApiModelProperty("业绩要求")
    private String performanceRequire;

    /**
     * 必填
     */
    @TableField("VENDOR_QUALIFICATION_REQUIRE")
    @ApiModelProperty("供应商资质要求")
    private String vendorQualificationRequire;

    @TableField("HAS_ASSIGNED")
    @ApiModelProperty("是否已分配(招标+供应商负责人)")
    private Enable hasAssigned;

    @TableField("HAS_SEND_SOU_PROFILE")
    @ApiModelProperty("是否已提交招标资料")
    private Enable hasSendSouProfile;

    @TableField("HAS_CREATE_SOU")
    @ApiModelProperty("是否已经创建寻源")
    private Enable hasCreateSou;

    @TableField("HAS_CREATE_SOU_REQ")
    @ApiModelProperty("是否已创建寻源需求")
    private Enable hasCreateSouReq;

    @TableField("HAS_CREATE_VENDOR_RECOMMEND")
    @ApiModelProperty("是否已创建供应商推荐")
    private Enable hasCreateVendorRecommend;

    @TableField("EARNEST_MONEY")
    @ApiModelProperty("意向金金额(万元)")
    private BigDecimal earnestMoney;

    @TableField("APPROVAL_PASS_TIME")
    @ApiModelProperty("需求审批完成时间")
    private Date approvalPassTime;

    /** @see ExtPrSouRequirementCancel#getCancelReason */
    @TableField("REQ_CANCEL_REASON")
    @ApiModelProperty("需求取消原因")
    private String reqCancelReason;

    /** @see PrSouRequirementStatusEnum */
    @TableField("SOU_REQ_STATUS")
    @ApiModelProperty("需求状态")
    private String souReqStatus;

    /** @see PrSouRequirementSendProfileStatusEnum */
    @TableField("SEND_SOU_PROFILE_STATUS")
    @ApiModelProperty("招标资料状态")
    private String sendSouProfileStatus;

    @TableField("HAS_SUBMIT")
    @ApiModelProperty("是否已提交")
    private Enable hasSubmit;

    @TableField("SUBMIT_APPROVAL_TIME")
    @ApiModelProperty("提交审批时间")
    private Date submitApprovalTime;

    // ---------------------------------------------------- 变更相关信息 ----------------------------------------------------
    /** @see PrRequirementHead#getRequirementHeadId */
    @TableField("CHANGE_REQUIREMENT_HEAD_ID")
    @ApiModelProperty("变更来源计划ID")
    private Long changeRequirementHeadId;

    /** @see PrRequirementHead#getRequirementHeadNum */
    @TableField("CHANGE_REQUIREMENT_HEAD_NUM")
    @ApiModelProperty("变更来源计划单号")
    private String changeRequirementHeadNum;

    @TableField("AFTER_TOTAL_AMOUNT_BY_TEN_KILO")
    @ApiModelProperty("变更后概算金额(万元)")
    private BigDecimal afterTotalAmountByTenKilo;

    @TableField("CHANGE_REASON")
    @ApiModelProperty("变更原因")
    private String changeReason;

    // ---------------------------------------------------- 下游寻源单信息 ----------------------------------------------------
    /** @see SouTypeEnum */
    @TableField("SOU_TYPE")
    @ApiModelProperty("关联寻源单类型")
    private String souType;

    @TableField("SOU_PROJECT_ID")
    @ApiModelProperty("关联寻源单ID")
    private Long souProjectId;

    @TableField("SOU_NO")
    @ApiModelProperty("关联寻源单号")
    private String souNo;

    @TableField("SOU_NAME")
    @ApiModelProperty("关联寻源单名称")
    private String souName;
    /** ---------------------------------------------------- 下游寻源需求信息 ---------------------------------------------------- */
    @TableField("SOU_REQ_ID")
    @ApiModelProperty("寻源需求ID")
    private Long souReqId;

    @TableField("SOU_REQ_NO")
    @ApiModelProperty("寻源需求单号")
    private String souReqNo;

    /** ---------------------------------------------------- 下游推荐供应商信息 ---------------------------------------------------- */
    @TableField("RECOMMEND_VENDOR_BILL_ID")
    @ApiModelProperty("推荐供应商单据ID")
    private Long recommendVendorBillId;

    @TableField("RECOMMEND_VENDOR_BILL_NO")
    @ApiModelProperty("推荐供应商单据编码")
    private String recommendVendorBillNo;

    @TableField("START_BPM_USERNAME")
    @ApiModelProperty("bpm发起人账号")
    private String startBpmUsername;

    @TableField("START_BPM_NICKNAME")
    @ApiModelProperty("bpm发起人名称")
    private String startBpmNickname;

}
