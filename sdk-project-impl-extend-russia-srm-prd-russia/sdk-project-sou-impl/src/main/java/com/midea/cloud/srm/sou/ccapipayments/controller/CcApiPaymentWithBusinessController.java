package com.midea.cloud.srm.sou.ccapipayments.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.sccpjcmscallbacktemps.entity.SccPjCmsCallbackTemp;
import com.midea.cloud.srm.sou.ccapipayments.service.CcApiPaymentWithBusinessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/04/17/ $
 * @Description: 财务共享接口
 */
@Slf4j
@RestController
@RequestMapping("/ccApi/payment")
@Api("财务共享接口控制类")
public class CcApiPaymentWithBusinessController extends BaseController {

    @Autowired
    private CcApiPaymentWithBusinessService ccApiPaymentWithBusinessService;

    /**
     * /ccApi/payment/callBackAsApiPayment
     * @param sccPjCmsCallbackTempList
     */
    @ApiOperation("财务共享接口-回调SRM")
    @PostMapping("/callBackAsApiPayment")
    public void callBackAsApiPayment(@RequestBody List<SccPjCmsCallbackTemp> sccPjCmsCallbackTempList) {
        try {
            ccApiPaymentWithBusinessService.callBackAsApiPayment(sccPjCmsCallbackTempList);
        } catch (Exception e) {
            log.error("callBackAsApiPayment Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
