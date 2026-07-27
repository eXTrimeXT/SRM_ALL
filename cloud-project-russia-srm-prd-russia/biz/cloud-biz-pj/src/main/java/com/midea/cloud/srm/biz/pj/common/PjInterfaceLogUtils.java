package com.midea.cloud.srm.biz.pj.common;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogService;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: panmq
 * @Date: 2024/04/12/ $
 * @Description: 接口日志工具
 */
@Component
@Slf4j
public class PjInterfaceLogUtils {
    @Autowired
    private IInterfaceLogService interfaceLogService;

    private static PjInterfaceLogUtils pjInterfaceLogUtils;

    @PostConstruct
    private void init() {
        pjInterfaceLogUtils = this;
    }

    private void saveInterfaceLog(InterfaceLogDTO interfaceLog) {
        try {
            interfaceLogService.createInterfaceLog(interfaceLog);
        } catch (Exception e) {
            log.error("saveInterfaceLog Exception", e);
        }
    }

    /**
     * 保存接口日志
     * @param apiInfoEnum
     * @param reqeustParams
     * @param response
     */
    public static void sendInterfaceLog(ApiInfoEnum apiInfoEnum, Object reqeustParams, String response) {
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,reqeustParams);
        interfaceLog.setReturnInfo(response);
        pjInterfaceLogUtils.saveInterfaceLog(interfaceLog);
    }

}
