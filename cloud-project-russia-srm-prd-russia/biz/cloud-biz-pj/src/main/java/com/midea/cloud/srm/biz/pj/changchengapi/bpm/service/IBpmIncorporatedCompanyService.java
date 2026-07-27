package com.midea.cloud.srm.biz.pj.changchengapi.bpm.service;

import com.midea.cloud.common.service.BaseService;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmIncorporatedCompany;
import com.midea.cloud.srm.model.pj.changchengapi.dto.BpmIncorporatedCompanyParam;
import com.midea.cloud.srm.model.pj.changchengapi.dto.BpmIncorporatedCompanyResultDto;
import com.midea.cloud.srm.model.pj.changchengapi.material.MaterialResultDto;

/**
 * bpm法务-法人公司主数据接口类
 * @author huangbf3
 */
public interface IBpmIncorporatedCompanyService extends BaseService<BpmIncorporatedCompany> {

    /**
     * 拉取物料
     * @param param 法务-法人公司主数据接接口参数
     */
    void pullData(BpmIncorporatedCompanyParam param);

    /**
     * 从获取的法务-法人公司接口数据新增或更新入库
     * @param resultDto 接口返回的法务-法人公司数据
     */
    void saveOrUpdateFromApi(BpmIncorporatedCompanyResultDto resultDto);
}
