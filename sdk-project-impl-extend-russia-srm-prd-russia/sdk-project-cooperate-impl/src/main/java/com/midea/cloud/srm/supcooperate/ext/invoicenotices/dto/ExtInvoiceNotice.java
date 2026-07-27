package com.midea.cloud.srm.supcooperate.ext.invoicenotices.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.InvoiceNotice;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author zenghx2
 */
@Accessors(chain = true)
@Data
public class ExtInvoiceNotice extends InvoiceNotice {

    private String extStatus;
    private String extVendorContacts;
    private String extVendorPhone;
    private String extInvoiceType;
    private String extInvoiceCompany;
    private String extInvoiceOpeningName;
    private String extInvoiceOpeningAccount;
    private String extInvoiceTaxpayerNum;
    private String extInvoiceAddress;
    private String extInvoicePhone;
    private String extInvoiceReceiver;
    private String extInvoiceReceiveAddr;

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

    /**
     * 邮箱
     */
    @TableField("COLLECT_MAIL")
    private String collectMail;
}
