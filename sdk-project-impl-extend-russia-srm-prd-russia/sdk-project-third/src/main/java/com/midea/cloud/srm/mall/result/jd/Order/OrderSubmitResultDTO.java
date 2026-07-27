package com.midea.cloud.srm.mall.result.jd.Order;

import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.JDBaseResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

// 物流信息
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderSubmitResultDTO extends JDBaseResult implements CommonResultDTO {

    private JDOrderSubmitResult result;

    @Data
    public static class JDOrderSubmitResult {
        private Long jdOrderId;
        private BigDecimal orderPrice;
        private BigDecimal orderNakedPrice;
        private List<Sku> sku;
        private BigDecimal orderTaxPrice;

        @Data
        public static class Sku {
            private Long skuId;
            private Integer num;
            private Integer category;
            private BigDecimal price;
            private String name;
            private Integer tax;
            private BigDecimal taxPrice;
            private BigDecimal nakedPrice;
            private Integer type;
            private Long oid;
        }
    }
}
