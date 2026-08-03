package com.midea.cloud.srm.model.pj.sou.comp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.comp.entity.ExtCompSouOrderItemHis;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompOrderItemHisInfoVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.typehandler.ApiCompSouOrderItemHisInfoVOTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 竞价 - 报价行历史快照
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "scc_sou_comp_order_item_his", autoResultMap = true)
@ApiModel("竞价.报价行历史快照")
public class CompSouOrderItemHis extends ExtCompSouOrderItemHis {

    @TableId("COMP_ORDER_ITEM_HIS_ID")
    @ApiModelProperty("ID")
    private Long compOrderItemHisId;

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
    @TableField(value = "ORDER_ITEM_INFO", typeHandler = ApiCompSouOrderItemHisInfoVOTypeHandler.class)
    @ApiModelProperty("报价行快照信息")
    private ApiCompOrderItemHisInfoVO orderItemInfo;

}
