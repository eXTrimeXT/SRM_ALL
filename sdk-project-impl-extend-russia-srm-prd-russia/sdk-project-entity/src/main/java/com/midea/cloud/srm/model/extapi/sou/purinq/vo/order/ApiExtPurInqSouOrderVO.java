package com.midea.cloud.srm.model.extapi.sou.purinq.vo.order;

import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
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
public class ApiExtPurInqSouOrderVO extends SouOrder {

    /** @see ExtPurInqSouOrder#getOrderByNickname */
    @ApiModelProperty("报价人")
    private String orderByNickname;

    /** @see ExtPurInqSouOrder#getOrderPhone */
    @ApiModelProperty("报价电话")
    private String orderPhone;

    /** @see ExtPurInqSouOrder#getOrderEmail */
    @ApiModelProperty("报价邮箱")
    private String orderEmail;

}
