package com.midea.cloud.srm.model.sou.recommvendor.dto;

import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.supplier.bpm.dto.ContactInfoDto;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("供应商风险-参数")
@EqualsAndHashCode
public class RecommvendorRiskParamDto extends BaseObjectX {

    @ApiModelProperty("供应商联系人")
    private Map<Long, ContactInfoDto> vendorContactMap;
    @ApiModelProperty("寻源报名联系人")
    private Map<Long, SouReqApply> vendorReqApplyMap;

}
