package com.midea.cloud.srm.model.extapi.sou.inq.dto;

import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 长城 - 询比价 - 删除邀请供应商信息
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPjInqSouVendorDelDTO extends SouVendor {

    @ApiModelProperty("删除原因")
    private String delReason;

}
