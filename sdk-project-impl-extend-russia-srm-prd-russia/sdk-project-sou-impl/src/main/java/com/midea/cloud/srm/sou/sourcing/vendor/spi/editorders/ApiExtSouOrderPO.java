package com.midea.cloud.srm.sou.sourcing.vendor.spi.editorders;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("确认投标-PO类")
@Data
public class ApiExtSouOrderPO extends BaseObjectX {

    @ApiModelProperty("投标头表")
    private ExtSouOrder souOrder;
}
