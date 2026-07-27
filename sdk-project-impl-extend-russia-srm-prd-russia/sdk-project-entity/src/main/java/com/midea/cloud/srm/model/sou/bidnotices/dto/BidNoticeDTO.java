package com.midea.cloud.srm.model.sou.bidnotices.dto;


import com.midea.cloud.srm.model.common.BaseDTO;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author kuangzm
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/9 18:03:27
 *  修改内容:
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "BidNoticeDTO", description = "中/落标通知头表DTO")
public class BidNoticeDTO extends BaseDTO {

    @ApiModelProperty(value = "中落标通知ID", example = "1")
    private Long bidNoticeId;

    @ApiModelProperty(value = "中/落标通知单号", example = "202109210001")
    private String bidNoticeNo;

    @ApiModelProperty(value = "类型", example = "1")
    private String type;

    @ApiModelProperty(value = "单据状态", example = "1")
    private String status;

    @ApiModelProperty(value = "板块ID", example = "1")
    private Long extOrgBuId;

    @ApiModelProperty(value = "板块编码", example = "001")
    private String extOrgBuCode;

    @ApiModelProperty(value = "板块名称", example = "板块1")
    private String extOrgBuName;

    @ApiModelProperty(value = "公司ID", example = "1")
    private Long extOrgOuId;

    @ApiModelProperty(value = "公司编码", example = "001")
    private String extOrgOuCode;

    @ApiModelProperty(value = "公司名称", example = "公司1")
    private String extOrgOuName;

    @ApiModelProperty(value = "项目ID", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "项目名称", example = "项目1")
    private String souName;

    @ApiModelProperty(value = "寻源单号", example = "202109210001")
    private String souNo;

    @ApiModelProperty(value = "寻源类型", example = "1")
    private String souType;

    @ApiModelProperty(value = "招标项目编号", example = "001")
    private String extProjectNo;

    @ApiModelProperty(value = "需求部门ID", example = "1")
    private String demandDepartmentId;

    @ApiModelProperty(value = "需求部门编码", example = "001")
    private String demandDepartmentCode;

    @ApiModelProperty(value = "需求部门名称", example = "部门1")
    private String demandDepartmentName;

    @ApiModelProperty(value = "需求人id(可多选)", example = "1")
    private String demandUserId;

    @ApiModelProperty(value = "需求人工号", example = "001")
    private String demandUserName;

    @ApiModelProperty(value = "需求人名称", example = "张三")
    private String demandUserNickname;

    @ApiModelProperty(value = "招标技术负责人", example = "李四")
    private String extTechPrincipal;

    @ApiModelProperty(value = "联系电话", example = "13800138000")
    private String extTechPhone;

    @ApiModelProperty(value = "备注", example = "备注信息")
    private String remark;

    @ApiModelProperty(value = "原中落标通知ID", example = "1")
    private Long originalBidNoticeId;

    @ApiModelProperty(value = "原中/落标通知单号", example = "202109210001")
    private String originalBidNoticeNo;

    @ApiModelProperty(value = "废弃类型", example = "1")
    private String discardType;

    @ApiModelProperty(value = "废弃原因", example = "废弃原因信息")
    private String discardReason;

    @ApiModelProperty(value = "中/落标废弃原因", example = "废弃原因信息")
    private String abandonReason;

    @ApiModelProperty(value = "合并申请号", example = "PR2023092300101,PR2023092300102,PR2023092300103")
    private String applicantNo;

    @ApiModelProperty(value = "合并申请ID", example = "1,2,3")
    private String applicantId;

    @ApiModelProperty(value = "定/废标申请单ID", example = "1", required = true)
    private Long caId;

    @ApiModelProperty(value = "定/废标申请单单号", example = "CA20220101001", required = true)
    private String caNo;

    @ApiModelProperty(value = "bpm发起人账号")
    private String startBpmUsername;

    @ApiModelProperty(value = "bpm发起人名称")
    private String startBpmNickname;

    @ApiModelProperty(value = "废弃 bpm发起人账号")
    private String startBpmDiscardUsername;

    @ApiModelProperty(value = "废弃 bpm发起人名称")
    private String startBpmDiscardNickname;

    @ApiModelProperty("审批通过时间")
    private Date passTime;

    @ApiModelProperty(value = "中/落标信息")
    private List<BidNoticeDetailDTO> bidNoticeDetails;

    @ApiModelProperty(value = "内部通知书")
    private List<BidNoticeInternalDTO> bidNoticeInternals;

    @ApiModelProperty(value = "附件")
    private List<FileUpload> sceneFiles;
}

