package com.midea.cloud.srm.supcooperate.ext.order.repo;

import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pj.base.organization.entity.OrgInvoiceInfo;
import com.midea.cloud.srm.model.sou.req.SouInviteHistory;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouVendor;
import com.midea.cloud.srm.model.suppliercooperate.mql.enums.PurchaseSchemaEnum;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.Order;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.OrderDetailStatus;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.PurchaseOrderEnum;
import com.midea.cloud.srm.po.order.service.OrderRelationService;
import com.midea.cloud.srm.supcooperate.ext.checkorders.dto.CheckOrderDetail;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrder;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrderDetail;
import com.midea.cloud.srm.supcooperate.ext.order.enums.ExtOrderStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.order.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.supcooperate.ext.order.mapper.ExtOrderTodoMapper;
import com.midea.cloud.srm.supcooperate.ext.order.service.ExtOrderService;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.OrderPriceParams;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementLineDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.RequirementSelectionQueryDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.enums.PrBuyTypeEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.mapper.PurchaseRequirementMapper;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.PurchaseRequirementService;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import com.midea.cloud.srm.supcooperate.meiql.order.dto.OrderSaveDTO;
import com.midea.cloud.srm.supcooperate.meiql.util.PurchaseMqlUtils;
import com.midea.cloud.srm.supcooperate.openapi.UserTypeUtils;
import com.midea.cloud.srm.supcooperate.spi.openapi.order.save.OrderAmountSpiService;
import com.midea.cloud.srm.supcooperate.spi.openapi.order.save.OrderBeforeSaveSpiService;
import com.midea.cloud.srm.supcooperate.spi.openapi.order.save.OrderRequirementSpiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Slf4j
@Component
public class ExtOrderRepository extends PurchaseRepository<Order> {

    private static final String EQ = "eq";
    private static final String MANUAL = "MANUAL";
    private static final String EXTERNAL_ID = "externalId";
    private static final String STR200 = "200";
    private static final String CODE = "code";


    public ExtOrderRepository() {
        super("Order", "orderId", "订单");

        this.register("extSaveOrUpdate", this::extSaveOrUpdate, true, "暂存/提交采购订单");
        this.register("extRevoke", this::extRevoke, true, "撤回");
        this.register("extCancel", this::extCancel, true, "取消");
        this.register("extCreateByRequirement", this::extCreateByRequirement, true, "选择协议单创建订单");
        this.register("extCreateByFixPrice", this::extCreateByFixPrice, true, "选择定价单创建订单");
        this.register("extSupplierConfirm", this::extSupplierConfirm, true, "供应商确认");
        this.register("extSupplierRefuse", this::extSupplierRefuse, true, "供应商拒绝");
        this.register("extApprove", this::extApprove, true, "审批通过");
        this.register("extReject", this::extReject, true, "审批驳回");
        this.register("extWithdraw", this::extWithdraw, true, "审批撤回");
    }

    @Autowired
    private ExtOrderService extOrderService;
    @Autowired
    private PurchaseRequirementService purchaseRequirementService;
    @Autowired
    private OrderRelationService orderRelationService;
    @Autowired
    private QlService qlService;
    @Autowired
    private OrderAmountSpiService orderAmountSpiService;
    @Autowired
    private OrderRequirementSpiService orderRequirementSpiService;
    @Autowired
    private OrderBeforeSaveSpiService orderBeforeSaveSpiService;
    @Autowired
    private PurchaseRequirementMapper purchaseRequirementMapper;

    @Autowired
    private ExtOrderTodoMapper extOrderTodoMapper;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    private static final String IF_CREAT_DELIVERY = "ifCreatDelivery";

    @Override
    public QlResult query(QlQueryAction queryAction) {
        try {
            /**
             * {"type":"Order","action":"query",
             * "payload":{"filter":{"orderNumber":{"contains":"34343"}},"page":{"pageNum":1,"pageSize":15,"sort":"lastUpdateDate desc"}},"query":{"*":{}},"lang":"zh-cn","tree":true,"__page":1,"__pagesize":15}
             */
            if(MqlType.ORDER_VENDOR.equals(queryAction.getType())) {
                Object object = queryAction.getPayload();
                if(object instanceof QueryParam) {
                    QueryParam queryParam = (QueryParam) object;
                    if(queryParam.getFilter().containsKey(IF_CREAT_DELIVERY) && YesOrNo.YES.getValue().equals(MapUtils.getString(queryParam.getFilter().getValue(IF_CREAT_DELIVERY), EQ))) {
                        List<Order> orders = extOrderTodoMapper.listDeliveryTodoList(AppUserUtil.getLoginAppUser().getCompanyId());
                        List<Long> orderIdList = new ArrayList<>(8);
                        if(CollectionUtils.isNotEmpty(orders)) {
                            orderIdList = orders.stream().map(Order::getOrderId).collect(Collectors.toList());
                        } else {
                            orderIdList.add(SrmConstant.LONG_MINUS_ONE);
                        }
                        Map<String, Object> orderIdIn = new HashMap<>(15);
                        orderIdIn.put("in", orderIdList);
                        queryParam.getFilter().put(QlQueryFieldWrapper.field(Order::getOrderId).getFieldName(), orderIdIn);
                    }
                }
                log.info("1");
            }

            QlResult result = super.query(queryAction);
            if (UserTypeUtils.isSupplier()) {
                List<Long> orderIds = PurchaseMqlUtils.fetchResult(result, schemaType).values()
                        .stream().map(e -> e.get(Order::getOrderId)).collect(Collectors.toList());

                Map<Long, BigDecimal> unDeliveryQtyMap = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.ORDER_DETAIL.getType())
                        .select(OrderDetail::getOrderId)
                        .select(QlQueryFieldWrapper.sum(OrderDetail::getOrderNum, LambdaUtil.getFieldName(OrderDetail::getOrderNum)))
                        .select(QlQueryFieldWrapper.sum(OrderDetail::getReceiveSum, LambdaUtil.getFieldName(OrderDetail::getReceiveSum)))
                        .in(OrderDetail::getOrderId, orderIds)
                        .groupBy(OrderDetail::getOrderId), Record.class)
                        .stream().collect(Collectors.toMap(e -> e.get(OrderDetail::getOrderId),
                                e -> BigDecimalUtil.sub(e.get(OrderDetail::getOrderNum), e.get(OrderDetail::getReceiveSum))));

                PurchaseMqlUtils.buildResult(result, schemaType, e -> {
                    e.put("unDeliveryNum", unDeliveryQtyMap.get(e.get(Order::getOrderId)));
                });
            }

            PurchaseMqlUtils.buildResult(result, schemaType, e -> {
                if (e.get(ExtOrder::getExtStatus) == null) {
                    e.put(ExtOrder::getExtStatus, e.get(ExtOrder::getOrderStatus));
                }
            });
            return result;
        } catch (Exception e) {
            log.error("采购订单查询接口异常", e);
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public QlResult delete(QlQueryAction queryAction) {
        List<Record> records = getRecords(queryAction);
        List<Long> orderIds = records.stream().map(r -> r.get(Order::getOrderId)).collect(Collectors.toList());
        List<Record> orders = qlService.readByKeys(PurchaseSchemaEnum.ORDER.getType(), orderIds, Record.class);
        PurchaseMqlUtils.checkEntityPK(orderIds, orders, businessName);

        // 回写
        for (Record order : orders) {
            // 回写预算、采购申请、合同
            orderRelationService.handleBudgetReqContract(null, null, order.get(Order::getOrderId));
        }

        // 删除单据
        QlResult result = super.delete(queryAction);
        return result;
    }

    private QlResult extSaveOrUpdate(QlQueryAction queryAction){
        List<Record> records = getRecords(queryAction);
        Record record = getRecord(records);
        OrderSaveDTO orderSaveDTO = BeanCopyUtil.convertWithExtensions(record, OrderSaveDTO.class);
        List<OrderDetail> orderDetails = PurchaseMqlUtils.trimDeleteFlag(orderSaveDTO.getDetailList());

        // 封装订单信息
        if (orderSaveDTO.getOrderId() != null) {
            Record oldOrder = this.readByKey(orderSaveDTO.getOrderId());
            Assert.notNull(oldOrder, LocaleHandler.getLocaleMsg("订单ID不存在"));
            orderSaveDTO.setOrderNumber(oldOrder.get(Order::getOrderNumber));
        }

        orderRequirementSpiService.setRequirement(orderSaveDTO, orderDetails);
        orderAmountSpiService.setAmount(orderSaveDTO, orderDetails);
        orderBeforeSaveSpiService.beforeSave(orderSaveDTO, orderDetails, orderSaveDTO.getPaymentProvisionList(), orderSaveDTO.getAttachmentList());
        orderRelationService.handleBudgetReqContract(orderSaveDTO, orderDetails, orderSaveDTO.getOrderId());

        // 保存
        record.putAll(MeiQl.toValue(orderSaveDTO, Record.class));
        QlResult result = super.doSave(queryAction, records);
        //如果订单状态是UNDER_APPROVAL 提交审批，则发送短信通知 供应商联系人
        //订单提醒:“业务实体”于“订单日期（YYYY-MM-DD）”向您下单,订单编号POXXXXX,请尽快登录长城慧采云平台确认。
        if((PurchaseOrderEnum.APPROVED_INVALID.equals(orderSaveDTO.getOrderStatus()) || PurchaseOrderEnum.UNDER_APPROVAL.equals(orderSaveDTO.getOrderStatus()))&&
            ObjectUtil.isNotNull(orderSaveDTO.getExtensions().get("extVendorPhone"))) {
            //短信发送客户端
            String phone = orderSaveDTO.getExtensions().get("extVendorPhone").toString();
            String PurchaseOrderDate = DateUtil.format(orderSaveDTO.getCeeaPurchaseOrderDate(),"yyyy-MM-dd");
            String content = orderSaveDTO.getCeeaOrgName()+"于"+PurchaseOrderDate+" 向您下单,订单编号"+orderSaveDTO.getOrderNumber()+",请尽快登录长城慧采云平台确认";
            log.info(MessageFormat.format("发送短信，手机号{0}，短信内容：{1}", phone, content));
            pjProjectExtClient.message(content,phone);
        }

        return result;
    }




    private QlResult extApprove(QlQueryAction action) {
        return super.doUpdate(action, beforeApprove(action, PurchaseOrderEnum.APPROVED_INVALID));
    }

    private QlResult extReject(QlQueryAction action) {
        return super.doUpdate(action, beforeApprove(action, PurchaseOrderEnum.REJECT));
    }

    private QlResult extWithdraw(QlQueryAction action) {
        return super.doUpdate(action, beforeApprove(action, PurchaseOrderEnum.WITHDRAW));
    }

    private List<Record> beforeApprove(QlQueryAction action, PurchaseOrderEnum status){
        List<Record> records = getRecords(action);
        Record record = getRecord(action);
        Record order = readByRecord(record);
        Assert.isTrue(PurchaseOrderEnum.UNDER_APPROVAL == order.get(Order::getOrderStatus), "当前不为审批中状态");
        record.put(ExtOrder::getExtStatus, null);
        record.put(ExtOrder::getOrderStatus, status);
        return records;
    }

    /**
     * 撤回
     */
    private QlResult extRevoke(QlQueryAction action) {
        Record record = getRecord(action);
        Record order = readByRecord(record);
        Assert.isTrue(PurchaseOrderEnum.APPROVED_INVALID == order.get(Order::getOrderStatus), "当前不为待供应商确认状态，不可撤销");
        record.put(ExtOrder::getExtStatus, null);
        record.put(ExtOrder::getOrderStatus, PurchaseOrderEnum.DRAFT);
        return super.doUpdate(action, Arrays.asList(record));
    }

    /**
     * 取消
     * {"orderId":1,"extReturnRequirement":"Y"}
     */
    private QlResult extCancel(QlQueryAction action) {
        Record record = getRecord(action);
        Record order = readByRecord(record);
        if (PurchaseOrderEnum.APPROVED.name().equals(order.get(ExtOrder::getOrderStatus))
                && ExtOrderStatusEnum.ONGOING.name().equals(order.get(ExtOrder::getExtStatus))) {
            throw new BaseException("当前状态不能取消");
        }

        // 查询明细
        List<Record> records = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.ORDER_DETAIL.getType())
                .eq(OrderDetail::getOrderId, order.get(Order::getOrderId)), Record.class);
        List<Long> orderDetailIds = records.stream()
                .filter(e->OrderDetailStatus.CLOSED != e.get(ExtOrderDetail::getOrderDetailStatus))
                .map(e -> e.get(OrderDetail::getOrderDetailId)).collect(Collectors.toList());

        // 取消行
        String closedCause = record.get(ExtOrder::getClosedCause);
        extOrderService.cancelOrderDetail(orderDetailIds, record.get(ExtOrderDetail::getExtReturnRequirement), closedCause);

        return QlResult.empty();
    }

    /**
     * 选择协议单创建订单
     */
    private QlResult extCreateByRequirement(QlQueryAction action) {
        // 参数校验
        List<Record> records = getRecords(action);
        records.forEach(e -> {
            Assert.notNull(e.get(PurchaseRequirementLineDTO::getRequirementLineId), "需求id不能为空");
            Assert.notNull(e.get("orderQty"), "本次下单数量不能为空");
        });

        // 需求校验
        List<Long> requirementLineIds = records.stream().map(e -> e.get(PurchaseRequirementLineDTO::getRequirementLineId)).collect(Collectors.toList());
        List<PurchaseRequirementLineDTO> requirementLines = qlService.readByKeys("PurchaseRequirementLine", requirementLineIds, PurchaseRequirementLineDTO.class);
        Assert.isTrue(requirementLines.size() == requirementLineIds.size(), "存在需求ID错误或同一需求匹配多个协议价格");
        requirementLines.forEach(e -> {
            Assert.isTrue(YesOrNo.YES.getValue().equals(e.getExtPoolStatus()), "存在非需求池数据");
            Assert.isTrue(PrBuyTypeEnum.HAS_PRICE.name().equals(e.getExtBuyType()), "存在单据没有有效价格");
            Assert.notNull(e.getCeeaPerformUserId(), "存在单据未分配采购员");
        });

        // 生成订单
        Map<String, OrderPriceParams> priceMap = purchaseRequirementService.getPriceMap(requirementLines);
        groupCreateOrder(records, requirementLines, priceMap, true);

        return QlResult.empty();
    }

    /**
     * 选择定价单创建订单
     */
    private QlResult extCreateByFixPrice(QlQueryAction action) { // 参数校验
        List<Record> records = getRecords(action);
        Map<String, OrderPriceParams> priceMap = records.stream().map(e -> {
            OrderPriceParams priceParams = new OrderPriceParams()
                    .setRequirementLienId(e.get(PurchaseRequirementLineDTO::getRequirementLineId))
                    .setVendorId(e.get(PurchaseRequirementLineDTO::getVendorId))
                    .setVendorCode(e.get(PurchaseRequirementLineDTO::getVendorCode))
                    .setVendorName(e.get(PurchaseRequirementLineDTO::getVendorName))
                    .setNoTaxPrice(e.get(PurchaseRequirementLineDTO::getUnitPrice))
                    .setTaxRate(e.get(PurchaseRequirementLineDTO::getTaxRate))
                    .setCurrencyName(e.get(PurchaseRequirementLineDTO::getCurrencyName))
                    .setLeadTime(e.get(PurchaseRequirementLineDTO::getLeadTime))
                    .setInvoiceType(e.get(PurchaseRequirementLineDTO::getInvoiceType))
                    .setWarrantyPeriod(e.get(PurchaseRequirementLineDTO::getWarrantyPeriod))
                    .setPaymentTerm(e.get(PurchaseRequirementLineDTO::getPaymentTerm))
                    .setPaymentMethod(e.get(PurchaseRequirementLineDTO::getPaymentMethod));

            Assert.notNull(priceParams.getRequirementLienId(), "需求id不能为空");
            Assert.notNull(priceParams.getVendorId(), "供应商id不能为空");
            Assert.notNull(priceParams.getVendorCode(), "供应商编码不能为空");
            Assert.notNull(priceParams.getVendorName(), "供应商名称不能为空");
            Assert.notNull(priceParams.getNoTaxPrice(), "未税单价不能为空");
            Assert.notNull(priceParams.getTaxRate(), "税率不能为空");
            Assert.notNull(priceParams.getCurrencyName(), "币种不能为空");
            Assert.notNull(priceParams.getLeadTime(), "交期不能为空");
            Assert.notNull(priceParams.getPaymentTerm(), "付款条款不能为空");
            Assert.notNull(priceParams.getPaymentMethod(), "付款方式不能为空");
            Assert.notNull(e.get("orderQty"), "本次下单数量不能为空");

            return priceParams;
        }).collect(Collectors.toMap(e -> e.getRequirementLienId().toString(), e -> e));

        // 需求校验
        List<Long> requirementLineIds = records.stream().map(e -> e.get(PurchaseRequirementLineDTO::getRequirementLineId)).collect(Collectors.toList());
        RequirementSelectionQueryDTO queryDTO = new RequirementSelectionQueryDTO();
        queryDTO.setRequirementLineIds(requirementLineIds);
        List<PurchaseRequirementLineDTO> requirementLines = purchaseRequirementMapper.selectWithFixPrice(queryDTO);

//        List<PurchaseRequirementLineDTO> requirementLines = qlService.readByKeys("PurchaseRequirementLine", requirementLineIds, PurchaseRequirementLineDTO.class);
        Assert.isTrue(requirementLines.size() == requirementLineIds.size(), "存在需求ID错误");
        requirementLines.forEach(e -> {
            Assert.isTrue(YesOrNo.YES.getValue().equals(e.getExtPoolStatus()), "存在非需求池数据");
            Assert.isTrue(PrBuyTypeEnum.NONE_PRICE.name().equals(e.getExtBuyType()) || PrBuyTypeEnum.RECENT_PURCHASE.name().equals(e.getExtBuyType()), "存在非定价单数据");
            Assert.notNull(e.getCeeaPerformUserId(), "存在单据未分配采购员");
        });

        // 生成订单
        groupCreateOrder(records, requirementLines, priceMap, false);

        return QlResult.empty();
    }

    private void groupCreateOrder(List<Record> records,
                                  List<PurchaseRequirementLineDTO> requirementLines,
                                  Map<String, OrderPriceParams> priceMap,
                                  boolean fromPriceAgreement) {
        Map<Long, BigDecimal> orderQtyMap = records.stream().collect(Collectors.toMap(e -> e.get(PurchaseRequirementLineDTO::getRequirementLineId), e -> e.getBigDecimal("orderQty")));
        purchaseRequirementService.groupCreateOrder(requirementLines, fromPriceAgreement, priceMap, orderQtyMap, PurchaseOrderEnum.DRAFT);
    }

    /**
     * 供应商确认
     */
    private QlResult extSupplierConfirm(QlQueryAction action) {
        Record record = getRecord(action);
        ExtOrder extOrder = BeanCopyUtil.convertWithExtensions(record, ExtOrder.class);
        Record order = readByRecord(record);
        Assert.isTrue(PurchaseOrderEnum.APPROVED_INVALID == order.get(Order::getOrderStatus), "当前不为待供应商确认状态");

        List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER.getType())
                .set(ExtOrder::getOrderStatus, PurchaseOrderEnum.APPROVED)
                .set(ExtOrder::getExtStatus, null)
                .set(ExtOrder::getVersion, order.get(ExtOrder::getVersion) + 1)
                .eq(ExtOrder::getOrderId, order.get(ExtOrder::getOrderId))
                .eq(ExtOrder::getVersion, order.get(ExtOrder::getVersion)));
        Assert.notEmpty(ids, "数据已发生变化请重试");

        List<Record> details = extOrder.getDetailList().stream().map(e -> {
            Record r = new Record();
            r.set(OrderDetail::getOrderDetailId, e.getOrderDetailId());
            r.set(OrderDetail::getCeeaPromiseReceiveDate, e.getCeeaPromiseReceiveDate());
            r.set(OrderDetail::getOrderDetailStatus, OrderDetailStatus.ACCEPT);
            return r;
        }).collect(Collectors.toList());
        //EDM回传接口，触发节点回传(供应商已确认),就是供应商确认触发，手工订单不做处理
        if (!MANUAL.equals(extOrder.getOrderType())) {
            JSONArray ja = new JSONArray();
            extOrder.getDetailList().forEach(e -> {
                PurchaseRequirementHeadDTO purReq = purchaseRequirementMapper.selectOne(new LambdaQueryWrapper<PurchaseRequirementHeadDTO>().eq(PurchaseRequirementHeadDTO::getRequirementHeadNum, e.getCeeaRequirementHeadNum()));
                if (StringUtils.isNotBlank(purReq.getEdmExNo())) {
                    /*List<Record> purList = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementLine")
                            .eq(PurchaseRequirementLineDTO::getRequirementHeadNum, e.getCeeaRequirementHeadNum())
                            .eq(PurchaseRequirementLineDTO::getRowNum, e.getCeeaRowNum())
                            .eq(PurchaseRequirementLineDTO::getMaterialCode, e.getMaterialCode()), Record.class);*/
                    Record record1 = qlService.readByKey("PurchaseRequirementLine", e.getCeeaRequirementLineId(), Record.class);
                    if (StringUtils.isNotBlank(record1.getString(EXTERNAL_ID))) {
                        JSONObject jo = new JSONObject();
                        jo.put("purOrderCode", purReq.getEdmExNo());
                        jo.put("purOrderItemCode", record1.get("externalId"));
                        jo.put("orderNumber", extOrder.getOrderNumber());
                        jo.put("price", e.getCeeaUnitNoTaxPrice());
                        if (e.getCeeaPromiseReceiveDate() != null) {
                            jo.put("arriveDate", new SimpleDateFormat("yyyy-MM-dd").format(e.getCeeaPromiseReceiveDate()));
                        } else {
                            jo.put("arriveDate", e.getCeeaPromiseReceiveDate());
                        }
                        jo.put("supplierCode", extOrder.getVendorCode());
                        jo.put("supplierName", extOrder.getVendorName());
                        jo.put("suppliercontact", extOrder.getExtVendorPhone());
                        jo.put("tenantId", record1.get("tenantId"));
                        jo.put("edmOrgId", record1.get("edmOrgId"));
                        ja.add(jo);
                    }
                }
            });
            if (CollectionUtils.isNotEmpty(ja)) {
                JSONObject jobi = new JSONObject();
                jobi.put("data", ja);
                JSONObject reStr = pjProjectExtClient.pushPurOrderToEdm(jobi.toString());
                log.info("--------------------------" + reStr);
                if (!STR200.equals(String.valueOf(reStr.get(CODE)))) {
                    throw new BaseException(reStr.get("msg").toString());
                }
            }
        }
        qlService.update(PurchaseSchemaEnum.ORDER_DETAIL.getType(), details);
        return QlResult.empty();
    }

    /**
     * 供应商拒绝
     */
    private QlResult extSupplierRefuse(QlQueryAction action) {
        Record record = getRecord(action);
        Record order = readByRecord(record);
        Assert.isTrue(PurchaseOrderEnum.APPROVED_INVALID == order.get(Order::getOrderStatus), "当前不为待供应商确认状态");

        List<Record> orderDetails = qlService.queryByWrapper(QlWrappers.query("OrderDetail")
                .eq(OrderDetail::getOrderId, order.get(Order::getOrderId)), Record.class);

        // 释放采购申请数量
        if (!ExtOrderTypeEnum.MANUAL.name().equals(order.get(Order::getOrderType))) {
            Map<Long, BigDecimal> changeQtyMap = orderDetails.stream()
                    .collect(Collectors.toMap(e -> e.get(OrderDetail::getCeeaRequirementLineId), e -> e.get(OrderDetail::getOrderNum), (v1, v2) -> v1.add(v2)));
            if (MapUtils.isNotEmpty(changeQtyMap)) {
                orderRelationService.updateRequirementQty(changeQtyMap);
            }
        }

        List<Record> detailRecords = orderDetails.stream().map(e -> {
            Record r = new Record();
            r.set(OrderDetail::getOrderDetailId, e.get(OrderDetail::getOrderDetailId));
            r.set(OrderDetail::getOrderDetailStatus, OrderDetailStatus.REJECT);
            return r;
        }).collect(Collectors.toList());
        qlService.update(PurchaseSchemaEnum.ORDER_DETAIL.getType(), detailRecords);

        List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update(PurchaseSchemaEnum.ORDER.getType())
                .set(ExtOrder::getOrderStatus, PurchaseOrderEnum.REFUSED)
                .set(ExtOrder::getExtStatus, null)
                .set(ExtOrder::getRefuseReason, record.get(Order::getRefuseReason))
                .set(ExtOrder::getVersion, order.get(ExtOrder::getVersion) + 1)
                .eq(ExtOrder::getOrderId, order.get(ExtOrder::getOrderId))
                .eq(ExtOrder::getVersion, order.get(ExtOrder::getVersion)));
        Assert.notEmpty(ids, "数据已发生变化请重试");
        return QlResult.empty();
    }

}
