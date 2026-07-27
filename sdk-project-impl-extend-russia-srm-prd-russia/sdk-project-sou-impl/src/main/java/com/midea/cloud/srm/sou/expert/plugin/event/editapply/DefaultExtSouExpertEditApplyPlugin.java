package com.midea.cloud.srm.sou.expert.plugin.event.editapply;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.entity.*;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyFromTypeEnum;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyStatusEnum;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertLevelEnum;
import com.midea.cloud.srm.ql.util.MqlCreateUpdateUtils;
import com.midea.cloud.srm.sou.expert.spi.event.editapply.ExtSouExpertEditApplyContext;
import com.midea.cloud.srm.sou.expert.spi.event.editapply.IExtSouExpertEditApplyPlugin;
import com.midea.cloud.srm.sou.expert.spi.event.editapply.IExtSouExpertEditApplyValidatePlugin;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 寻源 - 专家申请编辑插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/12
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertEditApplyPlugin implements IExtSouExpertEditApplyPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private BaseClient baseClient;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertEditApplyContext judgeEditApplyAuth(ExtSouExpertEditApplyContext context) {
        if (context.getParam().getExpertApplyId() != null) {
            ExtSouExpertApply expertApply = qlService.readByKey(ExtSouExpertApply.class.getSimpleName(), context.getParam().getExpertApplyId(), ExtSouExpertApply.class);
            expertApply.setHighestDegree(context.getParam().getHighestDegree());
            AssertUtils.notNull(expertApply, "专家申请[{0}]不存在", context.getParam().getExpertApplyId());
            switch (expertApply.getApplyStatus()) {
                case "DRAFT":
                    // 拟定
                case "REJECTED":
                    // 已驳回
                case "WITHDRAW":
                    // 已撤回
                    break;
                case "SUBMITTED":
                    // 已提交
                case "APPROVING":
                    // 审批中
                    throw new IllegalArgumentException("申请已提交，禁止编辑");
                case "APPROVED":
                    // 已审批
                    // 绿色通道是例外的，绿色通道的允许专家本人进行编辑更新
                    if (ExtSouExpertApplyFromTypeEnum.GREEN_CHANNEL.name().equals(expertApply.getApplyFromType())) {
                        // 绿色通道
                        AssertUtils.isTrue(Enable.N.equals(expertApply.getIfGreenPersonUpdate()), "禁止编辑操作");
                        AssertUtils.isTrue(expertApply.getApplyById().equals(AppUserUtil.getLoginAppUser().getUserId()), "只能由专家本人填补信息");
                        break;
                    } else {
                        throw new IllegalArgumentException("申请已审批，禁止编辑");
                    }
                case "ABANDONED":
                    // 已废弃
                    throw new IllegalArgumentException("申请已废弃，禁止编辑");
                default:
                    throw new IllegalArgumentException("无法识别的单据状态，请自行处理");
            }
            context.setExistExpertApply(expertApply);
        } else {
            AssertUtils.notNull(context.getParam().getApplyById(), "缺少applyById参数");
            AssertUtils.notNull(context.getParam().getApplyLevel(), "缺少applyLevel参数");
            // 判断是否已有其他
            if (!ExtSouExpertApplyFromTypeEnum.CHANGE.name().equals(context.getParam().getApplyFromType())) {
                List<ExtSouExpertApply> userApplyList = qlService.queryByWrapper(QlWrappers.query(ExtSouExpertApply.class)
                                .eq(ExtSouExpertApply::getApplyById, context.getParam().getApplyById()), ExtSouExpertApply.class)
                        .stream().filter(e -> ExtSouExpertApplyStatusEnum.APPROVED.name().equals(e.getApplyStatus()) || ExtSouExpertApplyStatusEnum.APPROVING.name().equals(e.getApplyStatus()))
                        .collect(Collectors.toList());
                if (!userApplyList.isEmpty()) {
                    ExtSouExpertApply seniorApply = userApplyList.stream().filter(e -> ExtSouExpertLevelEnum.SENIOR.name().equals(e.getExpertLevel()))
                            .findAny().orElse(null);
                    if (seniorApply != null) {
                        if (ExtSouExpertLevelEnum.NORMAL.name().equals(context.getParam().getApplyLevel())) {
                            // 当前申请的是普通的
                            if (ExtSouExpertApplyStatusEnum.APPROVED.name().equals(seniorApply.getApplyStatus())) {
                                throw new IllegalArgumentException("用户已申请通过高级专家");
                            } else {
                                throw new IllegalArgumentException("用户的高级专家申请正在审批中");
                            }
                        } else {
                            // 当前申请的是高级的
                            if (ExtSouExpertApplyStatusEnum.APPROVED.name().equals(seniorApply.getApplyStatus())) {
                                throw new IllegalArgumentException("用户已申请通过高级专家");
                            } else {
                                throw new IllegalArgumentException("用户的高级专家申请正在审批中");
                            }
                        }
                    } else {
                        ExtSouExpertApply normalApply = userApplyList.stream().filter(e -> StringUtils.isEmpty(e.getExpertLevel()) || ExtSouExpertLevelEnum.NORMAL.name().equals(e.getExpertLevel()))
                                .findAny().orElse(null);
                        if (normalApply != null) {
                            if (ExtSouExpertLevelEnum.NORMAL.name().equals(context.getParam().getApplyLevel())) {
                                // 当前申请的是普通的
                                if (ExtSouExpertApplyStatusEnum.APPROVED.name().equals(normalApply.getApplyStatus())) {
                                    throw new IllegalArgumentException("用户已申请通过普通专家");
                                } else {
                                    throw new IllegalArgumentException("用户的普通专家申请正在审批中");
                                }
                            } else {
                                // 当前申请的是高级的
                                if (ExtSouExpertApplyStatusEnum.APPROVED.name().equals(normalApply.getApplyStatus())) {
//                                throw new IllegalArgumentException("用户已申请通过普通专家");
                                } else {
                                    throw new IllegalArgumentException("用户的普通专家申请正在审批中");
                                }
                            }
                        }
                    }
                }
            }
        }

        return context;
    }

    @Override
    @ApiOperation("数据处理")
    public ExtSouExpertEditApplyContext prepareEditApply(ExtSouExpertEditApplyContext context) {
        // 1: 查询现有的适用品类
        if (context.getExistExpertApply() != null) {
            context.setExistExpertCategoryRelationMap(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertCategoryRelation.class)
                    .eq(ExtSouExpertCategoryRelation::getExpertApplyId, context.getParam().getExpertApplyId()), ExtSouExpertCategoryRelation.class)
                    .stream().collect(Collectors.toMap(ExtSouExpertCategoryRelation::getExpertCategoryId, Function.identity())));
        }
        // 2: 查询现有的学历
        if (context.getExistExpertApply() != null) {
            context.setExistExpertEducationMap(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertEducation.class)
                    .eq(ExtSouExpertEducation::getExpertApplyId, context.getParam().getExpertApplyId()), ExtSouExpertEducation.class)
                    .stream().collect(Collectors.toMap(ExtSouExpertEducation::getExpertEducationId, Function.identity())));
        }
        // 3: 查询现有的适用组织
        if (context.getExistExpertApply() != null) {
            context.setExistExpertOrgRelationMap(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertOrgRelation.class)
                    .eq(ExtSouExpertOrgRelation::getExpertApplyId, context.getParam().getExpertApplyId()), ExtSouExpertOrgRelation.class)
                    .stream().collect(Collectors.toMap(ExtSouExpertOrgRelation::getExpertOrgId, Function.identity())));
        }
        // 4: 查询现有的工作经历
        if (context.getExistExpertApply() != null) {
            context.setExistExpertWorkMap(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertWork.class)
                    .eq(ExtSouExpertWork::getExpertApplyId, context.getParam().getExpertApplyId()), ExtSouExpertWork.class)
                    .stream().collect(Collectors.toMap(ExtSouExpertWork::getExpertWorkId, Function.identity())));
        }
        // 5: 查询现有的亲属工作经历
        if (context.getExistExpertApply() != null) {
            context.setExistExpertWorkRelationMap(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertWorkRelation.class)
                    .eq(ExtSouExpertWorkRelation::getExpertApplyId, context.getParam().getExpertApplyId()), ExtSouExpertWorkRelation.class)
                    .stream().collect(Collectors.toMap(ExtSouExpertWorkRelation::getExpertWorkRelateId, Function.identity())));
        }
        // 6: 查询专家已通过的申请信息
        if (context.getParam().getApplyBy() != null) {
            //noinspection unchecked
            context.setHasPassedExpertApplyList(qlService.queryByWrapper(QlWrappers.query(ExtSouExpertApply.class)
                    .eq(ExtSouExpertApply::getApplyBy, context.getParam().getApplyBy())
                    .eq(ExtSouExpertApply::getApplyStatus, ExtSouExpertApplyStatusEnum.APPROVED)
                    .orderByDesc(ExtSouExpertApply::getCreationDate), ExtSouExpertApply.class));
        }
        // 7: 查询品类
        if (CollectionUtils.isNotEmpty(context.getParam().getCategoryList())) {
            Set<String> categoryCodes = context.getParam().getCategoryList().stream()
                    .map(e -> StringUtils.trimToNull(e.getCategoryCode())).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!categoryCodes.isEmpty()) {
                context.setCategoryMap(baseClient.getCategoryByCodes(categoryCodes));
            }
        }
        // 8: 查询组织
        if (CollectionUtils.isNotEmpty(context.getParam().getOrgList())) {
            Set<String> orgCodes = context.getParam().getOrgList().stream()
                    .map(e -> StringUtils.trimToNull(e.getOrgCode())).filter(Objects::nonNull).collect(Collectors.toSet());
            context.getParam().setOrgOuCode(StringUtils.trimToNull(context.getParam().getOrgOuCode()));
            if (context.getParam().getOrgOuCode() != null) {
                orgCodes.add(context.getParam().getOrgOuCode());
            }
            if (!orgCodes.isEmpty()) {
                context.setOrgMap(baseClient.getOrganizationsByCodes(orgCodes));
            }
        }

        return context;
    }

    @Override
    @ApiOperation("前置处理")
    public ExtSouExpertEditApplyContext beforeEditApply(ExtSouExpertEditApplyContext context) {
        return SdkPluginProxy.proxy(IExtSouExpertEditApplyValidatePlugin.class, context).execute(context);
    }

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertEditApplyContext executeEditApply(ExtSouExpertEditApplyContext context) {
        long expertApplyId = context.getExpertApplyEntity().getExpertApplyId();
        // 1: 保存专家申请
        MqlCreateUpdateUtils.saveOrUpdate(ExtSouExpertApply.class, expertApplyId, Collections.singletonList(context.getExpertApplyEntity()), ExtSouExpertApply::getExpertApplyId);
        // 2: 保存专家申请适用品类
        MqlCreateUpdateUtils.saveOrUpdate(ExtSouExpertCategoryRelation.class, expertApplyId, context.getExpertCategoryRelationEntityList(), ExtSouExpertCategoryRelation::getExpertApplyId);
        // 3: 保存专家申请学历
        MqlCreateUpdateUtils.saveOrUpdate(ExtSouExpertEducation.class, expertApplyId, context.getExpertEducationEntityList(), ExtSouExpertEducation::getExpertApplyId);
        // 4: 保存专家申请适用组织
        MqlCreateUpdateUtils.saveOrUpdate(ExtSouExpertOrgRelation.class, expertApplyId, context.getExpertOrgRelationEntityList(), ExtSouExpertOrgRelation::getExpertApplyId);
        // 5: 保存专家申请工作经历
        MqlCreateUpdateUtils.saveOrUpdate(ExtSouExpertWork.class, expertApplyId, context.getExpertWorkEntityList(), ExtSouExpertWork::getExpertApplyId);
        // 6: 保存专家申请亲属工作经历
        MqlCreateUpdateUtils.saveOrUpdate(ExtSouExpertWorkRelation.class, expertApplyId, context.getExpertWorkRelationEntityList(), ExtSouExpertWorkRelation::getExpertApplyId);
        // 7: 保存专家申请附件
        baseClient.bindSceneFile(
                ExtSouExpertApply.ATTACH_FILE_SCENE_TYPE,
                context.getExpertApplyEntity().getExpertApplyId(),
                true,
                context.getExpertApplyAttachFileEntityList() != null ? context.getExpertApplyAttachFileEntityList() : Collections.emptyList());

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
