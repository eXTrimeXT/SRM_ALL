package com.midea.cloud.srm.sou.sourcing.vendor.spi.editorderitems;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderItem;
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
@ApiModel("商务投标报价PO")
public class ApiExtSouOrderItemPO extends BaseObjectX {

    @ApiModelProperty("报价单头表")
    private ExtSouOrder souOrder;

    @ApiModelProperty("报价表")
    private List<ExtSouOrderItem> souOrderItemList;

    @ApiModelProperty("报价文件")
    private List<ExtSouOrderFile> souOrderFileList;





}
