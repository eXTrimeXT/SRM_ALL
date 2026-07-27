package com.midea.cloud.srm.supcooperate.ext.requirement.pr.controller;

import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.PrPushConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Controller
@RequestMapping("/pr-anon/test")
public class TestController {

    @Autowired
    private PrPushConfigService prPushConfigService;

    @GetMapping("/task")
    public void test(){
        prPushConfigService.autoPushPoolTask();
    }

}
