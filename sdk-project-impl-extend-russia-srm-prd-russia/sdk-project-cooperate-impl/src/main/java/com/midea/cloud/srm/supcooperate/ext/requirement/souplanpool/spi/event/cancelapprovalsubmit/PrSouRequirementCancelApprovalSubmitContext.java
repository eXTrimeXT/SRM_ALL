package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalsubmit;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 招标计划 - 计划取消 - 审批提交回调上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouRequirementCancelApprovalSubmitContext extends SdkPluginContext {

    /** @see ExtPrSouRequirementCancel#getRequirementCancelId */
    @ApiModelProperty("入参: 取消单据ID")
    private Long requirementCancelId;

    public PrSouRequirementCancelApprovalSubmitContext(Long requirementCancelId) {
        this.requirementCancelId = requirementCancelId;
    }

}
