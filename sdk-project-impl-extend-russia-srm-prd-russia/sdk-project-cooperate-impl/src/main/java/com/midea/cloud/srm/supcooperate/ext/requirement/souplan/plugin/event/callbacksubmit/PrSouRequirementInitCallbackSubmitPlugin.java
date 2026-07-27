package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.event.callbacksubmit;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementFromEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.pr.division.service.IDivisionCategoryService;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.service.PrSouProjectPlanEventService;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.callbacksubmit.IRequirementInitCallbackSubmitPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.callbacksubmit.RequirementInitCallbackSubmitContext;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 采购申请 - 立项通过回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/06/02
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementInitCallbackSubmitPlugin implements IRequirementInitCallbackSubmitPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private IDivisionCategoryService divisionCategoryService;
    @Autowired
    private RbacClient rbacClient;
    @Autowired
    private PrSouProjectPlanEventService prSouProjectPlanEventService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public RequirementInitCallbackSubmitContext judgeCallbackSubmitAuth(RequirementInitCallbackSubmitContext context) {
        PrRequirementHead reqHead = (PrRequirementHead)this.qlService.readByKey(PrRequirementHead.class.getSimpleName(), context.getRequirementHeadId(), PrRequirementHead.class);
        AssertUtils.notNull(reqHead, "采购申请单[{0}]不存在", new Object[]{context.getRequirementHeadId()});

        return context;
    }

    @Override
    @ApiOperation("更新数据")
    public RequirementInitCallbackSubmitContext executeCallbackSubmit(RequirementInitCallbackSubmitContext context) {
        context = SdkPluginProxy.callSuper(IRequirementInitCallbackSubmitPlugin.class, context, this).executeCallbackSubmit(context);

        // 更新提交审批时间
        qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementHead.class)
                .set(ExtPrSouRequirementHead::getSubmitApprovalTime, new Date())
                .eq(ExtPrSouRequirementHead::getRequirementHeadId, context.getRequirementHeadId()));

        return context;
    }

    @Override
    @ApiOperation("后置处理")
    public RequirementInitCallbackSubmitContext afterCallbackSubmit(RequirementInitCallbackSubmitContext context) {
        PrRequirementHead prHead = qlService.readByKey(PrRequirementHead.class.getSimpleName(), context.getRequirementHeadId(), PrRequirementHead.class);
        ExtPrSouRequirementHead prSouHead = qlService.readByKey(ExtPrSouRequirementHead.class.getSimpleName(), context.getRequirementHeadId(), ExtPrSouRequirementHead.class);
        // 1: 绑定/解绑项目计划
        if (prSouHead.getProjectPlanId() != null) {
            prSouProjectPlanEventService.bindPlan(prSouHead.getProjectPlanId(), prHead.getRequirementHeadId());
        } else {
            prSouProjectPlanEventService.unbindPlan(null, prHead.getRequirementHeadId());
        }
        // 2: 根据品类分工分配人员
        List<DivisionCategory> divisionList = divisionCategoryService.lambdaQuery()
                .eq(DivisionCategory::getOrgId, prHead.getOrgId())
                .eq(DivisionCategory::getCategoryId, prHead.getCategoryId())
                .list();
        if (!divisionList.isEmpty()) {
            List<ExtPrSouRequirementGroup> groupList = new ArrayList<>();
            int num10 = 10;
            // 如果金额小于10万，或者是特殊招标，就找非主负责人
            boolean notMainPerson = prSouHead.getTotalAmountByTenKilo().compareTo(new BigDecimal(10)) < 0
                    || PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(prSouHead.getRequireFrom());

            // 金额大于10万，选择主招标负责人/主供应商负责人
            if (!notMainPerson) {
                divisionList = divisionList.stream().filter(e -> Enable.Y.name().equals(e.getIfMainPerson())).collect(Collectors.toList());
                if (!divisionList.isEmpty()) {
                    // 主招标负责人
                    DivisionCategory souDc = divisionList.stream().filter(e -> "Person in charge of bidding".equals(e.getDuty())).collect(Collectors.toList())
                            .stream().findFirst().orElse(null);
                    if (souDc != null) {
                        ExtPrSouRequirementGroup souGroup = new ExtPrSouRequirementGroup();
                        groupList.add(souGroup);
                        // ID
                        souGroup.setRequirementGroupId(IdGenrator.generate());
                        // 招标计划ID
                        souGroup.setRequirementHeadId(prSouHead.getRequirementHeadId());
                        // 用户
                        souGroup.setUserId(souDc.getPersonInChargeUserId());
                        souGroup.setUsername(souDc.getPersonInChargeUsername());
                        souGroup.setFullName(souDc.getPersonInChargeNickname());
                        // 职责
                        souGroup.setGroupType(PrSouRequirementGroupTypeEnum.SOU.name());
                        // 联系方式
                        // 邮箱
                        // 所属部门
                        // 排序
                        souGroup.setSortIndex(10);
                    }
                    // 主供应商负责人
                    souDc = divisionList.stream().filter(e -> "Supplier Leader".equals(e.getDuty())).collect(Collectors.toList())
                            .stream().findFirst().orElse(null);
                    if (souDc != null) {
                        ExtPrSouRequirementGroup souGroup = new ExtPrSouRequirementGroup();
                        groupList.add(souGroup);
                        // ID
                        souGroup.setRequirementGroupId(IdGenrator.generate());
                        // 招标计划ID
                        souGroup.setRequirementHeadId(prSouHead.getRequirementHeadId());
                        // 用户
                        souGroup.setUserId(souDc.getPersonInChargeUserId());
                        souGroup.setUsername(souDc.getPersonInChargeUsername());
                        souGroup.setFullName(souDc.getPersonInChargeNickname());
                        // 职责
                        souGroup.setGroupType(PrSouRequirementGroupTypeEnum.VENDOR.name());
                        // 联系方式
                        // 邮箱
                        // 所属部门
                        // 排序
                        souGroup.setSortIndex(10);
                    }
                }
            } else {
                // 选择非主招标负责人/供应商负责人
                divisionList = divisionList.stream().filter(e -> !Enable.Y.name().equals(e.getIfMainPerson())).collect(Collectors.toList());
                if (!divisionList.isEmpty()) {
                    // 非主招标负责人
                    DivisionCategory souDc = divisionList.stream().filter(e -> "Person in charge of bidding".equals(e.getDuty())).collect(Collectors.toList())
                            .stream().findFirst().orElse(null);
                    if (souDc != null) {
                        ExtPrSouRequirementGroup souGroup = new ExtPrSouRequirementGroup();
                        groupList.add(souGroup);
                        // ID
                        souGroup.setRequirementGroupId(IdGenrator.generate());
                        // 招标计划ID
                        souGroup.setRequirementHeadId(prSouHead.getRequirementHeadId());
                        // 用户
                        souGroup.setUserId(souDc.getPersonInChargeUserId());
                        souGroup.setUsername(souDc.getPersonInChargeUsername());
                        souGroup.setFullName(souDc.getPersonInChargeNickname());
                        // 职责
                        souGroup.setGroupType(PrSouRequirementGroupTypeEnum.SOU.name());
                        // 联系方式
                        // 邮箱
                        // 所属部门
                        // 排序
                        souGroup.setSortIndex(10);
                    }
                    // 非主供应商负责人
                    souDc = divisionList.stream().filter(e -> "Supplier Leader".equals(e.getDuty())).collect(Collectors.toList())
                            .stream().findFirst().orElse(null);
                    if (souDc != null) {
                        ExtPrSouRequirementGroup souGroup = new ExtPrSouRequirementGroup();
                        groupList.add(souGroup);
                        // ID
                        souGroup.setRequirementGroupId(IdGenrator.generate());
                        // 招标计划ID
                        souGroup.setRequirementHeadId(prSouHead.getRequirementHeadId());
                        // 用户
                        souGroup.setUserId(souDc.getPersonInChargeUserId());
                        souGroup.setUsername(souDc.getPersonInChargeUsername());
                        souGroup.setFullName(souDc.getPersonInChargeNickname());
                        // 职责
                        souGroup.setGroupType(PrSouRequirementGroupTypeEnum.VENDOR.name());
                        // 联系方式
                        // 邮箱
                        // 所属部门
                        // 排序
                        souGroup.setSortIndex(10);
                    }
                }
            }

            if (!groupList.isEmpty()) {
                Set<String> usernames = groupList.stream().map(ExtPrSouRequirementGroup::getUsername).filter(Objects::nonNull).collect(Collectors.toSet());
                if (!usernames.isEmpty()) {
                    Map<String/* username */, User> userMap = rbacClient.getUserMapByNames(usernames);
                    for (ExtPrSouRequirementGroup group : groupList) {
                        User user = userMap.get(group.getUsername());
                        if (user != null) {
                            group.setDepartmentId(user.getCeeaDeptId());
                            group.setDepartmentName(user.getDepartment());
                            group.setPhone(user.getPhone());
                            group.setEmail(user.getEmail());
                        }
                    }
                }
            }

            /** 特殊招标类型不需要校验品类 24-5-28：暂时去掉此处的校验逻辑，后续将在保存时增加校验
            if(!PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(prSouHead.getRequireFrom())) {
                boolean hasSouAssigned = groupList.stream().anyMatch(e -> PrSouRequirementGroupTypeEnum.SOU.name().equals(e.getGroupType()));
                boolean hasVendorAssigned = groupList.stream().anyMatch(e -> PrSouRequirementGroupTypeEnum.VENDOR.name().equals(e.getGroupType()));

                AssertUtils.isTrue(hasSouAssigned, "根据品类分工找不到招标负责人");
                AssertUtils.isTrue(hasVendorAssigned, "根据品类分工找不到供应商负责人");
            }
             */

            if(CollectionUtils.isNotEmpty(groupList)) {
                qlService.create(groupList);
            }

        }

        return context;
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
