package com.midea.cloud.srm.base.org.controller;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.base.org.service.OrgQueryService;
import com.midea.cloud.srm.model.base.dto.OrgQueryDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationRelation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zenghx2
 */
@Api(value = "OrgQueryController", tags = {"非招标需求组织查询"})
@RestController
@Slf4j
@RequestMapping("/orgQuery")
public class OrgQueryController {

    @Autowired
    private OrgQueryService orgQueryService;

    /**
     * 查询所有下级组织列表
     */
    @PostMapping("/getSubOrgs")
    public List<Organization> getSubOrgs(@RequestBody OrgQueryDTO orgQueryDTO) {
        Assert.notNull(orgQueryDTO.getParentId(),"父级id不能为空");
        return orgQueryService.getSubOrgs(orgQueryDTO);
    }

    /**
     * 查询父级板块
     */
    @GetMapping("/getBuOrg")
    public Organization getBuOrg(@RequestParam Long orgId) {
        return orgQueryService.getBuOrg(orgId);
    }


    /**
     * 资质审查获取所有的组织信息
     */
    @GetMapping("/listAllForReviewForm")
    public List<Organization> listAllForReviewForm(@RequestParam Long companyId) throws Exception {
        return orgQueryService.listAllForReviewForm(companyId);
    }


    /**
     * 根据关系ID与组织ID查找下级组织
     * @param organizationId
     * @return
     */
    @PostMapping({"/listChildrenOrganization"})
    @ApiOperation(value = "根据关系ID与组织ID查找下级组织", notes = "根据关系ID与组织ID查找下级组织")
    public List<OrganizationRelation> listChildrenOrganization(Long organizationId) {
        Assert.notNull(organizationId, "organizationId不能为空");
        return this.orgQueryService.listChildrenOrganization(organizationId);
    }


    /**
     * 单个递归查询组织地点信息
     * @param orgId
     * @return
     */
    @GetMapping("/getOrgAddress")
    public List<Record> getOrgAddress(@RequestParam Long orgId) {
        return orgQueryService.getOrgAddress(Arrays.asList(orgId));
    }

    /**
     * 批量查询组织地点信息
     * @param orgIdList
     * @return
     */
    @PostMapping("/getOrgAddressBatch")
    public Map<Long, List<Record>> getOrgAddressBatch(@RequestBody List<Long> orgIdList) {
        Map<Long, List<Record>> result = new HashMap<>(15);
        orgQueryService.getOrgAddressBatch(orgIdList, result, new HashMap<>(15));
        return result;
    }
}
