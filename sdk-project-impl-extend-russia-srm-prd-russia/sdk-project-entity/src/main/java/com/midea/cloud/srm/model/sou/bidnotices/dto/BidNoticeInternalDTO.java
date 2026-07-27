package com.midea.cloud.srm.model.sou.bidnotices.dto;

import com.midea.cloud.srm.model.common.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

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
 *  修改日期: 2023/10/9 18:11:51
 *  修改内容:
 * </pre>
 */
@Data
@ApiModel(value = "BidNoticeInternalDTO", description = "内部通知书表")
public class BidNoticeInternalDTO extends BaseDTO {

    @ApiModelProperty(value = "内部通知ID", example = "1")
    private Long internalId;

    @ApiModelProperty(value = "中落标通知ID", example = "1")
    private Long bidNoticeId;

    @ApiModelProperty(value = "序号", example = "1")
    private Integer lineNum;

    @ApiModelProperty(value = "板块ID", example = "1")
    private Long extOrgBuId;

    @ApiModelProperty(value = "板块编码", example = "bu001")
    private String extOrgBuCode;

    @ApiModelProperty(value = "板块名称", example = "板块1")
    private String extOrgBuName;

    @ApiModelProperty(value = "公司ID", example = "1")
    private Long extOrgOuId;

    @ApiModelProperty(value = "公司编码", example = "ou001")
    private String extOrgOuCode;

    @ApiModelProperty(value = "公司名称", example = "公司1")
    private String extOrgOuName;

    @ApiModelProperty(value = "需求部门ID", example = "1", required = true)
    private String demandDepartmentId;

    @ApiModelProperty(value = "需求部门编码", example = "dept001", required = true)
    private String demandDepartmentCode;

    @ApiModelProperty(value = "需求部门名称", example = "需求部门1", required = true)
    private String demandDepartmentName;

    @ApiModelProperty(value = "通知书附件ID", example = "1")
    private Long attachmentId;

    @ApiModelProperty(value = "通知书附件名称", example = "通知书附件1")
    private String attachmentName;

    @ApiModelProperty(value = "是否发送", example = "是")
    private String isSend;

    @ApiModelProperty(value = "发送时间", example = "2022-01-01 00:00:00")
    private Date sendTime;

    @ApiModelProperty(value = "是否已盖章", example = "Y")
    private String isSign;

}
