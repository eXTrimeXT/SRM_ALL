package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.createsoureq;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 招标计划池 - 创建寻源需求上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouRequirementCreateSouReqContext extends SdkPluginContext {

    @ApiModelProperty("入参: 招标计划池数据")
    private List<ExtPrSouRequirementHeadDTO> params;

    @ApiModelProperty("招标计划信息(IPrSouRequirementCreateSouReqPlugin#judgeCreateSouReqAuth环节填补)")
    private List<ExtPrSouRequirementHead> souPrHeadList;
    @ApiModelProperty("采购申请信息(IPrSouRequirementCreateSouReqPlugin#judgeCreateSouReqAuth环节填补)")
    private List<PrRequirementHead> prHeadList;

    @ApiModelProperty("寻源需求对象")
    private RecordDTO result;

    public PrSouRequirementCreateSouReqContext(List<ExtPrSouRequirementHeadDTO> params) {
        this.params = params;
    }

}
