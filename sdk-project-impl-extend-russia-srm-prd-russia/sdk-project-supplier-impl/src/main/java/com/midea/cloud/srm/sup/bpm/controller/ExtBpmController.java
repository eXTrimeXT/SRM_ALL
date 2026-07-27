package com.midea.cloud.srm.sup.bpm.controller;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Api(value = "/ext/bpm", tags = {"bpm审批流"})
@RestController
@RequestMapping("/ext/bpm")
public class ExtBpmController {


    @ApiOperation(value = "获取bpm业务单据传值", notes = "获取bpm业务单据传值", httpMethod = "GET")
    @GetMapping("/getDataPushFlow")
    public JSONObject getDataPushFlow(@RequestParam("serviceBean") String serviceBean, @RequestParam("businessId")Long businessId) throws Exception {
        IFlowBusinessCallbackService iFlowBusinessCallbackService = null;

        Class clazz = Class.forName(serviceBean);
        Object bean = SpringContextHolder.getApplicationContext().getBean(clazz);
        iFlowBusinessCallbackService = (IFlowBusinessCallbackService) bean;

        String dataPushFlowStr = iFlowBusinessCallbackService.getDataPushFlow(businessId,"");
        return JSONObject.parseObject(dataPushFlowStr);
    }
}
