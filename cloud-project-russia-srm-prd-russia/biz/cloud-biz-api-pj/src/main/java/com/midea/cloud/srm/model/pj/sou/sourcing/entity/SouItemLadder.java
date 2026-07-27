package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 寻源核心-阶梯价模板
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/15
 */
@ApiModel(description = "寻源核心-阶梯价模板")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_item_ladder")
public class SouItemLadder extends BaseEntity<SouItemLadder> {

    @ApiModelProperty("寻源核心-阶梯价模板ID")
    @TableId("SOU_ITEM_LADDER_ID")
    private Long souItemLadderId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouItem#getSouItemId */
    @ApiModelProperty("物料需求行ID")
    @TableField("SOU_ITEM_ID")
    private Long souItemId;

    @ApiModelProperty("阶梯区间从")
    @TableField("BEGIN_QUANTITY")
    private BigDecimal beginQuantity;

    @ApiModelProperty("阶梯区间到")
    @TableField("END_QUANTITY")
    private BigDecimal endQuantity;

    @ApiModelProperty("排序")
    @TableField("SORT_INDEX")
    private Integer sortIndex;

}
