package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("投标控制")
public class ExtSouProjectControlDto extends BaseObjectX {

    @ApiModelProperty("本轮需投标供应商数量")
    private Integer needTenderNum;

    @ApiModelProperty("已提交投标供应商数量")
    private Integer haveTenderNum;

    @ApiModelProperty("本轮截止时间")
    private Date orderEndTime;
}
