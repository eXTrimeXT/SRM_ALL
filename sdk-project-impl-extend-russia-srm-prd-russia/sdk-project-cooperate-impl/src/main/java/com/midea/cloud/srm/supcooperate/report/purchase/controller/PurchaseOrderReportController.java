package com.midea.cloud.srm.supcooperate.report.purchase.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.supcooperate.report.purchase.dto.PurchaseOrderProcessDto;
import com.midea.cloud.srm.supcooperate.report.purchase.service.PurchaseOrderReportService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
@Api("采购进度报表控制类")
@RestController
@RequestMapping("/purchaseOrder")
public class PurchaseOrderReportController extends BaseController {

    @Autowired
    private PurchaseOrderReportService purchaseOrderReportService;

    @PostMapping("/process/listPage")
    PageInfo<PurchaseOrderProcessDto> purchaseOrderProcessListPage(@RequestBody Map<String, Object> query) {
        try {
            return purchaseOrderReportService.listPage(query);
        } catch (Exception e) {
            log.error("purchaseOrderProcesslistPage Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
