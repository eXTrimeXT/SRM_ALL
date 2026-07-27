package com.midea.cloud.srm.model.sou.agreement.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreementInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zenghx2
 * 物料+区域价格查询
 */
@Data
public class PriceAgreementDTO extends SccSouJcAgreementInfo {

    @ApiModelProperty("公司主体,必须带有发票信息的类型为公司的组织")
    @TableField("COMPANY_ID")
    private Long companyId;

    @ApiModelProperty("供应区域")
    @TableField("SUPPLY_AREA")
    private String supplyArea;

    @ApiModelProperty("供应商")
    @TableField("SUP_ID")
    private Long supId;
    @ApiModelProperty("供应商编码")
    @TableField("SUP_CODE")
    private String supCode;
    @ApiModelProperty("供应商名称")
    @TableField("SUP_NAME")
    private String supName;

    @ApiModelProperty("币种")
    @TableField("CURRENCY_TYPE")
    private String currencyType;

    @ApiModelProperty("发票类型")
    @TableField("INVOICE_TYPE")
    private String invoiceType;

    @ApiModelProperty("付款条款")
    @TableField("PAYMENT")
    private String payment;

    @ApiModelProperty("付款方式")
    @TableField("PAY_WAY")
    private String payWay;

    @ApiModelProperty("品牌")
    @TableField("BRAND")
    private String brand;

    @ApiModelProperty("协议类型")
    @TableField("AGREEMENT_TYPE")
    private String agreementType;
}
