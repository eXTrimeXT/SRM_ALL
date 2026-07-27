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
 * @author panmq
 * @description scc_npm_sou_margin
 * @date 2023-10-07
 */
@Data
@ApiModel("scc_npm_sou_margin")
@TableName("scc_npm_sou_margin")
public class ExtSouMargin extends BaseEntity {
 @TableId
 /**
  * 主键
  */
 private Long marginId;
 /**
  * 关联招标基本信息主键ID
  */
 private Long projectId;

    /**
     * 来源招标单ID，年度保证金用
     */
    private Long sourceProjectId;

    /**
     * 引用年度保证金
     */
    private Long relYearMarginId;
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
  * 是否缴纳
  */
 private String payFlag;
 /**
  * 缴纳凭证
  */
 private String payVoucher;

 @ApiModelProperty("缴纳凭证附件ID")
 private Long payVoucherFileId;
 /**
  * 供应商缴纳账户
  */
 private String payAccount;
 /**
  * 缴纳户名
  */
 private String payAccountName;
 /**
  * 缴纳银行
  */
 private String payBank;
 /**
  * 缴纳金额（万元）
  */
 private BigDecimal payAmount;
 /**
  * 扣款金额（万元）
  */
 private BigDecimal chargeAmount;
 /**
  * 可退款金额（万元）
  */
 private BigDecimal refundAmount;
 /**
  * 是否年度保证金
  */
 private String yearFlag;
 /**
  * 是否内部供应商
  */
 private String innerFlag;
 /**
  * 是否允许不缴纳
  */
 private String noNeedPayFlag;
 /**
  * 不缴纳的原因
  */
 private String noPayCause;
 /**
  * 退款状态
  */
 private String refundStatus;
 /**
  * 退款失败原因
  */
 private String refundFailCause;

 /**
  * 品类ID
  */
 private Long categoryId;
 /**
  * 品类编码
  */
 private String categoryCode;
 /**
  * 品类名称
  */
 private String categoryName;

 @ApiModelProperty("保证金缴纳状态")
 private String marginStatus;

 /**
  * 退款失败原因
  */
 @ApiModelProperty("保证金缴纳失败原因")
 private String marginFailCause;


 @ApiModelProperty("处理方式, 字典：MARGIN_HANDER_MODE")
 private String handerMode;

    @ApiModelProperty("原因说明")
    private String causeDesc;

    @ApiModelProperty("付款方名称")
    private String payName;

    @ApiModelProperty("银行行号")
    private String bankLine;

    @ApiModelProperty("退款时间")
    private Date refundPaymentDate;

    @ApiModelProperty("付款时间")
    private Date transTime;

    /**
     * 是否代缴
     */
    private String extIsBehalfPay;
}

