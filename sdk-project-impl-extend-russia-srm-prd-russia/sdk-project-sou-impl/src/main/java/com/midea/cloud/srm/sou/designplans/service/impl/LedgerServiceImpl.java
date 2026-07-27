package com.midea.cloud.srm.sou.designplans.service.impl;

import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.model.sou.designplans.dto.ScheduleDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChLedger;
import com.midea.cloud.srm.sou.designplans.mapper.LedgerMapper;
import com.midea.cloud.srm.sou.designplans.service.LedgerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Service
public class LedgerServiceImpl extends BaseServiceImpl<LedgerMapper, SccSouChLedger> implements LedgerService {

    @Resource
    private LedgerMapper ledgerMapper;

    /**
     * 获取集采台账列表
     *
     * @param obj 参数
     * @return 返回值
     */
    @Override
    public List<SccSouChLedger> getLedgerList(SccSouChLedger obj) {
        return ledgerMapper.getLedgerList(obj);
    }

    /**
     * 获取集采台账列表
     *
     * @param obj 参数
     * @return 返回值
     */
    @Override
    public List<ScheduleDto> getScheduleList(ScheduleDto obj) {
        return ledgerMapper.getScheduleList(obj);
    }
}
