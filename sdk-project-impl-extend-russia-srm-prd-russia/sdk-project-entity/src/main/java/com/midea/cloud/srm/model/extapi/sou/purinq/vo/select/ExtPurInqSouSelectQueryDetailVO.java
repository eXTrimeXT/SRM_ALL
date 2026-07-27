package com.midea.cloud.srm.model.extapi.sou.purinq.vo.select;

import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ApiPurInqSouOrderItemVO;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.init.ApiInqSouItemVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 100014337
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtPurInqSouSelectQueryDetailVO extends ApiInqSouItemVO {

    @ApiModelProperty("轮次")
    private Integer round;
    @ApiModelProperty("报价次数")
    private Integer orderCount;
    @ApiModelProperty("中标供应商ID")
    private Long winVendorId;
    @ApiModelProperty("中标供应商编码")
    private String winVendorCode;
    @ApiModelProperty("中标供应商名称")
    private String winVendorName;
    @ApiModelProperty("中标税率编码")
    private String winTaxKey;
    @ApiModelProperty("中标税率")
    private BigDecimal winTaxRate;
    @ApiModelProperty("中标未税单价")
    private BigDecimal winStandardNotaxPrice;
    @ApiModelProperty("中标未税总价")
    private BigDecimal winStandardTotalPrice;
    @ApiModelProperty("中标发票类型")
    private String winInvoiceType;
    @ApiModelProperty("中标供货周期")
    private Integer winExtLeadTime;
    @ApiModelProperty("中标保修期(保质期)")
    private Integer winExtWarrantyPeriod;

    /** @see ExtPurInqSouItem#getDesignVendorId */
    @ApiModelProperty("历史供应商ID")
    private Long designVendorId;

    /** @see ExtPurInqSouItem#getDesignVendorCode */
    @ApiModelProperty("历史供应商编码")
    private String designVendorCode;

    /** @see ExtPurInqSouItem#getDesignVendorName */
    @ApiModelProperty("历史供应商名称")
    private String designVendorName;

    /** @see ExtPurInqSouItem#getDesignNotaxPrice */
    @ApiModelProperty("历史未税价格")
    private BigDecimal designNotaxPrice;

    @ApiModelProperty("是否已定价")
    private Enable hasFixPrice;

    @ApiModelProperty("指定轮次供应商报价详情")
    private List<ApiPurInqSouOrderItemVO> orderItemList;

}