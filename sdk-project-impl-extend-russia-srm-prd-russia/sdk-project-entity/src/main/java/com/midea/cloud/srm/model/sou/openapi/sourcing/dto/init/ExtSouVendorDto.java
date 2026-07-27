package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("技术标管理-投标供应商")
public class ExtSouVendorDto extends ExtSouVendor {

    /**
     * 风险标识
     */
    @ApiModelProperty("风险标识")
    private String riskFlag;
}
