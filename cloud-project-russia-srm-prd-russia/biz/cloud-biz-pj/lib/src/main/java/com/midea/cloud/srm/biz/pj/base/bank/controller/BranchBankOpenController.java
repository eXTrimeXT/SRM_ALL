package com.midea.cloud.srm.biz.pj.base.bank.controller;

import com.midea.cloud.srm.biz.pj.base.bank.service.BranchBankOpenApiService;
import com.midea.cloud.srm.model.base.organization.entity.ErpBranchBank;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 1
 */
@RestController
@RequestMapping({"/branch-Bank"})
public class BranchBankOpenController {

    @Autowired
    BranchBankOpenApiService branchBankOpenApiService;

    @ApiOperation(value = "同步银行支行信息")
    @PostMapping("/bankSync")
    public List<ErpBranchBank> bankSync(){
        return branchBankOpenApiService.findBankInfoFromOpen();
    }

}
