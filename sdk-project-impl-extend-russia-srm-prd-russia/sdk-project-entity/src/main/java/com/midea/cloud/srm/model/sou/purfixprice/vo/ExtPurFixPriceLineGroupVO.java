package com.midea.cloud.srm.model.sou.purfixprice.vo;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLine;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
public class ExtPurFixPriceLineGroupVO {

    @ApiModelProperty("区域集合")
    private Map<String, String> areas;

    @ApiModelProperty("物料集合")
    private PageInfo<ExtPurFixPriceLineGroupDetailVO> itemList;

    public static ExtPurFixPriceLineGroupVO empty() {
        ExtPurFixPriceLineGroupVO vo = new ExtPurFixPriceLineGroupVO();
        vo.setAreas(Collections.emptyMap());
        vo.setItemList(new PageInfo<>());
        return vo;
    }

    public static ExtPurFixPriceLineGroupVO build(Map<String/* code */, String/* name */> areaDictMap,
                                                  List<ExtPurFixPriceLine> priceLineList,
                                                  PageInfo<Long> pageInfo) {
        Map<String/* code */, String/* name */> argAreaDictMap = new HashMap<>(areaDictMap.size()); {
            priceLineList.forEach(pl -> {
                String areaName = areaDictMap.get(pl.getArea());
                if (areaName != null) {
                    argAreaDictMap.put(pl.getArea(), areaName);
                }
            });
        }

        ExtPurFixPriceLineGroupVO vo = new ExtPurFixPriceLineGroupVO();
        vo.setAreas(argAreaDictMap);

        List<ExtPurFixPriceLineGroupDetailVO> itemList = new ArrayList<>(pageInfo.getList().size());
        priceLineList.stream().collect(Collectors.groupingBy(ExtPurFixPriceLine::getItemId)).forEach((itemId, priceLines) -> {
            // 对物料进行分组
            ExtPurFixPriceLineGroupDetailVO detail = new ExtPurFixPriceLineGroupDetailVO();
            itemList.add(detail);

            BeanUtils.copyProperties(priceLines.get(0), detail);

            // 处理区域供应商报价详情
            detail.setVendorOrderItemList(new HashMap<>(15));
            Map<String, List<ExtPurFixPriceLine>> tempMap = priceLines.stream().collect(Collectors.groupingBy(ExtPurFixPriceLine::getArea));
            detail.setVendorOrderItemList(new HashMap<>(tempMap.size()));
            tempMap.forEach((area, pls) -> {
                if (CollectionUtils.isNotEmpty(pls)) {
                    detail.getVendorOrderItemList().put(area, pls.get(0));
                }
            });
        });
        PageInfo<ExtPurFixPriceLineGroupDetailVO> itemPage = new PageInfo<>(itemList);
        itemPage.setTotal(pageInfo.getTotal());
        itemPage.setPageSize(pageInfo.getPageSize());
        itemPage.setPageNum(pageInfo.getPageNum());
        vo.setItemList(itemPage);

        return vo;
    }

}
