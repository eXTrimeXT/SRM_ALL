package com.midea.cloud.srm.model.pj.scoreconfigdetails.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 寻源评分配置项明细表
 * @author panmq
 * @date 2023-09-21
 */
@Data
@ApiModel("寻源评分配置项明细表")
public class SccPjSouScoreConfigDetail extends BaseEntity {
 @TableId
 /**
  * 主键
  */
 private Long configDetailId;
 /**
  * 关联寻源评分配置表主键
  */
 private Long scoreConfigId;
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

