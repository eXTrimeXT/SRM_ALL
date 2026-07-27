package com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 共享库存
 * </p>
 *
 * @author zenghx2
 * @since 2023-10-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_pr_share_stock")
public class PrShareStock implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId("STOCK_ID")
    private Long stockId;

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
    @TableField("MODEL")
    private String model;

    /**
     * 计量单位
     */
    @TableField("UNIT")
    private String unit;

    /**
     * 数量
     */
    @TableField("QTY")
    private BigDecimal qty;

    /**
     * 参考单价
     */
    @TableField("PRICE")
    private BigDecimal price;

    /**
     * 公司名称
     */
    @TableField("COMPANY")
    private String company;

    /**
     * 业务实体
     */
    @TableField("ORG")
    private String org;

    /**
     * 区域
     */
    @TableField("AREA")
    private String area;

    /**
     * 创建单位
     */
    @TableField("DEPARTMENT")
    private String department;

    /**
     * 库位
     */
    @TableField("STORE_NAME")
    private String storeName;

    /**
     * 库房地址
     */
    @TableField("STORE_ADDRESS")
    private String storeAddress;

    /**
     * 库房联系人
     */
    @TableField("STORE_CONTACTS")
    private String storeContacts;

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
