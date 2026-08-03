package com.midea.cloud.srm.biz.pj.sou.sourcing.select.score;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouScoreDimensionContextData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 寻源模块 - 价格维度算分标准接口
 *
 * @author zhangwk12@midea.com
 * @since 2022/04/01
 */
public interface ISouCalculatePriceScoreService {

    BigDecimal B_100 = new BigDecimal(100);

    /**
     * 合理高价法: 标准算分公式
     * PS: 100 - [(Pmax - P) / Pmax] * 100
     * @param quotePrice 供应商报价
     * @param highestPrice 对某物料的最高报价
     * @return 维度得分
     */
    default BigDecimal calculateHighPriceScore(BigDecimal quotePrice, BigDecimal highestPrice) {
        // 计算 价格得分
        BigDecimal priceScore = B_100
                .subtract(highestPrice.subtract(quotePrice).divide(highestPrice, 10, RoundingMode.HALF_UP).multiply(B_100))
                .setScale(2, RoundingMode.HALF_UP);
        if (priceScore.compareTo(BigDecimal.ZERO) < 0) {
            priceScore = BigDecimal.ZERO;
        }

        return priceScore;
    }

    /**
     * 合理低价法: 标准算分公式
     * PS: 100 - [(P - Pmin) / Pmin] * 100
     * @param quotePrice 供应商报价
     * @param lowestPrice 对某物料的最低报价
     * @return 维度得分
     */
    default BigDecimal calculateLowPriceScore(BigDecimal quotePrice, BigDecimal lowestPrice) {
        // 计算 价格得分
        BigDecimal priceScore = B_100
                .subtract(quotePrice.subtract(lowestPrice).divide(lowestPrice, 10, RoundingMode.HALF_UP).multiply(B_100))
                .setScale(2, RoundingMode.HALF_UP);
        if (priceScore.compareTo(BigDecimal.ZERO) < 0) {
            priceScore = BigDecimal.ZERO;
        }

        return priceScore;
    }

    /**
     * 用于单项场景，获取每个物料的最低报价
     * @param params
     * @return
     */
    default Map<Long/* souItemId */, BigDecimal/* 最低报价 */> getIndividualMinPriceMap(List<? extends SouScoreDimensionContextData> params) {
        Map<Long/* souItemId */, BigDecimal/* 最低报价 */> minPriceMap = new HashMap<>(64);
        params.forEach(data -> {
            BigDecimal minPrice = minPriceMap.computeIfAbsent(data.getSouItemId(), k -> data.getPrice());
            if (minPrice.compareTo(data.getPrice()) > 0) {
                minPriceMap.put(data.getSouItemId(), data.getPrice());
            }
        });
        return minPriceMap;
    }

    /**
     * 用于单项场景，获取每个物料的最高报价
     * @param params
     * @return
     */
    default Map<Long/* souItemId */, BigDecimal/* 最高报价 */> getIndividualMaxPriceMap(List<? extends SouScoreDimensionContextData> params) {
        Map<Long/* souItemId */, BigDecimal/* 最低报价 */> maxPriceMap = new HashMap<>(64);
        params.forEach(data -> {
            BigDecimal maxPrice = maxPriceMap.computeIfAbsent(data.getSouItemId(), k -> data.getPrice());
            if (maxPrice.compareTo(data.getPrice()) < 0) {
                maxPriceMap.put(data.getSouItemId(), data.getPrice());
            }
        });
        return maxPriceMap;
    }

    /**
     * 用于组合场景，计算每个供应商的组合报价情况
     * @param params
     * @return
     */
    default Map<String/* vendorTab_itemGroup */, BigDecimal/* 供应商组合报价 */> getVendorGroupPriceMap(List<? extends SouScoreDimensionContextData> params) {
        params.forEach(data -> {
            AssertUtils.notNull(data.getItemGroup(), "组合报价时itemGroup(组合)不能为空");
            AssertUtils.notNull(data.getRequireQuantity(), "demandQuantity(物料需求数量)不能为空");
        });
        Map<String/* vendor_itemGroup */, BigDecimal> vendorGroupPriceMap = new HashMap<>(params.size());
        params.forEach(data -> {
            BigDecimal groupPrice = vendorGroupPriceMap.get(data.getVendorTab() + "_" + data.getItemGroup());
            if (groupPrice != null) {
                groupPrice = groupPrice.add(data.getPrice()
                        .multiply(data.getRequireQuantity())
                        .setScale(10, RoundingMode.HALF_UP)); // 中间结果保留10位小数
            } else {
                groupPrice = data.getPrice()
                        .multiply(data.getRequireQuantity())
                        .setScale(10, RoundingMode.HALF_UP);
            }
            vendorGroupPriceMap.put(data.getVendorTab() + "_" + data.getItemGroup(), groupPrice);
        });
        return vendorGroupPriceMap;
    }

    /**
     * 用于组合场景，获取物料组合的最低报价
     * @param params
     * @return
     */
    default Map<String/* itemGroup */, BigDecimal/* 最低报价 */> getCombinationMinPriceMap(List<? extends SouScoreDimensionContextData> params) {
        // 1. 计算每个供应商的每个组合的的总本币未税报价
        Map<String/* vendorTab_itemGroup */, BigDecimal> vendorGroupPriceMap = this.getVendorGroupPriceMap(params);
        // 2. 收集每个组合的最低报价
        Map<String/* itemGroup */, BigDecimal> groupMinPriceMap = new HashMap<>(vendorGroupPriceMap.size());
        vendorGroupPriceMap.forEach((vendorGroup, price) -> {
            String itemGroup = vendorGroup.split("_")[1];
            BigDecimal minPrice = groupMinPriceMap.get(itemGroup);
            if (minPrice == null) {
                minPrice = price;
            } else {
                if (price.compareTo(minPrice) < 0) {
                    minPrice = price;
                }
            }
            groupMinPriceMap.put(itemGroup, minPrice);
        });
        return groupMinPriceMap;
    }

    /**
     * 用于组合场景，获取物料组合的最高报价
     * @param params
     * @return
     */
    default Map<String/* itemGroup */, BigDecimal/* 最高报价 */> getCombinationMaxPriceMap(List<? extends SouScoreDimensionContextData> params) {
        // 1. 计算每个供应商的每个组合的的总本币未税报价
        Map<String/* vendorTab_itemGroup */, BigDecimal> vendorGroupPriceMap = this.getVendorGroupPriceMap(params);
        // 2. 收集每个组合的最低报价
        Map<String/* itemGroup */, BigDecimal> groupMaxPriceMap = new HashMap<>(vendorGroupPriceMap.size());
        vendorGroupPriceMap.forEach((vendorGroup, price) -> {
            String itemGroup = vendorGroup.split("_")[1];
            BigDecimal maxPrice = groupMaxPriceMap.get(itemGroup);
            if (maxPrice == null) {
                maxPrice = price;
            } else {
                if (price.compareTo(maxPrice) > 0) {
                    maxPrice = price;
                }
            }
            groupMaxPriceMap.put(itemGroup, maxPrice);
        });
        return groupMaxPriceMap;
    }

}
