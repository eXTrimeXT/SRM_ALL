package com.midea.cloud.common.sms;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.noticetemplate.entity.NoticeTemplate;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeTemplateModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
public class SmsClient {
    /**
     * 短信模板编码
     */
    private Map<String, String> smsTemplateMap = new HashMap<>();
    /**
     * 客户端
     */
    private BaseClient baseClient;
    /**
     * 发送短信客户端
     */
    private PjProjectExtClient pjProjectExtClient;

    public static SmsClient newInstance(BaseClient baseClient, PjProjectExtClient pjProjectExtClient) {
        SmsClient smsClient = new SmsClient();
        smsClient.baseClient = baseClient;
        smsClient.pjProjectExtClient = pjProjectExtClient;
        return smsClient;
    }

    public String getSmsTemplate(String smsCode) {
        String template = smsTemplateMap.get(smsCode);
        if(StringUtils.isBlank(template)) {
            try {
                NoticeTemplate noticeTemplate = baseClient.listPageNoticeTemplate(new NoticeTemplate().setNoticeTemplateCode(smsCode)
                        .setNoticeTemplateMode(NoticeTemplateModeEnum.MESSAGE.getValue()).setNoticeTemplateValid(YesOrNo.YES.getValue())).getList().get(0);
                template = new String(noticeTemplate.getNoticeTemplateContent());
                smsTemplateMap.put(smsCode, template);
            } catch (Exception e) {
                log.error("getSmsTemplate Exception", e);
            }
        }
        return template;
    }

    public Boolean sendSms(String phone, String smsCode, Map<String, String> var) {
        if(StringUtils.isBlank(phone)) {
            log.info("短信通知手机号码为空，未发送短信信息: " + smsCode);
            return false;
        }
        String content = new String(getSmsTemplate(smsCode));
        if(StringUtils.isBlank(content)) {
            log.info("短信模板内容为空：" + smsCode);
            return false;
        }
        try {
            //去掉段落符号
            content = content.replaceAll("<p>", "");
            content = content.replaceAll("</p>", "");
            //发送短信前替换变量
            if(MapUtils.isNotEmpty(var)) {
                for(String key : var.keySet()) {
                    content = content.replace(key, var.get(key));
                }
            }
            log.info(MessageFormat.format("发送短信，手机号{0}，短信内容：{1}", phone, content));
            pjProjectExtClient.message(content,phone);
        } catch (Exception e) {
            log.error("sendSms Exception", e);
            return false;
        }

        return true;
    }
}
