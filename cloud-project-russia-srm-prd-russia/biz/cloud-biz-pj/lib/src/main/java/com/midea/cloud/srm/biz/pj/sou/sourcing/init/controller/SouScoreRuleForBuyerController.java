package com.midea.cloud.srm.biz.pj.sou.sourcing.init.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouScoreRuleEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouScoreRuleQueryService;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouScoreRuleDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouScoreRuleQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouScoreRuleVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouScoreRule;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 寻源(采购商端) - 评分规则
 *
 * @author zhangwk12@midea.com
 * @since 2022/07/22
 */
@RestController
@RequestMapping("/buyer/scoreRule")
@Api(value = "寻源评分规则接口(采购商端)", tags = "寻源评分规则接口(采购商端)")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouScoreRuleForBuyerController {

    @Autowired
    private SouScoreRuleQueryService souScoreRuleQueryService;
    @Autowired
    private SouScoreRuleEventService souScoreRuleEventService;

    @PostMapping("/page")
    @ApiOperation(value = "分页查询评分规则")
    public PageInfo<SouScoreRule> pageScoreRules(@RequestBody ApiSouScoreRuleQueryDTO queryParam) {
        SouUserTypeCheckUtils.validRole(AppUserUtil.getLoginAppUser(), true);
        return new PageInfo<>(souScoreRuleQueryService.listScoreRules(queryParam));
    }

    @GetMapping("/{scoreRuleId}")
    @ApiOperation(value = "查询评分规则明细")
    public ApiSouScoreRuleVO pageScoreRules(@PathVariable("scoreRuleId") Long scoreRuleId) {
        SouUserTypeCheckUtils.validRole(AppUserUtil.getLoginAppUser(), true);
        return souScoreRuleQueryService.getScoreRule(scoreRuleId);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑评分规则")
    public long/* scoreRuleId */ editScoreRule(@RequestBody ApiSouScoreRuleDTO param) {
        SouUserTypeCheckUtils.validRole(AppUserUtil.getLoginAppUser(), true);
        return souScoreRuleEventService.editScoreRule(param, true);
    }

    @PostMapping("/submit")
    @ApiOperation(value = "编辑评分规则")
    public long/* scoreRuleId */ submitScoreRule(@RequestBody ApiSouScoreRuleDTO param) {
        SouUserTypeCheckUtils.validRole(AppUserUtil.getLoginAppUser(), true);
        return souScoreRuleEventService.editScoreRule(param, false);
    }

    @PostMapping("/valid/{scoreRuleId}")
    @ApiOperation(value = "生效评分规则")
    public void validScoreRule(@PathVariable("scoreRuleId") Long scoreRuleId) {
        SouUserTypeCheckUtils.validRole(AppUserUtil.getLoginAppUser(), true);
        souScoreRuleEventService.validScoreRule(scoreRuleId, SouTypeEnum.DEFAULT.name());
    }

    @PostMapping("/invalid/{scoreRuleId}")
    @ApiOperation(value = "失效评分规则")
    public void invalidScoreRule(@PathVariable("scoreRuleId") Long scoreRuleId) {
        SouUserTypeCheckUtils.validRole(AppUserUtil.getLoginAppUser(), true);
        souScoreRuleEventService.invalidScoreRule(scoreRuleId, SouTypeEnum.DEFAULT.name());
    }

    @DeleteMapping("/remove/{scoreRuleId}")
    @ApiOperation(value = "失效评分规则")
    public void removeScoreRule(@PathVariable("scoreRuleId") Long scoreRuleId) {
        SouUserTypeCheckUtils.validRole(AppUserUtil.getLoginAppUser(), true);
        souScoreRuleEventService.removeScoreRule(scoreRuleId, SouTypeEnum.DEFAULT.name());
    }

}
