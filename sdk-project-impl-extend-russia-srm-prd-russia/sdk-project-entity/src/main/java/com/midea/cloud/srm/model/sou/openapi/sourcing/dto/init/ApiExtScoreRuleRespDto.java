package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.analysis.ScoreAnalysisDynamicFormDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
@ApiModel("查看技术评分明细")
public class ApiExtScoreRuleRespDto extends BaseObjectX {

    @ApiModelProperty("合并招标标识")
    private Boolean mergeFlag;

    @ApiModelProperty("技术评分供应商列表")
    List<ExtSouVendor> vendorList;

    @ApiModelProperty("招标工作小组")
    List<ExtSouGroup> groupList;

    @ApiModelProperty("招标组员评分项")
    Map<Long, List<ApiExtScoreRuleDto>> scoreMap;

    /** 评标历史 */
    @ApiModelProperty("评标历史日期")
    List<String> submiteDateList;

    @ApiModelProperty("评分历史")
    private List<ApiExtScoreRuleDto> scoreHistoryList;

    @ApiModelProperty("招标组员评分项附件")
    Map<Long, List<ExtSouTechScoreFile>> scoreMapFile;

    @ApiModelProperty("评分分析动态表单")
    private ScoreAnalysisDynamicFormDto scoreAnalysisDynamicForm;

}
