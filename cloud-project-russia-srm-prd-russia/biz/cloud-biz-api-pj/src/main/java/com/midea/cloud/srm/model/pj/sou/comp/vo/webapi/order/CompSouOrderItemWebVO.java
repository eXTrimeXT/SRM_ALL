package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.midea.cloud.srm.model.base.formula.entity.EssentialFactor;
import com.midea.cloud.srm.model.base.formula.vo.EssentialFactorVO;
import com.midea.cloud.srm.model.base.material.MaterialItemAttributeRelate;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouBaseMaterialPrice;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;

/**
 * 竞价 - 报价行明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompSouOrderItemWebVO extends ApiCompSouOrderItemVO {

    @ApiModelProperty("本次推荐报价(原币未税)")
    private BigDecimal recommendOrderNotaxPrice;
    @ApiModelProperty("本次推荐报价(原币含税)")
    private BigDecimal recommendOrderTaxPrice;

    @SuppressWarnings("rawtypes")
    public static List<CompSouOrderItemWebVO> convertCompVO(List<ApiSouOrderItemVO> voList) {
        if (voList.isEmpty()) { return Collections.emptyList(); }
        List<CompSouOrderItemWebVO> compVOList; {
            if (voList instanceof Page) {
                compVOList = new Page<>();
                ((Page)compVOList).setTotal(((Page)voList).getTotal());
                ((Page)compVOList).setPageNum(((Page)voList).getPageNum());
                ((Page)compVOList).setPageSize(((Page)voList).getPageSize());
            } else {
                compVOList = new ArrayList<>(voList.size());
            }
        }
        voList.forEach(vo -> compVOList.add(SouObjectXUtil.convertTargetObj(vo, CompSouOrderItemWebVO.class)));

        return compVOList;
    }

    public static List<EssentialFactorVO> convertCompFormulaInfo(
            Map<Long/* factorId */, EssentialFactor> factorMap,
            Map<Long/* factorId */, CompSouBaseMaterialPrice> compBaseMaterialPriceMap,
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
        CompSouBaseMaterialPrice baseMaterialPrice;
        MaterialItemAttributeRelate materialAttrRelate;
        for (Map.Entry<Long/* factorId */, EssentialFactor> factorEntry : factorMap.entrySet()) {
            vo = new EssentialFactorVO();
            BeanUtils.copyProperties(factorEntry.getValue(), vo);

            /* 1. 基价 */
            switch (vo.getEssentialFactorFrom()) {
                //基价
                case "BASE_MATERIAL_PRICE":
                    if (compBaseMaterialPriceMap != null) {
                        baseMaterialPrice = compBaseMaterialPriceMap.get(factorEntry.getKey());
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
     * @param formulaAttrValues {@link CompSouOrderItem#getFormulaResult}
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
