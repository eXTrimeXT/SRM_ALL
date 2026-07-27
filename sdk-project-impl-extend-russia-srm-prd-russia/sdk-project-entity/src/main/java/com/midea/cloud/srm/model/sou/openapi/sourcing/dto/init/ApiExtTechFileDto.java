package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderFile;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("技术方案")
public class ApiExtTechFileDto extends BaseObjectX {

    @ApiModelProperty("投标文件")
    List<ExtSouOrderFileDto> orderFileList;

    @ApiModelProperty("脱敏文件")
    List<ExtSouOrderFileDto> secretFileList;

    @ApiModelProperty("寻源单ID")
    private Long projectId;


}
