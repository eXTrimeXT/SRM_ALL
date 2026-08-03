package com.midea.cloud.srm.biz.pj.sou.comp.process.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.sou.SouUserTypeCheckUtils;
import com.midea.cloud.srm.biz.pj.sou.comp.process.service.CompSouProcessEventWebService;
import com.midea.cloud.srm.biz.pj.sou.comp.process.service.CompSouProcessQueryWebService;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.process.CompSouProcessConfigWebVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.process.ApiCompSouProcessConfigEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.process.ApiSouProcessConfigQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouProcessNodeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 竞价(采购商端) - 流程信息
 *
 * @author zhangwk12@midea.com
 * @since 2022/12/15
 */
@RestController
@RequestMapping("/buyer/comp/process")
@Api(value = "竞价流程接口(采购商端)", tags = "竞价流程接口(采购商端)")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class WebCompSouProcessForBuyerController {

    @Autowired
    private CompSouProcessQueryWebService compSouProcessQueryService;
    @Autowired
    private CompSouProcessEventWebService compSouProcessEventService;

    /**
     * 流程配置列表查询
     */
    @PostMapping("/page")
    @ApiOperation("流程配置列表查询")
    public PageInfo<CompSouProcessConfigWebVO> pageProcessConfigs(@RequestBody ApiSouProcessConfigQueryDTO queryParam) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return new PageInfo<>(compSouProcessQueryService.listProcessConfigs(queryParam));
    }

    /**
     * 查询指定的流程配置信息
     */
    @GetMapping("/{processConfigId}")
    @ApiOperation("查询指定的流程配置信息")
    public CompSouProcessConfigWebVO getProcessConfig(@PathVariable("processConfigId") Long processConfigId) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return compSouProcessQueryService.getProcessConfig(processConfigId, null);
    }

    /**
     * 查询询价单关联的流程节点信息
     */
    @GetMapping("/nodes/{projectId}")
    @ApiOperation("查询询价单关联的流程节点信息")
    public List<ApiSouProcessNodeVO> listProcessNodes(@PathVariable("projectId") Long projectId) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return compSouProcessQueryService.listProcessNodes(projectId);
    }

    /**
     * 编辑/提交流程配置
     */
    @PostMapping("/editProcessConfig")
    @ApiOperation("编辑/提交流程配置")
    public long/* processConfigId */ editProcessConfig(@RequestBody ApiCompSouProcessConfigEditDTO param) {
        SouUserTypeCheckUtils.checkIsBuyer();
        return compSouProcessEventService.editProcessConfig(param, param.isTempSave());
    }

    /**
     * 生效流程配置
     */
    @PostMapping("/valid/{processConfigId}")
    @ApiOperation("生效流程配置")
    public void validProcessConfig(@PathVariable("processConfigId") Long processConfigId) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compSouProcessEventService.validProcessConfig(processConfigId);
    }

    /**
     * 失效流程配置
     */
    @PostMapping("/invalid/{processConfigId}")
    @ApiOperation("失效流程配置")
    public void invalidProcessConfig(@PathVariable("processConfigId") Long processConfigId) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compSouProcessEventService.invalidProcessConfig(processConfigId);
    }

    /**
     * 删除流程配置
     */
    @DeleteMapping("/remove/{processConfigId}")
    @ApiOperation("删除流程配置")
    public void removeProcessConfig(@PathVariable("processConfigId") Long processConfigId) {
        SouUserTypeCheckUtils.checkIsBuyer();
        compSouProcessEventService.removeProcessConfig(processConfigId);
    }

}
