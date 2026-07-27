package com.midea.cloud.srm.model.sou.fixprice.vo;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouOrderItem;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtFixPriceInqOrderItemVO extends SouOrderItem {

    /** @see SouVendor#getVendorCode */
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商名称")
    private String vendorName;

    /** @see SouOrder#getSubmitTime */
    @ApiModelProperty("提交时间")
    private Date submitTime;

    /** @see ExtInqSouOrderItem#getAdvancePaymentRemark */
    @ApiModelProperty("预付款说明")
    private Enable advancePaymentRemark;

    /** @see ExtInqSouOrderItem#getExtLeadTime */
    @ApiModelProperty("供货周期")
    private Integer extLeadTime;

    /** @see ExtInqSouOrderItem#getExtWarrantyPeriod */
    @ApiModelProperty("质保期")
    private Integer extWarrantyPeriod;

    /** @see ExtPjInqSouOrder#getPriceActiveDay */
    @ApiModelProperty("报价有效期")
    private BigDecimal priceActiveDay;

    /** @see ExtPjInqSouOrder#getExtOrderByNickname */
    @ApiModelProperty("报价人")
    private String extOrderByNickname;

}
