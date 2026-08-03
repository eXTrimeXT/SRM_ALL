package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.control;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.control.*;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouVendorAuthEditDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.risk.dto.MonitoringDTO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 流程控制接口校验
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/01
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouControlJudgeHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouRoundDAOImpl souRoundDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouGroupDAOImpl souGroupDao;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SupplierClient supplierClient;

    @ApiOperation("当前是否可以查询报价管理信息")
    public SouProject judgeGetControlInfo(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源单与")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        return project;
    }

    @ApiOperation("当前是否可以查看供应商报价详情")
    public SouOrder judgeGetVendorOrderAuth(long orderId, String souType) {
        SouOrder order = souOrderDao.getById(orderId);
        AssertUtils.notNull(order, LocaleHandler.getLocaleMsg("报价单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), orderId);
        SouProject project = souProjectDao.getById(order.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        boolean needCheckDecrypt = order.getRound().equals(project.getCurrentRound()) && Enable.Y.equals(project.getNeedEncryptPrice());
        if (needCheckDecrypt) {
            SouRound currentRound = souRoundDao.lambdaQuery()
                    .eq(SouRound::getProjectId, project.getProjectId())
                    .eq(SouRound::getRound, project.getCurrentRound())
                    .one();
            AssertUtils.isTrue(Enable.Y.equals(currentRound.getPriceDecrypt()), "未解密报价，禁止查看供应商报价信息");
        }
        return order;
    }

    @ApiOperation("当前是否可以查看物料需求变更记录")
    public void judgeListSouItemRecords(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
    }

    @ApiOperation("当前是否查询最新的物料变更记录")
    public void judgeGetLatestItemRecord(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
    }

    @ApiOperation("当前是否可以修改报价开始时间")
    public void judgeChangeOrderStartTime(ApiSouChangeOrderStartTimeDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        switch (project.getProjectStatus()) {
            //报名截止
            case SIGN_UP_END:
                //报价未开始
            case ORDER_NOT_START:
                //接收报价中
            case ACCEPT_ORDER:
                break;
            default:
                throw new IllegalArgumentException("当前单据状态禁止操作");
        }
        SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
        if (Enable.Y.equals(processConfig.getSignUpManagement())) {
            List<SouVendor> vendorList = souVendorDao.list(SouVendor::getProjectId, param.getProjectId());
            if (SouPublishScopeEnum.OPEN_TENDER.equals(project.getPublishScope())) {
                // 公开
                AssertUtils.notEmpty(vendorList, "当前无供应商报名");
                boolean hasNeedSignUpConfirm = vendorList.stream().map(SouVendor::getSignUpStatus)
                        .anyMatch(SouSignUpStatusEnum.CONFIRM_ING::equals);
                AssertUtils.isFalse(hasNeedSignUpConfirm, "尚有供应商待确认报名情况");
            }
            boolean hasSignUpVendors = vendorList.stream().map(SouVendor::getSignUpStatus)
                    .anyMatch(SouSignUpStatusEnum.SIGN_UP_DONE::equals);
            AssertUtils.isTrue(hasSignUpVendors, "无供应商通过报名");
        }
        Date startTime = param.isStartNow() ? new Date() : param.getOrderStartTime();
        AssertUtils.isTrue(startTime.before(project.getOrderEndTime()), "报价开始时间不能晚于报价截止时间");
    }

    @ApiOperation("当前是否可以修改报价截止时间")
    public void judgeChangeOrderEndTimeAuth(ApiSouChangeOrderEndTimeDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        switch (project.getProjectStatus()) {
            //接收报价中
            case ACCEPT_ORDER:
                //报价截止
            case ORDER_END:
                break;
            default:
                throw new IllegalArgumentException("当前单据状态禁止操作");
        }
        Date endTime = param.isEndNow() ? new Date() : param.getOrderEndTime();
        AssertUtils.isTrue(endTime.after(project.getOrderStartTime()), "报价截止时间不能早于报价开始时间");

        // 不能晚于最早开标时间
        if (project.getEarliestBusinessOpenTime() != null) {
            AssertUtils.isFalse(endTime.after(project.getEarliestBusinessOpenTime()), LocaleHandler.getLocaleMsg("报价截止时间不能晚于本轮次最早开标时间")+":{0}",
                    project.getEarliestBusinessOpenTime());
        }
    }

    @ApiOperation("当前是否可以修改最早开标时间")
    public SouProject judgeChangeEarliestBusinessOpenTimeAuth(ApiSouChangeEarliestBusinessOpenTimeDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), "寻源单与souType类型不匹配");

        SouRound currentRound = souRoundDao.lambdaQuery()
                .eq(SouRound::getProjectId, project.getProjectId())
                .eq(SouRound::getRound, project.getCurrentRound())
                .one();
        AssertUtils.isTrue(Enable.N.equals(currentRound.getBusinessOpen()), "本轮已商务开标，禁止修改开标时间");

        return project;
    }

    @ApiOperation("当前是否可以生成开标密码")
    public SouProject judgeGenerateBidPwdAuth(ApiSouBidPwdGenerateDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isFalse(SouProjectStatusEnum.DRAFT.equals(project.getProjectStatus()), "寻源单未发布，不能生成开标密码");
        AssertUtils.notNull(project.getNeedPwdOperations(), "寻源单无需生成开标密码");
        for (String openBidType : param.getOpenBidTypes()) {
            AssertUtils.isTrue(project.getNeedPwdOperations().contains(openBidType), "[{0}]"+LocaleHandler.getLocaleMsg("场景无需生成开标密码"), openBidType);
        }

        return project;
    }

    @ApiOperation("当前是否可以确认开标密码")
    public SouProject judgeConfirmOpeningBid(ApiSouOpenBidDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.notNull(project.getNeedPwdOperations(), "[{0}]"+LocaleHandler.getLocaleMsg("无需校验开标密码"), param.getOpenBidType());
        AssertUtils.isTrue(project.getNeedPwdOperations().contains(param.getOpenBidType()), "[{0}]"+LocaleHandler.getLocaleMsg("无需校验开标密码"), param.getOpenBidType());
        return project;
    }

    @ApiOperation("当前是否可以进行商务开标")
    public SouProject judgeBusinessOpenAuth(long projectId, @Nullable Long currentUserId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);

        // 只有指定人才能商务开标
        AssertUtils.notNull(currentUserId, "缺少currentUserId参数");
        Optional<SouGroup> groupOptional = souGroupDao.lambdaQuery()
                .eq(SouGroup::getProjectId, projectId)
                .eq(SouGroup::getUserId, currentUserId)
                .list().stream().filter(g -> g.getOperateAuth().contains(SouGroupOperateAuthEnum.SOU_BUSINESS_OPEN.name()))
                .findFirst();
        AssertUtils.isTrue(groupOptional.isPresent(), "没有商务开标权限，禁止操作");

        switch (project.getProjectStatus()) {
            //报价未开始
            case ORDER_NOT_START:
                throw new IllegalArgumentException("未完成供应商报价环节，禁止商务开标");
                //接收报价中
            case ACCEPT_ORDER:
                throw new IllegalArgumentException("报价未截止，禁止商务开标");
                //报价已截止
            case ORDER_END:
                //技术开标
            case TECH_EVAL:
                break;
                //商务开标
            case BUSINESS_EVAL:
                //评选中
            case EVALUATING:
                throw new IllegalArgumentException("当前已开标，请勿重复操作");
            default:
                throw new IllegalArgumentException("当前单据状态禁止操作");
        }
        // 当前是否满足最早开标时间要求
        if (project.getEarliestBusinessOpenTime() != null) {
            AssertUtils.isFalse(project.getEarliestBusinessOpenTime().after(new Date()), LocaleHandler.getLocaleMsg("未到最早开标时间要求")+"[{0}]，"+LocaleHandler.getLocaleMsg("禁止商务开标"),
                    project.getEarliestBusinessOpenTime());
        }
        // 查看当前轮次提交报价的供应商数量，如果没有供应商提交报价，则商务开标没有意义
        long count = souOrderDao.lambdaQuery()
                .eq(SouOrder::getProjectId, projectId)
                .eq(SouOrder::getRound, project.getCurrentRound())
                .eq(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .count();
        AssertUtils.isTrue(count > 0, "当前轮次没有供应商提交报价");

        return project;
    }

    @ApiOperation("当前是否可以报价解密")
    public SouProject judgeDecryptPriceAuth(ApiSouDecryptPriceDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isTrue(Enable.Y.equals(project.getNeedEncryptPrice()), "无需报价解密");

        // 只有指定人才能报价解密
        AssertUtils.notNull(param.getCurrentUserId(), "缺少currentUserId参数");
        Optional<SouGroup> groupOptional = souGroupDao.lambdaQuery()
                .eq(SouGroup::getProjectId, param.getProjectId())
                .eq(SouGroup::getUserId, param.getCurrentUserId())
                .list().stream().filter(g -> g.getOperateAuth().contains(SouGroupOperateAuthEnum.SOU_DECRYPT_PRICE.name()))
                .findFirst();
        AssertUtils.isTrue(groupOptional.isPresent(), "没有报价解密权限，禁止操作");

        switch (project.getProjectStatus()) {
            //报价截止
            case ORDER_END:
                throw new IllegalArgumentException("未商务开标，禁止操作");
                //商务开标
            case BUSINESS_EVAL:
                //技术开标
            case TECH_EVAL:
                break;
                //评选中
            case EVALUATING:
                //定价中
            case PRICING:
                //定价驳回
            case PRICE_REJECT:
                //已定价
            case PRICE_END:
                throw new IllegalArgumentException("已报价解密，无需重复操作");
            default:
                throw new IllegalArgumentException("当前单据状态禁止操作");
        }
        SouRound currentRound = souRoundDao.lambdaQuery()
                .eq(SouRound::getProjectId, param.getProjectId())
                .eq(SouRound::getRound, project.getCurrentRound())
                .one();
        if (SouProjectStatusEnum.TECH_EVAL.equals(project.getProjectStatus())) {
            /* 技术开标，判断是否已进行商务开标 */
            AssertUtils.isTrue(Enable.Y.equals(currentRound.getBusinessOpen()), "未商务开标，禁止操作");
        }
        AssertUtils.isTrue(Enable.N.equals(currentRound.getPriceDecrypt()), "已报价解密，请勿重复操作");
        return project;
    }

    @ApiOperation("当前是否可以发起新一轮")
    public SouProject judgeStartNewRoundAuth(ApiSouStartNewRoundDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        switch (project.getProjectStatus()) {
            //评选中
            case EVALUATING:
                //定价驳回
            case PRICE_REJECT:
                break;
            default:
                throw new IllegalArgumentException("当前单据状态不能发起新一轮");
        }
        SouRound round = souRoundDao.lambdaQuery()
                .eq(SouRound::getProjectId, param.getProjectId())
                .eq(SouRound::getRound, project.getCurrentRound())
                .one();
        AssertUtils.isTrue(Enable.Y.equals(round.getHasPublishResult()), "公开本轮结果后才能发起新一轮");

        if (CollectionUtils.isNotEmpty(param.getNewVendors())) {
            Map<Long/* vendorId */, SouVendor> existVendors = souVendorDao.lambdaQuery()
                    .eq(SouVendor::getProjectId, param.getProjectId())
                    .list().stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
            param.getNewVendors().forEach(vendor -> {
                AssertUtils.notNull(vendor.getVendorId(), "缺少vendorId信息");
                SouVendor existVendor = existVendors.get(vendor.getVendorId());
                if (existVendor != null) {
                    throw new IllegalArgumentException(MessageFormat.format("供应商[{0}]已存在，不能再次邀请", existVendor.getVendorName()));
                }
            });
        }

        if (param.getEarliestBusinessOpenTime() != null) {
            AssertUtils.isFalse(param.getOrderEndTime().after(param.getEarliestBusinessOpenTime()), "报价截止时间不能晚于最早开标时间");
        }

        // 校验供应商风险
        this.checkVendorRiskForAddNewVendorsWhileNewRound(param, souType);

        return project;
    }

    /**
     * 1. 供应商维度：不能投标   -- 不能添加
     * 2. 供应商维度：不能中标   -- 可以添加
     * 3. 供应商+品类维度：不能投标 -- （新增供应商是支持指定报价权限的，如果没指定就默认就有完全的报价权限）如果存在可投标的物料，则可以添加
     * 4. 供应商+品类维度：不能中标 -- 可以添加(不校验报价权限)
     */
    @ApiOperation("校验供应商风险(发起新一轮时)")
    private void checkVendorRiskForAddNewVendorsWhileNewRound(ApiSouStartNewRoundDTO param, String souType) {
        if (CollectionUtils.isEmpty(param.getNewVendors())) { return; }

        SouProject souProject = souProjectDao.getById(param.getProjectId());

        Set<Long> newVendorIds = new HashSet<>(param.getNewVendors().size());
        // 供应商有权限的品类集合
        Map<Long/* vendorId */, Set<String/* categoryCode */>> vendorAuthCategories = new HashMap<>(param.getNewVendors().size()); {
            Map<Long/* souItemId */, SouItem> souItemMap = souItemDao.lambdaQuery()
                    .eq(SouItem::getProjectId, param.getProjectId()).list()
                    .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
            boolean isGroupPrice = SouOrderWayEnum.COMBINED.equals(souProject.getOrderWay());
            Map<String/* itemGroup */, Set<Long/* souItemId */>> itemGroupMap; {
                if (isGroupPrice) {
                    itemGroupMap = souItemMap.values().stream()
                            .collect(Collectors.groupingBy(SouItem::getItemGroup, Collectors.mapping(SouItem::getSouItemId, Collectors.toSet())));
                } else {
                    itemGroupMap = Collections.emptyMap();
                }
            }
            param.getNewVendors().forEach(newVendor -> {
                newVendorIds.add(newVendor.getVendorId());

                if (CollectionUtils.isEmpty(newVendor.getAuthList())) {
                    // 新增供应商时没有定义报价权限，则具有全部报价权限
                    vendorAuthCategories.put(newVendor.getVendorId(),
                            souItemMap.values().stream().map(SouItem::getCategoryCode).filter(Objects::nonNull).collect(Collectors.toSet()));
                } else {
                    // 新增供应商时定义了报价权限
                    Map<Long/* souItemId */, Enable/* Y-可报价/N-不能报价 */> authEnableMap = new HashMap<>(newVendor.getAuthList().size());
                    for (ApiSouVendorAuthEditDTO auth : newVendor.getAuthList()) {
                        if (auth.getSouItemId() == null) { continue; }
                        authEnableMap.put(auth.getSouItemId(), auth.getForbidPrice() != null ? (Enable.Y.equals(auth.getForbidPrice()) ? Enable.N : Enable.Y) : Enable.Y);
                    }
                    if (isGroupPrice) {
                        // 组合报价时，判断现有已添加的是否存在冲突
                        itemGroupMap.forEach((itemGroup, souItemIds) -> {
                            Enable enable = null;
                            for (Long souItemId : souItemIds) {
                                Enable ee = authEnableMap.get(souItemId);
                                if (ee == null) { continue; } // 前端未指定
                                if (enable == null) {
                                    enable = ee;
                                } else {
                                    AssertUtils.isTrue(enable.equals(ee), "新增供应商在组合[{0}]下的报价权限不一致(必须都能报价或都不能报价)", itemGroup);
                                }
                            }
                            // 对剩下的，进行填补
                            for (Long souItemId : souItemIds) {
                                if (authEnableMap.containsKey(souItemId)) { continue; }
                                authEnableMap.putIfAbsent(souItemId, enable != null ? enable : Enable.Y);
                            }
                        });
                    } else {
                        souItemMap.forEach((souItemId, souItem) -> {
                            if (!authEnableMap.containsKey(souItemId) && souItem.getCategoryCode() != null) {
                                authEnableMap.put(souItemId, Enable.Y);
                            }
                        });
                    }
                    souItemMap.forEach((souItemId, souItem) -> {
                        if (souItem.getCategoryCode() != null) {
                            Enable flag = authEnableMap.get(souItemId);
                            if (!Enable.N.equals(flag)) {
                                vendorAuthCategories.computeIfAbsent(newVendor.getVendorId(), k -> new HashSet<>(16))
                                        .add(souItem.getCategoryCode());
                            }
                        }
                    });
                }
            });
        }

        extracted(newVendorIds, vendorAuthCategories);
    }

    private void extracted(Set<Long> newVendorIds, Map<Long, Set<String>> vendorAuthCategories) {
        Map<Long/* vendorId */, List<MonitoringDTO>> monitorMap = supplierClient.listMonitoringByCompanyIds(newVendorIds);
        Map<Long/* vendorId */, CompanyInfo> companyMap = supplierClient
                .getComponyByIds(new ArrayList<>(newVendorIds))
                .stream().collect(Collectors.toMap(CompanyInfo::getCompanyId, Function.identity()));
        log.info("寻源-发起新一轮-供应商风险信息：供应商有效品类=" + JSON.toJSONString(vendorAuthCategories) + "，供应商风险信息=" + JSON.toJSONString(monitorMap));
        for (Long newVendorId : newVendorIds) {
            CompanyInfo companyInfo = companyMap.get(newVendorId);
            AssertUtils.notNull(companyInfo, "供应商[{0}]不存在", newVendorId);

            List<MonitoringDTO> monitorList = monitorMap.get(newVendorId);
            if (CollectionUtils.isNotEmpty(monitorList)) {
                // 判断供应商维度
                List<MonitoringDTO> globals = monitorList.stream()
                        .filter(e -> e.getCategoryId() == null)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(globals)) {
                    globals.forEach(global -> {
                        AssertUtils.isFalse(Enable.Y.equals(global.getNoBid()), "供应商[{0}]被限制禁止投标(供应商风险)，不能添加", companyInfo.getCompanyName());
                    });
                }
                // 判断供应商+品类维度
                List<MonitoringDTO> unGlobals = monitorList.stream()
                        .filter(e -> e.getCategoryId() != null)
                        .collect(Collectors.toList());
                vendorAuthCategories.forEach((vendorId, categoryCodes) -> categoryCodes.forEach(categoryCode -> {
                    unGlobals.forEach(monitor -> {
                        if (categoryCode.equals(monitor.getCategoryCode())) {
                            AssertUtils.isFalse(Enable.Y.equals(monitor.getNoBid()), "供应商[{0}]被限制禁止投标品类[{0}]下的任何物料，不能添加",
                                    companyInfo.getCompanyName(), categoryCode);
//                            AssertUtils.isFalse(Enable.Y.equals(monitor.getNoWinBid()), "供应商[{0}]被限制禁止中标品类[{0}]下的任何物料，不能中标",
//                                    companyInfo, categoryCode);
                        }
                    });
                }));
            }
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    @ApiOperation("当前是否可以记录物料变更情况")
    public SouProject judgeRecordSouItemRefreshAuth(ApiSouItemRecordDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isTrue(Enable.Y.equals(project.getAllowItemChange()), "当前单据不支持物料变更");

        return project;
    }

    @ApiOperation("当前是否可以执行物料变更")
    public SouProject judgeExecuteSouItemRefreshAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isTrue(Enable.Y.equals(project.getAllowItemChange()), "当前单据不支持物料变更");

        return project;
    }

    @SuppressWarnings("UnusedReturnValue")
    @ApiOperation("当前是否可以记录追加供应商信息")
    public SouProject judgeRecordSouVendorAddAuth(ApiSouVendorRecordDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isTrue(Enable.Y.equals(project.getAllowNewVendors()), "当前单据不支持追加供应商");

        return project;
    }

    @ApiOperation("当前是否可以执行追加供应商")
    public SouProject judgeExecuteSouVendorAddAuth(long projectId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型")+"[{0}]"+LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isTrue(Enable.Y.equals(project.getAllowNewVendors()), "当前单据不支持追加供应商");

        return project;
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
