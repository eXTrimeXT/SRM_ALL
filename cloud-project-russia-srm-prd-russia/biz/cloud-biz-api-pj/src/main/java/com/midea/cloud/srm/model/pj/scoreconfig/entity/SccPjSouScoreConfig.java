package com.midea.cloud.srm.model.pj.scoreconfig.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
/**
 * @description 寻源评分配置表
 * @author panmq
 * @date 2023-09-21
 */
@Data
@ApiModel("寻源评分配置表")
public class SccPjSouScoreConfig extends BaseEntity {
 @TableId
 /**
  * 主键
  */
 private Long scoreConfigId;
 /**
  * 模板单号
  */
 private String configNumber;
 /**
  * 评分模板名称
  */
 private String scoreTempName;
 /**
  * 模板状态
  */
 private String status;
 /**
  * 模板版本
  */
 private Long configVer;
}

