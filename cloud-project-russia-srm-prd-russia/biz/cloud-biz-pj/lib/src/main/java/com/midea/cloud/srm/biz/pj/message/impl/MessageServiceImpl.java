package com.midea.cloud.srm.biz.pj.message.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.srm.biz.pj.message.MessageService;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
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
@Deprecated
public class MessageServiceImpl implements MessageService {

    /**
     *  短信下发接口url
     */
    @Value("${gwm.url.sms-Submit-url}")
    private String smsSubmitUrl;

    /**
     * 用户账号
     */
    @Value("${gwm.url.sms-Submit-account}")
    private String account;

    /**
     * 用户密码
     */
    @Value("${gwm.url.sms-Submit-password}")
    private String password;

    /**
     * 短信签名
     */
    @Value("${gwm.url.sms-Submit-sign}")
    private String sign;

    @Value("${gwm.appkey}")
    private String appKey;

    @Value("${gwm.secret}")
    private String secret;

    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;

    /** * MD5加密之方法一 * @explain 借助apache工具类DigestUtils实现 * @param str * 待加密字符串 * @return 16进制加密字符串 */
    public static String encryptToMd5(String str) {
        return DigestUtils.md5Hex(str);
    }

    /***UUID32位***/
    public static String generateUniqueId() { UUID uuid = UUID.randomUUID(); return uuid.toString();}

    /**
     * 短信通知-发送短信 ,入参为内容和电话列表,多个手机号码用英文逗号分隔
     * **/
    @Override
    public JSONObject message(String content, String phones) {
        Assert.isTrue(phones!=null,"接收手机号码不能为空");
        Assert.isTrue(content!=null,"短信内容不能为空");

        JSONObject smsObject = new JSONObject();
//        必填 用户账号
        smsObject.put("account",account);
//        必填 用户密码
        smsObject.put("password",encryptToMd5(password));
//        选填 该批短信编号，需保证唯一
        smsObject.put("msgid",generateUniqueId());
//        必填 接收手机号码，多个手机号码用英文逗号分隔
        smsObject.put("phones",phones);
//        必填 短信内容，最多1000个汉字，内容中不要出现【】[]这两种方括号，该字符为签名专用
        smsObject.put("content",content);
//        选填 短信签名
//        smsObject.put("sign",sign);
        smsObject.put("sign","【长城汽车】");
//        选填 短信签名对应子码
        smsObject.put("subcode","");
//        选填 定时发送时间
        smsObject.put("sendtime","");

        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);
        log.info("smsObject123123:"+JSONObject.toJSONString(smsObject));
        OpenClient openClient = new OpenClient(appKey,secret);
        JSONObject jsonObject = JSONObject.parseObject(openClient.sendHttpPost(smsSubmitUrl, smsObject.toString(), "application/json", headers));
        log.info("smsResult111:"+JSONObject.toJSONString(jsonObject));
        return jsonObject;



    }
}
