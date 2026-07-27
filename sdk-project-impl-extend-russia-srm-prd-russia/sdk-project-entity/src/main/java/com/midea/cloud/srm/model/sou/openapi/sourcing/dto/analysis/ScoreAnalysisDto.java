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
public class ScoreAnalysisDto extends BaseObjectX {
    @ApiModelProperty("成员账号")
    private String userName;

    @ApiModelProperty("成员姓名")
    private String fullName;

    @ApiModelProperty("供应商评分信息信息")
    private List<ScoreAnalysisVendorDto> scoreAnalysisVendorDtoList;
}
