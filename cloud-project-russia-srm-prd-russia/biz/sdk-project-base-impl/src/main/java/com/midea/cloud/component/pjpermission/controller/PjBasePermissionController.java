package com.midea.cloud.component.pjpermission.controller;

import com.midea.cloud.component.pjpermission.service.PjBasePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 备注
 * @author huangbf3
 */
@RestController
@RequestMapping("/base-anon/permission")
@Slf4j
public class PjBasePermissionController {
    @Autowired
    private PjBasePermissionService pjBasePermissionService;

    /**
     * 测试接口
     * @param typeCode
     */
    @GetMapping(value = "/test1")
    public void test1(@RequestParam("typeCode") String typeCode) {
        pjBasePermissionService.getOrgIdList(null, typeCode);
    }

    /**
     * 删除缓存
     * @param userId
     */
    @GetMapping(value = "/delRedisByUser")
    public void delRedisByUser(@RequestParam("userName") Long userId) {
        pjBasePermissionService.delRedisByUser(userId);
    }

    /**
     * 获取集团列表
     */
    @GetMapping(value = "/getGroupList")
    public String getGroupList() {
        return pjBasePermissionService.getOrgIdList(null, "GROUP");
    }

    /**
     * 获取板块列表
     */
    @GetMapping(value = "/getBuList")
    public String getBuList() {
        return pjBasePermissionService.getOrgIdList(null, "BU");
    }

    /**
     * 获取公司列表
     */
    @GetMapping(value = "/getOuList")
    public String getOuList() {
        return pjBasePermissionService.getOrgIdList(null, "OU");
    }

    /**
     * 获取部门列表
     */
    @GetMapping(value = "/getDepList")
    public String getDepList() {
        return pjBasePermissionService.getOrgIdList(null, "DEP");
    }
}

