package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.removecancel;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.vo.ExtPrSouRequirementCancelVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * 招标计划 - 计划取消删除上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouRequirementCancelRemoveContext extends SdkPluginContext {

    @ApiModelProperty("入参: 计划取消单据ID")
    private Long requirementCancelId;

    @ApiModelProperty("计划取消单据(IPrSouRequirementCancelRemovePlugin#judgeRemoveCancelAuth环节填补)")
    private ExtPrSouRequirementCancel reqCancel;

    @ApiModelProperty("被删除的数据")
    private ExtPrSouRequirementCancelVO result;

    public PrSouRequirementCancelRemoveContext(Long requirementCancelId) {
        this.requirementCancelId = requirementCancelId;
    }
    @Override
    @Nullable
    public String getSceneType() {
        return ISdkPlugin.DEFAULT_SCENE;
    }

}
