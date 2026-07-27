package com.midea.cloud.srm.sou.agreement.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.sou.agreement.entity.SccSouJcAgreement;
import com.midea.cloud.srm.model.sou.agreement.enums.AgreementStatusEnums;
import com.midea.cloud.srm.sou.agreement.service.JcAgreementChangeService;
import com.midea.cloud.srm.sou.agreement.service.JcAgreementContractService;
import com.midea.cloud.srm.sou.agreement.service.JcAgreementInfoService;
import com.midea.cloud.srm.sou.agreement.service.TieredPricingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author 100014336 ganyh19
 */
@Api(value = "JcContractController", tags = {"集采合同协议管理"})
@RestController
@Slf4j
@RequestMapping("/jcAgreement/contract")
public class JcContractController {

    @Autowired
    private JcAgreementContractService jcAgreementContractService;

    @ApiOperation(value = "添加或更新集采管理", notes = "添加或更新集采管理", httpMethod = "POST")
    @PostMapping("/saveOrUpdateJcAgreement")
    public SccSouJcAgreement saveOrUpdateJcAgreement(@RequestBody SccSouJcAgreement sccSouJcAgreement) {
        if (!AgreementStatusEnums.DRAFT.getCode().equals(sccSouJcAgreement.getAgreementStatus())) {
            throw new BaseException("必须提交拟定状态");
        }
        jcAgreementContractService.save(sccSouJcAgreement);
        return sccSouJcAgreement;
    }

}
