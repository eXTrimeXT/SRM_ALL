package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select;

import com.midea.cloud.srm.model.bid.enums.BiddingApprovalStatus;
import com.midea.cloud.srm.model.competition.purchaser.projectmanagement.initiating.entity.Competition;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源核心 - 价格审批单状态回调
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/28
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouSelectChangePricingResultDTO extends BaseObjectX {

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("价格审批单的目标状态")
    private BiddingApprovalStatus approvalStatus;

    /**
     * 价格审批单是否被删除了
     * PS: isDeleted、approvalStatus属于互斥属性
     *     如果价格审批单被删除，那么 isDeleted = true，approvalStatus应该设置为空
     */
    @ApiModelProperty("价格审批单是否被删除了")
    private boolean isDeleted;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
    }

}
