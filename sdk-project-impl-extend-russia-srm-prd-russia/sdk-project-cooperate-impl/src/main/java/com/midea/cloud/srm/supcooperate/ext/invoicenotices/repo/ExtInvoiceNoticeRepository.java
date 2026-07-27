package com.midea.cloud.srm.supcooperate.ext.invoicenotices.repo;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.BigDecimalUtil;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.srm.feign.base.service.NoticeSendGlobalClientService;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.material.MaterialItem;
import com.midea.cloud.srm.model.base.noticetemplate.dto.NoticeSendDTO;
import com.midea.cloud.srm.model.common.enums.UserType;
import com.midea.cloud.srm.model.pj.base.organization.dto.OrganizationEditDto;
import com.midea.cloud.srm.model.pj.base.organization.entity.OrgCollectInfo;
import com.midea.cloud.srm.model.pj.base.organization.entity.OrgInvoiceInfo;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.suppliercooperate.deliverynote.entry.DeliveryNote;
import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.InvoiceNotice;
import com.midea.cloud.srm.model.suppliercooperate.invoice.entity.InvoiceNoticeDetail;
import com.midea.cloud.srm.model.suppliercooperate.invoice.enums.InvoiceNoticeStatus;
import com.midea.cloud.srm.model.suppliercooperate.mql.enums.PurchaseSchemaEnum;
import com.midea.cloud.srm.model.suppliercooperate.order.entry.WarehousingReturnDetail;
import com.midea.cloud.srm.model.suppliercooperate.storagereturn.openapi.enums.StorageReturnTypeEnum;
import com.midea.cloud.srm.supcooperate.ext.checkorders.dto.CheckOrderSaveDTO;
import com.midea.cloud.srm.supcooperate.ext.deliverynotes.dto.ExtDeliveryNote;
import com.midea.cloud.srm.supcooperate.ext.invoicenotices.dto.ExtInvoiceNotice;
import com.midea.cloud.srm.supcooperate.ext.invoicenotices.dto.ExtInvoiceNoticeDetail;
import com.midea.cloud.srm.supcooperate.ext.invoicenotices.dto.ExtInvoiceNoticeSaveDTO;
import com.midea.cloud.srm.supcooperate.ext.invoicenotices.enums.ExtInvoiceNoticeStatusEnum;
import com.midea.cloud.srm.supcooperate.ext.order.PurchaseUtils;
import com.midea.cloud.srm.supcooperate.meiql.base.PurchaseRepository;
import com.midea.cloud.srm.supcooperate.meiql.util.PurchaseMqlUtils;
import com.midea.cloud.srm.supcooperate.order.service.IWarehousingReturnDetailService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zenghx2
 */
@Component
public class ExtInvoiceNoticeRepository extends PurchaseRepository<ExtInvoiceNotice> {

    @Autowired
    private NoticeSendGlobalClientService noticeSendGlobalClientService;

    @Autowired
    private RbacClient rbacClient;


    public ExtInvoiceNoticeRepository() {
        super("InvoiceNotice", "invoiceNoticeId", "对账单");
        this.register("extCreateByStorageReturn", this::extCreateByStorageReturn, true, "选入库退货明细创建对账单");
        this.register("extSaveOrUpdate", this::extSaveOrUpdate,null,this::afterExtSaveOrUpdate,true,false, "暂存/提交");
        this.register("extSubmit", this::extSubmit, null,this::afterExtSubmit,true, false,"批量提交");
        this.register("extSupplierConfirm", this::extSupplierConfirm, true, "供应商确认");
        this.register("extSupplierRefuse", this::extSupplierRefuse, true, "供应商拒绝");
        this.register("extWithdraw", this::extWithdraw, true, "撤回");
    }




    /**
     * 撤回
     * @param action
     * @return
     */
    private QlResult extWithdraw(QlQueryAction action) {
        //解析前端请求参数为 Record 集合
        List<Record> records = getRecords(action);
        //校验集合不允许空
        AssertUtils.notEmpty(records, "请求参数不允许为空");
        //校验全部状态为待供应商确认
        List<Record> invoidList = qlService.queryByWrapper(QlWrappers.query(action.getType()).in(InvoiceNotice::getInvoiceNoticeId, records.stream().map(r -> r.get(InvoiceNotice::getInvoiceNoticeId)).collect(Collectors.toList())), Record.class);
        AssertUtils.notEmpty(invoidList, "请求参数有误，查询不到数据");
        AssertUtils.isFalse(invoidList.stream().anyMatch(r -> !ExtInvoiceNoticeStatusEnum.WAITING.name().equals(r.get(ExtInvoiceNotice::getExtStatus))), "请求参数有误，勾选行必须全部为待供应商确认数据");

        qlService.updateByWrapper(QlWrappers.update(action.getType()).in(ExtInvoiceNotice::getInvoiceNoticeId, records.stream().map(r -> r.get(InvoiceNotice::getInvoiceNoticeId)).collect(Collectors.toList()))
        .set(ExtInvoiceNotice::getExtStatus, null).set(InvoiceNotice::getInvoiceNoticeStatus, InvoiceNoticeStatus.DRAFT.name()));
        return QlResult.empty();
    }

    @Autowired
    private IWarehousingReturnDetailService warehousingReturnDetailService;
    @Autowired
    private PjProjectExtClient pjClient;

    @Override
    protected void afterQuery(QlQueryAction queryAction, Collection<Record> records) {
        super.afterQuery(queryAction, records);
        records.stream().forEach(e -> {
            if (e.get(ExtInvoiceNotice::getExtStatus) == null) {
                e.put(ExtInvoiceNotice::getExtStatus, e.get(ExtInvoiceNotice::getInvoiceNoticeStatus));
            }
        });
    }

    /**
     * 删除
     */
    @Override
    protected void beforeDelete(QlQueryAction queryAction, Collection<Record> records) {
        // 回写对账单
        Record record = records.iterator().next();
        Record invoiceNotice = qlService.readByKey("InvoiceNotice", record.get(InvoiceNotice::getInvoiceNoticeId), Record.class);
        Assert.notNull(invoiceNotice, "对账单不存在");
        List<Record> invoiceNoticeDetails = qlService.queryByWrapper(QlWrappers.query("InvoiceNoticeDetail")
                .eq(InvoiceNoticeDetail::getInvoiceNoticeId, record.get(InvoiceNotice::getInvoiceNoticeId)), Record.class);
        if (CollectionUtils.isNotEmpty(invoiceNoticeDetails)) {
            List<Long> storageReturnIds = invoiceNoticeDetails.stream().map(e -> e.get(InvoiceNoticeDetail::getWarehousingReturnDetailId)).collect(Collectors.toList());
            List<Record> storages = qlService.readByKeys(PurchaseSchemaEnum.STORAGE_RETURN.getType(), storageReturnIds, Record.class);
            storages.forEach(e -> {
                writebackStorageReturn(e, false);
            });
        }
        super.beforeDelete(queryAction, records);
    }

    /**
     * 选择出入库单创建
     */
    private QlResult extCreateByStorageReturn(QlQueryAction action) {
        Record record = getRecord(action);
        List<Long> storageReturnIds = (List<Long>)record.get("storageReturnIds");
        Assert.notNull(storageReturnIds, "出入库明细ID不能能为空");
        List<Record> storages = qlService.readByKeys(PurchaseSchemaEnum.STORAGE_RETURN.getType(), storageReturnIds, Record.class);
        PurchaseMqlUtils.checkEntityPK(storageReturnIds, storages, "出入库明细");
        storages.forEach(e -> {
            Assert.isTrue(e.get(WarehousingReturnDetail::getNotInvoiceQuantity).compareTo(BigDecimal.ZERO) > 0, "存在已对账数据");
        });

        // 账期
        LocalDate startDate = LocalDate.now().plusMonths(-1L).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = LocalDate.now().plusMonths(-1L).with(TemporalAdjusters.lastDayOfMonth());

        // 分组创建
        Map<String, List<Record>> storageMap = storages.stream().collect(Collectors.groupingBy(e -> e.get(WarehousingReturnDetail::getOrgId) + "-" + e.get(WarehousingReturnDetail::getVendorId) + "-" + e.get(WarehousingReturnDetail::getCurrencyName)));

        // 供应商联系人信息
        List<String> deliveryNumbers = storageMap.values().stream().map(e -> e.get(0).get(WarehousingReturnDetail::getDeliveryNumber)).distinct().collect(Collectors.toList());
        Map<Long, List<Record>> vendorMap = qlService.queryByWrapper(QlWrappers.query(PurchaseSchemaEnum.DELIVERY_NOTE.getType())
                .select(ExtDeliveryNote::getVendorId, ExtDeliveryNote::getExtVendorContacts, ExtDeliveryNote::getExtVendorPhone)
                .in(DeliveryNote::getDeliveryNumber, deliveryNumbers), Record.class).stream().collect(Collectors.groupingBy(e -> e.get(ExtDeliveryNote::getVendorId)));

        // 物料规格
        Set<Long> materialIds = storages.stream().map(e -> e.getLong("itemId")).collect(Collectors.toSet());
        List<MaterialItem> materialItems = baseClient.listMaterialByIdBatch(materialIds);
        Map<Long, String> materialMap = materialItems.stream().collect(Collectors.toMap(MaterialItem::getMaterialId, MaterialItem::getDescription));
        // 对账单
        storageMap.forEach((k, v) -> {
            // 封装对账明细
            AtomicInteger lineNum = new AtomicInteger(1);
            List<ExtInvoiceNoticeDetail> details = v.stream().map(e -> {
                BigDecimal taxPrice = e.get(WarehousingReturnDetail::getUnitPriceContainingTax);
                BigDecimal noTaxPrice = e.get(WarehousingReturnDetail::getUnitPriceExcludingTax);
                BigDecimal qty = e.get(WarehousingReturnDetail::getReceiveNum);
                BigDecimal taxAmount = BigDecimalUtil.mul(taxPrice, qty);
                /**
                 * 保留2位小数
                 */
                taxAmount = taxAmount.setScale(2, RoundingMode.HALF_UP);
                BigDecimal noTaxAmount = BigDecimalUtil.mul(noTaxPrice, qty);
                String extMaterialModel = materialMap.get(e.get(WarehousingReturnDetail::getItemId));
                ExtInvoiceNoticeDetail invoiceNoticeDetail = new ExtInvoiceNoticeDetail();
                invoiceNoticeDetail.setInvoiceDetailNum(lineNum.getAndIncrement())
                        .setOrgId(e.get(WarehousingReturnDetail::getOrgId)).setOrgName(e.get(WarehousingReturnDetail::getOrgName)).setOrgCode(e.get(WarehousingReturnDetail::getOrgCode))
                        .setOrganizationId(e.get(WarehousingReturnDetail::getOrganizationId)).setOrganizationCode(e.get(WarehousingReturnDetail::getOrganizationCode)).setOrganizationName(e.get(WarehousingReturnDetail::getOrganizationName))
                        .setVendorId(e.get(WarehousingReturnDetail::getVendorId)).setVendorCode(e.get(WarehousingReturnDetail::getVendorCode)).setVendorName(e.get(WarehousingReturnDetail::getVendorName))
                        .setWarehousingReturnDetailId(e.get(WarehousingReturnDetail::getWarehousingReturnDetailId))
                        .setReceiveOrderNo(e.get(WarehousingReturnDetail::getReceiveOrderNo)).setType(e.get(WarehousingReturnDetail::getType)).setReceiveDate(e.get(WarehousingReturnDetail::getReceiveDate))
                        .setReceiveOrderLineNo(e.get(WarehousingReturnDetail::getReceiveOrderLineNo))
                        .setOrderNumber(e.get(WarehousingReturnDetail::getOrderNumber)).setLineNum(e.get(WarehousingReturnDetail::getLineNum))
                        .setReceiveNum(qty).setInvoiceQuantity(qty).setNotInvoiceQuantity(qty)
                        .setItemId(e.get(WarehousingReturnDetail::getItemId)).setItemName(e.get(WarehousingReturnDetail::getItemName)).setItemCode(e.get(WarehousingReturnDetail::getItemCode))
                        .setCurrencyId(e.get(WarehousingReturnDetail::getCurrencyId)).setCurrencyCode(e.get(WarehousingReturnDetail::getCurrencyCode)).setCurrencyName(e.get(WarehousingReturnDetail::getCurrencyName))
                        .setTaxRate(e.get(WarehousingReturnDetail::getTaxRate)).setTaxKey(e.get(WarehousingReturnDetail::getTaxKey))
                        .setUnit(e.get(WarehousingReturnDetail::getUnit))
                        .setUnitPriceContainingTax(taxPrice).setUnitPriceExcludingTax(noTaxPrice).setTaxAmount(taxAmount).setNoTaxAmount(noTaxAmount).setTax(BigDecimalUtil.sub(taxAmount, noTaxAmount));
                invoiceNoticeDetail.setExtMaterialModel(extMaterialModel);
                return invoiceNoticeDetail;
            }).collect(Collectors.toList());

            // 计算总金额
            BigDecimal totalTaxAmount = details.stream().map(e -> getCalcAmount(e.getType(), e.getTaxAmount())).reduce(BigDecimal::add).get();
            BigDecimal totalNoTaxAmount = details.stream().map(e -> getCalcAmount(e.getType(), e.getNoTaxAmount())).reduce(BigDecimal::add).get();

            // 封装对账头
            Record storageReturn = v.get(0);
            Record vendor = vendorMap.get(storageReturn.get(WarehousingReturnDetail::getVendorId)).get(0);
            InvoiceNotice invoiceNotice = new ExtInvoiceNotice()
                    .setExtVendorContacts(vendor.get(ExtDeliveryNote::getExtVendorContacts)).setExtVendorPhone(vendor.get(ExtDeliveryNote::getExtVendorPhone))
                    .setInvoiceNoticeNumber(baseClient.seqGen(SequenceCodeConstant.SEQ_SSC_INVOICE_NOTICE_NUM))
                    .setInvoiceNoticeStatus(InvoiceNoticeStatus.DRAFT)
                    .setOrgId(storageReturn.get(WarehousingReturnDetail::getOrgId)).setOrgName(storageReturn.get(WarehousingReturnDetail::getOrgName)).setOrgCode(storageReturn.get(WarehousingReturnDetail::getOrgCode))
                    .setOrganizationId(storageReturn.get(WarehousingReturnDetail::getOrganizationId)).setOrganizationCode(storageReturn.get(WarehousingReturnDetail::getOrganizationCode)).setOrganizationName(storageReturn.get(WarehousingReturnDetail::getOrganizationName))
                    .setVendorId(storageReturn.get(WarehousingReturnDetail::getVendorId)).setVendorCode(storageReturn.get(WarehousingReturnDetail::getVendorCode)).setVendorName(storageReturn.get(WarehousingReturnDetail::getVendorName))
                    .setCeeaInvoiceDate(LocalDate.now())
                    .setCurrencyId(storageReturn.get(WarehousingReturnDetail::getCurrencyId)).setCurrencyCode(storageReturn.get(WarehousingReturnDetail::getCurrencyCode)).setCurrencyName(storageReturn.get(WarehousingReturnDetail::getCurrencyName))
                    .setCeeaIfSupplierConfirm(YesOrNo.YES.getValue())
                    .setUserType(UserType.BUYER)
                    .setTaxKey(storageReturn.get(WarehousingReturnDetail::getTaxKey)).setTaxRate(storageReturn.get(WarehousingReturnDetail::getTaxRate))
                    .setCeeaReceiveStartDate(startDate).setCeeaReceiveEndDate(endDate)
                    .setCeeaNoTaxTotalAmount(totalNoTaxAmount).setCeeaTaxTotalAmount(totalTaxAmount).setCeeaTotalTax(BigDecimalUtil.sub(totalTaxAmount, totalNoTaxAmount));

            // 保存对账单
            List<Serializable> invoiceNoticeIds = qlService.create(schemaType, Arrays.asList(invoiceNotice));

            // 保存对账明细
            details.stream().forEach(e -> {
                e.setInvoiceNoticeId((Long) invoiceNoticeIds.get(0));
            });
            qlService.create("InvoiceNoticeDetail", details);
        });

        // 更新出入库明细
        storages.forEach(e -> {
            writebackStorageReturn(e, true);
        });
        return QlResult.empty();
    }

    /**
     * 暂存、提交
     */
    private QlResult extSaveOrUpdate(QlQueryAction action) {
        List<Record> records = getRecords(action);
        Record record = getRecord(records);
        ExtInvoiceNoticeSaveDTO invoiceNoticeSaveDTO = BeanCopyUtil.convertWithExtensions(record, ExtInvoiceNoticeSaveDTO.class);
        if (InvoiceNoticeStatus.DRAFT != invoiceNoticeSaveDTO.getInvoiceNoticeStatus()
                && InvoiceNoticeStatus.SUBMITTED != invoiceNoticeSaveDTO.getInvoiceNoticeStatus()) {
            throw new BaseException("不支持的状态参数");
        }

        List<Long> newDetails = PurchaseMqlUtils.trimDeleteFlag(invoiceNoticeSaveDTO.getDetailList())
                .stream().map(e -> {
                    e.setNotInvoiceQuantity(e.getInvoiceQuantity());
                    return e.getWarehousingReturnDetailId();
                }).collect(Collectors.toList());
        Assert.notEmpty(newDetails, "入库退货明细不能为空");

        List<Long> oldDetails = new ArrayList<>();
        if (invoiceNoticeSaveDTO.getInvoiceNoticeId() != null) {
            Record invoiceNotice = qlService.readByKey("InvoiceNotice", invoiceNoticeSaveDTO.getInvoiceNoticeId(), Record.class);
            if (InvoiceNoticeStatus.DRAFT != invoiceNotice.get(InvoiceNotice::getInvoiceNoticeStatus)
                    && !ExtInvoiceNoticeStatusEnum.REFUSE.name().equals(invoiceNotice.get(ExtInvoiceNotice::getExtStatus))) {
                throw new BaseException("当前状态不能修改");
            }
            oldDetails = qlService.queryByWrapper(QlWrappers.query("InvoiceNoticeDetail")
                    .eq(InvoiceNoticeDetail::getInvoiceNoticeId, invoiceNotice.get(ExtInvoiceNotice::getInvoiceNoticeId)), Record.class)
                    .stream().map(e -> e.get(InvoiceNoticeDetail::getWarehousingReturnDetailId)).collect(Collectors.toList());
        }

        // 回写出入库
        List<Long> addDetails = new ArrayList<>(newDetails);
        // 新增
        addDetails.removeAll(oldDetails);
        // 删除
        oldDetails.removeAll(newDetails);
        List<Long> allDetails = new ArrayList<>(oldDetails);
        allDetails.addAll(addDetails);
        Map<Long, Record> detailMap = qlService.readByKeys(PurchaseSchemaEnum.STORAGE_RETURN.getType(), allDetails, Record.class)
                .stream().collect(Collectors.toMap(e -> e.get(WarehousingReturnDetail::getWarehousingReturnDetailId), e -> e));
        if (CollectionUtils.isNotEmpty(oldDetails)) {
            oldDetails.forEach(e -> {
                writebackStorageReturn(detailMap.get(e), false);
            });
        }
        if (CollectionUtils.isNotEmpty(addDetails)) {
            addDetails.forEach(e -> {
                writebackStorageReturn(detailMap.get(e), true);
            });
        }

        if (InvoiceNoticeStatus.SUBMITTED == record.get(InvoiceNotice::getInvoiceNoticeStatus)) {
            invoiceNoticeSaveDTO.setExtStatus(ExtInvoiceNoticeStatusEnum.WAITING.name());
        }

        record.putAll(MeiQl.toValue(invoiceNoticeSaveDTO, Record.class));
        return super.doSave(action, records);
    }

    private void afterExtSaveOrUpdate(QlQueryAction qlQueryAction, QlResult qlResult, Map<String, Collection<Record>> stringCollectionMap) {
        List<Record> records = getRecords(qlQueryAction);
        Record record = getRecord(records);
        ExtInvoiceNoticeSaveDTO invoiceNoticeSaveDTO = BeanCopyUtil.convertWithExtensions(record, ExtInvoiceNoticeSaveDTO.class);

        if (InvoiceNoticeStatus.SUBMITTED != invoiceNoticeSaveDTO.getInvoiceNoticeStatus()) {
            return;
        }
        User u = rbacClient.getUserByUserName(invoiceNoticeSaveDTO.getExtVendorPhone());
        sendMail(invoiceNoticeSaveDTO,u);
    }

    private void sendMail(ExtInvoiceNotice extInvoiceNotice,User u) {
        if(Objects.isNull(u)){
            return;
        }
        String msgTemplateCode = "SUP_CE_INVOICE_NOTICE";
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setMsgTemplateCode(msgTemplateCode);
        noticeSendDTO.setMsgUuid(UUID.randomUUID().toString());
        Map<String, Object> msgParams = new HashMap(15);
        msgParams.put(NoticeSendDTO.NOTICE_RECEIVER_INFO, u.getEmail());
        msgParams.put("extInvoiceCompany", extInvoiceNotice.getExtInvoiceCompany());
        msgParams.put("invoiceNoticeNumber", extInvoiceNotice.getInvoiceNoticeNumber());
        noticeSendDTO.setMsgParams(msgParams);
        noticeSendGlobalClientService.send(noticeSendDTO);

    }


    /**
     * 批量提交供应商确认
     */
    private QlResult extSubmit(QlQueryAction action) {
        Record record = getRecord(action);
        List<Long> invoiceNoticeIds = (List) record.get("invoiceNoticeIds");
        Assert.notEmpty(invoiceNoticeIds, "对账单id不能为空");
        List<Record> invoiceNotices = qlService.readByKeys(schemaType, invoiceNoticeIds, Record.class);
        invoiceNotices.forEach(e -> {
            Assert.isTrue(InvoiceNoticeStatus.DRAFT == e.get(InvoiceNotice::getInvoiceNoticeStatus), "存在非拟定状态单据");
            // 批量保存发票信息
            OrganizationEditDto orgDto = pjClient.findList(e.get(InvoiceNotice::getOrgId));
            if(orgDto != null){
                List<OrgInvoiceInfo> orgInvoiceInfoList = orgDto.getOrgInvoiceInfoList();
                List<OrgCollectInfo> orgCollectInfoList = orgDto.getOrgCollectInfoList();
                if(CollectionUtils.isNotEmpty(orgInvoiceInfoList)){
                    OrgInvoiceInfo invoiceInfo = orgInvoiceInfoList.get(0);
                    e.set(ExtInvoiceNotice::getExtInvoiceType, invoiceInfo.getInvoiceType());
                    e.set(ExtInvoiceNotice::getExtInvoiceCompany, invoiceInfo.getCompanyName());
                    e.set(ExtInvoiceNotice::getExtInvoiceOpeningName, invoiceInfo.getOpeningName());
                    e.set(ExtInvoiceNotice::getExtInvoiceOpeningAccount, invoiceInfo.getOpeningAccount());
                    e.set(ExtInvoiceNotice::getExtInvoiceTaxpayerNum, invoiceInfo.getTaxpayerNum());
                    e.set(ExtInvoiceNotice::getExtInvoicePhone, invoiceInfo.getPhone());
                    e.set(ExtInvoiceNotice::getExtInvoiceAddress, invoiceInfo.getAddress());
                    if(CollectionUtils.isNotEmpty(orgCollectInfoList)){
                        OrgCollectInfo orgCollectInfo = orgCollectInfoList.get(0);
                        e.set(ExtInvoiceNotice::getExtInvoiceReceiver, orgCollectInfo.getCollectPerson());
                        e.set(ExtInvoiceNotice::getExtInvoiceReceiveAddr, orgCollectInfo.getCollectAddress());
                        e.set(ExtInvoiceNotice::getCollectMail,orgCollectInfo.getCollectMail());
                    }
                    e.set(InvoiceNotice::getInvoiceNoticeStatus, InvoiceNoticeStatus.SUBMITTED);
                    e.set(ExtInvoiceNotice::getExtStatus, ExtInvoiceNoticeStatusEnum.WAITING.name());
                }
            }
        });
        qlService.update("InvoiceNotice", invoiceNotices);
        // 更新状态
        return QlResult.empty();
    }

    private void afterExtSubmit(QlQueryAction qlQueryAction, QlResult qlResult, Map<String, Collection<Record>> stringCollectionMap) {
        Record record = getRecord(qlQueryAction);
        List<Long> invoiceNoticeIds = (List) record.get("invoiceNoticeIds");
        List<Record> records = qlService.readByKeys(schemaType, invoiceNoticeIds, Record.class);
        records.forEach(e-> {
            ExtInvoiceNotice extInvoiceNotice = BeanCopyUtil.convertWithExtensions(e, ExtInvoiceNotice.class);

            User u = rbacClient.getUserByUserName(extInvoiceNotice.getExtVendorPhone());
            sendMail(extInvoiceNotice,u);
        });
    }


    /**
     * 供应商确认
     */
    private QlResult extSupplierConfirm(QlQueryAction action) {
        Record invoiceNotice = doSupplierOperate(action, ExtInvoiceNoticeStatusEnum.CONFIRM);

        // 推送EAS
//        List<Record> invoiceNoticeDetails = qlService.queryByWrapper(QlWrappers.query("InvoiceNoticeDetail")
//                .eq(InvoiceNoticeDetail::getInvoiceNoticeId, invoiceNotice.get(InvoiceNotice::getInvoiceNoticeId)), Record.class);
//        List<Record> records = invoiceNoticeDetails.stream().map(e -> {
//            Record record = new Record();
//            record.put("billno", e.get(InvoiceNoticeDetail::getReceiveOrderNo));
//            record.put("seq", e.get(InvoiceNoticeDetail::getReceiveOrderLineNo));
//            return record;
//        }).collect(Collectors.toList());
//        pjProjectExtClient.pushInvoiceNotice(records);

        return QlResult.empty();
    }

    /**
     * 供应商取消
     */
    private QlResult extSupplierRefuse(QlQueryAction action) {
        doSupplierOperate(action, ExtInvoiceNoticeStatusEnum.REFUSE);
        return QlResult.empty();
    }

    /**
     * 供应商确认和取消操作
     */
    private Record doSupplierOperate(QlQueryAction action, ExtInvoiceNoticeStatusEnum extStatus) {
        Record record = getRecord(action);
        Long invoiceNoticeId = record.get(InvoiceNotice::getInvoiceNoticeId);
        String rejectReason = record.get(ExtInvoiceNotice::getRejectReason);
        Assert.notNull(invoiceNoticeId, "对账单id不能为空");
        Record invoiceNotice = qlService.readByKey("InvoiceNotice", invoiceNoticeId, Record.class);
        Assert.isTrue(InvoiceNoticeStatus.SUBMITTED == invoiceNotice.get(InvoiceNotice::getInvoiceNoticeStatus), "当前状态不能操作确认");
        Assert.isTrue(ExtInvoiceNoticeStatusEnum.WAITING.name().equals(invoiceNotice.get(ExtInvoiceNotice::getExtStatus)), "当前状态不能操作确认");

        qlService.updateByWrapper(QlWrappers.update(schemaType)
                .set(ExtInvoiceNotice::getExtStatus, extStatus.name())
                .set(StringUtils.isNoneBlank(rejectReason), ExtInvoiceNotice::getRejectReason, rejectReason)
                .eq(InvoiceNotice::getInvoiceNoticeId, invoiceNoticeId));
        return record;
    }

    /**
     * 回写入库退货明细
     */
    private void writebackStorageReturn(Record record, boolean used) {
        Assert.notNull(record, "出入库明细不存在");
        boolean success = warehousingReturnDetailService.update(new LambdaUpdateWrapper<WarehousingReturnDetail>()
                .set(WarehousingReturnDetail::getNotInvoiceQuantity, used ? BigDecimal.ZERO : record.get(WarehousingReturnDetail::getReceiveNum))
                .set(WarehousingReturnDetail::getVersion, PurchaseUtils.increaseVersion(record.get(WarehousingReturnDetail::getVersion)))
                .eq(WarehousingReturnDetail::getWarehousingReturnDetailId, record.get(WarehousingReturnDetail::getWarehousingReturnDetailId))
                .eq(WarehousingReturnDetail::getVersion, record.get(WarehousingReturnDetail::getVersion)));
        Assert.isTrue(success, "数据发生变化，请重试");
    }

    private BigDecimal getCalcAmount(String type, BigDecimal amount) {
        if (StorageReturnTypeEnum.RECEIVE.getValue().equals(type)) {
            return amount;
        } else {
            return BigDecimalUtil.sub(BigDecimal.ZERO, amount);
        }
    }

}
