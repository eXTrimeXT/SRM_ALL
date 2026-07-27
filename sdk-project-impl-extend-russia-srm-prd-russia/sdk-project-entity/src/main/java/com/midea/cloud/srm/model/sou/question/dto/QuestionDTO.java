package com.midea.cloud.srm.model.sou.question.dto;

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
 *  修改日期: 2023/10/17 08:49:50
 *  修改内容:
 * </pre>
 */


@Data
@ApiModel(value = "QuestionDTO", description = "供应商-质疑DTO")
public class QuestionDTO extends BaseDTO {

    @ApiModelProperty(value = "ID", example = "1")
    private Long questionId;

    @ApiModelProperty(value = "质疑编号", example = "Q0001", required = true)
    private String questionNum;

    @ApiModelProperty(value = "寻源模块基础表ID,该表与基础表为一对一关系", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "寻源单号", example = "S0001")
    private String souNo;

    @ApiModelProperty(value = "寻源单名称", example = "寻源单1")
    private String souName;

    @ApiModelProperty(value = "寻源类型", example = "类型1")
    private String souType;

    @ApiModelProperty(value = "质疑标题", example = "标题1")
    private String questionTitle;

    @ApiModelProperty(value = "提交时间", example = "2022-01-01 00:00:00")
    private Date submitTime;

    @ApiModelProperty(value = "质疑状态", example = "待处理")
    private String questionStatus;

    @ApiModelProperty(value = "废弃原因", example = "废弃原因")
    private String reasonDesc;

    @ApiModelProperty(value = "质疑内容", example = "内容1")
    private String questionContent;

    @ApiModelProperty(value = "驳回原因", example = "原因1")
    private String rejectReason;

    @ApiModelProperty(value = "驳回时间", example = "2022-01-02 00:00:00")
    private Date rejectTime;

    @ApiModelProperty(value = "供应商ID", example = "1")
    private Long vendorId;

    @ApiModelProperty(value = "供应商编号", example = "V0001")
    private String vendorCode;

    @ApiModelProperty(value = "供应商名称", example = "供应商1")
    private String vendorName;

    @ApiModelProperty(value = "供应商负责人ID", example = "3")
    private Long extVendorUserId;

    @ApiModelProperty(value = "供应商负责人账号", example = "user1")
    private String extVendorUsername;

    @ApiModelProperty(value = "供应商负责人名称", example = "张三")
    private String extVendorNickname;

    @ApiModelProperty(value = "招标负责人ID", example = "4")
    private Long extBidUserId;

    @ApiModelProperty(value = "招标负责人账号", example = "user2")
    private String extBidUsername;

    @ApiModelProperty(value = "招标负责人名称", example = "李四")
    private String extBidNickname;

    @ApiModelProperty(value = "评标组长用户ID", example = "5")
    private Long extBidEvalLeaderId;

    @ApiModelProperty(value = "评标组长名称", example = "王五")
    private String extBidEvalLeaderName;

    @ApiModelProperty(value = "类型", example = "SOU")
    private String extType;

    @ApiModelProperty(value = "是否推荐完成", example = "Y")
    private String extIfRecommendFinish;

    @ApiModelProperty(value = "回复内容")
    private String extReplayContent;
}

