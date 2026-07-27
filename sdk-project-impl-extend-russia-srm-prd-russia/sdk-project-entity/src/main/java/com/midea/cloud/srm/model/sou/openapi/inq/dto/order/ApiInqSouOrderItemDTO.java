package com.midea.cloud.srm.model.sou.openapi.inq.dto.order;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItemPayment;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderItemEditDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiInqSouOrderItemDTO extends ApiSouOrderItemEditDTO {

    /** @see InqSouOrderItem#getFormulaAttrValues */
    @ApiModelProperty("供应商填写的公式报价信息")
    protected String formulaAttrValues;

    @ApiModelProperty("附件")
    private List<SceneFile> orderItemFiles;

    @ApiModelProperty("账期")
    private List<InqSouOrderItemPayment> paymentList;

    // -------------------------- 长城询比价报价明细额外字段 -----------------------------
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

    /** @see ExtInqSouOrderItem#getExtWarrantyPeriod */
    @ApiModelProperty("保修期(保质期)")
    private Integer extWarrantyPeriod;

}
