package com.midea.cloud.srm.model.pj.sou.priceapproval.core.dto;

import com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity.PriceApprovalItem;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity.PriceApprovalItemLadder;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.entity.PriceApprovalItemPayment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 价格审批单 - 中标行
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PriceApprovalItemDTO extends PriceApprovalItem {

    @ApiModelProperty("阶梯价")
    private List<PriceApprovalItemLadder> ladderPriceList;

    @ApiModelProperty("付款条款")
    private List<PriceApprovalItemPayment> paymentList;

}
