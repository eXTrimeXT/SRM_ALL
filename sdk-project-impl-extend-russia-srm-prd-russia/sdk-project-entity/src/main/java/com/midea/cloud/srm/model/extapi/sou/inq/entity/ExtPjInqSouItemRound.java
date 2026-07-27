package com.midea.cloud.srm.model.extapi.sou.inq.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 长城 - 询比价 - 物料轮次信息
 * PS: 用来记录物料在哪个轮次是可报价的
 * @author huangbf3
 */
@Data
@TableName("scc_npm_sou_inq_item_round")
@EqualsAndHashCode(callSuper = true)
public class ExtPjInqSouItemRound extends BaseEntity<ExtPjInqSouItemRound> {

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
