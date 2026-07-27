package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel(description = "调整投标截止时间")
@EqualsAndHashCode(callSuper = true)
public class ApiExtSouEndTimeDto extends BaseObjectX {

    @ApiModelProperty("主键ID")
    private Long projectId;

    @ApiModelProperty("当前投标截止时间")
    private Date currentEndTime;

    @ApiModelProperty("调整截止时间")
    private Date adjustEndTime;

    @ApiModelProperty("调整原因")
    private String adjustReason;

}
