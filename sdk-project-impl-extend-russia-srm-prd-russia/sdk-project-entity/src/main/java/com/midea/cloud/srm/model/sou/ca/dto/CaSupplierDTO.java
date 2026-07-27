package com.midea.cloud.srm.model.sou.ca.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/5 10:58:53
 *  修改内容:
 * </pre>
 */

@Data
public class CaSupplierDTO extends BaseDTO {

    @ApiModelProperty(value = "供应商总体情况ID", example = "1")
    private Long caSupplierId;

    @ApiModelProperty(value = "序号", example = "1")
    private Integer lineNum;

    @ApiModelProperty(value = "定标申请ID", example = "1")
    private Long caId;

    @ApiModelProperty(value = "供应商ID", example = "1")
    private Long vendorId;

    @ApiModelProperty(value = "供应商编码", example = "V001")
    private String vendorCode;

    @ApiModelProperty(value = "供应商名称", example = "ABC供应商")
    private String vendorName;

    @ApiModelProperty(value = "投标含税总价（万元）", example = "100.00")
    private BigDecimal bidTotalPrice;

    @ApiModelProperty(value = "技术得分", example = "80.000")
    private BigDecimal techScore;

    @ApiModelProperty(value = "价格得分", example = "90.000")
    private BigDecimal priceScore;

    @ApiModelProperty(value = "综合得分", example = "85.000")
    private BigDecimal compositeScore;

    @ApiModelProperty(value = "综合评定", example = "优秀")
    private String comprehensiveEvaluation;

    @ApiModelProperty(value = "报价行ID", example = "1")
    private Long orderItemId;

    @ApiModelProperty(value = "报价单ID", example = "1")
    private Long orderId;

}
