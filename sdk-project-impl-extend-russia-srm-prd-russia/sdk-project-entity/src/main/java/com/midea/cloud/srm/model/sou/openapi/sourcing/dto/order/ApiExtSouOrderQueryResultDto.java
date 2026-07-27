package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("编制定标结果")
@Data
public class ApiExtSouOrderQueryResultDto extends BaseObjectX {

    @ApiModelProperty("项目ID")
    private Long projectId;

    @ApiModelProperty("供应商ID")
    private Long vendorId;
}
