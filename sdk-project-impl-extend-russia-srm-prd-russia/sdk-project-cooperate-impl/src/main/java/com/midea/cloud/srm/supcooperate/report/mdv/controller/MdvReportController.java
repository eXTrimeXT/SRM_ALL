package com.midea.cloud.srm.supcooperate.report.mdv.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.supcooperate.report.mdv.dto.*;
import com.midea.cloud.srm.supcooperate.report.mdv.service.MdvReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2024/4/9 17:45
 *  修改内容:
 * </pre>
 */
@Api("mdv系统大屏报表类查询")
@RestController
@RequestMapping("/mdv")
public class MdvReportController {

    @Autowired
    private MdvReportService mdvReportService;

    @ApiOperation(value = "供应商数量", notes = "供应商数量", httpMethod = "POST")
    @PostMapping("/vendorQuantityListPage")
    PageInfo<VendorQuantityDTO> vendorQuantityListPage(@RequestBody VendorQuantityDTO params) {
        return mdvReportService.vendorQuantityListPage(params);
    }

    @ApiOperation(value = "已完结项目数量", notes = "已完结项目数量", httpMethod = "POST")
    @PostMapping("/approvedProjectListPage")
    PageInfo<ApprovedProjectDTO> approvedProjectListPage(@RequestBody ApprovedProjectDTO params) {
        return mdvReportService.approvedProjectListPage(params);
    }

    @ApiOperation(value = "黑名单供应商数量", notes = "黑名单供应商数量", httpMethod = "POST")
    @PostMapping("/blackSupplierPage")
    PageInfo<BlackSupplierQuantityDTO> blackSupplierPage(@RequestBody BlackSupplierQuantityDTO params) {
        return mdvReportService.blackSupplierPage(params);
    }

    @ApiOperation(value = "受限供应商数量", notes = "受限供应商数量", httpMethod = "POST")
    @PostMapping("/controlSupplierPage")
    PageInfo<ControlSupplierQuantityDTO> controlSupplierPage(@RequestBody ControlSupplierQuantityDTO params) {
        return mdvReportService.controlSupplierPage(params);
    }

    @ApiOperation(value = "预算与定标金额", notes = "预算与定标金额", httpMethod = "POST")
    @PostMapping("/budgetAndWinAmountPage")
    PageInfo<BudgetAndWinAmountDTO> budgetAndWinAmountPage(@RequestBody BudgetAndWinAmountDTO params) {
        return mdvReportService.budgetAndWinAmountPage(params);
    }

    @ApiOperation(value = "特殊招标项目数量分布", notes = "特殊招标项目数量分布", httpMethod = "POST")
    @PostMapping("/specialSouPage")
    PageInfo<SpecialSouDTO> specialSouPage(@RequestBody SpecialSouDTO params) {
        return mdvReportService.specialSouPage(params);
    }

    @ApiOperation(value = "供应商活跃状态", notes = "供应商活跃状态", httpMethod = "POST")
    @PostMapping("/supplierActiveStatusPage")
    PageInfo<SupplierActiveStatusDTO> supplierActiveStatusPage(@RequestBody SupplierActiveStatusDTO params) {
        return mdvReportService.supplierActiveStatusPage(params);
    }

    @ApiOperation(value = "供应商履约评价", notes = "供应商履约评价", httpMethod = "POST")
    @PostMapping("/supplierPerEvalPage")
    PageInfo<SupplierPerEvalDTO> supplierPerEvalPage(@RequestBody SupplierPerEvalDTO params) {
        return mdvReportService.supplierPerEvalPage(params);
    }

    @ApiOperation(value = "专家活力值", notes = "专家活力值", httpMethod = "POST")
    @PostMapping("/expertVitValuePage")
    PageInfo<ExpertVitValueDTO> expertVitValuePage(@RequestBody ExpertVitValueDTO params) {
        return mdvReportService.expertVitValuePage(params);
    }

    @ApiOperation(value = "评标专家热度榜", notes = "评标专家热度榜", httpMethod = "POST")
    @PostMapping("/expertHotListPage")
    PageInfo<ExpertHotListDTO> expertHotListPage(@RequestBody ExpertHotListDTO params) {
        return mdvReportService.expertHotListPage(params);
    }

    @ApiOperation(value = "招标计划实施率", notes = "招标计划实施率", httpMethod = "POST")
    @PostMapping("/bidPlanImplRatePage")
    PageInfo<BidPlanImplRateDTO> bidPlanImplRatePage(@RequestBody BidPlanImplRateDTO params) {
        return mdvReportService.bidPlanImplRatePage(params);
    }

    @ApiOperation(value = "扣除质保金", notes = "扣除质保金", httpMethod = "POST")
    @PostMapping("/deductionDepositPage")
    PageInfo<DeductionDepositDTO> deductionDepositPage(@RequestBody DeductionDepositDTO params) {
        return mdvReportService.deductionDepositPage(params);
    }
}
