package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description scc_npm_sou_demand
 * @author panmq
 * @date 2023-09-25
 */
@Data
@ApiModel("scc_npm_sou_demand")
@TableName("scc_npm_sou_demand")
public class ExtSouDemand extends BaseEntity {
 @TableId
 /**
  * 主键ID
  */
 private Long demandId;
 /**
  * 关联招标基本信息主键ID
  */
 private Long projectId;
 /**
  * 申请单号
  */
 private String applicantNo;

 @ApiModelProperty("包名")
 private String packageName;

 @ApiModelProperty("排序")
 private Integer sortIndex;

 @ApiModelProperty("状态，0：正常，1：取消")
 private Integer status;

}

