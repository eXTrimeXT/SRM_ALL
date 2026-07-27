package com.midea.cloud.srm.sou.expert.plugin.event.editapply;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertApplyDTO;
import com.midea.cloud.srm.model.sou.expert.entity.*;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyFromTypeEnum;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyStatusEnum;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.ql.util.MqlCreateUpdateUtils;
import com.midea.cloud.srm.sou.expert.spi.event.editapply.ExtSouExpertEditApplyContext;
import com.midea.cloud.srm.sou.expert.spi.event.editapply.IExtSouExpertEditApplyValidatePlugin;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 寻源 - 专家申请编辑校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertEditApplyValidatePlugin implements IExtSouExpertEditApplyValidatePlugin {

    @Autowired
    private BaseClient baseClient;

    @Override
    @ApiOperation("校验及转化申请")
    public ExtSouExpertEditApplyContext validateAndConvertApply(ExtSouExpertEditApplyContext context) {
        // 1: 数据校验
        ExtSouExpertApplyDTO param = context.getParam(); {
            // 1.1: ID(略)
            // 1.2: 专家申请编号(略)
            // 1.3: 数据来源
            param.setApplyFromType(StringUtils.trimToNull(param.getApplyFromType()));
            AssertUtils.notNull(param.getApplyFromType(), "请选择数据来源");
            if (ExtSouExpertApplyFromTypeEnum.UPGRADE.name().equals(param.getApplyFromType())) {
                // 升级申请
                AssertUtils.notEmpty(context.getHasPassedExpertApplyList(), "专家未提交过申请，不能提交升级申请");
            } else if (ExtSouExpertApplyFromTypeEnum.INDEPENDENT.name().equals(param.getApplyFromType())) {
                // 自主申请
                AssertUtils.isTrue(context.getHasPassedExpertApplyList().isEmpty(), "只有未提交过申请的，才能提交自主申请");
            } else if (ExtSouExpertApplyFromTypeEnum.CHANGE.name().equals(param.getApplyFromType())) {
                // 变更申请
                AssertUtils.notEmpty(context.getHasPassedExpertApplyList(), "专家未提交过申请，不能提交变更申请");
            }
            // 1.4: 申请人
            AssertUtils.notNull(param.getApplyById(), "缺少applyById参数");
            AssertUtils.notNull(param.getApplyBy(), "缺少applyBy参数");
            AssertUtils.notNull(param.getApplyByNickname(), "缺少applyByNickname参数");
            // 1.5: 申请状态
            if (context.getExistExpertApply() != null) {
                param.setApplyStatus(context.getExistExpertApply().getApplyStatus());
            } else {
                param.setApplyStatus(ExtSouExpertApplyStatusEnum.DRAFT.name());
            }
            // 1.6: 是否已提交
            param.setHasSubmit(param.getTempSave() ? Enable.N : Enable.Y);
            // 1.7: 最高学历(略 - 后续 educationList 处理时填补)
            param.setHighestDegree(param.getHighestDegree());
            // 1.8: 性别
            param.setSex(StringUtils.trimToNull(param.getSex()));
            param.setOrgOuCode(StringUtils.trimToNull(param.getOrgOuCode()));
            // 1.10: 所属部门
            param.setDepartmentId(StringUtils.trimToNull(param.getDepartmentId()));
            param.setDepartmentName(StringUtils.trimToNull(param.getDepartmentName()));
            // 1.11: 职务
            param.setJob(StringUtils.trimToNull(param.getJob()));
            if (param.getJob() != null) {
                AssertUtils.isTrue(param.getJob().length() <= 30, "职务的输入长度不能超过30");
            }
            // 1.12: 职务职级(序列等级)
            param.setJobRank(StringUtils.trimToNull(param.getJobRank()));
            if (param.getJobRank() != null) {
                AssertUtils.isTrue(param.getJobRank().length() <= 30, "序列等级的输入长度不能超过30");
            }
            // 1.12: 专家等级
            if (CollectionUtils.isEmpty(context.getHasPassedExpertApplyList())) {
                param.setExpertLevel(null);
            } else {
                param.setExpertLevel(context.getHasPassedExpertApplyList().get(0).getApplyLevel());
            }
            // 1.13: 在职状态
            param.setJobStatus(StringUtils.trimToNull(param.getJobStatus()));
            param.setPhone(StringUtils.trimToNull(param.getPhone()));
            if (param.getPhone() != null) {
                AssertUtils.isTrue(param.getPhone().length() <= 50, "手机号码的输入长度不能超过50");
            }
            // 1.15: 入职时间
            if (CollectionUtils.isEmpty(context.getHasPassedExpertApplyList())) {
            } else {
                param.setHireDate(context.getHasPassedExpertApplyList().get(0).getHireDate());
            }
            // 1.16: 申请等级
            param.setApplyLevel(StringUtils.trimToNull(param.getApplyLevel()));
            AssertUtils.isTrue(param.getTempSave() || param.getApplyLevel() != null, "请选择申请等级");
            // 1.17: 用于专家升级时记录来源申请单号
            if (CollectionUtils.isEmpty(context.getHasPassedExpertApplyList())) {
                param.setFromApplyId(null);
            } else {
                param.setFromApplyId(context.getHasPassedExpertApplyList().get(0).getExpertApplyId());
            }
            // 1.18: 绿色通道原因
            if (ExtSouExpertApplyFromTypeEnum.GREEN_CHANNEL.name().equals(param.getApplyFromType())) {
                // 绿色通道
                param.setGreenReason(StringUtils.trimToNull(param.getGreenReason()));
            } else {
                param.setGreenReason(null);
            }
            // 1.19: 升级/变更申请原因
            if (ExtSouExpertApplyFromTypeEnum.UPGRADE.name().equals(param.getApplyFromType()) || ExtSouExpertApplyFromTypeEnum.CHANGE.name().equals(param.getApplyFromType())) {
                // 升级/变更申请
                param.setUpgradeReason(StringUtils.trimToNull(param.getUpgradeReason()));
            } else {
                param.setUpgradeReason(null);
            }
            // 1.20: 绿色通道个人编辑状态
            if (ExtSouExpertApplyFromTypeEnum.GREEN_CHANNEL.name().equals(param.getApplyFromType())) {
                // 绿色通道
                if (param.getIfGreenPersonUpdate() == null) {
                    param.setIfGreenPersonUpdate(Enable.N);
                }
                if (context.getExistExpertApply() != null && ExtSouExpertApplyStatusEnum.APPROVED.name().equals(context.getExistExpertApply().getApplyStatus())) {
                    param.setApplyStatus(ExtSouExpertApplyStatusEnum.APPROVED.name());
                }
            } else {
                param.setIfGreenPersonUpdate(Enable.N);
            }
        }
        // 2: 数据转化
        ExtSouExpertApply entity = getExtSouExpertApply(context, param);

        context.setExpertApplyEntity(entity);
        return context;
    }

    /**
     * 数据转化
     * @param context 参数
     * @param param 参数
     * @return 返回
     */
    @NotNull
    private ExtSouExpertApply getExtSouExpertApply(ExtSouExpertEditApplyContext context, ExtSouExpertApplyDTO param) {
        ExtSouExpertApply entity;
        {
            if (context.getExistExpertApply() != null) {
                entity = context.getExistExpertApply();
            } else {
                entity = new ExtSouExpertApply();
            }
            //noinspection unchecked
            SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(param, entity,
                    ExtSouExpertApply::getExpertApplyId,
                    ExtSouExpertApply::getExpertApplyNo,
                    ExtSouExpertApply::getCreatedId,
                    ExtSouExpertApply::getCreatedBy,
                    ExtSouExpertApply::getCreatedByIp,
                    ExtSouExpertApply::getCreationDate,
                    ExtSouExpertApply::getCreatedUserName,
                    ExtSouExpertApply::getCreatedFullName);
            MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtSouExpertApply.class.getSimpleName());
            if (context.getExistExpertApply() == null) {
                entity.setExpertApplyId(IdGenrator.generate());
                param.setGenerateCode(StringUtils.trimToNull(param.getGenerateCode()));
                entity.setExpertApplyNo(baseClient.seqGen(param.getGenerateCode() != null ? param.getGenerateCode() : "SEQ_SOU_EXPERT_APPLY_NO"));
            }
            SouObjectXUtil.mergeProperties(entity, param);
        }
        return entity;
    }

    @Override
    @ApiOperation("校验及转化附件")
    public ExtSouExpertEditApplyContext validateAndConvertAttaches(ExtSouExpertEditApplyContext context) {
        context.setExpertApplyAttachFileEntityList(context.getParam().getAttachList());
        if (CollectionUtils.isNotEmpty(context.getExpertApplyAttachFileEntityList())) {
            context.getExpertApplyAttachFileEntityList().removeIf(e -> e.getFileuploadId() == null || e.getFileName() == null);
            for (SceneFile sceneFile : context.getExpertApplyAttachFileEntityList()) {
                sceneFile.setSceneFileId(null);
                sceneFile.setBusinessId(context.getExpertApplyEntity().getExpertApplyId());
            }
        }
        return context;
    }

    @Override
    @ApiOperation("校验及转化适用品类")
    public ExtSouExpertEditApplyContext validateAndConvertCategoryRelations(ExtSouExpertEditApplyContext context) {
        // 1: 数据校验
        List<ExtSouExpertCategoryRelation> categoryList = context.getParam().getCategoryList(); {
            if (CollectionUtils.isEmpty(categoryList)) {
                AssertUtils.isTrue(context.getParam().getTempSave(), "请选择适用品类");
                return context;
            }
            int index = 0;
            for (ExtSouExpertCategoryRelation category : categoryList) {
                index++;
                // 1.1: ID(略)
                // 1.2: 专家申请ID
                category.setExpertApplyId(context.getExpertApplyEntity().getExpertApplyId());
                // 1.3: 品类
                category.setCategoryCode(StringUtils.trimToNull(category.getCategoryCode()));
                AssertUtils.notNull(category.getCategoryCode(), "适用品类列表第{0}行请选择品类", index);
                PurchaseCategory purchaseCategory = context.getCategoryMap().get(category.getCategoryCode());
                AssertUtils.notNull(purchaseCategory, "适用品类列表第{0}行所选品类[{1}]不存在", index, category.getCategoryCode());
                AssertUtils.isTrue(Enable.Y.equals(purchaseCategory.getLastLevelFlag()), "适用品类列表第{0}行所选品类[{1}]不是末级品类", index, category.getCategoryCode());
                category.setCategoryId(purchaseCategory.getCategoryId());
                category.setCategoryName(purchaseCategory.getCategoryName());
                // 1.4: 排序
                category.setSortIndex(index);
            }
        }
        // 2: 数据转化
        List<ExtSouExpertCategoryRelation> entityList = new ArrayList<>(categoryList.size()); {
            for (ExtSouExpertCategoryRelation category : categoryList) {
                ExtSouExpertCategoryRelation entity;
                ExtSouExpertCategoryRelation existCategory = context.getExistExpertCategoryRelationMap().get(category.getExpertCategoryId());
                if (existCategory != null) {
                    entity = existCategory;
                } else {
                    entity = new ExtSouExpertCategoryRelation();
                }
                entityList.add(entity);
                //noinspection unchecked
                SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(category, entity,
                        ExtSouExpertCategoryRelation::getExpertCategoryId,
                        ExtSouExpertCategoryRelation::getCreatedId,
                        ExtSouExpertCategoryRelation::getCreatedBy,
                        ExtSouExpertCategoryRelation::getCreatedByIp,
                        ExtSouExpertCategoryRelation::getCreationDate,
                        ExtSouExpertCategoryRelation::getCreatedUserName,
                        ExtSouExpertCategoryRelation::getCreatedFullName);
                MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtSouExpertCategoryRelation.class.getSimpleName());
                if (existCategory == null) {
                    entity.setExpertCategoryId(IdGenrator.generate());
                }
                SouObjectXUtil.mergeProperties(entity, category);
            }
        }

        context.setExpertCategoryRelationEntityList(entityList);
        return context;
    }

    @Override
    @ApiOperation("校验及转化学历")
    public ExtSouExpertEditApplyContext validateAndConvertEducations(ExtSouExpertEditApplyContext context) {
        // 1: 数据校验
        List<ExtSouExpertEducation> educationList = context.getParam().getEducationList(); {
            if (CollectionUtils.isEmpty(educationList)) {
                return context;
            }
            int index = 0;
            for (ExtSouExpertEducation education : educationList) {
                index++;
                // 1.1: ID(略)
                // 1.2: 专家申请ID
                education.setExpertApplyId(context.getExpertApplyEntity().getExpertApplyId());
                // 1.3: 学历
                education.setEducation(StringUtils.trimToNull(education.getEducation()));
                // 1.4: 就读院校
                education.setStudyCollege(StringUtils.trimToNull(education.getStudyCollege()));
                if (education.getStudyCollege() != null) {
                    AssertUtils.isTrue(education.getStudyCollege().length() <= 255, "学历列表第{0}行就读院校的输入长度不能超过255", index);
                }
                if (education.getStudyDateFrom() != null && education.getStudyDateTo() != null) {
                }
                education.setMajor(StringUtils.trimToNull(education.getMajor()));
                education.setSortIndex(index);
            }
        }
        // 2: 数据转化
        List<ExtSouExpertEducation> entityList = new ArrayList<>(educationList.size()); {
            for (ExtSouExpertEducation education : educationList) {
                ExtSouExpertEducation entity;
                ExtSouExpertEducation existEducation = context.getExistExpertEducationMap().get(education.getExpertEducationId());
                if (existEducation != null) {
                    entity = existEducation;
                } else {
                    entity = new ExtSouExpertEducation();
                }
                entityList.add(entity);
                //noinspection unchecked
                SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(education, entity,
                        ExtSouExpertEducation::getExpertEducationId,
                        ExtSouExpertEducation::getCreatedId,
                        ExtSouExpertEducation::getCreatedBy,
                        ExtSouExpertEducation::getCreatedByIp,
                        ExtSouExpertEducation::getCreationDate,
                        ExtSouExpertEducation::getCreatedUserName,
                        ExtSouExpertEducation::getCreatedFullName);
                MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtSouExpertEducation.class.getSimpleName());
                if (existEducation == null) {
                    entity.setExpertEducationId(IdGenrator.generate());
                }
                SouObjectXUtil.mergeProperties(entity, education);
            }
        }

        context.setExpertEducationEntityList(entityList);
        return context;
    }

    @Override
    @ApiOperation("校验及转化适用组织")
    public ExtSouExpertEditApplyContext validateAndConvertOrgRelations(ExtSouExpertEditApplyContext context) {
        // 1: 数据校验
        List<ExtSouExpertOrgRelation> orgList = context.getParam().getOrgList(); {
            if (CollectionUtils.isEmpty(orgList)) {
                AssertUtils.isTrue(context.getParam().getTempSave(), "请选择适用组织");
                return context;
            }
            int index = 0;
            for (ExtSouExpertOrgRelation org : orgList) {
                index++;
                // 1.1: ID(略)
                // 1.2: 专家申请ID
                org.setExpertApplyId(context.getExpertApplyEntity().getExpertApplyId());
                // 1.3: 适用组织
                org.setOrgCode(StringUtils.trimToNull(org.getOrgCode()));
                AssertUtils.notNull(org.getOrgCode(), "适用组织列表第{0}行请选择组织", index);
                Organization organization = context.getOrgMap().get(org.getOrgCode());
                AssertUtils.notNull(organization, "适用组织列表第{0}行组织[{1}]不存在", index, org.getOrgCode());
                org.setOrgId(organization.getOrganizationId());
                org.setOrgName(organization.getOrganizationName());
                // 1.4: 组织全路径ID
                org.setFullPathId(StringUtils.trimToNull(org.getFullPathId()));
                if (org.getFullPathId() != null) {
                    AssertUtils.isTrue(org.getFullPathId().length() <= 150, "fullPathId长度不能超过150");
                }
                // 1.5: 组织全路径名称
                org.setFullPathName(StringUtils.trimToNull(org.getFullPathName()));
                if (org.getFullPathName() != null) {
                    AssertUtils.isTrue(org.getFullPathName().length() <= 300, "fullPathName长度不能超过150");
                }
                // 1.6: 排序
                org.setSortIndex(index);
            }
        }
        // 2: 数据转化
        List<ExtSouExpertOrgRelation> entityList = new ArrayList<>(orgList.size()); {
            for (ExtSouExpertOrgRelation org : orgList) {
                ExtSouExpertOrgRelation entity;
                ExtSouExpertOrgRelation existOrg = context.getExistExpertOrgRelationMap().get(org.getExpertOrgId());
                if (existOrg != null) {
                    entity = existOrg;
                } else {
                    entity = new ExtSouExpertOrgRelation();
                }
                entityList.add(entity);
                //noinspection unchecked
                SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(org, entity,
                        ExtSouExpertOrgRelation::getExpertOrgId,
                        ExtSouExpertOrgRelation::getCreatedId,
                        ExtSouExpertOrgRelation::getCreatedBy,
                        ExtSouExpertOrgRelation::getCreatedByIp,
                        ExtSouExpertOrgRelation::getCreationDate,
                        ExtSouExpertOrgRelation::getCreatedUserName,
                        ExtSouExpertOrgRelation::getCreatedFullName);
                MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtSouExpertOrgRelation.class.getSimpleName());
                if (existOrg == null) {
                    entity.setExpertOrgId(IdGenrator.generate());
                }
                SouObjectXUtil.mergeProperties(entity, org);
            }
        }

        context.setExpertOrgRelationEntityList(entityList);
        return context;
    }

    @Override
    @ApiOperation("校验及转化工作经历")
    public ExtSouExpertEditApplyContext validateAndConvertWorks(ExtSouExpertEditApplyContext context) {
        // 1: 数据校验
        List<ExtSouExpertWork> workList = context.getParam().getWorkList(); {
            if (CollectionUtils.isEmpty(workList)) {
                return context;
            }
            int index = 0;
            for (ExtSouExpertWork work : workList) {
                index++;
                // 1.1: ID(略)
                // 1.2: 专家申请ID
                work.setExpertApplyId(context.getExpertApplyEntity().getExpertApplyId());
                // 1.3: 工作单位
                work.setWorkUnit(StringUtils.trimToNull(work.getWorkUnit()));
                AssertUtils.isTrue(context.getParam().getTempSave() || work.getWorkUnit() != null, "工作经历列表第{0}行请填写工作单位", index);
                if (work.getWorkUnit() != null) {
                    AssertUtils.isTrue(work.getWorkUnit().length() <= 255, "工作经历列表第{0}行工作单位的输入长度不能超过255", index);
                }
                // 1.4: 职务
                work.setJob(StringUtils.trimToNull(work.getJob()));
                if (work.getJob() != null) {
                    AssertUtils.isTrue(work.getJob().length() <= 150, "工作经历列表第{0}行职务的输入长度不能超过150", index);
                }
                // 1.5: 职务职级(序列等级)
                work.setJobRank(StringUtils.trimToNull(work.getJobRank()));
                if (work.getJobRank() != null) {
                    AssertUtils.isTrue(work.getJobRank().length() <= 150, "工作经历列表第{0}行序列等级的输入长度不能超过150", index);
                }
                // 1.5: 入职时间
                AssertUtils.isTrue(context.getParam().getTempSave() || work.getEntryDate() != null, "工作经历列表第{0}行请选择入职时间", index);
                // 1.6: 离职时间
                if (work.getEntryDate() != null && work.getQuitDate() != null) {
//                    AssertUtils.isTrue(work.getEntryDate().isBefore(work.getQuitDate()), "工作经历列表第{0}行入职时间必须早于离职时间", index);
                }
                // 1.7: 排序
                work.setSortIndex(index);
            }
        }
        // 2: 数据转化
        List<ExtSouExpertWork> entityList = new ArrayList<>(workList.size()); {
            for (ExtSouExpertWork work : workList) {
                ExtSouExpertWork entity;
                ExtSouExpertWork existWork = context.getExistExpertWorkMap().get(work.getExpertWorkId());
                if (existWork != null) {
                    entity = existWork;
                } else {
                    entity = new ExtSouExpertWork();
                }
                entityList.add(entity);
                //noinspection unchecked
                SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(work, entity,
                        ExtSouExpertWork::getExpertWorkId,
                        ExtSouExpertWork::getCreatedId,
                        ExtSouExpertWork::getCreatedBy,
                        ExtSouExpertWork::getCreatedByIp,
                        ExtSouExpertWork::getCreationDate,
                        ExtSouExpertWork::getCreatedUserName,
                        ExtSouExpertWork::getCreatedFullName);
                MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtSouExpertWork.class.getSimpleName());
                if (existWork == null) {
                    entity.setExpertWorkId(IdGenrator.generate());
                }
                SouObjectXUtil.mergeProperties(entity, work);
            }
        }

        context.setExpertWorkEntityList(entityList);
        return context;
    }

    @Override
    @ApiOperation("校验及转化亲属工作经历")
    public ExtSouExpertEditApplyContext validateAndConvertWorkRelations(ExtSouExpertEditApplyContext context) {
        // 1: 数据校验
        List<ExtSouExpertWorkRelation> workRelationList = context.getParam().getWorkRelationList(); {
            if (CollectionUtils.isEmpty(workRelationList)) {
                return context;
            }
            int index = 0;
            for (ExtSouExpertWorkRelation workRelation : workRelationList) {
                index++;
                // 1.1: ID(略)
                // 1.2: 专家申请ID
                workRelation.setExpertApplyId(context.getExpertApplyEntity().getExpertApplyId());
                // 1.3: 亲属名称(略)
                // 1.4: 亲属关系
                workRelation.setRelativeType(StringUtils.trimToNull(workRelation.getRelativeType()));
                if (workRelation.getRelativeType() != null) {
                    AssertUtils.isTrue(workRelation.getRelativeType().length() <= 30, "工作经历列表第{0}行亲属关系的输入长度不能超过30", index);
                }
                // 1.5: 工作单位
                workRelation.setWorkUnit(StringUtils.trimToNull(workRelation.getWorkUnit()));
                AssertUtils.isTrue(context.getParam().getTempSave() || workRelation.getWorkUnit() != null, "工作经历列表第{0}行请填写工作单位", index);
                if (workRelation.getWorkUnit() != null) {
                    AssertUtils.isTrue(workRelation.getWorkUnit().length() <= 255, "工作经历列表第{0}行工作单位的输入长度不能超过255", index);
                }
                // 1.6: 职务
                workRelation.setJob(StringUtils.trimToNull(workRelation.getJob()));
                if (workRelation.getJob() != null) {
                    AssertUtils.isTrue(workRelation.getJob().length() <= 150, "工作经历列表第{0}行职务的输入长度不能超过150", index);
                }
                if (workRelation.getEntryDate() != null && workRelation.getQuitDate() != null) {
//                    AssertUtils.isTrue(workRelation.getEntryDate().isBefore(workRelation.getQuitDate()), "工作经历列表第{0}行入职时间必须早于离职时间", index);
                }
                // 1.9: 排序
                workRelation.setSortIndex(index);
            }
        }
        // 2: 数据转化
        List<ExtSouExpertWorkRelation> entityList = new ArrayList<>(workRelationList.size()); {
            for (ExtSouExpertWorkRelation workRelation : workRelationList) {
                ExtSouExpertWorkRelation entity;
                ExtSouExpertWorkRelation existWorkRelation = context.getExistExpertWorkRelationMap().get(workRelation.getExpertWorkRelateId());
                if (existWorkRelation != null) {
                    entity = existWorkRelation;
                } else {
                    entity = new ExtSouExpertWorkRelation();
                }
                entityList.add(entity);
                //noinspection unchecked
                SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(workRelation, entity,
                        ExtSouExpertWorkRelation::getExpertWorkRelateId,
                        ExtSouExpertWorkRelation::getCreatedId,
                        ExtSouExpertWorkRelation::getCreatedBy,
                        ExtSouExpertWorkRelation::getCreatedByIp,
                        ExtSouExpertWorkRelation::getCreationDate,
                        ExtSouExpertWorkRelation::getCreatedUserName,
                        ExtSouExpertWorkRelation::getCreatedFullName);
                MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtSouExpertWorkRelation.class.getSimpleName());
                if (existWorkRelation == null) {
                    entity.setExpertWorkRelateId(IdGenrator.generate());
                }
                SouObjectXUtil.mergeProperties(entity, workRelation);
            }
        }

        context.setExpertWorkRelationEntityList(entityList);
        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
