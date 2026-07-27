package com.midea.cloud.srm.sou.inq.ext.service;

import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqCloseItemParams;

import java.util.Set;

/**
 * 备注
 * @author huangbf3
 */
public interface ExtInqSouSelectEventService {

    /**
     * 总价比价
     * @param projectId 参数
     */
    void totalPriceCompare(long projectId);

    /**
     * 结束询价
     * @param projectId 参数
     */
    void finishSou(long projectId);

    /**
     * 关闭物料需求
     * @param souItemIds 参数
     */
    void closeSouItems(Set<Long> souItemIds);

    /**
     * 关闭物料需求
     * @param params 参数
     */
    void closeSouItems(ExtInqCloseItemParams params);

    /**
     * draftCloseSouItems
     * @param souItemIds
     */
    void draftCloseSouItems(Set<Long> souItemIds);
}
