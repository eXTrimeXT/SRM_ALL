package com.midea.cloud.srm.biz.pj.sunhonestyinfo.service;

import com.midea.cloud.srm.model.pj.sunhonesty.dto.StudentTrainStatusMsgDto;

/**
 * @author fu
 */
public interface ExamRecordService {
    /**
     * 备注
     * @param studentTrainStatusMsgDto 参数
     * @return
     */
    void addExamRecord(StudentTrainStatusMsgDto studentTrainStatusMsgDto);
}
