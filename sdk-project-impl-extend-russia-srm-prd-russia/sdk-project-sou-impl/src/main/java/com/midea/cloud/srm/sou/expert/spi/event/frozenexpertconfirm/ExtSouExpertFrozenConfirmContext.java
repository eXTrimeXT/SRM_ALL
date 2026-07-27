package com.midea.cloud.srm.sou.expert.spi.event.frozenexpertconfirm;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 寻源 - 专家库 - 专家确认冻结上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertFrozenConfirmContext extends SdkPluginContext {

    /** @see ExtSouExpert#getExpertId */
    @ApiModelProperty("入参: 专家ID")
    private Long expertId;

    @ApiModelProperty("专家(IExtSouExpertFrozenPlugin#judgeFrozenExpertAuth环节填补)")
    private ExtSouExpert expert;

    public ExtSouExpertFrozenConfirmContext(Long expertId) {
        this.expertId = expertId;
    }

}
