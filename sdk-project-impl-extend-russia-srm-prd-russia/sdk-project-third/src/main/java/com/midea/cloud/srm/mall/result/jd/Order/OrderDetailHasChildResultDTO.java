package com.midea.cloud.srm.mall.result.jd.Order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 父订单响应参数
 */
@Data
public class OrderDetailHasChildResultDTO {

    private POrder pOrder;
    private List<COrder> cOrder;
    private Integer orderState;
    private Integer submitState;
    private Integer type;
    private Integer orderType;
    private String createOrderTime;
    private String finishTime;
    private Integer jdOrderState;
    private String address;
    private String name;
    private String mobile;

    @Data
    public static class POrder {
        private long jdOrderId;
        private List<Sku> sku;
        private BigDecimal freight;
        private BigDecimal orderPrice;
        private BigDecimal orderNakedPrice;
        private BigDecimal orderTaxPrice;
    }

    @Data
    public static class COrder {
        private Integer pOrder;
        private Integer orderState;
        private Long jdOrderId;
        private Integer state;
        private Integer submitState;
        private Integer type;
        private List<Sku> sku;
        private BigDecimal freight;
        private BigDecimal orderPrice;
        private BigDecimal orderNakedPrice;
        private BigDecimal orderTaxPrice;
    }

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

}
