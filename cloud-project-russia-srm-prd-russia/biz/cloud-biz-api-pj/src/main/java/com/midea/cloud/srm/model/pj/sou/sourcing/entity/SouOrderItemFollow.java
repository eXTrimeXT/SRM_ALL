package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderItemFollowStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderItemFollowTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源.核心表 - 供应商报价明细后续单据情况
 * PS: 该表在价格审批单回迁后才开始使用
 * PS: 主要用于寻源生成价格审批单时，记录关联的下游单据情况
 *
 * @author zhangwk12@midea.com
 * @since 2023/09/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_order_item_follow")
@ApiModel("供应商报价明细后续单据情况")
public class SouOrderItemFollow extends BaseEntity<SouOrderItemFollow> {

    @TableId("ORDER_ITEM_FOLLOW_ID")
    @ApiModelProperty("ID")
    private Long orderItemFollowId;

    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("物料需求ID")
    private Long souItemId;

    @TableField("ORDER_ID")
    @ApiModelProperty("报价单ID")
    private Long orderId;

    @TableField("ORDER_ITEM_ID")
    @ApiModelProperty("报价明细ID")
    private Long orderItemId;

    /** @see SouOrderItemFollowTypeEnum */
    @TableField("FOLLOW_TYPE")
    @ApiModelProperty("后续单据类型")
    private String followType;

    /** @see SouOrderItemFollowStatusEnum */
    @TableField("FOLLOW_STATUS")
    @ApiModelProperty("后续单据状态")
    private String followStatus;

    @TableField("FOLLOW_ID")
    @ApiModelProperty("后续单据ID")
    private String followId;

    @TableField("FOLLOW_NO")
    @ApiModelProperty("后续单据编号")
    private String followNo;

    @TableField("FOLLOW_NAME")
    @ApiModelProperty("后续单据名称")
    private String followName;

    @TableField("FOLLOW_LINE_ID")
    @ApiModelProperty("后续单据明细ID")
    private String followLineId;

}
