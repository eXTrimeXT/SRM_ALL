package com.midea.cloud.srm.sou.purinq.service;

import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ExtPurInqSouVendorQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouVendorDel;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
public interface ExtPurInqSouInitQueryService {

    /**
     * 查看被删除的邀请供应商
     * @param queryParam
     * @return
     */
    List<ExtPurInqSouVendorDel> queryVendorDel(ExtPurInqSouVendorQueryDTO queryParam);

}
