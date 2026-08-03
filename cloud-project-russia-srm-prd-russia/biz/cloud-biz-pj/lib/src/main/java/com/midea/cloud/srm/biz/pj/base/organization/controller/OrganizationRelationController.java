package com.midea.cloud.srm.biz.pj.base.organization.controller;

import com.midea.cloud.srm.biz.pj.base.organization.service.IOrganizationRelationService;
import com.midea.cloud.srm.model.pj.base.organization.dto.TreeNew;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author huangbf3
 */
@RestController
@RequestMapping({"/organization/relation"})
@Api(value = "OrganizationRelationController", tags = "组织关系")
public class OrganizationRelationController {

    @Resource
    private IOrganizationRelationService iOrganizationRelationService;


    @PostMapping({"/treeNew"})
    @ApiOperation(value = "通过父节点组装组织架构树（New）", notes = "通过父节点组装组织架构树（New）")
    public List<TreeNew> allTree() {
        return iOrganizationRelationService.assembleTreeByParentNew(new TreeNew());
    }

    @PostMapping("/treeNewAllGroupBuOu")
    @ApiOperation("查询集团-板块-公司结构(所有-不受当前操作人影响)")
    public List<TreeNew> treeNewAllGroupBuOu() {
        return iOrganizationRelationService.treeNewAllGroupBuOu();
    }

}
