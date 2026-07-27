package com.midea.cloud.srm.supcooperate.report.mdv.service;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.supcooperate.report.mdv.dto.*;

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
 *  修改日期: 2024/4/9 17:49
 *  修改内容:
 * </pre>
 */
public interface MdvReportService {
    /**
     * 供应商数量分页查询
     * @param params
     * @return
     */
    PageInfo<VendorQuantityDTO> vendorQuantityListPage(VendorQuantityDTO params);

    /**
     * 已完结项目数量
     *
     * @param params
     * @return
     */
    PageInfo<ApprovedProjectDTO> approvedProjectListPage(ApprovedProjectDTO params);

    /**
     * 黑名单供应商数量
     * @param params
     * @return
     */

    PageInfo<BlackSupplierQuantityDTO> blackSupplierPage(BlackSupplierQuantityDTO params);

    /**
     * 受限供应商数量
     * @param params
     * @return
     */

    PageInfo<ControlSupplierQuantityDTO> controlSupplierPage(ControlSupplierQuantityDTO params);

    /**
     * 预算与定标金额
     * @param params
     * @return
     */

    PageInfo<BudgetAndWinAmountDTO> budgetAndWinAmountPage(BudgetAndWinAmountDTO params);

    /**
     * 特殊招标项目数量分布
     * @param params
     * @return
     */

    PageInfo<SpecialSouDTO> specialSouPage(SpecialSouDTO params);

    /**
     * 供应商活跃状态
     * @param params
     * @return
     */
    PageInfo<SupplierActiveStatusDTO> supplierActiveStatusPage(SupplierActiveStatusDTO params);

    /**
     * 供应商履约评价
     * @param params
     * @return
     */
    PageInfo<SupplierPerEvalDTO> supplierPerEvalPage(SupplierPerEvalDTO params);

    /**
     * 专家活力值
     * @param params
     * @return
     */
    PageInfo<ExpertVitValueDTO> expertVitValuePage(ExpertVitValueDTO params);

    /**
     * 评标专家热度榜
     *
     * @param params
     * @return
     */
    PageInfo<ExpertHotListDTO> expertHotListPage(ExpertHotListDTO params);

    /**
     * 招标计划实施率
     * @param params
     * @return
     */
    PageInfo<BidPlanImplRateDTO> bidPlanImplRatePage(BidPlanImplRateDTO params);

    /**
     * 扣除质保金
     *
     * @param params
     * @return
     */
    PageInfo<DeductionDepositDTO> deductionDepositPage(DeductionDepositDTO params);
}
