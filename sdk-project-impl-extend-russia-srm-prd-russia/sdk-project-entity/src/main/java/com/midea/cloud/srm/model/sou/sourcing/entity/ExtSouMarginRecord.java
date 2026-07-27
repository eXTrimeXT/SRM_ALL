package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description scc_npm_sou_margin_record
 * @author panmq
 * @date 2023-10-07
 */
@Data
@ApiModel("scc_npm_sou_margin_record")
@TableName(value = "scc_npm_sou_margin_record")
public class ExtSouMarginRecord extends BaseEntity {
 @TableId
 /**
  * 主键
  */
 private Long recordId;
 /**
  * 关联保证金缴纳记录表ID
  */
 private Long marginId;
 /**
  * 关联招标基本信息主键ID
  */
 private Long projectId;
 /**
  * 供应商ID
  */
 private Long vendorId;
 /**
  * 供应商编码
  */
 private String vendorCode;
 /**
  * 供应商名称
  */
 private String vendorName;
 /**
  * 类型
  */
 private String type;
 /**
  * 扣款(退款)金额（万元）
  */
 private BigDecimal amount;
 /**
  * 公司（业务实体）ID
  */
 private Long ouOrgId;
 /**
  * 公司（业务实体）编码
  */
 private String ouOrgCode;
 /**
  * 公司（业务实体）名称
  */
 private String ouOrgName;
 /**
  * 退款账户
  */
 private String refundAccount;
 /**
  * 退款户名
  */
 private String refundAccountName;
 /**
  * 退款银行
  */
 private String refundBank;
 /**
  * 退款银行行号
  */
 private String refundBankNum;
 /**
  * 扣款（退款）说明
  */
 private String description;
 /**
  * 附件ID
  */
 private Long fileId;
 /**
  * 附件名称
  */
 private String fileName;

 @ApiModelProperty("期望退款时间")
 private Date expectRefundTime;

 @ApiModelProperty("是否拉黑")
 private String blackFlag;

 /**
  * 扣款类型
  */
 private String extDeductionType;
}

