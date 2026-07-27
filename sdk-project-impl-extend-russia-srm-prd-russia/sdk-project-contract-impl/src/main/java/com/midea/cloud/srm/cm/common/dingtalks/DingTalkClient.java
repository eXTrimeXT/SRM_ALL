package com.midea.cloud.srm.cm.common.dingtalks;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.noticetemplate.entity.NoticeTemplate;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeTemplateModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
public class DingTalkClient {
    /**
     * 钉钉模板编码
     */
    private Map<String, String> smsTemplateMap = new HashMap<>(15);
    /**
     * 客户端
     */
    private BaseClient baseClient;
    /**
     * 发送钉钉客户端
     */
    private PjProjectExtClient pjProjectExtClient;

    public static DingTalkClient newInstance(BaseClient baseClient, PjProjectExtClient pjProjectExtClient) {
        DingTalkClient smsClient = new DingTalkClient();
        smsClient.baseClient = baseClient;
        smsClient.pjProjectExtClient = pjProjectExtClient;
        return smsClient;
    }

    public String getDingTalkTemplate(String dingTalkCode) {
        String template = smsTemplateMap.get(dingTalkCode);
        if(StringUtils.isBlank(template)) {
            try {
                NoticeTemplate noticeTemplate = baseClient.listPageNoticeTemplate(new NoticeTemplate().setNoticeTemplateCode(dingTalkCode)
                        .setNoticeTemplateMode(NoticeTemplateModeEnum.MESSAGE.getValue())).getList().get(0);
                template = new String(noticeTemplate.getNoticeTemplateContent());
                smsTemplateMap.put(dingTalkCode, template);
            } catch (Exception e) {
                log.error("getDingTalkTemplate Exception", e);
            }
        }
        return template;
    }

    public Boolean sendDingTalk(List<String> userNameList, String dingTalkCode, Map<String, String> var) {
        if(CollectionUtils.isEmpty(userNameList)) {
            log.info("钉钉用户为空，未发送钉钉信息: " + dingTalkCode);
            return false;
        }
        String content = new String(ObjectUtils.defaultIfNull(getDingTalkTemplate(dingTalkCode), ""));
        if(StringUtils.isBlank(content)) {
            log.info("钉钉模板内容为空：" + dingTalkCode);
            return false;
        }
        try {
            //去掉段落符号
            content = content.replaceAll("<p>", "").replaceAll("<pre>", "");
            content = content.replaceAll("</p>", "").replaceAll("</pre>", "");
            //发送短信前替换变量
            if(MapUtils.isNotEmpty(var)) {
                for(String key : var.keySet()) {
                    content = content.replace(key, var.get(key));
                }
            }
            log.info(MessageFormat.format("发送钉钉，用户{0}，短信内容：{1}", userNameList.stream().collect(Collectors.joining("; ")), content));
            pjProjectExtClient.workNotices(content, userNameList);
        } catch (Exception e) {
            log.error("sendDingTalk Exception", e);
            return false;
        }

        return true;
    }
}
