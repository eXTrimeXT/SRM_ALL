package com.midea.cloud.srm.sou.sourcing.spi.init.editorderitemresults;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderItem;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
@ApiModel("编辑定标结果PO类")
public class ApiExtSouOrderItemResultPO extends BaseObjectX {

    @ApiModelProperty("定标结果")
    private List<ExtSouOrderItem> orderItemList;
}
