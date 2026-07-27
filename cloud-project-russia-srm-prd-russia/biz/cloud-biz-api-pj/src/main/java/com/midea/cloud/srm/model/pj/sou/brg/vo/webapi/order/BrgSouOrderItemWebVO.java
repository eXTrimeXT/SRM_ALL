package com.midea.cloud.srm.model.pj.sou.brg.vo.webapi.order;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.base.formula.entity.EssentialFactor;
import com.midea.cloud.srm.model.base.formula.vo.EssentialFactorVO;
import com.midea.cloud.srm.model.base.material.MaterialItemAttributeRelate;
import com.midea.cloud.srm.model.pj.sou.brg.entity.*;
import com.midea.cloud.srm.model.pj.sou.openapi.brg.vo.order.ApiBrgSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;

/**
 * 项目式询价 - 报价行明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BrgSouOrderItemWebVO extends ApiBrgSouOrderItemVO {

    public static List<EssentialFactorVO> convertBrgFormulaInfo(
            Map<Long/* factorId */, EssentialFactor> factorMap,
            Map<Long/* factorId */, BrgSouBaseMaterialPrice> brgBaseMaterialPriceMap,
            Map<Long/* factorId */, MaterialItemAttributeRelate> materialAttrMap,
            Map<String/* fromCurrency_toCurrency */, BigDecimal> exchangeRateMap,
            String currencyCode,
            @Nullable String formulaAttrValues) {

        if (factorMap == null || factorMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<EssentialFactorVO> voList = new ArrayList<>(16);
        EssentialFactorVO vo;

        /* json反序列化  factorId  price */
        Map<Long, String> userPriceMap;
        if (formulaAttrValues != null) {
            userPriceMap = deserializationUserPrices(formulaAttrValues);
        } else {
            userPriceMap = Collections.emptyMap();
        }

        /* 填充数据 */
        BrgSouBaseMaterialPrice baseMaterialPrice;
        MaterialItemAttributeRelate materialAttrRelate;
        for (Map.Entry<Long/* factorId */, EssentialFactor> factorEntry : factorMap.entrySet()) {
            vo = new EssentialFactorVO();
            BeanUtils.copyProperties(factorEntry.getValue(), vo);

            /* 1. 基价 */
            switch (vo.getEssentialFactorFrom()) {
                //基价
                case "BASE_MATERIAL_PRICE":
                    if (brgBaseMaterialPriceMap != null) {
                        baseMaterialPrice = brgBaseMaterialPriceMap.get(factorEntry.getKey());
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
                    //物料属性
                case "MATERIAL_MAIN_DATA":
                    if (materialAttrMap != null) {
                        materialAttrRelate = materialAttrMap.get(factorEntry.getKey());
                        if (materialAttrRelate != null) {
                            vo.setValue(materialAttrRelate.getAttributeValue());
                        }
                    }
                    break;
                    //用户报价
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
     * @param formulaAttrValues {@link BrgSouOrderItem#getFormulaResult}
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
