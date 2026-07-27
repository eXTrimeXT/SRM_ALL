package com.midea.cloud.srm.sou.designplans.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.sou.designplans.dto.PaaAdjustDto;
import com.midea.cloud.srm.model.sou.designplans.entity.SccSouChPaaAdjust;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ex_liuxy46
 */
@Mapper
public interface PaaAdjustMapper extends BaseMapper<SccSouChPaaAdjust> {

    /**
     * 获取集采台账-调价申请列表
     * @param obj 参数
     * @return 返回值
     */
    List<PaaAdjustDto> getAdjustList(@Param("obj") SccSouChPaaAdjust obj);
}
