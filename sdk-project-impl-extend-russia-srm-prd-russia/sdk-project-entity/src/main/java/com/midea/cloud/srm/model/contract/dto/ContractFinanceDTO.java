package com.midea.cloud.srm.model.contract.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class ContractFinanceDTO {
    /**
     * 计价方式：1.固定总价 2.无固定总价 3.无金额
     */
    private Integer priceType;
    /**
     * 合同金额
     */
    private BigDecimal amount;
    /**
     * 币种
     */
    private Integer currency;
    /**
     * 金额收支方向
     */
    private Integer payDirection;
    /**
     * 付款方式
     */
    private String paymentType;
    /**
     * 暂估金额
     */
    private Integer estimateAmount;

}