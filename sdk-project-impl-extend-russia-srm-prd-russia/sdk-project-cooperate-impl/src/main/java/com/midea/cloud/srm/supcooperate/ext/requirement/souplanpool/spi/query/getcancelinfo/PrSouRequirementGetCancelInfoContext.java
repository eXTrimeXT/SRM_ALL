package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.query.getcancelinfo;

import com.meicloud.paas.core.web.NoWrapperAsReturnT;
import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCancelVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招标计划池 - 取消单据查询上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoWrapperAsReturnT
public class PrSouRequirementGetCancelInfoContext extends SdkPluginContext {

    /** @see ExtPrSouRequirementCancel#getRequirementCancelId */
    @ApiModelProperty("入参: 取消单据ID")
    private Long requirementCancelId;

    @ApiModelProperty("查询结果")
    private ExtPrSouRequirementCancelVO result;

    public PrSouRequirementGetCancelInfoContext(Long requirementCancelId) {
        this.requirementCancelId = requirementCancelId;
    }

}
