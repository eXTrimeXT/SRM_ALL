package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.changesouplan;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementHeadVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

/**
 * 招标计划池 - 计划变更上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PrSouRequirementChangePlanContext extends SdkPluginContext {

    @ApiModelProperty("入参: 计划变更数据")
    private ExtPrSouRequirementHeadDTO param;

    @ApiModelProperty("需要变更的招标计划(IPrSouRequirementChangeReqPlugin#judgeChangeReqAuth环节填补)")
    private ExtPrSouRequirementHeadVO existSouPrHead;

    @ApiModelProperty("变更好的计划数据")
    private ExtPrSouRequirementHeadDTO result;

    public PrSouRequirementChangePlanContext(ExtPrSouRequirementHeadDTO param) {
        this.param = param;
    }
    @Override
    @Nullable
    public String getSceneType() {
        return ISdkPlugin.DEFAULT_SCENE;
    }

}
