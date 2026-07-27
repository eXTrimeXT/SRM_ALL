package com.midea.cloud.srm.biz.pj.sou.quotetemplate.controller;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempService;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempDataDetailVO;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempDataVO;
import com.midea.cloud.srm.model.common.enums.UserType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 寻源 - 模型报价模板
 *
 * @author zhangwk12@midea.com
 * @since 2022/08/02
 */
@RestController
@RequestMapping("/vendor/quote-temp")
@Api(value = "寻源模型报价模板-(供应商端)", tags = "寻源模型报价模板-(供应商端)")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouQuoteTempForVendorController {

    @Autowired
    private ISouQuoteTempService souQuoteTempService;

    private void validRole() {
        boolean isBuyer = AppUserUtil.getLoginAppUser() != null && UserType.VENDOR.name().equals(AppUserUtil.getLoginAppUser().getUserType());
        AssertUtils.isTrue(isBuyer, "非供应商角色，禁止操作");
    }

    @GetMapping("/order/{tempId}/{businessId}")
    @ApiOperation(value = "查看报价模板数据")
    public SouQuoteTempDataVO getTempOrderInfo(@PathVariable("tempId") Long tempId, @PathVariable("businessId") String businessId) {
        this.validRole();
        return souQuoteTempService.queryTempData(tempId, businessId, true);
    }

    @PostMapping("/order/{tempId}/{businessId}")
    @ApiOperation(value = "供应商报价")
    public SouQuoteTempDataDetailVO tempOrder(@PathVariable("tempId") Long tempId, @PathVariable("businessId") String businessId,
                                              @RequestBody Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> tempData) {
        this.validRole();
        return souQuoteTempService.computeTempData(tempId, businessId, tempData, true, false);
    }

}
