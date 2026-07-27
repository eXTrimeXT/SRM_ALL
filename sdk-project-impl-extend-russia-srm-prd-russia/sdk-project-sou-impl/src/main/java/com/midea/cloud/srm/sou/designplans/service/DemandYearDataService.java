package com.midea.cloud.srm.sou.designplans.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.designplans.dto.PullQueryDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChDemandYearData;

import java.util.List;
/**
 * 备注
 * @author huangbf3
 */
public interface DemandYearDataService extends IService<SccSouChDemandYearData> {

    /**
     * 拉取数据
     * @param pullQuery 入参
     * @param type 类型
     * @param designId 提报策划方案
     * @return 返回数据
     */
    List<SccSouChDemandYearData> pullOrder(PullQueryDto pullQuery, Integer type, Long designId);
}
