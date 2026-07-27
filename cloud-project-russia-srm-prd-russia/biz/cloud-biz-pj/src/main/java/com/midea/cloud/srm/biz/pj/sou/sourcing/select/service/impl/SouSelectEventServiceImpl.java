package com.midea.cloud.srm.biz.pj.sou.sourcing.select.service.impl;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service.SouControlEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouOrderItemFollowDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouRoundDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouProcessEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemHisDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao.SouOrderResultDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao.SouSelectFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao.SouSelectOnFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.score.SouCalculateScoreManager;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.service.SouSelectEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.service.SouSelectQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.select.ApiSouSelectEventHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.select.ApiSouSelectJudgeHandler;
import com.midea.cloud.srm.model.bid.enums.BiddingApprovalStatus;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.inq.price.entity.ApprovalHeader;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.*;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.priceapproval.core.dto.PriceApprovalDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 寻源核心 - 评选
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/13
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouSelectEventServiceImpl implements SouSelectEventService {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    private SouCalculateScoreManager souCalculateScoreManager;
    @Autowired
    private SouRoundDAOImpl souRoundDao;
    @Autowired
    private SouProcessEventService souProcessEventService;
    @Autowired
    private SouOrderItemFollowDAOImpl souOrderItemFollowDao;
    @Autowired
    private SouSelectFileDAOImpl souSelectFileDao;
    @Autowired
    private SouSelectOnFileDAOImpl souSelectOnFileDao;
    @Autowired
    private SouControlEventService souControlEventService;

    @Autowired
    private SouOrderResultDAOImpl souOrderResultDao;


    /**
     * 智能评选
     *
     * @param param   智能评选信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void intelligentSelect(ApiSouIntelligentSelectDTO param, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 3: 校验操作条件/权限
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectJudgeHandler.class).judgeIntelligentSelectAuth(param.getProjectId(), souType);
        // 2: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectEventHandler.class).doHandlerBeforeIntelligentSelect(param, souType);
        // 4: 算分排名
        if (param.isNeedAutoScore()) {
            // 4.1: 查找本轮次需要评分的供应商报价信息
            if (CollectionUtils.isEmpty(param.getScoreDataList())) {
                List<SouOrderItem> orderItemList = souOrderItemDao.lambdaQuery()
                        .eq(SouOrderItem::getProjectId, param.getProjectId())
                        .eq(SouOrderItem::getRound, project.getCurrentRound())
                        .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                        .list();
                param.setScoreDataList(SouScoreDimensionContextData.convert2ContextData(orderItemList));
            }
            // 4.2: 评选
            souCalculateScoreManager.calculateAndSort(param.getProjectId(), souType, project.getOrderWay(), project.getScoreRuleType(),
                    param.getScoreDataList(), project.getScoreTemplateId());
            // 4.3: 保存数据
            List<SouOrderItem> orderItemList = SouScoreDimensionContextData.convertFromContextData(param.getScoreDataList());
            souOrderItemDao.updateBatchById(orderItemList);
        }
        // 5: 更新单价状态为评选中
        souProjectDao.lambdaUpdate()
                .set(SouProject::getProjectStatus, SouProjectStatusEnum.EVALUATING)
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
        // 6: 更新节点状态
        souProcessEventService.updateProcessNodeStatus(project.getProjectId(), SouProcessNodeEnum.businessManagement.name(), Enable.Y, souType);
        // 7: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectEventHandler.class).doHandlerAfterIntelligentSelect(param, souType);
    }

    /**
     * 入围/淘汰
     *
     * @param param   需要操作的数据
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return 受影响的报价行数据 {@link SouOrderItem#getOrderItemId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Set<Long/* orderItemId */> changeWinStatus(ApiSouChangeWinStatusDTO param, String souType) {
        // 0: 刷新数据
        if (param.getProjectId() != null) {
            souControlEventService.refreshProjectByWin(param.getProjectId());
        }
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        Set<Long> orderResultIds = new HashSet<>();
        for (ApiSouChangeSelectStatusItemDTO select : param.getSelects()) {
            souOrderResultDao.lambdaUpdate()
                    .set(SouOrderResult::getOrderRemark, select.getOrderRemark())
                    .set(SouOrderResult::getWinVendorId, select.getWinVendorId())
                    .set(SouOrderResult::getWinVendorName, select.getWinVendorName())
                    .set(SouOrderResult::getWinReason, select.getWinReason())
                    .set(SouOrderResult::getFailureBidFlag, select.getFailureBidFlag())
                    .set(SouOrderResult::getFailureReason, select.getFailureReason())
                    .eq(SouOrderResult::getOrderResultId, select.getOrderResultId())
                    .update();
        }

        List<SouSelectFile> souSelectFiles = SouObjectXUtil.convertList(param.getSelectFileList(), SouSelectFile.class);
        for (SouSelectFile souSelectFile : souSelectFiles) {
            if (souSelectFile.getSelectFileId() == null) {
                souSelectFile.setSelectFileId(IdGenrator.generate());
                souSelectFile.setProjectId(param.getProjectId());
                souSelectFile.setApprovalProcess(param.getApprovalProcess());
            }
        }
        souSelectFileDao.saveOrUpdate(param.getProjectId(), souSelectFiles, SouSelectFile::getProjectId);

        return param.getSelects().stream().map(ApiSouChangeSelectStatusItemDTO::getOrderResultId).collect(Collectors.toSet());
    }

    /**
     * 中标/落标
     * PS: 同组合下需要级联处理
     *
     * @return 受影响的报价行数据 {@link SouOrderItem#getOrderItemId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Set<Long/* orderItemId */> changeSelectStatus(ApiSouChangeSelectStatusDTO param, String souType) {
        // 0: 刷新数据
        if (param.getProjectId() != null) {
            souControlEventService.refreshProjectBySelect(param.getProjectId());
        }
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        Set<Long> orderResultIds = new HashSet<>();
        for (ApiSouChangeSelectStatusItemDTO select : param.getSelects()) {
            orderResultIds.add(select.getOrderResultId());
            //入围/淘汰
            souOrderResultDao.lambdaUpdate()
                   // .set(SouOrderResult::getSelectStatus, param.isToWin() ? SouSelectStatusEnum.WIN : SouSelectStatusEnum.FAIL)
                    .set(SouOrderResult::getSelectStatus, select.getSelectStatus() == null ? SouSelectStatusEnum.WIN : SouSelectStatusEnum.FAIL)
                    .set(SouOrderResult::getSelectRemark, select.getSelectRemark())
                    .eq(SouOrderResult::getOrderResultId, select.getOrderResultId())
                    .update();
        }

        // 5: 更新竞价单状态为已定价，编制中标结果提交，状态修改为‘中标通知’
        souProjectDao.lambdaUpdate()
                .set(SouProject::getProjectStatus, SouProjectStatusEnum.LOA)
                .eq(SouProject::getProjectId, param.getProjectId())
                .eq(SouProject::getProjectStatus, SouProjectStatusEnum.PRICING)
                .update();
        return param.getSelects().stream().map(ApiSouChangeSelectStatusItemDTO::getOrderResultId).collect(Collectors.toSet());
    }

    /**
     * 修改中标数量
     *
     * @param params  中标数量信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeWinAmount(List<ApiSouChangeWinAmountDTO> params, String souType) {
        // 1: 入参格式化
        ApiSouChangeWinAmountDTO.formatParams(params);
        // 2: 校验操作条件/权限
        List<SouOrderItem> orderItemList = SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectJudgeHandler.class)
                .judgeChangeOrderWinAmountAuth(params, souType);
        // 3: 行业包额外处理(前置)
        orderItemList = SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectEventHandler.class)
                .doHandlerBeforeChangeWinAmount(params, souType, orderItemList);
        // 4: 保存数据
        souOrderItemDao.updateBatchById(orderItemList);
        // 5: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectEventHandler.class).doHandlerAfterChangeWinAmount(params, souType, orderItemList);
    }

    /**
     * 公开本轮结果
     *
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void openResult(long projectId, String souType) {
        // 1: 校验操作条件/权限
        SouProject souProject = SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectJudgeHandler.class).judgeOpenResultAuth(projectId, souType);
        // 2: 行业包额外处理(前置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectEventHandler.class).doHandlerBeforeOpenResult(projectId, souType);
        // 3: 更新数据状态
        souRoundDao.lambdaUpdate()
                .set(SouRound::getHasPublishResult, Enable.Y)
                .set(SouRound::getPublishResultTime, new Date())
                .eq(SouRound::getProjectId, projectId)
                .eq(SouRound::getRound, souProject.getCurrentRound())
                .update();
        // 4: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectEventHandler.class).doHandlerAfterOpenResult(projectId, souType);
    }

    /**
     * 生成价格审批单
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param souType   寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ApprovalHeader createPricingApproval(long projectId, String souType) {
        // 1: 校验操作条件/权限
        List<SouOrderItem> winOrderItemList = SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectJudgeHandler.class)
                .judgeCreatePricingApproval(projectId, souType);
        // 2: 数据组装
        ApiSouCreatePricingDTO pricingDTO = SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectEventHandler.class)
                .doHandlerBeforeCreatePricing(projectId, souType, winOrderItemList);
        log.info("寻源生成价格审批单数据打印:" + JSON.toJSONString(pricingDTO));
        // 3: 调用价格中台，创建价格审批单
        ApprovalHeader approvalHeader = SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectEventHandler.class)
                .doHandlerForCreatePricing(projectId, souType, pricingDTO);
        // 4: 状态变更处理
        souProjectDao.lambdaUpdate()
                .set(SouProject::getProjectStatus, SouProjectStatusEnum.PRICING)
                .eq(SouProject::getProjectId, projectId)
                .update();
        // 4: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectEventHandler.class)
                .doHandlerAfterCreatePricing(projectId, souType, winOrderItemList, pricingDTO, approvalHeader);

        return approvalHeader;
    }

    /**
     * 新的生成价格审批单(对接回迁SRM的价格审批单)
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public PriceApprovalDTO createPricingApprovalNew(ApiSouCreatePricingApprovalDTO param) {
        // 1: 校验操作条件/权限
        List<SouOrderItem> winOrderItemList = SouActiveBeanUtils.getActiveBean(param.getSouType(), ApiSouSelectJudgeHandler.class)
                .judgeCreatePricingApprovalNew(param);
        // 2: 数据组装
        PriceApprovalDTO pricingDTO = SouActiveBeanUtils.getActiveBean(param.getSouType(), ApiSouSelectEventHandler.class)
                .doHandlerBeforeCreatePricingNew(param, winOrderItemList);
        log.info("寻源生成价格审批单数据打印:" + JSON.toJSONString(pricingDTO));
        // 3: 调用价格中台，创建价格审批单
        pricingDTO = SouActiveBeanUtils.getActiveBean(param.getSouType(), ApiSouSelectEventHandler.class)
                .doHandlerForCreatePricingNew(param, pricingDTO);
        // 4: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(param.getSouType(), ApiSouSelectEventHandler.class)
                .doHandlerAfterCreatePricingNew(param, winOrderItemList, pricingDTO);

        return pricingDTO;
    }

    /**
     * 根据价格审批单的审批情况更新寻源单状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changePricingResult(ApiSouSelectChangePricingResultDTO param, String souType) {
        // 1: 入参格式化
        param.formatParams();
        SouProject souProject = souProjectDao.getById(param.getProjectId());
        // 2: 处理招标单的目标状态
        SouProjectStatusEnum targetProjectStatus;
        if (param.getApprovalStatus() != null) {
            if (BiddingApprovalStatus.DRAFT.equals(param.getApprovalStatus())
                    || BiddingApprovalStatus.WITHDRAW.equals(param.getApprovalStatus())
                    || BiddingApprovalStatus.SUBMITTED.equals(param.getApprovalStatus())) {
                // 拟定/已撤回/已提交
                return;
            } else if (BiddingApprovalStatus.ABANDONED.equals(param.getApprovalStatus())) {
                // 已废弃
                targetProjectStatus = SouProjectStatusEnum.EVALUATING;
            } else if (BiddingApprovalStatus.REJECTED.equals(param.getApprovalStatus())) {
                // 已驳回
                targetProjectStatus = SouProjectStatusEnum.PRICE_REJECT;
            } else if (BiddingApprovalStatus.APPROVED.equals(param.getApprovalStatus())) {
                // 已审批
                targetProjectStatus = SouProjectStatusEnum.PRICE_END;
            } else {
                return;
            }
        } else if (param.isDeleted()) {
            targetProjectStatus = SouProjectStatusEnum.EVALUATING;
        } else {
            return;
        }
        // 3: 更新数据
        souProjectDao.lambdaUpdate()
                .set(SouProject::getProjectStatus, targetProjectStatus)
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
        if (SouProjectStatusEnum.EVALUATING.equals(targetProjectStatus) || SouProjectStatusEnum.PRICE_REJECT.equals(targetProjectStatus)) {
            // 更新报价明细后续单据状态为作废
            souOrderItemFollowDao.lambdaUpdate()
                    .set(SouOrderItemFollow::getFollowStatus, SouOrderItemFollowStatusEnum.INVALID)
                    .eq(SouOrderItemFollow::getProjectId, param.getProjectId())
                    .eq(SouOrderItemFollow::getFollowStatus, SouOrderItemFollowStatusEnum.VALID)
                    .update();
        }
        if (SouProjectStatusEnum.PRICE_END.equals(targetProjectStatus)) {
            // 修改"评选"节点状态
            souProcessEventService.updateProcessNodeStatus(param.getProjectId(), SouProcessNodeEnum.evaluation.name(), Enable.Y, souProject.getSouType());
        }
        // 4: 行业包额外处理(后置)
        SouActiveBeanUtils.getActiveBean(souProject.getSouType(), ApiSouSelectEventHandler.class).doHandlerAfterChangePricingResult(param, souProject.getSouType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void placeOnFile(ApiSouPlaceOnFileDTO param, String souType) {
        // 1: 入参格式化
        param.formatParams();
        // 2: 校验操作条件/权限
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectJudgeHandler.class)
                .judgePlaceOnFileStatusAuth(param, souType);
        // 3: 归档
        //保存归档附件
        List<SouSelectPlaceOnFile> souSelectPlaceOnFileList = SouObjectXUtil.convertList(param.getPlaceOnFileList(), SouSelectPlaceOnFile.class);
        for (SouSelectPlaceOnFile souSelectPlaceOnFile : souSelectPlaceOnFileList) {
            if (souSelectPlaceOnFile.getFileId() == null) {
                souSelectPlaceOnFile.setFileId(IdGenrator.generate());
            }
        }
        souSelectOnFileDao.saveOrUpdate(param.getProjectId(), souSelectPlaceOnFileList, SouSelectPlaceOnFile::getProjectId);
        //更新竞价单状态
        souProjectDao.lambdaUpdate()
                .set(SouProject::getProjectStatus, SouProjectStatusEnum.FILE)
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
    }

    /**
     * 中标通知
     *
     * @param param
     * @param souType
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeProjectStatus(ApiSouChangeSelectStatusDTO param, String souType) {
        // 1: 入参格式化
        param.formatParams();

        for (ApiSouChangeSelectStatusItemDTO select : param.getSelects()) {
            souOrderResultDao.lambdaUpdate()
//                    .set(SouOrderResult::getWinNoticeStatus, SouApprovalStatusEnum.SUBMITTED)
                    .set(SouOrderResult::getWinNoticeRemark, select.getWinNoticeRemark())
                    .eq(SouOrderResult::getOrderResultId, select.getOrderResultId())
                    .update();
        }
        //更新竞价单状态
        souProjectDao.lambdaUpdate()
                //.set(SouProject::getProjectStatus, AuctSouProjectStatusEnum.LOA) // 用原来的字典 lcw
                .set(SouProject::getProjectStatus, SouProjectStatusEnum.LOA)
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
    }
}
