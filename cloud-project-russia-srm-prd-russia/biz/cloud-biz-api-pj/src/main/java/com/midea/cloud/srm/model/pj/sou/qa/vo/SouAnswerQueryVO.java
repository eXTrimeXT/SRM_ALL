package com.midea.cloud.srm.model.pj.sou.qa.vo;

import com.midea.cloud.srm.model.pj.sou.qa.entity.SouAnswer;
import com.midea.cloud.srm.model.pj.sou.qa.entity.SouQuestion;
import com.midea.cloud.srm.model.pj.sou.qa.enums.SouQuestionStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhangwk12@midea.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("招标澄清信息")
public class SouAnswerQueryVO extends SouAnswer {


    /**
     * @see SouQuestion#getQuestionTitle()
     */
    @ApiModelProperty("质疑标题")
    private String questionTitle;
    /**
     * @see SouQuestion#getQuestionStatus() ()
     */
    @ApiModelProperty("质疑状态")
    private SouQuestionStatusEnum questionStatus;



}
