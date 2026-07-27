package com.midea.cloud.srm.sou.sourcing.spi.init.editsouitems;

import com.midea.cloud.srm.model.sou.sourcing.entity.*;
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
public class ExtSouItemEditPO extends BaseObjectX {

    @ApiModelProperty("报价信息")
    private List<ExtSouItem> itemList;

}
