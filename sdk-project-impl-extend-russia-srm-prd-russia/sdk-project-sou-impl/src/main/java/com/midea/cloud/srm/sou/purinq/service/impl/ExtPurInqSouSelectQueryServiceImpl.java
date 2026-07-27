package com.midea.cloud.srm.sou.purinq.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.component.filter.HttpServletHolder;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseUnit;
import com.midea.cloud.srm.model.extapi.sou.inq.enums.ExtPurInqSouTypeEnum;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.select.ExtPurInqSouSelectQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.*;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.init.ApiPurInqSouProjectVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.order.ApiPurInqSouOrderItemVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.select.ExtPurInqSouSelectOrderItemVendorVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.select.ExtPurInqSouSelectQueryDetailVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.select.ExtPurInqSouSelectQueryVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.select.ExtPurInqSouSelectionManagementVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.sou.purinq.dao.*;
import com.midea.cloud.srm.sou.purinq.service.ExtPurInqSouSelectQueryService;
import com.midea.cloud.srm.sou.sourcing.control.service.SouControlEventService;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouRoundDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderFileDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 100014337
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouSelectQueryServiceImpl implements ExtPurInqSouSelectQueryService {

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private ExtPurInqSouProjectDAO extPurInqSouProjectDAO;
    @Autowired
    private SouOrderDAO souOrderDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private SouOrderFileDAO souOrderFileDAO;
    @Autowired
    private SouControlEventService souControlEventService;
    @Autowired
    private SouRoundDAO souRoundDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private ExtPurInqSouItemDAO extPurInqSouItemDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private ExtPurInqSouOrderItemDAO extPurInqSouOrderItemDAO;
    @Autowired
    private ExtPurInqSouOrderDAO extPurInqSouOrderDAO;
    @Autowired
    private ExtPurInqSouItemRoundDAO extPurInqSouItemRoundDAO;
    @Autowired
    private BaseClient baseClient;

    /**
     * 查询询比价管理界面信息
     */
    @Override
    public ExtPurInqSouSelectionManagementVO getInqSelectManagementInfo(long projectId) {
        // 0: 刷新数据
        souControlEventService.refreshProjectBySouTime(projectId);
        // 1. 校验操作条件/权限
        SouProject souProject = souProjectDAO.getById(projectId);
        AssertUtils.notNull(souProject, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        // 2. 查询询价单
        ApiPurInqSouProjectVO projectVO; {
            ExtPurInqSouProject inqSouProject = extPurInqSouProjectDAO.getById(projectId);
            projectVO = SouObjectXUtil.convertTargetObj(souProject, ApiPurInqSouProjectVO.class);
            SouObjectXUtil.mergeProperties(inqSouProject, projectVO);
        }
        boolean canShowCurrentRoundVendorPrice = false; {
            SouRound currentRound = souRoundDAO.lambdaQuery().eq(SouRound::getProjectId, projectId).eq(SouRound::getRound, souProject.getCurrentRound()).one();
            if (!new Date().before(currentRound.getOrderEndTime())) {
                canShowCurrentRoundVendorPrice = true;
            }
        }
        // 3. 查询本轮供应商的报价单信息
        List<SouOrder> souOrderList = souOrderDAO.lambdaQuery()
                .eq(SouOrder::getProjectId, projectId)
                .eq(SouOrder::getRound, souProject.getCurrentRound())
                .in(SouOrder::getOrderStatus, SouOrderStatusEnum.SUBMISSION, SouOrderStatusEnum.WITHDRAW, SouOrderStatusEnum.CANCEL)
                .orderByDesc(SouOrder::getCreationDate)
                .list();
        Map<Long/* orderId */, ExtPurInqSouOrder> inqOrderMap = Collections.emptyMap(); {
            if (!souOrderList.isEmpty()) {
                inqOrderMap = extPurInqSouOrderDAO.listByIds(souOrderList.stream().map(SouOrder::getOrderId).collect(Collectors.toSet()))
                        .stream().collect(Collectors.toMap(ExtPurInqSouOrder::getOrderId, Function.identity()));
            }
        }
        // 本轮报价未结束，不能查看供应商的报价
        if (!canShowCurrentRoundVendorPrice) {
            souOrderList.forEach(o -> {
                o.setStandardNotaxTotalPrice(null);
                o.setStandardTaxTotalPrice(null);
            });
        }
        // 4. 查询本轮应报价供应商信息
        Set<Long> vendorIds = SouActiveBeanUtils.getActiveBean(ExtPurInqSouTypeEnum.ext_pur_inq.name(), ApiSouOrderQueryHandler.class).getAuthedVendors(projectId, null);
        List<SouVendor> souVendorList;
        if (vendorIds.isEmpty()) {
            souVendorList = Collections.emptyList();
        } else {
            souVendorList = souVendorDAO.lambdaQuery()
                    .eq(SouVendor::getProjectId, projectId)
                    .in(SouVendor::getVendorId, vendorIds)
                    .list();
        }
        // 5: 查询本轮次供应商的报价附件
        Map<Long/* orderId */, List<SouOrderFile>> orderFileMap = Collections.emptyMap(); {
            if (canShowCurrentRoundVendorPrice) {
                // 报价截止后，才能查看附件
                Set<Long> orderIds = souOrderList.stream().map(SouOrder::getOrderId).collect(Collectors.toSet());
                if (!orderIds.isEmpty()) {
                    orderFileMap = souOrderFileDAO.lambdaQuery()
                            .in(SouOrderFile::getOrderId, orderIds)
                            .list().stream().collect(Collectors.groupingBy(SouOrderFile::getOrderId));
                }
            }
        }
        return ExtPurInqSouSelectionManagementVO.convert(projectVO, souOrderList, inqOrderMap, souVendorList, souProject, orderFileMap);
    }

    /**
     * 评选列表信息查询
     */
    @Override
    public ExtPurInqSouSelectQueryVO queryItemSelectInfo(ExtPurInqSouSelectQueryDTO queryParam) {
        queryParam.formatParams();
        SouProject souProject = souProjectDAO.getById(queryParam.getProjectId());
        AssertUtils.notNull(souProject, "询价单[{0}]不存在", queryParam.getProjectId());
        AssertUtils.isTrue(ExtPurInqSouTypeEnum.ext_pur_inq.name().equals(souProject.getSouType()), "单据非询价场景");
        // 1: 查询数据
        // 1.1: 查询物料需求
        List<SouItem> souItemList; {
            souItemList = souItemDAO.list(SouItem::getProjectId, queryParam.getProjectId());
        }
        Map<Long/* souItemId */, ExtPurInqSouItem> inqSouItemMap = extPurInqSouItemDAO.list(ExtPurInqSouItem::getProjectId, queryParam.getProjectId())
                .stream().collect(Collectors.toMap(ExtPurInqSouItem::getSouItemId, Function.identity()));
        // 1.2: 查询报价明细(所有轮次供应商已提交的报价明细--如果当前轮次尚未截止报价，则不能查看当前轮次的供应商报价)
        List<SouOrderItem> orderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, queryParam.getProjectId())
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .list();
        Map<String/* souItemId_round */, List<SouOrderItem>> orderItemMap = orderItemList.stream().collect(Collectors.groupingBy(e -> e.getSouItemId() + "_" + e.getRound()));
        Map<Long/* orderItemId */, ExtPurInqSouOrderItem> inqOrderItemMap = Collections.emptyMap(); {
            if (!orderItemList.isEmpty()) {
                inqOrderItemMap = extPurInqSouOrderItemDAO
                        .listByIds(orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                        .stream().collect(Collectors.toMap(ExtPurInqSouOrderItem::getOrderItemId, Function.identity()));
            }
        }
        // 1.3: 查询供应商集合
        Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDAO.list(SouVendor::getProjectId, queryParam.getProjectId())
                .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 1.4: 查询物料轮次信息
        Map<String/* souItemId_round */, ExtPurInqSouItemRound> itemRoundMap = extPurInqSouItemRoundDAO.list(ExtPurInqSouItemRound::getProjectId, souProject.getProjectId())
                .stream().collect(Collectors.toMap(e -> e.getSouItemId() + "_" + e.getRound(), Function.identity()));
        // 1.5: 查看所有供应商首轮已提交的报价明细(用于界面展示 - 第一次未税价格)
        Map<String/* souItemId_vendorId */, SouOrderItem> firstVendorOrderItemMap = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, souProject.getProjectId())
                .eq(SouOrderItem::getRound, 1)
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .list().stream().collect(Collectors.toMap(e -> e.getSouItemId() + "_" + e.getVendorId(), Function.identity()));
        // 2: 组装数据
        ExtPurInqSouSelectQueryVO result = ExtPurInqSouSelectQueryVO.convert(souProject, souItemList, inqSouItemMap, orderItemMap, inqOrderItemMap, vendorMap, itemRoundMap, firstVendorOrderItemMap);

        // 3.供应商总价
//        Map<Long/* vendorId */, BigDecimal> vendorTotalPriceMap = new HashMap<>(vendorMap.size());
//        for (ExtPurInqSouSelectQueryDetailVO item : result.getItemList()) {
//            List<ApiPurInqSouOrderItemVO> itemList = item.getOrderItemList();
//            if (CollectionUtils.isNotEmpty(itemList)) {
//                long count = itemList.stream().filter(e -> e.getRanking() == null).count();
//                if (count > 0) {
//                    vendorTotalPriceMap.clear();
//                    break;
//                }
//                itemList.stream().forEach(vendorItem -> {
//                    vendorTotalPriceMap.compute(vendorItem.getVendorId(), (k, v) -> BigDecimalUtil.add(v, vendorItem.getPriceTaxTotal()));
//                });
//            } else {
//                vendorTotalPriceMap.clear();
//                break;
//            }
//        }
//        result.getVendorList().forEach(e -> {
//            ExtensionMap extensions = e.getExtensions();
//            if (extensions == null) {
//                extensions = new ExtensionMap();
//            }
//            extensions.put("extTotalAmount", vendorTotalPriceMap.get(e.getVendorId()));
//        });

        return result;
    }

    /**
     * 查询评选物料轮次的供应商报价明细
     */
    @Override
    public List<ExtPurInqSouSelectOrderItemVendorVO> queryOrderItemVendors(long souItemId, int round) {
        List<SouOrderItem> orderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getSouItemId, souItemId)
                .eq(SouOrderItem::getRound, round)
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .list();
        if (orderItemList.isEmpty()) { return Collections.emptyList(); }

        List<ExtPurInqSouSelectOrderItemVendorVO> resultList = SouObjectXUtil.convertList(orderItemList, ExtPurInqSouSelectOrderItemVendorVO.class);
        // 1: 查询额外报价明细
        Map<Long/* orderItemId */, ExtPurInqSouOrderItem> inqOrderItemMap = extPurInqSouOrderItemDAO.listByIds(orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ExtPurInqSouOrderItem::getOrderItemId, Function.identity()));
        resultList.forEach(result -> SouObjectXUtil.mergeProperties(inqOrderItemMap.get(result.getOrderItemId()), result));
        // 2: 查询物料明细
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDAO.listByIds(orderItemList.stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        resultList.forEach(result -> SouObjectXUtil.mergeProperties(souItemMap.get(result.getSouItemId()), result));
        Map<Long/* souItemId */, ExtPurInqSouItem> inqSouItemMap = extPurInqSouItemDAO.listByIds(souItemMap.keySet())
                .stream().collect(Collectors.toMap(ExtPurInqSouItem::getSouItemId, Function.identity()));
        resultList.forEach(result -> SouObjectXUtil.mergeProperties(inqSouItemMap.get(result.getSouItemId()), result));
        // 3: 查询供应商
        Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDAO.list(SouVendor::getProjectId, orderItemList.get(0).getProjectId())
                .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        resultList.forEach(result -> SouObjectXUtil.mergeProperties(vendorMap.get(result.getVendorId()), result));
        // 4: 排序
        Map<Long/* orderId */, SouOrder> orderMap = souOrderDAO.listByIds(orderItemList.stream().map(SouOrderItem::getOrderId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(SouOrder::getOrderId, Function.identity()));
        resultList.sort((a, b) -> {
            // 4.1: 根据价格排序
            int v = a.getStandardNotaxPrice().compareTo(b.getStandardNotaxPrice());
            if (v != 0) { return v; }
            // 4.2: 根据到货周期(越小越好)
            v = new BigDecimal(a.getExtLeadTime()).compareTo(new BigDecimal(b.getExtLeadTime()));
            if (v != 0) { return v; }
            // 4.3: 根据质保期(越大越好)
            v = new BigDecimal(b.getExtWarrantyPeriod()).compareTo(new BigDecimal(a.getExtWarrantyPeriod()));
            if (v != 0) { return v; }
            // 4.4: 根据报价时间
            SouOrder aOrder = orderMap.get(a.getOrderId());
            SouOrder bOrder = orderMap.get(b.getOrderId());
            return aOrder.getSubmitTime().compareTo(bOrder.getSubmitTime());
        });

        return resultList;
    }

    /**
     * 评选列表信息导出
     */
    @Override
    public void downLoadExcelForItemSelectInfo(ExtPurInqSouSelectQueryDTO queryParam, HttpServletResponse response) throws IOException {
        // 1: 查询数据
        ExtPurInqSouSelectQueryVO queryVO = this.queryItemSelectInfo(queryParam);
        // 2: 设置导出excel头信息
        List<List<String>> headList = new ArrayList<>(30); {
            // 2.1: 序号
            headList.add(new ArrayList<>(Collections.singletonList("序号")));
            // 2.2: 轮次
            headList.add(new ArrayList<>(Collections.singletonList("轮次")));
            // 2.3: 报价次数
            headList.add(new ArrayList<>(Collections.singletonList("报价次数")));
            // 2.4: 是否无料号寻源
            headList.add(new ArrayList<>(Collections.singletonList("是否无料号寻源")));
            // 2.5: 物料编码
            headList.add(new ArrayList<>(Collections.singletonList("物料编码")));
            // 2.6: 物料名称
            headList.add(new ArrayList<>(Collections.singletonList("物料名称")));
            // 2.7: 采购分类
            headList.add(new ArrayList<>(Collections.singletonList("采购分类")));
            // 2.8: 规格型号
            headList.add(new ArrayList<>(Collections.singletonList("规格型号")));
            // 2.9: 数量
            headList.add(new ArrayList<>(Collections.singletonList("数量")));
            // 2.10: 基本计量单位
            headList.add(new ArrayList<>(Collections.singletonList("计量单位")));
            // 2.11: 品牌
            headList.add(new ArrayList<>(Collections.singletonList("品牌")));
            // 2.12: 供应商编码
            headList.add(new ArrayList<>(Collections.singletonList("供应商编码")));
            // 2.13: 中标供应商
            headList.add(new ArrayList<>(Collections.singletonList("中标供应商")));
            // 2.14: 税率(%)
            headList.add(new ArrayList<>(Collections.singletonList("税率(%)")));
            // 2.15: 发票类型
            headList.add(new ArrayList<>(Collections.singletonList("发票类型")));
            // 2.16: 未税单价
            headList.add(new ArrayList<>(Collections.singletonList("未税单价")));
            // 2.17: 未税总价
            headList.add(new ArrayList<>(Collections.singletonList("未税总价")));
            // 2.18: 到货周期(自然日)
            headList.add(new ArrayList<>(Collections.singletonList("到货周期(自然日)")));
            // 2.19: 质保期(自然日)
            headList.add(new ArrayList<>(Collections.singletonList("质保期(自然日)")));
            // 2.20: 是否生成定价单
            headList.add(new ArrayList<>(Collections.singletonList("是否生成定价单")));
            // 2.21: 供应商-动态相关
            queryVO.getVendorList().forEach(vendor -> {
                List<String> tempL = Arrays.asList(vendor.getVendorName(), "TODO");
                // 2.21.1: 税率(%)
                List<String> v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
                v.set(1, "税率(%)");
                headList.add(v);
                // 2.21.2: 发票类型
                v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
                v.set(1, "发票类型");
                headList.add(v);
                // 2.21.3: 未税单价
                v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
                v.set(1, "未税单价");
                headList.add(v);
                // 2.21.4: 未税总价
                v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
                v.set(1, "未税总价");
                headList.add(v);
                // 2.21.5: 到货周期(自然日)
                v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
                v.set(1, "到货周期(自然日)");
                headList.add(v);
                // 2.21.6: 质保期(自然日)
                v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
                v.set(1, "质保期(自然日)");
                headList.add(v);
            });
        }
        // 3: 设置行数据
        List<List<Object>> dataList = new ArrayList<>(queryVO.getItemList().size()); {
            // 查询数据
            Map<String/* code */, String/* name */> unitMap = baseClient.listAllEnablePurchaseUnit().stream().collect(Collectors.toMap(PurchaseUnit::getUnitCode, PurchaseUnit::getUnitName));
            Map<String/* dictCode */, Map<String/* dictItemCode */, String/* dictItemName */>> dictMap = baseClient.listByDictCode(Arrays.asList("YES_OR_NO", "REGION", "EXT_SOU_INQ_ORDER_INVOICE_TYPE"))
                    .stream().collect(Collectors.groupingBy(DictItemDTO::getDictCode, Collectors.toMap(DictItemDTO::getDictItemCode, DictItemDTO::getDictItemName)));

            int index = 0;
            for (ExtPurInqSouSelectQueryDetailVO itemInfo : queryVO.getItemList()) {
                List<Object> row = new ArrayList<>(30);
                dataList.add(row);
                index++;
                // 3.1: 序号
                row.add(index);
                // 3.2: 轮次
                row.add(itemInfo.getRound());
                // 3.3: 报价次数
                row.add(itemInfo.getOrderCount());
                // 3.4: 是否无料号寻源
                row.add(Optional.of(dictMap.get("YES_OR_NO")).orElse(Collections.emptyMap()).get(itemInfo.getNoCodeItem().name()));
                // 3.5: 物料编码
                row.add(itemInfo.getItemCode());
                // 3.6: 物料名称
                row.add(itemInfo.getItemDesc());
                // 3.7: 采购分类
                row.add(itemInfo.getCategoryName());
                // 3.8: 规格型号
                row.add(itemInfo.getExtMaterialMode());
                // 3.9: 数量
                row.add(itemInfo.getRequireQuantity() != null ? itemInfo.getRequireQuantity().stripTrailingZeros().toPlainString() : null);
                // 3.10: 计量单位
                String v = unitMap.get(itemInfo.getUnit());
                row.add(v != null ? v : itemInfo.getUnit());
                // 3.11: 品牌
                row.add(itemInfo.getExtBrand());
                // 3.12: 供应商编码
                row.add(itemInfo.getWinVendorCode());
                // 3.13: 中标供应商
                row.add(itemInfo.getWinVendorName());
                // 3.14: 税率(%)
                row.add(itemInfo.getWinTaxRate() != null ? itemInfo.getWinTaxRate().stripTrailingZeros().toPlainString() : null);
                // 3.15: 发票类型
                row.add(Optional.of(dictMap.get("EXT_SOU_INQ_ORDER_INVOICE_TYPE")).orElse(Collections.emptyMap()).get(itemInfo.getWinInvoiceType()));
                // 3.16: 未税单价
                row.add(itemInfo.getWinStandardNotaxPrice() != null ? itemInfo.getWinStandardNotaxPrice().stripTrailingZeros().toPlainString() : null);
                // 3.17: 未税总价
                row.add(itemInfo.getWinStandardTotalPrice() != null ? itemInfo.getWinStandardTotalPrice().stripTrailingZeros().toPlainString() : null);
                // 3.18: 到货周期(自然日)
                row.add(itemInfo.getWinExtLeadTime());
                // 3.19: 质保期(自然日)
                row.add(itemInfo.getWinExtWarrantyPeriod());
                // 3.20: 是否生成定价单
                v = Optional.of(dictMap.get("YES_OR_NO")).orElse(Collections.emptyMap()).get(itemInfo.getHasFixPrice() != null ? itemInfo.getHasFixPrice().name() : null);
                row.add(v != null ? v : "否");
                // 3.21: 供应商-动态相关
                Map<Long/* vendorId */, ApiPurInqSouOrderItemVO> orderItemMap = itemInfo.getOrderItemList().stream()
                                .collect(Collectors.toMap(ApiPurInqSouOrderItemVO::getVendorId, Function.identity()));
                queryVO.getVendorList().forEach(vendor -> {
                    ApiPurInqSouOrderItemVO orderItem = orderItemMap.get(vendor.getVendorId());
                    // 3.21.1: 税率(%)
                    row.add(orderItem != null && orderItem.getTaxRate() != null ? orderItem.getTaxRate().stripTrailingZeros().toPlainString() : null);
                    // 3.21.2: 发票类型
                    row.add(orderItem != null ? Optional.of(dictMap.get("EXT_SOU_INQ_ORDER_INVOICE_TYPE")).orElse(Collections.emptyMap()).get(orderItem.getInvoiceType()) : null);
                    // 3.21.3: 未税单价
                    row.add(orderItem != null && orderItem.getStandardNotaxPrice() != null ? orderItem.getStandardNotaxPrice().stripTrailingZeros().toPlainString() : null);
                    // 3.21.4: 未税总价
                    row.add(orderItem != null && orderItem.getStandardNotaxPrice() != null && orderItem.getRequireQuantity() != null ?
                            orderItem.getStandardNotaxPrice().multiply(orderItem.getRequireQuantity()).setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() : null);
                    // 3.21.5: 到货周期(自然日)
                    row.add(orderItem != null ? orderItem.getExtLeadTime() : null);
                    // 3.21.6: 质保期(自然日)
                    row.add(orderItem != null ? orderItem.getExtWarrantyPeriod() : null);
                });
            }
        }
        // 4: 导出
        try (OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "集采询比价评选列表信息.xlsx")) {
            EasyExcel.write(outputStream)
                    .sheet(0)
                    .head(headList)
                    .doWrite(dataList);
        }
    }

}
