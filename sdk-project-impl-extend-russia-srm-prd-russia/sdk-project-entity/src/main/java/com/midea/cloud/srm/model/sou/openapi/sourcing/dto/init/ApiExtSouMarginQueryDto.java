package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.common.BasePage;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("年度保证金查询请求参数")
public class ApiExtSouMarginQueryDto extends BasePage {

    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("是否生效")
    private String effectFlag;
}
