package com.midea.cloud.srm.model.sou.recommvendor.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("供应商推荐快速查询请求参数")
@Data
public class RecommvendorQuickQueryParam extends BaseDTO {

    @ApiModelProperty("项目ID")
    private Long projectId;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("品类编码")
    private String categoryCode;
}
