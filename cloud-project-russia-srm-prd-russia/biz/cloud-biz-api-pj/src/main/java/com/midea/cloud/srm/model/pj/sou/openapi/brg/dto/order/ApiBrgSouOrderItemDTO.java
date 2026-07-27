package com.midea.cloud.srm.model.pj.sou.openapi.brg.dto.order;

import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouOrderItem;
import com.midea.cloud.srm.model.pj.sou.brg.entity.BrgSouOrderItemPayment;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 项目式询价openAPI - 报价明细
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiBrgSouOrderItemDTO extends ApiSouOrderItemDTO {

    /** @see BrgSouOrderItem#getTransportType */
    @ApiModelProperty("运输方式(字典值: TRANSF_TYPE)")
    private String transportType;

    /** @see BrgSouOrderItem#getMqo */
    @ApiModelProperty("最小订单量")
    private String mqo;

    /** @see BrgSouOrderItem#getLeadTime */
    @ApiModelProperty("供货周期")
    private String leadTime;

    /** @see BrgSouOrderItem#getWarrantyPeriod */
    @ApiModelProperty("保修期")
    private Integer warrantyPeriod;

    /** @see BrgSouOrderItem#getDeliverDate */
    @ApiModelProperty("承诺交货期")
    private Date deliverDate;

    /** @see BrgSouOrderItem#getFormulaResult */
    @ApiModelProperty("供应商填写的公式报价json")
    private String formulaResult;

    @ApiModelProperty("账期")
    private List<BrgSouOrderItemPayment> paymentList;

}
