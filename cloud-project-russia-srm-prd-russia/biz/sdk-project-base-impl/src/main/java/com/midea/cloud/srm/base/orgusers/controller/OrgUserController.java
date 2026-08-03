package com.midea.cloud.srm.base.orgusers.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.base.orgusers.mapper.OrgUserMapper;
import com.midea.cloud.srm.base.orgusers.service.PjOrganizationUserService;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationUser;
import com.midea.cloud.srm.model.base.pjorganizationusers.dto.PjOrganizationUserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@RestController
@RequestMapping("/orgUser")
@Slf4j
public class OrgUserController {
    @Resource
    private OrgUserMapper orgUserMapper;

    @Autowired
    private PjOrganizationUserService pjOrganizationUserService;

    private static final Integer NUM = 100;

    @PostMapping(value = "/insertOrUpdateOrgUser")
    public List<OrganizationUser> insertOrUpdateOrgUser(@RequestBody List<OrganizationUser> infoList) {
        if (CollectionUtils.isNotEmpty(infoList)) {
            try {
                if (infoList.size() <= NUM) {
                    pjOrganizationUserService.saveOrUpdateBatch(infoList);
                } else {
                    for (int i = 0; i < infoList.size() / NUM + (infoList.size() % NUM == 0 ? 0 : 1); i++) {
                        pjOrganizationUserService.saveOrUpdateBatch(infoList.subList(i * NUM, Math.min(i * NUM + NUM, infoList.size())));
                    }
                }
                Long userId = infoList.get(0).getUserId();
                if (userId == null) {
                    throw new BaseException("用户不存在");
                }
                Set<Long> orgUserRel = new HashSet<>();
                Set<Long> infoOrgUserRel = new HashSet<>();
                List<OrganizationUser> ouList = pjOrganizationUserService.list(new LambdaQueryWrapper<OrganizationUser>().eq(OrganizationUser::getUserId, userId));
                ouList.forEach(e -> orgUserRel.add(e.getOrganizationUserRelId()));
                infoList.forEach(e -> {
                    if (e.getOrganizationUserRelId() != null) {
                        infoOrgUserRel.add(e.getOrganizationUserRelId());
                    }
                });
                List<Long> difference = orgUserRel.stream().filter(element -> !infoOrgUserRel.contains(element)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(difference)) {
                    pjOrganizationUserService.removeBatchByIds(difference);
                }
            } catch (Exception e) {
                throw new BaseException(e.getMessage());
            }
        }
        return infoList;
    }

    @PostMapping(value = "/getOrganizationUserList")
    public PageInfo<OrganizationUser> getOrganizationUserList(@RequestBody OrganizationUser ou) {
        PageUtil.startPage(ou.getPageNum(), ou.getPageSize());
        List<OrganizationUser> list = orgUserMapper.listOrganUserByParam(ou.getUserId());
        return new PageInfo<>(list);
    }

    /**
     * 自动授权用户组织权限
     * @param pjOrganizationUserDtoList
     * @return
     */
    @PostMapping("/autoAuthorizationOrganizationUser")
    public Long autoAuthorizationOrganizationUser(@RequestBody List<PjOrganizationUserDto> pjOrganizationUserDtoList) {
        try {
            return pjOrganizationUserService.autoAuthorizationOrganizationUser(pjOrganizationUserDtoList);
        } catch (Exception e) {
            log.error("autoAuthorizationOrganizationUser Exception", e);
            throw new BaseException();
        }
    }
}
