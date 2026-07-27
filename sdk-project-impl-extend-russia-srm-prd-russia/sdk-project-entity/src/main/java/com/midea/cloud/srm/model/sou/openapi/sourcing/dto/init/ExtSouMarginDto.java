package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("年度保证金")
public class ExtSouMarginDto extends ExtSouMargin {

    /**
     * 招标项目编号
     */
    @ApiModelProperty("来源单号")
    private String extProjectNo;

    /**
     * 招标项目名称
     */
    @ApiModelProperty("缴纳来源")
    private String souName;

    /**
     * 是否缴纳保证金
     */
    private String extEarnestFlag;
    /**
     * 保证金缴纳金额（万元）
     */
    private BigDecimal extEarnestAmount;
    /**
     * 开户银行
     */
    private String extBankName;
    /**
     * 开户行号
     */
    private String extBankNumber;
    /**
     * 开户账号
     */
    private String extBankAccount;
    /**
     * 开户户名
     */
    private String extBankAccountName;
}
