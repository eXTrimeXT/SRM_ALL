package com.midea.cloud.srm.biz.pj.cooperate.edm.controller;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.enums.api.ResultStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogService;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-06-06
 */
@Slf4j
@Api("汇率控制层")
@RestController
@RequestMapping("/external/edm")
public class PushEdmController {

    @Value("${gwm.prdAppkey}")
    private String preappKey;

    @Value("${gwm.prdSecret}")
    private String presecret;

    @Value("${gwm.url.edmPurOrderPushUrl}")
    private String edmPurOrderPushUrl;
    @ApiModelProperty("EDM草稿信息回传接口")
    @Value("${gwm.url.edmPurDraftPushUrl}")
    private String edmPurDraftPushUrl;
    @Resource
    private IInterfaceLogService interfaceLogService;

    private static final String CONTENT_TYPE = "application/json";


    @ApiOperation(value = "推送采购订单到EDM")
    @PostMapping("/pur/order/push")
    public JSONObject pushPurOrderToEdm(@RequestBody String pa) {
        if (StringUtils.isBlank(pa)) {
            throw new BaseException("参数为空");
        }
        OpenClient openClient = new OpenClient(preappKey, presecret);
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.EDM_DRAFT_ORDER_BACK;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum, pa);
        String result = null;
        try {
            log.info("单号===" + pa);
            result = openClient.sendHttpPost(edmPurOrderPushUrl, pa, CONTENT_TYPE);
            log.info("返回的信息" + result);
            JSONObject jo = JSONObject.parseObject(result);
            log.info("组装后的信息" + result);
            return jo;
        } catch (Exception e) {
            log.info(apiInfoEnum.getServiceName() + "报错:" + e.getMessage());
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLog.setErrorInfo(e.getMessage());
        } finally {
            interfaceLog.setReturnInfo(result);
            interfaceLogService.createInterfaceLog(interfaceLog);
        }
        if (ResultStatus.FAIL.toString().equals(interfaceLog.getStatus())) {
            throw new BaseException("调用接口异常");
        }
        return null;
    }

    @ApiOperation(value = "EDM草稿/订单信息回传接口", notes = "EDM草稿/订单信息回传接口", httpMethod = "POST")
    @PostMapping("/edmDraftOrderBackHaul")
    public JSONObject edmDraftOrderBackHaul(@RequestBody String paStr) {
        if (StringUtils.isBlank(paStr)) {
            throw new BaseException("参数为空");
        }
        OpenClient openClient = new OpenClient(preappKey, presecret);
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.EDM_DRAFT_ORDER_BACK;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum, paStr);
        String result = null;
        try {
            log.info("单号===" + paStr);
            result = openClient.sendHttpPost(edmPurDraftPushUrl, paStr, CONTENT_TYPE);
            log.info("返回的信息" + result);
            JSONObject jo = JSONObject.parseObject(result);
            log.info("组装后的信息" + jo);
            return jo;
        } catch (Exception e) {
            log.info(apiInfoEnum.getServiceName() + "报错:" + e.getMessage());
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLog.setErrorInfo(e.getMessage());
        } finally {
            interfaceLog.setReturnInfo(result);
            interfaceLogService.createInterfaceLog(interfaceLog);
        }
        if (ResultStatus.FAIL.toString().equals(interfaceLog.getStatus())) {
            throw new BaseException("调用接口异常");
        }
        return null;
    }

}
