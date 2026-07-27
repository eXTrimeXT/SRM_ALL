package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (非材) 招标计划推荐供应商表
 * @author huangbf3
 */
@Data
@TableName("scc_npm_pr_require_vendor")
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementVendor extends BaseEntity<ExtPrSouRequirementVendor> {

    @TableId("REQUIREMENT_VENDOR_ID")
    @ApiModelProperty("主键ID")
    private Long requirementVendorId;

    /** @see PrRequirementHead#getRequirementHeadId */
    @ApiModelProperty("招标计划ID")
    @TableField("REQUIREMENT_HEAD_ID")
    private Long requirementHeadId;

    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @TableField("VENDOR_CODE")
    @ApiModelProperty("供应商编码")
    private String vendorCode;

    @TableField("VENDOR_NAME")
    @ApiModelProperty("供应商名称")
    private String vendorName;

    @TableField("CONTACT_NAME")
    @ApiModelProperty("联系人名称")
    private String contactName;

    @TableField("PHONE")
    @ApiModelProperty("联系方式")
    private String phone;

    @TableField("EMAIL")
    @ApiModelProperty("邮箱")
    private String email;

    @TableField("RECOMMEND_FROM")
    @ApiModelProperty("推荐来源")
    private String recommendFrom;

    @TableField("SORT_INDEX")
    @ApiModelProperty("排序")
    private Integer sortIndex;

}
