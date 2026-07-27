package com.midea.cloud.srm.sou.expert.plugin.event.greenapprovalsubmit;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyStatusEnum;
import com.midea.cloud.srm.sou.expert.spi.event.greenapprovalsubmit.ExtSouExpertGreenApprovalSubmitContext;
import com.midea.cloud.srm.sou.expert.spi.event.greenapprovalsubmit.IExtSouExpertGreenApprovalSubmitPlugin;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * 寻源 - 专家库 - 专家申请绿色通道审批提交回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/19
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertGreenApprovalSubmitPlugin implements IExtSouExpertGreenApprovalSubmitPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertGreenApprovalSubmitContext judgeGreenApprovalSubmitAuth(ExtSouExpertGreenApprovalSubmitContext context) {
        ExtSouExpertApply expertApply = qlService.readByKey(ExtSouExpertApply.class.getSimpleName(), context.getExpertApplyId(), ExtSouExpertApply.class);
        AssertUtils.notNull(expertApply, "专家申请[{0}]不存在", context.getExpertApplyId());
        AssertUtils.isTrue(Enable.Y.equals(expertApply.getHasSubmit()), "专家申请信息[{0}]尚未提交验证", context.getExpertApplyId());
        switch (expertApply.getApplyStatus()) {
            case "DRAFT":
                // 拟定
            case "SUBMITTED":
                // 已提交
            case "REJECTED":
                // 已驳回
            case "WITHDRAW":
                // 已撤回
            case "APPROVING":
                // 审批中(接口幂等)
                break;
            case "APPROVED":
                // 已审批
                throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]已审批通过", context.getExpertApplyId()));
            case "ABANDONED":
                // 已废弃
                throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]已废弃", context.getExpertApplyId()));
            default:
                throw new IllegalArgumentException("无法识别的专家申请状态:" + expertApply.getApplyStatus());
        }

        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertGreenApprovalSubmitContext executeGreenApprovalSubmit(ExtSouExpertGreenApprovalSubmitContext context) {
        qlService.updateByWrapper(QlWrappers.update(ExtSouExpertApply.class)
                .set(ExtSouExpertApply::getApplyStatus, ExtSouExpertApplyStatusEnum.APPROVING)
                .eq(ExtSouExpertApply::getExpertApplyId, context.getExpertApplyId()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
