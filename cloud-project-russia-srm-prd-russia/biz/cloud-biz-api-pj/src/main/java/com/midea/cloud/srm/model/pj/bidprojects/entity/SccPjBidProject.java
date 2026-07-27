package com.midea.cloud.srm.model.pj.bidprojects.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description scc_pj_bid_project
 * @author panmq
 * @date 2023-09-25
 */
@Data
@ApiModel("scc_pj_bid_project")
public class SccPjBidProject extends BaseEntity {
 @TableId
 /**
  * 主键ID
  */
 private Long projectId;
 /**
  * 招标项目编号
  */
 private String souNo;
 /**
  * 项目名称
  */
 private String souName;
 /**
  * 招标地点
  */
 private String orderSite;
 /**
  * 招标流程
  */
 private Long processConfigId;
 /**
  * 招标类型（公开、邀请）
  */
 private String publishScope;
 /**
  * 收标方式
  */
 private String souMode;
 /**
  * 评分规则（字典）
  */
 private String scoreRule;
 /**
  * 评标模板ID
  */
 private Long scoreTemplateId;
 /**
  * 评标模板
  */
 private String scoreTemplateName;
 /**
  * 品类ID
  */
 private Long categoryId;
 /**
  * 品类编码
  */
 private String categoryCode;
 /**
  * 品类
  */
 private String categoryName;
 /**
  * 投标截止时间
  */
 private Date orderEndTime;
 /**
  * 招标类型
  */
 private String souType;
 /**
  * 是否缴纳保证金
  */
 private String earnestFlag;
 /**
  * 保证金缴纳金额（万元）
  */
 private BigDecimal earnestAmount;
 /**
  * 开户银行
  */
 private String bankName;
 /**
  * 开户行号
  */
 private String bankNumber;
 /**
  * 开户账号
  */
 private String bankAccount;
 /**
  * 开户户名
  */
 private String bankAccountName;
 /**
  * 联系人姓名
  */
 private String linkman;
 /**
  * 联系人办公电话
  */
 private String tel;
 /**
  * 联系人邮箱
  */
 private String email;
 /**
  * 推荐供应商单号
  */
 private String recommendNo;
 /**
  * 是否合并招标
  */
 private String mergeBidFlag;
 /**
  * 当前轮次
  */
 private Integer currentRound;
 /**
  * 项目状态
  */
 private String projectStatus;
 /**
  * 立项审核状态
  */
 private String createApprovalStatus;

}

