package com.midea.cloud.srm.supcooperate.ext.order.repo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.api.spec.schema.QlType;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.meiql.core.util.SchemaUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.sou.bidprices.dto.BidPriceDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RecommType;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.suppliercooperate.mql.enums.PurchaseSchemaEnum;
import com.midea.cloud.srm.model.suppliercooperate.order.dto.OrderDetailComplexDTO;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.OrderDetailStatus;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrder;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrderDetail;
import com.midea.cloud.srm.supcooperate.ext.order.service.ExtOrderService;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import com.midea.cloud.srm.supcooperate.meiql.util.PurchaseMqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Component
public class ExtOrderDetailRepository extends PurchaseRepository<OrderDetail>  {

    public ExtOrderDetailRepository() {
        super("OrderDetail", "orderDetailId", "订单明细");

        this.register("listDetailForBuyer", this::listDetailForBuyer, false, "[采购商]查询采购订单明细列表");
        this.register("listDetailForVendor", this::listDetailForVendor, false, "[供应商]查询采购订单明细列表");
        this.register("extCancel", this::extCancel, true, "取消");
    }

    @Autowired
    private ExtOrderService extOrderService;

    @Autowired
    private BaseClient baseClient;

    private QlResult listDetailForVendor(QlQueryAction qlQueryAction) {

        QlResult query = super.query(qlQueryAction);
        this.statisticsNum(query);
//        this.setExtProperty(query);
        return query;
    }

    private QlResult listDetailForBuyer(QlQueryAction qlQueryAction) {
        QlResult query = super.query(qlQueryAction);
        this.statisticsNum(query);
//        this.setExtProperty(query);
        return query;
    }

    private void statisticsNum(QlResult result) {
        List<OrderDetailComplexDTO> orderDetailComplexDTOList = new ArrayList();
        PurchaseMqlUtils.buildResult(result, "OrderDetail", (record) -> {
            OrderDetailComplexDTO orderDetailComplexDTO = new OrderDetailComplexDTO();
            orderDetailComplexDTO.setOrderDetailId(record.get(OrderDetailComplexDTO::getOrderDetailId));
            orderDetailComplexDTO.setOrderDetailStatus(record.get(OrderDetailComplexDTO::getOrderDetailStatus));
            orderDetailComplexDTO.setOrderNum(record.get(OrderDetailComplexDTO::getOrderNum));
            orderDetailComplexDTO.setConfirmNum(record.get(OrderDetailComplexDTO::getConfirmNum));
            orderDetailComplexDTO.setDeliveryNoticeQuantity(record.get(OrderDetailComplexDTO::getDeliveryNoticeQuantity));
            orderDetailComplexDTO.setNotifiedNum(record.get(OrderDetailComplexDTO::getNotifiedNum));
            orderDetailComplexDTO.setStorageNum(record.get(OrderDetailComplexDTO::getStorageNum));
            orderDetailComplexDTO.setReturnNum(record.get(OrderDetailComplexDTO::getReturnNum));
            orderDetailComplexDTOList.add(orderDetailComplexDTO);
        });
        this.statisticsNum(orderDetailComplexDTOList);
        Map<Long, OrderDetailComplexDTO> complexDTOMap = orderDetailComplexDTOList.stream().collect(Collectors.toMap(OrderDetailComplexDTO::getOrderDetailId, Function.identity(), (a, b) -> a));
        PurchaseMqlUtils.buildResult(result, PurchaseSchemaEnum.ORDER_DETAIL.getType(), (record) -> {
            OrderDetailComplexDTO orderDetailComplexDTO = complexDTOMap.get(record.getLong("orderDetailId"));
            record.put("notifiedNum", orderDetailComplexDTO.getNotifiedNum());
            record.put("notNotifiedNum", orderDetailComplexDTO.getNotNotifiedNum());
            record.put("inStockNum", orderDetailComplexDTO.getInStockNum());
            record.put("inDeliveryNum", orderDetailComplexDTO.getInDeliveryNum());
            record.put("unDeliveryNum", orderDetailComplexDTO.getUnDeliveryNum());
            record.put(ExtOrderDetail::getConfirmNum, orderDetailComplexDTO.getOrderNum());
            record.put("remainNum", BigDecimalUtil.sub(orderDetailComplexDTO.getOrderNum(), orderDetailComplexDTO.getReceiveSum()));
            if (record.get(ExtOrderDetail::getExtDetailStatus) == null) {
                record.put(ExtOrderDetail::getExtDetailStatus, record.get(ExtOrderDetail::getOrderDetailStatus));
            }
        });
        PurchaseMqlUtils.buildResult(result, PurchaseSchemaEnum.ORDER.getType(), (record) -> {
            if (record.get(ExtOrder::getExtStatus) == null) {
                record.put(ExtOrder::getExtStatus, record.get(ExtOrder::getOrderStatus));
            }
        });
    }

    private void setExtProperty(QlResult result){
        List<String> materialCodes = PurchaseMqlUtils.fetchResult(result,PurchaseSchemaEnum.ORDER_DETAIL).values().stream().map(e->e.get(OrderDetail::getMaterialCode)).collect(Collectors.toList());
        if(CollUtil.isNotEmpty(materialCodes)){
            Map<String, MaterialItem> materialItemMap  = baseClient.listMaterialItemsByCodes(materialCodes);
            PurchaseMqlUtils.buildResult(result,PurchaseSchemaEnum.ORDER_DETAIL.getType(),(record -> {
                MaterialItem materialItem = materialItemMap.get(record.get(MaterialItem::getMaterialCode));
                if(ObjectUtil.isNotNull(materialItem)){
                    record.put(ExtOrderDetail::getExtMaterialItemDesc,materialItem.getDescription());
                    record.put(ExtOrderDetail::getExtMaterialItemType,materialItem.getMaterialType());
                }
            }));
        }
    }

    private void statisticsNum(List<OrderDetailComplexDTO> list) {
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach((e) -> {
                BigDecimal orderNum = e.getOrderNum();
                e.setInStockNum(BigDecimalUtil.sub(e.getStorageNum(), e.getReturnNum()));
                BigDecimal deliveryNum = e.getReceiveSum();
                e.setInDeliveryNum(BigDecimalUtil.sub(deliveryNum, e.getStorageNum()));
                if (OrderDetailStatus.CLOSED.equals(e.getOrderDetailStatus())) {
                    e.setUnDeliveryNum(BigDecimal.ZERO);
                } else {
                    e.setUnDeliveryNum(BigDecimalUtil.sub(orderNum, deliveryNum));
                }
            });
        }
    }

    /**
     * {"orderDetailIds":[1,2],"extReturnRequirement":"Y"}
     */
    private QlResult extCancel(QlQueryAction action) {
        Record record = getRecord(action);
        List<Long> orderDetailIds = (List) record.get("orderDetailIds");
        String closedCause = record.get(ExtOrderDetail::getClosedCause);

        // 取消逻辑
        extOrderService.cancelOrderDetail(orderDetailIds, record.get(ExtOrderDetail::getExtReturnRequirement), closedCause);

        return QlResult.empty();
    }

}
