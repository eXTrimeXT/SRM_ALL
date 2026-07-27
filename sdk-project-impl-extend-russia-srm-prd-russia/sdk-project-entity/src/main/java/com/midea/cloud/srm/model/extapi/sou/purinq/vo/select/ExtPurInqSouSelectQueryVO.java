package com.midea.cloud.srm.model.extapi.sou.purinq.vo.select;

import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItemRound;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrderItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ApiPurInqSouOrderItemVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;

import java.math.RoundingMode;
import java.util.*;

/**
 * @author 100014337
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouSelectQueryVO extends BaseObjectX {

    @ApiModelProperty("供应商集合")
    private List<SouVendor> vendorList;

    @ApiModelProperty("物料明细")
    private List<ExtPurInqSouSelectQueryDetailVO> itemList;

    public static ExtPurInqSouSelectQueryVO convert(SouProject souProject, List<SouItem> souItemList,
                                                    Map<Long/* souItemId */, ExtPurInqSouItem> inqSouItemMap,
                                                    Map<String/* souItemId_round */, List<SouOrderItem>> orderItemMap,
                                                    Map<Long/* orderItemId */, ExtPurInqSouOrderItem> inqOrderItemMap,
                                                    Map<Long/* vendorId */, SouVendor> vendorMap,
                                                    Map<String/* souItemId_round */, ExtPurInqSouItemRound> itemRoundMap,
                                                    Map<String/* souItemId_vendorId */, SouOrderItem> firstVendorOrderItemMap) {
        boolean canQueryCurrentRoundOrderInfo = !souProject.getOrderEndTime().after(new Date());

        ExtPurInqSouSelectQueryVO vo = new ExtPurInqSouSelectQueryVO();
        vo.setVendorList(new ArrayList<>(vendorMap.values()));

        vo.setItemList(new ArrayList<>(souItemList.size() * souProject.getCurrentRound()));
        // 1: 构造得到物料明细集合(每个轮次都有)
        Set<Long> souItemIds = new HashSet<>(souItemList.size());
        for (SouItem souItem : souItemList) {
            for (int i = souProject.getCurrentRound(); i >= 1; i--) {
                ExtPurInqSouItemRound itemRound = itemRoundMap.get(souItem.getSouItemId() + "_" + i);
                if (itemRound == null) { continue; }
                // 每个物料，只获取最新的
                if (!souItemIds.add(souItem.getSouItemId())) { continue; }
                ExtPurInqSouSelectQueryDetailVO item = SouObjectXUtil.convertTargetObj(souItem, ExtPurInqSouSelectQueryDetailVO.class);
                vo.getItemList().add(item);
                item.setRound(i);
            }
        }
        // 2: 遍历物料明细
        for (ExtPurInqSouSelectQueryDetailVO item : vo.getItemList()) {
            ExtPurInqSouItem inqSouItem = inqSouItemMap.get(item.getSouItemId());
            SouObjectXUtil.mergeProperties(inqSouItem, item);

            List<SouOrderItem> orderItemList = orderItemMap.get(item.getSouItemId() + "_" + item.getRound());
            item.setOrderCount(orderItemList != null ? orderItemList.size() : 0);

            if (item.getRound() < souProject.getCurrentRound() || (canQueryCurrentRoundOrderInfo)) {
                // 历史轮次、或者当前轮次且已截止报价
                if (CollectionUtils.isNotEmpty(orderItemList)) {
                    item.setOrderItemList(SouObjectXUtil.convertList(orderItemList, ApiPurInqSouOrderItemVO.class));

                    for (ApiPurInqSouOrderItemVO orderItem : item.getOrderItemList()) {
                        ExtPurInqSouOrderItem inqOrderItem = inqOrderItemMap.get(orderItem.getOrderItemId());
                        SouObjectXUtil.mergeProperties(inqOrderItem, orderItem);

                        orderItem.putX("extStandardNotaxTotalPrice", orderItem.getStandardNotaxPrice()
                                // 未税总价
                                .multiply(orderItem.getRequireQuantity()).setScale(4, RoundingMode.HALF_UP));

                        SouOrderItem firstOI = firstVendorOrderItemMap.get(orderItem.getSouItemId() + "_" + orderItem.getVendorId());
                        if (firstOI != null) {
                            orderItem.putX("firstStandardNotaxPrice", firstOI.getStandardNotaxPrice());
                        }
                    }
                } else {
                    item.setOrderItemList(Collections.emptyList());
                }

                ApiPurInqSouOrderItemVO winOrderItem = item.getOrderItemList().stream().filter(e -> SouWinStatusEnum.Y.equals(e.getWinStatus())).findAny().orElse(null);
                if (winOrderItem != null) {
                    SouVendor vendor = vendorMap.get(winOrderItem.getVendorId());

                    item.setWinVendorId(winOrderItem.getVendorId());
                    item.setWinVendorCode(vendor.getVendorCode());
                    item.setWinVendorName(vendor.getVendorName());
                    item.setWinTaxKey(winOrderItem.getTaxKey());
                    item.setWinTaxRate(winOrderItem.getTaxRate());
                    item.setWinStandardNotaxPrice(winOrderItem.getStandardNotaxPrice());
                    item.setWinStandardTotalPrice(winOrderItem.getStandardNotaxPrice().multiply(winOrderItem.getRequireQuantity()));
                    item.setWinInvoiceType(winOrderItem.getInvoiceType());
                    item.setWinExtLeadTime(winOrderItem.getExtLeadTime());
                    item.setWinExtWarrantyPeriod(winOrderItem.getExtWarrantyPeriod());
                }
            }
        }

        return vo;
    }

}
