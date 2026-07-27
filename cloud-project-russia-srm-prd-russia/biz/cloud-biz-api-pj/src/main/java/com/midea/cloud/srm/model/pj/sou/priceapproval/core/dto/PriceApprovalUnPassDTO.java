package com.midea.cloud.srm.model.pj.sou.priceapproval.core.dto;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 价格审批单 - 审批未通过回调参数
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/08/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PriceApprovalUnPassDTO extends BaseObjectX {

    /** @see PriceApproval#getApprovalId */
    @ApiModelProperty("价格审批单ID")
    private Long approvalId;

    /** @see PriceApproval#getApprovalStatus */
    @ApiModelProperty("立项审批状态")
    private String approvalStatus;

    public void formatParams() {
        if (approvalId == null) {
            throw new IllegalArgumentException("缺少approvalId参数");
        }
        if (approvalStatus == null) {
            throw new IllegalArgumentException("缺少approvalStatus参数");
        }
        switch (approvalStatus) {
            //已驳回
            case "REJECTED":
//                已撤回
            case "WITHDRAW":
//                已废弃
            case "ABANDONED":
//                已通过
            case "APPROVED":
                break;
            default:
                throw new IllegalArgumentException("错误的审批未通过接口调用:错误参数" + approvalStatus);
        }
    }

}
