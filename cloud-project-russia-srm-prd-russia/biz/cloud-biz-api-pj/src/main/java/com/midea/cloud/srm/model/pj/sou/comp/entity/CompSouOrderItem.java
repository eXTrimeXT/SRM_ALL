package com.midea.cloud.srm.model.pj.sou.comp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.comp.entity.ExtCompSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 竞价 - 报价行
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_comp_order_item")
@ApiModel("竞价.报价行")
public class CompSouOrderItem extends ExtCompSouOrderItem {

    /** @see SouOrderItem#getOrderItemId */
    @TableId("ORDER_ITEM_ID")
    @ApiModelProperty("物料需求行ID")
    private Long orderItemId;

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

    /** @see SouOrderItem#getVendorId */
    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouOrderItem#getRound */
    @TableField("ROUND")
    @ApiModelProperty("轮次")
    private Integer round;

    @TableField("FORMULA_RESULT")
    @ApiModelProperty("供应商填写的公式报价json")
    private String formulaResult;

    /**
     * 提交人信息
     */
    @TableField("SUBMIT_BY_ID")
    @ApiModelProperty("提交人ID")
    private Long submitById;

    @TableField("SUBMIT_BY")
    @ApiModelProperty("提交人账号")
    private String submitBy;

    @TableField("SUBMIT_BY_IP")
    @ApiModelProperty("提交人IP")
    private String submitByIp;

    @TableField("SUBMIT_FULL_NAME")
    @ApiModelProperty("提交人昵称")
    private String submitFullName;

    @TableField("SUBMIT_TIME")
    @ApiModelProperty("提交时间")
    private Date submitTime;

}
