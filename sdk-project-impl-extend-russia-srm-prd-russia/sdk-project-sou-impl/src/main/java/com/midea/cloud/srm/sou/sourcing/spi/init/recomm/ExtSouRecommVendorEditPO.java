package com.midea.cloud.srm.sou.sourcing.spi.init.recomm;

import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExtSouRecommVendorEditPO extends BaseObjectX {

    /**
     * 项目信息
     */
    @ApiModelProperty("项目基本信息")
    private ExtSouProject project;

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

    private List<ExtSouDemand> souDemands;

}
