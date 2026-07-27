package com.midea.cloud.srm.biz.pj.job;

import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.result.BaseResult;
import com.midea.cloud.quartz.bind.Job;
import com.midea.cloud.quartz.handler.ExecuteableJob;
import com.midea.cloud.srm.model.pj.pricetax.dto.ResultInfoDto;
import com.midea.cloud.srm.model.pj.pricetax.dto.ResultPriceDto;
import com.midea.cloud.srm.model.pj.pricetax.entity.PriceRate;
import com.midea.cloud.srm.model.pj.pricetax.entity.PurchaseExchangeRate;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author huangbf3
 * 汇率定时任务
 */
@Job("priceTax")
@Slf4j
public class SceneFileWarningJob implements ExecuteableJob {

    @Resource
    private QlOpenClient qlOpenClient;

    @Value("${gwm.url.price-rate-url}")
    private String priceRateUrl;

    @Value("${gwm.appkey}")
    private String appKey;

    @Value("${gwm.secret}")
    private String secret;

    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;

    @Override
    public BaseResult executeJob(Map<String, String> params) {
        Map<String, Object> paramsJson = new HashMap<>(50);
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String ld = formatter.format(localDate);
        paramsJson.put("rateDate", ld);
        paramsJson.put("page", 1);
        paramsJson.put("size", 500);
        Integer i = createRateList(paramsJson);
        int num = 500;
        if (i >= num) {
            createRateList(paramsJson);
        }
        return BaseResult.buildSuccess("汇率定时任务-执行成功！");
    }

    public Integer createRateList(Map<String, Object> paramsJson) {
        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);
        OpenClient openClient = new OpenClient(appKey,secret);
        String re = openClient.sendHttpPost(priceRateUrl, JSONObject.toJSONString(paramsJson),"application/json", headers);
        ResultPriceDto rp = JSONObject.parseObject(re, ResultPriceDto.class);
        ResultInfoDto ri = rp.getResult();
        List<PriceRate> list = ri.getRows();
        List<PurchaseExchangeRate> perList = new ArrayList<>();
        list.forEach(e -> {
            PurchaseExchangeRate per = new PurchaseExchangeRate();
            per.setFromCurrencyCode(e.getCorrespondingCurrency());
            per.setToCurrencyCode(e.getTradingCurrency());
            per.setExchangeDate(e.getRateDate());
            per.setPriceTax(e.getRate());
            per.setRateType("COMPANY");
            per.setSourceType("MANUAL");
            per.setEnabled(e.getActiveFlag() ? "Y" : "N");
            perList.add(per);
        });
        qlOpenClient.save(ContextPath.BASE, "PurchaseExchangeRate", perList);
        return list.size();
    }

}
