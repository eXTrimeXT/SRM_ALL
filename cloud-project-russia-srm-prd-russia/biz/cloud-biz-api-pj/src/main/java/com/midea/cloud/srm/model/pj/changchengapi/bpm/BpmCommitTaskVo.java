package com.midea.cloud.srm.model.pj.changchengapi.bpm;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 前端提交待办任务参数
 * @author huangbf3
 */
@Data
@Accessors(chain = true)
@ApiModel("提交待办任务参数")
public class BpmCommitTaskVo {
    @ApiModelProperty("办理动作和意见留言  是")
    private BpmCommentInfo commentInfo;

    @ApiModelProperty("业务单据ID  是")
    private Long businessId;

    @ApiModelProperty("审批流模板  是")
    private String businessType;

    @ApiModelProperty("流程变量，JSON格式的字符串，变量名称和值一一对应，\n" +
            "     如： {\" formUrl \": \"http://xxx.jsp\",\t\" formUrlMobile \": \"\",\t\" param1 \": \"x\",\"param2\":\"xx\"}\n" +
            "     是")
    private JSONObject processVars;

    @ApiModelProperty("当前任务在BPM中的任务实例ID  是，传第一个节点的taskInstId")
    private String taskInstId;
}
