package com.midea.cloud.srm.biz.pj.sourcepubconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;

import java.util.List;

/**
 * @author huangbf3
 */
public interface ISccPjSourcePubconfigService extends IService<SccPjSourcePubconfig> {
    /**
     * 备注
     * @param sourcePubconfig
     * @param type
     * @return
     */
    public SccPjSourcePubconfig savePubconfig(SccPjSourcePubconfig sourcePubconfig, String type);

    /**
     * 备注
     * @param pubconfigIdList
     */
    public void delPubconfig(List<Long> pubconfigIdList);

    /**
     * 备注
     * @param pubconfigIdList
     */
    public void invalidPubconfig(List<Long> pubconfigIdList);

    /**
     * 备注
     * @param pubconfigIdList
     */
    public void validPubconfig(List<Long> pubconfigIdList);

    /**
     * 备注
     * @param sourcePubconfig
     * @return
     */
    public PageInfo<SccPjSourcePubconfig> queryPage(SccPjSourcePubconfig sourcePubconfig);

    /**
     * 备注
     * @param pubconfigId
     * @return
     */
    public SccPjSourcePubconfig queryPubconfig(Long pubconfigId);
}
