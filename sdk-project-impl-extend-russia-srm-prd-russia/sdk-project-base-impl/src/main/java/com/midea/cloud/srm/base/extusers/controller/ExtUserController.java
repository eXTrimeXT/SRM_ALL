package com.midea.cloud.srm.base.extusers.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.base.extusers.service.ExtUserService;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;
import com.midea.cloud.srm.model.common.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
@RestController
@Slf4j
@Api("用户二开控制类")
@RequestMapping("/extUser/v1")
public class ExtUserController extends BaseController {

    @Autowired
    private ExtUserService extUserService;

    @PostMapping("/initOrgnizationUser")
    @ApiOperation("初始化用户组织关系表")
    public List<OrganizationUser> initOrgnizationUser(@RequestBody List<OrganizationUser> organizationUserList) {
        try {
            return extUserService.initOrgnizationUser(organizationUserList);
        } catch (Exception e) {
            log.error("initOrgnizationUser Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
