package com.midea.cloud.srm.sou.fixprice.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.feign.SupplierCooperateClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementLine;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceHeadDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceLineCancelDTO;
import com.midea.cloud.srm.model.sou.fixprice.dto.ExtFixPriceLinePassDTO;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceFile;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceLineStatusEnum;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceSourceFromTypeEnum;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceStatusEnum;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouItem;
import com.midea.cloud.srm.model.sou.inq.entity.InqSouOrderItem;
import com.midea.cloud.srm.model.sou.openapi.utils.SouObjectXUtil;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouItem;
import com.midea.cloud.srm.model.sou.sourcing.entity.SouOrderItem;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.pr.requirement.enums.PrRequirementFixPriceStatusEnum;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceFileDAO;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceHeadDAO;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceHeadMapper;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceLineDAO;
import com.midea.cloud.srm.sou.fixprice.plugin.event.edit.ExtFixPriceEditContext;
import com.midea.cloud.srm.sou.fixprice.plugin.event.edit.ExtFixPriceEditPlugin;
import com.midea.cloud.srm.sou.fixprice.service.ExtFixPriceEventService;
import com.midea.cloud.srm.sou.inq.init.dao.InqSouItemDAO;
import com.midea.cloud.srm.sou.inq.order.dao.InqSouOrderItemDAO;
import com.midea.cloud.srm.sou.sourcing.init.dao.SouItemDAO;
import com.midea.cloud.srm.sou.sourcing.order.dao.SouOrderItemDAO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * @author 100014337
 */
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtFixPriceEventServiceImpl implements ExtFixPriceEventService {

    @Autowired
    private ExtFixPriceHeadDAO extFixPriceHeadDAO;
    @Autowired
    private ExtFixPriceLineDAO extFixPriceLineDAO;
    @Autowired
    private ExtFixPriceFileDAO extFixPriceFileDAO;
    @Autowired
    private ExtFixPriceEditPlugin extFixPriceEditPlugin;
    @Autowired
    private InqSouOrderItemDAO inqSouOrderItemDAO;
    @Autowired
    private InqSouItemDAO inqSouItemDAO;
    @Autowired
    private SouOrderItemDAO souOrderItemDAO;
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private SupplierCooperateClient supplierCooperateClient;
    @Autowired
    private SouItemDAO souItemDAO;
    @Resource
    private ExtFixPriceHeadMapper extFixPriceHeadMapper;

    /**
     * 编辑定价单
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ExtFixPriceHeadDTO editFixPrice(ExtFixPriceHeadDTO param) {
        ExtFixPriceEditContext context = new ExtFixPriceEditContext(param);
        // 1: 校验操作条件/权限
        extFixPriceEditPlugin.judgeEdit(context);
        // 2: 校验处理
        extFixPriceEditPlugin.validateAndConvertEntity(context);
        // 3: 保存数据
        extFixPriceHeadDAO.saveOrUpdate(param.getFixPriceHeadId(), Collections.singletonList(context.getFixPriceHeadEntity()), ExtFixPriceHead::getFixPriceHeadId);
        extFixPriceLineDAO.saveOrUpdate(param.getFixPriceHeadId(), context.getFixPriceLineEntityList(), ExtFixPriceLine::getFixPriceHeadId);
        extFixPriceFileDAO.saveOrUpdate(param.getFixPriceHeadId(), context.getFixPriceFileEntityList(), ExtFixPriceFile::getFixPriceHeadId);
        // 4: 回写上游信息
        List<ExtFixPriceLine> fixPriceLineList = context.getFixPriceLineEntityList();
        Set<String> tagIds;
        Set<Long> keepTagIds = new HashSet<>(32); {
            if (CollectionUtils.isNotEmpty(fixPriceLineList)) {
                tagIds = fixPriceLineList.stream().filter(e -> ExtFixPriceSourceFromTypeEnum.INQ.equals(e.getSourceFromType()) || ExtFixPriceSourceFromTypeEnum.PURCHASE_REQ.equals(e.getSourceFromType()))
                        .map(ExtFixPriceLine::getSourceFromLineId).collect(Collectors.toSet());
            } else {
                tagIds = Collections.emptySet();
            }
        }
        {
            // 需要加上被删除的
            if (CollectionUtils.isNotEmpty(context.getExistExtFixPriceLineList())) {
                context.getExistExtFixPriceLineList().forEach(pl -> {
                    if (!tagIds.contains(pl.getSourceFromLineId())) {
                        // 被删除的
                        fixPriceLineList.add(pl);
                    } else {
                        // 保留的
                        keepTagIds.add(Long.valueOf(pl.getSourceFromLineId()));
                    }
                });
            }
        }
        if (!fixPriceLineList.isEmpty()) {
            // 4.1: 处理来源于询比价
            Set<Long> requirementLineIds = new HashSet<>(32);
            Set<Long> noTagRequirementLineIds = new HashSet<>(32);
            {
                Map<String/* orderItemId */, ExtFixPriceLine> fixPriceLineMap = fixPriceLineList.stream().filter(e -> ExtFixPriceSourceFromTypeEnum.INQ.equals(e.getSourceFromType()))
                        .collect(Collectors.toMap(ExtFixPriceLine::getSourceFromLineId, Function.identity()));
                if (!fixPriceLineMap.isEmpty()) {
                    List<InqSouOrderItem> inqOrderItemList = inqSouOrderItemDAO.listByIds(fixPriceLineMap.keySet());
                    Map<Long/* orderItemId */, SouOrderItem> orderItemMap = souOrderItemDAO.listByIds(inqOrderItemList.stream().map(InqSouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                            .stream().collect(Collectors.toMap(SouOrderItem::getOrderItemId, Function.identity()));
                    Map<Long/* souItemId */, InqSouItem> inqSouItemMap = inqSouItemDAO.listByIds(orderItemMap.values().stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet()))
                            .stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));
                    for (InqSouOrderItem inqOrderItem : inqOrderItemList) {
                        ExtFixPriceLine priceLine = fixPriceLineMap.get(inqOrderItem.getOrderItemId().toString());
                        if (priceLine != null) {
                            boolean isToSaveTag = tagIds.contains(inqOrderItem.getOrderItemId().toString());
                            if (isToSaveTag) {
                                if (!keepTagIds.contains(inqOrderItem.getOrderItemId())) {
                                    // 非保留的
                                    AssertUtils.isFalse(Enable.Y.equals(inqOrderItem.getHasFixPrice()), "询比价报价明细已定价");
                                }
                                inqOrderItem.setHasFixPrice(Enable.Y);
                                inqOrderItem.setExtFixPriceHeadId(priceLine.getFixPriceHeadId());
                                inqOrderItem.setExtFixPriceNo(priceLine.getFixPriceNo());
                                inqOrderItem.setExtFixPriceLineId(priceLine.getFixPriceLineId());
                            } else {
                                // 这个数据是当前单据之前保存，但是目前删掉的
                                inqOrderItem.setHasFixPrice(Enable.N);
                                inqOrderItem.setExtFixPriceHeadId(null);
                                inqOrderItem.setExtFixPriceNo(null);
                                inqOrderItem.setExtFixPriceLineId(null);
                            }

                            SouOrderItem orderItem = orderItemMap.get(inqOrderItem.getOrderItemId());
                            InqSouItem inqSouItem = inqSouItemMap.get(orderItem.getSouItemId());
                            if (StringUtils.isNotEmpty(inqSouItem.getExtSourceFromLineIds())) {
                                Set<Long> ids = SouObjectXUtil.convertTargetObj(Arrays.asList(inqSouItem.getExtSourceFromLineIds().split(",")), new TypeReference<Set<Long>>() {});
                                if (isToSaveTag) {
                                    requirementLineIds.addAll(ids);
                                    keepTagIds.addAll(ids);
                                } else {
                                    noTagRequirementLineIds.addAll(ids);
                                }
                            }
                        }
                    }
                    inqSouOrderItemDAO.updateBatchById(inqOrderItemList);
                }
            }
            // 4.2: 处理来源于采购需求
            {
                Set<Long> ids = fixPriceLineList.stream().filter(e -> ExtFixPriceSourceFromTypeEnum.PURCHASE_REQ.equals(e.getSourceFromType()))
                        .map(e -> Long.valueOf(e.getSourceFromLineId())).collect(Collectors.toSet());
                if (!ids.isEmpty()) {
                    ids.forEach(id -> {
                        if (tagIds.contains(id.toString())) {
                            requirementLineIds.add(id);
                            keepTagIds.add(id);
                        } else {
                            noTagRequirementLineIds.add(id);
                        }
                    });
                    List<RequirementLine> reqLineList = Collections.emptyList();
                    if (!requirementLineIds.isEmpty()) {
                        reqLineList = SouObjectXUtil.convertList(qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementLine")
                                .in(RequirementLine::getRequirementLineId, new ArrayList<>(requirementLineIds))), RequirementLine.class);
                    }
                    reqLineList.forEach(reqLine -> {
                        if (!keepTagIds.contains(reqLine.getRequirementLineId())) {
                            // 非保留的
                            AssertUtils.isTrue(Enable.N.name().equals(reqLine.getX("ifCreateInq")), "需求池[{0} - {1}]已创建询比价，不能定价", reqLine.getRequirementHeadNum(), reqLine.getMaterialCode());
                            AssertUtils.isTrue(PrRequirementFixPriceStatusEnum.DRAFT.name().equals(reqLine.getX("fixPriceStatus"))
                                    || PrRequirementFixPriceStatusEnum.PRICE_FAIL.name().equals(reqLine.getX("fixPriceStatus")), "需求池[{0} - {1}]已定价，不能再次定价", reqLine.getRequirementHeadNum(), reqLine.getMaterialCode());
                        }
                    });
                }
            }
            // 4.3: 更新状态
            if (!requirementLineIds.isEmpty()) {
                qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update("PurchaseRequirementLine")
                        .set("fixPriceStatus", PrRequirementFixPriceStatusEnum.PRICE_ING)
                        .in(RequirementLine::getRequirementLineId, new ArrayList<>(requirementLineIds)));
            }
            if (!noTagRequirementLineIds.isEmpty()) {
                qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update("PurchaseRequirementLine")
                        .set("fixPriceStatus", PrRequirementFixPriceStatusEnum.DRAFT)
                        .in(RequirementLine::getRequirementLineId, new ArrayList<>(noTagRequirementLineIds)));
            }

            //校验协议价格
            Set<Long> requirementLineIdList = new HashSet<>();
            requirementLineIdList.addAll(requirementLineIds);
            requirementLineIdList.addAll(noTagRequirementLineIds);

            checkAgrrement(param, requirementLineIdList);
        }

        return param;
    }

    /**
     * 校验协议价格
     * @param param
     * @param requirementLineIdList
     */
    private void checkAgrrement(ExtFixPriceHeadDTO param, Set<Long> requirementLineIdList) {
        if(CollectionUtils.isEmpty(requirementLineIdList)) {
            return;
        }
        List<RecordDTO> recordList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(MqlType.PURCHASE_REQUIREMENT_LINE)
                .in(RequirementLine::getRequirementLineId, new ArrayList<>(requirementLineIdList)));
        if(CollectionUtils.isEmpty(recordList)) {
            return;
        }

        List<Long> materialIdList = new ArrayList<>();
        List<String> buyOrgCodeList = new ArrayList<>();
        List<String> supplyAreaList = new ArrayList<>();

        recordList.stream().forEach(record -> {
            materialIdList.add(record.get(RequirementLine::getMaterialId));
            buyOrgCodeList.add(record.get(RequirementLine::getOrgCode));
            supplyAreaList.add(record.getString("extAreaCode"));
        });

        Map<String, Object> mapperParam = new HashMap<>(15);
        mapperParam.put("materialIdList", materialIdList);
        mapperParam.put("buyOrgCodeList", buyOrgCodeList);
        mapperParam.put("supplyAreaList", supplyAreaList);

        List<Map<String, Object>> agreementList = extFixPriceHeadMapper.queryAgrrementInfo(mapperParam);

        if(CollectionUtils.isEmpty(agreementList)) {
            return;
        }

        Set<String> filterList = agreementList.stream().map(map -> StringUtils.joinWith(SrmConstant.UNDER_LINE,
                map.get("materialId"), map.get("supplyArea"), map.get("buyOrgCode"))).collect(Collectors.toSet());

        List<String> filterMaterialList = recordList.stream().filter(record ->
            filterList.contains(StringUtils.joinWith(SrmConstant.UNDER_LINE,
                    record.get(RequirementLine::getMaterialId), record.getString("extAreaCode"), record.get(RequirementLine::getOrgCode)))
        ).map(record -> record.get(RequirementLine::getMaterialCode)).distinct().collect(Collectors.toList());

        if(CollectionUtils.isNotEmpty(filterMaterialList)) {
            param.setCheckCode("1");
            param.setCheckMsg(filterMaterialList.stream().collect(Collectors.joining(SrmConstant.SIG_3)) + " 存在协议价格。");
        }

    }

    /**
     * 删除定价单
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteFixPrice(long fixPriceHeadId) {
        ExtFixPriceHead fixPrice = extFixPriceHeadDAO.getById(fixPriceHeadId);
        // 幂等处理
        if (fixPrice == null) { return; }
        AssertUtils.isTrue(ExtFixPriceStatusEnum.DRAFT.name().equals(fixPrice.getFixPriceStatus()), "非拟定状态不能删除");

        List<ExtFixPriceLine> fixPriceLineList = extFixPriceLineDAO.list(ExtFixPriceLine::getFixPriceHeadId, fixPriceHeadId)
                .stream().filter(e -> Enable.N.equals(e.getHasClosed())).collect(Collectors.toList());

        extFixPriceHeadDAO.removeById(fixPriceHeadId);
        extFixPriceLineDAO.lambdaUpdate().eq(ExtFixPriceLine::getFixPriceHeadId, fixPriceHeadId).remove();
        extFixPriceFileDAO.lambdaUpdate().eq(ExtFixPriceFile::getFixPriceHeadId, fixPriceHeadId).remove();
        // 4: 回写上游信息
        if (!fixPriceLineList.isEmpty()) {
            // 4.1: 处理来源于询比价
            Set<Long> requirementLineIds = new HashSet<>(32); {
                Map<String/* orderItemId */, ExtFixPriceLine> fixPriceLineMap = fixPriceLineList.stream().filter(e -> ExtFixPriceSourceFromTypeEnum.INQ.equals(e.getSourceFromType()))
                        .collect(Collectors.toMap(ExtFixPriceLine::getSourceFromLineId, Function.identity()));
                if (!fixPriceLineMap.isEmpty()) {
                    List<InqSouOrderItem> inqOrderItemList = inqSouOrderItemDAO.listByIds(fixPriceLineMap.keySet());
                    Map<Long/* orderItemId */, SouOrderItem> orderItemMap = souOrderItemDAO.listByIds(inqOrderItemList.stream().map(InqSouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                            .stream().collect(Collectors.toMap(SouOrderItem::getOrderItemId, Function.identity()));
                    Map<Long/* souItemId */, InqSouItem> inqSouItemMap = inqSouItemDAO.listByIds(orderItemMap.values().stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet()))
                            .stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));
                    for (InqSouOrderItem inqOrderItem : inqOrderItemList) {
                        ExtFixPriceLine priceLine = fixPriceLineMap.get(inqOrderItem.getOrderItemId().toString());
                        if (priceLine != null) {
                            inqOrderItem.setHasFixPrice(Enable.N);
                            inqOrderItem.setExtFixPriceHeadId(null);
                            inqOrderItem.setExtFixPriceNo(null);
                            inqOrderItem.setExtFixPriceLineId(null);

                            SouOrderItem orderItem = orderItemMap.get(inqOrderItem.getOrderItemId());
                            InqSouItem inqSouItem = inqSouItemMap.get(orderItem.getSouItemId());
                            if (StringUtils.isNotEmpty(inqSouItem.getExtSourceFromLineIds())) {
                                Set<Long> ids = SouObjectXUtil.convertTargetObj(Arrays.asList(inqSouItem.getExtSourceFromLineIds().split(",")), new TypeReference<Set<Long>>() {});
                                requirementLineIds.addAll(ids);
                            }
                        }
                    }
                    inqSouOrderItemDAO.updateBatchById(inqOrderItemList);
                }
            }
            // 4.2: 处理来源于采购需求
            {
                Set<Long> ids = fixPriceLineList.stream().filter(e -> ExtFixPriceSourceFromTypeEnum.PURCHASE_REQ.equals(e.getSourceFromType()))
                        .map(e -> Long.valueOf(e.getSourceFromLineId())).collect(Collectors.toSet());
                if (!ids.isEmpty()) {
                    requirementLineIds.addAll(ids);
                    List<RequirementLine> reqLineList = SouObjectXUtil.convertList(qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementLine")
                            .in(RequirementLine::getRequirementLineId, new ArrayList<>(ids))), RequirementLine.class);
                }
            }
            // 4.3: 更新状态
            qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update("PurchaseRequirementLine")
                    .set("fixPriceStatus", PrRequirementFixPriceStatusEnum.DRAFT)
                    .in(RequirementLine::getRequirementLineId, new ArrayList<>(requirementLineIds)));
        }
    }

    /**
     * 定价单审批提交
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApprovalSubmit(long fixPriceHeadId) {
        ExtFixPriceHead fixPrice = extFixPriceHeadDAO.getById(fixPriceHeadId);
        AssertUtils.notNull(fixPrice, "定价单[{0}]不存在", fixPriceHeadId);
        switch (fixPrice.getFixPriceStatus()) {
            // 拟定
            case "DRAFT":
                // 已驳回
            case "REJECTED":
                // 已撤回
            case "WITHDRAW":
//                AssertUtils.isTrue(Enable.Y.equals(fixPrice.getCanSubmit()), "尚未提交单据");
                break;
            // 审批中
            case "SUBMITTED":
                // 幂等处理
                return;
            // 已废弃
            case "ABANDONED":
                throw new IllegalArgumentException("单据已废弃");
                // 已审批
            case "APPROVED":
                throw new IllegalArgumentException("单据已审批");
            default:
                throw new IllegalArgumentException("无法识别的单据状态");
        }

        extFixPriceHeadDAO.lambdaUpdate()
                .set(ExtFixPriceHead::getFixPriceStatus, ExtFixPriceStatusEnum.SUBMITTED)
                .eq(ExtFixPriceHead::getFixPriceHeadId, fixPriceHeadId)
                .update();
    }

    /**
     * 定价单审批通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApprovalPass(long fixPriceHeadId) {
        ExtFixPriceHead fixPrice = extFixPriceHeadDAO.getById(fixPriceHeadId);
        AssertUtils.notNull(fixPrice, "定价单[{0}]不存在", fixPriceHeadId);
        switch (fixPrice.getFixPriceStatus()) {
            // 已审批
            case "APPROVED":
                // 幂等处理
                return;
            // 审批中
            case "SUBMITTED":
                break;
            // 拟定
            case "DRAFT":
                throw new IllegalArgumentException("单据尚未提交");
                // 已驳回
            case "REJECTED":
                throw new IllegalArgumentException("单据已驳回");
                // 已撤回
            case "WITHDRAW":
                throw new IllegalArgumentException("单据已撤回");
                // 已废弃
            case "ABANDONED":
                throw new IllegalArgumentException("单据已废弃");
            default:
                throw new IllegalArgumentException("无法识别的单据状态");
        }

        extFixPriceHeadDAO.lambdaUpdate()
                .set(ExtFixPriceHead::getFixPriceStatus, ExtFixPriceStatusEnum.APPROVED)
                .eq(ExtFixPriceHead::getFixPriceHeadId, fixPriceHeadId)
                .update();
        // 目前没有明细层面的审批处理，先全部通过
        List<ExtFixPriceLine> priceLineList = extFixPriceLineDAO.list(ExtFixPriceLine::getFixPriceHeadId, fixPriceHeadId);
        Map<Long/* fixPriceLineId */, Enable> passInfo = priceLineList.stream().collect(Collectors.toMap(ExtFixPriceLine::getFixPriceLineId, e -> Enable.Y));
        ExtFixPriceLinePassDTO dto = new ExtFixPriceLinePassDTO();
        dto.setFixPriceHeadId(fixPriceHeadId);
        dto.setPassInfo(passInfo);
        this.fixPriceLinePass(dto);
    }

    /**
     * 定价单审批未通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void callbackAfterApprovalUnPass(long fixPriceHeadId, String fixPriceStatus) {
        ExtFixPriceHead fixPrice = extFixPriceHeadDAO.getById(fixPriceHeadId);
        AssertUtils.notNull(fixPrice, "定价单[{0}]不存在", fixPriceHeadId);
        switch (fixPrice.getFixPriceStatus()) {
            // 已驳回
            case "REJECTED":
                if (ExtFixPriceStatusEnum.REJECTED.name().equals(fixPriceStatus)) {
                    // 幂等处理
                    return;
                }
                throw new IllegalArgumentException("单据已驳回");
                // 已撤回
            case "WITHDRAW":
                if (ExtFixPriceStatusEnum.WITHDRAW.name().equals(fixPriceStatus)) {
                    // 幂等处理
                    return;
                }
                throw new IllegalArgumentException("单据已撤回");
                // 已废弃
            case "ABANDONED":
                if (ExtFixPriceStatusEnum.ABANDONED.name().equals(fixPriceStatus)) {
                    // 幂等处理
                    return;
                }
                throw new IllegalArgumentException("单据已废弃");
                // 已审批
            case "APPROVED":
                throw new IllegalArgumentException("单据已审批");
                // 审批中
            case "SUBMITTED":
                break;
            // 拟定
            case "DRAFT":
                throw new IllegalArgumentException("单据尚未提交");
            default:
                throw new IllegalArgumentException("无法识别的单据状态");
        }

        extFixPriceHeadDAO.lambdaUpdate()
                .set(ExtFixPriceHead::getFixPriceStatus, fixPriceStatus)
                .eq(ExtFixPriceHead::getFixPriceHeadId, fixPriceHeadId)
                .update();

        if (ExtFixPriceStatusEnum.ABANDONED.name().equals(fixPriceStatus)) {
            // 审批作废
            // 1: 回写上游信息
            List<ExtFixPriceLine> fixPriceLineList = extFixPriceLineDAO.list(ExtFixPriceLine::getFixPriceHeadId, fixPriceHeadId);
            // 1.1: 处理来源于询比价
            Set<Long> requirementLineIds = new HashSet<>(32); {
                Map<String/* orderItemId */, ExtFixPriceLine> fixPriceLineMap = fixPriceLineList.stream().filter(e -> ExtFixPriceSourceFromTypeEnum.INQ.equals(e.getSourceFromType()))
                        .collect(Collectors.toMap(ExtFixPriceLine::getSourceFromLineId, Function.identity()));
                if (!fixPriceLineMap.isEmpty()) {
                    List<InqSouOrderItem> inqOrderItemList = inqSouOrderItemDAO.listByIds(fixPriceLineMap.keySet());

                    Map<Long/* orderItemId */, SouOrderItem> orderItemMap = souOrderItemDAO.listByIds(inqOrderItemList.stream().map(InqSouOrderItem::getOrderItemId).collect(Collectors.toSet()))
                            .stream().collect(Collectors.toMap(SouOrderItem::getOrderItemId, Function.identity()));
                    Map<Long/* souItemId */, InqSouItem> inqSouItemMap = inqSouItemDAO.listByIds(orderItemMap.values().stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet()))
                            .stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));
                    for (InqSouOrderItem inqOrderItem : inqOrderItemList) {
                        ExtFixPriceLine priceLine = fixPriceLineMap.get(inqOrderItem.getOrderItemId().toString());
                        if (priceLine != null) {
                            SouOrderItem orderItem = orderItemMap.get(inqOrderItem.getOrderItemId());
                            InqSouItem inqSouItem = inqSouItemMap.get(orderItem.getSouItemId());
                            if (StringUtils.isNotEmpty(inqSouItem.getExtSourceFromLineIds())) {
                                Set<Long> ids = SouObjectXUtil.convertTargetObj(Arrays.asList(inqSouItem.getExtSourceFromLineIds().split(",")), new TypeReference<Set<Long>>() {});
                                requirementLineIds.addAll(ids);
                            }
                        }
                    }

                    inqSouOrderItemDAO.lambdaUpdate()
                            .set(InqSouOrderItem::getHasFixPrice, Enable.N)
                            .set(InqSouOrderItem::getExtFixPriceHeadId, null)
                            .set(InqSouOrderItem::getExtFixPriceNo, null)
                            .set(InqSouOrderItem::getExtFixPriceLineId, null)
                            .in(InqSouOrderItem::getOrderItemId, fixPriceLineMap.keySet())
                            .update();
                }
            }
            // 1.2: 处理来源于采购需求
            {
                Set<Long> ids = fixPriceLineList.stream().filter(e -> ExtFixPriceSourceFromTypeEnum.PURCHASE_REQ.equals(e.getSourceFromType()))
                        .map(e -> Long.valueOf(e.getSourceFromLineId())).collect(Collectors.toSet());
                requirementLineIds.addAll(ids);
            }
            // 1.3: 更新状态
            qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update("PurchaseRequirementLine")
                    .set("fixPriceStatus", PrRequirementFixPriceStatusEnum.PRICE_FAIL)
                    .in(RequirementLine::getRequirementLineId, new ArrayList<>(requirementLineIds)));
        }
        // 目前没有明细层面的审批处理，先全部未通过
//        List<ExtFixPriceLine> priceLineList = extFixPriceLineDAO.list(ExtFixPriceLine::getFixPriceHeadId, fixPriceHeadId);
//        Map<Long/* fixPriceLineId */, Enable> passInfo = priceLineList.stream().collect(Collectors.toMap(ExtFixPriceLine::getFixPriceLineId, e -> Enable.N));
//        ExtFixPriceLinePassDTO dto = new ExtFixPriceLinePassDTO();
//        dto.setFixPriceHeadId(fixPriceHeadId);
//        dto.setPassInfo(passInfo);
//        this.fixPriceLinePass(dto);
    }

    /**
     * 定价明细审批通过/未通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void fixPriceLinePass(ExtFixPriceLinePassDTO param) {
        param.formatParams();
        ExtFixPriceHead fixPrice = extFixPriceHeadDAO.getById(param.getFixPriceHeadId());
        AssertUtils.notNull(fixPrice, "定价单[{0}]不存在", param.getFixPriceHeadId());
        AssertUtils.isTrue(ExtFixPriceStatusEnum.APPROVED.name().equals(fixPrice.getFixPriceStatus()), "定价单未审批通过");
        List<ExtFixPriceLine> fixPriceLineList = extFixPriceLineDAO.listByIds(param.getPassInfo().keySet());
        fixPriceLineList.forEach(priceLine -> AssertUtils.isTrue(param.getFixPriceHeadId().equals(priceLine.getFixPriceHeadId()), "禁止同时操作多个定价单"));
        // 1: 分组确定哪些定价明细是通过/未通过的
        List<ExtFixPriceLine> toPassLineList = new ArrayList<>(fixPriceLineList.size());
        List<ExtFixPriceLine> unPassLineList = new ArrayList<>(fixPriceLineList.size());
        fixPriceLineList.forEach(priceLine -> {
            if (Enable.Y.equals(param.getPassInfo().get(priceLine.getFixPriceLineId()))) {
                toPassLineList.add(priceLine);
            } else {
                unPassLineList.add(priceLine);
            }
        });
        // 2: 先更新定价明细的字段状态
        if (!toPassLineList.isEmpty()) {
            extFixPriceLineDAO.lambdaUpdate()
                    .set(ExtFixPriceLine::getFixPriceLineStatus, ExtFixPriceLineStatusEnum.PASS)
                    .in(ExtFixPriceLine::getFixPriceLineId, toPassLineList.stream().map(ExtFixPriceLine::getFixPriceLineId).collect(Collectors.toSet()))
                    .update();
        }
        if (!unPassLineList.isEmpty()) {
            extFixPriceLineDAO.lambdaUpdate()
                    .set(ExtFixPriceLine::getFixPriceLineStatus, ExtFixPriceLineStatusEnum.UN_PASS)
                    .in(ExtFixPriceLine::getFixPriceLineId, unPassLineList.stream().map(ExtFixPriceLine::getFixPriceLineId).collect(Collectors.toSet()))
                    .update();
        }
        // 回写上游询比价定价状态
        if (!toPassLineList.isEmpty()) {
            List<ExtFixPriceLine> tempLineList = toPassLineList.stream().filter(e -> ExtFixPriceSourceFromTypeEnum.INQ.equals(e.getSourceFromType())).collect(Collectors.toList());
            if (!tempLineList.isEmpty()) {
                List<InqSouOrderItem> inqOrderItemList = inqSouOrderItemDAO.listByIds(tempLineList.stream().map(ExtFixPriceLine::getSourceFromLineId).collect(Collectors.toSet()));
                Map<Long/* orderItemId */, ExtFixPriceLine> tempLineMap = tempLineList.stream().collect(Collectors.toMap(e -> Long.valueOf(e.getSourceFromLineId()), Function.identity()));
                inqOrderItemList.forEach(inqOrderItem -> {
                    ExtFixPriceLine priceLine = tempLineMap.get(inqOrderItem.getOrderItemId());
                    if (priceLine != null) {
                        inqOrderItem.setHasFixPrice(Enable.Y);
                        inqOrderItem.setExtFixPriceHeadId(priceLine.getFixPriceHeadId());
                        inqOrderItem.setExtFixPriceNo(fixPrice.getFixPriceNo());
                        inqOrderItem.setExtFixPriceLineId(priceLine.getFixPriceLineId());
                    }
                });

                inqSouOrderItemDAO.updateBatchById(inqOrderItemList);
            }
        }
        // 3: 回写上游更新需求池的定价状态
        {
            Set<Long> toPassRequirementIds = new HashSet<>(toPassLineList.size() << 2);
            Set<Long> unPassRequirementIds = new HashSet<>(unPassLineList.size() << 2);
            toPassLineList.forEach(priceLine -> {
                if (StringUtils.isNotBlank(priceLine.getRequirementLineIds())) {
                    Set<Long> ids = SouObjectXUtil.convertTargetObj(Arrays.asList(priceLine.getRequirementLineIds().split(",")), new TypeReference<Set<Long>>() {});
                    toPassRequirementIds.addAll(ids);
                }
            });
            unPassLineList.forEach(priceLine -> {
                if (StringUtils.isNotBlank(priceLine.getRequirementLineIds())) {
                    Set<Long> ids = SouObjectXUtil.convertTargetObj(Arrays.asList(priceLine.getRequirementLineIds().split(",")), new TypeReference<Set<Long>>() {});
                    unPassRequirementIds.addAll(ids);
                }
            });
            if (!toPassRequirementIds.isEmpty()) {
                qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update("PurchaseRequirementLine")
                        .set("fixPriceStatus", PrRequirementFixPriceStatusEnum.PRICE_END)
                        .in(RequirementLine::getRequirementLineId, new ArrayList<>(toPassRequirementIds)));
            }
            if (!unPassRequirementIds.isEmpty()) {
                qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update("PurchaseRequirementLine")
                        .set("fixPriceStatus", PrRequirementFixPriceStatusEnum.PRICE_FAIL)
                        .in(RequirementLine::getRequirementLineId, new ArrayList<>(unPassRequirementIds)));
            }
        }
        // 4: 创建订单、近期采购数据
        if (!toPassLineList.isEmpty()) {
            ExtFixPriceHeadDTO dto = new ExtFixPriceHeadDTO();
            BeanUtils.copyProperties(fixPrice, dto);
            dto.setLineList(toPassLineList);
            supplierCooperateClient.createOrderByFixPrice(dto);
        }
    }

    /**
     * 关闭
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void closeFixPriceLine(ExtFixPriceLine priceLine) {
        AssertUtils.notNull(priceLine.getSourceFromLineId(), "缺少sourceFromLineId参数");
        AssertUtils.notNull(priceLine.getSourceFromType(), "缺少sourceFromType参数");
        AssertUtils.notNull(priceLine.getFixPriceLineId(), "缺少fixPriceLineId参数");

        if (priceLine.getFixPriceLineId() != null) {
            extFixPriceLineDAO.lambdaUpdate()
                    .set(ExtFixPriceLine::getHasClosed, Enable.Y)
                    .set(ExtFixPriceLine::getCloseReason, priceLine.getCloseReason())
                    .eq(ExtFixPriceLine::getFixPriceLineId, priceLine.getFixPriceLineId())
                    .update();
        }

        // 查找关联的需求池明细
        List<RequirementLine> reqLineList = new ArrayList<>(100); {
            Map<Long/* orderItemId */, SouOrderItem> orderItemMap = Collections.emptyMap();
            Map<Long/* souItemId */, SouItem> souItemMap = Collections.emptyMap();
            Map<Long/* souItemId */, InqSouItem> inqSouItemMap = Collections.emptyMap();
            Map<Long/* requirementLineId */, RequirementLine> reqLineMap = Collections.emptyMap(); {
                Set<Long> orderItemIds = new HashSet<>(10);
                Set<Long> requirementLineIds = new HashSet<>(10);
                if (ExtFixPriceSourceFromTypeEnum.INQ.equals(priceLine.getSourceFromType())) {
                    orderItemIds.add(Long.valueOf(priceLine.getSourceFromLineId()));
                }
                if (!orderItemIds.isEmpty()) {
                    orderItemMap = souOrderItemDAO.listByIds(orderItemIds).stream().collect(Collectors.toMap(SouOrderItem::getOrderItemId, Function.identity()));
                    if (!orderItemMap.isEmpty()) {
                        souItemMap = souItemDAO.listByIds(orderItemMap.values().stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet()))
                                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
                        inqSouItemMap = inqSouItemDAO.listByIds(souItemMap.keySet()).stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));

                        for (InqSouItem inqSouItem : inqSouItemMap.values()) {
                            if (StringUtils.isNotBlank(inqSouItem.getExtSourceFromLineIds())) {
                                Set<Long> ids = SouObjectXUtil.convertTargetObj(Arrays.asList(inqSouItem.getExtSourceFromLineIds().split(",")), new TypeReference<Set<Long>>() {});
                                requirementLineIds.addAll(ids);
                            }
                        }
                    }
                }
                if (!requirementLineIds.isEmpty()) {
                    reqLineMap = SouObjectXUtil.convertList(qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementLine")
                                    .in(RequirementLine::getRequirementLineId, new ArrayList<>(requirementLineIds))), RequirementLine.class)
                            .stream().collect(Collectors.toMap(RequirementLine::getRequirementLineId, Function.identity()));
                }
            }

            if (ExtFixPriceSourceFromTypeEnum.INQ.equals(priceLine.getSourceFromType())) {
                // 来源于询比价
                SouOrderItem orderItem = orderItemMap.get(Long.valueOf(priceLine.getSourceFromLineId()));
                if (orderItem != null) {
                    InqSouItem inqSouItem = inqSouItemMap.get(orderItem.getSouItemId());
                    if (StringUtils.isNotEmpty(inqSouItem.getExtSourceFromLineIds())) {
                        Set<Long> reqLineIds = SouObjectXUtil.convertTargetObj(Arrays.asList(inqSouItem.getExtSourceFromLineIds().split(",")), new TypeReference<Set<Long>>() {});
                        for (Long reqLineId : reqLineIds) {
                            RequirementLine reqLine = reqLineMap.get(reqLineId);
                            if (reqLine != null) {
                                reqLineList.add(reqLine);
                            }
                        }
                    }
                }
            } else if (ExtFixPriceSourceFromTypeEnum.PURCHASE_REQ.equals(priceLine.getSourceFromType())) {
                // 来源于需求池
                RequirementLine reqLine = reqLineMap.get(Long.valueOf(priceLine.getSourceFromLineId()));
                if (reqLine != null) { reqLineList.add(reqLine); }
            }
            reqLineList = reqLineList.stream().filter(e -> !Enable.N.name().equals(e.getX("extPoolStatus"))).collect(Collectors.toList());
        }
        if (reqLineList.isEmpty()) { return; }
        // 更新状态
        qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update("PurchaseRequirementLine")
                .set("extPoolStatus", Enable.N)
                .set("extClosedCause", priceLine.getCloseReason())
                .in(RequirementLine::getRequirementLineId, reqLineList.stream().map(RequirementLine::getRequirementLineId).collect(Collectors.toList())));
    }

    /**
     * 取消
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void cancelFixPriceLine(ExtFixPriceLineCancelDTO param) {
        param.formatParams();

        extFixPriceLineDAO.lambdaUpdate()
                .set(ExtFixPriceLine::getHasCancel, Enable.Y)
                .set(ExtFixPriceLine::getCancelReason, param.getCancelReason())
                .in(ExtFixPriceLine::getFixPriceLineId, param.getFixPriceLineIds())
                .update();

        // 查找关联的需求池明细
        List<RequirementLine> reqLineList = new ArrayList<>(100); {
            Map<Long/* orderItemId */, SouOrderItem> orderItemMap = Collections.emptyMap();
            Map<Long/* souItemId */, SouItem> souItemMap = Collections.emptyMap();
            Map<Long/* souItemId */, InqSouItem> inqSouItemMap = Collections.emptyMap();
            Map<Long/* requirementLineId */, RequirementLine> reqLineMap = Collections.emptyMap();

            List<ExtFixPriceLine> priceLineList = extFixPriceLineDAO.listByIds(param.getFixPriceLineIds());
            {
                Set<Long> orderItemIds = new HashSet<>(10);
                Set<Long> requirementLineIds = new HashSet<>(10);
                priceLineList.forEach(priceLine -> {
                    if (ExtFixPriceSourceFromTypeEnum.INQ.equals(priceLine.getSourceFromType())) {
                        orderItemIds.add(Long.valueOf(priceLine.getSourceFromLineId()));
                    }
                });
                if (!orderItemIds.isEmpty()) {
                    orderItemMap = souOrderItemDAO.listByIds(orderItemIds).stream().collect(Collectors.toMap(SouOrderItem::getOrderItemId, Function.identity()));
                    if (!orderItemMap.isEmpty()) {
                        souItemMap = souItemDAO.listByIds(orderItemMap.values().stream().map(SouOrderItem::getSouItemId).collect(Collectors.toSet()))
                                .stream().collect(Collectors.toMap(SouItem::getSouItemId, Function.identity()));
                        inqSouItemMap = inqSouItemDAO.listByIds(souItemMap.keySet()).stream().collect(Collectors.toMap(InqSouItem::getSouItemId, Function.identity()));

                        for (InqSouItem inqSouItem : inqSouItemMap.values()) {
                            if (StringUtils.isNotBlank(inqSouItem.getExtSourceFromLineIds())) {
                                Set<Long> ids = SouObjectXUtil.convertTargetObj(Arrays.asList(inqSouItem.getExtSourceFromLineIds().split(",")), new TypeReference<Set<Long>>() {});
                                requirementLineIds.addAll(ids);
                            }
                        }
                    }
                }
                if (!requirementLineIds.isEmpty()) {
                    reqLineMap = SouObjectXUtil.convertList(qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementLine")
                                    .in(RequirementLine::getRequirementLineId, new ArrayList<>(requirementLineIds))), RequirementLine.class)
                            .stream().collect(Collectors.toMap(RequirementLine::getRequirementLineId, Function.identity()));
                }
            }

            for (ExtFixPriceLine priceLine : priceLineList) {
                if (ExtFixPriceSourceFromTypeEnum.INQ.equals(priceLine.getSourceFromType())) {
                    // 来源于询比价
                    SouOrderItem orderItem = orderItemMap.get(Long.valueOf(priceLine.getSourceFromLineId()));
                    if (orderItem != null) {
                        InqSouItem inqSouItem = inqSouItemMap.get(orderItem.getSouItemId());
                        if (StringUtils.isNotEmpty(inqSouItem.getExtSourceFromLineIds())) {
                            Set<Long> reqLineIds = SouObjectXUtil.convertTargetObj(Arrays.asList(inqSouItem.getExtSourceFromLineIds().split(",")), new TypeReference<Set<Long>>() {});
                            for (Long reqLineId : reqLineIds) {
                                RequirementLine reqLine = reqLineMap.get(reqLineId);
                                if (reqLine != null) {
                                    reqLineList.add(reqLine);
                                }
                            }
                        }
                    }
                } else if (ExtFixPriceSourceFromTypeEnum.PURCHASE_REQ.equals(priceLine.getSourceFromType())) {
                    // 来源于需求池
                    RequirementLine reqLine = reqLineMap.get(Long.valueOf(priceLine.getSourceFromLineId()));
                    if (reqLine != null) { reqLineList.add(reqLine); }
                }
            }
            reqLineList = reqLineList.stream().filter(e -> !Enable.N.name().equals(e.getX("extPoolStatus"))).collect(Collectors.toList());
        }
        if (reqLineList.isEmpty()) { return; }
        // 更新状态
        qlOpenClient.update(ContextPath.SUP_CE, QlOpenWrappers.update("PurchaseRequirementLine")
                .set("ifCreateInq", Enable.N)
                .set("fixPriceStatus", PrRequirementFixPriceStatusEnum.DRAFT)
                .in(RequirementLine::getRequirementLineId, reqLineList.stream().map(RequirementLine::getRequirementLineId).collect(Collectors.toList())));
    }

}
