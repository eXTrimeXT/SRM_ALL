package com.midea.cloud.srm.cm.contract.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.enums.contract.OperationTypeProcessEnum;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.cm.contract.service.IOperationLogService;
import com.midea.cloud.srm.model.contract.dto.ContractOperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author 100014336 ganyh19
 */
@Service
public class OperationLogServiceImpl implements IOperationLogService {


    @Autowired
    private QlService qlService;

    @Override
    public void addByType(String operationType,Long contractHeadId,String operationDesc) {
        if(ObjectUtil.isNotEmpty(operationType)){
            ContractOperationLog contractOperationLog = new ContractOperationLog();
            contractOperationLog.setContractHeadId(contractHeadId);
            contractOperationLog.setOperationType(operationType);
            contractOperationLog.setOperationDesc(operationDesc);
            qlService.create("OperationLog", Arrays.asList(contractOperationLog));
        }
    }

    @Override
    public void addByType(String operationType, Long contractHeadId) {
        addByType(operationType,contractHeadId,null);
    }
}
