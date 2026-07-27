package com.midea.cloud.srm.sou.expert.spi.event.expertscore;

import com.midea.cloud.common.sdkplugin.SdkPluginContext;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertDoScoreDTO;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScore;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScoreLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 寻源 - 专家库 - 专家评分上下文
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExtSouExpertDoScoreContext extends SdkPluginContext {

    /** @see ExtSouExpert#getExpertId */
    @ApiModelProperty("入参: 专家评分信息")
    private List<ExtSouExpertDoScoreDTO> params;

    @ApiModelProperty("专家评分信息")
    private ExtSouExpertScore expertScore;
    @ApiModelProperty("专家评分明细")
    private List<ExtSouExpertScoreLine> expertScoreLineList;

    public ExtSouExpertDoScoreContext(List<ExtSouExpertDoScoreDTO> params) {
        this.params = params;
    }

}
