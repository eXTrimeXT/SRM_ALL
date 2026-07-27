package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.hall;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouOrderItemHis;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 竞价 - 价格走势
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompSouHallOrderItemPriceTrendVO extends BaseObjectX {

    /** @see CompSouOrderItemHis#getVendorId */
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouVendor#getVendorCode */
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /** @see CompSouOrderItemHis#getOrderItemInfo */
    @ApiModelProperty("原币未税报价")
    private BigDecimal orderNotaxPrice;

    /** @see CompSouOrderItemHis#getSubmitTime */
    @ApiModelProperty("报价时间")
    private Date submitTime;

    /**
     * 便捷方法
     */
    public static List<CompSouHallOrderItemPriceTrendVO> convert(List<CompSouOrderItemHis> orderItemHisList, List<SouVendor> vendorList) {
        if (orderItemHisList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long/* vendorId */, SouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 按照创建时间升序排列
        orderItemHisList.sort(Comparator.comparing(CompSouOrderItemHis::getSubmitTime));

        List<CompSouHallOrderItemPriceTrendVO> voList = new ArrayList<>(orderItemHisList.size());
        for (CompSouOrderItemHis compOrderItem : orderItemHisList) {
            CompSouHallOrderItemPriceTrendVO vo = new CompSouHallOrderItemPriceTrendVO();
            voList.add(vo);

            // 供应商
            SouVendor vendor = vendorMap.get(compOrderItem.getVendorId());
            BeanUtils.copyProperties(vendor, vo);
            // 原币未税报价
            vo.orderNotaxPrice = compOrderItem.getOrderItemInfo().getOrderNotaxPrice();
            // 报价时间
            vo.submitTime = compOrderItem.getSubmitTime();
        }

        return voList;
    }

}
