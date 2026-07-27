package com.midea.cloud.srm.biz.pj.sou.sourcing.signup.service;

import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpChangeEndTimeDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpConfirmDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpVendorDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;

/**
 * 项目式询价 - 报名事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/23
 */
public interface SouSignUpEventService {

    /**
     * 供应商报名
     *
     * @param param   报名信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void vendorSignUp(ApiSouSignUpVendorDTO param, String souType);

    /**
     * 确认/驳回报名
     *
     * @param param   报名确认/驳回信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void confirmSignUp(ApiSouSignUpConfirmDTO param, String souType);

    /**
     * 立即截止报名/延长报名时间
     *
     * @param param   修改信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    void changeSignUpEndTime(ApiSouSignUpChangeEndTimeDTO param, String souType);

}
