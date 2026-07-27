package com.midea.cloud.srm.model.sou.report.inq.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@ApiModel("报价率报表-DTO")
@Data
public class ExtInquiryPriceRateDto extends BaseDTO {

    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @ApiModelProperty("供应商名称")
    private String vendorName;

    @ApiModelProperty("询价方ID")
    private Long orgOuId;

    @ApiModelProperty("询价方编码")
    private String orgOuCode;

    @ApiModelProperty("询价方")
    private String orgOuName;

    @ApiModelProperty("询价单次数")
    private BigDecimal inqTimes;

    @ApiModelProperty("报价单次数")
    private BigDecimal orderTimes;

    @ApiModelProperty("报价参与率：报价单次数/询价单次数")
    private String orderJoinRate;

    @ApiModelProperty("报价物资项数")
    private BigDecimal itemTimes;
}
