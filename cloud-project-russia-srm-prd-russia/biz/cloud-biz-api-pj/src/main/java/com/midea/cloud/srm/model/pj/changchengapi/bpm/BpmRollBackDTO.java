package com.midea.cloud.srm.model.pj.changchengapi.bpm;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @author huangbf3
 */
@Data
@ApiOperation("BPM撤回参数对象")
public class BpmRollBackDTO {

    @ApiModelProperty("撤回说明")
    @JSONField(name = "COMMENTMSG")
    private String commentmsg;

    @ApiModelProperty("操作人账号")
    @JSONField(name = "CREATEUSER")
    private String createuser;

    @ApiModelProperty("BPM流程实例ID")
    @JSONField(name = "PROCESSINSTID")
    private String processinstid;

    @ApiModelProperty("审批流程模板")
    private String bussinessType;

    @ApiModelProperty("业务单据ID")
    private Long dataId;
}
