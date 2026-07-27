package com.midea.cloud.project.spi.impl;

import cn.hutool.json.JSONUtil;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.Order;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.OrderDetailStatus;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.PurchaseOrderEnum;
import com.midea.cloud.srm.supcooperate.spi.openapi.order.save.OrderAmountSpiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
@Component
public class ExtOrderAmountSpiServiceImpl implements OrderAmountSpiService {

    @Override
    public void setAmount(Order order, List<OrderDetail> orderDetails) {
        if(CollectionUtils.isEmpty(orderDetails)){
            return;
        }

        log.info("长城计算订单金额开始：{}，{}", JSONUtil.toJsonStr(order), JSONUtil.toJsonStr(orderDetails));

        BigDecimal totalNum = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal noTaxAmount = BigDecimal.ZERO;
        int lineNum = 1;
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getLineNum() == null) {
                orderDetail.setLineNum(lineNum++);
            }

            // 设置明细状态
            if (PurchaseOrderEnum.DRAFT == order.getOrderStatus() || PurchaseOrderEnum.SUBMITTED == order.getOrderStatus()) {
                orderDetail.setOrderDetailStatus(OrderDetailStatus.DRAFT);
            } else if (PurchaseOrderEnum.APPROVED_INVALID == order.getOrderStatus()) {
                orderDetail.setOrderDetailStatus(OrderDetailStatus.WAITING_VENDOR_CONFIRM);
            } else if (PurchaseOrderEnum.APPROVED == order.getOrderStatus()) {
                orderDetail.setOrderDetailStatus(OrderDetailStatus.ACCEPT);
                orderDetail.setConfirmNum(orderDetail.getOrderNum());
            }

            BigDecimal noTaxPrice = orderDetail.getCeeaUnitNoTaxPrice();
            BigDecimal taxRate = orderDetail.getCeeaTaxRate().divide(new BigDecimal(100), 8, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(1));
            //TODO 京东测试临时调整,将京东订单的含税价格调整为集采协议的含税价而不是直接计算
            BigDecimal taxPrice = "JD001".equals(order.getVendorCode())?orderDetail.getCeeaUnitTaxPrice():noTaxPrice.multiply(taxRate);

            BigDecimal orderNum = orderDetail.getOrderNum();
            BigDecimal ceeaAmountIncludingTax = orderNum.multiply(taxPrice).setScale(2,BigDecimal.ROUND_HALF_UP);
            BigDecimal ceeaAmountExcludingTax = orderNum.multiply(noTaxPrice).setScale(2,BigDecimal.ROUND_HALF_UP);
            BigDecimal ceeaTaxAmount = ceeaAmountExcludingTax.subtract(ceeaAmountIncludingTax);
            // 含税单价
            orderDetail.setCeeaUnitTaxPrice(taxPrice)
                    //含税金额
                    .setCeeaAmountIncludingTax(ceeaAmountIncludingTax)
                    //不含税金额
                    .setCeeaAmountExcludingTax(ceeaAmountExcludingTax)
                    //税额
                    .setCeeaTaxAmount(ceeaTaxAmount);

            totalNum = totalNum.add(orderNum);
            taxAmount = taxAmount.add(ceeaAmountIncludingTax);
            noTaxAmount = noTaxAmount.add(ceeaAmountExcludingTax);
        }

        order.setCeeaTotalNum(totalNum);
        order.setCeeaTaxAmount(taxAmount);
        order.setCeeaNoTaxAmount(noTaxAmount);
        order.setVersion(0L);

        log.info("长城计算订单金额结束：{}，{}", JSONUtil.toJsonStr(order), JSONUtil.toJsonStr(orderDetails));
    }

    @Override
    public void setLadderPrice(OrderDetail orderDetail) {
    }

}
