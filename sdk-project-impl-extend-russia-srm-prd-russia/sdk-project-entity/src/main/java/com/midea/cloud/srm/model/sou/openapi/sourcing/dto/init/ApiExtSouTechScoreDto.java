package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreFile;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
@ApiModel("招标评分保存")
public class ApiExtSouTechScoreDto extends BaseObjectX {

    @ApiModelProperty("打分项")
    private List<ApiExtScoreRuleDto> scoreRuleDtoList;

    @ApiModelProperty("导入评分项数据")
    private List<Map<String, Object>> importDataList;

    @ApiModelProperty("评分项MAP")
    private Map<String, List<ApiExtScoreRuleDto>> ruleDtoMap;

    @ApiModelProperty("导入校验标识")
    private AtomicBoolean importCheck = new AtomicBoolean(true);

    /**
     * 关联招标基本信息主键ID
     */
    @ApiModelProperty("关联招标基本信息主键ID")
    private Long projectId;

    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave;

    @ApiModelProperty("附件列表")
    private List<ExtSouTechScoreFile> fileList;

    @ApiModelProperty("头id")
    private Long techScoreHeadId;
}
