package com.midea.cloud.srm.model.contract.dto;

import com.midea.cloud.srm.model.cm.contract.entity.ContractMaterial;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 100014336 ganyh19
 */
@Data
public class ContractMaterialExt extends ContractMaterial {

    @ApiModelProperty("发票类型")
    private String extInvoiceType;

    @ApiModelProperty("订价单号")
    private String extFixPriceLineId;
}
