package com.midea.cloud.srm.sou.agreement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.feign.supplier.SupplierClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseUnit;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.agreement.dto.AreGroupDto;
import com.midea.cloud.srm.model.sou.agreement.dto.LinePageDto;
import com.midea.cloud.srm.model.sou.agreement.dto.PriceAgreementDTO;
import com.midea.cloud.srm.model.sou.agreement.dto.SccSouJcAgreementDto;
import com.midea.cloud.srm.model.sou.agreement.entity.*;
import com.midea.cloud.srm.model.sou.agreement.enums.AgreementStatusEnums;
import com.midea.cloud.srm.model.sou.agreement.enums.FieldValueEnums;
import com.midea.cloud.srm.model.sou.agreement.excel.ExportJcHtHeadLineData;
import com.midea.cloud.srm.model.sou.agreement.excel.ImportExcelJcAgreementLineDto;
import com.midea.cloud.srm.model.sou.agreement.excel.ImportExcelJcAgreementLineEditDto;
import com.midea.cloud.srm.model.sou.agreement.excel.ImportExcelJcAgreementLineTieredDto;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.agreement.mapper.JcAgreementMapper;
import com.midea.cloud.srm.sou.agreement.service.JcAgreementInfoService;
import com.midea.cloud.srm.sou.agreement.service.JcAgreementOrgService;
import com.midea.cloud.srm.sou.agreement.service.JcAgreementService;
import com.midea.cloud.srm.sou.agreement.service.TieredPricingService;
import com.midea.cloud.srm.sou.constants.NumConstant;
import com.midea.cloud.srm.sou.constants.SouConstant;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Service
public class JcAgreementServiceImpl extends BaseServiceImpl<JcAgreementMapper, SccSouJcAgreement> implements JcAgreementService {

    @Resource
    private QlOpenClient qlOpenClient;

    @Resource
    private JcAgreementOrgService jcAgreementOrgService;

    @Resource
    private JcAgreementMapper jcAgreementMapper;

    @Resource
    private JcAgreementInfoService agreementInfoService;

    @Resource
    private TieredPricingService tieredPricingService;

    @Resource
    private BaseClient baseClient;

    @Resource
    private RbacClient rbacClient;

    @Resource
    private SupplierClient supplierClient;

    @Resource
    private PjSouClient pjSouClient;

    private static final String POINT = ".";

    /**
     * 获取列表
     * @param ja 参数
     * @return 列表
     */
    @Override
    public List<SccSouJcAgreement> agreementList(SccSouJcAgreementDto ja) {
        List<SccSouJcAgreement> list = jcAgreementMapper.agreementList(ja);
        for (SccSouJcAgreement e : list) {
            List<SccSouJcAgreementOrg> orgList = jcAgreementOrgService.list(new LambdaQueryWrapper<SccSouJcAgreementOrg>().eq(SccSouJcAgreementOrg::getAgreementId, e.getAgreementId()));
            e.setSccSouJcAgreementOrgList(orgList);
        }
        return list;
    }

    /**
     * 获取集采id
     *
     * @param ja 参数
     * @return 列表
     */
    @Override
    public List<Long> agreementIdList(SccSouJcAgreementDto ja) {
        return jcAgreementMapper.agreementIdList(ja);
    }

    /**
     * @return list
     */
    @Override
    public List<AreGroupDto> getAreList(Set<Long> agreementIds) {
        return jcAgreementMapper.getAreList(agreementIds);
    }

    /**
     * 检验
     * @param newArea 区域
     * @param newMaterialCode 物料编码
     * @param newOrg 组织
     */
    @Override
    public void checkMa(String[] newArea, String newMaterialCode, List<SccSouJcAgreementOrg> newOrg,  SccSouJcAgreement ja ) {
        String agreementCode = ja.getAgreementCode();
        List<LinePageDto> lineHeadList = jcAgreementMapper.chMa(agreementCode, newMaterialCode, ja.getEffectiveStartDate(), ja.getEffectiveEndDate());
        for (LinePageDto linePageDto : lineHeadList) {
            List<SccSouJcAgreementOrg> orgList = jcAgreementOrgService.list(new LambdaQueryWrapper<SccSouJcAgreementOrg>().eq(SccSouJcAgreementOrg::getAgreementId, linePageDto.getAgreementId()));
            String[] areaList = linePageDto.getSupplyArea().split(",");
            boolean ab = false;
            for (String area1 : areaList) {
                for (String area2 : newArea) {
                    if (area1.equals(area2)) {
                        ab = true;
                        break;
                    }
                }
                if (ab) {
                    break;
                }
            }
            boolean orgb = false;
            for (SccSouJcAgreementOrg org1 : orgList) {
                for (SccSouJcAgreementOrg org2 : newOrg) {
                    if (org1.getBuyOrgCode().equals(org2.getBuyOrgCode())) {
                        orgb = true;
                        break;
                    }
                }
                if (orgb) {
                    break;
                }
            }
            if (linePageDto.getMaterialCode().equals(newMaterialCode) && ab && orgb) {
                throw new BaseException("同一个供应区域、采购组织在有效期内，不允许存在同一个物料" + newMaterialCode);
            }
        }
    }

    /**
     * 变更集采管理
     *
     * @param oldObject 老对象
     * @param newObject 新对象
     */
    @Override
    public List<SccSouJcAgreementChange> changeJcAgreement(SccSouJcAgreement oldObject, SccSouJcAgreement newObject) {
        List<SccSouJcAgreementChange> changList = new ArrayList<>();
        createInfo(FieldValueEnums.AGREEMENT_CODE, oldObject.getAgreementCode(), newObject.getAgreementCode(), changList);
        createInfo(FieldValueEnums.AGREEMENT_NAME, oldObject.getAgreementName(), newObject.getAgreementName(), changList);
        createInfo(FieldValueEnums.PAYMENT, oldObject.getPayment(), newObject.getPayment(), changList);
        createInfo(FieldValueEnums.TRADING, oldObject.getTrading(), newObject.getTrading(), changList);
        createInfo(FieldValueEnums.BUY_PERSON_NAME, oldObject.getBuyPersonName(), newObject.getBuyPersonName(), changList);
        createInfo(FieldValueEnums.INVOICE_TYPE, oldObject.getInvoiceType(), newObject.getInvoiceType(), changList);
        createInfo(FieldValueEnums.PAY_WAY, oldObject.getPayWay(), newObject.getPayWay(), changList);
        createInfo(FieldValueEnums.AGREEMENT_FILE_ID, oldObject.getAgreementFileId(), newObject.getAgreementFileId(), changList);
        createInfo(FieldValueEnums.AGREEMENT_TYPE, oldObject.getAgreementType(), newObject.getAgreementType(), changList);
        createInfo(FieldValueEnums.REMARK, oldObject.getRemark(), newObject.getRemark(), changList);
        changList.forEach(e -> e.setType(1));
        List<SccSouJcAgreementInfo> oldInfoList = oldObject.getSccSouJcAgreementInfoList();
        List<SccSouJcAgreementInfo> newInfoList = newObject.getSccSouJcAgreementInfoList();
        if (CollectionUtils.isNotEmpty(oldInfoList)) {
            List<SccSouJcAgreementChange> changInfoList = new ArrayList<>();
            for (SccSouJcAgreementInfo o : oldInfoList) {
                for (SccSouJcAgreementInfo n : newInfoList) {
                    if (Objects.equals(o.getAgreementInfoId(), n.getAgreementInfoId())) {
                        createInfo(FieldValueEnums.PRICE_TAX, o.getPriceTax(), n.getPriceTax(), changInfoList);
                        createInfo(FieldValueEnums.TAX_RATE, o.getTaxRate(), n.getTaxRate(), changInfoList);
                        createInfo(FieldValueEnums.REFERENCE_PRICE, o.getReferencePrice(), n.getReferencePrice(), changInfoList);
                        createInfo(FieldValueEnums.LEAD_TIME, String.valueOf(o.getLeadTime()), String.valueOf(n.getLeadTime()), changInfoList);
                        createInfo(FieldValueEnums.SELL_BY_DATE, String.valueOf(o.getSellByDate()), String.valueOf(n.getSellByDate()), changInfoList);
                        createInfo(FieldValueEnums.START_NUM, String.valueOf(o.getStartNum()), String.valueOf(n.getStartNum()), changInfoList);
                        createInfo(FieldValueEnums.MULTIPLE_START_NUM, String.valueOf(o.getMultipleStartNum()), String.valueOf(n.getMultipleStartNum()), changInfoList);
                        createInfo(FieldValueEnums.AGREEMENT_DES, o.getAgreementDes(), n.getAgreementDes(), changInfoList);
                        createInfo(FieldValueEnums.IS_TIERED_PRICING, String.valueOf(o.getIsTieredPricing()), String.valueOf(n.getIsTieredPricing()), changInfoList);
                        changInfoList.forEach(e -> {
                            e.setType(2);
                            e.setAgreementInfoId(o.getAgreementInfoId());
                        });
                        //阶梯价
                        List<SccSouTieredPricing> jtjOldList = o.getSccSouTieredPricingList();
                        List<SccSouTieredPricing> jtjNewList = n.getSccSouTieredPricingList();
                        if (CollectionUtils.isNotEmpty(jtjOldList)) {
                            List<SccSouJcAgreementChange> changJtjList = new ArrayList<>();
                            for (SccSouTieredPricing jtjOld : jtjOldList) {
                                for (SccSouTieredPricing jtjNew : jtjNewList) {
                                    if (Objects.equals(jtjOld.getTieredPricingId(), jtjNew.getTieredPricingId())) {
                                        createInfo(FieldValueEnums.MORE_NUM, String.valueOf(jtjOld.getMoreNum()), String.valueOf(jtjNew.getMoreNum()), changJtjList);
                                        createInfo(FieldValueEnums.LESS_NUM, String.valueOf(jtjOld.getLessNum()), String.valueOf(jtjNew.getLessNum()), changJtjList);
                                        createInfo(FieldValueEnums.UNIT, jtjOld.getUnit(), jtjNew.getUnit(), changJtjList);
                                        createInfo(FieldValueEnums.PRICE_TAX1, jtjOld.getPriceTax(), jtjNew.getPriceTax(), changJtjList);
                                        createInfo(FieldValueEnums.RATE_PRICE, jtjOld.getRatePrice(), jtjNew.getRatePrice(), changJtjList);
                                        createInfo(FieldValueEnums.REFER_PRICE, jtjOld.getReferPrice(), jtjNew.getReferPrice(), changJtjList);
                                        changJtjList.forEach(e -> {
                                            e.setType(3);
                                            e.setTieredPricingId(jtjOld.getTieredPricingId());
                                        });
                                    }
                                }
                            }
                            changList.addAll(changJtjList);
                        }
                    }
                }
            }
            changList.addAll(changInfoList);
        }
        changList.forEach(e -> e.setAgreementId(oldObject.getAgreementId()));
        return changList;
    }

    public void createInfo(FieldValueEnums en, String oldValue, String newValue, List<SccSouJcAgreementChange> changList) {
        try {
            if (!oldValue.equals(newValue)) {
                SccSouJcAgreementChange ac = new SccSouJcAgreementChange();
                ac.setOldValue(oldValue);
                ac.setNewValue(newValue);
                ac.setFieldName(en.getName());
                ac.setFieldValue(en.getCode());
                changList.add(ac);
            }
        } catch (Exception e) {
            log.error("添加的有异常");
        }
    }

    public void createInfo(FieldValueEnums en, BigDecimal oldValue, BigDecimal newValue, List<SccSouJcAgreementChange> changList) {
        try {
            if (oldValue.compareTo(newValue) != 0) {
                SccSouJcAgreementChange ac = new SccSouJcAgreementChange();
                ac.setOldValue(oldValue.toString());
                ac.setNewValue(newValue.toString());
                ac.setFieldName(en.getName());
                ac.setFieldValue(en.getCode());
                changList.add(ac);
            }
        } catch (Exception e) {
            log.error("添加的有异常");
        }
    }

    /**
     * 导入协议头
     *
     * @param file 导入的文件
     */
    @Override
    public void importAgreementHead(MultipartFile file, String agreementType, String suffix) throws IOException {
        Workbook xwb;
        String xlsx = "xlsx";
        if (xlsx.equals(suffix)) {
            xwb = new XSSFWorkbook(file.getInputStream());
        } else {
            xwb = new HSSFWorkbook(file.getInputStream());
        }
        Sheet xssfSheet = xwb.getSheetAt(0);
        checkThrow(xssfSheet.getLastRowNum() > 1000, "数据行超过了1000行");
        List<SccSouJcAgreement> agreementList = new ArrayList<>();
        //协议编号
        Set<String> codeSet = new HashSet<>();
        //供应区域
        List<DictItemDTO> gyqyList = baseClient.listAllByDictCode("REGION");
        //公司主体
        List<com.midea.cloud.srm.model.pj.base.organization.entity.Organization> companyList = pjSouClient.findListFilterInvoiceInfo();
        log.info("最后一行编号==={}", xssfSheet.getLastRowNum());
        for (int i = 3; i <= xssfSheet.getLastRowNum(); i++) {
            SccSouJcAgreement souJcAgreement = new SccSouJcAgreement();
            Row row = xssfSheet.getRow(i);
            if(row==null) {
                break;
            }
            int lineNum = "集采协议".equals(agreementType) ? NumConstant.THIRTEEN : NumConstant.FOURTEEN;
            log.info(lineNum+"");
            log.info(agreementType);
            for (int j = 0; j <= lineNum; j++) {
                Cell cell = row.getCell(j);
                log.info(j+"");
                checkThrow(cell == null && j != 12,"必填项不能为空");
            }
            checkThrow(!codeSet.add(row.getCell(0).toString()),"协议编号不能重复");
            //协议编号
            souJcAgreement.setAgreementCode(row.getCell(0).toString());
            //协议名称
            souJcAgreement.setAgreementName(row.getCell(1).toString());
            //公司主体编码
            checkCompany(companyList, row.getCell(2).toString(), i, souJcAgreement);
            //供应商编码
            checkSup(row.getCell(3).toString(), i, souJcAgreement);
            //有效期(开始)
            souJcAgreement.setEffectiveStartDate(checkDte(row.getCell(4), " 00:00:00"));
            //有效期(结束)
            souJcAgreement.setEffectiveEndDate(checkDte(row.getCell(5), " 23:59:59"));
            checkThrow(souJcAgreement.getEffectiveEndDate().before(souJcAgreement.getEffectiveStartDate()),"结束日期不能比开始日期小");
            //交易方式
            souJcAgreement.setTrading(dealWay(row.getCell(6).toString(), "交易方式", i));
            //付款方式
            souJcAgreement.setPayWay(dealWay(row.getCell(7).toString(), "付款方式", i));
            //付款条款代码
            souJcAgreement.setPayment(dealWay(row.getCell(8).toString(), "付款条款", i));
            //采购员编码
            checkAgent(row.getCell(9).toString(), i, souJcAgreement);
            //供应区域
            souJcAgreement.setSupplyArea(checkArea(gyqyList, row.getCell(10).toString(), i));
            //发票类型
            souJcAgreement.setInvoiceType(dealWay(row.getCell(11).toString(), "发票类型", i));
            //备注
            souJcAgreement.setRemark(row.getCell(12) == null ? "" : row.getCell(12).toString());
            //金额
            souJcAgreement.setMixAmount(NumberUtils.createBigDecimal(row.getCell(13).toString()));
            //采购组编码
            if ("集采协议".equals(agreementType)) {
                List<SccSouJcAgreementOrg> ol = new ArrayList<>();
                Organization orga = new Organization();
                orga.setOrganizationTypeCode("OU");
                List<Organization> orgList = baseClient.listOrganizationByParam(orga);
                for (Organization e : orgList) {
                    SccSouJcAgreementOrg ao = new SccSouJcAgreementOrg();
                    ao.setBuyOrgId(e.getOrganizationId());
                    ao.setBuyOrgCode(e.getOrganizationCode());
                    ao.setBuyOrgName(e.getOrganizationName());
                    ol.add(ao);
                }
                souJcAgreement.setSccSouJcAgreementOrgList(ol);
            } else {
                if (row.getCell(14) != null) {
                    Set<String> zzCode = Arrays.stream(row.getCell(14).toString().split(",")).collect(Collectors.toSet());
                    souJcAgreement.setSccSouJcAgreementOrgList(dealOrg(zzCode, i));
                }
            }
            agreementList.add(souJcAgreement);
        }
        checkThrow(this.count(new LambdaQueryWrapper<SccSouJcAgreement>().in(SccSouJcAgreement::getAgreementCode, codeSet)) > 0, "协议编号不能重复");
        for (SccSouJcAgreement e : agreementList) {
            e.setCurrencyType("RMB");
            e.setPricingWay("1");
            e.setAgreementType(agreementType);
            e.setAgreementStatus(AgreementStatusEnums.DRAFT.getCode());
            this.save(e);
            List<SccSouJcAgreementOrg> orgList = e.getSccSouJcAgreementOrgList();
            if (CollectionUtils.isNotEmpty(orgList)) {
                orgList.forEach(a -> a.setAgreementId(e.getAgreementId()));
                jcAgreementOrgService.saveBatch(orgList);
            }
        }
    }
    public void checkThrow(Boolean boo, String str) {
        if (boo) {
            throw new BaseException(str);
        }
    }

    public void checkCompany(List<com.midea.cloud.srm.model.pj.base.organization.entity.Organization> companyList, String comCode, int i, SccSouJcAgreement souJcAgreement) {
        //公司主体
        Set<String> supSet = companyList.stream().map(com.midea.cloud.srm.model.pj.base.organization.entity.Organization::getOrganizationCode).collect(Collectors.toSet());
        checkThrow(!supSet.contains(comCode), String.format("第%d行,公司主体编码(%S)不存在", i, comCode));
        for (com.midea.cloud.srm.model.pj.base.organization.entity.Organization e : companyList) {
            if (comCode.equals(e.getOrganizationCode())) {
                souJcAgreement.setCompanyId(e.getOrganizationId());
                souJcAgreement.setCompanyCode(comCode);
                souJcAgreement.setCompanyName(e.getOrganizationName());
            }
        }
    }

    public void checkSup(String supCode, int i, SccSouJcAgreement souJcAgreement) {
        List<String> codeList = new ArrayList<>();
        codeList.add(supCode);
        List<CompanyInfo> supList = supplierClient.listCompanyByCodes(codeList);
        //供应商
        checkThrow(CollectionUtils.isEmpty(supList), String.format("第%d行,供应商编码(%S)不存在", i, supCode));
        for (CompanyInfo e : supList) {
            if (supCode.equals(e.getCompanyCode())) {
                souJcAgreement.setSupId(e.getCompanyId());
                souJcAgreement.setSupCode(supCode);
                souJcAgreement.setSupName(e.getCompanyName());
                break;
            }
        }
    }

    public void checkAgent(String cgyCode, int i, SccSouJcAgreement souJcAgreement) {
        //采购员
        User u = rbacClient.getUserByUserName(cgyCode);
        checkThrow(u == null, String.format("第%d行,采购员编码(%S)不存在", i, cgyCode));
        souJcAgreement.setBuyPersonCode(cgyCode);
        souJcAgreement.setBuyPersonName(u.getNickname());
        souJcAgreement.setBuyPersonId(u.getUserId());
    }

    public String checkArea(List<DictItemDTO> list, String areaName, int i) {
        Set<String> areaNameSet = Arrays.stream(areaName.split(",")).collect(Collectors.toSet());
        Set<String> setName = list.stream().map(DictItemDTO::getDictItemName).collect(Collectors.toSet());
        List<String> nameList = new ArrayList<>();
        for (String ac : areaNameSet) {
            checkThrow(!setName.contains(ac), String.format("第%d行,供应区域(%S)不存在", i + 1, ac));
            for (DictItemDTO dict : list) {
                if (ac.equals(dict.getDictItemName())) {
                    nameList.add(dict.getDictItemCode());
                }
            }
        }
        return String.join(",", nameList);
    }

    public String dealWay(String va, String type, int i) {
        int a = i + 1;
        String text = "付款方式";
        String text2 = "付款条款";
        String text3 = "交易方式";
        String text4 = "发票类型";
        if (text.equals(type)) {
            List<String> code = new ArrayList<>();
            code.add("1");
            code.add("2");
            code.add("3");
            code.add("4");
            code.add("5");
            code.add("6");
            code.add("7");
            code.add("1.0");
            code.add("2.0");
            code.add("3.0");
            code.add("4.0");
            code.add("5.0");
            code.add("6.0");
            code.add("7.0");
            checkThrow(!code.contains(va), String.format("第%d行,付款方式类型错误", a));
        }
        if (text2.equals(type)) {
            List<String> code = new ArrayList<>();
            code.add("NET90");
            code.add("NET80");
            code.add("NET60");
            code.add("NET45");
            code.add("NET40");
            code.add("NET30");
            code.add("NET10");
            code.add("GR90");
            code.add("GR80");
            code.add("GR60");
            code.add("GR45");
            code.add("GR40");
            code.add("GR30");
            code.add("GR10");
            checkThrow(!code.contains(va), String.format("第%d行,付款条款类型错误", a));
        }
        if (text3.equals(type) || text4.equals(type)) {
            List<String> code = new ArrayList<>();
            code.add("1");
            code.add("1.0");
            code.add("2");
            code.add("2.0");
            checkThrow(!code.contains(va), String.format("第%d行,%s错误", a, type));
        }
        if (SouConstant.FPLX.equals(type)) {
            return va.contains("1") ? "SPECIAL_TICKET" : "ORDINARY_TICKET";
        }
        return va.replace(".0", "");
    }

    public Date checkDte(Cell d, String m) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str = d.toString().replace("/", "-") + m;
            return sdf.parse(str);
        } catch (Exception e) {
            throw new BaseException(d + ", 日期格式不正确");
        }
    }

    public List<SccSouJcAgreementOrg> dealOrg(Set<String> zzCode, int i) {
        //采购组织
        List<Organization> listOrganization = new ArrayList<>();
        for (String s : zzCode) {
            Organization o = new Organization();
            o.setOrganizationTypeCode("OU");
            o.setOrganizationCode(s);
            Organization ora = baseClient.getOrganization(o);
            checkThrow(ora == null, String.format("第%d行,存在错误的采购组织编码%s", i, s));
            listOrganization.add(ora);
        }
        List<SccSouJcAgreementOrg> orgList = new ArrayList<>();
        for (String e : zzCode) {
            for (Organization org : listOrganization) {
                if (e.equals(org.getOrganizationCode())) {
                    SccSouJcAgreementOrg aOrg = new SccSouJcAgreementOrg();
                    aOrg.setBuyOrgCode(e);
                    aOrg.setBuyOrgId(org.getOrganizationId());
                    aOrg.setBuyOrgName(org.getOrganizationName());
                    orgList.add(aOrg);
                }
            }
        }
        return orgList;
    }

    public void setLineNum(SccSouJcAgreementInfo info, Long aId) {
        log.info("qwertyasdftyhzxcv===" + aId);
        Integer al = this.getMaxLineInfoNum(aId);
        int a = 1;
        if (al != null) {
            a = al + 1;
        }
        info.setMaterialLine(a);
    }

    public static String cellToString(Object obj) {
        String str;
        if (obj == null || StringUtils.isBlank(obj.toString())) {
            return null;
        }
        str = obj.toString();
        if (str.contains(POINT)) {
            return str.substring(0, str.lastIndexOf("."));
        }
        return str;
    }

    public String slbm(String str) {
        Map<String, String> map = new HashMap<>(16);
        //手动添加
        map.put("VAT_IN_0", "0");
        //荆门精工税率
        map.put("VAT_IN_1", "1");
        //手动添加
        map.put("VAT_IN_13", "13");
        //长城税率
        map.put("VAT_IN_16", "16");
        //手动添加
        map.put("VAT_IN_3", "3");
        //手动添加
        map.put("VAT_IN_5", "5");
        //手动添加
        map.put("VAT_IN_6", "6");
        //手动添加
        map.put("VAT_IN_9", "9");
        return map.get(str);
    }

    public void checkSlbm(String str) {
        checkThrow(StringUtils.isBlank(slbm(str)), String.format("税率编码%S不正确，请按照税率代码表,填写对应的税率编码", str));
    }

    public SccSouJcAgreement getAgreementInfo(String code, String xyType) {
        LambdaQueryWrapper<SccSouJcAgreement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SccSouJcAgreement::getAgreementCode, code);
        queryWrapper.eq(SccSouJcAgreement::getAgreementType, xyType);
        queryWrapper.eq(SccSouJcAgreement::getAgreementStatus, AgreementStatusEnums.DRAFT.getCode());
        SccSouJcAgreement agreement = jcAgreementMapper.selectOne(queryWrapper);
        if (agreement == null || agreement.getAgreementId() == null) {
            throw new BaseException(String.format("协议状态为拟定，%s协议编码为%s的协议信息不存在", xyType, code));
        }
        return agreement;
    }

    public static BigDecimal hsdjInfo(SccSouJcAgreementInfo info) {
        //含税单价 = 未税单价 * (100 + 税率)/100
        BigDecimal sl = (info.getTaxRate().add(new BigDecimal("100"))).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal wsdj = info.getPriceTax();
        return wsdj.multiply(sl).setScale(4, RoundingMode.HALF_UP);
    }

    public void checkMaterial(List<MaterialItem> list, String code, SccSouJcAgreementInfo info) {
        Set<String> materialCodeSet = list.stream().map(MaterialItem::getMaterialCode).collect(Collectors.toSet());
        checkThrow(!materialCodeSet.contains(code), String.format("物料编码(%S)不存在", code));
        list.forEach(e -> {
            if (code.equals(e.getMaterialCode())) {
                info.setMaterialCode(code);
                info.setMaterialName(e.getMaterialName());
                info.setGoodsTypeId(e.getCategoryId());
                info.setGoodsTypeCode(e.getCategoryCode());
                info.setGoodsTypeName(e.getCategoryName());
                info.setStandards(e.getMaterialType());
                info.setUnit(e.getUnit());
                info.setMaterialId(e.getMaterialId());
            }
        });
    }

    /**
     * @return 返回信息
     */
    @Override
    public List<LinePageDto> getLinePageList(LinePageDto ja) {
        //供应区域
        List<DictItemDTO> gyqyList = baseClient.listAllByDictCode("REGION");
        List<LinePageDto> list = jcAgreementMapper.getLinePageList(ja);
        for (LinePageDto e : list) {
            String[] ar = e.getSupplyArea().split(",");
            List<DictItemDTO> ad = new ArrayList<>();
            for (String a : ar) {
                for (DictItemDTO b : gyqyList) {
                    if (a.equals(b.getDictItemCode())) {
                        ad.add(b);
                    }
                }
            }
            e.setAreaList(ad);
        }
       return list;
    }

    @Override
    public List<PriceAgreementDTO> getValidPriceList(List<Long> orgIds, List<String> supplyAreas, List<Long> materialIds) {
        if (CollectionUtils.isEmpty(orgIds) || CollectionUtils.isEmpty(materialIds)) {
            return Collections.emptyList();
        }
        return jcAgreementMapper.getValidPriceList(orgIds, supplyAreas, materialIds);
    }

    /**
     * 导入
     * @param lineList 行列表
     * @param lineTieredList 阶梯价列表
     * @param xyType 协议类型
     */
    @Override
    public void importAgreementLine(List<ImportExcelJcAgreementLineDto> lineList, List<ImportExcelJcAgreementLineTieredDto> lineTieredList, String xyType) {
        List<SccSouTieredPricing> tpList = new ArrayList<>();
        Set<Integer> jtjLineNumSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(lineTieredList)) {
            for (ImportExcelJcAgreementLineTieredDto t : lineTieredList) {
                SccSouTieredPricing tp = new SccSouTieredPricing();
                checkThrow(t.getRowLine() == null, "导入数据_阶梯价页，表格行号不允许为空");
                checkThrow(t.getLessNum() == null, "导入数据_阶梯价页，阶梯上限不允许为空");
                checkThrow(t.getPriceTax() == null, "导入数据_阶梯价页，协议未税单价不允许为空");
                checkThrow(t.getReferPrice() == null, "导入数据_阶梯价页，参考价不允许为空");
                jtjLineNumSet.add(t.getRowLine());
                tp.setLineNum(t.getRowLine());
                tp.setLessNum(t.getLessNum());
                tp.setPriceTax(t.getPriceTax());
                tp.setReferPrice(t.getReferPrice());
                tpList.add(tp);
            }
        }
        Set<Integer> infoLineNumSet = new HashSet<>();
        //物料
        Set<String> maSet = new HashSet<>();
        Map<Integer, List<SccSouTieredPricing>> tpMap = tpList.stream().collect(Collectors.groupingBy(SccSouTieredPricing::getLineNum));
        Map<String, HashSet<String>> cf = new HashMap<>(15);
        if (CollectionUtils.isNotEmpty(lineList)) {
            for (ImportExcelJcAgreementLineDto line : lineList) {
                if (line.getRowLine() != null) {
                    checkThrow(!infoLineNumSet.add(line.getRowLine()), "表格行号不允许重复");
                }
                checkThrow(StringUtils.isBlank(line.getAgreementCode()), "协议编号不允许为空");
                cf.put(line.getAgreementCode(), new HashSet<>());
                checkThrow(StringUtils.isBlank(line.getMaterialCode()), "物料编码不允许为空");
                maSet.add(line.getMaterialCode());
                //协议价类型就是,是否阶梯价（1否2是）
                if (line.getXyjType() == null || line.getXyjType() == 1) {
                    checkThrow(CollectionUtils.isNotEmpty(tpMap.get(line.getRowLine())), "协议价类型为1时, 不允许存在阶梯价");
                } else if (line.getXyjType() == 2) {
                    checkThrow(line.getXyjType() == null, "协议价类型为2时，表格行号不允许为空");
                    checkThrow(CollectionUtils.isEmpty(tpMap.get(line.getXyjType())), String.format("表格行号%d，没有与之对应的阶梯价", line.getXyjType()));
                } else {
                    checkThrow(true, "协议价类型填写错误，请参考填写说明");
                }
                checkThrow(line.getPriceTax() == null, "协议未税单价不允许为空");
                checkThrow(StringUtils.isBlank(line.getTaxRateCode()), "税率编码不允许为空");
                checkSlbm(line.getTaxRateCode());
                checkThrow(line.getReferencePrice() == null, "参考价不允许为空");
                checkThrow(line.getLeadTime() == null, "交货周期不允许为空");
            }
            checkThrow(!infoLineNumSet.containsAll(jtjLineNumSet), "导入数据_阶梯价页的存在表格行号");
            List<MaterialItem> materialList = baseClient.listMaterialByCodeBatch(new ArrayList<>(maSet));
            List<SccSouJcAgreementInfo> infoList = new ArrayList<>();
            for (ImportExcelJcAgreementLineDto line : lineList) {
                SccSouJcAgreementInfo info = new SccSouJcAgreementInfo();
                if (line.getRowLine() != null) {
                    List<SccSouTieredPricing> tList = tpMap.get(line.getRowLine());
                    if (CollectionUtils.isNotEmpty(tList)) {
                        info.setSccSouTieredPricingList(tList);
                    }
                }
                SccSouJcAgreement agreement = getAgreementInfo(line.getAgreementCode(), xyType);
                info.setAgreementId(agreement.getAgreementId());
                checkThrow(!cf.get(line.getAgreementCode()).add(line.getMaterialCode()), String.format("协议编码为%S的物料编码%S重复", line.getAgreementCode(), line.getMaterialCode()));
                Boolean b = agreementInfoService.exists(new LambdaQueryWrapper<SccSouJcAgreementInfo>().eq(SccSouJcAgreementInfo::getAgreementId, agreement.getAgreementId()).eq(SccSouJcAgreementInfo::getMaterialCode, line.getMaterialCode()));
                checkThrow(b, String.format("协议编码为%S的物料编码%S已存在", line.getAgreementCode(), line.getMaterialCode()));
                //物料
                checkMaterial(materialList, line.getMaterialCode(), info);
                info.setIsTieredPricing(line.getXyjType() == null || line.getXyjType() == 1 ? 0 : 1);
                info.setStartNum(line.getStartNum() == null ? 1 : line.getStartNum());
                info.setMultipleStartNum(line.getMultipleStartNum());
                info.setPriceTax(line.getPriceTax());
                info.setTaxRate(new BigDecimal(slbm(line.getTaxRateCode())));
                info.setRatePrice(hsdjInfo(info));
                //参考价
                info.setReferencePrice(line.getReferencePrice());
                info.setLeadTime(line.getLeadTime());
                info.setSellByDate(line.getSellByDate());
                info.setAgreementDes(line.getAgreementDes());
                info.setBrand(line.getBrand());
                setLineNum(info, agreement.getAgreementId());
                List<SccSouJcAgreementOrg> newOrg = jcAgreementOrgService.list(new LambdaQueryWrapper<SccSouJcAgreementOrg>().eq(SccSouJcAgreementOrg::getAgreementId, agreement.getAgreementId()));
                checkMa(agreement.getSupplyArea().split(","), info.getMaterialCode(), newOrg, agreement);
                infoList.add(info);
            }
            agreementInfoService.saveBatch(infoList);
            List<SccSouTieredPricing> allTpList = new ArrayList<>();
            infoList.forEach(e -> {
                List<SccSouTieredPricing> sccSouTieredPricingList = e.getSccSouTieredPricingList();
                if (CollectionUtils.isNotEmpty(sccSouTieredPricingList)) {
                    sccSouTieredPricingList.forEach(a -> a.setAgreementInfoId(e.getAgreementInfoId()));
                    allTpList.addAll(sccSouTieredPricingList);
                }
            });
            if (CollectionUtils.isNotEmpty(allTpList)) {
                tieredPricingService.saveBatch(allTpList);
            }
        }
    }

    /**
     * 编辑页面导入协议行
     *
     * @param lineList 行列表
     * @param lineTieredList 阶梯价列表
     * @param ja 参数
     * @throws IOException 报错
     */
    @Override
    public void importEditAgreementLine(List<ImportExcelJcAgreementLineEditDto> lineList, List<ImportExcelJcAgreementLineTieredDto> lineTieredList, SccSouJcAgreement ja) throws IOException {
        List<SccSouTieredPricing> tpList = new ArrayList<>();
        Set<Integer> jtjLineNumSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(lineTieredList)) {
            for (ImportExcelJcAgreementLineTieredDto t : lineTieredList) {
                SccSouTieredPricing tp = new SccSouTieredPricing();
                checkThrow(t.getRowLine() == null, "导入数据_阶梯价页，表格行号不允许为空");
                checkThrow(t.getLessNum() == null, "导入数据_阶梯价页，阶梯上限不允许为空");
                checkThrow(t.getPriceTax() == null, "导入数据_阶梯价页，协议未税单价不允许为空");
                checkThrow(t.getReferPrice() == null, "导入数据_阶梯价页，参考价不允许为空");
                jtjLineNumSet.add(t.getRowLine());
                tp.setLineNum(t.getRowLine());
                tp.setLessNum(t.getLessNum());
                tp.setPriceTax(t.getPriceTax());
                tp.setReferPrice(t.getReferPrice());
                tpList.add(tp);
            }
        }
        Set<Integer> infoLineNumSet = new HashSet<>();
        //物料
        Set<String> maSet = new HashSet<>();
        Map<Integer, List<SccSouTieredPricing>> tpMap = tpList.stream().collect(Collectors.groupingBy(SccSouTieredPricing::getLineNum));
        if (CollectionUtils.isNotEmpty(lineList)) {
            for (ImportExcelJcAgreementLineEditDto line : lineList) {
                if (line.getRowLine() != null) {
                    checkThrow(!infoLineNumSet.add(line.getRowLine()), "表格行号不允许重复");
                }
                checkThrow(StringUtils.isBlank(line.getMaterialCode()), "物料编码不允许为空");
                maSet.add(line.getMaterialCode());
                //协议价类型就是,是否阶梯价（1否2是）
                if (line.getXyjType() == null || line.getXyjType() == 1) {
                    checkThrow(CollectionUtils.isNotEmpty(tpMap.get(line.getRowLine())), "协议价类型为1时, 不允许存在阶梯价");
                } else if (line.getXyjType() == 2) {
                    checkThrow(line.getXyjType() == null, "协议价类型为2时，表格行号不允许为空");
                    checkThrow(CollectionUtils.isEmpty(tpMap.get(line.getXyjType())), String.format("表格行号%d，没有与之对应的阶梯价", line.getXyjType()));
                } else {
                    checkThrow(true, "协议价类型填写错误，请参考填写说明");
                }
                checkThrow(line.getPriceTax() == null, "协议未税单价不允许为空");
                checkThrow(StringUtils.isBlank(line.getTaxRateCode()), "税率编码不允许为空");
                checkSlbm(line.getTaxRateCode());
                checkThrow(line.getReferencePrice() == null, "参考价不允许为空");
                checkThrow(line.getLeadTime() == null, "交货周期不允许为空");
            }
            checkThrow(!infoLineNumSet.containsAll(jtjLineNumSet), "导入数据_阶梯价页的存在表格行号");
            List<MaterialItem> materialList = baseClient.listMaterialByCodeBatch(new ArrayList<>(maSet));
            List<SccSouJcAgreementInfo> infoList = new ArrayList<>();
            for (ImportExcelJcAgreementLineEditDto line : lineList) {
                SccSouJcAgreementInfo info = new SccSouJcAgreementInfo();
                if (line.getRowLine() != null) {
                    List<SccSouTieredPricing> tList = tpMap.get(line.getRowLine());
                    if (CollectionUtils.isNotEmpty(tList)) {
                        info.setSccSouTieredPricingList(tList);
                    }
                }
                info.setAgreementId(ja.getAgreementId());
                Boolean b = agreementInfoService.exists(new LambdaQueryWrapper<SccSouJcAgreementInfo>().eq(SccSouJcAgreementInfo::getAgreementId, ja.getAgreementId()).eq(SccSouJcAgreementInfo::getMaterialCode, line.getMaterialCode()));
                checkThrow(b, String.format("该协议的物料编码%S已存在", line.getMaterialCode()));
                //物料
                checkMaterial(materialList, line.getMaterialCode(), info);
                info.setIsTieredPricing(line.getXyjType() == null || line.getXyjType() == 1 ? 0 : 1);
                info.setStartNum(line.getStartNum() == null ? 1 : line.getStartNum());
                info.setMultipleStartNum(line.getMultipleStartNum());
                info.setPriceTax(line.getPriceTax());
                info.setTaxRate(new BigDecimal(slbm(line.getTaxRateCode())));
                info.setRatePrice(hsdjInfo(info));
                //参考价
                info.setReferencePrice(line.getReferencePrice());
                info.setLeadTime(line.getLeadTime());
                info.setSellByDate(line.getSellByDate());
                info.setAgreementDes(line.getAgreementDes());
                info.setBrand(line.getBrand());
                setLineNum(info, ja.getAgreementId());
                List<SccSouJcAgreementOrg> newOrg = jcAgreementOrgService.list(new LambdaQueryWrapper<SccSouJcAgreementOrg>().eq(SccSouJcAgreementOrg::getAgreementId, ja.getAgreementId()));
                checkMa(ja.getSupplyArea().split(","), info.getMaterialCode(), newOrg, ja);
                infoList.add(info);
            }
            agreementInfoService.saveBatch(infoList);
            List<SccSouTieredPricing> allTpList = new ArrayList<>();
            infoList.forEach(e -> {
                List<SccSouTieredPricing> sccSouTieredPricingList = e.getSccSouTieredPricingList();
                if (CollectionUtils.isNotEmpty(sccSouTieredPricingList)) {
                    sccSouTieredPricingList.forEach(a -> a.setAgreementInfoId(e.getAgreementInfoId()));
                    allTpList.addAll(sccSouTieredPricingList);
                }
            });
            if (CollectionUtils.isNotEmpty(allTpList)) {
                tieredPricingService.saveBatch(allTpList);
            }
        }
    }

    /**
     * 备注
     *
     * @param agreementId 参数
     * @return 返回
     */
    @Override
    public Integer getMaxLineInfoNum(Long agreementId) {
        return jcAgreementMapper.getMaxLineInfoNum(agreementId);
    }

    /**
     * 协议查询报表
     *
     * @param ja 参数
     * @return list
     */
    @Override
    public List<SccSouJcAgreementDto> getJcHeadLinePageList(SccSouJcAgreementDto ja) {
        return jcAgreementMapper.getJcHeadLinePageList(ja);
    }

    /**
     * 导出集采和合同头加行信息
     *
     * @param ja 参数
     * @return list
     */
    @Override
    public List<ExportJcHtHeadLineData> exportJcHtHeadLineData(SccSouJcAgreementDto ja) {
        if (StringUtils.isBlank(ja.getOrderByStr())) {
            ja.setOrderByStr("ORDER BY LAST_UPDATE_DATE DESC");
        }
        //采购组织
        Set<Long> jaSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(ja.getOrgIds())) {
            List<SccSouJcAgreementOrg> orgList = jcAgreementOrgService.list(new LambdaQueryWrapper<SccSouJcAgreementOrg>().eq(SccSouJcAgreementOrg::getBuyOrgId, ja.getBuyOrgId()));
            Set<Long> agreementIds = orgList.stream().map(SccSouJcAgreementOrg::getAgreementId).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(agreementIds)) {
                jaSet.addAll(agreementIds);
            }
        }
        if (StringUtils.isNotBlank(ja.getMaterialCode())) {
            LambdaQueryWrapper<SccSouJcAgreementInfo> infoQuery = new LambdaQueryWrapper<>();
            infoQuery.select(SccSouJcAgreementInfo::getAgreementId);
            infoQuery.like(SccSouJcAgreementInfo::getMaterialCode, ja.getMaterialCode());
            infoQuery.groupBy(SccSouJcAgreementInfo::getAgreementId);
            List<SccSouJcAgreementInfo> jaiList = agreementInfoService.list(infoQuery);
            if (CollectionUtils.isNotEmpty(jaiList)) {
                Set<Long> agreementIds = jaiList.stream().map(SccSouJcAgreementInfo::getAgreementId).collect(Collectors.toSet());
                jaSet.addAll(agreementIds);
            }
        }
        if (CollectionUtils.isNotEmpty(ja.getAgreementIds()) && CollectionUtils.isNotEmpty(jaSet)) {
            ja.getAgreementIds().addAll(jaSet);
        }

        List<PurchaseUnit> unitList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query(PurchaseUnit.class)
                .eq(PurchaseUnit::getEnabled, YesOrNo.YES.getValue()), PurchaseUnit.class);
        Map<String, String> unitMap = unitList.stream()
                .collect(Collectors.toMap(PurchaseUnit::getUnitCode, PurchaseUnit::getUnitName));

        List<ExportJcHtHeadLineData> list = jcAgreementMapper.exportJcHtHeadLineData(ja);
        Map<String, String> areaMap = baseClient.getDictItmeMapByDictCode("REGION");
        list.forEach(e -> {
            e.setAgreementStatus(agreementStatusStr(e.getAgreementStatus()));
            e.setIsTieredPricing("1".equals(e.getIsTieredPricing()) ? "是" : "否");
            e.setInvoiceType(dealInvoiceType(e.getInvoiceType()));
            String supplyArea = e.getSupplyArea();
            if(StringUtils.isNotEmpty(supplyArea)){
                String[] areaList = supplyArea.split(",");
                ArrayList<String> nameList = new ArrayList<>();
                for (String area : areaList){
                    String name = areaMap.get(area);
                    nameList.add(name);
                }
                String result = String.join(",", nameList);
                e.setSupplyArea(result);
            }
        });
        list.forEach(e -> e.setUnit(unitMap.get(e.getUnit())));
        return list;
    }

    @Override
    public List<SccSouJcAgreementInfo> getjcInfoList(SccSouJcAgreementInfo jcAgreementInfo) {
        return jcAgreementMapper.getjcInfoList(jcAgreementInfo);
    }

    public static String agreementStatusStr(String obj) {
        switch (obj) {
            case "DRAFT":
                return "拟定";
            case "EXECUTE":
                return "待执行";
            case "EXECUTING":
                return "执行中";
            case "STOP":
                return "已终止";
            case "EXPIRED":
                return "已失效";
            default:
                return "";
        }
    }

    public static String dealInvoiceType(String obj) {
        switch (obj) {
            case "SPECIAL_TICKET":
                return "增值税专用发票";
            case "ORDINARY_TICKET":
                return "增值税普通发票";
            default:
                return "";
        }
    }
}
