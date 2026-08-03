package com.midea.cloud.srm.biz.pj.sou.comp.signup.service;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpChangeEndTimeDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpConfirmDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpVendorDTO;

/**
 * 竞价 - 报名事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
public interface CompSouSignUpEventWebService {

    /**
     * 供应商报名
     * @param param
     */
    void vendorSignUp(ApiSouSignUpVendorDTO param);

    /**
     * 确认/驳回报名
     * @param param
     */
    void confirmSignUp(ApiSouSignUpConfirmDTO param);

    /**
     * 立即截止报名/延长报名时间
     * @param param
     */
    void changeSignUpEndTime(ApiSouSignUpChangeEndTimeDTO param);

}
