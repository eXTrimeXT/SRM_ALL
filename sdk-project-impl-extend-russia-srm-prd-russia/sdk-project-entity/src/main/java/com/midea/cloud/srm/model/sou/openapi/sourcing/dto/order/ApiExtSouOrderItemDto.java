package com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouOrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
/**
 * 备注
 * @author huangbf3
 */
@ApiModel("寻源核心-报价单行表DTO")
@Data
public class ApiExtSouOrderItemDto extends ExtSouOrderItem {

    /**
     * 标段
     */
    private String extBidSection;
    /**
     * 区域
     */
    private String extRegion;
    /**
     * 品牌
     */
    private String extBrand;
    /**
     * 固定未税单价（万元）
     */
    private BigDecimal extFixedPriceNoTax;
    /**
     * 暂定未税总价（万元）
     */
    private BigDecimal extProvPriceSumNoTax;
    /**
     * 固定含税单价（万元）
     */
    private BigDecimal extFixedPriceTax;
    /**
     * 暂定含税总价（万元）
     */
    private BigDecimal extProvPriceSumTax;
    /**
     * 项目特征
     */
    private String extFeature;
    /**
     * 施工内容
     */
    private String extConstructionItem;
    /**
     * 数量/工程量
     */
    private BigDecimal extQuantity;
    /**
     * 未税单价（万元）
     */
    private BigDecimal extPriceNoTax;
    /**
     * 未税总价（万元）
     */
    private BigDecimal extPriceSumNoTax;
    /**
     * 含税单价（万元）
     */
    private BigDecimal extPriceTax;
    /**
     * 含税总价（万元）
     */
    private BigDecimal extPriceSumTax;
    /**
     * 发票类型
     */
    private String extInvoiceType;
    /**
     * 税率（%）
     */
    private BigDecimal extTaxRate;
    /**
     * 币种
     */
    private String extCurrency;
    /**
     * 供应商备注
     */
    private String extSubitem;

    /**
     * 未税/固定单价（万元）
     */
    private BigDecimal extPriceOrFixedNoTax;

    /**
     * 包名
     */
    @ApiModelProperty("包名")
    private String extPackageName;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("供应商名称")
    private String vendorName;

    /**
     * 板块ID
     */
    private Long extOrgBuId;
    /**
     * 板块编码
     */
    private String extOrgBuCode;
    /**
     * 板块名称
     */
    private String extOrgBuName;
    /**
     * 公司ID
     */
    private Long extOrgOuId;
    /**
     * 公司编码
     */
    private String extOrgOuCode;
    /**
     * 公司名称
     */
    private String extOrgOuName;

    @ApiModelProperty("投标时间")
    private Date submitTime;

    @ApiModelProperty("组织报价原因")
    private String extOrderReason;

    @ApiModelProperty("备注")
    private String remark;

    /**
     * 转换成报价字段
     */
    public void coverOrderFields() {
        setOrderCurrency(this.extCurrency);
        setStandardNotaxPrice(this.extFixedPriceNoTax);
        setStandardNotaxGroupPrice(this.extProvPriceSumNoTax);
        setStandardTaxPrice(this.extFixedPriceTax);
        setStandardTaxGroupPrice(this.extProvPriceSumTax);
        setOrderNotaxPrice(this.extPriceNoTax);
        setOrderNotaxGroupPrice(this.extPriceSumNoTax);
        setOrderTaxPrice(this.extPriceTax);
        setOrderTaxGroupPrice(this.extPriceSumTax);
        setTaxRate(this.extTaxRate);


    }

    /**
     * 转换成报价模板字段
     */
    public void coverItemFields() {
        this.extCurrency = getOrderCurrency();
        this.extFixedPriceNoTax = getStandardNotaxPrice();
        this.extProvPriceSumNoTax = getStandardNotaxGroupPrice();
        this.extFixedPriceTax = getStandardTaxPrice();
        this.extProvPriceSumTax = getStandardTaxGroupPrice();
        this.extPriceNoTax = getOrderNotaxPrice();
        this.extPriceSumNoTax = getOrderNotaxGroupPrice();
        this.extPriceTax = getOrderTaxPrice();
        this.extPriceSumTax = getOrderTaxGroupPrice();
        this.extTaxRate = getTaxRate();
    }

    /**
     * 按报价模板字段转换税率
     */
    public void convertExchangeRateAsItemFields() {
        //固定未税单价（万元）
        this.extFixedPriceNoTax = priceAsExchangeRate(this.extFixedPriceNoTax, this.getExtExchangeRate());
        //暂定未税总价（万元）
        this.extProvPriceSumNoTax = priceAsExchangeRate(this.extProvPriceSumNoTax, this.getExtExchangeRate());
        //固定含税单价（万元）
        this.extFixedPriceTax = priceAsExchangeRate(this.extFixedPriceTax, this.getExtExchangeRate());
        //暂定含税总价（万元）
        this.extProvPriceSumTax = priceAsExchangeRate(this.extProvPriceSumTax, this.getExtExchangeRate());
        //未税单价（万元）
        this.extPriceNoTax = priceAsExchangeRate(this.extPriceNoTax, this.getExtExchangeRate());
        //未税总价（万元）
        this.extPriceSumNoTax = priceAsExchangeRate(this.extPriceSumNoTax, this.getExtExchangeRate());
        //含税单价（万元）
        this.extPriceTax = priceAsExchangeRate(this.extPriceTax, this.getExtExchangeRate());
        //含税总价（万元）
        this.extPriceSumTax = priceAsExchangeRate(this.extPriceSumTax, this.getExtExchangeRate());
    }

    private BigDecimal priceAsExchangeRate(BigDecimal price, BigDecimal rate) {
        if(Objects.isNull(price)) {
            return price;
        }
        return price.multiply(ObjectUtils.defaultIfNull(rate, BigDecimal.ONE));
    }
}
