package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode
@ApiModel("指定专家")
public class ApiExtSouGroupEditDto extends BaseObjectX {

    @ApiModelProperty("招标单ID")
    private Long projectId;

    @ApiModelProperty("招标组员列表")
    private List<ExtSouGroup> groupList;
}
