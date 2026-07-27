package com.midea.cloud.srm.model.pj.changchengapi.bpm;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangbf3
 */
@Data
public class BpmCallback {
    @ApiModelProperty("状态")
    @JSONField(name = "ControlState")
    private String controlState;

    @ApiModelProperty("标题")
    private String processInstTitle;

    @ApiModelProperty("操作时间")
    private String actionTime;

    @ApiModelProperty("数据")
    private JSONObject data;

    @ApiModelProperty("链接")
    private String trackingUrl;

    @ApiModelProperty("链接")
    private String recordUrl;

    @ApiModelProperty("流程ID")
    private String processInstId;

    @ApiModelProperty("视图链接")
    private String viewUrl;

    @ApiModelProperty("流程ID")
    private String processDefId;

    @ApiModelProperty("状态描述")
    @JSONField(name = "ControlStateMessage")
    private String controlStateMessage;

    @ApiModelProperty("操作描述")
    private String actionComment;

    @ApiModelProperty("操作名称")
    private String actionName;

    @ApiModelProperty("业务单据ID")
    private Long bussinessId;

    @ApiModelProperty("流程模板")
    private String bussinessType;
}
