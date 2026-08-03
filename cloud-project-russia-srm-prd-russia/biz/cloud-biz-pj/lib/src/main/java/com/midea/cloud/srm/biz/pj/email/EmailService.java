package com.midea.cloud.srm.biz.pj.email;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;

/**
 * @author huangbf3
 */
public interface EmailService {
    /**
     * 备注
     * @param jsonData
     * @return
     */
    @ApiOperation(value = "邮件接口-发送普通邮件")
    JSONObject sendEmail(JSONObject jsonData);
}
