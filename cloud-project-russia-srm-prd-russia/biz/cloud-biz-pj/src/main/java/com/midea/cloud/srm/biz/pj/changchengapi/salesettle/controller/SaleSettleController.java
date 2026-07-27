package com.midea.cloud.srm.biz.pj.changchengapi.salesettle.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.changchengapi.salesettle.service.CcApiSettleAcountingService;
import com.midea.cloud.srm.biz.pj.changchengapi.salesettle.service.ISaleSettleService;
import com.midea.cloud.srm.model.pj.ccapiinvoices.dto.CcApiInvoiceCreateRequestDto;
import com.midea.cloud.srm.model.pj.ccapiinvoices.dto.CcApiInvoiceCreateResponseDto;
import com.midea.cloud.srm.model.pj.ccapisettleacountings.dto.ApiSettleAcountingRequestDto;
import com.midea.cloud.srm.model.pj.ccapisettleacountings.dto.ApiSettleAcountingResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangbf3
 * 销售结算中心控制类
 */
@Slf4j
@RestController
@RequestMapping("/pj-anon/saleSettle")
public class SaleSettleController {
    @Autowired
    private ISaleSettleService iSaleSettleService;

    @Autowired
    private CcApiSettleAcountingService ccApiSettleAcountingService;

    /**
     * 接口平台https://open.gwm.cn/goodsdetail?group_id=765bd9673d0a41459765f0b2d0031673&tabQueryType=0
     * 发票开具创建
     * @param param 发票开具创建请求参数
     * @return 发票开具创建 返回结果
     */
    @PostMapping("/createInvoice")
    public JSONObject createInvoice(@RequestBody JSONObject param) {
        try {
            CcApiInvoiceCreateRequestDto requestDto = JSON.parseObject(JSON.toJSONString(param), CcApiInvoiceCreateRequestDto.class);
            CcApiInvoiceCreateResponseDto responseDto = iSaleSettleService.createInvoiceSimple(requestDto);
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(responseDto));
            return jsonObject;
        } catch (Exception e) {
            log.error("createInvoice Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 接口平台https://open.gwm.cn/goodsdetail?group_id=765bd9673d0a41459765f0b2d0031673&tabQueryType=0
     * 结算结果查询
     * @param param 结算结果查询请求参数
     * @return 结算结果查询 返回结果
     */
    @PostMapping("/settleResult")
    public JSONObject settleResult(@RequestBody JSONObject param) {
        return iSaleSettleService.settleResult(param);
    }

    @ApiOperation("发票开具创建  封装版")
    @PostMapping("/createInvoiceSimple")
    public CcApiInvoiceCreateResponseDto createInvoiceSimple(@RequestBody CcApiInvoiceCreateRequestDto requestDto) {
        try {
            return iSaleSettleService.createInvoiceSimple(requestDto);
        } catch (Exception e) {
            log.error("createInvoiceSimple Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    @ApiOperation("结算记账 销售结算记账接口，完成会计记账  封装版")
    @PostMapping("/accounting")
    public ApiSettleAcountingResponseDto accounting(@RequestBody ApiSettleAcountingRequestDto requestDto) {
        try {
            return ccApiSettleAcountingService.accounting(requestDto);
        } catch (Exception e) {
            log.error("accounting Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}