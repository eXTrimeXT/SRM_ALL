package com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.select;

import com.midea.cloud.srm.model.inq.price.entity.ApprovalHeader;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalItemPayment;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItem;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItemPayment;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouCreatePricingDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouCreatePricingItemDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 竞价openAPI - 寻源转价格目录
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/02/15
 */
@Data
public class ApiCompSouCreatePricingDTO {

    public static void convertApiVO(ApiSouCreatePricingDTO pricingDTO,
                                    CompSouProject compProject,
                                    Map<Long/* souItemId */, CompSouItem> compItemMap,
                                    Map<Long/* orderItemId */, CompSouOrderItem> compOrderItemMap,
                                    Map<Long/* souItemId */, List<CompSouItemPayment>> paymentMap) {
        // 1: 基本信息
        convertApprovalHeader(pricingDTO.getApprovalHeader(), compProject);
        // 2: 中标行
        convertApprovalItems(pricingDTO.getApprovalBiddingItemList(), compItemMap, compOrderItemMap, paymentMap);
    }

    private static void convertApprovalHeader(ApprovalHeader approval, CompSouProject compProject) {
        // 寻源方式
        approval.setSourceType("COMPETITION");
        // 汇率类型
        approval.setExchangeRateType(compProject.getExchangeRateType());
        // 汇率转换日期
        approval.setCurrencyExchangeDate(compProject.getCurrencyExchangeDate());
    }

    private static void convertApprovalItems(List<ApiSouCreatePricingItemDTO> approvalItemList,
                                             Map<Long/* souItemId */, CompSouItem> compItemMap,
                                             Map<Long/* orderItemId */, CompSouOrderItem> compOrderItemMap,
                                             Map<Long/* souItemId */, List<CompSouItemPayment>> paymentMap) {
        approvalItemList.forEach(approvalItem -> {
            CompSouItem compItem = compItemMap.get(approvalItem.getSourceRequirementItemId());
            CompSouOrderItem compOrderItem = compOrderItemMap.get(approvalItem.getSourceQuoteItemId());
            List<CompSouItemPayment> payments = paymentMap.get(approvalItem.getSourceRequirementItemId());

            // 寻源类型
            approvalItem.setSourceType("COMPETITION");
            // 价格类型
            approvalItem.setPriceType(compItem.getPriceType());
            // 到货地点
            approvalItem.setArrivalPlace(compItem.getDeliveryPlace());
            // 公式ID
            approvalItem.setFormulaId(compItem.getFormulaId());
            // 公式名称
            approvalItem.setFormulaName(compItem.getFormulaName());
            // 公式值
            approvalItem.setFormulaValue(compItem.getFormulaValue());
            // 供应商填写的公式报价
            approvalItem.setFormulaAttrValues(compOrderItem.getFormulaResult());
            // 配额分配类型(略)
            // 配额比例(略)
            // L/T(略)
            // 最小起订量(略)
            // 贸易术语
            approvalItem.setTradeTerm(compItem.getTradeTerm());
            // 保质期
            approvalItem.setWarrantyPeriod(compItem.getWarrantyPeriod() != null ? new BigDecimal(compItem.getWarrantyPeriod()) : null);
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
