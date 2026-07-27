package com.midea.cloud.common.permission;

import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;
import com.midea.cloud.srm.model.rbac.role.common.DefaultDataUserPermission;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Component
public class ExtBasePermissionService {

    private static final String ID_NOT_EXIST = "-1000";


    public String getDepIdList(DefaultDataUserPermission dataUserPermission) {
        return getOrgIdList(dataUserPermission, "DEP");
    }


    private String getOrgIdList(DefaultDataUserPermission dataUserPermission, String organizationTypeCode) {
        LoginAppUser user = dataUserPermission.getLoginAppUser();
        Map<Long, Organization> cacheOrganization = dataUserPermission.getCacheOrganization();
        List<Long> orgIdList = new ArrayList();

        for(int j = 0; j < user.getOrganizationUsers().size(); ++j) {
            OrganizationUser orgUser = (OrganizationUser)user.getOrganizationUsers().get(j);
            if (orgUser != null) {
                Organization organization = (Organization)cacheOrganization.get(orgUser.getOrganizationId());
                if (organization != null) {
                    String userOrgType = organization.getOrganizationTypeCode();
                    if (organizationTypeCode.equals(userOrgType)) {
                        orgIdList.add(organization.getOrganizationId());
                    }
                }
            }
        }

        if (CollectionUtils.isEmpty(orgIdList)) {
            return ID_NOT_EXIST;
        } else {
            return StringUtils.join(orgIdList, ",");
        }
    }


}