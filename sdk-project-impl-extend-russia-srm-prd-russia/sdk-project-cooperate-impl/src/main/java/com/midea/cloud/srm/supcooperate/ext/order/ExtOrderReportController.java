package com.midea.cloud.srm.supcooperate.ext.order;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.component.pagehelper.SrmPageHelper;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.common.Page;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceiveDetail;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceiveOnTimeDetailQueryParam;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceiveOnTimeQueryParam;
import com.midea.cloud.srm.model.supcooperate.ext.order.OrderReceivePerEmp;
import com.midea.cloud.srm.supcooperate.ext.order.service.ExtOrderService;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

/**
 * 采购订单报表
 * @author 100014336 ganyh19
 */
@RestController
@RequestMapping("/pj/order/report")
public class ExtOrderReportController extends BaseController {

    @Autowired
    private ExtOrderService extOrderService;

    @PostMapping("/receiveOnTimeRatio")
    public PageInfo<OrderReceivePerEmp> findReceiveOnTimeRatio(@RequestBody OrderReceiveOnTimeQueryParam orderReceiveOnTimeQueryParam){
        return extOrderService.findReceiveOnTimeRatio(orderReceiveOnTimeQueryParam);
    }

    @PostMapping("/receiveOnTimeRatioDetail")
    public PageInfo<OrderReceiveDetail> findReceiveOnTimeRatioDetail(@RequestBody OrderReceiveOnTimeDetailQueryParam orderReceiveOnTimeQueryParam) throws ParseException {
        checkOnTimeDetailParams(orderReceiveOnTimeQueryParam);
        return extOrderService.findReceiveOnTimeDetail(orderReceiveOnTimeQueryParam);
    }

    private void checkOnTimeDetailParams(OrderReceiveOnTimeDetailQueryParam orderReceiveOnTimeQueryParam) {
        int dateSize = 2;
        String dateErr = "请输入正确的日期范围格式";
        if(CollUtil.isNotEmpty(orderReceiveOnTimeQueryParam.getCeeaPurchaseOrderDate())&&orderReceiveOnTimeQueryParam.getCeeaPurchaseOrderDate().size()!=dateSize){
            throw new BaseException(dateErr);
        }
        if(CollUtil.isNotEmpty(orderReceiveOnTimeQueryParam.getDeliveryDate())&&orderReceiveOnTimeQueryParam.getDeliveryDate().size()!=dateSize){
            throw new BaseException(dateErr);
        }
    }
}
