package com.midea.cloud.srm.sou.expert.plugin.event.changeapprovalpass;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpert;
import com.midea.cloud.srm.model.sou.expert.entity.ExtSouExpertApply;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyStatusEnum;
import com.midea.cloud.srm.sou.expert.spi.event.changeapprovalpass.ExtSouExpertChangeApprovalPassContext;
import com.midea.cloud.srm.sou.expert.spi.event.changeapprovalpass.IExtSouExpertChangeApprovalPassPlugin;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * 寻源 - 专家库 - 专家变更审批通过回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/19
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtSouExpertChangeApprovalPassPlugin implements IExtSouExpertChangeApprovalPassPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtSouExpertChangeApprovalPassContext judgeChangeApprovalPassAuth(ExtSouExpertChangeApprovalPassContext context) {
        ExtSouExpertApply expertApply = qlService.readByKey(ExtSouExpertApply.class.getSimpleName(), context.getExpertApplyId(), ExtSouExpertApply.class);
        AssertUtils.notNull(expertApply, "专家申请[{0}]不存在", context.getExpertApplyId());
        switch (expertApply.getApplyStatus()) {
            case "APPROVING":
                // 审批中
            case "APPROVED":
                // 已审批
                break;
            case "DRAFT":
                // 拟定
            case "SUBMITTED":
                // 已提交
            case "REJECTED":
                // 已驳回
            case "WITHDRAW":
                // 已撤回
                throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]尚未提交审批", context.getExpertApplyId()));
            case "ABANDONED":
                // 已废弃
                throw new IllegalArgumentException(MessageFormat.format("专家申请[{0}]已废弃", context.getExpertApplyId()));
            default:
                throw new IllegalArgumentException("无法识别的专家申请状态:" + expertApply.getApplyStatus());
        }
        context.setExpertApply(expertApply);

        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public ExtSouExpertChangeApprovalPassContext executeChangeApprovalPass(ExtSouExpertChangeApprovalPassContext context) {
        // 1: 更新专家申请状态
        qlService.updateByWrapper(QlWrappers.update(ExtSouExpertApply.class)
                .set(ExtSouExpertApply::getApplyStatus, ExtSouExpertApplyStatusEnum.APPROVED)
                .set(ExtSouExpertApply::getApplyTime, LocalDateTime.now())
                .eq(ExtSouExpertApply::getExpertApplyId, context.getExpertApplyId()));
        // 2: 创建专家库
        boolean toCreateExpert;
        ExtSouExpert extSouExpert; {
            extSouExpert = qlService.queryByWrapper(QlWrappers.query(ExtSouExpert.class)
                    .eq(ExtSouExpert::getExpertUsername, context.getExpertApply().getApplyBy()), ExtSouExpert.class)
                    .stream().findFirst().orElse(null);
            toCreateExpert = extSouExpert == null;
            if (extSouExpert == null) {
                extSouExpert = new ExtSouExpert();
                extSouExpert.setExpertId(IdGenrator.generate());
                extSouExpert.setHasQuite(Enable.N);
                extSouExpert.setHasFrozen(Enable.N);
                extSouExpert.setFrozenStatus(null);
            }
            extSouExpert.setExpertApplyId(context.getExpertApplyId());
            extSouExpert.setExpertApplyNo(context.getExpertApply().getExpertApplyNo());
            extSouExpert.setExpertLevel(context.getExpertApply().getApplyLevel());
            extSouExpert.setExpertUserId(context.getExpertApply().getApplyById());
            extSouExpert.setExpertUsername(context.getExpertApply().getApplyBy());
            extSouExpert.setExpertUserCode(context.getExpertApply().getApplyByCode());
            extSouExpert.setExpertFullName(context.getExpertApply().getApplyByNickname());
            extSouExpert.setApplyIdFullPath(extSouExpert.getApplyIdFullPath() != null ? extSouExpert.getApplyIdFullPath() + "," + context.getExpertApplyId() : context.getExpertApplyId().toString());
        }
        if (toCreateExpert) {
            qlService.create(Collections.singletonList(extSouExpert));
        } else {
            qlService.update(Collections.singletonList(extSouExpert));
        }

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
