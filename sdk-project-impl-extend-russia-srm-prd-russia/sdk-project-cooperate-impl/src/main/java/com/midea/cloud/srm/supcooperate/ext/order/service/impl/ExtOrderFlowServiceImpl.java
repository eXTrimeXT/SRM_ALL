package com.midea.cloud.srm.supcooperate.ext.order.service.impl;

import com.midea.cloud.srm.model.suppliercooperate.order.entry.Order;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.PurchaseOrderEnum;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.po.order.enums.OrderTypeEnum;
import com.midea.cloud.srm.po.order.enums.PurchaseOrderEventTagEnum;
import com.midea.cloud.srm.supcooperate.order.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zenghx2
 */
@Slf4j
@Component
public class ExtOrderFlowServiceImpl implements IFlowBusinessCallbackService {

    @Autowired
    IOrderService iOrderService;

    @Autowired
    private com.midea.cloud.srm.po.order.service.IOrderService orderService;

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        log.info("order submitFlow: {}, {}", businessId, param);

        Order order = new Order().setOrderId(businessId).setOrderStatus(PurchaseOrderEnum.UNDER_APPROVAL);
        iOrderService.updateById(order);
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        log.info("order passFlow: {}, {}", businessId, param);
        orderService.approval(businessId);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        log.info("order rejectFlow: {}, {}", businessId, param);
        Order order = new Order().setOrderId(businessId).setOrderStatus(PurchaseOrderEnum.REJECT);
        iOrderService.updateById(order);
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        log.info("order withdrawFlow: {}, {}", businessId, param);
        Order order = new Order().setOrderId(businessId).setOrderStatus(PurchaseOrderEnum.WITHDRAW);
        iOrderService.updateById(order);
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        log.info("order destoryFlow: {}, {}", businessId, param);
        orderService.abandon(businessId);
    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        return null;
    }
}
