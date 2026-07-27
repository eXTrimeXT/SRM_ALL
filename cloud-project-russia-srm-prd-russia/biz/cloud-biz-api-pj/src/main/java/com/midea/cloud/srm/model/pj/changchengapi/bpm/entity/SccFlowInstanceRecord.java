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

import java.util.Date;

/**
 * @author ex_liuxy46
 */
@Data
@TableName("scc_flow_instance_record")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "流程记录表")
public class SccFlowInstanceRecord extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId("FLOW_INSTANCE_RECORD_ID")
    private Long flowInstanceRecordId;

    @ApiModelProperty(value = "业务ID，用于业务单据关联")
    @TableField("BUSINESS_ID")
    private Long businessId;

    @ApiModelProperty(value = "模板编码")
    @TableField("TEMPLATE_CODE")
    private String templateCode;

    @ApiModelProperty(value = "流程参数")
    @TableField("BUSINESS_DATA")
    private String businessData;

    @ApiModelProperty(value = "流程实例ID")
    @TableField("INSTANCE_ID")
    private String instanceId;
    @ApiModelProperty(value = "流程提交数据")
    @TableField("FLOW_DATA")
    private String flowData;

    @ApiModelProperty(value = "流程状态")
    @TableField("FLOW_STATUS")
    private String flowStatus;

    @ApiModelProperty(value = "处理状态")
    @TableField("DEAL_STATUS")
    private String dealStatus;

    @ApiModelProperty("处理时间")
    @TableField(value = "DEAL_DATE")
    private Date dealDate;
}
