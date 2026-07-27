package com.midea.cloud.srm.biz.pj.sou.comp.signup.service.impl;

import com.midea.cloud.srm.biz.pj.sou.comp.signup.service.CompSouSignUpEventWebService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service.SouControlEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.signup.service.SouSignUpEventService;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpChangeEndTimeDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpConfirmDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.signup.ApiSouSignUpVendorDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 竞价 - 报名事件服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouSignUpEventWebServiceImpl implements CompSouSignUpEventWebService {

    @Autowired
    private SouSignUpEventService souSignUpEventService;
    @Autowired
    private SouControlEventService souControlEventService;

    /**
     * 供应商报名
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void vendorSignUp(ApiSouSignUpVendorDTO param) {
        // 0: 刷新数据
        souControlEventService.refreshProjectBySouTime(param.getProjectId());
        // 1: 报名
        souSignUpEventService.vendorSignUp(param, SouTypeEnum.comp.name());
    }

    /**
     * 确认/驳回报名
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void confirmSignUp(ApiSouSignUpConfirmDTO param) {
        // 0: 刷新数据
        souControlEventService.refreshProjectBySouTime(param.getProjectId());
        // 1: 确认/驳回报名
        souSignUpEventService.confirmSignUp(param, SouTypeEnum.comp.name());
    }

    /**
     * 立即截止报名/延长报名时间
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeSignUpEndTime(ApiSouSignUpChangeEndTimeDTO param) {
        // 1: 修改报名截止时间
        souSignUpEventService.changeSignUpEndTime(param, SouTypeEnum.comp.name());
    }

}
