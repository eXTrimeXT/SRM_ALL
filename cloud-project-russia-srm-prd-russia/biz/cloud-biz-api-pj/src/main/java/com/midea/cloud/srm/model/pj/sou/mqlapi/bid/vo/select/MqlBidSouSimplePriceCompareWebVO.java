package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.select;

import com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.init.MqlBidSouItemVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.select.MqlBidSouSimplePriceCompareDetailWebVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招投标MQL - 比价信息(普通报价)
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
public class MqlBidSouSimplePriceCompareWebVO extends BaseObjectX {

    @ApiModelProperty("轮次")
    private Integer round;

    @ApiModelProperty("物料需求信息")
    private List<MqlBidSouItemVO> itemList;

    @ApiModelProperty("供应商信息")
    private List<SouVendor> vendorList;

    /**
     * 指定轮次的供应商报价信息
     */
    private List<MqlBidSouSimplePriceCompareDetailWebVO> orderItemList;

    /**
     * 便捷转换方法
     * @param itemList 物料需求信息
     * @param vendorList 本轮次的可报价的供应商信息
     * @param orderItemList 本轮次供应商已提交的报价信息
     */
    public static MqlBidSouSimplePriceCompareWebVO convert(Integer searchRound,
                                                           List<MqlBidSouItemVO> itemList,
                                                           List<SouVendor> vendorList,
                                                           List<SouOrderItem> orderItemList,
                                                           Map<Long/* vendorId */, List<SouItem>> availableItemMap) {
        Map<String/* souItemId_vendorId */, SouOrderItem> orderItemMap = orderItemList.stream()
                .collect(Collectors.toMap(e -> e.getSouItemId() + "_" + e.getVendorId(), Function.identity()));
        Set<String/* souItemId_vendorId */> availableSouItems = new HashSet<>(itemList.size() * vendorList.size());
        availableItemMap.forEach((vendorId, reqLines) ->
                reqLines.forEach(reqLine -> availableSouItems.add(reqLine.getSouItemId() + "_" + vendorId))
        );

        MqlBidSouSimplePriceCompareWebVO vo = new MqlBidSouSimplePriceCompareWebVO();
        // 轮次
        vo.round = searchRound;
        // 物料需求信息
        vo.itemList = itemList;
        // 供应商信息
        vo.vendorList = vendorList;
        // 供应商报价信息
        vo.setOrderItemList(new ArrayList<>(itemList.size()));
        for (MqlBidSouItemVO souItem : itemList) {
            List<MqlBidSouSimplePriceCompareDetailWebVO> orders = new ArrayList<>(vendorList.size());

            for (SouVendor vendor : vendorList) {
                MqlBidSouSimplePriceCompareDetailWebVO order = new MqlBidSouSimplePriceCompareDetailWebVO();
                orders.add(order);
                // 物料需求行ID
                order.setSouItemId(souItem.getSouItemId());
                // 供应商ID
                order.setVendorId(vendor.getVendorId());
                // 本币未税单价
                SouOrderItem orderItem = orderItemMap.get(souItem.getSouItemId() + "_" + vendor.getVendorId());
                if (orderItem != null) {
                    order.setStandardNotaxPrice(orderItem.getStandardNotaxPrice().stripTrailingZeros().toPlainString());
                } else {
                    // 可能未提交报价，也可能对该物料无报价权限了
                    boolean hasAuth = availableSouItems.contains(souItem.getSouItemId() + "_" + vendor.getVendorId());
                    order.setStandardNotaxPrice(hasAuth ? "未报价" : "禁止报价");
                }
            }
            vo.getOrderItemList().addAll(orders);
        }

        return vo;
    }

}
