package com.midea.cloud.srm.sou.inq.ext.service;

import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtPjInqSouVendorCheckDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtPjInqSouVendorDelDTO;

/**
 * 长城 - 询比价 - 立项 - 事件服务
 * @author huangbf3
 */
public interface ExtInqSouInitEventService {

    /**
     * 删除新增邀请供应商
     * @param param 参数
     */
    void removeVendor(ExtPjInqSouVendorDelDTO param);

    /**
     * checkVendor
     * @param param
     * @return
     */
    Boolean checkVendor(ExtPjInqSouVendorCheckDTO param);
}
