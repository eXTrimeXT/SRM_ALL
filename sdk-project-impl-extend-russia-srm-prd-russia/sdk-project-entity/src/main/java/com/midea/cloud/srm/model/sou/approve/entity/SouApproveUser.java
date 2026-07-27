package com.midea.cloud.srm.model.sou.approve.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author panmq2
 * @description scc_npm_sou_approve_user
 * @date 2023-10-23
 */
@Data
@ApiModel("scc_npm_sou_approve_user")
@TableName(value = "scc_npm_sou_approve_user")
public class SouApproveUser extends BaseEntity {
    @TableId
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long approveUserId;
    /**
     * 关联业务单据ID
     */
    @ApiModelProperty("关联业务单据ID")
    private Long businessId;
    /**
     * 审批人用户表ID
     */
    @ApiModelProperty("审批人用户表ID")
    private Long userId;
    /**
     * 审批人用户账号
     */
    @ApiModelProperty("审批人用户账号")
    private String userName;
    /**
     * 审批人用户名字
     */
    @ApiModelProperty("审批人用户名字")
    private String fullName;

    @ApiModelProperty("审批状态")
    private String approveStatus;
    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sortIndex;
}

