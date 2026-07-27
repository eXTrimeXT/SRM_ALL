package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouRecommendedVendor;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
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
@ApiModel(description = "供应商信息保存")
@EqualsAndHashCode(callSuper = true)
public class ApiExtSouRecommVendorInfoDTO extends BaseObjectX {

    /**
     * 项目信息
     */
    private ExtSouProjectDto project;

    /**
     * 标的物信息
     */
    @ApiModelProperty("标的物信息")
    private ExtSouRecommendedVendor souRecommendedVendor;

    /**
     * 推荐供应商
     */
    @ApiModelProperty("推荐供应商")
    private List<ExtSouVendor> souVendor;

    /**
     * 供应商文件
     */
    @ApiModelProperty("附件")
    private List<ExtSouFile> vendorFileList;

    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("true-暂存/false-提交")
    protected Boolean isTempSave = true;
}
