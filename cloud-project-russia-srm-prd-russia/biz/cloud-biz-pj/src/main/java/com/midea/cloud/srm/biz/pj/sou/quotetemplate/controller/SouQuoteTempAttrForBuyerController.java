package com.midea.cloud.srm.biz.pj.sou.quotetemplate.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempAttrService;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempAttrEditDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempAttrQueryDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempAttr;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrVO;
import com.midea.cloud.srm.model.common.enums.UserType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 寻源 - 模型报价模板 - 报价属性
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/27
 */
@RestController
@RequestMapping("/buyer/quote-temp/attr")
@Api(value = "寻源模型报价模板-报价属性服务(采购商端)", tags = "寻源模型报价模板-报价属性服务(采购商端)")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouQuoteTempAttrForBuyerController {

    @Autowired
    private ISouQuoteTempAttrService souQuoteTempAttrService;

    private void validRole() {
        boolean isBuyer = AppUserUtil.getLoginAppUser() == null || UserType.BUYER.name().equals(AppUserUtil.getLoginAppUser().getUserType());
        AssertUtils.isTrue(isBuyer, "非采购商角色，禁止操作");
    }

    @PostMapping("/page")
    @ApiOperation(value = "报价属性分页查询")
    public PageInfo<SouQuoteTempAttr> pageAttrs(@RequestBody SouQuoteTempAttrQueryDTO queryParam) {
        this.validRole();
        return new PageInfo<>(souQuoteTempAttrService.listAttrsById(queryParam.formatAndConvert(), queryParam.getPageNum(), queryParam.getPageSize()));
    }

    @GetMapping("/detail/{attrId}")
    @ApiOperation(value = "获取报价属性详情信息")
    public SouQuoteTempAttrVO getAttr(@PathVariable("attrId") Long attrId) {
        this.validRole();
        return souQuoteTempAttrService.getAttr(attrId);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑/提交报价属性")
    public long/* attrId */ editAttr(@RequestBody SouQuoteTempAttrEditDTO param) {
        this.validRole();
        return souQuoteTempAttrService.editAttr(param, param.isTempSave());
    }

    @PostMapping("/remove/{attrId}")
    @ApiOperation(value = "删除报价属性")
    public void removeAttr(@PathVariable("attrId") Long attrId) {
        this.validRole();
        souQuoteTempAttrService.removeAttr(attrId);
    }

    @PostMapping("/valid/{attrId}")
    @ApiOperation(value = "生效报价属性")
    public void validAttr(@PathVariable("attrId") Long attrId) {
        this.validRole();
        souQuoteTempAttrService.validAttr(attrId, AppUserUtil.getLoginAppUser());
    }

    @PostMapping("/invalid/{attrId}")
    @ApiOperation(value = "失效报价属性")
    public void invalidAttr(@PathVariable("attrId") Long attrId) {
        this.validRole();
        souQuoteTempAttrService.invalidAttr(attrId);
    }

    @PostMapping("/copy/{attrId}")
    @ApiOperation(value = "复制报价属性")
    public SouQuoteTempAttr copyAttr(@PathVariable("attrId") Long attrId) {
        this.validRole();
        return souQuoteTempAttrService.copyAttr(attrId);
    }

}
