package com.midea.cloud.srm.model.pj.sou.qa.dto;

import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.pj.sou.qa.entity.SouAnswer;
import com.midea.cloud.srm.model.pj.sou.qa.entity.SouAnswerInfoChange;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangwk12@midea.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("招标澄清信息")
public class SouAnswerEditDTO extends SouAnswer {

    /**
     * 澄清附件
     */
    @SuppressWarnings("AlibabaPojoNoDefaultValue")
    @ApiModelProperty("招标澄清附件")
    private List<SceneFile> sceneFiles = new ArrayList<>();


    /**
     * 变更信息
     */
    private List<SouAnswerInfoChange> souAnswerInfoChangeList;


    /**
     * true-暂存、false-提交
     */
    @ApiModelProperty("true-暂存/false-提交")
    private Boolean tempSave;

    /**
     * 入参校验
     */
    public void formatAndValidate() {
        // 格式化+校验澄清信息
        this.doFormatAndValidateAnswer();
        // 格式化+校验附件
        this.doFormatAndValidateFiles();
    }

    /** 格式化+校验澄清信息 */
    private void doFormatAndValidateAnswer() {
        // ID
        if (answerId != null) {
            //更新
            projectId = null;
        } else {
            //新增澄清
            //有质疑单
            if (questionId != null) {
                // 从质疑单拿
                projectId = null;
            } else {
                //无质疑单
                if (projectId == null) {
                    throw new IllegalArgumentException("请选择招标项目");
                }
            }
        }
        // 澄清标题
        answerTitle = StringUtils.trimToNull(answerTitle);
        int length = 200;
        if (answerTitle == null) {
            throw new IllegalArgumentException("请输入澄清标题");
        } else {
            if (answerTitle.length() > length) {
                throw new IllegalArgumentException("澄清标题长度不能超过200");
            }
        }
        // 澄清内容
        answerContent = StringUtils.trimToNull(answerContent);
        int length2 = 2000;
        if (answerContent == null) {
            throw new IllegalArgumentException("请输入澄清内容");
        } else {
            if (answerContent.length() > length2) {
                throw new IllegalArgumentException("澄清内容长度不能超过2000");
            }
        }

        validateSouAnswerInfoChange(souAnswerInfoChangeList);



    }

    private void validateSouAnswerInfoChange(List<SouAnswerInfoChange> souAnswerInfoChangeList) {
        if (CollectionUtils.isEmpty(souAnswerInfoChangeList)) {
            //小鹏没有信息变更
            return;
        }
        for (SouAnswerInfoChange souAnswerInfoChange : souAnswerInfoChangeList) {
            //变更后信息
            if (!"after".equals(souAnswerInfoChange.getChangeType())) {
                continue;
            }
            // 变更后信息-投标截止时间
            if (souAnswerInfoChange.getOrderStartTime() != null && souAnswerInfoChange.getOrderEndTime() != null) {
                if (souAnswerInfoChange.getOrderEndTime().before(souAnswerInfoChange.getOrderStartTime())) {
                    throw new IllegalArgumentException("变更后的投标截止时间不能早于变更后的投标开始时间");
                }
            }
            // 变更后信息-投标地点
            souAnswerInfoChange.setOrderSite(StringUtils.trimToNull(souAnswerInfoChange.getOrderSite()));
            if (souAnswerInfoChange.getOrderSite() != null) {
                if (souAnswerInfoChange.getOrderSite().length() > 240) {
                    throw new IllegalArgumentException("变更后的投标地点长度不能超过240");
                }
            }
        }

    }

    /** 格式化+校验附件 */
    private void doFormatAndValidateFiles() {
        if (CollectionUtils.isEmpty(sceneFiles)) {
            return;
        }

        int index = 0;
        String fileName;
        String remark;
        for (SceneFile answerFile : sceneFiles) {
            index++;
            // ID(略)
            // 招标澄清表ID(置空 - 后端处理)
            // 附件ID
            // 附件名称
            fileName = StringUtils.trimToNull(answerFile.getFileName());
            if (fileName == null) {
                throw new IllegalArgumentException(MessageFormat.format("附件列表第{0}行请上传附件", index));
            } else {
                if (fileName.length() > 120) {
                    throw new IllegalArgumentException(MessageFormat.format("附件列表第{0}行附件名称长度不能超过120", index));
                }
            }
            // 备注
            remark = StringUtils.trimToNull(answerFile.getRemark());
            if (remark != null) {
                if (remark.length() > 300) {
                    throw new IllegalArgumentException(MessageFormat.format("附件列表第{0}行附件备注长度不能超过300", index));
                }
            }
        }
    }

}
