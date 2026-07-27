package com.midea.cloud.srm.biz.pj.bidproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author GW00311146
 */

@Data
@Accessors(chain = true)
@ApiModel(description = "根据供应商ids获取对应参与率、中标率、被邀请率DTO")
public class SupWinRateDTO {
    /** 供应商ID */
    @ApiModelProperty(value = "供应商ID")
    @JsonProperty("vendor_id")
    private Long vendorId;
    /** 供应商编码 */
    @ApiModelProperty(value = "供应商编码")
    @JsonProperty("vendor_code")
    private String vendorCode;
    /** 供应商编码 */
    @ApiModelProperty(value = "供应商名称")
    @JsonProperty("vendor_name")
    private String vendorName;
    /** 被邀请次数 */
    @ApiModelProperty(value = "被邀请次数")
    @JsonProperty("inv_times")
    private Long invTimes;
    /** 参与投标有效性次数*/
    @ApiModelProperty(value = "参与投标有效性次数")
    @JsonProperty("cy_times")
    private Long cyTimes;
    /** 中标次数*/
    @ApiModelProperty(value = "中标次数")
    @JsonProperty("zb_times")
    private Long zbTimes;
    /** 推荐供应商中，供应商属性是①的次数*/
    @ApiModelProperty(value = "推荐供应商中，供应商属性是①的次数")
    @JsonProperty("tj1_times")
    private Long tj1Times;
    /** 参与率*/
    @ApiModelProperty(value = "参与率")
    @JsonProperty("cy_rate")
    private BigDecimal cyRate;
    /** 中标率*/
    @ApiModelProperty(value = "中标率")
    @JsonProperty("zb_rate")
    private BigDecimal zbRate;
    /** 中标率*/
    @ApiModelProperty(value = "被推荐率")
    @JsonProperty("tj1_rate")
    private BigDecimal tj1Rate;

}
