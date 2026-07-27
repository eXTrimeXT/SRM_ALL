package com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.init;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouItem;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 招投标openAPI - 编辑物料需求信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/12/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouItemDTO extends ApiSouItemDTO {

    /** @see BidSouItem#getSouItemId */
    @ApiModelProperty("物料需求行ID")
    private Long souItemId;

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
    @ApiModelProperty("价格类型")
    private String priceType;

    /** @see BidSouItem#getPurchaseType */
    @ApiModelProperty("采购类型")
    private String purchaseType;

    /** @see BidSouItem#getTradeTerm */
    @ApiModelProperty("贸易条款")
    private String tradeTerm;

    /** @see BidSouItem#getTransportType */
    @ApiModelProperty("运输方式")
    private String transportType;

    /** @see BidSouItem#getWarrantyPeriod */
    @ApiModelProperty("保修期(月)")
    private Integer warrantyPeriod;

    @ApiModelProperty("项目需求信息-标准附件")
    private List<SceneFile> itemFiles;

}
