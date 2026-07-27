package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.midea.cloud.srm.model.extapi.sou.auct.entity.ExtAuctSouOrderItemHis;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.order.MqlAuctSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.vo.order.handler.MqlAuctSouOrderItemDTOTypeHandler;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 竞价MQL - 报价明细历史快照
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/14
 */
@Data
@TableName("scc_sou_auct_order_item_his")
@EqualsAndHashCode(callSuper = true)
public class AuctSouOrderItemHis extends ExtAuctSouOrderItemHis {

    @TableId("auct_order_item_his_id")
    @ApiModelProperty("ID")
    private Long auctOrderItemHisId;

    /** @see SouOrderItem#getProjectId */
    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** @see SouOrderItem#getSouItemId */
    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("物料需求行ID")
    private Long souItemId;

    /** @see SouOrderItem#getOrderId */
    @TableField("ORDER_ID")
    @ApiModelProperty("报价单ID")
    private Long orderId;

    /** @see SouOrderItem#getOrderItemId */
    @TableField("ORDER_ITEM_ID")
    @ApiModelProperty("报价行ID")
    private Long orderItemId;

    /** @see SouOrderItem#getVendorId */
    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouOrderItem#getRound */
    @TableField("ROUND")
    @ApiModelProperty("轮次")
    private Integer round;

    // ----------------------------------------------------------- 提交人信息 --------------------------------------------------------------
    /** @see CompSouOrderItem#getSubmitById */
    @ApiModelProperty("提交人ID")
    @TableField("SUBMIT_BY_ID")
    private Long submitById;

    /** @see CompSouOrderItem#getSubmitBy */
    @ApiModelProperty("提交人账号")
    @TableField("SUBMIT_BY")
    private String submitBy;

    /** @see CompSouOrderItem#getSubmitByIp */
    @ApiModelProperty("提交人IP")
    @TableField("SUBMIT_BY_IP")
    private String submitByIp;

    /** @see CompSouOrderItem#getSubmitFullName */
    @ApiModelProperty("提交人昵称")
    @TableField("SUBMIT_FULL_NAME")
    private String submitFullName;

    /** @see CompSouOrderItem#getSubmitTime */
    @ApiModelProperty("提交时间")
    @TableField("SUBMIT_TIME")
    private Date submitTime;

    /**
     * 快照信息
     */
    @TableField(value = "ORDER_ITEM_INFO", typeHandler = MqlAuctSouOrderItemDTOTypeHandler.class)
    @ApiModelProperty("报价行快照信息")
    @JsonSerialize
    private MqlAuctSouOrderItemDTO orderItemInfo;

}
