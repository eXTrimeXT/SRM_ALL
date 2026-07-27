package com.midea.cloud.srm.sou.meiql.inspect.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 考察单位
 * </p>
 *
 * @author zenghx2
 * @since 2023-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_inspect_vendor")
public class InspectVendor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
      @TableId(value = "INSPECT_VENDOR_ID", type = IdType.AUTO)
    private Long inspectVendorId;

    /**
     * 考察申请id
     */
    @TableField("INSPECT_ID")
    private Long inspectId;

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
     * 地点
     */
    @TableField("INSPECT_ADDRESS")
    private String inspectAddress;

    /**
     * 考察原因
     */
    @TableField("INSPECT_CAUSE")
    private String inspectCause;

    /**
     * 考察时间
     */
    @TableField("INSPECT_TIME")
    private Date inspectTime;

    /**
     * 考察内容
     */
    @TableField("INSPECT_CONTENT")
    private String inspectContent;

    /**
     * 现场管理评价
     */
    @TableField("MANAGE_EVALUATE")
    private String manageEvaluate;

    /**
     * 生产设备评价
     */
    @TableField("DEVICE_EVALUATE")
    private String deviceEvaluate;

    /**
     * 人员状况
     */
    @TableField("STAFF_EVALUATE")
    private String staffEvaluate;

    /**
     * 绩效情况
     */
    @TableField("PERFORMANCE_EVALUATE")
    private String performanceEvaluate;

    /**
     * 综合评估意见
     */
    @TableField("COMPREHENSIVE_EVALUATION")
    private String comprehensiveEvaluation;

    /**
     * 其他方面
     */
    @TableField("COMMENT")
    private String comment;

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
