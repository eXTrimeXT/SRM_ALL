package com.midea.cloud.srm.biz.pj.hrusertemp.controller;


import com.alibaba.fastjson.JSON;
import com.midea.cloud.srm.biz.pj.hrusertemp.service.ISccPjUserTempService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.hrusertemps.entity.SccPjUserTemp;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjHrUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author huangbf3
 */
@Slf4j
@RestController
@RequestMapping("/pj-anon/hrUser")
public class SccPjUserTempController extends BaseController {

    @Autowired
    private ISccPjUserTempService iSccPjUserTempService;

    /**
     * 全量同步HR人员信息
     * @param param
     * @return
     */
    @PostMapping("/syncAllHrUser")
    public List<SccPjUserTemp> syncAllHrUser(@RequestBody Map<String, Object> param) {
        return iSccPjUserTempService.syncAllHrUser(param);
    }

    /**
     * 全量处理HR人员信息接口临时表
     * @param
     * @return
     */
    @PostMapping("/doAllPending")
    public String doAllPending() {
        return JSON.toJSONString(iSccPjUserTempService.doAllPending());
    }

    @GetMapping("/getHrUserInfo")
    public SccPjHrUserInfo getHrUserInfo(@RequestParam("personnelNo") String personnelNo) {
        return iSccPjUserTempService.getHrUserInfo(personnelNo);
    }

    @GetMapping("/getHrUserInfoWithoutErr")
    public SccPjHrUserInfo getHrUserInfoWithoutErr(@RequestParam("personnelNo") String personnelNo){
        try {
            return iSccPjUserTempService.getHrUserInfo(personnelNo);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @PostMapping("/listHrUserInfos")
    public Map<String/* 工号 */, SccPjHrUserInfo> listHrUserInfos(@RequestBody Set<String> personnelNo) {
        return iSccPjUserTempService.listHrUserInfos(personnelNo);
    }

}
