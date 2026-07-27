package com.midea.cloud.srm.sou.ccapipayments.service;

import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.model.pj.sccpjcmscallbacktemps.entity.SccPjCmsCallbackTemp;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouMarginRecord;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/04/09/ $
 * @Description: 财务共享-批量付款及自动提交审批-业务触发-接口
 */
public interface CcApiPaymentWithBusinessService {

    /**
     * 保证金扣款或者退款
     * @param type
     * @param marginRecordList
     */
    public void callApiPaymentWithMargin(String type, List<ExtSouMarginRecord> marginRecordList);

    /**
     * 意向金退款申请
     * @param souIntDepositRefundList
     */
    public void callApiPaymentWithDepositRefund(List<Record> souIntDepositRefundList);

    /**
     * callBackAsApiPayment
     * @param sccPjCmsCallbackTempList
     */
    public void callBackAsApiPayment(List<SccPjCmsCallbackTemp> sccPjCmsCallbackTempList);

}
