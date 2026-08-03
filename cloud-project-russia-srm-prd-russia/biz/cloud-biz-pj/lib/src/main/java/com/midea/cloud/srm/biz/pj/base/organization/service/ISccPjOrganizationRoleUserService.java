package com.midea.cloud.srm.biz.pj.base.organization.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.pj.base.organization.entity.SccPjOrganizationRoleUser;

/**
 * @author huangbf3
 * 组织角色员工实现类
 */
public interface ISccPjOrganizationRoleUserService extends IService<SccPjOrganizationRoleUser> {
    /**
     * 备注
     * @param sccPjOrganizationRoleUser
     * @return
     */
    PageInfo<SccPjOrganizationRoleUser> listPage(SccPjOrganizationRoleUser sccPjOrganizationRoleUser);

}
