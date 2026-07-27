package com.midea.cloud.srm.model.sou.report.bid.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: panmq
 * @Date: 2024/03/11/ $
 * @Description: 项目进度报表-实体类
 */
@Data
@ApiModel("项目进度报表-实体类")
public class ScheduleReportDto extends BaseDTO {

    /**
     * 关联键-招标项目主表ID
     */
    private Long projectId;

    @ApiModelProperty("计划类型")
    private String requirementPlanType;
    @ApiModelProperty("公司代码")
    private String companyShortCode;
    @ApiModelProperty("年")
    private String year;
    @ApiModelProperty("月")
    private String month;
    @ApiModelProperty("板块")
    private String extOrgBuName;
    @ApiModelProperty("板块代码")
    private String extOrgBuCode;
    @ApiModelProperty("公司")
    private String extOrgOuName;
    @ApiModelProperty("公司代码")
    private String extOrgOuCode;
    @ApiModelProperty("招标项目编号")
    private String extProjectNo;
    @ApiModelProperty("项目名称")
    private String souName;
    @ApiModelProperty("招标流程")
    private String extSouProcess;
    @ApiModelProperty("一级品类")
    private String classification;
    @ApiModelProperty("末级品类")
    private String extCategoryName;
    @ApiModelProperty("招标负责人")
    private String souPrincipal;
    @ApiModelProperty("供应商负责人")
    private String vendorPrincipal;
    @ApiModelProperty("评标组长")
    private String leaderPrincipal;
    @ApiModelProperty("技术负责人")
    private String extTechPrincipal;
    @ApiModelProperty("采购数量")
    private String extScaleQuantity;
    @ApiModelProperty("预算金额（万元）")
    private String totalBudget;
    @ApiModelProperty("申请资料计划递交时间")
    private String sendSouProfileEndDate;
    @ApiModelProperty("申请资料审核通过时间")
    private String approvalPassTime;
    @ApiModelProperty("公示截止日期")
    private String publicEndTime;
    @ApiModelProperty("计划出表时间")
    private String planRequirementTime;
    @ApiModelProperty("实际出表时间")
    private String actualRequirementTime;
    @ApiModelProperty("计划发标时间")
    private String planPublishTime;
    @ApiModelProperty("实际发标时间")
    private String actualPublishTime;
    @ApiModelProperty("计划收标时间")
    private String planAcceptanceBidTime;
    @ApiModelProperty("实际收标时间")
    private String actualAcceptanceBidTime;
    @ApiModelProperty("计划标评完时间")
    private String planTechEvaluationTime;
    @ApiModelProperty("实际标评完时间")
    private String actualTechEvaluationTime;
    @ApiModelProperty("计划汇总上报时间")
    private String planSumReportTime;
    @ApiModelProperty("实际汇总上报时间")
    private String actualSumReportTime;
    @ApiModelProperty("计划定标时间")
    private String planPicketageTime;
    @ApiModelProperty("实际定标时间")
    private String actualPicketageTime;
    @ApiModelProperty("计划中标通知时间")
    private String planPublishWinLossTime;
    @ApiModelProperty("实际中标通知时间")
    private String actualPublishWinLossTime;
    @ApiModelProperty("履约分数")
    private String honourScore;
    @ApiModelProperty("履约结果")
    private String honourResult;
    @ApiModelProperty("招标状态")
    private String projectStatus;
    @ApiModelProperty("发标单位数量")
    private String sendBidNumber;
    @ApiModelProperty("推荐单位投标数量")
    private String sendBidAsSubmitNumber;
    @ApiModelProperty("追加单位数量")
    private String addBidNumber;
    @ApiModelProperty("追加单位投标数量")
    private String addBidAsSubmitNumber;
    @ApiModelProperty("新供应商数量")
    private String newVendorBidNumber;
    @ApiModelProperty("开发新单位数量")
    private String newUniteVendorBidNumber;
    @ApiModelProperty("总发标单位数量")
    private String totalBidNumber;
    @ApiModelProperty("总投标单位数量")
    private String totalBidAsSubmitNumber;
    @ApiModelProperty("总计划周期")
    private String planTotalCycle;
    @ApiModelProperty("总实际周期")
    private String actualTotalCycle;
    @ApiModelProperty("供应商推荐延期天数")
    private String vendorPostponeCycle;
    @ApiModelProperty("发标延期天数")
    private String publishPostponeCycle;
    @ApiModelProperty("发标延期占比率")
    private String publishPostponeProportion;
    @ApiModelProperty("收标延期天数")
    private String acceptancePostponeCycle;
    @ApiModelProperty("收标延期占比率")
    private String acceptancePostponeProportion;
    @ApiModelProperty("汇总上报延期天数")
    private String sumReportPostponeCycle;
    @ApiModelProperty("汇总上报延期占比率")
    private String sumReportPostponeProportion;
    @ApiModelProperty("中标延期天数")
    private String winPostponeCycle;
    @ApiModelProperty("中标延期占比率")
    private String winPostponeProportion;
    @ApiModelProperty("资料递交延期天数")
    private String dataSubmitPostponeCycle;
    @ApiModelProperty("评标延期天数")
    private String evaluationPostponeCycle;
    @ApiModelProperty("评标延期占比率")
    private String evaluationPostponeProportion;
    @ApiModelProperty("定标延期天数")
    private String picketagePostponeCycle;
    @ApiModelProperty("定标延期占比率")
    private String picketagePostponeProportion;
    @ApiModelProperty("澄清次数")
    private String answerIssuedCount;
    @ApiModelProperty("取消原因")
    private String cancelReason;
    @ApiModelProperty("中标详情")
    private List<Map<String, Object>> winVendorInfoList;
}
