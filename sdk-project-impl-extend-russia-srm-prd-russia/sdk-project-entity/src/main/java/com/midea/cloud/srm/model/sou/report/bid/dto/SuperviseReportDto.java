package com.midea.cloud.srm.model.sou.report.bid.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Data
@ApiModel("上报监察报表-实体类")
public class SuperviseReportDto extends BaseDTO {

    @ApiModelProperty("计划类型")
    private String requirementPlanType;

    @ApiModelProperty("板块")
    private String extOrgBuName;

    @ApiModelProperty("申请单位")
    private String extOrgOuName;

    @ApiModelProperty("公司代码")
    private String companyShortCode;

    @ApiModelProperty("年")
    private String year;

    @ApiModelProperty("月")
    private String month;

    @ApiModelProperty("完成月份")
    private String completeMonth;

    @ApiModelProperty("招标项目编号")
    private String extProjectNo;

    @ApiModelProperty("项目名称")
    private String souName;

    @ApiModelProperty("招标流程")
    private String extSouProcess;

    @ApiModelProperty("分类--改成一级品类名称")
    private String classification;

    @ApiModelProperty("采购分类名称")
    private String extCategoryName;

    @ApiModelProperty("标的物名称")
    private String itemDesc;

    @ApiModelProperty("招标负责人")
    private String souPrincipal;

    @ApiModelProperty("供应商负责人")
    private String vendorPrincipal;

    @ApiModelProperty("评标组长")
    private String leaderPrincipal;

    @ApiModelProperty("技术负责人")
    private String extTechPrincipal;

    @ApiModelProperty("技术负责人电话")
    private String extTechPrincipalTel;

    @ApiModelProperty("采购数量")
    private String extScaleQuantity;

    @ApiModelProperty("预算金额（万元）")
    private BigDecimal totalBudget;

    @ApiModelProperty("投标单位")
    private String vendorName;

    @ApiModelProperty("供应商来源")
    private String extVendorAttr;

    @ApiModelProperty("联系人")
    private String linkmanName;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("是否新供应商（只是填是或否）")
    private String extIsNewVendor;

    @ApiModelProperty("第一轮供应商报价总价（含税）")
    private BigDecimal quotedPriceWithTaxFirst;

    @ApiModelProperty("第二轮供应商报价总价（含税）")
    private BigDecimal quotedPriceWithTaxSecond;

    @ApiModelProperty("第三轮供应商报价总价（含税）")
    private BigDecimal quotedPriceWithTaxThird;

    @ApiModelProperty("第四轮供应商报价总价（含税）")
    private BigDecimal quotedPriceWithTaxFour;

    @ApiModelProperty("第五轮供应商报价总价（含税）")
    private BigDecimal quotedPriceWithTaxFive;

    @ApiModelProperty("第六轮供应商报价总价（含税）")
    private BigDecimal quotedPriceWithTaxSix;

    @ApiModelProperty("第七轮供应商报价总价（含税）")
    private BigDecimal quotedPriceWithTaxSeven;

    @ApiModelProperty("第八轮供应商报价总价（含税）")
    private BigDecimal quotedPriceWithTaxEight;

    @ApiModelProperty("技术得分")
    private BigDecimal techSocre;

    @ApiModelProperty("综合得分")
    private BigDecimal comprehensiveScore;

    @ApiModelProperty("是否中标")
    private String isWin;

    @ApiModelProperty("审批定标金额（万元）")
    private BigDecimal caPrice;

    @ApiModelProperty("中标通知金额（万元）")
    private BigDecimal noticePrice;

    @ApiModelProperty("招标单号")
    private String souNo;

    @ApiModelProperty("招标单ID")
    private Long projectId;

    @ApiModelProperty("预算")
    private BigDecimal extBudget;

    @ApiModelProperty("采购分类ID")
    private Long extCategoryId;

    @ApiModelProperty("采购分类编码")
    private String extCategoryCode;

    @ApiModelProperty("板块ID")
    private Long extOrgBuId;

    @ApiModelProperty("板块编码")
    private String extOrgBuCode;

    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @ApiModelProperty("公司ID")
    private Long extOrgOuId;

    @ApiModelProperty("公司编码")
    private String extOrgOuCode;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("投标ID")
    private String orderId;

    @ApiModelProperty("投标状态")
    private String orderStatus;

    @ApiModelProperty("项目状态")
    private String projectStatus;

    @ApiModelProperty("供应商来源名称")
    private String extVendorAttrName;
}
