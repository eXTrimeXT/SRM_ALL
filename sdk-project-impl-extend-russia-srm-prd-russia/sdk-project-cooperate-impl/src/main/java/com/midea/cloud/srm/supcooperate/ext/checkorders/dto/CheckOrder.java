package com.midea.cloud.srm.supcooperate.ext.checkorders.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 验收单
 * </p>
 *
 * @author zenghx2
 * @since 2023-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_check_order")
public class CheckOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,验收单ID
     */
      @TableId("CHECK_ORDER_ID")
    private Long checkOrderId;

    /**
     * 验收单号
     */
    @TableField("CHECK_ORDER_NUMBER")
    private String checkOrderNumber;

    /**
     * 验收单状态
     */
    @TableField("CHECK_ORDER_STATUS")
    private String checkOrderStatus;

    private Long orgBuId;
    private String orgBuCode;
    private String orgBuName;

    /**
     * 业务实体ID
     */
    @TableField("ORG_ID")
    private Long orgId;

    /**
     * 业务实体编码
     */
    @TableField("ORG_CODE")
    private String orgCode;

    /**
     * 业务实体名称
     */
    @TableField("ORG_NAME")
    private String orgName;

    /**
     * 供应商ID
     */
    @TableField("VENDOR_ID")
    private Long vendorId;

    /**
     * 供应商编码
     */
    @TableField("VENDOR_CODE")
    private String vendorCode;

    /**
     * 供应商名称
     */
    @TableField("VENDOR_NAME")
    private String vendorName;

    /**
     * 申请部门id
     */
    @TableField("DEPARTMENT_ID")
    private String departmentId;

    /**
     * 申请部门编码
     */
    @TableField("DEPARTMENT_CODE")
    private String departmentCode;

    /**
     * 申请部门名称
     */
    @TableField("DEPARTMENT_NAME")
    private String departmentName;

    /**
     * 申请人名称
     */
    @TableField("APPLICANT_NAME")
    private String applicantName;

    /**
     * 申请人工号
     */
    @TableField("APPLICANT_CODE")
    private String applicantCode;

    /**
     * 申请日期
     */
    @TableField("APPLY_DATE")
    private Date applyDate;

    /**
     * 未税总金额
     */
    @TableField("NO_TAX_TOTAL_AMOUNT")
    private BigDecimal noTaxTotalAmount;

    /**
     * 含税总金额
     */
    @TableField("TAX_TOTAL_AMOUNT")
    private BigDecimal taxTotalAmount;

    /**
     * 币种ID
     */
    @TableField("CURRENCY_ID")
    private Long currencyId;

    /**
     * 币种名称
     */
    @TableField("CURRENCY_NAME")
    private String currencyName;

    private String checkAdvice;

    /**
     * 创建人ID
     */
    @TableField("CREATED_ID")
    private Long createdId;

    /**
     * 创建人
     */
    @TableField("CREATED_BY")
    private String createdBy;

    /**
     * 创建时间
     */
    @TableField("CREATION_DATE")
    private Date creationDate;

    /**
     * 创建人IP
     */
    @TableField("CREATED_BY_IP")
    private String createdByIp;

    /**
     * 创建人姓名
     */
    @TableField("CREATED_FULL_NAME")
    private String createdFullName;

    /**
     * 最后更新人ID
     */
    @TableField("LAST_UPDATED_ID")
    private Long lastUpdatedId;

    /**
     * 更新人
     */
    @TableField("LAST_UPDATED_BY")
    private String lastUpdatedBy;

    /**
     * 最后更新时间
     */
    @TableField("LAST_UPDATE_DATE")
    private Date lastUpdateDate;

    /**
     * 最后更新人IP
     */
    @TableField("LAST_UPDATED_BY_IP")
    private String lastUpdatedByIp;

    /**
     * 最后更新人姓名
     */
    @TableField("LAST_UPDATED_FULL_NAME")
    private String lastUpdatedFullName;

    /**
     * 租户ID
     */
    @TableField("TENANT_ID")
    private String tenantId;

    /**
     * 版本号
     */
    @TableField("VERSION")
    private Long version;


}
