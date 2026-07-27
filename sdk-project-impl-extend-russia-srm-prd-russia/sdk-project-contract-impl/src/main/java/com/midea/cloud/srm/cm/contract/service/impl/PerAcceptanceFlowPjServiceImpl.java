package com.midea.cloud.srm.cm.contract.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.cm.contract.model.dto.MilestoneHasCreatePefDto;
import com.midea.cloud.srm.cm.contract.service.IContractExtService;
import com.midea.cloud.srm.cm.contract.service.IContractPerPlanExtService;
import com.midea.cloud.srm.cm.contract.utils.ContractBpmCreator;
import com.midea.cloud.srm.cm.contract.utils.DingTalkSender;
import com.midea.cloud.srm.cm.old.perform.service.PerAcceptanceService;
import com.midea.cloud.srm.cm.old.perform.service.impl.PerAcceptanceFlowServiceImpl;
import com.midea.cloud.srm.feign.ContractBaseExtClient;
import com.midea.cloud.srm.feign.ContractPjExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.cm.contract.constants.ContractMqlSchemaType;
import com.midea.cloud.srm.model.cm.perform.entity.PerAcceptance;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlan;
import com.midea.cloud.srm.model.cm.perform.entity.PerPlanMilestone;
import com.midea.cloud.srm.model.contract.constant.ContractHeadFieldName;
import com.midea.cloud.srm.model.contract.constant.PerEvalNoticeTemplateVar;
import com.midea.cloud.srm.model.contract.dto.PerPlanExt;
import com.midea.cloud.srm.model.contract.dto.PerPlanMilestoneExtDto;
import com.midea.cloud.srm.model.contract.enums.ContractSourceTypeEnums;
import com.midea.cloud.srm.model.contract.dto.PerAcceptanceExt;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.midea.cloud.srm.model.contract.constant.DingTalkTempConstant.PER_EVAL_NOTICE;

/**
 * @author 100014336 ganyh
 */
@Service
@Slf4j
public class PerAcceptanceFlowPjServiceImpl extends PerAcceptanceFlowServiceImpl implements IFlowBusinessCallbackService {

    @Value("${bpm.HTYS.processGroupId}")
    private String lcHtysProcessGroupId;
    @Value("${bpm.HTYS.processGroupId2}")
    private String lcHtysProcessGroupId2;

    @Value("${bpm.HTYS.appId}")
    private String lcHtysAppId;

    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;

    @Autowired
    private PerAcceptanceService acceptanceService;

    @Autowired
    private QlService qlService;
    @Autowired
    private IContractPerPlanExtService contractPerPlanExtService;

    @Autowired
    private ContractPjExtClient contractPjExtClient;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private ContractPjExtClient pjExtClient;
    @Resource
    private ContractBaseExtClient baseExtClient;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private PjProjectExtClient pjProjectExtClient;
    private static final String BUSINESS_TYPE = "performAcceptance";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                Record r = new Record();
                r.put(PerAcceptanceExt::getPerAcceptanceId,businessId);
                r.put(PerAcceptanceExt::getStartBpmUsername, loginAppUser.getUsername());
                r.put(PerAcceptanceExt::getStartBpmNickname, loginAppUser.getNickname());
                r.put(PerAcceptanceExt::getStatus, "SUBMITTED");
                qlService.update(ContractMqlSchemaType.PerAcceptance.getType(), Arrays.asList(r));
                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            Record r = new Record();
            r.put(PerAcceptanceExt::getPerAcceptanceId,businessId);
            r.put(PerAcceptanceExt::getStatus, "SUBMITTED");
            qlService.update(ContractMqlSchemaType.PerAcceptance.getType(), Arrays.asList(r));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passFlow(Long businessId, String param) throws Exception {
        super.passFlow(businessId, param);
        LambdaQueryWrapper<PerAcceptance> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(PerAcceptance::getPerAcceptanceId,businessId);
        PerAcceptance perAcceptance = acceptanceService.selectFirst(queryWrapper);
        /*
         * 验收完成后处理
         */
        if(ObjectUtil.isNotNull(perAcceptance)){
            /* 回写完成时间 */
            Long milestoneId = perAcceptance.getPerPlanMilestoneId();
            Record record = new Record();
            record.put(PerPlanMilestone::getPerPlanMilestoneId,milestoneId);
            //更新实际结束时间
            record.put(PerPlanMilestone::getActualCompleteDate,new Date(System.currentTimeMillis()));
            qlService.update(ContractMqlSchemaType.PerPlanMilestone.getType(),Collections.singletonList(record));
            /* 发送钉钉通知 */
            /*判断是否需要评分*/
            PerPlanMilestoneExtDto perPlanMilestoneExtDto = qlService.readByKey(ContractMqlSchemaType.PerPlanMilestone.getType(),milestoneId,PerPlanMilestoneExtDto.class);
            PerPlanExt perPlan = formateExtPerPanByMilestone(contractPerPlanExtService.getPerPanByMilestoneId(perPlanMilestoneExtDto));
            //            if(true){

            if(contractPerPlanExtService.isNeedPerfEval(perPlan,perPlanMilestoneExtDto)){
                List<Record> contractRecords = qlService.queryByWrapper(QlWrappers.query(ContractMqlSchemaType.ContractHead.getType()).select("extInviteHeadId","extContractHandlerId","extContractHandlerAccount","extContractHandlerName").eq(ContractHeadFieldName.CONTRACT_HEAD_ID_FIELD,perPlan.getContractHeadId()),Record.class);
                if(CollUtil.isNotEmpty(contractRecords)){
                    Record contract = contractRecords.get(0);
                    Long extInviteHeadId =  contract.getLong("extInviteHeadId");
                    Long extContractHandlerId =  contract.getLong("extContractHandlerId");
                    List<String> accounts  = new ArrayList<>();
                    if(ObjectUtil.isNotNull(extInviteHeadId)){
                        User inviteUser = rbacClient.getUserByIdAnon(extInviteHeadId);
                        if(ObjectUtil.isNotNull(inviteUser)){
                            accounts.add(inviteUser.getUsername());
                        }
                    }
                    if(ObjectUtil.isNotNull(extContractHandlerId)){
                        User handleUser = rbacClient.getUserByIdAnon(extContractHandlerId);
                        if(ObjectUtil.isNotNull(handleUser)){
                            accounts.add(handleUser.getUsername());
                        }
                    }
                   sendEvalNotice(perPlan,accounts);
                }
            }
        }
    }

    /**
     * 转换实体类
     * @param perPlan
     * @return
     */
    private PerPlanExt formateExtPerPanByMilestone(PerPlan perPlan) {
        if(ObjectUtils.anyNull(perPlan)) {
            return null;
        }
        PerPlanExt perPlanExt = new PerPlanExt();
        BeanCopyUtil.copyProperties(perPlanExt, perPlan);
        return perPlanExt;
    }

    private JSONObject sendEvalNotice(PerPlan perPlan,List<String> accounts){
        DingTalkSender dingTalkSender = DingTalkSender.create(baseClient,contractPjExtClient);
        Map<String, String> var = new HashMap<>(16);
        var.put(PerEvalNoticeTemplateVar.PER_PLAN_PER_PLAN_NO, perPlan.getPerPlanNo());
        var.put(PerEvalNoticeTemplateVar.PER_PLAN_CURRENT_MILESTONE_TYPE, getMilestoneTypeName(perPlan.getCurrentMilestoneType()));
        var.put(PerEvalNoticeTemplateVar.PER_PLAN_CONTRACT_NO, perPlan.getContractNo());
        var.put(PerEvalNoticeTemplateVar.PER_PLAN_CONTRACT_NAME, perPlan.getContractName());
        return dingTalkSender.sendDingTalk(accounts,PER_EVAL_NOTICE,var,null);
    }

    private String getMilestoneTypeName(String currentMilestoneType) {
        String mileName = "";
        List<DictItemDTO> itemDTOS = baseClient.listAllByDictCode("MILESTONE_SCHEDULE");
        for (DictItemDTO itemDTO:itemDTOS){
            if(itemDTO.getDictItemCode().equalsIgnoreCase(currentMilestoneType)){
                mileName = itemDTO.getDictItemName();
            }
        }
       return mileName;
    }

    /**
     * 封装 根据类别启动流程接口参数
     * @param businessId
     * @return
     */
    public String getDataPushFlow(Long businessId){
        Record acceptance = qlService.readByKey(ContractMqlSchemaType.PerAcceptance.getType(), businessId,Record.class);

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+acceptance.getString("perAcceptanceNo")+"-"+AppUserUtil.getLoginAppUser().getNickname());
        bpmParam.setProcessGroupId(lcHtysProcessGroupId2);
        bpmParam.setProcessVars(new JSONObject());
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null||StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }

        Record acceptance = qlService.readByKey(ContractMqlSchemaType.PerAcceptance.getType(), businessId,Record.class);
        if(ObjectUtil.isNotEmpty(acceptance)){
            ContractBpmCreator contractBPMCreator = new ContractBpmCreator(baseClient,qlService,fileDownloadPath,lcHtysAppId,lcHtysProcessGroupId,pjExtClient);
            JSONObject jsonObject = contractBPMCreator.createAcceptanceBPM(acceptance);
            log.info("===================进入合同验收组装数据结束{}",jsonObject);
            return JsonUtil.entityToJsonStr(jsonObject);
        }

        return super.getDataPushFlow(businessId, param);
    }

    private String getNoticeContent(PerPlan perPlan) {
        String planNo = perPlan.getPerPlanNo();
        String mType = perPlan.getCurrentMilestoneType();
        String contractNo = perPlan.getContractNo();
        String contractName = perPlan.getContractName();
        return String.format("你好,你的履约计划【%s】，里程碑【%s】已完成。合同编号【%s】，合同名称【%s】 请进行评分。",planNo,mType,contractNo,contractName);
    }
}
