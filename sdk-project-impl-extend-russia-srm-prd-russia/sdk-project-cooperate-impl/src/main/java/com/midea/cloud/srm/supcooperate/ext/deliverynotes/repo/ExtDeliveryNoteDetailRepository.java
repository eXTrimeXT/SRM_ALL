package com.midea.cloud.srm.supcooperate.ext.deliverynotes.repo;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.ExtDeliveryNoteDetail;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.enums.ExtDeliveryNoteDetailStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.service.ExtDeliveryNoteService;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author zenghx2
 */
@Component
public class ExtDeliveryNoteDetailRepository extends PurchaseRepository<ExtDeliveryNoteDetail> {

    public ExtDeliveryNoteDetailRepository() {
        super("DeliveryNoteDetail", "deliveryNoteDetailId", "送货单明细");

        this.register("extCancel", this::extCancel, true, "取消发货");
    }

    @Autowired
    private ExtDeliveryNoteService extDeliveryNoteService;

    /**
     * {"deliveryNoteDetailIds":[1,2]}
     */
    private QlResult extCancel(QlQueryAction action) {
        Record record = getRecord(action);
        List<Long> deliveryNoteDetailIds = (List) record.get("deliveryNoteDetailIds");
        String cancelReason = record.get(ExtDeliveryNoteDetail::getExtCancelReason);
        Assert.notEmpty(deliveryNoteDetailIds, "送货单明细id不能为空");
        List<Record> details = qlService.readByKeys("DeliveryNoteDetail", deliveryNoteDetailIds, Record.class);
        details.forEach(e -> {
            if (!ExtDeliveryNoteDetailStatusEnum.DELIVERED.name().equals(e.get(ExtDeliveryNoteDetail::getExtDetailStatus))
                    && !ExtDeliveryNoteDetailStatusEnum.PART_RECEIPT.name().equals(e.get(ExtDeliveryNoteDetail::getExtDetailStatus))) {
                throw new BaseException("所勾选行不为已发货或者部分入库状态，不允许取消，请检查");
            }
        });
        extDeliveryNoteService.cancelDeliveryDetails(details, cancelReason);
        return QlResult.empty();
    }
}
