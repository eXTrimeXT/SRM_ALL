package com.midea.cloud.srm.base.organization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author luxc18
 */
@Mapper
public interface PjBasePermissionMapper extends BaseMapper<Organization> {

    /**
     * 向上获取上一级的数据
     * @param orgIdList
     * @return
     */
    List<Organization> listUp(@Param("list") List<Long> orgIdList);

    /**
     * 向下获取下一级的数据
     * @param orgIdList
     * @return
     */
    List<Organization> listDown(@Param("list") List<Long> orgIdList);


}
