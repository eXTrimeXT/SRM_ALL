package com.midea.cloud.srm.cm.contract.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCurrency;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 100014336
 */
public class BasicDataUtil {

    private static BasicDataUtil basicDataUtil;

    private final BaseClient baseClient;

    private final Map<String,String> currentCodeNameMap = new HashMap<>(16);

    public static BasicDataUtil newInstance(BaseClient baseClient){
        if(ObjectUtil.isNull(basicDataUtil)){
            basicDataUtil = new BasicDataUtil(baseClient);
        }
        return basicDataUtil;
    }

    private BasicDataUtil(BaseClient baseClient){
        this.baseClient = baseClient;
    }

    public String getCurrencyName(String currencyCode){
        PurchaseCurrency purchaseCurrency = new PurchaseCurrency();
        purchaseCurrency.setCurrencyCode(currencyCode);
        String currencyName = "";
        if(ObjectUtil.isNotEmpty(currencyCode)){
            if(!currentCodeNameMap.containsKey(currencyCode)){
                List<PurchaseCurrency> currencies = baseClient.listAllPurchaseCurrency();
                if(CollUtil.isNotEmpty(currencies)){
                    currencyName = currencies.stream().filter(e->currencyCode.equalsIgnoreCase(e.getCurrencyCode())).collect(Collectors.toList()).get(0).getCurrencyName();
                }
            } else {
                currencyName = currentCodeNameMap.get(currencyCode);
            }
        }
        return currencyName;
    }
}
