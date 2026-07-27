package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouFile;
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
@ApiModel("招标协同查看招标文件")
public class ApiExtSouBidFileDto extends BaseObjectX {

    @ApiModelProperty("合并标识")
    private boolean mergeFlag;

    @ApiModelProperty("查看招标文件")
    private List<ExtSouFile> fileList;
}
