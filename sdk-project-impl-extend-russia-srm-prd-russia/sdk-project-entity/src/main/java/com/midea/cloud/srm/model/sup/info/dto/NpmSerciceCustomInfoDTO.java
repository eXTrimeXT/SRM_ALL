package com.midea.cloud.srm.model.sup.info.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
public class NpmSerciceCustomInfoDTO {

    @ApiModelProperty("身份证号码*")
    private String lcCode;

    @ApiModelProperty("公司ID")
    private Long vendorId;

    @ApiModelProperty("品类服务头表ID")
    private Long categoryJournalId;

    @ApiModelProperty("品类编码*")
    private String categoryCode;

    @ApiModelProperty("品类名称*")
    private String categoryName;

    @ApiModelProperty("联系人")
    private String contactName;

    @ApiModelProperty("业绩额（万元）")
    private Double performanceAmount;

    @ApiModelProperty("主要客户")
    private String mainCustom;
}
