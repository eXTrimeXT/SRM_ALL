package com.midea.cloud.srm.sou.expert.spi.event.changeapprovalsubmit;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 寻源 - 专家库 - 专家变更审批提交回调上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertChangeApprovalSubmitContext extends SdkPluginContext {

    /** @see ExtSouExpertApply#getExpertApplyId */
    @ApiModelProperty("专家申请ID")
    private Long expertApplyId;

    public ExtSouExpertChangeApprovalSubmitContext(long expertApplyId) {
        this.expertApplyId = expertApplyId;
    }

}
