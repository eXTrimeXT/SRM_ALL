package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description scc_npm_sou_ajust_time
 * @author panmq
 * @date 2023-12-13
 */
@Data
@ApiModel("scc_npm_sou_ajust_time")
@TableName(value = "scc_npm_sou_ajust_time")
public class ExtNpmSouAjustTime extends BaseEntity {
 @TableId
 /**
  * ID
  */
 @ApiModelProperty("ID")
 private Long ajustTimeId;
 /**
  * 寻源单ID
  */
 @ApiModelProperty("寻源单ID")
 private Long projectId;
 /**
  * 当前投标截止时间
  */
 @ApiModelProperty("当前投标截止时间")
 private Date currentEndTime;
 /**
  * 调整截止时间为
  */
 @ApiModelProperty("调整截止时间为")
 private Date adjustEndTime;
 /**
  * 调整原因
  */
 @ApiModelProperty("调整原因")
 private String adjustReason;
}

