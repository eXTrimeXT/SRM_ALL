package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorAuthEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorEditDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 寻源openAPI - 邀请供应商
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouVendorDTO extends ApiSouVendorEditDTO {

    @ApiModelProperty("报价权限")
    private List<ApiSouVendorAuthEditDTO> authList;

}
