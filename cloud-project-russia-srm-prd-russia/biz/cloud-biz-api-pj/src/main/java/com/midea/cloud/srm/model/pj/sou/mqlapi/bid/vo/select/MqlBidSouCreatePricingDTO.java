package com.midea.cloud.srm.model.pj.sou.mqlapi.bid.vo.select;

import com.midea.cloud.srm.model.inq.price.entity.ApprovalHeader;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalItemPayment;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouItem;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouProject;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select.MqlSouCreatePricingDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select.MqlSouCreatePricingItemDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 招投标MQL - 寻源转价格目录
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/04/04
 */
@Data
public class MqlBidSouCreatePricingDTO {

    public static void convertApiVO(MqlSouCreatePricingDTO pricingDTO,
                                    BidSouProject bidProject,
                                    Map<Long/* souItemId */, BidSouItem> bidItemMap,
                                    Map<Long/* orderItemId */, BidSouOrderItem> bidOrderItemMap,
                                    Map<Long/* orderItemId */, List<BidSouOrderItemPayment>> paymentMap) {
        // 1: 基本信息
        convertApprovalHeader(pricingDTO.getApprovalHeader(), bidProject);
        // 2: 中标行
        convertApprovalItems(pricingDTO.getApprovalBiddingItemList(), bidItemMap, bidOrderItemMap, paymentMap);
    }

    private static void convertApprovalHeader(ApprovalHeader approval, BidSouProject bidProject) {
        // 寻源方式
        approval.setSourceType("BIDING");
        // 汇率类型
        approval.setExchangeRateType(bidProject.getExchangeRateType());
        // 汇率转换日期
        approval.setCurrencyExchangeDate(bidProject.getCurrencyExchangeDate());
    }

    private static void convertApprovalItems(List<MqlSouCreatePricingItemDTO> approvalItemList,
                                             Map<Long/* souItemId */, BidSouItem> bidItemMap,
                                             Map<Long/* orderItemId */, BidSouOrderItem> bidOrderItemMap,
                                             Map<Long/* orderItemId */, List<BidSouOrderItemPayment>> paymentMap) {
        approvalItemList.forEach(approvalItem -> {
            BidSouItem bidItem = bidItemMap.get(approvalItem.getSourceRequirementItemId());
            BidSouOrderItem bidOrderItem = bidOrderItemMap.get(approvalItem.getSourceQuoteItemId());
            List<BidSouOrderItemPayment> payments = paymentMap.get(approvalItem.getSourceQuoteItemId());

            // 寻源类型
            approvalItem.setSourceType("BIDING");
            // 价格类型
            approvalItem.setPriceType(bidItem.getPriceType());
            // 到货地点
            approvalItem.setArrivalPlace(bidItem.getDeliveryPlace());
            // 公式ID
            approvalItem.setFormulaId(bidItem.getFormulaId());
            // 公式名称
            approvalItem.setFormulaName(bidItem.getFormulaName());
            // 公式值
            approvalItem.setFormulaValue(bidItem.getFormulaValue());
            // 供应商填写的公式报价
            approvalItem.setFormulaAttrValues(bidOrderItem.getFormulaResult());
            // 配额分配类型(略)
            // 配额比例(略)
            // L/T
            approvalItem.setLAndT(bidOrderItem.getLeadTime());
            // 最小起订量
            String mqo = StringUtils.trimToNull(bidOrderItem.getMqo());
            if (mqo != null) {
                try {
                    approvalItem.setMinOrderQuantity(new BigDecimal(mqo));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("最小订单量不是数字，无法转价格审批单:" + mqo);
                }
            }
            // 贸易术语
            approvalItem.setTradeTerm(bidItem.getTradeTerm());
            // 保质期
            approvalItem.setWarrantyPeriod(bidOrderItem.getWarrantyPeriod() != null ? new BigDecimal(bidOrderItem.getWarrantyPeriod()) : null);
            // 处理账期信息
            {
                List<ApprovalItemPayment> paymentList = new ArrayList<>(payments.size());
                payments.forEach(inqPayment -> {
                    ApprovalItemPayment payment = new ApprovalItemPayment();
                    BeanUtils.copyProperties(inqPayment, payment);

                    paymentList.add(payment);
                });
                approvalItem.setPaymentList(paymentList);
            }
        });
    }

}