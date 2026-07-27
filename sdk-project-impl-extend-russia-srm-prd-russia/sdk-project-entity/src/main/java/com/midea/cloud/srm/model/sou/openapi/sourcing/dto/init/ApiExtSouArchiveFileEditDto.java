package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

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
@ApiModel("项目归档-附件资料")
public class ApiExtSouArchiveFileEditDto extends BaseObjectX {

    @ApiModelProperty("招标单ID")
    private Long projectId;

    @ApiModelProperty("项目归档资料")
    List<ExtSouFile> archiveFileList;

    @ApiModelProperty("true-暂存/false-提交")
    protected boolean isTempSave = true;
}
