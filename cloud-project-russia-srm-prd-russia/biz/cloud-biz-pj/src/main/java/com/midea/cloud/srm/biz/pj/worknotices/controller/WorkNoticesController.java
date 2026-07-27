package com.midea.cloud.srm.biz.pj.worknotices.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.srm.biz.pj.worknotices.WorkNoticesService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author huangbf3
 * 钉钉消息-发送工作通知controller
 * **/
@RestController
@RequestMapping("/external/dingding")
public class WorkNoticesController {

    @Autowired
    private WorkNoticesService workNoticesService;

    /**
     * 钉钉消息-发送工作通知
     * 20230926
     * **/
    @ApiOperation(value = "钉钉消息-发送工作通知,入参为内容和员工工号列表")
    @PostMapping("/workNotices")
    public JSONObject workNotices(@RequestParam String content , @RequestBody List<String> userList)  {

        return  workNoticesService.workNotices(content,userList);
    }

}
