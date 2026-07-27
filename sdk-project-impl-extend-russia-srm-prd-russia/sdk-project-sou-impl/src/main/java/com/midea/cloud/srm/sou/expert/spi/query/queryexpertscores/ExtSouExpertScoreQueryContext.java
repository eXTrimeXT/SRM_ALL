package com.midea.cloud.srm.sou.expert.spi.query.queryexpertscores;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertScoreQueryDTO;
import com.midea.cloud.srm.model.sou.expert.vo.ExtSouExpertScoreQueryVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 专家库 - 列表查询上下文
 *
 * @author zhangwk12@meicloud.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertScoreQueryContext extends SdkPluginContext {

    @ApiModelProperty("入参: 查询条件")
    private ExtSouExpertScoreQueryDTO param;

    @ApiModelProperty("查询结果")
    private List<ExtSouExpertScoreQueryVO> result;

    public ExtSouExpertScoreQueryContext(ExtSouExpertScoreQueryDTO param) {
        this.param = param;
    }

}
