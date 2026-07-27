package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description scc_sou_tech_score_line
 * @author panmq
 * @date 2023-10-09
 */
@Data
@ApiModel("scc_sou_tech_score_line")
@TableName(value = "scc_sou_tech_score_line")
public class ExtSouTechScoreLine extends BaseEntity {
 @TableId
 /**
  * ID
  */
 @ApiModelProperty("ID")
 private Long techScoreLineId;
 /**
  * 寻源单ID
  */
 @ApiModelProperty("寻源单ID")
 private Long projectId;
 /**
  * 评分规则明细ID
  */
 @ApiModelProperty("评分规则明细ID")
 private Long scoreRuleLineId;
 /**
  * 技术评分头ID
  */
 @ApiModelProperty("技术评分头ID")
 private Long techScoreHeadId;
 /**
  * 报价单ID
  */
 @ApiModelProperty("报价单ID")
 private Long orderId;
 /**
  * 工作小组成员ID
  */
 @ApiModelProperty("工作小组成员ID")
 private Long groupId;
 /**
  * 供应商ID
  */
 @ApiModelProperty("供应商ID")
 private Long vendorId;
 /**
  * 评分
  */
 @ApiModelProperty("评分")
 private BigDecimal score;
 /**
  * 评分说明
  */
 @ApiModelProperty("评分说明")
 private String extDescription;
}

