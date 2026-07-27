package com.midea.cloud.srm.biz.pj.changchengapi.invoice.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-17
 */
@Slf4j
@RestController
@RequestMapping("/invoiceApi")
public class InvoiceApiController {

    @Value("${gwm.appkey}")
    private String appKey;
    @Value("${gwm.secret}")
    private String secret;
    @Value("${gwm.url.invoiceSearchUrl}")
    private String invoiceSearchUrl;
    @Value("${gwm.url.invoiceReimburseUrl}")
    private String invoiceReimburseUrl;

    private static final String RETURN_INFO = "returnInfo";
    private static final String NUM_0000 = "0000";
    private static final String RETURN_CODE = "returnCode";

    /**
     * 查询发票
     */
    @PostMapping("/search")
    public Object search(@RequestBody Map<String, Object> params) {
        String enterpriseCode = (String) params.get("enterpriseCode");
        Assert.hasText(enterpriseCode, "企业编码不能为空");

        OpenClient openClient = new OpenClient(appKey, secret);
        String url = invoiceSearchUrl + "?enterpriseCode=" + enterpriseCode;
        String body = JSONUtil.toJsonStr(params);
        log.info("调用查询发票接口，url:{}, params:{}", url, body);
        String result = openClient.sendHttpPost(url, body, "application/json");
        log.info("调用查询发票接口，result:{}", result);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        if (jsonObject.get(RETURN_INFO) == null) {
            throw new BaseException("调用查询发票接口异常：" + result);
        }

        return jsonObject;
    }

    /**
     * 修改发票报销状态
     */
    @PostMapping("/reimburse")
    public Object reimburse(@RequestBody List<Map<String, Object>> params) {
        Assert.notEmpty(params, "参数不能为空");
        String enterpriseCode = (String) params.get(0).get("enterpriseCode");
        Assert.hasText(enterpriseCode, "企业编码不能为空");

        OpenClient openClient = new OpenClient(appKey, secret);
        String url = invoiceReimburseUrl + "?enterpriseCode=" + enterpriseCode;
        String body = JSONUtil.toJsonStr(params);
        log.info("调用报销发票接口，url:{}, params:{}", url, body);
        String result = openClient.sendHttpPost(url, body, "application/json");
        log.info("调用报销发票接口，result:{}", result);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        Map<String, Object> returnInfo = (Map<String, Object>) jsonObject.get(RETURN_INFO);
        if (returnInfo == null || !NUM_0000.equals(returnInfo.get(RETURN_CODE))) {
            throw new BaseException("调用查询发票接口异常：" + result);
        }

        return jsonObject;
    }

}
