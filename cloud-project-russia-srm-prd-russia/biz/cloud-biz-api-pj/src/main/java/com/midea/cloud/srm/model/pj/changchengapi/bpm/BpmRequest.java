package com.midea.cloud.srm.model.pj.changchengapi.bpm;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangbf3
 */
@Data
public class BpmRequest {

    @ApiModelProperty("请求json")
    private JSONObject requestJson;

    @ApiModelProperty("业务系统业务ID")
    private String dataId;

    @ApiModelProperty("操作用户工号")
    private String userId;
}
