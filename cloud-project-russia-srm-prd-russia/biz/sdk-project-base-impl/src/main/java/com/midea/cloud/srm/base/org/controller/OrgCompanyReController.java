package com.midea.cloud.srm.base.org.controller;

import com.midea.cloud.srm.base.org.service.OrgCompanyReService;
import com.midea.cloud.srm.model.base.organization.dto.OrganizationEditDto;
import com.midea.cloud.srm.model.common.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 100014336
 */
@RestController
@RequestMapping({"/organization/organization/pj/"})
@Api(
        value = "OrganizationController",
        tags = {"组织信息"}
)
public class OrgCompanyReController extends BaseController {

    @Autowired
    private OrgCompanyReService orgCompanyService;

    @GetMapping({"/getCompanyByOuId"})
    @ApiOperation(
            value = "根据业务实体ID获取公司信息",
            notes = "根据业务实体ID获取公司信息"
    )
    public OrganizationEditDto getCompanyByOuId(@RequestParam("organizationId") Long organizationId) {
        Assert.notNull(organizationId, "organizationId不能为空");
        return this.orgCompanyService.getByOuId(organizationId);
    }
}
