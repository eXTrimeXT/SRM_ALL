package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description scc_npm_sou_plan
 * @author panmq
 * @date 2023-09-26
 */
@Data
@ApiModel("scc_npm_sou_plan")
@TableName("scc_npm_sou_plan")
public class ExtSouPlan extends BaseEntity {
 @TableId
 /**
  * 主键ID
  */
 private Long planId;
 /**
  * 关联招标基本信息主键ID
  */
 private Long projectId;
 /**
  * 计划类型
  */
 private String planType;
 /**
  * 招标单创建时间
  */
 private Date bidCreationDate;
 /**
  * 发标时间（招标单发布时间）
  */
 private Date publishTime;
 /**
  * 技术标截止时间
  */
 private Date techEndTime;

 @ApiModelProperty("技术标调整后的截止时间")
 private Date techEndFixTime;
 /**
  * 开技术标时间
  */
 private Date techOpenTime;
 /**
  * 技术评标时间
  */
 private Date techEvaluationTime;
 /**
  * 商务标截止时间
  */
 private Date busEndTime;
 /**
  * 开价格标时间
  */
 private Date priceOpenTime;
 /**
  * 汇总上报时间
  */
 private Date sumReportTime;
 /**
  * 定标时间
  */
 private Date picketageTime;
 /**
  * 发布中落标时间
  */
 private Date publishWinLossTime;
 /**
  * 完成时间
  */
 private Date completeTime;
}

