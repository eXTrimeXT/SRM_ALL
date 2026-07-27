package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description scc_sou_tech_score_head
 * @author panmq
 * @date 2023-10-09
 */
@Data
@ApiModel("scc_sou_tech_score_head")
@TableName(value = "scc_sou_tech_score_head")
public class ExtSouTechScoreHead extends BaseEntity {
 @TableId
 /**
  * ID
  */
 @ApiModelProperty("ID")
 private Long techScoreHeadId;
 /**
  * 寻源单ID
  */
 @ApiModelProperty("寻源单ID")
 private Long projectId;
 /**
  * 报价单ID
  */
 @ApiModelProperty("报价单ID")
 private Long orderId;
 /**
  * 评委ID
  */
 @ApiModelProperty("评委ID")
 private Long groupId;
 /**
  * 供应商ID
  */
 @ApiModelProperty("供应商ID")
 private Long vendorId;
 /**
  * 评分状态
  */
 @ApiModelProperty("评分状态-SOU_TECH_SCORE_STATUS")
 private String scoreStatus;
 /**
  * 技术评分总分
  */
 @ApiModelProperty("技术评分总分")
 private BigDecimal totalScore;
 /**
  * 技术评分意见
  */
 @ApiModelProperty("技术评分意见")
 private String techComments;
 /**
  * 驳回原因
  */
 @ApiModelProperty("驳回原因")
 private String extRejectReason;
 /**
  * 是否代理评分
  */
 @ApiModelProperty("是否代理评分")
 private String isProxy;

 @ApiModelProperty("是否已确认")
 private String extConfirmFlag;
}



