package com.midea.cloud.srm.supcooperate.ext.order.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 自动转订单配置
 * </p>
 *
 * @author zenghx2
 * @since 2023-11-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_order_config")
public class OrderConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,配置id
     */
      @TableId("CONFIG_ID")
    private Long configId;

    /**
     * 规则编码
     */
    @TableField("CONFIG_NUM")
    private String configNum;

    /**
     * 规则名称
     */
    @TableField("CONFIG_NAME")
    private String configName;

    /**
     * 创建人公司ID
     */
    @TableField("CREATOR_ORG_ID")
    private Long creatorOrgId;

    /**
     * 创建人公司编码
     */
    @TableField("CREATOR_ORG_CODE")
    private String creatorOrgCode;

    /**
     * 创建人公司名称
     */
    @TableField("CREATOR_ORG_NAME")
    private String creatorOrgName;

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
