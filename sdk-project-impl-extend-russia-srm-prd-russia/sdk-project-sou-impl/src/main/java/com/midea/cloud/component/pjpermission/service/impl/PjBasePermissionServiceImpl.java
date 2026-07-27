package com.midea.cloud.component.pjpermission.service.impl;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.feign.BasePermissionExtClient;
import com.midea.cloud.srm.model.rbac.role.common.DefaultDataUserPermission;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
/**
 * 备注
 * @author huangbf3
 */
@Service
public class PjBasePermissionServiceImpl {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private BasePermissionExtClient basePermissionExtClient;

    private static final String REDIS_KEY = "SRM-USER-ORG-PERMISSION";

    private Map<String, Integer> orgLevelMap = new HashMap<>(16);

    public PjBasePermissionServiceImpl() {
        orgLevelMap.put("GROUP", 1);
        orgLevelMap.put("BU ", 2);
        orgLevelMap.put("OU", 3);
        orgLevelMap.put("INV", 4);
        orgLevelMap.put("DEP", 5);
    }

    private String checkRedisBeforeFeign(String organizationTypeCode){
        LoginAppUser user = AppUserUtil.getLoginAppUser();
        String orgKey = REDIS_KEY + user.getUserId() + "-" + organizationTypeCode;
        String orgListStr = (String) redisUtil.get(orgKey);
        if (StringUtils.isNotEmpty(orgListStr)) {
            return orgListStr;
        }
        return null;
    }
    /**
     * 获取公司列表
     *
     * @param dataUserPermission
     * @return
     */
    public String getOuIdList(DefaultDataUserPermission dataUserPermission) {
        String orgListStr = checkRedisBeforeFeign("OU");
        return StringUtils.isNotEmpty(orgListStr) ? orgListStr : basePermissionExtClient.getOuList();
    }

    /**
     * 获取集团权限
     *
     * @param dataUserPermission
     * @return
     */
    public String getGroupIdList(DefaultDataUserPermission dataUserPermission) {
        String orgListStr = checkRedisBeforeFeign("GROUP");
        return StringUtils.isNotEmpty(orgListStr) ? orgListStr :basePermissionExtClient.getGroupList();
    }

    /**
     * 获取板块权限
     *
     * @param dataUserPermission
     * @return
     */
    public String getBuIdList(DefaultDataUserPermission dataUserPermission) {
        String orgListStr = checkRedisBeforeFeign("BU");
        return StringUtils.isNotEmpty(orgListStr) ? orgListStr :basePermissionExtClient.getBuList();
    }

    /**
     * 获取部门权限
     *
     * @param dataUserPermission
     * @return
     */
    public String getDepIdList(DefaultDataUserPermission dataUserPermission) {
        String orgListStr = checkRedisBeforeFeign("DEP");
        return StringUtils.isNotEmpty(orgListStr) ? orgListStr :basePermissionExtClient.getDepList();
    }

}