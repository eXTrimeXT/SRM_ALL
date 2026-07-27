package com.midea.cloud.srm.sou.fixprice.service;

import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceHeadDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceLineCancelDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceLinePassDTO;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
/**
 * 备注
 * @author huangbf3
 */
public interface ExtFixPriceEventService {

    /**
     * 编辑定价单
     * @param param 参数
     * @return 返回
     */
    ExtFixPriceHeadDTO editFixPrice(ExtFixPriceHeadDTO param);

    /**
     * 删除定价单
     * @param fixPriceHeadId 参数
     */
    void deleteFixPrice(long fixPriceHeadId);

    /**
     * 定价单审批提交
     * @param fixPriceHeadId 参数
     */
    void callbackAfterApprovalSubmit(long fixPriceHeadId);

    /**
     * 定价单审批通过
     * @param fixPriceHeadId 参数
     */
    void callbackAfterApprovalPass(long fixPriceHeadId);

    /**
     * 定价单审批未通过
     * @param fixPriceHeadId 参数
     * @param fixPriceStatus 参数
     */
    void callbackAfterApprovalUnPass(long fixPriceHeadId, String fixPriceStatus);

    /**
     * 定价明细审批通过/未通过
     * @param param 参数
     */
    void fixPriceLinePass(ExtFixPriceLinePassDTO param);

    /**
     * 关闭
     * @param priceLine 参数
     */
    void closeFixPriceLine(ExtFixPriceLine priceLine);

    /**
     * 取消
     * @param param 参数
     */
    void cancelFixPriceLine(ExtFixPriceLineCancelDTO param);

}
