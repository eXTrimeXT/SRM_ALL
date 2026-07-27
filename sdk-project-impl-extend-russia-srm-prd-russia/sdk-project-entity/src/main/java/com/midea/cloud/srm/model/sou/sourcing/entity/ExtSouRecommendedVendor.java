package com.midea.cloud.srm.model.sou.sourcing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * 备注
 * @author huangbf3
 */
@Data
@ApiModel("寻源核心-推荐供应商要标的物信息表")
@TableName(value = "scc_sou_recomm_vendor")
public class ExtSouRecommendedVendor extends BaseEntity {


    @TableId("RECOMMENDED_VENDOR_ID")
    private Long recommendedVendorId;

    @TableField("PROJECT_ID")
    private Long projectId;

    /**
     * 推荐供应商单号
     */
    @TableField("EXT_RECOMMEND_NO")
    private String extRecommendNo;

    /**
     * 项目概述及招标范围
     */
    @TableField("PROJECT_REMARK")
    private String projectRemark;

    /**
     * 供应商资质要求
     */
    @TableField("VENDOR_FLAIR_ADJURE")
    private String vendorFlairAdjure;

    /**
     * 供应商业绩要求
     */
    @TableField("VENDOR_BIZ_ADJURE")
    private String vendorBizAdjure;

    /**
     * 前期招标情况
     */
    @TableField("PRE_INVITE_TENDERS")
    private String preInviteTenders;

    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;

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
