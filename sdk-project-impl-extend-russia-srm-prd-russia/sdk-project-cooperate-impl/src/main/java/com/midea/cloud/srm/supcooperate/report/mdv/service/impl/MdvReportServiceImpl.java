package com.midea.cloud.srm.supcooperate.report.mdv.service.impl;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.supcooperate.report.mdv.dto.*;
import com.midea.cloud.srm.supcooperate.report.mdv.mapper.MdvReportMapper;
import com.midea.cloud.srm.supcooperate.report.mdv.service.MdvReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
 *  修改日期: 2024/4/9 17:53
 *  修改内容:
 * </pre>
 */
@Slf4j
@Service
public class MdvReportServiceImpl implements MdvReportService {

    @Autowired
    private MdvReportMapper mdvReportMapper;
    @Override
    public PageInfo<VendorQuantityDTO> vendorQuantityListPage(VendorQuantityDTO params) {
        PageUtil.startPage(params.getPageNum(),params.getPageSize());
        List<VendorQuantityDTO> list=mdvReportMapper.queryVendorQuantityDAO(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<ApprovedProjectDTO> approvedProjectListPage(ApprovedProjectDTO params) {
        PageUtil.startPage(params.getPageNum(),params.getPageSize());
        List<ApprovedProjectDTO> list=mdvReportMapper.queryApprovedProjectDAO(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<BlackSupplierQuantityDTO> blackSupplierPage(BlackSupplierQuantityDTO params) {
        PageUtil.startPage(params.getPageNum(),params.getPageSize());
        List<BlackSupplierQuantityDTO> list=mdvReportMapper.queryBlackSupplierPageDAO(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<ControlSupplierQuantityDTO> controlSupplierPage(ControlSupplierQuantityDTO params) {
        PageUtil.startPage(params.getPageNum(),params.getPageSize());
        List<ControlSupplierQuantityDTO> list=mdvReportMapper.queryControlSupplierPageDAO(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<BudgetAndWinAmountDTO> budgetAndWinAmountPage(BudgetAndWinAmountDTO params) {
        PageUtil.startPage(params.getPageNum(),params.getPageSize());
        List<BudgetAndWinAmountDTO> list=mdvReportMapper.queryBudgetAndWinAmountDAO(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<SpecialSouDTO> specialSouPage(SpecialSouDTO params) {
        PageUtil.startPage(params.getPageNum(),params.getPageSize());
        List<SpecialSouDTO> list=mdvReportMapper.querySpecialSouDAO(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<SupplierActiveStatusDTO> supplierActiveStatusPage(SupplierActiveStatusDTO params) {
        PageUtil.startPage(params.getPageNum(),params.getPageSize());
        List<SupplierActiveStatusDTO> list=mdvReportMapper.querySupplierActiveStatusDAO(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<SupplierPerEvalDTO> supplierPerEvalPage(SupplierPerEvalDTO params) {
        PageUtil.startPage(params.getPageNum(),params.getPageSize());
        List<SupplierPerEvalDTO> list=mdvReportMapper.querySupplierPerEvalDAO(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<ExpertVitValueDTO> expertVitValuePage(ExpertVitValueDTO params) {
        PageUtil.startPage(params.getPageNum(),params.getPageSize());
        List<ExpertVitValueDTO> list=mdvReportMapper.queryExpertVitValueDAO(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<ExpertHotListDTO> expertHotListPage(ExpertHotListDTO params) {
        PageUtil.startPage(params.getPageNum(),params.getPageSize());
        List<ExpertHotListDTO> list=mdvReportMapper.queryExpertHotListDAO(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<BidPlanImplRateDTO> bidPlanImplRatePage(BidPlanImplRateDTO params) {
        PageUtil.startPage(params.getPageNum(),params.getPageSize());
        List<BidPlanImplRateDTO> list=mdvReportMapper.queryBidPlanImplRateDAO(params);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<DeductionDepositDTO> deductionDepositPage(DeductionDepositDTO params) {
        PageUtil.startPage(params.getPageNum(), params.getPageSize());
        List<DeductionDepositDTO> list = mdvReportMapper.queryDeductionDepositDAO(params);
        return new PageInfo<>(list);
    }
}
