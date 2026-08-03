package com.midea.cloud.srm.biz.pj.hrorganizationtemp.controller;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.srm.biz.pj.hrorganizationtemp.service.ISccPjOrganizationTempService;
import com.midea.cloud.srm.model.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author huangbf3
 */
@Slf4j
@RestController
@RequestMapping("/pj-anon/hrOrganization")
public class SccPjOrganizationTempController extends BaseController {

    @Autowired
    private ISccPjOrganizationTempService iSccPjOrganizationTempService;

    /**
     * 全量同步HR组织信息
     * @param param
     * @return
     */
    @PostMapping("/syncAllHrOrganization")
    public String syncAllHrOrganization(@RequestBody Map<String, Object> param) {
        return JSON.toJSONString(iSccPjOrganizationTempService.syncAllHrOrganization(param));
    }

    /**
     * 全量处理HR组织信息
     * @return
     */
    @PostMapping("/doAllPending")
    public String doAllPending() {
        return JSON.toJSONString(iSccPjOrganizationTempService.doAllPending());
    }
}
