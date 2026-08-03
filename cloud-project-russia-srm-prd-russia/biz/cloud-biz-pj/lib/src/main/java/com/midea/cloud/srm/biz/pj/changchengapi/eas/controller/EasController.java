package com.midea.cloud.srm.biz.pj.changchengapi.eas.controller;

import com.midea.cloud.srm.biz.pj.changchengapi.eas.service.EasOrgUnitService;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.service.EasUserService;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author huangbf3
 * EAS接口控制层
 */
@Slf4j
@RestController
@RequestMapping("/eas")
public class EasController {
    @Autowired
    private EasOrgUnitService easOrgUnitService;
    @Autowired
    private EasUserService easUserService;

    @ApiOperation(value = "推送所有的组织")
    @RequestMapping(value="/pushAllOrg",method= RequestMethod.POST)
    public void pushAllOrg() throws Exception {
        easOrgUnitService.pushAllOrg();
    }

    @ApiOperation(value = "推送选择的组织")
    @RequestMapping(value="/pushOrgListToEas",method= RequestMethod.POST)
    public void pushOrgListToEas(@RequestBody List<Organization> organizationList) throws Exception {
        easOrgUnitService.pushOrgListToEas(organizationList);
    }

    @ApiOperation(value = "推送所有的用户")
    @RequestMapping(value="/pushAllUser",method= RequestMethod.POST)
    public void pushAllUser() throws Exception {
        easUserService.pushAllUser();
    }

    @ApiOperation(value = "推送选择的用户")
    @RequestMapping(value="/pushUserListToEas",method= RequestMethod.POST)
    public void pushUserListToEas(@RequestBody List<SccPjUser> sccPjUsers) throws Exception {
        easUserService.pushUserListToEas(sccPjUsers);
    }

}