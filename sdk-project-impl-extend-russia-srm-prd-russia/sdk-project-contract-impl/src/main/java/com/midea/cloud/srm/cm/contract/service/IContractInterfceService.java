package com.midea.cloud.srm.cm.contract.service;

import com.midea.cloud.srm.model.contract.vo.ContractHeadVo;
import com.midea.cloud.srm.model.contract.vo.ContractPartnerVo;

import java.util.List;

/**
 * @author 100014336
 */
public interface IContractInterfceService {
    /**
     * 根据合同id查询相关信息，用来发钉钉通知
     */
    ContractHeadVo selectById(Long contractId);

    /**
     * 根据合同id查询伙伴合同状态
     */
    List<ContractPartnerVo>getById(Long id);

    /**
     * 更新合同为已签署
     */
     void updateStampState(String contractId,String tenantName) ;

    /**
     * 合同归档
     * @param contractHeadId
     * @return
     */
    Long contractFiling(Long contractHeadId);

    /**
     * 根据合同id获取电子签章平台url
     *
     * @param contractHeadId
     * @param extStampSignSeq
     * @return
     */
    String getUrlById(Long contractHeadId, String extStampSignSeq);


    /**
     * 电子签章确认,合同可签署状态
     *
     * @param contractHeadId
     * @param extStampSignSeq
     */
    void confirm(Long contractHeadId, String extStampSignSeq);

}
