package com.midea.cloud.srm.supcooperate.ext.requirement.pr.repo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.meiql.api.component.paging.PageRequest;
import com.midea.cloud.meiql.api.function.SFunction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryCondition;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.api.spec.result.RepoData;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryFilter;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pj.base.organization.entity.OrgInvoiceInfo;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApplyStatus;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.supcooperate.orderhistorys.dto.SccScOrderHistoryDto;
import com.midea.cloud.srm.model.suppliercooperate.mql.enums.PurchaseSchemaEnum;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.Order;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrderDetail;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.ExtPurchaseRequirementCreateSouDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PrVendorConfig;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementLineDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.mapper.PurchaseRequirementMapper;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.PurchaseRequirementService;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Slf4j
@Component
public class PurchaseRequirementLineRepository extends PurchaseRepository<PurchaseRequirementLineDTO> {

    @Autowired
    private PurchaseRequirementService purchaseRequirementService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private PurchaseRequirementMapper purchaseRequirementMapper;

    private static final int NUM3 = 3;

    private static final String EXT_POOL_STATUS_VALID = "有效";

    private static final String EXT_POOL_STATUS_COMPLETE = "已完成";

    private static final String EXT_POOL_STATUS_CLOSE = "已关闭";

    public PurchaseRequirementLineRepository() {
        super("PurchaseRequirementLine", "requirementLineId", "采购申请明细");

        this.register("list", this::list, true, "列表查询");
        this.register("close", this::close, true, "关闭");
        this.register("searchHistory", this::searchHistory, true, "引出历史供应商");
        this.register("createSou", this::createSou, true, "创建寻源单");
        this.register("assign", this::assign, true, "分配转办");
        this.register("changeBuyType", this::changeBuyType, true, "修改购买类型");
    }

    /**
     * 查询历史供应商周期
     */
    @Value("${pr.config.searchHistoryDays}")
    private String PR_CONFIG_SEARCH_HISTORY_DAYS;

    private Map<String, Method> queryWrapperMethod(QlQueryWrapper qw) {
        Map<String, Method> methodMap = new HashMap<>(16);
        if(ObjectUtils.allNotNull(qw)) {
            Arrays.stream(qw.getClass().getMethods()).forEach(method -> {
                Parameter[] parameters = method.getParameters();
                if(ObjectUtils.isNotEmpty(parameters)) {
                    List<String> nameList = new ArrayList<>(16);
                    nameList.add(method.getName());
                    for(int i = 0; i < parameters.length; i++) {
                        nameList.add(parameters[i].getType().getSimpleName());
                    }
                    methodMap.put(nameList.stream().collect(Collectors.joining(SrmConstant.UNDER_LINE)), method);
                } else {
                    methodMap.put(method.getName(), method);
                }

            });
        }
        return methodMap;
    }


    private <F> void addQueryCondtion(QlQueryWrapper qw, Method method, String alias, String fieldName, String con, Object value) {
        if(ObjectUtils.allNotNull(method, value)) {
            ReflectionUtils.invokeMethod(method, qw, QlQueryFieldWrapper.field(alias, fieldName), value);
        }
    }

    private <F> void addQueryCondtion(QlQueryWrapper qw, Method method, String alias, String fieldName, String con, Object value1, Object value2) {
        if(ObjectUtils.allNotNull(method, value1, value2)) {
            ReflectionUtils.invokeMethod(method, qw, QlQueryFieldWrapper.field(alias, fieldName), value1, value2);
        }
    }

    @Override
    public QlResult query(QlQueryAction queryAction) {

        QlQueryCondition condition = queryAction.getQuery().getQueryCondition();
        if (null == condition) {
            condition = new QlQueryCondition();
        }

        QueryParam queryParam = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asQuery();
        QueryFilter queryFilter = queryParam.getFilter();
        PageRequest pageRequest = queryParam.getPage();

        QlQueryWrapper qw = QlWrappers.query(queryAction.getType(), "a")
                .innerJoin(MqlType.PURCHASE_REQUIREMENT_HEAD, "b",
                        s -> s.eq( QlQueryFieldWrapper.field("b", PurchaseRequirementHeadDTO::getRequirementHeadId), QlQueryFieldWrapper.field("a", PurchaseRequirementLineDTO::getRequirementHeadId))
                                .eq(QlQueryFieldWrapper.field("b", PurchaseRequirementHeadDTO::getExtBidFlag), YesOrNo.NO.getValue())
                                .eq(QlQueryFieldWrapper.field("b", PurchaseRequirementHeadDTO::getExtInPool), YesOrNo.YES.getValue()));

        Map<String, Method> methodMap = queryWrapperMethod(qw);

        if(CollectionUtils.isNotEmpty(queryFilter.keySet())) {
            for(String fieldName : queryFilter.keySet()) {
                Map<String, Object> fillterMap = queryFilter.getValue(fieldName);
                if(MapUtils.isNotEmpty(fillterMap)) {
                    for(String con : fillterMap.keySet()) {

                        if("between".equals(con)) {
                            String methodName = StringUtils.joinWith(SrmConstant.UNDER_LINE, con, QlQueryFieldWrapper.class.getSimpleName(), Object.class.getSimpleName(), Object.class.getSimpleName());
                            List<Object> values = (List<Object>) fillterMap.get(con);
                            addQueryCondtion(qw, methodMap.get(methodName), "a", fieldName, con, values.get(0), values.get(1));
                        } else {
                            String methodName = StringUtils.joinWith(SrmConstant.UNDER_LINE, con, QlQueryFieldWrapper.class.getSimpleName(), Object.class.getSimpleName());
                            String alias = "a";
                            if("demandType".equals(fieldName)) {
                                alias = "b";
                            }

                            addQueryCondtion(qw, methodMap.get(methodName), alias, fieldName, con, fillterMap.get(con));
                        }

                    }
                }
            }
        }

        qw.select(QlQueryFieldWrapper.field("b", PurchaseRequirementHeadDTO::getCeeaAppointReason),
                QlQueryFieldWrapper.field("b", PurchaseRequirementHeadDTO::getCeeaPrType),
                QlQueryFieldWrapper.field("b", PurchaseRequirementHeadDTO::getExtOrgBuName),
                QlQueryFieldWrapper.field("b", PurchaseRequirementHeadDTO::getOrgId),
                QlQueryFieldWrapper.field("b", PurchaseRequirementHeadDTO::getOrgCode),
                QlQueryFieldWrapper.field("b", PurchaseRequirementHeadDTO::getOrgName),
                QlQueryFieldWrapper.field("b", PurchaseRequirementHeadDTO::getRequirementHeadId),
                QlQueryFieldWrapper.field("b", PurchaseRequirementHeadDTO::getDemandType));
        qw.select(QlQueryFieldWrapper.field("a", "*"));
        Page<Record> lines = qlService.queryPageByWrapper(qw, pageRequest, Record.class);

        QlResult result = ResultUtil.build(queryAction, LambdaUtil.getFieldName(PurchaseRequirementLineDTO::getRequirementLineId), lines, false);

        if (result == null || CollectionUtils.isEmpty(result.getRecords())) {
            return result;
        }
        RepoData repoData = result.getRef();
        Set<Map.Entry<Serializable,Record>> entries = repoData.get(this.schemaType).entrySet();
        List<Long> organizationIds = new ArrayList<>();
        for(Map.Entry<Serializable,Record> recordEntry:entries){
           Long organizationId = recordEntry.getValue().get(PurchaseRequirementLineDTO::getOrganizationId);
            organizationIds.add(organizationId);
        }
        /* 开票信息 */
        QlOpenQueryWrapper invoiceWrapper = QlOpenWrappers.query("OrgInvoiceInfo");
        invoiceWrapper.in(true, OrgInvoiceInfo::getOrganizationId, organizationIds);
        List<OrgInvoiceInfo> invoiceInfoList = qlOpenClient.query(ContextPath.BASE, invoiceWrapper, OrgInvoiceInfo.class);
        Map<Long,String> orgInvoiceOrgMap = new HashMap<>(15);
        if(CollUtil.isNotEmpty(invoiceInfoList)){
            invoiceInfoList.forEach(orgInvoiceInfo -> orgInvoiceOrgMap.putIfAbsent(orgInvoiceInfo.getOrganizationId(),orgInvoiceInfo.getCompanyName()));
        }
        for(Map.Entry<Serializable,Record> recordEntry:entries){
            Record record = recordEntry.getValue();
            if(ObjectUtil.isNotNull(record)){
                String orgName = orgInvoiceOrgMap.get(record.get(PurchaseRequirementLineDTO::getOrganizationId));
                record.put(PurchaseRequirementLineDTO::getInvoiceOrgName,orgName);

                Record head = new Record();
                head.put(PurchaseRequirementHeadDTO::getCeeaAppointReason, record.get(PurchaseRequirementHeadDTO::getCeeaAppointReason));
                head.put(PurchaseRequirementHeadDTO::getCeeaPrType, record.get(PurchaseRequirementHeadDTO::getCeeaPrType));
                head.put(PurchaseRequirementHeadDTO::getExtOrgBuName, record.get(PurchaseRequirementHeadDTO::getExtOrgBuName));
                head.put(PurchaseRequirementHeadDTO::getOrgId, record.get(PurchaseRequirementHeadDTO::getOrgId));
                head.put(PurchaseRequirementHeadDTO::getOrgCode, record.get(PurchaseRequirementHeadDTO::getOrgCode));
                head.put(PurchaseRequirementHeadDTO::getOrgName, record.get(PurchaseRequirementHeadDTO::getOrgName));
                head.put(PurchaseRequirementHeadDTO::getRequirementHeadId, record.get(PurchaseRequirementHeadDTO::getRequirementHeadId));
                head.put(PurchaseRequirementHeadDTO::getDemandType, record.get(PurchaseRequirementHeadDTO::getDemandType));

                record.put(LambdaUtil.getFieldName(PurchaseRequirementLineDTO::getRequirementHeadId), head);
                record.put("extPoolStatusValue", extPoolStatus(record.get(PurchaseRequirementLineDTO::getExtPoolStatus), record.get(PurchaseRequirementLineDTO::getOrderQuantity)));

            }
        }
        return result;
    }

    /**
     * 自定义导出----状态
     * @param extPoolStatus
     * @param orderQuantity
     * @return
     */
    private String extPoolStatus(String extPoolStatus, BigDecimal orderQuantity) {
        if(YesOrNo.YES.getValue().equals(extPoolStatus)) {
            if(ObjectUtils.defaultIfNull(orderQuantity, BigDecimal.ZERO).compareTo(BigDecimal.ZERO) == 0) {
                return EXT_POOL_STATUS_COMPLETE;
            }
            return EXT_POOL_STATUS_VALID;
        }
        return EXT_POOL_STATUS_CLOSE;
    }


    /**
     * 列表查询
     */
    private QlResult list(QlQueryAction action) {
        QlResult result = super.query(action);

        // 查询有效价格
        return result;
    }

    /**
     * 关闭
     */
    private QlResult close(QlQueryAction action) {
        Record record = getRecord(action);

        // 校验
        List<Long> requirementLineIds = ((List<Long>) record.get("requirementLineIds"));
        Assert.notEmpty(requirementLineIds, "需求id不能为空");
        Long extClosedFileId = record.get(PurchaseRequirementLineDTO::getExtClosedFileId);
        String extClosedFileName = record.get(PurchaseRequirementLineDTO::getExtClosedFileName);
        List<PurchaseRequirementLineDTO> list = getByIds(requirementLineIds);
        list.forEach(e -> {
            Assert.isTrue(YesOrNo.YES.getValue().equals(e.getExtPoolStatus()), "需求不为有效状态");
        });
        List<Record> purList = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementLine")
                .in(PurchaseRequirementLineDTO::getRequirementLineId, requirementLineIds), Record.class);
        //edm关闭
        for (Record record1 : purList) {
            if (StringUtils.isNotBlank(record1.getString("externalId"))) {
                PurchaseRequirementHeadDTO purReq = purchaseRequirementMapper.selectOne(new LambdaQueryWrapper<PurchaseRequirementHeadDTO>().eq(PurchaseRequirementHeadDTO::getRequirementHeadNum, record1.getString("requirementHeadNum")));
                if (StringUtils.isNotBlank(purReq.getEdmExNo())) {
                    //edm删除
                    JSONObject jo = new JSONObject();
                    jo.put("applyOutsideCode", purReq.getEdmExNo());
                    jo.put("outerItemCode", record1.getString("externalId"));
                    jo.put("apporderNumber", purReq.getRequirementHeadNum());
                    jo.put("draftStatus", "关闭");
                    jo.put("tenantId", record1.get("tenantId"));
                    jo.put("edmOrgId", record1.get("edmOrgId"));
                    JSONObject reStr = pjProjectExtClient.edmDraftOrderBackHaul(jo.toString());
                    log.info("--------------------------" + reStr);
                    if (!"200".equals(String.valueOf(reStr.get("code")))) {
                        throw new BaseException(reStr.get("msg").toString());
                    }
                }
            }
        }
        // 保存
        qlService.updateByWrapper(QlWrappers.update(schemaType)
                .set(PurchaseRequirementLineDTO::getExtPoolStatus, YesOrNo.NO.getValue())
                .set(PurchaseRequirementLineDTO::getExtClosedCause, record.get(PurchaseRequirementLineDTO::getExtClosedCause))
                .set(PurchaseRequirementLineDTO::getExtClosedFileId, extClosedFileId)
                .set(PurchaseRequirementLineDTO::getExtClosedFileName, extClosedFileName)
                .in(PurchaseRequirementLineDTO::getRequirementLineId, requirementLineIds));
        return QlResult.empty();
    }

    /**
     * 引出历史供应商
     */
    private QlResult searchHistory(QlQueryAction action) {
        Record record = getRecord(action);
        List<Long> requirementLineIds = ((List<Long>) record.get("requirementLineIds"));
        Assert.notEmpty(requirementLineIds, "需求id不能为空");
        List<PurchaseRequirementLineDTO> lines = getByIds(requirementLineIds);
        /*long existsCount = qlService.countByWrapper(QlWrappers.query(PurchaseSchemaEnum.ORDER_DETAIL.getType())
                .in(OrderDetail::getCeeaRequirementLineId, requirementLineIds));
        Assert.isTrue(existsCount == 0, "所勾选的需求行存在下游单据，不允许引出历史供应商");*/

        // 起始时间
        Date formDate = DateUtils.addDays(new Date(), 0 - Integer.valueOf(PR_CONFIG_SEARCH_HISTORY_DAYS));

        // 查找配置
        List<Long> orgIds = lines.stream().map(e -> e.getOrgId()).distinct().collect(Collectors.toList());
        Map<Long, PrVendorConfig> orgAreasMap = qlService.queryByWrapper(QlWrappers.query("PrVendorConfig")
                        .in(PrVendorConfig::getOrgId, orgIds)
                        .eq(PrVendorConfig::getStatus, YesOrNo.YES.getValue()), PrVendorConfig.class)
                .stream().collect(Collectors.toMap(e -> e.getOrgId(), e -> e, (v1, v2) -> v1));

        List<Record> updateRecords = new ArrayList<>();
        lines.forEach(e -> {
            Record requirementLine = new Record();
            requirementLine.put(PurchaseRequirementLineDTO::getRequirementLineId, e.getRequirementLineId());
            requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorFlag, YesOrNo.YES.getValue());
            updateRecords.add(requirementLine);

            if (StringUtils.isBlank(e.getMaterialCode())) {
                return;
            }

            List<PurchaseRequirementLineDTO> lowestPriceList = mergeLowestPrice(this.queryOrderDetailFromSrm(e, orgAreasMap.get(e.getOrgId()), formDate), this.queryOrderHistory(e, orgAreasMap.get(e.getOrgId()), formDate));

            if (CollectionUtils.isEmpty(lowestPriceList)) {
                log.info("未查询到历史供应商数据，物料编码:{}，业务实体编码:{}", e.getMaterialCode(), e.getOrgCode());
                return;
            }

            for(int i = 0; i < lowestPriceList.size() && i < NUM3; i++) {
                PurchaseRequirementLineDTO order = lowestPriceList.get(i);
                switch (i) {
                    case 0:
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorCode1, order.getVendorCode());
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorName1, order.getVendorName());
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorPrice1, order.getNotaxPrice());
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorBrand1, order.getExtHistoryVendorBrand1());
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryOrderDetail1, order.getExtHistoryOrderDetail1());
                        break;
                    case 1:
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorCode2, order.getVendorCode());
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorName2, order.getVendorName());
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorPrice2, order.getNotaxPrice());
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorBrand2, order.getExtHistoryVendorBrand1());
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryOrderDetail2, order.getExtHistoryOrderDetail1());
                        break;
                    case 2:
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorCode3, order.getVendorCode());
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorName3, order.getVendorName());
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorPrice3, order.getNotaxPrice());
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryVendorBrand3, order.getExtHistoryVendorBrand1());
                        requirementLine.put(PurchaseRequirementLineDTO::getExtHistoryOrderDetail3, order.getExtHistoryOrderDetail1());
                        break;
                    default:
                }
            }

        });

        if (CollectionUtils.isNotEmpty(updateRecords)) {
            qlService.update(schemaType, updateRecords);
        }
        return QlResult.empty();
    }

    /**
     * 合并SRM订单和接口历史订单最低三个价格
     * @param srmOrderDetails
     * @param apiOrderHistory
     * @return
     */
    private List<PurchaseRequirementLineDTO> mergeLowestPrice(List<Record> srmOrderDetails, List<SccScOrderHistoryDto> apiOrderHistory) {
        List<PurchaseRequirementLineDTO> requirementLineList = new ArrayList<>(12);

        if(CollectionUtils.isNotEmpty(srmOrderDetails)) {
            srmOrderDetails.stream().forEach(e -> {
                PurchaseRequirementLineDTO requirementLine = new PurchaseRequirementLineDTO();
                requirementLineList.add(requirementLine);
                requirementLine.setVendorCode(e.get(Order::getVendorCode));
                requirementLine.setVendorName(e.get(Order::getVendorName));
                requirementLine.setNotaxPrice(e.get(OrderDetail::getCeeaUnitNoTaxPrice));
                requirementLine.setExtHistoryVendorBrand1(e.get(ExtOrderDetail::getExtBrand));
                requirementLine.setExtHistoryOrderDetail1(e.get(OrderDetail::getOrderDetailId));
            });
        }

        if(CollectionUtils.isNotEmpty(apiOrderHistory)) {
            apiOrderHistory.stream().forEach(e -> {
                PurchaseRequirementLineDTO requirementLine = new PurchaseRequirementLineDTO();
                requirementLineList.add(requirementLine);
                requirementLine.setVendorCode(e.getSupCode());
                requirementLine.setVendorName(StringUtils.joinWith(SrmConstant.SHORT_LINE, e.getSupName(), SrmConstant.SUPPLIER_COOPERATE));
                requirementLine.setNotaxPrice(e.getNoTaxPrice());
                requirementLine.setExtHistoryVendorBrand1(e.getBrand());
                requirementLine.setExtHistoryOrderDetail1(e.getOrderHistoryId());
            });
        }

        return requirementLineList.stream().sorted(new Comparator<PurchaseRequirementLineDTO>() {
            @Override
            public int compare(PurchaseRequirementLineDTO o1, PurchaseRequirementLineDTO o2) {
                return ObjectUtils.defaultIfNull(o1.getNotaxPrice(), BigDecimal.valueOf(Integer.MAX_VALUE)).compareTo(ObjectUtils.defaultIfNull(o2.getNotaxPrice(), BigDecimal.valueOf(Integer.MAX_VALUE)));
            }
        }).collect(Collectors.toList());

    }

    /**
     * 查询SRM系统订单历史最低三个价格
     * @param requirementLine
     * @param prVendorConfig
     * @param formDate
     * @return
     */
    private List<Record> queryOrderDetailFromSrm(PurchaseRequirementLineDTO requirementLine, PrVendorConfig prVendorConfig, Date formDate) {
        Long orgId = null;
        List<String> areaCodeList = null;
        if(!Objects.isNull(prVendorConfig)) {
            if(YesOrNo.YES.getValue().equals(prVendorConfig.getRangFlag())) {
                orgId = prVendorConfig.getOrgId();
            } else {
                if(StringUtils.isNotBlank(prVendorConfig.getAreaCodes())) {
                    areaCodeList = new ArrayList<>(Arrays.asList(prVendorConfig.getAreaCodes().split(SrmConstant.SIG_3)));
                }
            }
        }

        Page<Record> page = qlService.queryPageByWrapper(QlWrappers.query(PurchaseSchemaEnum.ORDER.getType(), "o")
                .innerJoin(PurchaseSchemaEnum.ORDER_DETAIL.getType(), "d",
                        t -> t.eq(QlQueryFieldWrapper.field("o", LambdaUtil.getFieldName(Order::getOrderId)),
                                QlQueryFieldWrapper.field("d", LambdaUtil.getFieldName(ExtOrderDetail::getOrderId))))
                .select(QlQueryFieldWrapper.field("o", LambdaUtil.getFieldName(Order::getVendorCode)),
                        QlQueryFieldWrapper.field("o", LambdaUtil.getFieldName(Order::getVendorName)),
                        QlQueryFieldWrapper.field("d", LambdaUtil.getFieldName(ExtOrderDetail::getOrderDetailId)),
                        QlQueryFieldWrapper.field("d", LambdaUtil.getFieldName(ExtOrderDetail::getExtBrand)),
                        QlQueryFieldWrapper.field("d", LambdaUtil.getFieldName(ExtOrderDetail::getCeeaUnitNoTaxPrice)))
                .eq(OrderDetail::getMaterialCode, requirementLine.getMaterialCode())
                .eq(orgId != null, Order::getCeeaOrgId, orgId)
                .in(CollectionUtils.isNotEmpty(areaCodeList), QlQueryFieldWrapper.field("d", LambdaUtil.getFieldName(ExtOrderDetail::getExtAreaCode)), areaCodeList)
                .ge(Order::getCeeaPurchaseOrderDate, formDate)
                .orderByAsc(OrderDetail::getCeeaUnitTaxPrice), 1L, 3L, Record.class);
        return page.getRecords();
    }

    /**
     * 查询历史订单最低三个价格
     * @param requirementLine
     * @param prVendorConfig
     * @param formDate
     * @return
     */
    private List<SccScOrderHistoryDto> queryOrderHistory(PurchaseRequirementLineDTO requirementLine, PrVendorConfig prVendorConfig, Date formDate) {
        String orgCode = null;
        List<String> areaCodeList = null;
        if(!Objects.isNull(prVendorConfig)) {
            if(YesOrNo.YES.getValue().equals(prVendorConfig.getRangFlag())) {
                orgCode = prVendorConfig.getOrgCode();
            } else {
                if(StringUtils.isNotBlank(prVendorConfig.getAreaCodes())) {
                    areaCodeList = new ArrayList<>(Arrays.asList(prVendorConfig.getAreaCodes().split(SrmConstant.SIG_3)));
                }
            }
        }
        Page<SccScOrderHistoryDto> page = qlService.queryPageByWrapper(QlWrappers.query(MqlType.SCC_SC_ORDER_HISTORY)
                .ge(SccScOrderHistoryDto::getOrderDate, formDate)
                .eq(SccScOrderHistoryDto::getMaterialCode, requirementLine.getMaterialCode())
                .eq(StringUtils.isNotBlank(orgCode), SccScOrderHistoryDto::getOrgCode, orgCode)
                .in(CollectionUtils.isNotEmpty(areaCodeList), SccScOrderHistoryDto::getAreaCode, areaCodeList)
                .orderByAsc(SccScOrderHistoryDto::getNoTaxPrice), 1L, 3L, SccScOrderHistoryDto.class);
        return page.getRecords();
    }

    private QlResult createSou(QlQueryAction queryAction) {
        ExtPurchaseRequirementCreateSouDTO param = SouObjectXUtil.convertTargetObj(queryAction.getPayload(), ExtPurchaseRequirementCreateSouDTO.class);

        SouProject souProject = purchaseRequirementService.createSou(param);
        return ResultUtil.build(queryAction, "projectId", Collections.singletonList(souProject), false);
    }

    private QlResult assign(QlQueryAction action) {
        Record record = getRecord(action);
        List<Long> requirementLineIds = ((List<Long>) record.get("requirementLineIds"));
        Assert.notEmpty(requirementLineIds, "需求id不能为空");
        Long userId = record.get(RequirementLine::getCeeaPerformUserId);
        String nickname = record.get(RequirementLine::getCeeaPerformUserNickname);
        String username = record.get(RequirementLine::getCeeaPerformUserName);
        Assert.notEmpty(requirementLineIds, "需求id不能为空");
        Assert.notNull(userId, "采购员id不能为空");
        Assert.hasText(nickname, "采购员名称不能为空");
        Assert.hasText(username, "采购员账号不能为空");

        List<PurchaseRequirementLineDTO> list = getByIds(requirementLineIds);
        list.forEach(e -> {
            Assert.isTrue(YesOrNo.YES.getValue().equals(e.getExtPoolStatus()), "需求不为有效状态");
        });

        qlService.updateByWrapper(QlWrappers.update("PurchaseRequirementLine")
                .set(RequirementLine::getApplyStatus, RequirementApplyStatus.ASSIGNED)
                .set(RequirementLine::getCeeaPerformUserId, userId)
                .set(RequirementLine::getCeeaPerformUserNickname, nickname)
                .set(RequirementLine::getCeeaPerformUserName, username)
                .in(RequirementLine::getRequirementLineId, requirementLineIds));
        return QlResult.empty();
    }

    private QlResult changeBuyType(QlQueryAction action) {
        Record record = getRecord(action);

        // 校验
        List<Long> requirementLineIds = (List<Long>) record.get("requirementLineIds");
        Assert.notEmpty(requirementLineIds, "需求id不能为空");
        String extBuyType = record.get(PurchaseRequirementLineDTO::getExtBuyType);
        Assert.hasText(extBuyType, "购买类型不能为空");

        // 保存
        qlService.updateByWrapper(QlWrappers.update(schemaType)
                .set(PurchaseRequirementLineDTO::getExtBuyType, extBuyType)
                .set(PurchaseRequirementLineDTO::getExtBuyTypeComment, record.get(PurchaseRequirementLineDTO::getExtBuyTypeComment))
                .in(PurchaseRequirementLineDTO::getRequirementLineId, requirementLineIds));
        return QlResult.empty();
    }
}
