package com.midea.cloud.srm.model.pj.sunhonesty.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

/**
 * @author fu
 */
@Data
public class StudentTrainExamMsg {
    /**
     * 培训ID
     */
    private Long trainId;
    /**
     * 学员编号
     */
    private String studentNo;
    /**
     * 试卷ID
     */
    private Long paperId;

    /**
     * 状态, 0:未开始, 1:未通过, 2:通过
     */
    private Integer status;

    /**
     * 考试次数
     */
    private Integer examTimes;

    /**
     * 考试记录
     */
    private List<StudentTrainExamLogMsg> logs;

}
