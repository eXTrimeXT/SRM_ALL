package com.midea.cloud.srm.base.industry.noticetemplate.service.impl;

import com.alibaba.fastjson.JSON;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.common.utils.FreemarkerUtils;
import com.midea.cloud.srm.base.industry.config.GwmProperties;
import com.midea.cloud.srm.base.noticetemplate.service.NoticeSendEventService;
import com.midea.cloud.srm.base.noticetemplate.service.impl.AbstractNoticeSendEventService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeReturn;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.base.noticetemplate.entity.NoticeRecord;
import com.midea.cloud.srm.model.base.noticetemplate.entity.NoticeTemplate;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeStatus;
import com.midea.cloud.srm.model.base.noticetemplate.enums.NoticeTemplateModeEnum;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.mideacloud.common.dto.response.BaseResponse;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @date: 2023/9/1 16:38
 * @author huangbf3
 */
@Slf4j
@Service
public class PjChangChengEmailNoticeSendEventServiceImpl extends AbstractNoticeSendEventService implements NoticeSendEventService {

    @Autowired
    private GwmProperties gwmProperties;

    @Autowired
    private BaseClient baseClient;

    @Override
    public String templateCode() {
        return "default";
    }

    @Override
    public String mode() {
        return NoticeTemplateModeEnum.EMAIL.getValue();
    }

    /**
     * 原有email发送实现，默认不用。默认都使用新配置
     * @return
     */
    @Override
    public String channel() {
        return "";
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    public List<NoticeRecord> createRecord(NoticeTemplate noticeTemplate, NoticeSendDTO noticeSendDTO) {
        NoticeRecord noticeRecord = createByTemplate(noticeTemplate);
        noticeRecord.setBusinessId(noticeSendDTO.getBusinessId());
        noticeRecord.setNoticeRecordBatchUuid(noticeSendDTO.getMsgUuid());
        noticeRecord.setNoticeRecordUuid(UUID.randomUUID().toString());

        Map<String, Object> msgParams = noticeSendDTO.getMsgParams();

        Object receiverType = msgParams.getOrDefault(NoticeSendDTO.NOTICE_RECEIVER_TYPE, "");
        Object receiverInfo = msgParams.getOrDefault(NoticeSendDTO.NOTICE_RECEIVER_INFO, "");
        noticeRecord.setNoticeReceiverType(receiverType.toString());
        noticeRecord.setNoticeReceiverInfo(receiverInfo.toString());

        String content = FreemarkerUtils.replace(noticeTemplate.getNoticeTemplateContent(), msgParams);
        String title = FreemarkerUtils.replace(noticeTemplate.getNoticeTemplateTitle(), msgParams);
        noticeRecord.setNoticeRecordTitle(title);
        noticeRecord.setNoticeRecordContent(content);
        noticeRecord.setNoticeRecordParams(JSON.toJSONString(msgParams));

        noticeRecord.setNoticeStatus(NoticeStatus.WAIT.name());

        noticeRecord.setErrorCount(0L);
        noticeRecord.setReadStatus(Enable.N.toString());

        List<NoticeRecord> recordList = new ArrayList<>();
        recordList.add(noticeRecord);
        return recordList;
    }

    @Override
    public BaseResponse<NoticeReturn> sendNotice(NoticeRecord noticeRecord) throws BaseException {
        String title = noticeRecord.getNoticeRecordTitle();
        String content = noticeRecord.getNoticeRecordContent();
        String realReceiverInfo = noticeRecord.getNoticeRealReceiverInfo();

        OpenClient openClient = new OpenClient(gwmProperties.getAppkey(), gwmProperties.getSecret());
        Map<String, Object> param = new HashMap<>(50);

        param.put("emailNo", noticeRecord.getNoticeRecordUuid());
        param.put("content", content);
        param.put("subject", title);
        param.put("isHtml", true);

        if (Enable.Y.name().equals(noticeRecord.getNoticeMergeReceiver())) {
            // 合并发送
            List<String> emails = JSON.parseArray(realReceiverInfo, String.class);
            param.put("toAddress", emails);
        } else {
            String[] toAddress = realReceiverInfo.split(",");

            param.put("toAddress", toAddress);
        }

        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM", gwmProperties.getSrcSystem());

        String res = openClient.sendHttpPost(gwmProperties.getSendEmailUrl(), JSON.toJSONString(param), MediaType.APPLICATION_JSON_VALUE, headers);

        log.info("长城邮件发送结果:" + res);

        Map<String, String> resMap = JSON.parseObject(res, Map.class);
        String text = "200";
        String code = "code";
        if (!text.equals(String.valueOf(resMap.get(code)))) {
            throw new BaseException(ResultCode.UNKNOWN_ERROR, res);
        }

        return new BaseResponse<>();
    }
}
