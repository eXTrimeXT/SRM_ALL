package com.midea.cloud.srm.model.extapi.sou.purinq.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouVendorDelDTO extends BaseObjectX {

    /** @see SouVendor#getSouVendorId */
    @ApiModelProperty("供应商表ID")
    private Long souVendorId;

    @ApiModelProperty("删除原因")
    private String delReason;

}