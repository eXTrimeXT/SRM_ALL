package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.order;

import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源核心 MQL - 报价结果列表查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/07/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderItemResultVO extends MqlSouOrderItemVO {

    @ApiModelProperty("报价单")
    private SouOrder souOrder;

    @ApiModelProperty("供应商")
    private SouVendor souVendor;

    public static List<MqlSouOrderItemResultVO> convertMqlVO(List<SouOrderItem> orderItemList,
                                                             List<SouItem> souItemList,
                                                             List<SouOrder> orderList,
                                                             List<SouVendor> vendorList,
                                                             Map<Long/* orderItemId */, List<SouOrderItemHis>> orderItemLadderMap) {
        if (CollectionUtils.isEmpty(orderItemList)) { return Collections.emptyList(); }

        Map<Long/* souItemId */, SouItem> souItemMap = souItemList.stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        Map<Long/* orderId */, SouOrder> souOrderMap = orderList.stream().collect(Collectors.toMap(SouOrder::getOrderId, Function.identity()));
        Map<Long/* vendorId */, SouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));

        List<MqlSouOrderItemResultVO> voList = SouObjectXUtil.convertList(orderItemList, MqlSouOrderItemResultVO.class);
        voList.forEach(vo -> {
            vo.setSouItem(souItemMap.get(vo.getSouItemId()));
            vo.setSouOrder(souOrderMap.get(vo.getOrderId()));
            vo.setSouVendor(vendorMap.get(vo.getVendorId()));
            vo.setLadderPriceList(orderItemLadderMap.get(vo.getOrderItemId()));
        });
        return voList;
    }

}
