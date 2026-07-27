package com.midea.cloud.srm.biz.pj.changchengapi.contract;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.model.pj.contract.dto.CreateContractReturnDTO;
import io.swagger.annotations.ApiOperation;

/**
 * @author huangbf3
 */
public interface ContractService {

    /**
     * 备注
     * @param requestJsn
     * @return
     */
    @ApiOperation("长城开放平台_创建已签署合同接口")
    CreateContractReturnDTO createContract(JSONObject requestJsn);
}
