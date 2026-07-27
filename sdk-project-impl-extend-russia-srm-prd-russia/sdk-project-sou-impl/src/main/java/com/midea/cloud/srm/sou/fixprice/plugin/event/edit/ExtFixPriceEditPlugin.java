package com.midea.cloud.srm.sou.fixprice.plugin.event.edit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceHeadDTO;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceFile;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPricePaymentMethodEnum;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceStatusEnum;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouProject;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouVendor;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouProjectStatusEnum;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouWinStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceFileDAO;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceHeadDAO;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceLineDAO;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouItemDAO;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouProjectDAO;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouProjectDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouVendorDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 100014337
 */
@Component
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtFixPriceEditPlugin {

    @Autowired
    private ExtFixPriceHeadDAO extFixPriceHeadDAO;
    @Autowired
    private ExtFixPriceLineDAO extFixPriceLineDAO;
    @Autowired
    private ExtFixPriceFileDAO extFixPriceFileDAO;
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private SouProjectDAO souProjectDAO;
    @Autowired
    private SouItemDAO souItemDAO;
    @Autowired
    private InqSouItemDAO inqSouItemDAO;
    @Autowired
    private InqSouOrderItemDAO inqSouOrderItemDAO;
    @Autowired
    private SouVendorDAO souVendorDAO;
    @Autowired
    private InqSouProjectDAO inqSouProjectDAO;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @ApiModelProperty("校验操作条件/权限")
    public void judgeEdit(ExtFixPriceEditContext context) {
        if (context.getParam().getFixPriceHeadId() != null) {
            ExtFixPriceHead existFixPrice = extFixPriceHeadDAO.getById(context.getParam().getFixPriceHeadId());
            AssertUtils.notNull(existFixPrice, "定价单[{0}]不存在", context.getParam().getFixPriceHeadId());
            switch (existFixPrice.getFixPriceStatus()) {
                // 拟定
                case "DRAFT":
                    // 已驳回
                case "REJECTED":
                    // 已撤回
                case "WITHDRAW":
                    break;
                // 审批中
                case "SUBMITTED":
                    throw new IllegalArgumentException("单据审批中，禁止编辑");
                    // 已废弃
                case "ABANDONED":
                    throw new IllegalArgumentException("单据已废弃，禁止编辑");
                    // 已审批
                case "APPROVED":
                    throw new IllegalArgumentException("单据已审批，禁止编辑");
                default:
                    break;
            }
            context.setExistExtFixPriceHead(existFixPrice);
            context.setExistExtFixPriceLineList(extFixPriceLineDAO.list(ExtFixPriceLine::getFixPriceHeadId, context.getParam().getFixPriceHeadId()));
        }
    }

    @ApiModelProperty("编辑校验及实体类生成")
    public void validateAndConvertEntity(ExtFixPriceEditContext context) {
        this.validateAndConvertFixPriceHead(context);
        this.validateAndConvertFixPriceLines(context);
        this.validateAndConvertFixPriceFiles(context);
    }

    @ApiModelProperty("处理定价单头信息")
    private void validateAndConvertFixPriceHead(ExtFixPriceEditContext context) {
        // 1: 数据校验
        ExtFixPriceHeadDTO param = context.getParam(); {
            // 1.1: ID
            if (param.getFixPriceHeadId() == null) {
                param.setFixPriceHeadId(IdGenrator.generate());
            }
            // 1.2: 定价单号
            // TODO
            param.setFixPriceNo(context.getExistExtFixPriceHead() != null ? context.getExistExtFixPriceHead().getFixPriceNo() : baseClient.seqGen("abc"));
            // 1.3: 申请日期
            AssertUtils.isTrue(param.getTempSave() || param.getFixPriceDate() != null, "请选择申请日期");
            // 1.4: 采购部门
            if (param.getOrgDepName() == null) {
                AssertUtils.isTrue(param.getTempSave(), "请选择采购部门");
            }
            // 1.5: 定价状态
            param.setFixPriceStatus(context.getExistExtFixPriceHead() != null ? context.getExistExtFixPriceHead().getFixPriceStatus() : ExtFixPriceStatusEnum.DRAFT.name());
            // 1.6: 总金额(物料明细校验后填补)
            // 1.7: 价税合计(物料明细校验后填补)
            // 1.8: 备注
            param.setRemark(StringUtils.trimToNull(param.getRemark()));
            // 1.9: 是否可提交
            param.setCanSubmit(param.getTempSave() ? Enable.N : Enable.Y);
            // 1.10: 创建人所在公司
            // 写入创建人所在公司信息
            HrUserOrgnizationDto userOrgnizationDto = pjProjectExtClient.getHrUserOrgnizationByUsername(AppUserUtil.getLoginAppUser().getCeeaEmpNo());
            if (userOrgnizationDto == null || userOrgnizationDto.getOuOrganization() == null || userOrgnizationDto.getBuOrganization() == null) {
                throw new IllegalArgumentException("查询采购员hr信息失败");
            }
            param.setCreateUserOrgOuId(userOrgnizationDto.getOuOrganization().getOrganizationId());
            param.setCreateUserOrgOuCode(userOrgnizationDto.getOuOrganization().getOrganizationCode());
            param.setCreateUserOrgOuName(userOrgnizationDto.getOuOrganization().getOrganizationName());
            param.setCreateUserOrgBuId(userOrgnizationDto.getBuOrganization().getOrganizationId());
            param.setCreateUserOrgBuCode(userOrgnizationDto.getBuOrganization().getOrganizationCode());
            param.setCreateUserOrgBuName(userOrgnizationDto.getBuOrganization().getOrganizationName());
        }
        // 2: 数据转换
        ExtFixPriceHead entity = new ExtFixPriceHead();
        //noinspection unchecked
        SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(param, entity,
                ExtFixPriceHead::getCreatedId,
                ExtFixPriceHead::getCreatedBy,
                ExtFixPriceHead::getCreationDate,
                ExtFixPriceHead::getCreatedByIp,
                ExtFixPriceHead::getCreatedFullName,
                ExtFixPriceHead::getTenantId,
                ExtFixPriceHead::getVersion);
        context.setFixPriceHeadEntity(entity);
    }

    private void loadDataForValidate(ExtFixPriceEditContext context) {
        Map<Long/* orderItemId */, SouOrderItem> orderItemMap = Collections.emptyMap();
        Map<Long/* orderItem */, InqSouOrderItem> inqOrderItemMap = Collections.emptyMap();
        Map<Long/* souItemId */, SouItem> souItemMap = Collections.emptyMap();
        Map<Long/* souItemId */, InqSouItem> inqSouItemMap = Collections.emptyMap();
        Map<Long/* projectId */, SouProject> souProjectMap = Collections.emptyMap();
        Map<Long/* projectId */, InqSouProject> inqProjectMap = Collections.emptyMap();
        Map<String/* projectId_vendorId */, SouVendor> souVendorMap = Collections.emptyMap();
        Map<Long/* souItemId */, Set<Long/* requirementLineId */>> orderItemReqLineMap = new HashMap<>(30);
        Map<Long/* requirementLineId */, RequirementLine> reqLineMap = Collections.emptyMap();
        Map<Long/* requirementHeadId */, RequirementHead> reqHeadMap = Collections.emptyMap(); {
            Set<Long> orderItemIds = new HashSet<>(context.getParam().getLineList().size());
            Set<Long> requirementLineIds = new HashSet<>(context.getParam().getLineList().size());
            context.getParam().getLineList().forEach(line -> {
                AssertUtils.notNull(line.getSourceFromType(), "缺少sourceFromType参数");
                line.setSourceFromLineId(line.getSourceFromLineId());
                AssertUtils.notNull(line.getSourceFromLineId(), "缺少sourceFromLineId参数");
                if (ExtFixPriceSourceFromTypeEnum.INQ.equals(line.getSourceFromType())) {
                    AssertUtils.isTrue(orderItemIds.add(Long.valueOf(line.getSourceFromLineId())), "请勿重复添加询比价物资信息");
                } else if (ExtFixPriceSourceFromTypeEnum.PURCHASE_REQ.equals(line.getSourceFromType())) {
                    AssertUtils.isTrue(requirementLineIds.add(Long.valueOf(line.getSourceFromLineId())), "请勿重复添加近期采购物资信息");
                }
            });
            if (!orderItemIds.isEmpty()) {
                orderItemMap = souOrderItemDAO.listByIds(orderItemIds).stream().collect(Collectors.toMap(SouOrderItem::getOrderItemId, Function.identity()));
                if (!orderItemMap.isEmpty()) {
                    souProjectMap = souProjectDAO.listByIds(orderItemMap.values().stream().map(SouOrderItem::getProjectId).collect(Collectors.toSet()))
                            .stream().collect(Collectors.toMap(SouProject::getProjectId, Function.identity()));
                    inqProjectMap = inqSouProjectDAO.listByIds(souProjectMap.keySet()).stream().collect(Collectors.toMap(InqSouProject::getProjectId, Function.identity()));
                    souVendorMap = souVendorDAO.lambdaQuery().in(SouVendor::getProjectId, souProjectMap.keySet()).list()
                            .stream().collect(Collectors.toMap(e -> e.getProjectId() + "_" + e.getVendorId(), Function.identity()));
                    inqOrderItemMap = inqSouOrderItemDAO.listByIds(orderItemMap.keySet()).stream().collect(Collectors.toMap(InqSouOrderItem::getOrderItemId, Function.identity()));
                    souItemMap = souItemDAO.listByIds(orderItemMap.values().stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet()))
                            .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
                    inqSouItemMap = inqSouItemDAO.listByIds(souItemMap.keySet()).stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));

                    for (InqSouItem inqSouItem : inqSouItemMap.values()) {
                        if (StringUtils.isNotBlank(inqSouItem.getExtSourceFromLineIds())) {
                            Set<Long> ids = SouObjectXUtil.convertTargetObj(Arrays.asList(inqSouItem.getExtSourceFromLineIds().split(",")), new TypeReference<Set<Long>>() {});
                            requirementLineIds.addAll(ids);
                            orderItemReqLineMap.put(inqSouItem.getSouItemId(), ids);
                        }
                    }
                }
            }
            if (!requirementLineIds.isEmpty()) {
                reqLineMap = SouObjectXUtil.convertList(qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementLine")
                                .in(RequirementLine::getRequirementLineId, new ArrayList<>(requirementLineIds))), RequirementLine.class)
                        .stream().collect(Collectors.toMap(RequirementLine::getRequirementLineId, Function.identity()));
                if (!reqLineMap.isEmpty()) {
                    reqHeadMap = SouObjectXUtil.convertList(qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementHead")
                                    .in(RequirementHead::getRequirementHeadId, new ArrayList<>(reqLineMap.values().stream().map(RequirementLine::getRequirementHeadId).collect(Collectors.toSet())))), RequirementHead.class)
                            .stream().collect(Collectors.toMap(RequirementHead::getRequirementHeadId, Function.identity()));
                }
            }
        }
        context.setOrderItemMap(orderItemMap);
        context.setInqOrderItemMap(inqOrderItemMap);
        context.setSouItemMap(souItemMap);
        context.setInqSouItemMap(inqSouItemMap);
        context.setSouProjectMap(souProjectMap);
        context.setInqProjectMap(inqProjectMap);
        context.setSouVendorMap(souVendorMap);
        context.setReqLineMap(reqLineMap);
        context.setReqHeadMap(reqHeadMap);
        context.setOrderItemReqLineMap(orderItemReqLineMap);
    }

    @ApiModelProperty("处理定价单物料明细")
    private void validateAndConvertFixPriceLines(ExtFixPriceEditContext context) {
        if (CollectionUtils.isEmpty(context.getParam().getLineList())) {
            AssertUtils.isTrue(context.getParam().getTempSave(), "请添加物资明细");
            return;
        }
        this.loadDataForValidate(context);

        Map<Long/* fixPriceLineId */, ExtFixPriceLine> existFixPriceLineMap = Collections.emptyMap(); {
            Set<Long> priceLineIds = context.getParam().getLineList().stream().map(ExtFixPriceLine::getFixPriceLineId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!priceLineIds.isEmpty()) {
                existFixPriceLineMap = extFixPriceLineDAO.listByIds(priceLineIds).stream().collect(Collectors.toMap(ExtFixPriceLine::getFixPriceLineId, Function.identity()));
            }
        }

        // 1: 数据校验
        List<ExtFixPriceLine> lineList = context.getParam().getLineList(); {
            int index = 0;
            BigDecimal totalNotaxPrice = BigDecimal.ZERO;
            BigDecimal totalTaxPrice = BigDecimal.ZERO;
            BigDecimal highestTaxPrice = BigDecimal.ZERO;
            for (ExtFixPriceLine line : lineList) {
                ExtFixPriceLine existPriceLine = existFixPriceLineMap.get(line.getFixPriceLineId());
                boolean isClose = Enable.Y.equals(line.getHasClosed());

                // 1.1: ID
                line.setFixPriceLineId(existPriceLine != null ? existPriceLine.getFixPriceLineId() : IdGenrator.generate());
                // 1.2: 定价单ID
                line.setFixPriceHeadId(context.getFixPriceHeadEntity().getFixPriceHeadId());
                // 1.3: 定价单号
                line.setFixPriceNo(context.getFixPriceHeadEntity().getFixPriceNo());
                // 1.4: 来源类型
                AssertUtils.notNull(line.getSourceFromType(), "缺少sourceFromType参数");
                // 1.5: 来源单据明细ID
                line.setSourceFromLineId(StringUtils.trimToNull(line.getSourceFromLineId()));
                AssertUtils.notNull(line.getSourceFromLineId(), "缺少sourceFromLineId参数");
                // 1.6: 付款方式
                line.setPaymentMethod(StringUtils.trimToNull(line.getPaymentMethod()));
                AssertUtils.isTrue(isClose || context.getParam().getTempSave() || line.getPaymentMethod() != null, "请选择付款方式");
                if (line.getPaymentMethod() != null) {
                    Map<String/* dictItemCode */, DictItemDTO> paymentMethods = baseClient.listByDictCode(Collections.singletonList(ExtFixPricePaymentMethodEnum.getDictCode()))
                            .stream().collect(Collectors.toMap(DictItemDTO::getDictItemCode, Function.identity()));
                    DictItemDTO dictItem = paymentMethods.get(line.getPaymentMethod());
                    AssertUtils.notNull(dictItem, "付款方式[{0}]字典值不存在", line.getPaymentMethod());
                    AssertUtils.isFalse(dictItem.getDisabled(), "该付款方式已禁用");
                }
                // 1.7: 付款条款
                line.setPaymentTerm(StringUtils.trimToNull(line.getPaymentTerm()));
                AssertUtils.isTrue(isClose || context.getParam().getTempSave() || line.getPaymentTerm() != null, "请填写付款条款");
                if (line.getPaymentTerm() != null) {
                    AssertUtils.isTrue(line.getPaymentTerm().length() <= 100, "付款条款的输入长度不能超过100");
                }
                // 1.8: 预付款说明
                if (line.getAdvancePaymentRemark() == null) {
                    line.setAdvancePaymentRemark(Enable.N);
                }
                // 1.9: 是否签订合同
                if (line.getHasSignedContract() == null) {
                    line.setHasSignedContract(Enable.N);
                }
                // 1.10: 处理其余字段
                if (ExtFixPriceSourceFromTypeEnum.INQ.equals(line.getSourceFromType())) {
                    this.doValidateFixPriceLineFromInq(context, line);
                } else if (ExtFixPriceSourceFromTypeEnum.PURCHASE_REQ.equals(line.getSourceFromType())) {
                    line.setExtOrderCount(1);
                    this.doValidateFixPriceLineFromPurchaseReq(context, line);
                }

                totalNotaxPrice = totalNotaxPrice.add(line.getNotaxTotalPrice() != null ? line.getNotaxTotalPrice() : BigDecimal.ZERO);
                if (line.getNotaxTotalPrice() != null && line.getTaxRate() != null) {
                    if (line.getTaxRate().compareTo(BigDecimal.ZERO) == 0) {
                        totalTaxPrice = totalTaxPrice.add(line.getNotaxTotalPrice());
                    } else {
                        totalTaxPrice = totalTaxPrice.add(line.getNotaxTotalPrice().multiply(BigDecimal.ONE.add(line.getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))));
                    }
                }
                if (line.getNotaxPrice() != null && line.getTaxRate() != null) {
                    BigDecimal highest;
                    if (line.getTaxRate().compareTo(BigDecimal.ZERO) == 0) {
                        highest = line.getNotaxPrice();
                    } else {
                        highest = line.getNotaxPrice().multiply(BigDecimal.ONE.add(line.getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP)));
                    }
                    if (highest.compareTo(highestTaxPrice) > 0) {
                        highestTaxPrice = highest;
                    }
                }

                line.setSortIndex(index++);
            }

            context.getFixPriceHeadEntity().setTotalNotaxPrice(totalNotaxPrice);
            context.getFixPriceHeadEntity().setTotalTaxPrice(totalTaxPrice);
            context.getFixPriceHeadEntity().setHighestTaxPrice(highestTaxPrice.compareTo(BigDecimal.ZERO) > 0 ? highestTaxPrice : null);
        }
        // 2: 数据转换
        List<ExtFixPriceLine> entityList = new ArrayList<>(lineList.size()); {
            for (ExtFixPriceLine line : lineList) {
                ExtFixPriceLine entity = new ExtFixPriceLine();
                //noinspection unchecked
                SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(line, entity,
                        ExtFixPriceLine::getCreatedId,
                        ExtFixPriceLine::getCreatedBy,
                        ExtFixPriceLine::getCreationDate,
                        ExtFixPriceLine::getCreatedByIp,
                        ExtFixPriceLine::getCreatedFullName,
                        ExtFixPriceLine::getTenantId,
                        ExtFixPriceLine::getVersion);

                entityList.add(entity);
            }
        }
        context.setFixPriceLineEntityList(entityList);
    }

    @ApiOperation("校验来源于询比价的物资明细")
    private void doValidateFixPriceLineFromInq(ExtFixPriceEditContext context, ExtFixPriceLine line) {
        long sourceFromLineId = Long.parseLong(line.getSourceFromLineId());
        SouOrderItem orderItem = context.getOrderItemMap().get(sourceFromLineId);
        AssertUtils.notNull(orderItem, "询比价报价明细[{0}]不存在", sourceFromLineId);
        SouProject souProject = context.getSouProjectMap().get(orderItem.getProjectId());
        InqSouProject inqSouProject = context.getInqProjectMap().get(orderItem.getProjectId());
        AssertUtils.isTrue(SouProjectStatusEnum.PRICE_END.equals(souProject.getProjectStatus()), "询价单[{0}]未询价结束", souProject.getSouNo());
        AssertUtils.isTrue(SouWinStatusEnum.Y.equals(orderItem.getWinStatus()), "询价单[{0}]所选的报价明细不是中标供应商", souProject.getSouNo());
        InqSouOrderItem inqOrderItem = context.getInqOrderItemMap().get(sourceFromLineId);
        SouItem souItem = context.getSouItemMap().get(orderItem.getSouItemId());
        InqSouItem inqSouItem = context.getInqSouItemMap().get(orderItem.getSouItemId());
        SouVendor souVendor = context.getSouVendorMap().get(orderItem.getProjectId() + "_" + orderItem.getVendorId());

        List<RequirementLine> reqLineList = Collections.emptyList();
        RequirementHead reqHead = null;
        RequirementLine minPriceReqLine = null; {
            Set<Long/* requirementLineId */> reqLineIds = context.getOrderItemReqLineMap().get(orderItem.getSouItemId());
            if (CollectionUtils.isNotEmpty(reqLineIds)) {
                reqLineList = new ArrayList<>(reqLineIds.size()); {
                    for (Long reqLineId : reqLineIds) {
                        RequirementLine reqLine = context.getReqLineMap().get(reqLineId);
                        if (reqLine != null) { reqLineList.add(reqLine); }
                    }
                    reqHead = reqLineList.isEmpty() ? null : context.getReqHeadMap().get(reqLineList.get(0).getRequirementHeadId());
                    // 按照最低价供应商进行排序
                    List<RequirementLine> tempReqLineList = reqLineList.stream()
                            .filter(e -> e.getX("extHistoryVendorPrice1") != null)
                            .sorted((a, b) -> {
                                BigDecimal aVendorMinPrice = new BigDecimal(StringUtils.trimToNull(a.getX("extHistoryVendorPrice1").toString()));
                                BigDecimal bVendorMinPrice = new BigDecimal(StringUtils.trimToNull(b.getX("extHistoryVendorPrice1").toString()));
                                return aVendorMinPrice.compareTo(bVendorMinPrice);
                            })
                            .collect(Collectors.toList());
                    minPriceReqLine = tempReqLineList.isEmpty() ? null : tempReqLineList.get(0);
                }
            }
        }

        // 1: 来源单据ID
        line.setSourceFromId(souProject.getProjectId().toString());
        // 2: 来源单据编号
        line.setSourceFromNo(souProject.getSouNo());
        line.setRequirementLineIds(inqSouItem.getExtSourceFromLineIds());
        // 3: 业务实体
        line.setOrgOuId(souItem.getOrgOuId());
        line.setOrgOuCode(souItem.getOrgOuCode());
        line.setOrgOuName(souItem.getOrgOuName());
        // 4: 物料
        line.setItemId(souItem.getItemId());
        line.setItemCode(souItem.getItemCode());
        line.setItemDesc(souItem.getItemDesc());
        // 5: 物料规格型号
        line.setExtMaterialModel(inqSouItem.getExtMaterialModel());
        // 6: 单位
        line.setUnit(souItem.getUnit());
        // 7: 数量
        line.setQuantity(souItem.getRequireQuantity());
        // 8: 品牌
        line.setBrand(inqSouItem.getExtBrand());
        // 9: 供应商
        line.setVendorId(souVendor.getVendorId());
        line.setVendorCode(souVendor.getVendorCode());
        line.setVendorName(souVendor.getVendorName());
        // 10: 币种
        line.setCurrencyCode(orderItem.getOrderCurrency());
        // 11: 未税单价
        line.setNotaxPrice(orderItem.getStandardNotaxPrice());
        // 12: 税率
        line.setTaxKey(orderItem.getTaxKey());
        line.setTaxRate(orderItem.getTaxRate());
        // 13: 未税总价
        line.setNotaxTotalPrice(orderItem.getStandardNotaxPrice().multiply(orderItem.getRequireQuantity()).setScale(4, RoundingMode.HALF_UP));
        // 14: 近期最低价格(未税)
        if (minPriceReqLine != null) {
            Object v = minPriceReqLine.getX("extHistoryVendorPrice1");
            if (v != null) {
                String latestMinNotaxPrice = StringUtils.trimToNull(v.toString());
                if (latestMinNotaxPrice != null) {
                    line.setLatestMinNotaxPrice(new BigDecimal(latestMinNotaxPrice));
                }
            }
        }
        // 15: 浮动比例
        if (line.getNotaxPrice() != null && line.getLatestMinNotaxPrice()!= null) {
            line.setPriceFloatScale(line.getNotaxPrice().subtract(line.getLatestMinNotaxPrice()).divide(line.getLatestMinNotaxPrice(), 2, RoundingMode.HALF_UP));
        }
        // 16: 近期最低供应商
        if (minPriceReqLine != null) {
            line.setLatestMinVendorId(null);
            line.setLatestMinVendorCode(StringUtils.trimToNull(minPriceReqLine.getX("extHistoryVendorCode1")));
            line.setLatestMinVendorName(StringUtils.trimToNull(minPriceReqLine.getX("extHistoryVendorName1")));
        }
        // 17: 近期最低供品牌
        line.setBrand(minPriceReqLine != null ? minPriceReqLine.getBrand() : null);
        // 18: 中标原因
        line.setExtWinReason(inqOrderItem.getExtWinReason());
        // 19: 供应商备注
        line.setOrderRemark(orderItem.getOrderRemark());
        // 20: 预付款说明
        line.setAdvancePaymentRemark(inqOrderItem.getAdvancePaymentRemark());
        // 21: 特殊付款说明
        line.setSpecialPaymentRemark(inqOrderItem.getSpecialPaymentRemark());
        // 22: 申请单位
        line.setOrgOuId(souItem.getOrgOuId());
        line.setOrgOuCode(souItem.getOrgOuCode());
        line.setOrgOuName(souItem.getOrgOuName());
        // 23: 供货周期
        line.setExtLeadTime(inqOrderItem.getExtLeadTime());
        // 24: 采购员账号
        line.setBuyerUsername(souProject.getCreatedBy());
        line.setBuyerNickname(souProject.getCreatedFullName());
        // 25: 申请类型
        line.setApplyType(reqHead != null ? reqHead.getCeeaPrType() : null);
        // 26: 质保期
        line.setExtWarrantyPeriod(inqOrderItem.getExtWarrantyPeriod());
        // 27: 预估单价
        BigDecimal predictNotaxPrice = BigDecimal.ZERO;
        for (RequirementLine reqLine : reqLineList) {
            Object v = reqLine.getX("extPredictPrice");
            if (v != null) {
                String extPredictPrice = StringUtils.trimToNull(v.toString());
                if (extPredictPrice != null) {
                    predictNotaxPrice = predictNotaxPrice.add(new BigDecimal(extPredictPrice));
                }
            }
        }
        line.setExtPredictPrice(predictNotaxPrice);
        // 28: 预估总价
        BigDecimal totalPredictPrice = BigDecimal.ZERO;
        for (RequirementLine reqLine : reqLineList) {
            Object v = reqLine.getX("extPredictAmount");
            if (v != null) {
                String extPredictAmount = StringUtils.trimToNull(v.toString());
                if (extPredictAmount != null) {
                    totalPredictPrice = totalPredictPrice.add(new BigDecimal(extPredictAmount));
                }
            }
        }
        line.setExtPredictAmount(totalPredictPrice);
        // 29: 购买类型
        line.setExtBuyType(reqLineList.isEmpty() ? null : reqLineList.get(0).getX("extBuyType"));
        // 30: 审批状态
        line.setFixPriceLineStatus(null);
        // 31: 是否关闭
        line.setHasClosed(Enable.N);
        // 32: 发票类型
        line.setInvoiceType(inqOrderItem.getInvoiceType());
    }

    @ApiOperation("校验来源于近期采购的物资明细")
    private void doValidateFixPriceLineFromPurchaseReq(ExtFixPriceEditContext context, ExtFixPriceLine line) {
        long sourceFromLineId = Long.parseLong(line.getSourceFromLineId());
        RequirementLine reqLine = context.getReqLineMap().get(sourceFromLineId);
        AssertUtils.notNull(reqLine, "近期采购信息[{0}]不存在", sourceFromLineId);
        RequirementHead reqHead = context.getReqHeadMap().get(reqLine.getRequirementHeadId());

        // 1: 来源单据ID
        line.setSourceFromId(reqLine.getRequirementHeadId().toString());
        // 2: 来源单据编号
        line.setSourceFromNo(reqHead.getRequirementHeadNum());
        line.setRequirementLineIds(reqLine.getRequirementLineId().toString());
        // 3: 业务实体
        line.setOrgOuId(reqLine.getOrgId());
        line.setOrgOuCode(reqLine.getOrgCode());
        line.setOrgOuName(reqLine.getOrgName());
        // 3: 物料
        line.setItemId(reqLine.getMaterialId());
        line.setItemCode(reqLine.getMaterialCode());
        line.setItemDesc(reqLine.getMaterialName());
        // 4: 物料规格型号
        line.setExtMaterialModel(StringUtils.trimToNull(reqLine.getX("extMaterialModel")));
        // 5: 单位
        line.setUnit(reqLine.getUnitCode());
        // 6: 数量
        line.setQuantity(reqLine.getRequirementQuantity());
        // 7: 品牌
        line.setBrand(reqLine.getBrand());
        // 8: 供应商
        line.setVendorId(reqLine.getVendorId());
        line.setVendorCode(reqLine.getVendorCode());
        line.setVendorName(reqLine.getVendorName());
        // 9: 未税单价
        line.setNotaxPrice(reqLine.getNotaxPrice());
        // 10: 税率
        line.setTaxKey(reqLine.getTaxKey());
        line.setTaxRate(reqLine.getTaxRate());
        // 11: 未税总价
        if (reqLine.getNotaxPrice() != null && reqLine.getRequirementQuantity() != null) {
            line.setNotaxTotalPrice(reqLine.getNotaxPrice().multiply(reqLine.getRequirementQuantity()).setScale(4, RoundingMode.HALF_UP));
        }
        // 12: 近期最低价格(未税)
        {
            Object v = reqLine.getX("extHistoryVendorPrice1");
            if (v != null) {
                String latestMinNotaxPrice = StringUtils.trimToNull(v.toString());
                line.setLatestMinNotaxPrice(latestMinNotaxPrice != null ? new BigDecimal(latestMinNotaxPrice) : null);
            }
        }
        // 13: 浮动比例
        if (line.getNotaxPrice() != null && line.getLatestMinNotaxPrice() != null) {
            line.setPriceFloatScale(line.getNotaxPrice().subtract(line.getLatestMinNotaxPrice()).divide(line.getLatestMinNotaxPrice(), 2, RoundingMode.HALF_UP));
        }
        // 14: 近期最低供应商
        line.setLatestMinVendorId(null);
        line.setLatestMinVendorCode(StringUtils.trimToNull(reqLine.getX("extHistoryVendorCode1")));
        line.setLatestMinVendorName(StringUtils.trimToNull(reqLine.getX("extHistoryVendorName1")));
        // 15: 近期最低供品牌
        line.setLatestMinBrand(reqLine.getX("extHistoryVendorBrand1"));
        // 21: 采购员账号
        line.setBuyerUsername(reqLine.getCeeaPerformUserName());
        line.setBuyerNickname(reqLine.getCeeaPerformUserNickname());
        // 22: 申请类型
        line.setApplyType(StringUtils.trimToNull(reqHead.getCeeaPrType()));
        // 23: 质保期
        {
            Object v = reqLine.getX("extWarrantyPeriod");
            if (v != null) {
                String vs = StringUtils.trimToNull(v.toString());
                line.setExtWarrantyPeriod(vs != null ? Integer.valueOf(v.toString()) : null);
            }
        }
        // 24: 预估单价
        {
            Object v = reqLine.getX("extPredictPrice");
            if (v != null) {
                String extPredictPrice = StringUtils.trimToNull(v.toString());
                line.setExtPredictPrice(extPredictPrice != null ? new BigDecimal(extPredictPrice) : null);
            }
        }
        // 25: 预估总价
        {
            Object v = reqLine.getX("extPredictAmount");
            if (v != null) {
                String extPredictAmount = StringUtils.trimToNull(v.toString());
                line.setExtPredictAmount(extPredictAmount != null ? new BigDecimal(extPredictAmount) : null);
            }
        }
        // 26: 购买类型
        line.setExtBuyType(StringUtils.trimToNull(reqLine.getX("extBuyType")));
        // 27: 审批状态
        line.setFixPriceLineStatus(null);
        // 28: 是否关闭
        line.setHasClosed(Enable.N);
        // 29: 发票类型
        line.setInvoiceType(StringUtils.trimToNull(reqLine.getX("extInvoiceType")));
        // 30: 预付款说明
        {
            String vs = StringUtils.trimToNull(reqLine.getX("extAdvancePaymentRemark"));
            if (vs != null) {
                try {
                    line.setAdvancePaymentRemark(Enable.valueOf(vs));
                } catch (Exception e) {
                    throw new IllegalArgumentException("近期采购数据存在非法的预付款说明：" + vs);
                }
            }
        }
    }

    @ApiModelProperty("处理定价单附件")
    private void validateAndConvertFixPriceFiles(ExtFixPriceEditContext context) {
        if (CollectionUtils.isEmpty(context.getParam().getFileList())) { return; }

        Map<Long/* fixPriceFileId */, ExtFixPriceFile> existFileMap = Collections.emptyMap(); {
            Set<Long> fixPriceFileIds = context.getParam().getFileList().stream().map(ExtFixPriceFile::getFixPriceFileId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (!fixPriceFileIds.isEmpty()) {
                existFileMap = extFixPriceFileDAO.listByIds(fixPriceFileIds).stream().collect(Collectors.toMap(ExtFixPriceFile::getFixPriceFileId, Function.identity()));
            }
        }

        // 1: 数据校验
        List<ExtFixPriceFile> fileList = context.getParam().getFileList(); {
            int index = 0;
            for (ExtFixPriceFile file : fileList) {
                ExtFixPriceFile existPriceFile = existFileMap.get(file.getFixPriceFileId());

                // 1.1: ID
                file.setFixPriceFileId(existPriceFile != null ? existPriceFile.getFixPriceFileId() : null);
                // 1.2: 定价单ID
                file.setFixPriceHeadId(context.getFixPriceHeadEntity().getFixPriceHeadId());
                // 1.3: 文件ID
                AssertUtils.notNull(file.getFileId(), "请上次附件");
                // 1.4: 文件名称
                file.setFileName(StringUtils.trimToNull(file.getFileName()));
                AssertUtils.notNull(file.getFileName(), "请上次附件");
                AssertUtils.isTrue(file.getFileName().length() <= 150, "附件的名称长度不能超过150");

                file.setSortIndex(index++);
            }
        }
        // 2: 数据转换
        List<ExtFixPriceFile> entityList = new ArrayList<>(fileList.size()); {
            for (ExtFixPriceFile file : fileList) {
                ExtFixPriceFile entity = new ExtFixPriceFile();
                entityList.add(entity);
                //noinspection unchecked
                SouObjectXUtil.mergePropertiesIgnoreFieldsWithoutExts(file, entity,
                        ExtFixPriceFile::getCreatedId,
                        ExtFixPriceFile::getCreatedBy,
                        ExtFixPriceFile::getCreationDate,
                        ExtFixPriceFile::getCreatedByIp,
                        ExtFixPriceFile::getCreatedFullName,
                        ExtFixPriceFile::getTenantId,
                        ExtFixPriceFile::getVersion);
            }
        }
        context.setFixPriceFileEntityList(entityList);
    }

}
