package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.event.remverequire;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.vo.init.MqlPrRequirementHeadVO;
import com.midea.cloud.srm.model.pm.pr.shopcart.entity.ShopCart;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementHeadVO;
import com.midea.cloud.srm.supcooperate.ext.requirement.projectplan.service.PrSouProjectPlanEventService;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.remverequire.IRequirementInitRemovePlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.remverequire.RequirementInitRemoveContext;
import io.seata.common.util.StringUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 招标计划 - 删除插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/09/28
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementInitRemovePlugin implements IRequirementInitRemovePlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private PrSouProjectPlanEventService prSouProjectPlanEventService;
    private static final String IS_SOU = "IS_SOU";

    @Override
    @ApiOperation("校验操作条件/权限")
    public RequirementInitRemoveContext judgeRemoveRequirementAuth(RequirementInitRemoveContext context) {
        PrSouRequirementInitRemoveContext souContext = SouObjectXUtil.convertTargetObj(context, PrSouRequirementInitRemoveContext.class);
        // 1: 调用核心方法
        souContext = (PrSouRequirementInitRemoveContext) SdkPluginProxy.callSuper(IRequirementInitRemovePlugin.class, souContext, this).judgeRemoveRequirementAuth(souContext);

        return souContext;
    }

    @Override
    @ApiOperation("数据删除")
    public RequirementInitRemoveContext executeRemoveRequirement(RequirementInitRemoveContext context) {
        PrSouRequirementInitRemoveContext souContext = (PrSouRequirementInitRemoveContext)context;
        // 1: 调用核心方法
        souContext = (PrSouRequirementInitRemoveContext)SdkPluginProxy.callSuper(IRequirementInitRemovePlugin.class, souContext, this).executeRemoveRequirement(souContext);
        // 2: 处理额外招标计划数据
        ExtPrSouRequirementHead souPrHead = qlService.readByKey(ExtPrSouRequirementHead.class.getSimpleName(), context.getRequirementHeadId(), ExtPrSouRequirementHead.class);
        if (souPrHead != null) {
            souContext.putX(IS_SOU, Enable.Y.name());

            ExtPrSouRequirementHeadVO souResult = SouObjectXUtil.convertTargetObj(souContext.getResult(), ExtPrSouRequirementHeadVO.class);
            ExtPrSouRequirementHead souReqHead = qlService.readByKey(ExtPrSouRequirementHead.class.getSimpleName(), souContext.getReqHead().getRequirementHeadId(), ExtPrSouRequirementHead.class);
            souResult.setSouReqHead(souReqHead);
            List<ExtPrSouRequirementGroup> souGroupList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class)
                    .eq(ExtPrSouRequirementGroup::getRequirementHeadId, souContext.getReqHead().getRequirementHeadId()), ExtPrSouRequirementGroup.class);
            souResult.setSouGroupList(souGroupList);
            List<ExtPrSouRequirementVendor> souVendorList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementVendor.class)
                    .eq(ExtPrSouRequirementVendor::getRequirementHeadId, souContext.getReqHead().getRequirementHeadId()), ExtPrSouRequirementVendor.class);
            souResult.setSouVendorList(souVendorList);
            List<ExtPrSouRequirementAttach> souAttachList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementAttach.class)
                    .eq(ExtPrSouRequirementAttach::getRequirementHeadId, souContext.getReqHead().getRequirementHeadId()), ExtPrSouRequirementAttach.class);
            souResult.setSouAttachList(souAttachList);

            qlService.deleteByKeys(ExtPrSouRequirementHead.class.getSimpleName(), Collections.singletonList(souContext.getReqHead().getRequirementHeadId()));
            qlService.deleteByWrapper(QlWrappers.update(ExtPrSouRequirementGroup.class)
                    .eq(ExtPrSouRequirementGroup::getRequirementHeadId, souContext.getReqHead().getRequirementHeadId()));
            qlService.deleteByWrapper(QlWrappers.update(ExtPrSouRequirementVendor.class)
                    .eq(ExtPrSouRequirementVendor::getRequirementHeadId, souContext.getReqHead().getRequirementHeadId()));
            qlService.deleteByWrapper(QlWrappers.update(ExtPrSouRequirementAttach.class)
                    .eq(ExtPrSouRequirementAttach::getRequirementHeadId, souContext.getReqHead().getRequirementHeadId()));

            souContext.setResult(SouObjectXUtil.convertTargetObj(souResult, MqlPrRequirementHeadVO.class));
        }
        return souContext;
    }

    @Override
    @ApiOperation("后置处理")
    public RequirementInitRemoveContext afterRemoveRequirement(RequirementInitRemoveContext context) {
        PrSouRequirementInitRemoveContext souContext = (PrSouRequirementInitRemoveContext)context;
        // 1: 调用核心方法
        souContext = (PrSouRequirementInitRemoveContext) SdkPluginProxy.callSuper(IRequirementInitRemovePlugin.class, souContext, this).afterRemoveRequirement(souContext);
        // 2: 解绑项目计划
        if (Enable.Y.name().equals(souContext.getX(IS_SOU))) {
            prSouProjectPlanEventService.unbindPlan(null, souContext.getRequirementHeadId());
        }
        // 3: 如果是变更单据，需要回写被变更的单据
        ExtPrSouRequirementHeadVO souResult = SouObjectXUtil.convertTargetObj(souContext.getResult(), ExtPrSouRequirementHeadVO.class);
        if (souResult.getSouReqHead() != null && souResult.getSouReqHead().getChangeRequirementHeadId() != null) {
            qlService.updateByWrapper(QlWrappers.update(ExtPrSouRequirementHead.class)
                    .set(ExtPrSouRequirementHead::getSouReqStatus, PrSouRequirementStatusEnum.EXECUTING)
                    .eq(ExtPrSouRequirementHead::getRequirementHeadId, souResult.getSouReqHead().getChangeRequirementHeadId()));
        }
        // 4: 删除购物车关联
        MqlPrRequirementHeadVO result = context.getResult();
        if(StringUtils.isNotBlank(result.getRequirementHeadNum())){
            qlService.updateByWrapper(QlWrappers.update(MqlType.SHOP_CART)
                    .set(ShopCart::getStatus, "APPROVED")
                    .set(ShopCart::getRequirementHeadNum, null)
                    .eq(ShopCart::getRequirementHeadNum, result.getRequirementHeadNum()));
        }

        return context;
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
