package com.midea.cloud.srm.mall.api.jd.impl;

import com.midea.cloud.srm.mall.api.InvoiceApi;
import com.midea.cloud.srm.mall.config.UriPropertiesConfiguration;
import com.midea.cloud.srm.mall.utils.SrmHttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 京东商城发票实现处理
 */
@Service("JDInvoiceServiceInstance")
@Slf4j
public class JDInvoiceApiImpl implements InvoiceApi {
    @Autowired
    private UriPropertiesConfiguration uriPropertiesConfiguration;

    @Autowired
    private SrmHttpRequestUtil srmHttpUtil;


}
