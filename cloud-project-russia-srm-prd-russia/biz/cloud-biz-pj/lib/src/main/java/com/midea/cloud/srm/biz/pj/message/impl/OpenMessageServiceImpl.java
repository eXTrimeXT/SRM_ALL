package com.midea.cloud.srm.biz.pj.message.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.srm.biz.pj.message.MessageService;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author huangbf3
 */
@Slf4j
@Service
@Primary
public class OpenMessageServiceImpl implements MessageService {

    /**
     *  短信下发接口url
     */
    @Value("${gwm.url.sms-notice-url}")
    private String smsSubmitUrl;

    @Value("${gwm.prdAppkey}")
    private String appKey;

    @Value("${gwm.prdSecret}")
    private String secret;

    /**
     * 短信通知-发送短信 ,入参为内容和电话列表,多个手机号码用英文逗号分隔
     * **/
    @Override
    public JSONObject message(String content, String phones) {
        Assert.isTrue(phones!=null,"接收手机号码不能为空");
        Assert.isTrue(content!=null,"短信内容不能为空");
        JSONObject smsObject = new JSONObject();
//        必填 接收手机号码，多个手机号码用英文逗号分隔
        smsObject.put("phones",phones);
//        必填 短信内容，最多1000个汉字，内容中不要出现【】[]这两种方括号，该字符为签名专用
        smsObject.put("content",content);
        OpenClient openClient = new OpenClient(appKey,secret);
        String res = openClient.sendHttpPost(smsSubmitUrl, smsObject.toString(), "application/json");
        //结果 转为 大汉三通格式 防止调用方 对结果处理 异常
        return JSONObject.parseObject(res);
    }



}
