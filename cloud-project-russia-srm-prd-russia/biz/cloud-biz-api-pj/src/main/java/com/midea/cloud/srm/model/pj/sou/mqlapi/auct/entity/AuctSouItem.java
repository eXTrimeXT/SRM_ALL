package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.extapi.sou.auct.entity.ExtAuctSouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 寻源MQL - 竞价 - 物料需求
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/08
 */
@Data
@TableName("scc_sou_auct_item")
@EqualsAndHashCode(callSuper = true)
public class AuctSouItem extends ExtAuctSouItem {

    /** @see SouItem#getSouItemId */
    @TableId("SOU_ITEM_ID")
    @ApiModelProperty("物料需求行ID")
    private Long souItemId;

    @TableField("PROJECT_ID")
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    /** 是否含税根据 {@link SouProject#getIsPriceNotax} 来决定 */
    @TableField("STANDARD_START_PRICE")
    @ApiModelProperty("起拍价(本币)")
    private BigDecimal standardStartPrice;

    @TableField("ORDER_START_PRICE")
    @ApiModelProperty("起拍价(报价币种)")
    private BigDecimal orderStartPrice;

    @TableField("STANDARD_NO_BID_PRICE")
    @ApiModelProperty("流拍价(本币)")
    private BigDecimal standardNoBidPrice;

    @TableField("ORDER_NO_BID_PRICE")
    @ApiModelProperty("流拍价(报价币种)")
    private BigDecimal orderNoBidPrice;

    @TableField("PRICE_TYPE")
    @ApiModelProperty("价格类型(字典值: DMAND_LINE_TYPE)")
    private String priceType;

    @TableField("TRADE_TERM")
    @ApiModelProperty("贸易条款[字典值: trade_clause]")
    private String tradeTerm;

    @TableField("WARRANTY_PERIOD")
    @ApiModelProperty("保修期(月)")
    private Integer warrantyPeriod;

}
