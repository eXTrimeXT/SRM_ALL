package com.midea.cloud.srm.sou.sourcing.spi.init.startprices;

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
public class ExtStartPriceEditPO extends BaseObjectX {

    @ApiModelProperty("招标主表")
    private ExtSouProject project;

    @ApiModelProperty("报价信息")
    private List<ExtSouItem> itemList;

    @ApiModelProperty("供应商列表")
    private List<ExtSouVendor> vendorList;

    @ApiModelProperty("轮次表")
    private ExtSouRound round;

    @ApiModelProperty("报价单表")
    private List<ExtSouOrder> orderList;

    @ApiModelProperty("报价单明细表")
    private List<ExtSouOrderItem> orderItemList;

}
