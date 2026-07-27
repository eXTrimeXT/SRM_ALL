package com.midea.cloud.srm.sou.inq.ext.domain;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.feign.SouExtRbacClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.purchase.dto.PurchaseExchangeRateEditDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseExchangeRate;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouCurrency;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.sou.inq.enums.InqSouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.init.ApiInqSouProjectEditDTO;
import com.midea.cloud.srm.sou.inq.domain.service.impl.InqSouDomainServiceImpl;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouCurrencyDAO;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouProjectDAO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Primary
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtInqSouDomainServiceImpl extends InqSouDomainServiceImpl {

    @Autowired
    private InqSouProjectDAO inqSouProjectDAO;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private InqSouCurrencyDAO inqSouCurrencyDAO;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Override
    public void editProjectInfo(long projectId, ApiInqSouProjectEditDTO projectInfo, boolean isTempSave) {
        extFormatAndValidateProjectInfo(projectInfo, isTempSave);
        InqSouProject inqProject = inqSouProjectDAO.getById(projectId);
        if (inqProject == null) {
            inqProject = new InqSouProject();
        }

        BeanUtils.copyProperties(projectInfo, inqProject);
        inqProject.setExtProjectStatus(InqSouProjectStatusEnum.DRAFT);

        // 写入创建人所在公司信息
        HrUserOrgnizationDto userOrgnizationDto = pjProjectExtClient.getHrUserOrgnizationByUsername(AppUserUtil.getLoginAppUser().getCeeaEmpNo());
        if (userOrgnizationDto == null || userOrgnizationDto.getOuOrganization() == null || userOrgnizationDto.getBuOrganization() == null) {
            throw new IllegalArgumentException("查询采购员hr信息失败");
        }
        inqProject.setCreateUserOrgOuId(userOrgnizationDto.getOuOrganization().getOrganizationId());
        inqProject.setCreateUserOrgOuCode(userOrgnizationDto.getOuOrganization().getOrganizationCode());
        inqProject.setCreateUserOrgOuName(userOrgnizationDto.getOuOrganization().getOrganizationName());
        inqProject.setCreateUserOrgBuId(userOrgnizationDto.getBuOrganization().getOrganizationId());
        inqProject.setCreateUserOrgBuCode(userOrgnizationDto.getBuOrganization().getOrganizationCode());
        inqProject.setCreateUserOrgBuName(userOrgnizationDto.getBuOrganization().getOrganizationName());

        inqSouProjectDAO.saveOrUpdate(inqProject);
    }

    private void extFormatAndValidateProjectInfo(ApiInqSouProjectEditDTO projectInfo, boolean isTempSave) {
        projectInfo.setInquiryType(StringUtils.trimToNull(projectInfo.getInquiryType()));
        AssertUtils.isTrue(projectInfo.getInquiryType() != null || isTempSave, "请选择询价类型", new Object[0]);
        AssertUtils.isTrue(projectInfo.getStandardCurrency() != null || isTempSave, "请选择币种", new Object[0]);
        projectInfo.setExchangeRateType(StringUtils.trimToNull(projectInfo.getExchangeRateType()));
        projectInfo.setExchangeRateType(null);
        projectInfo.setCurrencyExchangeDate(null);
        projectInfo.setIsTargetPriceOk(Enable.N);
        if (projectInfo.getNeedEncryptPrice() == null) {
            projectInfo.setNeedEncryptPrice(Enable.N);
        }

        if (projectInfo.getAllowProxyOrder() == null) {
            projectInfo.setAllowProxyOrder(Enable.Y.equals(projectInfo.getNeedEncryptPrice()) ? Enable.N : Enable.Y);
        }

        if (projectInfo.getExcludeBlackVendors() == null) {
            projectInfo.setExcludeBlackVendors(Enable.N);
        }

        if (projectInfo.getExcludeNoCurrentOrgVendors() == null) {
            projectInfo.setExcludeNoCurrentOrgVendors(Enable.N);
        }

        if (projectInfo.getExcludeOrgQuitVendors() == null) {
            projectInfo.setExcludeOrgQuitVendors(Enable.N);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editCurrency(ApiInqSouProjectEditDTO inqProject, @Nullable List<InqSouCurrency> currencyList) {
        if (CollectionUtils.isEmpty(currencyList) || inqProject.getStandardCurrency() == null) { return; }

        Map<String/* fromCurrency */, PurchaseExchangeRate> exchangeRateMap; {
            if (currencyList.isEmpty()) {
                exchangeRateMap = Collections.emptyMap();
            } else {
                PurchaseExchangeRateEditDTO queryParam = new PurchaseExchangeRateEditDTO();
                queryParam.setToCurrencyCode(inqProject.getStandardCurrency());
                exchangeRateMap = baseClient.listExchangeRateByParams(queryParam)
                        .stream().collect(Collectors.toMap(PurchaseExchangeRate::getFromCurrencyCode, Function.identity()));
            }
        }

        for (InqSouCurrency entity : currencyList) {
            // ID(略)
            // 汇率
            PurchaseExchangeRate rate;
            if (Objects.equals(entity.getCurrencyCode(), inqProject.getStandardCurrency())) {
                rate = new PurchaseExchangeRate();
                rate.setPriceTax(BigDecimal.ONE);
            } else {
                rate = exchangeRateMap.get(entity.getCurrencyCode());
                AssertUtils.notNull(rate, "[{0} -> {1}]"+ LocaleHandler.getLocaleMsg("的汇率不存在"), entity.getCurrencyCode(), inqProject.getStandardCurrency());
            }
            entity.setPriceTax(rate.getPriceTax());
            entity.setExchangeRateId(rate.getExchangeRateId());
        }
        inqSouCurrencyDAO.saveOrUpdate(inqProject.getProjectId(), currencyList, InqSouCurrency::getProjectId);
    }

}
