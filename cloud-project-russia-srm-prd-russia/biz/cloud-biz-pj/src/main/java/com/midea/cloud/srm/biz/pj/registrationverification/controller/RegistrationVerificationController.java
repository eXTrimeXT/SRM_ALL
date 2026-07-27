package com.midea.cloud.srm.biz.pj.registrationverification.controller;

import com.midea.cloud.srm.biz.pj.registrationverification.RegistrationVerificationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author huangbf3
 * 供应商注册验证码验证接口
 * **/
@RestController
@RequestMapping("/external/verification")
public class RegistrationVerificationController {

    @Autowired
    private RegistrationVerificationService registrationVerificationService;

    /**
     * 供应商注册验证码验证接口,可以选择短信验证或者邮件验证
     * 20231005    verifyType  验证方式   phone:短信   email:邮箱,
     *             contactInformation
     * **/
    @ApiOperation(value = "供应商注册验证码验证接口")
    @PostMapping("/sendVerifyCode")
    public void sendVerifyCode(@RequestParam String verifyType, @RequestParam String email, @RequestParam String phone)  {
        registrationVerificationService.sendVerifyCode(verifyType,email,phone);
    }


}
