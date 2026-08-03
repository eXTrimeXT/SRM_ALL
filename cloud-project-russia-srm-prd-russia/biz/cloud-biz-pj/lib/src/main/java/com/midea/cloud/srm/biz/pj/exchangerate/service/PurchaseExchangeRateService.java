package com.midea.cloud.srm.biz.pj.exchangerate.service;

import com.midea.cloud.srm.model.pj.pricetax.entity.PriceRate;

import java.util.Date;
import java.util.List;

/**
 * @author huangbf3
 */
public interface PurchaseExchangeRateService {

    /**
     * 备注
     * @param correspondingCurrency
     * @param currenyCode
     * @param date
     * @return
     */
    public List<PriceRate> queryExchangeRate(String correspondingCurrency, String currenyCode, Date date);
}
