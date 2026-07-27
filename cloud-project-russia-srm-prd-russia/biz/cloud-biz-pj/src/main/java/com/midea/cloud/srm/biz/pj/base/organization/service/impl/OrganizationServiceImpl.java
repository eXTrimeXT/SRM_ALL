package com.midea.cloud.srm.biz.pj.base.organization.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.common.utils.StringUtil;
import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.srm.biz.pj.base.organization.service.IOrganizationService;
import com.midea.cloud.srm.feign.pj.base.BaseExtClient;
import com.midea.cloud.srm.model.base.organization.dto.OrganizationOpenApiDTO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.base.organization.dto.OrganizationEditDto;
import com.midea.cloud.srm.model.pj.base.organization.entity.*;
import com.midea.cloud.srm.model.pj.changchengapi.dto.OrganizationApiDTO;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenUpdateWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * @author huangbf3
 * 组织业务实现类
 */
@Service
@Slf4j
public class OrganizationServiceImpl implements IOrganizationService {

    @Autowired
    private BaseExtClient baseExtClient;

    @Resource
    private QlOpenClient qlOpenClient;

    @Override
    public PageInfo<Organization> listAllOrganization(Organization organization) {
        PageUtil.startPage(organization.getPageNum(), organization.getPageSize());
        QlOpenQueryWrapper queryWrapper = QlOpenWrappers.query("Organization");
        if (StringUtils.isNotBlank(organization.getOrganizationTypeCode())) {
            queryWrapper.eq(Organization::getOrganizationTypeCode, organization.getOrganizationTypeCode());
        }
        if (organization.getOrganizationId() != null) {
            queryWrapper.ne(Organization::getOrganizationId, organization.getOrganizationId());
        }
        if (!StringUtil.isEmpty(organization.getOrganizationName())) {
            queryWrapper.contains(Organization::getOrganizationName, organization.getOrganizationName());
        }
        if (!StringUtil.isEmpty(organization.getParentOrganizationIds())) {
            queryWrapper.eq(Organization::getParentOrganizationIds, organization.getParentOrganizationIds());
        }
        if (!StringUtil.isEmpty(organization.getEnabled()) && organization.getEnabled().equals(Enable.Y.name())) {
            queryWrapper.le(Organization::getStartDate, LocalDate.now());
            queryWrapper.and(queryWrapperItem -> queryWrapperItem.gt(Organization::getEndDate, LocalDate.now()).or(true, null).isNull(Organization::getEndDate));
        } else if (!StringUtil.isEmpty(organization.getEnabled()) && organization.getEnabled().equals(Enable.N.name())) {
            queryWrapper.le(Organization::getEndDate, LocalDate.now());
        }
        if (!StringUtil.isEmpty(organization.getOrganizationRegion())) {
            queryWrapper.contains(Organization::getOrganizationRegion, organization.getOrganizationRegion());

        }
        queryWrapper.contains(StringUtils.isNotEmpty(organization.getOrganizationCode()), Organization::getOrganizationCode, organization.getOrganizationCode());
        queryWrapper.orderByDesc(Organization::getLastUpdateDate);

        log.info("queryWrapper:"+ JSONObject.toJSONString(queryWrapper));
        Page<Organization> page = qlOpenClient.query(ContextPath.BASE, queryWrapper, Long.valueOf(organization.getPageNum()),Long.valueOf(organization.getPageSize()), Organization.class);

        log.info("page:"+ JSONObject.toJSONString(page));

        for (Organization e : page.getRecords()) {
            if (StringUtils.isNotBlank(e.getParentOrganizationIds()) && !Objects.equals(e.getParentOrganizationIds(), "-1")) {
                try {
                    Organization p = qlOpenClient.read(ContextPath.BASE,"Organization", e.getParentOrganizationIds(), Organization.class);
                    if (p != null) {
                        e.setParentOrganizationNames(p.getOrganizationName());
                    }
                } catch (Exception ignored) {

                }
            }
        }
        log.info("page.getRecords:"+ JSONObject.toJSONString(page.getRecords()));
        PageInfo<Organization> pageResult = new PageInfo<>();
        pageResult.setList(page.getRecords());
        pageResult.setPageNum(page.getPageNum());
        pageResult.setPageSize(page.getPageSize());
        pageResult.setTotal(page.getTotal());
        pageResult.setPages(page.getPageCount());
        return pageResult;
    }

    @Override
    public OrganizationEditDto getOrganization(Long id) {
        /*组织 */
        Organization organization = qlOpenClient.read(ContextPath.BASE,"Organization", id.toString(), Organization.class);
        setParentOrganizationNames(organization);
        /* 调用中台接口获取子表信息 */
        QlOpenQueryWrapper siteWrapper = QlOpenWrappers.query("Site");
        siteWrapper.eq(true, "organizationId", id);
        List<Site> siteList = qlOpenClient.query(ContextPath.BASE, siteWrapper, Site.class);
        log.info(id+"");
        /* 公司 */
        QlOpenQueryWrapper companyWrapper = QlOpenWrappers.query("OrgCompany");
        companyWrapper.eq(true, OrgCompanyBank::getOrganizationId, id);
        List<OrgCompany> orgCompany = qlOpenClient.query(ContextPath.BASE, companyWrapper, OrgCompany.class);
        /* 公司银行账户 */
        QlOpenQueryWrapper backWrapper = QlOpenWrappers.query("OrgCompanyBank");
        backWrapper.eq(true, OrgCompanyBank::getOrganizationId, id);
        List<OrgCompanyBank> orgCompanyBankList = qlOpenClient.query(ContextPath.BASE, backWrapper, OrgCompanyBank.class);
        /* 公司地址 */
        QlOpenQueryWrapper addressWrapper = QlOpenWrappers.query("OrgCompanyAddress");
        addressWrapper.eq(true, OrgCompanyAddress::getOrganizationId, id);
        List<OrgCompanyAddress> orgCompanyAddressList = qlOpenClient.query(ContextPath.BASE, addressWrapper, OrgCompanyAddress.class);
        /* 公司联系人 */
        QlOpenQueryWrapper perWrapper = QlOpenWrappers.query("OrgCompanyPerson");
        perWrapper.eq(true, OrgCompanyPerson::getOrganizationId, id);
        List<OrgCompanyPerson> orgCompanyPersonList = qlOpenClient.query(ContextPath.BASE, perWrapper, OrgCompanyPerson.class);
        /* 开票信息 */
        QlOpenQueryWrapper invoiceWrapper = QlOpenWrappers.query("OrgInvoiceInfo");
        invoiceWrapper.eq(true, OrgInvoiceInfo::getOrganizationId, id);
        List<OrgInvoiceInfo> invoiceInfoList = qlOpenClient.query(ContextPath.BASE, invoiceWrapper, OrgInvoiceInfo.class);
        /* 收票信息con */
        QlOpenQueryWrapper collectWrapper = QlOpenWrappers.query("OrgCollectInfo");
        collectWrapper.eq(true, OrgCollectInfo::getOrganizationId, id);
        List<OrgCollectInfo> collectInfoList = qlOpenClient.query(ContextPath.BASE, collectWrapper, OrgCollectInfo.class);
        OrganizationEditDto organizationEditDto = new OrganizationEditDto();
        organizationEditDto.setSiteList(siteList);
        organizationEditDto.setOrgCompany(orgCompany != null && orgCompany.size() > 0 ? orgCompany.get(0) : new OrgCompany());
        organizationEditDto.setOrgCompanyBankList(orgCompanyBankList);
        organizationEditDto.setOrgCompanyAddressList(orgCompanyAddressList);
        organizationEditDto.setOrgCompanyPersonList(orgCompanyPersonList);
        organizationEditDto.setOrganization(organization);
        organizationEditDto.setOrgInvoiceInfoList(invoiceInfoList);
        organizationEditDto.setOrgCollectInfoList(collectInfoList);
        return organizationEditDto;
    }

    /**
     * 获取有开票信息的组织
     * @return 组织信息
     */
    @Override
    public List<Organization> findListFilterInvoiceInfo() {
        /* 开票信息 */
        QlOpenQueryWrapper invoiceWrapper = QlOpenWrappers.query("OrgInvoiceInfo");
        List<OrgInvoiceInfo> invoiceInfoList = qlOpenClient.query(ContextPath.BASE, invoiceWrapper, OrgInvoiceInfo.class);
        if (CollectionUtils.isNotEmpty(invoiceInfoList)) {
            Set<Long> orgId = invoiceInfoList.stream().map(OrgInvoiceInfo::getOrganizationId).collect(Collectors.toSet());
            QlOpenQueryWrapper queryWrapper = QlOpenWrappers.query("Organization");
            queryWrapper.in(Organization::getOrganizationId, new ArrayList<>(orgId));
            List<Organization> reList = qlOpenClient.query(ContextPath.BASE, queryWrapper, Organization.class);
            log.info(JSONObject.toJSONString(reList));
            return reList;
        } else {
            return new ArrayList<>();
        }
    }

    /** 给每个组织设置父组织名称 */
    private void setParentOrganizationNames(Organization organization) {
        if (organization == null) {
            return;
        };
        String parentOrganizationIds = organization.getParentOrganizationIds();
        if (StringUtils.isNotBlank(parentOrganizationIds)) {
            List<Long> parentIdList = StringUtil.stringConvertNumList(parentOrganizationIds, ",");
            /*从组织中取出父ID集,根据ID集查出对应父组织的名字再组合成字符串返回给前端. */
            QlOpenQueryWrapper wrapper = QlOpenWrappers.query("Organization");
            wrapper.in(Organization::getOrganizationId, parentIdList);
            List<Organization> parentOrganizations = qlOpenClient.query(ContextPath.BASE, wrapper, Organization.class);
            List<String> parentOrganizationNames = new ArrayList<>();
            for (Organization parentOrganization : parentOrganizations) {
                if (parentOrganization == null) {
                    continue;
                }
                parentOrganizationNames.add(parentOrganization.getOrganizationName());
            }
            organization.setParentOrganizationNames(String.join(",", parentOrganizationNames));
        }
    }

    /**
     * 新增或编辑组织
     * @param organizationEditDto 组织
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(OrganizationEditDto organizationEditDto) {
        Organization organization = organizationEditDto.getOrganization();
        Assert.notNull(organization, "organization不能为空");

        organization.getExtensions().remove("ceeaCurrencyId");

        List<OrgCompanyAddress> orgCompanyAddressList = organizationEditDto.getOrgCompanyAddressList();
        List<OrgCompanyBank> orgCompanyBankList = organizationEditDto.getOrgCompanyBankList();
        List<OrgCompanyPerson> orgCompanyPersonList = organizationEditDto.getOrgCompanyPersonList();
        List<Site> siteList = organizationEditDto.getSiteList();
        if (CollectionUtils.isNotEmpty(orgCompanyAddressList)) {
            long amount = orgCompanyAddressList.stream().filter(item -> Enable.Y.name().equals(item.getIsActive())).count();
            if (amount > 1) {
                throw new BaseException("地址信息只能有1个启用");
            }
            long amount2 = orgCompanyAddressList.stream().filter(item -> Enable.Y.name().equals(item.getIsDefault())).count();
            if (amount2 > 1) {
                throw new BaseException("地址信息只能有1个默认");
            }
        }
        if (CollectionUtils.isNotEmpty(orgCompanyPersonList)) {
            long amount = orgCompanyPersonList.stream().filter(item -> Enable.Y.equals(item.getIsDefault())).count();
            if (amount > 1) {
                throw new BaseException("联系人只能有1个默认人");
            }

            amount = orgCompanyPersonList.stream().filter(item -> item.getIsActive() == null).count();
            if (amount >= 1) {
                throw new BaseException("联系人激活状态不能为空");
            }
        }
        if (CollectionUtils.isNotEmpty(orgCompanyBankList)) {
            long amount = orgCompanyBankList.stream().filter(item -> Enable.Y.equals(item.getIsMain())).count();
            if (amount > 1) {
                throw new BaseException("账户信息只能有一个主账号");
            }

            amount = orgCompanyPersonList.stream().filter(item -> item.getIsActive() == null).count();
            if (amount >= 1) {
                throw new BaseException("账户信息激活状态不能为空");
            }
        }
        if (CollectionUtils.isNotEmpty(siteList)) {
            long siteNameAmount = siteList.stream().map(Site::getSiteName).distinct().count();
            if (siteNameAmount != siteList.size()) {
                throw new BaseException("收货地址名称不能一样");
            }
            long amount2 = siteList.stream().filter(item -> Enable.Y.name().equals(item.getIsDefault())).count();
            if (amount2 > 1) {
                throw new BaseException("收货信息只能有1个默认");
            }
        }
        if (CollectionUtils.isNotEmpty(organizationEditDto.getOrgCollectInfoList())) {
            long amount = organizationEditDto.getOrgCollectInfoList().stream().filter(item -> Enable.Y.name().equals(item.getIsDefault())).count();
            if (amount > 1) {
                throw new BaseException("收票信息只能有1个默认");
            }
        }
        /*保存组织和组织关系 */
        saveOrUpdateOrganization(organization);

        /*保存地址列表 */
        Long organizationId = organization.getOrganizationId();
        if (CollectionUtils.isNotEmpty(siteList)) {
            organizationEditDto.getSiteList().forEach(item -> {
                item.setOrganizationId(organizationId);
                item.setOrganizationCode(organization.getOrganizationCode());
                item.setOrganizationName(organization.getOrganizationName());
            });
            if (organizationId != null) {
                QlOpenQueryWrapper siteWrapper = QlOpenWrappers.query("Site");
                siteWrapper.eq(Site::getOrganizationId, organizationId);
                List<Site> selectSiteList = qlOpenClient.query(ContextPath.BASE, siteWrapper, Site.class);
                if (CollectionUtils.isNotEmpty(selectSiteList)) {
                    List<Long> selectIds = selectSiteList.stream().map(Site::getSiteId).collect(Collectors.toList());
                    List<Long> addIds = siteList.stream().map(Site::getSiteId).collect(Collectors.toList());
                    getDifferenceSet("Site", addIds, selectIds);
                }
            }
            log.info("地址的数据信息==={}", JSONObject.toJSONString(siteList));
            qlOpenClient.save(ContextPath.BASE, "Site", siteList);
        } else {
            QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("Site").eq(Site::getOrganizationId, organizationId);
            qlOpenClient.delete(ContextPath.BASE,wrapper);
        }
        /* 保存公司信息 */
        OrgCompany orgCompany = organizationEditDto.getOrgCompany();
        if (orgCompany != null) {
            orgCompany.setOrganizationId(organizationId);
            qlOpenClient.save(ContextPath.BASE, "OrgCompany", Collections.singletonList(orgCompany));
        }

        if (CollectionUtils.isNotEmpty(orgCompanyAddressList)) {
            orgCompanyAddressList.forEach(item -> item.setOrganizationId(organizationId));
            if (organizationId != null) {
                QlOpenQueryWrapper addressWrapper = QlOpenWrappers.query("OrgCompanyAddress");
                addressWrapper.eq(OrgCompanyAddress::getOrganizationId, organizationId);
                List<OrgCompanyAddress> selectCompanyAddressList = qlOpenClient.query(ContextPath.BASE, addressWrapper, OrgCompanyAddress.class);
                if (CollectionUtils.isNotEmpty(selectCompanyAddressList)) {
                    List<Long> selectIds = selectCompanyAddressList.stream().map(OrgCompanyAddress::getCompanyAddressId).collect(Collectors.toList());
                    List<Long> addIds = orgCompanyAddressList.stream().map(OrgCompanyAddress::getCompanyAddressId).collect(Collectors.toList());
                    getDifferenceSet("OrgCompanyAddress", addIds, selectIds);
                }
            }
            qlOpenClient.save(ContextPath.BASE, "OrgCompanyAddress", organizationEditDto.getOrgCompanyAddressList());
        } else {
            QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("OrgCompanyAddress").eq(OrgCompanyAddress::getOrganizationId, organizationId);
            qlOpenClient.delete(ContextPath.BASE, wrapper);
        }
        if (CollectionUtils.isNotEmpty(orgCompanyBankList)) {
            orgCompanyBankList.forEach(item -> item.setOrganizationId(organizationId));
            if (organizationId != null) {
                QlOpenQueryWrapper addressWrapper = QlOpenWrappers.query("OrgCompanyBank");
                addressWrapper.eq(OrgCompanyBank::getOrganizationId, organizationId);
                List<OrgCompanyBank> selectCompanyAddressList = qlOpenClient.query(ContextPath.BASE, addressWrapper, OrgCompanyBank.class);
                if (CollectionUtils.isNotEmpty(selectCompanyAddressList)) {
                    List<Long> selectIds = selectCompanyAddressList.stream().map(OrgCompanyBank::getCompanyBankId).collect(Collectors.toList());
                    List<Long> addIds = orgCompanyBankList.stream().map(OrgCompanyBank::getCompanyBankId).collect(Collectors.toList());
                    getDifferenceSet("OrgCompanyBank", addIds, selectIds);
                }
            }
            qlOpenClient.save(ContextPath.BASE, "OrgCompanyBank", orgCompanyBankList);
        } else {
                QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("OrgCompanyBank").eq(OrgCompanyBank::getOrganizationId, organizationId);
                qlOpenClient.delete(ContextPath.BASE, wrapper);
        }
        if (CollectionUtils.isNotEmpty(orgCompanyPersonList)) {
            orgCompanyPersonList.forEach(item -> item.setOrganizationId(organizationId));
            if (organizationId != null) {
                QlOpenQueryWrapper perWrapper = QlOpenWrappers.query("OrgCompanyPerson");
                perWrapper.eq(OrgCompanyPerson::getOrganizationId, organizationId);
                List<OrgCompanyPerson> selectCompanyPersonList = qlOpenClient.query(ContextPath.BASE, perWrapper, OrgCompanyPerson.class);
                if (CollectionUtils.isNotEmpty(selectCompanyPersonList)) {
                    List<Long> selectIds = selectCompanyPersonList.stream().map(OrgCompanyPerson::getCompanyPersonId).collect(Collectors.toList());
                    List<Long> addIds = orgCompanyPersonList.stream().map(OrgCompanyPerson::getCompanyPersonId).collect(Collectors.toList());
                    getDifferenceSet("OrgCompanyPerson", addIds, selectIds);
                }
            }
            qlOpenClient.save(ContextPath.BASE, "OrgCompanyPerson", orgCompanyPersonList);
        } else {
            QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("OrgCompanyPerson").eq(OrgCompanyPerson::getOrganizationId, organizationId);
            qlOpenClient.delete(ContextPath.BASE, wrapper);
        }
        /*开票信息 */
        if (CollectionUtils.isNotEmpty(organizationEditDto.getOrgInvoiceInfoList())) {
            organizationEditDto.getOrgInvoiceInfoList().forEach(e -> e.setOrganizationId(organizationId));
            if (organizationId != null) {
                QlOpenQueryWrapper invoiceWrapper = QlOpenWrappers.query("OrgInvoiceInfo");
                invoiceWrapper.eq(OrgInvoiceInfo::getOrganizationId, organizationId);
                List<OrgInvoiceInfo> selectInvoiceInfoList = qlOpenClient.query(ContextPath.BASE, invoiceWrapper, OrgInvoiceInfo.class);
                if (CollectionUtils.isNotEmpty(selectInvoiceInfoList)) {
                    List<Long> selectIds = selectInvoiceInfoList.stream().map(OrgInvoiceInfo::getInvoiceId).collect(Collectors.toList());
                    List<Long> addIds = organizationEditDto.getOrgInvoiceInfoList().stream().map(OrgInvoiceInfo::getInvoiceId).collect(Collectors.toList());
                    getDifferenceSet("OrgInvoiceInfo", addIds, selectIds);
                }
            }
            qlOpenClient.save(ContextPath.BASE, "OrgInvoiceInfo", organizationEditDto.getOrgInvoiceInfoList());
        } else {
            QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("OrgInvoiceInfo").eq(OrgInvoiceInfo::getOrganizationId, organizationId);
            qlOpenClient.delete(ContextPath.BASE, wrapper);
        }
        /*收票信息 */
        if (CollectionUtils.isNotEmpty(organizationEditDto.getOrgCollectInfoList())) {
            organizationEditDto.getOrgCollectInfoList().forEach(e -> e.setOrganizationId(organizationId));
            if (organizationId != null) {
                QlOpenQueryWrapper collectWrapper = QlOpenWrappers.query("OrgCollectInfo");
                collectWrapper.eq(OrgCollectInfo::getOrganizationId, organizationId);
                List<OrgCollectInfo> selectCollectInfoList = qlOpenClient.query(ContextPath.BASE, collectWrapper, OrgCollectInfo.class);
                if (CollectionUtils.isNotEmpty(selectCollectInfoList)) {
                    List<Long> selectIds = selectCollectInfoList.stream().map(OrgCollectInfo::getCollectId).collect(Collectors.toList());
                    List<Long> addIds = organizationEditDto.getOrgCollectInfoList().stream().map(OrgCollectInfo::getCollectId).collect(Collectors.toList());
                    getDifferenceSet("OrgCollectInfo", addIds, selectIds);
                }
            }
            qlOpenClient.save(ContextPath.BASE, "OrgCollectInfo", organizationEditDto.getOrgCollectInfoList());
        } else {
            QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("OrgCollectInfo").eq(OrgCollectInfo::getOrganizationId, organizationId);
            qlOpenClient.delete(ContextPath.BASE, wrapper);
        }
    }

    public void getDifferenceSet(String type, List<Long> addList, List<Long> selectList) {
        List<Long> list = selectList.stream().filter(item -> item != null && !addList.contains(item)).collect(Collectors.toList());
        log.info("地址的数据信息list==={}", JSONObject.toJSONString(list));
        if (list.size() > 0) {
            qlOpenClient.delete(ContextPath.BASE, type, list);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateOrganization(Organization organization) {
        Assert.notNull(organization, "organization不能为空");
        /*根据组织名称获取对应的erp库存组织ID、库存组织Code、业务实体ID和业务实体Code、地点ID、地点名称等相关 页面传类 */
        String oldOrganizationCode = organization.getOrganizationCode();
        if (StringUtils.isBlank(organization.getOrganizationCode())) {
            organization.setOrganizationCode(oldOrganizationCode);
        }
        if (organization.getOrganizationId() != null) {
            /*判断组织编码 */
            QlOpenQueryWrapper organizationWrapper = QlOpenWrappers.query("Organization");
            organizationWrapper.eq(Organization::getOrganizationCode, organization.getOrganizationCode());
            organizationWrapper.ne(Organization::getOrganizationId, organization.getOrganizationId());
            List<Organization> organizationList = qlOpenClient.query(ContextPath.BASE, organizationWrapper, Organization.class);
            if (organizationList != null && organizationList.size() > 0) {
                throw new BaseException("组织编码已存在");
            }
            QlOpenUpdateWrapper wrapper = QlOpenWrappers.update("Organization")
                    .eq(Organization::getOrganizationId, organization.getOrganizationId())
                    .set(Organization::getOrganizationTypeId, organization.getOrganizationTypeId())
                    .set(Organization::getOrganizationTypeCode, organization.getOrganizationTypeCode())
                    .set(Organization::getOrganizationTypeName, organization.getOrganizationTypeName())
                    .set(Organization::getOrganizationName, organization.getOrganizationName())
                    .set(Organization::getParentOrganizationIds, StringUtils.isNotBlank(organization.getParentOrganizationIds())?organization.getParentOrganizationIds():"-1")
                    .set(Organization::getParentOrganizationNames, organization.getParentOrganizationNames())
                    .set(Organization::getStartDate, organization.getStartDate())
                    .set(Organization::getEndDate, organization.getEndDate())
                    .set(Organization::getErpOrgId, organization.getErpOrgId())
                    .set(Organization::getOrganizationRegion, organization.getOrganizationRegion())
                    .set(Organization::getDefaultInv, organization.getDefaultInv());
            qlOpenClient.update(ContextPath.BASE, wrapper);

            QlOpenUpdateWrapper relWrapper = QlOpenWrappers.update("OrganizationRelation")
                    .eq(OrganizationRelation::getOrganizationId, organization.getOrganizationId())
                    .set(OrganizationRelation::getParentOrganizationId, StringUtils.isNotBlank(organization.getParentOrganizationIds())?organization.getParentOrganizationIds(): "-1");
            qlOpenClient.update(ContextPath.BASE, relWrapper);
        } else {
            addOrganization(organization);
            addOrganizationRelations(organization);
        }
    }

    private void addOrganization(Organization organization) {
        Assert.hasText(organization.getOrganizationCode(), "组织编码不能为空");
        Assert.hasText(organization.getOrganizationName(), "组织名称不能为空");
        Assert.notNull(organization.getOrganizationTypeId(), "组织类型id不能为空");
        Assert.hasText(organization.getOrganizationTypeCode(), "组织类型编码不能为空");
        Assert.hasText(organization.getOrganizationTypeName(), "组织类型名称不能为空");
        Assert.notNull(organization.getStartDate(), "生效日期不能为空");
        /*判断组织编码 */
        QlOpenQueryWrapper organizationWrapper = QlOpenWrappers.query("Organization");
        organizationWrapper.eq(Organization::getOrganizationCode, organization.getOrganizationCode());
        List<Organization> organizationList = qlOpenClient.query(ContextPath.BASE, organizationWrapper, Organization.class);
        if (organizationList != null && organizationList.size() > 0) {
            throw new BaseException("组织编码已存在");
        }
        /*设置组织常用的属性 */
        organization.setEnabled(Enable.Y.toString());
        organization.setDataResource("MANUAL");
        if (StringUtils.isBlank(organization.getParentOrganizationIds())) {
            organization.setParentOrganizationIds("-1");
        }
        try {
            List<Serializable> keys = qlOpenClient.save(ContextPath.BASE,"Organization", Arrays.asList(organization));
            assertTrue(CollectionUtils.isNotEmpty(keys));
            organization.setOrganizationId((Long) keys.get(0));
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            if (cause instanceof SQLIntegrityConstraintViolationException) {
                String errMsg = cause.getMessage();
                String organizationNameText = "ORGANIZATION_NAME";
                String organizationCodeText = "ORGANIZATION_CODE";
                if (StringUtils.isNotBlank(errMsg) && errMsg.contains(organizationNameText)) {
                    throw new BaseException("组织名称已存在,无需再添加,请编辑");
                } else if (StringUtils.isNotBlank(errMsg) && errMsg.contains(organizationCodeText)) {
                    throw new BaseException("组织编码已存在,无需再添加,请编辑");
                }
            }
        }
    }

    private void addOrganizationRelations(Organization organization) {
        /*过滤organizationId为null的情况 */
        String orgIds = organization.getParentOrganizationIds();
        if (StringUtils.isNotBlank(orgIds)) {
            OrganizationRelation organizationRelation = new OrganizationRelation();
            organizationRelation.setOrganizationId(organization.getOrganizationId());
            organizationRelation.setParentOrganizationId(Long.valueOf(organization.getParentOrganizationIds() != null?organization.getParentOrganizationIds(): "-1"));
            /*添加顶级组织 */
            qlOpenClient.save(ContextPath.BASE,"OrganizationRelation", Arrays.asList(organizationRelation));
        }
    }

    @Override
    public void saveOrganizationBatch(List<OrganizationApiDTO> organizationApiDtos) {
        String orgTypeCode = "";
        List<OrganizationOpenApiDTO> organizationList = getOrganizationOpenApiDTO(organizationApiDtos);
        baseExtClient.saveOrganizationBatch(orgTypeCode,organizationList);
    }

    /**
     * 从长城api接口返回字段获取批量保存或更新产品接口数据
     * @param organizationApiDtos
     * @return
     */
    private List<OrganizationOpenApiDTO> getOrganizationOpenApiDTO(List<OrganizationApiDTO> organizationApiDtos) {
        List<OrganizationOpenApiDTO> organizationList = new ArrayList<>();
        return organizationList;
    }


}
