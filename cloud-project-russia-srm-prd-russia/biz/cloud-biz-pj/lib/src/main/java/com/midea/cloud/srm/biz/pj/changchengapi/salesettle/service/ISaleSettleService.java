package com.midea.cloud.srm.biz.pj.changchengapi.salesettle.service;


import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.model.pj.ccapiinvoices.dto.CcApiInvoiceCreateRequestDto;
import com.midea.cloud.srm.model.pj.ccapiinvoices.dto.CcApiInvoiceCreateResponseDto;

/**
 * 发票开具 接口
 * @author huangbf3
 */
public interface ISaleSettleService {
    /**
     * 接口平台https://open.gwm.cn/goodsdetail?group_id=765bd9673d0a41459765f0b2d0031673&tabQueryType=0
     * 发票开具创建
     * @param param 发票开具创建请求参数
     * @return 发票开具创建 返回结果
     */
    JSONObject createInvoice(JSONObject param);

    /**
     * 接口平台https://open.gwm.cn/goodsdetail?group_id=765bd9673d0a41459765f0b2d0031673&tabQueryType=0
     * 结算结果查询
     * @param param 结算结果查询请求参数
     * @return 结算结果查询 返回结果
     */
    JSONObject settleResult(JSONObject param);

    /**
     * 发票开具创建  封装版
     * @param requestDto
     * @return
     */
    CcApiInvoiceCreateResponseDto createInvoiceSimple(CcApiInvoiceCreateRequestDto requestDto);
}
