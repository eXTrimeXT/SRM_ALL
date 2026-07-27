package com.midea.cloud.srm.supcooperate.report.purchase.mapper;

import com.midea.cloud.srm.model.supcooperate.report.purchase.dto.PurchaseOrderProcessDto;

import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
public interface PurchaseOrderReportMapper {

    /**
     * listPurchaseOrderProcess
     * @param param
     * @return
     */
    List<PurchaseOrderProcessDto> listPurchaseOrderProcess(Map<String, Object> param);
}
