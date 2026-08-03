package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.select;

import cn.hutool.core.collection.CollectionUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.tech.dao.SouTechScoreHeadDAOImpl;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouGeneratePriceApprovalTypeEnum;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.score.enums.SouScoreDimensionCodeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import com.midea.cloud.srm.model.supplier.risk.dto.MonitoringDTO;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 评选接口校验
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/01
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouSelectJudgeHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouGroupDAOImpl souGroupDao;
    @Autowired
    private SouTechScoreHeadDAOImpl souTechScoreHeadDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouRoundDAOImpl souRoundDao;
    @Autowired
    private SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SupplierClient supplierClient;
    @Value("${srm.sou.meiqlApproval:N}")
    private String souMeiqlApproval;

    @ApiOperation("当前是否可以进行评选列表查询")
    public void judgeListEvaluationsAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), souType);
        AssertUtils.isTrue(project.getSouType().equals(souType), "寻源类型不匹配", souType);
    }

    /**
     * 当前操作人是否可以生成定价汇总信息
     */
    public SouProject judgeGeneratePriceReportAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);

        switch (project.getProjectStatus()) {
            //评选中
            case EVALUATING:
                //定价中
            case PRICING:
                //已定价
            case PRICE_END:
                //定价驳回
            case PRICE_REJECT:
                break;
            default:
                throw new IllegalArgumentException("当前寻源单状态禁止查看定价报表信息");
        }

        return project;
    }

    @ApiOperation("当前是否可以进行智能评选")
    public SouProject judgeIntelligentSelectAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        switch (project.getProjectStatus()) {
            //商务开标
            case BUSINESS_EVAL:
                //技术开标
            case TECH_EVAL:
                break;
            case EVALUATING:
                //评选中
                throw new IllegalArgumentException("请勿重复操作");
            default:
                throw new IllegalArgumentException("当前单据状态禁止该操作");
        }
        //如果有技术标，确保已完成技术评分
        if (project.getCurrentRound() == 1) {
            boolean needCheckTech; {
                SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
                needCheckTech = Enable.Y.equals(processConfig.getTechManagement());
            }
            if (needCheckTech) {
                AssertUtils.isTrue(Enable.Y.equals(project.getTechOpen()), "请先进行技术开标");
                //2.1 查询本轮已投标的供应商
                Set<Long> vendorIds = souOrderDao.lambdaQuery()
                        .eq(SouOrder::getProjectId, projectId)
                        .eq(SouOrder::getRound, 1)
                        .eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                        .list()
                        .stream().map(SouOrder::getVendorId).collect(Collectors.toSet());
                AssertUtils.notEmpty(vendorIds, "本轮无供应商提交报价，无法进行智能评选");
                /* 2.2 查询技术评委 */
                List<SouGroup> groupList = souGroupDao.lambdaQuery()
                        .eq(SouGroup::getProjectId, projectId)
                        .like(SouGroup::getScoreAuth, SouScoreDimensionCodeEnum.SOU_TECH)
                        .list();
                if (groupList.size() > 0) {
                    /* 2.3 查询技术评分信息 */
                    /* groupId_vendorId */
                    Map<String, SouTechScoreHead> techScoreHeadMap = souTechScoreHeadDao.lambdaQuery()
                            .eq(SouTechScoreHead::getProjectId, projectId)
                            .eq(SouTechScoreHead::getScoreStatus, SouTechScoreStatusEnum.FINISHED)
                            .list()
                            .stream().collect(Collectors.toMap(e -> e.getGroupId() + "_" + e.getVendorId(), Function.identity()));
                    /* 2.4 确保每个技术评委对每个提交报价的供应商均已评分 */
                    SouTechScoreHead techScoreHead;
                    for (SouGroup group : groupList) {
                        for (Long vendorId : vendorIds) {
                            techScoreHead = techScoreHeadMap.get(group.getGroupId() + "_" + vendorId);
                            if (techScoreHead == null) {
                                SouVendor vendor = souVendorDao.lambdaQuery()
                                        .eq(SouVendor::getProjectId, projectId)
                                        .eq(SouVendor::getVendorId, vendorId)
                                        .select(SouVendor::getVendorName)
                                        .one();
                                throw new IllegalArgumentException(MessageFormat.format("评委[{0}]未提交对供应商[{1}]的技术评分，不能智能评选",
                                        group.getFullName(),
                                        vendor.getVendorName()));
                            }
                        }
                    }
                }
            }
        }
        /* 商务开标 + 报价解密 */
        SouRound currentRound = souRoundDao.lambdaQuery()
                .eq(SouRound::getProjectId, projectId)
                .eq(SouRound::getRound, project.getCurrentRound())
                .one();
        AssertUtils.isTrue(Enable.Y.equals(currentRound.getBusinessOpen()), "未商务开标，不能智能评选");
        if (Enable.Y.equals(project.getNeedEncryptPrice())) {
            AssertUtils.isTrue(Enable.Y.equals(currentRound.getPriceDecrypt()), "未解密报价，不能智能评选");
        }
        return project;
    }

    @ApiOperation("当前是否可以入围/淘汰供应商")
    public List<SouOrderItem> judgeChangeOrderWinStatusAuth(Set<Long> orderItemIds, String souType) {
        AssertUtils.notEmpty(orderItemIds, "请选择需要入围/淘汰的数据");
        List<SouOrderItem> orderItemList = souOrderItemDao.listByIds(orderItemIds);
        AssertUtils.notEmpty(orderItemList, "找不到供应商报价信息");
        /* 1: 确保只对一个寻源单操作 */
        final long projectId = orderItemList.get(0).getProjectId();
        boolean isOneProject = orderItemList.stream().allMatch(e -> projectId == e.getProjectId());
        AssertUtils.isTrue(isOneProject, "禁止同时操作多个寻源单据");
        /* 2: 校验寻源单状态 */
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        switch (project.getProjectStatus()) {
            //报价未开始
            case ORDER_NOT_START:
                //接收报价中
            case ACCEPT_ORDER:
                throw new IllegalArgumentException("报价未截止，禁止操作");
                //报价截止
            case ORDER_END:
                throw new IllegalArgumentException("请先进行智能评选");
                //评选中
            case EVALUATING:
                //定价中
            case PRICING:
                //定价驳回
            case PRICE_REJECT:
                break;
            default:
                throw new IllegalArgumentException("非评选阶段，禁止操作");
        }
        /* 3: 校验供应商报价状态信息 */
        boolean isAllCurrentRound = orderItemList.stream().allMatch(e -> project.getCurrentRound().equals(e.getRound()));
        AssertUtils.isTrue(isAllCurrentRound, "禁止修改历史轮次的信息");
        boolean isAllSubmit = orderItemList.stream().allMatch(e -> SouOrderStatusEnum.SUBMISSION.equals(e.getOrderStatus()));
        AssertUtils.isTrue(isAllSubmit, "只能对已提交的供应商报价进行操作");

        return orderItemList;
    }

    @ApiOperation("当前是否可以中标/落标供应商")
    public List<SouOrderItem> judgeChangeOrderSelectStatusAuth(ApiSouChangeSelectStatusDTO param, String souType) {
        if (param.isToWin()) {
            return this.judgeChangeOrderWinAmountAuth(SouObjectXUtil.convertList(param.getSelects(), ApiSouChangeWinAmountDTO.class), souType);
        } else {
            return this.judgeChangeOrderWinStatusAuth(param.getSelects().stream()
                    .map(ApiSouChangeSelectStatusItemDTO::getOrderResultId).collect(Collectors.toSet()), souType);
        }
    }

    @ApiOperation("当前竞价单是否可以归档")
    public void judgePlaceOnFileStatusAuth(ApiSouPlaceOnFileDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        switch (project.getProjectStatus()) {
            case LOA:
                /* 竞价单状态是中标通知的才可归档 */
                break;
            default:
                throw new IllegalArgumentException("非中标通知阶段，禁止归档");
        }
    }

    @ApiOperation("修改竞价单状态为中标通知")
    public void judgeChangeStatusAuth(ApiSouChangProjectDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        switch (project.getProjectStatus()) {
            case PRICE_END:
                /* 竞价单状态是已定价的才可中标通知 */
                break;
            default:
                throw new IllegalArgumentException("非已定价阶段，禁止中标通知");
        }
    }

    @ApiOperation("中标时校验供应商风险")
    private void checkVendorRiskForWinStatus(List<SouOrderItem> orderItemList, String souType) {
        if (CollectionUtils.isEmpty(orderItemList)) { return; }
        /* 1: 查询关联(同组合)的全部报价数据 */
        orderItemList = SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectQueryHandler.class).getSameItemGroupOrderItems(orderItemList);
        /* 2: 校验供应商风险 */
        Map<Long/* vendorId */, List<SouOrderItem>> orderItemMap = orderItemList.stream().collect(Collectors.groupingBy(SouOrderItem::getVendorId));
        Map<Long/* vendorId */, List<MonitoringDTO>> monitorMap = supplierClient.listMonitoringByCompanyIds(orderItemMap.keySet());
        Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, orderItemList.get(0).getProjectId())
                .in(SouVendor::getVendorId, orderItemMap.keySet())
                .list().stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        orderItemMap.forEach((vendorId, orderItems) -> {
            SouVendor vendor = vendorMap.get(vendorId);
            AssertUtils.notNull(vendor, "供应商[{0}]不存在");

            List<MonitoringDTO> monitorList = monitorMap.get(vendorId);
            if (CollectionUtils.isNotEmpty(monitorList)) {
                /* 判断供应商维度 */
                List<MonitoringDTO> globals = monitorList.stream()
                        .filter(e -> e.getCategoryId() == null)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(globals)) {
                    globals.forEach(global -> {
                        AssertUtils.isFalse(Enable.Y.equals(global.getNoWinBid()), "供应商[{0}]被限制禁止中标(供应商风险)", vendor.getVendorName());
                    });
                }
                /* 判断供应商+品类维度 */
                List<MonitoringDTO> unGlobals = monitorList.stream()
                        .filter(e -> e.getCategoryId() != null)
                        .collect(Collectors.toList());
                orderItems.forEach(orderItem -> unGlobals.forEach(monitor -> {
                    if (orderItem.getCategoryId() != null && orderItem.getCategoryId().equals(monitor.getCategoryId())) {
                            AssertUtils.isFalse(Enable.Y.equals(monitor.getNoWinBid()), "供应商[{0}]被限制禁止中标品类[{0}]下的任何物料",
                                    vendor.getVendorName(), orderItem.getCategoryName());
                    }
                }));
            }
        });
    }

    @ApiOperation("当前是否可以修改中标数量")
    public List<SouOrderItem> judgeChangeOrderWinAmountAuth(List<ApiSouChangeWinAmountDTO> amountList, String souType) {
        Map<Long/* orderItemId */, BigDecimal/* winAmount */> amountMap = CollectionUtil.toMap(amountList, new HashMap<>(amountList.size()),
                ApiSouChangeWinAmountDTO::getOrderItemId, ApiSouChangeWinAmountDTO::getWinAmount);
        List<SouOrderItem> orderItemList = this.judgeChangeOrderWinStatusAuth(amountMap.keySet(), souType);
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDao
                .listByIds(orderItemList.stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        /* 查询当前轮次所有中标的报价行信息(进行中标数量校验时，需要校验某一物料下所有中标数量之和) */
        Map<Long/* souItemId */, List<SouOrderItem>> latestOrderItemMap = souOrderItemDao.lambdaQuery()
                .in(SouOrderItem::getSouItemId, souItemMap.keySet())
                .eq(SouOrderItem::getRound, orderItemList.get(0).getRound())
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .list().stream().collect(Collectors.groupingBy(SouOrderItem::getSouItemId));

        Map<Long/* souItemId */, BigDecimal> souAmountMap = new HashMap<>(souItemMap.size());
        orderItemList.forEach(orderItem -> {
            orderItem.setWinAmount(amountMap.get(orderItem.getOrderItemId()));
            switch (orderItem.getWinStatus()) {
                case D: /* 待定 */
                    AssertUtils.isNull(orderItem.getWinAmount(), "未标记中标的不能填写中标数量");
                    break;
                case Y: /* 中标 */
                    break;
                case N: /* 落标 */
                    AssertUtils.isTrue(orderItem.getWinAmount() == null || orderItem.getWinAmount().compareTo(BigDecimal.ZERO) == 0,
                            "标记落标的不能指定中标数量");
                    break;
                default:
                    throw new IllegalArgumentException("非法的中标状态");
            }

            SouItem souItem = souItemMap.get(orderItem.getSouItemId());
            if (souItem.getRequireQuantity() != null) {
                BigDecimal totalAmount = latestOrderItemMap.get(orderItem.getSouItemId()).stream().map(oi -> {
                    BigDecimal amount = amountMap.get(oi.getOrderItemId());
                    if (amount == null) {
                        amount = oi.getWinAmount();
                    }
                    if (amount == null) {
                        amount = BigDecimal.ZERO;
                    }
                    return amount;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

                AssertUtils.isTrue(souItem.getRequireQuantity().compareTo(totalAmount) >= 0, LocaleHandler.getLocaleMsg("对物料")+"[{0}]"+LocaleHandler.getLocaleMsg("的中标数量总和")+"[{1}]"+LocaleHandler.getLocaleMsg("不能超过其需求数量")+"[{2}]",
                        souItem.getItemDesc(), totalAmount, souItem.getRequireQuantity());
            }
        });

        this.checkVendorRiskForWinStatus(orderItemList, souType);
        return orderItemList;
    }

    @ApiOperation("当前是否可以公开本轮结果")
    public SouProject judgeOpenResultAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isTrue(SouProjectStatusEnum.EVALUATING.equals(project.getProjectStatus())
                        || SouProjectStatusEnum.PRICE_END.equals(project.getProjectStatus()),
                "当前单据状态禁止该操作");

        List<SouOrderItem> selectionList = souOrderItemDao.lambdaQuery()
                .eq(SouOrderItem::getProjectId, projectId)
                .eq(SouOrderItem::getRound, project.getCurrentRound())
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .list();
        if (!selectionList.isEmpty()) {
            boolean canOpen = selectionList.stream().map(SouOrderItem::getWinStatus)
                    .allMatch(e -> e.equals(SouWinStatusEnum.Y) || e.equals(SouWinStatusEnum.N));
            AssertUtils.isTrue(canOpen, "当前轮次尚有供应商未评定入围/淘汰，不能公开本轮结果");
        }

        return project;
    }

    @ApiOperation("当前是否可以生成价格审批单")
    public List<SouOrderItem> judgeCreatePricingApproval(long projectId, String souType) {
        AssertUtils.isFalse(Enable.Y.name().equals(souMeiqlApproval), "回迁版价格审批单已启用，禁止访问旧接口");
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isTrue(SouGeneratePriceApprovalTypeEnum.BY_TOTAL.name().equals(project.getGeneratePriceApprovalType()), "该单据非整单维度创建价格审批单，禁止访问该接口");

        //校验单据状态
        switch (project.getProjectStatus()) {
            //评选中
            case EVALUATING:
                //定价驳回
            case PRICE_REJECT:
                break;
                //定价中
            case PRICING:
                throw new IllegalArgumentException("当前单据已生成价格审批单，请勿重复操作");
                //已定价
            case PRICE_END:
                throw new IllegalArgumentException("当前单据已定价");
            default:
                throw new IllegalArgumentException("当前单据状态不能生成价格审批单");
        }
        //当前是否已公开本轮结果
        SouRound currentRound = souRoundDao.lambdaQuery()
                .eq(SouRound::getProjectId, projectId)
                .eq(SouRound::getRound, project.getCurrentRound())
                .one();
        AssertUtils.isTrue(Enable.Y.equals(currentRound.getHasPublishResult()), "尚未公开本轮结果，不能生成价格审批单");
        /* 3: 当前轮次已提交的报价数据是否均已评定中标/落标 */
        List<SouOrderItem> selectionList = souOrderItemDao.lambdaQuery()
                .eq(SouOrderItem::getProjectId, projectId)
                .eq(SouOrderItem::getRound, project.getCurrentRound())
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .list();
        AssertUtils.notEmpty(selectionList, "当前轮次尚无供应商提交报价，不能生成价格审批单");
        boolean canOpen = selectionList.stream().map(SouOrderItem::getSelectStatus)
                .allMatch(e -> SouSelectStatusEnum.WIN.equals(e) || SouSelectStatusEnum.FAIL.equals(e));
        AssertUtils.isTrue(canOpen, "当前轮次尚有供应商未评定中标/落标，不能生成价格审批单");
        boolean hasWins = selectionList.stream().map(SouOrderItem::getSelectStatus)
                .anyMatch(SouSelectStatusEnum.WIN::equals);
        AssertUtils.isTrue(hasWins, "当前轮次尚无供应商评定为中标，不能生成价格审批单");
        boolean hasWinAmount = selectionList.stream()
                .filter(e -> SouSelectStatusEnum.WIN.equals(e.getSelectStatus()))
                .map(SouOrderItem::getWinAmount).allMatch(Objects::nonNull);
        AssertUtils.isTrue(hasWinAmount, "当前轮次尚有中标供应商未分配中标数量，不能生成价格审批单");

        return selectionList.stream().filter(e -> SouSelectStatusEnum.WIN.equals(e.getSelectStatus())).collect(Collectors.toList());
    }

    @ApiOperation("当前是否可以生成价格审批单")
    public List<SouOrderItem> judgeCreatePricingApprovalNew(ApiSouCreatePricingApprovalDTO param) {
        AssertUtils.isTrue(Enable.Y.name().equals(souMeiqlApproval), "回迁版价格审批单已启用，禁止访问旧接口");
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(param.getSouType()), "寻源类型[{0}]不匹配", param.getSouType());

        //校验单据状态
        switch (project.getProjectStatus()) {
            //评选中
            case EVALUATING:
                //定价驳回
            case PRICE_REJECT:
                break;
            //定价中
            case PRICING:
                throw new IllegalArgumentException("当前单据已生成价格审批单，请勿重复操作");
                //已定价
            case PRICE_END:
                throw new IllegalArgumentException("当前单据已定价");
            default:
                throw new IllegalArgumentException("当前单据状态不能生成价格审批单");
        }
        //2: 当前是否已公开本轮结果
        SouRound currentRound = souRoundDao.lambdaQuery()
                .eq(SouRound::getProjectId, param.getProjectId())
                .eq(SouRound::getRound, project.getCurrentRound())
                .one();
        AssertUtils.isTrue(Enable.Y.equals(currentRound.getHasPublishResult()), "尚未公开本轮结果，不能生成价格审批单");
        //3: 当前轮次已提交的报价数据是否均已评定中标/落标
        List<SouOrderItem> selectionList = souOrderItemDao.lambdaQuery()
                .eq(SouOrderItem::getProjectId, param.getProjectId())
                .eq(SouOrderItem::getRound, project.getCurrentRound())
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .list();
        AssertUtils.notEmpty(selectionList, "当前轮次尚无供应商提交报价，不能生成价格审批单");
        boolean canOpen = selectionList.stream().map(SouOrderItem::getSelectStatus)
                .allMatch(e -> SouSelectStatusEnum.WIN.equals(e) || SouSelectStatusEnum.FAIL.equals(e));
        AssertUtils.isTrue(canOpen, "当前轮次尚有供应商未评定中标/落标，不能生成价格审批单");
        boolean hasWins = selectionList.stream().map(SouOrderItem::getSelectStatus)
                .anyMatch(SouSelectStatusEnum.WIN::equals);
        AssertUtils.isTrue(hasWins, "当前轮次尚无供应商评定为中标，不能生成价格审批单");
        boolean hasWinAmount = selectionList.stream()
                .filter(e -> SouSelectStatusEnum.WIN.equals(e.getSelectStatus()))
                .map(SouOrderItem::getWinAmount).allMatch(Objects::nonNull);
        AssertUtils.isTrue(hasWinAmount, "当前轮次尚有中标供应商未分配中标数量，不能生成价格审批单");

        if (SouGeneratePriceApprovalTypeEnum.BY_TOTAL.name().equals(project.getGeneratePriceApprovalType())) {
            return selectionList.stream().filter(e -> SouSelectStatusEnum.WIN.equals(e.getSelectStatus())).collect(Collectors.toList());
        } else if (SouGeneratePriceApprovalTypeEnum.BY_ROWS.name().equals(project.getGeneratePriceApprovalType())) {
            AssertUtils.notEmpty(param.getOrderItemIds(), "缺少orderItemIds数据");
            List<SouOrderItem> orderItemList = selectionList.stream()
                    .filter(e -> SouSelectStatusEnum.WIN.equals(e.getSelectStatus()))
                    .filter(e -> param.getOrderItemIds().contains(e.getOrderItemId()))
                    .collect(Collectors.toList());
            AssertUtils.notNull(orderItemList, "缺少orderItemIds数据");
            return orderItemList;
        } else {
            throw new IllegalArgumentException("无法识别的generatePriceApprovalType类型，请自行实现");
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
