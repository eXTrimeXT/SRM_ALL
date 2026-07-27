package com.midea.cloud.srm.supcooperate.ext.order.dto;

import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zenghx2
 */
@Data
public class ExtOrderDetail extends OrderDetail {

    /**
     * 执行中：orderStatus=ACCEPT, extStatus=ONGOING
     * 已完成：orderStatus=ACCEPT, extStatus=FINISHED
     */
    private String extDetailStatus;
    private String extBuyType;
    private Long extAttachId;
    private String extAttachName;
    private String extReturnRequirement;
    private String extBrand;
    private String extUseDepartmentCode;
    private String extUseDepartmentName;
    private String extUserName;
    private String extUserCode;
    private BigDecimal extCheckQty;
    private String extAreaCode;
    private Integer extDeliveryCycle;
    private String extInvoiceType;
    private Integer extWarrantyPeriod;
    private String extAgreementType;
    private Long extAgreementInfoId;
    /**
     * 物料描述
     */
    private String extMaterialItemDesc;

    /**
     * 规格型号
     */
    private String extMaterialItemType;
    /**
     * 供应商
     */
    private String extOrderRemark;
}
