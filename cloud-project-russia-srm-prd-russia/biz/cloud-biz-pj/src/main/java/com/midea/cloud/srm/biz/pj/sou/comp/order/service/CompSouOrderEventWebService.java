package com.midea.cloud.srm.biz.pj.sou.comp.order.service;

import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.order.ApiCompSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderCancelDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderWithdrawDTO;

/**
 * 竞价 - 报价业务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
public interface CompSouOrderEventWebService {

    /**
     *
     * 提供一个计算公式报价结果的接口，让界面体验更友好
     *
     * @param souItemId     物料需求行ID
     * @param currency      供应商选择的报价币种
     * @param taxKey        供应商选择的税率
     * @param formulaResult 公式报价
     * @param vendorId      供应商ID
     * @return
     */
    ApiCompSouOrderItemVO computeFormulaPrice(long souItemId, String currency, String taxKey, String formulaResult, long vendorId);

    /**
     * 暂存/提交报价
     * @param param 参数
     * @return
     */
    long/* orderId */ editOrder(ApiCompSouOrderDTO param);

    /**
     * 备注
     * @param param 参数
     * @return
     */
    long/* orderId */ initOrder(ApiCompSouOrderDTO param);

    /**
     * 供应商：撤回报价
     * @param param 参数
     */
    void withdrawOrder(ApiSouOrderWithdrawDTO param);

    /**
     * 作废报价
     * @param param 参数
     */
    void cancelOrder(ApiSouOrderCancelDTO param);

}
