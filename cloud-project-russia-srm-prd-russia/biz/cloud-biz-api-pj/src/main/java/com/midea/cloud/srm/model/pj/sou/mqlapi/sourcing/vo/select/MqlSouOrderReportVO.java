package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.select;

import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.select.MqlSouOrderItemReportVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.select.MqlSouOrderPriceNodeVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.select.MqlSouOrderVendorReportVO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.vo.select.MqlSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
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
 * 寻源报价报表
 *
 * @author zhangwk12@meicloud.com
 * @since 203/03/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouOrderReportVO extends BaseObjectX {

    @ApiModelProperty("物料需求维度信息(以及中标供应商对该物料的最终报价)")
    private List<MqlSouOrderItemReportVO> souItemInfos;

    @ApiModelProperty("中标供应商的总报价信息")
    private List<MqlSouOrderVendorReportVO> vendorOrderInfos;
/**    itemId vendorName */
    @ApiModelProperty("物料历史价格信息(每个物料所属列表，按时间升序排列)")
    private Map<String, Map<String, List<MqlSouOrderPriceNodeVO>>> priceNodes;

    /**
     * 转换方法
     * @param souProject 竞价单
     * @param souItemList 需求物料信息
     * @param orderItemList 中标的评选信息
     * @param orderMap 中标的报价头信息
     * @param vendorMap 邀请供应商信息
     */
    public static MqlSouOrderReportVO convertMqlVO(SouProject souProject,
                                                   List<SouItem> souItemList,
                                                   List<SouOrderItem> orderItemList,
                                                   Map<Long/* orderId */, SouOrder> orderMap,
                                                   Map<Long/* vendorId */, SouVendor> vendorMap) {

        MqlSouOrderReportVO vo = new MqlSouOrderReportVO();
        vo.doConvertSouItemOrderInfos(souItemList, orderItemList, vendorMap);
        vo.doConvertVendorOrderInfos(souItemList, orderItemList, orderMap, vendorMap);
        vo.priceNodes = Collections.emptyMap();
        return vo;
    }

    private void doConvertSouItemOrderInfos(List<SouItem> souItemList, List<SouOrderItem> orderItemList,
                                            Map<Long/* vendorId */, SouVendor> vendorMap) {
        Map<Long/* souItemId */, List<SouOrderItem>> orderItemMap = orderItemList.stream()
                .collect(Collectors.groupingBy(SouOrderItem::getSouItemId));

        this.souItemInfos = new ArrayList<>(souItemList.size());
        MqlSouOrderItemReportVO souItemVO;
        List<SouOrderItem> orderItems;
        MqlSouSelectQueryVO vo;
        SouVendor vendor;
        for (SouItem souItem : souItemList) {
            orderItems = orderItemMap.get(souItem.getSouItemId());
            // 可能有物料没有供应商中标
            if (orderItems == null) {
                continue;
            }

            souItemVO = new MqlSouOrderItemReportVO();
            souItemVO.setSouItem(souItem);
            souItemVO.setOrderInfos(new ArrayList<>(orderItems.size()));
            for (SouOrderItem orderItem : orderItems) {
                vendor = vendorMap.get(orderItem.getVendorId());
                vo = new MqlSouSelectQueryVO();
                BeanUtils.copyProperties(souItem, vo);
                BeanUtils.copyProperties(orderItem, vo);
                vo.setVendorId(vendor.getVendorId());

                souItemVO.getOrderInfos().add(vo);
            }

            this.souItemInfos.add(souItemVO);
        }
    }

    private void doConvertVendorOrderInfos(List<SouItem> souItemL,
                                           List<SouOrderItem> orderItemList,
                                           Map<Long/* orderId */, SouOrder> orderMap,
                                           Map<Long/* vendorId */, SouVendor> vendorMap) {
        this.vendorOrderInfos = new ArrayList<>(orderItemList.size());
        Map<Long/* vendorId */, MqlSouOrderVendorReportVO> vendorOrderMap = new HashMap<>(16);
        Map<Long/* reqLineId */, SouItem> souItemMap = souItemL.stream()
                .collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));

        MqlSouOrderVendorReportVO vo;
        SouItem sou;
        SouVendor vendor;
        SouOrder order;
        for (SouOrderItem orderItem : orderItemList) {
            vo = vendorOrderMap.get(orderItem.getVendorId());
            sou = souItemMap.get(orderItem.getSouItemId());
            order = orderMap.get(orderItem.getOrderId());
            if (vo == null) {
                vo = new MqlSouOrderVendorReportVO();
                this.vendorOrderInfos.add(vo);
                vendorOrderMap.put(orderItem.getVendorId(), vo);

                vendor = vendorMap.get(orderItem.getVendorId());
                vo.setVendorId(vendor.getVendorId());
                vo.setVendorCode(vendor.getVendorCode());
                vo.setVendorName(vendor.getVendorName());
                vo.setStandardNotaxTotalPrice(order.getStandardNotaxTotalPrice());
                vo.setStandardTaxTotalPrice(order.getStandardTaxTotalPrice());
                vo.setWinNotaxTotalPrice(BigDecimal.ZERO);
                vo.setWinTaxTotalPrice(BigDecimal.ZERO);
            }
            // 中标总金额(未税)
            vo.setWinNotaxTotalPrice(vo.getWinNotaxTotalPrice().add(orderItem.getWinAmount().multiply(orderItem.getStandardNotaxPrice())));
            // 中标总金额(含税)
            vo.setWinTaxTotalPrice(vo.getWinTaxTotalPrice().add(orderItem.getWinAmount().multiply(orderItem.getStandardTaxPrice())));
        }
    }

}
