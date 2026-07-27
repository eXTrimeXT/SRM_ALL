package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 寻源核心-供应商报价头
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
@ApiModel(description = "寻源核心-供应商报价头")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_order")
public class SouOrder extends BaseEntity<SouOrder> {

    @ApiModelProperty("寻源核心-供应商报价头ID")
    @TableId("ORDER_ID")
    private Long orderId;

    @ApiModelProperty("供应商报价单号")
    @TableField("ORDER_NO")
    private String orderNo;

    /**
     * @see SouProject#getProjectId
     */
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /**
     * @see SouVendor#getVendorId
     */
    @ApiModelProperty("供应商ID")
    @TableField("VENDOR_ID")
    private Long vendorId;

    /**
     * @see SouRound#getRound
     */
    @ApiModelProperty("报价轮次")
    @TableField("ROUND")
    private Integer round;

    @ApiModelProperty("已报价次数")
    @TableField("ORDER_ROUND")
    private Integer orderRound;

    @ApiModelProperty("报价单状态")
    @TableField("ORDER_STATUS")
    private SouOrderStatusEnum orderStatus;

    /** 提交人信息 */
    @ApiModelProperty("提交人ID")
    @TableField("SUBMIT_BY_ID")
    private Long submitById;

    @ApiModelProperty("提交人账号")
    @TableField("SUBMIT_BY")
    private String submitBy;

    @ApiModelProperty("提交人IP")
    @TableField("SUBMIT_BY_IP")
    private String submitByIp;

    @ApiModelProperty("提交人昵称")
    @TableField("SUBMIT_FULL_NAME")
    private String submitFullName;

    @ApiModelProperty("提交时间")
    @TableField("SUBMIT_TIME")
    private Date submitTime;

    /** 撤回/作废 */
    @ApiModelProperty("撤回原因")
    @TableField("WITHDRAW_REASON")
    private String withdrawReason;

    @ApiModelProperty("撤回时间")
    @TableField("WITHDRAW_TIME")
    private Date withdrawTime;

    @ApiModelProperty("作废原因")
    @TableField("REJECT_REASON")
    private String rejectReason;

    @ApiModelProperty("作废时间")
    @TableField("REJECT_TIME")
    private Date rejectTime;

    /** 价格信息 */
    @ApiModelProperty("本币未税总价")
    @TableField("STANDARD_NOTAX_TOTAL_PRICE")
    private BigDecimal standardNotaxTotalPrice;

    @ApiModelProperty("本币含税总价")
    @TableField("STANDARD_TAX_TOTAL_PRICE")
    private BigDecimal standardTaxTotalPrice;

    /** 代理信息 */
    @ApiModelProperty("是否代理报价")
    @TableField("IS_PROXY")
    private Enable isProxy;

    @ApiModelProperty("代理授权文件ID")
    @TableField("PROXY_DOC_ID")
    private Long proxyDocId;

    @ApiModelProperty("代理授权文件名称")
    @TableField("PROXY_FILE_NAME")
    private String proxyFileName;

    @ApiModelProperty("代理授权说明")
    @TableField("PROXY_REMARK")
    private String proxyRemark;

}
