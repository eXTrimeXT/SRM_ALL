package com.midea.cloud.srm.mall.result.jd.Order;

import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.JDBaseResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

// 物流信息
@EqualsAndHashCode(callSuper = true)
@Data
public class DeliveryResultDTO extends JDBaseResult implements CommonResultDTO {

    private JDDeliveryInfo result;

    @Data
    public static class JDDeliveryInfo {
        private List<WaybillCode> waybillCode;
        private List<OrderTrack> orderTrack;
        private Long jdOrderId;
    }

    @Data
    public static class WaybillCode {
        private Long orderId;
        private Long parentId;
        private String carrier;
        private String deliveryOrderId;
    }

    @Data
    public static class OrderTrack {
        private String content;
        private String msgTime;
        private String operator;
    }
}
