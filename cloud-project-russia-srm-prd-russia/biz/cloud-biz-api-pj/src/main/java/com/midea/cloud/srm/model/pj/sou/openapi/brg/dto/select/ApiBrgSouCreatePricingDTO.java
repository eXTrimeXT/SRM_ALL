package com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.select;

import com.midea.cloud.srm.model.inq.price.entity.ApprovalHeader;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalItemPayment;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouItem;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouCreatePricingDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouCreatePricingItemDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 项目式询价openAPI - 寻源转价格目录
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/02/15
 */
@Data
public class ApiBrgSouCreatePricingDTO {

    public static void convertApiVO(ApiSouCreatePricingDTO pricingDTO,
                                    BrgSouProject brgProject,
                                    Map<Long/* souItemId */, BrgSouItem> brgItemMap,
                                    Map<Long/* orderItemId */, BrgSouOrderItem> brgOrderItemMap,
                                    Map<Long/* orderItemId */, List<BrgSouOrderItemPayment>> paymentMap) {
        // 1: 基本信息
        convertApprovalHeader(pricingDTO.getApprovalHeader(), brgProject);
        // 2: 中标行
        convertApprovalItems(pricingDTO.getApprovalBiddingItemList(), brgItemMap, brgOrderItemMap, paymentMap);
    }

    private static void convertApprovalHeader(ApprovalHeader approval, BrgSouProject brgProject) {
        // 寻源方式
        approval.setSourceType("BARGAIN");
        // 汇率类型
        approval.setExchangeRateType(brgProject.getExchangeRateType());
        // 汇率转换日期
        approval.setCurrencyExchangeDate(brgProject.getCurrencyExchangeDate());
    }

    private static void convertApprovalItems(List<ApiSouCreatePricingItemDTO> approvalItemList,
                                             Map<Long/* souItemId */, BrgSouItem> brgItemMap,
                                             Map<Long/* orderItemId */, BrgSouOrderItem> brgOrderItemMap,
                                             Map<Long/* orderItemId */, List<BrgSouOrderItemPayment>> paymentMap) {
        approvalItemList.forEach(approvalItem -> {
            BrgSouItem brgItem = brgItemMap.get(approvalItem.getSourceRequirementItemId());
            BrgSouOrderItem brgOrderItem = brgOrderItemMap.get(approvalItem.getSourceQuoteItemId());
            List<BrgSouOrderItemPayment> payments = paymentMap.get(approvalItem.getSourceQuoteItemId());

            // 寻源类型
            approvalItem.setSourceType("BARGAIN");
            // 价格类型
            approvalItem.setPriceType(brgItem.getPriceType());
            // 到货地点
            approvalItem.setArrivalPlace(brgItem.getDeliveryPlace());
            // 公式ID
            approvalItem.setFormulaId(brgItem.getFormulaId());
            // 公式名称
            approvalItem.setFormulaName(brgItem.getFormulaName());
            // 公式值
            approvalItem.setFormulaValue(brgItem.getFormulaValue());
            // 供应商填写的公式报价
            approvalItem.setFormulaAttrValues(brgOrderItem.getFormulaResult());
            // 配额分配类型(略)
            // 配额比例(略)
            // L/T
            approvalItem.setLAndT(brgOrderItem.getLeadTime());
            // 最小起订量
            String mqo = StringUtils.trimToNull(brgOrderItem.getMqo());
            if (mqo != null) {
                try {
                    approvalItem.setMinOrderQuantity(new BigDecimal(mqo));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("最小订单量不是数字，无法转价格审批单:" + mqo);
                }
            }
            // 贸易术语
            approvalItem.setTradeTerm(brgItem.getTradeTerm());
            // 保质期
            approvalItem.setWarrantyPeriod(brgOrderItem.getWarrantyPeriod() != null ? new BigDecimal(brgOrderItem.getWarrantyPeriod()) : null);
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
