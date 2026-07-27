package com.midea.cloud.srm.model.extapi.sou.inq.dto;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.control.ApiSouStartNewRoundDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

/**
 * 长城 - 询比价 - 发起新一轮
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtInqSouStartNewRoundDTO extends ApiSouStartNewRoundDTO {

    @ApiModelProperty("新一轮可用的物料需求ID集合")
    private Set<Long> inqChooseSouItemIds;

    @ApiModelProperty("新一轮中可用的供应商集合")
    private List<ExtPjInqSouVendorDTO> inqChooseVendorList;

}
