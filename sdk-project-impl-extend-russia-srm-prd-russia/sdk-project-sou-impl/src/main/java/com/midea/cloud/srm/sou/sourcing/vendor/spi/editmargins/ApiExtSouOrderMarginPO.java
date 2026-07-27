package com.midea.cloud.srm.sou.sourcing.vendor.spi.editmargins;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
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
@ApiModel("供应商提交保证金缴纳凭证")
public class ApiExtSouOrderMarginPO extends BaseObjectX {

    @ApiModelProperty("保证金")
    private ExtSouMargin souMargin;
}
