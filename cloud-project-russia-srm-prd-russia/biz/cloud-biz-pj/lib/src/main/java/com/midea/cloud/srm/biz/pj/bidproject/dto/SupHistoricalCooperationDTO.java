package com.midea.cloud.srm.biz.pj.bidproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author GW00311146
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "根据供应商ids获取对应参与率、中标率、被邀请率DTO")
public class SupHistoricalCooperationDTO {
    /** 寻源单ID */
    @ApiModelProperty(value = "寻源单ID")
    @JsonProperty("project_id")
    private Long projectId;
    /** 寻源单号 */
    @ApiModelProperty(value = "寻源单号")
    @JsonProperty("sou_no")
    private String souNo;
    /** 寻源名称 */
    @ApiModelProperty(value = "寻源名称")
    @JsonProperty("sou_name")
    private String souName;
    /** 品类ID */
    @ApiModelProperty(value = "品类ID")
    @JsonProperty("ext_category_id")
    private Long extCategoryId;
    /** 品类编码*/
    @ApiModelProperty(value = "品类编码")
    @JsonProperty("ext_category_code")
    private String extCategoryCode;
    /** 品类*/
    @ApiModelProperty(value = "品类")
    @JsonProperty("ext_category_name")
    private String extCategoryName;
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
    /** 是否中标 */
    @ApiModelProperty(value = "是否中标")
    @JsonProperty("is_win")
    private String isWin;
    /** 中标范围 */
    @ApiModelProperty(value = "中标范围")
    @JsonProperty("win_range")
    private String winRange;
    /** 投标时间*/
    @ApiModelProperty(value = "投标时间")
    @JsonProperty("submit_time")
    private Date submitTime;



}
