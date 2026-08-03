package com.midea.cloud.srm.biz.pj.sou.quotetemplate.service;

import com.midea.cloud.srm.model.bid.quotetemplate.client.SouQuoteTempApiCallbackService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author huangbf3
 */
@Service
public class SouQuoteTestApiServiceImpl implements SouQuoteTempApiCallbackService {

    @Override
    public Object callback(Map<String, Object> params) {
        return BigDecimal.TEN;
    }

}
