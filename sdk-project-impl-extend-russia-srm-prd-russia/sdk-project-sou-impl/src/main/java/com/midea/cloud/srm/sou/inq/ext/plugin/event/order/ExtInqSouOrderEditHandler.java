package com.midea.cloud.srm.sou.inq.ext.plugin.event.order;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderItemDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.inq.spi.order.editorder.InqSouOrderEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.order.editorder.SouOrderEditPO;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
/**
 * 备注
 * @author huangbf3
 */
@Component
public class ExtInqSouOrderEditHandler extends InqSouOrderEditHandler {

    @Override
    @ApiOperation("转换处理单个报价行")
    protected SouOrderItem doConvertOrderItem(SouOrderEditPO po, SouItem availableItem, ApiSouOrderItemDTO dto, boolean isTempSave) {
        String orderRemark = dto.getOrderRemark();
        SouOrderItem orderItem = super.doConvertOrderItem(po, availableItem, dto, isTempSave);

        // 修复当前版本产品问题
        orderItem.setOrderRemark(orderRemark);

        return orderItem;
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.inq.name();
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
