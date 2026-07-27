package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("随机抽取专家")
@Data
public class ApiExtSouExpertRandomExtractDto extends BaseObjectX {

    @ApiModelProperty("招标单ID")
    private Long projectId;

    @ApiModelProperty("专家抽取范围，字典：SOU_EXPERT_RANGE")
    private String extExpertRange;
}
