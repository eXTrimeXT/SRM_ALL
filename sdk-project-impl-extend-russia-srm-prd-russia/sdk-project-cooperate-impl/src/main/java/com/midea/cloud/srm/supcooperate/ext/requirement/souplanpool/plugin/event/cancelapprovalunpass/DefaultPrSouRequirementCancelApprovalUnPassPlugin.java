package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.cancelapprovalunpass;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.base.enums.FlowStatus;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.enums.PrSouRequirementCancelStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalunpass.IPrSouRequirementCancelApprovalUnPassPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalunpass.PrSouRequirementCancelApprovalUnPassContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 招标计划 - 计划取消 - 审批未通过回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementCancelApprovalUnPassPlugin implements IPrSouRequirementCancelApprovalUnPassPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementCancelApprovalUnPassContext judgeApprovalUnPassAuth(PrSouRequirementCancelApprovalUnPassContext context) {
        context.getParam().formatParams();
        ExtPrSouRequirementCancel reqCancel = qlService.readByKey(ExtPrSouRequirementCancel.class.getSimpleName(), context.getParam().getRequirementCancelId(), ExtPrSouRequirementCancel.class);
        switch (reqCancel.getCancelStatus()) {
            case FlowStatus.APPROVING:
                // 审批中
                break;
            case FlowStatus.REJECTED:
                // 已驳回
                if (FlowStatus.REJECTED.equals(context.getParam().getCancelStatus().name())) {
                    break;
                } else {
                    throw new IllegalArgumentException("当前单据已驳回，禁止访问该接口" + context.getParam().getCancelStatus());
                }
            case FlowStatus.ABANDONED:
                // 已废弃
                if (FlowStatus.ABANDONED.equals(context.getParam().getCancelStatus().name())) {
                    break;
                } else {
                    throw new IllegalArgumentException("当前单据已废弃，禁止访问该接口" + context.getParam().getCancelStatus());
                }
            case FlowStatus.WITHDRAW:
                // 已撤回
                if (FlowStatus.WITHDRAW.equals(context.getParam().getCancelStatus().name())) {
                    break;
                } else {
                    throw new IllegalArgumentException("当前单据已撤回，禁止访问该接口" + context.getParam().getCancelStatus());
                }
            default:
                throw new IllegalArgumentException("当前单据状态，禁止访问该接口");
        }

        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementCancelApprovalUnPassContext executeApprovalUnPass(PrSouRequirementCancelApprovalUnPassContext context) {
        qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementCancel.class)
                .set(ExtPrSouRequirementCancel::getCancelStatus, context.getParam().getCancelStatus())
                .eq(ExtPrSouRequirementCancel::getRequirementCancelId, context.getParam().getRequirementCancelId())
                .eq(ExtPrSouRequirementCancel::getCancelStatus, PrSouRequirementCancelStatusEnum.APPROVING.name()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
