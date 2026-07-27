package com.midea.cloud.srm.sou.expert.spi.event.greenapprovalunpass;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertApplyUnPassDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 寻源 - 专家库 - 专家申请绿色通道审批未通过回调上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertGreenApprovalUnPassContext extends SdkPluginContext {

    /** @see ExtSouExpertApply#getExpertApplyId */
    @ApiModelProperty("专家申请ID")
    private ExtSouExpertApplyUnPassDTO param;

    public ExtSouExpertGreenApprovalUnPassContext(ExtSouExpertApplyUnPassDTO param) {
        this.param = param;
    }

}
