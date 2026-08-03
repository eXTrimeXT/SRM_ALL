package com.midea.cloud.srm.biz.pj.sapcostcenterinfo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.biz.pj.sapcostcenterinfo.service.CostCenterInfoService;
import com.midea.cloud.srm.model.pj.sapcostcenter.CostCenterInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author huangbf3
 */
@RestController
@RequestMapping("/sap/costCenter")
public class CostCenterInfoController {

    @Resource
    private CostCenterInfoService costCenterInfoService;

    /**
     * SAP成本中心科目信息
     * @param costCenterInfo 查询
     */
    @PostMapping("/getCostCenterInfo")
    @ApiOperation(value = "SAP成本中心科目信息", notes = "SAP成本中心科目信息")
    public PageInfo<CostCenterInfo> getCostCenterInfo(@RequestBody CostCenterInfo costCenterInfo) {
        PageUtil.startPage(costCenterInfo.getPageNum(), costCenterInfo.getPageSize());
        return new PageInfo<>(costCenterInfoService.list(new QueryWrapper<>(costCenterInfo)));
    }

}
