package com.midea.cloud.srm.model.pj.sunhonesty.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description 考试记录实体
 * @author fu
 * @date 2024-08-19
 */
@Data
@ApiModel("考试记录")
public class StudentTrainExamLogMsg {

    /**
     * 考试记录ID
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 得分
     */
    @ApiModelProperty("得分")
    private Double score;

    /**
     * 试卷总分
     */
    @ApiModelProperty("试卷总分")
    private Double paperScore;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 考试开始时间
     */
    @ApiModelProperty("考试开始时间")
    private Date startTime;

    /**
     * 考试结束时间
     */
    @ApiModelProperty("考试结束时间")
    private Date endTime;

    /**
     * 考试次数(第几次)
     */
    @ApiModelProperty("考试次数(第几次)")
    private Integer examTimes;
}
