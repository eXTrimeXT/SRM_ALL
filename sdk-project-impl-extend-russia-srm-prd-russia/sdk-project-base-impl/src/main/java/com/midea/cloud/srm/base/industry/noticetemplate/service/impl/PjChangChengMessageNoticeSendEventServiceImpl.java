package com.midea.cloud.srm.base.industry.noticetemplate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.common.utils.FreemarkerUtils;
import com.midea.cloud.srm.base.industry.config.GwmProperties;
import com.midea.cloud.srm.base.noticetemplate.service.NoticeSendEventService;
import com.midea.cloud.srm.base.noticetemplate.service.impl.AbstractNoticeSendEventService;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeReturn;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.base.noticetemplate.entity.NoticeRecord;
import com.midea.cloud.srm.model.base.noticetemplate.entity.NoticeTemplate;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeStatus;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeTemplateModeEnum;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.third.sms.config.SmsChannelProperties;
import com.mideacloud.common.dto.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author GW00302625
 * @description: 对接开放平台短信通道
 * @date: 2023/9/21 13:48
 */
@Slf4j
@Service
public class PjChangChengMessageNoticeSendEventServiceImpl extends AbstractNoticeSendEventService implements NoticeSendEventService {

    @Autowired
    private GwmProperties gwmProperties;

    @Autowired
    private SmsChannelProperties smsChannelProperties;

    @Override
    public String templateCode() {
        return "default";
    }

    @Override
    public String mode() {
        return NoticeTemplateModeEnum.MESSAGE.getValue();
    }

    @Override
    public String channel() {
        return "default";
    }

    @Override
    public int order() {
        return 10;
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
        String phones = realReceiverInfo;
        JSONObject smsObject = new JSONObject();
        //必填 接收手机号码，多个手机号码用英文逗号分隔
        smsObject.put("phones",phones);
        //必填 短信内容，最多1000个汉字，内容中不要出现【】[]这两种方括号，该字符为签名专用
        smsObject.put("content",content);

        OpenClient openClient = new OpenClient(gwmProperties.getPrdAppkey(), gwmProperties.getPrdSecret());
        String res = openClient.sendHttpPost(gwmProperties.getSendSmsUrl(), smsObject.toString(),"application/json");
        log.info("长城短信发送结果:" + res);
        Map<String, String> resMap = JSON.parseObject(res, Map.class);
        if (!SrmConstant.SUCCESS_CODE.equals(String.valueOf(resMap.get(SrmConstant.CODE)))) {
            throw new BaseException(ResultCode.UNKNOWN_ERROR, res);
        }
        return new BaseResponse<>();
    }

}
