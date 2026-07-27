package com.midea.cloud.srm.model.pj.sou.qa.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.midea.cloud.srm.model.bid.enums.BidAnswerEnum;
import com.midea.cloud.srm.model.bid.enums.BidQuestionStatusEnum;
import com.midea.cloud.srm.model.bid.purchaser.projectmanagement.bidinitiating.entity.Biding;
import com.midea.cloud.srm.model.bid.purchaser.projectmanagement.clearquestion.entity.BidingAnswer;
import com.midea.cloud.srm.model.bid.purchaser.projectmanagement.clearquestion.entity.BidingQuestion;
import com.midea.cloud.srm.model.common.BasePage;
import com.midea.cloud.srm.model.pj.sou.qa.enums.SouAnswerStatusEnum;
import com.midea.cloud.srm.model.pj.sou.qa.enums.SouQuestionStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author zhangwk12@midea.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("招标质疑/澄清查询条件")
public class SouQuestionAnswerQueryDTO extends BasePage {

    /** @see SouProject#getSouNo */
    @ApiModelProperty("寻源单号")
    @TableField("sou_no")
    private String souNo;

    /** @see SouProject#getSouName */
    @ApiModelProperty("寻源单名称")
    @TableField("sou_name")
    private String souName;

    /** @see SouTypeEnum */
    @ApiModelProperty("寻源类型")
    @TableField("sou_type")
    private String souType;

    @TableField("QUESTION_TITLE")
    @ApiModelProperty("质疑标题")
    private String questionTitle;

    @TableField("QUESTION_NUM")
    @ApiModelProperty("质疑编号")
    private String questionNum;

    @TableField("QUESTION_STATUS")
    @ApiModelProperty("质疑状态")
    private SouQuestionStatusEnum questionStatus;

    @ApiModelProperty("查询多个质疑状态")
    private List<String> questionStatusList;

    @TableField("ANSWER_STATUS")
    @ApiModelProperty("澄清状态")
    private SouAnswerStatusEnum answerStatus;

    @ApiModelProperty("查询多个澄清状态")
    private List<String> answerStatusList;

    @TableField("ANSWER_NUM")
    @ApiModelProperty("澄清编号")
    protected String answerNum;

    @TableField("ANSWER_TITLE")
    @ApiModelProperty("澄清标题")
    protected String answerTitle;

    /**
     * 当前用户ID
     */
    private Long currentUserId;

    /**
     * 当前公司ID
     */
    private Long currentCompanyId;

    /**
     * 入参格式化
     */
    public void formatParams() {
        // 招标编码
        souNo = StringUtils.trimToNull(souNo);
        // 招标名称
        souName = StringUtils.trimToNull(souName);
        // 质疑标题
        questionTitle = StringUtils.trimToNull(questionTitle);
        // 质疑编号
        questionNum = StringUtils.trimToNull(questionNum);
        // 质疑状态(略)
        // 澄清标题
        answerTitle = StringUtils.trimToNull(answerTitle);
        // 澄清编码
        answerNum = StringUtils.trimToNull(answerNum);
        // 澄清状态(略)
    }

}
