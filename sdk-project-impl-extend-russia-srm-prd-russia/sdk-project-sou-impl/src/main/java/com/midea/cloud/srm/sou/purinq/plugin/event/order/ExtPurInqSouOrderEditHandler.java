package com.midea.cloud.srm.sou.purinq.plugin.event.order;

import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouCurrency;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiSouOrderItemDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouCurrencyDAO;
import com.midea.cloud.srm.sou.sourcing.spi.order.editorder.ApiSouOrderEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.order.editorder.SouOrderEditPO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouOrderEditHandler extends ApiSouOrderEditHandler {

    @Autowired
    private ExtPurInqSouCurrencyDAO extPurInqSouCurrencyDAO;

    @Override
    protected Map<String/* fromCurrency_toCurrency */, BigDecimal> getExchangeRateMap(Set<String> fromCurrencies, String toCurrency, SouProject souProject) {
        return extPurInqSouCurrencyDAO.list(ExtPurInqSouCurrency::getProjectId, souProject.getProjectId())
                .stream().collect(Collectors.toMap(e -> e.getCurrencyCode() + "_" + souProject.getStandardCurrency(), ExtPurInqSouCurrency::getPriceTax));
    }

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
        return ExtPurInqSouTypeEnum.ext_pur_inq.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
