package com.midea.cloud.srm.biz.pj.sou.sourcing.select.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.biz.pj.sou.sourcing.controller.service.SouControlEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouVendorDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemHisDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao.SouOrderResultDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao.SouSelectFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao.SouSelectMapper;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao.SouSelectOnFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.service.SouSelectQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.select.ApiSouSelectJudgeHandler;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.select.ApiSouSelectQueryHandler;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.enums.SouRulesEnums;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectItemQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectResultVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouSelectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouOrderReportVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.select.ApiSouSelectQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源核心 - 评选服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/09/08
 */
@Slf4j
@Service
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
public class SouSelectQueryServiceImpl implements SouSelectQueryService {

    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    private SouVendorDAOImpl souVendorDao;
    @Autowired
    private SouOrderItemHisDAOImpl souOrderItemLadderDao;
    @Autowired
    private SouSelectMapper souSelectMapper;
    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouProcessConfigDAOImpl souProcessConfigDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouSelectOnFileDAOImpl souSelectOnFileDao;
    @Autowired
    private SouOrderResultDAOImpl souOrderResultDao;
    @Autowired
    private SupplierClient supplierClient;
    @Autowired
    private SouControlEventService souControlEventService;

    @Autowired
    private SouSelectFileDAOImpl souSelectFileDao;

    /**
     * 评选信息列表查询
     */
    @Override
    public List<ApiSouSelectQueryVO> listEvaluations(ApiSouSelectQueryDTO queryParam, String souType) {
        /* 0: 刷新数据 */
        if (queryParam.getProjectId() != null) {
            souControlEventService.refreshProjectByWin(queryParam.getProjectId());
        }
        /* 1: 入参格式化 */
        queryParam.formatParams();
        /* 2: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectJudgeHandler.class).judgeListEvaluationsAuth(queryParam.getProjectId(), souType);
        /* 3: 查询数据 */
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        List<ApiSouSelectQueryVO> voList = null;
        if (!voList.isEmpty()) {
            /* 3.1: 查询转化基本信息 */
            SouProject souProject = souProjectDao.getById(queryParam.getProjectId());
            boolean isPriceNotax = Enable.N.equals(souProject.getIsPriceNotax());
            voList.forEach(vo -> SouObjectXUtil.mergeProperties(souProject, vo));
            /* 3.2: 查询转化物料需求 */
            Set<Long> souItemIds = voList.stream().map(ApiSouSelectQueryVO::getSouItemId).collect(Collectors.toSet());
            Map<Long/* souItemId */, SouItem> souItemMap = souItemDao.listByIds(souItemIds)
                    .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
            voList.forEach(vo -> SouObjectXUtil.mergeProperties(souItemMap.get(vo.getSouItemId()), vo));
            /* 3.3: 查询转化报价行 */
            Set<Long> orderItemIds = voList.stream().map(ApiSouSelectQueryVO::getOrderItemId).collect(Collectors.toSet());
            Map<Long/* orderItemId */, SouOrderItem> orderItemMap = souOrderItemDao.listByIds(orderItemIds)
                    .stream().collect(Collectors.toMap(SouOrderItem::getOrderItemId, Function.identity()));
            voList.forEach(vo -> {
                if (isPriceNotax) {
                    vo.setMonthlyTotalAmount(vo.getMonthlyProduction().multiply(vo.getOrderNotaxPrice()));
                } else {
                    vo.setMonthlyTotalAmount(vo.getMonthlyProduction().multiply(vo.getOrderTaxPrice()));
                }
                SouObjectXUtil.mergeProperties(orderItemMap.get(vo.getOrderItemId()), vo);
            });
            /* 3.4: 查询转化供应商信息 */
            Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDao.list(SouVendor::getProjectId, queryParam.getProjectId())
                    .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
            voList.forEach(vo -> SouObjectXUtil.mergeProperties(vendorMap.get(vo.getVendorId()), vo));
            /* 3.5: 查询转化阶梯价 */
            Map<Long/* orderItemId */, List<SouOrderItemHis>> ladderMap = souOrderItemLadderDao.lambdaQuery()
                    .in(SouOrderItemHis::getOrderItemId, orderItemIds)
                    .orderByAsc(SouOrderItemHis::getOrder_round)
                    .list().stream().collect(Collectors.groupingBy(SouOrderItemHis::getOrderItemId));
            voList.forEach(vo -> vo.setLadderPriceList(ladderMap.get(vo.getOrderItemId())));
            /*根据供应商金额排序并计算供应商及金额 */
            voList.sort(Comparator.comparing(ApiSouSelectQueryVO::getOrderTaxPrice).reversed());
            ApiSouSelectResultVO selectVendorPriceDTO = new ApiSouSelectResultVO();
            for (int i = 0; i < voList.size(); i++) {
                ApiSouSelectQueryVO apiSouSelectQueryVO = voList.get(i);
                switch (i) {
                    case 0:
                        /* 最高价 */
                        if (isPriceNotax) {
                            selectVendorPriceDTO.setMaxPrice(apiSouSelectQueryVO.getOrderNotaxPrice());
                        } else {
                            selectVendorPriceDTO.setMaxPrice(apiSouSelectQueryVO.getOrderTaxPrice());
                        }
                        selectVendorPriceDTO.setMaxVendorName(apiSouSelectQueryVO.getVendorName());
                        break;
                    case 1:
                        /* 第二高 */
                        if (isPriceNotax) {
                            selectVendorPriceDTO.setSecondPrice(apiSouSelectQueryVO.getOrderNotaxPrice());
                        } else {
                            selectVendorPriceDTO.setSecondPrice(apiSouSelectQueryVO.getOrderTaxPrice());
                        }
                        selectVendorPriceDTO.setSecondVendorName(apiSouSelectQueryVO.getVendorName());
                        break;
                    case 2:
                        /* 第三高 */
                        if (isPriceNotax) {
                            selectVendorPriceDTO.setThirdPrice(apiSouSelectQueryVO.getOrderNotaxPrice());
                        } else {
                            selectVendorPriceDTO.setThirdPrice(apiSouSelectQueryVO.getOrderTaxPrice());
                        }
                        selectVendorPriceDTO.setThirdVendorName(apiSouSelectQueryVO.getVendorName());
                        break;
                    default:
                }
                /*查询上期中标供应商及金额 */
                List<SouOrderItem> periodWinList = souOrderItemDao.lambdaQuery()
                        .eq(SouOrderItem::getProjectId, queryParam.getProjectId())
                        .eq(SouOrderItem::getItemId, apiSouSelectQueryVO.getItemId())
                        .eq(SouOrderItem::getAffiliatedUnit, apiSouSelectQueryVO.getAffiliatedUnit())
                        .eq(SouOrderItem::getSelectStatus, SouSelectStatusEnum.WIN)
                        .list();
                if (CollectionUtils.isNotEmpty(periodWinList)) {
                    periodWinList.sort(Comparator.comparing(SouOrderItem::getOrderTaxPrice).reversed());
                    periodWinList.sort(Comparator.comparing(SouOrderItem::getLastUpdateDate).reversed());
                    selectVendorPriceDTO.setPeriodPrice(periodWinList.get(0).getOrderTaxPrice());
                    selectVendorPriceDTO.setPeriodVendorName(supplierClient
                            .getCompanyInfo(periodWinList.get(0).getVendorId()).getCompanyName());
                    /*本期最高价-上期中标单价）上期中标单价 */
                    selectVendorPriceDTO.setDifferenceRate(selectVendorPriceDTO.getMaxPrice()
                            .subtract(selectVendorPriceDTO.getPeriodPrice()));
                }
                apiSouSelectQueryVO.setSelectVendorPriceDTO(selectVendorPriceDTO);
            }
        }
        /* 4: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectQueryHandler.class).doHandlerAfterListEvaluations(queryParam, souType, voList);
    }

    /**
     * 获取报价报表信息
     */
    @Override
    public ApiSouOrderReportVO generatePriceReport(long projectId, String souType) {
        /* 1: 校验操作条件/权限 */
        SouProject souProject = SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectJudgeHandler.class)
                .judgeGeneratePriceReportAuth(projectId, souType);
        /* 2: 查询数据 */
        /* 2.1: 查询物料需求信息 */
        List<SouItem> souItemList = souItemDao.list(SouItem::getProjectId, projectId);
        /* 2.2: 查询供应商信息 */
        Map<Long, SouVendor> vendorMap;/* vendorId */
        {
            SouProcessConfig processConfig = souProcessConfigDao.getById(souProject.getProcessConfigId());
            boolean needCheckSignUp = Enable.Y.equals(processConfig.getSignUpManagement());
            vendorMap = souVendorDao.lambdaQuery()
                    .eq(SouVendor::getProjectId, projectId)
                    /* 检查报名情况 */
                    .eq(needCheckSignUp, SouVendor::getSignUpStatus, SouSignUpStatusEnum.SIGN_UP_DONE)
                    .list()
                    .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
        }
        /* 2.3: 查询中标的供应商报价信息 */
        List<SouOrderItem> winOrderLineList = souOrderItemDao.lambdaQuery()
                .eq(SouOrderItem::getProjectId, projectId)
                .eq(SouOrderItem::getRound, souProject.getCurrentRound())
                .eq(SouOrderItem::getSelectStatus, SouSelectStatusEnum.WIN)
                .list();
        Map<Long/* orderId */, SouOrder> orderMap = souOrderDao.lambdaQuery()
                .in(SouOrder::getOrderId, winOrderLineList.stream().map(SouOrderItem::getOrderId).collect(Collectors.toSet()))
                .list()
                .stream().collect(Collectors.toMap(SouOrder::getOrderId, Function.identity()));
        /* 3: 组装数据 */
        ApiSouOrderReportVO vo = ApiSouOrderReportVO.convertApiVO(souProject, souItemList, winOrderLineList, orderMap, vendorMap);
        /* 4: 行业包额外处理(后置) */
        return SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectQueryHandler.class).doHandlerAfterGeneratePriceReport(projectId, souType, vo);
    }

    @Override
    public List<ApiSouSelectItemQueryVO> listItemEvaluations(ApiSouSelectQueryDTO queryParam, String souType) {
        /* 0: 刷新数据 */
        if (queryParam.getProjectId() != null) {
            souControlEventService.refreshProjectByWin(queryParam.getProjectId());
        }
        /* 1: 入参格式化 */
        queryParam.formatParams();
        /* 2: 校验操作条件/权限 */
        SouActiveBeanUtils.getActiveBean(souType, ApiSouSelectJudgeHandler.class).judgeListEvaluationsAuth(queryParam.getProjectId(), souType);
        /* 3: 查询数据 */
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        List<ApiSouSelectItemQueryVO> voList = souSelectMapper.queryHisItemList(queryParam);
        if (!voList.isEmpty()) {
            /* 3.1: 查询转化基本信息 */
            SouProject souProject = souProjectDao.getById(queryParam.getProjectId());
            boolean isPriceNotax = Enable.N.equals(souProject.getIsPriceNotax());
            voList.forEach(vo -> SouObjectXUtil.mergeProperties(souProject, vo));
            /* 3.2: 查询转化物料需求 */
            Set<Long> souItemIds = voList.stream().map(ApiSouSelectItemQueryVO::getSouItemId).collect(Collectors.toSet());
            Map<Long/* souItemId */, SouItem> souItemMap = souItemDao.listByIds(souItemIds)
                    .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
            voList.forEach(vo -> SouObjectXUtil.mergeProperties(souItemMap.get(vo.getSouItemId()), vo));

            /* 3.4: 查询转化供应商信息 */
            Map<Long/* vendorId */, SouVendor> vendorMap = souVendorDao.list(SouVendor::getProjectId, queryParam.getProjectId())
                    .stream().collect(Collectors.toMap(SouVendor::getVendorId, Function.identity()));
            voList.forEach(vo -> SouObjectXUtil.mergeProperties(vendorMap.get(vo.getVendorId()), vo));

            /* 3.3: 查询转化报价行 */
            Set<Long> orderItemIds = voList.stream().map(ApiSouSelectItemQueryVO::getOrderItemId).collect(Collectors.toSet());
            Map<Long/* orderItemId */, SouOrderItemHis> orderItemMap = souOrderItemLadderDao.listByIds(orderItemIds)
                    .stream().collect(Collectors.toMap(SouOrderItemHis::getOrderItemId, Function.identity()));
            voList.forEach(vo -> {
                SouObjectXUtil.mergeProperties(orderItemMap.get(vo.getOrderItemId()), vo);
            });
        }
        return voList;
    }

    @Override
    public List<ApiSouSelectResultVO> listOrderResult(ApiSouSelectQueryDTO queryParam, String name) {

        /* 获取projectId */
        Long projectId = queryParam.getProjectId();

        SouProject souProject = souProjectDao.getById(projectId);

        /* 物料表数据 scc_sou_item */
        List<SouItem> souItemList = souItemDao.list(SouItem::getProjectId, projectId);
        /* 供应商表数据 scc_sou_vendor */
        List<SouVendor> souVendorList = souVendorDao.list(SouVendor::getProjectId, projectId);
        /* 报价明细 scc_sou_order_item */
        List<ApiSouSelectItemQueryVO> souOrderItemList = souSelectMapper.querySelectList(queryParam);
        List<ApiSouSelectItemQueryVO> newsouOrderItemList = souSelectMapper.querySelectList(queryParam);


        Map<Long,BigDecimal> orderTaxPriceMap = new HashMap<>(50);
        for(int i = 0;i < souOrderItemList.size();i++){
            orderTaxPriceMap.put(souOrderItemList.get(i).getOrderItemId(),souOrderItemList.get(i).getOrderTaxPrice());
        }


        Map<Long, BigDecimal> taxPriceList = new HashMap<>(50);
        /*得到分组 */
        Set<String> itemGroupList = new HashSet<>();
        for (int i = 0; i < souItemList.size(); i++) {
            if (!StringUtils.isEmpty(souItemList.get(i).getItemGroup()) && null != souItemList.get(i).getItemGroup()) {
                itemGroupList.add(souItemList.get(i).getItemGroup());
            }
        }

        if(itemGroupList.size() == 0){
            itemGroupList.add("没分组");
        }/* 分组 和 物料 */
        Map<Long, String> souItemMap = new HashMap<>(50);
        for (int i = 0; i < souItemList.size(); i++) {
            souItemMap.put(souItemList.get(i).getSouItemId(), souItemList.get(i).getItemGroup());
        }
        /* 将分组存到报价明细表中 */
        for (int i = 0; i < souOrderItemList.size(); i++) {
            String group = souItemMap.get(souOrderItemList.get(i).getSouItemId());
            /* 先清空 */
            souOrderItemList.get(i).setLastUpdatedByIp(null);
            souOrderItemList.get(i).setLastUpdatedByIp(group);
        }

        Map<Long, List<SouOrderItem>> newMap = new HashMap<>(50);
        /* 根据供应商id分组创建多个map集合 */
        for (int i = 0; i < souVendorList.size(); i++) {
            List<SouOrderItem> list = new ArrayList<>();
            newMap.put(souVendorList.get(i).getVendorId(), list);
        }
        /* 供应商分组 */
        for (int i = 0; i < souVendorList.size(); i++) {
            Long vendorId = souVendorList.get(i).getVendorId();
            for (int m = 0; m < souOrderItemList.size(); m++) {
                /* 得到同一个供应商的数据 */
                if (vendorId.equals(souOrderItemList.get(m).getVendorId())) {
                    List<SouOrderItem> list = newMap.get(vendorId);
                    list.add(souOrderItemList.get(m));
                }
            }
        }
        /* 根据ItemGroup来进行分组 */
        Map<String, List<SouOrderItem>> groupMap = new HashMap<>(50);
        List<SouOrderItem> orderItemlist = new ArrayList<>();
        for (String groupId : itemGroupList) {
            groupMap.put(groupId, new ArrayList<SouOrderItem>());
            /* 三个供应商的数据 */
            for (Map.Entry entry : newMap.entrySet()) {
                /* 供应商id */
                Long key = (Long) entry.getKey();
                /* 供应商对应的报价明细 */
                List<SouOrderItem> list = (List<SouOrderItem>) entry.getValue();
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        /* 有分组 */
                        if ((!StringUtils.isEmpty(list.get(i).getLastUpdatedByIp())) && (null != list.get(i).getLastUpdatedByIp()) ) {
                            if (groupId.equals(list.get(i).getLastUpdatedByIp())) {
                                orderItemlist = groupMap.get(groupId);
                                orderItemlist.add(list.get(i));
                            }
                            /* 没有分组 */
                        } else {
                            taxPriceList.put(list.get(i).getOrderItemId(), list.get(i).getOrderTaxPrice());
                        }
                    }

                    for(int i = 0;i<orderItemlist.size();i++){
                        /* 物资单价，组合的话，需要乘以月产量 */
                          BigDecimal orderTaxPrice = orderItemlist.get(i).getOrderTaxPrice();
                          BigDecimal monthlyProduction = orderItemlist.get(i).getMonthlyProduction();
                        orderItemlist.get(i).setOrderTaxPrice(orderTaxPrice.multiply(monthlyProduction));
                    }
                    if(orderItemlist.size()>0){
                        /* 求和 */
                        BigDecimal sum = orderItemlist.stream().map(e -> e.getOrderTaxPrice()).reduce(BigDecimal::add).get();
                        for (int i = 0; i < orderItemlist.size(); i++) {
                            taxPriceList.put(orderItemlist.get(i).getOrderItemId(), sum);
                        }
                    }

                    orderItemlist.clear();
                }
            }
        }
        /* taxPriceList 中是每个物料对应的报价,取前三就可以 */
        for (int i = 0; i < newsouOrderItemList.size(); i++) {
            newsouOrderItemList.get(i).setOrderTaxPrice(taxPriceList.get(newsouOrderItemList.get(i).getOrderItemId()));
        }
        List<ApiSouSelectResultVO> orderResultList = new ArrayList<>();
        /* 根据物料分组排序 */
        for (int i = 0; i < souItemList.size(); i++) {
            Long souItemId = souItemList.get(i).getSouItemId();
            List<ApiSouSelectItemQueryVO> list = new ArrayList<>();
            for (int m = 0; m < newsouOrderItemList.size(); m++) {
                if (souItemId.longValue() == newsouOrderItemList.get(m).getSouItemId().longValue()) {
                    /* 取list集合中前三大的记录 */
                    list.add(newsouOrderItemList.get(m));
                }
            }

            Collections.sort(list, new Comparator<SouOrderItem>() {
                @Override
                public int compare(SouOrderItem p1, SouOrderItem p2) {
                    int result =  p2.getOrderTaxPrice().compareTo(p1.getOrderTaxPrice());
                    if (result == 0) {
                        /* 取报价明细最后更新时间 */
                        result = p1.getLastUpdateDate().compareTo(p2.getLastUpdateDate());
                    }
                    return result;
                }
            });


            for (int n = 0; n < list.size(); n++) {

                log.info("====" + i + "=========物资" + list.get(n).getSouItemId() + "====供应商" + list.get(n).getVendorId() + "=======报价" + list.get(n).getOrderTaxPrice());
            }
            if(list.size()>0){
                LambdaQueryWrapper<SouOrderResult> queryResultWrapper = new LambdaQueryWrapper<>();
                queryResultWrapper.eq(SouOrderResult::getProjectId, list.get(0).getProjectId());
                queryResultWrapper.eq(SouOrderResult::getSouItemId, list.get(0).getSouItemId());

                /*返回至前端显示的VO */
                ApiSouSelectResultVO souOrderResult = new ApiSouSelectResultVO();
                /*保存至数据库的对象 */
                SouOrderResult resultDate = souOrderResultDao.selectFirst(queryResultWrapper);
                if (ObjectUtils.isEmpty(resultDate)) {
                    resultDate = new SouOrderResult();
                    BeanUtils.copyProperties(list.get(0), resultDate);
                    resultDate.setOrderResultId(IdGenrator.generate());
                    resultDate.setResultStatus(SouApprovalStatusEnum.DRAFT);
                    BeanUtils.copyProperties(resultDate, souOrderResult);
                } else {
                    BeanUtils.copyProperties(resultDate, souOrderResult);
                }

                Integer resultSize = list.size();
                for (int y = 0; y < list.size(); y++) {

                    if(SouRulesEnums.FORWARD_RULE.getCode().equals(souProject.getSouRules())) {

                    }
                    if (matchBeforeOrAfterThree(y, resultSize, 0, souProject.getSouRules())) {
                        long  orderTiemId = list.get(y).getOrderItemId();
                        souOrderResult.setMaxPrice(orderTaxPriceMap.get(orderTiemId));
                        souOrderResult.setMaxVendorName(list.get(y).getVendorName());
                        souOrderResult.setMaxVendorId(list.get(y).getVendorId());
                        souOrderResult.setMeteringUnit(list.get(y).getMeteringUnit());
                    } else if (matchBeforeOrAfterThree(y, resultSize, 1, souProject.getSouRules())) {
                        long  orderTiemId = list.get(y).getOrderItemId();
                        souOrderResult.setSecondPrice(orderTaxPriceMap.get(orderTiemId));
                        souOrderResult.setSecondVendorName(list.get(y).getVendorName());
                        souOrderResult.setSecondVendorId(list.get(y).getVendorId());
                    } else if (matchBeforeOrAfterThree(y, resultSize, 2, souProject.getSouRules())) {
                        long  orderTiemId = list.get(y).getOrderItemId();
                        souOrderResult.setThirdPrice(orderTaxPriceMap.get(orderTiemId));
                        souOrderResult.setThirdVendorName(list.get(y).getVendorName());
                        souOrderResult.setThirdVendorId(list.get(y).getVendorId());
                    }
                }
                /*查询上期中标供应商及金额 */
                List<SouOrderItem> periodWinList = souOrderItemDao.lambdaQuery()
                        .eq(SouOrderItem::getProjectId, queryParam.getProjectId())
                        .eq(SouOrderItem::getSouItemId, list.get(0).getSouItemId())
                        .eq(SouOrderItem::getAffiliatedUnit, list.get(0).getAffiliatedUnit())
                        .eq(SouOrderItem::getSelectStatus, SouSelectStatusEnum.WIN)
                        .list();
                if (CollectionUtils.isNotEmpty(periodWinList)) {
                    periodWinList.sort(Comparator.comparing(SouOrderItem::getOrderTaxPrice).reversed());
                    periodWinList.sort(Comparator.comparing(SouOrderItem::getLastUpdateDate).reversed());
                    souOrderResult.setPeriodPrice(periodWinList.get(0).getOrderTaxPrice());
                    souOrderResult.setPeriodVendorId(periodWinList.get(0).getVendorId());
                    souOrderResult.setPeriodVendorName(supplierClient
                            .getCompanyInfo(periodWinList.get(0).getVendorId()).getCompanyName());
                    /*本期最高价-上期中标单价）上期中标单价 */
                    souOrderResult.setDifferenceRate(souOrderResult.getMaxPrice()
                            .subtract(souOrderResult.getPeriodPrice()));
                }
                /*评选附件 */
                LambdaQueryWrapper<SouSelectFile> queryFileWrapper = new LambdaQueryWrapper<>();
                queryFileWrapper.eq(SouSelectFile::getProjectId, projectId);
                List<SouSelectFile> souFileList = souSelectFileDao.list(queryFileWrapper);
                souOrderResult.setSelectFileList(souFileList);
                orderResultList.add(souOrderResult);
                log.info("================");

            }

        }
        List<Long> souItemIdList = new ArrayList<>();
        /* 保存报价结果 20231107 lcw */
        if (CollectionUtils.isNotEmpty(orderResultList)) {
            List<SouOrderResult> souOrderResults = SouObjectXUtil.convertList(orderResultList, SouOrderResult.class);
            for(int i = 0 ; i<souOrderResults.size();i++){
                souOrderResultDao.saveOrUpdate(souOrderResults.get(i));
            }
            souItemIdList = orderResultList.stream().map(o -> o.getSouItemId()).distinct().collect(Collectors.toList());
        }
        /** 保存无报价结果的数据 */
        List<Long> finalSouItemIdList = souItemIdList;
        List<SouOrderResult> saveResultWithoutPrice = new ArrayList<>();
        souItemList.stream().filter(o -> !finalSouItemIdList.contains(o.getSouItemId())).forEach(item -> {
            SouOrderResult souOrderResult = new SouOrderResult();
            souOrderResult.setOrderId(-1L);
            souOrderResult.setProjectId(projectId);
            souOrderResult.setSouItemId(item.getSouItemId());
            souOrderResult.setItemDesc(item.getItemDesc());
            souOrderResult.setAffiliatedUnit(item.getAffiliatedUnit());
            souOrderResult.setMonthlyProduction(item.getMonthlyProduction());
            souOrderResult.setMaxPrice(BigDecimal.ZERO);
            souOrderResult.setSecondPrice(BigDecimal.ZERO);
            souOrderResult.setThirdPrice(BigDecimal.ZERO);
            souOrderResult.setPeriodPrice(BigDecimal.ZERO);
            souOrderResult.setDifferenceRate(BigDecimal.ZERO);
            souOrderResult.setMonthTotalAmount(BigDecimal.ZERO);
            souOrderResult.setMaxVendorName("");
            souOrderResult.setSecondVendorName("");
            souOrderResult.setThirdVendorName("");
            souOrderResult.setPeriodVendorName("");
            souOrderResult.setWinStatus(SouWinStatusEnum.D.name());
            souOrderResult.setOrderResultId(IdGenrator.generate());
            souOrderResult.setResultStatus(SouApprovalStatusEnum.DRAFT);
            saveResultWithoutPrice.add(souOrderResult);
        });

        if(CollectionUtils.isNotEmpty(saveResultWithoutPrice)) {
            LambdaQueryWrapper<SouOrderResult> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SouOrderResult::getProjectId, projectId);
            queryWrapper.in(SouOrderResult::getSouItemId, saveResultWithoutPrice.stream().map(k -> k.getSouItemId()).collect(Collectors.toList()));
            List<SouOrderResult> existResults = souOrderResultDao.list(queryWrapper);
            if(CollectionUtils.isNotEmpty(existResults)) {
                List<ApiSouSelectResultVO> voList = SouObjectXUtil.convertList(existResults, ApiSouSelectResultVO.class);
                orderResultList.addAll(voList);
            }

            List<Long> existSouItemIdList = existResults.stream().map(k -> k.getSouItemId()).distinct().collect(Collectors.toList());
            List<SouOrderResult> saveWithoutPrices = saveResultWithoutPrice.stream().filter(f -> !existSouItemIdList.contains(f.getSouItemId())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(saveWithoutPrices)) {
                souOrderResultDao.saveBatch(saveWithoutPrices);

                List<ApiSouSelectResultVO> voList = SouObjectXUtil.convertList(saveWithoutPrices, ApiSouSelectResultVO.class);
                orderResultList.addAll(voList);
            }

        }
        //根据souItemId进行升序排序
        Collections.sort(orderResultList, new Comparator<ApiSouSelectResultVO>() {
            @Override
            public int compare(ApiSouSelectResultVO o1, ApiSouSelectResultVO o2) {
                Long data1=o1.getSouItemId();
                Long data2=o2.getSouItemId();
                return data1.compareTo(data2);
            }
        });
        return orderResultList;
    }

    /**
     * 匹配前三或者后三家
     * @param index
     * @param size
     * @param matchIndex
     * @param souRules
     * @return
     */
    private boolean matchBeforeOrAfterThree(Integer index, Integer size, Integer matchIndex, String souRules) {

        if(SouRulesEnums.FORWARD_RULE.getCode().equals(souRules)) {
            if(index.compareTo(matchIndex) == 0) {
                return true;
            }
        } else {
            if(index.compareTo((size - matchIndex-1)) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<SouSelectPlaceOnFile> listPlaceOnFile(ApiSouSelectQueryDTO queryParam, String name) {
        /* 1: 入参格式化 */
        queryParam.formatParams();
        /* 2: 查询数据 */
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        LambdaQueryWrapper<SouSelectPlaceOnFile> queryResultWrapper = new LambdaQueryWrapper<>();
        queryResultWrapper.eq(SouSelectPlaceOnFile::getProjectId, queryParam.getProjectId());
        List<SouSelectPlaceOnFile> listFile = souSelectOnFileDao.list(queryResultWrapper);

        return listFile;
    }

    @Override
    public List<ApiSouSelectResultVO> listWinNotice(ApiSouSelectQueryDTO queryParam, String name) {

        /* 1: 入参格式化 */
        queryParam.formatParams();
        /* 2: 查询数据 */
        if (queryParam.getPageNum() != null && queryParam.getPageSize() != null) {
            PageMethod.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        }
        /*只查询同一物料，同一单位，同一竞价单号的数据 */
        List<ApiSouSelectResultVO> orderItemList = souSelectMapper.queryWinNoticeList(queryParam);
        /*根据单位名称过滤  每个单位名称一条数据 */
        for(int i = 0 ; i < orderItemList.size()-1; i ++){
             for(int j = orderItemList.size()-1; j >i; j--){
                 if(orderItemList.get(j).getAffiliatedUnit().equals(orderItemList.get(i).getAffiliatedUnit())){
                      orderItemList.remove(j);
                 }
             }
        }
        return orderItemList;
    }

}
