package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
@ApiModel("签署方信息")
public class ApiExtSignatoryDto extends BaseObjectX {

    @ApiModelProperty("签署方名称")
    private String tenantName;

    @ApiModelProperty("接收人姓名")
    private String receiverName;

    @ApiModelProperty("接收人联系方式")
    private String contact;

}
