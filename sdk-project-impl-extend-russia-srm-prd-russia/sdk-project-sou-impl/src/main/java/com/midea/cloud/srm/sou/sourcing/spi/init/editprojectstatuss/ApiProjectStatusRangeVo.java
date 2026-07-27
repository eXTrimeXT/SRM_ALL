package com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("单据状态范围")
public class ApiProjectStatusRangeVo<T> extends BaseObjectX {

    @ApiModelProperty("状态")
    private T status;

    @ApiModelProperty("往前可回滚的状态")
    private List<T> preStatus;

    @ApiModelProperty("进入下一个状态")
    private List<T> nextStatus;
}
