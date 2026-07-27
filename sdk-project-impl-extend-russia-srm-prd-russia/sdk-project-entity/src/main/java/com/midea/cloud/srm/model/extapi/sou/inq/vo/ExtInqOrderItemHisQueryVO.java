package com.midea.cloud.srm.model.extapi.sou.inq.vo;

import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouOrder;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.order.ApiInqSouOrderItemVO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
/**
 * 备注
 * @author huangbf3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtInqOrderItemHisQueryVO extends ApiInqSouOrderItemVO {

    /** @see SouOrder#getOrderNo */
    @ApiModelProperty("报价单号")
    private String orderNo;

    /** @see SouOrder#getSubmitByIp */
    @ApiModelProperty("提交人IP")
    private String submitByIp;

    /** @see SouProject#getSouNo */
    @ApiModelProperty("询价单号")
    private String souNo;

    /** @see ExtPjInqSouOrder#getPriceActiveDay */
    @ApiModelProperty("报价有效期(自然日)")
    private BigDecimal priceActiveDay;

    /** @see ExtPjInqSouOrder#getExtOrderByNickname */
    @ApiModelProperty("报价人")
    private String extOrderByNickname;

    /** @see ExtPjInqSouOrder#getExtOrderPhone */
    @ApiModelProperty("报价联系方式")
    private String extOrderPhone;

    /** @see SouVendor#getVendorCode */
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商名称")
    private String vendorName;

}
