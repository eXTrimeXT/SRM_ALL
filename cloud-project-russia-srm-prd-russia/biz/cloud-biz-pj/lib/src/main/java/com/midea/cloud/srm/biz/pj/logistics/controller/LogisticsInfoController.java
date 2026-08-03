package com.midea.cloud.srm.biz.pj.logistics.controller;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.srm.biz.pj.logistics.entity.FailMessage;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangbf3
 * 物流信息 SRM->快递100
 */
@Slf4j
@RestController
@RequestMapping("/logistics")
public class LogisticsInfoController {

    @Value("${gwm.prdAppkey}")
    private String preappKey;

    @Value("${gwm.prdSecret}")
    private String presecret;

    @Value("${gwm.url.logisticsInfo-url}")
    private String logisticsInfoUrl;

    @ApiOperation(value = "实时快递查询")
    @PostMapping("/logistics/info/list")
    public Object getLogisticsInfoList(@RequestParam("num") String num) {
        int length = 32;
        if (StringUtils.isBlank(num) || num.length() > length) {
            return new FailMessage();
        }
        log.info("单号==={}", num);
        OpenClient openClient = new OpenClient(preappKey, presecret);
        JSONObject paMap = new JSONObject();
        paMap.put("num", num);
        return openClient.sendHttpPost(logisticsInfoUrl, paMap.toString(),"application/json");
    }
}
