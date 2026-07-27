package com.midea.cloud.srm.biz.pj.ccapipayments.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.ccapipayments.service.CcApiPaymentService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.ccapipayments.dto.ApiPaymentRequestDto;
import com.midea.cloud.srm.model.pj.ccapipayments.dto.ApiPaymentResponseDto;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description:
 */
@RestController
@RequestMapping("/ccApi/payment")
@Slf4j
@Api("财务接口-控制类")
public class CcApiPaymentController extends BaseController {

    @Autowired
    private CcApiPaymentService ccApiPaymentService;

    /**
     * 批量付款及自动提交审批
     * 文档地址：https://open.gwm.cn/goodsdetail?group_id=ac51cb7f39c347f8957ba1f94102b0f7&tabQueryType=0
     * @param apiPaymentRequestDto
     * @return
     */
    @PostMapping("/saveOutSourceOneVo")
    ApiPaymentResponseDto saveOutSourceOneVo(@RequestBody ApiPaymentRequestDto apiPaymentRequestDto) {
        try {
            return ccApiPaymentService.saveOutSourceOneVo(apiPaymentRequestDto);
        } catch (Exception e) {
            log.error("saveOutSourceOneVo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }


}
