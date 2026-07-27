package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtScoreRule;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("评分规则")
public class ApiExtSouScoreRuleDto extends BaseObjectX {

    @ApiModelProperty("招标规则列表")
    private List<ExtScoreRule> scoreRuleList;

    /**
     * 关联招标基本信息主键ID
     */
    @ApiModelProperty("关联招标基本信息主键ID")
    private Long projectId;

    @ApiModelProperty("true-暂存/false-提交")
    protected boolean tempSave;
}
