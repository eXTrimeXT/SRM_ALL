package com.midea.cloud.srm.supcooperate.ext.order.mapper;

import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceiveDetail;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceiveOnTimeDetailQueryParam;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceivePerEmp;

import java.util.List;

/**
 * @author 100014336 ganyh
 */
public interface PurchaseOrderReceiveOnTimeDetailMapper {

    /**
     * 查询接受详情
     * @param param
     * @return
     */
    List<OrderReceiveDetail> queryReceiveOnTimeDetail(OrderReceiveOnTimeDetailQueryParam param);

    /**
     * queryReceiveOnTimeDetail2
     * @param list
     * @return
     */
    List<OrderReceiveDetail>queryReceiveOnTimeDetail2(List<OrderReceivePerEmp>list);

}
