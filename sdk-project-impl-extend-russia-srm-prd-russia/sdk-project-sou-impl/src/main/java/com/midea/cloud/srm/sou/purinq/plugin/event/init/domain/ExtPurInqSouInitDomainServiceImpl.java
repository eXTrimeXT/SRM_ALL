package com.midea.cloud.srm.sou.purinq.plugin.event.init.domain;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.purchase.dto.PurchaseExchangeRateEditDTO;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseExchangeRate;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ApiPurInqSouItemDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.init.ApiPurInqSouProjectEditDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouCurrency;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouItem;
import com.midea.cloud.srm.model.extapi.sou.purinq.entity.ExtPurInqSouProject;
import com.midea.cloud.srm.model.extapi.sou.purinq.enums.ExtPurInqSouProjectStatusEnum;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouCurrencyDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouItemDAO;
import com.midea.cloud.srm.sou.purinq.dao.ExtPurInqSouProjectDAO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-18
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurInqSouInitDomainServiceImpl implements ExtPurInqSouInitDomainService {

    @Autowired
    private ExtPurInqSouProjectDAO extPurInqSouProjectDAO;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private ExtPurInqSouCurrencyDAO extPurInqSouCurrencyDAO;
    @Autowired
    private ExtPurInqSouItemDAO extPurInqSouItemDAO;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    /**
     * 保存简易询价额外的基本信息
     */
    @Override
    public void editProjectInfo(long projectId, ApiPurInqSouProjectEditDTO projectInfo, boolean isTempSave) {
        // 1: 入参校验
        this.formatAndValidateProjectInfo(projectInfo, isTempSave);
        // 2: 保存数据
        ExtPurInqSouProject inqProject = extPurInqSouProjectDAO.getById(projectId);
        if (inqProject == null) { inqProject = new ExtPurInqSouProject(); }
        BeanUtils.copyProperties(projectInfo, inqProject);
        inqProject.setExtProjectStatus(ExtPurInqSouProjectStatusEnum.DRAFT);

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
        if (userOrgnizationDto.getDepartmentOrganization() != null) {
            inqProject.setCreateUserDeptId(userOrgnizationDto.getDepartmentOrganization().getOrganizationId());
            inqProject.setCreateUserDeptCode(userOrgnizationDto.getDepartmentOrganization().getOrganizationCode());
            inqProject.setCreateUserDeptName(userOrgnizationDto.getDepartmentOrganization().getOrganizationName());
        }

        extPurInqSouProjectDAO.saveOrUpdate(inqProject);
    }

    private void formatAndValidateProjectInfo(ApiPurInqSouProjectEditDTO projectInfo, boolean isTempSave) {
        // 1: 采购申请部门ID
        // 2: 采购申请部门编码
        // 3: 采购申请部门名称
        // 4: 项目策划方案ID
        // 5: 项目策划方案编码
        // 6: 项目策划方案名称
        // 7: 项目策划轮数
        // 8: 项目策划创建人账号
        // 9: 项目策划创建人昵称
        // 10: 项目策划介绍
        // 11: 项目策划定价思路
    }

    /**
     * 保存询价额外的币种信息
     */
    @Override
    public void editCurrency(ApiPurInqSouProjectEditDTO inqProject, @Nullable List<ExtPurInqSouCurrency> currencyList) {
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
        for (ExtPurInqSouCurrency entity : currencyList) {
            // ID(略)
            // 汇率
            PurchaseExchangeRate rate;
            if (Objects.equals(entity.getCurrencyCode(), inqProject.getStandardCurrency())) {
                rate = new PurchaseExchangeRate();
                rate.setPriceTax(BigDecimal.ONE);
            } else {
                rate = exchangeRateMap.get(entity.getCurrencyCode());
                AssertUtils.notNull(rate, LocaleHandler.getLocaleMsg("[{0} -> {1}]的汇率不存在"), entity.getCurrencyCode(), inqProject.getStandardCurrency());
            }
            entity.setPriceTax(rate.getPriceTax());
            entity.setExchangeRateId(rate.getExchangeRateId());
        }
        extPurInqSouCurrencyDAO.saveOrUpdate(inqProject.getProjectId(), currencyList, ExtPurInqSouCurrency::getProjectId);
    }

    /**
     * 保存简易询价额外的物料需求信息
     */
    @Override
    public void editRequireInfo(SouProject project, List<ApiPurInqSouItemDTO> itemList, @Nullable Long userId, boolean isTempSave) {
        // 1: 校验
        this.formatAndValidateRequires(project, itemList, isTempSave);
        // 要保存的简易询价物料行信息
        List<ExtPurInqSouItem> inqItemList = new ArrayList<>(itemList.size());
        for (ApiPurInqSouItemDTO itemDTO : itemList) {
            ExtPurInqSouItem inqItem = SouObjectXUtil.convertTargetObj(itemDTO, ExtPurInqSouItem.class);

            inqItemList.add(inqItem);
        }
        // 保存简易询价物料需求行信息
        extPurInqSouItemDAO.saveOrUpdateBatch(inqItemList);
    }

    private void formatAndValidateRequires(SouProject project, List<ApiPurInqSouItemDTO> itemList, boolean isTempSave) {
        int index = 0;
        for (ApiPurInqSouItemDTO item : itemList) {
            index++;
        }
    }

}
