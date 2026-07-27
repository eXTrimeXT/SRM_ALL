package com.midea.cloud.srm.model.pj.sou.inq.vo.webapi.select;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.model.base.formula.entity.EssentialFactor;
import com.midea.cloud.srm.model.base.formula.entity.PricingFormulaLine;
import com.midea.cloud.srm.model.base.formula.enums.PricingFormulaLineType;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.init.ApiInqSouItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 简易询价模块 - "公式报价"比价信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/04/19
 */
@Data
public class InqSouFormulaPriceCompareWebVO {

    @ApiModelProperty("轮次")
    private Integer round;
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("物料需求信息")
    private List<ApiInqSouItemVO> itemList = new ArrayList<>();
    @ApiModelProperty("供应商信息")
    private List<SouVendor> vendorList;
    @ApiModelProperty("物料价格公式明细")
    /** souItem */
    private Map<Long, List<InqFormulaInfo>> orderItemFormulaMap;
    @ApiModelProperty("指定轮次的供应商报价信息")
    private List<InqFormulaQuotePrice> orderItemList;

    /**
     * 公式元素信息
     */
    @Data
    private static class InqFormulaInfo {
        /**
         * 元素ID
         */
        private Long factorId;
        /**
         * 元素名称
         */
        private String factorName;
        /**
         * 公式值
         */
        private String formulaValue;
    }

    @Data
    private static class InqFormulaQuotePrice {
        @ApiModelProperty("物料需求行ID")
        private Long souItemId;
        @ApiModelProperty("供应商ID")
        private Long vendorId;
        /**
         * 供应商本币未税单价
         * PS: 类型为String，如果供应商未报价，则值为"未报价"；如果供应商无权限报价，则值为"禁止报价"
         */
        @ApiModelProperty("供应商本币未税单价")
        private String standardNotaxPrice;
        @ApiModelProperty("供应商公式报价")
        private String formulaResult;
    }

    /**
     * 便捷转换方法
     *
     * @param souItemList         物料需求信息
     * @param vendorList       本轮次的可报价的供应商信息
     * @param quoteItemList    本轮次供应商已提交的报价信息
     * @param availableItemMap 本轮次的供应商可报价物料ID集合
     * @param formulaLineMap   公式元素信息
     * @param formulaFactorMap 公式要素信息
     */
    public static InqSouFormulaPriceCompareWebVO convert(
            Integer searchRound,
            SouProject inqHeader,
            List<SouItem> souItemList,
            List<SouVendor> vendorList,
            List<SouOrderItem> quoteItemList,
            List<InqSouOrderItem> inqSouOrderItemList,
            Map<Long/* vendorId */, List<SouItem>> availableItemMap,
            Map<Long/* formulaId */, List<PricingFormulaLine>> formulaLineMap,
            Map<Long/* formulaId */, List<EssentialFactor>> formulaFactorMap,
            Map<String/* fromCurrency_toCurrency */, BigDecimal> exchangeRateMap,
            Map<Long/* factorId */, BigDecimal> baseMaterialPriceMap,
            List<InqSouItem> inqSouItemList
    ) {
        Map<String/* inquiryItemId_vendorId */, SouOrderItem> orderLineMap = quoteItemList.stream()
                .collect(Collectors.toMap(e -> e.getSouItemId() + "_" + e.getVendorId(), Function.identity()));

        Map<Long, InqSouOrderItem> inqSouOrderItemMap = inqSouOrderItemList.stream()
                .collect(Collectors.toMap(InqSouOrderItem::getOrderItemId, Function.identity(), (k1, k2) -> k1));

        Map<Long, InqSouItem> inqSouItemMap = inqSouItemList.stream()
                .collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity(), (k1, k2) -> k1));

        Set<String/* inquiryItemId_vendorId */> availableReqLines = new HashSet<>(souItemList.size() * vendorList.size());
        availableItemMap.forEach((vendorId, souItems) ->
                souItems.forEach(souItem -> availableReqLines.add(souItem.getSouItemId() + "_" + vendorId))
        );
        Set<Long> quotePriceFactorIds = new HashSet<>(formulaFactorMap.size() << 3);
        {
            for (List<EssentialFactor> factors : formulaFactorMap.values()) {
                for (EssentialFactor factor : factors) {
                    if ("SUPPLIER_QUOTED_PRICE".equals(factor.getEssentialFactorFrom())) {
                        quotePriceFactorIds.add(factor.getEssentialFactorId());
                    }
                }
            }
        }

        InqSouFormulaPriceCompareWebVO vo = new InqSouFormulaPriceCompareWebVO();
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
        vo.vendorList = vendorList;
        // 供应商报价信息
        vo.setOrderItemList(new ArrayList<>(souItemList.size()));
        for (SouItem item : souItemList) {
            List<InqFormulaQuotePrice> orders = new ArrayList<>(vendorList.size());

            for (SouVendor vendor : vendorList) {
                InqFormulaQuotePrice order = new InqFormulaQuotePrice();
                orders.add(order);
                // 物料需求行ID
                order.souItemId = item.getSouItemId();
                // 供应商ID
                order.vendorId = vendor.getVendorId();
                // 本币未税单价
                SouOrderItem quoteItem = orderLineMap.get(item.getSouItemId() + "_" + vendor.getVendorId());
                if (quoteItem != null) {
                    order.standardNotaxPrice = quoteItem.getStandardNotaxPrice().stripTrailingZeros().toPlainString();
                    order.formulaResult = formatFormulaAttrs(
                            quoteItem.getOrderCurrency(),
                            inqHeader.getStandardCurrency(),
                            inqSouOrderItemMap.get(quoteItem.getOrderItemId()).getFormulaAttrValues(),
                            quotePriceFactorIds,
                            exchangeRateMap,
                            baseMaterialPriceMap
                    );
                } else {
                    // 可能未提交报价，也可能对该物料无报价权限了
                    boolean hasAuth = availableReqLines.contains(item.getSouItemId() + "_" + vendor.getVendorId());
                    order.standardNotaxPrice = hasAuth ? "未报价" : "禁止报价";
                    order.formulaResult = "";
                }
            }
            vo.getOrderItemList().addAll(orders);
        }
        // 物料价格公式明细
        vo.setOrderItemFormulaMap(new HashMap<>(souItemList.size() << 3));
        Map<Long/* factorId */, EssentialFactor> factorIdMap; {
            factorIdMap = new HashMap<>(64);
            formulaFactorMap.values().forEach(factorList -> factorList.forEach(factor -> factorIdMap.put(factor.getEssentialFactorId(), factor)));
        }
        for (SouItem item : souItemList) {
            Set<Long> factorIds = new HashSet<>(32);
            List<PricingFormulaLine> formulaLineList = formulaLineMap.get(inqSouItemMap.get(item.getSouItemId()).getFormulaId());
            if (formulaLineList == null){
                continue;
            }
            List<InqFormulaInfo> formulaInfos = vo.getOrderItemFormulaMap()
                    .computeIfAbsent(item.getSouItemId(), k -> new ArrayList<>(10));
            for (PricingFormulaLine formulaLine : formulaLineList) {
                if (!PricingFormulaLineType.FIELD.equals(formulaLine.getPricingFormulaLineType())) {
                    continue;
                }
                if (factorIds.contains(formulaLine.getEssentialFactorId())) {
                    continue;
                } else {
                    factorIds.add(formulaLine.getEssentialFactorId());
                }
                InqFormulaInfo formulaInfo = new InqFormulaInfo();
                formulaInfos.add(formulaInfo);
                // 元素ID
                formulaInfo.factorId = formulaLine.getEssentialFactorId();
                // 元素名称
                formulaInfo.factorName = factorIdMap.get(formulaLine.getEssentialFactorId()).getEssentialFactorName();
                // 公式值
                formulaInfo.formulaValue = inqSouItemMap.get(item.getSouItemId()).getFormulaValue();
            }
        }

        return vo;
    }

    private static String formatFormulaAttrs(String quoteCurrency, String inqCurrency,
                                             String formulaResult, Set<Long> quotePriceFactorIds,
                                             Map<String/* fromCurrency_toCurrency */, BigDecimal> exchangeRateMap,
                                             Map<Long/* factorId */, BigDecimal> baseMaterialPriceMap) {
        // 1: 反序列化公式报价json
        Map<String/* factorId */, BigDecimal/* price */> userPriceMap = new HashMap<>(16);
        {
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
                } else if (quotePriceFactorIds.contains(key)) {
                    // 供应商报价(需要进行汇率转换)
                    BigDecimal exchangeRate;
                    if (quoteCurrency.equals(inqCurrency)) {
                        exchangeRate = BigDecimal.ONE;
                    } else {
                        exchangeRate = exchangeRateMap.get(quoteCurrency + "_" + inqCurrency);
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
