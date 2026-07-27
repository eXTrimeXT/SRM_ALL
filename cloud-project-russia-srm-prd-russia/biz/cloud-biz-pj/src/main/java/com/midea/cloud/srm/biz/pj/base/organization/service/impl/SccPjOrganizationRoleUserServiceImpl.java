package com.midea.cloud.srm.biz.pj.base.organization.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.biz.pj.base.organization.mapper.SccPjOrganizationRoleUserMapper;
import com.midea.cloud.srm.biz.pj.base.organization.service.ISccPjOrganizationRoleUserService;
import com.midea.cloud.srm.model.pj.base.organization.entity.SccPjOrganizationRoleUser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huangbf3
 * 组织角色实现类
 */
@Service
public class SccPjOrganizationRoleUserServiceImpl extends ServiceImpl<SccPjOrganizationRoleUserMapper, SccPjOrganizationRoleUser> implements ISccPjOrganizationRoleUserService {


    @Override
    public PageInfo<SccPjOrganizationRoleUser> listPage(SccPjOrganizationRoleUser sccPjOrganizationRoleUser) {
        PageUtil.startPage(sccPjOrganizationRoleUser.getPageNum(),sccPjOrganizationRoleUser.getPageSize());
        QueryWrapper<SccPjOrganizationRoleUser> queryWrapper = new QueryWrapper<>(sccPjOrganizationRoleUser);
        List<SccPjOrganizationRoleUser> list = list(queryWrapper);
        return new PageInfo<>(list);
    }
}
