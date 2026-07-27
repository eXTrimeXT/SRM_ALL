package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

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
@ApiModel("编辑定标结果")
public class ApiExtSouOrderItemResultEditParam extends BaseObjectX {

    @ApiModelProperty("寻源单ID")
    private Long projectId;

    @ApiModelProperty("定标结果")
    List<ApiExtSouOrderItemDto> orderItemList;

    @ApiModelProperty("定标操作类型")
    private String type;
}
