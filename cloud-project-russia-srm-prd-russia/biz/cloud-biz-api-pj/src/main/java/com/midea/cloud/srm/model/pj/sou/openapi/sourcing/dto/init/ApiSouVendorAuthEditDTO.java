package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 供应商权限
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouVendorAuthEditDTO extends BaseObjectX {

    @ApiModelProperty("ID")
    private Long vendorAuthId;

    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @ApiModelProperty(value = "物料需求ID", required = true)
    private Long souItemId;

    @ApiModelProperty("是否禁止报价")
    private Enable forbidPrice;

    @ApiModelProperty("排序")
    private Integer sortIndex;

}
