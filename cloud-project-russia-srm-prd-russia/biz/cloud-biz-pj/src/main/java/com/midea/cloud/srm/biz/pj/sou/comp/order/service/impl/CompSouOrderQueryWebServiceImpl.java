package com.midea.cloud.srm.biz.pj.sou.comp.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouBaseMaterialPriceDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouItemHisDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.comp.order.dao.CompSouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.comp.order.service.CompSouOrderQueryWebService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouFileConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouRoundDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.service.SouOrderQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderJudgeHandler;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.formula.entity.EssentialFactor;
import com.midea.cloud.srm.model.base.formula.vo.EssentialFactorVO;
import com.midea.cloud.srm.model.base.material.MaterialItemAttributeRelate;
import com.midea.cloud.srm.model.base.purchase.dto.PurchaseExchangeRateQueryDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseExchangeRate;
import com.midea.cloud.srm.model.pj.sou.comp.dto.webapi.order.CompSouVendorViewOrderDetailQueryWebDTO;
import com.midea.cloud.srm.model.pj.sou.comp.entity.*;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order.CompSouOrderDetailWebVO;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order.CompSouOrderItemWebVO;
import com.midea.cloud.srm.model.pj.sou.comp.vo.webapi.order.CompSouVendorViewOrderDetailsWebVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouInitProjectVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.init.ApiCompSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderResultQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.order.ApiSouOrderQueryVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.signup.ApiSouOrderSignUpInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 竞价 - 报价查询
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouOrderQueryWebServiceImpl implements CompSouOrderQueryWebService {

    @Autowired
    private SouOrderQueryService souOrderQueryService;
    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private CompSouProjectDAOImpl compSouProjectDao;
    @Autowired
    private SouRoundDAOImpl souRoundDao;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private CompSouItemDAOImpl compSouItemDao;
    @Autowired
    private SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    private CompSouOrderItemDAOImpl compSouOrderItemDao;
    @Autowired
    private SouFileConfigDAOImpl souFileConfigDao;
    @Autowired
    private SouOrderFileDAOImpl souOrderFileDao;
    @Autowired
    private CompSouBaseMaterialPriceDAOImpl compSouBaseMaterialPriceDao;
    @Autowired
    private CompSouItemHisDAOImpl compSouItemHisDao;
    @Autowired
    private BaseClient baseClient;

    /**
     * 查询询价单列表
     */
    @Override
    public List<ApiCompSouOrderQueryVO> listCompOrders(ApiSouOrderQueryDTO queryParam) {
        /* 1: 查询报价单数据 */
        List<ApiSouOrderQueryVO> souVOList = souOrderQueryService.listOrders(queryParam, SouTypeEnum.comp.name());
        /* 2: 数据转化 */
        return ApiCompSouOrderQueryVO.convertCompVO(souVOList);
    }

    /**
     * 查看项目信息【项目信息】
     */
    @Override
    public ApiCompSouInitProjectVO getCompProjectInfo(long projectId, long vendorId) {
        /* 1: 查询核心数据 */
        ApiSouInitProjectInfoVO souVO = souOrderQueryService.getProjectInfo(projectId, vendorId, SouTypeEnum.comp.name());
        /* 2: 数据转化 */
        return SouObjectXUtil.convertTargetObj(souVO, ApiCompSouInitProjectVO.class);
    }

    /**
     * 查看项目信息【项目需求】
     */
    @Override
    public List<ApiCompSouItemVO> getCompRequireInfo(long projectId, long vendorId) {
        /* 1: 查询数据 */
        List<ApiSouItemVO> souVOList = souOrderQueryService.getRequireInfo(projectId, vendorId, SouTypeEnum.comp.name());
        /* 2: 数据转化 */
        return SouObjectXUtil.convertTargetObj(souVOList, new TypeReference<List<ApiCompSouItemVO>>() {});
    }

    /**
     * 查看项目信息【报名信息】
     */
    @Override
    public ApiSouOrderSignUpInfoVO getCompSignUpInfo(long projectId, long vendorId) {
        /* 1: 查询核心数据 */
        ApiSouOrderSignUpInfoVO souVO = souOrderQueryService.getSignUpInfo(projectId, vendorId, SouTypeEnum.comp.name());
        /* 2: 数据转化 */
        return SouObjectXUtil.convertTargetObj(souVO, ApiSouOrderSignUpInfoVO.class);
    }

    /**
     * 查看项目信息【投标明细】
     */
    @Override
    public CompSouVendorViewOrderDetailsWebVO getOrderDetails(CompSouVendorViewOrderDetailQueryWebDTO queryParam, long vendorId) {
        /* 1: 入参格式化 */
        queryParam.formatParams();
        /* 1: 查询数据 */
        SouProject souProject = souProjectDao.getById(queryParam.getProjectId());
        CompSouProject compProject = compSouProjectDao.getById(queryParam.getProjectId());
        SouRound currentRound = souRoundDao.lambdaQuery()
                .eq(SouRound::getProjectId, queryParam.getProjectId())
                .eq(SouRound::getRound, souProject.getCurrentRound())
                .one();
        List<SouItem> souItemList = souItemDao.lambdaQuery().eq(SouItem::getProjectId, queryParam.getProjectId()).list();
        List<CompSouItem> compItemList = compSouItemDao.lambdaQuery().eq(CompSouItem::getProjectId, queryParam.getProjectId()).list();
        List<SouOrderItem> souOrderItemList = souOrderItemDao.lambdaQuery()
                .eq(SouOrderItem::getProjectId, queryParam.getProjectId())
                .eq(SouOrderItem::getVendorId, vendorId)
                .list();
        List<CompSouOrderItem> compOrderItemList; {
            if (souOrderItemList.isEmpty()) {
                compOrderItemList = Collections.emptyList();
            } else {
                compOrderItemList = compSouOrderItemDao.lambdaQuery()
                        .in(CompSouOrderItem::getOrderItemId, souOrderItemList.stream()
                                .map(SouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                        .list();
            }
        }
        List<SouFileConfig> fileConfigList = souFileConfigDao.lambdaQuery()
                .eq(SouFileConfig::getProjectId, queryParam.getProjectId())
                .orderByAsc(SouFileConfig::getSortIndex)
                .list();
        List<SouOrderFile> orderFileList; {
            if (souOrderItemList.isEmpty()) {
                orderFileList = Collections.emptyList();
            } else {
                orderFileList = souOrderFileDao.lambdaQuery()
                        .in(SouOrderFile::getOrderId, souOrderItemList.stream()
                                .map(SouOrderItem::getOrderId).collect(Collectors.toSet()))
                        .list();
            }
        }
        Map<String/* souItemId_round */, CompSouItemHis> compSouItemHisMap = compSouItemHisDao.lambdaQuery()
                .eq(CompSouItemHis::getProjectId, souProject.getProjectId())
                .list().stream().collect(Collectors.toMap(e -> e.getSouItemId() + "_" + e.getRound(), Function.identity()));
        /* 3: 组装数据 */
        return CompSouVendorViewOrderDetailsWebVO.convert(souProject, compProject, currentRound, souItemList, compItemList, compSouItemHisMap,
                souOrderItemList, compOrderItemList, fileConfigList, orderFileList);
    }

    /**
     * 查看结果
     */
    @Override
    public List<CompSouOrderItemWebVO> listOrderResult(ApiSouOrderResultQueryDTO queryParam, boolean isBuyer) {
        /* 1: 查询数据 */
        List<ApiSouOrderItemVO> voList = souOrderQueryService.listOrderResult(queryParam, isBuyer, SouTypeEnum.comp.name());
        /* 2: 数据转化 */
        return CompSouOrderItemWebVO.convertCompVO(voList);
    }

    /**
     * 查询询价单详情(用于报价)
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param vendorId 供应商ID
     */
    @Override
    public CompSouOrderDetailWebVO getCompSouOrderInfo(long projectId, long vendorId, boolean isBuyer) {
        /* 1: 查询报价详情数据 */
        ApiSouOrderDetailVO orderVO = souOrderQueryService.getSouOrderInfo(projectId, vendorId, null, isBuyer, SouTypeEnum.comp.name());
        /* 2: 数据转化 */
        SouProject souProject = souProjectDao.getById(projectId);
        List<CompSouItemHis> compItemHisList = compSouItemHisDao.lambdaQuery()
                .eq(CompSouItemHis::getProjectId, projectId)
                .eq(CompSouItemHis::getRound, souProject.getCurrentRound())
                .list();
        return CompSouOrderDetailWebVO.convertCompVO(orderVO, compItemHisList);
    }

    /**
     * 提供一个单独的查看公式报价的接口
     * PS: 因为有好几个页面需要查看公式报价
     *
     * @param souItemId    物料需求行ID
     * @param orderItemId  报价单行ID(可能为空，此时供应商还未填写阶梯报价)
     * @param currencyCode 基价转换的目标币种
     */
    @Override
    public List<EssentialFactorVO> getOrderFormulaPrices(long souItemId, @Nullable Long orderItemId,
                                                         String currencyCode, long vendorId, boolean isBuyer) {
        /* 1: 校验操作条件/权限 */
        if (orderItemId != null) {
            SouOrderItem souOrderItem = souOrderItemDao.getById(orderItemId);
            AssertUtils.notNull(souOrderItem, LocaleHandler.getLocaleMsg("报价行信息[{0}]不存在"), orderItemId);
            SouActiveBeanUtils.getActiveBean(SouTypeEnum.comp.name(), ApiSouOrderJudgeHandler.class)
                    .judgeGetOrderInfoAuth(souOrderItem.getOrderId(), vendorId, isBuyer, SouTypeEnum.comp.name());
        } else {
            SouItem souItem = souItemDao.getById(souItemId);
            AssertUtils.notNull(souItem, LocaleHandler.getLocaleMsg("物料需求信息[{0}]不存在"), souItemId);
            SouActiveBeanUtils.getActiveBean(SouTypeEnum.comp.name(), ApiSouOrderJudgeHandler.class)
                    .judgeGetOrderInfoAuth(souItem.getProjectId(), vendorId, null, isBuyer, SouTypeEnum.comp.name());
        }
        /* 2. 查询公式信息 */
        CompSouItem compItem = compSouItemDao.lambdaQuery().eq(CompSouItem::getSouItemId, souItemId).one();
        AssertUtils.notNull(compItem, LocaleHandler.getLocaleMsg("物料需求信息[{0}]不存在"), souItemId);
        SouProject souProject = souProjectDao.getById(compItem.getProjectId());
        CompSouProject compProject = compSouProjectDao.getById(souProject.getProjectId());
        if (!SouOrderTypeEnum.FORMULA.equals(souProject.getOrderType())) {
            /* 非公式报价 */
            return Collections.emptyList();
        }
        SouOrderItem orderItem = null;
        CompSouOrderItem compOrderItem = null;
        if (orderItemId != null) {
            orderItem = souOrderItemDao.getById(orderItemId);
            compOrderItem = compSouOrderItemDao.getById(orderItemId);
            AssertUtils.notNull(orderItem, LocaleHandler.getLocaleMsg("报价信息[{0}]不存在"), orderItemId);
        }
        int round = orderItem != null ? orderItem.getRound() : souProject.getCurrentRound();

        /* 查询公式要素 */
        Map<Long/* factorId */, EssentialFactor> factorMap = baseClient.getFactorInfoByFormulaId(compItem.getFormulaId())
                .stream()
                .collect(Collectors.toMap(EssentialFactor::getEssentialFactorId, Function.identity()));
        if (factorMap.isEmpty()) {
            return Collections.emptyList();
        }
        /* 查询基材价格 */
        Map<Long/* factorId */, CompSouBaseMaterialPrice> compBaseMaterialPriceMap = compSouBaseMaterialPriceDao.lambdaQuery()
                .eq(CompSouBaseMaterialPrice::getProjectId, souProject.getProjectId())
                .eq(CompSouBaseMaterialPrice::getRound, round)
                .list()
                .stream().collect(Collectors.toMap(CompSouBaseMaterialPrice::getFactorId, Function.identity()));
        /* 查询物料属性值 */
        Map<Long/* factorId */, MaterialItemAttributeRelate> materialAttrMap = baseClient
                .getMaterialAttrPriceInfo(compItem.getMaterialFormulaRelateId());
        /* 查询汇率 */
        Map<String/* fromCurrency_toCurrency */, BigDecimal> exchangeRateMap; {
            PurchaseExchangeRateQueryDTO queryParam = new PurchaseExchangeRateQueryDTO();
            queryParam.setExchangeDate(compProject.getCurrencyExchangeDate());
//            必须包含汇率类型
            queryParam.setRateType(compProject.getExchangeRateType());
            Map<String/* fromCurrency */, List<PurchaseExchangeRate>> exchangeRateInfos = baseClient.listExchangeRateByFromCurrencies(queryParam);
            exchangeRateMap = new HashMap<>(exchangeRateInfos.size() << 2);
            exchangeRateInfos.forEach((fromCurrency, exchangeRates) ->
                    exchangeRates.forEach(exchangeRate ->
                            exchangeRateMap.put(fromCurrency + "_" + exchangeRate.getToCurrencyCode(), exchangeRate.getPriceTax()))
            );
        }

        return CompSouOrderItemWebVO.convertCompFormulaInfo(factorMap, compBaseMaterialPriceMap, materialAttrMap, exchangeRateMap,
                currencyCode, compOrderItem != null ? compOrderItem.getFormulaResult() : null);
    }

}
