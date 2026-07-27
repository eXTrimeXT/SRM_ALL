package com.midea.cloud.srm.sou.inq.ext.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseUnit;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouSelectQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouItemRound;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.ExtPjInqSouOrder;
import com.midea.cloud.srm.model.extapi.sou.inq.entity.PjInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.inq.filter.ExtInqSouSelectFilterUtils;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouSelectOrderItemVendorVO;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouSelectQueryDetailVO;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouSelectQueryVO;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouSelectionManagementVO;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.init.ApiInqSouProjectVO;
import com.midea.cloud.srm.model.sou.openapi.inq.vo.order.ApiInqSouOrderItemVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtInqSouItemMapper;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouItemRoundDAO;
import com.midea.cloud.srm.sou.inq.ext.dao.ExtPjInqSouOrderDAO;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouSelectQueryService;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouItemDAO;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouProjectDAO;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
import com.midea.cloud.srm.sou.inq.select.mapper.SiSouVendorMapper;
import com.midea.cloud.srm.sou.sourcing.control.service.SouControlEventService;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouRoundDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouRoundService;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderFileDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
import com.mideacloud.common.objectx.ExtensionMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouSelectQueryServiceImpl implements ExtInqSouSelectQueryService {

    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private InqSouProjectDAO inqSouProjectDAO;
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
    @Resource
    private ExtInqSouItemMapper extInqSouItemMapper;
    @Autowired
    private InqSouItemDAO inqSouItemDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private InqSouOrderItemDAO inqSouOrderItemDAO;
    @Autowired
    private ExtPjInqSouOrderDAO extPjInqSouOrderDAO;
    @Autowired
    private ExtPjInqSouItemRoundDAO extPjInqSouItemRoundDAO;
    @Autowired
    private BaseClient baseClient;
    @Resource
    private SiSouVendorMapper siSouVendorMapper;
    @Autowired
    private IExtSouRoundService souRoundService;
    @Autowired
    private QlOpenClient qlOpenClient;

    /**
     * 查询询比价管理界面信息
     */
    @Override
    public ExtInqSouSelectionManagementVO getInqSelectManagementInfo(long projectId) {
        // 0: 刷新数据
        souControlEventService.refreshProjectBySouTime(projectId);
        // 1. 校验操作条件/权限
        SouProject souProject = souProjectDAO.getById(projectId);
        AssertUtils.notNull(souProject, LocaleHandler.getLocaleMsg("寻源单")+"[{0}]"+LocaleHandler.getLocaleMsg("不存在"), projectId);
        // 2. 查询询价单
        ApiInqSouProjectVO projectVO; {
            InqSouProject inqSouProject = inqSouProjectDAO.getById(projectId);
            projectVO = SouObjectXUtil.convertTargetObj(souProject, ApiInqSouProjectVO.class);
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
        Map<Long/* orderId */, ExtPjInqSouOrder> inqOrderMap = Collections.emptyMap(); {
            if (!souOrderList.isEmpty()) {
                inqOrderMap = extPjInqSouOrderDAO.listByIds(souOrderList.stream().map(SouOrder::getOrderId).collect(Collectors.toSet()))
                        .stream().collect(Collectors.toMap(ExtPjInqSouOrder::getOrderId, Function.identity()));
            }
        }
        if (!canShowCurrentRoundVendorPrice) {
            souOrderList.forEach(o -> {
                // 本轮报价未结束，不能查看供应商的报价
                o.setStandardNotaxTotalPrice(null);
                o.setStandardTaxTotalPrice(null);
            });
        }
        // 4. 查询本轮应报价供应商信息
        Set<Long> vendorIds = SouActiveBeanUtils.getActiveBean(SouTypeEnum.inq.name(), ApiSouOrderQueryHandler.class).getAuthedVendors(projectId, null);
        List<com.midea.cloud.srm.model.extapi.sou.inq.entity.SouVendor> souVendorList;
        if (vendorIds.isEmpty()) {
            souVendorList = Collections.emptyList();
        } else {
            /*souVendorList = souVendorDAO.lambdaQuery()
                    .eq(SouVendor::getProjectId, projectId)
                    .in(SouVendor::getVendorId, vendorIds)
                    .list();*/
            LambdaQueryWrapper<com.midea.cloud.srm.model.extapi.sou.inq.entity.SouVendor> qw = new LambdaQueryWrapper<>();
            qw.eq(com.midea.cloud.srm.model.extapi.sou.inq.entity.SouVendor::getProjectId, projectId);
            qw.in(com.midea.cloud.srm.model.extapi.sou.inq.entity.SouVendor::getVendorId, vendorIds);
            souVendorList = siSouVendorMapper.selectList(qw);
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
        return ExtInqSouSelectionManagementVO.convert(projectVO, souOrderList, inqOrderMap, souVendorList, souProject, orderFileMap);
    }

    /**
     * 采购需求行ID解析
     * @param requiremnetLineIdSet
     * @param requirementLineIds
     */
    private void appendReuirementLineId(Set<Long> requiremnetLineIdSet, String requirementLineIds) {
        if(StringUtils.isBlank(requirementLineIds)) {
            return;
        }
        String[] requirementLineIdsArrary = requirementLineIds.split(SrmConstant.SIG_3);
        Arrays.stream(requirementLineIdsArrary).forEach(id -> requiremnetLineIdSet.add(new Long(id)));
    }

    /**
     * 评选列表信息查询
     */
    @Override
    public ExtInqSouSelectQueryVO queryItemSelectInfo(ExtInqSouSelectQueryDTO queryParam) {
        queryParam.formatParams();
        SouProject souProject = souProjectDAO.getById(queryParam.getProjectId());
        AssertUtils.notNull(souProject, "询价单[{0}]不存在", queryParam.getProjectId());
        AssertUtils.isTrue(SouTypeEnum.inq.name().equals(souProject.getSouType()), "单据非询价场景");

        // 1.1: 查询物料需求
        List<SouItem> souItemList = souItemDAO.list(SouItem::getProjectId, queryParam.getProjectId());
        Map<Long, PjInqSouItem> extInqSouItemMap = new HashMap<>(15);
        Set<Long> requirementLineIdSet = new HashSet<>(15);

        if(CollectionUtils.isNotEmpty(souItemList)) {

            //查询寻源需求明细
            List<PjInqSouItem> extInqSouItemList = extInqSouItemMapper.selectList(new LambdaQueryWrapper<PjInqSouItem>()
            .in(PjInqSouItem::getSouItemId, souItemList.stream().map(SouItem::getSouItemId).collect(Collectors.toList())));

            //已关闭的单据
            Set<Long> hasCloseSet = new HashSet<>();

            extInqSouItemMap = extInqSouItemList.stream().peek(e -> {
                //解析需求明细行ID，多个用逗号分割
                appendReuirementLineId(requirementLineIdSet, e.getExtSourceFromLineIds());
                if(Enable.Y.equals(e.getHasClose())) {
                    hasCloseSet.add(e.getSouItemId());
                }
            }).collect(Collectors.toMap(k -> k.getSouItemId(), Function.identity(), (k1, k2) -> k2));

            //过滤未关闭的单据
            souItemList = souItemList.stream().filter(e -> !hasCloseSet.contains(e.getSouItemId())).collect(Collectors.toList());
        }

        //采购需求行ID
        List<Long> requirementLineIdList =  new ArrayList<>(requirementLineIdSet);

        List<RecordDTO> requirementLineList = null;
        if(CollectionUtils.isNotEmpty(requirementLineIdList)) {
            requirementLineList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_LINE).in(RequirementLine::getRequirementLineId, requirementLineIdList));
        }

        if(CollectionUtils.isNotEmpty(requirementLineList)) {
            //根据历史最低价进行排序
            String LOW_VENDOR_PRICE = "extHistoryVendorPrice1";
             requirementLineList = requirementLineList.stream().filter(r -> ObjectUtils.allNotNull(r.get(LOW_VENDOR_PRICE))).sorted(new Comparator<RecordDTO>() {
                @Override
                public int compare(RecordDTO o1, RecordDTO o2) {
                    return o1.getBigDecimal(LOW_VENDOR_PRICE).compareTo(o2.getBigDecimal(LOW_VENDOR_PRICE));
                }
            }).collect(Collectors.toList());

        }


        Map<Long/* souItemId */, InqSouItem> inqSouItemMap = inqSouItemDAO.list(InqSouItem::getProjectId, queryParam.getProjectId())
                .stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));
        // 1.2: 查询报价明细(所有轮次供应商已提交的报价明细--如果当前轮次尚未截止报价，则不能查看当前轮次的供应商报价)
        List<SouOrderItem> orderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getProjectId, queryParam.getProjectId())
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .list();
        Map<String/* souItemId_round */, List<SouOrderItem>> orderItemMap = orderItemList.stream().collect(Collectors.groupingBy(e -> e.getSouItemId() + "_" + e.getRound()));
        Map<Long/* orderItemId */, InqSouOrderItem> inqOrderItemMap = Collections.emptyMap(); {
            if (!orderItemList.isEmpty()) {
                inqOrderItemMap = inqSouOrderItemDAO
                        .listByIds(orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                        .stream().collect(Collectors.toMap(InqSouOrderItem::getOrderItemId, Function.identity()));
            }
        }
        // 1.3: 查询供应商集合
        Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDAO.list(SouVendor::getProjectId, queryParam.getProjectId())
                .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        // 1.4: 查询物料轮次信息
        Map<String/* souItemId_round */, ExtPjInqSouItemRound> itemRoundMap = extPjInqSouItemRoundDAO.list(ExtPjInqSouItemRound::getProjectId, souProject.getProjectId())
                .stream().collect(Collectors.toMap(e -> e.getSouItemId() + "_" + e.getRound(), Function.identity()));
        // 2: 组装数据
        ExtInqSouSelectQueryVO result = ExtInqSouSelectQueryVO.convert(souProject, souItemList, inqSouItemMap, orderItemMap, inqOrderItemMap, vendorMap, itemRoundMap);

        //历史最低价供应商
        if(CollectionUtils.isNotEmpty(result.getItemList()) && CollectionUtils.isNotEmpty(requirementLineList)) {
            Map<Long, PjInqSouItem> finalExtInqSouItemMap = extInqSouItemMap;
            List<RecordDTO> finalRequirementLineList = requirementLineList;
            result.getItemList().stream().forEach(item -> {
                PjInqSouItem extInqSouItem = finalExtInqSouItemMap.getOrDefault(item.getSouItemId(), new PjInqSouItem());
                Set<Long> lineIdSet = new HashSet<>();
                appendReuirementLineId(lineIdSet, extInqSouItem.getExtSourceFromLineIds());
                if(CollectionUtils.isNotEmpty(lineIdSet)) {
                    //匹配第一个历史最低价
                   RecordDTO requirementLineRecord = finalRequirementLineList.stream().filter(r -> lineIdSet.contains(r.get(RequirementLine::getRequirementLineId))).findFirst().orElse(new RecordDTO());
                   item.setExtHistoryVendorCode1(requirementLineRecord.getString("extHistoryVendorCode1"));
                   item.setExtHistoryVendorName1(requirementLineRecord.getString("extHistoryVendorName1"));
                   item.setExtHistoryVendorPrice1(requirementLineRecord.getBigDecimal("extHistoryVendorPrice1"));
                }
            });
        }

        // 3.供应商总价
        List<ExtSouRound> list = souRoundService.lambdaQuery()
                .eq(ExtSouRound::getProjectId, souProject.getProjectId())
                .eq(ExtSouRound::getRound, souProject.getCurrentRound()).list();
        if (CollectionUtils.isNotEmpty(list) && YesOrNo.YES.getValue().equals(list.get(0).getExtTotalCompare())) {
            Map<Long/* vendorId */, BigDecimal> vendorTotalPriceMap = new HashMap<>(vendorMap.size());
            for (ExtInqSouSelectQueryDetailVO item : result.getItemList()) {
                List<ApiInqSouOrderItemVO> itemList = item.getOrderItemList();
                if (CollectionUtils.isNotEmpty(itemList)) {
                    itemList.stream().forEach(vendorItem -> {
                        vendorTotalPriceMap.compute(vendorItem.getVendorId(), (k, v) -> BigDecimalUtil.add(v, vendorItem.getPriceTaxTotal()));
                    });
                }
            }
            result.getVendorList().forEach(e -> {
                ExtensionMap extensions = e.getExtensions();
                if (extensions == null) {
                    extensions = new ExtensionMap();
                }
                extensions.put("extTotalAmount", vendorTotalPriceMap.get(e.getVendorId()));
            });
        }

        return result;
    }

    /**
     * 查询评选物料轮次的供应商报价明细
     */
    @Override
    public List<ExtInqSouSelectOrderItemVendorVO> queryOrderItemVendors(long souItemId, int round) {
        List<SouOrderItem> orderItemList = souOrderItemDAO.lambdaQuery()
                .eq(SouOrderItem::getSouItemId, souItemId)
                .eq(SouOrderItem::getRound, round)
                .eq(SouOrderItem::getOrderStatus, SouOrderStatusEnum.SUBMISSION)
                .list();
        /** 过滤报价为0的数据 */
        orderItemList = orderItemList.stream().filter(ExtInqSouSelectFilterUtils.selectOrderPriceWithoutNullOrZero()).collect(Collectors.toList());
        if (orderItemList.isEmpty()) { return Collections.emptyList(); }

        List<ExtInqSouSelectOrderItemVendorVO> resultList = SouObjectXUtil.convertList(orderItemList, ExtInqSouSelectOrderItemVendorVO.class);
        // 1: 查询额外报价明细
        Map<Long/* orderItemId */, InqSouOrderItem> inqOrderItemMap = inqSouOrderItemDAO.listByIds(orderItemList.stream().map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(InqSouOrderItem::getOrderItemId, Function.identity()));
        resultList.forEach(result -> SouObjectXUtil.mergeProperties(inqOrderItemMap.get(result.getOrderItemId()), result));
        // 2: 查询物料明细
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDAO.listByIds(orderItemList.stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        resultList.forEach(result -> SouObjectXUtil.mergeProperties(souItemMap.get(result.getSouItemId()), result));
        Map<Long/* souItemId */, InqSouItem> inqSouItemMap = inqSouItemDAO.listByIds(souItemMap.keySet())
                .stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));
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
    public void downLoadExcelForItemSelectInfo(ExtInqSouSelectQueryDTO queryParam, HttpServletResponse response) throws IOException {
        // 1: 查询数据
        ExtInqSouSelectQueryVO queryVO = this.queryItemSelectInfo(queryParam);
        // 2: 设置导出excel头信息
        List<List<String>> headList = new ArrayList<>(30); {
            // 2.1: 序号
            headList.add(new ArrayList<>(Collections.singletonList("序号")));
            // 2.2: 轮次
            headList.add(new ArrayList<>(Collections.singletonList("轮次")));
            // 2.3: 业务实体
            headList.add(new ArrayList<>(Collections.singletonList("业务实体")));
            // 2.4: 区域
            headList.add(new ArrayList<>(Collections.singletonList("区域")));
            // 2.5: 报价次数
            headList.add(new ArrayList<>(Collections.singletonList("报价次数")));
            // 2.6: 是否无料号寻源
            headList.add(new ArrayList<>(Collections.singletonList("是否无料号寻源")));
            // 2.7: 物料编码
            headList.add(new ArrayList<>(Collections.singletonList("物料编码")));
            // 2.8: 物料名称
            headList.add(new ArrayList<>(Collections.singletonList("物料名称")));
            // 2.9: 采购分类
            headList.add(new ArrayList<>(Collections.singletonList("采购分类")));
            // 2.10: 规格型号
            headList.add(new ArrayList<>(Collections.singletonList("规格型号")));
            // 2.11: 数量
            headList.add(new ArrayList<>(Collections.singletonList("数量")));
            // 2.12: 基本计量单位
            headList.add(new ArrayList<>(Collections.singletonList("基本计量单位")));
            // 2.13: 品牌
            headList.add(new ArrayList<>(Collections.singletonList("品牌")));
            // 2.14: 供应商编码
            headList.add(new ArrayList<>(Collections.singletonList("供应商编码")));
            // 2.15: 中标供应商
            headList.add(new ArrayList<>(Collections.singletonList("中标供应商")));
            // 2.16: 税率(%)
            headList.add(new ArrayList<>(Collections.singletonList("税率(%)")));
            // 2.17: 发票类型
            headList.add(new ArrayList<>(Collections.singletonList("发票类型")));
            // 2.18: 未税单价
            headList.add(new ArrayList<>(Collections.singletonList("未税单价")));
            // 2.19: 未税总价
            headList.add(new ArrayList<>(Collections.singletonList("未税总价")));
            // 2.20: 到货周期(自然日)
            headList.add(new ArrayList<>(Collections.singletonList("到货周期(自然日)")));
            // 2.21: 质保期(自然日)
            headList.add(new ArrayList<>(Collections.singletonList("质保期(自然日)")));
            // 2.22: 是否生成定价单
            headList.add(new ArrayList<>(Collections.singletonList("是否生成定价单")));
            headList.add(new ArrayList<>(Collections.singletonList("供应商备注")));
            // 2.23: 供应商-动态相关
            queryVO.getVendorList().forEach(vendor -> {
                List<String> tempL = Arrays.asList(vendor.getVendorName(), "TODO");
                // 2.23.1: 税率(%)
                List<String> v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
                v.set(1, "税率(%)");
                headList.add(v);
                // 2.23.2: 发票类型
                v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
                v.set(1, "发票类型");
                headList.add(v);
                // 2.23.3: 未税单价
                v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
                v.set(1, "未税单价");
                headList.add(v);
                // 2.23.4: 未税总价
                v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
                v.set(1, "未税总价");
                headList.add(v);
                // 2.23.5: 到货周期(自然日)
                v = JSON.parseArray(JSON.toJSONString(tempL), String.class);
                v.set(1, "到货周期(自然日)");
                headList.add(v);
                // 2.23.6: 质保期(自然日)
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
            for (ExtInqSouSelectQueryDetailVO itemInfo : queryVO.getItemList()) {
                List<Object> row = new ArrayList<>(30);
                dataList.add(row);
                index++;
                // 3.1: 序号
                row.add(index);
                // 3.2: 轮次
                row.add(itemInfo.getRound());
                // 3.3: 业务实体
                row.add(itemInfo.getOrgOuName());
                // 3.4: 区域
                String v = Optional.of(dictMap.get("REGION")).orElse(Collections.emptyMap()).get(itemInfo.getExtAreaCode());
                row.add(v != null ? v : itemInfo.getExtAreaName());
                // 3.5: 报价次数
                row.add(itemInfo.getOrderCount());
                // 3.6: 是否无料号寻源
                row.add(Optional.of(dictMap.get("YES_OR_NO")).orElse(Collections.emptyMap()).get(itemInfo.getNoCodeItem().name()));
                // 3.7: 物料编码
                row.add(itemInfo.getItemCode());
                // 3.8: 物料名称
                row.add(itemInfo.getItemDesc());
                // 3.9: 采购分类
                row.add(itemInfo.getCategoryName());
                // 3.10: 规格型号
                row.add(itemInfo.getExtMaterialModel());
                // 3.11: 数量
                row.add(itemInfo.getRequireQuantity() != null ? itemInfo.getRequireQuantity().stripTrailingZeros().toPlainString() : null);
                // 3.12: 基本计量单位
                v = unitMap.get(itemInfo.getUnit());
                row.add(v != null ? v : itemInfo.getUnit());
                // 3.13: 品牌
                row.add(itemInfo.getExtBrand());
                // 3.14: 供应商编码
                row.add(itemInfo.getWinVendorCode());
                // 3.15: 中标供应商
                row.add(itemInfo.getWinVendorName());
                // 3.16: 税率(%)
                row.add(itemInfo.getWinTaxRate() != null ? itemInfo.getWinTaxRate().stripTrailingZeros().toPlainString() : null);
                // 3.17: 发票类型
                row.add(Optional.of(dictMap.get("EXT_SOU_INQ_ORDER_INVOICE_TYPE")).orElse(Collections.emptyMap()).get(itemInfo.getWinInvoiceType()));
                // 3.18: 未税单价
                row.add(itemInfo.getWinStandardNotaxPrice() != null ? itemInfo.getWinStandardNotaxPrice().stripTrailingZeros().toPlainString() : null);
                // 3.19: 未税总价
                row.add(itemInfo.getWinStandardTotalPrice() != null ? itemInfo.getWinStandardTotalPrice().stripTrailingZeros().toPlainString() : null);
                // 3.20: 到货周期(自然日)
                row.add(itemInfo.getWinExtLeadTime());
                // 3.21: 质保期(自然日)
                row.add(itemInfo.getWinExtWarrantyPeriod());
                // 3.22: 是否生成定价单
                v = Optional.of(dictMap.get("YES_OR_NO")).orElse(Collections.emptyMap()).get(itemInfo.getHasFixPrice() != null ? itemInfo.getHasFixPrice().name() : null);
                row.add(v != null ? v : "否");
                // 供应商备注
                Long vendorId = itemInfo.getWinVendorId();
                List<ApiInqSouOrderItemVO> orderItemList = itemInfo.getOrderItemList();
                String orderRemark = null;
                if(CollectionUtils.isNotEmpty(orderItemList)){
                    List<ApiInqSouOrderItemVO> collect = orderItemList.stream().filter(f -> ObjectUtils.defaultIfNull(f.getVendorId(), SrmConstant.LONG_ZERO).compareTo(ObjectUtils.defaultIfNull(vendorId, SrmConstant.LONG_ZERO)) == 0 ).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(collect)){
                        orderRemark = collect.get(0).getOrderRemark();
                    }
                }
                row.add(orderRemark != null ? orderRemark : "");
                // 3.23: 供应商-动态相关
                Map<Long/* vendorId */, ApiInqSouOrderItemVO> orderItemMap = itemInfo.getOrderItemList().stream()
                                .collect(Collectors.toMap(ApiInqSouOrderItemVO::getVendorId, Function.identity()));
                queryVO.getVendorList().forEach(vendor -> {
                    ApiInqSouOrderItemVO orderItem = orderItemMap.get(vendor.getVendorId());
                    // 3.23.1: 税率(%)
                    row.add(orderItem != null && orderItem.getTaxRate() != null ? orderItem.getTaxRate().stripTrailingZeros().toPlainString() : null);
                    // 3.23.2: 发票类型
                    row.add(orderItem != null ? Optional.of(dictMap.get("EXT_SOU_INQ_ORDER_INVOICE_TYPE")).orElse(Collections.emptyMap()).get(orderItem.getInvoiceType()) : null);
                    // 3.23.3: 未税单价
                    row.add(orderItem != null && orderItem.getStandardNotaxPrice() != null ? orderItem.getStandardNotaxPrice().stripTrailingZeros().toPlainString() : null);
                    // 3.23.4: 未税总价
                    row.add(orderItem != null && orderItem.getStandardNotaxPrice() != null && orderItem.getRequireQuantity() != null ?
                            orderItem.getStandardNotaxPrice().multiply(orderItem.getRequireQuantity()).setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() : null);
                    // 3.23.5: 到货周期(自然日)
                    row.add(orderItem != null ? orderItem.getExtLeadTime() : null);
                    // 3.23.6: 质保期(自然日)
                    row.add(orderItem != null ? orderItem.getExtWarrantyPeriod() : null);
                });
            }
        }
        // 4: 导出
        try (OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "询比价评选列表信息")) {
            EasyExcel.write(outputStream)
                    .sheet(0)
                    .head(headList)
                    .doWrite(dataList);
        } catch (Exception e) {
            log.error("询价导出异常", e);
        }
    }

    /**
     * 设置行数据
     * @param queryVO 参数
     * @return 返回
     */
    @NotNull
    private List<List<Object>> getLists(ExtInqSouSelectQueryVO queryVO) {
        List<List<Object>> dataList = new ArrayList<>(queryVO.getItemList().size());
        {
            // 查询数据
            Map<String/* code */, String/* name */> unitMap = baseClient.listAllEnablePurchaseUnit().stream().collect(Collectors.toMap(PurchaseUnit::getUnitCode, PurchaseUnit::getUnitName));
            Map<String/* dictCode */, Map<String/* dictItemCode */, String/* dictItemName */>> dictMap = baseClient.listByDictCode(Arrays.asList("YES_OR_NO", "REGION", "EXT_SOU_INQ_ORDER_INVOICE_TYPE"))
                    .stream().collect(Collectors.groupingBy(DictItemDTO::getDictCode, Collectors.toMap(DictItemDTO::getDictItemCode, DictItemDTO::getDictItemName)));

            int index = 0;
            List<Object> row = new ArrayList<>(30);
            dataList.add(row);
            for (ExtInqSouSelectQueryDetailVO itemInfo : queryVO.getItemList()) {
                index++;
                // 3.1: 序号
                row.add(index);
                // 3.2: 轮次
                row.add(itemInfo.getRound());
                // 3.3: 业务实体
                row.add(itemInfo.getOrgOuName());
                // 3.4: 区域
                String v = Optional.of(dictMap.get("REGION")).orElse(Collections.emptyMap()).get(itemInfo.getExtAreaName());
                row.add(v != null ? v : itemInfo.getExtAreaName());
                // 3.5: 报价次数
                row.add(itemInfo.getOrderCount());
                // 3.6: 是否无料号寻源
                row.add(Optional.of(dictMap.get("YES_OR_NO")).orElse(Collections.emptyMap()).get(itemInfo.getNoCodeItem().name()));
                // 3.7: 物料编码
                row.add(itemInfo.getItemCode());
                // 3.8: 物料名称
                row.add(itemInfo.getItemDesc());
                // 3.9: 采购分类
                row.add(itemInfo.getCategoryName());
                // 3.10: 规格型号
                row.add(itemInfo.getExtMaterialMode());
                // 3.11: 数量
                row.add(itemInfo.getRequireQuantity() != null ? itemInfo.getRequireQuantity().stripTrailingZeros().toPlainString() : null);
                // 3.12: 基本计量单位
                v = unitMap.get(itemInfo.getUnit());
                row.add(v != null ? v : itemInfo.getUnit());
                // 3.13: 品牌
                row.add(itemInfo.getExtBrand());
                // 3.14: 供应商编码
                row.add(itemInfo.getWinVendorCode());
                // 3.15: 中标供应商
                row.add(itemInfo.getWinVendorName());
                // 3.16: 税率(%)
                row.add(itemInfo.getWinTaxRate() != null ? itemInfo.getWinTaxRate().stripTrailingZeros().toPlainString() : null);
                // 3.17: 发票类型
                row.add(Optional.of(dictMap.get("EXT_SOU_INQ_ORDER_INVOICE_TYPE")).orElse(Collections.emptyMap()).get(itemInfo.getWinInvoiceType()));
                // 3.18: 未税单价
                row.add(itemInfo.getWinStandardNotaxPrice() != null ? itemInfo.getWinStandardNotaxPrice().stripTrailingZeros().toPlainString() : null);
                // 3.19: 未税总价
                row.add(itemInfo.getWinStandardTotalPrice() != null ? itemInfo.getWinStandardTotalPrice().stripTrailingZeros().toPlainString() : null);
                // 3.20: 到货周期(自然日)
                row.add(itemInfo.getWinExtLeadTime());
                // 3.21: 质保期(自然日)
                row.add(itemInfo.getWinExtWarrantyPeriod());
                // 3.22: 是否生成定价单
                v = Optional.of(dictMap.get("YES_OR_NO")).orElse(Collections.emptyMap()).get(itemInfo.getHasFixPrice() != null ? itemInfo.getHasFixPrice().name() : null);
                row.add(v != null ? v : "否");
                // 3.23: 供应商-动态相关
                Map<Long/* vendorId */, ApiInqSouOrderItemVO> orderItemMap = itemInfo.getOrderItemList().stream()
                                .collect(Collectors.toMap(ApiInqSouOrderItemVO::getVendorId, Function.identity()));
                queryVO.getVendorList().forEach(vendor -> {
                    ApiInqSouOrderItemVO orderItem = orderItemMap.get(vendor.getVendorId());
                    // 3.23.1: 税率(%)
                    row.add(orderItem != null && orderItem.getTaxRate() != null ? orderItem.getTaxRate().stripTrailingZeros().toPlainString() : null);
                    // 3.23.2: 发票类型
                    row.add(orderItem != null ? Optional.of(dictMap.get("EXT_SOU_INQ_ORDER_INVOICE_TYPE")).orElse(Collections.emptyMap()).get(orderItem.getInvoiceType()) : null);
                    // 3.23.3: 未税单价
                    row.add(orderItem != null && orderItem.getStandardNotaxPrice() != null ? orderItem.getStandardNotaxPrice().stripTrailingZeros().toPlainString() : null);
                    // 3.23.4: 未税总价
                    row.add(orderItem != null && orderItem.getStandardNotaxPrice() != null && orderItem.getRequireQuantity() != null ?
                            orderItem.getStandardNotaxPrice().multiply(orderItem.getRequireQuantity()).setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() : null);
                    // 3.23.5: 到货周期(自然日)
                    row.add(orderItem != null ? orderItem.getExtLeadTime() : null);
                    // 3.23.6: 质保期(自然日)
                    row.add(orderItem != null ? orderItem.getExtWarrantyPeriod() : null);
                });
            }
        }
        return dataList;
    }

}
