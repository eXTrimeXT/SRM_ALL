package com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.swagger.init;

import com.midea.cloud.srm.model.pj.sou.bid.entity.BidSouCurrency;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouCurrency;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouCurrency;
import com.midea.cloud.srm.model.pj.sou.inq.entity.InqSouCurrency;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouCurrency;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 可用币种 (swagger接口专用)
 * PS: 由于目前的寻源结构（ObjectX + SPI），很难描述不同寻源场景对入参的需求差别，
 *     因此用一个专有的类来装所有的信息。
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/01/05
 */
@Data
@ApiModel(description = "可用币种")
@EqualsAndHashCode(callSuper = true)
public class ApiSouCurrencySwaggerVO extends SouCurrency {

    /**
     * @see InqSouCurrency#getPriceTax
     * @see BidSouCurrency#getPriceTax
     * @see BrgSouCurrency#getPriceTax
     * @see CompSouCurrency#getPriceTax
     */
    @ApiModelProperty("汇率(仅用于简易询价-inq/招投标-bid/项目式询价-brg/竞价-comp)")
    private BigDecimal priceTax;

}
