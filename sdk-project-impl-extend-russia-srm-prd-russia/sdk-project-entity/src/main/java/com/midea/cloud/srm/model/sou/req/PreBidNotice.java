package com.midea.cloud.srm.model.sou.req;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * 标前交流通知表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-11-13
 */
@ApiModel(description = "标前交流通知表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("scc_npm_pre_bid_notice")
public class PreBidNotice extends BaseEntity {
    @ApiModelProperty("主键")
    @TableId
    private Long bidNoticeId;

    @ApiModelProperty("标前交流通知单号")
    private String bidNoticeNo;

    @ApiModelProperty("标前交流通知单标题")
    private String bidNoticeTitle;

    @ApiModelProperty("申请单ID")
    private Long requirementHeadId;

    @ApiModelProperty("申请单号")
    private String requirementHeadNo;

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

    @ApiModelProperty("招标负责人用户ID")
    private Long bidUserId;

    @ApiModelProperty("招标负责人用户名称")
    private String bidUserNickname;

    @ApiModelProperty("供应商负责人联系电话")
    private String vendorUserPhone;

    @ApiModelProperty("供应商负责人办公电话")
    private String vendorUserOfficePhone;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("标前交流通知-技术交流供应商表")
    private List<PreBidNoticeVendor> noticeVendorList;

    @ApiModelProperty("交流大纲签字版附件")
    private List<SceneFile> commSignFiles;

    @ApiModelProperty("交流大纲编辑附件")
    private List<SceneFile> commEditFiles;
}
