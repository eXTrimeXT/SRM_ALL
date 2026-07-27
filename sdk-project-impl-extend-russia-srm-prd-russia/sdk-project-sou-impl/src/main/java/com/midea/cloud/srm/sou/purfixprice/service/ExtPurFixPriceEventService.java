package com.midea.cloud.srm.sou.purfixprice.service;

import com.midea.cloud.srm.model.sou.purfixprice.dto.ExtPurFixPriceEditDTO;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface ExtPurFixPriceEventService {

    /**
     * 编辑定价单
     * @param param
     */
    void editFixPrice(ExtPurFixPriceEditDTO param);

    /**
     * 删除定价单
     * @param purFixPriceHeadId
     */
    void removeFixPrice(long purFixPriceHeadId);

    /**
     * 定价单审批提交
     * @param purFixPriceHeadId
     */
    void callbackAfterApprovalSubmit(long purFixPriceHeadId);

    /**
     * 定价单审批通过
     * @param purFixPriceHeadId
     */
    void callbackAfterApprovalPass(long purFixPriceHeadId);

    /**
     * 定价单审批未通过
     * @param purFixPriceHeadId
     * @param fixPriceStatus
     */
    void callbackAfterApprovalUnPass(long purFixPriceHeadId, String fixPriceStatus);

}
