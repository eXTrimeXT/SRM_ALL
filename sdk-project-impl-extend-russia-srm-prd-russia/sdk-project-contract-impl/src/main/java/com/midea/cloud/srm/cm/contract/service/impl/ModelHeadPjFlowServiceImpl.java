package com.midea.cloud.srm.cm.contract.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.enums.contract.OperationTypeProcessEnum;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.model.cm.model.entity.ModelHead;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author 100014336 ganyh19
 */
@Slf4j
@Service
public class ModelHeadPjFlowServiceImpl   implements IFlowBusinessCallbackService {

    @Autowired
    private QlService qlService;

    private static final String MEI_Q_TYPE = "ModelHead";

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
       log.info("submitFlow: {}, {}", businessId, param);
       checkModel(businessId,param);
       updateStatus(businessId,OperationTypeProcessEnum.SUBMIT.name());

    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        log.info("passFlow: {}, {}", businessId, param);
        checkModel(businessId,param);
        updateStatus(businessId,OperationTypeProcessEnum.PASS.name());
    }
    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        log.info("rejectFlow: {}, {}", businessId, param);
        checkModel(businessId,param);
        updateStatus(businessId,OperationTypeProcessEnum.REJECT.name());
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        log.info("withdrawFlow: {},{}",businessId,param);
        checkModel(businessId,param);
        updateStatus(businessId,OperationTypeProcessEnum.WITHDRAW.name());
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        log.info("destoryFlow: {}.{}",businessId,param);
        checkModel(businessId,param);
        updateStatus(businessId,"DESTORY");
    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        log.info("getVariableFlow: {}, {}", businessId, param);
        return null;
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("getDataPushFlow: {}, {}", businessId, param);
        return null;
    }

    private void checkModel(Long businessId,String param){
        Record record = qlService.readByKey(MEI_Q_TYPE,businessId,Record.class);
        if(ObjectUtil.isEmpty(record)) {
            throw new BaseException("模板不存在");
        }
    }

    private void updateStatus(Long businessId,String status){
        ModelHead modelHead = new ModelHead().setModelHeadId(businessId).setStatus(status);
        qlService.update(MEI_Q_TYPE, Arrays.asList(modelHead));
    }
}
