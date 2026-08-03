package com.midea.cloud.srm.biz.pj.sou.comp.order.domain.impl;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouBaseMaterialPriceDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouCurrencyDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.CompSouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.comp.order.domain.CompSouOrderDomainService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouCurrencyDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.base.PricingFormulaCalculateClient;
import com.midea.cloud.srm.model.base.formula.dto.calculate.PricingFormulaCalculateDTO;
import com.midea.cloud.srm.model.base.purchase.dto.PurchaseExchangeRateQueryDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseExchangeRate;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseTax;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouBaseMaterialPrice;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouCurrency;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouItem;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.order.ApiCompSouOrderItemVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouCurrency;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 竞价 - 供应商报价服务
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/15
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouOrderDomainServiceImpl implements CompSouOrderDomainService {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private CompSouProjectDAOImpl compSouProjectDao;
    @Autowired
    private SouCurrencyDAOImpl souCurrencyDao;
    @Autowired
    private CompSouBaseMaterialPriceDAOImpl compSouBaseMaterialPriceDao;
    @Autowired
    private CompSouItemDAOImpl compSouItemDao;
    @Autowired
    private CompSouCurrencyDAOImpl compSouCurrencyDao;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private PricingFormulaCalculateClient pricingFormulaCalculateClient;

    /**
     * 计算公式报价
     *
     * @param projectId        询价单ID
     * @param orderItemList    公式报价信息
     * @param availableItemMap 当前供应商在当前轮次的可报价物料(可为空)
     * @param currencyMap      可用外币信息(可为空)
     * @param vendorId         供应商ID
     */
    @Override
    public void computeFormulaPrice(long projectId,
                                    List<ApiCompSouOrderItemVO> orderItemList,
                                    @Nullable Map<Long/* suoItemId */, SouItem> availableItemMap,
                                    @Nullable Map<String/* currencyCode */, SouCurrency> currencyMap,
                                    long vendorId) {
        if (orderItemList.isEmpty()) {
            return;
        }

        SouProject souProject = souProjectDao.getById(projectId);
        CompSouProject compProject = compSouProjectDao.getById(projectId);
        if (!SouOrderTypeEnum.FORMULA.equals(souProject.getOrderType())) {
            return;
        }
        if (orderItemList.isEmpty()){
            return;
        }
        orderItemList.forEach(orderItem -> {
            AssertUtils.notNull(orderItem.getSouItemId(), "缺少souItemId参数");
            AssertUtils.notEmpty(orderItem.getFormulaResult(), "缺少formulaResult参数");
            AssertUtils.notNull(orderItem.getOrderCurrency(), "请选择币种");
        });
        /* 获取当前轮次供应商的可报价物料信息 */
        if (availableItemMap == null) {
            availableItemMap = SouActiveBeanUtils.getActiveBean(SouTypeEnum.comp.name(), ApiSouOrderQueryHandler.class).getAvailableItemsForVendor(
                    projectId, souProject.getCurrentRound(), vendorId)
                    .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
        }
        /* 获取询价单上设定的可报价外币币种信息(特别是"报价精确度") */
        if (currencyMap == null) {
            currencyMap = souCurrencyDao.lambdaQuery()
                    .eq(SouCurrency::getProjectId, projectId)
                    .list().stream()
                    .collect(Collectors.toMap(SouCurrency::getCurrencyCode, Function.identity()));
        }
        /* 基材价格 */
        List<CompSouBaseMaterialPrice> baseMaterialPriceList = compSouBaseMaterialPriceDao.lambdaQuery()
                .eq(CompSouBaseMaterialPrice::getProjectId, projectId)
                .eq(CompSouBaseMaterialPrice::getRound, souProject.getCurrentRound())
                .list();
        /* 查询汇率 */
        Map<String/* fromCurrency_toCurrency */, BigDecimal> exchangeRateMap;
        if (baseMaterialPriceList.isEmpty()) {
            exchangeRateMap = Collections.emptyMap();
        } else {
            PurchaseExchangeRateQueryDTO queryParam = new PurchaseExchangeRateQueryDTO();
            queryParam.setExchangeDate(compProject.getCurrencyExchangeDate());
            //必须包含汇率类型
            queryParam.setRateType(compProject.getExchangeRateType());
            Map<String/* fromCurrency */, List<PurchaseExchangeRate>> exchangeRateInfos = baseClient.listExchangeRateByFromCurrencies(queryParam);
            exchangeRateMap = new HashMap<>(exchangeRateInfos.size() << 2);
            exchangeRateInfos.forEach((fromCurrency, exchangeRates) ->
                    exchangeRates.forEach(exchangeRate ->
                            exchangeRateMap.put(fromCurrency + "_" + exchangeRate.getToCurrencyCode(), exchangeRate.getPriceTax()))
            );
        }

        Map<Long/* souItemId */, CompSouItem> compItemMap = compSouItemDao.lambdaQuery()
                .in(CompSouItem::getSouItemId, orderItemList.stream().map(ApiCompSouOrderItemVO::getSouItemId).collect(Collectors.toSet()))
                .list()
                .stream().collect(Collectors.toMap(CompSouItem::getSouItemId, Function.identity()));
        for (ApiCompSouOrderItemVO orderItem : orderItemList) {
            CompSouItem compItem = compItemMap.get(orderItem.getSouItemId());
            if (SouOrderTypeEnum.FORMULA.equals(souProject.getOrderType())) {
                PricingFormulaCalculateDTO calculateParam = new PricingFormulaCalculateDTO();
                calculateParam.setRelateId(compItem.getMaterialFormulaRelateId());
                calculateParam.setFormulaAttrValues(orderItem.getFormulaResult());
                Map<Long/* factorId */, BigDecimal> baseMaterialPriceMap = new HashMap<>(baseMaterialPriceList.size());
                baseMaterialPriceList.forEach(baseMaterialPrice -> {
                    BigDecimal exchangeRate;
                    if (baseMaterialPrice.getCurrencyType().equals(orderItem.getOrderCurrency())) {
                        exchangeRate = BigDecimal.ONE; /* 来源/目标币种一样 */
                    } else {
                        exchangeRate = exchangeRateMap.get(baseMaterialPrice.getCurrencyType() + "_" + orderItem.getOrderCurrency());
                        AssertUtils.notNull(exchangeRate, LocaleHandler.getLocaleMsg("基材[{0}]使用了[{1}]，找不到转换为[{2}]的汇率信息"),
                                baseMaterialPrice.getBaseMaterialName(),
                                baseMaterialPrice.getCurrencyType(),
                                orderItem.getOrderCurrency());
                    }

                    baseMaterialPriceMap.put(baseMaterialPrice.getFactorId(),
                            baseMaterialPrice.getBaseMaterialPrice().multiply(exchangeRate).setScale(10, RoundingMode.HALF_UP));
                });
                calculateParam.setBaseMaterialPriceMap(baseMaterialPriceMap);

                orderItem.setOrderNotaxPrice(pricingFormulaCalculateClient.computeFormula(calculateParam)
                        .setScale(currencyMap.get(orderItem.getOrderCurrency()).getPricePrecision(), RoundingMode.HALF_UP));
            }
        }
    }

    /**
     * 计算原币含税单价、本币含税/未税单价
     *
     * @param projectId     寻源单ID
     * @param orderItemList 报价信息
     * @param souProject    寻源单(可为空)
     * @param taxMap        税率信息(可为空)
     */
    @Override
    public void computeTaxPriceAndStandardPrice(long projectId,
                                                List<ApiCompSouOrderItemVO> orderItemList,
                                                @Nullable SouProject souProject,
                                                @Nullable Map<String/* taxKey */, BigDecimal> taxMap) {
        if (orderItemList.isEmpty()) {
            return;
        }
        orderItemList.forEach(quoteItem -> {
            AssertUtils.notNull(quoteItem.getTaxKey(), "请选择税率");
            AssertUtils.notNull(quoteItem.getOrderCurrency(), "请选择币种");
        });
        if (souProject == null) {
            souProject = souProjectDao.getById(projectId);
        }
        if (taxMap == null) {
            taxMap = baseClient.listTaxAll().stream()
                    .collect(Collectors.toMap(PurchaseTax::getTaxKey, PurchaseTax::getTaxCode, (a, b) -> a));
        }
        Map<String/* currencyCode */, CompSouCurrency> compCurrencyMap = compSouCurrencyDao.lambdaQuery()
                .eq(CompSouCurrency::getProjectId, projectId)
                .list()
                .stream().collect(Collectors.toMap(CompSouCurrency::getCurrencyCode, Function.identity()));

        /* 9. 根据报价+税率信息，换算得到供应商的含税报价 */
        BigDecimal tax;
        CompSouCurrency currencyInfo;
        for (ApiCompSouOrderItemVO orderItem : orderItemList) {
            tax = taxMap.get(orderItem.getTaxKey());
            currencyInfo = compCurrencyMap.get(orderItem.getOrderCurrency());
            /* 含税报价 = 未税报价 * (1 + 税率/100)；并且保留位数为外币的设定精确度 */
            orderItem.setOrderTaxPrice(orderItem.getOrderNotaxPrice()
                    .multiply(BigDecimal.ONE.add(tax.divide(new BigDecimal(100), 8, RoundingMode.HALF_UP)))
                    .setScale(currencyInfo.getPricePrecision(), RoundingMode.HALF_UP));
        }
        /* 10. 计算本币未税/含税报价 */
        for (ApiCompSouOrderItemVO orderItem : orderItemList) {
            BigDecimal exchangeRate;
            if (souProject.getStandardCurrency().equals(orderItem.getOrderCurrency())) {
                exchangeRate = BigDecimal.ONE;
            } else {
                CompSouCurrency compCurrency = compCurrencyMap.get(orderItem.getOrderCurrency());
                AssertUtils.notNull(compCurrency, LocaleHandler.getLocaleMsg("找不到[{0} -> {1}]的汇率"), orderItem.getOrderCurrency(),
                        souProject.getCurrentRound());
                exchangeRate = compCurrency.getPriceTax();
            }

            /* 本币未税报价 */
            orderItem.setStandardNotaxPrice(orderItem.getOrderNotaxPrice().multiply(exchangeRate)
                    .setScale(souProject.getPricePrecision(), RoundingMode.HALF_UP));
            /* 本币含税报价 */
            orderItem.setStandardTaxPrice(orderItem.getOrderTaxPrice().multiply(exchangeRate)
                    .setScale(souProject.getPricePrecision(), RoundingMode.HALF_UP));
        }
    }

}
