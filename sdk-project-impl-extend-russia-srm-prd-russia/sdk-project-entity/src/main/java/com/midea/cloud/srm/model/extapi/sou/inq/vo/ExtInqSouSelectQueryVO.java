package com.midea.cloud.srm.model.extapi.sou.inq.vo;

import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouItemRound;
import com.midea.cloud.srm.model.extapi.sou.inq.filter.ExtInqSouSelectFilterUtils;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.order.ApiInqSouOrderItemVO;
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

import java.math.BigDecimal;
import java.util.function.Predicate;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtInqSouSelectQueryVO extends BaseObjectX {

    @ApiModelProperty("供应商集合")
    private List<SouVendor> vendorList;

    @ApiModelProperty("物料明细")
    private List<ExtInqSouSelectQueryDetailVO> itemList;

    public static ExtInqSouSelectQueryVO convert(SouProject souProject, List<SouItem> souItemList,
                                                 Map<Long/* souItemId */, InqSouItem> inqSouItemMap,
                                                 Map<String/* souItemId_round */, List<SouOrderItem>> orderItemMap,
                                                 Map<Long/* orderItemId */, InqSouOrderItem> inqOrderItemMap,
                                                 Map<Long/* vendorId */, SouVendor> vendorMap,
                                                 Map<String/* souItemId_round */, ExtPjInqSouItemRound> itemRoundMap) {
        boolean canQueryCurrentRoundOrderInfo = !souProject.getOrderEndTime().after(new Date());

        ExtInqSouSelectQueryVO vo = new ExtInqSouSelectQueryVO();
        vo.setVendorList(new ArrayList<>(vendorMap.values()));

        vo.setItemList(new ArrayList<>(souItemList.size() * souProject.getCurrentRound()));
        // 1: 构造得到物料明细集合(每个轮次都有)
        Set<Long> souItemIds = new HashSet<>(souItemList.size());
        for (SouItem souItem : souItemList) {
            for (int i = souProject.getCurrentRound(); i >= 1; i--) {
                ExtPjInqSouItemRound itemRound = itemRoundMap.get(souItem.getSouItemId() + "_" + i);
                if (itemRound == null) { continue; }
                // 每个物料，只获取最新的
                if (!souItemIds.add(souItem.getSouItemId())) { continue; }
                ExtInqSouSelectQueryDetailVO item = SouObjectXUtil.convertTargetObj(souItem, ExtInqSouSelectQueryDetailVO.class);
                vo.getItemList().add(item);
                item.setRound(i);
            }
        }
        // 2: 遍历物料明细
        for (ExtInqSouSelectQueryDetailVO item : vo.getItemList()) {
            InqSouItem inqSouItem = inqSouItemMap.get(item.getSouItemId());
            SouObjectXUtil.mergeProperties(inqSouItem, item);

            List<SouOrderItem> orderItemList = orderItemMap.get(item.getSouItemId() + "_" + item.getRound());
            /** 过滤报价为0的数据 */
            item.setOrderCount(orderItemList != null ? orderItemList.stream().filter(ExtInqSouSelectFilterUtils.selectOrderPriceWithoutNullOrZero()).collect(Collectors.toList()).size() : 0);

            if (item.getRound() < souProject.getCurrentRound() || (canQueryCurrentRoundOrderInfo)) {
                // 历史轮次、或者当前轮次且已截止报价
                if (CollectionUtils.isNotEmpty(orderItemList)) {
                    item.setOrderItemList(SouObjectXUtil.convertList(orderItemList, ApiInqSouOrderItemVO.class));

                    for (ApiInqSouOrderItemVO orderItem : item.getOrderItemList()) {
                        InqSouOrderItem inqOrderItem = inqOrderItemMap.get(orderItem.getOrderItemId());
                        SouObjectXUtil.mergeProperties(inqOrderItem, orderItem);
                        // 未税总价
                        orderItem.putX("extStandardNotaxTotalPrice", orderItem.getStandardNotaxPrice()
                                .multiply(orderItem.getRequireQuantity()).setScale(4, RoundingMode.HALF_UP));
                    }
                } else {
                    item.setOrderItemList(Collections.emptyList());
                }

                ApiInqSouOrderItemVO winOrderItem = item.getOrderItemList().stream().filter(e -> SouWinStatusEnum.Y.equals(e.getWinStatus())).findAny().orElse(null);
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
