package com.midea.cloud.srm.sou.sourcing.spi.init.open;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouTechScoreHead;
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
@ApiModel("商务开标")
public class ExtBusOpenEditPO extends BaseObjectX {

    @ApiModelProperty("寻源项目")
    private ExtSouProject souProject;


}
