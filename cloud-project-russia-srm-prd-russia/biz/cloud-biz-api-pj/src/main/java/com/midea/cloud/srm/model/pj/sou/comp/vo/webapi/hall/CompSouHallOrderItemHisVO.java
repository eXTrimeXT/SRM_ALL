package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.hall;

import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouCurrency;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouOrderItemHis;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 竞价 - 供应商报价信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompSouHallOrderItemHisVO extends BaseObjectX {

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

    @ApiModelProperty("原币未税总价")
    private BigDecimal orderNotaxTotalPrice;

    /** @see CompSouOrderItemHis#getSubmitTime */
    @ApiModelProperty("报价时间")
    private Date submitTime;

    /** @see CompSouOrderItemHis#getSubmitByIp */
    @ApiModelProperty("IP")
    private String submitByIp;

    /**
     * 便捷方法
     */
    public static List<CompSouHallOrderItemHisVO> convert(SouItem souItem,
                                                          List<CompSouOrderItemHis> orderItemHisList,
                                                          List<SouVendor> vendorList,
                                                          CompSouCurrency currency) {
        if (orderItemHisList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long/* vendorId */, SouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 按照创建时间降序
        orderItemHisList.sort(Comparator.comparing(CompSouOrderItemHis::getSubmitTime).reversed());

        List<CompSouHallOrderItemHisVO> voList = new ArrayList<>(orderItemHisList.size());
        for (CompSouOrderItemHis orderItemHis : orderItemHisList) {
            CompSouHallOrderItemHisVO vo = new CompSouHallOrderItemHisVO();
            voList.add(vo);

            // 供应商
            SouVendor vendor = vendorMap.get(orderItemHis.getVendorId());
            BeanUtils.copyProperties(vendor, vo);
            // 原币未税报价
            vo.orderNotaxPrice = orderItemHis.getOrderItemInfo().getOrderNotaxPrice();
            // 原币未税总价
            vo.orderNotaxTotalPrice = orderItemHis.getOrderItemInfo().getOrderNotaxPrice().multiply(souItem.getRequireQuantity())
                    .setScale(currency.getPricePrecision(), RoundingMode.HALF_UP)
                    .stripTrailingZeros();
            // 报价时间
            vo.submitTime = orderItemHis.getSubmitTime();
            // IP
            vo.submitByIp = orderItemHis.getSubmitByIp();
        }

        return voList;
    }

}
