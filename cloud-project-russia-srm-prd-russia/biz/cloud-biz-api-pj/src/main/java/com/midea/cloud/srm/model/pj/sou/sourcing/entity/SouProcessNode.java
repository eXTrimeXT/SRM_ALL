package com.midea.cloud.srm.model.pj.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProcessConfig;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouProcessNodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源.核心表 - 流程节点记录
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_process_node")
@ApiModel("寻源流程节点")
public class SouProcessNode extends BaseEntity<SouProcessNode> {

    @ApiModelProperty("ID")
    @TableId("PROCESS_NODE_ID")
    private Long processNodeId;

    /** @see SouProcessConfig#getProcessConfigId */
    @ApiModelProperty("流程配置ID")
    @TableField("PROCESS_CONFIG_ID")
    private Long processConfigId;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单据ID")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouProcessNodeEnum */
    @ApiModelProperty("流程节点")
    @TableField("PROCESS_NODE")
    private String processNode;

    @ApiModelProperty("流程节点执行状态(Y/N)")
    @TableField("NODE_STATUS")
    private Enable nodeStatus;

}
