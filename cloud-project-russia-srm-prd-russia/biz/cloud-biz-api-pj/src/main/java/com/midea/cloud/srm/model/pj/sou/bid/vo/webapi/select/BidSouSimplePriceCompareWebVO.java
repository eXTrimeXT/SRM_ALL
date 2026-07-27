package com.midea.cloud.srm.model.pj.sou.bid.vo.webapi.select;

import com.midea.cloud.srm.model.pj.sou.openapi.bid.vo.init.ApiBidSouItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemLadder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招投标 - 比价信息(普通报价)
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@Data
public class BidSouSimplePriceCompareWebVO {

    @ApiModelProperty("轮次")
    private Integer round;

    @ApiModelProperty("物料需求信息")
    private List<ApiBidSouItemVO> itemList;

    @ApiModelProperty("供应商信息")
    private List<SouVendor> vendorList;

    /**
     * 指定轮次的供应商报价信息
     */
    private List<BidSouSimplePriceCompareDetailWebVO> orderItemList;
    /**
     * 阶梯价列表
     */
    /** orderItemId */
    @ApiModelProperty("阶梯价信息")
    private Map<Long, List<SouItemLadder>> itemLadderPriceMap;

    /**
     * 便捷转换方法
     *
     * @param itemList      物料需求信息
     * @param vendorList    本轮次的可报价的供应商信息
     * @param orderItemList 本轮次供应商已提交的报价信息
     */
    public static BidSouSimplePriceCompareWebVO convert(Integer searchRound,
                                                        List<ApiBidSouItemVO> itemList,
                                                        List<SouVendor> vendorList,
                                                        List<SouOrderItem> orderItemList,
                                                        Map<Long/* vendorId */, List<SouItem>> availableItemMap,
                                                        List<SouItemLadder> souItemLadderList) {
        Map<String/* souItemId_vendorId */, SouOrderItem> orderItemMap = orderItemList.stream()
                .collect(Collectors.toMap(e -> e.getSouItemId() + "_" + e.getVendorId(), Function.identity()));
        Set<String/* souItemId_vendorId */> availableSouItems = new HashSet<>(itemList.size() * vendorList.size());
        availableItemMap.forEach((vendorId, reqLines) ->
                reqLines.forEach(reqLine -> availableSouItems.add(reqLine.getSouItemId() + "_" + vendorId))
        );

        BidSouSimplePriceCompareWebVO vo = new BidSouSimplePriceCompareWebVO();
        // 轮次
        vo.round = searchRound;
        // 物料需求信息
        vo.itemList = itemList;
        // 供应商信息
        vo.vendorList = vendorList;
        // 供应商报价信息
        vo.setOrderItemList(new ArrayList<>(itemList.size()));
        for (ApiBidSouItemVO souItem : itemList) {
            List<BidSouSimplePriceCompareDetailWebVO> orders = new ArrayList<>(vendorList.size());

            for (SouVendor vendor : vendorList) {
                BidSouSimplePriceCompareDetailWebVO order = new BidSouSimplePriceCompareDetailWebVO();
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
        // 物料阶梯价关联信息
        vo.itemLadderPriceMap = souItemLadderList.stream()
                .collect(Collectors.groupingBy(SouItemLadder::getSouItemId));
        vo.itemLadderPriceMap.values().forEach(lpList -> {
            for (SouItemLadder lp : lpList) {
                if (lp.getBeginQuantity() != null) {
                    lp.setBeginQuantity(lp.getBeginQuantity().stripTrailingZeros());
                }
                if (lp.getEndQuantity() != null) {
                    lp.setEndQuantity(lp.getEndQuantity().stripTrailingZeros());
                }
            }
        });

        return vo;
    }

}
