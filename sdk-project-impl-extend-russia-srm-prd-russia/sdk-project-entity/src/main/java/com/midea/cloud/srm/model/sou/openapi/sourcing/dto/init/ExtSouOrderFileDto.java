package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("投标文件")
public class ExtSouOrderFileDto extends ExtSouOrderFile {

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("供应商名称")
    private String vendorName;
}
