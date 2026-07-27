package com.midea.cloud.srm.biz.pj.email.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.srm.biz.pj.email.EmailService;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${gwm.appkey}")
    private String appKey;

    @Value("${gwm.secret}")
    private String secret;


    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;

    /**
     * 邮件发送接口url
     */
    @Value("${gwm.url.sendEmail-url}")
    private String sendEmailUrl;

    /**
     * 邮件接口-发送普通邮件
     * **/
    @Override
    public JSONObject sendEmail(JSONObject jsonData) {
        Assert.isTrue(jsonData!=null,"请求参数不能为空");
//        选填 邮件内容是否是html格式的 true:是 默认false: 否(普通文本格式)
        jsonData.put("isHtml",false);
//        选填 发送时间
        jsonData.put("sendTime","");
//        填 是否定时发送 0:正常发送 1:定时发送选
        jsonData.put("timeFlag",0);
//        选填 模板编号
        jsonData.put("templateNo","");
//        选填 模板内容
        jsonData.put("replaceMap","");
//        邮件编号
        jsonData.put("emailNo", UUID.randomUUID().toString());


        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);
//        沙盒环境
        OpenClient openClient = new OpenClient(appKey,secret);
        return  JSONObject.parseObject(openClient.sendHttpPost(sendEmailUrl,jsonData.toString(),"application/json",headers));

    }
}
