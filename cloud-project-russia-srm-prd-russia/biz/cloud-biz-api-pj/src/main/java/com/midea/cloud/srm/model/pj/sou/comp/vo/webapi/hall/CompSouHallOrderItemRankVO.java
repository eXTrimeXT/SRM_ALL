package com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.hall;

import com.midea.cloud.srm.model.competition.utils.DecimalUtil;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouCurrency;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 竞价 - 具体物料维度下的最新实时排名
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompSouHallOrderItemRankVO extends BaseObjectX {

    /** @see SouOrderItem#getRanking */
    @ApiModelProperty("排名")
    private Integer ranking;

    /** @see SouOrderItem#getOrderNotaxPrice */
    @ApiModelProperty("原币未税报价")
    private BigDecimal orderNotaxPrice;

    @ApiModelProperty("原币未税总金额")
    private BigDecimal orderNotaxTotalPrice;

    /** @see SouOrderItem#getVendorId */
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouVendor#getVendorCode */
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("升降幅比例(供应商的报价相比于起始价的情况)")
    private BigDecimal pricePercent;

    /**
     * 便捷方法
     */
    public static List<CompSouHallOrderItemRankVO> convert(SouProject souProject,
                                                           SouItem souItem,
                                                           CompSouItem compSouItem,
                                                           List<SouOrderItem> souOrderItemList,
                                                           List<SouVendor> vendorList,
                                                           CompSouCurrency currency) {
        if (souOrderItemList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long/* vendorId */, SouVendor> vendorMap = vendorList.stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        souOrderItemList.sort((a, b) -> {
            // 按照价格排序(如果是正向，则价格高的排前面;否则价格低的排前面)
            switch (souProject.getScoreRuleType()) {
                case MAX_PRICE: // 合理高价
                    return b.getOrderNotaxPrice().compareTo(a.getOrderNotaxPrice());
                case MIN_PRICE: // 合理低价
                case COMPOSITE_PRICE: // 综合
                    return a.getOrderNotaxPrice().compareTo(b.getOrderNotaxPrice());
                default:
                    return 0;
            }
        });

        List<CompSouHallOrderItemRankVO> voList = new ArrayList<>(souOrderItemList.size());
        int index = 1;
        for (SouOrderItem souOrderItem : souOrderItemList) {
            CompSouHallOrderItemRankVO vo = new CompSouHallOrderItemRankVO();
            voList.add(vo);

            // 排名
            vo.ranking = index++;
            // 原币未税报价
            vo.orderNotaxPrice = souOrderItem.getOrderNotaxPrice();
            // 原币未税总价
            vo.orderNotaxTotalPrice = souOrderItem.getOrderNotaxPrice().multiply(souItem.getRequireQuantity())
                    .setScale(currency.getPricePrecision(), RoundingMode.HALF_UP)
                    .stripTrailingZeros();
            // 供应商
            SouVendor vendor = vendorMap.get(souOrderItem.getVendorId());
            BeanUtils.copyProperties(vendor, vo);
            // 升降幅【(原币未税报价 - 原币起始价) / 原币起始价】
            vo.pricePercent = souOrderItem.getOrderNotaxPrice().subtract(compSouItem.getStartOrderNotaxPrice())
                    .divide(compSouItem.getStartOrderNotaxPrice(), currency.getPricePrecision(), RoundingMode.HALF_UP)
                    .multiply(DecimalUtil.B_100)
                    .stripTrailingZeros().abs();
        }

        return voList;
    }

}
