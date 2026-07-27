package com.midea.cloud.srm.supcooperate.ext.requirement.pr.plugin.event.createsou;

import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.init.ApiInqSouInitDTO;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.init.ApiInqSouItemDTO;
import com.midea.cloud.srm.model.sou.openapi.inq.dto.init.ApiInqSouProjectEditDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiSouInitDTO;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.spi.event.createsou.ExtPrRequirementCreateSouContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.spi.event.createsou.IExtPrRequirementCreateSouPlugin;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 非招需求池 - 创建询比价
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/11/04
 */
@Slf4j
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class InqExtPrRequirementCreateSouPlugin implements IExtPrRequirementCreateSouPlugin {

    @Autowired
    private QlService qlService;

    @Override
    @ApiOperation("前置处理")
    public ExtPrRequirementCreateSouContext beforeCreateSou(ExtPrRequirementCreateSouContext context) {
        log.info("创建询价单");
        // 1: 调用基础服务
        context = SdkPluginProxy.callSuper(IExtPrRequirementCreateSouPlugin.class, context, this).beforeCreateSou(context);
        // 2: 处理额外信息
        ApiInqSouInitDTO inqSouInitInfo = SouObjectXUtil.convertTargetObj(context.getSouInitInfo(), ApiInqSouInitDTO.class);
        inqSouInitInfo.getProjectInfo().setSequenceCode("SEQ_SOU_INQ_NO");
        // 2.1: 寻源基础信息
        {
            // 2.1.1: 寻源单
            {
                ApiInqSouProjectEditDTO inqSouProject = inqSouInitInfo.getProjectInfo().getProject();
                // 2.1.1.1: 询价状态(略)
                // 2.1.1.2: 询价类型
                inqSouProject.setInquiryType("PRODUCTION_MATERIAL");
                // 2.1.1.3: 汇率类型(略)
                // 2.1.1.4: 币种转换日期(略)
                // 2.1.1.5: 是否已设定目标价(略)
                // 2.1.1.6: 是否排除黑名单供应商
                inqSouProject.setExcludeBlackVendors(Enable.Y);
                // 2.1.1.7: 是否排除非本业务实体供应商
                inqSouProject.setExcludeNoCurrentOrgVendors(Enable.Y);
                // 2.1.1.8: 是否排除业务实体退出/冻结供应商
                inqSouProject.setExcludeOrgQuitVendors(Enable.Y);
                // 2.1.1.9: 需要排除指定品类状态的供应商(略)
                // 2.1.1.10: 排序非本业务受限实体供应商
                inqSouProject.setExtExcludeOrgLimitVendors(Enable.Y);
                // 2.1.1.11: 供应商绩效前几名(略)
                // 2.1.1.12: 是否随机
                inqSouProject.setExtIsRandom(Enable.Y);
                // 2.1.1.13: 部门
                if (AppUserUtil.getLoginAppUser() != null) {
                    inqSouProject.setExtDepartmentId(AppUserUtil.getLoginAppUser().getCeeaDeptId());
                    inqSouProject.setExtDepartmentName(AppUserUtil.getLoginAppUser().getDepartment());
                }
            }
        }
        // 2.2: 寻源物料需求
        // 根据【物料编码+公司+区域】对需求池数据进行分组
        Map<String/* materialCode_materialName_orgCode_areaCode */, List<RequirementLine>> reqLineMap = context.getPrRequirementLineList().stream()
                .collect(Collectors.groupingBy(e -> e.getMaterialCode() + "_" + e.getMaterialName() + "_" + e.getOrgCode() + "_" + e.getX("extAreaCode")));
        for (ApiInqSouItemDTO souItem : inqSouInitInfo.getRequireInfo().getItemList()) {
            String key = souItem.getX("ext_id_0");
            List<RequirementLine> rqLineList = reqLineMap.get(key);
            // 将需求池数据，按照需求数量由高到低排序
            rqLineList.sort(Comparator.comparing(RequirementLine::getRequirementQuantity).reversed());

            // 2.2.1: 阶梯价类型
            souItem.setLadderType("standard");
            // 2.2.2: 是否公示报价
            souItem.setIsFormula(Enable.N);
            // 2.2.3: 物料价格公式(略)
            // 2.2.4: 公式ID(略)
            // 2.2.5: 行类型
            souItem.setItemType("STANDARD");
            // 2.2.6: 公式名称(略)
            // 2.2.7: 公式值(略)
            // 2.2.8: 未税目标价(略)
            // 2.2.9: 物料规格型号
            souItem.setExtMaterialModel(rqLineList.get(0).getX("extMaterialModel"));
            // 2.2.10: 品牌
            souItem.setExtBrand(rqLineList.get(0).getBrand());
            // 2.2.11: 区域
            if (CollectionUtils.isNotEmpty(rqLineList)) {
                souItem.setExtAreaId(rqLineList.get(0).getX("extAreaId"));
                souItem.setExtAreaCode(rqLineList.get(0).getX("extAreaCode"));
                souItem.setExtAreaName(rqLineList.get(0).getX("extAreaName"));
            }
            //备注
            souItem.setRemark(rqLineList.get(0).getComments());
            // 2.2.12: 来源单据明细ID集合
            Set<Long> reqLineIds = new HashSet<>(rqLineList.size());
            StringBuilder sb = new StringBuilder(200);
            for (RequirementLine rl : rqLineList) {
                if (reqLineIds.add(rl.getRequirementLineId())) {
                    sb.append(rl.getRequirementLineId()).append(",");
                }
            }
            if (sb.length() > 0 ) {
                souItem.setExtSourceFromLineIds(sb.substring(0, sb.length() - 1));
            }
            // 2.2.13: 附件
            souItem.setItemFiles(Collections.singletonList(new SceneFile())); {
                souItem.getItemFiles().get(0).setFileuploadId(rqLineList.get(0).getX("extAttachId"));
                souItem.getItemFiles().get(0).setFileName(rqLineList.get(0).getX("extAttachName"));
            }
        }
        context.setSouInitInfo(SouObjectXUtil.convertTargetObj(inqSouInitInfo, ApiSouInitDTO.class));
        return context;
    }

    @Override
    @ApiOperation("后置处理")
    public ExtPrRequirementCreateSouContext afterCreateSou(ExtPrRequirementCreateSouContext context) {
        // 1: 调用基础服务
        context = SdkPluginProxy.callSuper(IExtPrRequirementCreateSouPlugin.class, context, this).afterCreateSou(context);
        // 2: 更新相关字段
        qlService.updateByWrapper(QlWrappers.update("PurchaseRequirementLine")
                .set("extInqProjectId", context.getResult().getProjectId())
                .set("extInqSouNo", context.getResult().getSouNo())
                .in(RequirementLine::getRequirementLineId, context.getParam().getRequirementLineIds()));

        return context;
    }

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public String matchScene() {
        return SouTypeEnum.inq.name();
    }

}
