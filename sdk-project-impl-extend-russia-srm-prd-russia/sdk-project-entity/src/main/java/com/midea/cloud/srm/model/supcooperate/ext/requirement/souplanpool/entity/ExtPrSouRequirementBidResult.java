package com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 招标计划 - 定标信息
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/28
 */
@Data
@TableName("scc_npm_pr_require_bid_result")
@EqualsAndHashCode(callSuper = true)
public class ExtPrSouRequirementBidResult extends BaseEntity<ExtPrSouRequirementBidResult> {

    @TableId("BID_RESULT_ID")
    @ApiModelProperty("ID")
    private Long bidResultId;

    @TableField("REQUIREMENT_HEAD_ID")
    @ApiModelProperty("招标计划ID")
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

    @TableField("LINKMAN")
    @ApiModelProperty("供应商联系人")
    private String linkman;

    @TableField("PHONE")
    @ApiModelProperty("联系人电话")
    private String phone;

    @TableField("BID_AMOUNT_BY_TEN_KILO")
    @ApiModelProperty("定标金额(万元)")
    private BigDecimal bidAmountByTenKilo;

}
