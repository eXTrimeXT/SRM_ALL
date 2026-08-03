package com.midea.cloud.srm.biz.pj.base.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangbf3
 * 获取访问者电脑信息
 */
@Slf4j
@RestController
@RequestMapping("/external/visitorInfo")
public class GetVisitorInfoController {

//    /**
//     * 获取访问者电脑配置信息
//     */
//    @ApiOperation(value = "获取访问者电脑配置信息")
//    @GetMapping("/getVisitorInfo")
//    public void getVisitorInfo(HttpServletRequest request) {
//        log.info("CPU序列号:"+ GetVisitorInfo.GetCpuSerialNumber());
//        log.info("计算机网卡MAC地址:"+ GetVisitorInfo.GetMacAddress());
//        log.info("硬盘序列号:"+ GetVisitorInfo.GetHardDiskSerialNumber());
//    }
}
