package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouExpertRisk;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("抽取风险")
public class ExtSouExpertRiskDto extends ExtSouExpertRisk {

    @ApiModelProperty("亲友工作单位")
    private String relationVendorName;

    @ApiModelProperty("亲友名字")
    private String relationsFullName;

}
