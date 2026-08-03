package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderCancelDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemQuoteTempDownloadDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouPublishScopeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 报价接口校验
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/11/30
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouOrderJudgeHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouRoundDAOImpl souRoundDao;

    @Autowired
    private SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    private ISouQuoteTempService souQuoteTempService;

    @Autowired
    private SouItemDAOImpl souItemDao;

    /**
     * 当前操作人是否可以查看到寻源单据
     * PS: 查看到，不代表有报价权限(例如需要先报名)
     *
     * @param projectId {@link SouProject#getProjectId}
     */
    @SuppressWarnings("unused")
    @ApiOperation("当前操作人是否可以查看到寻源单据")
    public SouProject judgeGetProjectAuth(long projectId, long vendorId, String souType) {
        SouProject project = souProjectDao.getById(projectId);
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单据") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型") + "[{0}]" + LocaleHandler.getLocaleMsg("不匹配"), souType);
        boolean isInviteScope = SouPublishScopeEnum.INVITE_TENDER.equals(project.getPublishScope());
        if (isInviteScope) {
            // 邀请寻源
            SouVendor vendor = souVendorDao.lambdaQuery()
                    .eq(SouVendor::getProjectId, projectId)
                    .eq(SouVendor::getVendorId, vendorId)
                    .one();
            AssertUtils.notNull(vendor, "非受邀供应商，禁止操作");
        }

        return project;
    }

    @Nullable
    @ApiOperation("当前是否可以查看供应商报名信息")
    public SouVendor judgeGetSignUpInfoAuth(long projectId, long vendorId, String souType) {
        SouProject project = this.judgeGetProjectAuth(projectId, vendorId, souType);
        SouProcessConfig processConfig = souProcessConfigDao.getById(project.getProcessConfigId());
        SouVendor vendor = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, projectId)
                .eq(SouVendor::getVendorId, vendorId)
                .one();
        if (SouPublishScopeEnum.INVITE_TENDER.equals(project.getPublishScope())) {
            // 邀请
            AssertUtils.notNull(vendor, "供应商不在被邀请范围");
        }
        return vendor;
    }

    @ApiOperation("当前是否可以查看供应商报价信息")
    public SouProject judgeGetOrderInfoAuth(long projectId, long vendorId, @Nullable Integer round, boolean isBuyer, String souType) {
        SouProject souProject = souProjectDao.getById(projectId);
        AssertUtils.notNull(souProject, LocaleHandler.getLocaleMsg("寻源单") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(souProject.getSouType().equals(souType), "寻源类型不匹配");
        round = round != null ? round : souProject.getCurrentRound();
        SouVendor vendor = souVendorDao.lambdaQuery()
                .eq(SouVendor::getProjectId, projectId)
                .eq(SouVendor::getVendorId, vendorId)
                .one();
        if (SouPublishScopeEnum.INVITE_TENDER.equals(souProject.getPublishScope())) {
            AssertUtils.notNull(vendor, "非受邀供应商，禁止访问");
        }
        if (isBuyer && round.equals(souProject.getCurrentRound()) && Enable.Y.equals(souProject.getNeedEncryptPrice())) {
            // 对于采购商，如果设置了密封报价，仅当本轮报价解密后，才能查看
            SouRound currentRound = souRoundDao.lambdaQuery().eq(SouRound::getProjectId, projectId).eq(SouRound::getRound, round).one();
            AssertUtils.isTrue(Enable.Y.equals(currentRound.getPriceDecrypt()), "本轮未解密报价，禁止查看供应商信息");
        }
        return souProject;
    }

    @ApiOperation("当前是否可以查看供应商的料费分离报价数据")
    public String/* businessId */ judgeGetQuoteTempOrderInfoAuth(long tempId, String businessId, boolean isBuyer, @Nullable Long vendorId,
                                                                 boolean searchLatestData, String souType) {
        // projectId_round_vendorId_souItemId
        long projectId, targetVendorId, souItemId;
        int round;
        {
            businessId = StringUtils.trimToNull(businessId);
            AssertUtils.notNull(businessId, "缺少businessId参数");
            AssertUtils.isTrue(isBuyer || vendorId != null, "缺少vendorId参数");
            String[] tempArr = businessId.split("_");
            AssertUtils.isTrue(tempArr.length == 4, "businessId格式错误");
            try {
                projectId = Long.parseLong(tempArr[0]);
                round = Integer.parseInt(tempArr[1]);
                targetVendorId = Long.parseLong(tempArr[2]);
                souItemId = Long.parseLong(tempArr[3]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("businessId格式错误");
            }
        }
        if (!isBuyer) {
            AssertUtils.isTrue(targetVendorId == vendorId, "禁止查看他人的报价明细");
        }
        SouProject souProject = this.judgeGetOrderInfoAuth(projectId, targetVendorId, round, isBuyer, souType);

        if (searchLatestData && round > 1) {
            // 如果当前轮次找不到用户报价，则寻找用户的上一轮报价
            Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> data = souQuoteTempService
                    .queryTempData(souProject.getQuoteTempId(), businessId, true).getPriceData().getData();
            boolean hasData = true;
            {
                if (data == null || data.isEmpty()) {
                    hasData = false;
                } else {
                    AtomicInteger count = new AtomicInteger(0);
                    data.values().forEach(d -> {
                        if (CollectionUtils.isEmpty(d)) {
                            count.addAndGet(1);
                        }
                    });
                    if (count.get() == data.size()) {
                        hasData = false;
                    }
                }
            }
            if (!hasData) {
                businessId = projectId + "_" + (round - 1) + "_" + targetVendorId + "_" + souItemId;
            }
        }

        return businessId;
    }

    @ApiOperation("当前是否可以导出物理需求维度报价模板导出excel文件")
    public void judgeDownloadOrderItemQuoteTempExcel(ApiSouOrderItemQuoteTempDownloadDTO param, boolean isBuyer) {
        SouProject souProject = this.judgeGetOrderInfoAuth(param.getProjectId(), param.getVendorId(), param.getRound(), isBuyer, param.getSouType());
        AssertUtils.isTrue(SouOrderTypeEnum.MATERIAL_COST_SEPARATION.equals(souProject.getOrderType()), "非料费分离报价，禁止下载模板文件");
    }


    @ApiOperation("当前是否可以查看供应商报价信息")
    public SouOrder judgeGetOrderInfoAuth(long orderId, long vendorId, boolean isBuyer, String souType) {
        SouOrder order = souOrderDao.getById(orderId);
        AssertUtils.notNull(order, LocaleHandler.getLocaleMsg("报价单") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), orderId);
        SouProject souProject = souProjectDao.getById(order.getProjectId());
        AssertUtils.isTrue(souProject.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型") + "[{0}]" + LocaleHandler.getLocaleMsg("不匹配"), souType);
        if (isBuyer) {
            // 采购商，如果设置了密封报价，仅当本轮报价解密后，才能查看
            if (Enable.Y.equals(souProject.getNeedEncryptPrice())) {
                if (order.getRound().equals(souProject.getCurrentRound())) {
                    SouRound currentRound = souRoundDao.lambdaQuery()
                            .eq(SouRound::getProjectId, souProject.getProjectId())
                            .eq(SouRound::getRound, souProject.getCurrentRound())
                            .one();
                    AssertUtils.isTrue(Enable.Y.equals(currentRound.getPriceDecrypt()), "本轮未解密报价，禁止查看供应商信息");
                }
            }
        } else {
            // 供应商
            AssertUtils.isTrue(order.getVendorId().equals(vendorId), "禁止查看他人报价信息");
        }
        return order;
    }

    /**
     * 校验指定供应商是否可以进行报价
     * PS: 首轮，如果是邀请报价，必须在受邀行列；如果是公开报价，则无限制
     * PS: 非首轮，必须是上轮次入围供应商，或当前轮次新加入的供应商
     * PS: 如果本轮报价被采购商作废，则不能报价
     * PS: 受控于寻源状态，必须在可报价的时间范围内:
     * * 报价未开始+已到达报价时间+未到达报价截止时间
     * * 接收报价中+未到达报价截止时间
     *
     * @param projectId 询价单ID
     * @param vendorId  供应商ID
     * @param isBuyer   true-采购商代理报价、false-供应商报价
     */
    public void judgeOrderAuth(long projectId, long vendorId, boolean isBuyer, String souType) {
        SouProject souProject = souProjectDao.getById(projectId);
        AssertUtils.notNull(souProject, LocaleHandler.getLocaleMsg("寻源单") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(souProject.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型") + "[{0}]" + LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isTrue(!isBuyer || Enable.Y.equals(souProject.getAllowProxyOrder()), "该寻源单禁止代理报价");

        /* 2. 是否处于可报价(时间/状态)范围内：报价未开始 or 接收报价中，且处于报价时间范围内 */
        switch (souProject.getProjectStatus()) {
            //报价未开始
            case ORDER_NOT_START:
                //接收报价中
            case ACCEPT_ORDER:
                break;
                //已截止报价
            case ORDER_END:
                throw new IllegalArgumentException("已截止报价，禁止操作");
            default:
                throw new IllegalArgumentException("当前单据状态禁止该操作");
        }
        Date now = new Date();
        boolean availableTime = souProject.getOrderStartTime().before(now);
        AssertUtils.isTrue(availableTime, "未到报价时间，禁止操作");
        availableTime = souProject.getOrderEndTime().after(now);
        AssertUtils.isTrue(availableTime, "报价时间已截止，禁止操作");
        //本轮报价是否被提交/作废
        SouOrder order = souOrderDao.lambdaQuery()
                /* 指定询价单 */
                .eq(SouOrder::getProjectId, souProject.getProjectId())
                /* 指定供应商 */
                .eq(SouOrder::getVendorId, vendorId)
                /* 指定轮次 */
                .eq(SouOrder::getRound, souProject.getCurrentRound())
                .one();
        if (order != null) {
            if (order.getOrderRound().equals(souProject.getOrderNum())) {
                throw new BaseException("报价次数已达上限，禁止报价");
            } else if (SouOrderStatusEnum.CANCEL.equals(order.getOrderStatus())) {
                throw new BaseException("报价已作废，禁止报价");
            }
        }
    }


    public void judgeOrderAuth1(ApiSouOrderDTO param,long projectId, long vendorId, boolean isBuyer, String souType) {
        SouProject souProject = souProjectDao.getById(projectId);
        AssertUtils.notNull(souProject, LocaleHandler.getLocaleMsg("寻源单") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(souProject.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型") + "[{0}]" + LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isTrue(!isBuyer || Enable.Y.equals(souProject.getAllowProxyOrder()), "该寻源单禁止代理报价");

        // 2. 是否处于可报价(时间/状态)范围内：报价未开始 or 接收报价中，且处于报价时间范围内
        switch (souProject.getProjectStatus()) {
            //报价未开始
            case ORDER_NOT_START:
                //接收报价中
            case ACCEPT_ORDER:
                break;
                //已截止报价
            case ORDER_END:
                throw new IllegalArgumentException("已截止报价，禁止操作");
            default:
                throw new IllegalArgumentException("当前单据状态禁止该操作");
        }
        Date now = new Date();
        boolean availableTime = souProject.getOrderStartTime().before(now);
        AssertUtils.isTrue(availableTime, "未到报价时间，禁止操作");
        availableTime = souProject.getOrderEndTime().after(now);
        AssertUtils.isTrue(availableTime, "报价时间已截止，禁止操作");
        // 3. 当前轮次是否有可报价物料
        // 4. 本轮报价是否被提交/作废
        SouOrder order = souOrderDao.lambdaQuery()
                // 指定询价单
                .eq(SouOrder::getProjectId, souProject.getProjectId())
                // 指定供应商
                .eq(SouOrder::getVendorId, vendorId)
                // 指定轮次
                .eq(SouOrder::getRound, souProject.getCurrentRound())
                .one();
        param.getOrderItemList().get(0);
        if (order != null) {

            Long souItemId = param.getOrderItemList().get(0).getSouItemId();
            Long souvendorId = param.getOrderItemList().get(0).getVendorId();
            List<SouOrderItem> souOrderItemList = souOrderItemDao.lambdaQuery()
                    .eq(SouOrderItem::getSouItemId, souItemId)
                    .eq(SouOrderItem::getVendorId,souvendorId)
                    .list();
            int round =0;
            if(souOrderItemList.size()>0){
                if(null != souOrderItemList.get(0).getOrderRound()){
                    round = souOrderItemList.get(0).getOrderRound();
                }

            }


            log.info("===报价次数上线为=="+souProject.getOrderNum());
            log.info("===该物料报价次数为=="+round);

            //if (order.getOrderRound().equals(souProject.getOrderNum())) {
            if (souProject.getOrderNum().equals(round) && round > 0) {
                throw new BaseException("报价次数已达上限，禁止报价");
            } else if (SouOrderStatusEnum.CANCEL.equals(order.getOrderStatus())) {
                throw new BaseException("报价已作废，禁止报价");
            }
        }
    }

    public void judgeImportOrderItemQuoteTempExcel(long projectId, long vendorId, long souItemId, boolean isBuyer, String souType) {

        this.judgeOrderAuth(projectId, vendorId, isBuyer, souType);
        SouProject souProject = souProjectDao.getById(projectId);
        Set<Long> availableItemIds = SouActiveBeanUtils.getActiveBean(souProject.getSouType(), ApiSouOrderQueryHandler.class)
                .getAvailableItemsForVendor(souProject.getProjectId(), souProject.getCurrentRound(), vendorId)
                .stream().map(SouItem::getSouItemId).collect(Collectors.toSet());
        AssertUtils.isTrue(availableItemIds.contains(souItemId), "对物料需求[{0}]没有报价权限", souItemId);
    }

    @ApiOperation("当前操作人是否可以作废报价")
    public SouOrder judgeCancelOrderAuth(ApiSouOrderCancelDTO param, String souType) {
        SouProject project = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(project, LocaleHandler.getLocaleMsg("寻源单") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        AssertUtils.isTrue(project.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型") + "[{0}]" + LocaleHandler.getLocaleMsg("不匹配"), souType);
        switch (project.getProjectStatus()) {
            //接收报价中
            case ACCEPT_ORDER:
                //报价已截止
            case ORDER_END:
                break;
                //商务开标
            case BUSINESS_EVAL:
                throw new IllegalArgumentException("已商务开标，禁止作废报价");
                //技术开标
            case TECH_EVAL:
                throw new IllegalArgumentException("已技术开标，禁止作废报价");
            default:
                throw new IllegalArgumentException("当前单据状态禁止操作");
        }
        Date now = new Date();
        boolean availableTime = project.getOrderStartTime().before(now);
        AssertUtils.isTrue(availableTime, "未到报价时间，禁止操作");
        availableTime = project.getOrderEndTime().after(now);
        AssertUtils.isTrue(availableTime, "报价时间已截止，禁止操作");
        SouOrder order = souOrderDao.lambdaQuery()
                .eq(SouOrder::getProjectId, param.getProjectId())
                .eq(SouOrder::getRound, project.getCurrentRound())
                .eq(SouOrder::getVendorId, param.getVendorId())
                .one();
        AssertUtils.notNull(order, "供应商在当前轮次没有报价");
        AssertUtils.isTrue(order.getRound().equals(project.getCurrentRound()), "非最新轮次报价，禁止作废");
        AssertUtils.isFalse(SouOrderStatusEnum.DRAFT.equals(order.getOrderStatus()), "供应商尚未提交本轮报价");
        return order;
    }

    @ApiOperation("校验是否能撤回指定的报价单")
    public SouProject judgeRollbackAuth(long projectId, long vendorId, String souType) {
        SouProject souProject = souProjectDao.getById(projectId);
        AssertUtils.notNull(souProject, LocaleHandler.getLocaleMsg("寻源单") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), projectId);
        AssertUtils.isTrue(souProject.getSouType().equals(souType), LocaleHandler.getLocaleMsg("寻源类型") + "[{0}]" + LocaleHandler.getLocaleMsg("不匹配"), souType);
        AssertUtils.isTrue(Enable.Y.equals(souProject.getAllowWithdraw()), "禁止撤回报价");
        switch (souProject.getProjectStatus()) {
            //报价未开始
            case ORDER_NOT_START:
                //接收报价中d
            case ACCEPT_ORDER:
                break;
                //报价已截止
            case ORDER_END:
                throw new IllegalArgumentException("本轮报价时间已截止");
            default:
                throw new IllegalArgumentException("当前单据状态禁止该操作");
        }

        SouOrder souOrder = souOrderDao.lambdaQuery()
                //指定询价单
                .eq(SouOrder::getProjectId, souProject.getProjectId())
                //指定供应商
                .eq(SouOrder::getVendorId, vendorId)
                //指定轮次
                .eq(SouOrder::getRound, souProject.getCurrentRound())
                .one();
        AssertUtils.notNull(souOrder, "无当前轮次报价信息，撤回失败");
        AssertUtils.isTrue(SouOrderStatusEnum.SUBMISSION.equals(souOrder.getOrderStatus()), "禁止操作");
        return souProject;
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
