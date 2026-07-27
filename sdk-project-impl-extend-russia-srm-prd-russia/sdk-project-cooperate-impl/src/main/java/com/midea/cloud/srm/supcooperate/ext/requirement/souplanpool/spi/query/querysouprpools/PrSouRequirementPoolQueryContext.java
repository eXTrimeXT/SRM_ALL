package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.query.querysouprpools;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementPoolQueryVO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.dto.ExtPrSouRequirementPoolQueryDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 招标计划池 - 列表查询上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PrSouRequirementPoolQueryContext extends SdkPluginContext {

    @ApiModelProperty("查询条件信息")
    private ExtPrSouRequirementPoolQueryDTO param;

    @ApiModelProperty("查询结果")
    private List<ExtPrSouRequirementPoolQueryVO> result;

    public PrSouRequirementPoolQueryContext(ExtPrSouRequirementPoolQueryDTO param) {
        this.param = param;
    }
    @Override
    @Nullable
    public String getSceneType() {
        return ISdkPlugin.DEFAULT_SCENE;
    }

}
