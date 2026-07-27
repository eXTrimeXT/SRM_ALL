package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.tech;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRuleLine;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouTechScoreLine;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 寻源openAPI - 技术评分明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouTechScoreLineDTO extends BaseObjectX {

    /** @see SouTechScoreLine#getTechScoreLineId */
    @ApiModelProperty("评分详情ID")
    private Long techScoreLineId;

    /** @see SouScoreRuleLine#getScoreRuleLineId */
    @ApiModelProperty("评分规则模板行ID")
    private Long scoreRuleLineId;

    /** @see SouTechScoreLine#getScore */
    @ApiModelProperty("评分")
    private BigDecimal score;

}
