package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRuleLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源openAPI - 评分规则信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouScoreRuleVO extends SouScoreRule {

    @ApiModelProperty("评分规则明细")
    private List<SouScoreRuleLine> ruleLineList;

}
