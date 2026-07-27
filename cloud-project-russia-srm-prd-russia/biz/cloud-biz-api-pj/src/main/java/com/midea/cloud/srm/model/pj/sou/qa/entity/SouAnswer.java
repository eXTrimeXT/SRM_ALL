package com.midea.cloud.srm.model.pj.sou.qa.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.midea.cloud.srm.model.common.BaseEntity;
import com.midea.cloud.srm.model.pj.sou.qa.entity.SouQuestion;
import com.midea.cloud.srm.model.pj.sou.qa.enums.SouAnswerStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 采购商-澄清表
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("scc_sou_answer")
@ApiModel("采购商-澄清")
public class SouAnswer extends BaseEntity<SouAnswer> {

    private static final long serialVersionUID = 1L;

    @TableId("ANSWER_ID")
    @ApiModelProperty("ID")
    protected Long answerId;

    @TableField("ANSWER_NUM")
    @ApiModelProperty("澄清编号")
    protected String answerNum;

    /**
     * PS: 公开澄清时，该字段为空; 澄清质疑时，该字段有值
     * @see SouQuestion#getQuestionId
     */
    @TableField("QUESTION_ID")
    @ApiModelProperty("质疑ID")
    protected Long questionId;

    @TableField("QUESTION_NUM")
    @ApiModelProperty("质疑编号")
    private String questionNum;

    /** @see SouProject#getProjectId */
    @ApiModelProperty("寻源模块基础表ID,该表与基础表为一对一关系")
    @TableField("PROJECT_ID")
    protected Long projectId;

    /** @see SouProject#getSouNo */
    @ApiModelProperty("寻源单号")
    @TableField("SOU_NO")
    protected String souNo;

    /** @see SouProject#getSouName */
    @ApiModelProperty("寻源单名称")
    @TableField("SOU_NAME")
    protected String souName;

    /** @see SouTypeEnum */
    @ApiModelProperty("寻源类型")
    @TableField("SOU_TYPE")
    private String souType;

    @TableField("SUBMIT_TIME")
    @ApiModelProperty("提交时间")
    protected Date submitTime;

    @TableField("ANSWER_STATUS")
    @ApiModelProperty("澄清状态[字典值: BRG_ANSWER_STATUS]")
    protected SouAnswerStatusEnum answerStatus;

    @TableField("ANSWER_TITLE")
    @ApiModelProperty("澄清标题")
    protected String answerTitle;

    @TableField("ANSWER_CONTENT")
    @ApiModelProperty("澄清内容")
    protected String answerContent;

}
