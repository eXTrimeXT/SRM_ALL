package com.midea.cloud.srm.biz.pj.screen.controller;

import com.midea.cloud.srm.biz.pj.screen.AuthenticationScreenService;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/***
 * @author huangbf3
 * 供应商风险信息controller
 * **/
@RestController
@RequestMapping("/external/authentication")
public class AuthenticationScreenController {

    @Autowired
    private AuthenticationScreenService authenticationScreenService;

    @SneakyThrows(value = {Exception.class})
    @ApiOperation(value = "调用鉴权接口获取令牌")
    @PostMapping("/findToken")
    public Map<String,String> findToken() {
        return authenticationScreenService.findToken();
    }


    @SneakyThrows(value = {Exception.class})
    @ApiOperation(value = "筛查请求")
    @PostMapping("/importScreening")
    public String importScreening(@RequestParam Long companyId) {
        return authenticationScreenService.importScreening(companyId);
    }


}
