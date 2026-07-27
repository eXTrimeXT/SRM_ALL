package com.midea.cloud.srm.base.organization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.midea.cloud.srm.model.base.entity.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ex_liuxy46
 */
@Mapper
public interface OrganizationChildMapper extends BaseMapper<Organization> {

    /**
     * 获取列表
     * @param organization 参数
     * @return list
     */
    List<Organization> getOrganizationChildList(@Param("organization") Organization organization);
}
