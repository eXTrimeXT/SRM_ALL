package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.midea.cloud.srm.model.inq.price.entity.ApprovalBiddingItem;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalItemPayment;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalLadderPrice;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源openAPI - 寻源转价格审批单
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/02/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouCreatePricingItemDTO extends ApprovalBiddingItem {

    /** 阶梯价信息 */
    private List<ApprovalLadderPrice> ladderPriceList;
    /** 账期信息 */
    private List<ApprovalItemPayment> paymentList;

}
