package com.midea.cloud.srm.sou.deposit.controller;

import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;
import com.midea.cloud.srm.sou.deposit.service.FinanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ex_liuxy46
 */
@Api(value = "FinanceController", tags = {"财务接口"})
@RestController
@Slf4j
@RequestMapping("/external/finance")
public class FinanceController {

   @Resource
   private FinanceService financeService;

    /**
     * 意向金缴款
     * @param reqHeadId 寻源id
     * @param payAccountName 意向金/保证金付款方名称，付款账号名称(付款方名称)
     * @param vendorId 供应商id
     */
    @ApiOperation(value = "意向金缴款接口", notes = "意向金缴款接口", httpMethod = "POST")
    @PostMapping("/dealIntentionalDepositPayment")
    @Transactional(rollbackFor = Exception.class)
    public void dealIntentionalDepositPayment(Long reqHeadId, String payAccountName, Long vendorId) {
        financeService.dealIntentionalDepositPayment(reqHeadId, payAccountName, vendorId);
    }

    /**
     * 保证金缴款
     * @param extSouMargin 寻源id
     */
    @ApiOperation(value = "保证金缴款接口", notes = "保证金缴款接口", httpMethod = "POST")
    @PostMapping("/dealEarnestMoneyDepositPayment")
    @Transactional(rollbackFor = Exception.class)
    public void dealEarnestMoneyDepositPayment(ExtSouMargin extSouMargin, String payAccountName, Long vendorId) {
        financeService.dealEarnestMoneyDepositPayment(extSouMargin, payAccountName, vendorId);
    }

}