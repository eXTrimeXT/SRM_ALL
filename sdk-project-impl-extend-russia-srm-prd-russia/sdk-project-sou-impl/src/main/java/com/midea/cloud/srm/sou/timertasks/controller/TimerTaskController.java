package com.midea.cloud.srm.sou.timertasks.controller;

import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.sou.timertasks.service.SrmNpmSouTimerTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: panmq
 * @Date: 2024/03/20/ $
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/timertask")
public class TimerTaskController extends BaseController {

    @Autowired
    private SrmNpmSouTimerTaskService srmNpmSouTimerTaskService;

    /**
     * 定时任务监听任务
     */
    @PostMapping("/listeningTaskJob")
    public void listeningTaskJob(@RequestBody Map<String, Object> param) {
        srmNpmSouTimerTaskService.listeningTaskJob();
    }
}
