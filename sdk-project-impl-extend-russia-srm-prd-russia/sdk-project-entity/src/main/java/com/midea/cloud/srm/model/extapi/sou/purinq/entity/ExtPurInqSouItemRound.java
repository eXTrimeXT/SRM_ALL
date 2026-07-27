package com.midea.cloud.srm.model.extapi.sou.purinq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@TableName("scc_npm_sou_purinq_item_round")
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouItemRound extends BaseEntity<ExtPurInqSouItemRound> {

    @TableId("INQ_SOU_ITEM_ROUND_ID")
    @ApiModelProperty("ID")
    private Long inqSouItemRoundId;

    @TableField("PROJECT_ID")
    @ApiModelProperty("询价单ID")
    private Long projectId;

    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("物料需求ID")
    private Long souItemId;

    @TableField("ROUND")
    @ApiModelProperty("轮次")
    private Integer round;

    @TableField("CAN_ORDER")
    @ApiModelProperty("物料在指定轮次是否可报价")
    private Enable canOrder;

}
