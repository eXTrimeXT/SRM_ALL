package com.midea.cloud.srm.biz.pj.message.controller;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.biz.pj.message.MessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author huangbf3
 * 短信通知-发送短信controller
 * **/
@RestController
@RequestMapping("/external/SMS")
public class MessageController {

    @Autowired
    private MessageService messageService;
    /**
     * 短信通知-发送短信
     * 20230927
     * **/
    @ApiOperation(value = "短信平台-发送短信通知,入参为内容和电话列表,多个手机号码用英文逗号分隔")
    @PostMapping("/message")
    public JSONObject message(@RequestParam String content , @RequestParam String phones)  {
        return  messageService.message(content,phones);

    }


}
