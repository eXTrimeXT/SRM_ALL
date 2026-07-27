package com.midea.cloud.srm.sou.purinq.service;

import java.util.Set;

/**
 * @author 100014337
 */
public interface ExtPurInqSouSelectEventService {

    /**
     * 总价比价
     * @param  projectId
     */
    void totalPriceCompare(long projectId);

    /**
     * 结束询价
     * @param  projectId
     */
    void finishSou(long projectId);

}
