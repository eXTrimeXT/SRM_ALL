package com.midea.cloud.srm.sou.sourcing.spi.init.editpricetemplates;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMarginRecord;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPriceTemplate;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExtSouPriceTemplatePo extends BaseObjectX {

    @ApiModelProperty("报价模板字段")
    private List<ExtSouPriceTemplate> priceTemplateList;
}
