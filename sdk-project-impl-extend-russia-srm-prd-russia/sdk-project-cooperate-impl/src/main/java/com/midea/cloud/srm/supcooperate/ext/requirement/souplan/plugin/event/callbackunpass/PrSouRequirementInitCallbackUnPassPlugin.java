package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.event.callbackunpass;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.callbackunpass.IRequirementInitCallbackUnPassPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.callbackunpass.RequirementInitCallbackUnPassContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 招标计划 - 立项未通过回调插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementInitCallbackUnPassPlugin implements IRequirementInitCallbackUnPassPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public RequirementInitCallbackUnPassContext judgeCallbackUnPassAuth(RequirementInitCallbackUnPassContext context) {
        // 1: 调用核心方法
        context = SdkPluginProxy.callSuper(IRequirementInitCallbackUnPassPlugin.class, context, this).judgeCallbackUnPassAuth(context);
        // 2: 判断单据是否为招标计划
        ExtPrSouRequirementHead souReqHead = qlService.readByKey(ExtPrSouRequirementHead.class.getSimpleName(), context.getParam().getRequirementHeadId(), ExtPrSouRequirementHead.class);
        AssertUtils.notNull(souReqHead, "非招标计划，禁止访问该接口");

        return context;
    }

    @Override
    @ApiOperation("更新数据")
    public RequirementInitCallbackUnPassContext executeCallbackUnPass(RequirementInitCallbackUnPassContext context) {
        // 1: 更新头表数据
        qlService.updateByWrapper(QlWrappers.update(PrRequirementHead.class)
                .set(PrRequirementHead::getAuditStatus, context.getParam().getAuditStatus())
                .eq(PrRequirementHead::getRequirementHeadId, context.getParam().getRequirementHeadId()));

        return context;
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
