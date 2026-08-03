package com.midea.cloud.srm.biz.pj.sunhonestyinfo.service.impl;

import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.biz.pj.sunhonestyinfo.service.ExamRecordService;
import com.midea.cloud.srm.feign.pj.rbac.RbacExtClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.pj.sunhonesty.dto.StudentTrainExamLogMsg;
import com.midea.cloud.srm.model.pj.sunhonesty.dto.StudentTrainStatusMsgDto;
import com.midea.cloud.srm.model.pj.sunhonesty.entity.SccPjExamRecord;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.rbac.user.entity.UserThird;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author fu
 */
@Slf4j
@Service
public class ExamRecordServiceImpl implements ExamRecordService {
    @Autowired
    private RbacClient rbacClient;
    @Autowired
    private QlService qlService;
    @Autowired
    private RbacExtClient rbacExtClient;
    /**
     * 阳光诚信学生ID头
     */
    private static final String PREFIX = "YGCX";
    /**
     * 阳光诚信返回考试通过标识
     */
    private static final int PASS_STATUS = 2;
    /**
     * 阳光诚信渠道标识
     */
    private static final String YGCX_SISS = "SISS";
    @Override
    public void addExamRecord(StudentTrainStatusMsgDto studentTrainStatusMsgDto) {
        //1通过学员编号查询是否是SRM学员信息，如果是则添加考试记录，不是不处理
        if(studentTrainStatusMsgDto==null) {
            return;
        }
        if(studentTrainStatusMsgDto.getStudentNo()==null) {
            return;
        }
        if(studentTrainStatusMsgDto.getCompleted()==null) {
            return;
        }
        if(studentTrainStatusMsgDto.getExamList()==null){
            return;
        }
        UserThird userThird=new UserThird();
        userThird.setThirdUnionId(studentTrainStatusMsgDto.getStudentNo().replace(PREFIX, ""));
        userThird.setThirdSource(YGCX_SISS);
        List<UserThird> userThirdList = rbacExtClient.selectUserThird(userThird);
        if(userThirdList.size()==0) {
            return;
        }
        //根据用户账号查询用户信息
        User user=new User();
        user.setUsername(userThirdList.get(0).getUserAccount());
        List<User> userList=rbacClient.listByUser(user);
        if(userList.size()==0){
            return;
        }
        //获取考试结果
        Comparator<StudentTrainExamLogMsg> comparator= Comparator.comparingInt(StudentTrainExamLogMsg::getExamTimes);
        if(studentTrainStatusMsgDto.getExamList().get(0).getLogs()==null){
            return;
        }
        StudentTrainExamLogMsg studentTrainExamLogMsg= Collections.max(studentTrainStatusMsgDto.getExamList().get(0).getLogs(),comparator);
        //添加考试记录
        //组装数据
        SccPjExamRecord sccPjExamRecord=new SccPjExamRecord();
        sccPjExamRecord.setCompanyId(userList.get(0).getCompanyId().toString());
        sccPjExamRecord.setCompletedDate(new Date());
        sccPjExamRecord.setNickname(userList.get(0).getNickname());
        sccPjExamRecord.setStudentNo(studentTrainStatusMsgDto.getStudentNo().replace(PREFIX,""));
        if(studentTrainStatusMsgDto.getTrainId()!=null) {
            sccPjExamRecord.setTrainId(studentTrainStatusMsgDto.getTrainId().toString());
        }
        sccPjExamRecord.setUsername(userList.get(0).getUsername());
        sccPjExamRecord.setUserId(userList.get(0).getUserId().toString());
        if(studentTrainStatusMsgDto.getCompleted() && studentTrainExamLogMsg.getStatus()==PASS_STATUS){
            sccPjExamRecord.setCompleted(YesOrNo.YES.getValue());
        }else{
            sccPjExamRecord.setCompleted(YesOrNo.NO.getValue());
        }
        List<SccPjExamRecord>examRecordList=new ArrayList<>();
        examRecordList.add(sccPjExamRecord);
        //添加考试记录表
        qlService.create("SccPjExamRecord",examRecordList);
    }

}
