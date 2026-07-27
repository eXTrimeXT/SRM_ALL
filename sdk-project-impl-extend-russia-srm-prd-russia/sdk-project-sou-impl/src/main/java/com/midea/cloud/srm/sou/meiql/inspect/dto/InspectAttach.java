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
 * 考察附件
 * </p>
 *
 * @author zenghx2
 * @since 2023-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_inspect_attach")
public class InspectAttach implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
      @TableId(value = "INSPECT_ATTACH_ID", type = IdType.AUTO)
    private Long inspectAttachId;

    /**
     * 考察申请id
     */
    @TableField("INSPECT_ID")
    private Long inspectId;

    /**
     * 附件ID
     */
    @TableField("ATTACH_ID")
    private Long attachId;

    /**
     * 附件名称
     */
    @TableField("ATTACH_NAME")
    private String attachName;

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
