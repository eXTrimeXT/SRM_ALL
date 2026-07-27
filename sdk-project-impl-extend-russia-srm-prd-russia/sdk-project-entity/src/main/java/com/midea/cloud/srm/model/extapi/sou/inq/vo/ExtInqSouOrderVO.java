package com.midea.cloud.srm.model.extapi.sou.inq.vo;

import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouOrder;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrder;
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
public class ExtInqSouOrderVO extends SouOrder {

    /** @see ExtPjInqSouOrder#getPriceActiveDay */
    @ApiModelProperty("报价有效期(自然日)")
    private BigDecimal priceActiveDay;

    /** @see ExtPjInqSouOrder#getExtOrderByNickname */
    @ApiModelProperty("报价人")
    private String extOrderByNickname;

    /** @see ExtPjInqSouOrder#getExtOrderPhone */
    @ApiModelProperty("报价联系方式")
    private String extOrderPhone;

}
