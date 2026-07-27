package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.CollectorUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderResultQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouOrderSignUpInfoVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 报价查询服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/28
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouOrderQueryHandler implements ISouSpiBean {

    @Autowired
    protected SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    protected SouVendorDAOImpl souVendorDao;
    @Autowired
    protected SouRoundDAOImpl souRoundDao;
    @Autowired
    protected SouProjectDAOImpl souProjectDao;
    @Autowired
    protected SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    protected SouItemDAOImpl souItemDao;
    @Autowired
    protected SouVendorAuthDAOImpl souVendorAuthDao;
    @Autowired
    protected SouOrderDAOImpl souOrderDao;
    @Autowired
    protected SouItemRecordDAOImpl souItemRecordDao;

    @ApiOperation("查询报价单列表后的额外处理")
    public List<ApiSouOrderQueryVO> doHandlerAfterListOrders(ApiSouOrderQueryDTO queryParam, String souType, List<ApiSouOrderQueryVO> voList) {
        this.orderQueryDataProcessing(queryParam.getVendorId(), voList);
        return voList;
    }

    @ApiOperation("报价单列表查询后的额外字段处理")
    protected void orderQueryDataProcessing(long vendorId, List<ApiSouOrderQueryVO> voList) {
        Set<Long> projectIds = voList.stream().map(ApiSouOrderQueryVO::getProjectId).collect(Collectors.toSet());
        if (!projectIds.isEmpty()) {
            // 查询供应商入围的报价信息
            Map<Long/* projectId */, List<Integer/* round */>> map = souOrderItemDao.lambdaQuery()
                    .in(SouOrderItem::getProjectId, projectIds)
                    .eq(SouOrderItem::getWinStatus, SouWinStatusEnum.Y)
                    .eq(SouOrderItem::getVendorId, vendorId)
                    .list()
                    .stream().collect(CollectorUtils.groupingBy(SouOrderItem::getProjectId, SouOrderItem::getRound));
            Map<Long/* projectId */, List<SouRound>> roundMap = souRoundDao.lambdaQuery()
                    .in(SouRound::getProjectId, projectIds)
                    .list()
                    .stream().collect(Collectors.groupingBy(SouRound::getProjectId));
            Map<Long/* projectId */, SouVendor> vendorMap = souVendorDao.lambdaQuery()
                    .in(SouVendor::getProjectId, projectIds)
                    .eq(SouVendor::getVendorId, vendorId)
                    .list()
                    .stream().collect(Collectors.toMap(SouVendor::getProjectId, Function.identity()));
            Map<Long/* processConfigId */, SouProcessConfig> processConfigMap; {
                Set<Long> processConfigIds = voList.stream().map(ApiSouOrderQueryVO::getProcessConfigId)
                        .filter(Objects::nonNull).collect(Collectors.toSet());
                if (processConfigIds.isEmpty()) {
                    processConfigMap = Collections.emptyMap();
                } else {
                    processConfigMap = souProcessConfigDao.lambdaQuery()
                            .in(SouProcessConfig::getProcessConfigId, processConfigIds)
                            .list()
                            .stream().collect(Collectors.toMap(SouProcessConfig::getProcessConfigId, Function.identity()));
                }
            }
            Date now = new Date();
            voList.forEach(vo -> {
                if (vo.getJoinRound() != null && vo.getCurrentRound().equals(vo.getJoinRound())) {
                    // 说明供应商是在最新轮次被引入进来的
                    vo.setCanOrder(Enable.Y);
                } else {
                    List<Integer> roundList = map.get(vo.getProjectId());
                    if (roundList == null) {
                        if (vo.getCurrentRound() == null || vo.getCurrentRound() == 1) {
                            vo.setCanOrder(Enable.Y);
                        } else {
                            vo.setCanOrder(Enable.N);
                        }
                    } else {
                        roundList.sort(Comparator.comparing(Function.identity()));
                        Integer latestWinRound = roundList.get(roundList.size() - 1);
                        if (latestWinRound < vo.getCurrentRound() - 1) {
                            vo.setCanOrder(Enable.N);
                        } else {
                            vo.setCanOrder(Enable.Y);
                        }
                    }
                }
                // 如果为空则说明还未提交报价，将报价状态设置为默认值
                if (vo.getOrderStatus() == null) {
                    vo.setOrderStatus(SouOrderStatusEnum.DRAFT);
                }

                // 如果有报名环节，供应商必须是已报名的
                SouProcessConfig processConfig = processConfigMap.get(vo.getProcessConfigId());
                vo.setHasSignUpNode(processConfig != null && Enable.Y.equals(processConfig.getSignUpManagement()) ?
                        Enable.Y : Enable.N);
                if (Enable.Y.equals(vo.getHasSignUpNode())) {
                    SouVendor vendor = vendorMap.get(vo.getProjectId());
                    if (vendor == null) {
                        vo.setCanOrder(Enable.N);
                    } else {
                        vo.setCanOrder(SouSignUpStatusEnum.SIGN_UP_DONE.equals(vendor.getSignUpStatus()) ?
                                Enable.Y : Enable.N);
                    }
                }
            });
        } else {
            voList.forEach(vo -> {
                SouOrderStatusEnum orderStatus = vo.getOrderStatus();
                if (SouOrderStatusEnum.DRAFT.equals(orderStatus) || SouOrderStatusEnum.WITHDRAW.equals(orderStatus)) {
                    vo.setCanOrder(Enable.Y);
                } else {
                    vo.setCanOrder(Enable.N);
                }
            });
        }
    }

    @ApiOperation("查看项目信息后的额外处理")
    public ApiSouInitProjectInfoVO doHandlerAfterGetProjectInfo(long projectId, long vendorId, String souType, ApiSouInitProjectInfoVO vo) {
        return vo;
    }

    @ApiOperation("查看项目需求后的额外处理")
    public List<ApiSouItemVO> doHandlerAfterGetRequireInfo(long projectId, long vendorId, String souType, List<ApiSouItemVO> voList) {
        return voList;
    }

    @ApiOperation("查看供应商报名信息后的额外处理")
    public ApiSouOrderSignUpInfoVO doHandlerAfterGetSignUpInfo(long projectId, long vendorId, String souType, ApiSouOrderSignUpInfoVO vo) {
        return vo;
    }

    @ApiOperation("供应商报价结果查询后的额外处理")
    public List<ApiSouOrderItemVO> doHandlerAfterListOrderResult(ApiSouOrderResultQueryDTO queryParam, String souType, List<ApiSouOrderItemVO> voList) {
        return voList;
    }

    @ApiOperation("查询供应商报价明细后的额外处理")
    public ApiSouOrderDetailVO doHandlerAfterGetSouOrderInfo(long projectId, @Nullable Integer round, long vendorId, String souType, ApiSouOrderDetailVO vo) {
        return vo;
    }

    @ApiOperation("查询供应商报价明细后的额外处理后去除干扰信息")
    public ApiSouOrderDetailVO doHandlerAfterGetSouOrderInfoForClearJammingInfos(long projectId, @Nullable Integer round, long vendorId, String souType, ApiSouOrderDetailVO vo) {
        if (vo.getOrder() != null) {
            if (!vo.getOrder().getRound().equals(vo.getInitInfo().getProjectInfo().getCurrentRound())) {
                // 去掉上一轮的相关参数，免得对后续本轮次的报价提交有影响
                vo.getOrder().setOrderId(null);
                vo.getOrder().setOrderNo(null);
                vo.getOrder().setRound(null);
                vo.getOrder().setOrderStatus(SouOrderStatusEnum.DRAFT);
                vo.getOrder().setSubmitBy(null);
                vo.getOrder().setSubmitById(null);
                vo.getOrder().setSubmitByIp(null);
                vo.getOrder().setSubmitFullName(null);
                vo.getOrder().setSubmitTime(null);
                vo.getOrder().setWithdrawReason(null);
                vo.getOrder().setWithdrawTime(null);
                vo.getOrder().setRejectReason(null);
                vo.getOrder().setRejectTime(null);
                vo.getOrder().setIsProxy(Enable.N);
                vo.getOrder().setProxyDocId(null);
                vo.getOrder().setProxyFileName(null);
                vo.getOrder().setProxyRemark(null);


                vo.getItemList().forEach(item -> {
                    item.setOrderItemId(null);
                    item.setRound(null);
                    item.setOrderStatus(null);
                    item.setSelectStatus(null);
                    item.setWinStatus(null);
                });

                vo.getOrderFileList().forEach(file -> file.setOrderFileId(null));
            }
        }
        return vo;
    }

    /**
     * 查询指定供应商在指定轮次的可报价物料集合
     * @param projectId {@link SouProject#getProjectId}
     * @param round 指定轮次(为空则默认最新轮次)
     * @param vendorId 供应商ID
     */
    @ApiOperation("查询指定供应商在指定轮次的可报价物料集合")
    public List<SouItem> getAvailableItemsForVendor(long projectId, @Nullable Integer round, long vendorId) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        final int searchRound = round != null ? round : project.getCurrentRound();
        if (SouProjectStatusEnum.DRAFT.equals(project.getProjectStatus())) {
            return Collections.emptyList();
        }
        SouVendor vendor = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, projectId)
                .eq(SouVendor::getVendorId, vendorId)
                .one();
        List<SouItem> souItemList = SouActiveBeanUtils.getActiveBean(project.getSouType(), ApiSouOrderQueryHandler.class)
                .getValidItemsInSpecifiedRound(projectId, round);
        List<SouVendorAuth> authList = souVendorAuthDao.lambdaQuery()
                .eq(SouVendorAuth::getProjectId, projectId)
                .eq(SouVendorAuth::getVendorId, vendorId)
                .eq(SouVendorAuth::getForbidPrice, Enable.N)
                .list();
        List<SouOrderItem> lastRoundWinOrderItemList; // 上一轮入围情况
        {
            //if (searchRound > 1) {
            if (true) {
                lastRoundWinOrderItemList = souOrderItemDao.lambdaQuery()
                        // 指定招标单
                        .eq(SouOrderItem::getProjectId, projectId)
                        // 上一轮
                        //.eq(SouOrderItem::getRound, searchRound - 1)
                        // 指定供应商
                        .eq(SouOrderItem::getVendorId, vendorId)
                        // 已提交的
                        //.eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                        // 入围的
                        //.eq(SouOrderItem::getWinStatus, SouWinStatusEnum.Y)
                        // 只查询物料需求行ID
                        .select(SouOrderItem::getSouItemId)
                        .list();
            } else {
                lastRoundWinOrderItemList = Collections.emptyList();
            }
        }

        return this.getAvailableItemsForVendor(project, null, souItemList, vendor, authList, lastRoundWinOrderItemList, searchRound);
    }

    /**
     * 查询寻源单指定轮次中哪些供应商具有报价的权限
     * @return 如果返回的是空集合，说明当前没有供应商具有报价权限；
     *         如果返回的是null，说明是首轮的公开报价，所有人都具有报价权限
     */
    @ApiOperation("查询寻源单指定轮次中哪些供应商具有报价的权限")
    public Set<Long/* vendorId */> getAuthedVendors(long projectId, @Nullable Integer round) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        if (round == null) {
            round = project.getCurrentRound();
        }
        boolean needSignUp=false;

        if (round == 1) {
            if (needSignUp) {
                return souVendorDao.lambdaQuery()
                        .eq(SouVendor::getProjectId, projectId)
                        .eq(SouVendor::getSignUpStatus, SouSignUpStatusEnum.SIGN_UP_DONE)
                        .eq(SouVendor::getJoinRound, 1)
                        .select(SouVendor::getVendorId)
                        .list()
                        .stream().map(SouVendor::getVendorId).collect(Collectors.toSet());
            } else {
                if (SouPublishScopeEnum.OPEN_TENDER.equals(project.getPublishScope())) {
                    /* 公开招标 */
                    boolean orderDone;
                    switch (project.getProjectStatus()) {
                        //拟定
                        case DRAFT:
                            //已取消
                        case CANCEL:
                            //报价未开始
                        case ORDER_NOT_START:
                            //接收报价中
                        case ACCEPT_ORDER:
                            orderDone = false;
                            break;
                        default:
                            orderDone = true;
                            break;
                    }
                    if (orderDone) {
                        /* 有几个供应商尝试过报价，就返回几个 */
                        return souOrderDao.lambdaQuery()
                                .eq(SouOrder::getProjectId, projectId)
                                .eq(SouOrder::getRound, round)
                                .list()
                                .stream().map(SouOrder::getVendorId).collect(Collectors.toSet());
                    } else {
                        return souVendorDao.list(SouVendor::getProjectId, projectId)
                                .stream().map(SouVendor::getVendorId).collect(Collectors.toSet());
                    }
                } else {
                    /* 邀请招标 */
                    return souVendorDao.lambdaQuery()
                            .eq(SouVendor::getProjectId, projectId)
                            .list()
                            .stream().map(SouVendor::getVendorId).collect(Collectors.toSet());
                }
            }
        } else {
            /* 查询上轮入围的供应商 + 指定轮次新加入的供应商 */
            Set<Long> lastWinVendorIds = souOrderItemDao.lambdaQuery()
                    .eq(SouOrderItem::getProjectId, projectId)
                    .eq(SouOrderItem::getRound, round - 1)
                    .eq(SouOrderItem::getWinStatus, SouWinStatusEnum.Y)
                    .list()
                    .stream().map(SouOrderItem::getVendorId).collect(Collectors.toSet());
            lastWinVendorIds.addAll(souVendorDao.lambdaQuery()
                    .eq(SouVendor::getProjectId, projectId)
                    .eq(SouVendor::getJoinRound, round)
                    //如果有报名节点，则只查询已报名的(虽然目前中途加入的供应商没有报名确认环节)
                    .eq(needSignUp, SouVendor::getSignUpStatus, SouSignUpStatusEnum.SIGN_UP_DONE)
                    .list()
                    .stream().map(SouVendor::getVendorId).collect(Collectors.toSet()));
            return lastWinVendorIds;
        }
    }

    /**
     * 查询供应商在指定轮次可报价的物料信息
     *
     * @param projectId 询价单ID
     * @param round     指定轮次(为空时默认最新轮次)
     * @param vendorIds 供应商ID集合
     * @return 可报价的物料需求信息，如果没有可报价信息，就返回空集合
     */
    @ApiOperation("查询供应商在指定轮次可报价的物料信息")
    public Map<Long/* vendorId */, List<SouItem>> getAvailableItemsForVendors(long projectId, @Nullable Integer round, Set<Long> vendorIds) {
        if (vendorIds.isEmpty()) { return Collections.emptyMap(); }
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        final int searchRound = round != null ? round : project.getCurrentRound();
        SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
        List<SouItem> souItemList = SouActiveBeanUtils.getActiveBean(project.getSouType(), ApiSouOrderQueryHandler.class)
                .getValidItemsInSpecifiedRound(projectId, round);
        Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, projectId)
                .list().stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        Map<Long/* vendorId */, List<SouVendorAuth>> authMap = souVendorAuthDao.lambdaQuery()
                .eq(SouVendorAuth::getProjectId, projectId)
                .list().stream().collect(Collectors.groupingBy(SouVendorAuth::getVendorId));
        Map<Long/* vendorId */, List<SouOrderItem>> lastRoundOrderItemMap; {
            if (searchRound == 1) {
                lastRoundOrderItemMap = Collections.emptyMap();
            } else {
                lastRoundOrderItemMap = souOrderItemDao.lambdaQuery()
                        .eq(SouOrderItem::getProjectId, projectId)
                        .eq(SouOrderItem::getRound, searchRound - 1)
                        .list().stream().collect(Collectors.groupingBy(SouOrderItem::getVendorId));
            }
        }

        Map<Long/* vendorId */, List<SouItem>> resultMap = new HashMap<>(vendorIds.size());
        for (Long vendorId : vendorIds) {
            resultMap.put(vendorId,
                    this.getAvailableItemsForVendor(project, processConfig, souItemList,
                            vendorMap.get(vendorId), authMap.get(vendorId), lastRoundOrderItemMap.get(vendorId), searchRound));
        }
        return resultMap;
    }

    private List<SouItem> getAvailableItemsForVendor(SouProject project, SouProcessConfig processConfig,
                                                     List<SouItem> souItemList, @Nullable SouVendor vendor,
                                                     List<SouVendorAuth> authList,
                                                     List<SouOrderItem> lastRoundWinOrderItemList,
                                                     int targetRound) {
        // 查询供应商在第几轮加入询价(邀请询价时可以中途加入供应商;如果是公开询价，则joinRound都是1)
        Integer joinRound = null;
        {
            if (vendor != null) {
                joinRound = vendor.getJoinRound();
            } else {
                // 邀请供应商表找不到
                if (SouPublishScopeEnum.OPEN_TENDER.equals(project.getPublishScope()) && project.getCurrentRound().equals(1)) {
                    // 公开询价+首轮
                    joinRound = 1;
                }
            }
        }
        if (joinRound == null || joinRound > targetRound) {
            // 供应商未加入该询价 || 供应商加入的轮次在指定的查询轮次后(供应商A第三轮加入，现在查询的是第二轮的可报价物料，则为空)
            return Collections.emptyList();
        } else {
            // joinRound < round，需判断上一轮入围情况
            // 获取上轮入围的信息
            Set<Long> lastWinSouItemIds;
            {
                if (CollectionUtils.isEmpty(lastRoundWinOrderItemList)) {
                    lastWinSouItemIds = Collections.emptySet();
                } else {
                    lastWinSouItemIds = lastRoundWinOrderItemList.stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet());
                }
            }
            Set<Long> availableSouItemIds = authList.stream().filter(e -> Enable.N.equals(e.getForbidPrice()))
                    .map(SouVendorAuth::getSouItemId).collect(Collectors.toSet());
            Set<Long> targetRoundNewSouItemIds = souItemRecordDao.lambdaQuery()
                    .eq(SouItemRecord::getProjectId, project.getProjectId())
                    .eq(SouItemRecord::getRefreshRound, targetRound)
                    .eq(SouItemRecord::getRefreshStatus, SouItemRefreshStatusEnum.DONE)
                    .eq(SouItemRecord::getRefreshType, SouItemRefreshTypeEnum.NEW)
                    .list()
                    .stream().map(SouItemRecord::getSouItemId).collect(Collectors.toSet());
            Set<Long> targetRoundAuthSouItemIds = souItemList.stream()
                    //目标轮次新加入的物料
                    .filter(e -> targetRoundNewSouItemIds.contains(e.getSouItemId()))
                    //供应商对新加入的物料有报价权限
                    .filter(e -> availableSouItemIds.contains(e.getSouItemId()))
                    .map(SouItem::getSouItemId)
                    .collect(Collectors.toSet());
            if (lastWinSouItemIds.isEmpty()) {
                return souItemList.stream()
                        .filter(e -> targetRoundAuthSouItemIds.contains(e.getSouItemId()))
                        .collect(Collectors.toList());
            } else {
                return souItemList.stream()
                        .filter(item -> targetRoundAuthSouItemIds.contains(item.getSouItemId()) || lastWinSouItemIds.contains(item.getSouItemId()))
                        .collect(Collectors.toList());
            }
        }
    }

    /**
     * 查询指定轮次中有效的物料需求信息
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param round 指定轮次(为空时默认最新轮次)
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @ApiOperation("查询指定轮次中有效的物料需求信息")
    public List<SouItem> getValidItemsInSpecifiedRound(long projectId, @Nullable Integer round) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        if (round == null) { round = project.getCurrentRound(); }
        List<SouItem> allSouItemList = souItemDao.lambdaQuery()
                .eq(SouItem::getProjectId, projectId)
                .orderByAsc(SouItem::getSortIndex)
                .list();
        /* 查询指定轮次的物料变更记录 */
        /* round  最新批次执行成功的数据 */
        Map<Integer, List<SouItemRecord>> recordMap = new HashMap<>(32); {
            Map<Integer, List<SouItemRecord>> tempRecordMap = souItemRecordDao.lambdaQuery()
                    .eq(SouItemRecord::getProjectId, projectId)
                    //小于等于指定轮次
                    .le(SouItemRecord::getRefreshRound, round)
                    //物料变更成功
                    .eq(SouItemRecord::getRefreshStatus, SouItemRefreshStatusEnum.DONE)
                    .list()
                    .stream().collect(Collectors.groupingBy(SouItemRecord::getRefreshRound));
            tempRecordMap.forEach((r, recordList) -> {
                if (recordList == null) {
                    /* 说明没有进行物料变更 */
                } else {
                    /* 批次号降序排列 */
                    recordList.sort(Comparator.comparing(SouItemRecord::getBatchNo).reversed());
                    String latestBatchNo = recordList.get(0).getBatchNo();
                    recordList = recordList.stream()
                            .filter(e -> e.getBatchNo().equals(latestBatchNo))
                            .filter(e -> SouItemRefreshTypeEnum.NEW.equals(e.getRefreshType()) || SouItemRefreshTypeEnum.EXIST.equals(e.getRefreshType()))
                            .collect(Collectors.toList());
                    recordMap.put(r, recordList);
                }
            });
        }
        /* 根据物料变更记录，确认指定轮次中哪些物料是有效的 */
        if (recordMap.isEmpty()) {
            /* 说明没有物料变更 */
            return allSouItemList;
        } else {
            int tempRound = round;
            do {
                List<SouItemRecord> recordList = recordMap.get(tempRound);
                if (recordList == null) { continue; }
                Set<Long> souItemIds = recordList.stream().map(SouItemRecord::getSouItemId).collect(Collectors.toSet());
                return allSouItemList.stream().filter(e -> souItemIds.contains(e.getSouItemId())).collect(Collectors.toList());
            } while (--tempRound >= 1);
            /* 走到这里，说明没有实际物料变更 */
            return allSouItemList;
        }
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
