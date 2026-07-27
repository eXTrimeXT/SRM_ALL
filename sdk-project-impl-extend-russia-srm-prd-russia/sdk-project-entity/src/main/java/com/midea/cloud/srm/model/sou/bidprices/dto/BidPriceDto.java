
package com.midea.cloud.srm.model.sou.bidprices.dto;

/**
 * <pre>
 *
 * </pre>
 *
 * @author panmq
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/11/27 10:49:18
 *  修改内容:
 * </pre>
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.midea.cloud.srm.model.common.BaseDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaOrderDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaSelectionResultDTO;
import com.midea.cloud.srm.model.sou.ca.dto.CaSupplierDTO;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "招标价格库DTO")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class BidPriceDto extends BaseDTO {

    @ApiModelProperty(value = "ID")
    private Long bidPriceId;
    @ApiModelProperty(value = "父ID，当父ID等于-1时，表示头表")
    private Long parentBidPriceId;
    @ApiModelProperty(value = "报价单明细ID")
    private Long orderItemId;
    @ApiModelProperty(value = "供应商ID")
    private Long vendorId;
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;
    @ApiModelProperty(value = "寻源单ID")
    private Long projectId;
    @ApiModelProperty(value = "招标项目编号")
    private String projectNo;
    @ApiModelProperty(value = "寻源名称")
    private String souName;
    /**
     * 板块ID
     */
    @ApiModelProperty(value = "板块ID")
    private Long extOrgBuId;
    /**
     * 板块编码
     */
    @ApiModelProperty(value = "板块编码")
    private String extOrgBuCode;
    /**
     * 板块名称
     */
    @ApiModelProperty(value = "板块名称")
    private String extOrgBuName;
    /**
     * 公司ID
     */
    @ApiModelProperty(value = "公司ID")
    private Long extOrgOuId;
    /**
     * 公司编码
     */
    @ApiModelProperty(value = "公司编码")
    private String extOrgOuCode;
    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String extOrgOuName;
    @ApiModelProperty(value = "招标负责人ID")
    private Long souPrincipalUserId;
    @ApiModelProperty(value = "招标负责人账号")
    private String souPrincipalUserName;
    @ApiModelProperty(value = "招标负责人")
    private String souPrincipal;
    @ApiModelProperty(value = "品类ID")
    private Long categoryId;
    @ApiModelProperty(value = "品类编码")
    private String categoryCode;
    @ApiModelProperty(value = "品类名称")
    private String categoryName;
    @ApiModelProperty(value = "物料ID")
    private Long itemId;
    @ApiModelProperty(value = "物料编码")
    private String itemCode;
    @ApiModelProperty(value = "名称")
    private String itemDesc;
    @ApiModelProperty(value = "规格/型号")
    private String specification;
    @ApiModelProperty(value = "品牌")
    private String brand;
    @ApiModelProperty(value = "项目特征")
    private String feature;
    @ApiModelProperty(value = "施工内容")
    private String constructionItem;
    @ApiModelProperty(value = "数量/工程量")
    private BigDecimal quantity;
    @ApiModelProperty(value = "未税单价（万元）")
    private BigDecimal priceNoTax;
    @ApiModelProperty(value = "未税总价（万元）")
    private BigDecimal priceSumNoTax;
    @ApiModelProperty(value = "含税单价（万元）")
    private BigDecimal priceTax;
    @ApiModelProperty(value = "含税总价（万元）")
    private BigDecimal priceSumTax;
    @ApiModelProperty(value = "发票类型")
    private String invoiceType;
    @ApiModelProperty(value = "税率（%）")
    private BigDecimal taxRate;
    @ApiModelProperty(value = "币种")
    private String currency;
    @ApiModelProperty(value = "标段")
    private String bidSection;
    @ApiModelProperty(value = "区域")
    private String region;
    @ApiModelProperty(value = "单位")
    private String unit;
    @ApiModelProperty(value = "暂定数量/工程量")
    private BigDecimal requireQuantity;
    @ApiModelProperty(value = "分项")
    private String subitem;
    @ApiModelProperty(value = "固定未税单价（万元）")
    private BigDecimal fixedPriceNoTax;
    @ApiModelProperty(value = "暂定未税总价（万元）")
    private BigDecimal provPriceSumNoTax;
    @ApiModelProperty(value = "固定含税单价（万元）")
    private BigDecimal fixedPriceTax;
    @ApiModelProperty(value = "暂定含税总价（万元）")
    private BigDecimal provPriceSumTax;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "产品类型")
    private String productType;
    @ApiModelProperty(value = "产品配置")
    private String productConfig;
}

