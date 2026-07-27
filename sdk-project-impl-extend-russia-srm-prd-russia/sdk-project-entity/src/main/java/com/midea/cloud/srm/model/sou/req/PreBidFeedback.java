package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 标前交流反馈表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-11-13
 */
@ApiModel(description = "标前交流反馈表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("scc_npm_pre_bid_feedback")
public class PreBidFeedback extends BaseEntity {
    @ApiModelProperty("主键")
    @TableId
    private Long bidFeedbackId;

    @ApiModelProperty("标前交流反馈单号")
    private String bidFeedbackNo;

    @ApiModelProperty("标前交流通知主键")
    private Long bidNoticeId;

    @ApiModelProperty("标前交流通知单号")
    private String bidNoticeNo;

    @ApiModelProperty("标前交流通知单标题")
    private String bidNoticeTitle;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("板块ID")
    private Long orgBuId;

    @ApiModelProperty("板块编码")
    private String orgBuCode;

    @ApiModelProperty("板块名称")
    private String orgBuName;

    @ApiModelProperty("公司ID(对应产品的业务实体id)")
    private Long orgId;

    @ApiModelProperty("公司编码(对应产品的业务实体编码)")
    private String orgCode;

    @ApiModelProperty("公司名称(对应产品的业务实体名称)")
    private String orgName;

    @ApiModelProperty("需求部门ID")
    private String demandDepartmentId;

    @ApiModelProperty("需求部门编码")
    private String demandDepartmentCode;

    @ApiModelProperty("需求部门名称")
    private String demandDepartmentName;

    @ApiModelProperty("单据状态")
    private String status;

    @ApiModelProperty("需求人id(跟申请单保持类型一致)")
    private String demandUserId;

    @ApiModelProperty("需求人工号")
    private String demandUserName;

    @ApiModelProperty("需求人名称")
    private String demandUserNickname;

    @ApiModelProperty("供应商负责人用户ID")
    private Long vendorUserId;

    @ApiModelProperty("供应商负责人用户名称")
    private String vendorUserNickname;

    @ApiModelProperty("供应商负责人联系电话")
    private String vendorUserPhone;

    @ApiModelProperty("供应商负责人办公电话")
    private String vendorUserOfficePhone;

    @ApiModelProperty("备注")
    private String remark;

}
