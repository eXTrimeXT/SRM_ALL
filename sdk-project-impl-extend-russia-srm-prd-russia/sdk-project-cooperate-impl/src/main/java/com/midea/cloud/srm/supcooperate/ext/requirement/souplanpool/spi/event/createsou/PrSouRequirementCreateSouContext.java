package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsou;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolCreateSouDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCreateSouVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 招标计划池 - 创建寻源上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouRequirementCreateSouContext extends SdkPluginContext {

    @ApiModelProperty("入参: 招标计划池数据")
    private ExtPrSouRequirementPoolCreateSouDTO param;

    @ApiModelProperty("招标计划信息(IPrSouRequirementCreateSouPlugin#judgeCreateSouAuth环节填补)")
    private List<ExtPrSouRequirementHead> souPrHeadList;
    @ApiModelProperty("采购申请信息(IPrSouRequirementCreateSouPlugin#judgeCreateSouAuth环节填补)")
    private List<PrRequirementHead> prHeadList;

    @ApiModelProperty("创建的寻源单信息")
    private ExtPrSouRequirementCreateSouVO result;

    public PrSouRequirementCreateSouContext(ExtPrSouRequirementPoolCreateSouDTO param) {
        this.param = param;
    }
    @Override
    @Nullable
    public String getSceneType() {
        return param.getSouType();
    }

}
