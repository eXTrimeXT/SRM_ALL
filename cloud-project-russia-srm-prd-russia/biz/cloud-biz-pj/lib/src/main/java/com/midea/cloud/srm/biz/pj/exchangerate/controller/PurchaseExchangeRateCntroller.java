package com.midea.cloud.srm.biz.pj.exchangerate.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.srm.biz.pj.exchangerate.service.PurchaseExchangeRateService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.pricetax.entity.PriceRate;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author huangbf3
 */
@Api("汇率控制层")
@RestController
@RequestMapping("/pj-anon/exchangeRate")
@Slf4j
public class PurchaseExchangeRateCntroller extends BaseController {

    @Autowired
    private PurchaseExchangeRateService purchaseExchangeRateService;

    /**
     *
     * @param correspondingCurrency
     * @param currenyCode
     * @param date
     * @return
     */
    @GetMapping("/queryExchangeRate")
    public List<PriceRate> queryExchangeRate(@RequestParam(value = "correspondingCurrency", required = false) String correspondingCurrency, @RequestParam("currenyCode") String currenyCode, @RequestParam("date") String date) {
        try {
            return purchaseExchangeRateService.queryExchangeRate(correspondingCurrency, currenyCode, DateUtil.parseDate(date));
        } catch (Exception e) {
            log.error("queryExchangeRate Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
