package com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItem;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItemPayment;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 竞价openAPI - 编辑物料需求信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/12/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiCompSouItemDTO extends ApiSouItemDTO {

    /** @see CompSouItem#getOrderCurrency */
    @ApiModelProperty("报价币种(由采购商指定)")
    private String orderCurrency;

    /** @see CompSouItem#getTaxKey */
    @ApiModelProperty("税率编码(由采购商指定)")
    private String taxKey;

    /** @see CompSouItem#getTaxRate */
    @ApiModelProperty("税率(由采购商指定)")
    private BigDecimal taxRate;

    /** @see CompSouItem#getStartOrderNotaxPrice */
    @ApiModelProperty("起拍价(原币未税)")
    private BigDecimal startOrderNotaxPrice;

    /** @see CompSouItem#getStartOrderTaxPrice */
    @ApiModelProperty("起拍价(原币含税)")
    private BigDecimal startOrderTaxPrice;

    /** @see CompSouItem#getRowType */
    @ApiModelProperty("行类型")
    private String rowType;

    /** @see CompSouItem#getDeliveryPlace */
    @ApiModelProperty("交货地点")
    private String deliveryPlace;

    /** @see CompSouItem#getPriceType */
    @ApiModelProperty("价格类型")
    private String priceType;

    /** @see CompSouItem#getPurchaseType */
    @ApiModelProperty("采购类型")
    private String purchaseType;

    /** @see CompSouItem#getTradeTerm */
    @ApiModelProperty("贸易条款[字典值: trade_clause]")
    private String tradeTerm;

    /** @see CompSouItem#getWarrantyPeriod */
    @ApiModelProperty("保修期(月)")
    private Integer warrantyPeriod;

    /** @see CompSouItem#getMaterialFormulaRelateId */
    @ApiModelProperty("物料价格公式关联ID")
    private Long materialFormulaRelateId;

    /** @see CompSouItem#getFormulaId */
    @ApiModelProperty("公式id")
    private Long formulaId;

    /** @see CompSouItem#getFormulaName */
    @ApiModelProperty("公式名称")
    private String formulaName;

    /** @see CompSouItem#getFormulaValue */
    @ApiModelProperty("公式值")
    private String formulaValue;

    @ApiModelProperty("项目需求信息-标准附件")
    private List<SceneFile> itemFiles;

    @ApiModelProperty("付款条款")
    private List<CompSouItemPayment> paymentList;

}
