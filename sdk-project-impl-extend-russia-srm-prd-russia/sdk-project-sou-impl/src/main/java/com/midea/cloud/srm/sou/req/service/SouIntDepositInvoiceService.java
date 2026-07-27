package com.midea.cloud.srm.sou.req.service;


import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.sou.req.SouIntDepositInvoice;

/**
 * 寻源单意向金开票表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-04
 */
public interface SouIntDepositInvoiceService extends BaseService<SouIntDepositInvoice> {

    /**
     * 获取最新一条开票记录
     *
     * @param record
     * @return
     */
    SouIntDepositInvoice getLatestData(SouIntDepositInvoice record);

    /**
     * 根据FromDepositInvoiceId获取对于的红字发票
     *
     * @param invoiceId
     * @return
     */
    SouIntDepositInvoice getByFromDepositInvoiceId(Long invoiceId);

    /**
     * 接口平台https://open.gwm.cn/goodsdetail?group_id=765bd9673d0a41459765f0b2d0031673&tabQueryType=0
     * 发票开具创建
     * @param invoice 寻源单意向金开票表
     * @return 发票开具创建 返回结果
     */
    JSONObject createInvoice(SouIntDepositInvoice invoice);

    /**
     * 查询（两个小时）状态为“已提交”的数据，触发查询接口《财务共享-发票结算结果查询》，有结果回写状态，开具成功or开具失败
     * @param invoice 寻源单意向金开票表
     * @return 发票开具创建 返回结果
     */
    void updateCreateInvoiceStatus(SouIntDepositInvoice invoice);
}
