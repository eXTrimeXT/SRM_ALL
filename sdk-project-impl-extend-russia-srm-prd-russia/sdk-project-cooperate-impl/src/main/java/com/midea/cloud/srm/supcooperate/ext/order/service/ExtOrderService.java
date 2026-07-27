package com.midea.cloud.srm.supcooperate.ext.order.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceiveDetail;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceiveOnTimeDetailQueryParam;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceiveOnTimeQueryParam;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceivePerEmp;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.WarehousingReturnDetail;
import com.midea.cloud.srm.supcooperate.eas.entity.ReceiveInfo;
import com.midea.cloud.srm.supcooperate.ext.order.enums.ExtOrderDetailStatusEnum;

import java.text.ParseException;
import java.util.List;

/**
 * @author zenghx2
 */
public interface ExtOrderService {

    /**
     * cancelOrderDetail
     * @param orderDetailIds
     * @param extReturnRequirement
     * @param closedCause
     */
    void cancelOrderDetail(List<Long>orderDetailIds, String extReturnRequirement, String closedCause);

    /**
     * updateExtOrderStatus
     * @param orderIds
     * @param closedCause
     */
    void updateExtOrderStatus(List<Long> orderIds, String closedCause);

    /**
     * calcExtOrderDetailStatus
     * @param orderDetail
     * @return ExtOrderDetailStatusEnum
     */
    ExtOrderDetailStatusEnum calcExtOrderDetailStatus(Record orderDetail);

    /**
     * writebackStorageQty
     * @param list
     */
    void writebackStorageQty(List<WarehousingReturnDetail> list);

    /**
     * writebackReceiveQty
     * @param list
     * @return void
     */
    void writebackReceiveQty(List<ReceiveInfo> list);

    /**
     * 到货及时率报表
     * @param orderReceiveOnTimeQueryParam
     * @return
     */
    PageInfo<OrderReceivePerEmp> findReceiveOnTimeRatio(OrderReceiveOnTimeQueryParam orderReceiveOnTimeQueryParam);

    /**
     * 到货及时明细
     * @param orderReceiveOnTimeDetailQueryParam
     * @return
     * @throws ParseException
     */
    PageInfo<OrderReceiveDetail> findReceiveOnTimeDetail(OrderReceiveOnTimeDetailQueryParam orderReceiveOnTimeDetailQueryParam) throws ParseException;
}
