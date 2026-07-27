package com.midea.cloud.srm.sou.designplans.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.sou.designplans.dto.ScheduleDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChLedger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
@Mapper
public interface LedgerMapper extends BaseMapper<SccSouChLedger> {

    /**
     * 获取集采台账列表
     * @param obj 参数
     * @return 返回值
     */
    List<SccSouChLedger> getLedgerList(@Param("obj") SccSouChLedger obj);

    /**
     * 获取集采台账列表
     * @param obj 参数
     * @return 返回值
     */
    List<ScheduleDto> getScheduleList(@Param("obj") ScheduleDto obj);
}
