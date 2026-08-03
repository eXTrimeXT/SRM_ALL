package com.midea.cloud.srm.biz.pj.changchengapi.contract.controller;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.biz.pj.changchengapi.contract.ContractService;
import com.midea.cloud.srm.model.pj.contract.dto.CreateContractReturnDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author huangbf3
 */
@RestController
@RequestMapping("/external/contract")
public class ContractController {

    @Autowired
    private ContractService contractService;


    /**
     * 调长城开放平台创建已签署合同接口
     * **/

    @ApiOperation(value = "长城开放平台_创建已签署合同接口")
    @PostMapping("/createContract")
    public CreateContractReturnDTO createContract(@RequestBody JSONObject requestJsn)  {
        return   contractService.createContract(requestJsn);
    }
}
