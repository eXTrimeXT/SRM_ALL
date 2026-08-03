package com.midea.cloud.srm.biz.pj.registrationverification.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.biz.pj.registrationverification.RegistrationVerificationService;
import com.midea.cloud.srm.biz.pj.email.EmailService;
import com.midea.cloud.srm.biz.pj.message.MessageService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.*;
import com.midea.cloud.common.utils.VerifyCode;
import com.midea.cloud.common.constants.RedisKey;
import javax.annotation.Resource;
import com.midea.cloud.srm.model.base.systemConfigure.dto.SystemConfigureDTO;
import com.midea.cloud.common.constants.SystemConfigureKey;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class RegistrationVerificationServiceImpl implements RegistrationVerificationService {

    @Autowired
    private RbacClient rbacClient;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private MessageService messageService;
    @Autowired
    private EmailService emailService;


    /**
     * 邮件验证时生成验证码
     **/
    public String getEmailRandomText() {
        /*生成验证码 */
        int width = 120;
        int height = 30;
        BufferedImage verifyImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        return VerifyCode.drawRandomText(width, height, verifyImg);
    }

    /**
     * 手机验证时生成验证码
     **/
    public String getPhoneRandomText() {
        /*生成验证码 */
        SecureRandom random = new SecureRandom();
        int code = (int) ((random.nextDouble() * 9 + 1) * 1000);
        return String.valueOf(code);

    }

    /**
     * 供应商注册验证码验证接口
     ***/
    @Override
    public void sendVerifyCode(String verifyType, String email, String phone) {

        Assert.isTrue(verifyType != null, "验证方式不能为空");
        Assert.isTrue(email != null, "邮箱不能为空");
        Assert.isTrue(phone != null, "电话不能为空");

        String phoneText = "phone";
        String emailText = "email";

        if (phoneText.equals(verifyType)) {
            /* 手机重复性校验 */
            User queryUserParam = new User().setPhone(phone.trim());
            User userByParmForAnon = rbacClient.getUserByParmForAnon(queryUserParam);
            if (userByParmForAnon != null && userByParmForAnon.getUserId() != null) {
                throw new BaseException("【" + phone + "】 手机号已经被注册过，请更换手机号后重试");
            }
            /* 生成验证码 */
            String randomText = getPhoneRandomText();

            if (redisUtil.exists(RedisKey.VERIFY_CODE + phone)) {
                throw new BaseException(LocaleHandler.getLocaleMsg("验证码已发至对应手机号,请检查!"));
            } else {
                SystemConfigureDTO systemConfigure = baseClient.getSystemConfigure(SystemConfigureKey.VERIFICATION_CODE_EXPIRATION_TIME.name());
                org.springframework.util.Assert.isTrue(systemConfigure != null, "请在系统参数配置菜单配置参数VERIFICATION_CODE_EXPIRATION_TIME");
                Long time = Long.valueOf(systemConfigure.getParamValue());

                /*发送验证码 */
                JSONObject phoneMessage =    messageService.message("您好，您的供应商注册验证码为"+randomText,phone);
                String zeroText = "0";
                String resultText = "result";
                if(zeroText.equals(phoneMessage.get(resultText)) ){
                    /*存储验证码 */
                    redisUtil.set(RedisKey.VERIFY_CODE + phone, randomText, time * 60);
                 }else{
                    log.error("-----手机验证码发送-错误信息:" + phoneMessage.get("desc"));
                    throw new BaseException(LocaleHandler.getLocaleMsg("短信验证码发送失败+"+phoneMessage.get("desc")+",请联系系统管理员!"));
                }
            }

        } else if (emailText.equals(verifyType)) {
            /* 邮箱重复性校验 */
            User queryUserParam = new User().setEmail(email.trim());
            User userByParmForAnon = rbacClient.getUserByParmForAnon(queryUserParam);
            if (userByParmForAnon != null && userByParmForAnon.getUserId() != null) {
                throw new BaseException("【" + email + "】 邮箱已经被注册过，请更换邮箱后重试");
            }
            String randomText = getEmailRandomText();

            if (redisUtil.exists(RedisKey.VERIFY_CODE + email)) {
                throw new BaseException(LocaleHandler.getLocaleMsg("验证码已发至对应邮箱,请检查!"));
            } else {
                SystemConfigureDTO systemConfigure = baseClient.getSystemConfigure(SystemConfigureKey.VERIFICATION_CODE_EXPIRATION_TIME.name());
                org.springframework.util.Assert.isTrue(systemConfigure != null, "请在系统参数配置菜单配置参数VERIFICATION_CODE_EXPIRATION_TIME");
                Long time = Long.valueOf(systemConfigure.getParamValue());

                JSONObject emailDate = new JSONObject();
                List<String> toAddressList = new ArrayList<>();
                List<String> ccAddressList = new ArrayList<>();
                toAddressList.add(email);
                /*必填 邮件内容 */
                emailDate.put("content", "您好，您的供应商注册验证码为"+randomText);
                /* 必填 邮件标题 */
                emailDate.put("subject", "美擎供应商注册验证码");
                /*必填 收件人邮箱列表 */
                emailDate.put("toAddress", toAddressList);
                /*选填 抄送人邮箱列表 */
                emailDate.put("ccAddress", ccAddressList);
                JSONObject emailMessage = emailService.sendEmail(emailDate);
                String  code  = emailMessage.getString("code");
                String successValue = "200";
                if(successValue.equals(code)){
                    /*存储验证码 */
                    redisUtil.set(RedisKey.VERIFY_CODE + email, randomText, time * 60);
                }else{
                    log.error("-----邮件验证码发送-错误信息:" + emailMessage.get("message"));
                    throw new BaseException(LocaleHandler.getLocaleMsg("邮箱发送失败,请联系系统管理员!" + emailMessage.get("message")));
                }

            }
        }
    }
}

