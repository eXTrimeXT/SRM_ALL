package com.midea.cloud.srm.sou.expert.spi.event.upgradeapprovalsubmit;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 寻源 - 专家库 - 专家升级审批提交回调上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertUpgradeApprovalSubmitContext extends SdkPluginContext {

    /** @see ExtSouExpertApply#getExpertApplyId */
    @ApiModelProperty("专家申请ID")
    private Long expertApplyId;

    public ExtSouExpertUpgradeApprovalSubmitContext(long expertApplyId) {
        this.expertApplyId = expertApplyId;
    }

}
