package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.editreqcancel;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancelLine;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.enums.PrSouRequirementCancelStatusEnum;
import com.midea.cloud.srm.ql.util.MqlCreateUpdateUtils;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.editreqcancel.IPrSouRequirementCancelEditPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.editreqcancel.IPrSouRequirementCancelEditValidatePlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.editreqcancel.PrSouRequirementCancelEditContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 招标计划 - 计划取消插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementCancelEditPlugin implements IPrSouRequirementCancelEditPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementCancelEditContext judgeEditReqCancelAuth(PrSouRequirementCancelEditContext context) {
        // 1: 判断单据状态是否合适
        if (context.getParam().getRequirementCancelId() != null) {
            ExtPrSouRequirementCancel existReqCancel = qlService.readByKey(ExtPrSouRequirementCancel.class.getSimpleName(),
                    context.getParam().getRequirementCancelId(), ExtPrSouRequirementCancel.class);
            context.setExistReqCancel(existReqCancel);
            if (existReqCancel != null) {
                switch (existReqCancel.getCancelStatus()) {
                    case "DRAFT":
                        // 拟定
                    case "REJECTED":
                        // 已驳回
                    case "WITHDRAW":
                        // 已撤回
                        break;
                    case "SUBMITTED":
                        //已提交
                    case "APPROVING":
                        //审批中
                        throw new IllegalArgumentException("单据审批中，禁止编辑");
                    case "ABANDONED":
                        // 已废弃
                        throw new IllegalArgumentException("单据已废弃，禁止编辑");
                    default:
                        throw new IllegalArgumentException(MessageFormat.format("无法识别的单据状态[{0}]，请自行定制处理", existReqCancel.getCancelStatus()));
                }
            }
        }
        // 2: 判断招标计划是否被重复引用
        if (context.getParam().getCancelLineList() != null) {
            AssertUtils.isTrue(context.getParam().getCancelLineList().size() == 1, "需求取消单中只能对应一个招标计划");
            AssertUtils.notNull(context.getParam().getCancelLineList().get(0).getRequirementHeadId(), "请选择需要取消的招标计划");

            ExtPrSouRequirementHead souPrHead = qlService.readByKey(ExtPrSouRequirementHead.class.getSimpleName(),
                    context.getParam().getCancelLineList().get(0).getRequirementHeadId(), ExtPrSouRequirementHead.class);
            AssertUtils.notNull(souPrHead, "招标计划[{0}]不存在", context.getParam().getCancelLineList().get(0).getRequirementHeadId());
            RequirementHead requirementHead = qlService.readByKey("PurchaseRequirementHead", souPrHead.getRequirementHeadId(), RequirementHead.class);
            AssertUtils.isTrue(RequirementApproveStatus.APPROVED.equals(requirementHead.getAuditStatus()), "招标计划非已审批");

            List<ExtPrSouRequirementCancelLine> cancelLineList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementCancelLine.class)
                    .notEq(context.getParam().getRequirementCancelId() != null, ExtPrSouRequirementCancelLine::getRequirementCancelId, context.getParam().getRequirementCancelId())
                    .eq(ExtPrSouRequirementCancelLine::getRequirementHeadId, context.getParam().getCancelLineList().get(0).getRequirementHeadId()), ExtPrSouRequirementCancelLine.class);
            if (!cancelLineList.isEmpty()) {
                Set<Long> cancelIds = cancelLineList.stream().map(ExtPrSouRequirementCancelLine::getRequirementCancelId).collect(Collectors.toSet());
                List<ExtPrSouRequirementCancel> cancelList = qlService.readByKeys(ExtPrSouRequirementCancel.class.getSimpleName(), new ArrayList<>(cancelIds), ExtPrSouRequirementCancel.class)
                        .stream().filter(e -> !PrSouRequirementCancelStatusEnum.ABANDONED.name().equals(e.getCancelStatus())).collect(Collectors.toList());
                AssertUtils.isTrue(cancelList.isEmpty(), "招标计划已被取消单[{0}]引用", cancelList.get(0).getRequirementCancelNo());
            }
        }
        return context;
    }

    @Override
    @ApiOperation("数据准备")
    public PrSouRequirementCancelEditContext prepareEditReqCancel(PrSouRequirementCancelEditContext context) {
        // 1: 查询现有的计划取消明细
        if (context.getExistReqCancel() != null) {
            //noinspection unchecked
            context.setExistReqCancelLineMap(qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementCancelLine.class)
                    .eq(ExtPrSouRequirementCancelLine::getRequirementCancelId, context.getParam().getRequirementCancelId())
                    .orderByAsc(ExtPrSouRequirementCancelLine::getSortIndex), ExtPrSouRequirementCancelLine.class)
                    .stream().collect(Collectors.toMap(ExtPrSouRequirementCancelLine::getRequirementCancelLineId, Function.identity())));
        }
        // 2: 查询现有的计划取消附件
        if (context.getExistReqCancel() != null) {
            //noinspection unchecked
            context.setExistReqCancelAttachMap(qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementCancelAttach.class)
                    .eq(ExtPrSouRequirementCancelAttach::getRequirementCancelId, context.getParam().getRequirementCancelId())
                    .orderByAsc(ExtPrSouRequirementCancelAttach::getSortIndex), ExtPrSouRequirementCancelAttach.class)
                    .stream().collect(Collectors.toMap(ExtPrSouRequirementCancelAttach::getRequirementCancelAttachId, Function.identity())));
        }

        return context;
    }

    @Override
    @ApiOperation("前置处理")
    public PrSouRequirementCancelEditContext beforeEditReqCancel(PrSouRequirementCancelEditContext context) {
        return SdkPluginProxy.proxy(IPrSouRequirementCancelEditValidatePlugin.class, context).execute(context);
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementCancelEditContext executeEditReqCancel(PrSouRequirementCancelEditContext context) {
        MqlCreateUpdateUtils.saveOrUpdate(ExtPrSouRequirementCancel.class, context.getReqCancelEntity().getRequirementCancelId(),
                Collections.singletonList(context.getReqCancelEntity()), ExtPrSouRequirementCancel::getRequirementCancelId);
        MqlCreateUpdateUtils.saveOrUpdate(ExtPrSouRequirementCancelLine.class, context.getReqCancelEntity().getRequirementCancelId(),
                context.getReqCancelLineEntityList(), ExtPrSouRequirementCancelLine::getRequirementCancelId);
        MqlCreateUpdateUtils.saveOrUpdate(ExtPrSouRequirementCancelAttach.class, context.getReqCancelEntity().getRequirementCancelId(),
                context.getReqCancelAttachEntityList(), ExtPrSouRequirementCancelAttach::getRequirementCancelId);

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
