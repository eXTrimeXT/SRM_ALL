package com.midea.cloud.srm.sou.purinq.service;

import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ExtPurInqSouVendorDelDTO;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface ExtPurInqSouInitEventService {

    /**
     * 删除新增供应商
     * @param param
     */
    void removeVendor(ExtPurInqSouVendorDelDTO param);

}
