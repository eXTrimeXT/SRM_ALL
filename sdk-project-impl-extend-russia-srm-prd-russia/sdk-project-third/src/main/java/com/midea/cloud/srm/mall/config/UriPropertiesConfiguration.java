package com.midea.cloud.srm.mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
@Data
@Configuration
@PropertySource("classpath:api-uri.properties")
@ConfigurationProperties(prefix = "srm.jd")
public class UriPropertiesConfiguration {
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
    private String accessTokenUrl;
    private String refreshTokenUrl;
    private String getJDAddressFromAddressUrl;
    private String getPageNumUrl;
    private String querySkuUrl;
    private String productDetailUrl;
    private String skuImageUrl;
    private String skuStateUrl;
    private String getNewStock;
    private String submitOrder;
    private String selectJdOrderUrl;
    private String orderTrackUrl;
    private String confirmReceivedUrl;
    private String batchConfirmReceivedUrl;
    private String getPromiseTipsUrl;
    private String getGoodsAttributesUrl;
    private String createAfsApplyUrl;
    private String getAfsOutlineUrl ;
    private String updateSendInfoUrl;
    private String cancelAfsApplyUrl;
    private String queryRenewOrderRelationshipInfosUrl;
    private String getApplyDetailInfoUrl;
    private String invoiceSubmitUrl;
    private String invoiceSelectUrl;
    private String queryInvoiceItemUrl;
    private String waybillUrl;
    private String queryDeliveryNoUrl;
    private String imgBaseUrl;
    private String totalCheckNewUrl;
    private String getMessageUrl;
    private String delMessageUrl;
    private String saveOrUpdatePoNoUrl;
    private String getSellPriceUrl;
}
