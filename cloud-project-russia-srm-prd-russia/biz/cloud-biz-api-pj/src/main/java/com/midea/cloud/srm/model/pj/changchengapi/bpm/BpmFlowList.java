package com.midea.cloud.srm.model.pj.changchengapi.bpm;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @author huangbf3
 */
@Data
@ApiOperation("BPM审批过程")
public class BpmFlowList {

    @ApiModelProperty("备注")
    private String msg;

    @ApiModelProperty("部门")
    private String deptName;

    @ApiModelProperty("")
    private String originalMsg;

    @ApiModelProperty("流程ID")
    private String processInstId;

    @ApiModelProperty("节点")
    private String activityName;

    @ApiModelProperty("创建人账号")
    private String createUser;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("审批节点ID")
    private String taskInstId;

    @ApiModelProperty("审批节点序号")
    private Integer activityNo;

    @ApiModelProperty("创建日期")
    private String createDate;

    @ApiModelProperty("阅读时间")
    private String readTime;

    @ApiModelProperty("节点操作类型")
    private String actionName;

    @ApiModelProperty("任务所属的节点模型ID")
    private String activityDefId;

    @ApiModelProperty("串并签标识，-1:单人节点，0:串签，")
    private Integer parallel;
}
