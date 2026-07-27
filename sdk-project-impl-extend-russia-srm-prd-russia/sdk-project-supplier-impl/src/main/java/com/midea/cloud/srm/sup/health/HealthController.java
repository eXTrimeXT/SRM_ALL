package com.midea.cloud.srm.sup.health;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangzh242
 * @date 2023/10/10 17:29
 */
@Api(value = "HealthController", tags = "健康检查接口")
@Slf4j
@RestController
@RequestMapping("/sup-anon/health")
public class HealthController {


    @GetMapping("/health")
    @ApiOperation(value = "健康检查接口")
    public String health() {
        log.info("应用正常");
        return "应用正常";
    }
}
