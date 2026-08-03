package com.midea.cloud.srm.biz.pj.rbac.user.controller;

import com.midea.cloud.srm.biz.pj.rbac.user.IUserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liangxz24
 * @date 2024/10/08
 * @apiNote
 */
@Slf4j
@RestController
@RequestMapping("/pj/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 更新用户的公司名称
     * @param
     */
    @ApiOperation(value = "更新用户的公司名称", notes = "更新用户的公司名称")
    @PostMapping("/updateUserCompanyName")
    public void updateUserCompanyName() {
        iUserService.updateUserCompanyName();
    }
}
