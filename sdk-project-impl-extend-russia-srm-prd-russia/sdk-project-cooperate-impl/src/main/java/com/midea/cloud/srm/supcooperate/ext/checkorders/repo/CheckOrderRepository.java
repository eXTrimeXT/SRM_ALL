package com.midea.cloud.srm.supcooperate.ext.checkorders.repo;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.suppliercooperate.mql.enums.PurchaseSchemaEnum;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import com.midea.cloud.srm.supcooperate.ext.checkorders.dto.CheckOrder;
import com.midea.cloud.srm.supcooperate.ext.checkorders.dto.CheckOrderDetail;
import com.midea.cloud.srm.supcooperate.ext.checkorders.dto.CheckOrderSaveDTO;
import com.midea.cloud.srm.supcooperate.ext.checkorders.enums.CheckOrderStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrderDetail;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import com.midea.cloud.srm.supcooperate.meiql.util.PurchaseMqlUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Component
public class CheckOrderRepository extends PurchaseRepository<CheckOrder> {

    public CheckOrderRepository() {
        super("CheckOrder", "checkOrderId", "验收单");

        this.register("saveOrUpdate", this::saveOrUpdate, true, "暂存/提交");
        this.register("supplierConfirm", this::supplierConfirm, true, "供应商确认");
        this.register("supplierRefuse", this::supplierRefuse, true, "供应商拒绝");
        this.register("approve", this::approve, true, "审批通过");
        this.register("reject", this::reject, true, "审批驳回");
        this.register("withdraw", this::withdraw, true, "审批撤回");
    }

    @Override
    public QlResult delete(QlQueryAction queryAction) {
        List<Record> records = getRecords(queryAction);
        List<Long> checkOrderIds = records.stream().map(r -> r.get(CheckOrder::getCheckOrderId)).collect(Collectors.toList());
        List<Record> checkOrders = qlService.readByKeys(schemaType, checkOrderIds, Record.class);
        PurchaseMqlUtils.checkEntityPK(checkOrderIds, checkOrders, businessName);

        Map<Long, BigDecimal> releaseQtyMap = qlService.queryByWrapper(QlWrappers.query("CheckOrderDetail")
                        .in(CheckOrderDetail::getCheckOrderId, checkOrderIds), Record.class)
                .stream().collect(Collectors.toMap(e -> e.get(CheckOrderDetail::getOrderDetailId), e -> e.get(CheckOrderDetail::getCheckQty), (v1, v2) -> BigDecimalUtil.add(v1, v2)));
        List<Record> orderDetails = qlService.readByKeys(PurchaseSchemaEnum.ORDER_DETAIL.getType(), new ArrayList(releaseQtyMap.keySet()), Record.class);
        orderDetails.forEach(e -> {
            BigDecimal releaseQty = releaseQtyMap.get(e.get(ExtOrderDetail::getOrderDetailId));
            BigDecimal checkQty = BigDecimalUtil.sub(e.get(ExtOrderDetail::getExtCheckQty), releaseQty);
            writeBackOrder(checkQty, e);
        });

        return super.delete(queryAction);
    }

    private QlResult saveOrUpdate(QlQueryAction action) {
        Record record = getRecord(action);
        CheckOrderSaveDTO checkOrderSaveDTO = BeanCopyUtil.convertWithExtensions(record, CheckOrderSaveDTO.class);
        if (!CheckOrderStatusEnum.DRAFT.name().equals(record.get(CheckOrder::getCheckOrderStatus))
                && !CheckOrderStatusEnum.APPROVING.name().equals(record.get(CheckOrder::getCheckOrderStatus))) {
            throw new BaseException("不支持的状态参数");
        }

        Map<Long, BigDecimal> oldCheckQtyMap = null;
        Long checkOrderId = record.get(CheckOrder::getCheckOrderId);
        if (checkOrderId != null) {
            CheckOrder checkOrder = qlService.readByKey("CheckOrder", checkOrderId, CheckOrder.class);
            if (!CheckOrderStatusEnum.DRAFT.name().equals(checkOrder.getCheckOrderStatus())
                    && !CheckOrderStatusEnum.REJECT.name().equals(checkOrder.getCheckOrderStatus())
                    && !CheckOrderStatusEnum.WITHDRAW.name().equals(checkOrder.getCheckOrderStatus())) {
                throw new BaseException("当前状态不能修改");
            }

            oldCheckQtyMap = qlService.queryByWrapper(QlWrappers.query("CheckOrderDetail")
                    .eq(CheckOrderDetail::getCheckOrderId, checkOrderId), Record.class)
                    .stream().collect(Collectors.toMap(e -> e.get(CheckOrderDetail::getOrderDetailId), e -> e.get(CheckOrderDetail::getCheckQty), (v1, v2) -> BigDecimalUtil.add(v1, v2)));
        }

        // 回写数量， 正数表示占用，负数表示释放
        Map<Long, BigDecimal> newCheckQtyMap = PurchaseMqlUtils.trimDeleteFlag(checkOrderSaveDTO.getDetailList())
                .stream().collect(Collectors.toMap(e -> e.getOrderDetailId(), e -> e.getCheckQty(), (v1, v2) -> BigDecimalUtil.add(v1, v2)));
        // 对比新旧单据差值
        if (MapUtils.isNotEmpty(oldCheckQtyMap)) {
            oldCheckQtyMap.forEach((k, v) -> {
                BigDecimal newQty = newCheckQtyMap.get(k);
                if (newQty == null) {
                    // 被删除了，则退回数量
                    newCheckQtyMap.put(k, BigDecimalUtil.sub(BigDecimal.ZERO, v));
                } else {
                    if (newQty.compareTo(v) == 0) {
                        // 新旧值一样，则不需要回写
                        newCheckQtyMap.remove(k);
                    } else {
                        // 新旧值不一样，则回写差值
                        newCheckQtyMap.put(k, BigDecimalUtil.sub(newQty, v));
                    }
                }
            });
        }

        // 回写订单
        if (MapUtils.isNotEmpty(newCheckQtyMap)) {
            List<Record> orderDetails = qlService.readByKeys("OrderDetail", new ArrayList(newCheckQtyMap.keySet()), Record.class);
            orderDetails.forEach(e -> {
                BigDecimal changeQty = newCheckQtyMap.get(e.get(ExtOrderDetail::getOrderDetailId));
                BigDecimal checkQty = BigDecimalUtil.add(e.get(ExtOrderDetail::getExtCheckQty), changeQty);
                writeBackOrder(checkQty, e);
            });
        }

        return super.doSave(action, Arrays.asList(record));
    }

    private QlResult withdraw(QlQueryAction qlQueryAction) {
        return super.doUpdate(qlQueryAction, beforeApprove(qlQueryAction, CheckOrderStatusEnum.APPROVING, CheckOrderStatusEnum.WITHDRAW));
    }

    private QlResult reject(QlQueryAction qlQueryAction) {
        return super.doUpdate(qlQueryAction, beforeApprove(qlQueryAction, CheckOrderStatusEnum.APPROVING, CheckOrderStatusEnum.REJECT));
    }

    private QlResult approve(QlQueryAction qlQueryAction) {
        return super.doUpdate(qlQueryAction, beforeApprove(qlQueryAction, CheckOrderStatusEnum.APPROVING, CheckOrderStatusEnum.APPROVED));
    }

    private QlResult supplierRefuse(QlQueryAction qlQueryAction) {
        return super.doUpdate(qlQueryAction, beforeApprove(qlQueryAction, CheckOrderStatusEnum.APPROVED, CheckOrderStatusEnum.REFUSE));
    }

    private QlResult supplierConfirm(QlQueryAction qlQueryAction) {
        return super.doUpdate(qlQueryAction, beforeApprove(qlQueryAction, CheckOrderStatusEnum.APPROVED, CheckOrderStatusEnum.CONFIRM));
    }

    private List<Record> beforeApprove(QlQueryAction action,CheckOrderStatusEnum orginStatus, CheckOrderStatusEnum status) {
        List<Record> records = getRecords(action);
        Record record = getRecord(action);
        Record checkOrder = readByRecord(record);
        Assert.isTrue(orginStatus.name().equals(checkOrder.get(CheckOrder::getCheckOrderStatus)), "当前不为审批中状态");
        record.put(CheckOrder::getCheckOrderStatus, status);
        return records;
    }

    private void writeBackOrder(BigDecimal checkQty, Record e) {
        if (checkQty.compareTo(BigDecimal.ZERO) < 0 || checkQty.compareTo(e.get(ExtOrderDetail::getOrderNum)) > 0) {
            throw new BaseException("验收数量异常");
        }
        List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER_DETAIL.getType())
                .set(ExtOrderDetail::getExtCheckQty, checkQty)
                .set(OrderDetail::getVersion, e.get(ExtOrderDetail::getVersion) + 1)
                .eq(OrderDetail::getOrderDetailId, e.get(ExtOrderDetail::getOrderDetailId))
                .eq(OrderDetail::getVersion, e.get(ExtOrderDetail::getVersion)));
        Assert.notEmpty(ids, "数据已发生变化，请重试");
    }

}
