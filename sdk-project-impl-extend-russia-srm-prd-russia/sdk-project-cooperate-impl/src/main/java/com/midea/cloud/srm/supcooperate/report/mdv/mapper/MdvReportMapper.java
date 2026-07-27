package com.midea.cloud.srm.supcooperate.report.mdv.mapper;

import com.midea.cloud.srm.model.supcooperate.report.mdv.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Mapper
public interface MdvReportMapper {

    /**
     * queryVendorQuantityDAO
     * @param params
     * @return
     */
    List<VendorQuantityDTO> queryVendorQuantityDAO(VendorQuantityDTO params);

    /**
     * queryApprovedProjectDAO
     * @param params
     * @return
     */
    List<ApprovedProjectDTO> queryApprovedProjectDAO(ApprovedProjectDTO params);

    /**
     * queryBlackSupplierPageDAO
     * @param params
     * @return
     */
    List<BlackSupplierQuantityDTO> queryBlackSupplierPageDAO(BlackSupplierQuantityDTO params);

    /**
     * queryControlSupplierPageDAO
     * @param params
     * @return
     */
    List<ControlSupplierQuantityDTO> queryControlSupplierPageDAO(ControlSupplierQuantityDTO params);

    /**
     * queryBudgetAndWinAmountDAO
     * @param params
     * @return
     */
    List<BudgetAndWinAmountDTO> queryBudgetAndWinAmountDAO(BudgetAndWinAmountDTO params);

    /**
     * querySpecialSouDAO
     * @param params
     * @return
     */
    List<SpecialSouDTO> querySpecialSouDAO(SpecialSouDTO params);

    /**
     * querySupplierActiveStatusDAO
     * @param params
     * @return
     */
    List<SupplierActiveStatusDTO> querySupplierActiveStatusDAO(SupplierActiveStatusDTO params);

    /**
     * querySupplierPerEvalDAO
     * @param params
     * @return
     */
    List<SupplierPerEvalDTO> querySupplierPerEvalDAO(SupplierPerEvalDTO params);

    /**
     * queryExpertVitValueDAO
     * @param params
     * @return
     */
    List<ExpertVitValueDTO> queryExpertVitValueDAO(ExpertVitValueDTO params);

    /**
     * queryExpertHotListDAO
     * @param params
     * @return
     */
    List<ExpertHotListDTO> queryExpertHotListDAO(ExpertHotListDTO params);

    /**
     * queryBidPlanImplRateDAO
     * @param params
     * @return
     */
    List<BidPlanImplRateDTO> queryBidPlanImplRateDAO(BidPlanImplRateDTO params);

    /**
     * queryDeductionDepositDAO
     * @param params
     * @return
     */
    List<DeductionDepositDTO> queryDeductionDepositDAO(DeductionDepositDTO params);
}
