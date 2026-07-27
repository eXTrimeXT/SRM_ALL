package com.midea.cloud.srm.model.pj.changchengapi.bpm;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 根据类别启动流程请求参数
 * @author huangbf3
 */
@Data
@Accessors(chain = true)
@ApiModel("根据类别启动流程请求参数")
public class BpmStartProcessByCategotyParam {

    @ApiModelProperty("办理动作和意见留言 是")
    private BpmCommentInfo commentInfo;

    @ApiModelProperty("流程创建人 是")
    private String createUser;

    @ApiModelProperty("流程标题 是")
    private String processTitle;

    @ApiModelProperty("应用ID 是")
    private String appId;

    @ApiModelProperty("流程分组ID 是")
    private String processGroupId;

    @ApiModelProperty("创建组织ID 是")
    private String createOrgId;

    @ApiModelProperty("流程变量，JSON格式的字符串，变量名称和值一一对应，\n" +
            "     如： {\" formUrl \": \"http://xxx.jsp\",\t\" formUrlMobile \": \"\",\t\" param1 \": \"x\",\"param2\":\"xx\"}\n" +
            "     是")
    private JSONObject processVars;
    
    @ApiModelProperty("下一个环节的任务办理人，多个办理人用空格分隔；如果为空，则由流程引擎根据流程中的配置自动解析办理人。\n" +
            "     该参数只支持下一步是单节点，不支持于下一步会同时有多个节点生成任务的场景。\t否")
    private String targetUser;
    
    @ApiModelProperty("是否自动完成第一个节点任务，默认为true,这里传false")
    private Boolean autoCompleteTask;
}
