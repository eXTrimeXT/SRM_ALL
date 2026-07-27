package com.midea.cloud.srm.model.pj.sou.mqlapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouVendorAuth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源MQL
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/02/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MqlSouVendorDTO extends SouVendor {

    @ApiModelProperty("报价权限")
    private List<SouVendorAuth> authList;

}
