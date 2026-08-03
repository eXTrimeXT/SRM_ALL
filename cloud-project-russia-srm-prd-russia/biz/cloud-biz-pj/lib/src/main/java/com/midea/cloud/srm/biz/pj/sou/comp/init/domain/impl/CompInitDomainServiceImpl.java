package com.midea.cloud.srm.biz.pj.sou.comp.init.domain.impl;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.comp.init.dao.*;
import com.midea.cloud.srm.biz.pj.sou.comp.init.domain.CompInitDomainService;
import com.midea.cloud.srm.biz.pj.sou.comp.process.dao.CompSouProcessConfigDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouItemDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.inq.InqClient;
import com.midea.cloud.srm.feign.supcooperate.SupcooperateClient;
import com.midea.cloud.srm.model.base.formula.entity.BaseMaterialPrice;
import com.midea.cloud.srm.model.base.formula.entity.MaterialFormulaRelate;
import com.midea.cloud.srm.model.base.formula.openapi.dto.ApiMaterialFormulaRelateSelectDTO;
import com.midea.cloud.srm.model.base.formula.openapi.vo.ApiMaterialFormulaRelateSelectVO;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.dto.PurchaseExchangeRateEditDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseExchangeRate;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseTax;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.base.scene.enums.SceneModuleCodeEnum;
import com.midea.cloud.srm.model.common.enums.BaseInfoStatusEnum;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.comp.entity.*;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouCurrencyEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouItemDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouProjectEditDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.dto.init.ApiCompSouRequireInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouRequireInfoDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitDetailVO;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouOrderTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouSourceFromTypeEnum;
import com.midea.cloud.srm.model.pm.pr.documents.param.RequirementPoolFollowNumsCallbackDTO;
import com.midea.cloud.srm.model.pm.pr.requirement.dto.RequirementManageDTO;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApplyStatus;
import com.midea.cloud.srm.model.pj.sou.comp.entity.CompSouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 竞价 - 立项额外处理
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/12/12
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompInitDomainServiceImpl implements CompInitDomainService {

    @Autowired
    private SouProjectDAOImpl souProjectDao;
    @Autowired
    private CompSouProjectDAOImpl compSouProjectDao;
    @Autowired
    private CompSouCurrencyDAOImpl compSouCurrencyDao;
    @Autowired
    private SouItemDAOImpl souItemDao;
    @Autowired
    private CompSouItemDAOImpl compSouItemDao;
    @Autowired
    private CompSouBaseMaterialPriceDAOImpl compSouBaseMaterialPriceDao;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private SupcooperateClient supcooperateClient;
    @Autowired
    private CompSouProcessConfigDAOImpl compSouProcessConfigDao;
    @Autowired
    private CompSouItemPaymentDAOImpl compSouItemPaymentDao;
    @Autowired
    private InqClient inqClient;

    /**
     * 保存项目额外信息
     */
    @Override
    public void editProjectInfo(ApiCompSouProjectEditDTO project, boolean isTempSave, boolean isCopy) {
        CompSouProject compProject = compSouProjectDao.getById(project.getProjectId());
        if (compProject == null) {
            compProject = new CompSouProject();
        }
        BeanUtils.copyProperties(project, compProject);
        CompSouProcessConfig compProcessConfig = null;
        if (project.getProcessConfigId() != null) {
            compProcessConfig = compSouProcessConfigDao.getById(project.getProcessConfigId());
        }
        // 开始校验参数:
        // 1: 预算金额
        if (compProject.getBudgetAmount() != null) {
            compProject.setBudgetAmount(compProject.getBudgetAmount().stripTrailingZeros());
            AssertUtils.isTrue(compProject.getBudgetAmount().compareTo(BigDecimal.ZERO) > 0, "预算金额不能小于0");
        }
        // 2: 最小涨/跌幅百分比
        if (compProject.getMinPercent() != null) {
            AssertUtils.isTrue(compProject.getMinPercent().compareTo(BigDecimal.ZERO) > 0, "最小涨/跌幅百分比必须大于0");
        }
        // 3: 最小涨/跌金额
        if (compProject.getMinAmount() != null) {
            AssertUtils.isTrue(compProject.getMinAmount().compareTo(BigDecimal.ZERO) > 0, "最小涨/跌金额必须大于0");
        }
        if (compProject.getMinPercent() == null && compProject.getMinAmount() == null) {
            AssertUtils.isTrue(isTempSave, "请输入最小涨/跌幅百分比或者最小涨/跌金额");
        }
        // 4: 截止前几分钟
        AssertUtils.isTrue(isTempSave || compProject.getExtendMinute() != null, "请输入截止前几分钟");
        if (compProject.getExtendMinute() != null) {
            AssertUtils.isTrue(compProject.getExtendMinute().compareTo(BigDecimal.ZERO) > 0, "请输入截止前几分钟");
        }
        // 5: 中标供应商数量
        AssertUtils.isTrue(isTempSave || compProject.getMaxWinVendorCount() != null, "请输入中标供应商数量");
        if (compProject.getMaxWinVendorCount() != null) {
            AssertUtils.isTrue(compProject.getMaxWinVendorCount() > 0, "中标供应商数量必须大于0");
        }
        // 6: 汇率类型
        compProject.setExchangeRateType(StringUtils.trimToNull(compProject.getExchangeRateType()));
        AssertUtils.isTrue(isTempSave || compProject.getExchangeRateType() != null, "请选择汇率类型");
        // 7: 币种转换日期
        AssertUtils.isTrue(isTempSave || compProject.getCurrencyExchangeDate() != null, "请选择币种转换日期");
        // 8: 商务要求 -- 保证金金额
        boolean needBond = compProcessConfig != null && Enable.Y.equals(compProcessConfig.getBondManagement());
        if (compProcessConfig != null) {
            if (needBond) {
                if (compProject.getBondAmount() == null) {
                    AssertUtils.isTrue(isTempSave || isCopy, "请输入商务要求:保证金金额");
                } else {
                    compProject.setBondAmount(compProject.getBondAmount().stripTrailingZeros());
                    AssertUtils.isTrue(compProject.getBondAmount().compareTo(BigDecimal.ZERO) > 0, "商务要求:保证金金额必须大于0");
                }
            } else {
                compProject.setBondAmount(null);
            }
        }
        // 9: 商务要求 -- 保证金说明
        compProject.setBondDesc(StringUtils.trimToNull(compProject.getBondDesc()));
        if (compProcessConfig != null) {
            if (needBond) {
                if (compProject.getBondDesc() != null) {
                    AssertUtils.isTrue(compProject.getBondDesc().length() <= 255, "商务要求:保证金说明长度不能超过255");
                }
            } else {
                compProject.setBondDesc(null);
            }
        } else {
            if (compProject.getBondDesc() != null) {
                AssertUtils.isTrue(compProject.getBondDesc().length() <= 255, "商务要求:保证金说明长度不能超过255");
            }
        }
        // 10: 商务要求 -- 保证金提交方式
        compProject.setBondMethod(StringUtils.trimToNull(compProject.getBondMethod()));
        if (compProcessConfig != null) {
            if (needBond) {
                if (compProject.getBondMethod() == null) {
                    AssertUtils.isTrue(isTempSave || isCopy, "请选择商务要求:保证金提交方式");
                }
            } else {
                compProject.setBondMethod(null);
            }
        }
        // 11: 保证金提交截止时间
        if (compProcessConfig != null) {
            if (needBond) {
                if (compProject.getBondEndTime() == null) {
                    AssertUtils.isTrue(isTempSave || isCopy, "请选择商务要求:保证金提交截止时间");
                } else {
                    AssertUtils.isTrue(isCopy || compProject.getBondEndTime().after(new Date()), "商务要求:保证金提交截止时间必须晚于当前时间");
                }
            } else {
                compProject.setBondEndTime(null);
            }
        }
        // 12: 商务要求 -- 保证金缴纳账号
        compProject.setBankAccountNum(StringUtils.trimToNull(compProject.getBankAccountNum()));
        if (compProcessConfig != null) {
            if (needBond) {
                if (compProject.getBankAccountNum() == null) {
                    AssertUtils.isTrue(isTempSave || isCopy, "请输入商务要求:保证金缴纳账号");
                } else {
                    AssertUtils.isTrue(compProject.getBankAccountNum().length() <= 255, "商务要求:保证金缴纳账号长度不能超过255");
                }
            } else {
                compProject.setBankAccountNum(null);
            }
        } else {
            if (compProject.getBankAccountNum() != null) {
                AssertUtils.isTrue(compProject.getBankAccountNum().length() <= 255, "商务要求:保证金缴纳账号长度不能超过255");
            }
        }
        // 13: 商务要求 -- 账户名称
        compProject.setBankAccountName(StringUtils.trimToNull(compProject.getBankAccountName()));
        if (compProcessConfig != null) {
            if (needBond) {
                if (compProject.getBankAccountName() == null) {
                    AssertUtils.isTrue(isTempSave || isCopy, "请输入商务要求:账户名称");
                } else {
                    AssertUtils.isTrue(compProject.getBankAccountName().length() <= 255, "商务要求:账户名称长度不能超过255");
                }
            } else {
                compProject.setBankAccountName(null);
            }
        } else {
            if (compProject.getBankAccountName() != null) {
                AssertUtils.isTrue(compProject.getBankAccountName().length() <= 255, "商务要求:保证金账户名称长度不能超过255");
            }
        }
        // 14: 商务要求 -- 开户支行
        compProject.setBankBranchName(StringUtils.trimToNull(compProject.getBankBranchName()));
        if (compProcessConfig != null) {
            if (needBond) {
                if (compProject.getBankBranchName() == null) {
                    AssertUtils.isTrue(isTempSave || isCopy, "请输入商务要求:开户支行");
                } else {
                    AssertUtils.isTrue(compProject.getBankBranchName().length() <= 255, "商务要求:开户支行长度不能超过255");
                }
            } else {
                compProject.setBankBranchName(null);
            }
        } else {
            if (compProject.getBankBranchName() != null) {
                AssertUtils.isTrue(compProject.getBankBranchName().length() <= 255, "商务要求:开户支行长度不能超过255");
            }
        }
        // 15: 需求简述
        compProject.setRequireDesc(StringUtils.trimToNull(compProject.getRequireDesc()));
        if (compProject.getRequireDesc() != null) {
            AssertUtils.isTrue(compProject.getRequireDesc().length() <= 300, "需求简述输入长度不能超过300");
        }
        //更新时,可设置null值
        compSouProjectDao.saveOrUpdateIgnored(compProject);
    }

    /**
     * 保存币种
     */
    @Override
    public void editCurrency(ApiCompSouProjectEditDTO project, List<ApiCompSouCurrencyEditDTO> currencyList) {
        if (CollectionUtils.isEmpty(currencyList)) { return; }

        Map<String/* fromCurrency */, PurchaseExchangeRate> exchangeRateMap; {
            if (currencyList.isEmpty()) {
                exchangeRateMap = Collections.emptyMap();
            } else {
                PurchaseExchangeRateEditDTO queryParam = new PurchaseExchangeRateEditDTO();
                queryParam.setToCurrencyCode(project.getStandardCurrency());
                queryParam.setRateType(project.getExchangeRateType());
                queryParam.setExchangeDate(project.getCurrencyExchangeDate());
                exchangeRateMap = baseClient.listExchangeRateByParams(queryParam)
                        .stream().collect(Collectors.toMap(PurchaseExchangeRate::getFromCurrencyCode, Function.identity()));
            }
        }

        List<CompSouCurrency> entityList = new ArrayList<>(currencyList.size());
        for (ApiCompSouCurrencyEditDTO c : currencyList) {
            CompSouCurrency entity = SouObjectXUtil.convertTargetObj(c, CompSouCurrency.class);
            // ID(略)
            // 汇率
            PurchaseExchangeRate rate;
            if (Objects.equals(c.getCurrencyCode(), project.getStandardCurrency())) {
                rate = new PurchaseExchangeRate();
                rate.setPriceTax(BigDecimal.ONE);
            } else {
                rate = exchangeRateMap.get(entity.getCurrencyCode());
                AssertUtils.notNull(rate, LocaleHandler.getLocaleMsg("[{0} -> {1}]的汇率不存在"), c.getCurrencyCode(), project.getStandardCurrency());
            }
            entity.setPriceTax(rate.getPriceTax());
            entityList.add(entity);
        }
        compSouCurrencyDao.saveOrUpdate(project.getProjectId(), entityList, CompSouCurrency::getProjectId);
    }

    /**
     * 保存项目需求信息
     */
    @Override
    public void editRequireInfo(ApiCompSouRequireInfoDTO param, @Nullable Long userId, boolean isTempSave, boolean isCopy) {
        // 1: 保存项目信息
        compSouProjectDao.lambdaUpdate()
                .set(CompSouProject::getRequireDesc, param.getRequireDesc())
                .eq(CompSouProject::getProjectId, param.getProjectId())
                .update();
        // 2: 保存物料需求 / 付款条款
        SouProject souProject = souProjectDao.getById(param.getProjectId());
        Map<String/* currencyCode */, CompSouCurrency> currencyMap = compSouCurrencyDao.list(CompSouCurrency::getProjectId, param.getProjectId())
                .stream().collect(Collectors.toMap(CompSouCurrency::getCurrencyCode, Function.identity()));
        Map<String/* taxKey */, BigDecimal> taxMap = baseClient.listTaxAll().stream()
                .collect(Collectors.toMap(PurchaseTax::getTaxKey, PurchaseTax::getTaxCode, (a, b) -> a));
        Map<Long/* souItemId */, SouItem> souItemMap = souItemDao.list(SouItem::getProjectId, param.getProjectId())
                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));

        List<CompSouItem> itemList;
        List<CompSouItemPayment> compItemPaymentArrayList; {
            if (CollectionUtils.isEmpty(param.getItemList())) {
                itemList = Collections.emptyList();
                compItemPaymentArrayList = Collections.emptyList();
            } else {
                itemList = new ArrayList<>(param.getItemList().size());

                // 查询物料信息(非无编码物料)
                Map<Long/* itemId */, MaterialItem> itemMap; {
                    Set<Long> itemIds = param.getItemList().stream().map(ApiCompSouItemDTO::getItemId).filter(Objects::nonNull).collect(Collectors.toSet());
                    if (itemIds.isEmpty()) {
                        itemMap = Collections.emptyMap();
                    } else {
                        itemMap = baseClient.listMaterialItemsByIds(new ArrayList<>(itemIds))
                                .stream().collect(Collectors.toMap(MaterialItem::getMaterialId, Function.identity()));
                    }
                }
                // 查询当前用户有权限的OU信息
                if (userId != null) {
                    Map<Long/* orgId */, Organization> authOrgMap = baseClient.getFullPathIdByTypeCode(userId)
                            .stream().collect(Collectors.toMap(Organization::getOrganizationId, Function.identity(), (a, b) -> a));
                    AssertUtils.isFalse(authOrgMap.isEmpty(), "当前用户无OU权限，不能添加物料需求信息");
                }

                ApiMaterialFormulaRelateSelectVO relateInfo = null;
                if (SouOrderTypeEnum.FORMULA.equals(param.getOrderType()) && !itemMap.isEmpty()) {
                    ApiMaterialFormulaRelateSelectDTO queryParam = new ApiMaterialFormulaRelateSelectDTO(); {
                        queryParam.setMaterialIds(new HashSet<>(param.getItemList().size()));
                        queryParam.setCategoryIds(new HashSet<>(param.getItemList().size()));
                        queryParam.setOrgIds(new HashSet<>(param.getItemList().size()));

                        param.getItemList().forEach(item -> {
                            if (item.getItemId() != null) { queryParam.getMaterialIds().add(item.getItemId()); }
                            if (item.getCategoryId() != null) { queryParam.getCategoryIds().add(item.getCategoryId()); }
                            Long orgOuId = SouObjectXUtil.getXbyLambda(item, SouItem::getOrgOuId);
                            if (orgOuId != null) { queryParam.getOrgIds().add(orgOuId); }
                        });
                    }
                    relateInfo = baseClient.getMaterialFormulaRelateInfos2(queryParam);
                }

                // 校验
                int rowIndex = 0;
                compItemPaymentArrayList = new ArrayList<>(param.getItemList().size() << 2);
                for (ApiCompSouItemDTO compItemDTO : param.getItemList()) {
                    rowIndex++;

                    // 1: 起拍价
                    if (Enable.Y.equals(souProject.getIsPriceNotax())) {
                        // 未税
                        AssertUtils.isTrue(isTempSave || compItemDTO.getStartOrderNotaxPrice() != null, LocaleHandler.getLocaleMsg("物料需求第{0}行请填写起拍价"), rowIndex);
                        if (compItemDTO.getStartOrderNotaxPrice() != null) {
                            AssertUtils.isTrue(compItemDTO.getStartOrderNotaxPrice().compareTo(BigDecimal.ZERO) >= 0, LocaleHandler.getLocaleMsg("物料需求第{0}行起拍价不能小于0"), rowIndex);
                        }
                    } else {
                        // 含税
                        AssertUtils.isTrue(isTempSave || compItemDTO.getStartOrderTaxPrice() != null, LocaleHandler.getLocaleMsg("物料需求第{0}行请填写起拍价"), rowIndex);
                        if (compItemDTO.getStartOrderTaxPrice() != null) {
                            AssertUtils.isTrue(compItemDTO.getStartOrderTaxPrice().compareTo(BigDecimal.ZERO) >= 0, LocaleHandler.getLocaleMsg("物料需求第{0}行起拍价不能小于0"), rowIndex);
                        }
                    }
                    // 2: 币种
                    compItemDTO.setOrderCurrency(StringUtils.trimToNull(compItemDTO.getOrderCurrency()));
                    AssertUtils.isTrue(isTempSave || compItemDTO.getOrderCurrency() != null, LocaleHandler.getLocaleMsg("物料需求第{0}行请选择币种"), rowIndex);
                    CompSouCurrency currency = null;
                    if (compItemDTO.getOrderCurrency() != null) {
                        currency = currencyMap.get(compItemDTO.getOrderCurrency());
                        AssertUtils.notNull(currency, LocaleHandler.getLocaleMsg("物料需求第{0}行币种[{1}]未在可用币种中定义"), rowIndex, compItemDTO.getOrderCurrency());
                    }
                    // 3: 税率
                    compItemDTO.setTaxKey(StringUtils.trimToNull(compItemDTO.getTaxKey()));
                    AssertUtils.isTrue(isTempSave || compItemDTO.getTaxKey() != null, LocaleHandler.getLocaleMsg("物料需求第{0}行请选择税率"), rowIndex);
                    if (compItemDTO.getTaxKey() != null) {
                        compItemDTO.setTaxRate(taxMap.get(compItemDTO.getTaxKey()));
                        AssertUtils.notNull(compItemDTO.getTaxRate(), LocaleHandler.getLocaleMsg("物料需求第{0}行的税率[{1}]不存在"), rowIndex, compItemDTO.getTaxKey());

                        if (currency != null) {
                            if (Enable.Y.equals(souProject.getIsPriceNotax())) {
                                if (compItemDTO.getStartOrderNotaxPrice() != null) {
                                    // 原币含税 = 原币未税 * (1 + 税率 / 100)
                                    compItemDTO.setStartOrderTaxPrice(compItemDTO.getStartOrderNotaxPrice()
                                            .multiply(BigDecimal.ONE.add(compItemDTO.getTaxRate().divide(new BigDecimal(100), 6, RoundingMode.HALF_UP)))
                                            .setScale(currency.getPricePrecision(), RoundingMode.HALF_UP));
                                }
                            } else {
                                if (compItemDTO.getStartOrderTaxPrice() != null) {
                                    // 原币未税 = 原币含税 / (1 + 税率 / 100)
                                    compItemDTO.setStartOrderNotaxPrice(compItemDTO.getStartOrderTaxPrice()
                                            .divide(BigDecimal.ONE.add(compItemDTO.getTaxRate().divide(new BigDecimal(100), 6, RoundingMode.HALF_UP)), 8, RoundingMode.HALF_UP)
                                            .setScale(currency.getPricePrecision(), RoundingMode.HALF_UP));
                                }
                            }
                        }
                    }
                    // 4: 行类型
                    compItemDTO.setRowType(StringUtils.trimToNull(compItemDTO.getRowType()));
                    // 5: 交货地点
                    compItemDTO.setDeliveryPlace(StringUtils.trimToNull(compItemDTO.getDeliveryPlace()));
                    if (compItemDTO.getDeliveryPlace() != null) {
                        AssertUtils.isTrue(compItemDTO.getDeliveryPlace().length() <= 250, LocaleHandler.getLocaleMsg("物料需求第{0}行交货地点的输入长度不能超过250"), rowIndex);
                    }
                    // 6: 价格类型(采购类型)
                    compItemDTO.setPriceType(StringUtils.trimToNull(compItemDTO.getPriceType()));
                    AssertUtils.isTrue(isTempSave || compItemDTO.getPriceType() != null, LocaleHandler.getLocaleMsg("物料需求第{0}行请选择价格类型(采购类型)"), rowIndex);
                    // 7: 贸易条款
                    compItemDTO.setTradeTerm(StringUtils.trimToNull(compItemDTO.getTradeTerm()));
                    AssertUtils.isTrue(isTempSave || compItemDTO.getTradeTerm() != null, LocaleHandler.getLocaleMsg("物料需求第{0}行请选择贸易条款"), rowIndex);
                    // 8: 保修期(月)
                    AssertUtils.isTrue(isTempSave || compItemDTO.getWarrantyPeriod() != null, LocaleHandler.getLocaleMsg("物料需求第{0}行请输入保修期(月)"), rowIndex);
                    if (compItemDTO.getWarrantyPeriod() != null) {
                        AssertUtils.isTrue(compItemDTO.getWarrantyPeriod() > 0, LocaleHandler.getLocaleMsg("物料需求第{0}行保修期(月)必须大于0"), rowIndex);
                    }
                    // 9: 付款条款
                    List<CompSouItemPayment> paymentList = compItemDTO.getPaymentList();
                    if (CollectionUtils.isEmpty(paymentList)) {
                        AssertUtils.isTrue(param.isTempSave(), LocaleHandler.getLocaleMsg("物料需求第{0}行请填写账期信息"), rowIndex);
                    } else {
                        BigDecimal totalProportion = BigDecimal.ZERO;
                        if (CollectionUtils.isNotEmpty(paymentList)) {
                            int paymentIndex = 0;
                            for (CompSouItemPayment payment : paymentList) {
                                paymentIndex++;
                                // 2.4.1: 询价单ID
                                payment.setProjectId(param.getProjectId());
                                // 2.4.2: 询价行ID
                                payment.setSouItemId(compItemDTO.getSouItemId());
                                // 2.4.3: 付款账期
                                payment.setPaymentPeriod(StringUtils.trimToNull(payment.getPaymentPeriod()));
                                AssertUtils.isTrue(param.isTempSave() || payment.getPaymentPeriod() != null, LocaleHandler.getLocaleMsg("物料需求第{0}行的账期第{1}行请选择付款账期"), rowIndex, paymentIndex);
                                if (payment.getPaymentPeriod() != null) {
                                    AssertUtils.isTrue(payment.getPaymentPeriod().length() <= 30, LocaleHandler.getLocaleMsg("物料需求第{0}行的账期第{1}行请付款账期输入长度不能超过30"), rowIndex, paymentIndex);
                                }
                                // 2.4.4: 付款条件
                                payment.setPaymentCondition(StringUtils.trimToNull(payment.getPaymentCondition()));
                                AssertUtils.isTrue(param.isTempSave() || payment.getPaymentCondition() != null, LocaleHandler.getLocaleMsg("物料需求第{0}行的账期第{1}行请选择付款条件"), rowIndex, paymentIndex);
                                AssertUtils.isTrue(param.isTempSave() || payment.getPaymentConditionId() != null, LocaleHandler.getLocaleMsg("物料需求第{0}行的账期第{1}行请选择付款条件"), rowIndex, paymentIndex);
                                if (payment.getPaymentCondition() != null) {
                                    AssertUtils.isTrue(payment.getPaymentCondition().length() <= 30, LocaleHandler.getLocaleMsg("物料需求第{0}行的账期第{1}行请付款条件输入长度不能超过30"), rowIndex, paymentIndex);
                                }
                                // 2.4.5: 付款方式
                                payment.setPaymentMode(StringUtils.trimToNull(payment.getPaymentMode()));
                                AssertUtils.isTrue(param.isTempSave() || payment.getPaymentMode() != null, LocaleHandler.getLocaleMsg("物料需求第{0}行的账期第几{1}行请选择付款方式"), rowIndex, paymentIndex);
                                if (payment.getPaymentMode() != null) {
                                    AssertUtils.isTrue(payment.getPaymentMode().length() <= 50, LocaleHandler.getLocaleMsg("物料需求第{0}行的账期第几{1}行请付款方式输入长度不能超过50"), rowIndex, paymentIndex);
                                }
                                // 2.4.6: 付款比例
                                AssertUtils.notNull(payment.getPaymentProportion(), LocaleHandler.getLocaleMsg("物料需求第{0}行的账期第{1}行请输入付款比例"), rowIndex, paymentIndex);
                                AssertUtils.isTrue(payment.getPaymentProportion().compareTo(BigDecimal.ZERO) > 0,
                                        "物料需求第{0}行的账期第{1}行付款比例必须大于0", rowIndex, paymentIndex);
                                AssertUtils.isTrue(payment.getPaymentProportion().compareTo(new BigDecimal(100)) <= 0,
                                        "物料需求第{0}行的账期第{1}行付款比例不能大于100", rowIndex, paymentIndex);
                                totalProportion = totalProportion.add(payment.getPaymentProportion());
                                // 2.4.7: 付款阶段
                                payment.setPaymentPhase(StringUtils.trimToNull(payment.getPaymentPhase()));
                                AssertUtils.isTrue(param.isTempSave() || payment.getPaymentPhase() != null, LocaleHandler.getLocaleMsg("物料需求第{0}行的账期第{1}行请选择付款阶段"), rowIndex, paymentIndex);
                                if (payment.getPaymentPhase() != null) {
                                    AssertUtils.isTrue(payment.getPaymentPhase().length() <= 50, LocaleHandler.getLocaleMsg("物料需求第{0}行的账期第{1}行请付款阶段输入长度不能超过50"), rowIndex, paymentIndex);
                                }
                                // 2.4.8: 排序
                                payment.setSortIndex(paymentIndex);

                                payment.setSouItemPaymentId(IdGenrator.generate());
                            }
                            AssertUtils.isTrue(totalProportion.compareTo(new BigDecimal(100)) == 0, LocaleHandler.getLocaleMsg("物料需求第{0}行的账期付款比例总和必须为100"), rowIndex);

                            // 将数据塞入待保存列表
                            compItemPaymentArrayList.addAll(paymentList);
                        }
                    }
                    // 10: 公式相关
                    if (SouOrderTypeEnum.FORMULA.equals(param.getOrderType())) {
                        Long relateId = compItemDTO.getMaterialFormulaRelateId();
                        if (relateId == null) {
                            AssertUtils.isTrue(isTempSave, LocaleHandler.getLocaleMsg("物料[{0}]请选择关联的公式"));
                        } else {
                            Long orgOuId = SouObjectXUtil.getXbyLambda(compItemDTO, SouItem::getOrgOuId);
                            List<MaterialFormulaRelate> relations = null;
                            if (compItemDTO.getItemId() != null) {
                                // 物料绑定
                                relations = relateInfo != null ? relateInfo.getMaterialRelateMap().get(compItemDTO.getItemId()) : null;
                                if (relations == null) {
                                    if (compItemDTO.getCategoryId() != null) {
                                        relations = relateInfo != null ? relateInfo.getCategoryRelateMap().get(compItemDTO.getCategoryId()) : null;
                                        if (relations == null && orgOuId != null) {
                                            relations = relateInfo != null ? relateInfo.getOrgOuRelateMap().get(orgOuId) : null;
                                        }
                                    } else if (orgOuId != null) {
                                        relations = relateInfo != null ? relateInfo.getOrgOuRelateMap().get(orgOuId) : null;
                                    }
                                }
                            } else if (compItemDTO.getCategoryId() != null) {
                                // 品类绑定
                                relations = relateInfo != null ? relateInfo.getCategoryRelateMap().get(compItemDTO.getCategoryId()) : null;
                                if (relations == null && orgOuId != null) {
                                    relations = relateInfo != null ? relateInfo.getOrgOuRelateMap().get(orgOuId) : null;
                                }
                            } else if (orgOuId != null) {
                                // 业务实体绑定
                                relations = relateInfo != null ? relateInfo.getOrgOuRelateMap().get(orgOuId) : null;
                            }

                            if (!isTempSave) {
                                AssertUtils.notNull(relations, LocaleHandler.getLocaleMsg("物料[{0}]没有绑定任何有效的公式(物料/品类/业务实体)"));
                            }

                            if (relations != null) {
                                for (MaterialFormulaRelate relate : relations) {
                                    if (!relate.getRelateId().equals(relateId)) { continue; }
                                    AssertUtils.isTrue(BaseInfoStatusEnum.VALID.name().equals(relate.getStatus()), LocaleHandler.getLocaleMsg("物料[{0}]绑定的公式[{1}]不是有效状态"),
                                            compItemDTO.getItemDesc(), relate.getFormulaName());
                                    // 物料公式关联ID
                                    compItemDTO.setMaterialFormulaRelateId(relate.getRelateId());
                                    // 公式ID
                                    compItemDTO.setFormulaId(relate.getFormulaId());
                                    // 公式名称
                                    compItemDTO.setFormulaName(relate.getFormulaName());
                                    // 公式值
                                    compItemDTO.setFormulaValue(relate.getFormulaValue());
                                }
                                AssertUtils.notNull(compItemDTO.getFormulaId(), LocaleHandler.getLocaleMsg("物料[{0}]关联的公式不存在[{1}]"), compItemDTO.getItemDesc(), relateId);
                            }
                        }
                    }


                    // -----
                    CompSouItem compItem = SouObjectXUtil.convertTargetObj(compItemDTO, CompSouItem.class);
                    itemList.add(compItem);

                    // PS: 处理起拍价相关的其他字段
                    if (!isTempSave && currency != null) {
                        // 10.1: 本币未税起拍价(= 原币未税 * 汇率)
                        compItem.setStartStandardNotaxPrice(compItem.getStartOrderNotaxPrice().multiply(currency.getPriceTax())
                                .setScale(souProject.getPricePrecision(), RoundingMode.HALF_UP));
                        // 10.2: 本币含税起拍价(原币含税 * 汇率)
                        compItem.setStartStandardTaxPrice(compItem.getStartOrderTaxPrice().multiply(currency.getPriceTax())
                                .setScale(souProject.getPricePrecision(), RoundingMode.HALF_UP));
                    }
                }

                // PS: 处理起拍价的额外字段
                if (!isTempSave) {
                    Map<String/* itemGroup */, BigDecimal> standardNotaxGroupPriceMap = new HashMap<>(param.getItemList().size());
                    Map<String/* itemGroup */, BigDecimal> standardTaxGroupPriceMap = new HashMap<>(param.getItemList().size());
                    itemList.forEach(compSouItem -> {
                        SouItem souItem = souItemMap.get(compSouItem.getSouItemId());

                        // 未税
                        BigDecimal notaxGroupPrice = standardNotaxGroupPriceMap.get(souItem.getItemGroup());
                        if (notaxGroupPrice == null) { notaxGroupPrice = BigDecimal.ZERO; }
                        notaxGroupPrice = notaxGroupPrice.add(compSouItem.getStartStandardNotaxPrice()
                                .multiply(souItem.getRequireQuantity()).setScale(souProject.getPricePrecision(), RoundingMode.HALF_UP));
                        standardNotaxGroupPriceMap.put(souItem.getItemGroup(), notaxGroupPrice);
                        // 含税
                        BigDecimal taxGroupPrice = standardTaxGroupPriceMap.get(souItem.getItemGroup());
                        if (taxGroupPrice == null) { taxGroupPrice = BigDecimal.ZERO; }
                        taxGroupPrice = taxGroupPrice.add(compSouItem.getStartStandardTaxPrice()
                                .multiply(souItem.getRequireQuantity()).setScale(souProject.getPricePrecision(), RoundingMode.HALF_UP));
                        standardTaxGroupPriceMap.put(souItem.getItemGroup(), taxGroupPrice);
                    });
                }
            }
        }
        compSouItemDao.saveOrUpdate(param.getProjectId(), itemList, CompSouItem::getProjectId);
        compSouItemPaymentDao.saveOrUpdate(param.getProjectId(), compItemPaymentArrayList, CompSouItemPayment::getProjectId);
        // 4. (公式报价)缓存基材价格
        this.saveCompBaseMaterialPrice(param.getProjectId(), null, 1);
        // 5: 更新附件
        Map<Long/* souItemId */, List<SceneFile>> fileMap = new HashMap<>(50);
        param.getItemList().forEach(item -> {
            if (CollectionUtils.isNotEmpty(item.getItemFiles())) {
                fileMap.put(item.getSouItemId(), item.getItemFiles());
                item.getItemFiles().forEach(file -> {
                    if (file.getSceneFileId() == null) {
                        file.setSceneFileId(IdGenrator.generate());
                    }
                    file.setBusinessId(item.getSouItemId());
                });
            } else {
                fileMap.put(item.getSouItemId(), Collections.emptyList()); // 清空
            }
        });
        baseClient.batchBindSceneFiles(
                SceneModuleCodeEnum.SCENE_SOU_ITEM_DRAWINGSHEAD_REQ_ATTACHMENT.toString(),
                false,
                fileMap);
    }

    /**
     * 询价立项 - (公式报价)缓存基材价格
     */
    @Override
    public void saveCompBaseMaterialPrice(long projectId, @Nullable List<CompSouItem> compItemList, int round) {
        SouProject project = souProjectDao.getById(projectId);
        if (!SouOrderTypeEnum.FORMULA.equals(project.getOrderType())){
            return;
        }
        // 删除旧数据
        compSouBaseMaterialPriceDao.lambdaUpdate()
                .eq(CompSouBaseMaterialPrice::getProjectId, projectId)
                .eq(CompSouBaseMaterialPrice::getRound, round)
                .remove();

        if (compItemList == null) {
            compItemList = compSouItemDao.lambdaQuery()
                    .eq(CompSouItem::getProjectId, projectId)
                    .list();
        }
        if (compItemList.isEmpty()) {
            return;
        }

        // 校验物料公式报价中的物料属性值是否已维护(这个远程调用方法，会自动校验，如果没有维护物料属性值就报错)
        baseClient.getMaterialAttrPriceInfos(compItemList.stream().map(CompSouItem::getMaterialFormulaRelateId).collect(Collectors.toSet()));

        // 查询基价信息
        Set<Long> formulaIds = compItemList.stream()
                .map(CompSouItem::getFormulaId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (formulaIds.isEmpty()) {
            return;
        }
        Map<Long/* factorId */, List<BaseMaterialPrice>> baseMaterialPriceMap = baseClient.getBaseMaterialPriceInfoByFormulaIds(formulaIds);

        // 构造数据
        List<CompSouBaseMaterialPrice> compPriceList = new ArrayList<>(baseMaterialPriceMap.size());
        CompSouBaseMaterialPrice compPrice;
        BaseMaterialPrice baseMaterialPrice;
        for (Map.Entry<Long, List<BaseMaterialPrice>> entry : baseMaterialPriceMap.entrySet()) {
            // 更新时间倒序排列，仅拿第一条数据(最新的基材价格)
            entry.getValue().sort(Comparator.comparing(BaseMaterialPrice::getLastUpdateDate).reversed());
            baseMaterialPrice = entry.getValue().get(0);

            compPrice = new CompSouBaseMaterialPrice();
            BeanUtils.copyProperties(baseMaterialPrice, compPrice);
            // ID
            compPrice.setCompBaseMaterialPriceId(IdGenrator.generate());
            // 寻源单ID
            compPrice.setProjectId(projectId);
            // 轮次
            compPrice.setRound(round);
            // 公式元素ID
            compPrice.setFactorId(entry.getKey());

            compPriceList.add(compPrice);
        }

        // 保存数据
        if (!compPriceList.isEmpty()) {
            compSouBaseMaterialPriceDao.saveBatch(compPriceList);
        }
    }

    /**
     * 删除/废弃寻源单据后，对应的处理寻源需求、采购需求单据信息
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     */
    @Override
    public void handlerRelativeBusinessAfterDeleteOrCancelComp(long projectId, ApiSouInitDetailVO initInfo, boolean toDelete) {
        if (SouSourceFromTypeEnum.PURCHASE_REQ.name().equals(initInfo.getProjectInfo().getSourceFromType())) {
            // 处理采购需求单据
            Set<Long> purchaseReqLineIds = initInfo.getRequireInfo()
                    .stream().map(SouItem::getSourceFromLineId).filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (!purchaseReqLineIds.isEmpty()) {
                RequirementPoolFollowNumsCallbackDTO param = new RequirementPoolFollowNumsCallbackDTO(); {
                    param.setFollowNum(initInfo.getProjectInfo().getSouNo());
                    param.setDeleted(toDelete);
                }
                supcooperateClient.requirementLineFollowNumsCallback(param);
            }
        } else if (SouSourceFromTypeEnum.SOU_REQ.name().equals(initInfo.getProjectInfo().getSourceFromType())) {
            // 处理寻源需求单据
            if (Objects.nonNull(initInfo.getProjectInfo().getSourceFromId())) {
                inqClient.reqSourceReset(initInfo.getProjectInfo().getSourceFromId());
            }
        }
    }

    /**
     * 转化来源于需求池的信息，以便后续保存寻源物料需求
     * @param projectId 寻源单ID{@link SouProject#getProjectId}
     * @param reqParams 需求池数据
     */
    @Override
    public ApiSouRequireInfoDTO handlerSouItemsFromReq(long projectId, List<RequirementManageDTO> reqParams) {
        // 1: 校验
        AssertUtils.notEmpty(reqParams, "缺少数据");
        Long ceeaStrategyUserId = reqParams.get(0).getCeeaStrategyUserId();
        boolean isSameStrategyUser = reqParams.stream().allMatch(e -> ceeaStrategyUserId.equals(e.getCeeaStrategyUserId()));
        AssertUtils.isTrue(isSameStrategyUser, "必须是同一采购策略用户");
        for (RequirementManageDTO reqParam : reqParams) {
            // 是否寻源
            AssertUtils.isFalse(Enable.Y.name().equals(reqParam.getIfCreateBid()), LocaleHandler.getLocaleMsg("需求池中物料[{0}]已寻源，勿重复操作"), reqParam.getMaterialName());
            // 单据状态
            AssertUtils.isTrue(RequirementApplyStatus.ASSIGNED.equals(reqParam.getApplyStatus()), "需求池中只有已分配的才能转寻源");
        }

        // 2: 开始构建数据
        ApiCompSouRequireInfoDTO dto = new ApiCompSouRequireInfoDTO();
        dto.setProjectId(projectId);
        dto.setTempSave(true);
        List<ApiCompSouItemDTO> compItemList = new ArrayList<>(reqParams.size());
        dto.setItemList(compItemList);

        for (RequirementManageDTO reqParam : reqParams) {
            ApiCompSouItemDTO compItem = new ApiCompSouItemDTO();
            compItemList.add(compItem);
            // 1: ID(略)
            // 2: 业务实体编码
            compItem.setOrgOuCode(reqParam.getOrgCode());
            // 3: 库存组织编码
            compItem.setOrgInvCode(reqParam.getOrganizationCode());
            // 4: 物料组合(略)
            // 5: 是否无料号物料
            compItem.setNoCodeItem(StringUtils.isEmpty(reqParam.getMaterialCode()) ? Enable.Y : Enable.N);
            // 6: 物料ID
            compItem.setItemId(reqParam.getMaterialId());
            // 7: 物料名称
            compItem.setItemDesc(reqParam.getMaterialName());
            // 8: 单位
            compItem.setUnit(reqParam.getUnitCode());
            // 9: 品类ID
            compItem.setCategoryId(reqParam.getCategoryId());
            // 10: 品类编码
            compItem.setCategoryCode(reqParam.getCategoryCode());
            // 11: 品类名称
            compItem.setCategoryName(reqParam.getCategoryName());
            // 12: 需求数量
            compItem.setRequireQuantity(reqParam.getRequirementQuantity());
            // 13: 需求时间
            compItem.setRequireDate(reqParam.getRequirementDate() != null ?
                    Date.from(reqParam.getRequirementDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null);
            // 14: 预计采购金额(略)
            // 15: 价格有效期从(略)
            // 16: 价格有效期到(略)
            // 17: 来源单据ID
            compItem.setSourceFromId(reqParam.getRequirementHeadId());
            // 18: 来源单据号
            compItem.setSourceFromNo(reqParam.getRequirementHeadNum());
            // 19: 来源单据行ID
            compItem.setSourceFromLineId(reqParam.getRequirementLineId());
            // 20: 来源单据行号
            compItem.setSourceFromLineNo(reqParam.getRowNum() != null ? reqParam.getRowNum().toString() : null);
            // 21: 备注(略)
            // 22: 是否阶梯报价
            compItem.setIsLadder(Enable.N);
            // 23: 来源类型
            compItem.setSourceFromType(SouSourceFromTypeEnum.PURCHASE_REQ.name());
            // 24: 排序(略)
            // 25: 物料价格公式关联ID(略)
            // 26: 公式id(略)
            // 27: 公式名称(略)
            // 28: 公式值(略)
            // 29: 拦标价(略)
            // 30: 交货地点
            compItem.setDeliveryPlace(reqParam.getCeeaDeliveryPlace());
            // 31: 价格类型(略)
            // 32: 采购类型
            compItem.setPurchaseType(reqParam.getCeeaPurchaseType());
            // 33: 贸易条款
            compItem.setTradeTerm(reqParam.getTradeTerm());
            // 34: 运输方式(略)
            // 35: 保修期(月)(略)
        }

        return SouObjectXUtil.convertTargetObj(dto, ApiSouRequireInfoDTO.class);
    }

}
