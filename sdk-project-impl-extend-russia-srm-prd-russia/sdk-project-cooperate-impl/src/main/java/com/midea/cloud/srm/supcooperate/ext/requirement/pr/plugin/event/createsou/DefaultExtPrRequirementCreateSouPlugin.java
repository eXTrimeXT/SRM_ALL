package com.midea.cloud.srm.supcooperate.ext.requirement.pr.plugin.event.createsou;

import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.ExtRbacClient;
import com.midea.cloud.srm.feign.SouExtClient;
import com.midea.cloud.srm.feign.sou.openapi.sourcing.SouForBuyerOpenApiClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.pm.pr.documents.entity.SubsequentDocuments;
import com.midea.cloud.srm.model.pm.pr.documents.enums.SubsequentDocStatusEnum;
import com.midea.cloud.srm.model.pm.pr.documents.enums.SubsequentDocTypeEnum;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.*;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.swagger.init.ApiSouInitSwaggerDTO;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.enums.*;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.pr.requirement.enums.PrRequirementFixPriceStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.spi.event.createsou.ExtPrRequirementCreateSouContext;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.spi.event.createsou.IExtPrRequirementCreateSouPlugin;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 非招需求池 - 创建寻源单
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/11/04
 */
@Slf4j
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class DefaultExtPrRequirementCreateSouPlugin implements IExtPrRequirementCreateSouPlugin {

    @Autowired
    private QlService qlService;
    @Autowired
    private SouForBuyerOpenApiClient souForBuyerOpenApiClient;

    @Autowired
    private SouExtClient souExtClient;

    @Autowired
    private ExtRbacClient extRbacClient;

    @Override
    @ApiOperation("校验操作条件/权限")
    public ExtPrRequirementCreateSouContext judgeCreateSouAuth(ExtPrRequirementCreateSouContext context) {
        context.getParam().formatParam();

        // 1: 查询数据
        List<RequirementLine> requirementLineList = qlService.readByKeys("PurchaseRequirementLine", new ArrayList<>(context.getParam().getRequirementLineIds()), RequirementLine.class);
        AssertUtils.notEmpty(requirementLineList, "请选择需要操作的数据");
        Map<Long/* requirementHeadId */, RequirementHead> requirementHeadMap = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementHead")
                        .in(RequirementHead::getRequirementHeadId, requirementLineList.stream().map(RequirementLine::getRequirementHeadId).collect(Collectors.toSet())), RequirementHead.class)
                .stream().collect(Collectors.toMap(RequirementHead::getRequirementHeadId, Function.identity()));
        // 2: 校验处理
        // 2.1: 确保需求池行数据都是ok的
        requirementLineList.forEach(reqLine -> {
            AssertUtils.isFalse(Enable.Y.name().equals(reqLine.getX("ifCreateInq")), "需求池[{0} - {1}]已创建询比价，请勿重复操作", reqLine.getRequirementHeadNum(), reqLine.getMaterialName());
            AssertUtils.isTrue(PrRequirementFixPriceStatusEnum.DRAFT.name().equals(reqLine.getX("fixPriceStatus"))
                    || PrRequirementFixPriceStatusEnum.PRICE_FAIL.name().equals(reqLine.getX("fixPriceStatus")), "需求池[{0} - {1}]已定价", reqLine.getRequirementHeadNum(), reqLine.getMaterialName());
        });
        // 2.2: 同一业务实体的需求池数据
        long orgId = requirementLineList.get(0).getOrgId();
        boolean isSameOrgOu = requirementLineList.stream().allMatch(e -> orgId == e.getOrgId());
        AssertUtils.isTrue(isSameOrgOu, "同一业务实体下才能创建寻源单");

        context.setPrRequirementLineList(requirementLineList);
        context.setPrRequirementHeadMap(requirementHeadMap);
        return context;
    }

    @Override
    @ApiOperation("前置处理")
    public ExtPrRequirementCreateSouContext beforeCreateSou(ExtPrRequirementCreateSouContext context) {
        log.info("创建寻源单");
        ApiSouInitDTO souInitInfo = new ApiSouInitDTO();
        souInitInfo.setCopy(true);
        souInitInfo.setCreateStep(ApiSouInitDTO.CreateStep.projectInfo);
        // 1: 寻源基本信息
        {
            souInitInfo.setProjectInfo(new ApiSouProjectInfoDTO());
            ExtUser extUser = extRbacClient.getByUserId(AppUserUtil.getLoginAppUser().getUserId());
            if(Objects.isNull(extUser)) {
                extUser = new ExtUser();
            }
            // 1.1: 寻源单
            {
                ApiSouProjectEditDTO souProject = new ApiSouProjectEditDTO();
                souInitInfo.getProjectInfo().setProject(souProject);
                // 1.1.1: ID(略)
                // 1.1.2: 寻源单号(略)
                // 1.1.3: 寻源单名称
                souProject.setSouName("非招需求转寻源_" + System.currentTimeMillis());
                // 1.1.4: 寻源类型(略)
                // 1.1.5: 流程配置ID(略)
                // 1.1.6: 评选方式
                souProject.setScoreRuleType(SouScoreRuleTypeEnum.MIN_PRICE);
                // 1.1.7: 评分模板(略)
                // 1.1.8: 本位币
                souProject.setStandardCurrency("RMB");
                // 1.1.9: 本位币价格精度
                souProject.setPricePrecision(4);
                // 1.1.10: 项目状态(略)
                // 1.1.11: 立项审核状态(略)
                // 1.1.12: 是否密封报价
                souProject.setNeedEncryptPrice(Enable.Y);
                // 1.1.13: 当前轮次(略)
                // 1.1.14: 预计报价地点(略)
                // 1.1.15: 是否同步至价格库
                souProject.setIsSyncToPriceLibrary(Enable.N);
                // 1.1.16: 生成价格审批单方式
                souProject.setGeneratePriceApprovalType(SouGeneratePriceApprovalTypeEnum.BY_TOTAL.name());
                // 1.1.17: 作废原因(略)
                // 1.1.18: 需要密码解密的操作(略)
                // 1.1.19: 是否允许物料变更
                souProject.setAllowItemChange(Enable.N);
                // 1.1.20: 是否允许追加供应商
                souProject.setAllowNewVendors(Enable.N);
                // 1.1.21: 是否允许代理报价
                souProject.setAllowProxyOrder(Enable.Y);
                // 1.1.22: 价格有效期范围(略)
                // 1.1.23: 发布时间(略)
                // 1.1.24: 报名时间范围(略)
                // 1.1.25: 报价时间范围(略)
                // 1.1.26: 最早开标时间(略)
                // 1.1.27: 发布范围
                souProject.setPublishScope(SouPublishScopeEnum.INVITE_TENDER);
                // 1.1.28: 报价方式
                souProject.setOrderWay(SouOrderWayEnum.SINGLE);
                // 1.1.29: 报价类型
                souProject.setOrderType(SouOrderTypeEnum.SIMPLE);
                // 1.1.30: 是否允许供应商撤回报
                souProject.setAllowWithdraw(Enable.Y);
                // 1.1.31: 是否允许供应商只对部分物料报价
                souProject.setAllowPartPrice(Enable.N);
                // 1.1.32: 是否使用未税报价
                souProject.setIsPriceNotax(Enable.Y);
                // 1.1.33: 联系人
                souProject.setLinkman(AppUserUtil.getLoginAppUser().getNickname());
                // 1.1.34: 电话
                souProject.setTel(extUser.getExtOfficePhone());
                // 1.1.35: 邮箱
                souProject.setEmail(AppUserUtil.getLoginAppUser().getEmail());
                // 1.1.36: 备注(略)
                // 1.1.37: 来源类型
                souProject.setSourceFromType(SouSourceFromTypeEnum.PURCHASE_REQ.name());
                // 1.1.38: 来源单据ID
                souProject.setSourceFromId(context.getPrRequirementLineList().get(0).getRequirementHeadId());
                // 1.1.39: 来源单据号
                Set<String> reqHeadNums = new HashSet<>(context.getPrRequirementLineList().size());
                StringBuilder sb = new StringBuilder(200);
                for (RequirementLine reqLine : context.getPrRequirementLineList()) {
                    if (reqHeadNums.add(reqLine.getRequirementHeadNum())) {
                        sb.append(reqLine.getRequirementHeadNum())
                                .append(",");
                    }
                }
                if (sb.length() > 0) {
                    souProject.setSourceFromNo(sb.substring(0, sb.length() - 1));
                }
                // 1.1.40: 报价模板(略)
            }
            // 1.2: 工作小组(略)
            // 1.3: 可用币种(略)
            // 1.4: 内外部查看附件(略)
            // 1.5: 供方必须上次附件(略)
        }
        // 2: 寻源项目需求
        extracted(context, souInitInfo);
        // 3: 寻源评分规则
        {
            souInitInfo.setScoreInfo(new ApiSouInitScoreInfoDTO());
            souInitInfo.getScoreInfo().setScoreRuleType(SouScoreRuleTypeEnum.MIN_PRICE);
        }

        context.setSouInitInfo(souInitInfo);
        return context;
    }

    /**
     * 寻源项目需求
     *
     * @param context     参数
     * @param souInitInfo 参数
     */
    private static void extracted(ExtPrRequirementCreateSouContext context, ApiSouInitDTO souInitInfo) {
        souInitInfo.setRequireInfo(new ApiSouRequireInfoDTO());
        souInitInfo.getRequireInfo().setItemList(new ArrayList<>(context.getPrRequirementLineList().size()));
        // 根据【物料编码+物料名称+公司+区域】对需求池数据进行分组(加上物料名称，用于处理无编码物料的情况)
        Map<String/* materialCode_materialName_orgCode_areaCode */, List<RequirementLine>> reqLineMap = context.getPrRequirementLineList().stream()
                .collect(Collectors.groupingBy(e -> e.getMaterialCode() + "_" + e.getMaterialName() + "_" + e.getOrgCode() + "_" + e.getX("extAreaCode")));
        for (List<RequirementLine> reqLineList : reqLineMap.values()) {
            ApiSouItemDTO souItem = new ApiSouItemDTO();
            souInitInfo.getRequireInfo().getItemList().add(souItem);

            RequirementLine reqLine = reqLineList.get(0);

            souItem.putX("ext_id_0", reqLine.getMaterialCode() + "_" + reqLine.getMaterialName() + "_" + reqLine.getOrgCode() + "_" + reqLine.getX("extAreaCode"));

            // 2.1: ID(略)
            // 2.2: 业务实体
            souItem.setOrgOuCode(reqLine.getOrgCode());
            // 2.3: 库存组织(略)
            // 2.4: 物料组合(略)
            // 2.5: 是否无编码物料
            souItem.setNoCodeItem(reqLine.getMaterialCode() == null ? Enable.Y : Enable.N);
            // 2.6: 物料
            souItem.setItemId(reqLine.getMaterialId());
            souItem.setItemDesc(reqLine.getMaterialName());
            // 2.7: 单位
            souItem.setUnit(reqLine.getUnitCode());
            // 2.8: 品类
            souItem.setCategoryId(reqLine.getCategoryId());
            souItem.setCategoryCode(reqLine.getCategoryCode());
            souItem.setCategoryName(reqLine.getCategoryName());
            // 2.9: 数量【需求池的数量累计】
            BigDecimal requireQuantity = BigDecimal.ZERO;
            {
                for (RequirementLine rl : reqLineList) {
                    requireQuantity = requireQuantity.add(rl.getRequirementQuantity() != null ? rl.getRequirementQuantity() : BigDecimal.ZERO);
                }
            }
            souItem.setRequireQuantity(requireQuantity);
            // 2.10: 需求时间(略)
            // 2.11: 预计采购金额
            BigDecimal buyAmount = BigDecimal.ZERO;
            {
                for (RequirementLine rl : reqLineList) {
                    buyAmount = buyAmount.add(rl.getTotalAmount() != null ? rl.getTotalAmount() : BigDecimal.ZERO);
                }
            }
            souItem.setBuyAmount(buyAmount);
            // 2.12: 价格有效期范围(略)
            // 2.13: 来源单据ID
            souItem.setSourceFromId(souInitInfo.getProjectInfo().getProject().getSourceFromId());
            // 2.14: 来源单据号
            Set<String> reqHeadNums = new HashSet<>(reqLineList.size());
            StringBuilder sb = new StringBuilder(200);
            for (RequirementLine rl : reqLineList) {
                if (reqHeadNums.add(rl.getRequirementHeadNum())) {
                    sb.append(rl.getRequirementHeadNum()).append(",");
                }
            }
            if (sb.length() > 0) {
                souItem.setSourceFromNo(sb.substring(0, sb.length() - 1));
            }
            // 2.15: 来源单据行ID
            souItem.setSourceFromLineId(reqLine.getRequirementLineId());
            // 2.16: 来源单据行号
            souItem.setSourceFromLineNo(reqLine.getRowNum() + "");
            // 2.17: 备注(略)
            // 2.18: 是否阶梯报价
            souItem.setIsLadder(Enable.N);
            // 2.19: 来演类型
            souItem.setSourceFromType(souInitInfo.getProjectInfo().getProject().getSourceFromType());
            // 2.20: 排序(略)
        }
    }

    @Override
    @ApiOperation("执行处理")
    public ExtPrRequirementCreateSouContext executeCreateSou(ExtPrRequirementCreateSouContext context) {
        ApiSouInitSwaggerDTO result = souExtClient.editInitInfo(SouObjectXUtil.convertTargetObj(context.getSouInitInfo(), ApiSouInitSwaggerDTO.class), context.getParam().getSouType());
        context.setResult(SouObjectXUtil.convertTargetObj(result.getProjectInfo().getProject(), SouProject.class));
        return context;
    }

    @Override
    @ApiOperation("后置处理")
    public ExtPrRequirementCreateSouContext afterCreateSou(ExtPrRequirementCreateSouContext context) {
        // 添加后续单据记录
        List<SubsequentDocuments> docList = new ArrayList<>(context.getPrRequirementLineList().size());
        {
            SubsequentDocuments doc;
            for (RequirementLine reqLine : context.getPrRequirementLineList()) {
                //添加后续单据记录
                doc = new SubsequentDocuments();
                docList.add(doc);
                doc.setSubsequentDocumentsId(IdGenrator.generate());
                doc.setFollowFormId(context.getSouInitInfo().getProjectId());
                doc.setSubsequentDocumentsNumber(context.getSouInitInfo().getSouNo());
                doc.setRequirementLineId(reqLine.getRequirementLineId());
                if (SouTypeEnum.inq.name().equals(context.getParam().getSouType())) {
                    doc.setIsubsequentDocumentssType(SubsequentDocTypeEnum.SOU_INQ.name());
                } else if (SouTypeEnum.bid.name().equals(context.getParam().getSouType())) {
                    doc.setIsubsequentDocumentssType(SubsequentDocTypeEnum.SOU_BID.name());
                } else if (SouTypeEnum.comp.name().equals(context.getParam().getSouType())) {
                    doc.setIsubsequentDocumentssType(SubsequentDocTypeEnum.SOU_COMP.name());
                } else if (SouTypeEnum.auct.name().equals(context.getParam().getSouType())) {
                    doc.setIsubsequentDocumentssType(SubsequentDocTypeEnum.SOU_AUCT.name());
                } else {
                    throw new IllegalArgumentException("无法识别的寻源类型，请自行处理");
                }
                doc.setDocStatus(SubsequentDocStatusEnum.VALID.name());
            }
            qlService.create(docList);
        }
        // 更新采购申请行状态为已寻源、"是否已创建后续单据"字段
        qlService.updateByWrapper(QlWrappers.update("PurchaseRequirementLine")
                .set(RequirementLine::getIfCreateFollowForm, Enable.Y)
                .set("ifCreateInq", Enable.Y)
                .in(RequirementLine::getRequirementLineId, context.getParam().getRequirementLineIds()));

        return context;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
