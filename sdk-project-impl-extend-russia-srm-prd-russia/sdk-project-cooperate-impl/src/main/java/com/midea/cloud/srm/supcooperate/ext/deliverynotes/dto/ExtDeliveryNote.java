package com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.supcooperate.meiql.deliverynote.dto.DeliveryNoteDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zenghx2
 */
@Data
public class ExtDeliveryNote extends DeliveryNoteDTO {

    /**
     * 已完成： deliveryNoteStatus=DELIVERED,extStatus=FINISHED
     */
    @TableField("EXT_STATUS")
    private String extStatus;
    @TableField("EXT_VENDOR_CONTACTS")
    private String extVendorContacts;
    @TableField("EXT_VENDOR_PHONE")
    private String extVendorPhone;
    @TableField("EXT_PURCHASER_NO")
    private String extPurchaserNo;
    @TableField("EXT_PURCHASER_ID")
    private Long extPurchaserId;
    @TableField("EXT_PURCHASER_NAME")
    private String extPurchaserName;
    @TableField("EXT_PURCHASER_ORG_NAME")
    private String extPurchaserOrgName;
    @TableField("EXT_PURCHASER_PHONE")
    private String extPurchaserPhone;
    @TableField("EXT_PURCHASER_EMAIL")
    private String extPurchaserEmail;
    @TableField("EXT_EXPRESS_TYPE")
    private String extExpressType;
    @TableField("EXT_EXPRESS_NO")
    private String extExpressNo;
    @TableField("EXT_EXPRESS_MAN")
    private String extExpressMan;
    @TableField("EXT_EXPRESS_PHONE")
    private String extExpressPhone;

    /**
     * 申请部门id
     */
    @TableField("EXT_DEPARTMENT_ID")
    private String extDepartmentId;
    /**
     * 申请部门编码
     */
    @TableField("EXT_DEPARTMENT_CODE")
    private String extDepartmentCode;

    /**
     * 申请部门名称
     */
    @TableField("EXT_DEPARTMENT_NAME")
    private String extDepartmentName;

    /**
     * 申请人名称
     */
    @TableField("EXT_APPLICANT_NAME")
    private String extApplicantName;

    /**
     * 申请人工号
     */
    @TableField("EXT_APPLICANT_CODE")
    private String extApplicantCode;

    /**
     * 申请日期
     */
    @TableField("EXT_APPLY_DATE")
    private Date extApplyDate;

    @TableField("EXT_UN_TAX_AMOUNT")
    private BigDecimal extUnTaxAmount;
    @TableField("EXT_IN_TAX_AMOUNT")
    private BigDecimal extInTaxAmount;
    @TableField("EXT_CURRENCY_ID")
    private Long extCurrencyId;
    @TableField("EXT_CURRENCY_CODE")
    private String extCurrencyCode;
    @TableField("EXT_CURRENCY_NAME")
    private String extCurrencyName;
    @TableField("EXT_RECEIVE_CONTACT")
    private String extReceiveContact;
    @TableField("EXT_RECEIVE_TELEPHONE")
    private String extReceiveTelephone;

    /**
     * 一个拆分后的京东订单生成一个送货单，记录生成此送货单的京东订单id
     */
    @TableField("EXT_JD_ORDER_ID")
    private Long extJdOrderId;

}
