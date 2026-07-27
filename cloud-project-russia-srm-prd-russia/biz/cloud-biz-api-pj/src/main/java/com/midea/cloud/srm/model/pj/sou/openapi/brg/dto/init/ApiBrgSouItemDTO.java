package com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.init;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouItem;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 项目式询价openAPI - 编辑物料需求信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouItemDTO extends ApiSouItemDTO {

    /** @see BrgSouItem#getSouItemId */
    @ApiModelProperty("物料需求行ID")
    private Long souItemId;

    /** @see BrgSouItem#getMaterialFormulaRelateId */
    @ApiModelProperty("物料价格公式关联ID")
    private Long materialFormulaRelateId;

    /** @see BrgSouItem#getFormulaId */
    @ApiModelProperty("公式id")
    private Long formulaId;

    /** @see BrgSouItem#getFormulaName */
    @ApiModelProperty("公式名称")
    private String formulaName;

    /** @see BrgSouItem#getFormulaValue */
    @ApiModelProperty("公式值")
    private String formulaValue;

    /** @see BrgSouItem#getTargetPrice */
    @ApiModelProperty("拦标价")
    private BigDecimal targetPrice;

    /** @see BrgSouItem#getDeliveryPlace */
    @ApiModelProperty("交货地点")
    private String deliveryPlace;

    /** @see BrgSouItem#getPriceType */
    @ApiModelProperty("价格类型")
    private String priceType;

    /** @see BrgSouItem#getPurchaseType */
    @ApiModelProperty("采购类型")
    private String purchaseType;

    /** @see BrgSouItem#getTradeTerm */
    @ApiModelProperty("贸易条款")
    private String tradeTerm;

    /** @see BrgSouItem#getTransportType */
    @ApiModelProperty("运输方式")
    private String transportType;

    /** @see BrgSouItem#getWarrantyPeriod */
    @ApiModelProperty("保修期(月)")
    private Integer warrantyPeriod;

    @ApiModelProperty("项目需求信息-标准附件")
    private List<SceneFile> itemFiles;

}
