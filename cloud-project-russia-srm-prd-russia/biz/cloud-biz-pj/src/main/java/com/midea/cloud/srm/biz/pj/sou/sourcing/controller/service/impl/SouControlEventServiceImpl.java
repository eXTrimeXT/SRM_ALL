package com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service.impl;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service.SouControlEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.ApiSouControlEventHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.ApiSouControlJudgeHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.itemrecord.ApiSouItemRecordHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.itemrecord.SouItemRecordPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.itemrefresh.ApiSouItemRefreshHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.itemrefresh.SouItemRefreshPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.vendoradd.ApiSouVendorAddHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.vendoradd.SouVendorAddPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.vendorrecord.ApiSouVendorAddRecordHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control.vendorrecord.SouVendorRecordPO;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderEventHandler;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.typehandler.SouPwdInfoVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 寻源核心 - 业务控制 - 业务事件服务
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/05
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class SouControlEventServiceImpl implements SouControlEventService {

    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouRoundDAOImpl souRoundDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    private SouProcessNodeDAOImpl souProcessNodeDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SouItemRecordDAOImpl souItemRecordDao;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SouVendorAuthDAOImpl souVendorAuthDao;
    @Autowired
    private SouVendorRecordDAOImpl souVendorRecordDao;

    public static final String SOU_ITEM_REFRESH_LOCK = "SOU_ITEM_REFRESH_LOCK_";
    public static final String SOU_VENDOR_ADD_LOCK = "SOU_VENDOR_ADD_LOCK_";

    /**
     * 修改报价开始时间(立即开始/延迟开始)
     *
     * @param param   报价修改信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeOrderStartTime(ApiSouChangeOrderStartTimeDTO param, String souType) {
        /* 1: 入参格式化 */
        param.formatParams();
        /* 2: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeChangeOrderStartTime(param, souType);
        /* 3: 行业包额外处理(前置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeChangeOrderStartTime(param, souType);
        /* 4: 更新数据 */
        Date startTime = param.isStartNow() ? new Date() : param.getOrderStartTime();
        souProjectDao.lambdaUpdate()
                .set(SouProject::getOrderStartTime, startTime)
                .set(SouProject::getProjectStatus, param.isStartNow() ?
//                                接收报价中  报价未开始
                        SouProjectStatusEnum.ACCEPT_ORDER : SouProjectStatusEnum.ORDER_NOT_START)
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
        souRoundDao.lambdaUpdate()
                .set(SouRound::getOrderStartTime, startTime)
                .eq(SouRound::getProjectId, param.getProjectId())
                .update();
        /* 5: 行业包额外处理(后置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterChangeOrderStartTime(param, souType);
    }

    /**
     * 修改报价截止时间(立即截止/延迟报价)
     *
     * @param param   报价时间修改信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeOrderEndTime(ApiSouChangeOrderEndTimeDTO param, String souType) {
        /* 1: 入参格式化 */
        param.formatParams();
        /* 2: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeChangeOrderEndTimeAuth(param, souType);
        /* 3: 行业包额外处理(前置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeChangeOrderEndTime(param, souType);
        /* 4: 更新数据 */
        Date endTime = param.isEndNow() ? new Date() : param.getOrderEndTime();
        souProjectDao.lambdaUpdate()
                .set(SouProject::getOrderEndTime, endTime)
                .set(SouProject::getProjectStatus, param.isEndNow() ?
                        //接收报价中 - 接受报价中
                        SouProjectStatusEnum.ORDER_END : SouProjectStatusEnum.ACCEPT_ORDER)
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
        souRoundDao.lambdaUpdate()
                .set(SouRound::getOrderEndTime, endTime)
                .set(SouRound::getChangeOrderEndTimeReason, param.getChangeOrderEndTimeReason())
                .eq(SouRound::getProjectId, param.getProjectId())
                .update();
        /* 5: 更新节点信息 */
        souProcessNodeDao.lambdaUpdate()
                .set(SouProcessNode::getNodeStatus, param.isEndNow() ? Enable.Y : Enable.N)
                .eq(SouProcessNode::getProjectId, param.getProjectId())
                .eq(SouProcessNode::getProcessNode, SouProcessNodeEnum.bidingControl)
                .update();
        /* 6: 行业包额外处理(后置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterChangeOrderEndTime(param, souType);
    }

    /**
     * 最早开标时间修改（仅未开标可前可修改）
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeEarliestBusinessOpenTime(ApiSouChangeEarliestBusinessOpenTimeDTO param, String souType) {
        /* 1: 参数格式化 */
        param.formatParams();
        /* 2: 校验操作条件/权限 */
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class)
                .judgeChangeEarliestBusinessOpenTimeAuth(param, souType);
        /* 3: 行业包额外处理(前置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeChangeEarliestBusinessOpenTime(param, souType);
        /* 4: 更新数据 */
        souProjectDao.lambdaUpdate()
                .set(SouProject::getEarliestBusinessOpenTime, param.getEarliestBusinessOpenTime())
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
        souRoundDao.lambdaUpdate()
                .set(SouRound::getEarliestBusinessOpenTime, param.getEarliestBusinessOpenTime())
                .eq(SouRound::getProjectId, param.getProjectId())
                .eq(SouRound::getRound, project.getCurrentRound())
                .update();
        /* 5: 行业包额外处理 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterChangeEarliestBusinessOpenTime(param, souType);
    }

    /**
     * 生成开标密码
     *
     * @param param   请求数据
     * @param souType 寻源类型
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void generateBidPwd(ApiSouBidPwdGenerateDTO param, String souType) {
        /* 1: 入参格式化 */
        param.formatParams();
        /* 2: 校验操作条件/权限 */
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeGenerateBidPwdAuth(param, souType);
        /* 3: 行业包额外处理(前置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeGenerateBidPwd(param, souType);
        /* 4: 生成开标密码 */
        Map<String/* operateAuth */, SouPwdInfoVO> pwdMap = SouPwdInfoVO.convertVO(SouActiveBeanUtils
                .getActiveBean(souType, ApiSouControlEventHandler.class)
                .doHandlerForGenerateOpenBidPwd(param, souType));
        /* 5: 保存数据 */
        SouRound currentRound = souRoundDao.lambdaQuery()
                .eq(SouRound::getProjectId, param.getProjectId())
                .eq(SouRound::getRound, project.getCurrentRound())
                .one();
        if (currentRound.getOpenPwdInfo() == null) {
            currentRound.setOpenPwdInfo(pwdMap);
        } else {
            currentRound.getOpenPwdInfo().putAll(pwdMap);
        }
        souRoundDao.updateById(currentRound);
        /* 6: 行业包额外处理(后置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterGenerateBidPwd(param, souType, pwdMap);
    }

    /**
     * 确认开标密码
     *
     * @param param   确认信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void confirmOpeningBid(ApiSouOpenBidDTO param, String souType) {
        /* 1: 入参格式化 */
        param.formatParams();
        /* 2: 校验操作条件/权限 */
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeConfirmOpeningBid(param, souType);
        /* 3: 行业包额外处理(前置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeConformOpeningBid(param, souType);
        /* 4: 确认开标密码 */
        SouRound currentRound = souRoundDao.lambdaQuery()
                .eq(SouRound::getProjectId, param.getProjectId())
                .eq(SouRound::getRound, project.getCurrentRound())
                .one();
        AssertUtils.notNull(currentRound.getOpenPwdInfo(), "还未生成开标密码");
        SouPwdInfoVO pwdInfo = currentRound.getOpenPwdInfo().get(param.getOpenBidType());
        AssertUtils.notNull(pwdInfo, "还未生成开标密码");
        AssertUtils.isTrue(pwdInfo.getOpenPwd().equals(param.getPwd()), "开标密码错误");
        /* 5: 行业包额外处理(后置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterConformOpeningBid(param, souType);
    }


    /**
     * 商务开标
     *
     * @param param   开标信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void businessOpen(ApiSouBusinessOpenDTO param, String souType) {
        /* 1: 入参格式化 */
        param.formatParams();
        /* 2: 校验操作条件/权限 */
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class)
                .judgeBusinessOpenAuth(param.getProjectId(), param.getCurrentUserId(), souType);
        /* 3: 行业包额外处理(前置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeBusinessOpen(param, souType);
        /* 4: 商务开标密码 */
        if (project.getNeedPwdOperations() != null && project.getNeedPwdOperations().contains(SouGroupOperateAuthEnum.SOU_BUSINESS_OPEN.name())) {
            AssertUtils.notNull(param.getOpenPwd(), "请输入商务开标密码");
            this.confirmOpeningBid(new ApiSouOpenBidDTO(project.getProjectId(), SouGroupOperateAuthEnum.SOU_BUSINESS_OPEN.name(), param.getOpenPwd()), souType);
        }
        /* 5: 商务开标 */
        souProjectDao.lambdaUpdate()
                //商务评选
                .set(SouProject::getProjectStatus, SouProjectStatusEnum.BUSINESS_EVAL)
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
        souRoundDao.lambdaUpdate()
                .set(SouRound::getBusinessOpen, Enable.Y)
                .set(SouRound::getBusinessOpenTime, new Date())
                .eq(SouRound::getProjectId, param.getProjectId())
                .eq(SouRound::getRound, project.getCurrentRound())
                .update();
        /* 6: 计算本轮最低/最高xx价 */
        {
            BigDecimal currentRoundNotaxMinPrice;
            BigDecimal currentRoundTaxMinPrice;
            BigDecimal currentRoundNotaxMaxPrice;
            BigDecimal currentRoundTaxMaxPrice;

            Map<Long/* souItemId */, List<SouOrderItem>> orderItemMap = souOrderItemDao.lambdaQuery()
                    .eq(SouOrderItem::getProjectId, param.getProjectId())
                    .eq(SouOrderItem::getRound, project.getCurrentRound())
                    .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                    .list().stream().collect(Collectors.groupingBy(SouOrderItem::getSouItemId));
            for (List<SouOrderItem> orderItemList : orderItemMap.values()) {
                currentRoundNotaxMinPrice = null;
                currentRoundTaxMinPrice = null;
                currentRoundNotaxMaxPrice = null;
                currentRoundTaxMaxPrice = null;
                for (SouOrderItem orderItem : orderItemList) {
                    if (currentRoundNotaxMinPrice == null || orderItem.getStandardNotaxPrice().compareTo(currentRoundNotaxMinPrice) < 0) {
                        currentRoundNotaxMinPrice = orderItem.getStandardNotaxPrice();
                    }
                    if (currentRoundTaxMinPrice == null || orderItem.getStandardTaxPrice().compareTo(currentRoundTaxMinPrice) < 0) {
                        currentRoundTaxMinPrice = orderItem.getStandardTaxPrice();
                    }
                    if (currentRoundNotaxMaxPrice == null || orderItem.getStandardNotaxPrice().compareTo(currentRoundNotaxMaxPrice) > 0) {
                        currentRoundNotaxMaxPrice = orderItem.getStandardNotaxPrice();
                    }
                    if (currentRoundTaxMaxPrice == null || orderItem.getStandardTaxPrice().compareTo(currentRoundTaxMaxPrice) > 0) {
                        currentRoundTaxMaxPrice = orderItem.getStandardTaxPrice();
                    }
                }
                souOrderItemDao.lambdaUpdate()
                        .set(SouOrderItem::getStandardNotaxMinPrice, currentRoundNotaxMinPrice)
                        .set(SouOrderItem::getStandardTaxMinPrice, currentRoundTaxMinPrice)
                        .set(SouOrderItem::getStandardNotaxMaxPrice, currentRoundNotaxMaxPrice)
                        .set(SouOrderItem::getStandardTaxMaxPrice, currentRoundTaxMaxPrice)
                        .in(SouOrderItem::getOrderItemId, orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                        .update();
            }
        }
        /* 7: 行业包额外处理 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterBusinessOpen(param, souType);
    }

    /**
     * 报价解密
     *
     * @param param   解密信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void decryptPrice(ApiSouDecryptPriceDTO param, String souType) {
        param.formatParams();
        /* 1: 校验操作条件/权限 */
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeDecryptPriceAuth(param, souType);
        /* 2: 行业包额外处理(前置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeDecryptPrice(param, souType);
        /* 3: 报价解密 */
        souRoundDao.lambdaUpdate()
                .set(SouRound::getPriceDecrypt, Enable.Y)
                .set(SouRound::getPriceDecryptTime, new Date())
                .eq(SouRound::getProjectId, param.getProjectId())
                .eq(SouRound::getRound, project.getCurrentRound())
                .update();
        /* 4: 行业包额外处理 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterDecryptPrice(param, souType);
    }

    /**
     * 发起新一轮
     *
     * @param param   发起新一轮填写的信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void startNewRound(ApiSouStartNewRoundDTO param, String souType) {
        /* 1: 入参格式化 */
        param.formatParams();
        /* 2: 校验操作条件/权限 */
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeStartNewRoundAuth(param, souType);
        /* 3: 行业包额外处理(前置) */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeStartNewRound(param, souType);
        /* 4: 发起新一轮 */
        souProjectDao.lambdaUpdate()
                .set(SouProject::getOrderStartTime, param.getOrderStartTime())
                .set(SouProject::getOrderEndTime, param.getOrderEndTime())
                .set(SouProject::getCurrentRound, project.getCurrentRound() + 1)
                .set(SouProject::getEarliestBusinessOpenTime, param.getEarliestBusinessOpenTime())
                .set(SouProject::getProjectStatus, param.isStartNow() ?
                        SouProjectStatusEnum.ACCEPT_ORDER : SouProjectStatusEnum.ORDER_NOT_START)
                .eq(SouProject::getProjectId, param.getProjectId())
                .update();
        /* 5: 新增轮次信息 */
        SouRound round = new SouRound();
        {
//            ID
            round.setRoundId(IdGenrator.generate());
//            寻源单ID
            round.setProjectId(param.getProjectId());
//            轮次
            round.setRound(project.getCurrentRound() + 1);
//            报价开始时间
            round.setOrderStartTime(param.getOrderStartTime());
//            报价截止时间
            round.setOrderEndTime(param.getOrderEndTime());
//            是否已公开本轮结果
            round.setHasPublishResult(Enable.N);
            int count;
            {
                count = (int) souOrderItemDao.lambdaQuery()
                        .eq(SouOrderItem::getProjectId, param.getProjectId())
                        .eq(SouOrderItem::getRound, project.getCurrentRound())
                        .eq(SouOrderItem::getWinStatus, SouWinStatusEnum.Y)
                        .list()
                        .stream().map(SouOrderItem::getVendorId).count();
                count += param.getNewVendors() != null ? param.getNewVendors().size() : 0;
            }
//            本轮应报价供应商数量
            round.setInviteCount(count);
//            已报价供应商数量
            round.setOrderCount(0);
//            商务开标标识
            round.setBusinessOpen(Enable.N);
//            报价解密标识
            round.setPriceDecrypt(Enable.N);
//            开标密码信息
            round.setOpenPwdInfo(new HashMap<>(50));
//            最早开标时间
            round.setEarliestBusinessOpenTime(param.getEarliestBusinessOpenTime());
        }
        souRoundDao.save(round);
        /* 6: 新增供应商 */
        if (Enable.Y.equals(project.getAllowNewVendors())) {
            if (CollectionUtils.isNotEmpty(param.getNewVendors())) {
                /* 6.1: 记录供应商追加信息 */
                ApiSouVendorRecordDTO vendorRecordDTO = new ApiSouVendorRecordDTO();
                {
                    vendorRecordDTO.setProjectId(param.getProjectId());
                    vendorRecordDTO.setVendorList(param.getNewVendors());
                }
                this.recordSouVendorAddInfo(vendorRecordDTO, souType);
            }
            /* 6.2: 执行供应商追加信息 */
            ApiSouVendorAddDTO vendorAddDTO = new ApiSouVendorAddDTO();
            {
                vendorAddDTO.setProjectId(param.getProjectId());
                vendorAddDTO.setAuthList(new ArrayList<>(32));
                if (CollectionUtils.isEmpty(param.getNewVendors())) {
                    param.setNewVendors(new ArrayList<>(16));
                }
                param.getNewVendors().forEach(vendor -> {
                    if (CollectionUtils.isNotEmpty(vendor.getAuthList())) {
                        vendor.getAuthList().forEach(auth -> auth.setVendorId(vendor.getVendorId()));
                        vendorAddDTO.getAuthList().addAll(vendor.getAuthList());
                    }
                });
            }
            this.executeSouVendorAdd(vendorAddDTO, souType);
        }
        /* 7: 更新本轮的应/已报价供应商数量 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouOrderEventHandler.class).doHandlerForOrderCountWhileOrder(param.getProjectId());
        /* 8: 行业包额外处理 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterStartNewRound(param, souType);
    }

    /**
     * 根据寻源的时间节点信息，调整寻源单的状态等信息
     *
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void refreshProjectBySouTime(long projectId) {
        /* 尝试加锁 */
        String key = "SOU_REFRESH_LOCK_" + projectId;
        boolean locked = redisUtil.tryLockInTime(
                key,
//                过期时间
                30,
//                获取锁的等待时间
                1,
//                重试次数
                1);
        if (!locked) {
            return;
        }
        try {
            SouProject project = souProjectDao.getById(projectId);
            if (project == null) {
                return;
            }
            Date now = new Date();

            /* 1: 根据报名截止时间进行判断 */
            if (SouProjectStatusEnum.ACCEPT_SIGN_UP.equals(project.getProjectStatus())) {
                /* 报名中，如果已到报名截止时间，则自动修改招标单状态，并修改节点状态 */
                if (project.getSignUpEndTime() != null && !project.getSignUpEndTime().after(now)) {
                    souProjectDao.lambdaUpdate()
                            .set(SouProject::getProjectStatus, SouProjectStatusEnum.SIGN_UP_END)
                            .eq(SouProject::getProjectId, projectId)
                            .eq(SouProject::getProjectStatus, SouProjectStatusEnum.ACCEPT_SIGN_UP)
                            .update();
                }
                return;
            }

            /* 2: 根据报价开始/截止时间进行判断 */
            boolean needChangeOrderStatus = SouProjectStatusEnum.SIGN_UP_END.equals(project.getProjectStatus())
                    || SouProjectStatusEnum.ORDER_NOT_START.equals(project.getProjectStatus())
                    || SouProjectStatusEnum.ACCEPT_ORDER.equals(project.getProjectStatus());
            if (needChangeOrderStatus) {
                if (project.getOrderEndTime().before(now)) {
                    /* 当前已达到投标截止时间(同时修改流程节点状态) */
                    souProjectDao.lambdaUpdate()
                            .set(SouProject::getProjectStatus, SouProjectStatusEnum.ORDER_END)
                            .eq(SouProject::getProjectId, projectId)
                            .update();
                } else /*noinspection StatementWithEmptyBody */
                    if (project.getOrderStartTime().before(now)) {
                        /* 当前已达到投标开始时间(同时修改流程节点状态) */
                        souProjectDao.lambdaUpdate()
                                .set(SouProject::getProjectStatus, SouProjectStatusEnum.ACCEPT_ORDER)
                                .set(SouProject::getCurrentRound, project.getCurrentRound())
                                .eq(SouProject::getProjectId, projectId)
                                .update();
                    } else {
                        /* 当前未到投标开始时间(不做任何处理) */
                    }
            }
        } finally {
            redisUtil.unLock(key);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void refreshProjectByWin(long projectId) {
        /* 尝试加锁 */
        String key = "SOU_REFRESH_LOCK_" + projectId;
        boolean locked = redisUtil.tryLockInTime(
                key,
//                过期时间
                30,
//                获取锁的等待时间
                1,
//                重试次数
                1);
        if (!locked) {
            return;
        }
        try {
            SouProject project = souProjectDao.getById(projectId);
            if (project == null) {
                return;
            }
            Date now = new Date();
            /* 1: 根据竞价状态进行判断,代替定时器功能 */
            if (SouProjectStatusEnum.ACCEPT_ORDER.equals(project.getProjectStatus())) {
                /* 报价中，如果已到报价截止时间，则自动修改招标单状态，并修改节点状态 */
                if (!project.getOrderEndTime().after(now)) {
                    souProjectDao.lambdaUpdate()
                            .set(SouProject::getProjectStatus, SouProjectStatusEnum.ORDER_END)
                            .eq(SouProject::getProjectId, projectId)
                            .eq(SouProject::getProjectStatus, SouProjectStatusEnum.ACCEPT_ORDER)
                            .update();
                }
                return;
            }
            /* 2: 评选列表查询，如已经截至报价则修改竞价单状态为评选中 */
            if (SouProjectStatusEnum.ORDER_END.equals(project.getProjectStatus())) {
                souProjectDao.lambdaUpdate()
                        .set(SouProject::getProjectStatus, SouProjectStatusEnum.PRICING)
                        .set(SouProject::getCurrentRound, project.getCurrentRound())
                        .eq(SouProject::getProjectId, projectId)
                        .eq(SouProject::getProjectStatus, SouProjectStatusEnum.ORDER_END)
                        .update();
            }
        } finally {
            redisUtil.unLock(key);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void refreshProjectBySelect(long projectId) {
        /* 尝试加锁 */
        String key = "SOU_REFRESH_LOCK_" + projectId;
        boolean locked = redisUtil.tryLockInTime(
                key,
//                过期时间
                30,
//                获取锁的等待时间
                1,
//                重试次数
                1);
        if (!locked) {
            return;
        }
        try {
            SouProject project = souProjectDao.getById(projectId);
            if (project == null) {
                return;
            }
            Date now = new Date();
            /* 1: 根据竞价状态进行判断，代替评选流程自动通过审批 */
            if (SouProjectStatusEnum.EVALUATING.equals(project.getProjectStatus())) {
                /* 如果是评选中，则自动修改招标单状态，并修改节点状态 */
                souProjectDao.lambdaUpdate()
                        .set(SouProject::getProjectStatus, SouProjectStatusEnum.PRICING)
                        .eq(SouProject::getProjectId, projectId)
                        .eq(SouProject::getProjectStatus, SouProjectStatusEnum.EVALUATING)
                        .update();
            }
        } finally {
            redisUtil.unLock(key);
        }
    }

    /**
     * 记录物料变更情况
     *
     * @param param   新的物料需求集合
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recordSouItemRefreshInfo(ApiSouItemRecordDTO param, String souType) {
        /* 1: 入参校验 */
        param.formatParams();
        /* 2: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeRecordSouItemRefreshAuth(param, souType);
        /* 3: 加锁 */
        String key = SOU_ITEM_REFRESH_LOCK + param.getProjectId();
        boolean locked = redisUtil.tryLockInTime(
                key,
//                过期时间
                10,
//                获取锁的等待时间
                3,
//                重试次数
                2);
        AssertUtils.isTrue(locked, "变更记录处理中，请稍后重试");
        try {
            /* 4: 行业包额外处理(前置) */
            SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeRecordItemRefreshInfo(param, souType);
            /* 5: 入参校验+转换处理 */
            SouItemRecordPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouItemRecordHandler.class).formatValidateAndConvert(param, souType);
            /* 6: 保存数据 */
            if (!po.getSaveRecordList().isEmpty()) {
                souItemRecordDao.saveBatch(po.getSaveRecordList());
            }
            if (!po.getUpdateRecordList().isEmpty()) {
                souItemRecordDao.updateBatchById(po.getUpdateRecordList());
            }
            /* 7: 行业包额外处理(后置) */
            SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterRecordItemRefreshInfo(param, souType);
        } finally {
            /* 8: 解锁 */
            redisUtil.unLock(key);
        }
    }

    /**
     * 执行物料变更
     *
     * @param param   物料变更执行信息
     * @param souType 寻源类型{@link SouTypeEnum}
     * @return 执行的记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<SouItemRecord> executeSouItemRefresh(ApiSouItemRefreshDTO param, String souType) {
        /* 1: 入参校验 */
        param.formatParams();
        /* 2: 校验操作条件/权限 */
        SouProject project = SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeExecuteSouItemRefreshAuth(param.getProjectId(), souType);
        /* 3: 加锁 */
        String key = SOU_ITEM_REFRESH_LOCK + param.getProjectId();
        boolean locked = redisUtil.tryLockInTime(
                key,
//                过期时间
                10,
//                获取锁的等待时间
                3,
//                重试次数
                2);
        AssertUtils.isTrue(locked, "变更记录处理中，请稍后重试");
        try {
            /* 4: 行业包额外处理(前置) */
            SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeItemRefresh(param, souType);
            /* 5: 执行物料变更 */
            SouItemRefreshPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouItemRefreshHandler.class).convert(param, souType);
            if (!po.getSaveItemList().isEmpty()) {
                souItemDao.saveBatch(po.getSaveItemList());
            }
            if (!po.getUpdateItemList().isEmpty()) {
                souItemDao.updateBatchById(po.getUpdateItemList());
            }
            if (!po.getSaveAuthList().isEmpty()) {
                souVendorAuthDao.saveBatch(po.getSaveAuthList());
            }
            if (!po.getUpdateAuthList().isEmpty()) {
                souVendorAuthDao.updateBatchById(po.getUpdateAuthList());
            }
            if (!po.getUpdateRecordList().isEmpty()) {
                souItemRecordDao.updateBatchById(po.getUpdateRecordList());
            }
            if (!po.getUpdateOrderItemList().isEmpty()) {
                souOrderItemDao.updateBatchById(po.getUpdateOrderItemList());
            }
            /* 5.1: 将现有的供应商已提交的报价修改为未提交 */
            souOrderDao.lambdaUpdate()
                    .set(SouOrder::getOrderStatus, SouOrderStatusEnum.DRAFT)
                    .eq(SouOrder::getProjectId, project.getProjectId())
                    .eq(SouOrder::getRound, project.getCurrentRound())
                    .eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                    .update();
            souOrderItemDao.lambdaUpdate()
                    .set(SouOrderItem::getOrderStatus, SouOrderStatusEnum.DRAFT)
                    .eq(SouOrderItem::getProjectId, project.getProjectId())
                    .eq(SouOrderItem::getRound, project.getCurrentRound())
                    .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                    .update();
            {/* 5.2: 将当前轮次失效物料对应的报价设置为失效 */
                Set<Long> invalidSouItemIds = souItemRecordDao.lambdaQuery()
                        .eq(SouItemRecord::getProjectId, project.getProjectId())
                        .eq(SouItemRecord::getRefreshRound, project.getCurrentRound())
                        .eq(SouItemRecord::getRefreshStatus, SouItemRefreshStatusEnum.DONE)
                        .eq(SouItemRecord::getRefreshType, SouItemRefreshTypeEnum.DELETE)
                        .list()
                        .stream().map(SouItemRecord::getSouItemId).collect(Collectors.toSet());
                if (!invalidSouItemIds.isEmpty()) {
                    souOrderItemDao.lambdaUpdate()
                            .set(SouOrderItem::getIsValid, Enable.N)
                            .eq(SouOrderItem::getProjectId, project.getProjectId())
                            .eq(SouOrderItem::getRound, project.getCurrentRound())
                            .in(SouOrderItem::getSouItemId, invalidSouItemIds)
                            .update();
                }
            }
            /* 6: 行业包额外处理(后置) */
            SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterItemRefresh(param, souType, po);

            return po.getUpdateRecordList();
        } finally {
            /* 7: 解锁 */
            redisUtil.unLock(key);
        }
    }

    /**
     * 记录追加供应商情况
     *
     * @param param   新增供应商信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recordSouVendorAddInfo(ApiSouVendorRecordDTO param, String souType) {
        /* 1: 入参校验 */
        param.formatParams();
        /* 2: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeRecordSouVendorAddAuth(param, souType);
        /* 3: 加锁 */
        String key = SOU_VENDOR_ADD_LOCK + param.getProjectId();
        boolean locked = redisUtil.tryLockInTime(
                key,
//                过期时间
                10,
//                获取锁的等待时间
                3,
//                重试次数
                2);
        AssertUtils.isTrue(locked, "新增记录处理中，请稍后重试");
        try {
            /* 4: 行业包额外处理(前置) */
            SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeRecordVendorAddInfo(param, souType);
            /* 5: 入参校验+转换处理 */
            SouVendorRecordPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouVendorAddRecordHandler.class).formatValidateAndConvert(param, souType);
            /* 6: 保存数据 */
            if (CollectionUtils.isNotEmpty(po.getSaveRecordList())) {
                souVendorRecordDao.saveBatch(po.getSaveRecordList());
            }
            if (CollectionUtils.isNotEmpty(po.getUpdateRecordList())) {
                souVendorRecordDao.updateBatchById(po.getUpdateRecordList());
            }
            /* 7: 行业包额外处理(后置) */
            SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterRecordVendorAddInfo(param, souType, po);
        } finally {
            /* 8: 解锁 */
            redisUtil.unLock(key);
        }
    }

    /**
     * 执行追加供应商
     *
     * @param param   追加供应商执行信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void executeSouVendorAdd(ApiSouVendorAddDTO param, String souType) {
        /* 1: 入参校验 */
        param.formatParams();
        /* 2: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouControlJudgeHandler.class).judgeExecuteSouVendorAddAuth(param.getProjectId(), souType);
        /* 3: 加锁 */
        String key = SOU_VENDOR_ADD_LOCK + param.getProjectId();
        boolean locked = redisUtil.tryLockInTime(
                key,
//                过期时间
                10,
//                获取锁的等待时间
                3,
//                重试次数
                2);
        AssertUtils.isTrue(locked, "新增记录处理中，请稍后重试");
        try {
            /* 4: 行业包额外处理(前置) */
            SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerBeforeVendorAdd(param, souType);
            /* 5: 执行物料变更 */
            SouVendorAddPO po = SouActiveBeanUtils.getActiveBean(souType, ApiSouVendorAddHandler.class).convert(param, souType);
            if (CollectionUtils.isNotEmpty(po.getUpdateRecordList())) {
                souVendorRecordDao.updateBatchById(po.getUpdateRecordList());
            }
            if (CollectionUtils.isNotEmpty(po.getSaveVendorList())) {
                souVendorDao.saveBatch(po.getSaveVendorList());
            }
            if (CollectionUtils.isNotEmpty(po.getSaveAuthList())) {
                souVendorAuthDao.saveBatch(po.getSaveAuthList());
            }
            /* 6: 行业包额外处理(后置) */
            SouActiveBeanUtils.getActiveBean(souType, ApiSouControlEventHandler.class).doHandlerAfterVendorAdd(param, souType, po);
        } finally {
            /* 7: 解锁 */
            redisUtil.unLock(key);
        }
    }

}
