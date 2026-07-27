package com.midea.cloud.srm.biz.pj.sccpjcmscallbacktemps.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.biz.pj.sccpjcmscallbacktemps.service.ISccPjCmsCallbackTempService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.sccpjcmscallbacktemps.entity.SccPjCmsCallbackTemp;
import com.midea.cloud.srm.model.pj.extapis.cmscloud.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @description 财务共享-回推接口表-控制类
 * @author panmq
 * @date 2024-03-04
 */
@RestController
@Slf4j
@RequestMapping("/cms/callback")
public class SccPjCmsCallbackTempController extends BaseController {

   @Autowired
    private ISccPjCmsCallbackTempService iSccPjCmsCallbackTempService;

    /**
     * 财务共享-付款结果回推接口
     * @param request
     * @return
     */
    @PostMapping("/resultToSrm")
    public CmscloudBodyDto<List<CmscloudBodyDataDto>> callbackSrm(@RequestBody CmscloudBodyDto<List<CmscloudBodyDataDto>> request) {
        return iSccPjCmsCallbackTempService.callbackSrm(request);
    }

    /**
     * 重新处理
     * @param param
     */
    @PostMapping("/rehandlerSrmWithBusiness")
    public void rehandlerSrmWithBusiness(@RequestBody Map<String, Object> param) {
        iSccPjCmsCallbackTempService.rehandlerSrmWithBusiness(param);
    }

}

