package com.midea.cloud.srm.model.contract.dto;

import com.midea.cloud.srm.model.cm.contract.entity.PayPlan;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@Data
public class PayPlanExt extends PayPlan {

    @ApiModelProperty("承兑比例")
    private Integer extAcceptanceRatio;

    @ApiModelProperty("承兑时间")
    private Date extAcceptanceDate;

    @ApiModelProperty("电汇比例")
    private Date extWireTransferRatio;


}
