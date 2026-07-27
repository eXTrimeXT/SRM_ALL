package com.midea.cloud.srm.cm.contract.service;

/**
 * @author 100014336 ganyh19
 */
public interface IOperationLogService {

    /**
     * 添加操作日志
     * @param operationType
     * @param contractHeadId
     * @param operationDesc
     */
    void addByType(String operationType,Long contractHeadId,String operationDesc);

    /**
     * 添加操作日志
     * @param operationType
     * @param contractHeadId
     */
    void addByType(String operationType,Long contractHeadId);
}
