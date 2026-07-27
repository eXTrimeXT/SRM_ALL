package com.midea.cloud.srm.sou.bid.turntos.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.sou.bidturns.dto.NpmSouBidTurnRquestParamDto;
import com.midea.cloud.srm.model.sou.enums.ExtSouGroupRoleEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.sou.bid.turntos.service.NpmSouBidTurnToService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouGroupService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @Author: panmq
 * @Date: 2024/04/07/ $
 * @Description: 招标负责人转办实现类
 */
@Service
@Slf4j
public class NpmSouBidTurnToServiceImpl implements NpmSouBidTurnToService {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouGroupService groupService;

    @Override
    public Long turnBidPricipal(NpmSouBidTurnRquestParamDto paramDto) {

        if(ObjectUtils.anyNull(paramDto.getProjectId(), paramDto.getUserId(), paramDto.getUserName(), paramDto.getFullName())) {
            throw new BaseException("请求参数有误（单据ID或者账号信息为空）!");
        }

        ExtSouProject project = projectService.getById(paramDto.getProjectId());

        if(!Arrays.asList(SouBiddingProStatusEnum.DRAW_UP.getCode(),
                SouBiddingProStatusEnum.TECH_BID.getCode(),
                SouBiddingProStatusEnum.TECH_BID_END.getCode(),
                SouBiddingProStatusEnum.TECH_BID_OPEN.getCode(),
                SouBiddingProStatusEnum.TECH_BID_EVA.getCode(),
                SouBiddingProStatusEnum.TECH_BID_EVA_DONE.getCode(),
                SouBiddingProStatusEnum.BUS_BID.getCode(),
                SouBiddingProStatusEnum.BUS_BID_END.getCode(),
                SouBiddingProStatusEnum.BUS_BID_OPEN.getCode()).contains(project.getProjectStatus())) {

            throw new BaseException("当前处于定标流程后的状态，不允许转办招标负责人！");
        }

        /** 更新招标项目创建人 */
        projectService.update(new LambdaUpdateWrapper<ExtSouProject>().set(ExtSouProject::getCreatedBy, paramDto.getUserName())
                .set(ExtSouProject::getCreatedId, paramDto.getUserId())
                .set(ExtSouProject::getCreatedFullName, paramDto.getFullName())
                .eq(ExtSouProject::getProjectId, paramDto.getProjectId()));

        /** 更新招标项目招标负责人 */
        groupService.update(new LambdaUpdateWrapper<ExtSouGroup>().set(ExtSouGroup::getUserId, paramDto.getUserId())
                .set(ExtSouGroup::getUserName, paramDto.getUserName())
                .set(ExtSouGroup::getFullName, paramDto.getFullName())
                .eq(ExtSouGroup::getProjectId, paramDto.getProjectId())
                .eq(ExtSouGroup::getGroupRole, ExtSouGroupRoleEnum.PRINCIPAL.getCode()));

        return paramDto.getProjectId();
    }
}
