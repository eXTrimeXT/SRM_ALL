package com.midea.cloud.srm.model.sou.expert.dto;

import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScore;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScoreLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源 - 专家库 - 创建专家评审
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertScoreCreateDTO extends ExtSouExpertScore {

    @ApiModelProperty("评审详情")
    private List<ExtSouExpertScoreLine> scoreLineList;

}
