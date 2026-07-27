package com.midea.cloud.srm.model.pj.sou.mqlapi.inq.vo.select;

import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 * 供应商评选-查看目标价视图对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MqlInqSouItemTargetPriceWebVO extends SouItem {

    /** @see InqSouItem#getNotaxTargetPrice */
    @ApiModelProperty("未税目标价（报价阶段设定目标价时才使用）")
    private BigDecimal notaxTargetPrice;

    /** @see InqSouItem#getItemType */
    @ApiModelProperty("行类型")
    private String itemType;

    public static List<MqlInqSouItemTargetPriceWebVO> convert(List<SouItem> souItemList, List<InqSouItem> inqSouItemList) {
        Map<Long, InqSouItem> inqSouItemMap = new HashMap<>(50);
        if (!CollectionUtils.isEmpty(inqSouItemList)) {
            inqSouItemMap = inqSouItemList.stream()
                    .collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity(), (k1, k2) -> k1));
        }

        List<MqlInqSouItemTargetPriceWebVO> inqSouItemTargetPriceVos = new ArrayList<>();

        for (SouItem souItem : souItemList) {
            MqlInqSouItemTargetPriceWebVO inqSouItemTargetPriceVO = new MqlInqSouItemTargetPriceWebVO();
            BeanUtils.copyProperties(souItem, inqSouItemTargetPriceVO);
            Long souItemId = inqSouItemTargetPriceVO.getSouItemId();
            InqSouItem inqSouItem = inqSouItemMap.get(souItemId);

            if (Objects.nonNull(inqSouItem)) {
                inqSouItemTargetPriceVO.setNotaxTargetPrice(inqSouItem.getNotaxTargetPrice());
                inqSouItemTargetPriceVO.setItemType(inqSouItem.getItemType());
            }
            inqSouItemTargetPriceVos.add(inqSouItemTargetPriceVO);
        }

        return inqSouItemTargetPriceVos;
    }
}
