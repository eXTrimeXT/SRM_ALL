package com.midea.cloud.srm.base.industry.noticetemplate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.common.utils.FreemarkerUtils;
import com.midea.cloud.srm.base.noticetemplate.service.NoticeSendEventService;
import com.midea.cloud.srm.base.noticetemplate.service.impl.AbstractNoticeSendEventService;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeReturn;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.base.noticetemplate.entity.NoticeRecord;
import com.midea.cloud.srm.model.base.noticetemplate.entity.NoticeTemplate;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeStatus;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeTemplateModeEnum;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.third.sms.config.SmsChannelProperties;
import com.midea.cloud.srm.third.sms.config.SmsProperties;
import com.mideacloud.common.dto.response.BaseResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @description:
 * @date: 2023/9/21 13:48
 * @author huangbf3
 */
@Slf4j
@Service
@Deprecated
public class PjDahanMessageNoticeSendEventServiceImpl extends AbstractNoticeSendEventService implements NoticeSendEventService {

    @Autowired
    private SmsChannelProperties smsChannelProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String templateCode() {
        return "default";
    }

    @Override
    public String mode() {
        return NoticeTemplateModeEnum.MESSAGE.getValue();
    }

    /**
     * 默认通道，通道为空的情况：阿里云
     * @return
     */
    @Override
    public String channel() {
        return "da_han_san_tong";
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    public List<NoticeRecord> createRecord(NoticeTemplate noticeTemplate, NoticeSendDTO noticeSendDTO) {
        NoticeRecord noticeRecord = createByTemplate(noticeTemplate);

        // 通道默认
        if (StringUtils.isBlank(noticeRecord.getNoticeTemplateChannel())) {
            noticeRecord.setNoticeTemplateChannel("default");
        }

        noticeRecord.setBusinessId(noticeSendDTO.getBusinessId());
        noticeRecord.setNoticeRecordBatchUuid(noticeSendDTO.getMsgUuid());
        noticeRecord.setNoticeRecordUuid(UUID.randomUUID().toString());

        Map<String, Object> msgParams = noticeSendDTO.getMsgParams();

        // 目前不做是否合并的判断，如果进行判断，则直接在创建记录时创建多条记录
        Object mergeReceiver = msgParams.getOrDefault(NoticeSendDTO.NOTICE_MERGE_RECEIVER, "N");

        // 默认为空根据手机号发送短信
        Object receiverType = msgParams.getOrDefault(NoticeSendDTO.NOTICE_RECEIVER_TYPE, "");
        Object receiverInfo = msgParams.getOrDefault(NoticeSendDTO.NOTICE_RECEIVER_INFO, "");
        String externalCode = FreemarkerUtils.replace(noticeTemplate.getNoticeTemplateExternalCode(), msgParams);
        noticeRecord.setNoticeReceiverType(receiverType.toString());
        noticeRecord.setNoticeReceiverInfo(receiverInfo.toString());
        noticeRecord.setNoticeRealReceiverInfo(receiverInfo.toString());
        noticeRecord.setNoticeRecordExternalCode(externalCode);

        String content = FreemarkerUtils.replace(noticeTemplate.getNoticeTemplateContent(), msgParams);
        noticeRecord.setNoticeRecordTitle(null);
        noticeRecord.setNoticeRecordContent(content);
        noticeRecord.setNoticeRecordParams(JSON.toJSONString(msgParams));

        noticeRecord.setNoticeStatus(NoticeStatus.WAIT.name());

        noticeRecord.setErrorCount(0L);
        noticeRecord.setReadStatus(Enable.N.toString());

        List<NoticeRecord> recordList = new ArrayList<>();
        recordList.add(noticeRecord);
        return recordList;
    }

    /**
     * "http://www.dh3t.com/json/sms/Submit"
     * {
     *     "account":"dh****",
     *     "password":"e717ebfd5271ea4a98bd38653c01113d",
     *     "msgid":"2c92825934837c4d0134837dcba00150",
     *     "phones":"1571166****,1571165****",
     *     "content":"您好，您的手机验证码为：430237。",
     *     "sign":"【****】",
     * }
     * @return
     */
    @Override
    public BaseResponse<NoticeReturn> sendNotice(NoticeRecord noticeRecord) throws BaseException {
        String content = noticeRecord.getNoticeRecordContent();
        String realReceiverInfo = noticeRecord.getNoticeRealReceiverInfo();
        String noticeTemplateChannel = noticeRecord.getNoticeTemplateChannel();

        SmsProperties smsProperties = this.smsChannelProperties.getSmsProperties(noticeTemplateChannel);
        String account = smsProperties.getAccessKey();
        String password = smsProperties.getAccessKeySecret();
        String sign = smsProperties.getSignName();
        String msgid = noticeRecord.getNoticeRecordUuid();
        String phones = realReceiverInfo;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("account", account);
        jsonParam.put("password", password);
        jsonParam.put("sign", sign);
        jsonParam.put("msgid", msgid);
        jsonParam.put("phones", phones);
        jsonParam.put("content", content);

        HttpEntity params = new HttpEntity(jsonParam, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(smsProperties.getEndPoint(), params, String.class);
        log.info("发送短信返回:{}", JSONObject.toJSON(responseEntity));
        if (HttpStatus.OK == responseEntity.getStatusCode()) {
            Map<String, String> resMap = JSON.parseObject(responseEntity.getBody(), Map.class);
            String text = "0";
            String result = "result";
            if (!text.equals(resMap.get(result))) {
                throw new BaseException(resMap.get(result), resMap.get("desc"));
            }
        } else {
            throw new BaseException(ResultCode.UNKNOWN_ERROR, "短信发送请求失败");
        }

        return new BaseResponse<>();
    }

}
