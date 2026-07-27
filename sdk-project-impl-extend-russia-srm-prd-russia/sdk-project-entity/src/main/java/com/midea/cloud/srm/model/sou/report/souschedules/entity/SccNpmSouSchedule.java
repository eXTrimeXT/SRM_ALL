package com.midea.cloud.srm.model.sou.report.souschedules.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: panmq
 * @Date: 2024/03/12/ $
 * @Description: 招标项目进度报表
 */
@Data
@ApiModel("招标项目进度报表")
@TableName(value = "scc_npm_sou_schedule")
public class SccNpmSouSchedule extends BaseEntity<SccNpmSouSchedule> {

    @TableId
    /**
     * 主键
     */
    private Long scheduleId;
    /**
     * 计划类型
     */
    private String requirementPlanType;
    /**
     * 关联键-招标项目主表ID
     */
    private Long projectId;
    /**
     * 公司简码
     */
    private String companyShortCode;
    /**
     * 年
     */
    private String year;
    /**
     * 月
     */
    private String month;
    /**
     * 一级品类
     */
    private String classification;
    /**
     * 招标负责人
     */
    private String souPrincipal;
    /**
     * 供应商负责人
     */
    private String vendorPrincipal;
    /**
     * 评标组长
     */
    private String leaderPrincipal;
    /**
     * 技术负责人
     */
    private String extTechPrincipal;
    /**
     * 申请资料计划递交时间
     */
    private Date sendSouProfileEndDate;
    /**
     * 申请资料审核通过时间
     */
    private Date approvalPassTime;
    /**
     * 公示截止日期
     */
    private Date publicEndTime;
    /**
     * 计划出表时间
     */
    private Date planRequirementTime;
    /**
     * 实际出表时间
     */
    private Date actualRequirementTime;
    /**
     * 计划发标时间
     */
    private Date planPublishTime;
    /**
     * 实际发标时间
     */
    private Date actualPublishTime;
    /**
     * 计划收标时间
     */
    private Date planAcceptanceBidTime;
    /**
     * 实际收标时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Date actualAcceptanceBidTime;
    /**
     * 计划标评完时间
     */
    private Date planTechEvaluationTime;
    /**
     * 实际标评完时间
     */
    private Date actualTechEvaluationTime;
    /**
     * 计划汇总上报时间
     */
    private Date planSumReportTime;
    /**
     * 实际汇总上报时间
     */
    private Date actualSumReportTime;
    /**
     * 计划定标时间
     */
    private Date planPicketageTime;
    /**
     * 实际定标时间
     */
    private Date actualPicketageTime;
    /**
     * 计划中标通知时间
     */
    private Date planPublishWinLossTime;
    /**
     * 实际中标通知时间
     */
    private Date actualPublishWinLossTime;
    /**
     * 履约分数
     */
    private BigDecimal honourScore;
    /**
     * 履约结果
     */
    private String honourResult;
    /**
     * 发标单位数量
     */
    private Long sendBidNumber;
    /**
     * 推荐单位投标数量
     */
    private Long sendBidAsSubmitNumber;
    /**
     * 追加单位数量
     */
    private Long addBidNumber;
    /**
     * 追加单位投标数量
     */
    private Long addBidAsSubmitNumber;
    /**
     * 新供应商数量
     */
    private Long newVendorBidNumber;
    /**
     * 开发新单位数量
     */
    private Long newUniteVendorBidNumber;
    /**
     * 总发标单位数量
     */
    private Long totalBidNumber;
    /**
     * 总投标单位数量
     */
    private Long totalBidAsSubmitNumber;
    /**
     * 总计划周期
     */
    private Long planTotalCycle;
    /**
     * 总实际周期
     */
    private Long actualTotalCycle;
    /**
     * 供应商推荐延期天数
     */
    private Long vendorPostponeCycle;
    /**
     * 发标延期天数
     */
    private Long publishPostponeCycle;
    /**
     * 发标延期占比率
     */
    private BigDecimal publishPostponeProportion;
    /**
     * 收标延期天数
     */
    private Long acceptancePostponeCycle;
    /**
     * 收标延期占比率
     */
    private BigDecimal acceptancePostponeProportion;
    /**
     * 汇总上报延期天数
     */
    private Long sumReportPostponeCycle;
    /**
     * 汇总上报延期占比率
     */
    private BigDecimal sumReportPostponeProportion;
    /**
     * 中标延期天数
     */
    private Long winPostponeCycle;
    /**
     * 中标延期占比率
     */
    private BigDecimal winPostponeProportion;
    /**
     * 资料递交延期天数
     */
    private Long dataSubmitPostponeCycle;
    /**
     * 评标延期天数
     */
    private Long evaluationPostponeCycle;
    /**
     * 评标延期占比率
     */
    private BigDecimal evaluationPostponeProportion;
    /**
     * 定标延期天数
     */
    private Long picketagePostponeCycle;
    /**
     * 定标延期占比率
     */
    private BigDecimal picketagePostponeProportion;
    /**
     * 澄清次数
     */
    private Long answerIssuedCount;
    /**
     * 取消原因
     */
    private String cancelReason;
}
