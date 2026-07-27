package com.midea.cloud.srm.sou.meiql.inspect.dto;

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
 * 考察申请
 * </p>
 *
 * @author zenghx2
 * @since 2023-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("scc_npm_inspect")
public class Inspect implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
      @TableId(value = "INSPECT_ID", type = IdType.AUTO)
    private Long inspectId;

    /**
     * 考察申请单号
     */
    @TableField("INSPECT_NUM")
    private String inspectNum;

    /**
     * 考察报告单号
     */
    @TableField("REPORT_NUM")
    private String reportNum;

    /**
     * 状态
     */
    @TableField("INSPECT_STATUS")
    private String inspectStatus;

    /**
     * 取消原因
     */
    @TableField("REASON_DESC")
    private String reasonDesc;

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

    /**
     * 招标项目负责人
     */
    @TableField("BIDING_head")
    private String bidingHead;

    /**
     * 招标部是否参加，Y/N
     */
    @TableField("BIDING_DEPARTMENT_FLAG")
    private String bidingDepartmentFlag;

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
     * 出行方式
     */
    @TableField("COME_TYPE")
    private String comeType;

    /**
     * 备注（拟参加人员）
     */
    @TableField("COMMENT")
    private String comment;

    /**
     * 综合评估意见【挪到考察单位表中】
     */
    @TableField("COMPREHENSIVE_EVALUATION")
    private String comprehensiveEvaluation;

    /**
     * 组长评估意见
     */
    @TableField("LEADER_EVALUATION")
    private String leaderEvaluation;

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


    @TableField("START_BPM_USERNAME")
    @ApiModelProperty("bpm发起人账号")
    private String startBpmUsername;

    @TableField("START_BPM_NICKNAME")
    @ApiModelProperty("bpm发起人名称")
    private String startBpmNickname;

    @TableField("START_BPM_REPORT_USERNAME")
    @ApiModelProperty("报告 bpm发起人账号")
    private String startBpmReportUsername;

    @TableField("START_BPM_REPORT_NICKNAME")
    @ApiModelProperty("报告 bpm发起人名称")
    private String startBpmReportNickname;
}
