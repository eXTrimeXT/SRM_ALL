package com.midea.cloud.srm.mall.result.jd.Order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 子订单响应参数
 */
@Data
public class OrderDetailNoneChildResultDTO {

    private Integer pOrder;
    private Integer orderState;
    private Long jdOrderId;
    private Integer state;
    private Integer submitState;
    private Integer type;
    private BigDecimal freight;
    private List<Sku> sku;
    private BigDecimal orderPrice;
    private BigDecimal orderNakedPrice;
    private BigDecimal orderTaxPrice;
    private Integer orderType;
    private String createOrderTime;
    private String finishTime;
    private Integer jdOrderState;
    private Integer paymentType;
    private List<PayDeatails> payDeatails;
    private Date outTime;
    private String invoiceType;
    private String poNo;

    @Data
    public static class Sku {
        private String name;
        private Long skuId;
        private Integer num;
        private Integer category;
        private BigDecimal price;
        private BigDecimal tax;
        private Integer oid;
        private Integer type;
        private Integer splitFreight;
        private Integer taxPrice;
        private Integer nakedPrice;

    }

    @Data
    private static class PayDeatails {
        private String flag;
        private String paymentType;
        private BigDecimal payMoney;
    }
}
