package com.midea.cloud.srm.model.sou.expert.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScore;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertScoreLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源 - 专家库 - 专家评审信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/20
 */
@Data
@TableName("scc_npm_sou_expert_score")
@EqualsAndHashCode(callSuper = true)
public class ExtSouExpertScoreVO extends ExtSouExpertScore {

    @ApiModelProperty("评审详情")
    private List<ExtSouExpertScoreLine> scoreLineList;

    @ApiModelProperty("专家信息")
    private ExtSouExpert expert;

}
