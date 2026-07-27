package com.midea.cloud.srm.model.sou.report.bid.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/03/11/ $
 * @Description: 招标项目进度报表-查询实体类
 */
@Data
@ApiModel("招标项目进度报表-查询实体类")
public class ScheduleReportQueryDto extends BaseDTO {

    @ApiModelProperty("计划类型")
    private String requirementPlanType;
    @ApiModelProperty("板块")
    private String extOrgBuName;
    @ApiModelProperty("公司")
    private String extOrgOuName;
    /**
     * 公司简码
     */
    @ApiModelProperty("公司简码")
    private String companyShortCode;
    /**
     * 年
     */
    @ApiModelProperty("年")
    private String year;
    /**
     * 月
     */
    @ApiModelProperty("月")
    private String month;
    /**
     * 板块编码
     */
    @ApiModelProperty("板块编码")
    private String extOrgBuCode;
    @ApiModelProperty("项目创建时间从")
    private Date creationDateFrom;
    @ApiModelProperty("项目创建时间止")
    private Date creationDateTo;
    @ApiModelProperty("招标项目编号")
    private String extProjectNo;
    @ApiModelProperty("项目名称")
    private String souName;
    @ApiModelProperty("招标流程")
    private String extSouProcess;
    /**
     * 品类ID
     */
    @ApiModelProperty("品类ID")
    private Long extCategoryId;
    @ApiModelProperty("末级品类")
    private String extCategoryName;
    @ApiModelProperty("招标负责人")
    private String souPrincipal;
    @ApiModelProperty("供应商负责人")
    private String vendorPrincipal;
    @ApiModelProperty("预算金额（万元）起")
    private BigDecimal totalBudgetFrom;
    @ApiModelProperty("预算金额（万元）止")
    private BigDecimal totalBudgetTo;
    @ApiModelProperty("履约分数起止")
    private BigDecimal honourScoreFrom;
    @ApiModelProperty("履约分数")
    private BigDecimal honourScoreTo;
    @ApiModelProperty("履约结果")
    private String honourResult;
    @ApiModelProperty("招标状态")
    private String projectStatus;
    @ApiModelProperty("追加单位数量")
    private Long addBidNumber;
    @ApiModelProperty("追加单位投标数量")
    private Long addBidAsSubmitNumber;
    @ApiModelProperty("新供应商数量")
    private Long newVendorBidNumber;
    @ApiModelProperty("开发新单位数量")
    private Long newUniteVendorBidNumber;
    @ApiModelProperty("总发标单位数量，小于等于")
    private Long totalBidNumber;
    @ApiModelProperty("总投标单位数量，小于等于")
    private Long totalBidAsSubmitNumber;

    @ApiModelProperty("供应商推荐延期天数")
    private Long vendorPostponeCycle;

    @ApiModelProperty("收标延期天数")
    private Long acceptancePostponeCycle;

    @ApiModelProperty("汇总上报延期天数")
    private Long sumReportPostponeCycle;

    @ApiModelProperty("资料递交延期天数")
    private Long dataSubmitPostponeCycle;

    @ApiModelProperty("评标延期天数起")
    private Long evaluationPostponeCycleFrom;

    @ApiModelProperty("评标延期天数止")
    private Long evaluationPostponeCycleTo;

    @ApiModelProperty("定标延期天数起")
    private Long picketagePostponeCycleFrom;

    @ApiModelProperty("定标延期天数止")
    private Long picketagePostponeCycleTo;

    @ApiModelProperty("澄清次数")
    private Long answerIssuedCount;

    @ApiModelProperty("更新时间从")
    private Date lastupdateDateFrom;
    @ApiModelProperty("更新时间止")
    private Date lastupdateDateTo;

}
