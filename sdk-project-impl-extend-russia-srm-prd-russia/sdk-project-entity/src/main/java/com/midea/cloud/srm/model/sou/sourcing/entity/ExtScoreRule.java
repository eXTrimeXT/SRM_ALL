package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description scc_npm_score_rule
 * @author panmq
 * @date 2023-10-06
 */
@Data
@ApiModel("scc_npm_score_rule")
@TableName("scc_npm_score_rule")
public class ExtScoreRule extends BaseEntity {
 @TableId
 /**
  * 主键
  */
 private Long scoreRuleId;
 /**
  * 关联寻源评分配置明细表主键
  */
 private Long configDetailId;
 /**
  * 关联寻源评分配置表主键
  */
 private Long scoreConfigId;
 /**
  * 关联招标基本信息主键ID
  */
 private Long projectId;
 /**
  * 打分项
  */
 private String scoreItem;
 /**
  * 评审项
  */
 private String reviewItem;
 /**
  * 评分说明
  */
 private String scoreDesc;
 /**
  * 最高分值
  */
 private String maxScore;
}

