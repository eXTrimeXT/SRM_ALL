package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMarginRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("退款、扣款历史")
public class ExtSouMarginRecordDto extends ExtSouMarginRecord {

    /**
     * 可退款金额（万元）
     */
    private BigDecimal refundAmount;

    /**
     * 原招标单ID，年度保证金用
     */
    private Long oldProjectId;
}
