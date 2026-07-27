package com.midea.cloud.srm.base.organizationrelation.controller;

import cn.hutool.core.lang.Assert;
import com.midea.cloud.srm.base.organization.service.IOrganizationRelationService;
import com.midea.cloud.srm.base.organizationrelation.service.OrgRelService;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationRelation;
import com.midea.cloud.srm.model.pj.base.organization.dto.TreeNew;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Api(value = "OrgQueryController", tags = {"非招标需求组织查询"})
@RestController
@Slf4j
@RequestMapping("/pjorganizationRelation")
public class PjOrganizationRelationController {
    @Autowired
    private IOrganizationRelationService iOrganizationRelationService;

    @Resource
    private OrgRelService orgRelService;

    @PostMapping("/listOrganizationRelation")
    public List<OrganizationRelation> listOrganizationRelation(@RequestBody List<Long> organizationIds) {
        Assert.notNull(organizationIds,"组织ID数组不能为空");
        return iOrganizationRelationService.lambdaQuery().in(OrganizationRelation::getOrganizationId,organizationIds).list();
    }

    @PostMapping({"/treeNew"})
    @ApiOperation(value = "通过父节点组装组织架构树（New）", notes = "通过父节点组装组织架构树（New）")
    public List<TreeNew> allTree() {
        return orgRelService.allTree();
    }
}
