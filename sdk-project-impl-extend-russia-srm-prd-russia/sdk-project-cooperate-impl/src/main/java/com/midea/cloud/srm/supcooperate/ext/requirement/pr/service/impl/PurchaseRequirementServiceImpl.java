package com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.impl;

import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.HashBiMap;
import com.meicloud.paas.audit.util.JsonUtils;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.enums.pm.po.SourceSystemEnum;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.*;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.ExtRbacClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.SouExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.mall.request.jd.JDAddressRequestDTO;
import com.midea.cloud.srm.mall.request.jd.order.JDOrderSubmitRequestDTO;
import com.midea.cloud.srm.mall.result.jd.common.AddressResultDTO;
import com.midea.cloud.srm.mall.result.jd.Order.OrderSubmitResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.AddressResultDTO;
import com.midea.cloud.srm.mall.result.jd.Order.OrderSubmitResultDTO;
import com.midea.cloud.srm.mall.result.jd.goods.ProductStockResultDTO;
import com.midea.cloud.srm.mall.result.jd.goods.TotalCheckNewResultDTO;
import com.midea.cloud.srm.mall.service.jd.MallService;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.pj.base.organization.entity.OrgInvoiceInfo;
import com.midea.cloud.srm.model.pj.base.organization.entity.Site;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApplyStatus;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.sou.agreement.dto.PriceAgreementDTO;
import com.midea.cloud.srm.model.sou.agreement.dto.PriceAgreementQueryDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceHeadDTO;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.supcooperate.enums.MallTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.ExternalMaterial;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementLineExportDto;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementLineExportRequestDto;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import com.midea.cloud.srm.model.suppliercooperate.order.dto.OrderSaveRequestDTO;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.Order;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.OrderDetail;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.OrderDetailStatus;
import com.midea.cloud.srm.model.suppliercooperate.order.enums.PurchaseOrderEnum;
import com.midea.cloud.srm.po.order.service.IOrderService;
import com.midea.cloud.srm.pr.division.service.IDivisionCategoryService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto.InvoicePrincipal;
import com.midea.cloud.srm.supcooperate.ext.order.PurchaseUtils;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrder;
import com.midea.cloud.srm.supcooperate.ext.order.dto.ExtOrderDetail;
import com.midea.cloud.srm.supcooperate.ext.order.dto.JDOrderDetailTotalCheckRequestDTO;
import com.midea.cloud.srm.supcooperate.ext.order.dto.OrderConfigCategory;
import com.midea.cloud.srm.supcooperate.ext.order.enums.ExtOrderProperty;
import com.midea.cloud.srm.supcooperate.ext.order.enums.ExtOrderTypeEnum;
import com.midea.cloud.srm.supcooperate.ext.order.repo.OrderConfigRepository;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.*;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.enums.ExtPriceSourceEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.enums.PrBuyTypeEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.mapper.PurchaseRequirementMapper;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.PurchaseRequirementService;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.spi.event.createsou.ExtPrRequirementCreateSouContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.spi.event.createsou.IExtPrRequirementCreateSouPlugin;
import com.midea.cloud.srm.supcooperate.meiql.util.PurchaseMqlUtils;
import com.midea.cloud.srm.supcooperate.mtmapping.service.ExternalMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Slf4j
@Service
public class PurchaseRequirementServiceImpl implements PurchaseRequirementService {

    @Autowired
    private QlService qlService;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private SouExtClient souExtClient;
    @Autowired
    private IDivisionCategoryService divisionCategoryService;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;
    @Autowired
    private ExtRbacClient extRbacClient;
    @Autowired
    private MallService mallService;
    @Autowired
    private ExternalMaterialService externalMaterialService;
    @Autowired
    private PurchaseRequirementMapper purchaseRequirementMapper;

    private static final String SIGN_COMMA = ",";
    private static final String JD001 = "JD001";
    private static final String STR0000 = "0000";
    private static final char CHAR_COMMA = ',';


    /**
     * 领单进入需求池
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void pushPool(PurchaseRequirementHeadDTO requirementHead, String pushUserCode, String pushUserName) {
        // 查询采购申请明细
        Long requirementHeadId = requirementHead.getRequirementHeadId();
        List<PurchaseRequirementLineDTO> requirementLines = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementLine")
                .eq(PurchaseRequirementLineDTO::getRequirementHeadId, requirementHeadId), PurchaseRequirementLineDTO.class);
        Assert.notEmpty(requirementLines, "采购申请缺少明细行数据");

        // 查询价格库
        Map<String, OrderPriceParams> priceMap = getPriceMap(requirementLines);
        requirementLines.forEach(e -> {
            OrderPriceParams price = priceMap.get(getPriceAgreementKey(e.getOrgId(), e.getExtAreaCode(), e.getMaterialId()));
            e.setExtBuyType(price == null ? PrBuyTypeEnum.NONE_PRICE.name() : PrBuyTypeEnum.HAS_PRICE.name());
        });

        // 设置采购员
        setPurchaser(requirementLines);

        // 設置購買類型
        setBuyType(requirementLines);

        // 保存申请明细
        List<Record> lineRecords = new ArrayList<>();
        requirementLines.forEach(e -> {
            Record record = new Record();
            record.put(PurchaseRequirementLineDTO::getRequirementLineId, e.getRequirementLineId());
            record.put(PurchaseRequirementLineDTO::getExtPoolStatus, YesOrNo.YES.getValue());
            record.put(PurchaseRequirementLineDTO::getExtPushTime, LocalDateTime.now());
            record.put(PurchaseRequirementLineDTO::getExtBuyType, e.getExtBuyType());
            record.set(PurchaseRequirementLineDTO::getExtPushUserCode, pushUserCode);
            record.set(PurchaseRequirementLineDTO::getExtPushUserName, pushUserName);
            record.set(PurchaseRequirementLineDTO::getCeeaPerformUserId, e.getCeeaPerformUserId());
            record.set(PurchaseRequirementLineDTO::getCeeaPerformUserName, e.getCeeaPerformUserName());
            record.set(PurchaseRequirementLineDTO::getCeeaPerformUserNickname, e.getCeeaPerformUserNickname());
            record.set(PurchaseRequirementLineDTO::getPurchaseOrganization, e.getPurchaseOrganization());
            record.set(PurchaseRequirementLineDTO::getApplyStatus, e.getApplyStatus());
            record.set(PurchaseRequirementLineDTO::getVendorId, e.getVendorId());
            record.set(PurchaseRequirementLineDTO::getVendorCode, e.getVendorCode());
            record.set(PurchaseRequirementLineDTO::getVendorName, e.getVendorName());
            record.set(PurchaseRequirementLineDTO::getNotaxPrice, e.getNotaxPrice());
            record.set(PurchaseRequirementLineDTO::getTaxRate, e.getTaxRate());
            record.set(PurchaseRequirementLineDTO::getExtWarrantyPeriod, e.getExtWarrantyPeriod());
            record.set(PurchaseRequirementLineDTO::getExtInvoiceType, e.getExtInvoiceType());
            record.set(PurchaseRequirementLineDTO::getExtLeadTime, e.getExtLeadTime());
            record.set(PurchaseRequirementLineDTO::getExtAdvancePaymentRemark, e.getExtAdvancePaymentRemark());
            lineRecords.add(record);
        });
        qlService.update("PurchaseRequirementLine", lineRecords);

        // 保存申请头
        List<Serializable> serializables = qlService.updateByWrapper(QlWrappers.update("PurchaseRequirementHead")
                .set(PurchaseRequirementHeadDTO::getExtInPool, YesOrNo.YES.getValue())
                .set(PurchaseRequirementHeadDTO::getVersion, PurchaseUtils.increaseVersion(requirementHead.getVersion()))
                .eq(PurchaseRequirementHeadDTO::getRequirementHeadId, requirementHead.getRequirementHeadId())
                .eq(PurchaseRequirementHeadDTO::getVersion, requirementHead.getVersion()));
        Assert.notEmpty(serializables, "数据发生变化请刷新重试");

        // 是否符合自动转单：有价格，有采购员，自动转单规则配置了品类和公司
        List<PurchaseRequirementLineDTO> items = requirementLines.stream()
                .filter(e -> e.getCeeaPerformUserId() != null && PrBuyTypeEnum.HAS_PRICE.name().equals(e.getExtBuyType()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        List<String> ruleList = getTransferOrderRules(items);
        if (CollectionUtils.isEmpty(ruleList)) {
            return;
        }

        // 创建订单
        List<PurchaseRequirementLineDTO> validLines = items.stream().filter(e -> ruleList.contains(getPurchaserKey(e.getOrgId(), e.getCategoryId()))).collect(Collectors.toList());
        groupCreateOrder(validLines, true, priceMap, null, PurchaseOrderEnum.APPROVED_INVALID);
    }

    private void setPurchaser(List<PurchaseRequirementLineDTO> requirementLines) {
        if (CollectionUtils.isEmpty(requirementLines)) {
            return;
        }

        List<Long> orgIds = requirementLines.stream().map(e -> e.getOrgId()).collect(Collectors.toList());
        List<Long> categoryIds = requirementLines.stream().map(e -> e.getCategoryId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(orgIds) || CollectionUtils.isEmpty(categoryIds)) {
            return;
        }

        Map<String, DivisionCategory> purchaserMap = divisionCategoryService.list(new LambdaQueryWrapper<DivisionCategory>()
                        .in(DivisionCategory::getOrgId, orgIds)
                        .in(DivisionCategory::getCategoryId, categoryIds)
                        .eq(DivisionCategory::getDuty, "purchaser")
                        .le(DivisionCategory::getStartDate, LocalDate.now())
                        .and(wrapper -> wrapper.isNull(DivisionCategory::getEndDate).or().ge(DivisionCategory::getEndDate, LocalDate.now())))
                .stream().collect(Collectors.toMap(e -> getPurchaserKey(e.getOrgId(), e.getCategoryId()), e -> e, (v1, v2) -> v1));
        if (MapUtils.isEmpty(purchaserMap)) {
            return;
        }

        requirementLines.forEach(e -> {
            DivisionCategory divisionCategory = purchaserMap.get(getPurchaserKey(e.getOrgId(), e.getCategoryId()));
            if (divisionCategory != null) {
                e.setCeeaPerformUserId(divisionCategory.getPersonInChargeUserId());
                e.setCeeaPerformUserName(divisionCategory.getPersonInChargeUsername());
                e.setCeeaPerformUserNickname(divisionCategory.getPersonInChargeNickname());
                e.setApplyStatus(RequirementApplyStatus.ASSIGNED);
            }
        });

        // 查询采购员公司
        List<String> usernames = purchaserMap.values().stream().map(purchaser -> purchaser.getPersonInChargeUsername()).collect(Collectors.toList());
        Map<String, Long> userOrgMap = new HashMap<>(15);
        usernames.stream().forEach(e -> {
            userOrgMap.put(e, getPurchaserOrgId(e));
        });
        requirementLines.stream().filter(e->StringUtils.isNotBlank(e.getCeeaPerformUserName())).forEach(e -> {
            Long orgId = userOrgMap.get(e.getCeeaPerformUserName());
            e.setPurchaseOrganization(orgId.toString());
        });
    }

    /**
     * 設置購買類型
     */
    private void setBuyType(List<PurchaseRequirementLineDTO> requirementLines) {
        if (CollectionUtils.isEmpty(requirementLines)) {
            return;
        }

        // 筛选出无价格，有物料编码，有采购员
        Map<String, List<PurchaseRequirementLineDTO>> orgReqLineMap = requirementLines.stream()
                .filter(e -> PrBuyTypeEnum.NONE_PRICE.name().equals(e.getExtBuyType())
                        && StringUtils.isNotBlank(e.getMaterialCode())
                        && e.getCeeaPerformUserId() != null)
                .collect(Collectors.groupingBy(o -> Objects.toString(o.getOrgId())));
        if (MapUtils.isEmpty(orgReqLineMap)) {
            return;
        }

        // 查詢配置
        List<PrRecentPurchaseConfig> recentPurchaseConfigs = qlService.queryByWrapper(QlWrappers.query("PrRecentPurchaseConfig")
                .in(PrRecentPurchaseConfig::getOrgId, orgReqLineMap.keySet()), PrRecentPurchaseConfig.class);
        if (CollectionUtils.isEmpty(recentPurchaseConfigs)) {
            log.info("没有查到近期采购类配置，{}", JSONUtil.toJsonStr(orgReqLineMap.keySet()));
            return;
        }

        // 查詢歷史數據並計算購買類型
        Date now = new Date();
        recentPurchaseConfigs.forEach(e -> {
            BigDecimal configAmount = e.getAmount(); // 金額
            BigDecimal configMultiple = e.getMultiple();// 倍數

            // 查历史数据
            List<PurchaseRequirementLineDTO> lines = orgReqLineMap.get(e.getOrgId().toString());
            Map<String, List<PurchaseRequirementLineDTO>> materialReqLineMap = lines.stream()
                    .collect(Collectors.groupingBy(PurchaseRequirementLineDTO::getMaterialCode));
            List<PrRecentPurchaseData> historyDatas = qlService.queryByWrapper(QlWrappers.query("PrRecentPurchaseData")
                            .eq(PrRecentPurchaseData::getStatus, YesOrNo.YES.getValue())
                            .eq(PrRecentPurchaseData::getOrgId, e.getOrgId())
                            .in(PrRecentPurchaseData::getMaterialCode, materialReqLineMap.keySet())
                            .ge(PrRecentPurchaseData::getEndTime, now)
                    , PrRecentPurchaseData.class);
            if (CollectionUtils.isEmpty(historyDatas)) {
                log.info("没有查到近期采购基础数据，orgId: {}, materialCodes: {}", e.getOrgId(), JSONUtil.toJsonStr(materialReqLineMap.keySet()));
                return;
            }

            // 計算購買類型
            Map<Long, PrRecentPurchaseData> materialDataMap = historyDatas.stream()
                    .collect(Collectors.toMap(PrRecentPurchaseData::getMaterialId, Function.identity(), (v1, v2)->v1));
            // 最大数量
            Map<Long, BigDecimal> qtyMaxMap = new HashMap<>(15);
            // 历史单价
            Map<Long, PrRecentPurchaseData> priceMap = new HashMap<>(15);
            materialDataMap.forEach((materialId, prRecentPurchaseData) -> {
                qtyMaxMap.put(materialId, BigDecimalUtil.mul(prRecentPurchaseData.getOrderNum(), configMultiple));
                priceMap.put(materialId, prRecentPurchaseData);
            });

            // 设置购买类型
            lines.stream().filter(reqLine -> qtyMaxMap.containsKey(reqLine.getMaterialId())).forEach(reqLine -> {
                PrRecentPurchaseData prRecentPurchaseData = priceMap.get(reqLine.getMaterialId());
                boolean qtyPass = BigDecimalUtil.sub(qtyMaxMap.get(reqLine.getMaterialId()), reqLine.getRequirementQuantity()).compareTo(BigDecimal.ZERO) >= 0;
                boolean amountPass = BigDecimalUtil.sub(configAmount, BigDecimalUtil.mul(prRecentPurchaseData.getTaxPrice(), reqLine.getRequirementQuantity())).compareTo(BigDecimal.ZERO) >= 0;
                if (qtyPass || amountPass) {
                    reqLine.setExtBuyType(PrBuyTypeEnum.RECENT_PURCHASE.name());
                    reqLine.setVendorId(prRecentPurchaseData.getVendorId());
                    reqLine.setVendorCode(prRecentPurchaseData.getVendorCode());
                    reqLine.setVendorName(prRecentPurchaseData.getVendorName());
                    reqLine.setNotaxPrice(prRecentPurchaseData.getNoTaxPrice());
                    reqLine.setTaxRate(prRecentPurchaseData.getTaxRate());
                    reqLine.setExtWarrantyPeriod(prRecentPurchaseData.getWarrantyPeriod());
                    reqLine.setExtInvoiceType(prRecentPurchaseData.getInvoiceType());
                    reqLine.setExtLeadTime(prRecentPurchaseData.getLeadTime());
                    reqLine.setExtAdvancePaymentRemark(prRecentPurchaseData.getAdvancePaymentRemark());
                } else {
                    log.info("数量和金额都不符合近期采购，reqLineId: {}", reqLine.getRequirementLineId());
                }
            });
        });
    }

    /**
     * 查询价格目录
     */
    @Override
    public Map<String, OrderPriceParams> getPriceMap(List<PurchaseRequirementLineDTO> requirementLines) {
        if (CollectionUtils.isEmpty(requirementLines)) {
            return Collections.emptyMap();
        }

        List<Long> orgIds = requirementLines.stream().map(e -> e.getOrgId()).distinct().collect(Collectors.toList());
        List<Long> materialIds = requirementLines.stream().map(e -> e.getMaterialId()).distinct().collect(Collectors.toList());
        List<String> areaCodes = requirementLines.stream().map(e -> e.getExtAreaCode()).distinct().collect(Collectors.toList());
        PriceAgreementQueryDTO priceAgreementQueryDTO = new PriceAgreementQueryDTO().setOrgIds(orgIds).setMaterialIds(materialIds);
        log.info("查询价格入参：{}", JSONUtil.toJsonStr(priceAgreementQueryDTO));
        List<PriceAgreementDTO> priceList = souExtClient.getValidPriceList(priceAgreementQueryDTO);
        log.info("查询价格返回：{}", JSONUtil.toJsonStr(priceList));

        if (CollectionUtils.isEmpty(priceList)) {
            return Collections.emptyMap();
        }

        Map<String, OrderPriceParams> priceParamsMap = new HashMap<>(15);
        priceList.forEach(e -> {
            String supplyArea = e.getSupplyArea();
            if (StringUtils.isBlank(supplyArea)) {
                return;
            }
            Integer warrantyPeriod = e.getSellByDate();
            Integer leadTime = e.getLeadTime();
            OrderPriceParams priceParams = new OrderPriceParams()
                    .setSupplyArea(e.getSupplyArea()).setMaterialId(e.getMaterialId())
                    .setNoTaxPrice(e.getPriceTax()).setTaxPrice(e.getRatePrice()).setTaxRate(e.getTaxRate())
                    .setVendorId(e.getSupId()).setVendorCode(e.getSupCode()).setVendorName(e.getSupName())
                    .setCurrencyName(e.getCurrencyType()).setLeadTime(leadTime)
                    .setInvoiceType(e.getInvoiceType()).setWarrantyPeriod(warrantyPeriod)
                    .setPaymentTerm(e.getPayment()).setPaymentMethod(e.getPayWay()).setBrand(e.getBrand())
                    .setAgreementInfoId(e.getAgreementInfoId()).setAgreementType(e.getAgreementType());
            for (String area : supplyArea.split(SIGN_COMMA)) {
                if (areaCodes.contains(area)) {
                    priceParamsMap.put(getPriceAgreementKey(e.getCompanyId(), area, e.getMaterialId()), priceParams);
                }
            }
        });
        return priceParamsMap;

//        return priceList.stream().collect(Collectors.toMap(e -> getPriceAgreementKey(e.getCompanyId(), e.getSupplyArea(), e.getMaterialId()),
//                e -> {
//                    Integer warrantyPeriod = StringUtils.isBlank(e.getSellByDate()) ? null : Integer.parseInt(e.getSellByDate());
//                    Integer leadTime = StringUtils.isBlank(e.getLeadTime()) ? null : Integer.parseInt(e.getLeadTime());
//                    return new OrderPriceParams()
//                            .setSupplyArea(e.getSupplyArea()).setMaterialId(e.getMaterialId())
//                            .setNoTaxPrice(e.getPriceTax()).setTaxPrice(e.getRatePrice()).setTaxRate(e.getTaxRate())
//                            .setVendorId(e.getSupId()).setVendorCode(e.getSupCode()).setVendorName(e.getSupName())
//                            .setCurrencyName(e.getCurrencyType()).setLeadTime(leadTime)
//                            .setInvoiceType(e.getInvoiceType()).setWarrantyPeriod(warrantyPeriod)
//                            .setPaymentTerm(e.getPayment()).setPaymentMethod(e.getPayWay()).setBrand(e.getBrand());
//                }, (v1, v2) -> v1));
    }


    @Override
    public List<OrderSaveRequestDTO> groupCreateOrder(List<PurchaseRequirementLineDTO> items,
                                                      Boolean fromPriceAgreement,
                                                      Map<String, OrderPriceParams> priceMap,
                                                      Map<Long, BigDecimal> orderQtyMap,
                                                      PurchaseOrderEnum status) {
        items.forEach(e -> {
            OrderPriceParams priceParams = fromPriceAgreement ? priceMap.get(getPriceAgreementKey(e.getOrgId(), e.getExtAreaCode(), e.getMaterialId()))
                    : priceMap.get(getFixPriceKey(e.getRequirementLineId()));
            e.setUnitPrice(priceParams.getNoTaxPrice());
            e.setTaxPrice(priceParams.getTaxPrice());
            e.setTaxRate(priceParams.getTaxRate());
            e.setCurrencyName(priceParams.getCurrencyName());
            e.setInvoiceType(priceParams.getInvoiceType());
            e.setWarrantyPeriod(priceParams.getWarrantyPeriod());
            e.setLeadTime(priceParams.getLeadTime());
            e.setPaymentMethod(priceParams.getPaymentMethod());
            e.setPaymentTerm(priceParams.getPaymentTerm());
            e.setVendorId(priceParams.getVendorId());
            e.setVendorCode(priceParams.getVendorCode());
            e.setVendorName(priceParams.getVendorName());
            e.setAgreementType(fromPriceAgreement ?priceParams.getAgreementType():"询比价");
        });

        // 需求分组
        Map<String, List<PurchaseRequirementLineDTO>> orderGroupMap = items.stream().collect(Collectors.groupingBy(e -> e.getOrderGroupKey()));
        log.info("需求分组结果：{}", JSONUtil.toJsonStr(orderGroupMap));
        List<OrderSaveRequestDTO> orderList = new ArrayList<>();
        orderGroupMap.values().forEach(lines -> {
            OrderSaveRequestDTO orderDTO = createOrder(lines, fromPriceAgreement, priceMap, orderQtyMap, status);
            orderList.add(orderDTO);
        });
        return orderList;
    }

    /**
     * 创建订单
     * @param items              参数
     * @param fromPriceAgreement 参数
     * @param priceMap
     * @param orderQtyMap        参数
     * @param status             参数
     * @return
     */
    @Override
    public OrderSaveRequestDTO createOrder(List<PurchaseRequirementLineDTO> items,
                                           Boolean fromPriceAgreement,
                                           Map<String, OrderPriceParams> priceMap,
                                           Map<Long, BigDecimal> orderQtyMap,
                                           PurchaseOrderEnum status) {
        if (CollectionUtils.isEmpty(items)) {
            return null;
        }

        Order order = new Order();
        List<OrderDetail> orderDetails = new ArrayList<>();
        log.info("京东商城测试items==>"+items);
        items.forEach(item -> {
            OrderPriceParams price = fromPriceAgreement ? priceMap.get(getPriceAgreementKey(item.getOrgId(), item.getExtAreaCode(), item.getMaterialId()))
                    : priceMap.get(getFixPriceKey(item.getRequirementLineId()));
            if (price == null) {
                log.info("没有有效价格,{}-{}-{}", item.getOrgCode(), item.getMaterialCode(), item.getRequirementLineId());
                return;
            }
            if (item.getCeeaPerformUserId() == null) {
                log.info("没有采购员,{}-{}", item.getRequirementHeadNum(), item.getRowNum());
                return;
            }
            item.setPaymentMethod(price.getPaymentMethod());
            item.setPaymentTerm(price.getPaymentTerm());

            if(order.getVendorId() == null) {
                order.setVendorId(price.getVendorId())
                        .setVendorName(price.getVendorName())
                        .setVendorCode(price.getVendorCode())
                        .setRfqSettlementCurrency(price.getCurrencyName());
            }

            OrderDetail orderDetail = new OrderDetail()
                    .setCeeaPlanReceiveDate(Date.from(item.getRequirementDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .setCeeaPromiseReceiveDate(Date.from(item.getRequirementDate().atStartOfDay(ZoneId.systemDefault()).toInstant())) //承诺到货日期 = 要求到货日期（需求日期）
                    .setCeeaUnitNoTaxPrice(price.getNoTaxPrice())   //未税单价
                    .setCeeaUnitTaxPrice(price.getTaxPrice())   //todo 京东测试,临时修改含税单价为集采协议的含税价
                    .setCeeaTaxRate(price.getTaxRate())//税率
                    .setCeeaTaxKey(price.getTaxRate().toString())//税率编码
                    .setCurrencyId(0L)    //币种id
                    .setCurrencyCode(price.getCurrencyName())   //币种编码
                    .setCurrency(price.getCurrencyName()) //币种
                    .setCurrencyName(price.getCurrencyName())  //币种名称
                    .setDeliveryDate(LocalDate.now().plusDays(price.getLeadTime()))// 交货日期
                    .setCategoryId(item.getCategoryId()) //采购分类ID(物料小类id)
                    .setCategoryCode(item.getCategoryCode()) //采购分类编码(物料小类编码)
                    .setCategoryName(item.getCategoryName()) //采购分类全名(物料小类名称)
                    .setMaterialId(item.getMaterialId())  //物料ID
                    .setMaterialCode(item.getMaterialCode())  //物料编码
                    .setMaterialName(item.getMaterialName())  //物料名称
                    .setSpecification(item.getExtMaterialModel()) //型号
                    .setReceiptPlace(item.getReceiveAddress())  //收货地点
                    .setReceivedFactory(item.getExtReceiver())
                    .setUnit(item.getUnit())  //单位
                    .setUnitCode(item.getUnitCode())
                    .setOrderNum(MapUtils.isEmpty(orderQtyMap)? item.getRequirementQuantity() : orderQtyMap.get(item.getRequirementLineId()))  //订单数量(本次下单数量)
                    .setRequirementDateBuff(item.getRequirementDate())
                    .setRequirementDate(item.getRequirementDate()) //需求日期
                    .setRequirementQuantity(item.getRequirementQuantity()) //需求数量
                    .setComments(item.getComments())  //备注
                    .setReceiveSum(new BigDecimal(0)) //订单累计收货量
                    .setCeeaIfRequirement("Y") //物料行是否采购需求
                    .setCeeaRequirementLineId(item.getRequirementLineId()) //采购申请物料行主键id
                    .setCeeaRequirementHeadNum(item.getRequirementHeadNum())  //采购申请编号(longi)
                    .setCeeaRowNum(String.valueOf(item.getRowNum()))  //
                    .setCeeaOrganizationId(item.getOrganizationId()) //库存组织ID
                    .setCeeaOrganizationName(item.getOrganizationName())  //库存组织名称
                    .setCeeaOrganizationCode(item.getOrganizationCode()) //库存组织编码
                    .setCeeaPriceSourceType(fromPriceAgreement ? ExtPriceSourceEnum.PRICE_AGREE.name() : ExtPriceSourceEnum.FIX_PRICE.name())
                    .setCeeaPriceSourceId(item.getCeeaPriceSourceId())
                    .setOrderDetailStatus(PurchaseOrderEnum.DRAFT == status ? OrderDetailStatus.DRAFT : OrderDetailStatus.WAITING_VENDOR_CONFIRM);

            Map orderDetailExts = new HashMap(50);
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtAttachId), item.getExtAttachId());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtAttachName), item.getExtAttachName());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtBrand), fromPriceAgreement?price.getBrand():item.getBrand());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtBuyType), item.getAgreementType());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtUseDepartmentCode), item.getExtUseDepartmentCode());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtUseDepartmentName), item.getExtUseDepartmentName());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtUserCode), item.getExtUserCode());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtUserName), item.getExtUserName());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtAreaCode), item.getExtAreaCode());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtDeliveryCycle), price.getLeadTime());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtInvoiceType), price.getInvoiceType());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtWarrantyPeriod), price.getWarrantyPeriod());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtAgreementInfoId), price.getAgreementInfoId());
//            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtAgreementType), price.getAgreementType());
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtAgreementType), "询比价");
            orderDetailExts.put(LambdaUtil.getFieldName(ExtOrderDetail::getExtOrderRemark), item.getOrderRemark());
            orderDetail.setExtensions(orderDetailExts);
            orderDetails.add(orderDetail);
        });

        if (CollectionUtils.isEmpty(orderDetails) || orderDetails.size() != items.size()) {
            return null;
        }

        //是否供应商确认
        order.setCeeaIfSupplierConfirm(YesOrNo.YES.getValue())
                //业务实体ID
                .setCeeaOrgId(items.get(0).getOrgId())
                //业务实体编码
                .setCeeaOrgCode(items.get(0).getOrgCode())
                //业务实体名称
                .setCeeaOrgName(items.get(0).getOrgName())
                //库存组织ID
                .setOrganizationId(items.get(0).getOrganizationId())
                //库存组织编码
                .setOrganizationCode(items.get(0).getOrganizationCode())
                //库存组织名称
                .setOrganizationName(items.get(0).getOrganizationName())
                //采购员名称
                .setCeeaEmpUsername(items.get(0).getCeeaPerformUserNickname())
                //采购员工号
                .setCeeaEmpNo(items.get(0).getCeeaPerformUserName())
                //采购员用户id
                .setCeeaEmpUseId(items.get(0).getCeeaPerformUserId())
                .setReceiveAddress(items.get(0).getReceiveAddress())
                .setReceiveContact(items.get(0).getExtReceiver())
                .setReceiveTelephone(items.get(0).getReceiveTelephone())
                .setPaymentMethod(items.get(0).getPaymentMethod())
                .setTermOfPayment(items.get(0).getPaymentTerm())
                //是否批量小订单
                .setIfSample("N")
                .setCeeaPurchaseOrderDate(new Date())
                .setOrderStatus(PurchaseOrderEnum.DRAFT)
                .setOrderType(fromPriceAgreement ? ExtOrderTypeEnum.PRICE_AGREEMENT.name() : ExtOrderTypeEnum.FIX_PRICE.name())
                .setSourceSystem(SourceSystemEnum.DEMAND.getValue())
                .setOrderStatus(status);

        Map orderExts = new HashMap(50);
        orderExts.put(LambdaUtil.getFieldName(ExtOrder::getExtApplyDate), new Date());
        //采购员id
        orderExts.put(LambdaUtil.getFieldName(ExtOrder::getCeeaEmpUserId), items.get(0).getCeeaPerformUserId());
        orderExts.put(LambdaUtil.getFieldName(ExtOrder::getExtAreaCode), items.get(0).getExtAreaCode());
        orderExts.put(LambdaUtil.getFieldName(ExtOrder::getExtOrderProperty), ExtOrderProperty.MATERIAL);

        // 设置采购员和供应商联系人信息
        if (PurchaseOrderEnum.APPROVED_INVALID == status) {
            ExtUser extUser = extRbacClient.getByUserId(items.get(0).getCeeaPerformUserId());
            if (extUser != null) {
                order.setCeeaDepartmentId(extUser.getCeeaDeptId());
                order.setCeeaDepartmentName(extUser.getDepartment());
                orderExts.put(LambdaUtil.getFieldName(ExtOrder::getExtPurchaserEmail), extUser.getEmail());
                orderExts.put(LambdaUtil.getFieldName(ExtOrder::getExtPurchaserPhone), extUser.getExtOfficePhone());
                orderExts.put(LambdaUtil.getFieldName(ExtOrder::getExtPurchaserOrgName), extUser.getCeeaCompany());
            }

            List<ContactInfo> contactInfos = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query("ContactInfo")
                    .eq(ContactInfo::getCompanyId, order.getVendorId()), ContactInfo.class);
            if (CollectionUtils.isNotEmpty(contactInfos)) {
                ContactInfo contactInfo = contactInfos.stream()
                        .filter(e -> YesOrNo.YES.getValue().equals(e.getCeeaDefaultContact())).findAny().orElse(null);
                contactInfo = contactInfo != null ? contactInfo : contactInfos.get(0);
                orderExts.put(LambdaUtil.getFieldName(ExtOrder::getExtVendorContacts), contactInfo.getContactName());
                orderExts.put(LambdaUtil.getFieldName(ExtOrder::getExtVendorPhone), contactInfo.getCeeaContactMethod());
            }
        }
        order.setExtensions(orderExts);

        OrderSaveRequestDTO orderSaveRequestDTO = new OrderSaveRequestDTO()
                .setOrder(order)
                .setDetailList(orderDetails);

        orderService.save(orderSaveRequestDTO);
        sendOrderMsgToVendor(order);

        /**
         * 订单创建之后新增对京东订单的处理
         *
         * 1、将订单的SRM收货地址转换为京东地址并保存
         * 2、对明细行物料的可采性进行校验，校验不通过需要设置订单明细行状态为拒绝，并在订单备注中新增原因
         * 3、提交订单
         */
        //根据供应商编码来判断，仅校验京东供应商：北京京东工业品贸易有限公司
        //todo 京东的编码暂未确定，后续需要更新
        if (JD001.equals(order.getVendorCode())) {

            //创建新对象保存数据
            ExtOrder extOrder = new ExtOrder();
            StringBuffer rejectReson = new StringBuffer();
            if (StringUtils.isNotEmpty(extOrder.getComments())) {
                rejectReson.append(extOrder.getComments());
                rejectReson.append("\r\n");
            }

            List<ExtOrderDetail> extOrderDetails = new ArrayList<>();
            BeanCopyUtil.copyProperties(extOrder,order);
            log.info("京东商城测试extOrder==>"+JSON.toJSONString(extOrder));

            //保存后订单明细有新生成的id,所以需要重新查询数据
            QlQueryWrapper orderQueryWrapper = QlWrappers.query("OrderDetail");
            orderQueryWrapper.select(ExtOrderDetail::getOrderDetailId,ExtOrderDetail::getOrderId,ExtOrderDetail::getMaterialCode
                            ,ExtOrderDetail::getMaterialId, ExtOrderDetail::getOrderNum,ExtOrderDetail::getCeeaUnitTaxPrice)
                    .eq(ExtOrderDetail::getOrderId,order.getOrderId());
            List<ExtOrderDetail> dbExtOrderDetails = qlService.queryByWrapper(orderQueryWrapper, ExtOrderDetail.class);
            //数据库查询时枚举和时间类型会有类型错误,这里单独给状态赋值为拟定,京东单据在正常情况下此阶段只会是拟定
            dbExtOrderDetails.stream().forEach(extOrderDetail -> extOrderDetail.setOrderDetailStatus(OrderDetailStatus.DRAFT));
            log.info("京东商城测试dbExtOrderDetails==>"+JSON.toJSONString(dbExtOrderDetails));
            for (OrderDetail orderDetail : dbExtOrderDetails) {
                ExtOrderDetail extOrderDetail = new ExtOrderDetail();
                BeanCopyUtil.copyProperties(extOrderDetail,orderDetail);
                extOrderDetails.add(extOrderDetail);
                log.info("京东商城测试extOrderDetail==>"+JSON.toJSONString(extOrderDetail));
            }
            log.info("京东商城测试extOrderDetails==>"+JSON.toJSONString(extOrderDetails));

            //1、 SRM收货地址转换成京东地址
            String receiveAddress = extOrder.getReceiveAddress();
            AddressResultDTO resultDTO = getJdAddress(receiveAddress);
            AddressResultDTO.AddressDetail result = resultDTO.getResult();
            Integer provinceId = result.getProvinceId();
            Integer cityId = result.getCityId();
            Integer countyId = result.getCountyId();
            Integer townId = result.getTownId();
            //国家+一级地址名称+二级地址名称+三级地址名称+四级地址名称
            String jdAddress = result.getNation()+result.getProvince()+result.getCity()+result.getCounty()+result.getTown();
            //更新订单地址为京东地址
            extOrder.setReceiveAddress(jdAddress);

            //2、物料在京东的可采的校验
            List<String> materialCodes = extOrderDetails.stream().map(ExtOrderDetail::getMaterialCode).collect(Collectors.toList());
            //通过外部物料与系统物料的映射表查询京东物料的信息
            LambdaQueryWrapper<ExternalMaterial> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ExternalMaterial::getMaterialCode,materialCodes);
            queryWrapper.eq(ExternalMaterial::getMaterialType,"JD");
            queryWrapper.eq(ExternalMaterial::getMappingFlag,"Y");
            List<ExternalMaterial> externalMaterials = externalMaterialService.list(queryWrapper);
            //转换为map对象，方便后面取用
            Map<String, ExternalMaterial> externalMaterialMap = externalMaterials.stream()
                    .collect(Collectors.toMap(ExternalMaterial::getMaterialCode, Function.identity()));
            Map<String, String> skuIdToMaterialCodeMap = externalMaterials.stream().collect(Collectors.toMap(ExternalMaterial::getSkuId, ExternalMaterial::getMaterialCode));

            //记录存在物料映射，可以进行物料可采校验的物料skuId
            StringBuffer skuIds = new StringBuffer();
            for (ExtOrderDetail extOrderDetail : extOrderDetails) {
                //判断物料是否有外部映射
                if (externalMaterialMap.containsKey(extOrderDetail.getMaterialCode())) {
                    //拼接京东物料的skuId
                    skuIds.append(externalMaterialMap.get(extOrderDetail.getMaterialCode()).getSkuId());
                    //使用,进行分割
                    skuIds.append(SIGN_COMMA);
                } else {
                    //该明细行的物料没有对应生效的京东物料映射
                    //修改明细行状态为拒绝
                    extOrderDetail.setOrderDetailStatus(OrderDetailStatus.REJECT);
                    //备注中明确拒绝原因
                    rejectReson.append(String.format("物料编码为:%s的物料未设置与京东物料的映射关系;",extOrderDetail.getMaterialCode()));
                    rejectReson.append("\r\n");
                }
            }

            if (StringUtils.isNotEmpty(skuIds.toString())) {
                //删除最后的一个,
                if (skuIds.charAt(skuIds.length() - 1) == CHAR_COMMA) {
                    skuIds.deleteCharAt(skuIds.length() - 1);
                }
                //组装物料可采校验的提交参数
                JDOrderDetailTotalCheckRequestDTO checkRequestDTO = new JDOrderDetailTotalCheckRequestDTO();
                checkRequestDTO.setProvince(provinceId.toString());
                checkRequestDTO.setCity(cityId.toString());
                checkRequestDTO.setCounty(countyId.toString());
                checkRequestDTO.setTown(townId.toString());
                checkRequestDTO.setSkuIds(skuIds.toString());
                checkRequestDTO.setMallType(MallTypeEnum.JD.getCode());
                //对物料进行可采校验
                TotalCheckNewResultDTO totalCheckNewResultDTO = mallService.totalCheckNew(checkRequestDTO);
                if (totalCheckNewResultDTO.isSuccess()) {
                    //校验接口调用成功，确定物料的校验状况
                    List<TotalCheckNewResultDTO.TotalCheckNew> checkResult = totalCheckNewResultDTO.getResult();
                    for (TotalCheckNewResultDTO.TotalCheckNew totalCheckNew : checkResult) {
                        if (!totalCheckNew.getCanPurchase()) {
                            //可采校验不通过,设置明细行状态为拒绝，并设置备注
                            //从map中通过skuId确定物料编码
                            String rejectMaterialCode = skuIdToMaterialCodeMap.get(totalCheckNew.getSkuId().toString());
                            //修改对应明细行的状态
                            extOrderDetails.stream().filter(extOrderDetail -> extOrderDetail.getMaterialCode().equals(rejectMaterialCode))
                                    .forEach(extOrderDetail -> {
                                        extOrderDetail.setOrderDetailStatus(OrderDetailStatus.REJECT);
                                    });
                            //设置备注
                            rejectReson.append(String.format("物料编码为:%s的物料可采购校验失败，失败原因:%s;",rejectMaterialCode,totalCheckNew.getMessage()));
                            rejectReson.append("\r\n");
                        }
                    }

                    //3、可采校验完成，校验通过的明细行，提交订单
                    //获取可采校验通过的订单行明细
                    List<ExtOrderDetail> submitExtOrderDetail = new ArrayList<>();
                    for (ExtOrderDetail extOrderDetail : extOrderDetails) {
                        if (OrderDetailStatus.DRAFT.equals(extOrderDetail.getOrderDetailStatus())) {
                            submitExtOrderDetail.add(extOrderDetail);
                        }
                    }

                    if (CollectionUtils.isNotEmpty(submitExtOrderDetail)) {

                        /** 组装下单商品信息 **/
                        JSONArray sku = getSku(submitExtOrderDetail, externalMaterialMap);

                        /** 查询收货信息 **/
                        QlOpenQueryWrapper siteWrapper = QlOpenWrappers.query("Site");
                        siteWrapper.eq(true, "organizationId", extOrder.getCeeaOrgId());
                        List<Site> siteList = qlOpenClient.query(ContextPath.BASE, siteWrapper, Site.class);
                        log.info("京东测试siteList:==>"+JSON.toJSONString(siteList));
                        //获取默认收货地址
                        Site defaultSite = siteList.stream()
                                .filter(site -> "Y".equals(site.getIsDefault()))
                                .findFirst()
                                .orElse(null);
                        log.info("京东测试defaultSite:==>"+JSON.toJSONString(defaultSite));
                        //将收货地址转换为京东地址,使用地址名称转换
                        AddressResultDTO siteJDAddressResultDTO = getJdAddress(defaultSite.getSiteName());
                        AddressResultDTO.AddressDetail siteJDAddressResult = siteJDAddressResultDTO.getResult();
                        Integer siteProvinceId = siteJDAddressResult.getProvinceId();
                        Integer siteCityId = siteJDAddressResult.getCityId();
                        Integer siteCountyId = siteJDAddressResult.getCountyId();
                        //国家+一级地址名称+二级地址名称+三级地址名称+四级地址名称
                        String siteJDAddress = siteJDAddressResult.getNation()+siteJDAddressResult.getProvince()+siteJDAddressResult.getCity()+siteJDAddressResult.getCounty()+siteJDAddressResult.getTown();


                        /** 查询专票信息 **/
                        QlOpenQueryWrapper invoiceWrapper = QlOpenWrappers.query("OrgInvoiceInfo");
                        invoiceWrapper.eq(true, OrgInvoiceInfo::getOrganizationId, extOrder.getCeeaOrgId());
                        List<OrgInvoiceInfo> invoiceInfoList = qlOpenClient.query(ContextPath.BASE, invoiceWrapper, OrgInvoiceInfo.class);
                        if(CollectionUtils.isEmpty(invoiceInfoList)){
                            //确认过，抛出异常会事务回滚
                            throw new BaseException("找不到开票信息，请确认系统是否已配置。");
                        }
                        log.info("京东商城:专票信息invoiceInfoList==>"+JSON.toJSONString(invoiceInfoList));
                        //todo 现在默认取第一个数据,根据沟通结果可能会有变动
                        OrgInvoiceInfo orgInvoiceInfo = invoiceInfoList.get(0);
                        log.info("京东商城:专票信息orgInvoiceInfo==>"+JSON.toJSONString(orgInvoiceInfo));
                        //将专票的地址转换为京东地址
                        AddressResultDTO invoiceJDAddressResultDTO = getJdAddress(orgInvoiceInfo.getAddress());
                        AddressResultDTO.AddressDetail invoiceJDAddressResult = invoiceJDAddressResultDTO.getResult();
                        //国家+一级地址名称+二级地址名称+三级地址名称+四级地址名称
                        String invoiceJDAddress = invoiceJDAddressResult.getNation()+invoiceJDAddressResult.getProvince()+invoiceJDAddressResult.getCity()+invoiceJDAddressResult.getCounty()+invoiceJDAddressResult.getTown();


                        /*组装待提交的订单信息*/
                        JDOrderSubmitRequestDTO orderSubmitRequestDTO = new JDOrderSubmitRequestDTO();
                        orderSubmitRequestDTO.setThirdOrder(extOrder.getOrderNumber());
                        orderSubmitRequestDTO.setSku(sku.toJSONString());
                        orderSubmitRequestDTO.setName(extOrder.getReceiveContact());
                        orderSubmitRequestDTO.setProvince(provinceId);
                        orderSubmitRequestDTO.setCity(cityId);
                        orderSubmitRequestDTO.setCounty(countyId);
                        orderSubmitRequestDTO.setTown(townId);
                        orderSubmitRequestDTO.setAddress(extOrder.getReceiveAddress());
                        orderSubmitRequestDTO.setMobile(extOrder.getReceiveTelephone());
                        orderSubmitRequestDTO.setEmail(extOrder.getExtPurchaserEmail());
                        orderSubmitRequestDTO.setInvoiceState(2);
                        orderSubmitRequestDTO.setInvoiceType(2);
                        orderSubmitRequestDTO.setSelectedInvoiceTitle(5);
                        orderSubmitRequestDTO.setCompanyName(extOrder.getCeeaOrgName());
                        orderSubmitRequestDTO.setInvoiceContent(1);
                        //支付方式101.可能会有变动
                        orderSubmitRequestDTO.setPaymentType(101);
                        orderSubmitRequestDTO.setIsUseBalance(0);
                        orderSubmitRequestDTO.setSubmitState(1);
                        //收票人信息,从组织设置-收货地点中获取
                        orderSubmitRequestDTO.setInvoiceName(defaultSite.getReceiver());
                        orderSubmitRequestDTO.setInvoicePhone(defaultSite.getReceiverPhone());
                        orderSubmitRequestDTO.setInvoiceProvice(siteProvinceId);
                        orderSubmitRequestDTO.setInvoiceCity(siteCityId);
                        orderSubmitRequestDTO.setInvoiceCounty(siteCountyId);
                        orderSubmitRequestDTO.setInvoiceAddress(siteJDAddress);
                        //专票信息,从组织设置-开票信息中获取
                        orderSubmitRequestDTO.setRegCompanyName(orgInvoiceInfo.getCompanyName());
                        orderSubmitRequestDTO.setRegCode(orgInvoiceInfo.getTaxpayerNum());
                        orderSubmitRequestDTO.setRegAddr(invoiceJDAddress);
                        orderSubmitRequestDTO.setRegPhone(orgInvoiceInfo.getPhone());
                        orderSubmitRequestDTO.setRegBank(orgInvoiceInfo.getOpeningName());
                        orderSubmitRequestDTO.setRegBankAccount(orgInvoiceInfo.getOpeningAccount());
                        orderSubmitRequestDTO.setMallType(MallTypeEnum.JD.getCode());

                        //提交订单
                        OrderSubmitResultDTO orderSubmitResultDTO = mallService.submitOrder(orderSubmitRequestDTO);
                        if (orderSubmitResultDTO.isSuccess()) {
                            //提交成功，更新数据
                            OrderSubmitResultDTO.JDOrderSubmitResult submitResult = orderSubmitResultDTO.getResult();
                            //更新京东订单id
                            extOrder.setExtJdOrderId(submitResult.getJdOrderId());
                            extOrder.setExtJdState(1);
                            //更新订单明细状态
                            for (ExtOrderDetail extOrderDetail : extOrderDetails) {
                                if (OrderDetailStatus.DRAFT.equals(extOrderDetail.getOrderDetailStatus())) {
                                    //修改订单状态为供应商已确认
                                    extOrderDetail.setOrderDetailStatus(OrderDetailStatus.ACCEPT);
                                }
                            }
                            //订单提交成功,更新订单状态为供应商已确认
                            extOrder.setOrderStatus(PurchaseOrderEnum.APPROVED);
                        } else {
                            //订单提交失败，修改所有明细行的状态为拒绝，并更新备注
                            for (ExtOrderDetail extOrderDetail : extOrderDetails) {
                                extOrderDetail.setOrderDetailStatus(OrderDetailStatus.REJECT);
                            }
                            //订单提交失败,修改订单状态为已拒绝
                            extOrder.setOrderStatus(PurchaseOrderEnum.REFUSED);
                            //设置备注
                            rejectReson.append("订单提交失败:"+orderSubmitResultDTO.getResultMessage());
                        }
                    } else {
                        //没有商品可以采购,修改订单状态为已拒绝
                        extOrder.setOrderStatus(PurchaseOrderEnum.REFUSED);
                        //设置备注
                        rejectReson.append("没有商品可以采购");
                    }
                } else {
                    //校验接口调用失败，修改所有明细行的状态为拒绝，并设置备注
                    for (ExtOrderDetail extOrderDetail : extOrderDetails) {
                        extOrderDetail.setOrderDetailStatus(OrderDetailStatus.REJECT);
                    }
                    //所有的物料都校验失败,修改订单状态为已拒绝
                    extOrder.setOrderStatus(PurchaseOrderEnum.REFUSED);
                    //设置备注
                    rejectReson.append("物料可采校验失败，请联系管理员处理");
                }
            } else {
                //没有映射成功的商品
                for (ExtOrderDetail extOrderDetail : extOrderDetails) {
                    extOrderDetail.setOrderDetailStatus(OrderDetailStatus.REJECT);
                }
                //所有的物料都校验失败,修改订单状态为已拒绝
                extOrder.setOrderStatus(PurchaseOrderEnum.REFUSED);
                //设置备注
                rejectReson.append("没有物料维护映射关系");
            }


            //更新订单
            List<ExtOrder> updateExtOrder = new ArrayList<>();
            extOrder.setComments(rejectReson.toString());
            updateExtOrder.add(extOrder);
            qlService.update("Order", PurchaseMqlUtils.beanToRecords(updateExtOrder));
            //更新订单明细
            qlService.update("OrderDetail", PurchaseMqlUtils.beanToRecords(extOrderDetails));

            //封装新的返回的對象
            OrderSaveRequestDTO returnDTO = new OrderSaveRequestDTO();
            returnDTO.setOrder(extOrder);
            List<OrderDetail> returnOrderDetails = new ArrayList<>();
            for (ExtOrderDetail extOrderDetail : extOrderDetails) {
                OrderDetail returnOrderDetail = new OrderDetail();
                BeanCopyUtil.copyProperties(returnOrderDetail,extOrderDetail);
            }
            returnDTO.setDetailList(returnOrderDetails);
        }
        return orderSaveRequestDTO;
    }


    /**
     * 组装提交订单的ksu参数
     * @param extOrderDetails   校验通过的订单明细行
     * @param externalMaterialMap   物料与京东物料的映射关系
     * @return
     */
    @NotNull
    private static JSONArray getSku(List<ExtOrderDetail> extOrderDetails,Map<String, ExternalMaterial> externalMaterialMap) {
        JSONArray sku = new JSONArray();
        log.info("京东商城getSku|extOrderDetails==>"+JSON.toJSONString(extOrderDetails));
        log.info("京东商城getSku|externalMaterialMap==>"+JSON.toJSONString(externalMaterialMap));
        for (ExtOrderDetail extOrderDetail : extOrderDetails) {
            //组装对象
            JSONObject skuItem = new JSONObject();
            skuItem.put("skuId",externalMaterialMap.get(extOrderDetail.getMaterialCode()).getSkuId());
            skuItem.put("num",extOrderDetail.getOrderNum());
            skuItem.put("price",extOrderDetail.getCeeaUnitTaxPrice());
            skuItem.put("bNeedGift",false);
            sku.add(skuItem);
        }
        log.info("getSku|sku==>"+sku);
        return sku;
    }

    /**
     * 转换成京东地址
     * @param receiveAddress 收货地址
     * @return JdResultDTO 响应报文
     */
    @NotNull
    private AddressResultDTO getJdAddress(String receiveAddress) {
        JDAddressRequestDTO jdAddressRequestDTO = new JDAddressRequestDTO();
        jdAddressRequestDTO.setAddress(receiveAddress);
        jdAddressRequestDTO.setMallType(MallTypeEnum.JD.getCode());
        log.info("发起接口getSkuInfo调用:{}", JsonUtils.toJsonString(jdAddressRequestDTO));
        AddressResultDTO resultDTO = mallService.getAddressFromAddress(jdAddressRequestDTO);
        if(!STR0000.equals(resultDTO.getResultCode())){
            throw new BaseException("京东地址转换异常，地址不存在或异常。");
        }
        return resultDTO;
    }

    public void sendOrderMsgToVendor(Order order){
        if(ObjectUtil.isNotNull(order.getExtensions().get(LambdaUtil.getFieldName(ExtOrder::getExtVendorPhone)))) {
            //短信发送客户端
            String phone = order.getExtensions().get(LambdaUtil.getFieldName(ExtOrder::getExtVendorPhone)).toString();
            String PurchaseOrderDate = DateUtil.format(order.getCeeaPurchaseOrderDate(),"yyyy-MM-dd");
            String content = order.getCeeaOrgName()+"于"+PurchaseOrderDate+" 向您下单,订单编号"+order.getOrderNumber()+",请尽快登录长城慧采云平台确认";
            log.info(MessageFormat.format("发送短信，手机号{0}，短信内容：{1}", phone, content));
            pjProjectExtClient.message(content,phone);
        }

    }

    /**
     * 非招创建寻源
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public SouProject createSou(ExtPurchaseRequirementCreateSouDTO param) {
        param.setSouType(StringUtils.trimToNull(param.getSouType()));
        AssertUtils.notNull(param.getSouType(), "缺少souType参数");
        // 1: 初始化上下文
        ExtPrRequirementCreateSouContext context = new ExtPrRequirementCreateSouContext(param);
        // 2: 校验操作条件/权限
        context = SdkPluginProxy.proxy(IExtPrRequirementCreateSouPlugin.class, context).judgeCreateSouAuth(context);
        // 3: 前置处理
        context = SdkPluginProxy.proxy(IExtPrRequirementCreateSouPlugin.class, context).beforeCreateSou(context);
        // 4: 执行处理
        context = SdkPluginProxy.proxy(IExtPrRequirementCreateSouPlugin.class, context).executeCreateSou(context);
        // 5: 后置处理
        context = SdkPluginProxy.proxy(IExtPrRequirementCreateSouPlugin.class, context).afterCreateSou(context);

        return context.getResult();
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void createOrderByFixPrice(ExtFixPriceHeadDTO extFixPriceHeadDTO) {
        // 创建定价订单
        createFixPriceOrder(extFixPriceHeadDTO);

        // 写入近期采购数据
        createRecentPurchaseData(extFixPriceHeadDTO);
    }

    private void createFixPriceOrder(ExtFixPriceHeadDTO extFixPriceHeadDTO){
        List<ExtFixPriceLine> priceLines = extFixPriceHeadDTO.getLineList();
        if(CollectionUtils.isEmpty(priceLines)){
            return;
        }

        // 查询需求行
        Map<Long, Long> reqPriceMap = new HashMap<>(15);
        priceLines.stream().filter(e -> StringUtils.isNotBlank(e.getRequirementLineIds())).forEach(price -> {
            Arrays.asList(price.getRequirementLineIds().split(SIGN_COMMA)).forEach(reqLineId -> {
                reqPriceMap.put(Long.parseLong(reqLineId), price.getFixPriceLineId());
            });
        });
        if (MapUtils.isEmpty(reqPriceMap)) {
            log.info("没有关联需求");
            return;
        }

        List<PurchaseRequirementLineDTO> reqLines = qlService.readByKeys("PurchaseRequirementLine", new ArrayList(reqPriceMap.keySet()), PurchaseRequirementLineDTO.class);

        // 查询转单配置
        List<String> ruleList = getTransferOrderRules(reqLines);
        if (CollectionUtils.isEmpty(ruleList)) {
            return;
        }
        List<PurchaseRequirementLineDTO> validReqLines = reqLines.stream()
                .filter(e -> ruleList.contains(getPurchaserKey(e.getOrgId(), e.getCategoryId()))).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(validReqLines)){
            return;
        }

        // 订单分组
        Map<Long, ExtFixPriceLine> fixPriceMap = priceLines.stream()
                .collect(Collectors.toMap(e -> e.getFixPriceLineId(), e -> e, (v1, v2) -> v1));
        Map<String, OrderPriceParams> priceMap = validReqLines.stream().collect(Collectors.toMap(e -> e.getRequirementLineId().toString(),
                e -> {
                    Long fixPriceLineId = reqPriceMap.get(e.getRequirementLineId());
                    ExtFixPriceLine price = fixPriceMap.get(fixPriceLineId);
                    return new OrderPriceParams()
                            .setVendorId(price.getVendorId()).setVendorCode(price.getVendorCode()).setVendorName(price.getVendorName())
                            .setMaterialId(price.getItemId()).setNoTaxPrice(price.getNotaxPrice()).setTaxRate(price.getTaxRate())
                            .setCurrencyName("RMB").setLeadTime(price.getExtLeadTime()).setWarrantyPeriod(price.getExtWarrantyPeriod())
                            .setInvoiceType(price.getInvoiceType()).setPaymentTerm(price.getPaymentTerm()).setPaymentMethod(price.getPaymentMethod());
                }, (v1, v2) -> v1));
        groupCreateOrder(validReqLines, false, priceMap, null, PurchaseOrderEnum.APPROVED_INVALID);
    }

    /**
     * 创建近期采购基础数据
     */
    private void createRecentPurchaseData(ExtFixPriceHeadDTO extFixPriceHeadDTO){
        List<ExtFixPriceLine> priceLines = extFixPriceHeadDTO.getLineList();
        if(CollectionUtils.isEmpty(priceLines)){
            return;
        }

        // 查询配置
        List<Long> orgIds = priceLines.stream().map(e -> e.getOrgOuId()).distinct().collect(Collectors.toList());
        Map<Long, PrRecentPurchaseConfig> orgConfigMap = qlService.queryByWrapper(QlWrappers.query("PrRecentPurchaseConfig")
                        .in(PrRecentPurchaseConfig::getOrgId, orgIds), PrRecentPurchaseConfig.class)
                .stream().collect(Collectors.toMap(e -> e.getOrgId(), e -> e, (v1, v2) -> v1));
        if (MapUtils.isEmpty(orgConfigMap)) {
            log.info("近期采购配置数据不存在");
            return;
        }

        // 查询历史数据
        Date now = new Date();
        List<String> materialCodes = priceLines.stream().map(e -> e.getItemCode()).distinct().collect(Collectors.toList());
        List<String> existsKeys = qlService.queryByWrapper(QlWrappers.query("PrRecentPurchaseData")
                        .eq(PrRecentPurchaseData::getStatus, YesOrNo.YES.getValue())
                        // 查询已配置的业务实体
                        .in(PrRecentPurchaseData::getOrgId, orgConfigMap.keySet())
                        .in(PrRecentPurchaseData::getMaterialCode, materialCodes)
                        .ge(PrRecentPurchaseData::getEndTime, now)
                , PrRecentPurchaseData.class).stream().map(e -> getOrgMaterialKey(e.getOrgId(), e.getMaterialCode())).collect(Collectors.toList());
        // 获取有效数据
        List<ExtFixPriceLine> validPriceLines = new ArrayList<>();
        Map<String, ExtFixPriceLine> fixPriceLineMap = priceLines.stream().filter(e -> orgConfigMap.containsKey(e.getOrgOuId()))
                .collect(Collectors.toMap(e -> getOrgMaterialKey(e.getOrgOuId(), e.getItemCode()), e -> e, (v1, v2) -> v1));
        fixPriceLineMap.forEach((k,v)->{
            // 不存在则有效
            if(!existsKeys.contains(k)){
                validPriceLines.add(v);
            }
        });
        if (CollectionUtils.isEmpty(validPriceLines)) {
            log.info("近期采购基础数据已存在");
            return;
        }

        // 保存数据
        List<PrRecentPurchaseData> datas = validPriceLines.stream().map(e -> {
            PrRecentPurchaseConfig config = orgConfigMap.get(e.getOrgOuId());
            BigDecimal noTaxPrice = e.getNotaxPrice();
            BigDecimal taxRate = e.getTaxRate();
            BigDecimal taxPrice = noTaxPrice.multiply(taxRate.divide(new BigDecimal(100)).add(BigDecimal.ONE));
            return new PrRecentPurchaseData().setConfigId(config.getConfigId())
                    .setStatus(YesOrNo.YES.getValue()).setStartTime(now).setEndTime(DateUtils.addDays(now, config.getValidDays()))
                    .setOrgId(e.getOrgOuId()).setOrgCode(e.getOrgOuCode()).setOrgName(e.getOrgOuName())
                    .setMaterialId(e.getItemId()).setMaterialCode(e.getItemCode()).setMaterialName(e.getItemDesc())
                    .setMaterialModel(e.getExtMaterialModel()).setUnit(e.getUnit())
                    .setNoTaxPrice(noTaxPrice).setTaxRate(taxRate).setTaxPrice(taxPrice).setOrderAmount(e.getNotaxTotalPrice())
                    .setOrderNum(e.getQuantity()).setDeliveryCycle(new BigDecimal(e.getExtLeadTime()))
                    .setCreatedBy(e.getCreatedBy()).setCreatedFullName(e.getCreatedFullName()).setCreatedId(e.getCreatedId())
                    .setVendorId(e.getVendorId()).setVendorCode(e.getVendorCode()).setVendorName(e.getVendorName())
                    .setCreatedByDepartment(extFixPriceHeadDTO.getOrgDepName())
                    .setAdvancePaymentRemark(e.getAdvancePaymentRemark())
                    .setInvoiceType(e.getInvoiceType()).setWarrantyPeriod(e.getExtWarrantyPeriod()).setLeadTime(e.getExtLeadTime());
        }).collect(Collectors.toList());
        qlService.create("PrRecentPurchaseData", datas);
    }

    private List<String> getTransferOrderRules(List<PurchaseRequirementLineDTO> items){
        List<Long> categoryIds = items.stream().map(e -> e.getCategoryId()).distinct().collect(Collectors.toList());
        List<Long> orgIds = items.stream().map(e -> e.getOrgId()).distinct().collect(Collectors.toList());
        List<String> ruleList = qlService.queryByWrapper(QlWrappers.query(OrderConfigRepository.ORDER_CONFIG_CATEGORY)
                        .in(OrderConfigCategory::getOrgId, orgIds).in(OrderConfigCategory::getCategoryId, categoryIds), OrderConfigCategory.class)
                .stream().map(e -> getPurchaserKey(e.getOrgId(), e.getCategoryId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ruleList)) {
            log.info("无法匹配转单规则, orgIds: {} categoryIds: {}", orgIds, categoryIds);
        }
        return ruleList;
    }

    private Long getPurchaserOrgId(String username){
        HrUserOrgnizationDto userOrgnizationDto = pjProjectExtClient.getHrUserOrgnizationByUsername(username);
        Assert.notNull(userOrgnizationDto.getOuOrganization(), "查询采购员实体失败");
        return userOrgnizationDto.getOuOrganization().getOrganizationId();
    }

    private String getPriceAgreementKey(Long orgId, String areaCode, Long materialId) {
        return orgId + "-" + areaCode + "-" + materialId;
    }

    private String getFixPriceKey(Long reqLineId) {
        return reqLineId.toString();
    }

    private String getPurchaserKey(Long orgId, Long categoryId) {
        return orgId + "-" + categoryId;
    }

    private String getOrgMaterialKey(Long orgId, String materialCode) {
        return orgId + "@" + materialCode;
    }

    @Override
    public void exportRequirementLine(ExtPrSouRequirementLineExportRequestDto params, HttpServletResponse response) throws Exception {
        if(CollectionUtils.isEmpty(params.getRequirementHeadIdList())) {
            throw new BaseException("请求参数不能为空");
        }
        /**
         * 获取区域字典
         */
        HashMap<String,String>flag=new HashMap<>(15);
        List<DictItem> items = baseClient.listDictItemByDictCode("REGION");
        /*Optional<DictItem> dictItem = items.stream()
                .filter(item -> yearData.getAreaName().equals(item.getDictItemName()))
                .findFirst();*/
        for(DictItem dictItem:items){
            flag.put(dictItem.getDictItemCode(), dictItem.getDictItemName());
        }
        List<PurchaseRequirementHeadDTO> requirementHeadList = qlService.queryByWrapper(QlWrappers.query(MqlType.PURCHASE_REQUIREMENT_HEAD)
                .in(PurchaseRequirementHeadDTO::getRequirementHeadId, params.getRequirementHeadIdList()), PurchaseRequirementHeadDTO.class);
        Map<Long, PurchaseRequirementHeadDTO> requirementHeadMap = requirementHeadList.stream().collect(Collectors.toMap(k -> k.getRequirementHeadId(), Function.identity(), (k1, k2)->k2));
        if(CollectionUtils.isNotEmpty(requirementHeadList)) {
            List<ExtPrSouRequirementLineExportDto> exportDtoList = qlService.queryByWrapper(QlWrappers.query(MqlType.PURCHASE_REQUIREMENT_LINE)
                    .in(PurchaseRequirementHeadDTO::getRequirementHeadId, params.getRequirementHeadIdList()), ExtPrSouRequirementLineExportDto.class);
            exportDtoList.stream().forEach(data -> {
                data.setRequirementHeadNum(requirementHeadMap.getOrDefault(data.getRequirementHeadId(), new PurchaseRequirementHeadDTO()).getRequirementHeadNum());
                data.setExtProductFlag(Arrays.asList(YesOrNo.YES.getValue(), YesOrNo.YES.getName()).contains(data.getExtProductFlag())? YesOrNo.YES.getName(): YesOrNo.NO.getName());
            });
            for(ExtPrSouRequirementLineExportDto extPrSouRequirementLineExportDto:exportDtoList)
            {
                if(flag.containsKey(extPrSouRequirementLineExportDto.getExtAreaCode())) {
                    extPrSouRequirementLineExportDto.setExtAreaName(flag.get(extPrSouRequirementLineExportDto.getExtAreaCode()));
                }
            }
            EasyExcel.write(EasyExcelUtil.getServletOutputStream(response,"采购需求明细"), ExtPrSouRequirementLineExportDto.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet().doWrite(exportDtoList);
        }
    }
}
