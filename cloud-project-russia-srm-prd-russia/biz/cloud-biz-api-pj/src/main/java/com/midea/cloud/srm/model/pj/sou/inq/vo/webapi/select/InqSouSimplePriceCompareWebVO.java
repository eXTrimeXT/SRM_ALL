package com.midea.cloud.srm.model.pj.sou.inq.vo.webapi.select;

import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouCurrency;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.init.ApiInqSouItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 简易询价模块 - "普通报价"比价信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/04/19
 */
@Data
public class InqSouSimplePriceCompareWebVO {

    @ApiModelProperty("轮次")
    private Integer round;
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("物料需求信息")
    private List<ApiInqSouItemVO> itemList = new ArrayList<>();
    @ApiModelProperty("供应商信息")
    private List<SouVendor> vendorList;
    /** orderItemId */
    @ApiModelProperty("阶梯价信息")
    private Map<Long, List<SouItemLadder>> itemLadderPriceMap;
    @ApiModelProperty("指定轮次的供应商报价信息")
    private List<InqLadderQuotePrice> orderItemList;

    /**
     * 指定轮次的供应商报价信息
     */
    @Data
    private static class InqLadderQuotePrice {
        @ApiModelProperty("物料需求行ID")
        private Long souItemId;
        @ApiModelProperty("供应商ID")
        private Long vendorId;
        /** 类型为String，如果供应商未报价，则值为"未报价"；如果供应商无权限报价，则值为"禁止报价" */
        @ApiModelProperty("供应商本币未税单价")
        private String standardNotaxPrice;
        @ApiModelProperty("阶梯报价详情")
        private List<SouOrderItemHis> souOrderItemLadderList;
    }

    /**
     * 便捷转换方法
     *
     * @param souItemList             物料需求信息
     * @param inqSouItemMap
     * @param souVendorList              本轮次的可报价的供应商信息
     * @param souOrderItemList           本轮次供应商已提交的报价信息
     * @param availableReqLineMap     本轮次的供应商可报价物料ID集合
     * @param inqSouCurrencyList            可用币种信息
     * @param souItemLadderList 阶梯价模板信息
     * @param souOrderItemLadderList    供应商阶梯报价信息
     */
    public static InqSouSimplePriceCompareWebVO convert(Integer searchRound,
                                                        List<SouItem> souItemList,
                                                        Map<Long /* souItemId */, InqSouItem> inqSouItemMap,
                                                        List<SouVendor> souVendorList,
                                                        List<SouOrderItem> souOrderItemList,
                                                        Map<Long/* vendorId */, List<SouItem>> availableReqLineMap,
                                                        List<InqSouCurrency> inqSouCurrencyList,
                                                        List<SouItemLadder> souItemLadderList,
                                                        List<SouOrderItemHis> souOrderItemLadderList) {
        Map<String/* souItemId_vendorId */, SouOrderItem> orderLineMap = souOrderItemList.stream()
                .collect(Collectors.toMap(e -> e.getSouItemId() + "_" + e.getVendorId(), Function.identity()));
        Set<String/* souItemId_vendorId */> availableReqLines = new HashSet<>(souItemList.size() * souVendorList.size());
        availableReqLineMap.forEach((vendorId, reqLines) ->
                reqLines.forEach(reqLine -> availableReqLines.add(reqLine.getSouItemId() + "_" + vendorId))
        );
        Map<String/* fromCurrencyCode */, BigDecimal> currencyMap = inqSouCurrencyList.stream()
                .collect(Collectors.toMap(InqSouCurrency::getCurrencyCode, InqSouCurrency::getPriceTax));


        Map<Long/* quoteItemId */, List<SouOrderItemHis>> quoteLadderPriceMap = souOrderItemLadderList.stream()
                .collect(Collectors.groupingBy(SouOrderItemHis::getOrderItemId));

        InqSouSimplePriceCompareWebVO vo = new InqSouSimplePriceCompareWebVO();
        // 轮次
        vo.round = searchRound;

        // 物料需求信息
        List<ApiInqSouItemVO> inqSouItemVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(souItemList)) {
            for (SouItem souItem : souItemList) {
                ApiInqSouItemVO inqSouItemVO = new ApiInqSouItemVO();
                BeanUtils.copyProperties(souItem, inqSouItemVO);
                inqSouItemVO.setNotaxTargetPrice(inqSouItemMap.get(inqSouItemVO.getSouItemId()).getNotaxTargetPrice());
                inqSouItemVOList.add(inqSouItemVO);
            }
        }
        vo.itemList = inqSouItemVOList;

        // 供应商信息
        vo.vendorList = souVendorList;
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
        // 供应商报价信息
        vo.setOrderItemList(new ArrayList<>(souItemList.size()));
        for (SouItem reqLine : souItemList) {
            List<InqLadderQuotePrice> orders = new ArrayList<>(souVendorList.size());

            for (SouVendor vendor : souVendorList) {
                InqLadderQuotePrice order = new InqLadderQuotePrice();
                orders.add(order);
                // 物料需求行ID
                order.souItemId = reqLine.getSouItemId();
                // 供应商ID
                order.vendorId = vendor.getVendorId();
                // 本币未税单价
                SouOrderItem quoteItem = orderLineMap.get(reqLine.getSouItemId() + "_" + vendor.getVendorId());
                if (quoteItem != null) {
                    order.standardNotaxPrice = quoteItem.getStandardNotaxPrice().stripTrailingZeros().toPlainString();
                    order.souOrderItemLadderList = quoteLadderPriceMap.get(quoteItem.getOrderItemId());
                    if (order.souOrderItemLadderList != null) {
                        for (SouOrderItemHis quoteLadderPrice : order.souOrderItemLadderList) {
                            // 将阶梯价中的原币未税单价转换为本币未税单价
                            quoteLadderPrice.setOrderNotaxPrice(quoteLadderPrice.getOrderNotaxPrice()
                                    .multiply(currencyMap.get(quoteItem.getOrderCurrency())).stripTrailingZeros());
                        }
                    }
                } else {
                    // 可能未提交报价，也可能对该物料无报价权限了
                    boolean hasAuth = availableReqLines.contains(reqLine.getSouItemId() + "_" + vendor.getVendorId());
                    order.standardNotaxPrice = hasAuth ? "未报价" : "禁止报价";
                    order.souOrderItemLadderList = Collections.emptyList();
                }
            }
            vo.getOrderItemList().addAll(orders);
        }

        return vo;
    }

}
