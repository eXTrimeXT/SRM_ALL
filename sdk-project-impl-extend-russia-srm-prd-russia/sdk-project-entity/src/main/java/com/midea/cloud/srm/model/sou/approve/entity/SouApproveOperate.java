package com.midea.cloud.srm.model.sou.approve.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author panmq2
 * @description scc_npm_sou_approve_operate
 * @date 2023-10-23
 */
@Data
@ApiModel("scc_npm_sou_approve_operate")
@TableName(value = "scc_npm_sou_approve_operate")
public class SouApproveOperate extends BaseEntity {
    @TableId
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long approveOperateId;
    /**
     * 关联审批人ID
     */
    @ApiModelProperty("关联审批人ID")
    private Long approveUserId;
    /**
     * 审批操作
     */
    @ApiModelProperty("审批操作")
    private String operate;
    /**
     * 审批意见说明
     */
    @ApiModelProperty("审批意见说明")
    private String descrption;
    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sortIndex;
}

