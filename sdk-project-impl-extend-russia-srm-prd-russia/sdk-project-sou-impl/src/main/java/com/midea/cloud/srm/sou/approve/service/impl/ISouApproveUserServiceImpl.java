package com.midea.cloud.srm.sou.approve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveUser;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.sou.approve.mapper.SouApproveUserMapper;
import com.midea.cloud.srm.sou.approve.service.ISouApproveUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description scc_npm_sou_approve_user
 * @author panmq2
 * @date 2023-10-23
 */
@Slf4j
@Service
public class ISouApproveUserServiceImpl extends ServiceImpl<SouApproveUserMapper, SouApproveUser> implements ISouApproveUserService {
    @Override
    public SouApproveUser addApproveUser(Long businessId, Long userId, String userName, String fullName) {
        LambdaQueryWrapper<SouApproveUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SouApproveUser::getBusinessId, businessId);
        queryWrapper.orderByDesc(SouApproveUser::getSortIndex);
        List<SouApproveUser> approveUserList = this.list(queryWrapper);
        SouApproveUser approveUser = new SouApproveUser();
        approveUser.setApproveUserId(IdGenrator.generate());
        approveUser.setUserId(userId);
        approveUser.setUserName(userName);
        approveUser.setFullName(fullName);
        approveUser.setBusinessId(businessId);
        approveUser.setApproveStatus(SouApprovalStatusEnum.DRAFT.name());
        approveUser.setSortIndex(1);
        if(CollectionUtils.isNotEmpty(approveUserList)) {
            approveUser.setSortIndex(ObjectUtils.defaultIfNull(approveUserList.get(0).getSortIndex(), 0) + 1);
        }
        this.save(approveUser);
        return approveUser;
    }

    @Override
    public void modifyApproveUser(SouApproveUser user) {
        this.updateById(user);
    }

    @Override
    public SouApproveUser getCurrentApproveUser(Long businessId) {
        LambdaQueryWrapper<SouApproveUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SouApproveUser::getBusinessId, businessId);
        queryWrapper.orderByAsc(SouApproveUser::getSortIndex);
        List<SouApproveUser> approveUserList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(approveUserList)) {
            return null;
        }
        Optional<SouApproveUser> firstApproveUser = approveUserList.stream().filter(u -> !SouApprovalStatusEnum.APPROVED.name().equals(u.getUserName())).findFirst();
        if(firstApproveUser.isPresent()) {
            return firstApproveUser.get();
        }
        return approveUserList.get(approveUserList.size()-1);
    }

    @Override
    public SouApproveUser getNewestApproveUser(Long businessId) {
        LambdaQueryWrapper<SouApproveUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SouApproveUser::getBusinessId, businessId);
        queryWrapper.orderByDesc(SouApproveUser::getSortIndex);
        List<SouApproveUser> approveUserList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(approveUserList)) {
            return null;
        }
        return approveUserList.get(0);
    }

    @Override
    public Map<Long, SouApproveUser> getNewestApproveUserMap(List<Long> businessIdList) {
        if(CollectionUtils.isEmpty(businessIdList)) {
            return new HashMap<>(50);
        }

        LambdaQueryWrapper<SouApproveUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SouApproveUser::getBusinessId, businessIdList);
        queryWrapper.orderByDesc(SouApproveUser::getSortIndex);
        List<SouApproveUser> approveUserList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(approveUserList)) {
            return new HashMap<>(50);
        }
        return approveUserList.stream().collect(Collectors.toMap(k -> k.getBusinessId(), Function.identity(), (k1, k2)->k1));
    }
}

