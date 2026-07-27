package com.midea.cloud.srm.model.extapi.sou.purinq.vo.order;

import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqOrderItemHisQueryVO extends ApiPurInqSouOrderItemVO {

    /** @see SouOrder#getOrderNo */
    @ApiModelProperty("报价单号")
    private String orderNo;

    /** @see SouOrder#getSubmitByIp */
    @ApiModelProperty("提交人IP")
    private String submitByIp;

    /** @see SouProject#getSouNo */
    @ApiModelProperty("询价单号")
    private String souNo;

    /** @see ExtPurInqSouOrder#getOrderByNickname */
    @ApiModelProperty("报价人")
    private String orderByNickname;

    /** @see ExtPurInqSouOrder#getOrderPhone */
    @ApiModelProperty("报价联系方式")
    private String orderPhone;

    /** @see SouVendor#getVendorCode */
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    /** @see SouVendor#getVendorName */
    @ApiModelProperty("供应商名称")
    private String vendorName;

}
