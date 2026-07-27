package com.midea.cloud.srm.cm.contract.mapper;

import com.midea.cloud.srm.model.contract.dto.ContractHeadSourceDto;
import com.midea.cloud.srm.model.contract.vo.ContractHeadVo;
import com.midea.cloud.srm.model.contract.vo.ContractPartnerVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/6/18
 */
public interface ExtPartnerMapper {
    /**
     * 合作伙伴对应的盖章状态改为已签署
     * @param contractId
     * @param tenantName
     */
    public void updateStampState(String contractId, String tenantName);
    /**
     * 根据 contractId 从scc_contract_head查出合同头表ID
     */
    public String getByContractId(String contractId);
    /**
     * 根据合同id查询伙伴信息
     */
    List<ContractPartnerVo>getById(Long id);

    ContractHeadVo selectById(Long contractId);

    /**
     * 合作伙伴更新签署状态为未签署
     */
    public void updateUnStampState(String partnerId);

    /**
     * 定时任务合同有效期前钉钉提醒查询
     * @param month 三月 二月 一月
     * @param extCycle 是否周期合同
     * @param contractStatus 合同状态
     * @return 结果
     */
    List<ContractHeadSourceDto> queryContractList(@Param("month") String month, @Param("extCycle") String extCycle, @Param("contractStatus") String contractStatus);
}
