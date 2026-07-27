package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 招标计划 - excel导入数据定义
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementImportExcelDTO extends BaseObjectX {

    /** @see PrRequirementHead#getDemandType */
    @ApiModelProperty("*需求类型")
    private String demandType;

    /** @see PrRequirementHead#getOrgCode */
    @ApiModelProperty("*申请公司编码")
    private String orgCode;

    /** @see ExtPrSouRequirementGroup#getUsername */
    @ApiModelProperty("*技术负责人账号")
    private String techUsername;

    /** @see ExtPrSouRequirementHead#getRequireFrom */
    @ApiModelProperty("*需求来源")
    private String requireFrom;

    /** @see ExtPrSouRequirementHead#getNoReportMonthPlanReason */
    @ApiModelProperty("未报月度计划原因")
    private String noReportMonthPlanReason;

    /** @see ExtPrSouRequirementHead#getProjectName */
    @ApiModelProperty("*项目名称")
    private String projectName;

    /** @see ExtPrSouRequirementHead#getProjectMonth */
    @ApiModelProperty("月份")
    private String projectMonth;

    /** @see PrRequirementHead#getCategoryCode */
    @ApiModelProperty("*所属品类编码")
    private String categoryCode;

    /** @see ExtPrSouRequirementHead#getInvestNo */
    @ApiModelProperty("投资编号")
    private String investNo;

    /** @see ExtPrSouRequirementHead#getRequireQuantity */
    @ApiModelProperty("*数量/规模")
    private String requireQuantity;

    /** @see ExtPrSouRequirementHead#getTotalAmountByTenKilo */
    @ApiModelProperty("*概算金额(万元)")
    private BigDecimal totalAmountByTenKilo;

    /** @see ExtPrSouRequirementHead#getNeedPublic */
    @ApiModelProperty("*是否公示")
    private Enable needPublic;

    /** @see ExtPrSouRequirementHead#getNoPublicReason */
    @ApiModelProperty("不公示理由")
    private String noPublicReason;

    /** @see ExtPrSouRequirementHead#getNoPublicReasonChoose */
    @ApiModelProperty("不公示理由选择")
    private String noPublicReasonChoose;

    /** @see ExtPrSouRequirementHead#getProjectAddress */
    @ApiModelProperty("*项目所在地")
    private String projectAddress;

    /** @see ExtPrSouRequirementHead#getPrefixTechDiscussion */
    @ApiModelProperty("*前置技术交流意向")
    private Enable prefixTechDiscussion;

    /** @see ExtPrSouRequirementHead#getPublicEndTime */
    @ApiModelProperty("*公示截止时间")
    private Date publicEndTime;

    /** @see ExtPrSouRequirementHead#getSendSouProfileEndDate */
    @ApiModelProperty("递交招标资料时间")
    private LocalDate sendSouProfileEndDate;

    /** @see ExtPrSouRequirementVendor#getRecommendFrom */
    @ApiModelProperty("推荐单位来源1")
    private String recommendFrom1;

    /** @see ExtPrSouRequirementVendor#getVendorName */
    @ApiModelProperty("推荐单位名称1")
    private String vendorName1;

    /** @see ExtPrSouRequirementVendor#getContactName */
    @ApiModelProperty("联系人名称1")
    private String vendorContactName1;

    /** @see ExtPrSouRequirementVendor#getPhone */
    @ApiModelProperty("联系方式1")
    private String vendorPhone1;

    /** @see ExtPrSouRequirementVendor#getEmail */
    @ApiModelProperty("邮箱1")
    private String vendorEmail1;

    /** @see ExtPrSouRequirementVendor#getRecommendFrom */
    @ApiModelProperty("推荐单位来源2")
    private String recommendFrom2;

    /** @see ExtPrSouRequirementVendor#getVendorName */
    @ApiModelProperty("推荐单位名称2")
    private String vendorName2;

    /** @see ExtPrSouRequirementVendor#getContactName */
    @ApiModelProperty("联系人名称2")
    private String vendorContactName2;

    /** @see ExtPrSouRequirementVendor#getPhone */
    @ApiModelProperty("联系方式2")
    private String vendorPhone2;

    /** @see ExtPrSouRequirementVendor#getEmail */
    @ApiModelProperty("邮箱2")
    private String vendorEmail2;

    /** @see ExtPrSouRequirementVendor#getRecommendFrom */
    @ApiModelProperty("推荐单位来源3")
    private String recommendFrom3;

    /** @see ExtPrSouRequirementVendor#getVendorName */
    @ApiModelProperty("推荐单位名称3")
    private String vendorName3;

    /** @see ExtPrSouRequirementVendor#getContactName */
    @ApiModelProperty("联系人名称3")
    private String vendorContactName3;

    /** @see ExtPrSouRequirementVendor#getPhone */
    @ApiModelProperty("联系方式3")
    private String vendorPhone3;

    /** @see ExtPrSouRequirementVendor#getEmail */
    @ApiModelProperty("邮箱3")
    private String vendorEmail3;

    /** @see ExtPrSouRequirementHead#getProjectOverview */
    @ApiModelProperty("*项目概况及范围")
    private String projectOverview;

    /** @see ExtPrSouRequirementHead#getTechRequire */
    @ApiModelProperty("*技术要求")
    private String techRequire;

    /** @see ExtPrSouRequirementHead#getPerformanceRequire */
    @ApiModelProperty("*业绩要求")
    private String performanceRequire;

    /** @see ExtPrSouRequirementHead#getVendorQualificationRequire */
    @ApiModelProperty("*供应商资质要求")
    private String vendorQualificationRequire;

    /** @see ExtPrSouRequirementHead#getIfAppointBrand */
    @ApiModelProperty("是否限定品牌")
    private Enable ifAppointBrand;

    /** @see ExtPrSouRequirementHead#getIfQualifyUnit */
    @ApiModelProperty("是否限定单位")
    private Enable ifQualifyUnit;

}
