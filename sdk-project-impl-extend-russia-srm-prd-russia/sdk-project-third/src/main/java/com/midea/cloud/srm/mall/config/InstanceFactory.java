package com.midea.cloud.srm.mall.config;

import com.midea.cloud.srm.mall.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InstanceFactory {
    @Autowired
    private Map<String, GoodsApi> goodsServiceMap = new HashMap<>(15);
    @Autowired
    private Map<String, AddressApi> addressServiceMap = new HashMap<>(15);
    @Autowired
    private Map<String, OrderApi> orderServiceMap = new HashMap<>(15);

    @Autowired
    private Map<String, AfterSaleApi> afterSaleServiceMap = new HashMap<>(15);

    @Autowired
    private Map<String, InvoiceApi> invoiceMap = new HashMap<>(15);

    public GoodsApi selectGoodsInstance(String mallType) {
        return goodsServiceMap.get(mallType + "GoodsServiceInstance");
    }

    public AddressApi selectAddressInstance(String mallType) {
        return addressServiceMap.get(mallType + "AddressServiceInstance");
    }

    public OrderApi selectOrderInstance(String mallType) {
        return orderServiceMap.get(mallType + "OrderServiceInstance");
    }

    public AfterSaleApi selectAfterSaleInstance(String mallType) {
        return afterSaleServiceMap.get(mallType + "AfterSaleServiceInstance");
    }

    public InvoiceApi selectInvoiceInstance(String mallType) {
        return invoiceMap.get(mallType + "InvoiceApiImpl");
    }
}
