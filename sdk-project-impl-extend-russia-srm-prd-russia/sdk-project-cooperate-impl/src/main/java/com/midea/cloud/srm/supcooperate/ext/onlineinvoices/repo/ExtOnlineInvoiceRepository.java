package com.midea.cloud.srm.supcooperate.ext.onlineinvoices.repo;

import cn.hutool.json.JSONUtil;
import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.enums.pm.po.CeeaWarehousingReturnDetailEnum;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.pm.ps.advance.entity.AdvanceApply;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.*;
import com.midea.cloud.srm.model.suppliercooperate.invoice.enums.InvoiceStatus;
import com.midea.cloud.srm.supcooperate.ext.checkorders.dto.CheckOrderDetail;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto.ExtOnlineInvoice;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto.ExtOnlineInvoiceDetail;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto.ExtOnlineInvoicePunishDTO;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.dto.ExtOnlineInvoiceSaveDTO;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.enums.ExtOnlineInvoiceStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.enums.OnlineInvoiceSourceEnum;
import com.midea.cloud.srm.supcooperate.ext.onlineinvoices.service.ExtOnlineInvoiceService;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import com.midea.cloud.srm.supcooperate.meiql.onlineinvoice.dto.OnlineInvoiceAdvanceApplyDTO;
import com.midea.cloud.srm.supcooperate.meiql.onlineinvoice.dto.OnlineInvoiceDTO;
import com.midea.cloud.srm.supcooperate.meiql.onlineinvoice.dto.OnlineInvoiceOcrInvoiceDTO;
import com.midea.cloud.srm.supcooperate.meiql.util.PurchaseMqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Slf4j
@Component
public class ExtOnlineInvoiceRepository extends PurchaseRepository<ExtOnlineInvoice> {

    public ExtOnlineInvoiceRepository() {
        super("OnlineInvoice", "onlineInvoiceId", "开票单");

        this.register("extSaveOrUpdate", this::extSaveOrUpdate, true, "暂存/提交");
        this.registerAfter("extSaveOrUpdate", this::afterSubmit);
        this.register("extClose", this::extClose, true, "关闭");
        this.register("extPushEas", this::extPushEas, false, "重推EAS");
    }

    @Autowired
    private ExtOnlineInvoiceService extOnlineInvoiceService;
    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    private static final List<String> COMMON_INVOICE_TYPE = Arrays.asList("10", "11", "14", "04", "97", "32", "186");

    @Override
    protected void afterQuery(QlQueryAction queryAction, Collection<Record> records) {
        super.afterQuery(queryAction, records);
        records.stream().forEach(e -> {
            if (e.get(ExtOnlineInvoice::getExtStatus) == null) {
                e.put(ExtOnlineInvoice::getExtStatus, e.get(ExtOnlineInvoice::getInvoiceStatus));
            }
        });
    }

    @Override
    public QlResult delete(QlQueryAction queryAction) {
        List<Record> records = getRecords(queryAction);
        getRecord(records);
        List<Long> onlineInvoiceIds = records.stream().map(r -> {
            Long onlineInvoiceId = r.get(OnlineInvoice::getOnlineInvoiceId);
            Assert.notNull(onlineInvoiceId, "单据id不能为空");
            return onlineInvoiceId;
        }).collect(Collectors.toList());
        List<Record> onlineInvoices = qlService.readByKeys(schemaType, onlineInvoiceIds, Record.class);
        PurchaseMqlUtils.checkEntityPK(onlineInvoiceIds, onlineInvoices, businessName);

        // 校验
        onlineInvoices.forEach(e -> {
            Assert.isTrue(InvoiceStatus.DRAFT.name().equals(e.get(OnlineInvoiceDTO::getInvoiceStatus)), "存在非拟定状态的单据");
        });

        // 回写
        onlineInvoiceIds.forEach(onlineInvoiceId -> {
            // 回写对账、验收单
            Map<Long, BigDecimal> noticeQtyMap = new HashMap<>(16);
            Map<Long, BigDecimal> checkQtyMap = new HashMap<>(16);
            getDetailHistory(onlineInvoiceId, noticeQtyMap, checkQtyMap);
            writeBackDetail(noticeQtyMap, checkQtyMap);

            // 回写预付款
            Map<Long, BigDecimal> advanceQtyMap = new HashMap<>(15);
            getAdvanceHistory(onlineInvoiceId, advanceQtyMap);
            writeBackAdvance(advanceQtyMap);
        });

        // 调用发票接口
        Record onlineInvoice = onlineInvoices.get(0);
        List<Record> delInvoices = qlService.queryByWrapper(QlWrappers.query(OnlineInvoiceOcrInvoice.class.getSimpleName())
                .eq(OnlineInvoiceOcrInvoice::getOnlineInvoiceId, onlineInvoice.get(OnlineInvoice::getOnlineInvoiceId)), Record.class);
        pushInvoice(onlineInvoice, null, delInvoices);

        return super.delete(queryAction);
    }

    private QlResult extClose(QlQueryAction qlQueryAction) {
        Record record = getRecord(qlQueryAction);
        List<Long> onlineInvoiceIds = ((List<Long>) record.get("onlineInvoiceIds"));
        String closeCause = record.get(ExtOnlineInvoice::getComment);
        Assert.notEmpty(onlineInvoiceIds, "开票单id不能为空");
        List<Record> onlineInvoices = qlService.readByKeys(schemaType, onlineInvoiceIds, Record.class);
        PurchaseMqlUtils.checkEntityPK(onlineInvoiceIds, onlineInvoices, businessName);
        onlineInvoices.forEach(e -> {
            Assert.isTrue(InvoiceStatus.FINAL_REVIEW_APPROVED.name().equals(e.get(OnlineInvoiceDTO::getInvoiceStatus)), "当前状态不能关闭");
        });
        qlService.updateByWrapper(QlWrappers.update(schemaType)
                .set(ExtOnlineInvoice::getExtStatus, ExtOnlineInvoiceStatusEnum.CLOSED)
                .set(ExtOnlineInvoice::getComment, closeCause)
                .in(OnlineInvoice::getOnlineInvoiceId, onlineInvoiceIds));
        return QlResult.empty();
    }


    private QlResult extPushEas(QlQueryAction qlQueryAction) {
        Record record = getRecord(qlQueryAction);
        List<Long> onlineInvoiceIds = ((List<Long>) record.get("onlineInvoiceIds"));
        Assert.notEmpty(onlineInvoiceIds, "开票单id不能为空");
        List<Record> onlineInvoices = qlService.readByKeys(schemaType, onlineInvoiceIds, Record.class);
        PurchaseMqlUtils.checkEntityPK(onlineInvoiceIds, onlineInvoices, businessName);
        onlineInvoices.forEach(e -> {
            Assert.isTrue(InvoiceStatus.FINAL_REVIEW_APPROVED.equals(e.get(OnlineInvoice::getInvoiceStatus)), "存在非审批单据，不能推送Eas");
            Assert.isTrue(!ExtOnlineInvoiceStatusEnum.CLOSED.name().equals(e.get(ExtOnlineInvoice::getExtStatus)), "存在已关闭单据，不能推送Eas");
            Assert.isTrue(OnlineInvoiceSourceEnum.INVOICE_NOTICE.name().equals(e.get(ExtOnlineInvoice::getExtSource)), "存在非对账单据，不能推送Eas");
            Assert.isTrue(!YesOrNo.YES.getValue().equals(e.get(ExtOnlineInvoice::getExtSyncEas)), "存在已推送单据，不能推送Eas");
        });

        Map<Long, List<Record>> detailMap = qlService.queryByWrapper(QlWrappers.query(OnlineInvoiceDetail.class.getSimpleName())
                        .in(OnlineInvoiceDetail::getOnlineInvoiceId, onlineInvoiceIds), Record.class)
                .stream().collect(Collectors.groupingBy(e -> e.get(OnlineInvoiceDetail::getOnlineInvoiceId)));
        Map<Long, List<Record>> ocrInvoiceMap = qlService.queryByWrapper(QlWrappers.query(OnlineInvoiceOcrInvoice.class.getSimpleName())
                        .in(OnlineInvoiceOcrInvoice::getOnlineInvoiceId, onlineInvoiceIds), Record.class)
                .stream().collect(Collectors.groupingBy(e -> e.get(OnlineInvoiceDetail::getOnlineInvoiceId)));
        AtomicInteger success = new AtomicInteger();
        onlineInvoices.forEach(e -> {
            try {
                extOnlineInvoiceService.pushEasInvoice(e, detailMap.get(e.get(OnlineInvoice::getOnlineInvoiceId)),
                        ocrInvoiceMap.get(e.get(OnlineInvoice::getOnlineInvoiceId)));
                success.getAndIncrement();
            } catch (Exception ex){
                log.error("推送eas开票信息失败：{}", ex.getMessage(), ex);
            }
        });
        QlResult result = QlResult.empty();
        result.setPayload(success.get()==onlineInvoices.size());
        return result;
    }

    private QlResult extSaveOrUpdate(QlQueryAction qlQueryAction) {
        List<Record> records = getRecords(qlQueryAction);
        Record record = getRecord(records);
        ExtOnlineInvoiceSaveDTO onlineInvoiceSaveDTO = BeanCopyUtil.convertWithExtensions(record, ExtOnlineInvoiceSaveDTO.class);
        List<ExtOnlineInvoiceDetail> detailList = PurchaseMqlUtils.trimDeleteFlag(onlineInvoiceSaveDTO.getDetailList());
        List<OnlineInvoiceAdvanceApplyDTO> advanceList = PurchaseMqlUtils.trimDeleteFlag(onlineInvoiceSaveDTO.getAdvanceApplyList());
        List<ExtOnlineInvoicePunishDTO> punishList = PurchaseMqlUtils.trimDeleteFlag(onlineInvoiceSaveDTO.getPunishList());
        List<OnlineInvoiceOcrInvoiceDTO> invoiceList = PurchaseMqlUtils.trimDeleteFlag(onlineInvoiceSaveDTO.getOcrInvoiceList());

        // 参数校验
        String extSource = onlineInvoiceSaveDTO.getExtSource();
        Assert.hasText(extSource, "单据来源不能为空");
        List<String> detailSourceList = detailList.stream().map(e -> {
            Assert.hasText(e.getExtSource(), "单据来源不能为空");
            Assert.notNull(e.getUnitPriceExcludingTax(), "未税单价不能为空");
            Assert.notNull(e.getTaxRate(), "税率不能为空");
            if (OnlineInvoiceSourceEnum.INVOICE_NOTICE.name().equals(e.getExtSource())) {
                Assert.notNull(e.getInvoiceDetailId(), "对账单明细id不能为空");
            } else {
                Assert.notNull(e.getExtCheckDetailId(), "验收单明细id不能为空");
            }
            return e.getExtSource();
        }).distinct().collect(Collectors.toList());
        Assert.isTrue(detailSourceList.size() == 1 && detailSourceList.get(0).equals(extSource), "单据来源与明细单据来源不一致");
        if (YesOrNo.YES.getValue().equals(onlineInvoiceSaveDTO.getExtFreeOfCharge())) {
            Assert.isTrue(CollectionUtils.isEmpty(invoiceList), "免赠不能添加发票");
        } else {
            Assert.notEmpty(invoiceList, "发票不能为空");
        }

        // 设置单号、获取发票变更值
        List<OnlineInvoiceOcrInvoiceDTO> addInvoices = invoiceList.stream().filter(e -> e.getOnlineInvoiceId() == null).collect(Collectors.toList());
        List<Record> delInvoices = null;
        if (onlineInvoiceSaveDTO.getOnlineInvoiceId() != null) {
            ExtOnlineInvoice onlineInvoice = getById(onlineInvoiceSaveDTO.getOnlineInvoiceId());
            Assert.isTrue(InvoiceStatus.DRAFT.name().equals(onlineInvoice.getInvoiceStatus()), "当前状态不能修改");
            onlineInvoiceSaveDTO.setOnlineInvoiceNum(onlineInvoice.getOnlineInvoiceNum());

            // 查询历史发票
            List<Record> oldInvoices = qlService.queryByWrapper(QlWrappers.query(OnlineInvoiceOcrInvoice.class.getSimpleName())
                    .eq(OnlineInvoiceOcrInvoice::getOnlineInvoiceId, onlineInvoiceSaveDTO.getOnlineInvoiceId()), Record.class);
            if (CollectionUtils.isNotEmpty(oldInvoices)) {
                List<Long> delIds = PurchaseMqlUtils.retainDeleteFlag(onlineInvoiceSaveDTO.getOcrInvoiceList());
                delInvoices = oldInvoices.stream().filter(e -> delIds.contains(e.get(OnlineInvoiceOcrInvoice::getOcrInvoiceId))).collect(Collectors.toList());
            }
        } else {
            String onlineInvoiceNum = baseClient.seqGen(SequenceCodeConstant.SEQ_PMP_PS_ONLINE_INVOICE_CODE);
            onlineInvoiceSaveDTO.setOnlineInvoiceNum(onlineInvoiceNum);
            onlineInvoiceSaveDTO.setExtSyncEas(YesOrNo.NO.getValue());
        }

        // 计算金额
        calcAmount(onlineInvoiceSaveDTO, detailList, advanceList, punishList, invoiceList);

        // 回写对账/验收单、预付款
        beforeSave(onlineInvoiceSaveDTO, detailList, advanceList);

        // 保存
        record.putAll(MeiQl.toValue(onlineInvoiceSaveDTO, Record.class));
        QlResult result = super.doSave(qlQueryAction, records);

        // 调用发票接口
        pushInvoice(record, MeiQl.toListValue(addInvoices, Record.class), delInvoices);

        return result;
    }


    private void afterSubmit(QlQueryAction qlQueryAction, QlResult qlResult, Map<String, Collection<Record>> repoData) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 推送给eas
                    try {
                        Thread.sleep(500);
                        List<Record> records = getRecords(qlQueryAction);
                        Record record = getRecord(records);
                        ExtOnlineInvoiceSaveDTO onlineInvoiceSaveDTO = BeanCopyUtil.convertWithExtensions(record, ExtOnlineInvoiceSaveDTO.class);
                        if(InvoiceStatus.FINAL_REVIEW_APPROVED.name().equals(onlineInvoiceSaveDTO.getInvoiceStatus())) {
                            onlineInvoiceSaveDTO.setOnlineInvoiceId(getResultId(qlResult));
                            List<ExtOnlineInvoiceDetail> detailList = PurchaseMqlUtils.trimDeleteFlag(onlineInvoiceSaveDTO.getDetailList());
                            List<OnlineInvoiceOcrInvoiceDTO> ocrInvoiceList = PurchaseMqlUtils.trimDeleteFlag(onlineInvoiceSaveDTO.getOcrInvoiceList());
                            extOnlineInvoiceService.pushEasInvoice(MeiQl.toValue(onlineInvoiceSaveDTO, Record.class),
                                    MeiQl.toListValue(detailList, Record.class),
                                    MeiQl.toListValue(ocrInvoiceList, Record.class));
                        }
                    } catch (Exception e) {
                        log.error("推送eas开票信息失败：{}", e.getMessage(), e);
                    }
                }
            }).start();
        } catch (Exception e) {
            log.error("推送eas开票信息失败：{}", e.getMessage(), e);
        }
    }

    private void beforeSave(ExtOnlineInvoiceSaveDTO onlineInvoiceSaveDTO,
                            List<ExtOnlineInvoiceDetail> detailList,
                            List<OnlineInvoiceAdvanceApplyDTO> advanceList) {
        // 回写对账单明细对应的未开票数量
        Map<Long, BigDecimal> noticeQtyMap = new HashMap<>(16);
        Map<Long, BigDecimal> checkQtyMap = new HashMap<>(16);
        Long onlineInvoiceId = onlineInvoiceSaveDTO.getOnlineInvoiceId();
        getDetailHistory(onlineInvoiceId, noticeQtyMap, checkQtyMap);
        detailList.forEach(e -> {
            BigDecimal releaseQty = BigDecimalUtil.sub(BigDecimal.ZERO, e.getInvoiceQuantity());
            if (OnlineInvoiceSourceEnum.INVOICE_NOTICE.name().equals(e.getExtSource())) {
                noticeQtyMap.compute(e.getInvoiceDetailId(), (k, v) -> BigDecimalUtil.add(releaseQty, v));
            } else {
                checkQtyMap.compute(e.getExtCheckDetailId(), (k, v) -> BigDecimalUtil.add(releaseQty, v));
            }
        });
        writeBackDetail(noticeQtyMap, checkQtyMap);

        // 回写预付款
        Map<Long, BigDecimal> advanceQtyMap = new HashMap<>(16);
        getAdvanceHistory(onlineInvoiceId, advanceQtyMap);
        advanceList.forEach(e -> {
            advanceQtyMap.compute(e.getAdvanceApplyId(), (k, v) -> BigDecimalUtil.add(v, e.getCurWrittenOffAmount()));
        });
        writeBackAdvance(advanceQtyMap);
    }


    private void getAdvanceHistory(Long onlineInvoiceId, Map<Long, BigDecimal> advanceQtyMap) {
        if (onlineInvoiceId == null) {
            return;
        }

        List<Record> oldRecords = qlService.queryByWrapper(QlWrappers.query("OnlineInvoiceAdvanceApply")
                .eq(OnlineInvoiceAdvance::getOnlineInvoiceId, onlineInvoiceId), Record.class);
        if (CollectionUtils.isNotEmpty(oldRecords)) {
            advanceQtyMap.putAll(oldRecords.stream().collect(Collectors.toMap(e -> e.get(OnlineInvoiceAdvanceApply::getAdvanceApplyId),
                    e -> BigDecimalUtil.sub(BigDecimal.ZERO, e.get(OnlineInvoiceAdvanceApply::getCurWrittenOffAmount)))));
        }
    }

    private void writeBackAdvance(Map<Long, BigDecimal> advanceQtyMap) {
        if (MapUtils.isEmpty(advanceQtyMap)) {
            return;
        }
        List<Long> advanceApplyDetailIds = new ArrayList<>();
        advanceQtyMap.forEach((k, v) -> {
            if (v.compareTo(BigDecimal.ZERO) != 0) {
                advanceApplyDetailIds.add(k);
            }
        });
        if (CollectionUtils.isEmpty(advanceApplyDetailIds)) {
            return;
        }

        List<Record> AdvanceApplyRecords = qlService.queryByWrapper(QlWrappers.query("AdvanceApply")
                .in(AdvanceApply::getAdvanceApplyId, advanceApplyDetailIds), Record.class);
        AdvanceApplyRecords.forEach(e -> {
            BigDecimal changeAmount = advanceQtyMap.get(e.get(AdvanceApply::getAdvanceApplyId));
            BigDecimal writtenOffAmount = BigDecimalUtil.add(e.get(AdvanceApply::getWrittenOffAmount), changeAmount);
            BigDecimal unWrittenOffAmount = BigDecimalUtil.sub(e.get(AdvanceApply::getUnWrittenOffAmount), changeAmount);
            BigDecimal advanceAmount = e.get(AdvanceApply::getIncludeTaxAmount);
            if (writtenOffAmount.compareTo(advanceAmount) > 0 || writtenOffAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new BaseException("核销金额不能大于未核销金额");
            }
            List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update("AdvanceApplyDetail")
                    .set(AdvanceApply::getVersion, e.get(AdvanceApply::getVersion) + 1)
                    .set(AdvanceApply::getWrittenOffAmount, writtenOffAmount)
                    .set(AdvanceApply::getUnWrittenOffAmount, unWrittenOffAmount)
                    .eq(AdvanceApply::getAdvanceApplyId, e.get(AdvanceApply::getAdvanceApplyId))
                    .eq(AdvanceApply::getVersion, e.get(AdvanceApply::getVersion)));
            Assert.notEmpty(ids, "数据已发生变化，请重试");
        });
    }


    private void getDetailHistory(Long onlineInvoiceId, Map<Long, BigDecimal> noticeQtyMap, Map<Long, BigDecimal> checkQtyMap) {
        if (onlineInvoiceId == null) {
            return;
        }
        List<Record> oldRecords = qlService.queryByWrapper(QlWrappers.query(OnlineInvoiceDetail.class.getSimpleName())
                .eq(OnlineInvoiceDetail::getOnlineInvoiceId, onlineInvoiceId), Record.class);
        if (CollectionUtils.isNotEmpty(oldRecords)) {
            oldRecords.forEach(e -> {
                BigDecimal releaseQty = e.get(ExtOnlineInvoiceDetail::getInvoiceQuantity);
                if (OnlineInvoiceSourceEnum.INVOICE_NOTICE.name().equals(e.get(ExtOnlineInvoiceDetail::getExtSource))) {
                    noticeQtyMap.put(e.get(ExtOnlineInvoiceDetail::getInvoiceDetailId), releaseQty);
                } else {
                    checkQtyMap.put(e.get(ExtOnlineInvoiceDetail::getExtCheckDetailId), releaseQty);
                }
            });
        }
    }

    private void writeBackDetail(Map<Long, BigDecimal> noticeQtyMap, Map<Long, BigDecimal> checkQtyMap) {
        if (MapUtils.isNotEmpty(noticeQtyMap)) {
            List<Long> noticeDetailIds = new ArrayList<>();
            noticeQtyMap.forEach((k, v) -> {
                if (v.compareTo(BigDecimal.ZERO) != 0) {
                    noticeDetailIds.add(k);
                }
            });
            if (CollectionUtils.isEmpty(noticeDetailIds)) {
                return;
            }

            List<Record> invoiceNoticeDetails = qlService.queryByWrapper(QlWrappers.query(InvoiceNoticeDetail.class.getSimpleName())
                    .in(InvoiceNoticeDetail::getInvoiceDetailId, noticeDetailIds), Record.class);
            invoiceNoticeDetails.forEach(e -> {
                BigDecimal changeAmount = noticeQtyMap.get(e.get(InvoiceNoticeDetail::getInvoiceDetailId));
                BigDecimal notInvoiceQty = BigDecimalUtil.add(e.get(InvoiceNoticeDetail::getNotInvoiceQuantity), changeAmount);
                BigDecimal invoiceQty = e.get(InvoiceNoticeDetail::getInvoiceQuantity);
                if (notInvoiceQty.compareTo(invoiceQty) > 0 || notInvoiceQty.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BaseException("对账单开票数量不能大于未开票数量");
                }
                List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update(InvoiceNoticeDetail.class.getSimpleName())
                        .set(InvoiceNoticeDetail::getVersion, e.get(InvoiceNoticeDetail::getVersion) + 1)
                        .set(InvoiceNoticeDetail::getNotInvoiceQuantity, notInvoiceQty)
                        .eq(InvoiceNoticeDetail::getInvoiceDetailId, e.get(InvoiceNoticeDetail::getInvoiceDetailId))
                        .eq(InvoiceNoticeDetail::getVersion, e.get(InvoiceNoticeDetail::getVersion)));
                Assert.notEmpty(ids, "数据已发生变化，请重试");

            });
        } else if (MapUtils.isNotEmpty(checkQtyMap)) {
            List<Long> checkDetailIds = new ArrayList<>();
            checkQtyMap.forEach((k, v) -> {
                if (v.compareTo(BigDecimal.ZERO) != 0) {
                    checkDetailIds.add(k);
                }
            });
            if (CollectionUtils.isEmpty(checkDetailIds)) {
                return;
            }

            List<Record> checkOrderDetails = qlService.queryByWrapper(QlWrappers.query(CheckOrderDetail.class.getSimpleName())
                    .in(CheckOrderDetail::getCheckOrderDetailId, checkDetailIds), Record.class);
            checkOrderDetails.forEach(e -> {
                BigDecimal changeAmount = checkQtyMap.get(e.get(CheckOrderDetail::getCheckOrderDetailId));
                BigDecimal invoiceQty = BigDecimalUtil.sub(e.get(CheckOrderDetail::getInvoiceQty), changeAmount);
                BigDecimal checkQty = e.get(CheckOrderDetail::getCheckQty);
                if (invoiceQty.compareTo(checkQty) > 0 || invoiceQty.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BaseException("验收单开票数量不能大于未开票数量");
                }
                List<Serializable> ids = qlService.updateByWrapper(QlWrappers.update(CheckOrderDetail.class.getSimpleName())
                        .set(CheckOrderDetail::getVersion, e.get(CheckOrderDetail::getVersion) + 1)
                        .set(CheckOrderDetail::getInvoiceQty, invoiceQty)
                        .eq(CheckOrderDetail::getCheckOrderId, e.get(CheckOrderDetail::getCheckOrderId))
                        .eq(CheckOrderDetail::getVersion, e.get(CheckOrderDetail::getVersion)));
                Assert.notEmpty(ids, "数据已发生变化，请重试");
            });
        }
    }

    private void calcAmount(ExtOnlineInvoiceSaveDTO onlineInvoiceSaveDTO, List<ExtOnlineInvoiceDetail> detailList,
                            List<OnlineInvoiceAdvanceApplyDTO> advanceList, List<ExtOnlineInvoicePunishDTO> punishList,
                            List<OnlineInvoiceOcrInvoiceDTO> invoiceList) {
        AtomicReference<BigDecimal> taxTotalAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> noTaxTotalAmount = new AtomicReference<>(BigDecimal.ZERO);
        // 明细金额
        AtomicInteger lineNum = new AtomicInteger(1);
        detailList.forEach(e -> {
            e.setLineNum(lineNum.getAndIncrement());
            BigDecimal invoiceQuantity = e.getInvoiceQuantity();
            BigDecimal noTaxPrice = e.getUnitPriceExcludingTax();
            BigDecimal adjustAmount = e.getExtAdjustAmount();
            BigDecimal taxRate = BigDecimalUtil.add(BigDecimal.ONE, BigDecimalUtil.div(e.getTaxRate(), 100));
            BigDecimal noTaxAmount = BigDecimalUtil.mul(noTaxPrice, invoiceQuantity);
            BigDecimal taxAmount = BigDecimalUtil.add(noTaxAmount, adjustAmount).multiply(taxRate);
            e.setTaxAmount(taxAmount);
            e.setNoTaxAmount(noTaxAmount);
            e.setTax(BigDecimalUtil.sub(taxAmount, noTaxAmount));
            if (OnlineInvoiceSourceEnum.INVOICE_NOTICE.name().equals(e.getExtSource())
                    && CeeaWarehousingReturnDetailEnum.RETURN.name().equals(e.getType())) {
                noTaxTotalAmount.set(BigDecimalUtil.sub(noTaxTotalAmount.get(), noTaxAmount));
                taxTotalAmount.set(BigDecimalUtil.sub(taxTotalAmount.get(), taxAmount));
            } else {
                noTaxTotalAmount.set(BigDecimalUtil.add(noTaxTotalAmount.get(), noTaxAmount));
                taxTotalAmount.set(BigDecimalUtil.add(taxTotalAmount.get(), taxAmount));
            }
        });
        // 奖惩金额
        punishList.forEach(e -> {
            taxTotalAmount.set(BigDecimalUtil.add(taxTotalAmount.get(), e.getActualAssessmentAmountY()));
            noTaxTotalAmount.set(BigDecimalUtil.add(noTaxTotalAmount.get(), e.getActualAssessmentAmountN()));
        });
        // 预付款金额
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        AtomicReference<BigDecimal> payAmount = new AtomicReference<>(BigDecimal.ZERO);
        advanceList.forEach(e -> {
            e.setAppliedId(loginAppUser.getUserId());
            e.setAppliedBy(loginAppUser.getUsername());
            e.setAppliedDate(new Date());
            payAmount.set(BigDecimalUtil.add(payAmount.get(), e.getCurWrittenOffAmount()));
        });
        onlineInvoiceSaveDTO.setUnPaidAmount(taxTotalAmount.get().subtract(payAmount.get()));

        // 单据总金额
        taxTotalAmount.set(taxTotalAmount.get().setScale(2, BigDecimal.ROUND_HALF_UP));
        noTaxTotalAmount.set(noTaxTotalAmount.get().setScale(2, BigDecimal.ROUND_HALF_UP));
        BigDecimal totalTax = BigDecimalUtil.sub(taxTotalAmount.get(), noTaxTotalAmount.get());

        // 发票金额
        AtomicInteger invoiceRow = new AtomicInteger(1);
        AtomicReference<BigDecimal> invoiceTaxAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> invoiceNoTaxAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> invoiceTotalTax = new AtomicReference<>(BigDecimal.ZERO);
        if (CollectionUtils.isNotEmpty(invoiceList)) {
            invoiceList.forEach(e -> {
                e.setRowNum(invoiceRow.getAndIncrement());
                invoiceTaxAmount.set(BigDecimalUtil.add(invoiceTaxAmount.get(), e.getTotalAmount()));
                // 普票未税金额取含税金额
                if (COMMON_INVOICE_TYPE.contains(e.getInvoiceType())) {
                    invoiceNoTaxAmount.set(BigDecimalUtil.add(invoiceNoTaxAmount.get(), e.getTotalAmount()));
                } else {
                    invoiceNoTaxAmount.set(BigDecimalUtil.add(invoiceNoTaxAmount.get(), e.getNoTaxTotalAmount()));
                }
                invoiceTotalTax.set(BigDecimalUtil.add(invoiceTotalTax.get(), e.getTotalTax()));
            });

            if (InvoiceStatus.FINAL_REVIEW_APPROVED.name().equals(onlineInvoiceSaveDTO.getInvoiceStatus())) {
                if (taxTotalAmount.get().compareTo(invoiceTaxAmount.get()) != 0) {
                    throw new BaseException("应开票含税总金额与发票含税总金额不一致");
                }
                if (noTaxTotalAmount.get().compareTo(invoiceNoTaxAmount.get()) != 0) {
                    throw new BaseException("应开票未税总金额与发票未税总金额不一致");
                }
                if (totalTax.compareTo(invoiceTotalTax.get()) != 0) {
                    throw new BaseException("应开票总税额与发票总税额不一致");
                }
            }
        }

        // 赋值
        onlineInvoiceSaveDTO.setTaxTotalAmount(taxTotalAmount.get());
        onlineInvoiceSaveDTO.setExcluTaxTotalAmount(noTaxTotalAmount.get());
        onlineInvoiceSaveDTO.setTotalTax(totalTax);
        onlineInvoiceSaveDTO.setActualInvoiceAmountY(invoiceTaxAmount.get());
        onlineInvoiceSaveDTO.setActualInvoiceAmountN(invoiceNoTaxAmount.get());
        onlineInvoiceSaveDTO.setInvoiceTax(invoiceTotalTax.get());
        onlineInvoiceSaveDTO.setPaidAmount(payAmount.get());
    }

    private void pushInvoice(Record onlineInvoice,
                             List<Record> addInvoices,
                             List<Record> delInvoices) {
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        List<Map<String, Object>> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(addInvoices)) {
            addInvoices.forEach(e -> {
                // 发票报销状态 0：未报销 2：报销中 3：已报销
                list.add(getInvoiceReimburse(onlineInvoice, e, 2, loginAppUser));
            });
        }
        if(CollectionUtils.isNotEmpty(delInvoices)) {
            delInvoices.forEach(e -> {
                // 发票报销状态 0：未报销 2：报销中 3：已报销
                list.add(getInvoiceReimburse(onlineInvoice, e, 0, loginAppUser));
            });
        }

        if (CollectionUtils.isNotEmpty(list)) {
            log.info("推送发票报销接口，params:{}", JSONUtil.toJsonStr(list));
            Object result = pjProjectExtClient.reimburse(list);
            log.info("推送发票报销接口，result:{}", JSONUtil.toJsonStr(result));
        }
    }

    private Map<String, Object> getInvoiceReimburse(Record onlineInvoice,
                                                    Record invoiceDetail,
                                                    Integer reimburseState,
                                                    LoginAppUser loginAppUser) {
        Map<String, Object> map = new HashMap<>(16);
        //公司代码
        map.put("enterpriseCode", onlineInvoice.get(ExtOnlineInvoice::getExtPrincipalCode));
        //利润中心代码
        map.put("profitCenterCode",onlineInvoice.get(ExtOnlineInvoice::getExtProfitCenterCode));
        // 用户工号
        map.put("userId", loginAppUser.getUsername());
        //发票编号
        map.put("uuid", invoiceDetail.get(OnlineInvoiceOcrInvoiceDTO::getInvoiceName));
        //报销流水号
        map.put("reimburseSerialNo", onlineInvoice.get(ExtOnlineInvoice::getOnlineInvoiceNum) + invoiceDetail.get(OnlineInvoiceOcrInvoiceDTO::getRowNum));
        //发票金额
        map.put("occupiedAmount", invoiceDetail.get(OnlineInvoiceOcrInvoiceDTO::getTotalAmount));
        //实报税额
        map.put("occupiedTaxAmount", invoiceDetail.get(OnlineInvoiceOcrInvoiceDTO::getTotalTax));
        //单据名称
        map.put("billName", "开票单");
        //退税标识（1 调用进项 抵扣认证 2 调用进项退税认证
        map.put("isReturnTax", 1);
        //报销企业
        map.put("occupiedEnterprise", onlineInvoice.get(ExtOnlineInvoice::getExtPrincipalCode));
        //是否上市（是否上市 0非上市 1上市）
        map.put("listed", 0);
        //报销人（制单人）
        map.put("reimburseUserId", loginAppUser.getUsername());
        //来源系统
        map.put("occupiedSys", "SRM");
        //发票报销状态 0：未报销 2：报销中 3：已报销
        map.put("reimburseState", reimburseState);
        //单据编号
        map.put("billNo", onlineInvoice.get(ExtOnlineInvoice::getOnlineInvoiceNum));
        return map;
    }

}
