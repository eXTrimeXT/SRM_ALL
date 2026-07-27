package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPriceTemplate;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.mideacloud.common.objectx.BaseObjectX;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("商务标管理-投标详情查询值域")
@Data
public class ApiExtSouOrderBusManagementDto extends BaseObjectX {

    @ApiModelProperty("轮次值域")
    private List<Integer> roundList;

    @ApiModelProperty("供应商值域")
    private List<ExtSouVendor> vendorList;

    /**
     * 已选字段（报价模板字段）
     */
    @ApiModelProperty("已选字段（报价模板字段）")
    private List<ExtSouPriceTemplate> selectedList;
}
