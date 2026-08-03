package com.midea.cloud.srm.biz.pj.hrorganization.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.srm.biz.pj.hrorganization.service.SccPjOrganizationService;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author huangbf3
 */
@RestController
@RequestMapping({"/hrOrganization"})
@Api(value = "HrOrganizationController", tags = "hr组织关系")
public class HrOrganizationController {

    @Resource
    private SccPjOrganizationService sccPjOrganizationService;


    @GetMapping({"/getHrOrganizationInfoByOrganizationId"})
    @ApiOperation(value = "通过组织id获取hr组织信息", notes = "通过组织id获取hr组织信息")
    public SccPjOrganization getHrOrganizationInfo(@RequestParam("organizationId") Long organizationId) {
        return sccPjOrganizationService.getOne(new LambdaQueryWrapper<SccPjOrganization>().eq(SccPjOrganization::getOrganizationId, organizationId));
    }
}
