package com.midea.cloud.srm.sou.expert.plugin.event.changeapprovalunpass;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.base.enums.FlowStatus;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.sou.expert.spi.event.changeapprovalunpass.ExtSouExpertChangeApprovalUnPassContext;
import com.midea.cloud.srm.sou.expert.spi.event.changeapprovalunpass.IExtSouExpertChangeApprovalUnPassPlugin;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * 寻源 - 专家库 - 专家变更审批未通过回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/13
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertChangeApprovalUnPassPlugin implements IExtSouExpertChangeApprovalUnPassPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertChangeApprovalUnPassContext judgeChangeApprovalUnPassAuth(ExtSouExpertChangeApprovalUnPassContext context) {
        context.getParam().formatParams();

        ExtSouExpertApply expertApply = qlService.readByKey(ExtSouExpertApply.class.getSimpleName(), context.getParam().getExpertApplyId(), ExtSouExpertApply.class);
        AssertUtils.notNull(expertApply, "专家申请[{0}]不存在", context.getParam().getExpertApplyId());
        switch (expertApply.getApplyStatus()) {
            // 审批中
            case FlowStatus.APPROVING:
                break;
            // 已审批
            case FlowStatus.APPROVED:
                throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]已审批", context.getParam().getExpertApplyId()));
            // 拟定
            case FlowStatus.DRAFT:
                // 已提交
            case FlowStatus.SUBMITTED:
                throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]尚未提交审批", context.getParam().getExpertApplyId()));
                // 已驳回
            case FlowStatus.REJECTED:
                if (FlowStatus.REJECTED.equals(context.getParam().getApplyStatus().name())) {
                    break;
                } else {
                    throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]已驳回", context.getParam().getExpertApplyId()));
                }
                // 已撤回
            case FlowStatus.WITHDRAW:
                if (FlowStatus.WITHDRAW.equals(context.getParam().getApplyStatus().name())) {
                    break;
                } else {
                    throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]已撤回", context.getParam().getExpertApplyId()));
                }
                // 已废弃
            case FlowStatus.ABANDONED:
                if (FlowStatus.ABANDONED.equals(context.getParam().getApplyStatus().name())) {
                    break;
                } else {
                    throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]已废弃", context.getParam().getExpertApplyId()));
                }
            default:
                throw new IllegalArgumentException("无法识别的专家申请状态:" + expertApply.getApplyStatus());
        }

        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertChangeApprovalUnPassContext executeChangeApprovalUnPass(ExtSouExpertChangeApprovalUnPassContext context) {
        qlService.updateByWrapper(QlWrappers.update(ExtSouExpertApply.class)
                .set(ExtSouExpertApply::getApplyStatus, context.getParam().getApplyStatus())
                .eq(ExtSouExpertApply::getExpertApplyId, context.getParam().getExpertApplyId()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
