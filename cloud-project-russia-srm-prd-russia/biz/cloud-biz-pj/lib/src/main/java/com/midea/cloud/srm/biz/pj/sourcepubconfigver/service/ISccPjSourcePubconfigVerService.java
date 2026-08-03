package com.midea.cloud.srm.biz.pj.sourcepubconfigver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;
import com.midea.cloud.srm.model.pj.sourcepubconfigvers.entity.SccPjSourcePubconfigVer;

import java.util.List;

/**
 * @author huangbf3
 */
public interface ISccPjSourcePubconfigVerService extends IService<SccPjSourcePubconfigVer> {
    /**
     * 备注
     * @param sourcePubconfig
     * @return
     */
    public SccPjSourcePubconfigVer saveSccPjSourcePubconfigVer(SccPjSourcePubconfig sourcePubconfig);

    /**
     * 备注
     * @param sccPjSourcePubconfigList
     * @return
     */
    public List<SccPjSourcePubconfigVer> saveSccPjSourcePubconfigVerBatch(List<SccPjSourcePubconfig> sccPjSourcePubconfigList);

}
