package com.midea.cloud.srm.model.sou.recommvendor.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel(description = "供应商推荐主表扩展表DTO")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class RecommvendorProjectExtendDto extends BaseDTO {

    @ApiModelProperty("ID")
    private Long recommendedVendorId;
    @ApiModelProperty("寻源单ID")
    private Long projectId;
    @ApiModelProperty("排序")
    private Integer sortIndex;
    @ApiModelProperty("项目概述及招标范围")
    private String projectRemark;
    @ApiModelProperty("供应商资质要求")
    private String vendorFlairAdjure;
    @ApiModelProperty("供应商业绩要求")
    private String vendorBizAdjure;
    @ApiModelProperty("前期招标情况")
    private String preInviteTenders;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("推荐供应商单号")
    private String extRecommendNo;
    @ApiModelProperty("推荐类型")
    private String rcommendType;
    @ApiModelProperty("是否公示")
    private String publishFlag;

    @ApiModelProperty("原推荐单号")
    private String originalExtRecommendNo;
    @ApiModelProperty("原推荐单ID")
    private Long originalProjectId;
    @ApiModelProperty("追加供应商原因")
    private String addVendorReason;
    @ApiModelProperty("单据来源")
    private String sourceFrom;

    @ApiModelProperty("寻源单号")
    private String souRequirementNo;

    @ApiModelProperty("寻源单ID")
    private Long souRequirementId;


}
