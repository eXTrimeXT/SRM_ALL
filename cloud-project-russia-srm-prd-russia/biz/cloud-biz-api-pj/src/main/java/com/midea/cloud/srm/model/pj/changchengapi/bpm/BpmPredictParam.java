package com.midea.cloud.srm.model.pj.changchengapi.bpm;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 提交待办任务参数
 * @author huangbf3
 */
@Data
@Accessors(chain = true)
@ApiModel("提交待办任务参数")
public class BpmPredictParam {
    @ApiModelProperty("流程实例id")
    private String processInstId;

    @ApiModelProperty("用户工号大写")
    private String uid;

    @ApiModelProperty("流程变量，JSON格式的字符串，变量名称和值一一对应，\n" +
            "     如： {\" formUrl \": \"http://xxx.jsp\",\t\" formUrlMobile \": \"\",\t\" param1 \": \"x\",\"param2\":\"xx\"}\n" +
            "     否")
    private JSONObject processVars;
}
