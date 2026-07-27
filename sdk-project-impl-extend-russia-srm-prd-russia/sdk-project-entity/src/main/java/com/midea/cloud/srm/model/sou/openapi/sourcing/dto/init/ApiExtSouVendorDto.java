package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class ApiExtSouVendorDto extends BaseObjectX {

    @ApiModelProperty("供应商列表")
    private List<ExtSouVendor> vendorList;
    /**
     * 关联招标基本信息主键ID
     */
    @ApiModelProperty("关联招标基本信息主键ID")
    private Long projectId;

    @ApiModelProperty("true-暂存/false-提交")
    protected boolean tempSave;
}
