package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createvendorrecommend;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouRecommVendorInfoDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 招标计划池 - 推荐供应商上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouRequirementCreateVendorRecommendContext extends SdkPluginContext {

    @ApiModelProperty("入参: 招标计划池数据")
    private List<ExtPrSouRequirementHeadDTO> params;

    @ApiModelProperty("招标计划信息(IPrSouRequirementCreateVendorRecommendPlugin#judgeVendorRecommendAuth环节填补)")
    private List<ExtPrSouRequirementHead> souPrHeadList;
    @ApiModelProperty("采购申请信息(IPrSouRequirementCreateVendorRecommendPlugin#judgeVendorRecommendAuth环节填补)")
    private List<PrRequirementHead> prHeadList;

    @Nullable
    @ApiModelProperty("推荐供应商单据信息")
    private ApiExtSouRecommVendorInfoDTO result;

    public PrSouRequirementCreateVendorRecommendContext(List<ExtPrSouRequirementHeadDTO> params) {
        this.params = params;
    }

}
