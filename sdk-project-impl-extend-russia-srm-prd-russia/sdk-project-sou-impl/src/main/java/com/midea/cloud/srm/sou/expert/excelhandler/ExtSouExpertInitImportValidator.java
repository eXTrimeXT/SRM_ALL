package com.midea.cloud.srm.sou.expert.excelhandler;

import com.meicloud.paas.ies.model.ImportResultModel;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.base.organization.entity.OrganizationRelation;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.expert.dto.*;
import com.midea.cloud.srm.model.sou.expert.entity.*;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyFromTypeEnum;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyStatusEnum;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertJobStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sies.pojo.*;
import com.midea.cloud.srm.sies.validator.AbstractImportValidator;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 专家库初始化导入校验
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/08
 */
@Component
@Slf4j
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtSouExpertInitImportValidator extends AbstractImportValidator {

    @Autowired
    private QlService qlService;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private QlOpenClient qlOpenClient;

    private static final DateTimeFormatter LOCAL_DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter LOCAL_DATE_PATTERN2 = DateTimeFormatter.ofPattern("yyyy/MM/d");
    private static final DateTimeFormatter LOCAL_DATE_PATTERN3 = DateTimeFormatter.ofPattern("yyyy/M/dd");
    private static final DateTimeFormatter LOCAL_DATE_PATTERN4 = DateTimeFormatter.ofPattern("yyyy/M/d");

    private final static int NUM30=30;
    private final static int NUM50=50;
    private final static int NUM255=255;
    private final static String OU = "OU";
    private final static String BU = "BU";
    private final static String GROUP = "GROUP";

    /**
     * 没法用ThreadLocal处理
     */
    private static final ConcurrentHashMap<String, ExtSouExpertInitImportContext> CONTEXT_HOLDER = new ConcurrentHashMap<>(64);

    private String getKeyForContextHolder() {
        return AppUserUtil.getUserName();
    }

    @Override
    public void beforeValidate(SiesExecuteParam param) {
        CONTEXT_HOLDER.put(getKeyForContextHolder(), new ExtSouExpertInitImportContext());
    }

    @Override
    public void afterValidate(boolean isSuccess, SiesExecuteParam param) {
        if (isSuccess) {
            ExtSouExpertInitImportContext context = CONTEXT_HOLDER.get(getKeyForContextHolder());
            if (context != null) {
                try {
                    qlService.create(context.expertEntityList);
                    qlService.create(context.expertApplyEntityList);
                    qlService.create(context.expertEducationEntityList);
                    qlService.create(context.expertWorkEntityList);
                    if (CollectionUtils.isNotEmpty(context.expertWorkRelationEntityList)) {
                        qlService.create(context.expertWorkRelationEntityList);
                    }
                    if (CollectionUtils.isNotEmpty(context.expertOrgEntityList)) {
                        qlService.create(context.expertOrgEntityList);
                    }
                    if (CollectionUtils.isNotEmpty(context.expertCategoryEntityList)) {
                        qlService.create(context.expertCategoryEntityList);
                    }
                } finally {
                    CONTEXT_HOLDER.remove(getKeyForContextHolder());
                }
            }
        }
    }

    @Override
    public SiesImportResult doValidate(String iesTaskId, SiesImportParam param, SiesMediator curMediator, int sheetNo, String sheetName, Integer batchNo, List<SiesData> dataList) {
        ExtSouExpertInitImportContext context = CONTEXT_HOLDER.get(getKeyForContextHolder());
        AssertUtils.notEmpty(dataList, "导入文件缺少数据");
        SiesImportResult importResult = new SiesImportResult();
        int num0 = 0;
        int num1 = 1;
        int num2 = 2;
        int num3 = 3;
        int num4 = 4;

        Map<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap = new HashMap<>(dataList.size());
        // 1: 读取数据+构造实体数据
        if (sheetNo == num0) {
            // 专家库初始化数据
            List<ExtSouExpertInitImportExcelDTO> dtoList = this.readDataForBaseEducationWork(dataList, errRowMap);
            this.validateAndConvertDataForBaseEductionWork(dtoList, errRowMap);
        } else if (sheetNo == num1) {
            // 适用评分组织
            List<ExtSouExpertInitOrgImportExcelDTO> dtoList = this.readDataForOrgRelation(dataList, errRowMap);
            this.validateAndConvertDataForOrgRelation(dtoList, errRowMap);
        } else if (sheetNo == num2) {
            // 适用品类
            List<ExtSouExpertInitCategoryImportExcelDTO> dtoList = this.readDataForCategoryRelation(dataList, errRowMap);
            this.validateAndConvertDataForCategoryRelation(dtoList, errRowMap);
        } else if (sheetNo == num3) {
            // 工作履历
            List<ExtSouExpertInitWorkImportExcelDTO> dtoList = this.readDataForWork(dataList, errRowMap);
            this.validateAndConvertDataForWork(dtoList, errRowMap);
        } else if (sheetNo == num4) {
            // 亲属工作单位
            List<ExtSouExpertInitWorkRelationImportExcelDTO> dtoList = this.readDataForWorkRelation(dataList, errRowMap);
            this.validateAndConvertDataForWorkRelation(dtoList, errRowMap);
        }
        if (!errRowMap.isEmpty()) {
            List<ImportResultModel.ErrorRow> errList = new ArrayList<>(errRowMap.values());
            errList.sort(Comparator.comparing(ImportResultModel.ErrorRow::getRowNum));
            importResult.setErrorRowList(errList);
            return importResult;
        }

        return importResult;
    }

    @ApiOperation("专家库初始化数据: sheet数据解读")
    private List<ExtSouExpertInitImportExcelDTO> readDataForBaseEducationWork(List<SiesData> dataList, Map<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap) {
        int index = -1;
        List<ExtSouExpertInitImportExcelDTO> dtoList = new ArrayList<>(dataList.size());
        for (SiesData data : dataList) {
            index++;
            StringBuilder errSb = new StringBuilder(100);
            ExtSouExpertInitImportExcelDTO dto = new ExtSouExpertInitImportExcelDTO();
            dtoList.add(dto);

            // 1: 公司名称
            String orgOuName = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getOrgOuName)));
            dto.setOrgOuName(orgOuName);
            // 2: 部门名称
            String departmentName = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getDepartmentName)));
            dto.setDepartmentName(departmentName);
            // 3: 姓名
            String applyByNickname = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getApplyByNickname)));
            dto.setApplyByNickname(applyByNickname);
            // 4: 工号
            String applyByCode = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getApplyByCode)));
            dto.setApplyByCode(applyByCode);
            // 5: 最高学历
            String education = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getEducation)));
            dto.setEducation(education);
            // 6: 毕业时间
            extracted(data, errSb, dto);
            // 13: 毕业院校
            String studyCollege = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getStudyCollege)));
            dto.setStudyCollege(studyCollege);
            // 14: 所学专业
            String major = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getMajor)));
            dto.setMajor(major);
            // 15: 申报等级
            String expertLevel = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getExpertLevel)));
            dto.setExpertLevel(expertLevel);

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

    /**
     * 拆分
     * @param data 参数
     * @param errSb 参数
     * @param dto 参数
     */
    private static void extracted(SiesData data, StringBuilder errSb, ExtSouExpertInitImportExcelDTO dto) {
        String studyDateTo = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getStudyDateTo)));
        if (studyDateTo != null) {
            try {
                dto.setStudyDateTo(LocalDate.parse(studyDateTo, LOCAL_DATE_PATTERN));
            } catch (DateTimeParseException e) {
                try {
                    dto.setStudyDateTo(LocalDate.parse(studyDateTo, LOCAL_DATE_PATTERN2));
                } catch (DateTimeParseException ex) {
                    try {
                        dto.setStudyDateTo(LocalDate.parse(studyDateTo, LOCAL_DATE_PATTERN3));
                    } catch (DateTimeParseException exx) {
                        try {
                            dto.setStudyDateTo(LocalDate.parse(studyDateTo, LOCAL_DATE_PATTERN4));
                        } catch (DateTimeParseException exxx) {
                            errSb.append(MessageFormat.format("毕业时间[{0}]格式错误;", studyDateTo));
                        }
                    }
                }
            }
        }
        // 7: 性别
        String sex = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getSex)));
        dto.setSex(sex);
        // 8: 职务
        String job = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getJob)));
        dto.setJob(job);
        // 9: 序列等级
        String jobRank = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getJobRank)));
        dto.setJobRank(jobRank);
        // 10: 在职状态
        String jobStatus = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getJobStatus)));
        dto.setJobStatus(jobStatus);
        // 11: 手机号码
        String phone = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getPhone)));
        dto.setPhone(phone);
        // 12: 入厂时间
        String entryDate = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitImportExcelDTO::getEntryDate)));
        if (entryDate != null) {
            try {
                dto.setEntryDate(LocalDate.parse(entryDate, LOCAL_DATE_PATTERN));
            } catch (DateTimeParseException e) {
                try {
                    dto.setEntryDate(LocalDate.parse(entryDate, LOCAL_DATE_PATTERN2));
                } catch (DateTimeParseException ex) {
                    try {
                        dto.setEntryDate(LocalDate.parse(entryDate, LOCAL_DATE_PATTERN3));
                    } catch (DateTimeParseException exx) {
                        try {
                            dto.setEntryDate(LocalDate.parse(entryDate, LOCAL_DATE_PATTERN4));
                        } catch (DateTimeParseException exxx) {
                            errSb.append(MessageFormat.format("入厂时间[{0}]格式错误(必须满足 年/月/日);", entryDate));
                        }
                    }
                }
            }
        }
    }

    @ApiOperation("专家库初始化数据: sheet数据校验转换")
    private void validateAndConvertDataForBaseEductionWork(List<ExtSouExpertInitImportExcelDTO> dtoList, Map<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap) {
        ExtSouExpertInitImportContext context = CONTEXT_HOLDER.get(getKeyForContextHolder());

        Map<String/* dictCode */, Set<String/* itemCode */>> dictMap = Collections.emptyMap(); {
            List<String> dictCodes = new ArrayList<>(); {
                // 在职状态
                dictCodes.add("EXT_SOU_EXPERT_JOB_STATUS");
                // 学历
                dictCodes.add("EXT_SOU_EXPERT_EDUCATION");
                // 性别
                dictCodes.add("EXT_SOU_EXPERT_SEX");
                // 申请等级
                dictCodes.add("EXT_SOU_EXPERT_LEVEL");
            }
            dictMap = baseClient.listByDictCode(dictCodes).stream()
                    .collect(Collectors.groupingBy(DictItemDTO::getDictCode, Collectors.mapping(DictItemDTO::getDictItemCode, Collectors.toSet())));
        }
        context.dictMap = dictMap;
        context.orgMap = baseClient.listAllOrganization()
                .stream().collect(Collectors.toMap(Organization::getOrganizationName, Function.identity(), (a, b) -> a));
        context.orgMap2 = context.orgMap.values().stream().collect(Collectors.toMap(Organization::getOrganizationId, Function.identity()));
        context.availableExpertApplyNoList = baseClient.batchGetSeq("SEQ_SOU_EXPERT_APPLY_NO",dtoList.size());
        Map<String/* ceeaEmpNo */, User> userMap = Collections.emptyMap(); {
            Set<String> ceeaEmpNos = dtoList.stream().map(ExtSouExpertInitImportExcelDTO::getApplyByCode).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!ceeaEmpNos.isEmpty()) {
                userMap = qlOpenClient.query(ContextPath.RBAC, QlOpenWrappers.query("rbac_user_ide")
                        .in(User::getCeeaEmpNo, new ArrayList<>(ceeaEmpNos)))
                        .stream()
                        .map(e -> SouObjectXUtil.convertTargetObj(e, User.class))
                        .collect(Collectors.toMap(User::getCeeaEmpNo, Function.identity(), (a, b) -> a));
            }
        }
        context.userMap = userMap;
        Map<String/* ceeaEmpNo */, ExtSouExpertApply> existExpertApplyMap = Collections.emptyMap(); {
            Set<String> ceeaEmpNos = dtoList.stream().map(ExtSouExpertInitImportExcelDTO::getApplyByCode).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!ceeaEmpNos.isEmpty()) {
                existExpertApplyMap = qlService.queryByWrapper(QlWrappers.query(ExtSouExpertApply.class)
                        .in(ExtSouExpertApply::getApplyByCode, new ArrayList<>(ceeaEmpNos)), ExtSouExpertApply.class)
                        .stream().collect(Collectors.toMap(ExtSouExpertApply::getApplyByCode, Function.identity()));
            }
        }
        context.existExpertApplyMap = existExpertApplyMap;


        int index = -1;
        Date now = new Date();
        Set<String> applyByCodes = new HashSet<>(dtoList.size());
        for (ExtSouExpertInitImportExcelDTO dto : dtoList) {
            index++;
            StringBuilder errSb = new StringBuilder(100);
            ImportResultModel.ErrorRow errRow = errRowMap.get(index);

            ExtSouExpert expert = new ExtSouExpert();
            ExtSouExpertApply expertApply = new ExtSouExpertApply();
            ExtSouExpertEducation expertEducation = new ExtSouExpertEducation();
            ExtSouExpertWork expertWork = new ExtSouExpertWork(); {
                expert.setExpertId(IdGenrator.generate());
                expertApply.setExpertApplyId(IdGenrator.generate());
                expertEducation.setExpertEducationId(IdGenrator.generate());
                expertWork.setExpertWorkId(IdGenrator.generate());

                context.expertEntityList.add(expert);
                context.expertApplyEntityList.add(expertApply);
                context.expertEducationEntityList.add(expertEducation);
                context.expertWorkEntityList.add(expertWork);
            }

            extracted(context, index, now, applyByCodes, dto, errSb, errRow, expert, expertApply);
            extracted(errRowMap, index, dto, errSb, errRow, expertApply, expertEducation, expertWork);
        }
    }

    /**
     * 组装数据
     * @param context 参数
     * @param index 参数
     * @param now 参数
     * @param applyByCodes 参数
     * @param dto 参数
     * @param errSb 参数
     * @param errRow 参数
     * @param expert 参数
     * @param expertApply 参数
     */
    private static void extracted(ExtSouExpertInitImportContext context, int index, Date now, Set<String> applyByCodes, ExtSouExpertInitImportExcelDTO dto, StringBuilder errSb, ImportResultModel.ErrorRow errRow, ExtSouExpert expert, ExtSouExpertApply expertApply) {
        // 1: 填补专家申请信息
        {
            // 1.1: 专家申请编号
            expertApply.setExpertApplyNo(context.availableExpertApplyNoList.get(index));
            // 1.2: 数据来源
            expertApply.setApplyFromType(ExtSouExpertApplyFromTypeEnum.GREEN_CHANNEL.name());
            // 1.3: 申请人信息
            User user = context.userMap.get(dto.getApplyByCode());
            ExtSouExpertApply existExpertApply = context.existExpertApplyMap.get(dto.getApplyByCode());
            if (!applyByCodes.add(dto.getApplyByCode())) {
                errSb.append("导入数据中存在多行数据是同一个工号;");
            } else if (existExpertApply != null) {
                errSb.append("该用户已有专家申请相关信息，不能进行初始化;");
                expertApply.setApplyById(existExpertApply.getApplyById());
                expertApply.setApplyBy(existExpertApply.getApplyBy());
                expertApply.setApplyByNickname(existExpertApply.getApplyByNickname());
            } else {
                if (user == null) {
                    errSb.append("无法根据工号找到用户;");
                } else {
                    expertApply.setApplyById(user.getUserId());
                    expertApply.setApplyBy(user.getUsername());
                    expertApply.setApplyByNickname(user.getNickname());
                }
            }
            expertApply.setApplyByCode(dto.getApplyByCode());
            expertApply.setApplyTime(now);
            // 1.4: 申请状态
            expertApply.setApplyStatus(ExtSouExpertApplyStatusEnum.APPROVED.name());
            // 1.5: 是否已提交
            expertApply.setHasSubmit(Enable.Y);
            // 1.6: 最高学历
            expertApply.setHighestDegree(dto.getEducation());
            if (expertApply.getHighestDegree() == null) {
                errSb.append("请填写最高学历;");
            } else {
                Set<String> dictItemCodes = context.dictMap.get("EXT_SOU_EXPERT_EDUCATION");
                if (dictItemCodes == null || !dictItemCodes.contains(expertApply.getHighestDegree())) {
                    errSb.append("请填写正确的最高学历;");
                }
            }
            // 1.7: 性别
            expertApply.setSex(dto.getSex());
            if (expertApply.getSex() == null) {
                errSb.append("请填写性别;");
            } else {
                Set<String> dictItemCodes = context.dictMap.get("EXT_SOU_EXPERT_SEX");
                if (dictItemCodes == null || !dictItemCodes.contains(expertApply.getSex())) {
                    errSb.append("请填写正确的性别;");
                }
            }
            // 1.8: 所属公司
            expertApply.setOrgOuName(dto.getOrgOuName());
            if (expertApply.getOrgOuName() == null) {
                errSb.append("请填写公司");
            } else {
                Organization orgOu = context.orgMap.get(expertApply.getOrgOuName());
                if (orgOu == null) {
                    errSb.append(MessageFormat.format("公司[{0}]不存在;", expertApply.getOrgOuName()));
                } else if (!OU.equals(orgOu.getOrganizationTypeCode())) {
                    errSb.append(MessageFormat.format("组织[{0}]不是公司级别;", expertApply.getOrgOuName()));
                } else {
                    expertApply.setOrgOuId(orgOu.getOrganizationId());
                    expertApply.setOrgOuCode(orgOu.getOrganizationCode());
                }
            }
            // 1.9: 部门
            expertApply.setDepartmentName(dto.getDepartmentName());
            if (expertApply.getDepartmentName() == null) {
                errSb.append("请填写部门;");
            }
            // 1.10: 职务
            expertApply.setJob(dto.getJob());
            if (expertApply.getJob() == null) {
                errSb.append("请填写职务;");
            } else {
                if (expertApply.getJob().length() > NUM30) {
                    errSb.append("职务的输入长度不能超过30;");
                }
            }
            // 1.11: 职务级别(序列等级)
            expertApply.setJobRank(dto.getJobRank());
            if (expertApply.getJobRank() != null) {
                if (expertApply.getJobRank().length() > NUM30) {
                    errSb.append("序列等级的输入长度不能超过30;");
                }
            }
            // 1.12: 申请等级
            expertApply.setExpertLevel(dto.getExpertLevel());
            if (expertApply.getExpertLevel() == null) {
                errSb.append("请填写申请等级;");
            } else {
                Set<String> dictItemCodes = context.dictMap.get("EXT_SOU_EXPERT_LEVEL");
                if (dictItemCodes == null || !dictItemCodes.contains(expertApply.getExpertLevel())) {
                    errSb.append("请填写正确的申请等级;");
                }
            }
            // 1.13: 在职状态
            expertApply.setJobStatus(dto.getJobStatus());
            if (expertApply.getJobStatus() == null) {
                errSb.append("请填写在职状态;");
            } else {
                Set<String> dictItemCodes = context.dictMap.get("EXT_SOU_EXPERT_JOB_STATUS");
                if (dictItemCodes == null || !dictItemCodes.contains(expertApply.getJobStatus())) {
                    errSb.append("请填写正确的在职状态;");
                }
            }
            // 1.14: 手机号码
            expertApply.setPhone(dto.getPhone());
            if (expertApply.getPhone() != null && expertApply.getPhone().length() > NUM50) {
                errSb.append("手机号码的输入长度不能超过50;");
            }
            // 1.15: 入厂时间
            expertApply.setHireDate(dto.getEntryDate());
            if (expertApply.getHireDate() == null) {
                boolean hasErrMsg = errRow != null && errRow.getReason().contains("入厂时间");
                if (!hasErrMsg) {
                    errSb.append("请填写入厂时间;");
                }
            }
            // 1.16: 申请等级
            expertApply.setApplyLevel(expertApply.getExpertLevel());
            // 1.17: 绿色通道原因
            expertApply.setGreenReason("初始化导入");
        }
        // 2: 填补专家信息
        {
            // 2.1: 专家申请ID
            expert.setExpertApplyId(expertApply.getExpertApplyId());
            // 2.2: 申请单ID记录
            expert.setApplyIdFullPath(expertApply.getExpertApplyId().toString());
            // 2.3: 专家申请编号
            expert.setExpertApplyNo(expertApply.getExpertApplyNo());
            // 2.4: 专家等级
            expert.setExpertLevel(expertApply.getExpertLevel());
            // 2.5: 专家ID
            expert.setExpertUserId(expertApply.getApplyById());
            // 2.6: 专家账号
            expert.setExpertUsername(expertApply.getApplyBy());
            // 2.7: 专家工号
            expert.setExpertUserCode(expertApply.getApplyByCode());
            // 2.8: 专家昵称
            expert.setExpertFullName(expertApply.getApplyByNickname());
            // 2.9: 在职状态
            expert.setJobStatus(expertApply.getJobStatus());
            // 2.10: 是否已退出
            expert.setHasQuite(Enable.N);
            // 2.11: 是否已冻结
            expert.setHasFrozen(Enable.N);
        }
    }

    /**
     * 组装数据
     * @param errRowMap 参数
     * @param index 参数
     * @param dto 参数
     * @param errSb 参数
     * @param errRow 参数
     * @param expertApply 参数
     * @param expertEducation 参数
     * @param expertWork 参数
     */
    private static void extracted(Map<Integer, ImportResultModel.ErrorRow> errRowMap, int index, ExtSouExpertInitImportExcelDTO dto, StringBuilder errSb, ImportResultModel.ErrorRow errRow, ExtSouExpertApply expertApply, ExtSouExpertEducation expertEducation, ExtSouExpertWork expertWork) {
        // 3: 填补专家学历
        {
            // 3.1: 专家申请ID
            expertEducation.setExpertApplyId(expertApply.getExpertApplyId());
            // 3.2: 学历
            expertEducation.setEducation(expertApply.getHighestDegree());
            // 3.3: 就读院校
            expertEducation.setStudyCollege(dto.getStudyCollege());
            if (expertEducation.getStudyCollege() == null) {
                errSb.append("请填写就读院校;");
            } else {
                if (expertEducation.getStudyCollege().length() > NUM255) {
                    errSb.append("就读院校的输入长度不能超过255;");
                }
            }
            // 3.4: 是否全日制
            expertEducation.setFullTimeStudy(Enable.Y);
            // 3.5: 就读时间从
            // 3.6: 就读时间到
            expertEducation.setStudyDateTo(dto.getStudyDateTo());
            if (expertEducation.getStudyDateTo() == null) {
                boolean hasErrMsg = errRow != null && errRow.getReason().contains("毕业时间");
                if (!hasErrMsg) {
                    errSb.append("请填写毕业时间;");
                }
            }
            // 3.7: 主修专业
            expertEducation.setMajor(dto.getMajor());
            if (expertEducation.getMajor() == null) {
                errSb.append("请填写主修专业;");
            }
            // 3.8: 排序
            expertEducation.setSortIndex(1);
        }
        // 4: 填补专家工作经历
        {
            // 4.1: 专家申请ID
            expertWork.setExpertApplyId(expertApply.getExpertApplyId());
            // 4.2: 工作单位
            expertWork.setWorkUnit(expertApply.getOrgOuName() + expertApply.getDepartmentName());
            // 4.3: 职务
            expertWork.setJob(expertApply.getJob());
            // 4.4: 序列等级
            expertWork.setJobRank(expertApply.getJobRank());
            // 4.5: 入职时间
            expertWork.setEntryDate(expertApply.getHireDate());
            // 4.6: 排序
            expertWork.setSortIndex(1);
        }

        if (errSb.length() > 0) {
            if (errRow == null) {
                errRow = new ImportResultModel.ErrorRow(index, "");
            }
            errRow.setReason(errRow.getReason() + errSb);
            errRowMap.put(index, errRow);
        }
    }

    @ApiOperation("适用评分组织: sheet数据解读")
    private List<ExtSouExpertInitOrgImportExcelDTO> readDataForOrgRelation(List<SiesData> dataList, Map<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap) {
        int index = -1;
        List<ExtSouExpertInitOrgImportExcelDTO> dtoList = new ArrayList<>(dataList.size());
        for (SiesData data : dataList) {
            index++;
            StringBuilder errSb = new StringBuilder(100);
            ExtSouExpertInitOrgImportExcelDTO dto = new ExtSouExpertInitOrgImportExcelDTO();
            dtoList.add(dto);

            // 1: 姓名
            String applyByNickname = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitOrgImportExcelDTO::getApplyByNickname)));
            dto.setApplyByNickname(applyByNickname);
            // 2: 工号
            String applyByCode = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitOrgImportExcelDTO::getApplyByCode)));
            dto.setApplyByCode(applyByCode);
            // 3: 适用组织
            String orgName = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitOrgImportExcelDTO::getOrgName)));
            dto.setOrgName(orgName);

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

    @ApiOperation("适用评分组织: sheet数据校验转换")
    private void validateAndConvertDataForOrgRelation(List<ExtSouExpertInitOrgImportExcelDTO> dtoList, Map<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap) {
        ExtSouExpertInitImportContext context = CONTEXT_HOLDER.get(getKeyForContextHolder());

        Map<String/* applyByCode(工号) */, ExtSouExpertApply> expertApplyEntityMap = context.expertApplyEntityList.stream()
                .collect(Collectors.toMap(ExtSouExpertApply::getApplyByCode, Function.identity(), (a, b) -> a));
        // 2: 查询组织关系
        List<OrganizationRelation> organizationRelationList; {
            Function<List<Long>, Collection<OrganizationRelation>> groupFunction = (a) -> qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query("OrganizationRelation")
                    .in(OrganizationRelation::getOrganizationId, a), 1L, 7000L, OrganizationRelation.class).getRecords();

            organizationRelationList = new ArrayList<>(20000);
            organizationRelationList.addAll(qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query("OrganizationRelation"), 1L, 8000L, OrganizationRelation.class).getRecords());
            organizationRelationList.addAll(qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query("OrganizationRelation"), 2L, 8000L, OrganizationRelation.class).getRecords());
        }
        // 3: 搜集组织父子关系
        Map<Long/* orgId(子) */, Set<Long/* orgId(父) */>> orgFatherChildMap = organizationRelationList.stream()
                .filter(e -> e.getParentOrganizationId() != null)
                .collect(Collectors.groupingBy(OrganizationRelation::getOrganizationId, Collectors.mapping(OrganizationRelation::getParentOrganizationId, Collectors.toSet())));

        int index = -1;
        for (ExtSouExpertInitOrgImportExcelDTO dto : dtoList) {
            index++;
            StringBuilder errSb = new StringBuilder(100);
            ImportResultModel.ErrorRow errRow = errRowMap.get(index);

            if (dto.getApplyByCode() == null) {
                errSb.append("请填写工号;");
            } else {
                ExtSouExpertApply expertApplyEntity = expertApplyEntityMap.get(dto.getApplyByCode());
                if (expertApplyEntity == null) {
                    errSb.append(MessageFormat.format("工号[{0}]在第一个sheet页(专家库初始化数据)中不存在;", dto.getApplyByCode()));
                } else {
                    ExtSouExpertOrgRelation orgRelation = new ExtSouExpertOrgRelation();
                    context.expertOrgEntityList.add(orgRelation);

                    orgRelation.setExpertOrgId(IdGenrator.generate());
                    orgRelation.setExpertApplyId(expertApplyEntity.getExpertApplyId());
                    orgRelation.setSortIndex(0);
                    // 适用评分组织
                    extracted(context, orgFatherChildMap, dto, errSb, orgRelation);
                }
            }

            if (errSb.length() > 0) {
                if (errRow == null) {
                    errRow = new ImportResultModel.ErrorRow(index, "");
                }
                errRow.setReason(errRow.getReason() + errSb);
                errRowMap.put(index, errRow);
            }
        }
    }

    /**
     * 适用评分组织
     * @param context 参数
     * @param orgFatherChildMap 参数
     * @param dto 参数
     * @param errSb 参数
     * @param orgRelation 参数
     */
    private static void extracted(ExtSouExpertInitImportContext context, Map<Long, Set<Long>> orgFatherChildMap, ExtSouExpertInitOrgImportExcelDTO dto, StringBuilder errSb, ExtSouExpertOrgRelation orgRelation) {
        if (dto.getOrgName() == null) {
            errSb.append("请填写适用组织;");
        } else {
            Organization org = context.orgMap.get(dto.getOrgName());
            if (org == null) {
                errSb.append("适用组织不存在;");
            } else {
                orgRelation.setOrgId(org.getOrganizationId());
                orgRelation.setOrgCode(org.getOrganizationCode());
                orgRelation.setOrgName(org.getOrganizationName());
                orgRelation.setFullPathId(org.getOrganizationId() + "");
                orgRelation.setFullPathName(org.getOrganizationName());
                orgRelation.setSortIndex(0);

                Set<Long> fatherOrgIds = orgFatherChildMap.get(org.getOrganizationId());
                if (CollectionUtils.isNotEmpty(fatherOrgIds)) {
                    Organization fatherOrg = null;
                    String fatherOrgTypeCode = null; {
                        if (OU.equals(org.getOrganizationTypeCode())) {
                            fatherOrgTypeCode = "BU";
                        } else if (BU.equals(org.getOrganizationTypeCode())) {
                            fatherOrgTypeCode = "GROUP";
                        }
                        if (fatherOrgTypeCode != null) {
                            for (Long fatherOrgId : fatherOrgIds) {
                                fatherOrg = context.orgMap2.get(fatherOrgId);
                                boolean isOk = fatherOrg != null && fatherOrgTypeCode.equals(fatherOrg.getOrganizationTypeCode());
                                if (isOk) {
                                    break;
                                } else {
                                    fatherOrg = null;
                                }
                            }
                        }
                    }
                    if (fatherOrg != null) {
                        orgRelation.setFullPathName(fatherOrg.getOrganizationName() + "/" + orgRelation.getFullPathName());

                        if (!GROUP.equals(fatherOrgTypeCode)) {
                            fatherOrgIds = orgFatherChildMap.get(fatherOrg.getOrganizationId());
                            fatherOrgTypeCode = null; {
                                if (OU.equals(fatherOrg.getOrganizationTypeCode())) {
                                    fatherOrgTypeCode = "BU";
                                } else if (BU.equals(fatherOrg.getOrganizationTypeCode())) {
                                    fatherOrgTypeCode = "GROUP";
                                }
                                if (fatherOrgTypeCode != null) {
                                    for (Long fatherOrgId : fatherOrgIds) {
                                        fatherOrg = context.orgMap2.get(fatherOrgId);
                                        boolean isOk = fatherOrg != null && fatherOrgTypeCode.equals(fatherOrg.getOrganizationTypeCode());
                                        if (isOk) {
                                            break;
                                        } else {
                                            fatherOrg = null;
                                        }
                                    }
                                }
                            }
                            if (fatherOrg != null) {
                                orgRelation.setFullPathName(fatherOrg.getOrganizationName() + "/" + orgRelation.getFullPathName());
                            }
                        }
                    }
                }
                log.info("123");
            }
        }
    }

    @ApiOperation("适用品类: sheet数据解读")
    private List<ExtSouExpertInitCategoryImportExcelDTO> readDataForCategoryRelation(List<SiesData> dataList, Map<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap) {
        int index = -1;
        List<ExtSouExpertInitCategoryImportExcelDTO> dtoList = new ArrayList<>(dataList.size());
        for (SiesData data : dataList) {
            index++;
            StringBuilder errSb = new StringBuilder(100);
            ExtSouExpertInitCategoryImportExcelDTO dto = new ExtSouExpertInitCategoryImportExcelDTO();
            dtoList.add(dto);

            // 1: 姓名
            String applyByNickname = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitCategoryImportExcelDTO::getApplyByNickname)));
            dto.setApplyByNickname(applyByNickname);
            // 2: 工号
            String applyByCode = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitCategoryImportExcelDTO::getApplyByCode)));
            dto.setApplyByCode(applyByCode);
            // 3: 适用品类编码
            String categoryCode = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitCategoryImportExcelDTO::getCategoryCode)));
            dto.setCategoryCode(categoryCode);
            // 4: 适用品类名称
            String categoryName = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitCategoryImportExcelDTO::getCategoryName)));
            dto.setCategoryName(categoryName);

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

    @ApiOperation("适用品类: sheet数据校验转换")
    private void validateAndConvertDataForCategoryRelation(List<ExtSouExpertInitCategoryImportExcelDTO> dtoList, Map<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap) {
        ExtSouExpertInitImportContext context = CONTEXT_HOLDER.get(getKeyForContextHolder());

        Map<String/* applyByCode(工号) */, ExtSouExpertApply> expertApplyEntityMap = context.expertApplyEntityList.stream()
                .collect(Collectors.toMap(ExtSouExpertApply::getApplyByCode, Function.identity(), (a, b) -> a));
        Map<String/* categoryCode */, PurchaseCategory> categoryMap = Collections.emptyMap(); {
            Set<String> categoryCodes = dtoList.stream().map(ExtSouExpertInitCategoryImportExcelDTO::getCategoryCode).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!categoryCodes.isEmpty()) {
                categoryMap = baseClient.getCategoryByCodes(categoryCodes);
                if (categoryMap == null) { categoryMap = Collections.emptyMap(); }
            }
        }
        context.categoryMap = categoryMap;

        int index = -1;
        for (ExtSouExpertInitCategoryImportExcelDTO dto : dtoList) {
            index++;
            StringBuilder errSb = new StringBuilder(100);
            ImportResultModel.ErrorRow errRow = errRowMap.get(index);

            if (dto.getApplyByCode() == null) {
                errSb.append("请填写工号;");
            } else {
                ExtSouExpertApply expertApplyEntity = expertApplyEntityMap.get(dto.getApplyByCode());
                if (expertApplyEntity == null) {
                    errSb.append(MessageFormat.format("工号[{0}]在第一个sheet页(专家库初始化数据)中不存在;", dto.getApplyByCode()));
                } else {
                    ExtSouExpertCategoryRelation categoryRelation = new ExtSouExpertCategoryRelation();
                    context.expertCategoryEntityList.add(categoryRelation);

                    categoryRelation.setExpertCategoryId(IdGenrator.generate());
                    categoryRelation.setExpertApplyId(expertApplyEntity.getExpertApplyId());
                    categoryRelation.setSortIndex(0);
                    // 适用品类
                    if (dto.getCategoryCode() == null) {
                        errSb.append("请填写品类编码;");
                    } else {
                        PurchaseCategory category = context.categoryMap.get(dto.getCategoryCode());
                        if (category == null) {
                            errSb.append("品类不存在;");
                        } else {
                            if (!Enable.Y.equals(category.getLastLevelFlag())) {
                                errSb.append("品类不是末级;");
                            } else {
                                categoryRelation.setCategoryId(category.getCategoryId());
                                categoryRelation.setCategoryCode(category.getCategoryCode());
                                categoryRelation.setCategoryName(category.getCategoryName());
                                categoryRelation.setSortIndex(0);
                            }
                        }
                    }
                }
            }

            if (errSb.length() > 0) {
                if (errRow == null) {
                    errRow = new ImportResultModel.ErrorRow(index, "");
                }
                errRow.setReason(errRow.getReason() + errSb);
                errRowMap.put(index, errRow);
            }
        }
    }

    @ApiOperation("适用工作履历: sheet数据解读")
    private List<ExtSouExpertInitWorkImportExcelDTO> readDataForWork(List<SiesData> dataList, Map<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap) {
        int index = -1;
        List<ExtSouExpertInitWorkImportExcelDTO> dtoList = new ArrayList<>(dataList.size());
        for (SiesData data : dataList) {
            index++;
            StringBuilder errSb = new StringBuilder(100);
            ExtSouExpertInitWorkImportExcelDTO dto = new ExtSouExpertInitWorkImportExcelDTO();
            dtoList.add(dto);

            // 1: 姓名
            String applyByNickname = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitWorkImportExcelDTO::getApplyByNickname)));
            dto.setApplyByNickname(applyByNickname);
            // 2: 工号
            String applyByCode = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitWorkImportExcelDTO::getApplyByCode)));
            dto.setApplyByCode(applyByCode);
            // 3: 工作单位名称
            String workUnit = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitWorkImportExcelDTO::getWorkUnit)));
            dto.setWorkUnit(workUnit);
            // 4: 工作时间从
            String entryDate = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitWorkImportExcelDTO::getEntryDate)));
            if (entryDate != null) {
                try {
                    dto.setEntryDate(LocalDate.parse(entryDate, LOCAL_DATE_PATTERN));
                } catch (DateTimeParseException e) {
                    try {
                        dto.setEntryDate(LocalDate.parse(entryDate, LOCAL_DATE_PATTERN2));
                    } catch (DateTimeParseException ex) {
                        try {
                            dto.setEntryDate(LocalDate.parse(entryDate, LOCAL_DATE_PATTERN3));
                        } catch (DateTimeParseException exx) {
                            try {
                                dto.setEntryDate(LocalDate.parse(entryDate, LOCAL_DATE_PATTERN4));
                            } catch (DateTimeParseException exxx) {
                                errSb.append(MessageFormat.format("工作时间从[{0}]格式错误(必须是年/月/日);", entryDate));
                            }
                        }
                    }
                }
            }
            // 5: 工作时间至
            String quitDate = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitWorkImportExcelDTO::getQuitDate)));
            if (quitDate != null) {
                try {
                    dto.setQuitDate(LocalDate.parse(quitDate, LOCAL_DATE_PATTERN));
                } catch (DateTimeParseException e) {
                    try {
                        dto.setQuitDate(LocalDate.parse(quitDate, LOCAL_DATE_PATTERN2));
                    } catch (DateTimeParseException ex) {
                        try {
                            dto.setQuitDate(LocalDate.parse(quitDate, LOCAL_DATE_PATTERN3));
                        } catch (DateTimeParseException exx) {
                            try {
                                dto.setQuitDate(LocalDate.parse(quitDate, LOCAL_DATE_PATTERN4));
                            } catch (DateTimeParseException exxx) {
                                errSb.append(MessageFormat.format("工作时间至[{0}]格式错误(必须是年/月/日);", quitDate));
                            }
                        }
                    }
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

    @ApiOperation("适用工作履历: sheet数据校验转换")
    private void validateAndConvertDataForWork(List<ExtSouExpertInitWorkImportExcelDTO> dtoList, Map<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap) {
        ExtSouExpertInitImportContext context = CONTEXT_HOLDER.get(getKeyForContextHolder());

        Map<String/* applyByCode(工号) */, ExtSouExpertApply> expertApplyEntityMap = context.expertApplyEntityList.stream()
                .collect(Collectors.toMap(ExtSouExpertApply::getApplyByCode, Function.identity(), (a, b) -> a));

        int index = -1;
        for (ExtSouExpertInitWorkImportExcelDTO dto : dtoList) {
            index++;
            StringBuilder errSb = new StringBuilder(100);
            ImportResultModel.ErrorRow errRow = errRowMap.get(index);

            if (dto.getApplyByCode() == null) {
                errSb.append("请填写工号;");
            } else {
                ExtSouExpertApply expertApplyEntity = expertApplyEntityMap.get(dto.getApplyByCode());
                if (expertApplyEntity == null) {
                    errSb.append(MessageFormat.format("工号[{0}]在第一个sheet页(专家库初始化数据)中不存在;", dto.getApplyByCode()));
                } else {
                    ExtSouExpertWork work = new ExtSouExpertWork();
                    context.expertWorkEntityList.add(work);

                    work.setExpertWorkId(IdGenrator.generate());
                    work.setExpertApplyId(expertApplyEntity.getExpertApplyId());
                    work.setWorkUnit(dto.getWorkUnit());
                    if (work.getWorkUnit() == null) {
                        errSb.append("请填写工作单位名称");
                    } else if (work.getWorkUnit().length() > 255) {
                        errSb.append("工作单位名称的输入长度不能超过255;");
                    }
                    work.setEntryDate(dto.getEntryDate());
                    if (work.getEntryDate() == null) {
                        boolean hasErr = errRow != null && errRow.getReason().contains("工作时间从");
                        if (!hasErr) {
                            errSb.append("请填写工作时间从;");
                        }
                    }
                    work.setQuitDate(dto.getQuitDate());
                    if (work.getEntryDate() != null && work.getQuitDate() != null) {
                        if (work.getEntryDate().isAfter(work.getQuitDate())) {
                            errSb.append("工作时间从不能晚于工作时间到;");
                        }
                    }
                    work.setSortIndex(0);
                }
            }

            if (errSb.length() > 0) {
                if (errRow == null) {
                    errRow = new ImportResultModel.ErrorRow(index, "");
                }
                errRow.setReason(errRow.getReason() + errSb);
                errRowMap.put(index, errRow);
            }
        }
    }

    @ApiOperation("适用亲属工作单位: sheet数据解读")
    private List<ExtSouExpertInitWorkRelationImportExcelDTO> readDataForWorkRelation(List<SiesData> dataList, Map<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap) {
        int index = -1;
        List<ExtSouExpertInitWorkRelationImportExcelDTO> dtoList = new ArrayList<>(dataList.size());
        for (SiesData data : dataList) {
            index++;
            StringBuilder errSb = new StringBuilder(100);
            ExtSouExpertInitWorkRelationImportExcelDTO dto = new ExtSouExpertInitWorkRelationImportExcelDTO();
            dtoList.add(dto);

            // 1: 姓名
            String applyByNickname = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitWorkRelationImportExcelDTO::getApplyByNickname)));
            dto.setApplyByNickname(applyByNickname);
            // 2: 工号
            String applyByCode = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitWorkRelationImportExcelDTO::getApplyByCode)));
            dto.setApplyByCode(applyByCode);
            // 3: 与本人亲属关系
            String relativeType = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitWorkRelationImportExcelDTO::getRelativeType)));
            dto.setRelativeType(relativeType);
            // 4: 工作单位名称
            String workUnit = StringUtils.trimToNull(data.getString(SouObjectXUtil.getFieldByLambda(ExtSouExpertInitWorkRelationImportExcelDTO::getWorkUnit)));
            dto.setWorkUnit(workUnit);

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

    @ApiOperation("适用亲属工作单位: sheet数据校验转换")
    private void validateAndConvertDataForWorkRelation(List<ExtSouExpertInitWorkRelationImportExcelDTO> dtoList, Map<Integer/* rowIndex */, ImportResultModel.ErrorRow> errRowMap) {
        ExtSouExpertInitImportContext context = CONTEXT_HOLDER.get(getKeyForContextHolder());

        Map<String/* applyByCode(工号) */, ExtSouExpertApply> expertApplyEntityMap = context.expertApplyEntityList.stream()
                .collect(Collectors.toMap(ExtSouExpertApply::getApplyByCode, Function.identity(), (a, b) -> a));

        int index = -1;
        for (ExtSouExpertInitWorkRelationImportExcelDTO dto : dtoList) {
            index++;
            StringBuilder errSb = new StringBuilder(100);
            ImportResultModel.ErrorRow errRow = errRowMap.get(index);

            if (dto.getApplyByCode() == null) {
                errSb.append("请填写工号;");
            } else {
                ExtSouExpertApply expertApplyEntity = expertApplyEntityMap.get(dto.getApplyByCode());
                if (expertApplyEntity == null) {
                    errSb.append(MessageFormat.format("工号[{0}]在第一个sheet页(专家库初始化数据)中不存在;", dto.getApplyByCode()));
                } else {
                    ExtSouExpertWorkRelation workRelation = new ExtSouExpertWorkRelation();
                    context.expertWorkRelationEntityList.add(workRelation);

                    workRelation.setExpertWorkRelateId(IdGenrator.generate());
                    workRelation.setExpertApplyId(expertApplyEntity.getExpertApplyId());
                    workRelation.setRelativeType(dto.getRelativeType());
                    if (workRelation.getRelativeType() == null) {
                        errSb.append("请填写与本人亲属关系;");
                    } else if (workRelation.getRelativeType().length() > 30) {
                        errSb.append("与本人亲属关系的输入长度不能超过30;");
                    }
                    workRelation.setWorkUnit(dto.getWorkUnit());
                    if (workRelation.getWorkUnit() == null) {
                        errSb.append("请填写工作单位名称");
                    } else if (workRelation.getWorkUnit().length() > 255) {
                        errSb.append("工作单位名称的输入长度不能超过255;");
                    }
                    workRelation.setSortIndex(0);
                }
            }

            if (errSb.length() > 0) {
                if (errRow == null) {
                    errRow = new ImportResultModel.ErrorRow(index, "");
                }
                errRow.setReason(errRow.getReason() + errSb);
                errRowMap.put(index, errRow);
            }
        }
    }

    static class ExtSouExpertInitImportContext {
        Map<String/* dictCode */, Set<String/* itemCode */>> dictMap;
        List<String> availableExpertApplyNoList;
        Map<String/* orgName */, Organization> orgMap;
        Map<Long/* orgId */, Organization> orgMap2;
        Map<String/* ceeaEmpNo */, User> userMap;
        Map<String/* ceeaEmpNo */, ExtSouExpertApply> existExpertApplyMap;
        Map<String/* categoryCode */, PurchaseCategory> categoryMap;

        List<ExtSouExpert> expertEntityList = new ArrayList<>(100);
        List<ExtSouExpertApply> expertApplyEntityList = new ArrayList<>(100);
        List<ExtSouExpertEducation> expertEducationEntityList = new ArrayList<>(100);
        List<ExtSouExpertWork> expertWorkEntityList = new ArrayList<>(100);
        List<ExtSouExpertWorkRelation> expertWorkRelationEntityList = new ArrayList<>(100);
        List<ExtSouExpertOrgRelation> expertOrgEntityList = new ArrayList<>(100);
        List<ExtSouExpertCategoryRelation> expertCategoryEntityList = new ArrayList<>(100);

    }

}
