package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init;

import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 寻源openAPI - 邀请供应商
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/10/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiSouVendorEditDTO extends BaseObjectX {

    @ApiModelProperty("ID")
    private Long souVendorId;

    @ApiModelProperty(value = "供应商ID", required = true)
    private Long vendorId;

    @ApiModelProperty(value = "联系人名称")
    private String linkmanName;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "排序", required = true)
    private Integer sortIndex;

}
