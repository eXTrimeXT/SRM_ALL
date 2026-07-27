package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 近期采购数据
 * </p>
 *
 * @author generator
 * @since 2023-10-10
 */
@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class PrRecentPurchaseData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "DATA_ID")
    private Long dataId;

    /**
     * 配置id
     */
    @TableField("CONFIG_ID")
    private Long configId;

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
     * 供应商名称
     */
    @TableField("VENDOR_NAME")
    private String vendorName;

    /**
     * 供应商编码
     */
    @TableField("VENDOR_CODE")
    private String vendorCode;

    /**
     * 物料ID
     */
    @TableField("MATERIAL_ID")
    private Long materialId;

    /**
     * 物料编码
     */
    @TableField("MATERIAL_CODE")
    private String materialCode;

    /**
     * 物料名称
     */
    @TableField("MATERIAL_NAME")
    private String materialName;

    /**
     * 规格型号
     */
    @TableField("MATERIAL_MODEL")
    private String materialModel;

    /**
     * 单位
     */
    @TableField("UNIT")
    private String unit;

    /**
     * 单位编码
     */
    @TableField("UNIT_CODE")
    private String unitCode;

    /**
     * 订单数量
     */
    @TableField("ORDER_NUM")
    private BigDecimal orderNum;

    /**
     * 含税单价
     */
    @TableField("TAX_PRICE")
    private BigDecimal taxPrice;

    /**
     * 未税单价
     */
    @TableField("NO_TAX_PRICE")
    private BigDecimal noTaxPrice;

    /**
     * 税率
     */
    @TableField("TAX_RATE")
    private BigDecimal taxRate;

    /**
     * 采购金额
     */
    @TableField("ORDER_AMOUNT")
    private BigDecimal orderAmount;

    /**
     * 税额
     */
    @TableField("TAX_AMOUNT")
    private BigDecimal taxAmount;

    /**
     * 到货周期
     */
    @TableField("DELIVERY_CYCLE")
    private BigDecimal deliveryCycle;

    /**
     * 开始时间
     */
    @TableField("START_TIME")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("END_TIME")
    private Date endTime;

    /**
     * 状态
     */
    @TableField("STATUS")
    private String status;

    /**
     * 创建人部门
     */
    @TableField("CREATED_BY_department")
    private String createdByDepartment;

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

    @ApiModelProperty("预付款说明")
    private Enable advancePaymentRemark;
    @ApiModelProperty("供货周期")
    private Integer leadTime;
    @ApiModelProperty("发票类型")
    private String invoiceType;
    @ApiModelProperty("质保期")
    private Integer warrantyPeriod;

}
