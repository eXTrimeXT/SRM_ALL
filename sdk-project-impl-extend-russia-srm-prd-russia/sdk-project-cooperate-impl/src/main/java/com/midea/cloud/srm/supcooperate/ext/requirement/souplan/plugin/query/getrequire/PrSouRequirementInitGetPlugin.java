package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.query.getrequire;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.vo.init.MqlPrRequirementHeadVO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementHeadVO;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.query.getrequire.IRequirementInitGetPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.query.getrequire.RequirementInitGetContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 招标计划 - 详情查询插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/09/28
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementInitGetPlugin implements IRequirementInitGetPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public RequirementInitGetContext judgeGetRequirementAuth(RequirementInitGetContext context) {
        PrSouRequirementInitGetContext souContext = SouObjectXUtil.convertTargetObj(context, PrSouRequirementInitGetContext.class);
        // 1: 调用核心方法
        souContext = (PrSouRequirementInitGetContext)SdkPluginProxy.callSuper(IRequirementInitGetPlugin.class, souContext, this).judgeGetRequirementAuth(souContext);

        return souContext;
    }

    @Override
    @ApiOperation("数据查询")
    public RequirementInitGetContext executeGetRequirement(RequirementInitGetContext context) {
        PrSouRequirementInitGetContext souContext = (PrSouRequirementInitGetContext) context;
        // 1: 调用核心方法
        souContext = (PrSouRequirementInitGetContext)SdkPluginProxy.callSuper(IRequirementInitGetPlugin.class, souContext, this).executeGetRequirement(souContext);
        // 2: 查询招标计划额外数据
        ExtPrSouRequirementHeadVO souResult = SouObjectXUtil.convertTargetObj(souContext.getResult(), ExtPrSouRequirementHeadVO.class);
        // 2.1: 查询招标计划
        ExtPrSouRequirementHead souReqHead = qlService.readByKey(ExtPrSouRequirementHead.class.getSimpleName(), souResult.getRequirementHeadId(), ExtPrSouRequirementHead.class);
        souResult.setSouReqHead(souReqHead);
        // 2.2: 查询招标相关负责人
        //noinspection unchecked
        List<ExtPrSouRequirementGroup> souGroupList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class)
                .eq(ExtPrSouRequirementGroup::getRequirementHeadId, souResult.getRequirementHeadId())
                .orderByAsc(ExtPrSouRequirementGroup::getSortIndex), ExtPrSouRequirementGroup.class);
        souResult.setSouGroupList(souGroupList);
        // 2.3: 查询推荐供应商
        //noinspection unchecked
        List<ExtPrSouRequirementVendor> souVendorList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementVendor.class)
                .eq(ExtPrSouRequirementVendor::getRequirementHeadId, souResult.getRequirementHeadId())
                .orderByAsc(ExtPrSouRequirementVendor::getSortIndex), ExtPrSouRequirementVendor.class);
        souResult.setSouVendorList(souVendorList);
        // 2.4: 查询招标计划附件
        //noinspection unchecked
        List<ExtPrSouRequirementAttach> souAttachList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementAttach.class)
                .eq(ExtPrSouRequirementAttach::getRequirementHeadId, souReqHead.getRequirementHeadId())
                .orderByAsc(ExtPrSouRequirementAttach::getSortIndex), ExtPrSouRequirementAttach.class);
        souResult.setSouAttachList(souAttachList);

        souContext.setResult(SouObjectXUtil.convertTargetObj(souResult, MqlPrRequirementHeadVO.class));
        return souContext;
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
