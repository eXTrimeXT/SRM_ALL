package com.midea.cloud.srm.supauth.review.service.flow.pjflow;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.workflow.WorkflowThirdService;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmCreateResult;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmResultDTO;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.workflow.dto.FlowCallbackDTO;
import com.midea.cloud.srm.model.workflow.dto.FlowResponseDTO;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @date: 2023/7/31 20:54
 * @author 100014323
 */
@Slf4j
@Service("changchengWorkflowThirdService")
public class ChangchengWorkflowThirdService implements WorkflowThirdService {

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    BaseExtClient baseExtClient;

    @Autowired
    BaseClient baseClient;

    @Override
    public FlowCallbackDTO getDataPushFlow(FlowCallbackDTO flowCallbackDTO) throws Exception {
        IFlowBusinessCallbackService iFlowBusinessCallbackService;

        Class clazz = Class.forName(flowCallbackDTO.getServiceBean());
        Object bean = SpringContextHolder.getApplicationContext().getBean(clazz);
        iFlowBusinessCallbackService = (IFlowBusinessCallbackService) bean;

        String flowData = iFlowBusinessCallbackService.getDataPushFlow(flowCallbackDTO.getBusinessId(), flowCallbackDTO.getParam());
        flowCallbackDTO.setFlowParam(flowData);
        return flowCallbackDTO;
    }

    @Override
    public FlowResponseDTO startThird(FlowCallbackDTO flowCallbackDTO) throws Exception {
        log.info("--------------startThird----------------");
        log.info("flowCallbackDTO:"+JSONObject.toJSONString(flowCallbackDTO));

        // 查询流程配置是否启用，没启用的直接返回一个InstanceId
        String businessType = flowCallbackDTO.getBusinessType();
        Map<String, Object> mapParam =new HashMap<>(16);
        mapParam.put("businessType",businessType);
        Boolean isOpen = baseExtClient.getIsEnableFlow(JSONObject.toJSONString(mapParam));

        BpmResultDTO<BpmCreateResult> resultDTO ;
        FlowResponseDTO flowResponseDTO = new FlowResponseDTO();
        if(isOpen){
            DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",flowCallbackDTO.getBusinessType());
            if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())){
                BpmStartProcessParam bpmParam = JSONObject.parseObject(flowCallbackDTO.getFlowParam(),BpmStartProcessParam.class);
                bpmParam = bpmParam==null?new BpmStartProcessParam():bpmParam;
                resultDTO = pjProjectExtClient.startProcessByCategoty(flowCallbackDTO.getBusinessId(),flowCallbackDTO.getBusinessType(),bpmParam);
            }else{
                resultDTO = pjProjectExtClient.createProcessByCategory(JSONObject.parseObject(flowCallbackDTO.getFlowParam())
                        ,flowCallbackDTO.getBusinessId().toString(),flowCallbackDTO.getBusinessType());
            }
            log.info("返回的信息===" + JSONObject.toJSONString(resultDTO));
            flowResponseDTO.setInstanceId(resultDTO.getData()==null?null:resultDTO.getData().getProcessInstId());
        }else{
            flowResponseDTO.setInstanceId(flowCallbackDTO.getBusinessId().toString());
        }
        flowResponseDTO.setBusinessId(flowCallbackDTO.getBusinessId());
        flowResponseDTO.setFlowParam(flowCallbackDTO.getFlowParam());
        flowResponseDTO.setParam(null);
        flowResponseDTO.setDealStatus(null);
        return flowResponseDTO;
    }
}
