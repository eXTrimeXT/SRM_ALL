package com.midea.cloud.srm.sou.sourcing.spi.init.open;

import com.midea.cloud.srm.model.sou.sourcing.entity.*;
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
@ApiModel("技术标报价")
public class ExtTechEvaEditPO extends BaseObjectX {

    @ApiModelProperty("寻源项目")
    private ExtSouProject souProject;

    @ApiModelProperty("技术标评分头表")
    private List<ExtSouTechScoreHead> techScoreHeadList;

}
