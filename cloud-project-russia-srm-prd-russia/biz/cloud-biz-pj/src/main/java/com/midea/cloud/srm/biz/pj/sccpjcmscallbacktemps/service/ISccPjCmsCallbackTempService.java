package com.midea.cloud.srm.biz.pj.sccpjcmscallbacktemps.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.pj.sccpjcmscallbacktemps.entity.SccPjCmsCallbackTemp;
import com.midea.cloud.srm.model.pj.extapis.cmscloud.dto.*;

import java.util.List;
import java.util.Map;

/**
 * @description 财务共享-回推接口表 接口定义
 * @author panmq
 * @date 2024-03-04
 */
public interface ISccPjCmsCallbackTempService extends IService<SccPjCmsCallbackTemp> {

    /**
     * 财务共享-付款结果回推接口
     * @param request
     * @return
     */
    public CmscloudBodyDto<List<CmscloudBodyDataDto>> callbackSrm(CmscloudBodyDto<List<CmscloudBodyDataDto>> request);

    /**
     * 处理到业务表
     * @param sccPjCmsCallbackTempList
     */
    public void handlerSrmWithBusiness(List<SccPjCmsCallbackTemp> sccPjCmsCallbackTempList);

    /**
     * 重新处理
     * @param param
     */
    public void rehandlerSrmWithBusiness(Map<String, Object> param);
}

