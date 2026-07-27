package com.midea.cloud.srm.biz.pj.changchengapi.bpm.controller;

import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmIncorporatedCompanyService;
import com.midea.cloud.srm.model.pj.changchengapi.dto.BpmIncorporatedCompanyParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 法务-法人公司主数据接接口
 * @author huangbf3
 */
@Slf4j
@RestController
@RequestMapping("/external/bpmIncorporated")
public class BpmIncorporatedController {
    @Autowired
    private IBpmIncorporatedCompanyService iBpmIncorporatedCompanyService;

    /**
     * 法务-法人公司主数据接接口查询
     */
    @PostMapping("/pullData")
    public void pullData(@RequestBody BpmIncorporatedCompanyParam param) {
        iBpmIncorporatedCompanyService.pullData(param);
    }
}