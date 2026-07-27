package com.midea.cloud.srm.model.pj.sunhonesty.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

/**
 * @author fu
 */
@Data
public class StudentTrainStatusMsgDto {
    /**培训ID */
    @TableId
    private Long trainId;
    /**学员编号 */
    private String studentNo;
    /**是否完成 */
    private Boolean completed;
    /**
     * 考试列表
     */
    private List<StudentTrainExamMsg> examList;

}
