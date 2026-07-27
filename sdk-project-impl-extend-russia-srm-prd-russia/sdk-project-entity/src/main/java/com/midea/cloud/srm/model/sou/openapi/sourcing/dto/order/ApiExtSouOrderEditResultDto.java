package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.sou.ca.dto.CaSelectionResultDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
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
@ApiModel("编制定标结果")
public class ApiExtSouOrderEditResultDto extends BaseObjectX {

    @ApiModelProperty("查看定标说明")
    private List<CaSelectionResultDTO> selectionResultList;

    @ApiModelProperty("编制定标结果")
    private List<ApiExtSouOrderItemDto> orderItemResultList;

    @ApiModelProperty("编制定标结果供应商合集")
    List<ExtSouVendor> vendorResultList;
}
