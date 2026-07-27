package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto;

import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectQueryDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Date;

/**
 * 招标计划池 - 列表查询条件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementPoolQueryDTO extends BasePage {

    /** @see PrRequirementHead#getDemandType */
    @ApiModelProperty("需求类型(等值查询)")
    private String demandType;

    /** @see PrRequirementHead#getCategoryCode */
    @ApiModelProperty("品类(编码/名称)(模糊查询)")
    private String categoryCode;

    /** @see ExtPrSouRequirementHead#getProjectName */
    @ApiModelProperty("项目名称(模糊查询)")
    private String projectName;

    /** @see ExtPrSouRequirementHead#getProjectAddress */
    @ApiModelProperty("项目所在地(模糊查询)")
    private String projectAddress;

    /**
     * {@link ExtPrSouRequirementGroup#getGroupType} = {@link PrSouRequirementGroupTypeEnum#TECH}
     * @see ExtPrSouRequirementGroup#getUsername
     */
    @ApiModelProperty("技术负责人账号(等值查询)")
    private String techUsername;

    /**
     * {@link ExtPrSouRequirementGroup#getGroupType} = {@link PrSouRequirementGroupTypeEnum#SOU}
     * @see ExtPrSouRequirementGroup#getUsername
     */
    @ApiModelProperty("招标负责人账号(等值查询)")
    private String souUsername;

    /**
     * {@link ExtPrSouRequirementGroup#getGroupType} = {@link PrSouRequirementGroupTypeEnum#SOU}
     * @see ExtPrSouRequirementGroup#getDepartmentName
     */
    @ApiModelProperty("招标负责人所在科室")
    private String souUserDeptName;

    /** @see PrRequirementHead#getApplyDate */
    @ApiModelProperty("申请日期范围")
    private LocalDate applyDateFrom;
    private LocalDate applyDateTo;

    /**
     * @see PrRequirementHead#getApplyBy
     * @see PrRequirementHead#getApplyByNickname
     */
    @ApiModelProperty("申请人(账号/昵称)(模糊查询)")
    private String applyBy;

    /**
     * @see PrRequirementHead#getCeeaDepartmentId
     * @see PrRequirementHead#getCeeaDepartmentName
     */
    @ApiModelProperty("申请部门(ID/名称)(模糊查询)")
    private String departmentId;

    /**
     * {@link ExtPrSouRequirementGroup#getGroupType} = {@link PrSouRequirementGroupTypeEnum#VENDOR}
     * @see ExtPrSouRequirementGroup#getUsername
     * @see ExtPrSouRequirementGroup#getFullName
     */
    @ApiModelProperty("供应商负责人")
    private String vendorUsername;

    /**
     * {@link ExtPrSouRequirementGroup#getGroupType} = {@link PrSouRequirementGroupTypeEnum#VENDOR}
     * @see ExtPrSouRequirementGroup#getDepartmentName
     */
    @ApiModelProperty("供应商负责人所在科室")
    private String vendorUserDeptName;

    /** @see PrRequirementHead#getRequirementHeadNum */
    @ApiModelProperty("申请单号(模糊查询)")
    private String requirementHeadNum;

    /** @see ExtPrSouRequirementHead#getHasAssigned */
    @ApiModelProperty("是否已分配(Y/N)")
    private Enable hasAssigned;

    /** @see ExtPrSouRequirementHead#getRequireFrom */
    @ApiModelProperty("需求来源(等值查询)")
    private String requireFrom;

    /** @see ExtPrSouRequirementHead#getHasSendSouProfile */
    @ApiModelProperty("是否已提交招标资料(Y/N)")
    private Enable hasSendSouProfile;

    /** @see ExtPrSouRequirementHead#getSendSouProfileEndDate */
    @ApiModelProperty("递交申请资料时间范围")
    private LocalDate sendSouProfileEndDateFrom;
    private LocalDate sendSouProfileEndDateTo;

    /** @see ExtPrSouRequirementHead#getProjectMonth */
    @ApiModelProperty("月份(等值查询)")
    private String projectMonth;

    /** @see ExtPrSouRequirementHead#getOrgBuCode */
    @ApiModelProperty("板块(模糊查询)")
    private String orgBuCode;

    /** @see PrRequirementHead#getOrgCode */
    @ApiModelProperty("公司(模糊查询)")
    private String orgCode;

    /** @see ExtPrSouRequirementHead#getPrefixTechDiscussion */
    @ApiModelProperty("前置交流意向")
    private Enable prefixTechDiscussion;

    /** @see ExtPrSouRequirementHead#getPublicEndTime */
    @ApiModelProperty("公示截止时间范围")
    private Date publicEndTimeFrom;
    private Date publicEndTimeTo;

    /** @see ExtPrSouRequirementHead#getSouReqStatus */
    @ApiModelProperty("需求状态(等值查询)")
    private String souReqStatus;

    /** @see ExtPrSouRequirementHead#getHasCreateSouReq */
    @ApiModelProperty("是否已创建寻源需求")
    private Enable hasCreateSouReq;

    /** @see ExtPrSouRequirementHead#getApprovalPassTime */
    private Date approvalPassTimeFrom;
    private Date approvalPassTimeTo;

    private String needPublic;

    @ApiModelProperty("是否指定品牌")
    private String ifAppointBrand;

    @ApiModelProperty("是否限定单位")
    private String ifQualifyUnit;

    @ApiModelProperty("特殊招标类型")
    private String specialSouType;

    @ApiModelProperty("不公示原因")
    private String noPublicReason;

    @ApiModelProperty("推荐单位名称")
    private String vendorName;
    @ApiModelProperty("不公示原因")
    private String noPublicReasonChoose;


    /**
     * 入参格式化
     */
    public void formatParams() {
        // 1: 需求类型
        demandType = StringUtils.trimToNull(demandType);
        // 2: 品类
        categoryCode = StringUtils.trimToNull(categoryCode);
        // 3: 项目名称
        projectName = StringUtils.trimToNull(projectName);
        // 4: 项目所在地
        projectAddress = StringUtils.trimToNull(projectAddress);
        // 5: 技术负责人账号
        techUsername = StringUtils.trimToNull(techUsername);
        // 6: 招标负责人账号
        souUsername = StringUtils.trimToNull(souUsername);
        souUserDeptName = StringUtils.trimToNull(souUserDeptName);
        // 7: 申请日期范围(略)
        // 8: 申请人
        applyBy = StringUtils.trimToNull(applyBy);
        // 9: 申请部门
        departmentId = StringUtils.trimToNull(departmentId);
        // 10: 供应商负责
        vendorUsername = StringUtils.trimToNull(vendorUsername);
        vendorUserDeptName = StringUtils.trimToNull(vendorUserDeptName);
        // 11: 申请单号
        requirementHeadNum = StringUtils.trimToNull(requirementHeadNum);
        // 12: 是否已分配(略)
        // 13: 需求来源
        requireFrom = StringUtils.trimToNull(requireFrom);
        // 14: 是否已提交招标资料(略)
        // 15: 递交申请资料时间范围(略)
        // 16: 月份(略)
        // 17: 板块
        orgBuCode = StringUtils.trimToNull(orgBuCode);
        // 18: 公司
        orgCode = StringUtils.trimToNull(orgCode);
        // 19: 前置交流意向(略)
        // 20: 公示截止时间范围
        if (publicEndTimeFrom != null) {
            publicEndTimeFrom = ApiExtSouProjectQueryDTO.getStartTimeOfDate(publicEndTimeFrom);
        }
        if (publicEndTimeTo != null) {
            publicEndTimeTo = ApiExtSouProjectQueryDTO.getEndTimeOfDay(publicEndTimeTo);
        }
        // 21: 需求状态
        souReqStatus = StringUtils.trimToNull(souReqStatus);
        needPublic = StringUtils.trimToNull(needPublic);
        // 22: 需求提交时间
        if (approvalPassTimeFrom != null) {
            approvalPassTimeFrom = ApiExtSouProjectQueryDTO.getStartTimeOfDate(approvalPassTimeFrom);
        }
        if (approvalPassTimeTo != null) {
            approvalPassTimeTo = ApiExtSouProjectQueryDTO.getStartTimeOfDate(approvalPassTimeTo);
        }
        ifAppointBrand = StringUtils.trimToNull(ifAppointBrand);
        ifQualifyUnit = StringUtils.trimToNull(ifQualifyUnit);
        specialSouType = StringUtils.trimToNull(specialSouType);
        noPublicReason = StringUtils.trimToNull(noPublicReason);
        vendorName = StringUtils.trimToNull(vendorName);
        noPublicReasonChoose = StringUtils.trimToNull(noPublicReasonChoose);
    }

}
