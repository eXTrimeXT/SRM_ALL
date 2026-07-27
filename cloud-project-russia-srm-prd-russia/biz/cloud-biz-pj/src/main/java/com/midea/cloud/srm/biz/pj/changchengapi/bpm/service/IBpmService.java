package com.midea.cloud.srm.biz.pj.changchengapi.bpm.service;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.*;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;

import java.io.IOException;
import java.util.List;

/**
 * @author huangbf3
 */
public interface IBpmService {
    /**
     * 创建流程
     * @param requestJsn 业务单据
     * @param dataId 业务单据ID
     * @return 返回
     */
    BpmResultDTO<BpmCreateResult> createProcess(JSONObject requestJsn, String dataId);

    /**
     * 按流程分组发起流程
     * @param requestJsn 业务单据
     * @param dataId 业务单据ID
     * @param businessType 审批流模板
     * @return 返回
     */
    BpmResultDTO<BpmCreateResult> createProcessByCategory(JSONObject requestJsn, String dataId,String businessType);


    /**
     * 发起人撤回流程
     * @param bpmRollBackDTO 撤回相关信息
     * @param dataId 业务单据ID
     * @return 返回
     */
    BpmResultDTO<BpmCreateResult> rollBackProcess(BpmRollBackDTO bpmRollBackDTO, String dataId);


    /**
     * 更新流程
     * @param requestJsn BPM参数
     * @param dataId 业务单据ID
     * @return 返回
     */
    BpmResultDTO updateBoData(JSONObject requestJsn,String dataId);

    /**
     * 驳回后重新提交流程
     * @param requestJsn BPM参数
     * @param dataId 业务单据ID
     * @return 返回
     */
    BpmResultDTO resubmitProcess(JSONObject requestJsn,String dataId);

    /**
     * 驳回后重新提交流程
     * @param requestJsn BPM参数
     * @param dataId 业务单据ID
     * @param businessType 审批流模板
     * @return 返回
     */
    BpmResultDTO resubmitProcess(JSONObject requestJsn,String dataId,String businessType);

    /**
     * 备注
     * @param requestJsn BPM参数
     * @return 返回
     */
    BpmResultDTO<List<BpmFlowList>> getCommentAndTodoTaskList(JSONObject requestJsn);

    /**
     * 审批回调
     * @param bpmCallback
     * @throws Exception
     */
    void callback(BpmCallback bpmCallback) throws Exception;

    /**
     * 审批权限验证
     * @param processInstId
     * @param username
     */
    void authority(String processInstId, String username);

    /**
     * 获取BPM-SRM审批流记录
     * @param flowInstanceRecord
     * @param flowInstanceRecord
     * @return
     */
    FlowInstanceRecord getLastFlowInstanceRecord(FlowInstanceRecord flowInstanceRecord);


    /**
     * bpm嵌套SRM页面
     * @param token
     * @param redirectUri
     * @throws IOException
     */
    void viewSrm(String token, String redirectUri) throws IOException;


    /**
     * bpm嵌套SRM页面
     * @param token
     * @param redirectUri
     * @throws IOException
     */
    void srmDirect(String token, String redirectUri) throws IOException;


    /**
     * 获取BPM-SRM审批流所有记录
     * @param flowInstanceRecord
     * @param flowInstanceRecord
     * @return
     */
    List <FlowInstanceRecord> getFlowInstanceRecord(FlowInstanceRecord flowInstanceRecord);

    /**
     * 备注
     * @param funName 这个对应功能的名字，前端一般用路由的name值来识别
     * @param formId 这个是业务单据ID
     * @param formNo 这个传的是单据标题或其他自定义标题
     * @return
     */
    String getViewSrmRollBackUrl(String funName, Long formId, String formNo);

    /**
     * 备注
     * @param funName 这个对应功能的名字，前端一般用路由的name值来识别
     * @param formId 这个是业务单据ID
     * @param formNo 这个传的是单据标题或其他自定义标题
     * @param otherUrlParam 其他参数 otherId_11111&param3=3333
     * @return
     */
    String getViewSrmRollBackUrl(String funName, Long formId, String formNo, String otherUrlParam);

    /**
     * 根据单据ID和审批模板CODE获取BPM审批历史
     * @param bussinessId
     * @param bussinessType
     * @return
     */
    List<BpmFlowList> findTaskList(Long bussinessId, String bussinessType);

    /**
     *  备注
     * @param bpmRollBackDTO
     */
    void rollBackAll(BpmRollBackDTO bpmRollBackDTO);

    /**
     *  发起流程
     * @param bpmParam bpm组装参数
     * @param businessId 业务单据ID
     * @param businessType 审批流模板
     * @return BPM发起流程返回结果
     */
    BpmResultDTO<BpmCreateResult> startProcessByCategoty(Long businessId,String businessType,BpmStartProcessParam bpmParam);

    /**
     *  退回任务
     * @param bpmParam
     * @return  退回接口返回
     */
    JSONObject rollBackTask(BpmRollBackTaskParam bpmParam);

    /**
     * 退回任务
     * @param rollBackTaskVo
     * @throws Exception
     */
    void rollBackTask(BpmRollBackTaskVo rollBackTaskVo) throws Exception;

    /**
     * 转办任务
     * @param bpmForwardTaskVo
     * @throws Exception
     */
    void forwardTask(BpmForwardTaskVo bpmForwardTaskVo) throws Exception;

    /**
     * 转办任务
     * @param bpmParam
     * @return
     */
    JSONObject forwardTask(BpmForwardTaskParam bpmParam);

    /**
     * 提交待办任务
     * @param bpmParam
     * @return
     */
    JSONObject commitTask(BpmCommitTaskParam bpmParam);

    /**
     * 预执行接口
     * @param commitTaskVo
     * @param bpmParam
     * @return
     */
    JSONObject predict(BpmCommitTaskVo commitTaskVo,BpmPredictParam bpmParam);

    /**
     * 提交
     * @param commitTaskVo
     * @throws Exception
     */
    void submitEngine(BpmCommitTaskVo commitTaskVo) throws Exception;

    /**
     * 提交前端通过参数
     * @param commitTaskVo
     * @throws Exception
     */
    void pass(BpmCommitTaskVo commitTaskVo) throws Exception;

    /**
     * 终止
     * @param commitTaskVo
     * @throws Exception
     */
    void destory(BpmCommitTaskVo commitTaskVo) throws Exception;

    /**
     * 查询审批记录
     * @param businessId
     * @param businessType
     * @return
     */
    List<BpmFlowList> findTaskListNew(Long businessId, String businessType);

    /**
     * 提前审批通过
     * @param bpmCommitTaskVo
     * @throws Exception
     */
    void end(BpmCommitTaskVo bpmCommitTaskVo) throws Exception;

    /**
     * 获取bpm业务单据传值
     * @param businessId
     * @param businessType
     * @return
     * @throws Exception
     */
    JSONObject getDataPushFlow(Long businessId,String businessType) throws Exception;

    /**
     *  SRM推BPM待办流程
     * @param businessId 业务单据ID
     * @param businessType 流程模板编码
     * @param processTitle 标题
     * @param todoUsername 待办人账号
     * @param extUrlParm url扩展参数 例如 id=1&code=2
     * @throws Exception
     */
    void srmbpmTodo(Long businessId,String businessType,String processTitle,String todoUsername, String extUrlParm) throws Exception;

    /**
     *  SRM推BPM待办转已办
     * @param businessId 业务单据ID
     * @param businessType 流程模板编码
     * @param todoUsername 待办人账号
     * @throws Exception
     */
    void srmbpmHavedone(Long businessId,String businessType,String todoUsername) throws Exception;
}
