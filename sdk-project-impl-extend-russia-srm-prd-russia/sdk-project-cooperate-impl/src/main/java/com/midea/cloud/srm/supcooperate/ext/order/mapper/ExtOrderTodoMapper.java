package com.midea.cloud.srm.supcooperate.ext.order.mapper;

import com.midea.cloud.srm.model.supcooperate.entity.SccScOrderPriceTrends;
import com.midea.cloud.srm.model.supcooperate.entity.SccScOrderPriceTrendsLine;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.Order;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: panmq
 * @Date: 2024/03/08/ $
 * @Description: 采购订单待办-查询
 */
public interface ExtOrderTodoMapper {

    /**
     * listDeliveryTodoList
     * @param vendorId
     * @return
     */
    public List<Order> listDeliveryTodoList(@Param("vendorId") Long vendorId);

    /**
     * getMaterialListByPa
     * @param localMoth
     * @return
     */
    List<SccScOrderPriceTrendsLine> getMaterialListByPa(@Param("localMoth") String localMoth);
}
