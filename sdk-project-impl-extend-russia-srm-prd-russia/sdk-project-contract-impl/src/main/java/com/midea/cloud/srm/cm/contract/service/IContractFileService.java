package com.midea.cloud.srm.cm.contract.service;

import com.midea.cloud.srm.model.cm.contract.entity.ContractHead;
import com.midea.cloud.srm.model.contract.dto.ContractHeadExt;

/**
 * @author 100014336 ganyh19
 */
public interface IContractFileService {

    /**
     * 通过html生成pdf
     * @param contractHeadExt
     */
    void makeHtmlFormalPdf(ContractHeadExt contractHeadExt);


}
