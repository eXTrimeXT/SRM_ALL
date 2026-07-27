package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select;

import com.midea.cloud.srm.model.inq.price.entity.ApprovalBiddingItem;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalItemPayment;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalLadderPrice;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * MQL - 寻源转价格审批单
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouCreatePricingItemDTO extends ApprovalBiddingItem {

    /** 阶梯价信息 */
    private List<ApprovalLadderPrice> ladderPriceList;
    /** 账期信息 */
    private List<ApprovalItemPayment> paymentList;

}
