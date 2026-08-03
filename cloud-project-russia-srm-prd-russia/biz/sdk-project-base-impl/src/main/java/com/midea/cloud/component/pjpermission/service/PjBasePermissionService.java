package com.midea.cloud.component.pjpermission.service;

import com.midea.cloud.srm.model.rbac.role.common.DefaultDataUserPermission;
/**
 * 备注
 * @author huangbf3
 */
public interface PjBasePermissionService {
    /**
     * 备注
     * @param dataUserPermission 参数
     * @param organizationTypeCode 参数
     * @return 返回
     */
    String getOrgIdList(DefaultDataUserPermission dataUserPermission, String organizationTypeCode);

    /**
     * 备注
     * @param userId 参数
     */
    void delRedisByUser(Long userId);
}