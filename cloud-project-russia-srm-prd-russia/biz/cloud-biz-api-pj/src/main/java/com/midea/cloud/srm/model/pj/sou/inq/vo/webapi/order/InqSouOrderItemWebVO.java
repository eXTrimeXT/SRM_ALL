package com.midea.cloud.srm.model.pj.sou.inq.vo.webapi.order;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.base.formula.entity.EssentialFactor;
import com.midea.cloud.srm.model.base.formula.vo.EssentialFactorVO;
import com.midea.cloud.srm.model.base.material.MaterialItemAttributeRelate;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouBaseMaterialPrice;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.openapi.inq.vo.order.ApiInqSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItemLadder;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItemHis;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;

/**
 * 简易询价 - 报价行明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InqSouOrderItemWebVO extends ApiInqSouOrderItemVO {

    public static List<SouOrderItemHis> convertLadderPrice(@Nullable List<SouItemLadder> ladderTemplateList) {
        if (CollectionUtils.isEmpty(ladderTemplateList)) {
            return Collections.emptyList();
        }
        List<SouOrderItemHis> ladderPriceList = new ArrayList<>(ladderTemplateList.size());
        SouOrderItemHis ladderPrice;
        for (SouItemLadder ladderTemplate : ladderTemplateList) {
            ladderPrice = new SouOrderItemHis();
            BeanUtils.copyProperties(ladderTemplate, ladderPrice);
            ladderPriceList.add(ladderPrice);
        }
        return ladderPriceList;
    }

    @SuppressWarnings("rawtypes")
    public static List<InqSouOrderItemWebVO> convertInqVO(List<ApiSouOrderItemVO> voList) {
        if (voList.isEmpty()) { return Collections.emptyList(); }
        List<InqSouOrderItemWebVO> inqVOList; {
            if (voList instanceof Page) {
                inqVOList = new Page<>();
                ((Page)inqVOList).setTotal(((Page)voList).getTotal());
                ((Page)inqVOList).setPageSize(((Page)voList).getPageSize());
                ((Page)inqVOList).setPageNum(((Page)voList).getPageNum());
            } else {
                inqVOList = new ArrayList<>(voList.size());
            }
            voList.forEach(vo -> inqVOList.add(SouObjectXUtil.convertTargetObj(vo, InqSouOrderItemWebVO.class)));
        }
        return inqVOList;
    }

    public static List<EssentialFactorVO> convertFormulaInfo(
            Map<Long/* factorId */, EssentialFactor> factorMap,
            Map<Long/* factorId */, InqSouBaseMaterialPrice> inqBaseMaterialPriceMap,
            Map<Long/* factorId */, MaterialItemAttributeRelate> materialAttrMap,
            Map<String/* fromCurrency_toCurrency */, BigDecimal> exchangeRateMap,
            String currencyCode,
            @Nullable String formulaAttrValues) {

        if (factorMap == null || factorMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<EssentialFactorVO> voList = new ArrayList<>(16);
        EssentialFactorVO vo;

        /* json反序列化 */
        Map<Long/* factorId */, String/* price */> userPriceMap;
        if (formulaAttrValues != null) {
            userPriceMap = deserializationUserPrices(formulaAttrValues);
        } else {
            userPriceMap = Collections.emptyMap();
        }

        /* 填充数据 */
        InqSouBaseMaterialPrice baseMaterialPrice;
        MaterialItemAttributeRelate materialAttrRelate;
        for (Map.Entry<Long/* factorId */, EssentialFactor> factorEntry : factorMap.entrySet()) {
            vo = new EssentialFactorVO();
            BeanUtils.copyProperties(factorEntry.getValue(), vo);

            /* 1. 基价 */
            switch (vo.getEssentialFactorFrom()) {
//                基价
                case "BASE_MATERIAL_PRICE":
                    if (inqBaseMaterialPriceMap != null) {
                        baseMaterialPrice = inqBaseMaterialPriceMap.get(factorEntry.getKey());
                        if (baseMaterialPrice != null) {
                            BigDecimal exchangeRate;
                            if (baseMaterialPrice.getCurrencyType().equals(currencyCode)) {
                                exchangeRate = BigDecimal.ONE;
                            } else {
                                exchangeRate = exchangeRateMap.get(baseMaterialPrice.getCurrencyType() + "_" + currencyCode);
                            }
                            vo.setValue(baseMaterialPrice.getBaseMaterialPrice() != null ?
                                    baseMaterialPrice.getBaseMaterialPrice().multiply(exchangeRate)
                                            .setScale(10, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
                                    : null);
                            vo.setBaseMaterialCurrency(baseMaterialPrice.getCurrencyType());
                        }
                    }
                    break;
//                物料属性
                case "MATERIAL_MAIN_DATA":
                    if (materialAttrMap != null) {
                        materialAttrRelate = materialAttrMap.get(factorEntry.getKey());
                        if (materialAttrRelate != null) {
                            vo.setValue(materialAttrRelate.getAttributeValue());
                        }
                    }
                    break;
//                用户报价
                case "SUPPLIER_QUOTED_PRICE":
                    vo.setValue(userPriceMap.get(factorEntry.getKey()));
                    break;
                default:
                    break;
            }

            voList.add(vo);
        }

        return voList;
    }

    /**
     * 反序列化用户填写的公式值信息
     *
     * @param formulaAttrValues {@link InqSouOrderItem#getFormulaAttrValues()}
     */
    public static Map<Long/* factorId */, String/* price */> deserializationUserPrices(String formulaAttrValues) {
        Map<Long/* factorId */, String/* price */> userPriceMap = new HashMap<>(16);
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(formulaAttrValues);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(MessageFormat.format("公式值json格式错误(反序列化失败):{0}", formulaAttrValues));
        }
        Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
        Map.Entry<String, Object> entry;
        Long key;
        while (iterator.hasNext()) {
            entry = iterator.next();
            try {
                key = Long.valueOf(entry.getKey().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(MessageFormat.format("公式值json格式错误(factorId):{0}", entry.getKey()));
            }
            iterator.remove();

            userPriceMap.put(key, entry.getValue().toString());
        }

        return userPriceMap;
    }

}
