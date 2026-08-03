package com.midea.cloud.srm.base.extusers.service;

import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
public interface ExtUserService {
    /**
     * 备注
     * @param organizationUserList  参数
     * @return 返回
     */
    public List<OrganizationUser> initOrgnizationUser(List<OrganizationUser> organizationUserList);
}
