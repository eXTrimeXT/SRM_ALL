package com.midea.cloud.srm.sou.bid.earlywarnings.service;

import java.util.List;
import java.util.Map;

/**
 * @Description: for srm 预警接口：递交申请资料预警、发标、收标、评标、上报、定标、中落标通知
 *
 * @author srm
 * @date 2024-05-20
 */
public interface SouBidEarlyWarningService {

    /**
     * 预警
     * @return
     */
    public String doWarning();

    /**
     * 发送钉钉消息
     * @param userNameList
     * @param var
     * @return
     */
    public Boolean dingTalk(List<String> userNameList, Map<String, String> var);
}
