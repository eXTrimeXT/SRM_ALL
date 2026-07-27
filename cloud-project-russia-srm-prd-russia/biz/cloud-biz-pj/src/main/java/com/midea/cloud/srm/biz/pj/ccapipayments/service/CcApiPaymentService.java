package com.midea.cloud.srm.biz.pj.ccapipayments.service;

import com.midea.cloud.srm.model.pj.ccapipayments.dto.ApiPaymentRequestDto;
import com.midea.cloud.srm.model.pj.ccapipayments.dto.ApiPaymentResponseDto;

/**
 * @Author: panmq
 * @Date: 2024/04/02/ $
 * @Description: 长城财务接口
 */
public interface CcApiPaymentService {

    /**
     * 批量付款及自动提交审批
     * 文档地址：https://open.gwm.cn/goodsdetail?group_id=ac51cb7f39c347f8957ba1f94102b0f7&tabQueryType=0
     * @param apiPaymentRequestDto
     * @return
     */
    ApiPaymentResponseDto saveOutSourceOneVo(ApiPaymentRequestDto apiPaymentRequestDto);
}
