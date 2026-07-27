package com.midea.cloud.srm.sou.meiql.borrow.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 借阅申请
 * </p>
 *
 * @author zenghx2
 * @since 2023-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_borrow")
public class Borrow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "BORROW_ID", type = IdType.AUTO)
    private Long borrowId;

    /**
     * 借阅申请单号
     */
    @TableField("BORROW_NUM")
    private String borrowNum;

    /**
     * 状态
     */
    @TableField("STATUS")
    private String status;

    /**
     * 招标项目ID
     */
    @TableField("BIDING_ID")
    private Long bidingId;

    /**
     * 招标项目编码
     */
    @TableField("BIDING_NUM")
    private String bidingNum;

    /**
     * 招标项目名称
     */
    @TableField("BIDING_NAME")
    private String bidingName;

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
     * 申请人工号
     */
    @TableField("APPLY_USER_CODE")
    private String applyUserCode;

    /**
     * 申请人名称
     */
    @TableField("APPLY_USER_NAME")
    private String applyUserName;

    /**
     * 申请人联系方式
     */
    @TableField("APPLY_CONTACTS")
    private String applyContacts;

    /**
     * 借阅资料类型
     */
    @TableField("BORROW_TYPE")
    private String borrowType;

    /**
     * 申请使用原因
     */
    @TableField("BORROW_CAUSE")
    private String borrowCause;

    /**
     * 使用方式
     */
    @TableField("USE_TYPE")
    private String useType;

    /**
     * 是否涉及报价，Y/N
     */
    @TableField("PRICE_FLAG")
    private String priceFlag;

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

    /**
     * 单位总经理工号
     */
    private String managerCode;

    /**
     * 单位总经理名称
     */
    private String managerName;


    @TableField("START_BPM_USERNAME")
    @ApiModelProperty("bpm发起人账号")
    private String startBpmUsername;

    @TableField("START_BPM_NICKNAME")
    @ApiModelProperty("bpm发起人名称")
    private String startBpmNickname;
}
