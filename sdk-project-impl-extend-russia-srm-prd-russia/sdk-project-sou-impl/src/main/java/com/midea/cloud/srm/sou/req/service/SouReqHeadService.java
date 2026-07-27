package com.midea.cloud.srm.sou.req.service;


import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.sou.req.SouReqHead;

import java.util.Map;

/**
 * 寻源需求单头表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-04
 */
public interface SouReqHeadService extends BaseService<SouReqHead> {
    /**
     * 扫描全表，需要报名截止的单据，变更状态(异步)
     */
    void handleSignupDone();

    /**
     * 扫描全表，需要报名截止的单据，变更状态(同步)
     */
    void handleSignupDoneSync();

    /**
     * 备注
     * @param params 参数
     */
    void updateExtPr(Map<String, Object> params);

    /**
     * updateRequirementHeadExtPr
     * @param params
     */
    void updateRequirementHeadExtPr(Map<String, Object> params);
}
