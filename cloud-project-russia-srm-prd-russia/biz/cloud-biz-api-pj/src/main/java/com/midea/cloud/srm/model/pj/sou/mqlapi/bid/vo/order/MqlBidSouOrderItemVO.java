package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.order;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouItem;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order.MqlSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 招投标MQL - 报价行信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlBidSouOrderItemVO extends MqlSouOrderItemVO {

    @ApiModelProperty("简易询价拓展数据")
    private BidSouOrderItem bidSouOrderItem;

    @ApiModelProperty("物料需求附件")
    private List<SceneFile> itemFileList;

    @ApiModelProperty("报价附件")
    private List<SceneFile> orderItemFileList;

    @ApiModelProperty("账期信息")
    private List<BidSouOrderItemPayment> bidPaymentList;

    // ---------------------------------------------------------- 下面是招投标物料需求拓展数据(冗余信息) -------------------------------------------------------
    /** @see BidSouItem#getOrderType */
    @ApiModelProperty("报价方式(冗余字段)")
    private SouOrderTypeEnum orderType;

    /** @see BidSouItem#getMaterialFormulaRelateId */
    @ApiModelProperty("物料价格公式关联ID")
    private Long materialFormulaRelateId;

    /** @see BidSouItem#getFormulaId */
    @ApiModelProperty("公式id")
    private Long formulaId;

    /** @see BidSouItem#getFormulaName */
    @ApiModelProperty("公式名称")
    private String formulaName;

    /** @see BidSouItem#getFormulaValue */
    @ApiModelProperty("公式值")
    private String formulaValue;

    /** @see BidSouItem#getTargetPrice */
    @ApiModelProperty("拦标价")
    private BigDecimal targetPrice;

    /** @see BidSouItem#getDeliveryPlace */
    @ApiModelProperty("交货地点")
    private String deliveryPlace;

    /** @see BidSouItem#getPriceType */
    @ApiModelProperty("价格类型[字典值: PRICE_TYPE]")
    private String priceType;

    /** @see BidSouItem#getPurchaseType */
    @ApiModelProperty("采购类型")
    private String purchaseType;

    /** @see BidSouItem#getTradeTerm */
    @ApiModelProperty("贸易条款[字典值: trade_clause]")
    private String tradeTerm;

    /** @see BidSouItem#getTransportType */
    @ApiModelProperty("运输方式[字典值: TRANSF_TYPE]")
    private String transportType;

    /** @see BidSouItem#getWarrantyPeriod */
    @ApiModelProperty("保修期(月)")
    private Integer warrantyPeriod;

}
