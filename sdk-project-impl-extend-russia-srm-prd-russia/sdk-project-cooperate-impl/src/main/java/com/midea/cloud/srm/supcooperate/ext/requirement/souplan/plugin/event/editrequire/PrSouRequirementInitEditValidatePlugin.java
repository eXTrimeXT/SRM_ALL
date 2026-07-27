package com.midea.cloud.srm.supcooperate.ext.requirement.souplan.plugin.event.editrequire;

import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.sdkplugin.SdkPluginProxy;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.organization.entity.OrganizationRelation;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseCategory;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.dto.init.MqlPrRequirementHeadDTO;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.entity.PrRequirementHead;
import com.midea.cloud.srm.model.pm.mql.pr.requirement.enums.PrRequirementSourceFromTypeEnum;
import com.midea.cloud.srm.model.pm.pr.division.dto.DivisionCategoryQueryDTO;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.projectplan.enums.ExtPrSouProjectPlanStatusEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.dto.ExtPrSouRequirementHeadDTO;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementFromEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementSpecialSouTypeEnum;
import com.midea.cloud.srm.pr.division.service.IDivisionCategoryService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.ql.util.MqlCreateUpdateUtils;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.editrequire.IRequirementInitEditValidatePlugin;
import com.midea.cloud.srm.supcooperate.spi.meiql.requirement.init.event.editrequire.RequirementInitEditContext;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 招标计划 - 详情编辑校验插件
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/04
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouRequirementInitEditValidatePlugin implements IRequirementInitEditValidatePlugin {

    @Autowired
    private BaseClient baseClient;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private QlService qlService;
    @Autowired
    private IDivisionCategoryService divisionCategoryService;

    private static final Pattern YEAR_MONTH_PATTERN = Pattern.compile("^(\\d{4})-(0?[1-9]|1[0-2])$");

    @Override
    public RequirementInitEditContext execute(RequirementInitEditContext context) {
        // 1: 校验及转化采购申请头
        context = SdkPluginProxy.proxy(IRequirementInitEditValidatePlugin.class, context).validateAndConvertReqHead(context);
        // 2: 校验及转化招标计划
        context = this.validateAndConvertPrSouHead(context);
        // 3: 校验及转化工作成员
        context = this.validateAndConvertPrSouGroups(context);
        // 4: 校验及转化推荐供应商
        context = this.validateAndConvertPrSouVendors(context);
        // 5: 校验及转化附件
        context =  this.validateAndConvertPrSouAttaches(context);

        // 6: 额外校验
        PrSouRequirementInitEditContext souContext = (PrSouRequirementInitEditContext) context;
        if (!context.getParam().isTempSave() && PrSouRequirementFromEnum.MONTH.name().equals(souContext.getSouReqHeadEntity().getRequireFrom())) {
            // 6.1: 当需求类型为月度时，依据提交审批时间找前30天内，是否有相同板块，相同品类的招标计划(审批中/已审批)，若存在，则提示“同一月内存在相同模块相同品类的招标计划，不允许重复提交”
            List<ExtPrSouRequirementHead> existSouReqList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementHead.class)
                    .eq(ExtPrSouRequirementHead::getOrgBuCode, souContext.getSouReqHeadEntity().getOrgBuCode())
                    .notEq(ExtPrSouRequirementHead::getRequireFrom, PrSouRequirementFromEnum.SPECIAL_SOU.name())
                    .notEq(ExtPrSouRequirementHead::getRequirementHeadId, souContext.getSouReqHeadEntity().getRequirementHeadId()), ExtPrSouRequirementHead.class);

            if (!existSouReqList.isEmpty()) {
                List<RequirementHead> reqHeadList = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementHead")
                        .in(RequirementHead::getRequirementHeadId, existSouReqList.stream().map(ExtPrSouRequirementHead::getRequirementHeadId).collect(Collectors.toList()))
                        .in(RequirementHead::getAuditStatus, Arrays.asList(RequirementApproveStatus.APPROVING.name(), RequirementApproveStatus.APPROVED.name()))
                        .eq(RequirementHead::getCategoryCode, souContext.getReqHeadEntity().getCategoryCode()), RequirementHead.class);
                Map<Long/* requirementHeadId */, ExtPrSouRequirementHead> existSouReqMap = existSouReqList.stream().collect(Collectors.toMap(ExtPrSouRequirementHead::getRequirementHeadId, Function.identity()));
                for (RequirementHead reqHead : reqHeadList) {
                    ExtPrSouRequirementHead souReqHead = existSouReqMap.get(reqHead.getRequirementHeadId());
                    if (souReqHead.getSubmitApprovalTime() == null) { continue; }
                    LocalDate date = souReqHead.getSubmitApprovalTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    long days = Math.abs(LocalDate.now().toEpochDay() - date.toEpochDay());
                    Object tszb = context.getParam().getExtensions().get("requireFrom");
                    if (!PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(tszb)) {
                        AssertUtils.isTrue(days > 30, "同一月内存在相同模块相同品类的招标计划[{0}]，不允许重复提交", reqHead.getRequirementHeadNum());
                    }
                }
            }
            // 6.2: 当需求类型为月度时，校验当前日期是否为1-19号，26-31号（即非20-25号），若是，则提示“月度计划仅限20-25号进行提报，其他计划情形请提计划外流程”
            int dayOfMonth = LocalDate.now().getDayOfMonth();
            List<DictItemDTO> monthTb = baseClient.listAllByDictCode("MONTH_LIMIT_TB");
            int startDay = 20;
            int endDay = 25;
            for (DictItemDTO dictItemDTO : monthTb) {
                if ("日期从".equals(dictItemDTO.getDictItemName())) {
                    startDay = Integer.parseInt(dictItemDTO.getDictItemCode());
                }
                if ("日期到".equals(dictItemDTO.getDictItemName())) {
                    endDay = Integer.parseInt(dictItemDTO.getDictItemCode());
                }
            }
            AssertUtils.isFalse(dayOfMonth < startDay || dayOfMonth > endDay, String.format("月度计划仅限%d-%d号进行提报，其他计划情形请提计划外流程", startDay, endDay));
        }

        return context;
    }

    @Override
    @ApiOperation("校验及构造采购申请头信息")
    public RequirementInitEditContext validateAndConvertReqHead(RequirementInitEditContext context) {
        // 1: 数据校验
        MqlPrRequirementHeadDTO param = context.getParam(); {
            // 1.1: ID
            if (param.getRequirementHeadId() != null) {
                AssertUtils.notNull(context.getExistReqHead(), LocaleHandler.getLocaleMsg("采购申请单[{0}]不存在"), param.getRequirementHeadId());
            }
            // 1.2: 采购申请编号(置空 - 后端处理)
            param.setRequirementHeadNum(null);
            // 1.3: 需求类型
            param.setDemandType(StringUtils.trimToNull(param.getDemandType()));
            AssertUtils.isTrue(param.isTempSave() || param.getDemandType() != null, "请选择需求类型");
            // 1.4: 采购类型(置空)
            param.setCeeaPurchaseType(null);
            // 1.5: 来源信息
            param.setSourceFromType(PrRequirementSourceFromTypeEnum.HAND_MAKE.name());
            param.setSourceFromId(null);
            param.setSourceFromNo(null);
            // 1.6: 业务实体
            AssertUtils.notNull(param.getOrgId(), "请选择业务实体");
            Organization org = context.getOrganizationMap().get(param.getOrgId());
            AssertUtils.notNull(org, LocaleHandler.getLocaleMsg("业务实体[{0} - {1}]不存在"), param.getOrgId(), param.getOrgCode());
            AssertUtils.isTrue("OU".equals(org.getOrganizationTypeCode()), LocaleHandler.getLocaleMsg("组织[{0} - {1}]的类型不是业务实体"), param.getOrgId(), param.getOrgCode());
            param.setOrgCode(org.getOrganizationCode());
            param.setOrgName(org.getOrganizationName());
            // 1.7: 库存组织(置空)
            param.setOrganizationId(null);
            param.setOrganizationCode(null);
            param.setOrganizationName(null);
            // 1.8: 申请部门
            param.setCeeaDepartmentId(StringUtils.trimToNull(param.getCeeaDepartmentId()));
            param.setCeeaDepartmentName(StringUtils.trimToNull(param.getCeeaDepartmentName()));
            // 1.9: 申请日期
            param.setApplyDate(LocalDate.now());
            // 1.10: 申请人信息(service层不包含当前人信息，由上层处理)
            AssertUtils.notNull(param.getApplyById(), "缺少applyById信息");
            AssertUtils.notNull(param.getApplyBy(), "缺少applyBy信息");
            AssertUtils.notNull(param.getApplyByNickname(), "缺少applyByNickname信息");
            // 1.11: 物料大类(置空)
            param.setCategoryCode(StringUtils.trimToNull(param.getCategoryCode()));
            AssertUtils.isTrue(param.isTempSave() || param.getCategoryCode() != null, "请选择所属品类");
            if (param.getCategoryCode() != null) {
                PurchaseCategory category = context.getCategoryMap().get(param.getCategoryCode());
                AssertUtils.notNull(category, LocaleHandler.getLocaleMsg("所属品类[{0}]不存在"), param.getCategoryCode());
                AssertUtils.isTrue(Enable.Y.equals(category.getLastLevelFlag()), "所属品类[{0}]不是末级品类", param.getCategoryCode());
                param.setCategoryId(category.getCategoryId());
                param.setCategoryName(category.getCategoryName());
                // 23-12-21: 如果需求来源是月度/计划外，且品类的是否招标范围为是，且金额>=10万，需要校验，这个招标计划对应的公司，品类在品类分工里面，是否存在
                ExtPrSouRequirementHeadDTO inqParam = SouObjectXUtil.convertTargetObj(context.getParam(), ExtPrSouRequirementHeadDTO.class);
                if (!param.isTempSave()) {
                    boolean needCheck = (PrSouRequirementFromEnum.MONTH.name().equals(inqParam.getSouReqHead().getRequireFrom()) || PrSouRequirementFromEnum.WITHOUT_PLAN.name().equals(inqParam.getSouReqHead().getRequireFrom()))
                            && Enable.Y.name().equals(category.getX("ifBid"))
                            && inqParam.getSouReqHead().getTotalAmountByTenKilo() != null
                            && inqParam.getSouReqHead().getTotalAmountByTenKilo().compareTo(BigDecimal.TEN) >= 0;
                    if (needCheck) {
                        DivisionCategoryQueryDTO queryP = new DivisionCategoryQueryDTO(); {
                            queryP.setOrgIds(Collections.singletonList(param.getOrgId()));
                            queryP.setCategoryId(category.getCategoryId());
                            queryP.setIfMainPerson(Enable.Y.name());
                            queryP.setEnable(Enable.Y.name());
                        }
                        List<DivisionCategory> dcList = divisionCategoryService.listPageByParam(queryP).getList().stream()
                                // 生效的
                                .filter(e -> e.getEndDate() == null || !e.getEndDate().isBefore(LocalDate.now()))
                                // 主负责人
                                .filter(e -> Enable.Y.name().equals(e.getIfMainPerson()))
                                .collect(Collectors.toList());
                        AssertUtils.notEmpty(dcList, "该公司下的品类未维护品类分工，不允许提交，请到品类分工规则进行维护");
                        // 存在职责为 招标负责人/供应商负责人
                        DivisionCategory souDc = dcList.stream().filter(e -> "Person in charge of bidding".equals(e.getDuty())).findFirst().orElse(null);
                        AssertUtils.isTrue(souDc != null && souDc.getPersonInChargeUsername() != null, "该公司下的品类未维护品类分工，不允许提交，请到品类分工规则进行维护");
                        DivisionCategory vendorDc = dcList.stream().filter(e -> "Supplier Leader".equals(e.getDuty())).findFirst().orElse(null);
                        AssertUtils.isTrue(vendorDc != null && vendorDc.getPersonInChargeUsername() != null, "该公司下的品类未维护品类分工，不允许提交，请到品类分工规则进行维护");
                    }
                }
            }
            // 1.12: 预算管理
            param.setBudgetManagementId(null);
            param.setBudgetManagementNum(null);
            param.setTotalBudget(BigDecimal.ZERO);
            param.setUnusedBudget(BigDecimal.ZERO);
            param.setUsedBudget(BigDecimal.ZERO);
            // 1.13: 紧急情况说明(置空)
            param.setCeeaUrgencyExplain(null);
            // 1.14: 指定原因(置空)
            param.setCeeaAppointReason(null);
            // 1.15: 备注
            param.setComments(null);
            // 1.16: 审核状态
            if (param.isTempSave()) {
                param.setAuditStatus(context.getExistReqHead() != null ? context.getExistReqHead().getAuditStatus() : RequirementApproveStatus.DRAFT);
            } else {
                param.setAuditStatus(RequirementApproveStatus.DRAFT);
            }
            // 1.17:采购项目
            param.setPurchaseProject(null);
        }

        // 2: 实体类构造
        PrRequirementHead entity; {
            if (context.getExistReqHead() != null) {
                entity = context.getExistReqHead();
            } else {
                entity = new PrRequirementHead();
            }

            //noinspection unchecked
            SouObjectXUtil.mergePropertiesIgnoreFields(param, entity,
                    PrRequirementHead::getRequirementHeadId,
                    PrRequirementHead::getRequirementHeadNum,
                    PrRequirementHead::getAuditStatus);
            MqlCreateUpdateUtils.clearExtInMqlRelations(entity, PrRequirementHead.class.getSimpleName());

            if (context.getExistReqHead() == null) {
                // ID
                entity.setRequirementHeadId(IdGenrator.generate());
                // 联动行情编号
                entity.setRequirementHeadNum(baseClient.seqGen(param.getGenerateCode() != null ? param.getGenerateCode() : SequenceCodeConstant.SEQ_PMP_PR_APPLY_NUM));
            }
            // 执行状态
            entity.setAuditStatus(RequirementApproveStatus.DRAFT);

            SouObjectXUtil.mergeProperties(entity, param);
        }
        context.setReqHeadEntity(entity);

        return context;
    }

    @ApiOperation("校验及转化招标计划")
    public RequirementInitEditContext validateAndConvertPrSouHead(RequirementInitEditContext context) {
        PrSouRequirementInitEditContext souContext = (PrSouRequirementInitEditContext) context;
        ExtPrSouRequirementHeadDTO dto = SouObjectXUtil.convertTargetObj(souContext.getParam(), ExtPrSouRequirementHeadDTO.class);
        // 1: 数据校验
        ExtPrSouRequirementHead param = dto.getSouReqHead(); {
            if (param == null) {
                dto.setSouReqHead(new ExtPrSouRequirementHead());
                param = dto.getSouReqHead();
            }
            // 1.1: ID
            param.setRequirementHeadId(dto.getRequirementHeadId());
            // 1.2: 所属板块
            if (param.getOrgBuId() != null) {
                AssertUtils.notNull(param.getOrgBuId(), "请选择所属板块");
                Organization org = souContext.getOrganizationMap().get(param.getOrgBuId());
                AssertUtils.notNull(org, "所属板块[{0}]不存在", param.getOrgBuId());
                param.setOrgBuCode(org.getOrganizationCode());
                param.setOrgBuName(org.getOrganizationName());
            } else if (context.getParam().getOrgId() != null) {
                // 根据业务实体填补
                List<OrganizationRelation> orgRelationList = baseClient.queryByOrganizationId(context.getParam().getOrgId());
                if (CollectionUtils.isNotEmpty(orgRelationList)) {
                    List<Organization> orgList = qlOpenClient.query(ContextPath.BASE, QlOpenWrappers.query("base_organization_ide")
                                    .in(Organization::getOrganizationId, orgRelationList.stream().map(OrganizationRelation::getParentOrganizationId).filter(Objects::nonNull).collect(Collectors.toList())))
                            .stream().map(e -> SouObjectXUtil.convertTargetObj(e, Organization.class)).collect(Collectors.toList());
                    for (Organization orgBu : orgList) {
                        if (orgBu != null && "BU".equals(orgBu.getOrganizationTypeCode())) {
                            param.setOrgBuId(orgBu.getOrganizationId());
                            param.setOrgBuCode(orgBu.getOrganizationCode());
                            param.setOrgBuName(orgBu.getOrganizationName());
                            break;
                        }
                    }
                }
            }
            if (param.getOrgBuId() == null || param.getOrgBuCode() == null || param.getOrgBuName() == null) {
                AssertUtils.isTrue(context.getParam().isTempSave(), "缺少板块信息");
            }
            // 1.3: 需求来源
            param.setRequireFrom(StringUtils.trimToNull(param.getRequireFrom()));
            AssertUtils.notNull(param.getRequireFrom(), "请选择需求来源");
            // 1.4: 未报月度计划原因
            if (PrSouRequirementFromEnum.WITHOUT_PLAN.name().equals(param.getRequireFrom())) {
                // 需求来源-计划外
                param.setNoReportMonthPlanReason(StringUtils.trimToNull(param.getNoReportMonthPlanReason()));
                AssertUtils.isTrue(dto.isTempSave() || param.getNoReportMonthPlanReason() != null, "请填写未报月度计划原因");
                if (param.getNoReportMonthPlanReason() != null) {
                    AssertUtils.isTrue(param.getNoReportMonthPlanReason().length() <= 150, "未报月度计划原因输入长度不能超过150");
                }
            } else {
                param.setNoReportMonthPlanReason(null);
            }
            // 1.5: 项目名称
            param.setProjectName(StringUtils.trimToNull(param.getProjectName()));
            AssertUtils.isTrue(dto.isTempSave() || param.getProjectName() != null, "请输入项目名称");
            if (param.getProjectName() != null) {
                AssertUtils.isTrue(param.getProjectName().length() <= 80, "项目名称的输入长度不能超过80");
            }
            // 1.6: 月份
            AssertUtils.isTrue(dto.isTempSave() || param.getProjectMonth() != null, "请输入月份");
            if (param.getProjectMonth() != null) {
                AssertUtils.isTrue(checkYearMonth(param.getProjectMonth()), "月份输入错误");
            }
            // 1.7: 投资编号
            param.setInvestNo(StringUtils.trimToNull(param.getInvestNo()));
            if (param.getInvestNo() != null) {
                AssertUtils.isTrue(param.getInvestNo().length() <= 50, "投资编号的输入长度不能超过50");
            }
            // 1.8: 数量/规模
            AssertUtils.isTrue(dto.isTempSave() || param.getRequireQuantity() != null, "请填写数量/规模");
            // 1.9: 概算金额(万元)
            AssertUtils.isTrue(dto.isTempSave() || param.getTotalAmountByTenKilo() != null, "请填写概算金额(万元)");
            if (param.getTotalAmountByTenKilo() != null) {
                AssertUtils.isTrue(param.getTotalAmountByTenKilo().compareTo(BigDecimal.ZERO) > 0, "概算金额(万元)必须大于0");
            }
            // 1.10: 是否公示
            if (!PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(param.getRequireFrom())) {
                if (param.getNeedPublic() == null) {
                    param.setNeedPublic(Enable.N);
                }
            } else {
                // 特殊招标
                param.setNeedPublic(Enable.N);
            }
            // 1.11: 不公示理由
            if (Enable.N.equals(param.getNeedPublic()) && !PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(param.getRequireFrom())) {
                param.setNoPublicReason(StringUtils.trimToNull(param.getNoPublicReason()));
                AssertUtils.isTrue(dto.isTempSave() || param.getNoPublicReason() != null, "请填写不公示具体原因说明");
                if (param.getNoPublicReason() != null) {
                    AssertUtils.isTrue(param.getNoPublicReason().length() <= 150, "不公示具体原因说明的输入长度不能超过150");
                }
            } else {
                param.setNoPublicReason(null);
            }
            // 1.12: 不公示理由选择
            if (Enable.N.equals(param.getNeedPublic()) && !PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(param.getRequireFrom())) {
                param.setNoPublicReasonChoose(StringUtils.trimToNull(param.getNoPublicReasonChoose()));
                AssertUtils.isTrue(dto.isTempSave() || param.getNoPublicReasonChoose() != null, "请填写不公示原因");
            } else {
                param.setNoPublicReasonChoose(null);
            }
            // 1.13: 公示截止时间
            if (Enable.Y.equals(param.getNeedPublic())) {
                AssertUtils.isTrue(dto.isTempSave() || param.getPublicEndTime() != null, "请选择公示截止时间");
            } else {
                param.setPublicEndTime(null);
            }
            // 1.14: 项目所在地
            param.setProjectAddress(StringUtils.trimToNull(param.getProjectAddress()));
            AssertUtils.isTrue(dto.isTempSave() || param.getProjectAddress() != null, "请填写项目所在地");
            if (param.getProjectAddress() != null) {
                AssertUtils.isTrue(param.getProjectAddress().length() <= 150, "项目所在地的输入长度不能超过150");
            }
            // 1.15: 前置技术交流意向
            if (PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(param.getRequireFrom()) || PrSouRequirementFromEnum.WITHOUT_PLAN.name().equals(param.getRequireFrom())) {
                param.setPrefixTechDiscussion(Enable.N);
            } else {
                if (param.getPrefixTechDiscussion() == null) {
                    param.setPrefixTechDiscussion(Enable.N);
                }
            }
            // 1.16: 递交招标资料时间
            boolean needSetSendProfileEndDate = Enable.Y.equals(param.getPrefixTechDiscussion())
                    || (PrSouRequirementFromEnum.MONTH.name().equals(param.getRequireFrom()) && Enable.N.equals(param.getNeedPublic()))
                    || (PrSouRequirementFromEnum.WITHOUT_PLAN.name().equals(param.getRequireFrom()) && Enable.N.equals(param.getNeedPublic()));
            if (needSetSendProfileEndDate) {
                AssertUtils.isTrue(dto.isTempSave() || param.getSendSouProfileEndDate() != null, "请选择递交招标资料时间");
                if (param.getSendSouProfileEndDate() != null) {
                    AssertUtils.isTrue(param.getSendSouProfileEndDate().isAfter(LocalDate.now()), "递交招标资料时间必须晚于今天");
                }
            } else {
                param.setSendSouProfileEndDate(null);
            }
            // 1.17: 是否指定品牌/指定品牌文件
            if (PrSouRequirementFromEnum.MONTH.name().equals(param.getRequireFrom()) || PrSouRequirementFromEnum.WITHOUT_PLAN.name().equals(param.getRequireFrom())) {
                AssertUtils.isTrue(dto.isTempSave() || param.getIfAppointBrand() != null, "请选择是否指定品牌");
                if (Enable.Y.equals(param.getIfAppointBrand())) {
                    AssertUtils.notNull(param.getAppointBrandFileId(), "请上传指定品牌文件");
                    param.setAppointBrandFileName(StringUtils.trimToNull(param.getAppointBrandFileName()));
                    AssertUtils.notNull(param.getAppointBrandFileName(), "请上传指定品牌文件");
                    AssertUtils.isTrue(param.getAppointBrandFileName().length() <= 150, "指定品牌文件的名称长度不能超过150");
                }
            } else {
                param.setIfAppointBrand(Enable.N);
            }
            // 1.18: 是否限制单位/限制单位文件
            if (PrSouRequirementFromEnum.MONTH.name().equals(param.getRequireFrom()) || PrSouRequirementFromEnum.WITHOUT_PLAN.name().equals(param.getRequireFrom())) {
                AssertUtils.isTrue(dto.isTempSave() || param.getIfQualifyUnit() != null, "请选择是否限制单位");
                if (Enable.Y.equals(param.getIfQualifyUnit())) {
                    AssertUtils.notNull(param.getQualifyUnitFileId(), "请上传限制单位文件");
                    param.setQualifyUnitFileName(StringUtils.trimToNull(param.getQualifyUnitFileName()));
                    AssertUtils.notNull(param.getQualifyUnitFileName(), "请上传限制单位文件");
                    AssertUtils.isTrue(param.getQualifyUnitFileName().length() <= 150, "限制单位文件的名称长度不能超过150");
                }
            } else {
                param.setIfQualifyUnit(Enable.N);
            }
            // 1.19: 项目计划
            if (param.getProjectPlanId() != null) {
                AssertUtils.notNull(souContext.getProjectPlan(), "项目计划[{0}]不存在", param.getProjectPlanId());
                if (souContext.getProjectPlan() != null) {
                    AssertUtils.isTrue(ExtPrSouProjectPlanStatusEnum.NORMAL.name().equals(souContext.getProjectPlan().getPlanStatus()), "所选项目计划不是正常状态");
                    param.setPlanNo(souContext.getProjectPlan().getPlanNo());
                }
            } else {
                param.setPlanNo(null);
            }
            // 1.20: 特殊招标类型
            if (PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(param.getRequireFrom())) {
                // 特殊招标
                param.setSpecialSouType(StringUtils.trimToNull(param.getSpecialSouType()));
                AssertUtils.isTrue(dto.isTempSave() || param.getSpecialSouType() != null, "请选择特殊招标类型");
            } else {
                param.setSpecialSouType(null);
            }
            // 1.21: 特定原因
            if (PrSouRequirementSpecialSouTypeEnum.SPECIAL_VENDOR_ONE.name().equals(param.getSpecialSouType())) {
                // 特定原因使得供应商唯一
                param.setSpecialReason(StringUtils.trimToNull(param.getSpecialReason()));
                AssertUtils.isTrue(dto.isTempSave() || param.getSpecialReason() != null, "请填写特定原因");
            } else {
                param.setSpecialReason(null);
            }
            // 1.22: 需求产生时间
            if (PrSouRequirementSpecialSouTypeEnum.TIME_URGENT.name().equals(param.getSpecialSouType())) {
                // 时间紧急
                AssertUtils.isTrue(dto.isTempSave() || param.getRequireProductDate() != null, "请选择需求产生时间");
            } else {
                param.setRequireProductDate(null);
            }
            // 1.23: 需求产生时间附件
            if (PrSouRequirementSpecialSouTypeEnum.TIME_URGENT.name().equals(param.getSpecialSouType())) {
                // 时间紧急
                param.setRequireProductFileName(StringUtils.trimToNull(param.getRequireProductFileName()));
                if (!dto.isTempSave()) {
                    AssertUtils.notNull(param.getRequireProductFileId(), "请上传需求产生时间附件");
                    AssertUtils.notNull(param.getRequireProductFileName(), "请上传需求产生时间附件");
                    AssertUtils.isTrue(param.getRequireProductFileName().length() <= 150, "需求产生时间附件的名称长度不能超过150");
                }
            } else {
                param.setRequireProductFileId(null);
                param.setRequireProductFileName(null);
            }
            // 1.24: 工期交货期
            if (PrSouRequirementSpecialSouTypeEnum.TIME_URGENT.name().equals(param.getSpecialSouType())) {
                // 时间紧急
                AssertUtils.isTrue(dto.isTempSave() || param.getDeliveryDay() != null, "请输入工期交货期");
                if (param.getDeliveryDay() != null) {
                    AssertUtils.isTrue(param.getDeliveryDay().compareTo(BigDecimal.ZERO) > 0, "工期交货期必须大于0");
                    param.setDeliveryDay(param.getDeliveryDay().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros());
                }
            } else {
                param.setDeliveryDay(null);
            }
            // 1.25: 工期交货期附件
            if (PrSouRequirementSpecialSouTypeEnum.TIME_URGENT.name().equals(param.getSpecialSouType())) {
                // 时间紧急
                param.setDeliveryDayFileName(StringUtils.trimToNull(param.getDeliveryDayFileName()));
                if (!dto.isTempSave()) {
                    AssertUtils.notNull(param.getDeliveryDayFileId(), "请上传工期交货期附件");
                    AssertUtils.notNull(param.getDeliveryDayFileName(), "请上传工期交货期附件");
                    AssertUtils.isTrue(param.getRequireProductFileName().length() <= 150, "工期交货期附件的名称长度不能超过150");
                }
            } else {
                param.setDeliveryDayFileId(null);
                param.setDeliveryDayFileName(null);
            }
            // 1.26: 签合同用时
            if (PrSouRequirementSpecialSouTypeEnum.TIME_URGENT.name().equals(param.getSpecialSouType())) {
                // 时间紧急
                AssertUtils.isTrue(dto.isTempSave() || param.getSignContractDay() != null, "请输入签合同用时");
                if (param.getSignContractDay() != null) {
                    AssertUtils.isTrue(param.getSignContractDay().compareTo(BigDecimal.ZERO) > 0, "签合同用时必须大于0");
                    param.setSignContractDay(param.getSignContractDay().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros());
                }
            } else {
                param.setSignContractDay(null);
            }
            // 1.27: 投入使用时间
            if (PrSouRequirementSpecialSouTypeEnum.TIME_URGENT.name().equals(param.getSpecialSouType())) {
                // 时间紧急
                AssertUtils.isTrue(dto.isTempSave() || param.getPutIntoUseDate() != null, "请选择投入使用时间");
            } else {
                param.setPutIntoUseDate(null);
            }
            // 1.28: 投入使用时间附件
            if (PrSouRequirementSpecialSouTypeEnum.TIME_URGENT.name().equals(param.getSpecialSouType())) {
                // 时间紧急
                param.setPutIntoUseDateFileName(StringUtils.trimToNull(param.getPutIntoUseDateFileName()));
                if (!dto.isTempSave()) {
                    AssertUtils.notNull(param.getPutIntoUseDateFileId(), "请上传投入使用时间附件");
                    AssertUtils.notNull(param.getPutIntoUseDateFileName(), "请上传投入使用时间附件");
                    AssertUtils.isTrue(param.getPutIntoUseDateFileName().length() <= 150, "投入使用时间附件的名称长度不能超过150");
                }
            } else {
                param.setPutIntoUseDateFileId(null);
                param.setPutIntoUseDateFileName(null);
            }
            // 1.29: 其他特殊原因补充
            if (PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(param.getRequireFrom())) {
                // 特殊招标
                param.setOtherSpecialReason(StringUtils.trimToNull(param.getOtherSpecialReason()));
            } else {
                param.setOtherSpecialReason(null);
            }
            // 1.30: 剩余时间
            if (param.getPutIntoUseDate() != null && param.getRequireProductDate() != null && param.getDeliveryDay() != null && param.getSignContractDay() != null) {
                param.setRemainingDay(new BigDecimal(param.getRequireProductDate().until(param.getPutIntoUseDate(), ChronoUnit.DAYS))
                        .subtract(param.getDeliveryDay()).subtract(param.getSignContractDay()));
            } else {
                param.setRemainingDay(null);
            }
            // 1.31: 项目概况及范围
            param.setProjectOverview(StringUtils.trimToNull(param.getProjectOverview()));
            AssertUtils.isTrue(context.getParam().isTempSave() || param.getProjectOverview() != null, "请填写项目概况及范围");
            // 1.32: 技术要求
            param.setTechRequire(StringUtils.trimToNull(param.getTechRequire()));
            AssertUtils.isTrue(context.getParam().isTempSave() || param.getTechRequire() != null, "请填写技术要求");
            // 1.33: 业绩要求
            param.setPerformanceRequire(StringUtils.trimToNull(param.getPerformanceRequire()));
            AssertUtils.isTrue(context.getParam().isTempSave() || param.getPerformanceRequire() != null, "请填写业绩要求");
            // 1.34: 供应商资质要求
            param.setVendorQualificationRequire(StringUtils.trimToNull(param.getVendorQualificationRequire()));
            AssertUtils.isTrue(context.getParam().isTempSave() || param.getVendorQualificationRequire() != null, "请填写供应商资质要求");
            // 1.35: 是否已分配
            param.setHasAssigned(Enable.N);
            // 1.36: 是否已提交招标资料
            param.setHasSendSouProfile(Enable.N);
            // 1.37: 是否已创建寻源
            param.setHasCreateSou(Enable.N);
            // 1.38: 是否已创建寻源需求
            param.setHasCreateSouReq(Enable.N);
            // 1.39: 是否已创建供应商推荐
            param.setHasCreateVendorRecommend(Enable.N);
            // 1.40: 是否已提交
            param.setHasSubmit(context.getParam().isTempSave() ? Enable.N : Enable.Y);
            // 1.41: 意向金金额(万元)
            param.setEarnestMoney(null);
            // 1.42: 需求审批完成时间
            param.setApprovalPassTime(null);
            // 1.43: 变更来源计划ID(略)
            // 1.44: 变更后概算金额(万元)
            if (param.getChangeRequirementHeadId() != null) {
                AssertUtils.notNull(param.getAfterTotalAmountByTenKilo(), "请填写变更后概算金额(万元)");
                AssertUtils.isTrue(param.getAfterTotalAmountByTenKilo().compareTo(BigDecimal.ZERO) > 0, "变更后概算金额必须大于0");
            } else {
                param.setAfterTotalAmountByTenKilo(null);
            }
            // 1.45: 变更原因
            param.setChangeReason(StringUtils.trimToNull(param.getChangeReason()));
            if (param.getChangeRequirementHeadId() != null) {
                AssertUtils.notNull(param.getChangeReason(), "请填写变更原因");
            } else {
                param.setChangeReason(null);
            }
        }
        // 2: 数据转化
        ExtPrSouRequirementHead entity; {
            if (souContext.getExistPrSouHead() != null) {
                entity = souContext.getExistPrSouHead();
            } else {
                entity = new ExtPrSouRequirementHead();
            }
            //noinspection unchecked
            SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(param, entity,
                    ExtPrSouRequirementHead::getCreatedId,
                    ExtPrSouRequirementHead::getCreatedBy,
                    ExtPrSouRequirementHead::getCreatedByIp,
                    ExtPrSouRequirementHead::getCreatedFullName,
                    ExtPrSouRequirementHead::getCreatedUserName,
                    ExtPrSouRequirementHead::getCreationDate,
                    ExtPrSouRequirementHead::getTenantId,
                    ExtPrSouRequirementHead::getVersion);
            MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtPrSouRequirementHead.class.getSimpleName());

            SouObjectXUtil.mergeProperties(entity, param);
        }

        souContext.setSouReqHeadEntity(entity);
        return souContext;
    }

    private Boolean checkYearMonth(String yearMonth) {
        if (yearMonth == null) {
            return false;
        }
        return YEAR_MONTH_PATTERN.matcher(yearMonth).matches();
    }

    @ApiOperation("校验及转化工作成员")
    public RequirementInitEditContext validateAndConvertPrSouGroups(RequirementInitEditContext context) {
        PrSouRequirementInitEditContext souContext = (PrSouRequirementInitEditContext) context;
        ExtPrSouRequirementHeadDTO dto = SouObjectXUtil.convertTargetObj(souContext.getParam(), ExtPrSouRequirementHeadDTO.class);
        // 1: 数据校验
        List<ExtPrSouRequirementGroup> groupList = dto.getSouGroupList(); {
            if (CollectionUtils.isNotEmpty(groupList)) {
                // 过滤掉删除行
                groupList = groupList.stream().filter(e -> e.getX("$delete") == null).collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(groupList)) {
                AssertUtils.isTrue(dto.isTempSave(), "请选择技术负责人信息");
                return souContext;
            }

            groupList.removeIf(group -> {
                group.setUsername(StringUtils.trimToNull(group.getUsername()));
                return group.getUsername() == null;
            });
            Set<String> groupTypes = new HashSet<>(groupList.size());
            int index = 0;
            for (ExtPrSouRequirementGroup group : groupList) {
                index++;
                // 1.1: ID(略)
                // 1.2: 招标计划ID
                group.setRequirementHeadId(dto.getRequirementHeadId());
                // 1.3: 用户
                User user = souContext.getUserMap().get(group.getUsername());
                AssertUtils.notNull(user, "用户[{0}]不存在", group.getUsername());
                group.setUserId(user.getUserId());
                group.setFullName(user.getNickname());
                // 1.4: 工作职责
                group.setGroupType(StringUtils.trimToNull(group.getGroupType()));
                AssertUtils.notNull(group.getGroupType(), "缺少工作职责描述");
                AssertUtils.isTrue(groupTypes.add(group.getGroupType()), "同一工作职责下只能选择一位成员[{0}]", group.getGroupType());
                // 1.5: 联系方式
                group.setPhone(StringUtils.trimToNull(group.getPhone()));
                if (group.getPhone() != null) {
                    AssertUtils.isTrue(group.getPhone().length() <= 30, "工作成员联系方式的输入长度不能超过30");
                }
                // 1.6: 邮箱(暂无)
                // 1.7: 工作年限
                if (PrSouRequirementGroupTypeEnum.TECH.name().equals(group.getGroupType())) {
                    AssertUtils.isTrue(souContext.getParam().isTempSave() || group.getWorkYear() != null, "请填写技术负责人工作年限");
                    if (group.getWorkYear() != null) {
                        group.setWorkYear(group.getWorkYear().stripTrailingZeros());
                        AssertUtils.isTrue(group.getWorkYear().compareTo(new BigDecimal(3)) >= 0, "技术负责人工作年限应≥3年；技术负责人不允许参与评标");
                    }
                }
                // 1.8: 排序
                group.setSortIndex(index);
            }
            if (!dto.isTempSave()) {
                AssertUtils.isTrue(groupTypes.contains(PrSouRequirementGroupTypeEnum.TECH.name()), "请选择技术负责人");
            }
        }
        // 2: 数据转化
        List<ExtPrSouRequirementGroup> entityList = new ArrayList<>(groupList.size()); {
            for (ExtPrSouRequirementGroup group : groupList) {
                ExtPrSouRequirementGroup entity;
                ExtPrSouRequirementGroup existGroup = souContext.getExistPrSouGroupMap().get(group.getRequirementGroupId());
                if (existGroup != null) {
                    entity = existGroup;
                } else {
                    entity = new ExtPrSouRequirementGroup();
                }
                entityList.add(entity);
                //noinspection unchecked
                SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(group, entity,
                        ExtPrSouRequirementGroup::getRequirementGroupId,
                        ExtPrSouRequirementGroup::getCreatedId,
                        ExtPrSouRequirementGroup::getCreatedBy,
                        ExtPrSouRequirementGroup::getCreatedByIp,
                        ExtPrSouRequirementGroup::getCreatedFullName,
                        ExtPrSouRequirementGroup::getCreatedUserName,
                        ExtPrSouRequirementGroup::getCreationDate);
                MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtPrSouRequirementGroup.class.getSimpleName());
                if (existGroup == null) {
                    entity.setRequirementGroupId(IdGenrator.generate());
                }
                SouObjectXUtil.mergeProperties(entity, group);
            }
        }

        souContext.setSouGroupEntityList(entityList);
        return souContext;
    }

    @ApiOperation("校验及转化推荐供应商")
    public RequirementInitEditContext validateAndConvertPrSouVendors(RequirementInitEditContext context) {
        PrSouRequirementInitEditContext souContext = (PrSouRequirementInitEditContext) context;
        ExtPrSouRequirementHeadDTO dto = SouObjectXUtil.convertTargetObj(souContext.getParam(), ExtPrSouRequirementHeadDTO.class);
        // 1: 数据校验
        List<ExtPrSouRequirementVendor> vendorList = dto.getSouVendorList(); {
            if (CollectionUtils.isNotEmpty(vendorList)) {
                // 过滤掉删除行
                vendorList = vendorList.stream().filter(e -> e.getX("$delete") == null).collect(Collectors.toList());
            }
            PurchaseCategory category = null; {
                if (context.getReqHeadEntity().getCategoryCode() != null && context.getReqHeadEntity().getCategoryId() != null) {
                    category = new PurchaseCategory();
                    category.setCategoryCode(context.getReqHeadEntity().getCategoryCode());
                    category.setCategoryId(context.getReqHeadEntity().getCategoryId());
                    category = baseClient.getPurchaseCategoryByParm(category);
                }
            }
            // 特殊招标
            boolean notNeedVendors = PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(dto.getSouReqHead().getRequireFrom())
                    // 概算金额小于10万
                    || (dto.getSouReqHead().getTotalAmountByTenKilo() != null && dto.getSouReqHead().getTotalAmountByTenKilo().compareTo(BigDecimal.TEN) < 0)
                    // 品类不属于招标范围
                    || (category != null && !Enable.Y.name().equals(category.getX("ifBid")));
            // 不是特殊招标
            boolean needTowMoreVendors = !PrSouRequirementFromEnum.SPECIAL_SOU.name().equals(dto.getSouReqHead().getRequireFrom())
                    // 概算金额大于10万
                    && (dto.getSouReqHead().getTotalAmountByTenKilo() != null && dto.getSouReqHead().getTotalAmountByTenKilo().compareTo(BigDecimal.TEN) > 0)
                    // 品类属于招标范围
                    && (category != null && Enable.Y.name().equals(category.getX("ifBid")));
            if (CollectionUtils.isEmpty(vendorList)) {
                if (!dto.isTempSave() && needTowMoreVendors) {
                    AssertUtils.isTrue(vendorList.size() >= 2, "请推荐至少2家有效供应商");
                }
                return souContext;
            }

            int index = 0;
            for (ExtPrSouRequirementVendor vendor : vendorList) {
                index++;
                // 1.1: ID(略)
                // 1.2: 招标计划ID
                vendor.setRequirementHeadId(dto.getRequirementHeadId());
                // 1.3: 供应商ID(暂无)
                // 1.4: 供应商编码(暂无)
                // 1.5: 供应商名称
                vendor.setVendorName(StringUtils.trimToNull(vendor.getVendorName()));
                AssertUtils.isTrue(dto.isTempSave() || vendor.getVendorName() != null, "请填写推荐供应商名称");
                if (vendor.getVendorName() != null) {
                    AssertUtils.isTrue(vendor.getVendorName().length() <= 150, "推荐供应商名称输入长度不能超过150");
                }
                // 1.6: 联系人名称
                vendor.setContactName(StringUtils.trimToNull(vendor.getContactName()));
                AssertUtils.isTrue(dto.isTempSave() || vendor.getContactName() != null, "请填写联系人名称");
                if (vendor.getContactName() != null) {
                    AssertUtils.isTrue(vendor.getContactName().length() <= 50, "推荐供应商联系人名称的输入长度不能超过50");
                }
                // 1.6: 联系方式
                vendor.setPhone(StringUtils.trimToNull(vendor.getPhone()));
                AssertUtils.isTrue(dto.isTempSave() || vendor.getPhone() != null, "请填写推荐供应商联系方式");
                if (vendor.getPhone() != null) {
                    AssertUtils.isTrue(vendor.getPhone().length() <= 30, "推荐供应商联系方式输入长度不能超过30");
                }
                // 1.7: 邮箱
                vendor.setEmail(StringUtils.trimToNull(vendor.getEmail()));
                AssertUtils.isTrue(dto.isTempSave() || vendor.getEmail() != null, "请填写推荐供应商邮箱");
                if (vendor.getEmail() != null) {
                    AssertUtils.isTrue(vendor.getEmail().length() <= 100, "推荐供应商邮箱的输入长度不能超过100");
                }
                // 1.8: 推荐来源
                vendor.setRecommendFrom(StringUtils.trimToNull(vendor.getRecommendFrom()));
                AssertUtils.isTrue(dto.isTempSave() || vendor.getRecommendFrom() != null, "请填写推荐来源");
                if (vendor.getRecommendFrom() != null) {
                    AssertUtils.isTrue(vendor.getRecommendFrom().length() <= 30, "推荐供应商推荐来源的输入长度不能超过30");
                }
                // 1.9: 排序
                vendor.setSortIndex(index);
            }
        }
        // 2: 数据转化
        List<ExtPrSouRequirementVendor> entityList = new ArrayList<>(vendorList.size()); {
            for (ExtPrSouRequirementVendor vendor : vendorList) {
                ExtPrSouRequirementVendor entity;
                ExtPrSouRequirementVendor existVendor = souContext.getExistPrSouVendorMap().get(vendor.getRequirementVendorId());
                if (existVendor != null) {
                    entity = existVendor;
                } else {
                    entity = new ExtPrSouRequirementVendor();
                }
                entityList.add(entity);
                //noinspection unchecked
                SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(vendor, entity,
                        ExtPrSouRequirementVendor::getRequirementVendorId,
                        ExtPrSouRequirementVendor::getCreatedId,
                        ExtPrSouRequirementVendor::getCreatedBy,
                        ExtPrSouRequirementVendor::getCreatedByIp,
                        ExtPrSouRequirementVendor::getCreatedFullName,
                        ExtPrSouRequirementVendor::getCreatedUserName,
                        ExtPrSouRequirementVendor::getCreationDate);
                MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtPrSouRequirementVendor.class.getSimpleName());
                if (existVendor == null) {
                    entity.setRequirementVendorId(IdGenrator.generate());
                }
                SouObjectXUtil.mergeProperties(entity, vendor);
            }
        }

        souContext.setSouVendorEntityList(entityList);
        return souContext;
    }

    @ApiOperation("校验及转化附件")
    public RequirementInitEditContext validateAndConvertPrSouAttaches(RequirementInitEditContext context) {
        PrSouRequirementInitEditContext souContext = (PrSouRequirementInitEditContext) context;
        ExtPrSouRequirementHeadDTO dto = SouObjectXUtil.convertTargetObj(souContext.getParam(), ExtPrSouRequirementHeadDTO.class);
        // 1: 数据校验
        List<ExtPrSouRequirementAttach> attachList = dto.getSouAttachList(); {
            if (CollectionUtils.isNotEmpty(attachList)) {
                //过滤删除字段
                attachList = attachList.stream().filter(e -> e.getX("$delete") == null).collect(Collectors.toList());
            }
            int index = 0;
            for (ExtPrSouRequirementAttach attach : attachList) {
                index++;
                // 1.1: ID(略)
                // 1.2: 招标计划ID
                attach.setRequirementHeadId(dto.getRequirementHeadId());
                // 1.3: 文件类型
                attach.setFileType(null);
                // 1.4: 文件ID/名称
                attach.setFileName(StringUtils.trimToNull(attach.getFileName()));
                if (attach.getFileName() != null) {
                    AssertUtils.isTrue(attach.getFileName().length() <= 150, "附件名称的长度不能超过150");
                } else {
                    AssertUtils.isTrue(dto.isTempSave(), "请上传附件文件");
                }
                // 1.5: 上传时间(置空)
                attach.setUpdateDate(null);
                // 1.6: 排序
                attach.setSortIndex(index);
            }
        }
        // 2: 数据转化
        List<ExtPrSouRequirementAttach> entityList = new ArrayList<>(attachList.size()); {
            for (ExtPrSouRequirementAttach attach : attachList) {
                ExtPrSouRequirementAttach entity;
                ExtPrSouRequirementAttach existAttach = souContext.getExistPrSouAttachMap().get(attach.getRequirementAttachId());
                if (existAttach != null) {
                    entity = existAttach;
                } else {
                    entity = new ExtPrSouRequirementAttach();
                }
                entityList.add(entity);
                //noinspection unchecked
                SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(attach, entity,
                        ExtPrSouRequirementAttach::getRequirementAttachId,
                        ExtPrSouRequirementAttach::getUpdateDate,
                        ExtPrSouRequirementAttach::getCreatedId,
                        ExtPrSouRequirementAttach::getCreatedBy,
                        ExtPrSouRequirementAttach::getCreatedByIp,
                        ExtPrSouRequirementAttach::getCreatedFullName,
                        ExtPrSouRequirementAttach::getCreatedUserName,
                        ExtPrSouRequirementAttach::getCreationDate);
                MqlCreateUpdateUtils.removeExtPropsInMqlRelations(entity, ExtPrSouRequirementAttach.class.getSimpleName());
                if (existAttach == null) {
                    entity.setRequirementAttachId(IdGenrator.generate());
                    entity.setUpdateDate(LocalDate.now());
                }
                SouObjectXUtil.mergeProperties(entity, attach);
            }
        }

        souContext.setSouAttachEntityList(entityList);
        return souContext;
    }

    @Override
    public int getOrder() {
        return 10;
    }

}
