package com.midea.cloud.srm.model.pj.sou.openapi.inq.dto.select;

import com.midea.cloud.srm.model.inq.price.entity.ApprovalHeader;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalItemPayment;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouCreatePricingDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouCreatePricingItemDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 简易询价openAPI - 寻源转价格目录
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/02/15
 */
@Data
public class ApiInqSouCreatePricingDTO {

    public static void convertApiVO(ApiSouCreatePricingDTO pricingDTO,
                                    InqSouProject inqProject,
                                    Map<Long/* souItemId */, InqSouItem> inqItemMap,
                                    Map<Long/* orderItemId */, InqSouOrderItem> inqOrderItemMap,
                                    Map<Long/* orderItemId */, List<InqSouOrderItemPayment>> paymentMap) {
        // 1: 基本信息
        convertApprovalHeader(pricingDTO.getApprovalHeader(), inqProject);
        // 2: 中标行
        convertApprovalItems(pricingDTO.getApprovalBiddingItemList(), inqItemMap, inqOrderItemMap, paymentMap);
    }

    private static void convertApprovalHeader(ApprovalHeader approval, InqSouProject inqProject) {
        // 寻源方式
        approval.setSourceType("INQUIRY");
        // 汇率类型
        approval.setExchangeRateType(inqProject.getExchangeRateType());
        // 汇率转换日期
        approval.setCurrencyExchangeDate(inqProject.getCurrencyExchangeDate());
    }

    private static void convertApprovalItems(List<ApiSouCreatePricingItemDTO> approvalItemList,
                                             Map<Long/* souItemId */, InqSouItem> inqItemMap,
                                             Map<Long/* orderItemId */, InqSouOrderItem> inqOrderItemMap,
                                             Map<Long/* orderItemId */, List<InqSouOrderItemPayment>> paymentMap) {
        approvalItemList.forEach(approvalItem -> {
            InqSouItem inqItem = inqItemMap.get(approvalItem.getSourceRequirementItemId());
            InqSouOrderItem inqOrderItem = inqOrderItemMap.get(approvalItem.getSourceQuoteItemId());
            List<InqSouOrderItemPayment> payments = paymentMap.get(approvalItem.getSourceQuoteItemId());

            // 寻源类型
            approvalItem.setSourceType("INQUIRY");
            // 价格类型
            approvalItem.setPriceType(inqItem.getItemType());
            // 到货地点(略)
            // 公式ID
            approvalItem.setFormulaId(inqItem.getFormulaId());
            // 公式名称
            approvalItem.setFormulaName(inqItem.getFormulaName());
            // 公式值
            approvalItem.setFormulaValue(inqItem.getFormulaValue());
            // 供应商填写的公式报价
            approvalItem.setFormulaAttrValues(inqOrderItem.getFormulaAttrValues());
            // 配额分配类型(略)
            // 配额比例(略)
            // L/T(略)
            // 最小起订量(略)
            // 贸易术语(略)
            // 保质期(略)
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
