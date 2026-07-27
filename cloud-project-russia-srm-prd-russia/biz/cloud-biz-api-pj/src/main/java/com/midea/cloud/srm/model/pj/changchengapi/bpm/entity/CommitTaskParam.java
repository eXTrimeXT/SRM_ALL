package com.midea.cloud.srm.model.pj.changchengapi.bpm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author ex_liuxy46
 */
@Data
@TableName("scc_commit_task_param")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "BPM提交参数记录表")
public class CommitTaskParam extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId("COMMIT_RASK_PARAM_ID")
    private Long commitRaskParamId;

    @ApiModelProperty(value = "业务ID，用于业务单据关联")
    @TableField("BUSINESS_ID")
    private Long businessId;

    @ApiModelProperty(value = "审批流模板")
    @TableField("BUSSINESS_TYPE")
    private String bussinessType;

    @ApiModelProperty(value = "提交参数")
    @TableField("SUBMIT_PARAM")
    private String submitParam;

    @ApiModelProperty(value = "预执行返回的审批节点数据")
    @TableField("PREDICT_ACTIVITY_PARAM")
    private String predictActivityParam;

    @ApiModelProperty(value = "第一次预执行返回的审批节点数据")
    @TableField("FIRST_PREDICT_ACTIVITY_PARAM")
    private String firstPredictActivityParam;
}
