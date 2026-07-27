package com.midea.cloud.srm.supcooperate.ext.order.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 自动转订单品类配置
 * </p>
 *
 * @author zenghx2
 * @since 2023-11-14
 */
@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_order_config_category")
public class OrderConfigCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,id
     */
      @TableId("CONFIG_CATEGORY_ID")
    private Long configCategoryId;

    /**
     * 配置id
     */
    @TableField("CONFIG_ID")
    private Long configId;

    /**
     * 品类id
     */
    @TableField("CATEGORY_ID")
    private Long categoryId;

    /**
     * 品类名称
     */
    @TableField("CATEGORY_NAME")
    private String categoryName;

    /**
     * 品类编码
     */
    @TableField("CATEGORY_CODE")
    private String categoryCode;

    /**
     * 公司id
     */
    @TableField("ORG_ID")
    private Long orgId;

    /**
     * 公司名称
     */
    @TableField("ORG_NAME")
    private String orgName;

    /**
     * 公司编码
     */
    @TableField("ORG_CODE")
    private String orgCode;

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
