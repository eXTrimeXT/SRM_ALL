package com.midea.cloud.srm.supcooperate.ext.requirement.pr.repo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pm.pr.catalogonshelves.entity.CatalogOnShelves;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApplyStatus;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.pm.pr.shopcart.entity.ShopCart;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementLineDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementSaveDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.mapper.PurchaseRequirementMapper;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.PurchaseRequirementService;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import com.midea.cloud.srm.supcooperate.meiql.util.PurchaseMqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Slf4j
@Component
public class PurchaseRequirementRepository extends PurchaseRepository<PurchaseRequirementHeadDTO> {

    private static final String EXTERNAL_ID = "externalId";

    public PurchaseRequirementRepository() {
        super("PurchaseRequirementHead", "requirementHeadId", "采购需求申请");

        this.register("getDetail", this::getDetail, false, "查询详情");
        this.register("saveOrUpdate", this::saveOrUpdate, true, "暂存/提交");
        this.register("pushPool", this::pushPool, true, "确认领单");
        this.register("confirmAbnormal", this::confirmAbnormal, true, "确认异常已处理（京东类商品用到）");
    }


    private QlResult confirmAbnormal(QlQueryAction queryAction) {
        Record record = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords().get(0);
        qlService.updateByWrapper(QlWrappers.update("PurchaseRequirementHead")
                .eq("requirementHeadId", record.get("requirementHeadId"))
                .set("extIsAbnormal", Enable.N.name()));
        return new QlResult();
    }

    @Autowired
    private PurchaseRequirementService purchaseRequirementService;

    @Autowired
    private QlService qlService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    private PurchaseRequirementMapper purchaseRequirementMapper;

    @Override
    protected void afterQuery(QlQueryAction queryAction, Collection<Record> records) {
        super.afterQuery(queryAction, records);
        records.stream().forEach(e -> {
            if (YesOrNo.YES.getValue().equals(e.get(PurchaseRequirementHeadDTO::getExtBidFlag))) {
                e.put(PurchaseRequirementHeadDTO::getExtInPool, null);
            }
        });
    }

    /**
     * 查详情
     */
    private QlResult getDetail(QlQueryAction action) {
        return super.query(action);
    }

    /**
     * 保存
     */
    private QlResult saveOrUpdate(QlQueryAction action) {
        List<Record> records = getRecords(action);
        Record record = getRecord(records);
        PurchaseRequirementSaveDTO requirementSaveDTO = BeanCopyUtil.convertWithExtensions(record, PurchaseRequirementSaveDTO.class);

        // 校验
        RequirementApproveStatus auditStatus = requirementSaveDTO.getAuditStatus();
        Assert.notNull(auditStatus, "状态不能为空");
        if (!RequirementApproveStatus.DRAFT.equals(auditStatus)
                && !RequirementApproveStatus.SUBMITTED.equals(auditStatus)) {
            throw new BaseException("不支持的状态参数");
        }
        log.info("111111111111===" + requirementSaveDTO.getRequirementHeadId());
        if (requirementSaveDTO.getRequirementHeadId() != null) {
            PurchaseRequirementHeadDTO requirementHeadDTO = getById(requirementSaveDTO.getRequirementHeadId());
            Assert.notNull(requirementHeadDTO, "采购申请ID不存在");
            if (RequirementApproveStatus.DRAFT.equals(requirementHeadDTO.getAuditStatus())
                    || RequirementApproveStatus.REJECTED.equals(requirementHeadDTO.getAuditStatus())
                    || RequirementApproveStatus.WITHDRAW.equals(requirementHeadDTO.getAuditStatus())) {
                requirementSaveDTO.setAuditStatus(requirementHeadDTO.getAuditStatus());
            } else {
                throw new BaseException("当前状态不能修改");
            }
            log.info("2222222222===");
            requirementSaveDTO.setRequirementHeadNum(requirementHeadDTO.getRequirementHeadNum());
            PurchaseRequirementHeadDTO purReq = purchaseRequirementMapper.selectById(requirementSaveDTO.getRequirementHeadId());
            log.info("--------==============" + JSONObject.toJSONString(purReq));
            if (StringUtils.isNotBlank(purReq.getEdmExNo())) {
                List<Record> purList = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementLine")
                        .eq(PurchaseRequirementLineDTO::getRequirementHeadNum, purReq.getRequirementHeadNum()), Record.class);
                log.info("purList=====" + JSONObject.toJSONString(purList));
                List<Record> rePurList = purList.stream().filter(e -> {
                    if (StringUtils.isNotBlank(e.getString(EXTERNAL_ID))) {
                        for (PurchaseRequirementLineDTO ll : requirementSaveDTO.getReqLineList()) {
                            if (e.getLong("requirementLineId").equals(ll.getRequirementLineId())) {
                                return false;
                            }
                        }
                    }
                    return true;
                }).collect(Collectors.toList());
                log.info("rePurList=====" + JSONObject.toJSONString(rePurList));
                for (Record record1 : rePurList) {
                    //edm删除
                    JSONObject jo = new JSONObject();
                    jo.put("applyOutsideCode", requirementHeadDTO.getEdmExNo());
                    jo.put("outerItemCode", record1.getString("externalId"));
                    jo.put("apporderNumber", requirementHeadDTO.getRequirementHeadNum());
                    jo.put("draftStatus", "删除");
                    jo.put("tenantId", record1.get("tenantId"));
                    jo.put("edmOrgId", record1.get("edmOrgId"));
                    JSONObject reStr = pjProjectExtClient.edmDraftOrderBackHaul(jo.toString());
                    log.info("--------------------------" + reStr);
                    if (!"200".equals(String.valueOf(reStr.get("code")))) {
                        throw new BaseException(reStr.get("msg").toString());
                    }
                }
            }
        } else {
            requirementSaveDTO.setRequirementHeadNum(baseClient.seqGen(SequenceCodeConstant.SEQ_PMP_PR_APPLY_NUM));
            requirementSaveDTO.setAuditStatus(RequirementApproveStatus.DRAFT);
        }

        // 校验商品起订量
        List<PurchaseRequirementLineDTO> reqLineList = requirementSaveDTO.getReqLineList();
        List<PurchaseRequirementLineDTO> requirementLines = PurchaseMqlUtils.trimDeleteFlag(reqLineList);
        if (RequirementApproveStatus.SUBMITTED.equals(auditStatus)) {
            checkProduct(requirementLines, requirementSaveDTO);

        }

        // 购物车回滚
        List<Long> delIds = PurchaseMqlUtils.retainDeleteFlag(reqLineList);
        if(CollectionUtils.isNotEmpty(delIds)){
            List<PurchaseRequirementLineDTO> delList = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementLine")
                    .in(PurchaseRequirementLineDTO::getRequirementLineId, delIds), PurchaseRequirementLineDTO.class);
            List<Long> shopCartIds = delList.stream()
                    .filter(p -> p.getShopCartId() != null)
                    .map(PurchaseRequirementLineDTO::getShopCartId)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(shopCartIds)) {
                qlService.updateByWrapper(QlWrappers.update(MqlType.SHOP_CART)
                        .set(ShopCart::getStatus, "APPROVED")
                        .set(ShopCart::getRequirementHeadNum, null)
                        .in(ShopCart::getShopCartId, shopCartIds));
            }
        }

        // 设置行信息
        AtomicInteger i = new AtomicInteger(1);
        requirementLines.forEach(e -> e.setApplyStatus(RequirementApplyStatus.UNASSIGNED)
                .setRequirementHeadNum(requirementSaveDTO.getRequirementHeadNum())
                .setOrgId(requirementSaveDTO.getOrgId())
                .setOrgCode(requirementSaveDTO.getOrgCode())
                .setOrgName(requirementSaveDTO.getOrgName())
                .setOrganizationId(requirementSaveDTO.getOrganizationId())
                .setOrganizationCode(requirementSaveDTO.getOrganizationCode())
                .setOrganizationName(requirementSaveDTO.getOrganizationName())
                .setOrderQuantity(e.getRequirementQuantity())
                .setCeeaExecutedQuantity(BigDecimal.ZERO)
                .setRowNum(i.getAndIncrement())
        );
        BigDecimal totalAmount = requirementLines.stream().map(s -> Optional.ofNullable(s.getExtPredictAmount()).orElse(new BigDecimal("0"))).reduce(BigDecimal.ZERO, BigDecimal::add);
        requirementSaveDTO.setExtExpectTotalAmount(totalAmount);
        // 转换record
        requirementSaveDTO.setApplyById(AppUserUtil.getLoginAppUser().getUserId());
        record.putAll(MeiQl.toValue(requirementSaveDTO, Record.class));
        return super.doSave(action, records);
    }





    /**
     * 手动领单
     */
    private QlResult pushPool(QlQueryAction action) {
        List<Record> records = getRecords(action);

        // 校验
        List<PurchaseRequirementHeadDTO> list = getByRecords(records);
        list.forEach(e -> {
            Assert.isTrue(YesOrNo.NO.getValue().equals(e.getExtBidFlag()), "所勾选行存在招标的采购申请，不允许领单");
            Assert.isTrue(!YesOrNo.YES.getValue().equals(e.getExtInPool()), "所勾选行存在已领单的采购申请，请检查");
            Assert.isTrue(RequirementApproveStatus.APPROVED.equals(e.getAuditStatus()), "所勾选行存在未审批的采购申请，请检查");
        });

        // 推单
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        list.forEach(e -> {
            purchaseRequirementService.pushPool(e, loginAppUser.getUsername(), loginAppUser.getNickname());
        });
        return QlResult.empty();
    }

    private void checkProduct(List<PurchaseRequirementLineDTO> requirementLines, PurchaseRequirementHeadDTO requirementHeadDTO) {
        List<PurchaseRequirementLineDTO> productLines = requirementLines.stream().filter(e -> YesOrNo.YES.getValue().equals(e.getExtProductFlag())).collect(Collectors.toList());
        List<String> materialCodes = productLines.stream().map(e -> e.getMaterialCode()).collect(Collectors.toList());
        List<String> areaCodes = productLines.stream().map(e -> e.getExtAreaCode()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(productLines)) {
            List<Record> productRecords = qlService.queryByWrapper(QlWrappers.query("CatalogOnShelves")
                    .contains("extOrgIdList", requirementHeadDTO.getOrgId())
                    .eq(CatalogOnShelves::getStatus, "ON_SHELVES")
                    .in("extAreaCode", areaCodes)
                    .in(CatalogOnShelves::getMaterialCode, materialCodes), Record.class);
            if (CollectionUtils.isEmpty(productRecords)) {
                productLines.forEach(e -> e.setExtProductFlag(YesOrNo.NO.getValue()));
                return;
            }

            Map<String, List<Record>> productMap = productRecords.stream()
                    .collect(Collectors.groupingBy(e -> e.get(CatalogOnShelves::getMaterialCode) + "-" + e.get("extAreaCode")));
            Map<String, BigDecimal> agreeAmountMap = new HashMap<>(16);
            Map<String, BigDecimal> agreeMinAmtMap = new HashMap<>(16);
            productLines.forEach(e -> {
                List<Record> products = productMap.get(e.getMaterialCode() + "-" + e.getExtAreaCode());
                if (products != null && products.size() == 1) {
                    // 是商品校验起订量
                    Record product = products.get(0);
                    BigDecimal minQty = product.get(CatalogOnShelves::getOrderQuantityMinimum);
                    if (minQty != null && e.getRequirementQuantity().compareTo(minQty) < 0) {
                        throw new BaseException(String.format("该物料不满足价格协议的起订量，请修改【物料编码：%s, 区域编码：%s】", e.getMaterialCode(), e.getExtAreaCode()));
                    }

                    String agreeNo = product.get(CatalogOnShelves::getPriceLibraryNo);
                    agreeAmountMap.compute(agreeNo, (k, v) -> BigDecimalUtil.add(v, e.getExtPredictAmount()));
                    agreeMinAmtMap.putIfAbsent(agreeNo, (BigDecimal) product.get("mixAmount"));
                } else {
                    e.setExtProductFlag(YesOrNo.NO.getValue());
                }
            });

            // 校验起订金额
            agreeMinAmtMap.forEach((k, v) -> {
                BigDecimal amount = agreeAmountMap.get(k);
                if (v != null && amount.compareTo(v) < 0) {
                    throw new BaseException(String.format("需求未满足价格协议的起订金额，请修改【价格协议：%s】", k));
                }
            });
        }
    }
}
