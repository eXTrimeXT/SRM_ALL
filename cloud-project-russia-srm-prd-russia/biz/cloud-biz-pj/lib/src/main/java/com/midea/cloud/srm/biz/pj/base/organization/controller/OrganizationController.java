package com.midea.cloud.srm.biz.pj.base.organization.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.annotation.CacheClear;
import com.midea.cloud.common.constants.RedisKey;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.biz.pj.base.organization.service.IOrganizationService;
import com.midea.cloud.srm.model.pj.base.organization.dto.OrganizationEditDto;
import com.midea.cloud.srm.model.pj.base.organization.entity.Organization;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author huangbf3
 */
@Slf4j
@RestController
@RequestMapping("/organization/organization")
public class OrganizationController {

    @Resource
    private IOrganizationService iOrganizationService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private QlOpenClient qlOpenClient;

    /**
     * 分页查询全部组织
     * @param organization 组织信息
     * @return 组织信息
     */
    @ApiOperation(value = "分页查询全部组织", notes = "分页查询全部组织")
    @PostMapping("/listAllOrganization")
    public PageInfo<Organization> listAllOrganization(@RequestBody Organization organization) {
        log.info("分页查询全部组织---listAllOrganization");
        return iOrganizationService.listAllOrganization(organization);
    }

    /**
     * 根据组织ID获取组织
     * @param organizationId 组织ID
     * @return 组织信息
     */
    @ApiOperation("根据组织ID获取组织")
    @GetMapping("/getOrganization")
    public OrganizationEditDto findList(@RequestParam("organizationId") Long organizationId) {
        Assert.notNull(organizationId, "organizationId不能为空");
        return iOrganizationService.getOrganization(organizationId);
    }

    /**
     * 根据组织ID获取组织
     * @return 组织信息
     */
    @ApiOperation("根据组织ID获取组织")
    @GetMapping("/findListFilterInvoiceInfo")
    public List<Organization> findListFilterInvoiceInfo() {
        return iOrganizationService.findListFilterInvoiceInfo();
    }

    /**
     * 根据组织ID获取组织
     * @param organizationId 组织ID
     * @return 组织信息
     */
    @ApiOperation("根据组织ID获取组织")
    @GetMapping("/getOrganizationById")
    public Organization getOrganizationById(@RequestParam("organizationId") Long organizationId) {
        Assert.notNull(organizationId, "organizationId不能为空");
        return qlOpenClient.read(ContextPath.BASE,"Organization", organizationId.toString(), Organization.class);
    }

    @PostMapping("/delTreeNewKey")
    @ApiOperation(value = "删除组织树redis的key", notes = "删除组织树redis的key")
    public void delTreeNewKey() {
        redisUtil.del(RedisKey.TREE_NEW_LOCK);
    }

    /**
     * 新增或编辑组织
     * @param organizationEditDto 组织
     */
    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "新增或编辑组织（新）", notes = "新增或编辑组织（新）")
    @CacheClear(keyName = {RedisKey.TREE_BY_PARENT,RedisKey.TREE_NEW_LOCK,RedisKey.Base.ALL_ORGANIZATION})
    public void saveOrUpdate(@RequestBody OrganizationEditDto organizationEditDto) {
        iOrganizationService.saveOrUpdate(organizationEditDto);
    }

}
