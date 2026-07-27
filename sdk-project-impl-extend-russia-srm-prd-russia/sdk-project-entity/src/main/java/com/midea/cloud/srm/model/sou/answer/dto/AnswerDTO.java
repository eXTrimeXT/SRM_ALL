package com.midea.cloud.srm.model.sou.answer.dto;

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
 *  修改日期: 2023/10/17 11:45:39
 *  修改内容:
 * </pre>
 */


@Data
@ApiModel(value = "AnswerDTO", description = "采购商-澄清")
public class AnswerDTO extends BaseDTO {

    @ApiModelProperty(value = "ID", example = "1")
    private Long answerId;

    @ApiModelProperty(value = "澄清编号", example = "CL0001*")
    private String answerNum;

    @ApiModelProperty(value = "质疑ID", example = "1")
    private Long questionId;

    @ApiModelProperty(value = "质疑编号", example = "Q0001")
    private String questionNum;

    @ApiModelProperty(value = "寻源模块基础表ID,该表与基础表为一对一关系", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "寻源单号", example = "S0001")
    private String souNo;

    @ApiModelProperty(value = "寻源单名称", example = "寻源单1")
    private String souName;

    @ApiModelProperty(value = "寻源类型", example = "线上寻源")
    private String souType;

    @ApiModelProperty(value = "提交时间", example = "2021-01-01 12:00:00")
    private Date submitTime;

    @ApiModelProperty(value = "澄清状态[字典值: BRG_ANSWER_STATUS]", example = "待审核")
    private String answerStatus;

    @ApiModelProperty(value = "废弃原因", example = "废弃原因")
    private String reasonDesc;

    @ApiModelProperty(value = "澄清标题", example = "关于寻源单1的澄清")
    private String answerTitle;

    @ApiModelProperty(value = "澄清内容", example = "请问寻源单1中的物料是否可替换？")
    private String answerContent;

    @ApiModelProperty(value = "招标负责人ID", example = "1")
    private Long extBidUserId;

    @ApiModelProperty(value = "招标负责人账号", example = "admin")
    private String extBidUsername;

    @ApiModelProperty(value = "招标负责人名称", example = "管理员")
    private String extBidNickname;

    @ApiModelProperty(value = "澄清来源（字典ANSWER_SOURCE_TYPE）", example = "澄清来源")
    private String extSource;

    /**
     * 回复ID
     */
    private Long replayId;

    /**
     * 确认状态
     */
    private String confirmStatus;

    /**
     * 澄清供应商ID
     */
    private Long answerVendorId;

    /**
     * 已读数量
     */
    private Integer readNum;

    /**
     * 已回复数量
     */
    private Integer replayNum;

    /**
     * 是否已回复
     */
    private String ifReplay;

    /**
     * extReplayFlag
     */
    private String extReplayFlag;

    /**
     * 供应商名字
     */
    private String vendorName;
}

