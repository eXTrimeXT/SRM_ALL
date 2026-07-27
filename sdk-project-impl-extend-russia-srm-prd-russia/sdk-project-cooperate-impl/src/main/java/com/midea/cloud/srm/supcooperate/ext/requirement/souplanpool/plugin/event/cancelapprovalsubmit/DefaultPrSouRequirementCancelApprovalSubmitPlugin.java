package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.cancelapprovalsubmit;

import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.entity.ExtPrSouRequirementCancel;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplanpool.enums.PrSouRequirementCancelStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalsubmit.IPrSouRequirementCancelApprovalSubmitPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.cancelapprovalsubmit.PrSouRequirementCancelApprovalSubmitContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 招标计划 - 计划取消 - 审批提交回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementCancelApprovalSubmitPlugin implements IPrSouRequirementCancelApprovalSubmitPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementCancelApprovalSubmitContext judgeApprovalSubmitAuth(PrSouRequirementCancelApprovalSubmitContext context) {
        ExtPrSouRequirementCancel reqCancel = qlService.readByKey(ExtPrSouRequirementCancel.class.getSimpleName(), context.getRequirementCancelId(), ExtPrSouRequirementCancel.class);
        switch (reqCancel.getCancelStatus()) {
            case "DRAFT":
                // 拟定
            case "SUBMITTED":
                //已提交
            case "APPROVING":
                //审批中
                break;
            default:
                throw new IllegalArgumentException("当前单据状态，禁止访问该接口");
        }

        return context;
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementCancelApprovalSubmitContext executeApprovalSubmit(PrSouRequirementCancelApprovalSubmitContext context) {
        qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementCancel.class)
                .set(ExtPrSouRequirementCancel::getCancelStatus, PrSouRequirementCancelStatusEnum.APPROVING.name())
                .eq(ExtPrSouRequirementCancel::getRequirementCancelId, context.getRequirementCancelId()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
