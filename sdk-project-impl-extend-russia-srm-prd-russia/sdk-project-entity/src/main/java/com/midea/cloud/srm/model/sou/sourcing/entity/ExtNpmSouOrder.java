package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author panmq
 * @description ext_scc_sou_order
 * @date 2023-11-08
 */
@Data
@ApiModel("ext_scc_sou_order")
@TableName(value = "ext_scc_sou_order")
public class ExtNpmSouOrder extends BaseEntity {
    @TableId
    /**
     * ID
     */
    @ApiModelProperty("ID")
    private Long extOrderId;
    /**
     * 投标ID
     */
    @ApiModelProperty("投标ID")
    private Long orderId;
    /**
     * 报价轮次
     */
    @ApiModelProperty("报价轮次")
    private Integer round;
    /**
     * 报价状态
     */
    @ApiModelProperty("报价状态")
    private String orderStatus;
    /**
     * 投标类型
     */
    @ApiModelProperty("投标类型")
    private String extOrderType;
    /**
     * 提交人ID
     */
    @ApiModelProperty("提交人ID")
    private Long submitById;
    /**
     * 提交人账号
     */
    @ApiModelProperty("提交人账号")
    private String submitBy;
    /**
     * 提交人IP
     */
    @ApiModelProperty("提交人IP")
    private String submitByIp;
    /**
     * 提交人昵称
     */
    @ApiModelProperty("提交人昵称")
    private String submitFullName;
    /**
     * 提交时间
     */
    @ApiModelProperty("提交时间")
    private Date submitTime;
    /**
     * 撤回报价原因
     */
    @ApiModelProperty("撤回报价原因")
    private String withdrawReason;
    /**
     * 撤回报价时间
     */
    @ApiModelProperty("撤回报价时间")
    private Date withdrawTime;
    /**
     * 作废报价原因
     */
    @ApiModelProperty("作废报价原因")
    private String rejectReason;
    /**
     * 作废报价时间
     */
    @ApiModelProperty("作废报价时间")
    private Date rejectTime;
    /**
     * 技术投标标识
     */
    @ApiModelProperty("技术投标标识")
    private String extTechFlag;
    /**
     * 下载标书时间
     */
    @ApiModelProperty("下载标书时间")
    private Date extDownBidFileTime;
    /**
     * 是否查阅标书
     */
    @ApiModelProperty("是否查阅标书")
    private String extReadBidFlag;

    @ApiModelProperty("不参与原因")
    private String extNotjoinReason;
}

