package com.midea.cloud.srm.biz.pj.supplier.controller;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.srm.model.pj.supplier.entry.dto.InternalSupplierQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangbf3
 * 内部供应商查询
 * 内部调用 /pj-anon
 * 不登录调用 /external
 */
@RestController
@RequestMapping("/pj-anon/supplier/open/platform")
@Api(tags = "开放平台-内部供应商查询")
public class InternalSupplierQueryController {

    @Value("${gwm.url.internalSupplier-url}")
    private String internalSupplierUrl;

    @Value("${gwm.internalSupplier.appKey}")
    private String appKey;

    @Value("${gwm.internalSupplier.secret}")
    private String secret;

    @Value("${gwm.internalSupplier.appCode}")
    private String appCode;

    /**
     * 内部供应商查询接口
     * @param creditCode 统一社会信用代码
     * @return map
     */
    @ApiOperation(value = "内部供应商查询接口")
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
