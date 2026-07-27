package com.midea.cloud.srm.mall.result.jd.Order;

import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单详情新增京东处理信息
 */

@Data
public class OrderDetailJDDTO extends OrderDetail {

    //京东返回的主订单号
    private String jdOrderId;

    //定时任务判断状态,1,已提交订单，待确认拆单消息；2,已确认拆单消息；3,校验未通过，已拒绝
    private Integer jdState;

}
