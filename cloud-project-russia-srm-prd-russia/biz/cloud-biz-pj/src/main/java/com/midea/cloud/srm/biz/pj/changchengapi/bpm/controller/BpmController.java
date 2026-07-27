package com.midea.cloud.srm.biz.pj.changchengapi.bpm.controller;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.api.ResultStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.pj.config.CommonSccConfig;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogService;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmService;
import com.midea.cloud.srm.feign.pj.base.BaseExtClient;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.*;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import io.seata.common.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author huangbf3
 * bpm=>srm接口
 */
@Slf4j
@RestController
@RequestMapping("/external/bpm")
public class BpmController {
    @Autowired
    private BaseExtClient baseExtClient;
    @Autowired
    private IBpmService iBpmService;

    @Autowired
    private CommonSccConfig commonSccConfig;
    @Autowired
    private IInterfaceLogService interfaceLogService;

    @Resource
    private RedisUtil redisUtil;

    @ApiOperation(value = "服务编排-BPM审批权限验证")
    @GetMapping("/authority")
    public void authority(@RequestParam("processInstId")String processInstId,
                         @RequestParam("username")String username) throws Exception {
        log.info("processInstId:" + processInstId);
        log.info("username:" + username);
        iBpmService.authority(processInstId,username);
    }

    @ApiOperation(value = "审批流状态回调")
    @PostMapping("/callback")
    public void callback(@RequestBody BpmCallback bpmCallback) throws Exception {
        log.info("bpmCallback:" + JSONObject.toJSONString(bpmCallback));
        log.info("bpmCallback:" + JSONObject.toJSONString(bpmCallback));
        iBpmService.callback(bpmCallback);
    }

    @ApiOperation(value = "创建流程")
    @PostMapping("/public/flow/native/createProcess")
    public BpmResultDTO<BpmCreateResult> createProcess(@RequestBody JSONObject requestJsn, @RequestParam("dataId") String dataId, @RequestParam("username") String username) {
        return iBpmService.createProcess(requestJsn,dataId);
    }

    @ApiOperation(value = "更新流程")
    @PostMapping("/public/flow/native/updateBoData")
    public BpmResultDTO updateBoData(@RequestBody JSONObject requestJsn, @RequestParam("dataId") String dataId) {
        return iBpmService.updateBoData(requestJsn,dataId);
    }

    @ApiOperation(value = "驳回后重新提交流程")
    @PostMapping("/public/flow/native/resubmitProcess")
    public BpmResultDTO resubmitProcess(@RequestBody JSONObject requestJsn, @RequestParam("dataId") String dataId) {
        return iBpmService.resubmitProcess(requestJsn,dataId);
    }

    @ApiOperation(value = "查询审批记录")
    @RequestMapping("/pre/public/flow/common/comment/getCommentAndTodoTaskList")
    public BpmResultDTO<List<BpmFlowList>> getCommentAndTodoTaskList(@RequestBody JSONObject requestJsn) {
        return iBpmService.getCommentAndTodoTaskList(requestJsn);
    }

    @ApiOperation(value = "查询审批记录")
    @RequestMapping("/findTaskList")
    public List<BpmFlowList> findTaskList(@RequestParam("businessId")Long businessId,@RequestParam("bussinessType")String bussinessType) {
        return iBpmService.findTaskList(businessId,bussinessType);
    }

    @ApiOperation(value = "按流程分组发起流程")
    @PostMapping("/public/flow/native/createProcessByCategory")
    public BpmResultDTO<BpmCreateResult> createProcessByCategory(@RequestBody JSONObject requestJsn, @RequestParam("dataId") String dataId, @RequestParam("businessType") String businessType) {
        return iBpmService.createProcessByCategory(requestJsn,dataId,businessType);
    }

    @ApiOperation(value = "根据业务单据ID或流程实例ID 获取BPM-SRM审批流最新记录")
    @PostMapping("/getLastFlowInstanceRecord")
    public FlowInstanceRecord getLastFlowInstanceRecord(@RequestBody FlowInstanceRecord flowInstanceRecord) {
        return iBpmService.getLastFlowInstanceRecord(flowInstanceRecord);
    }

    @ApiOperation(value = "范围SRM嵌套页面")
    @GetMapping("/viewSrm")
    public void viewSrm(@RequestParam("token") String token,@RequestParam("redirectUri") String redirectUri) throws IOException {
        // 跳转地址校验
        log.info("commonSccConfig:"+JSONObject.toJSONString(commonSccConfig));
        Set<String> bpmRedirectWhiteUris = commonSccConfig.getBpmRedirectWhiteUris();
        AtomicBoolean check = new AtomicBoolean(false);
        if (CollectionUtils.isNotEmpty(bpmRedirectWhiteUris)) {
            bpmRedirectWhiteUris.forEach(whiteUri -> {
                if (redirectUri.contains(whiteUri)) {
                    check.set(true);
                }
            });
            if (!check.get()) {
                throw new BaseException("跳转地址不合法，请联系管理员授权");
            }
        }
        iBpmService.srmDirect(token,redirectUri);
    }

    @ApiOperation(value = "范围SRM嵌套页面")
    @GetMapping("/srmDirect")
    public void srmDirect(@RequestParam("token") String token,@RequestParam("redirectUri") String redirectUri) throws IOException {
        // 跳转地址校验
        log.info("commonSccConfig:"+JSONObject.toJSONString(commonSccConfig));
        Set<String> bpmRedirectWhiteUris = commonSccConfig.getBpmRedirectWhiteUris();
        AtomicBoolean check = new AtomicBoolean(false);
        if (CollectionUtils.isNotEmpty(bpmRedirectWhiteUris)) {
            bpmRedirectWhiteUris.forEach(whiteUri -> {
                if (redirectUri.contains(whiteUri)) {
                    check.set(true);
                }
            });
            if (!check.get()) {
                throw new BaseException("跳转地址不合法，请联系管理员授权");
            }
        }
        iBpmService.srmDirect(token,redirectUri);
    }

    /**
     *
     * @param funName 这个对应功能的名字，前端一般用路由的name值来识别
     * @param formId 这个是业务单据ID
     * @param formNo 这个传的是单据标题或其他自定义标题
     * @return
     */
    @ApiOperation(value = "范围SRM嵌套页面回调地址")
    @GetMapping("/getViewSrmRollBackUrl")
    public String getViewSrmRollBackUrl(@RequestParam("funName") String funName,@RequestParam("formId") Long formId
            ,@RequestParam("formNo") String formNo) {
        return iBpmService.getViewSrmRollBackUrl(funName,formId,formNo);
    }

    @ApiOperation(value = "根据业务单据ID或流程实例ID 获取BPM-SRM审批流所有记录")
    @PostMapping("/getFlowInstanceRecord")
    public List<FlowInstanceRecord>  getFlowInstanceRecord(@RequestBody FlowInstanceRecord flowInstanceRecord) {
        return iBpmService.getFlowInstanceRecord(flowInstanceRecord);
    }

    @ApiOperation(value = "发起人撤回流程")
    @PostMapping("/public/flow/native/rollBackProcess")
    public BpmResultDTO<BpmCreateResult> rollBackProcess(@RequestBody BpmRollBackDTO bpmRollBackDTO, @RequestParam("dataId") String dataId) {
        return iBpmService.rollBackProcess(bpmRollBackDTO,dataId);
    }

    @ApiOperation(value = "发起人撤回流程")
    @PostMapping("/rollBackAll")
    public void rollBackAll(@RequestBody BpmRollBackDTO bpmRollBackDTO) {
        iBpmService.rollBackAll(bpmRollBackDTO);
    }

    @ApiOperation(value = "获取token")
    @GetMapping("/getFileToCode")
    public String getFileToCode() {
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        log.info("用户信息==={}", JSONObject.toJSONString(loginAppUser));
        return redisUtil.get(loginAppUser.getUsername().toUpperCase());
    }
    @ApiOperation(value = "发起人撤回流程")
    @PostMapping("/addLogTest")
    public void rollBackAll() {
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.CREATE_PROCESS;
        JSONObject requestJsn = new JSONObject();
        requestJsn.put("a","a");
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,requestJsn);
        String result = "success";
        interfaceLog.setReturnInfo(result);
        interfaceLogService.createInterfaceLog(interfaceLog);
    }
}