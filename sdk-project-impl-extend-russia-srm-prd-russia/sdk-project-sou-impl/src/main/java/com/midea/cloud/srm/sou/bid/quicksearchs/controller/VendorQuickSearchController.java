package com.midea.cloud.srm.sou.bid.quicksearchs.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.sou.bid.quicksearchs.service.VendorQuickSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: panmq
 * @Date: 2024/04/03/ $
 * @Description:
 */

@RestController
@Slf4j
@RequestMapping("/bid/vendorquickSearch")
public class VendorQuickSearchController extends BaseController {

    @Autowired
    private VendorQuickSearchService vendorQuickSearchService;

    /**
     * 快速查询供应商-澄清
     * @param params
     * @return
     */
    @PostMapping("/vendorQuickSearchForAnswer")
    PageInfo<ExtSouVendor> vendorQuickSearchForAnswer(@RequestBody Map<String, Object> params) {
        try {
            return vendorQuickSearchService.vendorQuickSearchForAnswer(params);
        } catch (Exception e) {
            log.error("vendorQuickSearchForAnswer Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
