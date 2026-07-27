package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.event.editrequire;

import com.alibaba.fastjson.JSON;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.entity.ExtPrSouProjectPlan;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.ql.util.MqlCreateUpdateUtils;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.editrequire.IRequirementInitEditPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.editrequire.RequirementInitEditContext;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招标计划 - 详情编辑插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Slf4j
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementInitEditPlugin implements IRequirementInitEditPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private RbacClient rbacClient;
    @Autowired
    private BaseClient baseClient;

    @Override
    @ApiOperation("校验操作条件/权限")
    public RequirementInitEditContext judgeEditRequirementAuth(RequirementInitEditContext context) {
        PrSouRequirementInitEditContext souContext = SouObjectXUtil.convertTargetObj(context, PrSouRequirementInitEditContext.class);
        // 1: 调用核心方法
        return SdkPluginProxy.callSuper(IRequirementInitEditPlugin.class, souContext, this).judgeEditRequirementAuth(souContext);
    }

    @Override
    @ApiOperation("数据准备")
    public RequirementInitEditContext prepareEditRequirement(RequirementInitEditContext context) {
        PrSouRequirementInitEditContext souContext = (PrSouRequirementInitEditContext) context;
        ExtPrSouRequirementHeadDTO souParam = SouObjectXUtil.convertTargetObj(souContext.getParam(), ExtPrSouRequirementHeadDTO.class);
        // 1: 查询现有的采购申请单
        if (souParam.getRequirementHeadId() != null) {
            souContext.setExistReqHead(qlService.readByKey(PrRequirementHead.class.getSimpleName(), souParam.getRequirementHeadId(), PrRequirementHead.class));
        }
        // 2: 查询现有的招标计划
        if (souParam.getRequirementHeadId() != null) {
            souContext.setExistPrSouHead(qlService.readByKey(ExtPrSouRequirementHead.class.getSimpleName(), souParam.getRequirementHeadId(), ExtPrSouRequirementHead.class));
        }
        // 3: 查询现有的招标计划工作小组
        if (souParam.getRequirementHeadId() != null) {
            souContext.setExistPrSouGroupMap(qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class)
                    .eq(ExtPrSouRequirementGroup::getRequirementHeadId, souParam.getRequirementHeadId()), ExtPrSouRequirementGroup.class)
                    .stream().collect(Collectors.toMap(ExtPrSouRequirementGroup::getRequirementGroupId, Function.identity())));
        }
        // 4: 查询现有的招标计划推荐供应商
        if (souParam.getRequirementHeadId() != null) {
            souContext.setExistPrSouVendorMap(qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementVendor.class)
                    .eq(ExtPrSouRequirementVendor::getRequirementHeadId, souParam.getRequirementHeadId()), ExtPrSouRequirementVendor.class)
                    .stream().collect(Collectors.toMap(ExtPrSouRequirementVendor::getRequirementVendorId, Function.identity())));
        }
        // 5: 查询现有的招标计划附件
        if (souParam.getRequirementHeadId() != null) {
            souContext.setExistPrSouAttachMap(qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementAttach.class)
                    .eq(ExtPrSouRequirementAttach::getRequirementHeadId, souParam.getRequirementHeadId()), ExtPrSouRequirementAttach.class)
                    .stream().collect(Collectors.toMap(ExtPrSouRequirementAttach::getRequirementAttachId, Function.identity())));
        }
        // 6: 查询项目计划
        if (souParam.getSouReqHead() != null && souParam.getSouReqHead().getProjectPlanId() != null) {
            ExtPrSouProjectPlan projectPlan = qlService.readByKey(ExtPrSouProjectPlan.class.getSimpleName(), souParam.getSouReqHead().getProjectPlanId(), ExtPrSouProjectPlan.class);
            souContext.setProjectPlan(projectPlan);
        }
        // 7: 查询可用成员信息
        if (CollectionUtils.isNotEmpty(souParam.getSouGroupList())) {
            Set<String> usernames = souParam.getSouGroupList().stream().map(ExtPrSouRequirementGroup::getUsername).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!usernames.isEmpty()) {
                souContext.setUserMap(rbacClient.listByUserNames(usernames)
                        .stream().collect(Collectors.toMap(User::getUsername, Function.identity())));
            }
        }
        // 8: 查询业务实体/库存组织
        Set<Long> orgIds = new HashSet<>(8); {
            if (souParam.getOrgId() != null) {
                orgIds.add(souParam.getOrgId());
            }
            if (souParam.getSouReqHead() != null && souParam.getSouReqHead().getOrgBuId() != null) {
                orgIds.add(souParam.getSouReqHead().getOrgBuId());
            }
        }
        Map<Long/* orgId */, Organization> orgMap; {
            if (orgIds.isEmpty()) {
                orgMap = Collections.emptyMap();
            } else {
                orgMap = baseClient.getOrganizationsByIds(new ArrayList<>(orgIds))
                        .stream().collect(Collectors.toMap(Organization::getOrganizationId, Function.identity()));
            }
        }
        souContext.setOrganizationMap(orgMap);
        // 9: 查询品类
        Map<String/* categoryCode */, PurchaseCategory> categoryMap = Collections.emptyMap(); {
            if (souParam.getCategoryCode() != null) {
                categoryMap = baseClient.getCategoryByCodes(Collections.singletonList(souParam.getCategoryCode()));
            }
        }
        souContext.setCategoryMap(categoryMap);

        return souContext;
    }

    @Override
    @ApiOperation("数据保存")
    public RequirementInitEditContext executeEditRequirement(RequirementInitEditContext context) {
        PrSouRequirementInitEditContext souContext = (PrSouRequirementInitEditContext) context;
        log.info("新增采购申请-排查:" + JSON.toJSONString(souContext.getReqHeadEntity()));
//        if (true) { throw new IllegalArgumentException("asd"); }

        // 1: 保存数据
        long requirementHeadId = souContext.getReqHeadEntity().getRequirementHeadId();
        MqlCreateUpdateUtils.saveOrUpdate(PrRequirementHead.class, requirementHeadId, Collections.singletonList(souContext.getReqHeadEntity()), PrRequirementHead::getRequirementHeadId);
        MqlCreateUpdateUtils.saveOrUpdate(ExtPrSouRequirementHead.class, requirementHeadId, Collections.singletonList(souContext.getSouReqHeadEntity()), ExtPrSouRequirementHead::getRequirementHeadId);
        MqlCreateUpdateUtils.saveOrUpdate(ExtPrSouRequirementGroup.class, requirementHeadId, souContext.getSouGroupEntityList(), ExtPrSouRequirementGroup::getRequirementHeadId);
        MqlCreateUpdateUtils.saveOrUpdate(ExtPrSouRequirementVendor.class, requirementHeadId, souContext.getSouVendorEntityList(), ExtPrSouRequirementVendor::getRequirementHeadId);
        MqlCreateUpdateUtils.saveOrUpdate(ExtPrSouRequirementAttach.class, requirementHeadId, souContext.getSouAttachEntityList(), ExtPrSouRequirementAttach::getRequirementHeadId);

        return souContext;
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
