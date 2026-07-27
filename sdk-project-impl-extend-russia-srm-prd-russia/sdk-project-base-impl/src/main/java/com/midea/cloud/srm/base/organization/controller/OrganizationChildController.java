package com.midea.cloud.srm.base.organization.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.base.organization.service.IOrganizationService;
import com.midea.cloud.srm.base.organization.service.OrganizationChildService;
import com.midea.cloud.srm.model.base.entity.Organization;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ex_liuxy46
 */
@Slf4j
@RestController
@RequestMapping("/organization/organization/ch")
public class OrganizationChildController {

    @Resource
    private OrganizationChildService organizationChildService;

    @Autowired
    private IOrganizationService iOrganizationService;
    /**
     * 分页查询全部组织
     * @param organization 组织信息
     * @return 组织信息
     */
    @ApiOperation(value = "分页查询全部组织", notes = "分页查询全部组织")
    @PostMapping("/listAllOrganization")
    public PageInfo<Organization> listAllOrganization(@RequestBody Organization organization) {
        log.info("分页查询全部组织---listAllOrganization");
        return organizationChildService.listAllOrganization(organization);
    }

    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @PostMapping("/getOrganizationByOrgId")
    public Organization getOrganizationByOrgId(@RequestParam("organizationId") Long organizationId) {
        log.info("获取数据---getOrganizationByOrgId");
        return organizationChildService.getById(organizationId);
    }

    @ApiOperation(value = "根据编码查询", notes = "根据编码查询")
    @PostMapping("/listOrganizationByOrgCodes")
    public List<Organization> listOrganizationByOrgCodes(@RequestBody List<String> organizationCodeList) {
        return organizationChildService.list(new LambdaQueryWrapper<Organization>().in(Organization::getOrganizationCode, organizationCodeList));
    }

    @ApiOperation(value = "根据orgCodes查询", notes = "根据orgCodes查询")
    @PostMapping("/getOrganizationByOrgCodes")
    public List<com.midea.cloud.srm.model.base.organization.entity.Organization> getOrganizationByOrgCodes(@RequestBody List<String> orgCodes) {
        Assert.isTrue(CollectionUtils.isNotEmpty(orgCodes), "查询组织编码列表不能为空");
        return iOrganizationService.list(new LambdaQueryWrapper<com.midea.cloud.srm.model.base.organization.entity.Organization>().in(com.midea.cloud.srm.model.base.organization.entity.Organization::getOrganizationCode, orgCodes));
    }
}
