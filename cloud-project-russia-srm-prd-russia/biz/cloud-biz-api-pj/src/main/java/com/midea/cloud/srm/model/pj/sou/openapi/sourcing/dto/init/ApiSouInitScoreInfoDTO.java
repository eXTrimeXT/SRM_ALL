package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouScoreRuleTypeEnum;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 评分规则
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/30
 */
@SuppressWarnings("ALL")
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouInitScoreInfoDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    protected Long projectId;
    @ApiModelProperty("评分方式")
    protected SouScoreRuleTypeEnum scoreRuleType;
    @ApiModelProperty("评分模板ID")
    protected Long scoreTemplateId;
    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
        if (scoreRuleType == null) {
            throw new IllegalArgumentException("请选择评分规则");
        } else if (SouScoreRuleTypeEnum.COMPOSITE_PRICE.equals(scoreRuleType)) {
            // 综合评分法
            if (scoreTemplateId == null) {
                throw new IllegalArgumentException("缺少scoreTemplateId参数");
            }
        }
    }

}
