package com.midea.cloud.srm.model.pj.sou.openapi.bid.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouInitScoreInfoDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 招投标openAPI - 评分规则
 * PS: 参考 {@link ApiSouInitScoreInfoDTO}
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/21
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBidSouScoreInfoDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("评分方式")
    private SouScoreRuleTypeEnum scoreRuleType;
    @ApiModelProperty("评分模板ID")
    private Long scoreTemplateId;
    @ApiModelProperty("true-暂存/false-提交")
    private boolean isTempSave = true;

}
