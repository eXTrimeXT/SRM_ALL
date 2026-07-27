package com.midea.cloud.srm.sou.sourcing.spi.init.editgroups;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
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
@ApiModel("指定专家")
public class ApiExtSouGroupEditPO extends BaseObjectX {

    @ApiModelProperty("招标组员列表")
    private List<ExtSouGroup> groupList;
}
