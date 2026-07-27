package com.midea.cloud.srm.base.orgusers.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;
import com.midea.cloud.srm.model.base.pjorganizationusers.dto.PjOrganizationUserDto;

import java.util.List;

/**
 * 备注
 * @author huangbf3
 */
public interface PjOrganizationUserService extends IService<OrganizationUser> {

    /**
     * 自动授权用户组织权限
     * @param pjOrganizationUserDtoList
     * @return
     */
    public Long autoAuthorizationOrganizationUser(List<PjOrganizationUserDto> pjOrganizationUserDtoList);
}
