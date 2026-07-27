package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.select;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.model.base.formula.entity.EssentialFactor;
import com.midea.cloud.srm.model.base.formula.entity.PricingFormulaLine;
import com.midea.cloud.srm.model.base.formula.enums.PricingFormulaLineType;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.init.MqlBidSouItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招投标MQL - 比价信息(公式报价)
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/03
 */
@Data
public class MqlBidSouFormulaPriceCompareWebVO extends BaseObjectX {

    @ApiModelProperty("轮次")
    private Integer round;
    @ApiModelProperty("物料需求信息")
    private List<MqlBidSouItemVO> itemList;
    @ApiModelProperty("供应商信息")
    private List<SouVendor> vendorList;
/**    souItemId */
    @ApiModelProperty("物料价格公式明细")
    private Map<Long, List<FormulaInfo>> orderItemFormulaMap;
    @ApiModelProperty("指定轮次的供应商报价信息")
    private List<FormulaOrderPrice> orderItemList;

    /**
     * 公式元素信息
     */
    @Getter
    private static class FormulaInfo {
        @ApiModelProperty("元素ID")
        private Long factorId;
        @ApiModelProperty("元素名称")
        private String factorName;
        @ApiModelProperty("公式值")
        private String formulaValue;
    }

    @Getter
    private static class FormulaOrderPrice {
        @ApiModelProperty("物料需求行ID")
        private Long souItemId;
        @ApiModelProperty("供应商ID")
        private Long vendorId;
        /**
         * PS: 类型为String，如果供应商未报价，则值为"未报价"；如果供应商无权限报价，则值为"禁止报价"
         */
        @ApiModelProperty("供应商本币未税单价")
        private String standardNotaxPrice;
        @ApiModelProperty("供应商公式报价")
        private String formulaResult;
    }

    /**
     * 便捷转换方法
     * @param itemList 物料需求信息
     * @param vendorList 本轮次的可报价的供应商信息
     * @param souOrderItemList 本轮次供应商已提交的报价信息
     * @param availableItemMap 本轮次的供应商可报价物料ID集合
     * @param formulaLineMap 公式元素信息
     */
    public static MqlBidSouFormulaPriceCompareWebVO convert(Integer searchRound,
                                                            SouProject souProject,
                                                            List<MqlBidSouItemVO> itemList,
                                                            List<SouVendor> vendorList,
                                                            List<SouOrderItem> souOrderItemList,
                                                            List<BidSouOrderItem> bidOrderItemList,
                                                            Map<Long/* vendorId */, List<SouItem>> availableItemMap,
                                                            Map<Long/* formulaId */, List<PricingFormulaLine>> formulaLineMap,
                                                            Map<Long/* formulaId */, List<EssentialFactor>> formulaFactorMap,
                                                            Map<String/* fromCurrency_toCurrency */, BigDecimal> exchangeRateMap,
                                                            Map<Long/* factorId */, BigDecimal> baseMaterialPriceMap) {
        Map<String/* souItemId_vendorId */, SouOrderItem> itemMap = souOrderItemList.stream()
                .collect(Collectors.toMap(e -> e.getSouItemId() + "_" + e.getVendorId(), Function.identity()));
        Map<Long/* orderItemId */, BidSouOrderItem> bidOrderItemMap = bidOrderItemList.stream()
                .collect(Collectors.toMap(BidSouOrderItem::getOrderItemId, Function.identity()));
        Set<String/* souItemId_vendorId */> availableItems = new HashSet<>(itemList.size() * vendorList.size());
        availableItemMap.forEach((vendorId, souItems) ->
                souItems.forEach(souItem -> availableItems.add(souItem.getSouItemId() + "_" + vendorId))
        );
        Set<Long> orderPriceFactorIds = new HashSet<>(formulaFactorMap.size() << 3); {
            for (List<EssentialFactor> factors : formulaFactorMap.values()) {
                for (EssentialFactor factor : factors) {
                    if ("SUPPLIER_QUOTED_PRICE".equals(factor.getEssentialFactorFrom())) {
                        orderPriceFactorIds.add(factor.getEssentialFactorId());
                    }
                }
            }
        }

        MqlBidSouFormulaPriceCompareWebVO vo = new MqlBidSouFormulaPriceCompareWebVO();
        // 轮次
        vo.round = searchRound;
        // 物料需求信息
        vo.itemList = itemList;
        // 供应商信息
        vo.vendorList = vendorList;
        // 供应商报价信息
        vo.setOrderItemList(new ArrayList<>(itemList.size()));
        for (SouItem souItem : itemList) {
            List<FormulaOrderPrice> orders = new ArrayList<>(vendorList.size());

            for (SouVendor vendor : vendorList) {
                FormulaOrderPrice order = new FormulaOrderPrice();
                orders.add(order);
                // 物料需求行ID
                order.souItemId = souItem.getSouItemId();
                // 供应商ID
                order.vendorId = vendor.getVendorId();
                // 本币未税单价
                SouOrderItem orderItem = itemMap.get(souItem.getSouItemId() + "_" + vendor.getVendorId());
                if (orderItem != null) {
                    BidSouOrderItem bidOrderItem = bidOrderItemMap.get(orderItem.getOrderItemId());

                    order.standardNotaxPrice = orderItem.getStandardNotaxPrice().stripTrailingZeros().toPlainString();
                    order.formulaResult = formatFormulaAttrs(
                            orderItem.getOrderCurrency(),
                            souProject.getStandardCurrency(),
                            bidOrderItem.getFormulaResult(),
                            orderPriceFactorIds,
                            exchangeRateMap,
                            baseMaterialPriceMap);
                } else {
                    // 可能未提交报价，也可能对该物料无报价权限了
                    boolean hasAuth = availableItems.contains(souItem.getSouItemId() + "_" + vendor.getVendorId());
                    order.standardNotaxPrice = hasAuth ? "未报价" : "禁止报价";
                    order.formulaResult = "";
                }
            }
            vo.getOrderItemList().addAll(orders);
        }
        // 物料价格公式明细
        vo.setOrderItemFormulaMap(new HashMap<>(itemList.size() << 3));
        for (MqlBidSouItemVO item : itemList) {
            Set<Long> factorIds = new HashSet<>(32);
            List<PricingFormulaLine> formulaLineList = formulaLineMap.get(item.getBidSouItem().getFormulaId());
            if (formulaLineList == null) {
                continue;
            }
            List<FormulaInfo> formulaInfos = vo.getOrderItemFormulaMap().computeIfAbsent(item.getSouItemId(), k -> new ArrayList<>(10));
            for (PricingFormulaLine formulaLine : formulaLineList) {
                if (!PricingFormulaLineType.FIELD.equals(formulaLine.getPricingFormulaLineType())) {
                    continue;
                }
                if (factorIds.contains(formulaLine.getEssentialFactorId())) {
                    continue;
                } else {
                    factorIds.add(formulaLine.getEssentialFactorId());
                }
                FormulaInfo formulaInfo = new FormulaInfo();
                formulaInfos.add(formulaInfo);
                // 元素ID
                formulaInfo.factorId = formulaLine.getEssentialFactorId();
                // 元素名称
                formulaInfo.factorName = formulaLine.getPricingFormulaLineValue();
                // 公式值
                formulaInfo.formulaValue = item.getBidSouItem().getFormulaValue();
            }
        }

        return vo;
    }

    private static String formatFormulaAttrs(String orderCurrency, String bidCurrency,
                                             String formulaResult, Set<Long> orderPriceFactorIds,
                                             Map<String/* fromCurrency_toCurrency */, BigDecimal> exchangeRateMap,
                                             Map<Long/* factorId */, BigDecimal> baseMaterialPriceMap) {
        // 1: 反序列化公式报价json
        Map<String/* factorId */, BigDecimal/* price */> userPriceMap = new HashMap<>(16); {
            // json反序列化
            JSONObject jsonObject;
            try {
                jsonObject = JSONObject.parseObject(formulaResult);
            } catch (RuntimeException e) {
                throw new IllegalArgumentException(MessageFormat.format("公式报价json格式错误(反序列化失败):{0}", e.getMessage()));
            }
            Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
            Map.Entry<String, Object> entry;
            long key;
            while (iterator.hasNext()) {
                entry = iterator.next();
                key = Long.valueOf(entry.getKey().trim());
                iterator.remove();

                BigDecimal price = baseMaterialPriceMap.get(key);
                if (price != null) {
                    // 基材价格(以本位币展现)
                    userPriceMap.put(String.valueOf(key), price.stripTrailingZeros());
                } else if (orderPriceFactorIds.contains(key)) {
                    // 供应商报价(需要进行汇率转换)
                    BigDecimal exchangeRate;
                    if (orderCurrency.equals(bidCurrency)) {
                        exchangeRate = BigDecimal.ONE;
                    } else {
                        exchangeRate = exchangeRateMap.get(orderCurrency + "_" + bidCurrency);
                    }
                    userPriceMap.put(String.valueOf(key), new BigDecimal(entry.getValue().toString()).multiply(exchangeRate).stripTrailingZeros());
                } else {
                    // 物料主数据
                    userPriceMap.put(String.valueOf(key), new BigDecimal(entry.getValue().toString()).stripTrailingZeros());
                }
            }
        }

        return JSONObject.toJSONString(userPriceMap);
    }

}
