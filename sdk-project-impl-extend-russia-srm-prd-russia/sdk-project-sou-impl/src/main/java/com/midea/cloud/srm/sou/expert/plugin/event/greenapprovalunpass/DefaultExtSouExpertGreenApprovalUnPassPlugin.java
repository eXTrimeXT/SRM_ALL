package com.midea.cloud.srm.sou.expert.plugin.event.greenapprovalunpass;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.base.enums.FlowStatus;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.sou.expert.spi.event.greenapprovalunpass.ExtSouExpertGreenApprovalUnPassContext;
import com.midea.cloud.srm.sou.expert.spi.event.greenapprovalunpass.IExtSouExpertGreenApprovalUnPassPlugin;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * 寻源 - 专家库 - 专家申请绿色通道审批未通过回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/19
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertGreenApprovalUnPassPlugin implements IExtSouExpertGreenApprovalUnPassPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertGreenApprovalUnPassContext judgeGreenApprovalUnPassAuth(ExtSouExpertGreenApprovalUnPassContext context) {
        context.getParam().formatParams();

        ExtSouExpertApply expertApply = qlService.readByKey(ExtSouExpertApply.class.getSimpleName(), context.getParam().getExpertApplyId(), ExtSouExpertApply.class);
        AssertUtils.notNull(expertApply, "专家申请[{0}]不存在", context.getParam().getExpertApplyId());
        switch (expertApply.getApplyStatus()) {
            case FlowStatus.APPROVING:
                // 审批中
                break;
            case FlowStatus.APPROVED:
                // 已审批
                throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]已审批", context.getParam().getExpertApplyId()));
            case FlowStatus.DRAFT:
                // 拟定
            case FlowStatus.SUBMITTED:
                // 已提交
                throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]尚未提交审批", context.getParam().getExpertApplyId()));
            case FlowStatus.REJECTED:
                // 已驳回
                if (FlowStatus.REJECTED.equals(context.getParam().getApplyStatus().name())) {
                    break;
                } else {
                    throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]已驳回", context.getParam().getExpertApplyId()));
                }
            case FlowStatus.WITHDRAW:
                // 已撤回
                if (FlowStatus.WITHDRAW.equals(context.getParam().getApplyStatus().name())) {
                    break;
                } else {
                    throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]已撤回", context.getParam().getExpertApplyId()));
                }
            case FlowStatus.ABANDONED:
                // 已废弃
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
    public ExtSouExpertGreenApprovalUnPassContext executeGreenApprovalUnPass(ExtSouExpertGreenApprovalUnPassContext context) {
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
