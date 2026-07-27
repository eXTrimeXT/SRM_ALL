package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRuleLine;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * MQL - 评分规则编辑信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/03/08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouScoreRuleDTO extends SouScoreRule {

    @ApiModelProperty("评分规则明细")
    private List<SouScoreRuleLine> ruleLineList;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    private Boolean tempSave = true;

}
