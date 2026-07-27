package com.midea.cloud.srm.supcooperate.ext.requirement.pr.spi.event.createsou;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouInitDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.ExtPurchaseRequirementCreateSouDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

/**
 * 非招需求池 - 创建寻源上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/11/04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtPrRequirementCreateSouContext extends SdkPluginContext {

    @ApiModelProperty("入参: 创建寻源数据")
    private ExtPurchaseRequirementCreateSouDTO param;

    @ApiModelProperty("非招需求明细(IExtPrRequirementCreateSouPlugin#judgeCreateSouAuth环节填补)")
    private List<RequirementLine> prRequirementLineList;
    @ApiModelProperty("非招需求信息(IExtPrRequirementCreateSouPlugin#judgeCreateSouAuth环节填补)")
    private Map<Long, RequirementHead> prRequirementHeadMap;

    @ApiModelProperty("组装好的寻源单信息")
    private ApiSouInitDTO souInitInfo;

    @ApiModelProperty("寻源单")
    private SouProject result;

    public ExtPrRequirementCreateSouContext(ExtPurchaseRequirementCreateSouDTO param) {
        this.param = param;
    }
    @Override
    @Nullable
    public String getSceneType() {
        return param.getSouType();
    }

}
