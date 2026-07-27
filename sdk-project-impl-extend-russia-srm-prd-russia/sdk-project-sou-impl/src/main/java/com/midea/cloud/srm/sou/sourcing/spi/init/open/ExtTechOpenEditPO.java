package com.midea.cloud.srm.sou.sourcing.spi.init.open;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("技术开标")
@Data
@EqualsAndHashCode
public class ExtTechOpenEditPO extends BaseObjectX {

    @ApiModelProperty("寻源项目信息")
    private ExtSouProject project;

    @ApiModelProperty("是否自动开始评标")
    private boolean isAutoEvaTech = false;
}
