package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description scc_npm_sou_expert_risk
 * @author panmq
 * @date 2023-10-20
 */
@Data
@ApiModel("scc_npm_sou_expert_risk")
@TableName(value = "scc_npm_sou_expert_risk")
public class ExtSouExpertRisk extends BaseEntity {
 @TableId
 /**
  * 主键
  */
 @ApiModelProperty("主键")
 private Long expertRiskId;
 /**
  * 关联招标基本信息主键ID
  */
 @ApiModelProperty("关联招标基本信息主键ID")
 private Long projectId;
 /**
  * 账号ID
  */
 @ApiModelProperty("账号ID")
 private Long userId;
 /**
  * 工（账）号
  */
 @ApiModelProperty("工（账）号")
 private String userName;
 /**
  * 名字
  */
 @ApiModelProperty("名字")
 private String fullName;
 /**
  * 供应商ID
  */
 @ApiModelProperty("供应商ID")
 private Long vendorId;
 /**
  * 供应商编码
  */
 @ApiModelProperty("供应商编码")
 private String vendorCode;
 /**
  * 供应商名称
  */
 @ApiModelProperty("供应商名称")
 private String vendorName;

 /**
  * 风险标识
  */
 @ApiModelProperty("风险标识")
 private String riskFlag;
}

