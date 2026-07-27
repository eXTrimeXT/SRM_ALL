package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.batchassign;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.batchassign.IPrSouRequirementPoolBatchAssignPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.batchassign.PrSouRequirementPoolBatchAssignContext;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招标计划池 - 分配/转办插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/07
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementPoolBatchAssignPlugin implements IPrSouRequirementPoolBatchAssignPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private RbacClient rbacClient;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private QlOpenClient qlOpenClient;

    private static final String GROUP_TYPE_VENDOR = "VENDOR";

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementPoolBatchAssignContext judgeBatchAssignAuth(PrSouRequirementPoolBatchAssignContext context) {
        context.getParam().formatParams();
        List<ExtPrSouRequirementHead> souPrHeadList = qlService.readByKeys(ExtPrSouRequirementHead.class.getSimpleName(),
                new ArrayList<>(context.getParam().getRequirementHeadIds()), ExtPrSouRequirementHead.class);
        AssertUtils.notEmpty(souPrHeadList, "请选择需要分配的招标计划");
        Map<Long/* requirementHeadId */, RequirementHead> reqHeadMap = qlService.readByKeys("PurchaseRequirementHead",
                souPrHeadList.stream().map(ExtPrSouRequirementHead::getRequirementHeadId).collect(Collectors.toList()), RequirementHead.class)
                        .stream().collect(Collectors.toMap(RequirementHead::getRequirementHeadId, Function.identity()));
        souPrHeadList.forEach(prHead -> AssertUtils.isFalse(PrSouRequirementStatusEnum.CANCEL.name().equals(prHead.getSouReqStatus()),
                "招标计划[{0}]已取消", reqHeadMap.get(prHead.getRequirementHeadId()).getRequirementHeadNum()));
        context.setSouPrHeadList(souPrHeadList);

        Set<Long> userIds = context.getParam().getGroupUsers().values().stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (!userIds.isEmpty()) {
            Map<Long/* userId */, User> userMap = rbacClient.getByUserIds(userIds).stream().collect(Collectors.toMap(User::getUserId, Function.identity()));
            context.getParam().getGroupUsers().forEach((groupType, userId) ->
                    AssertUtils.isTrue(userMap.containsKey(userId), "{0}职责下的责任人不存在[{1}]", groupType, userId));
            context.setUserMap(userMap);
        }

        return context;
    }

    @Override
    public PrSouRequirementPoolBatchAssignContext afterBatchAssign(PrSouRequirementPoolBatchAssignContext context) {
        List<Long> souReqIdList = null;
        if(CollectionUtils.isNotEmpty(context.getSouPrHeadList())) {
            souReqIdList = context.getSouPrHeadList().stream().map(r -> r.getSouReqId()).distinct().collect(Collectors.toList());
        }

        if(CollectionUtils.isNotEmpty(souReqIdList)) {
            /** 更新寻源需求供应商负责人 */
            List<ExtPrSouRequirementGroup> entityList = (List<ExtPrSouRequirementGroup>) context.getExtensions().getOrDefault(ExtPrSouRequirementGroup.class.getSimpleName(), new ArrayList<>(16));
            List<Long> finalSouReqIdList = souReqIdList;
            entityList.stream().filter(group -> GROUP_TYPE_VENDOR.equals(group.getGroupType())).forEach(group -> {
                qlOpenClient.update(ContextPath.SOU, QlOpenWrappers.update(MqlType.SOU_REQ_HEAD_BUYER).set(SouReqHead::getResponsibilityUserId, group.getUserId())
                        .set(SouReqHead::getResponsibilityUserName, group.getFullName()).in(SouReqHead::getReqHeadId, finalSouReqIdList));
            });
        }

        return IPrSouRequirementPoolBatchAssignPlugin.super.afterBatchAssign(context);
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementPoolBatchAssignContext executeBatchAssign(PrSouRequirementPoolBatchAssignContext context) {
        // 1: 构造数据
        List<ExtPrSouRequirementGroup> entityList = new ArrayList<>(context.getParam().getGroupUsers().size() * context.getParam().getRequirementHeadIds().size());
        for (Long requirementHeadId : context.getParam().getRequirementHeadIds()) {
            context.getParam().getGroupUsers().forEach((groupType, userId) -> {
                ExtPrSouRequirementGroup entity = new ExtPrSouRequirementGroup();
                entityList.add(entity);

                // 1.1: ID
                entity.setRequirementGroupId(IdGenrator.generate());
                // 1.2: 招标计划ID
                entity.setRequirementHeadId(requirementHeadId);
                // 1.3: 用户
                User user = context.getUserMap().get(userId);
                entity.setUserId(user.getUserId());
                entity.setUsername(user.getUsername());
                entity.setFullName(user.getNickname());
                // 1.4: 工作成员职责
                entity.setGroupType(groupType);
                // 1.5: 联系方式(略)
                // 1.6: 邮箱(略)
                // 1.7: 所属部门
                entity.setDepartmentId(user.getCeeaDeptId());
                entity.setDepartmentName(user.getDepartment());
                // 1.8: 排序
                entity.setSortIndex(10);
            });
        }
        //招标负责人
        Long souId = context.getParam().getGroupUsers().get(PrSouRequirementGroupTypeEnum.SOU.name());
        User u = rbacClient.getUserByIdAnon(souId);
        if (u != null) {
            HrUserOrgnizationDto userOrganization = pjProjectExtClient.getHrUserOrgnizationByUsername(u.getUsername());
            if (userOrganization != null) {
                List<Record> rList = new ArrayList<>();
                context.getParam().getRequirementHeadIds().forEach(businessId -> {
                    Record r = new Record();
                    r.put(RequirementHead::getRequirementHeadId, businessId);
                    //公司
                    Organization ouOrganization = userOrganization.getOuOrganization();
                    //板块
                    Organization buOrganization = userOrganization.getBuOrganization();
                    //部门
                    Organization departmentOrganization = userOrganization.getDepartmentOrganization();
                    if(ObjectUtil.isNotNull(buOrganization)) {
                        r.put(PurchaseRequirementHeadDTO::getBuId, buOrganization.getOrganizationId());
                        r.put(PurchaseRequirementHeadDTO::getBuCode, buOrganization.getOrganizationCode());
                        r.put(PurchaseRequirementHeadDTO::getBuName, buOrganization.getOrganizationName());
                    }
                    if(ObjectUtil.isNotNull(ouOrganization)) {
                        r.put(PurchaseRequirementHeadDTO::getComId, ouOrganization.getOrganizationId());
                        r.put(PurchaseRequirementHeadDTO::getComCode, ouOrganization.getOrganizationCode());
                        r.put(PurchaseRequirementHeadDTO::getComName, ouOrganization.getOrganizationName());
                    }
                    if(ObjectUtil.isNotNull(departmentOrganization)) {
                        r.put(PurchaseRequirementHeadDTO::getDepId, departmentOrganization.getOrganizationId());
                        r.put(PurchaseRequirementHeadDTO::getDepCode, departmentOrganization.getOrganizationCode());
                        r.put(PurchaseRequirementHeadDTO::getDepName, departmentOrganization.getOrganizationName());
                    }
                    rList.add(r);
                });

                qlService.update("PurchaseRequirementHead", rList);
            }
        }
        // 2: 清除旧数据
        qlService.deleteByWrapper(QlWrappers.update(ExtPrSouRequirementGroup.class)
                .in(ExtPrSouRequirementGroup::getRequirementHeadId, context.getParam().getRequirementHeadIds())
                .in(ExtPrSouRequirementGroup::getGroupType, context.getParam().getGroupUsers().keySet()));
        // 3: 新增数据
        qlService.deleteByWrapper(QlWrappers.update(ExtPrSouRequirementGroup.class)
                .in(ExtPrSouRequirementGroup::getRequirementHeadId, context.getParam().getRequirementHeadIds())
                .in(ExtPrSouRequirementGroup::getGroupType, context.getParam().getGroupUsers().keySet()));
        qlService.create(entityList);
        // 4: 判断是否能更新为已分配
        Map<Long, List<ExtPrSouRequirementGroup>> allGroupMap = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class)
                .in(ExtPrSouRequirementGroup::getRequirementHeadId, context.getParam().getRequirementHeadIds()), ExtPrSouRequirementGroup.class)
                .stream().collect(Collectors.groupingBy(ExtPrSouRequirementGroup::getRequirementHeadId));
        for (Long requirementHeadId : context.getParam().getRequirementHeadIds()) {
            List<ExtPrSouRequirementGroup> groups = allGroupMap.get(requirementHeadId);
            if (CollectionUtils.isNotEmpty(groups)) {
                boolean hasAssigned = groups.stream().map(ExtPrSouRequirementGroup::getGroupType).anyMatch(PrSouRequirementGroupTypeEnum.SOU.name()::equals)
                        && groups.stream().map(ExtPrSouRequirementGroup::getGroupType).anyMatch(PrSouRequirementGroupTypeEnum.VENDOR.name()::equals);
                qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementHead.class)
                        .set(ExtPrSouRequirementHead::getHasAssigned, hasAssigned ? Enable.Y : Enable.N)
                        .eq(ExtPrSouRequirementHead::getRequirementHeadId, requirementHeadId));
            }
        }

        context.getExtensions().put(ExtPrSouRequirementGroup.class.getSimpleName(), entityList);

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
