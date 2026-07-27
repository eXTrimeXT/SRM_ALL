package com.midea.cloud.srm.model.sou.fixprice.dto;

import com.midea.cloud.srm.model.common.enums.Enable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 100014336 ganyh19
 */
@Data
public class ExtFixPriceContractSignDTO  {

    @ApiModelProperty("定价单行ID")
    private Long fixPriceLineId;

    @ApiModelProperty("是否签订合同")
    private Enable hasSignedContract;
}
