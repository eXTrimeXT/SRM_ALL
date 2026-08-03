package com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.enums.api.ResultStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.IDassClient;
import com.midea.cloud.common.utils.IPUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogService;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmService;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmStartRecordService;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmTaskHistoryService;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.ICommitTaskParamService;
import com.midea.cloud.srm.biz.pj.hruser.service.ISccPjUserService;
import com.midea.cloud.srm.biz.pj.utils.MqlType;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.pj.base.BaseExtClient;
import com.midea.cloud.srm.feign.pj.bpm.BpmCallbackClient;
import com.midea.cloud.srm.feign.pj.cooperate.CooperateSignClient;
import com.midea.cloud.srm.feign.pj.pj.PjSignClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.monitor.enums.YesOrNo;
import com.midea.cloud.srm.model.flow.process.dto.OaRequestDTO;
import com.midea.cloud.srm.model.flow.process.dto.TemplateHeaderDTO;
import com.midea.cloud.srm.model.log.trace.dto.UserTraceInfoDto;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.*;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmStartRecord;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.CommitTaskParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.SccFlowInstanceRecord;
import com.midea.cloud.srm.model.pj.enums.FeignEnum;
import com.midea.cloud.srm.model.pj.enums.TodowithbpmStatusEnum;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pj.rbac.role.entity.PermissionLanguage;
import com.midea.cloud.srm.model.pj.rbac.role.entity.RolePermission;
import com.midea.cloud.srm.model.pj.todowithbpm.dto.SccPjTodowithbpmDto;
import com.midea.cloud.srm.model.rbac.role.entity.Role;
import com.midea.cloud.srm.model.rbac.role.entity.RoleUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangbf3
 * BPM 外部接口实现类
 */
@Slf4j
@Service
public class BpmServiceImpl implements IBpmService {

    private static final long HOW_TIME = 7200L;

    @Autowired
    private QlService qlService;

    @Autowired
    QlOpenClient qlOpenClient;
    @Autowired
    BaseClient baseClient;
    @Autowired
    PjSignClient pjSignClient;
//    @Autowired
//    LocalLoginApiFeign0 localLoginApiFeign0;
    /**
     * 流程模板对应跳转地址funName
     */
    private static final String BUSINESS_TYPE_FUN_NAME = "BUSINESS_TYPE_FUN_NAME";

    @ApiModelProperty("创建流程")
    @Value("${gwm.url.flow-create}")
    private String flowCreateUrl;


    @ApiModelProperty("按流程分组发起流程")
    @Value("${gwm.url.flowCategory-create}")
    private String createProcessByCategoryUrl;

    @ApiModelProperty("发起流程接口地址")
    @Value("${gwm.url.startProcessByCategoty-url}")
    private String startProcessByCategotyUrl;

    @ApiModelProperty("退回任务接口地址")
    @Value("${gwm.url.rollBackTask-url}")
    private String rollBackTaskUrl;

    @ApiModelProperty("转办任务接口地址")
    @Value("${gwm.url.forwardTask-url}")
    private String forwardTaskUrl;

    @ApiModelProperty("提交待办任务接口地址")
    @Value("${gwm.url.commitTask-url}")
    private String commitTaskUrl;

    @ApiModelProperty("预执行接口接口地址")
    @Value("${gwm.url.predict-url}")
    private String predictUrl;

    @ApiModelProperty("发起人撤回流程")
    @Value("${gwm.url.flow-rollBack}")
    private String rollBackProcess;


    @ApiModelProperty("更新流程")
    @Value("${gwm.url.flow-update}")
    private String flowUpdateUrl;

    @ApiModelProperty("提交流程")
    @Value("${gwm.url.flow-resubmit}")
    private String flowResubmitUrl;

    @ApiModelProperty("获取审批历史")
    @Value("${gwm.url.process-comment-info}")
    private String processCommentInfoUrl;

    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;
    @Value("${gwm.preappkey}")
    private String preappKey;
    @Value("${gwm.presecret}")
    private String presecret;

    @Value("${iam.iamDomain}")
    private String iamDomain;

    @Value("${iam.user.client.id}")
    private String iamAppKey;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${gwm.bpm.api-username}")
    private String apUsername;
    @Value("${gwm.sso.getUserInfoByTokenUrl}")
    private String getUserInfoByTokenUrl;
    @Value("${gwm.sso.platform_code}")
    private String platformCode;

    @Value("${srm.baseUrl}")
    private String srmBaseUrl;
    @Value("${gwm.appId}")
    private String appId;
    @Value("${gwm.srmbpmTodoGroupId}")
    private String srmbpmTodoGroupId;

    @Autowired
    private BaseExtClient baseExtClient;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private IDassClient iDassClient;

    @Resource
    private IInterfaceLogService interfaceLogService;

    @Resource
    private ICommitTaskParamService iCommitTaskParamService;

    @Resource
    private IBpmTaskHistoryService iBpmTaskHistoryService;
    @Resource
    private ISccPjUserService iSccPjUserService;
    @Resource
    private IBpmStartRecordService iBpmStartRecordService;
    @Resource
    private CooperateSignClient cooperateSignClient;

    private static final String TO_DO_DEAULT_USER = "SRMUSER";

    private static final String ABANDON = "废弃";
    
    private static final String NUM_ONE = "1";

    private static final Integer NUM_TWO_HUNDRED = 200;

    private static final String STATUS = "status";

    private static final String DATA = "data";

    private static final String CODE = "code";

    private static final String MQL_PR_SOU_REQUIREMENT_INIT = "MQL_PR_SOU_REQUIREMENT_INIT";

    private static final String BORROW = "BORROW";

    private static final String PRICE_FLAG = "priceFlag";
    
    @Override
    public BpmResultDTO<BpmCreateResult> createProcess(JSONObject requestJsn, String dataId) {
        String url = flowCreateUrl;
        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);
        headers.put("USERID",apUsername);
        headers.put("DATA-ID",dataId);
        OpenClient openClient = new OpenClient(preappKey,presecret);
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.CREATE_PROCESS;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,requestJsn);
        String result = null;
        try{
            log.info(apiInfoEnum.getServiceName()+" 参数:"+JSONObject.toJSONString(interfaceLog.getServiceInfo()));
            result = openClient.sendHttpPost(url,requestJsn.toString(),"application/json",headers);
        }catch (Exception e){
            log.info(apiInfoEnum.getServiceName()+" 报错:"+e.getMessage());
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLog.setErrorInfo(e.getMessage());
        }finally {
            interfaceLog.setReturnInfo(result);
            interfaceLogService.createInterfaceLog(interfaceLog);
        }
        return JSONObject.parseObject(result,BpmResultDTO.class);
    }

    @Override
    public BpmResultDTO<BpmCreateResult> createProcessByCategory(JSONObject requestJsn, String dataId,String businessType) {
        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord().setBusinessId(Long.valueOf(dataId)).setTemplateCode(businessType));
        log.info("createProcess创建BPM==={}==={}", requestJsn, dataId);
        log.info("createProcess的record数据==={}", JSONObject.toJSONString(record));
        if (record!=null) {
            return resubmitProcess(requestJsn, dataId,businessType);
        } else {
            String url = createProcessByCategoryUrl;

            Map<String,String> headers = new HashMap<>(50);
            headers.put("SRC-SYSTEM",srcSystem);
            headers.put("USERID",apUsername);
            headers.put("DATA-ID",dataId);

            OpenClient openClient = new OpenClient(preappKey,presecret);

            BpmResultDTO<BpmCreateResult> bpmResult = new BpmResultDTO<>();
            ApiInfoEnum apiInfoEnum = ApiInfoEnum.CREATE_PROCESS;
            InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,requestJsn);
//            log.info(apiInfoEnum.getServiceName()+" 参数："+JSONObject.toJSONString(interfaceLog.getServiceInfo()));
            String result = openClient.sendHttpPost(url,requestJsn.toString(),"application/json",headers);
            log.info("result:"+result);
            bpmResult = JSONObject.parseObject(result,BpmResultDTO.class);
            interfaceLog.setReturnInfo(result);
            Integer successValue = new Integer(NUM_TWO_HUNDRED);
            if(bpmResult==null||!successValue.equals(bpmResult.getCode())){
                interfaceLog.setStatus(ResultStatus.FAIL.toString());
                interfaceLogService.createInterfaceLog(interfaceLog);
                throw new BaseException(bpmResult==null?null:bpmResult.getMessage());
            }
            interfaceLogService.createInterfaceLog(interfaceLog);

            return bpmResult;
        }
    }

    @Override
    public BpmResultDTO<BpmCreateResult> rollBackProcess(BpmRollBackDTO bpmRollBackDTO, String dataId) {
        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);
        headers.put("USERID",apUsername);
        headers.put("DATA-ID",dataId);
        headers.put("X-AUTH-APPIDS",appId);

        OpenClient openClient = new OpenClient(preappKey,presecret);

        BpmResultDTO<BpmCreateResult> bpmResult = new BpmResultDTO<>();
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.ROLL_BACK_PROCESS;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,bpmRollBackDTO);
        String result = null;
        try{
            log.info(apiInfoEnum.getServiceName()+" 参数："+JSONObject.toJSONString(interfaceLog.getServiceInfo()));
            result = openClient.sendHttpPost(rollBackProcess,JSONObject.toJSONString(bpmRollBackDTO),"application/json",headers);
            bpmResult = JSONObject.parseObject(result,BpmResultDTO.class);
        }catch (Exception e){
            log.info(apiInfoEnum.getServiceName()+" 报错:"+e.getMessage());
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLog.setErrorInfo(e.getMessage());
        }finally {
            interfaceLog.setReturnInfo(result);
            interfaceLogService.createInterfaceLog(interfaceLog);
        }

        return bpmResult;
    }

    @Override
    public BpmResultDTO updateBoData(JSONObject requestJsn, String dataId) {
        Map<String,String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM",srcSystem);
        headers.put("USERID",apUsername);
        headers.put("DATA-ID",dataId);

        String url = flowUpdateUrl;
        log.info("请求url"+dataId);
        log.info("请求url"+url);
        log.info("requestJsn:"+JSONObject.toJSONString(requestJsn));
        OpenClient openClient = new OpenClient(preappKey,presecret);

        BpmResultDTO bpmResult = new BpmResultDTO<>();
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.UPDATE_PROCESS;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,requestJsn);
        String result = null;
        try{
            log.info(apiInfoEnum.getServiceName()+" 参数："+JSONObject.toJSONString(interfaceLog.getServiceInfo()));
            result = openClient.sendHttpPost(url,requestJsn.toString(),"application/json",headers);
            bpmResult = JSONObject.parseObject(result,BpmResultDTO.class);
        }catch (Exception e){
            log.info(apiInfoEnum.getServiceName()+" 报错:"+e.getMessage());
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLog.setErrorInfo(e.getMessage());
        }finally {
            interfaceLog.setReturnInfo(result);
            interfaceLogService.createInterfaceLog(interfaceLog);
        }

        return bpmResult;
    }

    @Override
    public BpmResultDTO<BpmCreateResult> resubmitProcess(JSONObject requestJsn, String dataId) {
        QlOpenQueryWrapper queryWrapper = QlOpenWrappers.query("FlowInstanceRecord");
        queryWrapper.eq(SccFlowInstanceRecord::getBusinessId, dataId);
        queryWrapper.contains(SccFlowInstanceRecord::getInstanceId, "-");
        queryWrapper.orderByDesc(SccFlowInstanceRecord::getCreationDate);
        List<SccFlowInstanceRecord> record = qlOpenClient.query(ContextPath.BASE, queryWrapper, SccFlowInstanceRecord.class);
        if (CollectionUtils.isEmpty(record) || StringUtils.isBlank(record.get(0).getInstanceId())) {
            throw new BaseException("该业务没有发起过流程，无法重新提交");
        }
        log.info("resubmitProcess==={}", record.get(0).getInstanceId());
        requestJsn.put("PROCESSINSTID", record.get(0).getInstanceId());
        Map<String, String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM", srcSystem);
        headers.put("USERID", apUsername);
        headers.put("DATA-ID", dataId);
        String url = flowResubmitUrl;
        log.info("requestJsn:"+JSONObject.toJSONString(requestJsn));
        log.info("dataId:"+dataId);

        OpenClient openClient = new OpenClient(preappKey, presecret);

        BpmResultDTO bpmResult = new BpmResultDTO<>();
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.RESUBMIT_PROCESS;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,requestJsn);
        log.info(apiInfoEnum.getServiceName()+" 参数："+JSONObject.toJSONString(interfaceLog.getServiceInfo()));
        String result = openClient.sendHttpPost(url, requestJsn.toString(), "application/json", headers);
        log.info("result:"+result);
        bpmResult = JSONObject.parseObject(result,BpmResultDTO.class);
        interfaceLog.setReturnInfo(result);
        Integer successValue = new Integer(NUM_TWO_HUNDRED);
        if(!successValue.equals(bpmResult.getCode())){
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLogService.createInterfaceLog(interfaceLog);
            throw new BaseException(bpmResult.getMessage());
        }
        interfaceLogService.createInterfaceLog(interfaceLog);
        return bpmResult;
    }

    @Override
    public BpmResultDTO<BpmCreateResult> resubmitProcess(JSONObject requestJsn, String dataId,String businessType) {
        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord().setBusinessId(Long.valueOf(dataId)).setTemplateCode(businessType));
        if (record==null||StringUtils.isBlank(record.getInstanceId())) {
            throw new BaseException("该业务没有发起过流程，无法重新提交");
        }
        log.info("resubmitProcess==={}", record.getInstanceId());
        requestJsn.put("PROCESSINSTID", record.getInstanceId());
        Map<String, String> headers = new HashMap<>(50);
        headers.put("SRC-SYSTEM", srcSystem);
        headers.put("USERID", apUsername);
        headers.put("DATA-ID", dataId);
        String url = flowResubmitUrl;
        log.info("requestJsn:"+JSONObject.toJSONString(requestJsn));
        log.info("dataId:"+dataId);

        OpenClient openClient = new OpenClient(preappKey, presecret);

        BpmResultDTO bpmResult = new BpmResultDTO<>();
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.RESUBMIT_PROCESS;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,requestJsn);
        log.info(apiInfoEnum.getServiceName()+" 参数："+JSONObject.toJSONString(interfaceLog.getServiceInfo()));
        String result = openClient.sendHttpPost(url, requestJsn.toString(), "application/json", headers);
        log.info("result:"+result);
        bpmResult = JSONObject.parseObject(result,BpmResultDTO.class);
        interfaceLog.setReturnInfo(result);
        Integer successValue = new Integer(NUM_TWO_HUNDRED);
        if(!successValue.equals(bpmResult.getCode())){
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLogService.createInterfaceLog(interfaceLog);
            throw new BaseException(bpmResult.getMessage());
        }
        interfaceLogService.createInterfaceLog(interfaceLog);
        return bpmResult;
    }

    @Override
    public BpmResultDTO<List<BpmFlowList>> getCommentAndTodoTaskList(JSONObject requestJsn) {
        String url = processCommentInfoUrl;

        OpenClient openClient = new OpenClient(preappKey,presecret);



        String result = openClient.sendHttpPost(url,requestJsn.toString(),"application/json");

        log.info("请求url："+url);
        log.info("返回结果："+result);

        BpmResultDTO bpmResultDTO = JSONObject.parseObject(result,BpmResultDTO.class);
        List<BpmFlowList> bpmFlowLists = new ArrayList<>();
        JSONArray jsonArray = (JSONArray) bpmResultDTO.getData();
        for(int i=0;i<jsonArray.size();i++){
            bpmFlowLists.add(JSONObject.parseObject(jsonArray.getJSONObject(i).toJSONString(),BpmFlowList.class));
        }
        bpmResultDTO.setData(bpmFlowLists);
        return bpmResultDTO;
    }

    /**
     * BPM审批结果回调SRM
     * @param bpmCallback
     * @deprecated 备注
     * @throws Exception 报错
     */
    @Override
    public void callback(BpmCallback bpmCallback) throws Exception {
        log.info("bpmCallback:"+JSONObject.toJSONString(bpmCallback));
        interfaceLogService.createInterfaceLog(new InterfaceLogDTO(ApiInfoEnum.BPM_CALLBACK,bpmCallback));
        OaRequestDTO wfOaParam = new OaRequestDTO();

        String action = null;
        String endText = "end";
        String endText2 = "terminate";
        String returnText = "退回";
        String returnTextDirect = "退回直达";
        String returnZbText = "退回招标负责人";
        if(StringUtils.equals(bpmCallback.getControlState(),endText)){
            log.info("审批通过");
            /*审批通过 */
            action = "passFlow";
        } else if(StringUtils.equals(bpmCallback.getActionName(), ABANDON)&&StringUtils.equals(bpmCallback.getControlState(),endText2)){
            /*废弃 */
            log.info(ABANDON);
            action = "destoryFlow";
        }  else if(StringUtils.equals(bpmCallback.getControlState(),endText2)){
            /*提前审批结束 */
            log.info("提前审批结束");
            action = "passFlow";
        } else if (StringUtils.equals(bpmCallback.getActionName(),returnText) || StringUtils.equals(bpmCallback.getActionName(),returnTextDirect)
                || StringUtils.equals(bpmCallback.getActionName(),returnZbText)) {
            /*驳回 */
            action = "rejectFlow";
        }else{
            /*其他情况下不处理 */
            return;
        }
        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord().setInstanceId(bpmCallback.getProcessInstId()).setBusinessId(bpmCallback.getBussinessId()));

        wfOaParam.setAction(action);
        wfOaParam.setFlowKey(record.getTemplateCode());
        wfOaParam.setFormDataId(record.getBusinessId());
        wfOaParam.setCreatedBy(JSONObject.toJSONString(bpmCallback.getData()));

        baseExtClient.callback(wfOaParam);
        iBpmTaskHistoryService.savePassBpmTaskHistory(record);
    }

    /**
     * 获取审批流程记录
     * @param flowInstanceRecord 业务单据流程实例关联对象
     * @return
     */
    @Override
    public FlowInstanceRecord getLastFlowInstanceRecord(FlowInstanceRecord flowInstanceRecord) {
        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("FlowInstanceRecord");
        wrapper.eq(StringUtils.isNotBlank(flowInstanceRecord.getInstanceId()), FlowInstanceRecord::getInstanceId, flowInstanceRecord.getInstanceId());
        wrapper.eq(flowInstanceRecord.getBusinessId()!=null, FlowInstanceRecord::getBusinessId, flowInstanceRecord.getBusinessId());
        wrapper.eq(StringUtils.isNotBlank(flowInstanceRecord.getTemplateCode()), FlowInstanceRecord::getTemplateCode, flowInstanceRecord.getTemplateCode());
        wrapper.eq(StringUtils.isNotBlank(flowInstanceRecord.getDealStatus()), FlowInstanceRecord::getDealStatus, flowInstanceRecord.getDealStatus());
        wrapper.eq(StringUtils.isNotBlank(flowInstanceRecord.getFlowStatus()), FlowInstanceRecord::getFlowStatus, flowInstanceRecord.getFlowStatus());
        wrapper.contains(SccFlowInstanceRecord::getInstanceId, "-");
        if(flowInstanceRecord.getBusinessId()!=null){
            wrapper.ne(flowInstanceRecord.getBusinessId()!=null, FlowInstanceRecord::getInstanceId, flowInstanceRecord.getBusinessId().toString());
        }
        wrapper.eq(FlowInstanceRecord::getFlowStatus, "SUBMITTED");
        wrapper.orderByDesc(FlowInstanceRecord::getFlowInstanceRecordId);

        Page<FlowInstanceRecord> page = qlOpenClient.query(ContextPath.BASE,wrapper,Long.valueOf(1),Long.valueOf(1),FlowInstanceRecord.class);

        FlowInstanceRecord record = page.getRecords().size()==0?null:page.getRecords().get(0);

        return record;
    }

    Map<String, String> tokenMap = new HashMap<>(50);

    @Override
    public void viewSrm(String token, String redirectUri) throws IOException {

    }

    @Override
    public void srmDirect(String token, String redirectUri) throws IOException {

    }

    public void saveUserTrace(String username, String userType) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddr = IPUtil.getRemoteIpAddr(request);
        CompletableFuture.runAsync(() -> {
            try {
                UserTraceInfoDto userTraceInfoDto = new UserTraceInfoDto();
                userTraceInfoDto.setUsername(username);
                userTraceInfoDto.setLogIp(ipAddr);
                if (StringUtils.isBlank(userType)) {
                    LoginAppUser byUsername = this.rbacClient.findByUsername(username);
                    userTraceInfoDto.setUserType(byUsername.getUserType());
                } else {
                    userTraceInfoDto.setUserType(userType);
                }

                rbacClient.saveUserTrace(userTraceInfoDto);
            } catch (Exception var6) {
            }

        });
    }

    /**
     * BPM审批人权限校验
     * @param processInstId
     * @param username
     */
    @Override
    public void authority(String processInstId, String username) {

        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("FlowInstanceRecord");
        wrapper.eq(FlowInstanceRecord::getInstanceId,processInstId);
        wrapper.orderByDesc(FlowInstanceRecord::getFlowInstanceRecordId);

        Page<FlowInstanceRecord> page = qlOpenClient.query(ContextPath.BASE,wrapper,Long.valueOf(1),Long.valueOf(1),FlowInstanceRecord.class);

        FlowInstanceRecord record = page.getRecords().get(0);

        Map<String, String> bpmAuthorityMap = baseClient.getDictItmeMapByDictCode("BPM_AUTHORITY");

        //对应功能名称
        String permissionName = bpmAuthorityMap.get(record.getTemplateCode());

        Assert.notNull(permissionName,"没有审批权限");

        User user = rbacClient.getUser(new User().setUsername(username));
        Assert.notNull(user,"找不到该用户");

        List<RoleUser> roleUsers = rbacClient.getRoleByUserId(Arrays.asList(new Long[]{user.getUserId()}));
        Assert.isTrue(roleUsers!=null&&roleUsers.size()>0,"没有审批权限");

        List<Role> roles = rbacClient.getRoleCodeByUserIdForAnon(roleUsers.stream().map(RoleUser::getRoleId).collect(Collectors.toList()));


        QlOpenQueryWrapper rolePermissionWrapper = QlOpenWrappers.query("RolePermission");
        wrapper.in(RolePermission::getRoleId,roles.stream().map(Role::getRoleCode).collect(Collectors.toList()));
        List<RolePermission> rolePermissions = qlOpenClient.query(ContextPath.RBAC,rolePermissionWrapper,RolePermission.class);

        Assert.isTrue(rolePermissions!=null&&rolePermissions.size()>0,"没有审批权限");

        QlOpenQueryWrapper permissionLanguageWrapper = QlOpenWrappers.query("PermissionLanguage");
        wrapper.in(PermissionLanguage::getPermissionId,rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toList()));
        List<PermissionLanguage> permissionLanguages = qlOpenClient.query(ContextPath.RBAC,permissionLanguageWrapper,PermissionLanguage.class);
        Assert.isTrue(permissionLanguages!=null&&permissionLanguages.size()>0,"没有审批权限");

        Set<String> permissionNameSet = permissionLanguages.stream().map(PermissionLanguage::getPermissionName).collect(Collectors.toSet());
        Assert.isTrue(permissionNameSet.contains(permissionName),"没有审批权限");
    }

    @Override
    public List<FlowInstanceRecord> getFlowInstanceRecord(FlowInstanceRecord flowInstanceRecord) {
        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("FlowInstanceRecord");
        wrapper.eq(StringUtils.isNotBlank(flowInstanceRecord.getInstanceId()), FlowInstanceRecord::getInstanceId, flowInstanceRecord.getInstanceId());
        wrapper.eq(flowInstanceRecord.getBusinessId()!=null, FlowInstanceRecord::getBusinessId, flowInstanceRecord.getBusinessId());
        wrapper.orderByDesc(FlowInstanceRecord::getFlowInstanceRecordId);
        List<FlowInstanceRecord>  list = qlOpenClient.query(ContextPath.BASE,wrapper,FlowInstanceRecord.class);
        return list;
    }

    @Override
    public String getViewSrmRollBackUrl(String funName, Long formId, String formNo) {

        String urlparam = "from=fromFun&funName="+funName+"&formId="+formId+"&formNo="+formNo;
        urlparam = Base64.getEncoder().encodeToString( urlparam.getBytes());
        urlparam  =  urlparam.replaceAll("\\+", "-").replaceAll("/", "_").replaceAll("=", ".");
        String url = srmBaseUrl+"cloud-srm/api-pj/external/bpm/viewSrm?token=Bpm_TokenValue&redirectUri="+srmBaseUrl+"%23/flowTaskViewBase/" +  urlparam;
        return url;
    }

    @Override
    public String getViewSrmRollBackUrl(String funName, Long formId, String formNo,String otherUrlParam) {
        String urlparam = "from=fromFun&funName="+funName+"&formId="+formId+"&formNo="+formNo;
        if(StringUtils.isNotBlank(otherUrlParam)){
            urlparam+="&"+otherUrlParam;
        }
        urlparam = Base64.getEncoder().encodeToString( urlparam.getBytes());
        urlparam  =  urlparam.replaceAll("\\+", "-").replaceAll("/", "_").replaceAll("=", ".");
        String url = srmBaseUrl+"cloud-srm/api-pj/external/bpm/viewSrm?token=Bpm_TokenValue&redirectUri="+srmBaseUrl+"%23/flowTaskViewBase/" +  urlparam;
        return url;
    }

    @Override
    public List<BpmFlowList> findTaskList(Long bussinessId, String bussinessType) {
        Assert.notNull(bussinessId,"单据ID不能为空");
        Assert.isTrue(StringUtils.isNotBlank(bussinessType),"模板编码不能为空");
        QlOpenQueryWrapper wrapper = QlOpenWrappers.query("FlowInstanceRecord");
        wrapper.eq(FlowInstanceRecord::getBusinessId, bussinessId);
        wrapper.eq(FlowInstanceRecord::getTemplateCode, bussinessType);
        wrapper.orderByDesc(FlowInstanceRecord::getFlowInstanceRecordId);
        Page<FlowInstanceRecord> recordPage = qlOpenClient.query(ContextPath.BASE,wrapper,1L,1L,FlowInstanceRecord.class);

        Assert.isTrue(recordPage.getPageSize()==1,"找不到bpm流程实例");
        Assert.notBlank(recordPage.getRecords().get(0).getInstanceId(),"找不到bpm流程实例");

        JSONObject req = new JSONObject();
        req.put("processInsId",recordPage.getRecords().get(0).getInstanceId());
        return getCommentAndTodoTaskList(req).getData();
    }

    @Override
    public void rollBackAll(BpmRollBackDTO bpmRollBackDTO) {
        log.info("bpmRollBackDTO:"+JSONObject.toJSONString(bpmRollBackDTO));
        Assert.notBlank(bpmRollBackDTO.getBussinessType(),"模板不能为空");
        Assert.isTrue(bpmRollBackDTO.getDataId()!=null,"单据ID不能为空");
        Assert.notBlank(bpmRollBackDTO.getCommentmsg(),"撤回说明不能为空");

        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord().setBusinessId(bpmRollBackDTO.getDataId()).setTemplateCode(bpmRollBackDTO.getBussinessType()));

        if (record == null) {
            throw new BaseException("当前单据没有流程，无法撤回");
        }
        Assert.isTrue(record!=null,"找不到bpm流程实例");

        bpmRollBackDTO.setProcessinstid(record.getInstanceId());
        bpmRollBackDTO.setCreateuser(record.getCreatedBy());
        BpmResultDTO<BpmCreateResult> bpmResultDTO = rollBackProcess(bpmRollBackDTO,bpmRollBackDTO.getDataId().toString());
        Integer successValue = new Integer(NUM_TWO_HUNDRED);
        if(!successValue.equals(bpmResultDTO.getCode())){
            throw new BaseException(bpmResultDTO.getMessage());
        }

        OaRequestDTO wfOaParam = new OaRequestDTO();

        wfOaParam.setAction("withdrawFlow");
        wfOaParam.setFlowKey(bpmRollBackDTO.getBussinessType());
        wfOaParam.setFormDataId(bpmRollBackDTO.getDataId());

        try {
            baseExtClient.callback(wfOaParam);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *  发起流程
     * @param businessId 业务单据ID
     * @param businessType 审批流模板
     * @param bpmParam 发起流程请求参数
     * @return BPM发起流程返回结果
     */
    @Override
    public BpmResultDTO<BpmCreateResult> startProcessByCategoty(Long businessId,String businessType,BpmStartProcessParam bpmParam) {
        BpmStartRecord bpmStartRecord = iBpmStartRecordService.getOne(Wrappers.lambdaQuery(BpmStartRecord.class)
                .eq(BpmStartRecord::getBusinessId,businessId).eq(BpmStartRecord::getBussinessType,businessType));
        log.info("bpmStartRecord:{}",JSONObject.toJSONString(bpmStartRecord));
        if(bpmStartRecord!=null){
            return JSONObject.parseObject(bpmStartRecord.getReturnInfo(),BpmResultDTO.class);
        }

        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord().setBusinessId(businessId).setTemplateCode(businessType));
        if(record!=null){
            bpmParam.setProcessDefId(record.getInstanceId());
        }
        bpmParam.setCommentInfo(new BpmCommentInfo().setActionName("提交").setCommentMsg(""));
        bpmParam.setAppId(appId);
        bpmParam.setCreateUser(StringUtils.isBlank(bpmParam.getCreateUser())?AppUserUtil.getUserName():bpmParam.getCreateUser());
        bpmParam.setAutoCompleteTask(false);

        if(Objects.isNull(bpmParam.getCreateOrgId())) {
            HrUserOrgnizationDto orgnizationDto = iSccPjUserService.getHrUserOrgnizationByUsername(bpmParam.getCreateUser());
            if(orgnizationDto!=null&&orgnizationDto.getOuOrganization()!=null){
                String[] orgCodeArr = orgnizationDto.getOuOrganization().getOrganizationCode().split("_");
                bpmParam.setCreateOrgId(orgCodeArr[orgCodeArr.length-1]);
            }
        }
        Map<String,String> headers = new HashMap<>(50);
        headers.put("X-AUTH-APPIDS",appId);

        OpenClient openClient = new OpenClient(preappKey,presecret);

        ApiInfoEnum apiInfoEnum = ApiInfoEnum.START_PROCESS_BY_CATEGOTY;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,bpmParam);
        String bpmParamStr = JSONObject.toJSONString(bpmParam);
        String result = openClient.sendHttpPost(startProcessByCategotyUrl,bpmParamStr,"application/json",headers);
        JSONObject resultJson = JSONObject.parseObject(result);
        interfaceLog.setReturnInfo(result);
        interfaceLog.setStatus(ResultStatus.SUCCESS.toString());
        if(!NUM_ONE.equals(resultJson.getString(STATUS))||resultJson.getJSONObject(DATA)==null
        ||resultJson.getJSONObject(DATA).getJSONObject("processInst")==null){
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLogService.createInterfaceLog(interfaceLog);
            String msg = "";
            if(resultJson.containsKey(CODE)){
                msg = resultJson.getString("message");
            }else if(resultJson.containsKey(STATUS)){
                msg = resultJson.getString("msg");
            }
            throw new BaseException(msg);
        }
        BpmResultDTO<BpmCreateResult> bpmResult = new BpmResultDTO<>();
        BpmCreateResult bpmCreateResult = new BpmCreateResult();
        bpmCreateResult.setProcessInstId(resultJson.getJSONObject(DATA).getJSONObject("processInst").getString("id"));
        bpmResult.setData(bpmCreateResult);

        interfaceLogService.createInterfaceLog(interfaceLog);

        iBpmStartRecordService.save(new BpmStartRecord()
                .setBpmStartRecordId(IdGenrator.generate())
                .setBusinessId(businessId)
                .setBussinessType(businessType)
                .setBpmStartRecordId(IdGenrator.generate())
                .setServiceInfo(bpmParamStr)
                .setReturnInfo(JSONObject.toJSONString(bpmResult)));
        return bpmResult;
    }

    /**
     *  退回任务
     * @param bpmParam 退回任务
     */
    @Override
    public JSONObject rollBackTask(BpmRollBackTaskParam bpmParam) {
        OpenClient openClient = new OpenClient(preappKey,presecret);
        Map<String,String> headers = new HashMap<>(50);
        headers.put("X-AUTH-APPIDS",appId);

        String result = null;
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.ROLL_BACK_TASK;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,bpmParam);
        try{
            result = openClient.sendHttpPost(rollBackTaskUrl,JSONObject.toJSONString(bpmParam),"application/json",headers);
        }catch (Exception e){
            log.info(apiInfoEnum.getServiceName()+" 报错:"+e.getMessage());
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLog.setErrorInfo(e.getMessage());
        }finally {
            interfaceLog.setReturnInfo(result);
            interfaceLogService.createInterfaceLog(interfaceLog);
        }
        return JSONObject.parseObject(result);
    }
    /**
     *  退回任务
     * @param rollBackTaskVo 退回任务
     */
    @Override
    public void rollBackTask(BpmRollBackTaskVo rollBackTaskVo) throws Exception {
        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord().setBusinessId(rollBackTaskVo.getBusinessId()).setTemplateCode(rollBackTaskVo.getBusinessType()));

        //1、BPM 退回
        BpmCommitTaskParam bpmParam = new BpmCommitTaskParam();
        BeanUtils.copyProperties(rollBackTaskVo,bpmParam);
        bpmParam.setProcessInstId(record.getInstanceId());
        bpmParam.setCurrentUser(AppUserUtil.getUserName());
        bpmParam.setProcessVars(new JSONObject());

        JSONObject rollBackTaskResult = this.commitTask(bpmParam);

        if(!StringUtils.equals(rollBackTaskResult.getString(STATUS),NUM_ONE)){
            String msg = "";
            if(rollBackTaskResult.containsKey(CODE)){
                msg = rollBackTaskResult.getString("message");
            }else if(rollBackTaskResult.containsKey(CODE)){
                msg = rollBackTaskResult.getString("msg");
            }
            throw new BaseException(msg);
        }

        //2、调产品审批驳回回调更新状态接口
        OaRequestDTO wfOaParam = new OaRequestDTO();
        wfOaParam.setAction("rejectFlow");
        wfOaParam.setFlowKey(rollBackTaskVo.getBusinessType());
        wfOaParam.setFormDataId(rollBackTaskVo.getBusinessId());
        baseExtClient.callback(wfOaParam);

        //3刷新审批历史记录
        iBpmTaskHistoryService.savePassBpmTaskHistory(record);

    }

    /**
     *  转办任务
     * @param bpmForwardTaskVo 转办任务
     */
    @Override
    public void forwardTask(BpmForwardTaskVo bpmForwardTaskVo) throws Exception {
        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord().setBusinessId(bpmForwardTaskVo.getBusinessId())
                .setTemplateCode(bpmForwardTaskVo.getBusinessType()));

        //1、BPM 转办
        BpmForwardTaskParam bpmParam = new BpmForwardTaskParam();
        BeanUtils.copyProperties(bpmForwardTaskVo,bpmParam);
        bpmParam.setProcessInstId(record.getInstanceId());
        bpmParam.setCurrentUser(AppUserUtil.getUserName());
        if(bpmParam.getCommentInfo()==null){
            bpmParam.setCommentInfo(new BpmCommentInfo().setActionName("转办"));
        }else{
            bpmParam.getCommentInfo().setActionName("转办");
        }
        bpmParam.setProcessVars(new JSONObject());

        JSONObject rollBackTaskResult = this.forwardTask(bpmParam);

        if(!StringUtils.equals(rollBackTaskResult.getString(STATUS),NUM_ONE)){
            String msg = "";
            if(rollBackTaskResult.containsKey(CODE)){
                msg = rollBackTaskResult.getString("message");
            }else if(rollBackTaskResult.containsKey(CODE)){
                msg = rollBackTaskResult.getString("msg");
            }
            throw new BaseException(rollBackTaskResult.getString("msg"));
        }

        //2刷新审批历史记录
        iBpmTaskHistoryService.savePassBpmTaskHistory(record);

    }
    /**
     *  转办任务
     * @param bpmParam 转办任务
     */
    @Override
    public JSONObject forwardTask(BpmForwardTaskParam bpmParam) {
        OpenClient openClient = new OpenClient(preappKey,presecret);
        Map<String,String> headers = new HashMap<>(50);
        headers.put("X-AUTH-APPIDS",appId);

        String result = null;
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.FORWARD_TASK;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,bpmParam);
        try{
            result = openClient.sendHttpPost(forwardTaskUrl,JSONObject.toJSONString(bpmParam),"application/json",headers);
        }catch (Exception e){
            log.info(apiInfoEnum.getServiceName()+" 报错:"+e.getMessage());
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLog.setErrorInfo(e.getMessage());
        }finally {
            interfaceLog.setReturnInfo(result);
            interfaceLogService.createInterfaceLog(interfaceLog);
        }
        return JSONObject.parseObject(result);
    }

    /**
     *  提交待办任务
     * @param bpmParam 提交待办任务
     */
    @Override
    public JSONObject commitTask(BpmCommitTaskParam bpmParam) {
        if(bpmParam.getProcessVars()==null){
            bpmParam.setProcessVars(new JSONObject());
        }

        OpenClient openClient = new OpenClient(preappKey,presecret);
        Map<String,String> headers = new HashMap<>(50);
        headers.put("X-AUTH-APPIDS",appId);
        JSONObject resultJson = new JSONObject();
        String result = null;
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.COMMIT_TASK;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,bpmParam);
        try{
            result = openClient.sendHttpPost(commitTaskUrl,JSONObject.toJSONString(bpmParam),"application/json",headers);
            resultJson = JSONObject.parseObject(result);
        }catch (Exception e){
            log.info(apiInfoEnum.getServiceName()+" 报错:"+e.getMessage());
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLog.setErrorInfo(e.getMessage());
        }finally {
            interfaceLog.setReturnInfo(result);
            interfaceLogService.createInterfaceLog(interfaceLog);
        }

        if(!NUM_ONE.equals(resultJson.getString(STATUS))){
            String msg = "";
            if(resultJson.containsKey(CODE)){
                msg = resultJson.getString("message");
            }else if(resultJson.containsKey(STATUS)){
                msg = resultJson.getString("msg");
            }
            throw new BaseException(msg);
        }
        return resultJson;
    }

    /**
     *  预执行接口
     * @param bpmParam 预执行接口
     */
    @Override
    public JSONObject predict(BpmCommitTaskVo commitTaskVo,BpmPredictParam bpmParam) {
        Assert.isTrue(StringUtils.isNotBlank(bpmParam.getProcessInstId()),"流程实例ID不能为空");
        OpenClient openClient = new OpenClient(preappKey,presecret);
        Map<String,String> headers = new HashMap<>(50);
        headers.put("X-AUTH-APPIDS",appId);

        String result = null;
        JSONObject resultJson = new JSONObject();
        ApiInfoEnum apiInfoEnum = ApiInfoEnum.PREDICT;
        InterfaceLogDTO interfaceLog = new InterfaceLogDTO(apiInfoEnum,bpmParam);
        try{
            result = openClient.sendHttpPost(predictUrl,JSONObject.toJSONString(bpmParam),"application/json",headers);
            resultJson = JSONObject.parseObject(result);
        }catch (Exception e){
            log.info(apiInfoEnum.getServiceName()+" 报错:"+e.getMessage());
            interfaceLog.setStatus(ResultStatus.FAIL.toString());
            interfaceLog.setErrorInfo(e.getMessage());
        }finally {
            interfaceLog.setReturnInfo(result);
            interfaceLogService.createInterfaceLog(interfaceLog);
        }

        if(!new Integer(NUM_TWO_HUNDRED).equals(resultJson.getInteger(CODE))){
            String msg = "";
            if(resultJson.containsKey(CODE)){
                msg = resultJson.getString("message");
            }else if(resultJson.containsKey(STATUS)){
                msg = resultJson.getString("msg");
            }
            throw new BaseException(msg);
        }else{
            //3、保存记录存储节点和对应的人员信息
            iCommitTaskParamService.saveOrUpdateCommitTaskParam(new CommitTaskParam().setBusinessId(commitTaskVo.getBusinessId())
                    .setBussinessType(commitTaskVo.getBusinessType()).setPredictActivityParam(result));
        }

        setBpmPeopleFlag(resultJson, commitTaskVo.getBusinessId(), commitTaskVo.getBusinessType());
        if(StringUtils.equals(commitTaskVo.getBusinessType(),MQL_PR_SOU_REQUIREMENT_INIT)){
            setBidFuzeren(resultJson, commitTaskVo.getBusinessId());
        }else if(StringUtils.equals(commitTaskVo.getBusinessType(),BORROW)){
            setBorrowPeople(resultJson, commitTaskVo.getBusinessId());
        }
        return resultJson;
    }

    /**
     * 设置借阅审批默认人
     * @param resultJson
     * @param businessId
     */
    private void setBorrowPeople(JSONObject resultJson, Long businessId) {
        Record borrow = qlOpenClient.read(ContextPath.SOU,"Borrow",businessId,Record.class);

        if(StringUtils.equals(borrow.getString(PRICE_FLAG),YesOrNo.Y.name())){
            int size = resultJson.getJSONArray(DATA).size();
            for(int i=0;i<size;i++){
                JSONObject taskItem = resultJson.getJSONArray(DATA).getJSONObject(i);
                JSONArray executor = taskItem.getJSONArray("executor");
                if(StringUtils.contains(taskItem.getString("activityName"),"外单位总经理")){
                    JSONObject executorItem = new JSONObject();
                    executorItem.put("isBpmPeople",false);
                    executorItem.put("userId",borrow.getString("managerCode"));
                    executorItem.put("userName",borrow.getString("managerName"));
                    if(executor.size()>0&&executor.getJSONObject(0).containsKey("isEnd")){
                        executorItem.put("isEnd",executor.getJSONObject(0).getString("isEnd"));
                    }
                    if(executor.size()>0&&executor.getJSONObject(0).containsKey("taskInstId")){
                        executorItem.put("taskInstId",executor.getJSONObject(0).getString("taskInstId"));
                    }
                    executor = new JSONArray();
                    executor.add(executorItem);
                }
                taskItem.put("executor",executor);
            }
        }
    }

    /**
     * 设置招标负责人
     * @param resultJson
     * @param businessId
     */
    private void setBidFuzeren(JSONObject resultJson, Long businessId) {
        JSONObject fuzeren = null;

        int size = resultJson.getJSONArray(DATA).size();
        Boolean callApi = false;
        for(int i=0;i<size;i++){
            JSONObject taskItem = resultJson.getJSONArray(DATA).getJSONObject(i);
            JSONArray executor = taskItem.getJSONArray("executor");

            if((StringUtils.contains(taskItem.getString("activityName"),"招标负责人") || StringUtils.contains(taskItem.getString("activityName"),"供应商负责人")) && CollectionUtils.isEmpty(executor)) {
                callApi = true;
            }
        }

        if(!callApi) {
            return;
        }

        fuzeren = cooperateSignClient.getBidFuZeRen(businessId);
        for(int i=0;i<size;i++){
            JSONObject taskItem = resultJson.getJSONArray(DATA).getJSONObject(i);
            JSONArray executor = taskItem.getJSONArray("executor");
            boolean isEmpty = CollectionUtils.isEmpty(executor);
            if(StringUtils.contains(taskItem.getString("activityName"),"招标负责人")&&fuzeren.containsKey("zzbfzr")){
                if(executor.size()>0&&executor.getJSONObject(0).containsKey("isEnd")){
                    fuzeren.getJSONObject("zzbfzr").put("isEnd",executor.getJSONObject(0).getString("isEnd"));
                }
                if(executor.size()>0&&executor.getJSONObject(0).containsKey("taskInstId")){
                    fuzeren.getJSONObject("zzbfzr").put("taskInstId",executor.getJSONObject(0).getString("taskInstId"));
                }
                executor = new JSONArray();
                executor.add(fuzeren.getJSONObject("zzbfzr"));
            }else if(StringUtils.contains(taskItem.getString("activityName"),"供应商负责人")&&fuzeren.containsKey("zgysfzr")){
                if(executor.size()>0&&executor.getJSONObject(0).containsKey("isEnd")){
                    fuzeren.getJSONObject("zgysfzr").put("isEnd",executor.getJSONObject(0).getString("isEnd"));
                }
                if(executor.size()>0&&executor.getJSONObject(0).containsKey("isEnd")){
                    fuzeren.getJSONObject("zgysfzr").put("taskInstId",executor.getJSONObject(0).getString("taskInstId"));
                }
                executor = new JSONArray();
                executor.add(fuzeren.getJSONObject("zgysfzr"));
            }
            if(isEmpty) {
                taskItem.put("executor",executor);
            }
        }
    }

    /**
     * 设置bpm默认的人员标识
     * @param resultJson
     * @param businessId
     * @param bussinessType
     */
    private void setBpmPeopleFlag(JSONObject resultJson,Long businessId,String bussinessType){
        CommitTaskParam dbCommitTaskParam = iCommitTaskParamService.lambdaQuery()
                .eq(CommitTaskParam::getBusinessId,businessId)
                .eq(CommitTaskParam::getBussinessType,bussinessType)
                .one();
        if(dbCommitTaskParam!=null&&StringUtils.isNotBlank(dbCommitTaskParam.getFirstPredictActivityParam())&&resultJson.getJSONArray(DATA)!=null){
            int size = resultJson.getJSONArray(DATA).size();
            for(int i=0;i<size;i++){
                JSONObject taskItem = resultJson.getJSONArray(DATA).getJSONObject(i);
                JSONArray executor = taskItem.getJSONArray("executor");
                String activityDefId = taskItem.getString("activityDefId");

                if(executor!=null&&executor.size()>0){
                    int size2 = executor.size();
                    for(int ii=0;ii<size2;ii++){
                        JSONObject executorItem = executor.getJSONObject(ii);
                        String userId = executorItem.getString("userId");
                        executorItem.put("isBpmPeople",isBpmPeople(dbCommitTaskParam,activityDefId,userId));
                    }
                }
            }
        }
    }

    /**
     * 校验是否bpm的默认人
     * @param dbCommitTaskParam BPM提交时报错的数据
     * @param activityDefId 流程接口ID
     * @param userId 用户账号
     * @return 是否bpm默认的人员
     */
    private boolean isBpmPeople(CommitTaskParam dbCommitTaskParam,String activityDefId,String userId){
        boolean isBpmPeole = false;
        if(StringUtils.isNotBlank(dbCommitTaskParam.getFirstPredictActivityParam())){
            JSONObject bpmPeopleJson = JSONObject.parseObject(dbCommitTaskParam.getFirstPredictActivityParam());
            int size = bpmPeopleJson.getJSONArray(DATA).size();
            for(int i=0;i<size;i++){
                JSONObject taskItem = bpmPeopleJson.getJSONArray(DATA).getJSONObject(i);
                JSONArray executor = taskItem.getJSONArray("executor");
                String dbActivityDefId = taskItem.getString("activityDefId");
                if(StringUtils.equals(activityDefId,dbActivityDefId)){
                    if(executor!=null&&executor.size()>0){
                        int size2 = executor.size();
                        for(int ii=0;ii<size2;ii++){
                            String dbUserId = executor.getJSONObject(ii).getString("userId");
                            if(StringUtils.equals(dbUserId,userId)){
                                isBpmPeole = true;
                                return isBpmPeole;
                            }
                        }
                    }
                }
            }
        }
        return isBpmPeole;
    }



    /**
     *  提交
     * @param commitTaskVo 前端提交参数
     */
    @Override
    public void submitEngine(BpmCommitTaskVo commitTaskVo) throws Exception {
        //1、组装参数后调，调提交待办任务接口(办理动作字段：提交，流程变量.流程节点人：带上选的人)，如果失败回滚2
        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord()
                .setBusinessId(commitTaskVo.getBusinessId())
                .setTemplateCode(commitTaskVo.getBusinessType())
        );

        BpmCommitTaskParam bpmParam = new BpmCommitTaskParam();
        BeanUtils.copyProperties(commitTaskVo,bpmParam);
        if(bpmParam.getCommentInfo()==null){
//            bpmParam.setCommentInfo(new BpmCommentInfo().setActionName("提交"));
        }else{
//            bpmParam.getCommentInfo().setActionName("提交");
        }
        bpmParam.setCurrentUser(AppUserUtil.getUserName());

        bpmParam.setProcessInstId(record.getInstanceId());
        this.commitTask(bpmParam);

        //2、调产品审批提交回调更新状态接口
        String redisKey = commitTaskVo.getBusinessId()+commitTaskVo.getBusinessType()+"submitFlow";
        redisUtil.set(redisKey, YesOrNo.Y.name(),20);
        OaRequestDTO wfOaParam = new OaRequestDTO();
        wfOaParam.setAction("submitFlow");
        wfOaParam.setFlowKey(commitTaskVo.getBusinessType());
        wfOaParam.setFormDataId(commitTaskVo.getBusinessId());
        baseExtClient.callback(wfOaParam);

        iCommitTaskParamService.saveOrUpdateCommitTaskParam(new CommitTaskParam().setBusinessId(commitTaskVo.getBusinessId())
                .setBussinessType(commitTaskVo.getBusinessType()).setSubmitParam(JSONObject.toJSONString(bpmParam)));

        //4刷新审批历史记录
        iBpmTaskHistoryService.savePassBpmTaskHistory(record);
    }

    /**
     *  审批通过
     * @param commitTaskVo 前端审批通过参数
     */
    @Override
    public void pass(BpmCommitTaskVo commitTaskVo) throws Exception {
        //1、组装参数后调，调提交待办任务接口(办理动作字段：提交，流程变量.流程节点人：带上选的人)，如果失败回滚2
        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord().setBusinessId(commitTaskVo.getBusinessId()).setTemplateCode(commitTaskVo.getBusinessType()));

        BpmCommitTaskParam bpmParam = new BpmCommitTaskParam();
        BeanUtils.copyProperties(commitTaskVo,bpmParam);
        if(bpmParam.getCommentInfo()==null){
            bpmParam.setCommentInfo(new BpmCommentInfo().setActionName("通过"));
        }else{
            bpmParam.getCommentInfo().setActionName("通过");
        }
        bpmParam.setCurrentUser(AppUserUtil.getUserName());
        bpmParam.setProcessInstId(record.getInstanceId());

        //2、针对驳回后审批通过需要更新审批中，调产品审批提交回调更新状态接口
        OaRequestDTO wfOaParam = new OaRequestDTO();
        wfOaParam.setAction("submitFlow");
        wfOaParam.setFlowKey(commitTaskVo.getBusinessType());
        wfOaParam.setFormDataId(commitTaskVo.getBusinessId());
        baseExtClient.callback(wfOaParam);

        this.commitTask(bpmParam);

        //3刷新审批历史记录
        iBpmTaskHistoryService.savePassBpmTaskHistory(record);
    }

    /**
     *  审批通过
     * @param commitTaskVo 前端审批通过参数
     */
    @Override
    public void end(BpmCommitTaskVo commitTaskVo) {
        //1、组装参数后调，调提交待办任务接口(办理动作字段：结束，流程变量.流程节点人：带上选的人)，如果失败回滚2
        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord().setBusinessId(commitTaskVo.getBusinessId()).setTemplateCode(commitTaskVo.getBusinessType()));

        BpmCommitTaskParam bpmParam = new BpmCommitTaskParam();
        BeanUtils.copyProperties(commitTaskVo,bpmParam);
        if(bpmParam.getCommentInfo()==null){
            bpmParam.setCommentInfo(new BpmCommentInfo().setActionName("结束"));
        }else{
            bpmParam.getCommentInfo().setActionName("结束");
        }
        bpmParam.setCurrentUser(AppUserUtil.getUserName());
        bpmParam.setProcessInstId(record.getInstanceId());

        this.commitTask(bpmParam);

        //2刷新审批历史记录
        iBpmTaskHistoryService.savePassBpmTaskHistory(record);
    }

    @Override
    public JSONObject getDataPushFlow(Long businessId, String businessType) throws Exception {
        TemplateHeaderDTO templateHeaderDTO = baseExtClient.queryProcessTemplateByCode(businessType);

        Class clazz = Class.forName(FeignEnum.getBpmFeignByCode(templateHeaderDTO.getFeignClient()).getBpmFeign());
        Object bean = SpringContextHolder.getApplicationContext().getBean(clazz);
        BpmCallbackClient bpmCallbackClient = (BpmCallbackClient) bean;

        return bpmCallbackClient.getDataPushFlow(templateHeaderDTO.getBussinessClass(),businessId);
    }

    /**
     * 获取待办记录
     * @param businessId
     * @param businessType
     * @param todoUsername
     * @return
     */
    private SccPjTodowithbpmDto beforeTodowithbpm(Long businessId,String businessType, String processTitle, String todoUsername) {
        SccPjTodowithbpmDto todowithbpmDto = queryLatestTodowithbpm(businessId, businessType, todoUsername);
        if(!Objects.isNull(todowithbpmDto)) {
            if(TodowithbpmStatusEnum.TODO.getCode().equals(todowithbpmDto.getTodoStatus())) {
                throw new BaseException(MessageFormat.format("单据ID{0},单据类型{1},待办账号{2} 已存在待办", businessId, businessType, todoUsername));
            }
            if(TodowithbpmStatusEnum.HAVEDONE.getCode().equals(todowithbpmDto.getTodoStatus())) {
                todowithbpmDto = new SccPjTodowithbpmDto();
            }
        } else {
            todowithbpmDto = new SccPjTodowithbpmDto();
        }

        todowithbpmDto.setBusinessId(businessId);
        todowithbpmDto.setBusinessType(businessType);
        todowithbpmDto.setTodoUsername(todoUsername);
        todowithbpmDto.setProcessTitle(processTitle);

        qlService.save(MqlType.SCC_PJ_TODOWITHBPM, Collections.singletonList(todowithbpmDto));
        if(Objects.isNull(todowithbpmDto.getTodoId())) {
            return queryLatestTodowithbpm(businessId, businessType, todoUsername);
        }
        return todowithbpmDto;

    }

    /**
     * 查询最新的待办信息
     * @param businessId
     * @param businessType
     * @param todoUsername
     * @return
     */
    private SccPjTodowithbpmDto queryLatestTodowithbpm(Long businessId,String businessType, String todoUsername) {
        List<SccPjTodowithbpmDto> todoList = qlService.queryByWrapper(QlWrappers.query(MqlType.SCC_PJ_TODOWITHBPM).eq(SccPjTodowithbpmDto::getBusinessId, businessId)
                .eq(SccPjTodowithbpmDto::getBusinessType, businessType).eq(SccPjTodowithbpmDto::getTodoUsername, todoUsername).orderByDesc(SccPjTodowithbpmDto::getCreationDate), SccPjTodowithbpmDto.class);

        if(CollectionUtils.isNotEmpty(todoList)) {
            return todoList.get(0);
        }
        return null;
    }

    /**
     *  SRM推BPM待办流程
     * @param businessId 业务单据ID
     * @param businessType 流程模板编码
     * @param processTitle 标题
     * @param todoUsername 待办人账号
     * @param extUrlParm 扩展url参数，例如 id=1&code=2
     */
    @Override
    public void srmbpmTodo(Long businessId,String businessType,String processTitle,String todoUsername, String extUrlParm) throws Exception {

        /** 记录待办表 */
        SccPjTodowithbpmDto todowithbpmDto = beforeTodowithbpm(businessId, businessType, processTitle, todoUsername);

        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setCreateUser(TO_DO_DEAULT_USER);
        bpmParam.setProcessTitle(processTitle);
        bpmParam.setProcessGroupId(srmbpmTodoGroupId);
        JSONObject processVars = new JSONObject();
        processVars.put("DBR",todoUsername);

        if(StringUtils.isBlank(extUrlParm)) {
            extUrlParm = "todoId=" + todowithbpmDto.getTodoId();
        } else {
            extUrlParm = StringUtils.joinWith("&", extUrlParm, "todoId=" + todowithbpmDto.getTodoId());
        }

        DictItem funNameDictItem = baseExtClient.getDictItem(BUSINESS_TYPE_FUN_NAME,businessType);
        if(funNameDictItem!=null){
            processVars.put("formUrl",this.getViewSrmRollBackUrl(funNameDictItem.getDictItemName(),businessId,funNameDictItem.getItemDescription(), extUrlParm));
        }
        bpmParam.setProcessVars(processVars);

        HrUserOrgnizationDto orgnizationDto = iSccPjUserService.getHrUserOrgnizationByUsername(todoUsername);
        if(orgnizationDto!=null&&orgnizationDto.getOuOrganization()!=null){
            String[] orgCodeArr = orgnizationDto.getOuOrganization().getOrganizationCode().split("_");
            bpmParam.setCreateOrgId(orgCodeArr[orgCodeArr.length-1]);
        }

        BpmResultDTO<BpmCreateResult> resultDTO = startProcessByCategoty(todowithbpmDto.getTodoId(),businessType,bpmParam);

        List<FlowInstanceRecord> records = new ArrayList<>();
        FlowInstanceRecord record = new FlowInstanceRecord();
        record.setBusinessId(todowithbpmDto.getTodoId());
        record.setTemplateCode(businessType);
        record.setInstanceId(resultDTO.getData().getProcessInstId());
        record.setFlowStatus(ApproveStatusType.SUBMITTED.getValue());
        record.setDealStatus("SUCCESS");
        records.add(record);
        qlOpenClient.save(ContextPath.BASE,"FlowInstanceRecord",records);

        todowithbpmDto.setTodoStatus(TodowithbpmStatusEnum.TODO.getCode());
        qlService.update(MqlType.SCC_PJ_TODOWITHBPM, Collections.singletonList(todowithbpmDto));
    }

    @Override
    public void srmbpmHavedone(Long businessId, String businessType, String todoUsername) throws Exception {

        SccPjTodowithbpmDto todowithbpmDto = queryLatestTodowithbpm(businessId, businessType, todoUsername);

        if(Objects.isNull(todowithbpmDto) || !TodowithbpmStatusEnum.TODO.getCode().equals(todowithbpmDto.getTodoStatus())) {
            log.info(MessageFormat.format("单据ID {0}，单据编码{1}, 待办人员{2}非待办状态，未进行后续处理", businessId, businessType, todoUsername));
            return;
        }

        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord().setBusinessId(todowithbpmDto.getTodoId()).setTemplateCode(businessType));
        JSONObject req = new JSONObject();
        req.put("processInsId",record.getInstanceId());
        List<BpmFlowList> flowLists = getCommentAndTodoTaskList(req).getData();
        /** 拿最后的任务ID处理 */
        if(CollectionUtils.isNotEmpty(flowLists)) {
            BpmFlowList activeTask = flowLists.get(flowLists.size()-1);
            BpmCommitTaskParam bpmParam = new BpmCommitTaskParam();

            bpmParam.setCommentInfo(new BpmCommentInfo().setActionName("通过"));
            bpmParam.setCurrentUser(todoUsername);
            bpmParam.setProcessInstId(record.getInstanceId());
            bpmParam.setTaskInstId(activeTask.getTaskInstId());

            this.commitTask(bpmParam);

            //3刷新审批历史记录
            iBpmTaskHistoryService.savePassBpmTaskHistory(record);

            todowithbpmDto.setTodoStatus(TodowithbpmStatusEnum.HAVEDONE.getCode());
            qlService.update(MqlType.SCC_PJ_TODOWITHBPM, Collections.singletonList(todowithbpmDto));
        }
    }

    /**
     *  终止
     * @param commitTaskVo 前端终止参数
     */
    @Override
    public void destory(BpmCommitTaskVo commitTaskVo) throws Exception{
        //1、组装参数后调，调提交待办任务接口(办理动作字段：提交，流程变量.流程节点人：带上选的人)，如果失败回滚2
        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord().setBusinessId(commitTaskVo.getBusinessId()).setTemplateCode(commitTaskVo.getBusinessType()));

        BpmCommitTaskParam bpmParam = new BpmCommitTaskParam();
        BeanUtils.copyProperties(commitTaskVo,bpmParam);
        if(bpmParam.getCommentInfo()==null){
            bpmParam.setCommentInfo(new BpmCommentInfo().setActionName("废弃"));
        }else{
            bpmParam.getCommentInfo().setActionName("废弃");
        }
        bpmParam.setCurrentUser(AppUserUtil.getUserName());
        bpmParam.setProcessInstId(record.getInstanceId());
        this.commitTask(bpmParam);

        //2、调产品审批作废回调更新状态接口
        /*OaRequestDTO wfOaParam = new OaRequestDTO();
        wfOaParam.setAction("destoryFlow");
        wfOaParam.setFlowKey(commitTaskVo.getBusinessType());
        wfOaParam.setFormDataId(commitTaskVo.getBusinessId());
        baseExtClient.callback(wfOaParam);*/

        //3刷新审批历史记录
        iBpmTaskHistoryService.savePassBpmTaskHistory(record);
    }

    @Override
    public List<BpmFlowList> findTaskListNew(Long bussinessId, String bussinessType) {
        Assert.notNull(bussinessId,"单据ID不能为空");
        Assert.isTrue(StringUtils.isNotBlank(bussinessType),"模板编码不能为空");

        //1、需获取审批历史
        FlowInstanceRecord record = getLastFlowInstanceRecord(new FlowInstanceRecord().setBusinessId(bussinessId).setTemplateCode(bussinessType));
        JSONObject req = new JSONObject();
        req.put("processInsId",record.getInstanceId());
        List<BpmFlowList> flowLists = getCommentAndTodoTaskList(req).getData();
        List<BpmFlowList> unFlowLists = new ArrayList<>();

        //2、并补上后续的未审批人
        CommitTaskParam commitTaskParam = iCommitTaskParamService.lambdaQuery()
                .eq(CommitTaskParam::getBusinessId,bussinessId)
                .eq(CommitTaskParam::getBussinessType,bussinessType)
                .orderByDesc(CommitTaskParam::getCommitRaskParamId)
                .one()
        ;
        if(StringUtils.isNotBlank(commitTaskParam.getSubmitParam())&&StringUtils.isNotBlank(commitTaskParam.getPredictActivityParam())){
            BpmCommitTaskParam bpmParam = JSONObject.parseObject(commitTaskParam.getSubmitParam(),BpmCommitTaskParam.class);
            JSONArray predictActivityParamArr = JSONObject.parseObject(commitTaskParam.getPredictActivityParam()).getJSONArray(DATA);

            JSONObject processVars = bpmParam.getProcessVars();
            List<String> usernames = new ArrayList<>();
            for(Object username : processVars.values()){
                String[] usernamsArr = username.toString().split(" ");
                usernames.addAll(Arrays.asList(usernamsArr));
            }
            List<User> users = rbacClient.listByUserNames(usernames);
            Map<String,User> userMap = users.stream().collect(Collectors.toMap(User::getUsername, Function.identity()));

            String lastActivityDefId = null;
            for(BpmFlowList bpmFlowList:flowLists){
                if(StringUtils.isNotBlank(bpmFlowList.getActivityDefId())&&!StringUtils.equals("跳过",bpmFlowList.getActionName())){
                    lastActivityDefId = bpmFlowList.getActivityDefId();
                }
            }

            int index = predictActivityParamArr.size()-1;
            for(index = predictActivityParamArr.size()-1;index>=0;index--){
                JSONObject jsonObject = predictActivityParamArr.getJSONObject(index);
                if(StringUtils.equals(jsonObject.getString("activityDefId"),lastActivityDefId)){
                    break;
                }
            }

            for(int index2 =index+1;index2<predictActivityParamArr.size();index2++){
                JSONObject jsonObject = predictActivityParamArr.getJSONObject(index2);
                String activityDefId = jsonObject.getString("activityDefId");
                if(StringUtils.isBlank(activityDefId)||!processVars.containsKey(activityDefId)){
                    continue;
                }
                BpmFlowList bpmFlowList = new BpmFlowList();
                bpmFlowList.setActivityName(jsonObject.getString("activityName"));
                bpmFlowList.setActivityDefId(activityDefId);
                bpmFlowList.setActivityNo(jsonObject.getInteger("activityNo"));


                String[] usernamsArr = processVars.getString(activityDefId).split(" ");

                StringBuffer createUserSb = new StringBuffer();
                StringBuffer createUserNameSb = new StringBuffer();
                for(String username:usernamsArr){
                    if(userMap.containsKey(username)){
                        User user = userMap.get(username);
                        createUserSb.append(username).append(" ");
                        createUserNameSb.append(user.getNickname()).append(" ");
                    }
                }
                if(createUserSb.length()>0){
                    bpmFlowList.setCreateUser(createUserSb.substring(0,createUserSb.length()-1));
                    bpmFlowList.setCreateUserName(createUserNameSb.substring(0,createUserNameSb.length()-1));
                }

                unFlowLists.add(bpmFlowList);
            }
        }

        log.info("commitTaskParam.getSubmitParam():{}",commitTaskParam.getSubmitParam());
        log.info("flowLists:{}",JSONObject.toJSONString(flowLists));
        log.info("unFlowLists:{}",JSONObject.toJSONString(unFlowLists));
        flowLists.addAll(unFlowLists);

        //设置串并签标识
        setParallel(flowLists, commitTaskParam);
        return flowLists;
    }

    /**
     * 设置串并签标识
     * @param flowLists bpm返回的审批流记录
     * @param commitTaskParam bpm提交时保存的数据
     */
    private static void setParallel(List<BpmFlowList> flowLists, CommitTaskParam commitTaskParam) {
        if(StringUtils.isNotBlank(commitTaskParam.getPredictActivityParam())) {
            JSONArray predictActivityParamArr = JSONObject.parseObject(commitTaskParam.getPredictActivityParam()).getJSONArray(DATA);
            Map<String,Integer> activityDefIdParallelMap = new HashMap<>(15);

            for(int index =0;index<predictActivityParamArr.size();index++) {
                JSONObject jsonObject = predictActivityParamArr.getJSONObject(index);
                if (jsonObject.containsKey("activityDefId")) {
                    activityDefIdParallelMap.put(jsonObject.getString("activityDefId"), jsonObject.getInteger("parallel"));
                }
            }
            for(BpmFlowList bpmFlowList: flowLists){
                if(StringUtils.isNotBlank(bpmFlowList.getActivityDefId())&&activityDefIdParallelMap.containsKey(bpmFlowList.getActivityDefId())){
                    bpmFlowList.setParallel(activityDefIdParallelMap.get(bpmFlowList.getActivityDefId()));
                }
            }
        }
    }
}
