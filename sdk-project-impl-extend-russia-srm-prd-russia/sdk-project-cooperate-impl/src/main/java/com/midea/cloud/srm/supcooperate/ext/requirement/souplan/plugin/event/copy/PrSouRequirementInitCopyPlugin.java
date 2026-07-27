package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.event.copy;

import com.midea.cloud.common.sdkplugin.ISdkPlugin;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlPrRequirementHeadDTO;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.copyrequire.IRequirementInitCopyPlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.copyrequire.RequirementInitCopyContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 采购申请 - 复制插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/06/02
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementInitCopyPlugin implements IRequirementInitCopyPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("校验操作条件/权限")
    public RequirementInitCopyContext judgeCopyRequirementAuth(RequirementInitCopyContext context) {
        PrSouRequirementInitCopyContext souContext = SouObjectXUtil.convertTargetObj(context, PrSouRequirementInitCopyContext.class);
        // 1: 调用核心方法
        souContext = (PrSouRequirementInitCopyContext) SdkPluginProxy.callSuper(IRequirementInitCopyPlugin.class, souContext, this).judgeCopyRequirementAuth(souContext);
        // 2: 判断单据是否为招标计划
        AssertUtils.isTrue(ISdkPlugin.DEFAULT_SCENE.equals(souContext.getReqHead().getX("sceneType").toString()), "非招标计划，禁止访问该接口");

        return souContext;
    }

    @Override
    @ApiOperation("前置处理")
    public RequirementInitCopyContext beforeCopyRequirement(RequirementInitCopyContext context) {
        PrSouRequirementInitCopyContext souContext = (PrSouRequirementInitCopyContext) context;
        // 1: 调用核心方法
        souContext = (PrSouRequirementInitCopyContext) SdkPluginProxy.callSuper(IRequirementInitCopyPlugin.class, souContext, this).beforeCopyRequirement(souContext);
        // 2: 组装招标计划相关字段
        ExtPrSouRequirementHeadDTO dto = SouObjectXUtil.convertTargetObj(souContext.getEditDTO(), ExtPrSouRequirementHeadDTO.class);
        // 2.1: 招标计划头信息
        {
            dto.setSouReqHead(qlService.readByKey(ExtPrSouRequirementHead.class.getSimpleName(), souContext.getRequirementHeadId(), ExtPrSouRequirementHead.class));
            // 2.1.1: ID(置空)
            dto.getSouReqHead().setRequirementHeadId(null);
            // 2.1.2: 所属板块(保留 - 略)
            // 2.1.3: 需求来源(保留 - 略)
            // 2.1.4: 未报月度计划原因(保留 - 略)
            // 2.1.5: 项目名称
            dto.getSouReqHead().setProjectName(dto.getSouReqHead().getProjectName() + "-复制");
            // 2.1.6: 月份(保留 - 略)
            // 2.1.7: 所属品类(保留 - 略)
            // 2.1.8: 投资编号(保留 - 略)
            // 2.1.9: 数量/规模(保留 - 略)
            // 2.1.10: 概算金额(万元)(保留 - 略)
            // 2.1.11: 是否公示(保留 - 略)
            // 2.1.12: 不公示理由(保留 - 略)
            // 2.1.13: 公示截止时间(保留 - 略)
            // 2.1.14: 项目所在地(保留 - 略)
            // 2.1.15: 前置技术交流意向(保留 - 略)
            // 2.1.16: 递交招标资料时间(保留 - 略)
            // 2.1.17: 项目计划(置空 - 每个招标计划只能对应一个项目计划)
            dto.getSouReqHead().setProjectPlanId(null);
            dto.getSouReqHead().setPlanNo(null);
            // 2.1.18: 特殊招标类型(保留 - 略)
            // 2.1.19: 特定原因(保留 - 略)
            // 2.1.20: 需求产生时间(保留 - 略)
            // 2.1.21: 需求产生时间附件ID(保留 - 略)
            // 2.1.22: 需求产生时间附件名称(保留 - 略)
            // 2.1.23: 工期交货期(保留 - 略)
            // 2.1.24: 工期交货期附件ID(保留 - 略)
            // 2.1.25: 工期交货期附件名称(保留 - 略)
            // 2.1.26: 签合同用时(保留 - 略)
            // 2.1.27: 投入使用时间附件ID(保留 - 略)
            // 2.1.28: 投入使用时间附件名称(保留 - 略)
            // 2.1.29: 投入使用时间(保留 - 略)
            // 2.1.30: 其他特殊原因补充(保留 - 略)
            // 2.1.31: 项目概况及范围(保留 - 略)
            // 2.1.32: 技术要求(保留 - 略)
            // 2.1.33: 业绩要求(保留 - 略)
            // 2.1.34: 供应商资质要求(保留 - 略)
        }
        // 2.2: 招标计划工作小组
        {
            //noinspection unchecked
            List<ExtPrSouRequirementGroup> souGroupList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class)
                    .eq(ExtPrSouRequirementGroup::getRequirementHeadId, souContext.getRequirementHeadId())
                    .orderByAsc(ExtPrSouRequirementGroup::getSortIndex), ExtPrSouRequirementGroup.class);
            dto.setSouGroupList(souGroupList);
            for (ExtPrSouRequirementGroup souGroup : souGroupList) {
                // 2.2.1: ID(置空)
                souGroup.setRequirementGroupId(null);
                // 2.2.2: 招标计划ID(置空)
                souGroup.setRequirementHeadId(null);
                // 2.2.3: 用户(保留 - 略)
                // 2.2.4: 工作职责(保留 - 略)
                // 2.2.5: 联系方式(保留 - 略)
                // 2.2.6: 邮箱(保留 - 略)
                // 2.2.7: 排序(保留 - 略)
            }
        }
        // 2.3: 招标计划附件
        {
            //noinspection unchecked
            List<ExtPrSouRequirementAttach> souAttachList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementAttach.class)
                    .eq(ExtPrSouRequirementAttach::getRequirementHeadId, souContext.getRequirementHeadId())
                    .orderByAsc(ExtPrSouRequirementAttach::getSortIndex), ExtPrSouRequirementAttach.class);
            dto.setSouAttachList(souAttachList);
            for (ExtPrSouRequirementAttach attach : souAttachList) {
                // 2.3.1: ID(置空)
                attach.setRequirementAttachId(null);
                // 2.3.2: 招标计划ID(置空)
                attach.setRequirementHeadId(null);
                // 2.3.3: 文件类型(保留 - 略)
                // 2.3.4: 文件ID/名称(保留 - 略)
                // 2.3.5: 上传时间(保留 - 略)
                // 2.3.6: 排序(保留 - 略)
            }
        }
        // 2.4: 招标计划推荐供应商
        {
            //noinspection unchecked
            List<ExtPrSouRequirementVendor> souVendorList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementVendor.class)
                    .eq(ExtPrSouRequirementVendor::getRequirementHeadId, souContext.getRequirementHeadId())
                    .orderByAsc(ExtPrSouRequirementVendor::getSortIndex), ExtPrSouRequirementVendor.class);
            dto.setSouVendorList(souVendorList);
            for (ExtPrSouRequirementVendor vendor : souVendorList) {
                // 2.4.1: ID(置空)
                vendor.setRequirementVendorId(null);
                // 2.4.2: 招标计划ID
                vendor.setRequirementHeadId(null);
                // 2.4.3: 供应商(保留 - 略)
                // 2.4.4: 联系方式(保留 - 略)
                // 2.4.5: 邮箱(保留 - 略)
                // 2.5.6: 推荐来源(保留 - 略)
                // 2.5.7: 排序(保留 - 略)
            }
        }

        souContext.setEditDTO(SouObjectXUtil.convertTargetObj(dto, MqlPrRequirementHeadDTO.class));
        return souContext;
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
