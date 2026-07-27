package com.midea.cloud.srm.sou.deposit.service;

import com.midea.cloud.srm.model.sou.deposit.entity.FinanceCompany;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMargin;

/**
 * @author ex_liuxy46
 */
public interface FinanceService {

    /**
     * 意向金缴款
     * @param reqHeadId 寻源id
     * @param payAccountName 意向金/保证金付款方名称，付款账号名称(付款方名称)
     * @param vendorId 供应商id
     */
    void dealIntentionalDepositPayment(Long reqHeadId, String payAccountName, Long vendorId);

    /**
     * 保证金缴款
     * @param extSouMargin 保证金
     * @param payAccountName 付款方名称
     * @param vendorId 供应商id
     */
    void dealEarnestMoneyDepositPayment(ExtSouMargin extSouMargin, String payAccountName, Long vendorId);

    boolean existsFinanceUseRecord(String souNo, String companyCode);

    /**
     * 查询财务信息
     * @param account
     * @return
     */
    FinanceCompany queryFinanceCompany(String account);
}
