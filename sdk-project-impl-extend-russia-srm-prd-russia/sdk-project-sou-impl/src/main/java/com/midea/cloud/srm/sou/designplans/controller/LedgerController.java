package com.midea.cloud.srm.sou.designplans.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChLedger;
import com.midea.cloud.srm.sou.designplans.service.LedgerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author ex_liuxy46
 */
@Api(value = "LedgerController", tags = {"集采台账"})
@Slf4j
@RestController
@RequestMapping("/design/plan/ledger")
public class LedgerController {

    @Resource
    private LedgerService ledgerService;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @ApiOperation(value = "获取集采台账列表", notes = "获取集采台账列表")
    @PostMapping("/getChLedgerPageList")
    public PageInfo<SccSouChLedger> getChLedgerPageList(@RequestBody SccSouChLedger ledger) {
        PageUtil.startPage(ledger.getPageNum(), ledger.getPageSize());
        List<SccSouChLedger> list = ledgerService.getLedgerList(ledger);
        return new PageInfo<>(list);
    }

    public void checkThrow(Boolean boo, String str) {
        if (boo) {
            throw new BaseException(str);
        }
    }

    @ApiOperation(value = "集采台账新增或更新", notes = "集采台账新增或更新", httpMethod = "POST")
    @PostMapping("/saveOrUpdateChLedgerInfo")
    public SccSouChLedger saveOrUpdateChLedgerInfo(@RequestBody SccSouChLedger ledger) {
        checkThrow(ledger == null,"查询参数为空");
        checkThrow(StringUtils.isBlank(ledger.getProjectName()),"项目名称不能为空");
        checkThrow(ledger.getContractStartDate() == null,"合同日期开始时间不能为空");
        checkThrow(ledger.getContractEndDate() == null,"合同日期结束时间不能为空");
        checkThrow(StringUtils.isBlank(ledger.getHeadPerson()),"负责人不能为空");
        checkThrow(ledger.getProjectTotalMoney() == null,"项目总金额不能为空");
        checkThrow(ledger.getAddNum() == null,"本次新增项目数不能为空");
        checkThrow(ledger.getAddBeforeMoney() == null,"原临采年采购额不能为空");
        checkThrow(ledger.getAddAfterMoney() == null,"集采后年采购额不能为空");
        checkThrow(ledger.getAboBeforeMoney() == null,"上期/集采前采购额不能为空");
        checkThrow(ledger.getAboAfterMoney() == null,"集采后年采购额不能为空");
        setHrCompanyInfo(ledger);
        //本次递减金额 = 集采后年采购额 - 原临采年采购额
        //成本递减比例 = 本次递减金额 ÷ 原临采年采购额
        //新增集采物资-本次递减金额(万元)
        /*BigDecimal addDecrementMoney = ledger.getAddAfterMoney().subtract(ledger.getAddBeforeMoney());
        ledger.setAddDecrementMoney(addDecrementMoney);
        //新增集采物资-成本递减比例(%)
        ledger.setAddDecrementRatio(addDecrementMoney.divide(ledger.getAddBeforeMoney(), 4, RoundingMode.HALF_UP));
        //原集采物资-本次递减金额(万元)
        BigDecimal aboDecrementMoney = ledger.getAboAfterMoney().subtract(ledger.getAboBeforeMoney());
        ledger.setAboDecrementMoney(aboDecrementMoney);
        //原集采物资-成本递减比例(%)
        ledger.setAboDecrementRatio(aboDecrementMoney.divide(ledger.getAboBeforeMoney(), 4, RoundingMode.HALF_UP));*/
        ledgerService.saveOrUpdate(ledger);
        return ledger;
    }

    private void setHrCompanyInfo(SccSouChLedger ledger) {
        if(ObjectUtil.isEmpty(ledger.getLedgerId())){
            LoginAppUser appUser = AppUserUtil.getLoginAppUser();
            HrUserOrgnizationDto userOrganization = pjProjectExtClient.getHrUserOrgnizationByUsername(appUser.getUsername());
            //公司
            Organization ouOrganization = userOrganization.getOuOrganization();
            //板块
            Organization buOrganization = userOrganization.getBuOrganization();
            //部门
            Organization departmentOrganization = userOrganization.getDepartmentOrganization();
            //设置公司
            if(ObjectUtil.isNotNull(ouOrganization)){
                ledger.setHrCompanyId(ouOrganization.getOrganizationId());
                ledger.setHrCompanyCode(ouOrganization.getOrganizationCode());
                ledger.setHrCompanyName(ouOrganization.getOrganizationName());
            }
            //设置板块
            if(ObjectUtil.isNotNull(buOrganization)){
                ledger.setHrSectorId(buOrganization.getOrganizationId());
                ledger.setHrSectorName(buOrganization.getOrganizationName());
                ledger.setHrSectorCode(buOrganization.getOrganizationCode());
            }
            //设置部门
            if(ObjectUtil.isNotNull(departmentOrganization)){
                ledger.setHrDeptId(departmentOrganization.getOrganizationId());
                ledger.setHrDeptCode(departmentOrganization.getOrganizationName());
                ledger.setHrDeptName(departmentOrganization.getOrganizationCode());
            }
        }
    }

    @ApiOperation(value = "集采台账获取详情", notes = "集采台账获取详情")
    @GetMapping("/getChLedgerInfo")
    public SccSouChLedger getChLedgerInfo(@RequestParam("ledgerId") Long ledgerId) {
        checkThrow(ledgerId == null,"台账id不能为空");
        return ledgerService.getById(ledgerId);
    }
}
