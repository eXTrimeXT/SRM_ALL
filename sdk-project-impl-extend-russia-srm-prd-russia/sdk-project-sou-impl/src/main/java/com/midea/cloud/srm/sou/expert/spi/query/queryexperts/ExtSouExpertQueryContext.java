package com.midea.cloud.srm.sou.expert.spi.query.queryexperts;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertQueryDTO;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertQueryVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 专家库 - 列表查询上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertQueryContext extends SdkPluginContext {

    @ApiModelProperty("入参: 查询条件")
    private ExtSouExpertQueryDTO param;

    @ApiModelProperty("查询结果")
    private List<ExtSouExpertQueryVO> result;

    public ExtSouExpertQueryContext(ExtSouExpertQueryDTO param) {
        this.param = param;
    }

}
