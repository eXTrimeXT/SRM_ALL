package com.midea.cloud.srm.sou.expert.spi.event.quiteexpert;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertQuiteDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 寻源 - 专家库 - 专家退出上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertQuiteContext extends SdkPluginContext {

    @ApiModelProperty("入参: 专家退出信息")
    private ExtSouExpertQuiteDTO param;

    @ApiModelProperty("专家(IExtSouExpertQuitePlugin#judgeQuiteExpertAuth环节填补)")
    private ExtSouExpert expert;

    public ExtSouExpertQuiteContext(ExtSouExpertQuiteDTO param) {
        this.param = param;
    }

}
