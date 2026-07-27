package com.midea.cloud.srm.model.pj.changchengapi.bpm;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 退回任务前端请求参数
 * @author huangbf3
 */
@Data
@Accessors(chain = true)
@ApiModel("退回任务前端请求参数")
public class BpmRollBackTaskVo {
    @ApiModelProperty("业务单据ID  是")
    private Long businessId;

    @ApiModelProperty("审批流模板  是")
    private String businessType;

    @ApiModelProperty("当前任务在BPM中的任务实例ID  是")
    private String taskInstId;


    @ApiModelProperty("办理动作和意见留言  是")
    private BpmCommentInfo commentInfo;

    @ApiModelProperty("\n" +
            "     * 流程变量，JSON格式的字符串，变量名称和值一一对应，\n" +
            "     * 如： {\" formUrl \": \"http://xxx.jsp\",\t\" formUrlMobile \": \"\",\t\" param1 \": \"x\",\"param2\":\"xx\"}\n" +
            "     * 是")
    private JSONObject processVars;

    @ApiModelProperty("下一个环节的任务办理人，多个办理人用空格分隔；\n" +
            "     * 如果为空，则由流程引擎根据流程中的配置自动解析办理人。\n" +
            "     * 该参数只支持下一步是单节点，不支持于下一步会同时有多个节点生成任务的场景。\n" +
            "     * 是")
    private String targetUser;
}
