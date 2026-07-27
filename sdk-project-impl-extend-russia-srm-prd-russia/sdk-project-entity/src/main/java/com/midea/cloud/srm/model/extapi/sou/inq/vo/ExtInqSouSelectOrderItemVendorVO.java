package com.midea.cloud.srm.model.extapi.sou.inq.vo;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItem;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.order.ApiInqSouOrderItemDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtInqSouSelectOrderItemVendorVO extends ApiSouOrderItemVO {

    /** @see SouVendor#getVendorCode */
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /** @see ExtInqSouItem#getExtMaterialModel */
    @ApiModelProperty("物料规格型号")
    private String extMaterialModel;

    /** @see ExtInqSouItem#getExtBrand */
    @ApiModelProperty("品牌")
    private String extBrand;

    /** @see ExtInqSouItem#getExtAreaId */
    @ApiModelProperty("区域ID")
    private String extAreaId;

    /** @see ExtInqSouItem#getExtAreaCode */
    @ApiModelProperty("区域编码")
    private String extAreaCode;

    /** @see ExtInqSouItem#getExtAreaName */
    @ApiModelProperty("区域名称")
    private String extAreaName;

    /** @see ExtInqSouItem#getExtSourceFromLineIds */
    @ApiModelProperty("来源单据明细ID集合(因为原表用Long类型，无法支持)")
    private String extSourceFromLineIds;

    /** @see ExtInqSouItem#getHasClose */
    @ApiModelProperty("是否关闭")
    private Enable hasClose;

    // -----------------------------------------------------------------------------------------------------------------

    /** @see ExtInqSouOrderItem#getInvoiceType */
    @ApiModelProperty("发票类型(EXT_SOU_INQ_ORDER_INVOICE_TYPE)")
    private String invoiceType;

    /** @see ExtInqSouOrderItem#getPriceTaxTotal */
    @ApiModelProperty("价税合计")
    private BigDecimal priceTaxTotal;

    /** @see ExtInqSouOrderItem#getAdvancePaymentRemark */
    @ApiModelProperty("预付款说明")
    private Enable advancePaymentRemark;

    /** @see ExtInqSouOrderItem#getSpecialPaymentRemark */
    @ApiModelProperty("特殊付款说明")
    private String specialPaymentRemark;

    /** @see ExtInqSouOrderItem#getExtLeadTime */
    @ApiModelProperty("供货周期")
    private Integer extLeadTime;

    /** @see ExtInqSouOrderItem#getInvoiceType */
    @ApiModelProperty("保修期(保质期)")
    private Integer extWarrantyPeriod;

    /** @see ExtInqSouOrderItem#getExtWinReason */
    @ApiModelProperty("中标原因")
    private String extWinReason;

    /** @see ExtInqSouOrderItem#getHasFixPrice */
    @ApiModelProperty("是否已定价")
    private Enable hasFixPrice;

    /** @see ExtInqSouOrderItem#getExtFixPriceHeadId */
    @ApiModelProperty("定价单ID")
    private Long extFixPriceHeadId;

    /** @see ExtInqSouOrderItem#getExtFixPriceNo */
    @ApiModelProperty("定价单号")
    private String extFixPriceNo;

    /** @see ExtInqSouOrderItem#getExtFixPriceLineId */
    @ApiModelProperty("定价单行ID")
    private Long extFixPriceLineId;

    /** @see ExtInqSouOrderItem#getLatestPriceTag */
    @ApiModelProperty("是否最新报价")
    private Enable latestPriceTag;

}
