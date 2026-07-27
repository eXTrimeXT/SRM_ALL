package com.midea.cloud.srm.biz.pj.changchengapi.bpm.controller;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmService;
import com.midea.cloud.srm.feign.pj.base.BaseExtClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.*;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import io.seata.common.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangbf3
 * bpm=>srm接口
 */
@Slf4j
@RestController
@RequestMapping("/bpmFlow")
public class BpmFlowController {
    @Autowired
    private BaseExtClient baseExtClient;
    @Autowired
    private IBpmService iBpmService;
    /**
     * 流程模板对应跳转地址funName
     */
    private static final String BUSINESS_TYPE_FUN_NAME = "BUSINESS_TYPE_FUN_NAME";

    private static final String OTHER_URL_PARAM = "otherUrlParam";

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "发起流程")
    @PostMapping("/startProcessByCategoty")
    public BpmResultDTO<BpmCreateResult> startProcessByCategoty(@RequestParam("businessId") Long businessId
            ,@RequestParam("businessType") String businessType,@RequestBody(required = false) BpmStartProcessParam bpmParam) {
        log.info("发起流程 businessId:{},businessType:{},bpmParam:{}",businessId,businessType,JSONObject.toJSONString(bpmParam));
        Assert.notNull(bpmParam,"参数不能为空");
        Assert.isTrue(StringUtils.isNotBlank(bpmParam.getProcessGroupId()),"流程分组ID不能为空");


        DictItem funNameDictItem = baseExtClient.getDictItem(BUSINESS_TYPE_FUN_NAME,businessType);
        if(funNameDictItem!=null){
            String otherUrlParam = null;
            if(bpmParam.getProcessVars().containsKey(OTHER_URL_PARAM)){
                otherUrlParam = bpmParam.getProcessVars().getString("otherUrlParam");
                bpmParam.getProcessVars().remove("otherUrlParam");
            }
            bpmParam.getProcessVars().put("formUrl",iBpmService.getViewSrmRollBackUrl(funNameDictItem.getDictItemName(),businessId,funNameDictItem.getItemDescription(),otherUrlParam));
        }
        return iBpmService.startProcessByCategoty(businessId,businessType,bpmParam);
    }

    @ApiOperation(value = "退回任务")
    @PostMapping("/rollBackTask")
    public void rollBackTask(@RequestBody BpmRollBackTaskVo rollBackTaskVo) throws Exception {
        Assert.notNull(rollBackTaskVo,"参数不能为空");
        Assert.isTrue(rollBackTaskVo.getBusinessId()!=null,"业务单据ID不能为空");
        Assert.isTrue(rollBackTaskVo.getBusinessType()!=null,"审批流模板不能为空");
        Assert.isTrue(rollBackTaskVo.getCommentInfo()!=null,"动作不能为空");
        Assert.isTrue(StringUtils.isNotBlank(rollBackTaskVo.getTaskInstId()),"当前任务在BPM中的任务实例ID不能为空");

        String actionName = rollBackTaskVo.getCommentInfo().getActionName();
        Assert.isTrue(StringUtils.isNotBlank(actionName),"办理动作不能为空");
        Assert.isTrue(StringUtils.equals(actionName,"退回")||StringUtils.equals(actionName,"退回直达")
                ||StringUtils.equals(actionName,"退回招标负责人"),"退回类型错误");

        iBpmService.rollBackTask(rollBackTaskVo);
    }

    @ApiOperation(value = "转办任务")
    @PostMapping("/forwardTask")
    public void forwardTask(@RequestBody BpmForwardTaskVo bpmForwardTaskVo) throws Exception{
        Assert.notNull(bpmForwardTaskVo,"参数不能为空");
        Assert.isTrue(bpmForwardTaskVo.getBusinessId()!=null,"业务单据ID不能为空");
        Assert.isTrue(bpmForwardTaskVo.getBusinessType()!=null,"审批流模板不能为空");
        Assert.isTrue(bpmForwardTaskVo.getTargetUser()!=null,"下一个环节的任务办理人不能为空");
        Assert.isTrue(bpmForwardTaskVo.getTaskInstId()!=null,"当前任务在BPM中的任务实例ID不能为空");
        Assert.isTrue(bpmForwardTaskVo.getCommentInfo()!=null,"转办原因不能为空");
        Assert.isTrue(StringUtils.isNotBlank(bpmForwardTaskVo.getCommentInfo().getCommentMsg()),"转办原因不能为空");

        iBpmService.forwardTask(bpmForwardTaskVo);
    }

    @ApiOperation(value = "提交待办任务")
    @PostMapping("/commitTask")
    public JSONObject commitTask(@RequestBody BpmCommitTaskParam bpmParam) {
        return iBpmService.commitTask(bpmParam);
    }

    @ApiOperation(value = "预执行接口")
    @PostMapping("/predict")
    public JSONArray predict(@RequestBody BpmCommitTaskVo commitTaskVo) throws Exception {
        if(StringUtils.equals(AppUserUtil.getLoginAppUser().getUserType(),UserType.VENDOR.name())){
            return new JSONArray();
        }
        Assert.notNull(commitTaskVo,"参数不能为空");
        Assert.isTrue(commitTaskVo.getBusinessId()!=null,"业务单据ID不能为空");
        Assert.isTrue(StringUtils.isNotBlank(commitTaskVo.getBusinessType()),"审批流模板不能为空");
        log.info(AppUserUtil.getUserName());
        FlowInstanceRecord record = iBpmService.getLastFlowInstanceRecord(
                new FlowInstanceRecord().setBusinessId(commitTaskVo.getBusinessId()).setTemplateCode(commitTaskVo.getBusinessType()));
        log.info("record:"+JSONObject.toJSONString(record));
        if(record==null||StringUtils.isBlank(record.getInstanceId())){
            redisUtil.del(commitTaskVo.getBusinessId()+commitTaskVo.getBusinessType()+"submitFlow");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("businessId",commitTaskVo.getBusinessId());
            jsonObject.put("businessType",commitTaskVo.getBusinessType());
            String submitResult = baseExtClient.submitEngine(jsonObject);
            log.info("submitResult:{}",submitResult);
        }
        record = iBpmService.getLastFlowInstanceRecord(
                new FlowInstanceRecord().setBusinessId(commitTaskVo.getBusinessId())
                        .setTemplateCode(commitTaskVo.getBusinessType())
                        .setDealStatus("SUCCESS")
        );
        log.info("record:"+JSONObject.toJSONString(record));
        JSONObject processVars = iBpmService.getDataPushFlow(commitTaskVo.getBusinessId(),commitTaskVo.getBusinessType());
        JSONObject pv = processVars.getJSONObject("processVars");
        if(pv != null){
            processVars.putAll(pv);
        }
        processVars.remove("processVars");
        BpmPredictParam bpmParam = new BpmPredictParam()
                .setUid(AppUserUtil.getUserName())
                .setProcessInstId(record.getInstanceId())
                .setProcessVars(processVars);

        DictItem funNameDictItem = baseExtClient.getDictItem(BUSINESS_TYPE_FUN_NAME,commitTaskVo.getBusinessType());
        if(funNameDictItem!=null){
            bpmParam.getProcessVars().put("formUrl",iBpmService.getViewSrmRollBackUrl(funNameDictItem.getDictItemName(),commitTaskVo.getBusinessId(),funNameDictItem.getItemDescription()));
        }

        JSONObject jsonObject = iBpmService.predict(commitTaskVo,bpmParam);
        return jsonObject.getJSONArray("data");
    }

    @ApiOperation(value = "提交")
    @PostMapping("/submitEngine")
    public void submitEngine(@RequestBody BpmCommitTaskVo commitTaskVo) throws Exception {
        Assert.notNull(commitTaskVo,"参数不能为空");
        Assert.isTrue(commitTaskVo.getBusinessId()!=null,"业务单据ID不能为空");
        Assert.isTrue(commitTaskVo.getProcessVars()!=null,"流程变量不能为空");
        Assert.isTrue(StringUtils.isNotBlank(commitTaskVo.getTaskInstId()),"当前任务在BPM中的任务实例ID不能为空");
        Assert.isTrue(StringUtils.isNotBlank(commitTaskVo.getBusinessType()),"审批流模板不能为空");
        iBpmService.submitEngine(commitTaskVo);
    }

    @ApiOperation(value = "终止")
    @PostMapping("/destory")
    public void destory(@RequestBody BpmCommitTaskVo bpmCommitTaskVo) throws Exception {
        Assert.notNull(bpmCommitTaskVo,"参数不能为空");
        Assert.isTrue(bpmCommitTaskVo.getBusinessId()!=null,"业务单据ID不能为空");
        Assert.isTrue(StringUtils.isNotBlank(bpmCommitTaskVo.getBusinessType()),"审批流模板不能为空");
//        Assert.isTrue(StringUtils.isNotBlank(bpmCommitTaskVo.getTaskInstId()),"当前任务在BPM中的任务实例ID不能为空");
        iBpmService.destory(bpmCommitTaskVo);
    }

    @ApiOperation(value = "审批通过")
    @PostMapping("/pass")
    public void pass(@RequestBody BpmCommitTaskVo bpmCommitTaskVo) throws Exception {
        Assert.notNull(bpmCommitTaskVo,"参数不能为空");
        Assert.isTrue(bpmCommitTaskVo.getBusinessId()!=null,"业务单据ID不能为空");
        Assert.isTrue(StringUtils.isNotBlank(bpmCommitTaskVo.getTaskInstId()),"当前任务在BPM中的任务实例ID不能为空");
        Assert.isTrue(StringUtils.isNotBlank(bpmCommitTaskVo.getBusinessType()),"审批流模板不能为空");
        iBpmService.pass(bpmCommitTaskVo);
    }

    @ApiOperation(value = "提前审批通过")
    @PostMapping("/end")
    public void end(@RequestBody BpmCommitTaskVo bpmCommitTaskVo) throws Exception {
        Assert.notNull(bpmCommitTaskVo,"参数不能为空");
        Assert.isTrue(bpmCommitTaskVo.getBusinessId()!=null,"业务单据ID不能为空");
        Assert.isTrue(StringUtils.isNotBlank(bpmCommitTaskVo.getTaskInstId()),"当前任务在BPM中的任务实例ID不能为空");
        Assert.isTrue(StringUtils.isNotBlank(bpmCommitTaskVo.getBusinessType()),"审批流模板不能为空");
        iBpmService.end(bpmCommitTaskVo);
    }

    @ApiOperation(value = "查询审批记录")
    @RequestMapping("/findTaskListNew")
    public List<BpmFlowList> findTaskListNew(@RequestParam(value = "businessId", required = false)Long businessId,@RequestParam("businessType")String businessType) {
        List<BpmFlowList> bpmFlowLists = new ArrayList<>();
        //优化前端初始化页面，无业务ID的场景，不报错
        if(ObjectUtils.anyNull(businessId)) {
            return bpmFlowLists;
        }
        try{
            bpmFlowLists = iBpmService.findTaskListNew(businessId,businessType);
            for(BpmFlowList flow : bpmFlowLists) {
                if("正在办理".equals(flow.getActionName())) {
                    flow.setCreateDate(null);
                }
            }
        }catch (Exception e){

        }
        return bpmFlowLists;
    }

    @ApiOperation(value = "查询审批记录")
    @PostMapping("/getLastFlowInstanceRecord")
    public FlowInstanceRecord getLastFlowInstanceRecord(@RequestBody FlowInstanceRecord flowInstanceRecord){
        return iBpmService.getLastFlowInstanceRecord(flowInstanceRecord);
    }

    /**
     * 发送待办
     * @param businessId
     * @param businessType
     * @param processTitle
     * @param todoUsername
     * @param extUrlParm ： 扩展的请求参数 例如 id=1&code=2
     * @throws Exception
     */
    @ApiOperation(value = "发送待办")
    @GetMapping("/srmbpmTodo")
    public void srmbpmTodo(@RequestParam("businessId") Long businessId,@RequestParam("businessType") String businessType
            ,@RequestParam("processTitle")String processTitle,@RequestParam("todoUsername")String todoUsername, @RequestParam(value = "extUrlParam", required = false) String extUrlParm) throws Exception {
        iBpmService.srmbpmTodo(businessId,businessType,processTitle,todoUsername, extUrlParm);
    }

    @ApiOperation(value = "待办转已办")
    @GetMapping("/srmbpmHavedone")
    public void srmbpmHavedone(@RequestParam("businessId") Long businessId,@RequestParam("businessType") String businessType
            ,@RequestParam("todoUsername")String todoUsername) throws Exception {
        iBpmService.srmbpmHavedone(businessId,businessType,todoUsername);
    }
}