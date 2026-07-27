package com.midea.cloud.srm.sou.report.inq.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.sou.report.inq.dto.ExtInquiryPriceRateDto;
import com.midea.cloud.srm.sou.report.inq.service.ExtReportInqPriceRateService;
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
@Api("询价管理报表")
@Slf4j
@RestController
@RequestMapping("/extReportInq")
public class ExtReportInqController extends BaseController {

    @Autowired
    private ExtReportInqPriceRateService extReportInqPriceRateService;

    @PostMapping("/priceRate/listPage")
    public PageInfo<ExtInquiryPriceRateDto> priceRateListPage(@RequestBody Map<String, Object> query) {
        try {
            return extReportInqPriceRateService.listPage(query);
        } catch (Exception e) {
            log.error("priceRateListPage Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
