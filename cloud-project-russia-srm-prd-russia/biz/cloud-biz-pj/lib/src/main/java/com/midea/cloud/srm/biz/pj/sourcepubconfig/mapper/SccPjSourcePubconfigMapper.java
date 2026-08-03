package com.midea.cloud.srm.biz.pj.sourcepubconfig.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.midea.cloud.component.mphelper.mapper.CustomMapper;
import com.midea.cloud.srm.model.pj.sourcepubconfig.entity.SccPjSourcePubconfig;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author huangbf3
 */
public interface SccPjSourcePubconfigMapper extends CustomMapper<SccPjSourcePubconfig> {
    /**
     * 备注
     * @param queryWrapper
     * @return
     */
    @Select("SELECT c.* FROM scc_pj_source_pubconfig c \n" +
            "JOIN scc_pj_source_pubconfig_ver v ON c.CONFIG_NUMBER = v.CONFIG_NUMBER  AND c.CONFIG_VER = v.CONFIG_VER ${ew.customSqlSegment}")
    List<SccPjSourcePubconfig> queryPage(@Param("ew") QueryWrapper<SccPjSourcePubconfig> queryWrapper);
}
