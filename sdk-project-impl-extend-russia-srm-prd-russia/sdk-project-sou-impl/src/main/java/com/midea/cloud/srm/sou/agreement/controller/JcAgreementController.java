package com.midea.cloud.srm.sou.agreement.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.listener.AnalysisEventListenerImpl;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.feign.SupplierCooperateClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseUnit;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelves;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.agreement.dto.LinePageDto;
import com.midea.cloud.srm.model.sou.agreement.dto.PriceAgreementDTO;
import com.midea.cloud.srm.model.sou.agreement.dto.PriceAgreementQueryDTO;
import com.midea.cloud.srm.model.sou.agreement.dto.SccSouJcAgreementDto;
import com.midea.cloud.srm.model.sou.agreement.entity.*;
import com.midea.cloud.srm.model.sou.agreement.enums.AgreementStatusEnums;
import com.midea.cloud.srm.model.sou.agreement.excel.ExportJcHtHeadLineData;
import com.midea.cloud.srm.model.sou.agreement.excel.ImportExcelJcAgreementLineDto;
import com.midea.cloud.srm.model.sou.agreement.excel.ImportExcelJcAgreementLineEditDto;
import com.midea.cloud.srm.model.sou.agreement.excel.ImportExcelJcAgreementLineTieredDto;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.enums.MallTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.ExternalMaterial;
import com.midea.cloud.srm.model.supcooperate.ext.catalogonshelvess.ExtCatalogOnShelvesStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenUpdateWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.agreement.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Api(value = "JcAgreementController", tags = {"集采协议管理"})
@RestController
@Slf4j
@RequestMapping("/jcAgreement")
public class JcAgreementController {
    @Resource
    private JcAgreementService agreementService;

    @Resource
    private JcAgreementInfoService agreementInfoService;

    @Resource
    private JcAgreementOrgService jcAgreementOrgService;

    @Resource
    private TieredPricingService tieredPricingService;

    @Resource
    private JcAgreementChangeService jcAgreementChangeService;

    @Resource
    private BaseClient baseClient;

    @Resource
    private SupplierCooperateClient supplierCooperateClient;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Resource
    private QlOpenClient qlOpenClient;



    private static final String JCXY = "集采协议";
    private static final String HTXY = "合同协议";


    @ApiOperation(value = "获取集采协议管理列表", notes = "获取集采协议管理列表", httpMethod = "POST")
    @PostMapping("/getJcAgreementPageList")
    public PageInfo<SccSouJcAgreement> getJcAgreementPageList(@RequestBody SccSouJcAgreementDto ja) {
        if (StringUtils.isBlank(ja.getAgreementType())) {
            throw new BaseException("协议类型不能为空");
        }
        /*if (ja.getBuyOrgId() == null) {
            ja.setBuyOrgId(-1L);
        }
        if (StringUtils.isBlank(ja.getMaterialCode())) {
            ja.setMaterialCode("不存在");
        }
        List<Long> ids = agreementService.agreementIdList(ja);
        if (CollectionUtils.isNotEmpty(ids)) {
            ja.setAgreementIds(ids);
        }*/
        PageUtil.startPage(ja.getPageNum(), ja.getPageSize());
        List<SccSouJcAgreement> agreementLists = agreementService.agreementList(ja);
        return new PageInfo<>(agreementLists);
    }

    @ApiOperation(value = "添加或更新集采管理", notes = "添加或更新集采管理", httpMethod = "POST")
    @PostMapping("/saveOrUpdateJcAgreement")
    public SccSouJcAgreement saveOrUpdateJcAgreement(@RequestBody SccSouJcAgreement sccSouJcAgreement) {
        checkAddOrUpdate(sccSouJcAgreement);
        setCompanyInfo(sccSouJcAgreement);
        if (sccSouJcAgreement.getAgreementId() != null) {
            SccSouJcAgreement ag = agreementService.getById(sccSouJcAgreement.getAgreementId());
            if (!AgreementStatusEnums.DRAFT.getCode().equals(ag.getAgreementStatus())) {
                throw new BaseException("非拟定状态不可编辑");
            }
        }

        sccSouJcAgreement.setAgreementStatus(AgreementStatusEnums.DRAFT.getCode());
        agreementService.saveOrUpdate(sccSouJcAgreement);
        List<SccSouJcAgreementOrg> sccSouJcAgreementOrgList;
        if (YesOrNo.YES.getValue().equals(sccSouJcAgreement.getDefaultAll())) {
            List<SccSouJcAgreementOrg> ol = new ArrayList<>();
            Organization organization = new Organization();
            organization.setOrganizationTypeCode("OU");
            List<Organization> orgList = baseClient.listOrganizationByParam(organization);
            for (Organization e : orgList) {
                SccSouJcAgreementOrg ao = new SccSouJcAgreementOrg();
                ao.setBuyOrgId(e.getOrganizationId());
                ao.setBuyOrgCode(e.getOrganizationCode());
                ao.setBuyOrgName(e.getOrganizationName());
                ol.add(ao);
            }
            sccSouJcAgreementOrgList = ol;
        } else if (YesOrNo.NO.getValue().equals(sccSouJcAgreement.getDefaultAll())) {
            sccSouJcAgreementOrgList = sccSouJcAgreement.getSccSouJcAgreementOrgList();
        } else {
            sccSouJcAgreementOrgList = new ArrayList<>();
        }
        List<SccSouJcAgreementInfo> sccSouJcAgreementInfoList = sccSouJcAgreement.getSccSouJcAgreementInfoList();
        if (CollectionUtils.isNotEmpty(sccSouJcAgreementOrgList)) {
            List<Long> aoList = new ArrayList<>();
            sccSouJcAgreementOrgList.forEach(e -> {
                if (e.getAgreementOrgId() != null) {
                    aoList.add(e.getAgreementOrgId());
                }
                e.setAgreementId(sccSouJcAgreement.getAgreementId());
            });
            LambdaQueryWrapper<SccSouJcAgreementOrg> orgLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orgLambdaQueryWrapper.eq(SccSouJcAgreementOrg::getAgreementId, sccSouJcAgreement.getAgreementId());
            List<SccSouJcAgreementOrg> orgList = jcAgreementOrgService.list(orgLambdaQueryWrapper);
            List<Long> agrOrgList = orgList.stream().map(SccSouJcAgreementOrg::getAgreementOrgId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(aoList) && !new HashSet<>(agrOrgList).containsAll(aoList)) {
                orgLambdaQueryWrapper.notIn(SccSouJcAgreementOrg::getAgreementOrgId, aoList);
                jcAgreementOrgService.remove(orgLambdaQueryWrapper);
            }
            jcAgreementOrgService.saveOrUpdateBatch(sccSouJcAgreementOrgList);
        } else {
            LambdaQueryWrapper<SccSouJcAgreementOrg> orgLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orgLambdaQueryWrapper.eq(SccSouJcAgreementOrg::getAgreementId, sccSouJcAgreement.getAgreementId());
            List<SccSouJcAgreementOrg> orgList = jcAgreementOrgService.list(orgLambdaQueryWrapper);
            if (CollectionUtils.isNotEmpty(orgList)) {
                jcAgreementOrgService.remove(orgLambdaQueryWrapper);
            }
        }
        if (CollectionUtils.isNotEmpty(sccSouJcAgreementInfoList)) {
            List<Long> infoIdList = new ArrayList<>();
            for (SccSouJcAgreementInfo e : sccSouJcAgreementInfoList) {
                if (e.getAgreementInfoId() != null) {
                    infoIdList.add(e.getAgreementInfoId());
                }
                e.setAgreementId(sccSouJcAgreement.getAgreementId());
                setLineNum(e, sccSouJcAgreement.getAgreementId());
            }
            LambdaQueryWrapper<SccSouJcAgreementInfo> infoQuery = new LambdaQueryWrapper<>();
            infoQuery.eq(SccSouJcAgreementInfo::getAgreementId, sccSouJcAgreement.getAgreementId());
            List<SccSouJcAgreementInfo> infoList = agreementInfoService.list(infoQuery);
            List<Long> infoIds = infoList.stream().map(SccSouJcAgreementInfo::getAgreementInfoId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(infoIds)) {
                if (CollectionUtils.isNotEmpty(infoIdList)) {
                    infoQuery.notIn(SccSouJcAgreementInfo::getAgreementInfoId, infoIdList);
                }
                agreementInfoService.remove(infoQuery);
                List<Long> priIds = infoIds.stream().filter(element -> !infoIdList.contains(element)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(priIds)) {
                    LambdaQueryWrapper<SccSouTieredPricing> ti = new LambdaQueryWrapper<>();
                    ti.in(SccSouTieredPricing::getAgreementInfoId, priIds);
                    tieredPricingService.remove(ti);
                }
            }
            for (SccSouJcAgreementInfo e : sccSouJcAgreementInfoList) {
                agreementInfoService.saveOrUpdate(e);
                List<SccSouTieredPricing> sccSouTieredPricingList = e.getSccSouTieredPricingList();
                if (CollectionUtils.isNotEmpty(sccSouTieredPricingList)) {
                    List<Long> tpList = new ArrayList<>();
                    for (SccSouTieredPricing a : sccSouTieredPricingList) {
                        if (a.getAgreementInfoId() != null) {
                            tpList.add(a.getTieredPricingId());
                        }
                        a.setAgreementInfoId(e.getAgreementInfoId());
                    }
                    tieredPricingService.saveOrUpdateBatch(sccSouTieredPricingList);
                    LambdaQueryWrapper<SccSouTieredPricing> ti = new LambdaQueryWrapper<>();
                    ti.eq(SccSouTieredPricing::getAgreementInfoId, e.getAgreementInfoId());
                    List<SccSouTieredPricing> list = tieredPricingService.list(ti);
                    List<Long> pList = list.stream().map(SccSouTieredPricing::getTieredPricingId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(pList) && !new HashSet<>(pList).containsAll(tpList)) {
                        ti.notIn(SccSouTieredPricing::getTieredPricingId, tpList);
                        tieredPricingService.remove(ti);
                    }
                } else {
                    LambdaQueryWrapper<SccSouTieredPricing> ti = new LambdaQueryWrapper<>();
                    ti.eq(SccSouTieredPricing::getAgreementInfoId, e.getAgreementInfoId());
                    List<SccSouTieredPricing> list = tieredPricingService.list(ti);
                    if (CollectionUtils.isNotEmpty(list)) {
                        tieredPricingService.remove(ti);
                    }
                }
            }
        } else {
            LambdaQueryWrapper<SccSouJcAgreementInfo> orgLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orgLambdaQueryWrapper.eq(SccSouJcAgreementInfo::getAgreementId, sccSouJcAgreement.getAgreementId());
            List<SccSouJcAgreementInfo> infoList = agreementInfoService.list(orgLambdaQueryWrapper);
            if (CollectionUtils.isNotEmpty(infoList)) {
                List<Long> infoIds = infoList.stream().map(SccSouJcAgreementInfo::getAgreementInfoId).collect(Collectors.toList());
                agreementInfoService.removeByIds(infoIds);
                LambdaQueryWrapper<SccSouTieredPricing> ti = new LambdaQueryWrapper<>();
                ti.in(SccSouTieredPricing::getAgreementInfoId, infoIds);
                tieredPricingService.remove(ti);
            }
        }
        return sccSouJcAgreement;
    }

    public void setLineNum(SccSouJcAgreementInfo info, Long aId) {
        Integer al = agreementService.getMaxLineInfoNum(aId);
        int a = 1;
        if (al != null) {
            a = al + 1;
        }
        info.setMaterialLine(a);
    }

    private void setCompanyInfo(SccSouJcAgreement sccSouJcAgreement) {
        if(ObjectUtil.isEmpty(sccSouJcAgreement.getAgreementId())){
            LoginAppUser appUser = AppUserUtil.getLoginAppUser();
            HrUserOrgnizationDto userOrganization = pjProjectExtClient.getHrUserOrgnizationByUsername(appUser.getUsername());
            //公司
            Organization ouOrganization = userOrganization.getOuOrganization();
            //板块
            Organization buOrganization = userOrganization.getBuOrganization();
            //部门
            Organization departmentOrganization = userOrganization.getDepartmentOrganization();
            //设置公司
            if(ObjectUtil.isNotNull(ouOrganization)){
                sccSouJcAgreement.setHrCompanyId(ouOrganization.getOrganizationId());
                sccSouJcAgreement.setHrCompanyCode(ouOrganization.getOrganizationCode());
                sccSouJcAgreement.setHrCompanyName(ouOrganization.getOrganizationName());
            }
            //设置板块
            if(ObjectUtil.isNotNull(buOrganization)){
                sccSouJcAgreement.setHrSectorId(buOrganization.getOrganizationId());
                sccSouJcAgreement.setHrSectorName(buOrganization.getOrganizationName());
                sccSouJcAgreement.setHrSectorCode(buOrganization.getOrganizationCode());
            }
            //设置部门
            if(ObjectUtil.isNotNull(departmentOrganization)){
                sccSouJcAgreement.setHrDeptId(departmentOrganization.getOrganizationId());
                sccSouJcAgreement.setHrDeptCode(departmentOrganization.getOrganizationName());
                sccSouJcAgreement.setHrDeptName(departmentOrganization.getOrganizationCode());
            }

        }
    }

    /**
     * 商城接口
     * @param agreementId 入参
     */
    public void addMallInfo(Long agreementId) {
        SccSouJcAgreement agreement = agreementService.getById(agreementId);
        List<SccSouJcAgreementOrg> orgList = jcAgreementOrgService.lambdaQuery().eq(SccSouJcAgreementOrg::getAgreementId, agreementId).list();
        String orgId = orgList.stream().map(SccSouJcAgreementOrg::getBuyOrgId).map(Object::toString).collect(Collectors.joining(","));
        String orgCode = orgList.stream().map(SccSouJcAgreementOrg::getBuyOrgCode).collect(Collectors.joining(","));
        String orgName = orgList.stream().map(SccSouJcAgreementOrg::getBuyOrgName).collect(Collectors.joining(","));
        List<SccSouJcAgreementInfo> agreementInfoList = agreementInfoService.lambdaQuery().eq(SccSouJcAgreementInfo::getAgreementId, agreementId).list();
        log.info("生成商品数据,查询的数据==={}", JSONObject.toJSONString(agreementInfoList));
        //供应区域
        List<DictItemDTO> gyqyList = baseClient.listAllByDictCode("REGION");
        List<PurchaseUnit> unitList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query(PurchaseUnit.class)
                .eq(PurchaseUnit::getEnabled, YesOrNo.YES.getValue()), PurchaseUnit.class);
        Map<String, String> unitMap = unitList.stream()
                .collect(Collectors.toMap(PurchaseUnit::getUnitCode, PurchaseUnit::getUnitName));
        //商城上下架集合
        List<Record> creatRecords = new ArrayList<>();
        List<Record> updateRecords = new ArrayList<>();
        List<SccSouJcAgreementInfo> updateSouJcAgreementInfoList = new ArrayList<>();
        for (SccSouJcAgreementInfo info : agreementInfoList) {
            Record record = new Record();
            record.put("catalogOnShelvesId", info.getCatalogOnShelvesId());
            record.put("vendorId", agreement.getSupId());
            record.put("vendorCode", agreement.getSupCode());
            record.put("vendorName", agreement.getSupName());
            record.put("weight", null);
            //无
            record.put("currencyId", 0);
            record.put("brand", info.getBrand());
            record.put("priceLibraryId", agreement.getAgreementId());
            record.put("priceLibraryNo", agreement.getAgreementCode());
            record.put("extOrgIdList", orgId);
            record.put("extOrgCodeList", orgCode);
            record.put("extOrgNameList", orgName);
            record.put("organizationId", null);
            record.put("organizationCode", null);
            record.put("organizationName", null);
            record.put("categoryId", info.getGoodsTypeId());
            record.put("categoryCode", info.getGoodsTypeCode());
            record.put("categoryName", info.getGoodsTypeName());
            record.put("materialId", info.getMaterialId());
            record.put("materialCode", info.getMaterialCode());
            record.put("materialName", info.getMaterialName());
            record.put("taxRate", info.getTaxRate());
            record.put("taxKey", null);
            record.put("unitCode", info.getUnit());
            record.put("unit", unitMap.get(info.getUnit()));
            record.put("size", null);
            record.put("specification", info.getStandards());
            //无
            record.put("currencyCode", agreement.getCurrencyType());
            record.put("currencyName", agreement.getCurrencyType());
            record.put("userType", null);
            record.put("color", null);
            record.put("effectiveDate", agreement.getEffectiveStartDate());
            record.put("expirationDate", agreement.getEffectiveEndDate());
            record.put("deliveryCycle", info.getLeadTime());
            record.put("orderQuantityMinimum", info.getStartNum());
            record.put("taxPrice", info.getRatePrice());
            record.put("notaxPrice", info.getPriceTax());
            record.put("extPriceLibraryStatus", agreement.getAgreementStatus());
            record.put("extAgreementType", agreement.getAgreementType());
            List<Long> extAreaId = new ArrayList<>();
            List<String> extAreaCode = new ArrayList<>();
            List<String> extAreaName = new ArrayList<>();
            String[] ar = agreement.getSupplyArea().split(",");
            for (String a : ar) {
                for (DictItemDTO b : gyqyList) {
                    if (a.equals(b.getDictItemCode())) {
                        extAreaId.add(b.getDictItemId());
                        extAreaCode.add(a);
                        extAreaName.add(b.getDictItemName());
                    }
                }
            }
            record.put("extAreaId", extAreaId.stream().map(String::valueOf).collect(Collectors.joining(",")));
            record.put("extAreaCode", String.join(",", extAreaCode));
            record.put("extAreaName", String.join(",", extAreaName));
            record.put("extShelfLife", info.getSellByDate());
            record.put("extProtocolRowNo", info.getMaterialLine());
            record.put("extReferencePrice", info.getReferencePrice());
            record.put("mixAmount", agreement.getMixAmount());
            log.info("生成商品数据==={}", JSONObject.toJSONString(record));
            boolean isCreate = ObjectUtil.isEmpty(record.get("catalogOnShelvesId"));
            if (isCreate) {
                //主键
                record.put("catalogOnShelvesId", IdGenrator.generate());
                creatRecords.add(record);
            } else {
                updateRecords.add(record);
            }
            //构造更新集合
            SccSouJcAgreementInfo updateSccSouJcAgreementInfo = new SccSouJcAgreementInfo();
            updateSccSouJcAgreementInfo.setAgreementInfoId(info.getAgreementInfoId());
            updateSccSouJcAgreementInfo.setCatalogOnShelvesId(record.getLong("catalogOnShelvesId"));
            updateSouJcAgreementInfoList.add(updateSccSouJcAgreementInfo);
        }
        //商城上下架数据保存
        List<Serializable> reList = supplierCooperateClient.create(creatRecords);
        List<Serializable> updateList = supplierCooperateClient.update(creatRecords);
        log.info("生成商品数据,返回的数据==={}", JSONObject.toJSONString(reList));
        log.info("更新商品数据,返回的数据==={}", JSONObject.toJSONString(updateList));
        //回写上下架商品主键
        agreementInfoService.updateBatchById(updateSouJcAgreementInfoList);
    }


    public static Boolean decide(String clo) {
        return StringUtils.isBlank(clo);
    }

    public void checkAddOrUpdate(SccSouJcAgreement jcAgreement) {
        if (jcAgreement == null) {
            throw new BaseException("参数不能为空");
        }
        //协议信息
        List<SccSouJcAgreementInfo> sccSouJcAgreementInfoList = jcAgreement.getSccSouJcAgreementInfoList();
        if (CollectionUtils.isEmpty(sccSouJcAgreementInfoList) && jcAgreement.getAgreementId() != null) {
            throw new BaseException("协议行信息不能为空");
        }
        //采购组织
        List<SccSouJcAgreementOrg> sccSouJcAgreementOrgList = jcAgreement.getSccSouJcAgreementOrgList();
        if (YesOrNo.YES.getValue().equals(jcAgreement.getDefaultAll())) {
            List<SccSouJcAgreementOrg> ol = new ArrayList<>();
            Organization organization = new Organization();
            organization.setOrganizationTypeCode("OU");
            List<Organization> orgList = baseClient.listOrganizationByParam(organization);
            for (Organization e : orgList) {
                SccSouJcAgreementOrg ao = new SccSouJcAgreementOrg();
                ao.setBuyOrgId(e.getOrganizationId());
                ao.setBuyOrgCode(e.getOrganizationCode());
                ao.setBuyOrgName(e.getOrganizationName());
                ol.add(ao);
            }
            sccSouJcAgreementOrgList = ol;
        }
        Set<String> wlCodeSet = new HashSet<>();
        checkThrow(decide(jcAgreement.getAgreementCode()),"协议编码不能为空");
        checkThrow(decide(jcAgreement.getAgreementName()),"协议名称不能为空");
        checkThrow(jcAgreement.getCompanyId() == null,"公司主体不能为空");
        checkThrow(decide(jcAgreement.getSupplyArea()),"供应区域不能为空");
        if (CollectionUtils.isNotEmpty(sccSouJcAgreementOrgList)) {
            for (SccSouJcAgreementOrg e : sccSouJcAgreementOrgList) {
                checkThrow(e.getBuyOrgId() == null,"采购组织不能为空");
            }
        }
        checkThrow(decide(jcAgreement.getPayment()),"付款条款不能为空");
        checkThrow(jcAgreement.getSupId() == null,"供应商不能为空");
        checkThrow(decide(jcAgreement.getTrading()),"交易方式不能为空");
        checkThrow(jcAgreement.getBuyPersonId() == null,"采购员不能为空");
        checkThrow(decide(jcAgreement.getPricingWay()),"定价方式不能为空");
        checkThrow(decide(jcAgreement.getInvoiceType()),"发票类型不能为空");
        checkThrow(decide(jcAgreement.getPayWay()),"付款方式不能为空");
        checkThrow(jcAgreement.getEffectiveStartDate() == null,"有效日期开始时间不能为空");
        checkThrow(jcAgreement.getEffectiveEndDate() == null,"有效日期结束时间不能为空");
        checkThrow(decide(jcAgreement.getCurrencyType()),"币种不能为空");
        checkThrow(decide(jcAgreement.getAgreementFileId()),"协议附件不能为空");
        checkThrow(decide(jcAgreement.getAgreementType()),"协议类型不能为空");
        checkThrow(jcAgreement.getMixAmount() == null,"最小起订金额不能为空");
        if (CollectionUtils.isNotEmpty(sccSouJcAgreementInfoList)) {
            Set<String> cf = new HashSet<>();
            for (SccSouJcAgreementInfo e : sccSouJcAgreementInfoList) {
                checkThrow(!wlCodeSet.add(e.getMaterialCode()),"物料不能重复");
                checkThrow(decide(e.getMaterialCode()),"物料编码不能为空");
                checkThrow(!cf.add(e.getMaterialCode()),"物料编码不能重复");
                checkThrow(e.getPriceTax() == null,"未税单价不能为空");
                checkThrow(e.getTaxRate() == null,"税率不能为空");
                checkThrow(e.getReferencePrice() == null,"参考价不能为空");
                if (e.getPriceTax().equals(e.getReferencePrice())) {
                    checkThrow(e.getReferencePrice() == null,"参考价不能与未税单价或含税单价一致");
                }
                //含税单价 = 未税单价 * (1 + 税率)
                BigDecimal sl = e.getTaxRate().add(new BigDecimal("1"));
                BigDecimal wsdj = e.getPriceTax();
                BigDecimal hsdj = wsdj.multiply(sl).setScale(4, RoundingMode.HALF_UP);
                if (hsdj.equals(e.getReferencePrice())) {
                    checkThrow(e.getReferencePrice() == null,"参考价不能与未税单价或含税单价一致");
                }
                checkThrow(e.getLeadTime() == null,"交货周期不能为空");
                checkThrow(e.getStartNum() == null,"起订量不能为空");
                //供应区域+物料+采购组织三者判断唯一
                agreementService.checkMa(jcAgreement.getSupplyArea().split(","), e.getMaterialCode(), sccSouJcAgreementOrgList, jcAgreement);
                //阶梯价
                List<SccSouTieredPricing> sccSouTieredPricingList = e.getSccSouTieredPricingList();
                if (CollectionUtils.isNotEmpty(sccSouJcAgreementInfoList)) {
                    for (SccSouTieredPricing sccSouTieredPricing : sccSouTieredPricingList) {
                        checkThrow(sccSouTieredPricing.getPriceTax() == null,"未税单价不能为空");
                        checkThrow(sccSouTieredPricing.getRatePrice() == null,"含税单价不能为空");
                        checkThrow(sccSouTieredPricing.getReferPrice() == null,"参考价不能为空");
                    }
                }
            }
        }
        LambdaQueryWrapper<SccSouJcAgreement> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SccSouJcAgreement::getAgreementCode, jcAgreement.getAgreementCode());
        lambdaQueryWrapper.ne(jcAgreement.getAgreementId() != null, SccSouJcAgreement::getAgreementId, jcAgreement.getAgreementId());
        long i = agreementService.count(lambdaQueryWrapper);
        checkThrow(i != 0,"协议编码已存在");
    }

    @ApiOperation(value = "修改状态", notes = "修改状态", httpMethod = "POST")
    @PostMapping("/changeStatusJcAgreementInfo")
    public SccSouJcAgreement changeStatusJcAgreementInfo(@RequestParam("agreementId") Long agreementId,
                                                         @RequestParam("operationType") String operationType,
                                                         @RequestParam(value = "stopReason", required = false) String stopReason) {
        checkThrow(agreementId == null,"协议id不能为空");
        SccSouJcAgreement agreement = agreementService.getById(agreementId);
        if (agreement == null) {
            throw new BaseException("没有查询到协议信息");
        }
        if (AgreementStatusEnums.EXECUTE.getCode().equals(operationType) && AgreementStatusEnums.DRAFT.getCode().equals(agreement.getAgreementStatus())) {
            long b = agreementInfoService.count(new LambdaQueryWrapper<SccSouJcAgreementInfo>().eq(SccSouJcAgreementInfo::getAgreementId, agreementId));
            if (b == 0) {
                throw new BaseException("至少有一条协议行信息");
            }
            Date date = new Date();
            if (date.after(agreement.getEffectiveEndDate())) {
                agreement.setAgreementStatus(AgreementStatusEnums.STOP.getCode());
            } else if (date.after(agreement.getEffectiveStartDate()) && date.before(agreement.getEffectiveEndDate())) {
                agreement.setAgreementStatus(AgreementStatusEnums.EXECUTING.getCode());
            } else {
                agreement.setAgreementStatus(AgreementStatusEnums.EXECUTE.getCode());
            }
            agreementService.updateById(agreement);
            try {
                addMallInfo(agreementId);
            } catch (Exception e) {
                log.info("推送异常==={}", JSONObject.toJSONString(e));
            }
        } else if (AgreementStatusEnums.STOP.getCode().equals(operationType) && (
                AgreementStatusEnums.DRAFT.getCode().equals(agreement.getAgreementStatus()) ||
                AgreementStatusEnums.EXECUTE.getCode().equals(agreement.getAgreementStatus()) ||
                AgreementStatusEnums.EXECUTING.getCode().equals(agreement.getAgreementStatus()))) {
            agreement.setAgreementStatus(AgreementStatusEnums.STOP.getCode());
            agreement.setStopReason(stopReason);
            agreementService.updateById(agreement);
            try {
                addMallInfo(agreementId);
            } catch (Exception e) {
                log.info("推送异常==={}", JSONObject.toJSONString(e));
            }

            QlOpenUpdateWrapper wrapper = QlOpenWrappers.update(MqlType.CATALOG_ON_SHELVES)
                    .set("extPriceLibraryStatus", AgreementStatusEnums.STOP.getCode())
                    .set(CatalogOnShelves::getStatus, ExtCatalogOnShelvesStatusEnum.OFF_SHELVES.name())
                    .set(CatalogOnShelves::getOffShelvesReason, "协议已终止，商品自动下架")
                    .eq(CatalogOnShelves::getPriceLibraryNo,agreement.getAgreementCode());
            qlOpenClient.update(ContextPath.SUP_CE, wrapper);
        }else if (AgreementStatusEnums.EXPIRED.getCode().equals(operationType) &&
                AgreementStatusEnums.STOP.getCode().equals(agreement.getAgreementStatus())) {
            agreement.setAgreementStatus(AgreementStatusEnums.EXPIRED.getCode());
            agreementService.updateById(agreement);
            try {
                addMallInfo(agreementId);
            } catch (Exception e) {
                log.info("推送异常==={}", JSONObject.toJSONString(e));
            }
        } else {
            throw new BaseException("当前状态不能提交");
        }
        return agreement;
    }

    @ApiOperation(value = "获取集采管理信息", notes = "获取集采管理信息", httpMethod = "GET")
    @GetMapping("/getJcAgreementInfo")
    public SccSouJcAgreement getJcAgreementInfo(@RequestParam("agreementId") Long agreementId) {
        List<PurchaseUnit> unitList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query(PurchaseUnit.class)
                .eq(PurchaseUnit::getEnabled, YesOrNo.YES.getValue()), PurchaseUnit.class);
        Map<String, String> unitMap = unitList.stream()
                .collect(Collectors.toMap(PurchaseUnit::getUnitCode, PurchaseUnit::getUnitName));
        checkThrow(agreementId == null,"协议id不能为空");
        SccSouJcAgreement agreement = agreementService.getById(agreementId);
        if (agreement == null) {
            throw new BaseException("没有查询到协议信息");
        }
        if (JCXY.equals(agreement.getAgreementType())) {
            agreement.setDefaultAll("Y");
        } else {
            agreement.setDefaultAll("N");
        }
        LambdaQueryWrapper<SccSouJcAgreementOrg> orgQuery = new LambdaQueryWrapper<>();
        orgQuery.eq(SccSouJcAgreementOrg::getAgreementId, agreementId);
        List<SccSouJcAgreementOrg> orgList = jcAgreementOrgService.list(orgQuery);
        agreement.setSccSouJcAgreementOrgList(orgList);
        LambdaQueryWrapper<SccSouJcAgreementInfo> infoQuery = new LambdaQueryWrapper<>();
        infoQuery.eq(SccSouJcAgreementInfo::getAgreementId, agreementId);
        List<SccSouJcAgreementInfo> infoList = agreementInfoService.list(infoQuery);
        List<Long> infoIds = infoList.stream().map(SccSouJcAgreementInfo::getAgreementInfoId).collect(Collectors.toList());
        //阶梯价
        if (CollectionUtils.isNotEmpty(infoIds)) {
            LambdaQueryWrapper<SccSouTieredPricing> jtjQuery = new LambdaQueryWrapper<>();
            jtjQuery.in(SccSouTieredPricing::getAgreementInfoId, infoIds);
            List<SccSouTieredPricing> jtjList = tieredPricingService.list(jtjQuery);
            Map<Long, List<SccSouTieredPricing>> jtjMap = jtjList.stream().collect(Collectors.groupingBy(SccSouTieredPricing::getAgreementInfoId));
            infoList.forEach(e -> e.setSccSouTieredPricingList(jtjMap.get(e.getAgreementInfoId())));
            infoList.forEach(e -> e.setUnit(unitMap.get(e.getUnit())));
            agreement.setSccSouJcAgreementInfoList(infoList);
        }
        return agreement;
    }

    @ApiOperation(value = "获取版本记录列表", notes = "获取版本记录列表", httpMethod = "POST")
    @PostMapping("/getChangeJcAgreementList")
    public List<Map<String, Object>> getChangeJcAgreementList(@RequestParam("agreementId") Long agreementId) {
        checkThrow(agreementId == null,"协议id不能为空");
        LambdaQueryWrapper<SccSouJcAgreementChange> changeQuery = new LambdaQueryWrapper<>();
        changeQuery.eq(SccSouJcAgreementChange::getAgreementId, agreementId);
        List<SccSouJcAgreementChange> list = jcAgreementChangeService.list(changeQuery);
        Set<Integer> set  = list.stream().map(SccSouJcAgreementChange::getChangeVersion).collect(Collectors.toSet());
        List<Map<String, Object>> resultList = new ArrayList<>();
        set.forEach(e -> {
            Map<String, Object> map = new HashMap<>(50);
            map.put("agreementId", agreementId);
            map.put("changeVersion", "V" + e);
            resultList.add(map);
        });
        return resultList;
    }

    @ApiOperation(value = "获取版本记录详情", notes = "获取版本记录详情")
    @GetMapping("/getChangeJcAgreementInfo")
    public Map<String, Object> getChangeJcAgreementInfo(@RequestParam("agreementId") Long agreementId, @RequestParam("version") String version) {
        checkThrow(agreementId == null,"协议id不能为空");
        checkThrow(StringUtils.isBlank(version),"版本号不能为空");
        LambdaQueryWrapper<SccSouJcAgreementChange> changeQuery = new LambdaQueryWrapper<>();
        changeQuery.eq(SccSouJcAgreementChange::getAgreementId, agreementId);
        changeQuery.in(SccSouJcAgreementChange::getType, 1, 2);
        String v = version.toUpperCase().replace("V", "");
        changeQuery.eq(SccSouJcAgreementChange::getChangeVersion, Integer.valueOf(v));
        List<SccSouJcAgreementChange> list = jcAgreementChangeService.list(changeQuery);
        List<SccSouJcAgreementChange> xy = list.stream().filter(e -> e.getType() == 1).collect(Collectors.toList());
        List<SccSouJcAgreementChange> xyInfo = list.stream().filter(e -> e.getType() == 2).collect(Collectors.toList());
        Map<String, Object> resultMap = new HashMap<>(50);
        resultMap.put("xy", xy);
        resultMap.put("xyInfo", xyInfo);
        return resultMap;
    }

    @ApiOperation(value = "变更集采管理", notes = "变更集采管理", httpMethod = "POST")
    @PostMapping("/changeJcAgreement")
    public SccSouJcAgreement changeJcAgreement(@RequestBody SccSouJcAgreement sccSouJcAgreement) {
        checkAddOrUpdate(sccSouJcAgreement);
        if (sccSouJcAgreement == null) {
            throw new BaseException("参数不能为空");
        }
        Long agreementId =  sccSouJcAgreement.getAgreementId();
        checkThrow(agreementId == null,"协议id不能为空");
        SccSouJcAgreement agreement = this.getJcAgreementInfo(agreementId);
        if (!AgreementStatusEnums.EXECUTE.getCode().equals(agreement.getAgreementStatus()) &&
                !AgreementStatusEnums.EXECUTING.getCode().equals(agreement.getAgreementStatus())) {
            throw new BaseException("当前状态不能变更");
        }
        List<SccSouJcAgreementChange> changeList = agreementService.changeJcAgreement(agreement, sccSouJcAgreement);
        if (CollectionUtils.isNotEmpty(changeList)) {
            Integer num = agreement.getChangeVersion() + 1;
            sccSouJcAgreement.setChangeVersion(num);
            agreementService.updateById(sccSouJcAgreement);
            List<SccSouJcAgreementInfo> infoList = sccSouJcAgreement.getSccSouJcAgreementInfoList();
            agreementInfoService.updateBatchById(infoList);
            List<SccSouTieredPricing> jtjAllList = new ArrayList<>();
            infoList.forEach(e -> jtjAllList.addAll(e.getSccSouTieredPricingList()));
            tieredPricingService.updateBatchById(jtjAllList);
            changeList.forEach(e -> e.setChangeVersion(num));
            jcAgreementChangeService.saveBatch(changeList);
        }
        try {
            addMallInfo(agreementId);
        } catch (Exception e) {
            log.info("推送异常==={}", JSONObject.toJSONString(e));
        }
        return sccSouJcAgreement;
    }

    @ApiOperation(value = "集采导入协议头", notes = "集采导入协议头", httpMethod = "POST")
    @PostMapping("/importJcAgreementHead")
    public void importJcAgreementHead(@RequestBody MultipartFile file) throws IOException {
        String suffix = validationExcelFile(file);
        agreementService.importAgreementHead(file, JCXY, suffix);
    }

    @ApiOperation(value = "合同导入协议头", notes = "合同导入协议头", httpMethod = "POST")
    @PostMapping("/importHtAgreementHead")
    public void importHtAgreementHead(@RequestBody MultipartFile file) throws IOException {
        String suffix = validationExcelFile(file);
        agreementService.importAgreementHead(file, HTXY, suffix);
    }

    @ApiOperation(value = "集采列表页面导入协议行", notes = "集采列表页面导入协议行", httpMethod = "POST")
    @PostMapping("/importJcAgreementLine")
    public void importJcAgreementLine(@RequestBody MultipartFile file) throws IOException {
        //行
        AnalysisEventListenerImpl<ImportExcelJcAgreementLineDto> listenerLine = null;
        AnalysisEventListenerImpl<ImportExcelJcAgreementLineTieredDto> listenerLineTiered = null;
        try {
            listenerLine = new AnalysisEventListenerImpl<>();
            ExcelReader excelReaderLine = EasyExcel.read(file.getInputStream(), listenerLine).build();
            ReadSheet readSheetLine = EasyExcel.readSheet(0).headRowNumber(4).head(ImportExcelJcAgreementLineDto.class).build();
            excelReaderLine.read(readSheetLine);
            //阶梯价
            listenerLineTiered = new AnalysisEventListenerImpl<>();
            ExcelReader excelReaderLineTiered = EasyExcel.read(file.getInputStream(), listenerLineTiered).build();
            ReadSheet readSheetLineTiered = EasyExcel.readSheet(1).headRowNumber(4).head(ImportExcelJcAgreementLineDto.class).build();
            excelReaderLineTiered.read(readSheetLineTiered);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
        agreementService.importAgreementLine(listenerLine.getDatas(), listenerLineTiered.getDatas(), JCXY);
    }

    @ApiOperation(value = "合同列表页面导入协议行", notes = "列表页面导入协议行", httpMethod = "POST")
    @PostMapping("/importHtAgreementLine")
    public void importHtAgreementLine(@RequestBody MultipartFile file) throws IOException {
        AnalysisEventListenerImpl<ImportExcelJcAgreementLineDto> listenerLine = null;
        AnalysisEventListenerImpl<ImportExcelJcAgreementLineTieredDto> listenerLineTiered = null;
        try {
            //行
            listenerLine = new AnalysisEventListenerImpl<>();
            ExcelReader excelReaderLine = EasyExcel.read(file.getInputStream(), listenerLine).build();
            ReadSheet readSheetLine = EasyExcel.readSheet(0).headRowNumber(4).head(ImportExcelJcAgreementLineDto.class).build();
            excelReaderLine.read(readSheetLine);
            //阶梯价
            listenerLineTiered = new AnalysisEventListenerImpl<>();
            ExcelReader excelReaderLineTiered = EasyExcel.read(file.getInputStream(), listenerLineTiered).build();
            ReadSheet readSheetLineTiered = EasyExcel.readSheet(1).headRowNumber(4).head(ImportExcelJcAgreementLineDto.class).build();
            excelReaderLineTiered.read(readSheetLineTiered);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
        agreementService.importAgreementLine(listenerLine.getDatas(), listenerLineTiered.getDatas(), HTXY);
    }

    @ApiOperation(value = "集采编辑页面导入协议行", notes = "集采编辑页面导入协议行", httpMethod = "POST")
    @PostMapping("/importEditAgreementLine")
    public void importEditAgreementLine(@RequestBody MultipartFile file, @RequestParam("agreementId") Long agreementId) throws IOException {
        checkThrow(agreementId == null,"协议id不能为空");
        SccSouJcAgreement jg = agreementService.getById(agreementId);
        if (jg == null) {
            throw new BaseException("没有查询到协议信息");
        }
        if (!JCXY.equals(jg.getAgreementType())) {
            throw new BaseException("当前导入非集采协议");
        }
        AnalysisEventListenerImpl<ImportExcelJcAgreementLineEditDto> listenerLine = null;
        AnalysisEventListenerImpl<ImportExcelJcAgreementLineTieredDto> listenerLineTiered = null;
        try {
            //行
            listenerLine = new AnalysisEventListenerImpl<>();
            ExcelReader excelReaderLine = EasyExcel.read(file.getInputStream(), listenerLine).build();
            ReadSheet readSheetLine = EasyExcel.readSheet(0).headRowNumber(4).head(ImportExcelJcAgreementLineEditDto.class).build();
            excelReaderLine.read(readSheetLine);
            //阶梯价
            listenerLineTiered = new AnalysisEventListenerImpl<>();
            ExcelReader excelReaderLineTiered = EasyExcel.read(file.getInputStream(), listenerLineTiered).build();
            ReadSheet readSheetLineTiered = EasyExcel.readSheet(1).headRowNumber(4).head(ImportExcelJcAgreementLineTieredDto.class).build();
            excelReaderLineTiered.read(readSheetLineTiered);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
        agreementService.importEditAgreementLine(listenerLine.getDatas(), listenerLineTiered.getDatas(), jg);
    }

    @ApiOperation(value = "合同编辑页面导入协议行", notes = "编辑页面导入协议行", httpMethod = "POST")
    @PostMapping("/importHtEditAgreementLine")
    public void importHtEditAgreementLine(@RequestBody MultipartFile file, @RequestParam("agreementId") Long agreementId) throws IOException {
        checkThrow(agreementId == null,"协议id不能为空");
        SccSouJcAgreement jg = agreementService.getById(agreementId);
        if (jg == null) {
            throw new BaseException("没有查询到协议信息");
        }
        if (!HTXY.equals(jg.getAgreementType())) {
            throw new BaseException("当前导入非合同协议");
        }
        AnalysisEventListenerImpl<ImportExcelJcAgreementLineEditDto> listenerLine = null;
        AnalysisEventListenerImpl<ImportExcelJcAgreementLineTieredDto> listenerLineTiered = null;
        try {
            //行
            listenerLine = new AnalysisEventListenerImpl<>();
            ExcelReader excelReaderLine = EasyExcel.read(file.getInputStream(), listenerLine).build();
            ReadSheet readSheetLine = EasyExcel.readSheet(0).headRowNumber(4).head(ImportExcelJcAgreementLineEditDto.class).build();
            excelReaderLine.read(readSheetLine);
            //阶梯价
            listenerLineTiered = new AnalysisEventListenerImpl<>();
            ExcelReader excelReaderLineTiered = EasyExcel.read(file.getInputStream(), listenerLineTiered).build();
            ReadSheet readSheetLineTiered = EasyExcel.readSheet(1).headRowNumber(4).head(ImportExcelJcAgreementLineTieredDto.class).build();
            excelReaderLineTiered.read(readSheetLineTiered);
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }
        agreementService.importEditAgreementLine(listenerLine.getDatas(), listenerLineTiered.getDatas(), jg);
    }

    public void checkThrow(Boolean boo, String str) {
        if (boo) {
            throw new BaseException(str);
        }
    }

    public String validationExcelFile(MultipartFile file) {
        checkThrow(file == null,"参数不能为空");
        assert file != null;
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        checkThrow(!"xlsx".equals(fileSuffix) && !"xls".equals(fileSuffix),"上传的文件格式不正确,只支持xlsx、xls");
        return fileSuffix;
    }

    @ApiOperation(value = "下载协议模板", notes = "下载协议模板")
    @RequestMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response, String type) throws IOException {
        if (StringUtils.isBlank(type)) {
            throw new BaseException("模板类型不能为空");
        }
        //1、集采协议导入模板。2、合同协议导入模板。3、协议行导入模板。4、编辑页协议行导入模板
        InputStream inputStream;
        String str = "";
        switch (type) {
            case "1":
                inputStream = this.getClass().getResourceAsStream("/template/集采协议信息模板.xlsx");
                str = "协议头附表";
                break;
            case "2":
                inputStream = this.getClass().getResourceAsStream("/template/合同协议信息模板.xlsx");
                str = "协议头附表";
                break;
            case "3":
                inputStream = this.getClass().getResourceAsStream("/template/协议行导入信息模板.xlsx");
                str = "协议行附表";
                break;
            case "4":
                inputStream = this.getClass().getResourceAsStream("/template/编辑页协议行导入信息模板.xlsx");
                str = "协议行附表";
                break;
            default:
                throw new BaseException("模板类型传入错误");
        }
        assert inputStream != null;
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, StringUtils.isNotBlank(str) ? str : "协议信息模板");
        workbook.write(outputStream);
        /*try {
            //1、集采协议导入模板。2、合同协议导入模板。3、协议行导入模板。4、编辑页协议行导入模板
            InputStream inputStream;
            switch (type) {
                case "1":
                    inputStream = this.getClass().getResourceAsStream("/template/集采协议信息模板.xlsx");
                    break;
                case "2":
                    inputStream = this.getClass().getResourceAsStream("/template/合同协议信息模板.xlsx");
                    break;
                case "3":
                    inputStream = this.getClass().getResourceAsStream("/template/协议行导入信息模板.xlsx");
                    break;
                case "4":
                    inputStream = this.getClass().getResourceAsStream("/template/编辑页协议行导入信息模板.xlsx");
                    break;
                default:
                    throw new BaseException("模板类型传入错误");
            }
            assert inputStream != null;
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, "协议信息模板");
            workbook.write(outputStream);
        } catch (Exception e) {
            throw new BaseException("下载失败");
        }*/
    }

    @ApiOperation(value = "获取集采协议管理列表", notes = "获取集采协议管理列表", httpMethod = "POST")
    @PostMapping("/getJcAgreementLinePageList")
    public JSONObject getJcAgreementLinePageList(@RequestBody LinePageDto ja) {
        PageUtil.startPage(ja.getPageNum(), ja.getPageSize());
        ja.setCatalogOnShelvesId(1L);
        List<LinePageDto> list = agreementService.getLinePageList(ja);
        if (ObjectUtil.isNotEmpty(list)) {
            List<String> materialItemCodes=list.stream().map(LinePageDto::getMaterialCode).collect(Collectors.toList());
            List<ExternalMaterial> externalMaterials =qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("ExternalMaterial")
                    .in(ExternalMaterial::getMaterialCode, materialItemCodes), ExternalMaterial.class);
            Map<String,ExternalMaterial> externalMaterialMap=new HashMap<>(15);
            if (ObjectUtil.isNotEmpty(externalMaterials)) {
                externalMaterialMap=externalMaterials.stream().collect(Collectors.toMap(ExternalMaterial::getMaterialCode,l->l));
            }
            for (LinePageDto e : list) {
                List<SccSouJcAgreementOrg> orgList = jcAgreementOrgService.list(new LambdaQueryWrapper<SccSouJcAgreementOrg>().in(SccSouJcAgreementOrg::getAgreementId, e.getAgreementId()));
                e.setSccSouJcAgreementOrgList(orgList);
                ExternalMaterial externalMaterial = externalMaterialMap.get(e.getMaterialCode());
                e.setMaterialType(externalMaterial == null ? MallTypeEnum.CC.getCode() : MallTypeEnum.JD.getCode());
            }
            log.info(JSONObject.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect));

            return JSONObject.parseObject(JSONObject.toJSONString(new PageInfo<>(list), SerializerFeature.DisableCircularReferenceDetect));
        }
        return new JSONObject();
    }

    @ApiOperation(value = "按区域+物料查询有效价格协议", notes = "获取集采协议管理列表", httpMethod = "POST")
    @PostMapping("/getValidPriceList")
    public List<PriceAgreementDTO> getValidPriceList(@RequestBody PriceAgreementQueryDTO dto) {
        return agreementService.getValidPriceList(dto.getOrgIds(), dto.getSupplyAreas(), dto.getMaterialIds());
    }

    @ApiOperation(value = "协议行导出", notes = "协议行导出", httpMethod = "POST")
    @PostMapping("/exportLineAgreementPage")
    public PageInfo<SccSouJcAgreementInfo> exportLineAgreementPage(@RequestBody SccSouJcAgreementInfo info) {
        if (info.getAgreementId() == null) {
            throw new BaseException("协议id不能为空");
        }
        PageUtil.startPage(info.getPageNum(), info.getPageSize());
        List<SccSouJcAgreementInfo> list = agreementInfoService.lambdaQuery().eq(SccSouJcAgreementInfo::getAgreementId, info.getAgreementId()).list();
        log.info("导出的行信息==={}", JSONObject.toJSONString(list));
        return new PageInfo<>(list);
    }

    @ApiOperation(value = "协议查询报表", notes = "协议查询报表", httpMethod = "POST")
    @PostMapping("/getJcHeadLinePageList")
    public PageInfo<SccSouJcAgreementDto> getJcHeadLinePageList(@RequestBody SccSouJcAgreementDto ja) {
        PageUtil.startPage(ja.getPageNum(), ja.getPageSize());
        List<SccSouJcAgreementDto> agreementList = agreementService.getJcHeadLinePageList(ja);
        return new PageInfo<>(agreementList);
    }

    @ApiOperation(value = "导出集采和合同头加行信息", notes = "导出集采和合同头加行信息", httpMethod = "POST")
    @PostMapping("/exportJcHtHeadLineData")
    public void exportJcHtHeadLineData(@RequestBody SccSouJcAgreementDto ja, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException, IOException {
        List<ExportJcHtHeadLineData> eList = agreementService.exportJcHtHeadLineData(ja);
        // 获取输出流
        OutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, ja.getAgreementType() + "明细");
        EasyExcel.write(outputStream).head(ExportJcHtHeadLineData.class).sheet(0).sheetName("sheet1").doWrite(eList);
    }

    @PostMapping("/queryQuickMaterial")
    @ApiOperation(value = "弹窗物料快查-价格协议单属于京东物料", notes = "弹窗物料快查-价格协议单属于京东物料")
    public PageInfo<SccSouJcAgreementInfo> listDialogQuickActiveInvMaterial(@RequestBody SccSouJcAgreementInfo jcAgreementInfo) {
        PageUtil.startPage(jcAgreementInfo.getPageNum(), jcAgreementInfo.getPageSize());
        List<SccSouJcAgreementInfo> materialItemPageInfo = agreementService.getjcInfoList(jcAgreementInfo);
        //
        if (ObjectUtil.isNotEmpty(materialItemPageInfo)) {
            List<String> materialItemCodes = materialItemPageInfo.stream().map(SccSouJcAgreementInfo::getMaterialCode).collect(Collectors.toList());
            List<ExternalMaterial> externalMaterials = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("ExternalMaterial")
                    .in(ExternalMaterial::getMaterialCode, materialItemCodes), ExternalMaterial.class);
            Map<String, ExternalMaterial> externalMaterialMap = new HashMap<>(15);
            if (ObjectUtil.isNotEmpty(externalMaterials)) {
                externalMaterialMap = externalMaterials.stream().collect(Collectors.toMap(ExternalMaterial::getMaterialCode, l -> l));
            }
            for (SccSouJcAgreementInfo materialItem : materialItemPageInfo) {
                ExternalMaterial externalMaterial = externalMaterialMap.get(materialItem.getMaterialCode());
                materialItem.setDescription(externalMaterial == null ? "N" : "Y");
            }
        }
        return new PageInfo<>(materialItemPageInfo);
    }

    /**
     * 行废弃
     * @param jcAgreementInfo 信息
     */
    @PostMapping("/discardAgreementLine")
    @ApiOperation(value = "行废弃", notes = "行废弃")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void discardAgreementLine(@RequestBody SccSouJcAgreementInfo jcAgreementInfo) {
        if (jcAgreementInfo == null ||
                jcAgreementInfo.getAgreementId() == null ||
                jcAgreementInfo.getAgreementInfoId() == null ||
                StringUtils.isEmpty(jcAgreementInfo.getMaterialCode())) {
            throw new BaseException("参数为空");
        }
        SccSouJcAgreement ja = agreementService.getById(jcAgreementInfo.getAgreementId());
        if (ja == null || AgreementStatusEnums.DRAFT.getCode().equals(ja.getAgreementStatus())) {
            throw new BaseException("状态不对");
        }
        agreementInfoService.removeById(jcAgreementInfo.getAgreementInfoId());
        String re = ja.getDiscardReason();
        if (StringUtils.isNotEmpty(re)) {
            re += "," + jcAgreementInfo.getMaterialCode();
        } else {
            re = jcAgreementInfo.getMaterialCode();
        }
        LambdaUpdateWrapper<SccSouJcAgreement> u = new LambdaUpdateWrapper<>();
        u.set(SccSouJcAgreement::getDiscardReason, re);
        u.eq(SccSouJcAgreement::getAgreementId, jcAgreementInfo.getAgreementId());
        agreementService.update(u);
    }
}
