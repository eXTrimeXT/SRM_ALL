package com.midea.cloud.srm.biz.pj.hruser.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.srm.biz.pj.hruser.service.ISccPjUserService;
import com.midea.cloud.srm.model.pj.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangbf3
 */
@RestController
@RequestMapping("/pj-anon/user")
public class HrUserController {

    @Autowired
    private ISccPjUserService iSccPjUserService;

    @GetMapping("/getHrUserOrgnizationByUsername")
    @ApiOperation("根据用户账号获取用户组织信息")
    public HrUserOrgnizationDto getHrUserOrgnizationByUsername(@RequestParam String username){
        return iSccPjUserService.getHrUserOrgnizationByUsername(username);
    }

    @GetMapping("/getBuOrganizationByOuOrgCode")
    @ApiOperation("根据公司组织编码获取板块组织")
    public Organization getBuOrganizationByOuOrgCode(@RequestParam String organizationCode){
        return iSccPjUserService.getBuOrganizationByOuOrgCode(organizationCode);
    }

    @GetMapping("/getSccUserByPersonnelNo")
    @ApiOperation("根据用户账号获取用户信息")
    public SccPjUser getSccUserByPersonnelNo(@RequestParam("personnelNo") String personnelNo){
        return iSccPjUserService.getOne(new LambdaQueryWrapper<SccPjUser>().eq(SccPjUser::getPersonnelNo, personnelNo));
    }
}
