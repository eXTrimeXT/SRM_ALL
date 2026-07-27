package com.midea.cloud.srm.model.pj.sou.brg.vo.webapi.select;

import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 项目式询价 - 比价明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/28
 */
@Data
public class BrgSouSimplePriceCompareDetailWebVO {

    /** @see SouOrderItem#getSouItemId */
    @ApiModelProperty("物料需求行ID")
    private Long souItemId;

    /** @see SouOrderItem#getVendorId */
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    /** @see SouOrderItem#getStandardNotaxPrice  */
    @ApiModelProperty("供应商本币未税报价(类型为String，如果供应商未报价，则值为\"未报价\"；如果供应商无权限报价，则值为\"禁止报价\")")
    private String standardNotaxPrice;

}
