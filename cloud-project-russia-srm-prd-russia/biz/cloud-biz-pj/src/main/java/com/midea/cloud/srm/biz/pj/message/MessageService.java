package com.midea.cloud.srm.biz.pj.message;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;

/**
 * @author huangbf3
 */
public interface MessageService {
    /**
     * 备注
     * @param content
     * @param phones
     * @return
     */
    @ApiOperation("短信通知-发送短信")
    JSONObject message(String content, String phones);
}
