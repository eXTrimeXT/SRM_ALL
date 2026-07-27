package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.batchassign;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolAssignDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 招标计划池 - 分配/转办上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouRequirementPoolBatchAssignContext extends SdkPluginContext {

    @ApiModelProperty("入参: 分配/转办数据")
    private ExtPrSouRequirementPoolAssignDTO param;

    @ApiModelProperty("招标计划集合(IPrSouRequirementPoolBatchAssignPlugin#judgeBatchAssignAuth环节填补)")
    private List<ExtPrSouRequirementHead> souPrHeadList;
    /** userId */
    @ApiModelProperty("招标计划集合(IPrSouRequirementPoolBatchAssignPlugin#judgeBatchAssignAuth环节填补)")
    private Map<Long, User> userMap = Collections.emptyMap();

    public PrSouRequirementPoolBatchAssignContext(ExtPrSouRequirementPoolAssignDTO param) {
        this.param = param;
    }

}
