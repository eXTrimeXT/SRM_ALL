package com.midea.cloud.srm.sou.expert.spi.event.greenapprovalpass;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 寻源 - 专家库 - 专家申请绿色通道审批通过回调上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertGreenApprovalPassContext extends SdkPluginContext {

    /** @see ExtSouExpertApply#getExpertApplyId */
    @ApiModelProperty("专家申请ID")
    private Long expertApplyId;

    @ApiModelProperty("专家申请信息(IExtSouExpertApplyCallbackPassPlugin#judgeApplyCallbackPassAuth环节填补)")
    private ExtSouExpertApply expertApply;

    public ExtSouExpertGreenApprovalPassContext(long expertApplyId) {
        this.expertApplyId = expertApplyId;
    }

}
