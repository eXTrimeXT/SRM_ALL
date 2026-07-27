package com.midea.cloud.srm.sou.designplans.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.sou.designplans.dto.ScheduleDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChLedger;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
public interface LedgerService extends IService<SccSouChLedger> {

    /**
     * 获取集采台账列表
     * @param obj 参数
     * @return 返回值
     */
    List<SccSouChLedger> getLedgerList(SccSouChLedger obj);

    /**
     * 获取集采台账列表
     * @param obj 参数
     * @return 返回值
     */
    List<ScheduleDto> getScheduleList(ScheduleDto obj);
}
