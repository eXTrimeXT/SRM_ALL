package com.midea.cloud.srm.model.sou.ca.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-19
 */
@ApiModel("定标申请历史价格")
@Data
public class CaPriceDto extends BaseDTO {

    @ApiModelProperty("主键ID")
    private Long caPriceId;

    @ApiModelProperty("定标申请单ID")
    private Long caId;

    @ApiModelProperty("招标价格库ID")
    private Long bidPriceId;
    @ApiModelProperty(value = "招标项目编号")
    private String projectNo;
    @ApiModelProperty(value = "寻源名称")
    private String souName;
    @ApiModelProperty(value = "名称")
    private String itemDesc;
    @ApiModelProperty(value = "规格/型号")
    private String specification;
    @ApiModelProperty(value = "品牌")
    private String brand;
    @ApiModelProperty(value = "含税单价（万元）")
    private BigDecimal priceTax;
    @ApiModelProperty(value = "固定含税单价（万元）")
    private BigDecimal fixedPriceTax;
    @ApiModelProperty(value = "含税总价（万元")
    private BigDecimal priceSumTax;
    @ApiModelProperty(value = "区域")
    private String region;
    @ApiModelProperty(value = "招标负责人ID")
    private Long souPrincipalUserId;
    @ApiModelProperty(value = "招标负责人账号")
    private String souPrincipalUserName;
    @ApiModelProperty(value = "招标负责人")
    private String souPrincipal;
    @ApiModelProperty(value = "定标时间")
    private Date bidDate;
}
