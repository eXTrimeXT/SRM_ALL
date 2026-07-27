package com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.editorder;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.order.dao.SouOrderItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.ISouSpiBean;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.SouActiveBeanUtils;
import com.midea.cloud.srm.biz.pj.sou.sourcing.spi.order.ApiSouOrderQueryHandler;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.bid.quotetemplate.vo.SouQuoteTempAttrTableColumnVO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouProjectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderFileDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.order.ApiSouOrderItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.model.supplier.risk.dto.MonitoringDTO;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源openAPI - 报价校验转换处理
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/01
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ApiSouOrderEditHandler implements ISouSpiBean {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private SouCurrencyDAOImpl souCurrencyDao;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private SouItemLadderDAOImpl souItemLadderDao;
    @Autowired
    private SouFileConfigDAOImpl souFileConfigDao;
    @Autowired
    private SouOrderDAOImpl souOrderDao;
    @Autowired
    private SouOrderItemDAOImpl souOrderItemDao;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private SupplierClient supplierClient;
    @Autowired
    private ISouQuoteTempService souQuoteTempService;

    /**
     * 供应商报价数据处理
     *
     * @param param   供应商报价信息
     * @param souType 寻源类型{@link SouTypeEnum}
     */
    public SouOrderEditPO formatValidateAndConvert(ApiSouOrderDTO param, String souType) {
        SouOrderEditPO po = new SouOrderEditPO();

        //构造业务所需的上下文数据，并保存到上下文中
        SouOrderDtoContext.setContextHolder(this.buildContextData(param));
        try {
            //基本信息
            this.formatParams(param, param.isTempSave());
            //数据过滤及校验、处理
            this.filterAndValidateItems(param.getOrderItemList(), param.isTempSave());
            //处理报价附件
            //校验完后进行的额外处理
            this.doExtraValidation(param, param.isTempSave());
            //转换得到报价单头信息
            this.doConvertSouOrder(po, param, param.isTempSave());
            //转换得到报价行信息(以及报价单行关联的额外信息)
            this.doConvertOrderItems(po, param.getOrderItemList(), param.isTempSave());
            //完成转化后的额外处理
            /*this.doExtraConvert(po, param, param.getIsTempSave());*/

            return po;
        } finally {
            // 4: 清除业务上下文
            SouOrderDtoContext.remove();
        }
    }

    public SouOrderEditPO formatAndConvert(ApiSouOrderDTO param) {
        SouOrderEditPO po = new SouOrderEditPO();

        // 1: 构造业务所需的上下文数据，并保存到上下文中
        SouOrderDtoContext.setContextHolder(this.buildContextDataByInit(param));
        try {
            // 2: 基本信息
            this.formatParams(param, param.isTempSave());
            // 3: 转换得到报价单头信息
            this.doConvertSouOrderByInit(po, param, param.isTempSave());
            // 4: 转换得到报价行信息(以及报价单行关联的额外信息)
            this.doConvertOrderItemsByInit(po, param.getOrderItemList(), param.isTempSave());
            return po;
        } finally {
            // 4: 清除业务上下文
            SouOrderDtoContext.remove();
        }
    }

    @ApiOperation("获取币种转换汇率信息(由其他方法实现)")
    protected Map<String/* fromCurrency_toCurrency */, BigDecimal> getExchangeRateMap(Set<String> fromCurrencies, String toCurrency, SouProject souProject) {
        return Collections.emptyMap();
    }

    @ApiOperation("构造业务所需的上下文数据")
    protected SouOrderDtoContext buildContextData(ApiSouOrderDTO param) {
        // 1: 查询询价单
        SouProject souProject = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(souProject, LocaleHandler.getLocaleMsg("寻源单") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        // 2: 查询供应商可报价的物料需求信息
        Map<Long/* souItemId */, SouItem> availableItemMap = SouActiveBeanUtils.getActiveBean(souProject.getSouType(), ApiSouOrderQueryHandler.class)
                .getAvailableItemsForVendor(param.getProjectId(), souProject.getCurrentRound(),
                        param.getVendorId()).stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));

        // 3: 查询供应商可报价币种信息
        Map<String /*currencyCode*/ , SouCurrency> currencyMap = souCurrencyDao.lambdaQuery()
                .eq(SouCurrency::getProjectId, param.getProjectId())
                .list().stream()
                .collect(Collectors.toMap(SouCurrency::getCurrencyCode, Function.identity()));


        // 6: 查询当前轮次已存在的报价单
        SouOrder existOrder = souOrderDao.lambdaQuery()
                .eq(SouOrder::getProjectId, param.getProjectId())
                .eq(SouOrder::getRound, souProject.getCurrentRound())
                .eq(SouOrder::getVendorId, param.getVendorId())
                .one();

        // 9: 构造业务上下文
        SouOrderDtoContext context = new SouOrderDtoContext();
        context.setVendorId(param.getVendorId());
        context.setSouProject(souProject);
        context.setAvailableItemMap(availableItemMap);
        context.setCurrencyMap(currencyMap);
        context.setTaxMap(null);
        context.setExistOrder(existOrder);
        return context;
    }


    @ApiOperation("构造业务所需的上下文数据")
    protected SouOrderDtoContext buildContextDataByInit(ApiSouOrderDTO param) {
        // 1: 查询询价单
        SouProject souProject = souProjectDao.getById(param.getProjectId());
        AssertUtils.notNull(souProject, LocaleHandler.getLocaleMsg("寻源单") + "[{0}]" + LocaleHandler.getLocaleMsg("不存在"), param.getProjectId());
        // 2: 查询供应商可报价的物料需求信息
        Map<Long/* souItemId */, SouItem> availableItemMap = SouActiveBeanUtils.getActiveBean(souProject.getSouType(), ApiSouOrderQueryHandler.class)
                .getValidItemsInSpecifiedRound(param.getProjectId(), souProject.getCurrentRound()
                ).stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));

        // 9: 构造业务上下文
        SouOrderDtoContext context = new SouOrderDtoContext();
        context.setVendorId(param.getVendorId());
        context.setSouProject(souProject);
        context.setAvailableItemMap(availableItemMap);
        context.setTaxMap(null);
        return context;
    }

    @ApiOperation("入参格式化")
    protected void formatParams(ApiSouOrderDTO param, boolean isTempSave) {
        // 1: 报价单号生成规则
        param.setOrderNoGenerateCode(StringUtils.trimToNull(param.getOrderNoGenerateCode()));
        AssertUtils.notNull(param.getOrderNoGenerateCode(), "缺少orderNoGenerateCode参数，无法生成报价单号");
        // 2: 代理信息
        // 2.1: 是否代理报价
        if (param.getIsProxy() == null) {
            param.setIsProxy(Enable.N);
        }
        // 2.2: 代理授权文件
        if (!Enable.Y.equals(param.getIsProxy())) {
            param.setProxyDocId(null);
            param.setProxyFileName(null);
        } else {
            param.setProxyFileName(StringUtils.trimToNull(param.getProxyFileName()));
            AssertUtils.isTrue(param.getProxyFileName() == null || param.getProxyFileName().length() <= 150,
                    "代理授权文件名称长度能超过150");
        }
        // 2.3: 代理授权说明
        if (!Enable.Y.equals(param.getIsProxy())) {
            param.setProxyRemark(null);
        } else {
            param.setProxyRemark(StringUtils.trimToNull(param.getProxyRemark()));
            AssertUtils.isTrue(param.getProxyRemark() == null || param.getProxyRemark().length() <= 300,
                    "代理授权说明长度不能超过300");
        }
        // 3: 提交人信息(需要填写，这里不给值)
        if (isTempSave) {
            param.setSubmitById(null);
            param.setSubmitBy(null);
            param.setSubmitByIp(null);
            param.setSubmitFullName(null);
        } else {
            AssertUtils.notNull(param.getSubmitById(), "缺少submitById参数");
            param.setSubmitBy(StringUtils.trimToNull(param.getSubmitBy()));
            AssertUtils.notNull(param.getSubmitBy(), "缺少submitBy参数");
            param.setSubmitFullName(StringUtils.trimToNull(param.getSubmitFullName()));
            AssertUtils.notNull(param.getSubmitFullName(), "缺少submitFullName参数");
            param.setSubmitByIp(StringUtils.trimToNull(param.getSubmitByIp()));
            AssertUtils.notNull(param.getSubmitByIp(), "缺少submitByIp参数");
        }
    }

    @ApiOperation("数据格式化及校验")
    protected void filterAndValidateItems(List<ApiSouOrderItemDTO> orderItemList, boolean isTempSave) {
        int index = 0;
        Iterator<ApiSouOrderItemDTO> iterator = orderItemList.iterator();
        ApiSouOrderItemDTO orderItem;
        SouItem availableItem;
        while (iterator.hasNext()) {
            index++;
            orderItem = iterator.next();
            availableItem = souItemDao.getById(orderItem.getSouItemId());

            // 2: 数据校验
            // 2.1: 校验基本字段
            this.validateBasicQuoteItemFields(index, availableItem, orderItem, isTempSave);
            // 2.3: 额外数据处理
            this.doExtraValidationForItem(index, availableItem, orderItem, isTempSave);
        }
        // 3: 判断是否允许供应商仅对部分物料报价
        this.validateDataMatch(orderItemList);
    }

    @ApiOperation("校验报价附件")
    protected void filterAndValidateOrderFiles(@Nullable List<ApiSouOrderFileDTO> orderFileList, boolean isTempSave) {
        Map<Long/* fileConfigId */, SouFileConfig> fileConfigMap = SouOrderDtoContext.getContextHolder().getFileConfigMap();
        if (CollectionUtils.isEmpty(orderFileList)) {
            if (!fileConfigMap.isEmpty()) {
                AssertUtils.isTrue(isTempSave, "请上传报价附件信息");
            }
            return;
        }

        int index = 0;
        for (ApiSouOrderFileDTO orderFile : orderFileList) {
            index++;
            // 1: 供方必须上传附件ID
            if (orderFile.getSouFileConfigId() != null) {
                AssertUtils.isTrue(fileConfigMap.containsKey(orderFile.getSouFileConfigId()), LocaleHandler.getLocaleMsg("报价附件第") + "{0}" + LocaleHandler.getLocaleMsg("行数据错误，找不到关联的附件要求信息") + "[{1}]",
                        index, orderFile.getSouFileConfigId());
            }
            // 2: 供应商报价附件表ID(略)
            // 3: 供应商报价附件ID
            AssertUtils.isTrue(isTempSave || orderFile.getOrderDocId() != null, LocaleHandler.getLocaleMsg("报价附件第") + "{0}" + LocaleHandler.getLocaleMsg("行请上传附件"), index);
            // 4: 供应商报价附件名称
            orderFile.setOrderFileName(StringUtils.trimToNull(orderFile.getOrderFileName()));
            if (orderFile.getOrderFileName() == null) {
                AssertUtils.isTrue(isTempSave, LocaleHandler.getLocaleMsg("报价附件第") + "{0}" + LocaleHandler.getLocaleMsg("行请上传附件"), index);
            } else {
                AssertUtils.isTrue(orderFile.getOrderFileName().length() <= 150, LocaleHandler.getLocaleMsg("报价附件第") + "{0}" + LocaleHandler.getLocaleMsg("行附件名称长度不能超过150"), index);
            }
            // 5: 备注
            orderFile.setOrderRemark(StringUtils.trimToNull(orderFile.getOrderRemark()));
            AssertUtils.isTrue(orderFile.getOrderRemark() == null || orderFile.getOrderRemark().length() <= 300,
                    "报价附件第{0}行备注的输入长度不能超过300", index);
        }

        // 确保所有附件要求均已上传
        if (!isTempSave) {
            Set<Long> submitOrderFileIds = orderFileList.stream().map(ApiSouOrderFileDTO::getSouFileConfigId).collect(Collectors.toSet());
            fileConfigMap.forEach((fileConfigId, fileConfig) ->
                    AssertUtils.isTrue(submitOrderFileIds.contains(fileConfigId), LocaleHandler.getLocaleMsg("请上传") + "[{0}]" + LocaleHandler.getLocaleMsg("相关附件"), fileConfig.getFileRequire()));
        }
    }

    @ApiOperation("对于用户提交的某条报价数据，是否要将其过滤掉")
    protected boolean needRemove(ApiSouOrderItemDTO orderItem) {
        // 通过 souItemId 定位供应商对哪个需求物料进行报价，如果定位不到，这行数据就改被过滤掉
        if (orderItem.getSouItemId() == null) {
            return true;
        }
        SouProject project = SouOrderDtoContext.getContextHolder().getSouProject();
        SouItem availableItem = souItemDao.getById(orderItem.getSouItemId());
        if (availableItem == null) {
            return true;
        }

        return Enable.N.equals(project.getIsPriceNotax()) ? (orderItem.getOrderNotaxPrice() == null) : (orderItem.getOrderTaxPrice() == null);
    }

    @ApiOperation("校验报价行基本字段")
    protected void validateBasicQuoteItemFields(int index, SouItem availableItem, ApiSouOrderItemDTO orderItem, boolean isTempSave) {
        // 3: 价格有效期从
        if (orderItem.getPriceStartTime() != null) {
            orderItem.setPriceStartTime(ApiSouProjectQueryDTO.getStartTimeOfDate(orderItem.getPriceStartTime()));
        }
        // 4: 价格有效期到
        if (orderItem.getPriceEndTime() != null) {
            orderItem.setPriceEndTime(ApiSouProjectQueryDTO.getEndTimeOfDay(orderItem.getPriceEndTime()));
            if (orderItem.getPriceStartTime() != null) {
                AssertUtils.isTrue(orderItem.getPriceStartTime().before(orderItem.getPriceEndTime()), "价格有效期开始时间必须早于价格有效期截止时间");
            }
        }
        // 5: 备注
        orderItem.setOrderRemark(StringUtils.trimToNull(orderItem.getOrderRemark()));
        if (orderItem.getOrderRemark() != null) {
            AssertUtils.isTrue(orderItem.getOrderRemark().length() <= 300, "报价备注的输入长度不能超过300");
        }
    }

    @ApiOperation("计算供应商原币未税/含税单价")
    protected void computePrice(int index, SouItem availableItem, ApiSouOrderItemDTO orderItem, boolean isTempSave) {
        if (SouOrderTypeEnum.SIMPLE.equals(SouOrderDtoContext.getContextHolder().getSouProject().getOrderType())) {
            // 1: 普通报价
            this.computePriceForSimple(index, availableItem, orderItem);
        } else if (SouOrderTypeEnum.MATERIAL_COST_SEPARATION.equals(SouOrderDtoContext.getContextHolder().getSouProject().getOrderType())) {
            // 2: 料费分离报价
            this.computePriceForTemplate(index, availableItem, orderItem);
        }

        int pricePrecision = 4;
        boolean isPriceNotax = Enable.Y.equals(SouOrderDtoContext.getContextHolder().getSouProject().getIsPriceNotax());
        if (isPriceNotax) {
            // 原币含税单价(未税报价 * (1 + 税率/100)；并且保留位数为外币的设定精确度)
            if (orderItem.getTaxRate() != null) {
                orderItem.setOrderTaxPrice(orderItem.getOrderNotaxPrice()
                        .multiply(BigDecimal.ONE.add(orderItem.getTaxRate().divide(new BigDecimal(100), 8, RoundingMode.HALF_UP)))
                        .setScale(pricePrecision, RoundingMode.HALF_UP));
            }
        } else {
            // 原币未税单价(含税报价 / (1 + 税率/100)；并且保留位数为外币的设定精确度)
            if (orderItem.getTaxRate() != null) {
                orderItem.setOrderNotaxPrice(orderItem.getOrderTaxPrice()
                        .divide(BigDecimal.ONE.add(orderItem.getTaxRate().divide(new BigDecimal(100), 8, RoundingMode.HALF_UP)), 8, RoundingMode.HALF_UP)
                        .setScale(pricePrecision, RoundingMode.HALF_UP));
            }
        }
    }

    @ApiOperation("普通报价(以及阶梯报价)")
    protected void computePriceForSimple(int index, SouItem availableItem, ApiSouOrderItemDTO orderItem) {
        // 价格精确度
        int pricePrecision = 4;
        boolean isPriceNotax = Enable.N.equals(SouOrderDtoContext.getContextHolder().getSouProject().getIsPriceNotax());

        if (!Enable.Y.equals(availableItem.getIsLadder())) {
            /* 1: 非阶梯报价 */
            if (isPriceNotax) {
                orderItem.setOrderNotaxPrice(orderItem.getOrderNotaxPrice().setScale(pricePrecision, RoundingMode.HALF_UP));
                /* 原币含税单价(未税报价 * (1 + 税率/100)；并且保留位数为外币的设定精确度) */
                if (orderItem.getTaxRate() != null) {
                    orderItem.setOrderTaxPrice(orderItem.getOrderNotaxPrice()
                            .multiply(BigDecimal.ONE.add(orderItem.getTaxRate().divide(new BigDecimal(100), 8, RoundingMode.HALF_UP)))
                            .setScale(pricePrecision, RoundingMode.HALF_UP));
                }
            } else {
                orderItem.setOrderTaxPrice(orderItem.getOrderTaxPrice().setScale(pricePrecision, RoundingMode.HALF_UP));
                /* 原币未税单价(含税报价 / (1 + 税率/100)；并且保留位数为外币的设定精确度) */
                if (orderItem.getTaxRate() != null) {
                    orderItem.setOrderNotaxPrice(orderItem.getOrderTaxPrice()
                            .divide(BigDecimal.ONE.add(orderItem.getTaxRate().divide(new BigDecimal(100), 8, RoundingMode.HALF_UP)), 8, RoundingMode.HALF_UP)
                            .setScale(pricePrecision, RoundingMode.HALF_UP));
                }
            }
        } else {
            /* 2: 阶梯报价 */
            AssertUtils.notEmpty(orderItem.getLadderPriceList(), LocaleHandler.getLocaleMsg("请填写报价第") + "{0}" + LocaleHandler.getLocaleMsg("行的阶梯价报价"), index);
            orderItem.getLadderPriceList().forEach(
                    ladderPrice ->
                            AssertUtils.notNull(ladderPrice.getSouItemLadderId(), "缺少souItemLadderId参数")
            );
            /* 2.1: 查询阶梯价模板信息 */
            Map<Long/* souItemLadderId */, SouItemLadder> ladderTemplates = SouOrderDtoContext.getContextHolder().getLadderTemplateMap()
                    .get(orderItem.getSouItemId());
            AssertUtils.notNull(ladderTemplates, LocaleHandler.getLocaleMsg("找不到物料需求") + "[{0}]" + LocaleHandler.getLocaleMsg("对应的阶梯价模板信息"), orderItem.getSouItemId());
            /* 2.2: 确保供应商填写了所有阶梯价区间的价格 */
            Map<Long/* souItemLadderId */, SouOrderItemHis> ladderPriceMap = orderItem.getLadderPriceList().stream()
                    .collect(Collectors.toMap(SouOrderItemHis::getOrderItemId, Function.identity()));
            orderItem.setOrderNotaxPrice(null);
            orderItem.setOrderTaxPrice(null);
            for (SouItemLadder ladderTemplate : ladderTemplates.values()) {
                String sectionMsg = MessageFormat.format("[{0}~{1}]区间的阶梯价",
                        ladderTemplate.getBeginQuantity().stripTrailingZeros().toPlainString(),
                        ladderTemplate.getEndQuantity() != null ? ladderTemplate.getEndQuantity().stripTrailingZeros().toPlainString() : "");

                SouOrderItemHis ladderPrice = ladderPriceMap.get(ladderTemplate.getSouItemLadderId());
                AssertUtils.notNull(ladderPrice, LocaleHandler.getLocaleMsg("报价第") + "{0}" + LocaleHandler.getLocaleMsg("行请填写") + "{1}", index, sectionMsg);
                if (isPriceNotax) {
                    AssertUtils.notNull(ladderPrice.getOrderNotaxPrice(), LocaleHandler.getLocaleMsg("报价第") + "{0}" + LocaleHandler.getLocaleMsg("行请填写"), index, sectionMsg);
                    AssertUtils.isTrue(ladderPrice.getOrderNotaxPrice().compareTo(BigDecimal.ZERO) > 0, LocaleHandler.getLocaleMsg("报价第") + "{0}" + LocaleHandler.getLocaleMsg("行") + "{1}" + LocaleHandler.getLocaleMsg("必须大于0"), index, sectionMsg);
                } else {
                    AssertUtils.notNull(ladderPrice.getOrderTaxPrice(), LocaleHandler.getLocaleMsg("报价第") + "{0}" + LocaleHandler.getLocaleMsg("行请填写"), index, sectionMsg);
                    AssertUtils.isTrue(ladderPrice.getOrderTaxPrice().compareTo(BigDecimal.ZERO) > 0, LocaleHandler.getLocaleMsg("报价第") + "{0}" + LocaleHandler.getLocaleMsg("行") + "{1}" + LocaleHandler.getLocaleMsg("必须大于0"), index, sectionMsg);
                }

                if (orderItem.getTaxKey() != null) {
                    if (isPriceNotax) {
                        // 含税单价(未税报价 * (1 + 税率/100)；并且保留位数为外币的设定精确度)
                        ladderPrice.setOrderTaxPrice(ladderPrice.getOrderNotaxPrice()
                                .multiply(BigDecimal.ONE.add(SouOrderDtoContext.getContextHolder().getTaxMap().get(orderItem.getTaxKey())
                                        .divide(new BigDecimal(100), 8, RoundingMode.HALF_UP)))
                                .setScale(pricePrecision, RoundingMode.HALF_UP));
                    } else {
                        // 未税单价(含税报价 / (1 + 税率/100)；并且保留位数为外币的设定精确度)
                        ladderPrice.setOrderNotaxPrice(ladderPrice.getOrderTaxPrice()
                                .divide(BigDecimal.ONE.add(SouOrderDtoContext.getContextHolder().getTaxMap().get(orderItem.getTaxKey())
                                        .divide(new BigDecimal(100), 8, RoundingMode.HALF_UP)), 8, RoundingMode.HALF_UP)
                                .setScale(pricePrecision, RoundingMode.HALF_UP));
                    }
                }

                // 2.3: 将第一个区间的价格设置为原币未税/含税单价
                if (isPriceNotax) {
                    if (orderItem.getOrderNotaxPrice() == null) {
                        orderItem.setOrderNotaxPrice(ladderPrice.getOrderNotaxPrice().setScale(pricePrecision, RoundingMode.HALF_UP));
                    }
                } else {
                    if (orderItem.getOrderTaxPrice() == null) {
                        orderItem.setOrderTaxPrice(ladderPrice.getOrderTaxPrice().setScale(pricePrecision, RoundingMode.HALF_UP));
                    }
                }
            }
        }
    }

    @ApiOperation("模板报价")
    protected void computePriceForTemplate(int index, SouItem availableItem, ApiSouOrderItemDTO orderItem) {
        int pricePrecision = SouOrderDtoContext.getContextHolder().getCurrencyMap().get(orderItem.getOrderCurrency()).getPricePrecision();
        boolean isPriceNotax = Enable.Y.equals(SouOrderDtoContext.getContextHolder().getSouProject().getIsPriceNotax());

        Map<Long/* attrId */, List<Map<String/* fieldId */, Object>>> quoteData;
        {
            if (orderItem.getQuoteData() == null || orderItem.getQuoteData().isEmpty()) {
                quoteData = SouOrderDtoContext.getContextHolder().getQuoteDataMap().get(availableItem.getSouItemId());
                AssertUtils.notEmpty(quoteData, "请填写报价第{0}行的模板数据", index);
            } else {
                quoteData = orderItem.getQuoteData();
            }
        }
        // projectId_round_vendorId_souItemId
        String businessId = availableItem.getProjectId()
                + "_" + SouOrderDtoContext.getContextHolder().getSouProject().getCurrentRound()
                + "_" + SouOrderDtoContext.getContextHolder().getVendorId()
                + "_" + availableItem.getSouItemId();
        BigDecimal price = souQuoteTempService.computeTempData(SouOrderDtoContext.getContextHolder().getSouProject().getQuoteTempId(),
                        businessId, quoteData, true, false)
                .getPrice();
        if (price == null) {
            // 2种可能，1：没有明细报价，2：有上一轮的报价(需要将上一轮报价拿过来使用)
            String businessId2 = availableItem.getProjectId()
                    + "_" + (SouOrderDtoContext.getContextHolder().getSouProject().getCurrentRound() - 1)
                    + "_" + SouOrderDtoContext.getContextHolder().getVendorId()
                    + "_" + availableItem.getSouItemId();

            quoteData = souQuoteTempService.queryTempData(SouOrderDtoContext.getContextHolder().getSouProject().getQuoteTempId(), businessId2, true).getPriceData().getData();
            quoteData.values().forEach(rows -> rows.forEach(row -> {
                row.put(SouQuoteTempAttrTableColumnVO.TABLE_ID, null);
                row.put(SouQuoteTempAttrTableColumnVO.BUSINESS_ID, businessId);
            }));
            price = souQuoteTempService.computeTempData(SouOrderDtoContext.getContextHolder().getSouProject().getQuoteTempId(),
                            businessId, quoteData, true, false)
                    .getPrice();
            AssertUtils.notNull(price, "报价明细第{0}行请填写模板数据", index);
        }
        if (isPriceNotax) {
            orderItem.setOrderNotaxPrice(price.setScale(pricePrecision, RoundingMode.HALF_UP));
            // 原币含税单价(未税报价 * (1 + 税率/100)；并且保留位数为外币的设定精度)
            if (orderItem.getTaxRate() != null) {
                orderItem.setOrderTaxPrice(orderItem.getOrderNotaxPrice()
                        .multiply(BigDecimal.ONE.add(orderItem.getTaxRate().divide(new BigDecimal(100), 8, RoundingMode.HALF_UP)))
                        .setScale(pricePrecision, RoundingMode.HALF_UP));
            }
        } else {
            orderItem.setOrderTaxPrice(price.setScale(pricePrecision, RoundingMode.HALF_UP));
            // 原币未税单价(含税报价 / (1 + 税率/100)；并且保留位数为外币的设定精确度)
            if (orderItem.getTaxRate() != null) {
                orderItem.setOrderNotaxPrice(orderItem.getOrderTaxPrice()
                        .divide(BigDecimal.ONE.add(orderItem.getTaxRate().divide(new BigDecimal(100), 8, RoundingMode.HALF_UP)), 8, RoundingMode.HALF_UP)
                        .setScale(pricePrecision, RoundingMode.HALF_UP));
            }
        }
    }

    @ApiOperation("校验是否允许供应商仅对部分物料报价")
    protected void validateDataMatch(List<ApiSouOrderItemDTO> orderItemList) {
        Collection<SouItem> availableItemList = SouOrderDtoContext.getContextHolder().getAvailableItemMap().values();
        Map<Long/* souItemId */, ApiSouOrderItemDTO> orderItemMap = orderItemList.stream()
                .collect(Collectors.toMap(ApiSouOrderItemDTO::getSouItemId, e -> e));

        if (true) {
            // 1: 允许部分报价(但同一组合下必须全部报价)
            // 1.1: 将Item按「组合」分组(已过滤掉无组合数据，即key不能为null)
            Map<String/* itemGroup */, List<SouItem>> itemGroupMap = new HashMap<>(availableItemList.size());
            availableItemList.forEach(item -> {
                if (item.getItemGroup() != null) {
                    // 过滤掉无组合数据
                    List<SouItem> items = itemGroupMap.computeIfAbsent(item.getItemGroup(), k -> new ArrayList<>(4));
                    items.add(item);
                }
            });

            // 1.2: 循环判断同一组合是否存在部分报价的情况
            itemGroupMap.forEach((group, items) -> {
                int matchCount = 0;
                for (SouItem item : items) {
                    if (orderItemMap.containsKey(item.getSouItemId())) {
                        matchCount++;
                    }
                }
                Assert.isTrue(matchCount == 0 || matchCount == items.size(),
                        MessageFormat.format("组合[{0}]下的物料不允许部分报价", group));
            });
        } else {
            // 2: 所有询价物料均要报价
            ApiSouOrderItemDTO dto;
            for (SouItem item : availableItemList) {
                dto = orderItemMap.get(item.getSouItemId());
                Assert.notNull(dto, MessageFormat.format("缺少对物料[{0}]的报价", item.getItemDesc()));
            }
        }
    }

    @ApiOperation("用于额外的数据处理(针对物料报价信息)")
    protected void doExtraValidationForItem(int index, SouItem availableItem, ApiSouOrderItemDTO orderItem, boolean isTempSave) {
    }

    @ApiOperation("校验完后的额外处理")
    protected void doExtraValidation(ApiSouOrderDTO param, boolean isTempSave) {
    }

    @ApiOperation("转换得到报价头信息")
    protected void doConvertSouOrder(SouOrderEditPO po, ApiSouOrderDTO param, boolean isTempSave) {
        SouProject souProject = SouOrderDtoContext.getContextHolder().getSouProject();
        SouOrder entity = SouOrderDtoContext.getContextHolder().getExistOrder();
        if (entity == null) {
            entity = new SouOrder();
            // 报价单ID
            entity.setOrderId(IdGenrator.generate());
            // 报价单号
            entity.setOrderNo(baseClient.seqGen(param.getOrderNoGenerateCode()));
            AssertUtils.notNull(entity.getOrderNo(), LocaleHandler.getLocaleMsg("报价单号生成规则") + "[{0}]" + LocaleHandler.getLocaleMsg("错误，无法拿到单号"), param.getOrderNoGenerateCode());
            // 寻源单ID
            entity.setProjectId(souProject.getProjectId());
            // 供应商ID
            entity.setVendorId(SouOrderDtoContext.getContextHolder().getVendorId());
            // 报价轮次
            entity.setRound(souProject.getCurrentRound());
            //报价次数
            entity.setOrderRound(0);
        }
        // 状态
        entity.setOrderStatus(SouOrderStatusEnum.SUBMISSION);
        // 报价次数
        entity.setOrderRound(entity.getOrderRound() + 1);
        // 提交信息
        entity.setSubmitBy(isTempSave ? null : param.getSubmitBy());
        entity.setSubmitFullName(isTempSave ? null : param.getSubmitFullName());
        entity.setSubmitByIp(isTempSave ? null : param.getSubmitByIp());
        entity.setSubmitTime(isTempSave ? null : new Date());
        // 代理信息
        entity.setIsProxy(param.getIsProxy());
        entity.setProxyDocId(param.getProxyDocId());
        entity.setProxyFileName(param.getProxyFileName());
        entity.setProxyRemark(param.getProxyRemark());


        po.setSouOrder(entity);

        SouObjectXUtil.mergeProperties(entity, param);
    }

    @ApiOperation("转换得到报价行信息(以及阶梯价、账期)")
    protected void doConvertOrderItems(SouOrderEditPO po, List<ApiSouOrderItemDTO> dtos, boolean isTempSave) {
        SouOrderItem orderItem;
        for (ApiSouOrderItemDTO dto : dtos) {
            SouItem availableItem = souItemDao.getById(dto.getSouItemId());
            if (availableItem == null) {
                // 无该物料报价权限
                continue;
            }
            // 1: 报价信息
            orderItem = this.doConvertOrderItem(po, availableItem, dto, isTempSave);
            // 2: 历史报价
            this.doConvertOrderItemHis(po, availableItem, dto, isTempSave);
            // 3: 额外处理，校验
            this.doExtraConvertForOrderItem(po, availableItem, orderItem, dto, isTempSave);
        }
    }


    @ApiOperation("转换处理单个报价行")
    protected SouOrderItem doConvertOrderItem(SouOrderEditPO po, SouItem availableItem, ApiSouOrderItemDTO dto, boolean isTempSave) {

        SouProject souProject = SouOrderDtoContext.getContextHolder().getSouProject();
        SouOrder souOrder = po.getSouOrder();
        SouOrderItem orderItem = new SouOrderItem();

        // ID
        orderItem.setOrderItemId(IdGenrator.generate());
        BeanUtils.copyProperties(availableItem, orderItem);
        // 寻源单ID
        orderItem.setProjectId(souProject.getProjectId());
        // 物料需求ID
        orderItem.setSouItemId(availableItem.getSouItemId());
        // 供应商ID
        orderItem.setVendorId(souOrder.getVendorId());
        // 报价单ID
        orderItem.setOrderId(souOrder.getOrderId());
        // 报价次数
        orderItem.setOrderRound((souOrder.getOrderRound()));
        // 报价单状态
        orderItem.setOrderStatus(souOrder.getOrderStatus());
        // 是否有效报价
        orderItem.setIsValid(Enable.Y);
        // 是否代理报价
        orderItem.setIsProxy(po.getSouOrder().getIsProxy());
        // 供应商报价币种
        orderItem.setOrderCurrency(dto.getOrderCurrency());
        // 税率编码
        orderItem.setTaxKey(dto.getTaxKey());

        // 原币含税单价(未税报价 * (1 + 税率/100)；并且保留位数为外币的设定精确度)
        if (ObjectUtils.isEmpty(dto.getOrderTaxPrice()) || dto.getOrderTaxPrice().compareTo(BigDecimal.valueOf(0)) == 0) {
            orderItem.setOrderTaxPrice(dto.getOrderNowPrice());
        } else {
            orderItem.setOrderTaxPrice(dto.getOrderNowPrice());
        }
        //本次报价
        orderItem.setOrderNowPrice(dto.getOrderNowPrice());

        // 本轮入围情况
        orderItem.setWinStatus(SouWinStatusEnum.D);

        SouObjectXUtil.mergeProperties(orderItem, dto);
        po.getOrderItemList().add(orderItem);

        return orderItem;
    }

    @ApiOperation("转换处理单个报价行")
    protected void doConvertOrderItemHis(SouOrderEditPO po, SouItem availableItem, ApiSouOrderItemDTO dto, boolean isTempSave) {
        SouProject souProject = SouOrderDtoContext.getContextHolder().getSouProject();
        SouOrder souOrder = po.getSouOrder();
        SouOrderItemHis orderItem = new SouOrderItemHis();

        // SouOrderItemHis
        orderItem.setOrderItemId(IdGenrator.generate());
        BeanUtils.copyProperties(availableItem, orderItem);
        // 寻源单ID
        orderItem.setProjectId(souProject.getProjectId());
        // 物料需求ID
        orderItem.setSouItemId(availableItem.getSouItemId());
        // 供应商ID
        orderItem.setVendorId(souOrder.getVendorId());
        // 报价单ID
        orderItem.setOrderId(souOrder.getOrderId());
        // 报价次数
        orderItem.setOrder_round(BigDecimal.valueOf(souOrder.getOrderRound()));
        // 报价单状态
        orderItem.setOrderStatus(souOrder.getOrderStatus());
        // 是否有效报价
        orderItem.setIsValid(Enable.Y);
        // 是否代理报价
        orderItem.setIsProxy(po.getSouOrder().getIsProxy());
        // 供应商报价币种
        orderItem.setOrderCurrency(dto.getOrderCurrency());
        // 税率编码
        orderItem.setTaxKey(dto.getTaxKey());

        // 原币含税单价(未税报价 * (1 + 税率/100)；并且保留位数为外币的设定精确度)
        if (ObjectUtils.isEmpty(dto.getOrderTaxPrice()) || dto.getOrderTaxPrice().compareTo(BigDecimal.valueOf(0)) == 0) {
            orderItem.setOrderTaxPrice(dto.getOrderNowPrice());
        } else {
            orderItem.setOrderTaxPrice(dto.getOrderNowPrice());
        }
        //本次报价
        orderItem.setOrderNowPrice(dto.getOrderNowPrice());

        // 本轮入围情况
        orderItem.setWinStatus(SouWinStatusEnum.D);

        SouObjectXUtil.mergeProperties(orderItem, dto);
        po.getHisPriceList().add(orderItem);
    }

    @ApiOperation("转换得到报价头信息")
    protected void doConvertSouOrderByInit(SouOrderEditPO po, ApiSouOrderDTO param, boolean isTempSave) {
        SouProject souProject = SouOrderDtoContext.getContextHolder().getSouProject();
        SouOrder entity = SouOrderDtoContext.getContextHolder().getExistOrder();
        if (entity == null) {
            entity = new SouOrder();
            // 报价单ID
            entity.setOrderId(IdGenrator.generate());
            // 报价单号
            entity.setOrderNo(baseClient.seqGen(param.getOrderNoGenerateCode()));
            AssertUtils.notNull(entity.getOrderNo(), LocaleHandler.getLocaleMsg("报价单号生成规则") + "[{0}]" + LocaleHandler.getLocaleMsg("错误，无法拿到单号"), param.getOrderNoGenerateCode());
            // 寻源单ID
            entity.setProjectId(souProject.getProjectId());
            // 供应商ID
            entity.setVendorId(SouOrderDtoContext.getContextHolder().getVendorId());
            // 报价轮次
            entity.setRound(souProject.getCurrentRound());
            //报价次数
            entity.setOrderRound(0);
        }
        // 状态
        entity.setOrderStatus(SouOrderStatusEnum.DRAFT);
        // 提交信息
        entity.setSubmitBy(isTempSave ? null : param.getSubmitBy());
        entity.setSubmitFullName(isTempSave ? null : param.getSubmitFullName());
        entity.setSubmitByIp(isTempSave ? null : param.getSubmitByIp());
        entity.setSubmitTime(isTempSave ? null : new Date());
        // 代理信息
        entity.setIsProxy(param.getIsProxy());
        entity.setProxyDocId(param.getProxyDocId());
        entity.setProxyFileName(param.getProxyFileName());
        entity.setProxyRemark(param.getProxyRemark());
        po.setSouOrder(entity);

        SouObjectXUtil.mergeProperties(entity, param);
    }


    @ApiOperation("转换得到报价行信息(以及阶梯价、账期)")
    protected void doConvertOrderItemsByInit(SouOrderEditPO po, List<ApiSouOrderItemDTO> dtos, boolean isTempSave) {
        SouOrderItem orderItem;
        for (ApiSouOrderItemDTO dto : dtos) {
            SouItem availableItem = souItemDao.getById(dto.getSouItemId());
            if (availableItem == null) {
                // 无该物料报价权限
                continue;
            }
            // 1: 报价信息
            orderItem = this.doConvertOrderItemByInit(po, availableItem, dto, isTempSave);
        }
    }

    @ApiOperation("转换处理单个报价行")
    protected SouOrderItem doConvertOrderItemByInit(SouOrderEditPO po, SouItem availableItem, ApiSouOrderItemDTO dto, boolean isTempSave) {
        SouProject souProject = SouOrderDtoContext.getContextHolder().getSouProject();
        SouOrder souOrder = po.getSouOrder();
        SouOrderItem orderItem = new SouOrderItem();

        // ID
        orderItem.setOrderItemId(IdGenrator.generate());

        BeanUtils.copyProperties(availableItem, orderItem);
        // 寻源单ID
        orderItem.setProjectId(souProject.getProjectId());
        // 物料需求ID
        orderItem.setSouItemId(availableItem.getSouItemId());
        // 供应商ID
        orderItem.setVendorId(souOrder.getVendorId());
        // 报价单ID
        orderItem.setOrderId(souOrder.getOrderId());
        // 轮次
        orderItem.setRound(souOrder.getRound());
        // 报价单状态
        orderItem.setOrderStatus(souOrder.getOrderStatus());
        // 是否有效报价
        orderItem.setIsValid(Enable.Y);
        // 是否代理报价
        orderItem.setIsProxy(po.getSouOrder().getIsProxy());
        // 供应商报价币种
        orderItem.setOrderCurrency(dto.getOrderCurrency());
        // 税率编码
        orderItem.setTaxKey(dto.getTaxKey());

        orderItem.setOrderTaxPrice(dto.getOrderTaxPrice());
        //本次报价
        orderItem.setOrderNowPrice(dto.getOrderNowPrice());
        // 本轮入围情况
        orderItem.setWinStatus(SouWinStatusEnum.D);
        SouObjectXUtil.mergeProperties(orderItem, dto);
        po.getOrderItemList().add(orderItem);
        return orderItem;
    }


    @ApiOperation("转化得到报价附件")
    protected void doConvertOrderFiles(SouOrderEditPO po, @Nullable List<ApiSouOrderFileDTO> orderFileList, boolean isTempSave) {
        if (CollectionUtils.isEmpty(orderFileList)) {
            return;
        }

        List<SouOrderFile> entityList = new ArrayList<>(orderFileList.size());
        for (ApiSouOrderFileDTO orderFile : orderFileList) {
            // ID
            if (orderFile.getOrderFileId() == null) {
                orderFile.setOrderFileId(IdGenrator.generate());
            }

            SouOrderFile entity = new SouOrderFile();
            entityList.add(entity);
            BeanUtils.copyProperties(orderFile, entity);

            SouFileConfig fileConfig = SouOrderDtoContext.getContextHolder().getFileConfigMap().get(orderFile.getSouFileConfigId());
            if (fileConfig != null) {
                BeanUtils.copyProperties(fileConfig, entity);
            } else {
                AssertUtils.notNull(entity.getFileType(), "请选择报价附件文件类型");
            }

            BeanUtils.copyProperties(po.getSouOrder(), entity);

            entity.setOrderDocId(orderFile.getOrderDocId());
            entity.setOrderFileName(orderFile.getOrderFileName());
            entity.setOrderRemark(orderFile.getOrderRemark());

            SouObjectXUtil.mergeProperties(entity, orderFile);
        }

        po.setOrderFileList(entityList);
    }


    @ApiOperation("用于额外的与报价行相关的信息处理")
    protected void doExtraConvertForOrderItem(SouOrderEditPO po, SouItem availableItem, SouOrderItem quoteItem, ApiSouOrderItemDTO dto, boolean isTempSave) {
        SouProject souProject = SouOrderDtoContext.getContextHolder().getSouProject();
        //报价上限
        Integer quoteCap = souProject.getQuoteCap();
        for (SouOrderItem souOrderItem : po.getOrderItemList()) {
            //起拍价格
            BigDecimal startPrice1 = souOrderItem.getStartPrice();
            //上次报价
            BigDecimal orderTaxPrice = souOrderItem.getOrderTaxPrice();
            //4: 校验
            if (ObjectUtils.isEmpty(souOrderItem.getOrderNowPrice())) {
                throw new BaseException("请输入物资：" + souOrderItem.getItemDesc() + " 的本次报价金额");
            }
            if (ObjectUtils.isEmpty(orderTaxPrice)) {
                return;
            }
            if (souOrderItem.getOrderNowPrice().compareTo(startPrice1) == -1) {
                throw new BaseException("物资名称：" + souOrderItem.getItemDesc() + "，的本次报价金额不能小于起拍价格：" + startPrice1);
            } else if (souOrderItem.getOrderNowPrice().compareTo(startPrice1) >= 1 && souOrderItem.getOrderNowPrice().compareTo(orderTaxPrice
                    .multiply(BigDecimal.valueOf(1).add(BigDecimal.valueOf(quoteCap).divide(BigDecimal.valueOf(100))))) >= 1) {
                throw new BaseException("物资名称：" + souOrderItem.getItemDesc() + "，的本次报价金额不能超过报价上限" + quoteCap + "%");
            }
        }
    }

    @ApiOperation("转换后的额外处理")
    protected void doExtraConvert(SouOrderEditPO po, ApiSouOrderDTO param, boolean isTempSave) {
        // 1: 原币未税总价
        // 2: 原币含税总价
        BigDecimal standardNotaxTotal = BigDecimal.ZERO;
        BigDecimal standardTaxTotal = BigDecimal.ZERO;
        for (SouOrderItem orderItem : po.getOrderItemList()) {
            if (orderItem.getOrderCurrency() == null) {
                continue;
            }
            SouCurrency currency = SouOrderDtoContext.getContextHolder().getCurrencyMap().get(orderItem.getOrderCurrency());
            if (orderItem.getOrderNotaxPrice() != null) {
                standardNotaxTotal = standardNotaxTotal.add(orderItem.getOrderNotaxPrice().multiply(orderItem.getRequireQuantity())
                        .setScale(currency.getPricePrecision(), RoundingMode.HALF_UP));
            }
            if (orderItem.getOrderTaxPrice() != null) {
                standardTaxTotal = standardTaxTotal.add(orderItem.getOrderTaxPrice().multiply(orderItem.getRequireQuantity())
                        .setScale(currency.getPricePrecision(), RoundingMode.HALF_UP));
            }
        }
        // 3: 本币未税总价
        // 4: 本币未税总价
        standardNotaxTotal = BigDecimal.ZERO;
        standardTaxTotal = BigDecimal.ZERO;
        for (SouOrderItem orderItem : po.getOrderItemList()) {
            if (orderItem.getOrderCurrency() == null) {
                continue;
            }
            SouCurrency currency = SouOrderDtoContext.getContextHolder().getCurrencyMap().get(orderItem.getOrderCurrency());
            if (orderItem.getStandardNotaxPrice() != null) {
                standardNotaxTotal = standardNotaxTotal.add(orderItem.getStandardNotaxPrice().multiply(orderItem.getRequireQuantity())
                        .setScale(currency.getPricePrecision(), RoundingMode.HALF_UP));
            }
            if (orderItem.getStandardTaxPrice() != null) {
                standardTaxTotal = standardTaxTotal.add(orderItem.getStandardTaxPrice().multiply(orderItem.getRequireQuantity())
                        .setScale(currency.getPricePrecision(), RoundingMode.HALF_UP));
            }
        }
        po.getSouOrder().setStandardNotaxTotalPrice(standardNotaxTotal);
        po.getSouOrder().setStandardTaxTotalPrice(standardTaxTotal);

        SouObjectXUtil.mergeProperties(po.getSouOrder(), param);
    }

    /**
     * 供应商风险不会反馈到报价权限上，报价权限属于采购商的强制限制，而供应商风险属于临时性动态限制。
     * 1. 如果是供应商维度限制(不能投标)，禁止报价。
     * 2. 如果是供应商维度限制(不能中标)，可以报价。
     * 3. 如果是供应商+品类维度(不能投标)，可以报价，前提是物料需求里有未限制品类的物料。
     * - 供应商可以看到被限制的品类的物料需求行，但是不能报价。
     * 4. 如果是供应商+品类维度(不能中标)，可以报价。
     */
    @ApiOperation("校验供应商风险")
    protected void checkVendorRiskForOrder(long projectId, long vendorId, Set<Long> souItemIds) {
        MonitoringDTO param = new MonitoringDTO();
        param.setVendorId(vendorId);
        List<MonitoringDTO> monitorList = supplierClient.listMonitoringByCompanyId(param);
        if (CollectionUtils.isNotEmpty(monitorList)) {
            // 判断供应商维度
            List<MonitoringDTO> globals = monitorList.stream()
                    .filter(e -> e.getCategoryId() == null)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(globals)) {
                globals.forEach(global ->
                        AssertUtils.isFalse(Enable.Y.equals(global.getNoBid()), "供应商被限制禁止投标(供应商风险)")
                );
            }
            // 判断供应商+品类维度
            List<MonitoringDTO> unGlobals = monitorList.stream()
                    .filter(e -> e.getCategoryId() != null)
                    .collect(Collectors.toList());
            /*Map<Long*//* categoryId *//*, String*//* categoryName *//*> canOrderCategoryMap = souItemDAO.listByIds(souItemIds)
                    .stream().filter(e -> e.getCategoryId() != null).collect(Collectors.toMap(SouItem::getCategoryId, SouItem::getCategoryName, (a, b) -> a));
            if (!unGlobals.isEmpty() && !canOrderCategoryMap.isEmpty()) {
                canOrderCategoryMap.forEach((id, name) ->
                        unGlobals.forEach(unGlobal -> {
                            if (id.equals(unGlobal.getCategoryId())) {
                                // 说明供应商+品类维度下存在限制
                                AssertUtils.isFalse(Enable.Y.equals(unGlobal.getNoBid()), "供应商被限制禁止对品类[{0}]下的任何物料投标(供应商风险)",
                                        name);
                            }
                        })
                );
            }*/
        }
    }

    @Override
    public String matchModule() {
        return SouTypeEnum.DEFAULT.name();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
