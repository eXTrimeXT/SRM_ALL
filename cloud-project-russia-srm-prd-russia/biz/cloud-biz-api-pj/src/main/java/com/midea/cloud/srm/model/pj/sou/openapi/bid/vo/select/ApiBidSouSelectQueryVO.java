package com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.select;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouItem;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.init.ApiBidSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 招投标openAPI - 评选列表查询结果
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouSelectQueryVO extends ApiSouSelectQueryVO {

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

    /** @see BidSouOrderItem#getMqo */
    @ApiModelProperty("最小订单量")
    private String mqo;

    /** @see BidSouOrderItem#getLeadTime */
    @ApiModelProperty("供货周期")
    private String leadTime;

    /** @see BidSouOrderItem#getDeliverDate */
    @ApiModelProperty("承诺交货期")
    private Date deliverDate;

    /** @see BidSouOrderItem#getFormulaResult */
    @ApiModelProperty("供应商填写的公式报价json")
    private String formulaResult;

    /** @see ApiBidSouItemVO#getItemFiles */
    @ApiModelProperty("项目需求信息-标准附件")
    private List<SceneFile> itemFiles;

    @ApiModelProperty("账期")
    private List<BidSouOrderItemPayment> paymentList;

}
