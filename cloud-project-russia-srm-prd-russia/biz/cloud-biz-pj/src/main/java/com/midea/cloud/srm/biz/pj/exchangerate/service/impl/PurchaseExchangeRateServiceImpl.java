package com.midea.cloud.srm.biz.pj.exchangerate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.srm.biz.pj.exchangerate.service.PurchaseExchangeRateService;
import com.midea.cloud.srm.model.pj.pricetax.dto.ResultPriceDto;
import com.midea.cloud.srm.model.pj.pricetax.entity.PriceRate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author huangbf3
 */
@Slf4j
@Api("汇率接口")
@Service
public class PurchaseExchangeRateServiceImpl implements PurchaseExchangeRateService {
    @Value("${gwm.url.price-rate-url}")
    private String priceRateUrl;

    @Value("${gwm.prdAppkey}")
    private String appKey;

    @Value("${gwm.prdSecret}")
    private String secret;

    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;
    
    private static final String RMB = "RMB";
    
    private static final String CNY = "CNY";

    @Override
    public List<PriceRate> queryExchangeRate(String correspondingCurrency, String currenyCode, Date date) {

        //将RMB 替换成 CNY
        correspondingCurrency = replaceRmbAsCny(correspondingCurrency);
        currenyCode = replaceRmbAsCny(currenyCode);

        correspondingCurrency = StringUtils.isBlank(correspondingCurrency)? CNY:correspondingCurrency;

        if(correspondingCurrency.equals(currenyCode)) {
            PriceRate rate = new PriceRate();
            //将CNY 替换成 RMB
            correspondingCurrency = replaceCnyAsRmb(correspondingCurrency);
            currenyCode = replaceCnyAsRmb(currenyCode);
            rate.setTradingCurrency(currenyCode);
            rate.setCorrespondingCurrency(correspondingCurrency);
            rate.setRate(BigDecimal.ONE);
            return Collections.singletonList(rate);
        }

        Map<String, Object> param = new HashMap<>(50);
//        汇率日期
        param.put("rateDate", DateUtil.format(date, DateUtil.DATE_FORMAT_10));
//        对应货币
        param.put("correspondingCurrency", correspondingCurrency);
//        基本货币
        param.put("tradingCurrency", currenyCode);
        param.put("page", 1);
        param.put("size", 500);

        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);
        OpenClient openClient = new OpenClient(appKey,secret);
        String re = openClient.sendHttpPost(priceRateUrl, JSONObject.toJSONString(param),"application/json", headers);

        //将CNY 替换成 RMB
        re = replaceCnyAsRmb(re);

        ResultPriceDto resultPriceDto = null;
        if(StringUtils.isNotBlank(re)) {
            resultPriceDto = JSON.parseObject(re, ResultPriceDto.class);
            if(ObjectUtils.allNotNull(resultPriceDto, resultPriceDto.getResult()) && CollectionUtils.isNotEmpty(resultPriceDto.getResult().getRows())) {
                return resultPriceDto.getResult().getRows();
            }
        }
        return new ArrayList<>();
    }

    private String replaceRmbAsCny(String currency) {
        if(StringUtils.isBlank(currency)) {
            return currency;
        }
        return currency.replaceAll(RMB, CNY);
    }

    private String replaceCnyAsRmb(String currency) {
        if(StringUtils.isBlank(currency)) {
            return currency;
        }
        return currency.replaceAll(CNY, RMB);
    }
}
