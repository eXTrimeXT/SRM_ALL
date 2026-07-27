package com.midea.cloud.srm.sou.meiql.recruit.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 招募
 * </p>
 *
 * @author zenghx2
 * @since 2023-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_recruit")
public class Recruit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
      @TableId(value = "RECRUIT_ID", type = IdType.AUTO)
    private Long recruitId;

    /**
     * 名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 标题
     */
    @TableField("TITLE")
    private String title;

    /**
     * 状态
     */
    @TableField("STATUS")
    private String status;

    /**
     * 品类id
     */
    @TableField("CATEGORY_ID")
    private Long categoryId;

    /**
     * 品类编码
     */
    @TableField("CATEGORY_CODE")
    private String categoryCode;

    /**
     * 品类名称
     */
    @TableField("CATEGORY_NAME")
    private String categoryName;

    /**
     * 发布时间
     */
    @TableField("PUBLISH_TIME")
    private Date publishTime;

    /**
     * 截止时间
     */
    @TableField("DEADLINE_TIME")
    private Date deadlineTime;

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

    private Long orgBuId;
    private String orgBuCode;
    private String orgBuName;
    private Long orgId;
    private String orgCode;
    private String orgName;

    /**
     * 部门id
     */
    @TableField("DEPARTMENT_ID")
    private Long departmentId;

    /**
     * 部门编码
     */
    @TableField("DEPARTMENT_CODE")
    private String departmentCode;

    /**
     * 部门名称
     */
    @TableField("DEPARTMENT_NAME")
    private String departmentName;

    /**
     * bpm发起人账号
     */
    @TableField("START_BPM_USERNAME")
    private String startBpmUsername;

    /**
     * bpm发起人名称
     */
    @TableField("START_BPM_NICKNAME")
    private String startBpmNickname;
}
