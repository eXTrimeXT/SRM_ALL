package com.midea.cloud.srm.biz.pj.sunhonesty.controller;


import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.biz.pj.sunhonesty.service.SunHonestyService;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.pj.siss.dto.SunHonestyReturnDto;
import com.midea.cloud.srm.model.pj.siss.dto.SunHonestySupDto;
import com.midea.cloud.srm.model.pj.sunhonesty.dto.SunHonestyExam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 对接阳光诚信自助平台相关
 * @author GW00311146
 */
@RestController
@RequestMapping({"/sun-honesty"})
public class SunHonestyController {

    @Autowired
    private SunHonestyService sunHonestyService;

    @ApiOperation(value = "推送供应商信息给阳光诚信自助平台")
    @PostMapping("/pushCompanyUser")
    public List<SunHonestyReturnDto> pushCompanyUser(@RequestBody List<SunHonestySupDto> sunHonestySupDtos){
        return sunHonestyService.pushCompanyUser(sunHonestySupDtos);
    }

    @ApiOperation(value = "单点认证登录阳光诚信自助平台-返回免登陆url")
    @PostMapping("/externalSso")
    public String externalSso() throws Exception {
        //判断当前身份
        boolean isVendor = AppUserUtil.getLoginAppUser() == null || UserType.VENDOR.name().equals(AppUserUtil.getLoginAppUser().getUserType());
        AssertUtils.isTrue(isVendor, "非供应商商角色，禁止操作");
        return sunHonestyService.getUrlForSunHonestySys(AppUserUtil.getLoginAppUser());
    }
    @ApiOperation(value = "校验供应商是否需要考试")
    @PostMapping("/checkExam")
    public SunHonestyExam checkExam() throws Exception {
        return sunHonestyService.checkExam();
    }


}
