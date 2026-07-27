package com.midea.cloud.srm.sou.sourcing.spi.init.editinvitesuppliers;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
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
@ApiModel("邀请供应商")
public class ExtSouVendorEditPO extends BaseObjectX {

    @ApiModelProperty("供应商信息")
    private List<ExtSouVendor> vendorList;

}
