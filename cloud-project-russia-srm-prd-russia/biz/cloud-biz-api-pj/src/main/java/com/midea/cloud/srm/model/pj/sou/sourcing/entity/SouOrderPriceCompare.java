package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源核心 - 价格对比缓存数据
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/24
 */
@Data
@TableName("scc_sou_order_price_compare")
@EqualsAndHashCode(callSuper = true)
public class SouOrderPriceCompare extends BaseEntity<SouOrderPriceCompare> {

    @TableId("PRICE_COMPARE_ID")
    private Long priceCompareId;

    @ApiModelProperty("寻源核心-询价单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    @ApiModelProperty("寻源核心-需求物料ID")
    @TableField("SOU_ITEM_ID")
    private Long souItemId;

    @ApiModelProperty("轮次 PS: 不同轮次，数据可能不一样")
    @TableField("ROUND")
    private Integer round;

    /** @see SouOrderPriceCompare */
    @ApiModelProperty("缓存的比价信息")
    @TableField("JSON_VALUE")
    private String jsonValue;

}
