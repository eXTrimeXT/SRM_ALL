package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.excelhandler;

import com.meicloud.paas.ies.model.ImportResultModel;
import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.srm.constant.NumConstant;
import com.midea.cloud.srm.constant.SouConstant;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationRelation;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.enums.PrRequirementSourceFromTypeEnum;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementImportExcelDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementFromEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sies.pojo.SiesData;
import com.midea.cloud.srm.sies.pojo.SiesImportParam;
import com.midea.cloud.srm.sies.pojo.SiesImportResult;
import com.midea.cloud.srm.sies.pojo.SiesMediator;
import com.midea.cloud.srm.sies.validator.AbstractImportValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 招标计划 - 项目计划导入校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/08
 */
@Component
@Slf4j
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementImportValidator extends AbstractImportValidator {

    @Autowired
    private QlService qlService;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private RbacClient rbacClient;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;
    @Autowired
    private QlOpenClient qlOpenClient;

    private static final DateTimeFormatter LOCAL_DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter LOCAL_DATE_PATTERN2 = DateTimeFormatter.ofPattern("yyyy/MM/d");
    private static final DateTimeFormatter LOCAL_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static final DateTimeFormatter LOCAL_DATE_TIME_PATTERN2 = DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm");
    private static final DateTimeFormatter LOCAL_DATE_TIME_PATTERN3 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private static final DateTimeFormatter LOCAL_DATE_TIME_PATTERN4 = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm");
    private static final DateTimeFormatter LOCAL_DATE_TIME_PATTERN5 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final Pattern YEAR_MONTH_PATTERN = Pattern.compile("^(\\d{4})-(0?[1-9]|1[0-2])$");

    @Override
    public SiesImportResult doValidate(String iesTaskId, SiesImportParam param, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, List<SiesData> dataList) {
        AssertUtils.notEmpty(dataList, "导入文件缺少数据");
        SiesImportResult importResult = new SiesImportResult();

        HashMap<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap = new HashMap<>(dataList.size());
        // 1: 读取数据
        List<ExtPrSouRequirementImportExcelDTO> dtoList = this.readData(dataList, errRowMap);
        // 2: 构造得到实体数据
        PrSouRequirementImportContext context = this.validateAndConvertData(dtoList, errRowMap);
        if (!errRowMap.isEmpty()) {
            List<ImportResultModel.ErrorRow> errList = new ArrayList<>(errRowMap.values());
            errList.sort(Comparator.comparing(ImportResultModel.ErrorRow::getRowNum));
            importResult.setErrorRowList(errList);
            return importResult;
        }
        // 3: 保存数据
        qlService.create(context.prHeadList);
        qlService.create(context.souPrHeadList);
        if (!context.souGroupList.isEmpty()) {
            qlService.create(context.souGroupList);
        }
        if (!context.souVendorList.isEmpty()) {
            qlService.create(context.souVendorList);
        }

        return importResult;
    }

    private List<ExtPrSouRequirementImportExcelDTO> readData(List<SiesData> dataList, HashMap<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap) {
        SimpleDateFormat sLocalDateTimePattern = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat sLocalDateTimePattern2 = new SimpleDateFormat("yyyy/MM/dd H:mm");
        SimpleDateFormat sLocalDateTimePattern3 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat sLocalDateTimePattern4 = new SimpleDateFormat("yyyy-MM-dd H:mm");
        SimpleDateFormat sLocalDateTimePattern5 = new SimpleDateFormat("yyyy-MM-dd HH:mm");


        int index = -1;
        List<ExtPrSouRequirementImportExcelDTO> dtoList = new ArrayList<>(dataList.size());
        for (SiesData data : dataList) {
            index++;
            StringBuilder errSb = new StringBuilder(100);
            ExtPrSouRequirementImportExcelDTO dto = new ExtPrSouRequirementImportExcelDTO();
            dtoList.add(dto);

            // 1: 需求类型
            String demandType = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getDemandType)));
            dto.setDemandType(demandType);
            // 2: 申请公司编码
            String orgCode = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getOrgCode)));
            dto.setOrgCode(orgCode);
            // 3: 技术负责人账号
            String techUsername = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getTechUsername)));
            dto.setTechUsername(techUsername);
            // 4: 需求来源
            String requireFrom = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getRequireFrom)));
            dto.setRequireFrom(requireFrom);
            // 5: 未报月度计划原因
            String noReportMonthPlanReason = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getNoReportMonthPlanReason)));
            dto.setNoReportMonthPlanReason(noReportMonthPlanReason);
            // 6: 项目名称
            String projectName = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getProjectName)));
            dto.setProjectName(projectName);
            // 7: 月份
            String projectMonth = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getProjectMonth)));
            if (projectMonth != null) {
                try {
                    dto.setProjectMonth(projectMonth);
                } catch (NumberFormatException e) {
                    errSb.append("月份格式错误，请填写正整数;");
                }
            }
            // 8: 所属品类编码
            String categoryCode = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getCategoryCode)));
            dto.setCategoryCode(categoryCode);
            // 9: 投资编号
            String investNo = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getInvestNo)));
            dto.setInvestNo(investNo);
            // 10: 数量/规模
            String requireQuantity = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getRequireQuantity)));
            dto.setRequireQuantity(requireQuantity);
            // 11: 概算金额(万元)
            String totalAmountByTenKilo = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getTotalAmountByTenKilo)));
            if (totalAmountByTenKilo != null) {
                try {
                    dto.setTotalAmountByTenKilo(new BigDecimal(totalAmountByTenKilo));
                } catch (NumberFormatException e) {
                    errSb.append("概算金额(万元)格式错误，请填写数字;");
                }
            }
            // 12: 是否公示
            String needPublic = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getNeedPublic)));
            if (needPublic != null) {
                try {
                    dto.setNeedPublic(Enable.valueOf(needPublic));
                } catch (Exception e) {
                    errSb.append(MessageFormat.format("是否公示值错误[{0}]，不是合法的字典值(Y/N);", needPublic));
                }
            }
            // 13: 不公示理由
            String noPublicReason = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getNoPublicReason)));
            dto.setNoPublicReason(noPublicReason);
            // 14: 不公示理由选择
            String noPublicReasonChoose = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getNoPublicReasonChoose)));
            dto.setNoPublicReasonChoose(noPublicReasonChoose);
            // 15: 项目所在地
            String projectAddress = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getProjectAddress)));
            dto.setProjectAddress(projectAddress);
            // 16: 前置技术交流意向
            String prefixTechDiscussion = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getPrefixTechDiscussion)));
            if (prefixTechDiscussion != null) {
                try {
                    dto.setPrefixTechDiscussion(Enable.valueOf(prefixTechDiscussion));
                } catch (Exception e) {
                    errSb.append(MessageFormat.format("前置技术交流意向值错误[{0}]，不是合法的字典值(Y/N);", prefixTechDiscussion));
                }
            }
            // 17: 公示截止时间
            String publicEndTime = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getPublicEndTime)));
            if (publicEndTime != null) {
                try {
                    dto.setPublicEndTime(sLocalDateTimePattern.parse(publicEndTime));
                } catch (ParseException e) {
                    try {
                        dto.setPublicEndTime(sLocalDateTimePattern2.parse(publicEndTime));
                    } catch (ParseException ex) {
                        try {
                            dto.setPublicEndTime(sLocalDateTimePattern3.parse(publicEndTime));
                        } catch (ParseException exx) {
                            try {
                                dto.setPublicEndTime(sLocalDateTimePattern4.parse(publicEndTime));
                            } catch (ParseException exxx) {
                                try {
                                    dto.setPublicEndTime(sLocalDateTimePattern5.parse(publicEndTime));
                                } catch (ParseException exxxx) {
                                    errSb.append(MessageFormat.format("公示截止时间[{0}]格式错误;", publicEndTime));
                                }
                            }
                        }
                    }
                }
            }
            // 18: 递交招标资料时间
            String sendSouProfileEndDate = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getSendSouProfileEndDate)));
            if (sendSouProfileEndDate != null) {
                try {
                    dto.setSendSouProfileEndDate(LocalDate.parse(sendSouProfileEndDate, LOCAL_DATE_PATTERN));
                } catch (DateTimeParseException e) {
                    try {
                        dto.setSendSouProfileEndDate(LocalDate.parse(sendSouProfileEndDate, LOCAL_DATE_PATTERN2));
                    } catch (DateTimeParseException ex) {
                        errSb.append(MessageFormat.format("递交招标资料时间[{0}]格式错误;", sendSouProfileEndDate));
                    }
                }
            }
            // 19: 推荐单位来源1
            String recommendFrom1 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getRecommendFrom1)));
            dto.setRecommendFrom1(recommendFrom1);
            // 20: 推荐单位名称1
            String vendorName1 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorName1)));
            dto.setVendorName1(vendorName1);
            // 21: 联系人名称1
            String vendorContactName1 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorContactName1)));
            dto.setVendorContactName1(vendorContactName1);
            // 22: 联系方式1
            String vendorPhone1 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorPhone1)));
            dto.setVendorPhone1(vendorPhone1);
            // 23: 邮箱1
            String vendorEmail1 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorEmail1)));
            dto.setVendorEmail1(vendorEmail1);
            // 24: 推荐单位来源2
            String recommendFrom2 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getRecommendFrom2)));
            dto.setRecommendFrom2(recommendFrom2);
            // 25: 推荐单位名称2
            String vendorName2 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorName2)));
            dto.setVendorName2(vendorName2);
            // 26: 联系人名称2
            String vendorContactName2 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorContactName2)));
            dto.setVendorContactName2(vendorContactName2);
            // 27: 联系方式2
            String vendorPhone2 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorPhone2)));
            dto.setVendorPhone2(vendorPhone2);
            // 28: 邮箱2
            String vendorEmail2 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorEmail2)));
            dto.setVendorEmail2(vendorEmail2);
            // 29: 推荐单位来源3
            String recommendFrom3 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getRecommendFrom3)));
            dto.setRecommendFrom3(recommendFrom3);
            // 30: 推荐单位名称3
            String vendorName3 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorName3)));
            dto.setVendorName3(vendorName3);
            // 31: 联系人名称3
            String vendorContactName3 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorContactName3)));
            dto.setVendorContactName3(vendorContactName3);
            // 32: 联系方式3
            String vendorPhone3 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorPhone3)));
            dto.setVendorPhone3(vendorPhone3);
            // 33: 邮箱3
            String vendorEmail3 = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorEmail3)));
            dto.setVendorEmail3(vendorEmail3);
            // 34: 项目概况即范围
            String projectOverview = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getProjectOverview)));
            dto.setProjectOverview(projectOverview);
            // 35: 技术要求
            String techRequire = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getTechRequire)));
            dto.setTechRequire(techRequire);
            // 36: 业绩要求
            String performanceRequire = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getPerformanceRequire)));
            dto.setPerformanceRequire(performanceRequire);
            // 37: 供应商资质要求
            String vendorQualificationRequire = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getVendorQualificationRequire)));
            dto.setVendorQualificationRequire(vendorQualificationRequire);
            // 38: 是否限定品牌
            String ifAppointBrand = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getIfAppointBrand)));
            if (ifAppointBrand != null) {
                try {
                    dto.setIfAppointBrand(Enable.valueOf(ifAppointBrand));
                } catch (Exception e) {
                    errSb.append(MessageFormat.format("是否限定品牌值错误[{0}]，不是合法的字典值(Y/N);", ifAppointBrand));
                }
            }
            // 39: 是否限定单位
            String ifQualifyUnit = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtPrSouRequirementImportExcelDTO::getIfQualifyUnit)));
            if (ifQualifyUnit != null) {
                try {
                    dto.setIfQualifyUnit(Enable.valueOf(ifQualifyUnit));
                } catch (Exception e) {
                    errSb.append(MessageFormat.format("是否限定单位值错误[{0}]，不是合法的字典值(Y/N);", ifQualifyUnit));
                }
            }

            if (errSb.length() > 0) {
                ImportResultModel.ErrorRow errRow = errRowMap.get(index);
                if (errRow == null) {
                    errRow = new ImportResultModel.ErrorRow(index, "");
                }
                errRow.setReason(errSb.toString());
                errRowMap.put(index, errRow);
            }
        }

        return dtoList;
    }

    private PrSouRequirementImportContext validateAndConvertData(List<ExtPrSouRequirementImportExcelDTO> dtoList, HashMap<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap) {
        PrSouRequirementImportContext context = new PrSouRequirementImportContext();

        // 前置数据查询
        Set<String> orgCodes = dtoList.stream().map(ExtPrSouRequirementImportExcelDTO::getOrgCode).filter(Objects::nonNull).collect(Collectors.toSet());
        if (!orgCodes.isEmpty()) {
            context.orgMap = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query("base_organization_ide")
                    .in(Organization::getOrganizationCode, new ArrayList<>(orgCodes)))
                    .stream().map(e -> SouObjectXUtil.convertTargetObj(e, Organization.class)).collect(Collectors.toMap(Organization::getOrganizationCode, Function.identity()));
        }
        context.orgMap2 = context.orgMap.values().stream().collect(Collectors.toMap(Organization::getOrganizationId, Function.identity()));
        Map<String/* categoryCode */, PurchaseCategory> categoryMap = Collections.emptyMap(); {
            Set<String> categoryCodes = dtoList.stream().map(ExtPrSouRequirementImportExcelDTO::getCategoryCode).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!categoryCodes.isEmpty()) {
                categoryMap = baseClient.getCategoryByCodes(categoryCodes);
                if (categoryMap == null) { categoryMap = Collections.emptyMap(); }
            }
        }
        context.categoryMap = categoryMap;
        Map<String/* username */, User> userMap = Collections.emptyMap(); {
            Set<String> usernames = dtoList.stream().map(ExtPrSouRequirementImportExcelDTO::getTechUsername).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!usernames.isEmpty()) {
                userMap = rbacClient.listByUserNames(usernames).stream().collect(Collectors.toMap(User::getUsername, Function.identity()));
            }
        }
        context.userMap = userMap;
        Map<String/* dictCode */, Set<String/* itemCode */>> dictMap = Collections.emptyMap(); {
            List<String> dictCodes = new ArrayList<>(); {
                dictCodes.add("DEMAND_TYPE");
                dictCodes.add("YES_OR_NO");
                dictCodes.add("PR_SOU_REQUIREMENT_FROM");
                dictCodes.add("PR_SOU_REQUIREMENT_SPECIAL_TYPE");
                dictCodes.add("PR_SOU_REQUIREMENT_SPECIAL_REASON");
                dictCodes.add("PR_SOU_REQUIREMENT_NO_PUBLIC");
            }
            dictMap = baseClient.listByDictCode(dictCodes).stream()
                    .collect(Collectors.groupingBy(DictItemDTO::getDictCode, Collectors.mapping(DictItemDTO::getDictItemCode, Collectors.toSet())));
        }
        context.dictMap = dictMap;
        Map<Long/* orgId */, List<OrganizationRelation>> orgRelationMap = Collections.emptyMap();
        if (!context.orgMap2.isEmpty()) {
            List<OrganizationRelation> orgRelationList = baseClient.queryByOrganizationIds(context.orgMap2.keySet());
            orgRelationMap = orgRelationList.stream().collect(Collectors.groupingBy(OrganizationRelation::getOrganizationId));
            Set<Long> otherOrgIds = orgRelationList.stream().map(OrganizationRelation::getParentOrganizationId)
                    .filter(Objects::nonNull)
                    .filter(e -> !context.orgMap2.containsKey(e)).collect(Collectors.toSet());
            if (!otherOrgIds.isEmpty()) {
                List<Organization> orgs = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query("base_organization_ide")
                        .in(Organization::getOrganizationId, new ArrayList<>(otherOrgIds)))
                        .stream().map(e -> SouObjectXUtil.convertTargetObj(e, Organization.class)).collect(Collectors.toList());
                orgs.forEach(o -> context.orgMap.put(o.getOrganizationCode(), o));
                orgs.forEach(o -> context.orgMap2.put(o.getOrganizationId(), o));
            }
        }
        context.orgRelationMap = orgRelationMap;

        int index = -1;
        for (ExtPrSouRequirementImportExcelDTO dto : dtoList) {
            index++;
            StringBuilder errSb = new StringBuilder(100);

            // 1: 校验及转化采购申请头
            PrRequirementHead prHead = this.validateAndConvertPrHead(context, dto, errSb);
            context.prHeadList.add(prHead);
            // 2: 校验及转化招标计划
            ExtPrSouRequirementHead souPrHead = this.validateAndConvertSouPrHead(context, dto, prHead, errSb);
            context.souPrHeadList.add(souPrHead);
            // 3: 校验及转化工作成员
            List<ExtPrSouRequirementGroup> souGroupList = this.validateAndConvertSouGroups(context, dto, prHead, souPrHead, errSb);
            context.souGroupList.addAll(souGroupList);
            // 4: 校验及转化推荐供应商
            List<ExtPrSouRequirementVendor> souVendorList = this.validateAndConvertSouVendors(context, dto, prHead, souPrHead, errSb);
            context.souVendorList.addAll(souVendorList);

            if (errSb.length() > 0) {
                ImportResultModel.ErrorRow errRow = errRowMap.get(index);
                if (errRow == null) {
                    errRow = new ImportResultModel.ErrorRow(index, "");
                }
                errRow.setReason(errRow.getReason() + errSb);
                errRowMap.put(index, errRow);
            }
        }

        return context;
    }

    private PrRequirementHead validateAndConvertPrHead(PrSouRequirementImportContext context, ExtPrSouRequirementImportExcelDTO dto, StringBuilder errSb) {
        PrRequirementHead entity = new PrRequirementHead();
        // 1: ID
        entity.setRequirementHeadId(IdGenrator.generate());
        // 2: 采购需求编号
        entity.setRequirementHeadNum(baseClient.seqGen(SequenceCodeConstant.SEQ_PMP_PR_APPLY_NUM));
        // 3: 需求类型
        entity.setDemandType(dto.getDemandType());
        if (entity.getDemandType() == null) {
            errSb.append("请填写需求类型;");
        } else {
            Set<String> dictItemCodes = context.dictMap.get("DEMAND_TYPE");
            if (dictItemCodes == null || !dictItemCodes.contains(entity.getDemandType())) {
                errSb.append("请填写正确的需求类型;");
            }
        }
        // 4: 采购类型(略)
        // 5: 来源类型
        entity.setSourceFromType(PrRequirementSourceFromTypeEnum.HAND_MAKE.name());
        // 6: 来源单据ID(略)
        // 7: 来源单据号(略)
        // 8: 业务实体(申请公司)
        if (dto.getOrgCode() == null) {
            errSb.append("请填写申请公司;");
        } else {
            Organization org = context.orgMap.get(dto.getOrgCode());
            String ou = "OU";
            if (org == null) {
                errSb.append(MessageFormat.format("申请公司[{0}]不存在;", dto.getOrgCode()));
            } else if (!ou.equals(org.getOrganizationTypeCode())) {
                errSb.append(MessageFormat.format("申请公司[{0}]不是业务实体级别;", dto.getOrgCode()));
            } else {
                entity.setOrgId(org.getOrganizationId());
                entity.setOrgCode(org.getOrganizationCode());
                entity.setOrgName(org.getOrganizationName());
            }
        }
        // 9: 库存组织(略)
        // 10: 申请部门
        // 11: 申请日期
        entity.setApplyDate(LocalDate.now());
        // 12: 申请人信息(略)
        if (AppUserUtil.getLoginAppUser() != null) {
            entity.setApplyById(AppUserUtil.getLoginAppUser().getUserId());
            entity.setApplyBy(AppUserUtil.getLoginAppUser().getUsername());
            entity.setApplyByNickname(AppUserUtil.getLoginAppUser().getNickname());
            entity.setApplyDate(LocalDate.now());
            HrUserOrgnizationDto hrInfo = pjProjectExtClient.getHrUserOrgnizationByUsername(entity.getApplyBy());
            if (hrInfo != null) {
                if (hrInfo.getDepartmentOrganization() != null) {
                    entity.setCeeaDepartmentName(hrInfo.getDepartmentOrganization().getOrganizationName());
                }
            }
        }
        // 13: 物料大类(所属品类)
        if (dto.getCategoryCode() == null) {
            errSb.append("请填写所属品类;");
        } else {
            PurchaseCategory category = context.categoryMap.get(dto.getCategoryCode());
            if (category == null) {
                errSb.append(MessageFormat.format("所属品类[{0}]不存在;", dto.getCategoryCode()));
            } else if (!Enable.Y.equals(category.getLastLevelFlag())) {
                errSb.append(MessageFormat.format("所属品类[{0}]不是末级品类;", dto.getCategoryCode()));
            } else {
                entity.setCategoryId(category.getCategoryId());
                entity.setCategoryCode(category.getCategoryCode());
                entity.setCategoryName(category.getCategoryName());
            }
        }
        // 14: 预算管理信息(略)
        // 15: 紧急情况说明(略)
        // 16: 指定原因(略)
        // 17: 备注(略)
        // 18: 审核状态
        entity.setAuditStatus(RequirementApproveStatus.DRAFT);
        // 19: 采购项目(略)

        return entity;
    }

    private ExtPrSouRequirementHead validateAndConvertSouPrHead(PrSouRequirementImportContext context, ExtPrSouRequirementImportExcelDTO dto,
                                                                PrRequirementHead prHead, StringBuilder errSb) {
        ExtPrSouRequirementHead entity = new ExtPrSouRequirementHead();
        // 1: ID
        entity.setRequirementHeadId(prHead.getRequirementHeadId());
        // 2: 所属板块
        if (prHead.getOrgCode() != null) {
            Organization orgOu = context.orgMap.get(prHead.getOrgCode());

            List<OrganizationRelation> orgRelations = context.orgRelationMap.get(orgOu.getOrganizationId());
            for (OrganizationRelation orgRelation : orgRelations) {
                Organization orgBu = context.orgMap2.get(orgRelation.getParentOrganizationId());
                if (orgBu != null && "BU".equals(orgBu.getOrganizationTypeCode())) {
                    entity.setOrgBuId(orgBu.getOrganizationId());
                    entity.setOrgBuCode(orgBu.getOrganizationCode());
                    entity.setOrgBuName(orgBu.getOrganizationName());
                    break;
                }
            }
        }
        // 3: 需求来源
        entity.setRequireFrom(dto.getRequireFrom());
        if (dto.getRequireFrom() == null) {
            errSb.append("请填写需求来源;");
        } else {
            Set<String> dictItemCodes = context.dictMap.get("PR_SOU_REQUIREMENT_FROM");
            if (dictItemCodes == null || !dictItemCodes.contains(entity.getRequireFrom())) {
                errSb.append("请填写正确的需求来源;");
            }
        }
        // 4: 未报月度计划原因
        entity.setNoReportMonthPlanReason(dto.getNoReportMonthPlanReason());
        if (PrSouRequirementFromEnum.WITHOUT_PLAN.name().equals(entity.getRequireFrom())) {
            // 需求来源-计划外
            int num150 = 150;
            if (entity.getNoReportMonthPlanReason() != null && entity.getNoReportMonthPlanReason().length() > num150) {
                errSb.append("未报月度计划原因的输入长度不能超过150;");
            }
        } else {
            entity.setNoReportMonthPlanReason(null);
        }
        // 5: 项目名称
        entity.setProjectName(dto.getProjectName());
        int num80 = 80;
        if (entity.getProjectName() == null) {
            errSb.append("请填写项目名称;");
        } else if (entity.getProjectName().length() > num80) {
            errSb.append("项目名称的输入长度不能超过80;");
        }
        // 6: 月份
        entity.setProjectMonth(dto.getProjectMonth());
        int num12 = 12;
        int num50 = 50;
        if (entity.getProjectMonth() == null) {
            errSb.append("请填写月份;");
        } else if (!checkYearMonth(entity.getProjectMonth())) {
            errSb.append("月份填写错误(格式为yyyy-MM);");
        }
        // 7: 投资编号
        entity.setInvestNo(dto.getInvestNo());
        if (entity.getInvestNo() != null && entity.getInvestNo().length() > num50) {
            errSb.append("投资编号的输入长度不能超过50;");
        }
        // 8: 数量/规模
        entity.setRequireQuantity(dto.getRequireQuantity());
        if (entity.getRequireQuantity() == null) {
            errSb.append("请填写数量/规模;");
        }
        // 9: 概算金额(万元)
        entity.setTotalAmountByTenKilo(dto.getTotalAmountByTenKilo() != null ? dto.getTotalAmountByTenKilo().setScale(6, RoundingMode.HALF_UP) : null);
        if (entity.getTotalAmountByTenKilo() == null) {
            errSb.append("请填写概算金额(万元);");
        } else if (entity.getTotalAmountByTenKilo().compareTo(BigDecimal.ZERO) <= 0) {
            errSb.append("概算金额(万元)必须大于0;");
        }
        // 10: 是否公示
        entity.setNeedPublic(dto.getNeedPublic());
        if (!PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(entity.getRequireFrom())) {
            // 特殊招标
            if (entity.getNeedPublic() == null) {
                entity.setNeedPublic(Enable.N);
            }
        } else {
            entity.setNeedPublic(Enable.N);
        }
        // 11 : 不公示理由
        entity.setNoPublicReasonChoose(StringUtils.trimToNull(dto.getNoPublicReasonChoose()));
        if (Enable.N.equals(entity.getNeedPublic()) && !PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(entity.getRequireFrom())) {
            if (entity.getNoPublicReasonChoose() == null) {
                errSb.append("请填写不公示理由;");
            } else {
                Set<String> dictItemCodes = context.dictMap.get("PR_SOU_REQUIREMENT_NO_PUBLIC");
                if (dictItemCodes == null || !dictItemCodes.contains(entity.getNoPublicReasonChoose())) {
                    errSb.append("请填写正确的不公示理由;");
                }
            }
        } else {
            entity.setNoPublicReasonChoose(null);
        }
        // 12: 不公示理由说明
        entity.setNoPublicReason(dto.getNoPublicReason());
        if (Enable.N.equals(entity.getNeedPublic()) && !PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(entity.getRequireFrom())) {
            entity.setNoPublicReason(StringUtils.trimToNull(entity.getNoPublicReason()));
            if (entity.getNoPublicReason() == null) {
                errSb.append("具体原因说明必填;");
            }
            int num150 = 150;
            if (entity.getNoPublicReason() != null && entity.getNoPublicReason().length() > num150) {
                errSb.append("具体原因说明的输入长度不能超过150;");
            }
        } else {
            entity.setNoPublicReason(null);
        }
        // 13: 公示截止时间
        entity.setPublicEndTime(dto.getPublicEndTime());
        if (Enable.Y.equals(entity.getNeedPublic())) {
            if (entity.getPublicEndTime() != null && !entity.getPublicEndTime().after(new Date())) {
                errSb.append("公示截止时间必须晚于当前时间;");
            }
        } else {
            entity.setPublicEndTime(null);
        }
        // 14: 项目所在地
        entity.setProjectAddress(dto.getProjectAddress());
        int num150 = 150;
        if (entity.getProjectAddress() != null && entity.getProjectAddress().length() > num150) {
            errSb.append("项目所在地的输入长度不能超过150;");
        }
        // 15: 前置技术交流意向
        entity.setPrefixTechDiscussion(dto.getPrefixTechDiscussion());
        if (PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(entity.getRequireFrom()) || PrSouRequirementFromEnum.WITHOUT_PLAN.name().equals(entity.getRequireFrom())) {
            entity.setPrefixTechDiscussion(Enable.N);
        } else {
            if (entity.getPrefixTechDiscussion() == null) {
                entity.setPrefixTechDiscussion(Enable.N);
            }
        }
        // 16: 递交招标资料时间
        entity.setSendSouProfileEndDate(dto.getSendSouProfileEndDate());
        if (Enable.Y.equals(entity.getPrefixTechDiscussion())) {
            if (entity.getSendSouProfileEndDate() != null && !entity.getSendSouProfileEndDate().isAfter(LocalDate.now())) {
                errSb.append("递交招标资料时间必须晚于今天;");
            }
        } else {
            entity.setSendSouProfileEndDate(null);
        }
        // 17: 项目计划编号(略)
        // 18: 特殊招标类型(略)
        // 19: 特定原因(略)
        // 20: 需求产生时间(略)
        // 21: 需求产生时间附件(略)
        // 22: 工期交货期(略)
        // 23: 工期交货期附件(略)
        // 24: 签合同用时(略)
        // 25: 投入使用时间(略)
        // 26: 投入使用时间附件(略)
        // 27: 其他特殊原因补充(略)
        // 28: 项目概况及范围
        entity.setProjectOverview(dto.getProjectOverview());
        // 29: 技术要求
        entity.setTechRequire(dto.getTechRequire());
        // 30: 业绩要求
        entity.setPerformanceRequire(dto.getPerformanceRequire());
        // 31: 供应商资质要求
        entity.setVendorQualificationRequire(dto.getVendorQualificationRequire());
        // 32: 是否限定品牌
        entity.setIfAppointBrand(dto.getIfAppointBrand());
        if (PrSouRequirementFromEnum.MONTH.name().equals(entity.getRequireFrom()) || PrSouRequirementFromEnum.WITHOUT_PLAN.name().equals(entity.getRequireFrom())) {
            // 月度、计划外
            if (entity.getIfAppointBrand() == null) {
                errSb.append("请填写是否限定品牌;");
            }
        } else {
            entity.setIfAppointBrand(Enable.N);
        }
        // 33: 是否限定单位
        entity.setIfQualifyUnit(dto.getIfQualifyUnit());
        if (PrSouRequirementFromEnum.MONTH.name().equals(entity.getRequireFrom()) || PrSouRequirementFromEnum.WITHOUT_PLAN.name().equals(entity.getRequireFrom())) {
            // 月度、计划外
            if (entity.getIfQualifyUnit() == null) {
                errSb.append("请填写是否限定单位;");
            }
        } else {
            entity.setIfQualifyUnit(Enable.N);
        }

        return entity;
    }

    private Boolean checkYearMonth(String yearMonth) {
        if (yearMonth == null) {
            return false;
        }
        return YEAR_MONTH_PATTERN.matcher(yearMonth).matches();
    }

    private List<ExtPrSouRequirementGroup> validateAndConvertSouGroups(PrSouRequirementImportContext context, ExtPrSouRequirementImportExcelDTO dto,
                                                                       PrRequirementHead prHead, ExtPrSouRequirementHead souPrHead, StringBuilder errSb) {
        List<ExtPrSouRequirementGroup> entityList = new ArrayList<>();
        ExtPrSouRequirementGroup entity = new ExtPrSouRequirementGroup();
        entityList.add(entity);

        // 1: ID
        entity.setRequirementGroupId(IdGenrator.generate());
        // 2: 招标计划ID
        entity.setRequirementHeadId(prHead.getRequirementHeadId());
        // 3: 用户
        User user = null;
        if (dto.getTechUsername() == null) {
            errSb.append("请填写技术负责人账号;");
        } else {
            user = context.userMap.get(dto.getTechUsername());
            if (user == null) {
                errSb.append(MessageFormat.format("技术负责人[{0}]不存在", dto.getTechUsername()));
            } else {
                entity.setUserId(user.getUserId());
                entity.setUsername(user.getUsername());
                entity.setFullName(user.getNickname());
            }
        }
        // 4: 工作职责
        entity.setGroupType(PrSouRequirementGroupTypeEnum.TECH.name());
        // 5: 联系方式
        if (user != null) {
            entity.setPhone(user.getPhone());
        }
        // 6: 邮箱(略)
        // 7: 所属部门
        if (user != null) {
            entity.setDepartmentId(user.getCeeaDeptId());
            entity.setDepartmentName(user.getDepartment());
        }
        // 8: 排序
        entity.setSortIndex(1);

        return entityList;
    }

    private List<ExtPrSouRequirementVendor> validateAndConvertSouVendors(PrSouRequirementImportContext context, ExtPrSouRequirementImportExcelDTO dto,
                                                                         PrRequirementHead prHead, ExtPrSouRequirementHead souPrHead, StringBuilder errSb) {
        List<ExtPrSouRequirementVendor> entityList = new ArrayList<>();
        int num3 = 3;
        for (int i = 0; i < num3; i++) {
            ExtPrSouRequirementVendor entity = new ExtPrSouRequirementVendor();

            // 1: ID
            entity.setRequirementVendorId(IdGenrator.generate());
            // 2: 招标计划ID
            entity.setRequirementHeadId(prHead.getRequirementHeadId());
            // 3: 供应商ID(略)
            // 4: 供应商编码(略)
            // 5: 供应商名称
            entity.setVendorName(i == 0 ? dto.getVendorName1() : (i == 1 ? dto.getVendorName2() : dto.getVendorName3()));
            if (entity.getVendorName() == null) { continue; }
            // 6: 联系人名称
            entity.setContactName(i == 0 ? dto.getVendorContactName1() : (i == 1 ? dto.getVendorContactName2() : dto.getVendorContactName3()));
            // 7: 联系方式
            entity.setPhone(i == 0 ? dto.getVendorPhone1() : (i == 1 ? dto.getVendorPhone2() : dto.getVendorPhone3()));
            // 8: 邮箱
            entity.setEmail(i == 0 ? dto.getVendorEmail1() : (i == 1 ? dto.getVendorEmail2() : dto.getVendorEmail3()));
            // 9: 推荐来源
            entity.setRecommendFrom(i == 0 ? dto.getRecommendFrom1() : (i == 1 ? dto.getRecommendFrom2() : dto.getRecommendFrom3()));

            entityList.add(entity);
        }

        int index = 0;
        for (ExtPrSouRequirementVendor entity : entityList) {
            entity.setSortIndex(++index);
        }

        return entityList;
    }

    private static class PrSouRequirementImportContext {
        List<PrRequirementHead> prHeadList = new ArrayList<>(20);
        List<ExtPrSouRequirementHead> souPrHeadList = new ArrayList<>(20);
        List<ExtPrSouRequirementGroup> souGroupList = new ArrayList<>(20);
        List<ExtPrSouRequirementVendor> souVendorList = new ArrayList<>(60);

        Map<Long/* orgId */, List<OrganizationRelation>> orgRelationMap = new HashMap<>(50);
        Map<String/* organizationCode */, Organization> orgMap = new HashMap<>(50);
        Map<Long/* organizationId */, Organization> orgMap2 = new HashMap<>(50);
        Map<String/* categoryCode */, PurchaseCategory> categoryMap = new HashMap<>(50);
        Map<String/* username */, User> userMap = new HashMap<>(50);
        Map<String/* dictCode */, Set<String/* itemCode */>> dictMap = new HashMap<>(50);
    }

    public static void main(String[] args) {
        String s = "2023-10-30 0:00";
        LocalDateTime d = LocalDateTime.parse(s, LOCAL_DATE_TIME_PATTERN2);
        log.info("123");
    }

}
