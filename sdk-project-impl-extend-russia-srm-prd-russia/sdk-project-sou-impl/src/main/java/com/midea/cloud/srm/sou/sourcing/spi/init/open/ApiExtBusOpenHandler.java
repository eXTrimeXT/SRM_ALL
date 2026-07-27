package com.midea.cloud.srm.sou.sourcing.spi.init.open;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.constant.DingTalkConstant;
import com.midea.cloud.common.dingtalks.DingTalkClient;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.sou.approve.entity.SouApproveUser;
import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.enums.ExtSouOrderTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBidSouModeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouOrderItemDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.approve.service.ISouApproveUserService;
import com.midea.cloud.srm.sou.bid.openrecords.service.IExtNpmSouOpenBidRecordService;
import com.midea.cloud.srm.sou.sourcing.fixstatus.service.SouFixedProjectStatusService;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusFactory;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusRangeVo;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtBusOpenHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouPlanService planService;
    @Autowired
    private IExtSouTechScoreHeadService techScoreHeadService;

    @Autowired
    private SouFixedProjectStatusService fixedProjectStatusService;

    @Autowired
    private IExtNpmSouOpenBidRecordService openBidRecordService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private ISouApproveUserService iSouApproveUserService;

    /**
     * 客户端
     */
    @Autowired
    private BaseClient baseClient;
    /**
     * 发送钉钉客户端
     */
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private ExtNpmSouOpenTodoService extNpmSouOpenTodoService;

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @ApiOperation("当前是否可以商务开标")
    public void judgeOpenBusAuth(long projectId, String souType) {
        //修正单据状态
        fixedProjectStatusService.fixedProjectStatus(projectService.getById(projectId), souType);

        //1.校验数据
        ExtSouProject project = projectService.getById(projectId);
        //商务报价中
        if(SouBiddingProStatusEnum.BUS_BID_END.getCode().equals(project.getProjectStatus())) {
            return;
        }

        if(StringUtils.equals(project.getExtSouMode(), SouBidSouModeEnum.TECH_THEN_BUS.getCode())){
            //先收技术后收商务
            if(Arrays.asList(SouBiddingProStatusEnum.BUS_BID_END).contains(SouBiddingProStatusEnum.valueOf(project.getProjectStatus()))) {
                return;
            }
        }else if(StringUtils.equals(project.getExtSouMode(), SouBidSouModeEnum.SAME_TIME.getCode())){
            //同时收标
            if(StringUtils.equals(project.getOrderType(),ExtSouOrderTypeEnum.TECHNOLOGY_BUSINESS.getCode())
                &&Arrays.asList(SouBiddingProStatusEnum.TECH_BID_EVA_DONE).contains(SouBiddingProStatusEnum.valueOf(project.getProjectStatus()))){
                return;
            }else if(StringUtils.equals(project.getOrderType(),ExtSouOrderTypeEnum.BUSINESS.getCode())
            &&Arrays.asList(SouBiddingProStatusEnum.BUS_BID_END).contains(SouBiddingProStatusEnum.valueOf(project.getProjectStatus()))){
                return;
            }
        }
        throw new BaseException("当前状态不允许商务开标！");
    }

    public ExtBusOpenEditPO doFormatevalidAndConvert(Long projectId, String souType) {

        //1.校验数据
        ExtSouProject project = this.doFormtevalid(projectId, souType);

        //操作记录
        openBidRecordService.openRecord(projectId, project.getCurrentRound(), ExtOrderTypeEnum.BUS.getCode());

        ExtBusOpenEditPO po = new ExtBusOpenEditPO();

        //招标负责人 和 招标部长都已开标时 更新状态
        if(openBidRecordService.isOpenByAllUser(projectId, project.getCurrentRound(), ExtOrderTypeEnum.BUS.getCode())) {
            po.setSouProject(project);
        } else {
            extNpmSouOpenTodoService.sendTodo(Collections.singletonList(project));
        }

        return po;
    }

    private ExtSouProject doFormtevalid(Long projectId, String souType) {
        ExtSouProject souProject = projectService.getById(projectId);
        AssertUtils.notNull(souProject, "寻源单据信息不存在！");

        if(SouBiddingProStatusEnum.BUS_BID_END.getCode().equals(souProject.getProjectStatus())) {
            souProject.setProjectStatus(SouBiddingProStatusEnum.BUS_BID_OPEN.getCode());
            return souProject;
        }

        SouBiddingProStatusEnum proStatusEnum = SouBiddingProStatusEnum.valueOf(souProject.getProjectStatus());

        //同时收标，技术+商务（简易招标、竞争性谈判） 技术评标中 且 已确认评标 -> 商务标已开标
        if(SouBidSouModeEnum.SAME_TIME.getCode().equals(souProject.getExtSouMode()) && ExtSouOrderTypeEnum.TECHNOLOGY_BUSINESS.getCode().equals(souProject.getOrderType())) {
            if(!Arrays.asList(SouBiddingProStatusEnum.TECH_BID_EVA_DONE).contains(proStatusEnum)) {
                throw new BaseException(MessageFormat.format("非{0}状态，不允许商务开标！", SouBiddingProStatusEnum.TECH_BID_EVA_DONE.getName()));
            }
        } else {
            /**
             * 同时收标，商务 商务标已截止->商务标已开标
             * 先收技术后收商务（标准招标） 商务标已截止->商务标已开标
             */
            if(!Arrays.asList(SouBiddingProStatusEnum.BUS_BID_END).contains(proStatusEnum)) {
                throw new BaseException(MessageFormat.format("非{0}状态，不允许商务开标！", SouBiddingProStatusEnum.BUS_BID_END.getName()));
            }
        }
        souProject.setProjectStatus(SouBiddingProStatusEnum.BUS_BID_OPEN.getCode());
        return souProject;
    }

    @ApiOperation("商务开标后置处理")
    public void doHandlerAfterOpenBusTech(long projectId, String souType, ExtBusOpenEditPO po) {
        if(!Objects.isNull(po.getSouProject())) {
            //更新实际商务开标时间
            planService.applyAtualPoint(projectId, new Date(), ExtSouPlan::getPriceOpenTime, false);
            //钉钉通知 招标书审批人 后一次报价比上一次价格上涨
            dingTalk(projectId, souType, po);
        }
    }

    protected void dingTalk(long projectId, String souType, ExtBusOpenEditPO po) {
        log.info(MessageFormat.format("dingTalkBid招标项目[{0}]预警后一次报价比上一次价格上涨开始", projectId));
        //查询报价信息
        List<ExtSouOrderItem> orderItemList = orderItemService.lambdaQuery().eq(ExtSouOrderItem::getProjectId, projectId).eq(ExtSouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name()).orderByDesc(ExtSouOrderItem::getRound).list();
        if(CollectionUtils.isEmpty(orderItemList)) {
            log.info(MessageFormat.format("dingTalkBid招标项目[{0}]预警后一次报价比上一次价格上涨 无报价信息", projectId));
            return;
        }
        //记录轮次 供应商ID 报价信息ID  轮次信息
        Map<String, List<Integer>> vendorRoundMap = new HashMap<>(15);
        Map<String, ApiExtSouOrderItemDto> orderItemMap = new HashMap<>(15);

        //只取最新一轮报价的供应商对比价
        int maxRound = orderItemList.stream().mapToInt(ExtSouOrderItem::getRound).max().getAsInt();
        //筛选出最新一轮的供应商
        Set<String> checkVendorSet = orderItemList.stream().filter(s -> s.getRound() == maxRound)
                .map(s -> StringUtils.joinWith(SrmConstant.UNDER_LINE, s.getVendorId(), s.getSouItemId()))
                        .collect(Collectors.toSet());


        orderItemList.stream().forEach(orderItem -> {
            ApiExtSouOrderItemDto orderItemDto = new ApiExtSouOrderItemDto();
            BeanCopyUtil.copyProperties(orderItemDto, orderItem);
            //将报价信息转换成报价信息表的字段
            orderItemDto.coverItemFields();
            String vendorRoundKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, orderItem.getVendorId(), orderItem.getSouItemId());
            String orderItemKey = StringUtils.joinWith(SrmConstant.UNDER_LINE, orderItem.getVendorId(), orderItem.getSouItemId(), orderItem.getRound());
            if(checkVendorSet.contains(vendorRoundKey)) {
                if(!vendorRoundMap.containsKey(vendorRoundKey)) {
                    vendorRoundMap.put(vendorRoundKey, new ArrayList<>());
                }
                if(!vendorRoundMap.get(vendorRoundKey).contains(orderItem.getRound())) {
                    vendorRoundMap.get(vendorRoundKey).add(orderItem.getRound());
                }

                orderItemMap.put(orderItemKey, orderItemDto);
            }
        });

        /** 后一次报价比上一次价格上涨 */
        List<Long> priceUpVendorIdList = new ArrayList<>();

        for (String vendorRoundKey : vendorRoundMap.keySet()) {
            List<Integer> roundList = vendorRoundMap.get(vendorRoundKey).stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            if(roundList.size() == 1) {
                continue;
            }
            log.info(JSON.toJSONString(roundList));
            String orderItemKey1 = StringUtils.joinWith(SrmConstant.UNDER_LINE, vendorRoundKey, roundList.get(0));
            String orderItemKey2 = StringUtils.joinWith(SrmConstant.UNDER_LINE, vendorRoundKey, roundList.get(1));

            ApiExtSouOrderItemDto orderItemDto1 = orderItemMap.get(orderItemKey1);
            ApiExtSouOrderItemDto orderItemDto2 = orderItemMap.get(orderItemKey2);

            if(ObjectUtils.anyNull(orderItemDto1, orderItemDto2)) {
                continue;
            }

            //含税单价
            BigDecimal extPriceTax1 = orderItemDto1.getExtPriceTax();
            BigDecimal extPriceTax2 = orderItemDto2.getExtPriceTax();

            if(ObjectUtils.allNotNull(extPriceTax1, extPriceTax2)) {
                if(extPriceTax1.compareTo(extPriceTax2) == 1) {
                    if(!priceUpVendorIdList.contains(orderItemDto1.getVendorId())) {
                        priceUpVendorIdList.add(orderItemDto1.getVendorId());
                        continue;
                    }
                }
            }

            //固定含税单价
            BigDecimal extFixedPriceTax1 = orderItemDto1.getExtFixedPriceTax();
            BigDecimal extFixedPriceTax2 = orderItemDto2.getExtFixedPriceTax();

            if(ObjectUtils.allNotNull(extFixedPriceTax1, extFixedPriceTax2)) {
                if(extFixedPriceTax1.compareTo(extFixedPriceTax2) == 1) {
                    if(!priceUpVendorIdList.contains(orderItemDto1.getVendorId())) {
                        priceUpVendorIdList.add(orderItemDto1.getVendorId());
                    }
                }
            }

        }

        /** 存在报价上涨情况 */
        if(CollectionUtils.isEmpty(priceUpVendorIdList)) {
            log.info(MessageFormat.format("dingTalkBid招标项目[{0}]预警后一次报价比上一次价格上涨 无报价上涨供应商", projectId));
            return;
        }

        SouApproveUser approveUser = iSouApproveUserService.getNewestApproveUser(projectId);
        if(ObjectUtils.anyNull(approveUser)) {
            log.info(MessageFormat.format("dingTalkBid招标项目[{0}]预警后一次报价比上一次价格上涨 无审批人信息", projectId));
            return;
        }

        List<ExtSouVendor> vendorList = vendorService.lambdaQuery().in(ExtSouVendor::getVendorId, priceUpVendorIdList).eq(ExtSouVendor::getProjectId, projectId).list();
        Map<String, String> var = new HashMap<>(15);
        var.put("${souNo}", po.getSouProject().getExtProjectNo());
        var.put("${souName}", po.getSouProject().getSouName());
        var.put("${vendorName}", vendorList.stream().map(v->v.getVendorName()).distinct().collect(Collectors.joining(SrmConstant.SIG_3)));

        DingTalkClient.newInstance(baseClient, pjProjectExtClient).sendDingTalk(Collections.singletonList(approveUser.getUserName()), DingTalkConstant.SOU_BID_PRICE_UP, var);
        log.info(MessageFormat.format("dingTalkBid招标项目[{0}]预警后一次报价比上一次价格上涨结束 通知账号-{1}，通知供应商-{2}", projectId, approveUser.getUserName(), vendorList.stream().map(v->v.getVendorName()).distinct().collect(Collectors.joining(SrmConstant.SIG_3))));
    }
}
