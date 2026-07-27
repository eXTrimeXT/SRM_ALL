package com.midea.cloud.srm.model.sou.report.bid.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: panmq
 * @Date: 2024/03/08/ $
 * @Description: 上报监察报表查询条件
 */
@Data
@ApiModel("上报监察报表查询条件")
public class SuperviseReportQueryDto extends BaseDTO {

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

    @ApiModelProperty("板块代码")
    private String extOrgBuCode;

    @ApiModelProperty("招标项目编号")
    private String extProjectNo;

    @ApiModelProperty("项目名称")
    private String souName;

    @ApiModelProperty("招标流程")
    private String extSouProcess;

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

    @ApiModelProperty("预算金额（万元）起")
    private BigDecimal totalBudgetFrom;

    @ApiModelProperty("预算金额（万元）止")
    private BigDecimal totalBudgetTo;

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

    @ApiModelProperty("技术得分起")
    private BigDecimal techSocreFrom;

    @ApiModelProperty("技术得分止")
    private BigDecimal techSocreTo;

    @ApiModelProperty("综合得分得分起")
    private BigDecimal comprehensiveScoreFrom;

    @ApiModelProperty("综合得分得分止")
    private BigDecimal comprehensiveScoreTo;

    @ApiModelProperty("是否中标")
    private String isWin;

}
