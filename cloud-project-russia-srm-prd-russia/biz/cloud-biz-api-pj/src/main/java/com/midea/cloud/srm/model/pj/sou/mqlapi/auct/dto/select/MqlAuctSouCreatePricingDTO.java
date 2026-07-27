package com.midea.cloud.srm.model.pj.sou.mqlapi.auct.dto.select;

import com.midea.cloud.srm.model.inq.price.entity.ApprovalHeader;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalItemPayment;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouItemPayment;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.mqlapi.auct.entity.AuctSouProject;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select.MqlSouCreatePricingDTO;
import com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select.MqlSouCreatePricingItemDTO;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author huangbf3
 */
public class MqlAuctSouCreatePricingDTO {

    public static void convertMqlVO(MqlSouCreatePricingDTO pricingDTO,
                                    AuctSouProject auctProject,
                                    Map<Long/* souItemId */, AuctSouItem> auctItemMap,
                                    Map<Long/* orderItemId */, AuctSouOrderItem> auctOrderItemMap,
                                    Map<Long/* souItemId */, List<AuctSouItemPayment>> paymentMap) {
        // 1: 基本信息
        convertApprovalHeader(pricingDTO.getApprovalHeader(), auctProject);
        // 2: 中标行
        convertApprovalItems(pricingDTO.getApprovalBiddingItemList(), auctItemMap, auctOrderItemMap, paymentMap);
    }

    private static void convertApprovalHeader(ApprovalHeader approval, AuctSouProject auctProject) {
        // 寻源方式
        approval.setSourceType("AUCT");
        // 汇率类型
//        approval.setExchangeRateType(auctProject.getExchangeRateType());
        // 汇率转换日期
//        approval.setCurrencyExchangeDate(auctProject.getCurrencyExchangeDate());
    }

    private static void convertApprovalItems(List<MqlSouCreatePricingItemDTO> approvalItemList,
                                             Map<Long/* souItemId */, AuctSouItem> auctItemMap,
                                             Map<Long/* orderItemId */, AuctSouOrderItem> auctOrderItemMap,
                                             Map<Long/* souItemId */, List<AuctSouItemPayment>> paymentMap) {
        approvalItemList.forEach(approvalItem -> {
            AuctSouItem auctItem = auctItemMap.get(approvalItem.getSourceRequirementItemId());
            AuctSouOrderItem auctOrderItem = auctOrderItemMap.get(approvalItem.getSourceQuoteItemId());
            List<AuctSouItemPayment> payments = paymentMap.get(approvalItem.getSourceRequirementItemId());

            // 寻源类型
            approvalItem.setSourceType("AUCT");
            // 价格类型
            approvalItem.setPriceType(auctItem.getPriceType());
            // 到货地点(略)
            // 公式ID(略)
            // 公式名称(略)
            // 公式值(略)
            // 供应商填写的公式报价(略)
            // 配额分配类型(略)
            // 配额比例(略)
            // L/T(略)
            // 最小起订量(略)
            // 贸易术语
            approvalItem.setTradeTerm(auctItem.getTradeTerm());
            // 保质期
            approvalItem.setWarrantyPeriod(auctItem.getWarrantyPeriod() != null ? new BigDecimal(auctItem.getWarrantyPeriod()) : null);
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
