package com.midea.cloud.srm.model.sou.purfixprice.vo;

import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLine;
import com.midea.cloud.srm.model.sou.purfixprice.enums.ExtPurFixPriceStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
public class ExtPurFixPriceLineGroupDetailVO {

    /** @see SouOrderItem#getItemId */
    @ApiModelProperty("物料ID")
    private Long itemId;

    /** @see SouOrderItem#getItemCode */
    @ApiModelProperty("物料编码")
    private String itemCode;

    /** @see SouOrderItem#getItemDesc */
    @ApiModelProperty("物料名称")
    private String itemDesc;

    /** @see ExtPurInqSouItem#getModel */
    @ApiModelProperty("规格型号")
    private String model;

    /** @see SouOrderItem#getUnit */
    @ApiModelProperty("单位")
    private String unit;

    /** @see SouItem#getRemark */
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("单据状态")
    private ExtPurFixPriceStatusEnum fixPriceStatus;

    @ApiModelProperty("供应商报价明细")
    private Map<String, ExtPurFixPriceLine> vendorOrderItemList;

}
