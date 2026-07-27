package com.midea.cloud.srm.biz.pj.supplier.controller;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.srm.model.pj.supplier.entry.dto.InternalSupplierQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fu
 * 黑名单供应商查询
 */
@RestController
@RequestMapping("/pj/supplier/blackCompany")
@Api(tags = "开放平台-分页查询失信名单")
public class BlackSupplierQueryController {

    @Value("${gwm.url.internalSupplier-url}")
    private String internalSupplierUrl;

    @Value("${gwm.internalSupplier.appKey}")
    private String appKey;

    @Value("${gwm.internalSupplier.secret}")
    private String secret;

    @Value("${gwm.internalSupplier.appCode}")
    private String appCode;

    /**
     * 分页查询失信名单
     * @param page 页码
     * @return map
     */
    @ApiOperation(value = "查询失信名单")
    @PostMapping("/getInternalSupplierData")
    public Map<String, List<InternalSupplierQuery>> getInternalSupplierData(@RequestBody List<String> creditCode)  {
        OpenClient openClient = new OpenClient(appKey, secret);
        Map<String, List<InternalSupplierQuery>> resultMap = new HashMap<>(50);
        for (String e : creditCode) {
            String par = "?credit_code=" + e + "&appCode=" + appCode;
            String re = openClient.sendHttpGet(internalSupplierUrl + par);
            List<InternalSupplierQuery> list = new ArrayList<>();
            if (StringUtils.isNotBlank(re)) {
                JSONObject obj = JSONObject.parseObject(re);
                if (obj.get("data") != null) {
                    List<InternalSupplierQuery> reList = (List<InternalSupplierQuery>) obj.get("data");
                    list.addAll(reList);
                }
            }
            resultMap.put(e, list);
        }
        return resultMap;
    }

}
