package com.midea.cloud.srm.sup.black.service.impl.controller;

import com.midea.cloud.srm.sup.black.service.impl.PjBlackServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * @author luxc18
 */
@RestController
@RequestMapping("/pj/supplier")
public class BlackController {

    @Autowired
    private PjBlackServiceImpl pjBlackServiceImpl;


    @ApiOperation(value = "供应商黑名单")
    @GetMapping("/getDataPushFlow")
    public String getDataPushFlow(@RequestParam("businessId") Long businessId,@RequestParam("param") String param) throws Exception {
        return pjBlackServiceImpl.getDataPushFlow(businessId, param);
    }
}
