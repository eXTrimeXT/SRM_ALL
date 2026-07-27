package com.midea.cloud.srm.sou.approve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveOperate;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveUser;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.sou.approve.mapper.SouApproveOperateMapper;
import com.midea.cloud.srm.sou.approve.service.ApproveCallBackService;
import com.midea.cloud.srm.sou.approve.service.ISouApproveOperateService;
import com.midea.cloud.srm.sou.approve.service.ISouApproveUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @description scc_npm_sou_approve_operate
 * @author panmq2
 * @date 2023-10-23
 */
@Slf4j
@Service
public class ISouApproveOperateServiceImpl extends ServiceImpl<SouApproveOperateMapper, SouApproveOperate> implements ISouApproveOperateService {
    @Autowired
    private ISouApproveUserService approveUserService;

    @Autowired
    private Map<String, ApproveCallBackService> callBackServiceMap;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SouApproveOperate operate(Long businessId, SouApprovalStatusEnum type, String description, String callbackBean) {
        SouApproveUser currentApproveUser = approveUserService.getCurrentApproveUser(businessId);
        if(Objects.isNull(currentApproveUser)) {
            return null;
        }
        List<SouApprovalStatusEnum> createOperate = Arrays.asList(SouApprovalStatusEnum.SUBMITTED, SouApprovalStatusEnum.WITHDRAW);
//        if(!createOperate.contains(type) && !AppUserUtil.getLoginAppUser().getUsername().equals(currentApproveUser.getUserName())) {
//            throw new BaseException("非当前审批人不允许进行该操作!");
//        }

        LambdaQueryWrapper<SouApproveOperate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SouApproveOperate::getApproveUserId, currentApproveUser.getApproveUserId());
        queryWrapper.orderByDesc(SouApproveOperate::getSortIndex);
        PageUtil.startPage(1, 1);
        List<SouApproveOperate> operateList = this.list(queryWrapper);

        SouApproveOperate operate = new SouApproveOperate();
        operate.setApproveOperateId(IdGenrator.generate());
        operate.setOperate(type.name());
        operate.setDescrption(description);
        operate.setApproveUserId(currentApproveUser.getApproveUserId());
        operate.setSortIndex(1);
        if(CollectionUtils.isNotEmpty(operateList)) {
            operate.setSortIndex(ObjectUtils.defaultIfNull(operateList.get(0).getSortIndex(), 0) +1);
        }


        //回调
        if(callBackServiceMap.containsKey(callbackBean)) {
            switch (type) {
                case SUBMITTED:
                    callBackServiceMap.get(callbackBean).submit(businessId);
                    break;
                case APPROVED:
                    callBackServiceMap.get(callbackBean).pass(businessId);
                    break;
                case REJECTED:
                    callBackServiceMap.get(callbackBean).reject(businessId);
                    break;
                case WITHDRAW:
                    callBackServiceMap.get(callbackBean).withdraw(businessId);
                    break;
                default:;
            }
            callBackServiceMap.get(callbackBean).extendOperation(businessId, type, new HashMap<>(50));
        }

        this.save(operate);

        currentApproveUser.setApproveStatus(type.name());
        approveUserService.modifyApproveUser(currentApproveUser);

        return operate;
    }

    @Override
    public SouApproveOperate getNewestOperate(Long approveUserId) {
        LambdaQueryWrapper<SouApproveOperate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SouApproveOperate::getApproveUserId, approveUserId);
        queryWrapper.orderByDesc(SouApproveOperate::getSortIndex);
        PageUtil.startPage(1, 1);
        List<SouApproveOperate> approveOperates = this.list(queryWrapper);
        if(CollectionUtils.isNotEmpty(approveOperates)) {
            return approveOperates.get(0);
        }
        return null;
    }
}

