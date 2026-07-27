package com.midea.cloud.srm.sou.sourcing.spi.init.startprices;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.constant.SmsConstant;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.sms.SmsClient;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.base.service.NoticeSendGlobalClientService;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouBiddingProStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtRoundDto;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouItemDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.sou.sourcing.init.service.*;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusFactory;
import com.midea.cloud.srm.sou.sourcing.spi.init.editprojectstatuss.ApiProjectStatusRangeVo;
import com.midea.cloud.srm.sou.sourcing.spi.init.editsouitems.ExtSouItemEditPO;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderItemService;
import com.midea.cloud.srm.sou.sourcing.vendor.service.IExtSouOrderService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class ApiExtStartPriceEditHandler implements ISouSpiBean {

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private IExtSouRoundService souRoundService;

    @Autowired
    private IExtSouOrderService orderService;

    @Autowired
    private IExtSouOrderItemService orderItemService;

    @Autowired
    private IExtSouItemService itemService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private IExtNpmSouOrderService extNpmSouOrderService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private IExtSouVendorService vendorService;

    private static final String SEQ_NO = "SEQ_INQ_QUOTENO_CODE";

    @Value("${global.srm.register-address:没有配置地址}")
    private String cloudUrl;

    @Autowired
    private NoticeSendGlobalClientService noticeSendGlobalClientService;


    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @ApiOperation("当前是否可以商务开标")
    public void judgeStartPriceAuth(ApiExtRoundDto param, String souType) {

    }

    public ExtStartPriceEditPO formatValidateAndConvert(ApiExtRoundDto param, String souType) {
        // 1: 数据格式化及校验
        this.formatAndValidate(param, souType);
        // 2: 数据转换
        return this.convert(param, souType);
    }

    /**
     * 数据格式化及校验
     *
     * @param param
     * @param souType
     */
    private void formatAndValidate(ApiExtRoundDto param, String souType) {
        ExtSouProject souProject = projectService.getById(param.getProjectId());
        AssertUtils.notNull(souProject, "项目信息不存在！");

        if(!Arrays.asList(SouBiddingProStatusEnum.TECH_BID_EVA_DONE.getCode(), SouBiddingProStatusEnum.BUS_BID_OPEN.getCode()).contains(souProject.getProjectStatus())) {
            throw new BaseException("未完成技术评分或未开标状态下不允许组织商务报价！");
        }
    }

    /**
     * 数据转换
     * @param param 参数
     * @param souType 参数
     * @return 返回
     */
    protected ExtStartPriceEditPO convert(ApiExtRoundDto param, String souType) {
        ExtStartPriceEditPO po = new ExtStartPriceEditPO();

        ExtSouProject project = this.doConvertExtSouProject(param, souType);
        //生成轮次表
        po.setRound(this.doConvertExtSouRound(param, project, souType));

        //转换基本信息表
        po.setProject(project);

        //生成报价表
        po.setOrderList(this.doConvertSouOrder(param, souType, po.getRound()));

        //生成报价明细
        po.setOrderItemList(this.doConvertSouOrderItem(param, souType, po.getOrderList()));

        //更新报价数量
        po.setItemList(this.doConvertSouItem(param, souType));

        return po;
    }

    protected List<ExtSouItem> doConvertSouItem(ApiExtRoundDto param, String souType) {

        if(!YesOrNo.YES.getValue().equals(param.getExtPriceFlag())) {
            return null;
        }

        List<ExtSouItem> itemList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(param.getItemList())) {
            LambdaQueryWrapper<ExtSouItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ExtSouItem::getProjectId, param.getProjectId());
            queryWrapper.in(ExtSouItem::getSouItemId, param.getItemList().stream().map(ExtSouItem::getSouItemId).collect(Collectors.toList()));

            List<ExtSouItem> souItemList = itemService.list(queryWrapper);
            Map<Long, ExtSouItem> souItemMap = souItemList.stream().collect(Collectors.toMap(ExtSouItem::getSouItemId, Function.identity()));

            param.getItemList().stream().forEach(item -> {
                ExtSouItem souItem = souItemMap.get(item.getSouItemId());
                if(!Objects.isNull(souItem)) {
                    souItem.setExtQuantity(item.getExtQuantity());
                    itemList.add(item);
                }
            });
        }
        return itemList;
    }

    protected List<ExtSouOrder> doConvertSouOrder(ApiExtRoundDto param, String souType, ExtSouRound round) {

        LambdaQueryWrapper<ExtSouOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouOrder::getProjectId, param.getProjectId());

        List<ExtSouOrder> orderList = orderService.list(queryWrapper);
        Map<Long, ExtSouOrder> orderMap = orderList.stream().collect(Collectors.toMap(o->o.getVendorId(), Function.identity(), (k1, k2)->k2));

        List<ExtSouOrder> souOrderList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(param.getVendorList())) {
            param.getVendorList().forEach(verdor -> {
                ExtSouOrder souOrder = new ExtSouOrder();
                if(orderMap.containsKey(verdor.getVendorId())) {
                    souOrder = orderMap.get(verdor.getVendorId());
                    souOrder.setRound(round.getRound());
                    souOrder.setOrderStatus(SouOrderStatusEnum.DRAFT);
                } else {
                    souOrder.setProjectId(param.getProjectId());
                    souOrder.setRound(round.getRound());
                    souOrder.setVendorId(verdor.getVendorId());
                    //创建ID
                    souOrder.setOrderId(IdGenrator.generate());
                    //创建单号
                    souOrder.setOrderNo(baseClient.seqGen(SEQ_NO));
                    //报价单状态：字典 SOU_ORDER_STATUS
                    souOrder.setOrderStatus(SouOrderStatusEnum.DRAFT);
                    souOrder.setIsProxy(Enable.N);
                }
                souOrder.setExtOrderType(ExtOrderTypeEnum.BUS.getCode());
                souOrderList.add(souOrder);

            });
        }

        return souOrderList;
    }

    protected List<ExtSouOrderItem> doConvertSouOrderItem(ApiExtRoundDto param, String souType, List<ExtSouOrder> souOrderList) {
        if(CollectionUtils.isEmpty(souOrderList)) {
            return new ArrayList<>();
        }

        Integer round = souOrderList.get(0).getRound();
        LambdaQueryWrapper<ExtSouOrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouOrderItem::getProjectId, param.getProjectId());
        queryWrapper.eq(ExtSouOrderItem::getRound, round);
        queryWrapper.in(ExtSouOrderItem::getOrderId, souOrderList.stream().map(ExtSouOrder::getOrderId).collect(Collectors.toList()));

        List<ExtSouOrderItem> orderItemList = orderItemService.list(queryWrapper);
        Map<String, ExtSouOrderItem> orderItemMap = orderItemList.stream().collect(Collectors.toMap(i -> StringUtils.joinWith("_", i.getVendorId(), i.getSouItemId()), Function.identity(), (k1, k2)->k2));


        List<ExtSouOrderItem> saveList = new ArrayList<>();
        //查询报价信息
        LambdaQueryWrapper<ExtSouItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(ExtSouItem::getProjectId, param.getProjectId());
        List<ExtSouItem> itemList = itemService.list(itemQuery);

        //查询上一轮次的报价信息
        Map<String, ExtSouOrderItem> lastRoudItemMap = new HashMap<>(50);
        if(Integer.compare(round, 1) == 1) {
            List<ExtSouOrderItem> lastRounItem = orderItemService.lambdaQuery().eq(ExtSouOrderItem::getProjectId, param.getProjectId())
//                    .eq(ExtSouOrderItem::getRound, round -1)
                    .eq(ExtSouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION.name())
                    .orderByAsc(ExtSouOrderItem::getRound).list();
            lastRoudItemMap = lastRounItem.stream().collect(Collectors.toMap(k -> StringUtils.joinWith("_", k.getVendorId(), k.getSouItemId()), Function.identity(), (k1, k2)->k2));
        }

        //生成报价明细
        Map<String, ExtSouOrderItem> finalLastRoudItemMap = lastRoudItemMap;
        souOrderList.stream().forEach(order -> {
            itemList.stream().forEach(item -> {
                ExtSouOrderItem orderItem = new ExtSouOrderItem();

                String key = StringUtils.joinWith("_", order.getVendorId(), item.getSouItemId());

                //上一轮次存在，则自动带出上一轮的报价信息
                if(finalLastRoudItemMap.containsKey(key)) {
                    ExtSouOrderItem lastRounItem = finalLastRoudItemMap.get(key);
                    lastRounItem.setOrderItemId(null);
                    lastRounItem.setOrderStatus(null);
                    BeanCopyUtil.copyProperties(orderItem, lastRounItem);
                }

                if(orderItemMap.containsKey(key)) {
                    orderItem = orderItemMap.get(key);
                } else {
                    copyItemToOrderItem(item, orderItem);
                    if(Objects.isNull(orderItem.getNoCodeItem())) {
                        orderItem.setNoCodeItem(Enable.Y);
                    }
                    if(Objects.isNull(orderItem.getIsProxy())) {
                        orderItem.setIsProxy(Enable.N);
                    }
                    if(Objects.isNull(orderItem.getWinStatus())) {
                        orderItem.setWinStatus(SouWinStatusEnum.D);
                    }

                    if(Objects.isNull(orderItem.getOrderItemId())) {
                        orderItem.setOrderItemId(IdGenrator.generate());
                    }
                    orderItem.setOrderId(order.getOrderId());
                    orderItem.setVendorId(order.getVendorId());
                    orderItem.setProjectId(param.getProjectId());
                }


                orderItem.setRound(order.getRound());
                orderItem.setOrderStatus(order.getOrderStatus());

                saveList.add(orderItem);
            });
        });

        return saveList;
    }

    protected void copyItemToOrderItem(ExtSouItem item, ExtSouOrderItem orderItem) {
        orderItem.setSouItemId(item.getSouItemId());
        orderItem.setItemGroup(item.getItemGroup());
        orderItem.setNoCodeItem(item.getNoCodeItem());
        orderItem.setItemId(item.getItemId());
        orderItem.setItemCode(item.getItemCode());
        orderItem.setItemDesc(item.getItemDesc());
        orderItem.setUnit(item.getUnit());
        orderItem.setCategoryId(item.getCategoryId());
        orderItem.setCategoryCode(item.getCategoryCode());
        orderItem.setCategoryName(item.getCategoryName());
    }

    protected ExtSouProject doConvertExtSouProject(ApiExtRoundDto param, String souType) {

        ExtSouProject project = projectService.getById(param.getProjectId());
        //如果是技术评分，轮次不变
        if(SouBiddingProStatusEnum.TECH_BID_EVA_DONE.getCode().equals(project.getProjectStatus())) {
            project.setCurrentRound(1);
        } else {
            project.setCurrentRound(ObjectUtils.defaultIfNull(project.getCurrentRound(), 0) +1);
        }

        project.setProjectStatus(SouBiddingProStatusEnum.BUS_BID.getCode());
        return project;
    }

    protected ExtSouRound doConvertExtSouRound(ApiExtRoundDto param, ExtSouProject project, String souType) {
        //获取最大轮次
        LambdaQueryWrapper<ExtSouRound> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouRound::getProjectId, param.getProjectId());
        queryWrapper.eq(ExtSouRound::getRound, project.getCurrentRound());
        queryWrapper.orderByDesc(ExtSouRound::getRound);
        PageUtil.startPage(1, 1);
        List<ExtSouRound> roundList = souRoundService.list(queryWrapper);

        ExtSouRound souRound = new ExtSouRound();
        souRound.setProjectId(param.getProjectId());
        souRound.setExtOrderReason(param.getExtOrderReason());
        souRound.setExtPriceFlag(param.getExtPriceFlag());
        souRound.setRound(project.getCurrentRound());
        souRound.setOrderStartTime(new Date());
        souRound.setOrderEndTime(param.getOrderEndTime());
        souRound.setHasPublishResult(Enable.N);
        souRound.setBusinessOpen(Enable.N);
        souRound.setPriceDecrypt(Enable.N);
        if(CollectionUtils.isNotEmpty(roundList)) {
            souRound.setRoundId(roundList.get(0).getRoundId());
        } else {
            souRound.setRoundId(IdGenrator.generate());
        }

        return souRound;
    }

    @ApiOperation("报价信息保存前的额外处理")
    public void doHandlerBeforeEditStartPrice(ApiExtRoundDto param, String souType) {
    }

    @ApiOperation("报价信息保存后的额外处理")
    public void doHandlerAfterEditStartPrice(ApiExtRoundDto param, String souType, ExtStartPriceEditPO po) {
        //生成报价扩展表数据
        extNpmSouOrderService.extendSouOrder(po.getOrderList());

        //短信通知
        ExtSouProject project = po.getProject();

        //短信发送客户端
        SmsClient smsClient = SmsClient.newInstance(baseClient, pjProjectExtClient);
        //发送短信
        po.getOrderList().stream().forEach(order -> {
            Map<String, String> var = new HashMap<>(15);
            var.put("${souProject.souName}", project.getSouName());
            var.put("${souProject.souNo}", project.getExtProjectNo());
            var.put("${souProject.linkMan}", project.getLinkman());
            var.put("${souProject.tel}", project.getTel());
            smsClient.sendSms(order.getExtTenderPhone(), SmsConstant.SOU_BID_START_PRICE, var);
        });
        param.getVendorList().forEach(vendor -> {
            sendEmail(project.getSouName(), vendor.getEmail());
        });

    }

    public void sendEmail(String souName, String email){
        String msgTemplateCode = "PJ_BIDDING_START_PRICE_VENDOR_NOTICE";
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setMsgTemplateCode(msgTemplateCode);
        noticeSendDTO.setMsgUuid(UUID.randomUUID().toString());
        Map<String, Object> msgParams = new HashMap(15);
        msgParams.put(NoticeSendDTO.NOTICE_RECEIVER_INFO, email);
        msgParams.put("projectName", souName);
        msgParams.put("baseUrl", cloudUrl);
        noticeSendDTO.setMsgParams(msgParams);
        noticeSendGlobalClientService.send(noticeSendDTO);
    }
}
