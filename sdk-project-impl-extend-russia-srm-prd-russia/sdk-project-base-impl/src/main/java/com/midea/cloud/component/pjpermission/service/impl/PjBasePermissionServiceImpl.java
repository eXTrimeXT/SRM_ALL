package com.midea.cloud.component.pjpermission.service.impl;

import com.midea.cloud.common.annotation.CacheData;
import com.midea.cloud.common.constants.RedisKey;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.BeanMapUtils;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.component.pjpermission.service.PjBasePermissionService;
import com.midea.cloud.srm.base.organization.mapper.PjBasePermissionMapper;
import com.midea.cloud.srm.base.organization.service.IOrganizationService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;
import com.midea.cloud.srm.model.rbac.role.common.DefaultDataUserPermission;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class PjBasePermissionServiceImpl implements PjBasePermissionService {

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private RedisUtil redisUtil;

    private static final long CACHE_TIME = 3600 * 6L;
    private static final String ID_NOT_EXIST = "-1000";

    private static final String REDIS_KEY = "SRM-USER-ORG-PERMISSION";

    @Autowired
    private IOrganizationService organizationService;

    @Autowired
    private PjBasePermissionMapper pjBasePermissionMapper;

    private Map<String, Integer> orgLevelMap = new HashMap<>(16);

    public PjBasePermissionServiceImpl() {
        orgLevelMap.put("GROUP", 1);
        orgLevelMap.put("BU ", 2);
        orgLevelMap.put("OU", 3);
        orgLevelMap.put("INV", 4);
        orgLevelMap.put("DEP", 5);
    }

    /**
     * 获取公司列表
     *
     * @param dataUserPermission
     * @return
     */
    public String getOuIdList(DefaultDataUserPermission dataUserPermission) {
        return getOrgIdList(dataUserPermission, "OU");
    }

    /**
     * 获取集团权限
     *
     * @param dataUserPermission
     * @return
     */
    public String getGroupIdList(DefaultDataUserPermission dataUserPermission) {
        return getOrgIdList(dataUserPermission, "GROUP");
    }

    /**
     * 获取板块权限
     *
     * @param dataUserPermission
     * @return
     */
    public String getBuIdList(DefaultDataUserPermission dataUserPermission) {
        return getOrgIdList(dataUserPermission, "BU");
    }

    /**
     * 获取部门权限
     *
     * @param dataUserPermission
     * @return
     */
    public String getDepIdList(DefaultDataUserPermission dataUserPermission) {
        return getOrgIdList(dataUserPermission, "DEP");
    }


    @Override
    public String getOrgIdList(DefaultDataUserPermission dataUserPermission, String organizationTypeCode) {
        LoginAppUser user = AppUserUtil.getLoginAppUser();

        String orgKey = REDIS_KEY + user.getUserId() + "-" + organizationTypeCode;
        String orgListStr = (String) redisUtil.get(orgKey);
        if (StringUtils.isNotEmpty(orgListStr)) {
            return orgListStr;
        }
        List<Long> orgIdList = new ArrayList<>();
        List<Organization> permissionIdList = new ArrayList<>();

        List<OrganizationUser> organizationUsers = user.getOrganizationUsers();
        if (CollectionUtils.isNotEmpty(organizationUsers)) {
            // 1.先获取当前对应类型的组织权限
            List<Long> totalOrgIdList = organizationUsers.stream().map(OrganizationUser::getOrganizationId).collect(Collectors.toList());
            List<Organization> organizations = organizationService.listByIds(totalOrgIdList);
            List<Organization> currentTypeTempList = organizations.stream().filter(item -> organizationTypeCode.equals(item.getOrganizationTypeCode())).collect(Collectors.toList());
            permissionIdList.addAll(currentTypeTempList);
            // 2.根据其他组织,分别向下和向上获取到对应层级的组织权限
            List<Organization> listUpList = listUp(permissionIdList, organizations, organizationTypeCode);
            List<Organization> listDownList = listDown(permissionIdList, organizations, organizationTypeCode);
            if (CollectionUtils.isNotEmpty(permissionIdList)) {
                Set<Long> orgIdSet = permissionIdList.stream().map(Organization::getOrganizationId).collect(Collectors.toSet());
                orgIdList = new ArrayList<>(orgIdSet);
            }
        } else {
            return ID_NOT_EXIST;
        }
        if (CollectionUtils.isEmpty(orgIdList)) {
            // 肯定不存在
            return ID_NOT_EXIST;
        }
        String result = StringUtils.join(orgIdList, ",");
        redisUtil.set(orgKey, result, CACHE_TIME);
        return result;
    }

    @Override
    public void delRedisByUser(Long userId) {
        String tempKey = REDIS_KEY + userId;
        for (String key : orgLevelMap.keySet()) {
            redisUtil.del(tempKey + "-" + key);
        }
    }

    private List<Organization> listUp(List<Organization> permissionIdList, List<Organization> otherTempList, String organizationTypeCode) {
        if (CollectionUtils.isEmpty(otherTempList)) {
            return permissionIdList;
        }
        List<Long> orgIdList = otherTempList.stream().map(Organization::getOrganizationId).collect(Collectors.toList());
        List<Organization> organizations = pjBasePermissionMapper.listUp(orgIdList);
        if (CollectionUtils.isNotEmpty(organizations)) {
            List<Organization> currentTypeTempList = organizations.stream().filter(item -> organizationTypeCode.equals(item.getOrganizationTypeCode())).collect(Collectors.toList());
            permissionIdList.addAll(currentTypeTempList);
            List<Organization> nextRoundList = new ArrayList<>();
            for (Organization organization : organizations) {
                if(orgLevelMap.containsKey(organization.getOrganizationTypeCode())
                        && orgLevelMap.get(organization.getOrganizationTypeCode()) >= orgLevelMap.get(organizationTypeCode)){
                    nextRoundList.add(organization);
                }
            }
            return listUp(permissionIdList, nextRoundList, organizationTypeCode);
        } else {
            return permissionIdList;
        }
    }

    private List<Organization> listDown(List<Organization> permissionIdList, List<Organization> otherTempList, String organizationTypeCode) {
        if (CollectionUtils.isEmpty(otherTempList)) {
            return permissionIdList;
        }
        List<Long> orgIdList = otherTempList.stream().map(Organization::getOrganizationId).collect(Collectors.toList());
        List<Organization> organizations = pjBasePermissionMapper.listDown(orgIdList);
        if (CollectionUtils.isNotEmpty(organizations)) {
            List<Organization> currentTypeTempList = organizations.stream().filter(item -> organizationTypeCode.equals(item.getOrganizationTypeCode())).collect(Collectors.toList());
            permissionIdList.addAll(currentTypeTempList);
            List<Organization> nextRoundList = new ArrayList<>();
            for (Organization organization : organizations) {
                if(orgLevelMap.containsKey(organization.getOrganizationTypeCode())
                        && orgLevelMap.get(organization.getOrganizationTypeCode()) <= orgLevelMap.get(organizationTypeCode)){
                    nextRoundList.add(organization);
                }
            }
            return listDown(permissionIdList, nextRoundList, organizationTypeCode);
        } else {
            return permissionIdList;
        }
    }

    @CacheData(keyName = RedisKey.Base.ALL_ORGANIZATION, cacheTime = 3600 * 6, interfaceName = "获取全部组织树")
    public Map<Long, Organization> getCacheOrganization() {
        List<Organization> allOrgList = this.baseClient.listAllOrganization();
        Map<Long, Organization> cacheOrganization = BeanMapUtils.list2Map(allOrgList, Organization::getOrganizationId);
        return cacheOrganization;
    }
}