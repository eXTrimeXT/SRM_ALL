package com.midea.cloud.srm.sou.bid.init.service.impl;

import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.approve.service.impl.ApproveComCallBackServiceImpl;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.init.starttechbids.ApiExtStartTechBidEditHandler;
import com.midea.cloud.srm.sou.sourcing.spi.init.starttechbids.ExtStartTechBidEditPO;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import com.midea.cloud.srm.sou.timertasks.enums.TimerTaskTypeEnum;
import com.midea.cloud.srm.sou.timertasks.service.SrmNpmSouTimerTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
/**
 * 寻源项目申请审批
 * @author huangbf3
 */
@Service(value = "SOU_PROJECT_APPLY")
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ExtBidSouProjectApproveCallbackImpl extends ApproveComCallBackServiceImpl {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Autowired
    private IExtSouRoundService roundService;

    @Autowired
    private IExtSouTechScoreHeadService techScoreHeadService;

    @Autowired
    private IExtSouMarginService marginService;

    @Autowired
    private IExtSouPlanService planService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private SrmNpmSouTimerTaskService srmNpmSouTimerTaskService;

    @Override
    public Long submit(Long businessId) {
        ExtSouProject souProject = projectService.getById(businessId);
        if(Objects.isNull(souProject)) {
            return businessId;
        }
        souProject.setProjectStatus(SouBiddingProStatusEnum.DRAW_UP.getCode());
        souProject.setCreateApprovalStatus(SouApprovalStatusEnum.SUBMITTED.name());
        projectService.updateById(souProject);
        return businessId;
    }

    @Override
    public Long reject(Long businessId) {
        ExtSouProject souProject = projectService.getById(businessId);
        if(Objects.isNull(souProject)) {
            return businessId;
        }
        souProject.setProjectStatus(SouBiddingProStatusEnum.DRAW_UP.getCode());
        souProject.setCreateApprovalStatus(SouApprovalStatusEnum.REJECTED.name());
        projectService.updateById(souProject);
        return businessId;
    }

    @Override
    public Long withdraw(Long businessId) {
        ExtSouProject souProject = projectService.getById(businessId);
        if(Objects.isNull(souProject)) {
            return businessId;
        }
        souProject.setProjectStatus(SouBiddingProStatusEnum.DRAW_UP.getCode());
        souProject.setCreateApprovalStatus(SouApprovalStatusEnum.WITHDRAW.name());
        projectService.updateById(souProject);
        return businessId;
    }

    public Long recommvendoPass(ExtSouProject project, List<RecommvendorDto> recommvendorDtoList) {

        //生成投标信息
        ExtStartTechBidEditPO po = SouActiveBeanUtils.getActiveBean(SouTypeEnum.bid.name(), ApiExtStartTechBidEditHandler.class).doExtendAndValidVendor(project, recommvendorDtoList, SouTypeEnum.bid.name());
        //插入供应商
        if(CollectionUtils.isNotEmpty(po.getVendorList())) {
            vendorService.saveOrUpdateBatch(po.getVendorList());
        }

        //插入投标数据
        if(CollectionUtils.isNotEmpty(po.getSouOrderList())) {
            orderService.saveOrUpdateBatch(po.getSouOrderList());
        }

        //保存报价明细
        if(CollectionUtils.isNotEmpty(po.getSouOrderItemList())) {
            orderItemService.saveOrUpdateBatch(po.getSouOrderItemList());
        }

        //保存供应商保证金
        if(CollectionUtils.isNotEmpty(po.getSouMarginList())) {
            marginService.saveOrUpdateBatch(po.getSouMarginList());
        }

        //审批通过后后置处理
        SouActiveBeanUtils.getActiveBean(SouTypeEnum.bid.name(), ApiExtStartTechBidEditHandler.class).doHandlerSouProjectAsAfterFlowPass(project.getProjectId(), SouTypeEnum.bid.name(), po);

        return project.getProjectId();
    }

    @Override
    public Long pass(Long businessId) {

        //生成投标信息
        ExtStartTechBidEditPO po = SouActiveBeanUtils.getActiveBean(SouTypeEnum.bid.name(), ApiExtStartTechBidEditHandler.class).doFormatevalidAndConvert(businessId, SouTypeEnum.bid.name());

        ExtSouProject souProject = po.getSouProject();

        //保存报价单
        if(CollectionUtils.isNotEmpty(po.getSouOrderList())) {
            orderService.saveOrUpdateBatch(po.getSouOrderList());
        }
        //保存报价明细
        if(CollectionUtils.isNotEmpty(po.getSouOrderItemList())) {
            orderItemService.saveOrUpdateBatch(po.getSouOrderItemList());
        }
        //保存轮次
        if(!Objects.isNull(po.getRound())) {
            roundService.saveOrUpdate(po.getRound());
            srmNpmSouTimerTaskService.listeningTask(businessId, TimerTaskTypeEnum.OPEN_TECHNICAL_BID.name(), po.getRound().getOrderEndTime());
        }
        //保存技术标
        if(CollectionUtils.isNotEmpty(po.getTechScoreHeadList())) {
            techScoreHeadService.saveOrUpdateBatch(po.getTechScoreHeadList());
        }

        //保存供应商保证金
        if(CollectionUtils.isNotEmpty(po.getSouMarginList())) {
            marginService.saveOrUpdateBatch(po.getSouMarginList());
        }

        souProject.setCreateApprovalStatus(SouApprovalStatusEnum.APPROVED.name());
        souProject.setPublishTime(new Date());
        projectService.updateById(souProject);

        //更新实际发布时间
        planService.applyAtualPoint(souProject.getProjectId(), souProject.getPublishTime(), ExtSouPlan::getPublishTime);

        //审批通过后后置处理
        SouActiveBeanUtils.getActiveBean(SouTypeEnum.bid.name(), ApiExtStartTechBidEditHandler.class).doHandlerSouProjectAsAfterFlowPass(businessId, SouTypeEnum.bid.name(), po);

        return businessId;
    }


}
