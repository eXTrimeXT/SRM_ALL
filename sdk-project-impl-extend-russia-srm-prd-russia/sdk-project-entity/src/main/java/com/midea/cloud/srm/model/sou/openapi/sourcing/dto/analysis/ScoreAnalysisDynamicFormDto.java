package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.analysis;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-23
 */
@Data
@ApiModel("评分分析实体类")
public class ScoreAnalysisDynamicFormDto extends BaseObjectX {

    @ApiModelProperty("供应商动态列")
    private List<ScoreAnalysisVendorDto> dynamicTitleList;

    @ApiModelProperty("表单数据列")
    private List<ScoreAnalysisDto> formDataList;
}
