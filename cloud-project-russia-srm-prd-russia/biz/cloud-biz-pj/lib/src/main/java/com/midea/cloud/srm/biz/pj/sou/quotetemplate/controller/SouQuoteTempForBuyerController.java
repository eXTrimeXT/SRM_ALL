package com.midea.cloud.srm.biz.pj.sou.quotetemplate.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempService;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempEditDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempQueryDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTemp;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempDataDetailVO;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempDataVO;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempVO;
import com.midea.cloud.srm.model.common.enums.UserType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 寻源 - 模型报价模板
 *
 * @author zhangwk12@midea.com
 * @since 2022/08/02
 */
@RestController
@RequestMapping("/buyer/quote-temp")
@Api(value = "寻源模型报价模板-(采购商端)", tags = "寻源模型报价模板-(采购商端)")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouQuoteTempForBuyerController {

    @Autowired
    private ISouQuoteTempService souQuoteTempService;

    private void validRole() {
        boolean isBuyer = AppUserUtil.getLoginAppUser() == null || UserType.BUYER.name().equals(AppUserUtil.getLoginAppUser().getUserType());
        AssertUtils.isTrue(isBuyer, "非采购商角色，禁止操作");
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页查询报价模板")
    public PageInfo<SouQuoteTemp> pageTemps(@RequestBody SouQuoteTempQueryDTO queryParam) {
        this.validRole();
        return new PageInfo<>(souQuoteTempService.listTemps(queryParam));
    }

    @GetMapping("/detail/{tempId}")
    @ApiOperation(value = "查询报价模板详情")
    public SouQuoteTempVO getTemp(@PathVariable("tempId") Long tempId) {
        this.validRole();
        return souQuoteTempService.getTemp(tempId);
    }

    @PostMapping("/check-select")
    @ApiOperation(value = "校验添加的报价属性是否是完整的")
    public void checkTempAttrs(@RequestBody Set<Long> attrIds) {
        this.validRole();
        souQuoteTempService.checkTempAttrs(attrIds);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑报价模板")
    public long/* tempId */ editTemp(@RequestBody SouQuoteTempEditDTO param) {
        this.validRole();
        return souQuoteTempService.editTemp(param, true);
    }

    @PostMapping("/submit")
    @ApiOperation(value = "提交报价模板")
    public long/* tempId */ submitTemp(@RequestBody SouQuoteTempEditDTO param) {
        this.validRole();
        return souQuoteTempService.editTemp(param, false);
    }

    @PostMapping("/remove/{tempId}")
    @ApiOperation(value = "删除报价模板")
    public void removeTemp(@PathVariable("tempId") Long tempId) {
        this.validRole();
        souQuoteTempService.removeTemp(tempId);
    }

    @PostMapping("/valid/{tempId}")
    @ApiOperation(value = "生效报价模板")
    public void validTemp(@PathVariable("tempId") Long tempId) {
        this.validRole();
        souQuoteTempService.validTemp(tempId);
    }

    @PostMapping("/invalid/{tempId}")
    @ApiOperation(value = "失效报价模板")
    public void invalidTemp(@PathVariable("tempId") Long tempId) {
        this.validRole();
        souQuoteTempService.invalidTemp(tempId);
    }

    @PostMapping("/copyTemp/{tempId}")
    @ApiOperation(value = "复制报价模板")
    public SouQuoteTemp copyTemp(@PathVariable("tempId") Long tempId) {
        this.validRole();
        return souQuoteTempService.copyTemp(tempId);
    }

    @GetMapping("/preview/{tempId}")
    @ApiOperation(value = "报价模板预览")
    public SouQuoteTempDataVO tempPreview(@PathVariable("tempId") Long tempId) {
        this.validRole();
        return souQuoteTempService.queryTempData(tempId, tempId + "_test", false);
    }

    @PostMapping("/previewTest/{tempId}")
    @ApiOperation(value = "报价测试")
    public SouQuoteTempDataDetailVO tempPreviewTest(@PathVariable("tempId") Long tempId,
                                                    @RequestBody Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> tempData) {
        this.validRole();
        return souQuoteTempService.computeTempData(tempId, tempId + "_test", tempData, false, true);
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
