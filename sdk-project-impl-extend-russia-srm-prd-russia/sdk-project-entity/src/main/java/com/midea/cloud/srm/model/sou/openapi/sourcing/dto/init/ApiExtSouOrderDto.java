package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("招标控制-投标明细")
@Data
public class ApiExtSouOrderDto extends ExtSouOrder {

    @ApiModelProperty("是否查阅标书")
    private String readBidFileFlag;

    /** 已投标包名 */
    @ApiModelProperty("已投标包名")
    private String tenderPackageName;

    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("供应商属性")
    private String extVendorAttr;

    @ApiModelProperty("不参与原因")
    private String extNotjoinReason;

    @ApiModelProperty("契约认证")
    private String contractVerification;
}
