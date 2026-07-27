package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.vo.init.MqlPrRequirementHeadVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementPoolQueryVO extends MqlPrRequirementHeadVO {

    /** @see ExtPrSouRequirementHead#getOrgBuId */
    @ApiModelProperty("所属板块ID")
    private Long orgBuId;

    /** @see ExtPrSouRequirementHead#getOrgBuCode */
    @ApiModelProperty("所属板块编码")
    private String orgBuCode;

    /** @see ExtPrSouRequirementHead#getOrgBuName */
    @ApiModelProperty("所属板块名称")
    private String orgBuName;

    /** @see ExtPrSouRequirementHead#getRequireFrom */
    @ApiModelProperty("需求来源")
    private String requireFrom;

    /** @see ExtPrSouRequirementHead#getNoReportMonthPlanReason */
    @ApiModelProperty("未报月度计划原因")
    private String noReportMonthPlanReason;

    /** @see ExtPrSouRequirementHead#getProjectName */
    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("年月-取申请时间的年份+月份")
    private String projectDate;

    @ApiModelProperty("年-取申请时间的年份")
    private Integer projectYear;

    /** @see ExtPrSouRequirementHead#getProjectMonth */
    @ApiModelProperty("月份")
    private String projectMonth;

    /** @see ExtPrSouRequirementHead#getInvestNo */
    @ApiModelProperty("投资编号")
    private String investNo;

    /** @see ExtPrSouRequirementHead#getRequireQuantity */
    @ApiModelProperty("数量/规模")
    private String requireQuantity;

    /** @see ExtPrSouRequirementHead#getTotalAmountByTenKilo */
    @ApiModelProperty("概算金额(万元)")
    private BigDecimal totalAmountByTenKilo;

    /** @see ExtPrSouRequirementHead#getNeedPublic */
    @ApiModelProperty("是否公示")
    private Enable needPublic;

    /** @see ExtPrSouRequirementHead#getNoPublicReason */
    @ApiModelProperty("不公示理由")
    private String noPublicReason;

    /** @see ExtPrSouRequirementHead#getNoPublicReasonChoose */
    @ApiModelProperty("不公示原因选择")
    private String noPublicReasonChoose;

    /** @see ExtPrSouRequirementHead#getPublicEndTime */
    @ApiModelProperty("公示截止时间")
    private Date publicEndTime;

    /** @see ExtPrSouRequirementHead#getPublicEndTime */
    @ApiModelProperty("调整公示截止时间")
    private Date extPublicEndTime;

    /** @see ExtPrSouRequirementHead#getProjectAddress */
    @TableField("PROJECT_ADDRESS")
    @ApiModelProperty("项目所在地")
    private String projectAddress;

    /** @see ExtPrSouRequirementHead#getPrefixTechDiscussion */
    @ApiModelProperty("前置技术交流意向")
    private Enable prefixTechDiscussion;

    /** @see ExtPrSouRequirementHead#getSendSouProfileEndDate */
    @ApiModelProperty("递交招标资料时间")
    private LocalDate sendSouProfileEndDate;

    /** @see ExtPrSouRequirementHead#getIfAppointBrand */
    @ApiModelProperty("是否指定品牌")
    private Enable ifAppointBrand;

    /** @see ExtPrSouRequirementHead#getAppointBrandFileId */
    @ApiModelProperty("指定品牌文件ID")
    private Long appointBrandFileId;

    /** @see ExtPrSouRequirementHead#getAppointBrandFileName */
    @ApiModelProperty("指定品牌文件名称")
    private String appointBrandFileName;

    /** @see ExtPrSouRequirementHead#getIfQualifyUnit */
    @ApiModelProperty("是否限定单位")
    private Enable ifQualifyUnit;

    /** @see ExtPrSouRequirementHead#getQualifyUnitFileId */
    @ApiModelProperty("限定单位文件ID")
    private Long qualifyUnitFileId;

    /** @see ExtPrSouRequirementHead#getQualifyUnitFileName */
    @ApiModelProperty("限定单位文件名称")
    private String qualifyUnitFileName;

    /** @see ExtPrSouRequirementHead#getProjectPlanId */
    @ApiModelProperty("项目计划ID")
    private Long projectPlanId;

    /** @see ExtPrSouRequirementHead#getPlanNo */
    @ApiModelProperty("项目计划编号")
    private String planNo;

    /** @see ExtPrSouRequirementHead#getSpecialSouType */
    @ApiModelProperty("特殊招标类型")
    private String specialSouType;

    /** @see ExtPrSouRequirementHead#getSpecialReason */
    @ApiModelProperty("特定原因")
    private String specialReason;

    /** @see ExtPrSouRequirementHead#getRequireProductDate */
    @ApiModelProperty("需求产生时间")
    private LocalDate requireProductDate;

    /** @see ExtPrSouRequirementHead#getRequireProductFileId */
    @ApiModelProperty("需求产生时间附件ID")
    private Long requireProductFileId;

    /** @see ExtPrSouRequirementHead#getRequireProductFileName */
    @ApiModelProperty("需求产生时间附件名称")
    private String requireProductFileName;

    /** @see ExtPrSouRequirementHead#getDeliveryDay */
    @ApiModelProperty("工期交货期")
    private BigDecimal deliveryDay;

    /** @see ExtPrSouRequirementHead#getRequireProductFileName */
    @ApiModelProperty("工期交货期附件ID")
    private Long deliveryDayFileId;

    /** @see ExtPrSouRequirementHead#getDeliveryDayFileName */
    @ApiModelProperty("工期交货期附件名称")
    private String deliveryDayFileName;

    /** @see ExtPrSouRequirementHead#getSignContractDay */
    @ApiModelProperty("签合同用时")
    private BigDecimal signContractDay;

    /** @see ExtPrSouRequirementHead#getPutIntoUseDate */
    @ApiModelProperty("投入使用时间")
    private LocalDate putIntoUseDate;

    /** @see ExtPrSouRequirementHead#getPutIntoUseDateFileId */
    @ApiModelProperty("投入使用时间附件ID")
    private Long putIntoUseDateFileId;

    /** @see ExtPrSouRequirementHead#getRequireProductFileName */
    @ApiModelProperty("投入使用时间附件名称")
    private String putIntoUseDateFileName;

    /** @see ExtPrSouRequirementHead#getOtherSpecialReason */
    @ApiModelProperty("其他特殊原因补充")
    private String otherSpecialReason;

    /** @see ExtPrSouRequirementHead#getRemainingDay */
    @ApiModelProperty("剩余时间")
    private BigDecimal remainingDay;

    /** @see ExtPrSouRequirementHead#getProjectOverview */
    @ApiModelProperty("项目概况及范围")
    private String projectOverview;

    /** @see ExtPrSouRequirementHead#getTechRequire */
    @ApiModelProperty("技术要求")
    private String techRequire;

    /** @see ExtPrSouRequirementHead#getPerformanceRequire */
    @ApiModelProperty("业绩要求")
    private String performanceRequire;

    /** @see ExtPrSouRequirementHead#getVendorQualificationRequire */
    @ApiModelProperty("供应商资质要求")
    private String vendorQualificationRequire;

    /** @see ExtPrSouRequirementHead#getHasAssigned */
    @ApiModelProperty("是否已分配(招标+供应商负责人)")
    private Enable hasAssigned;

    /** @see ExtPrSouRequirementHead#getHasSendSouProfile */
    @ApiModelProperty("是否已提交招标资料")
    private Enable hasSendSouProfile;

    /** @see ExtPrSouRequirementHead#getHasCreateSou */
    @ApiModelProperty("是否已经创建寻源")
    private Enable hasCreateSou;

    /** @see ExtPrSouRequirementHead#getHasCreateSouReq */
    @ApiModelProperty("是否已创建寻源需求")
    private Enable hasCreateSouReq;

    /** @see ExtPrSouRequirementHead#getHasCreateVendorRecommend */
    @ApiModelProperty("是否已创建供应商推荐")
    private Enable hasCreateVendorRecommend;

    /** @see ExtPrSouRequirementHead#getEarnestMoney */
    @ApiModelProperty("意向金金额(万元)")
    private BigDecimal earnestMoney;

    /** @see ExtPrSouRequirementHead#getApprovalPassTime */
    @ApiModelProperty("需求审批完成时间")
    private Date approvalPassTime;

    /** @see ExtPrSouRequirementHead#getReqCancelReason */
    @ApiModelProperty("需求取消原因")
    private String reqCancelReason;

    /** @see ExtPrSouRequirementHead#getSouReqStatus */
    @ApiModelProperty("需求状态")
    private String souReqStatus;

    /** @see ExtPrSouRequirementHead#getSendSouProfileStatus */
    @ApiModelProperty("招标资料状态")
    private String sendSouProfileStatus;

    /** @see ExtPrSouRequirementHead#getHasSubmit */
    @ApiModelProperty("是否已提交")
    private Enable hasSubmit;

    /** @see ExtPrSouRequirementHead#getSubmitApprovalTime */
    @ApiModelProperty("提交审批时间")
    private Date submitApprovalTime;

    /** @see ExtPrSouRequirementHead#getChangeRequirementHeadId */
    @ApiModelProperty("变更来源计划ID")
    private Long changeRequirementHeadId;

    /** @see ExtPrSouRequirementHead#getChangeRequirementHeadNum */
    @ApiModelProperty("变更来源计划单号")
    private String changeRequirementHeadNum;

    /** @see ExtPrSouRequirementHead#getAfterTotalAmountByTenKilo */
    @ApiModelProperty("变更后概算金额(万元)")
    private BigDecimal afterTotalAmountByTenKilo;

    /** @see ExtPrSouRequirementHead#getChangeReason */
    @ApiModelProperty("变更原因")
    private String changeReason;

    /** @see ExtPrSouRequirementHead#getSouType */
    @ApiModelProperty("关联寻源单类型")
    private String souType;

    /** @see ExtPrSouRequirementHead#getSouProjectId */
    @ApiModelProperty("关联寻源单ID")
    private Long souProjectId;

    /** @see ExtPrSouRequirementHead#getSouNo */
    @ApiModelProperty("关联寻源单号")
    private String souNo;

    /** @see ExtPrSouRequirementHead#getSouName */
    @ApiModelProperty("关联寻源单名称")
    private String souName;

    /** @see ExtPrSouRequirementHead#getSouReqId */
    @ApiModelProperty("寻源需求ID")
    private Long souReqId;

    /** @see ExtPrSouRequirementHead#getSouReqNo */
    @ApiModelProperty("寻源需求单号")
    private String souReqNo;

    /** @see ExtPrSouRequirementHead#getRecommendVendorBillId */
    @ApiModelProperty("推荐供应商单据ID")
    private Long recommendVendorBillId;

    /** @see ExtPrSouRequirementHead#getRecommendVendorBillNo */
    @ApiModelProperty("推荐供应商单据编码")
    private String recommendVendorBillNo;

    /** @see ExtPrSouRequirementVendor#getVendorId */
    @ApiModelProperty("推荐供应商ID")
    private Long vendorId;

    /** @see ExtPrSouRequirementVendor#getVendorCode */
    @ApiModelProperty("推荐供应商编码")
    private String vendorCode;

    /** @see ExtPrSouRequirementVendor#getVendorName */
    @ApiModelProperty("推荐供应商名称")
    private String vendorName;

    /** @see ExtPrSouRequirementVendor#getContactName */
    @ApiModelProperty("推荐联系人名称")
    private String contactName;

    /** @see ExtPrSouRequirementVendor#getPhone */
    @ApiModelProperty("推荐联系方式")
    private String phone;

    /** @see ExtPrSouRequirementVendor#getEmail */
    @ApiModelProperty("推荐邮箱")
    private String email;

    /** @see ExtPrSouRequirementVendor#getRecommendFrom */
    @ApiModelProperty("推荐来源")
    private String recommendFrom;

    /** @see ExtPrSouRequirementGroup#getUserId */
    @ApiModelProperty("招标负责人ID")
    private Long souGroupUserId;

    /** @see ExtPrSouRequirementGroup#getUsername */
    @ApiModelProperty("招标负责人账号")
    private String souGroupUsername;

    /** @see ExtPrSouRequirementGroup#getFullName */
    @ApiModelProperty("招标负责人昵称")
    private String souGroupFullName;

    /** @see ExtPrSouRequirementGroup#getPhone */
    @ApiModelProperty("招标负责人电话")
    private String souGroupPhone;

    /** @see ExtPrSouRequirementGroup#getDepartmentName */
    @ApiModelProperty("招标负责人所在部门")
    private String souGroupDepartmentName;

    /** @see ExtPrSouRequirementGroup#getUserId */
    @ApiModelProperty("技术负责人ID")
    private Long techGroupUserId;

    /** @see ExtPrSouRequirementGroup#getUsername */
    @ApiModelProperty("技术负责人账号")
    private String techGroupUsername;

    /** @see ExtPrSouRequirementGroup#getFullName */
    @ApiModelProperty("技术负责人昵称")
    private String techGroupFullName;

    /** @see ExtPrSouRequirementGroup#getPhone */
    @ApiModelProperty("技术负责人电话")
    private String techGroupPhone;

    /** @see ExtPrSouRequirementGroup#getWorkYear */
    @ApiModelProperty("技术负责人工作年限")
    private BigDecimal techGroupWorkYear;

    /** @see ExtPrSouRequirementGroup#getDepartmentName */
    @ApiModelProperty("技术负责人所在部门")
    private String techGroupDepartmentName;

    /** @see ExtPrSouRequirementGroup#getUserId */
    @ApiModelProperty("供应商负责人ID")
    private Long vendorGroupUserId;

    /** @see ExtPrSouRequirementGroup#getUsername */
    @ApiModelProperty("供应商负责人账号")
    private String vendorGroupUsername;

    /** @see ExtPrSouRequirementGroup#getFullName */
    @ApiModelProperty("供应商负责人昵称")
    private String vendorGroupFullName;

    /** @see ExtPrSouRequirementGroup#getPhone */
    @ApiModelProperty("供应商负责人电话")
    private String vendorGroupPhone;

    /** @see ExtPrSouRequirementGroup#getDepartmentName */
    @ApiModelProperty("供应商负责人所在部门")
    private String vendorGroupDepartmentName;

}
