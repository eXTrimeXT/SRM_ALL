package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.select;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.SouScoreDimensionContextData;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangwk12@meicloud.com
 * @since 2023/03/13
 */
@Data
@ApiModel(description = "智能评选参数")
@EqualsAndHashCode(callSuper = true)
public class MqlSouIntelligentSelectDTO extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("本轮次需要进行评分的供应商报价信息(如果没有额外特殊的计算所需信息，可以传空)")
    private List<SouScoreDimensionContextData> scoreDataList = new ArrayList<>();
    @ApiModelProperty("是否需要自动算分")
    private Boolean needAutoScore;
    @ApiModelProperty("寻源场景")
    private String souType;

    public void formatParams() {
        if (projectId == null) {
            throw new IllegalArgumentException("缺少projectId参数");
        }
    }

}
