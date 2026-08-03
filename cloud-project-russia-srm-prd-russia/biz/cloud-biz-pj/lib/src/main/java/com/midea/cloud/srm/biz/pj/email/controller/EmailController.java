package com.midea.cloud.srm.biz.pj.email.controller;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.biz.pj.email.EmailService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author huangbf3
 * 邮件接口-发送普通邮件controller
 * **/
@RestController
@RequestMapping("/external/email")
public class EmailController {

    @Autowired
    private EmailService emailService;
    /**
     * 邮件接口-发送普通邮件
     * 20230927
     * **/
    @ApiOperation(value = "邮件接口-发送普通邮件")
    @PostMapping("/sendEmail")
    public JSONObject sendEmail(@RequestBody JSONObject jsonData)  {
        return  emailService.sendEmail(jsonData);

    }


}
