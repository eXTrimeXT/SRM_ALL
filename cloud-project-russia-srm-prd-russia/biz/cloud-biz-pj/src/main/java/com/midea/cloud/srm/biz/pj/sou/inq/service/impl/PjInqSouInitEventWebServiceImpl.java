package com.midea.cloud.srm.biz.pj.sou.inq.service.impl;

import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.srm.biz.pj.sou.inq.service.PjInqSouInitEventWebService;
import com.midea.cloud.srm.feign.base.service.NoticeSendGlobalClientService;
import com.midea.cloud.srm.feign.pj.sou.SouSignClient;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.init.ApiInqSouInitDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouVendorDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-17
 */
@Service
public class PjInqSouInitEventWebServiceImpl implements PjInqSouInitEventWebService {

    @Autowired
    private SouSignClient souSignClient;

    @Autowired
    private NoticeSendGlobalClientService noticeSendGlobalClientService;

    private static final String DATE_FORMAT = "yyyy年MM月dd日 HH:mm:ss";

    @Value("${global.srm.register-address:没有配置地址}")
    private String cloudUrl;

    @Override
    public long editInq(ApiInqSouInitDTO param) {
        long projectId = souSignClient.editInq(param);
        //发邮件通知供应商联系人
        List<ApiSouVendorDTO> list = param.getVendorInfo().getVendorList();
        for(ApiSouVendorDTO dto : list) {
            if(StringUtils.isNotEmpty(dto.getEmail())) {
                sendEmailByTemplate(dto.getEmail());
            }
            ;
        }
        return projectId;
    }

    private void sendEmailByTemplate(String email) {
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setMsgTemplateCode("PJ_INQ_VENDOR_NOTICE");
        noticeSendDTO.setMsgUuid(UUID.randomUUID().toString());
        Map<String, Object> msgParams = new HashMap<>(16);
        msgParams.put(NoticeSendDTO.NOTICE_RECEIVER_INFO, email.trim());
        msgParams.put("srmAddress", cloudUrl);
        msgParams.put("sendTime", DateUtil.format(new Date(), DATE_FORMAT));
        noticeSendDTO.setMsgParams(msgParams);
        noticeSendGlobalClientService.send(noticeSendDTO);
    }


}
