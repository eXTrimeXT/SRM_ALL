package com.midea.cloud.srm.pr.division.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.ImportStatus;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.handler.TitleColorSheetWriteHandler;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.EasyExcelUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationRelation;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supcooperate.ext.division.dto.PersonInChargeUserDto;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.pr.division.dto.DivisionCategoryModelDTO;
import com.midea.cloud.srm.pr.division.mapper.PjDivisionCategoryMapper;
import com.midea.cloud.srm.pr.division.service.IPjDivisionCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <pre>
 *  品类分工规则表 服务实现类
 * </pre>
 *
 * @author chensl26@meiCloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2020-07-22 08:41:41
 *  修改内容:
 * </pre>
 */
@Service
@Slf4j
public class PjDivisionCategoryServiceImpl extends BaseServiceImpl<PjDivisionCategoryMapper, DivisionCategory> implements IPjDivisionCategoryService {
    @Autowired
    private FileCenterClient fileCenterClient;

    @Autowired
    private BaseExtClient baseExtClient;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private RbacClient rbacClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateDivisionCategory(List<DivisionCategory> divisionCategories) {

        if (!CollectionUtils.isEmpty(divisionCategories)) {
            /**
             * 增加校验:业务实体+物料小类+负责人（工号）+职责+是否主要负责人，作为数据唯一性标识。
             */
            divisionCategories.forEach(divisionCategory -> {
                if (ObjectUtils.isEmpty(divisionCategory.getDivisionCategoryId())) {
                    List<DivisionCategory> list = this.list(Wrappers.lambdaQuery(DivisionCategory.class).
                            eq(DivisionCategory::getOrgId, divisionCategory.getOrgId())
//                            .eq(DivisionCategory::getOrganizationId, divisionCategory.getOrganizationId())
                            .eq(DivisionCategory::getCategoryId, divisionCategory.getCategoryId())
                            .eq(DivisionCategory::getPersonInChargeUserId, divisionCategory.getPersonInChargeUserId())
                            .eq(DivisionCategory::getDuty, divisionCategory.getDuty())
                            .eq(DivisionCategory::getIfMainPerson, divisionCategory.getIfMainPerson()));
                    Assert.isTrue(CollectionUtils.isEmpty(list), "业务实体+物料小类+负责人（工号）+职责+是否主要负责人,存在重复");
                } else {
                    List<DivisionCategory> list = this.list(Wrappers.lambdaQuery(DivisionCategory.class).
                            ne(DivisionCategory::getDivisionCategoryId, divisionCategory.getDivisionCategoryId())
                            .eq(DivisionCategory::getOrgId, divisionCategory.getOrgId())
//                            .eq(DivisionCategory::getOrganizationId, divisionCategory.getOrganizationId())
                            .eq(DivisionCategory::getCategoryId, divisionCategory.getCategoryId())
                            .eq(DivisionCategory::getPersonInChargeUserId, divisionCategory.getPersonInChargeUserId())
                            .eq(DivisionCategory::getDuty, divisionCategory.getDuty())
                            .eq(DivisionCategory::getIfMainPerson,divisionCategory.getIfMainPerson())
                            .ne(DivisionCategory::getDivisionCategoryId, divisionCategory.getDivisionCategoryId()));
                    Assert.isTrue(CollectionUtils.isEmpty(list), "业务实体+物料小类+负责人（工号）+职责+是否主要负责人,存在重复");
                }
            });

            for (DivisionCategory divisionCategory : divisionCategories) {
                if (divisionCategory == null) {
                    continue;
                }
                if (divisionCategory.getDivisionCategoryId() == null) {
                    checkBeforeSave(divisionCategory);
                    divisionCategory.setDivisionCategoryId(IdGenrator.generate());
                    if (divisionCategory.getStartDate() == null) {
                        divisionCategory.setStartDate(LocalDate.now());
                    }
                    this.save(divisionCategory);
                } else {
                    if (divisionCategory.getStartDate() == null) {
                        divisionCategory.setStartDate(LocalDate.now());
                    }
                    if (divisionCategory.getEndDate() == null) {
                        this.update(Wrappers.lambdaUpdate(DivisionCategory.class)
                                .set(DivisionCategory::getEndDate, null)
                                .eq(DivisionCategory::getDivisionCategoryId, divisionCategory.getDivisionCategoryId()));
                    }
                    this.updateById(divisionCategory);
                }
            }
        }
    }

    @Override
    public void importModelDownload(HttpServletResponse response) throws IOException {
        String fileName = "品类分工规则导入模版";
        List<DivisionCategoryModelDTO> divisionCategoryModelDtos = new ArrayList<>();
        ServletOutputStream outputStream = EasyExcelUtil.getServletOutputStream(response, fileName);
        // 指定标颜色的行
        List<Integer> rows = Arrays.asList(0);
        // 指定标颜色的列
        List<Integer> columns = Arrays.asList(0, 2, 4, 5, 7, 8);
        TitleColorSheetWriteHandler titleColorSheetWriteHandler = new TitleColorSheetWriteHandler(rows, columns, IndexedColors.RED.index);
        EasyExcelUtil.writeExcelWithModel(outputStream, divisionCategoryModelDtos, DivisionCategoryModelDTO.class, fileName, titleColorSheetWriteHandler);
    }

    @Override
    public Map<String, Object> importExcelNew(MultipartFile file, Fileupload fileupload) {
        // 检查参数
        EasyExcelUtil.checkParam(file, fileupload);
        // 读取数据
        List<DivisionCategoryModelDTO> modelDTOList = EasyExcelUtil.readExcelWithModel(file, DivisionCategoryModelDTO.class);
        // 检查导入数据是否正确
        AtomicBoolean errorFlag = new AtomicBoolean(true);
        // 导入数据校验
        List<DivisionCategory> divisionCategoryList = checkImportParam(modelDTOList, errorFlag);
        if (errorFlag.get()) {
            // 保存
            try {
                this.saveBatch(divisionCategoryList);
            } catch (Exception e) {
                log.error("批量保存品类分工规则失败:{}", e.getMessage());
            }
        } else {
            // 有错误,上传错误文件
            Fileupload errorFileupload = EasyExcelUtil.uploadErrorFile(fileCenterClient, fileupload, modelDTOList, DivisionCategoryModelDTO.class, file);
            return ImportStatus.importError(errorFileupload.getFileuploadId(), errorFileupload.getFileSourceName());
        }
        return ImportStatus.importSuccess();
    }

    /**
     * 批量更新品类分工负责人
     * @param personInChargeUserDto 批量更新品类分工负责人参数
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void batchUpdatePersonInChargeUser(PersonInChargeUserDto personInChargeUserDto) {
        Assert.notNull(personInChargeUserDto, "参数不能为空");
        Assert.notEmpty(personInChargeUserDto.getDivisionCategoryIds(), "品类分工id不能为空");
        Assert.notNull(personInChargeUserDto.getPersonInChargeUserId(), "负责人id不能为空");
        Assert.hasText(personInChargeUserDto.getPersonInChargeNickname(), "负责人名称不能为空");
        Assert.hasText(personInChargeUserDto.getPersonInChargeUsername(), "负责人账号不能为空");

        this.update(Wrappers.lambdaUpdate(DivisionCategory.class)
                .set(DivisionCategory::getPersonInChargeUserId, personInChargeUserDto.getPersonInChargeUserId())
                .set(DivisionCategory::getPersonInChargeNickname, personInChargeUserDto.getPersonInChargeNickname())
                .set(DivisionCategory::getPersonInChargeUsername, personInChargeUserDto.getPersonInChargeUsername())
                .in(DivisionCategory::getDivisionCategoryId, personInChargeUserDto.getDivisionCategoryIds()));
    }

    private List<DivisionCategory> checkImportParam(List<DivisionCategoryModelDTO> modelDTOList, AtomicBoolean errorFlag) {
        List<DivisionCategory> resultList = new ArrayList<>();
        LocalDate parseStartDate = null;
        LocalDate parseEndDate = null;
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(modelDTOList)) {
            for (DivisionCategoryModelDTO modelDTO : modelDTOList) {
                StringBuffer errorMsg = new StringBuffer();
                if (StringUtils.isEmpty(modelDTO.getOrgCode())) {
                    errorMsg.append("业务实体编码不能为空;");
                    errorFlag.set(false);
                }
                if (StringUtils.isEmpty(modelDTO.getCategoryCode())) {
                    errorMsg.append("物料小类编码不能为空;");
                    errorFlag.set(false);
                }
                if (StringUtils.isEmpty(modelDTO.getErpNum())) {
                    errorMsg.append("账号不能为空;");
                    errorFlag.set(false);
                }
                if (StringUtils.isEmpty(modelDTO.getDuty())) {
                    errorMsg.append("职责不能为空;");
                    errorFlag.set(false);
                }
                if (StringUtils.isEmpty(modelDTO.getIfMainPerson())) {
                    errorMsg.append("是否主要负责人不能为空;");
                    errorFlag.set(false);
                }
                String startDate = modelDTO.getStartDate();
                String endDate = modelDTO.getEndDate();
                try {
                    Date begin = DateUtil.parseDate(startDate);
                    parseStartDate = DateUtil.dateToLocalDate(begin);
                    if (StringUtils.isNotBlank(endDate)) {
                        Date end = DateUtil.parseDate(endDate);
                        parseEndDate = DateUtil.dateToLocalDate(end);
                    }
                } catch (Exception pe) {
                    errorMsg.append("生效/失效日期格式有误;");
                    errorFlag.set(false);
                }
                if (errorMsg.length() != 0) {
                    modelDTO.setErrorMsg(errorMsg.toString());
                }
            }
            //如果上面校验没报错,则获取组织,品类,人员信息
            if (errorFlag.get()) {
                //获取信息参数
                List<String> categoryCodes = new ArrayList<>();
//                List<String> invCodes = new ArrayList<>();
                List<String> orgCodes = new ArrayList<>();
                List<String> userNos = new ArrayList<>();
                //设置信息
                modelDTOList.forEach(item -> {
                    categoryCodes.add(item.getCategoryCode());
                    orgCodes.add(item.getOrgCode());
                    userNos.add(item.getErpNum());
                });
                List<DictItemDTO> duty = baseClient.listAllByDictCode("DUTY");
                Map<String, DictItemDTO> dutyMap = duty.stream().collect(Collectors.toMap(DictItemDTO::getDictItemName, Function.identity()));
                // 品类信息
                Map<String, PurchaseCategory> categoryMap = baseClient.getCategoryByCodes(categoryCodes);
                // 业务实体信息
                Map<String, Organization> orgMap = baseClient.getOrganizationsByCodes(orgCodes);
                // 用户信息
                Map<String, User> userMap = rbacClient.getUserByNos(userNos);

                //校验组织和物料是否正确
                for (DivisionCategoryModelDTO modelDTO : modelDTOList) {
                    StringBuffer errorMsg = new StringBuffer();
                    if (!dutyMap.containsKey(modelDTO.getDuty())) {
                        errorMsg.append("职责信息有误,请配置;");
                        errorFlag.set(false);
                    }
                    if (!categoryMap.containsKey(modelDTO.getCategoryCode())) {
                        errorMsg.append("根据物料小类编码找不到物料小类信息;");
                        errorFlag.set(false);
                    }
                    if (!orgMap.containsKey(modelDTO.getOrgCode())) {
                        errorMsg.append("根据业务实体编码找不到对应的业务实体信息;");
                        errorFlag.set(false);
                    }
                    if (!userMap.containsKey(modelDTO.getErpNum())) {
                        errorMsg.append("无法找到该账号的用户;");
                        errorFlag.set(false);
                    }
                    if (!Objects.equals(modelDTO.getIfMainPerson(), YesOrNo.YES.getValue()) && !Objects.equals(modelDTO.getIfMainPerson(), YesOrNo.NO.getValue())) {
                        errorMsg.append("是否主要负责人(Y/N),请输入Y/N;");
                        errorFlag.set(false);
                    }
                    if (errorMsg.length() != 0) {
                        modelDTO.setErrorMsg(errorMsg.toString());
                    }
                }
                // 校验
                extracted(modelDTOList, errorFlag, resultList, parseStartDate, parseEndDate, dutyMap, categoryMap, orgMap, userMap);

            }
        }
        return resultList;
    }

    /**
     * 校验
     * @param modelDTOList 参数
     * @param errorFlag 参数
     * @param resultList 参数
     * @param parseStartDate 参数
     * @param parseEndDate 参数
     * @param dutyMap 参数
     * @param categoryMap 参数
     * @param orgMap 参数
     * @param userMap 参数
     */
    private void extracted(List<DivisionCategoryModelDTO> modelDTOList, AtomicBoolean errorFlag, List<DivisionCategory> resultList, LocalDate parseStartDate, LocalDate parseEndDate, Map<String, DictItemDTO> dutyMap, Map<String, PurchaseCategory> categoryMap, Map<String, Organization> orgMap, Map<String, User> userMap) {
        if (errorFlag.get()) {
            Set<Long> orgIds = orgMap.values().stream().map(Organization::getOrganizationId).collect(Collectors.toSet());

            List<OrganizationRelation> organizationRelations = baseExtClient.listOrganizationRelation(new ArrayList<>(orgIds));

            Map<Long,Long> orgIdInvIdSet = organizationRelations.stream().collect(Collectors.toMap(OrganizationRelation::getOrganizationId,OrganizationRelation::getParentOrganizationId));
            List<Organization> invOrgs = baseClient.getOrganizationsByIds(organizationRelations.stream().map(OrganizationRelation::getParentOrganizationId).collect(Collectors.toSet()));
            Map<Long,Organization> invOrgMap = invOrgs.stream().collect(Collectors.toMap(Organization::getOrganizationId,t->t));

                    Set<Long> cateogoryIds = categoryMap.values().stream().map(PurchaseCategory::getCategoryId).collect(Collectors.toSet());
                    LambdaQueryWrapper<DivisionCategory> query = Wrappers.lambdaQuery(DivisionCategory.class)
                            .in(!CollectionUtils.isEmpty(orgIds), DivisionCategory::getOrgId, orgIds)
                            .in(!CollectionUtils.isEmpty(cateogoryIds), DivisionCategory::getCategoryId, cateogoryIds);
                    List<DivisionCategory> list = list(query);

                    Map<String, DivisionCategory> existMap = new HashMap<>(50);
                    if (!CollectionUtils.isEmpty(list)) {
                        existMap = list.stream().collect(Collectors.toMap(item -> item.getOrgId()
                                        + "-" + item.getPersonInChargeNickname() + "-" + item.getDuty() + "-" + item.getCategoryId()
                                , item -> item, (k1, k2) -> k2));
                    }

            for (DivisionCategoryModelDTO modelDTO : modelDTOList) {
                StringBuffer errorMsg = new StringBuffer();
                DivisionCategory divisionCategory = BeanCopyUtil.copyProperties(modelDTO, DivisionCategory::new);
                Long orgId = orgMap.get(modelDTO.getOrgCode()).getOrganizationId();
                Organization organization = invOrgMap.get(orgIdInvIdSet.get(orgId));
                divisionCategory
                        .setOrganizationCode(organization.getOrganizationCode())
                        .setOrganizationName(organization.getOrganizationName())
                        .setOrganizationId(organization.getOrganizationId())
                        .setOrgCode(orgMap.get(modelDTO.getOrgCode()).getOrganizationCode())
                        .setOrgName(orgMap.get(modelDTO.getOrgCode()).getOrganizationName())
                        .setOrgId(orgId)
                        .setCategoryCode(categoryMap.get(modelDTO.getCategoryCode()).getCategoryCode())
                        .setCategoryId(categoryMap.get(modelDTO.getCategoryCode()).getCategoryId())
                        .setCategoryName(categoryMap.get(modelDTO.getCategoryCode()).getCategoryName())
                        .setPersonInChargeUserId(userMap.get(modelDTO.getErpNum()).getUserId())
                        .setPersonInChargeNickname(userMap.get(modelDTO.getErpNum()).getNickname())
                        .setPersonInChargeUsername(userMap.get(modelDTO.getErpNum()).getUsername())
                        .setStartDate(parseStartDate).setEndDate(parseEndDate)
                        .setDivisionCategoryId(IdGenrator.generate())
                        .setDuty(dutyMap.get(modelDTO.getDuty()).getDictItemCode());
                resultList.add(divisionCategory);
                if (existMap.containsKey(divisionCategory.getOrgId()
                        + "-" + divisionCategory.getPersonInChargeNickname() + "-" + divisionCategory.getDuty()
                        + "-" + divisionCategory.getCategoryId())) {
                    errorMsg.append("已存在相同的品类分工规则;");
                    errorFlag.set(false);
                }
                if (errorMsg.length() != 0) {
                    modelDTO.setErrorMsg(errorMsg.toString());
                }
            }
        }
    }

    private void checkBeforeSave(DivisionCategory divisionCategory) {
        if (YesOrNo.YES.getValue().equals(divisionCategory.getIfMainPerson())) {
            DivisionCategory divisionCategoryQuery = new DivisionCategory()
                    .setOrgId(divisionCategory.getOrgId())
                    .setOrganizationId(divisionCategory.getOrganizationId())
                    .setCategoryId(divisionCategory.getCategoryId())
                    .setDuty(divisionCategory.getDuty())
                    .setIfMainPerson(divisionCategory.getIfMainPerson());
            QueryWrapper<DivisionCategory> queryWrapper = new QueryWrapper<>(divisionCategoryQuery);
            List<DivisionCategory> divisionCategoryList = this.list(queryWrapper);
            Assert.isTrue(CollectionUtils.isEmpty(divisionCategoryList), LocaleHandler.getLocaleMsg("已存在相同的品类分工规则,请检查!"));
        }
        if (YesOrNo.NO.getValue().equals(divisionCategory.getIfMainPerson())) {
            DivisionCategory divisionCategoryQuery = new DivisionCategory()
                    .setOrgId(divisionCategory.getOrgId())
                    .setOrganizationId(divisionCategory.getOrganizationId())
                    .setCategoryId(divisionCategory.getCategoryId())
                    .setDuty(divisionCategory.getDuty())
                    .setPersonInChargeUserId(divisionCategory.getPersonInChargeUserId())
                    .setIfMainPerson(divisionCategory.getIfMainPerson());
            QueryWrapper<DivisionCategory> queryWrapper = new QueryWrapper<>(divisionCategoryQuery);
            List<DivisionCategory> divisionCategoryList = this.list(queryWrapper);
            Assert.isTrue(CollectionUtils.isEmpty(divisionCategoryList), LocaleHandler.getLocaleMsg("已存在相同的品类分工规则,请检查!"));
        }
    }

}
