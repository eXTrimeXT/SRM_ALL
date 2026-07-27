package com.midea.cloud.srm.model.pj.sou.qa.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.qa.enums.SouQuestionStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 供应商-质疑表
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/21
 */
@Data
@ApiModel("供应商-质疑")
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_question")
public class SouQuestion extends BaseEntity<SouQuestion> {
    private static final long serialVersionUID = 1L;

    @TableId("QUESTION_ID")
    @ApiModelProperty("ID")
    private Long questionId;

    @TableField("QUESTION_NUM")
    @ApiModelProperty("质疑编号")
    private String questionNum;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源模块基础表ID,该表与基础表为一对一关系")
    @TableField("PROJECT_ID")
    private Long projectId;

    /** @see SouProject#getSouNo */
    @ApiModelProperty("寻源单号")
    @TableField("SOU_NO")
    private String souNo;

    /** @see SouProject#getSouName */
    @ApiModelProperty("寻源单名称")
    @TableField("SOU_NAME")
    private String souName;

    /** @see SouTypeEnum */
    @ApiModelProperty("寻源类型")
    @TableField("SOU_TYPE")
    private String souType;

    @TableField("QUESTION_TITLE")
    @ApiModelProperty("质疑标题")
    private String questionTitle;

    @ApiModelProperty("提交时间")
    @TableField("SUBMIT_TIME")
    private Date submitTime;

    @TableField("QUESTION_STATUS")
    @ApiModelProperty("质疑状态")
    private SouQuestionStatusEnum questionStatus;

    @ApiModelProperty("质疑内容")
    @TableField("QUESTION_CONTENT")
    private String questionContent;

    @ApiModelProperty("驳回原因")
    @TableField("REJECT_REASON")
    private String rejectReason;

    @ApiModelProperty("驳回时间")
    @TableField("REJECT_TIME")
    private Date rejectTime;

    @TableField("VENDOR_ID")
    @ApiModelProperty("供应商ID")
    private Long vendorId;

    @TableField("VENDOR_CODE")
    @ApiModelProperty("供应商编号")
    private String vendorCode;

    @TableField("VENDOR_NAME")
    @ApiModelProperty("供应商名称")
    private String vendorName;



}
