package com.midea.cloud.srm.biz.pj.changchengapi.finance.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.ClientConfig;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author ex_liuxy46
 */
@Api(value = "FinanceController", tags = {"财务接口"})
@RestController
@Slf4j
@RequestMapping("/finance/coin")
public class FinanceController {

    @Value("${gwm.appkey}")
    private String appKey;
    @Value("${gwm.secret}")
    private String secret;

    @ApiModelProperty("认领结果查询接口")
    @Value("${gwm.url.claimUrl}")
    private String claimUrl;
    @ApiModelProperty("应收单撤销查询接口")
    @Value("${gwm.url.receivableUrl}")
    private String receivableUrl;
    @ApiModelProperty("接收应收单信息接口")
    @Value("${gwm.url.receiveUrl}")
    private String receiveUrl;

    @Resource
    private IInterfaceLogService interfaceLogService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final String CONTENT_TYPE = "application/json";

    private static final String CLAIM = "CLAIM";

    @ApiOperation(value = "财务接口", notes = "财务接口", httpMethod = "POST")
    @PostMapping("/sendFinance")
    public String sendFinance(String financeParam, String typeStr) {
        Map<String, String> typeMap = getUrlByType(typeStr);
        if (typeMap == null) {
            throw new BaseException("类型错误");
        }
        OpenClient openClient = new OpenClient(appKey, secret);
        // 设置请求超时时间为50秒（单位：毫秒）
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setConnectTimeout(50000);
        clientConfig.setConnectionRequestTimeout(50000);
        openClient.setClientConfig(clientConfig);
        ApiInfoEnum apiInfoEnum = getType(typeStr);
        assert apiInfoEnum != null;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum);
        String result = null;
        try {
            Map<String, String> headers = new HashMap<>(8);
            headers.put("TenantId", "1");
            headers.put("Timestamp", String.valueOf(System.currentTimeMillis()));
            headers.put("SystemNo", "SRM");
            headers.put("SystemName", "长城慧采云");
            int randomNumber = ThreadLocalRandom.current().nextInt(100, 1000);
            headers.put("RequestNo", LocalDateTime.now().format(FORMATTER) + randomNumber);
            headers.put("MesgNo", typeMap.get("mesNo"));
            log.info("财务接口head===" + JSONObject.toJSONString(headers));
            log.info("财务接口传入的参数===" + financeParam);
            log.info("财务接口传入的类型===" + typeStr);
            String paStr = dealPar(headers, financeParam, typeStr);
            log.info("完整的参数报文===" + paStr);
            result = openClient.sendHttpPost(typeMap.get("url"), paStr, CONTENT_TYPE, headers);
            interfaceLog.setServiceInfo(paStr);
            log.info("财务返回的数据==" + result);
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
        return result;
    }

    public static ApiInfoEnum getType(String typeStr) {
        switch (typeStr) {
            case CLAIM:
                return ApiInfoEnum.CLAIM_FINANCE_SCREENING;
            case "RECEIVABLE":
                return ApiInfoEnum.RECEIVABLE_FINANCE_SCREENING;
            case "RECEIVE":
                return ApiInfoEnum.RECEIVE_FINANCE_SCREENING;
            default:
                return null;
        }
    }

    public Map<String, String> getUrlByType(String typeStr) {
        Map<String, String> reMap = new HashMap<>(4);
        switch (typeStr) {
            case CLAIM:
                //认领结果查询接口
                reMap.put("url", claimUrl);
                reMap.put("mesNo", "ARMG-1002");
                return reMap;
            case "RECEIVABLE":
                //应收单撤销查询接口
                reMap.put("url", receivableUrl);
                reMap.put("mesNo", "ARMG-1003");
                return reMap;
            case "RECEIVE":
                //接收应收单信息接口
                reMap.put("url", receiveUrl);
                reMap.put("mesNo", "ARMG-1001");
                return reMap;
            default:
                return null;
        }
    }

    public static String dealPar(Map<String, String> headers, String str, String typeStr) {
        Map<String, Object> a = new HashMap<>(15);
        Map<String, Object> b = new HashMap<>(15);
        Map<String, Object> c = new HashMap<>(15);
        Map<String, Object> d = new HashMap<>(15);
        if (CLAIM.equals(typeStr)) {
            d.put("ParamSet", JSONArray.parseArray(str));
        } else {
            d.put("ParamSet", JSONObject.parseObject(str));
        }
        c.put("Data", d);
        c.put("Sign", "数据签名");
        b.put("Head", headers);
        b.put("Body", c);
        a.put("Cmscloud", b);
        log.info(JSONObject.toJSONString(a));
        return JSONObject.toJSONString(a);
    }
}
