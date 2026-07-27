package com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.plugin.event.changesouplan;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlPrRequirementHeadDTO;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.vo.init.MqlPrRequirementHeadVO;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementHeadVO;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.changesouplan.IPrSouRequirementChangePlanPlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.changesouplan.IPrSouRequirementChangePlanValidatePlugin;
import com.midea.cloud.srm.supcooperate.ext.requirement.souplanpool.spi.event.changesouplan.PrSouRequirementChangePlanContext;
import com.midea.cloud.srm.supcooperate.meiql.requirement.core.init.service.MqlPrRequirementInitEventService;
import com.midea.cloud.srm.supcooperate.meiql.requirement.core.init.service.MqlPrRequirementInitQueryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 招标计划池 - 计划变更插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/11
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultPrSouRequirementChangePlanPlugin implements IPrSouRequirementChangePlanPlugin {

    @Autowired
    private MqlPrRequirementInitQueryService mqlPrRequirementInitQueryService;
    @Autowired
    private MqlPrRequirementInitEventService mqlPrRequirementInitEventService;
    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public PrSouRequirementChangePlanContext judgeChangePlanAuth(PrSouRequirementChangePlanContext context) {
        AssertUtils.notNull(context.getParam().getRequirementHeadId(), "缺少requirementHeadId参数");
        MqlPrRequirementHeadVO vo = mqlPrRequirementInitQueryService.getRequirementInfo(context.getParam().getRequirementHeadId());
        AssertUtils.notNull(vo, "招标计划[{0}]不存在", context.getParam().getRequirementHeadId());
        AssertUtils.isTrue(RequirementApproveStatus.APPROVED.equals(vo.getAuditStatus()), "招标计划非已审批");
        ExtPrSouRequirementHeadVO souPrHead = SouObjectXUtil.convertTargetObj(mqlPrRequirementInitQueryService
                .getRequirementInfo(context.getParam().getRequirementHeadId()), ExtPrSouRequirementHeadVO.class);
        AssertUtils.notNull(souPrHead.getSouReqHead(), "招标计划[{0}]不存在", context.getParam().getRequirementHeadId());
        AssertUtils.isFalse(PrSouRequirementStatusEnum.CANCEL.name().equals(souPrHead.getSouReqHead().getSouReqStatus()), "招标计划[{0}]已取消",
                souPrHead.getRequirementHeadNum());
        if (PrSouRequirementStatusEnum.CHANGED.name().equals(souPrHead.getSouReqHead().getSouReqStatus())) {
            ExtPrSouRequirementHead existChangeReqHead = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementHead.class)
                    .eq(ExtPrSouRequirementHead::getChangeRequirementHeadId, context.getParam().getRequirementHeadId()), ExtPrSouRequirementHead.class)
                    .stream().findFirst().orElse(null);
            if (existChangeReqHead != null) {
                RequirementHead requirementHead = qlService.readByKey("PurchaseRequirementHead", existChangeReqHead.getRequirementHeadId(), RequirementHead.class);
                throw new IllegalArgumentException("该招标计划已做变更，单号:" + requirementHead.getRequirementHeadNum());
            } else {
                throw new IllegalArgumentException("该招标计划已做变更");
            }
        }
        context.setExistSouPrHead(souPrHead);

        return context;
    }

    @Override
    @ApiOperation("前置处理")
    public PrSouRequirementChangePlanContext beforeChangePlan(PrSouRequirementChangePlanContext context) {
        return SdkPluginProxy.proxy(IPrSouRequirementChangePlanValidatePlugin.class, context).execute(context);
    }

    @Override
    @ApiOperation("执行处理")
    public PrSouRequirementChangePlanContext executeChangePlan(PrSouRequirementChangePlanContext context) {
        // 1: 保存数据
        MqlPrRequirementHeadDTO param = mqlPrRequirementInitEventService.editRequirement(context.getResult());
        // 2: 更新状态
        qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementHead.class)
                .set(ExtPrSouRequirementHead::getSouReqStatus, PrSouRequirementStatusEnum.CHANGED)
                .eq(ExtPrSouRequirementHead::getRequirementHeadId, context.getExistSouPrHead().getRequirementHeadId()));

        context.setResult(SouObjectXUtil.convertTargetObj(param, ExtPrSouRequirementHeadDTO.class));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
