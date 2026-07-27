package com.midea.cloud.srm.biz.pj.sou.quotetemplate.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempApiService;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempApiEditDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempApiQueryDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempApi;
import com.midea.cloud.srm.model.common.enums.UserType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 寻源 - 模型报价模板 - 报价属性
 *
 * @author zhangwk12@midea.com
 * @since 2022/08/17
 */
@RestController
@RequestMapping("/buyer/quote-temp/api")
@Api(value = "寻源模型报价模板-API列表(采购商端)", tags = "寻源模型报价模板-API列表(采购商端)")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouQuoteTempApiForBuyerController {

    @Autowired
    private ISouQuoteTempApiService souQuoteTempApiService;

    private void validRole() {
        boolean isBuyer = AppUserUtil.getLoginAppUser() == null || UserType.BUYER.name().equals(AppUserUtil.getLoginAppUser().getUserType());
        AssertUtils.isTrue(isBuyer, "非采购商角色，禁止操作");
    }

    /**
     * 采购商端: 列表查询
     */
    @PostMapping("/page")
    @ApiOperation("列表查询")
    public PageInfo<SouQuoteTempApi> pageApis(@RequestBody SouQuoteTempApiQueryDTO queryParam) {
        this.validRole();
        return new PageInfo<>(souQuoteTempApiService.listApis(queryParam));
    }

    /**
     * 采购商端: 查询api详情
     */
    @GetMapping("/get/{apiId}")
    @ApiOperation("查询api详情")
    public SouQuoteTempApi getApi(@PathVariable("apiId") Long apiId) {
        this.validRole();
        return souQuoteTempApiService.getApi(apiId);
    }

    /**
     * 采购商端: 编辑/提交api
     */
    @PostMapping("/edit")
    @ApiOperation("编辑/提交api")
    public long/* apiId */ editApi(@RequestBody SouQuoteTempApiEditDTO param) {
        this.validRole();
        return souQuoteTempApiService.editApi(param, true);
    }

    /**
     * 采购商端: api测试
     */
    @PostMapping("/test/{apiId}")
    @ApiOperation("api测试")
    public Object testApi(@PathVariable("apiId") Long apiId,
                              @RequestBody Map<String/* argName */, Object> params) {
        this.validRole();
        return souQuoteTempApiService.executeApi(apiId, params, true);
    }

}
