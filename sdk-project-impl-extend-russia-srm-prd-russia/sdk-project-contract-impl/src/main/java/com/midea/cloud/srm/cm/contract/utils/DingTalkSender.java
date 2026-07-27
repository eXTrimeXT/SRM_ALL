package com.midea.cloud.srm.cm.contract.utils;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.srm.feign.ContractPjExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.noticetemplate.entity.NoticeTemplate;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeTemplateModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 100014336 ganyh19
 */
@Slf4j
public class DingTalkSender {

    /**
     * 钉钉模板编码
     */
    private final Map<String, String> smsTemplateMap = new HashMap<>();
    /**
     *客户端
     */
    private final BaseClient baseClient;
    /**
     * 发送钉钉客户端
     */
    private final ContractPjExtClient contractPjExtClient;


    public static DingTalkSender create(BaseClient baseClient,ContractPjExtClient contractPjExtClient){
        DingTalkSender dingTalkSender = new DingTalkSender(baseClient,contractPjExtClient);
        return dingTalkSender;
    }

    private DingTalkSender(BaseClient baseClient,ContractPjExtClient contractPjExtClient){
        this.baseClient = baseClient;
        this.contractPjExtClient = contractPjExtClient;
    }

    public String getTemplate(String dingTalkCode){
        String template = smsTemplateMap.get(dingTalkCode);
        if(StringUtils.isBlank(template)) {
            try {
                NoticeTemplate noticeTemplate = baseClient.listPageNoticeTemplate(new NoticeTemplate().setNoticeTemplateCode(dingTalkCode)
                        .setNoticeTemplateMode(NoticeTemplateModeEnum.MESSAGE.getValue()).setNoticeTemplateValid(YesOrNo.YES.getValue())).getList().get(0);
                template = noticeTemplate.getNoticeTemplateContent();
                smsTemplateMap.put(dingTalkCode, template);
            } catch (Exception e) {
                log.error("getDingTalkTemplate Exception", e);
            }
        }
        return template;
    }

    public JSONObject sendDingTalk(List<String> userNameList, String dingTalkCode, Map<String, String> params,String defaultTemplate) {
        JSONObject jsonObject = null;
        if(CollectionUtils.isEmpty(userNameList)) {
            log.info("钉钉用户为空，未发送钉钉信息: " + dingTalkCode);
            return null;
        }
        String content = defaultTemplate==null?getTemplate(dingTalkCode):defaultTemplate;
        if(StringUtils.isBlank(content)) {
            log.info("钉钉模板内容为空：" + dingTalkCode);
            return null;
        }
        try {
            //去掉段落符号
            content = content.replaceAll("<p>", "");
            content = content.replaceAll("</p>", "");
            //发送短信前替换变量
            if(MapUtils.isNotEmpty(params)) {
                for(String key : params.keySet()) {
                    if(content.contains(key)){
                        content = content.replace(key, params.get(key));
                    }
                }
            }
            log.info(MessageFormat.format("发送钉钉，用户{0}，短信内容：{1}", userNameList.stream().collect(Collectors.joining("; ")), content));
            jsonObject = contractPjExtClient.workNotices(content, userNameList);
        } catch (Exception e) {
            log.error("sendDingTalk Exception", e);
            return jsonObject;
        }

        return jsonObject;
    }
}
