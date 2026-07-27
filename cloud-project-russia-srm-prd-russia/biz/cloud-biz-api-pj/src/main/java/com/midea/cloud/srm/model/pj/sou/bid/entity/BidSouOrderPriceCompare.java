package com.midea.cloud.srm.model.pj.sou.bid.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.extapi.sou.bid.entity.ExtBidSouOrderPriceCompare;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 招投标.评选比价信息(缓存)
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scc_sou_bid_price_compare")
@ApiModel("招投标.评选比价信息(缓存)")
public class BidSouOrderPriceCompare extends ExtBidSouOrderPriceCompare {

    @TableId("ORDER_PRICE_COMPARE_ID")
    @ApiModelProperty("主键ID")
    private Long orderPriceCompareId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouItem#getSouItemId */
    @TableField("SOU_ITEM_ID")
    @ApiModelProperty("需求物料ID")
    private Long souItemId;

    @TableField("ROUND")
    @ApiModelProperty("轮次")
    private Integer round;

    @TableField("JSON_VALUE")
    @ApiModelProperty("缓存的比价信息")
    private String jsonValue;

}
