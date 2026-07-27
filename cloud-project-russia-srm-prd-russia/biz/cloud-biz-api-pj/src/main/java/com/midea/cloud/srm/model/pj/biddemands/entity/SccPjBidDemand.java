package com.midea.cloud.srm.model.pj.biddemands.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description scc_pj_bid_demand
 * @author panmq
 * @date 2023-09-25
 */
@Data
@ApiModel("scc_pj_bid_demand")
public class SccPjBidDemand extends BaseEntity {
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
 /**
  * 包名
  */
 private String bundleName;
 /**
  * 板块
  */
 private String plate;
 /**
  * 公司
  */
 private String companyname;
 /**
  * 申请部门
  */
 private String applicantDepart;
 /**
  * 申请人
  */
 private String applicant;
 /**
  * 投资编号
  */
 private String investNo;
 /**
  * 预算
  */
 private BigDecimal budget;
 /**
  * 规模数量
  */
 private String scaleQuantity;
 /**
  * 是否指定评标人
  */
 private String assignEvaluator;
 /**
  * 技术负责人
  */
 private String techPrincipal;

}

