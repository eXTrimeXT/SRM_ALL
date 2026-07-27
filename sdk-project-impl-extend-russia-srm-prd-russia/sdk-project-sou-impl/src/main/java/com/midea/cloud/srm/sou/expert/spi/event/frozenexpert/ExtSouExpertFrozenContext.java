package com.midea.cloud.srm.sou.expert.spi.event.frozenexpert;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertFrozenDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 寻源 - 专家库 - 专家冻结上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertFrozenContext extends SdkPluginContext {

    /** @see ExtSouExpert#getExpertId */
    @ApiModelProperty("入参: 专家ID")
    private ExtSouExpertFrozenDTO param;

    @ApiModelProperty("专家(IExtSouExpertFrozenPlugin#judgeFrozenExpertAuth环节填补)")
    private ExtSouExpert expert;

    public ExtSouExpertFrozenContext(ExtSouExpertFrozenDTO param) {
        this.param = param;
    }

}
