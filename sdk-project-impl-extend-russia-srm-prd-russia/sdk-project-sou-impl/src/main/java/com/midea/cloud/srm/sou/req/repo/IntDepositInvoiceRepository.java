package com.midea.cloud.srm.sou.req.repo;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.constants.SequenceCodeConstant;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Payload;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.meiql.core.util.ResultUtil;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.monitor.enums.YesOrNo;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.sou.req.SouIntDepositInvoice;
import com.midea.cloud.srm.model.sou.req.SouInvoiceInfo;
import com.midea.cloud.srm.model.sou.req.SouReqApply;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositInvoiceStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.InvoiceTypeEnum;
import com.midea.cloud.srm.model.sou.req.enums.SouReqApplyStatusEnum;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.req.service.SouIntDepositInvoiceService;
import com.nimbusds.jwt.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.*;

/**
 * <pre>
 *  功能名称
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/8 16:18
 *  修改内容:
 * </pre>
 */
@Slf4j
@Component
public class IntDepositInvoiceRepository extends CrudRepository {
    @Autowired
    protected QlOpenClient qlOpenClient;
    @Autowired
    protected QlService qlService;
    @Autowired
    protected PjProjectExtClient pjProjectExtClient;
    @Autowired
    private SouIntDepositInvoiceService souIntDepositInvoiceService;

    @Autowired
    public BaseClient baseClient;

    public IntDepositInvoiceRepository() {
        //注册action
        this.register("submit", this::submit, this::beforeSubmit, this::afterSubmit, true, "提交");
        //注册action
        this.register("getInfo", this::getInfo, true, "意向金开票-弹窗详情");
    }

    private QlResult getInfo(QlQueryAction queryAction) {
        Record record = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords().get(0);
        SouIntDepositInvoice souIntDepositInvoice = new SouIntDepositInvoice();
        if (ObjectUtil.isEmpty(record.get(SouIntDepositInvoice::getInvoiceId))) {
            //获取开票信息
            List<Record> invoiceInfoList = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_INVOICE_INFO)
                            .eq(SouInvoiceInfo::getVendorCode, AppUserUtil.getVendorCode())
                    , Record.class);
            //获取寻源头表信息
            SouReqHead souReqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, record.get(SouIntDepositInvoice::getReqHeadId), SouReqHead.class);
            Assert.isTrue(ObjectUtil.isNotEmpty(invoiceInfoList), "请先维护开票信息");
            Record invoiceInfo = invoiceInfoList.get(0);
            souIntDepositInvoice.setInvoiceType(record.get(SouIntDepositInvoice::getInvoiceType));
            //如果是开的红票，则需要查最新的普通发票记录
            if (record.get(SouIntDepositInvoice::getInvoiceType).equals(InvoiceTypeEnum.RED_INVOICE.getCode())) {
                SouIntDepositInvoice params = SouIntDepositInvoice.builder()
                        .invoiceType(InvoiceTypeEnum.INVOICE.getCode())
                        .reqHeadId(record.get(SouIntDepositInvoice::getReqHeadId))
                        .vendorId(invoiceInfo.get(SouInvoiceInfo::getVendorId))
                        .build();
                SouIntDepositInvoice latestDepositInvoice = souIntDepositInvoiceService.getLatestData(params);
                Assert.isTrue(ObjectUtil.isNotEmpty(latestDepositInvoice), "未申请普通发票，不允许申请红字发票");
                SouIntDepositInvoice redIntDepositInvoice = souIntDepositInvoiceService.getByFromDepositInvoiceId(latestDepositInvoice.getInvoiceId());
                Assert.isTrue(ObjectUtil.isEmpty(redIntDepositInvoice), "已有对冲的红字发票记录，不可重新发起红字发票申请");
                souIntDepositInvoice.setFromDepositInvoiceId(latestDepositInvoice.getInvoiceId());
                souIntDepositInvoice.setFromDepositInvoiceNo(latestDepositInvoice.getInvoiceNo());
            }
            //根据寻源单获取基本信息
            souIntDepositInvoice.setApplyId(record.get(SouIntDepositInvoice::getApplyId));
            souIntDepositInvoice.setProjectName(souReqHead.getProjectName());
            souIntDepositInvoice.setReqHeadId(souReqHead.getReqHeadId());
            souIntDepositInvoice.setVendorId(invoiceInfo.get(SouInvoiceInfo::getVendorId));
            souIntDepositInvoice.setVendorCode(invoiceInfo.get(SouInvoiceInfo::getVendorCode));
            souIntDepositInvoice.setVendorName(invoiceInfo.get(SouInvoiceInfo::getVendorName));
            souIntDepositInvoice.setInvoiceCompany(invoiceInfo.get(SouInvoiceInfo::getVendorName));
            souIntDepositInvoice.setTaxPayer(invoiceInfo.get(SouInvoiceInfo::getTaxPayer));
            souIntDepositInvoice.setPhone(invoiceInfo.get(SouInvoiceInfo::getPhone));
            souIntDepositInvoice.setBankName(invoiceInfo.get(SouInvoiceInfo::getBankName));
            souIntDepositInvoice.setBankAccount(invoiceInfo.get(SouInvoiceInfo::getBankAccount));
            souIntDepositInvoice.setInvoiceReceiverEmail(invoiceInfo.get(SouInvoiceInfo::getEmail));
            souIntDepositInvoice.setPrice(souReqHead.getDepositAmount());
            souIntDepositInvoice.setStatus(IntDepositInvoiceStatusEnum.DRAFT.getCode());
            //地址
            souIntDepositInvoice.setAddress(invoiceInfo.get(SouInvoiceInfo::getAddress));
        } else {
            souIntDepositInvoice = qlService.readByKey(MqlType.SOU_INVOICE_INFO, record.get(SouIntDepositInvoice::getInvoiceId), SouIntDepositInvoice.class);
        }
        return ResultUtil.build(queryAction, "reqHeadId", Collections.singletonList(souIntDepositInvoice), false);
    }

    /**
     * 1.只要有供应发起开票类型为【发票开具】的单据后，不允许再次发起该类型的发票。
     * 2.存在已提交、开具失败状态时，该项目不允许发起新的开票申请单
     * 3.仅发票开具成功后，可发起红字发票开票申请，仅可发起一次，红字发票开具成功后，可重新发起开票申请。
     *
     * @param qlQueryAction
     * @param payload
     */
    private void beforeSubmit(QlQueryAction qlQueryAction, Payload payload) {
        List<Record> records = payload.asRecords();
        SouIntDepositInvoice params = MeiQl.toValue(records.get(0), SouIntDepositInvoice.class);
        InvoiceTypeEnum invoiceTypeEnum = InvoiceTypeEnum.valueOf(records.get(0).get(SouIntDepositInvoice::getInvoiceType));
        switch (invoiceTypeEnum) {
            case INVOICE:
                // 发票
                //查询最新的一条红字发票记录
                SouIntDepositInvoice latestDepositInvoice = souIntDepositInvoiceService.getLatestData(params);
                if (ObjectUtil.isNotEmpty(latestDepositInvoice)) {
                    SouIntDepositInvoice redSouIntDepositInvoice = souIntDepositInvoiceService.getByFromDepositInvoiceId(latestDepositInvoice.getInvoiceId());
                    //如果是开票失败的则可以重新开票
                    if(IntDepositInvoiceStatusEnum.FAIL_INVOICED.getCode().equals(latestDepositInvoice.getStatus())){
                        break;
                    }

                    //最新的开票记录如果不为空，则需要判断是否已经开红票对冲，并且红票状态为已开具。
                    if (ObjectUtil.isEmpty(redSouIntDepositInvoice) || !redSouIntDepositInvoice.getStatus().equals(IntDepositInvoiceStatusEnum.INVOICED.getCode())) {
                        throw new BaseException("申请开票失败：不允许重复开票。");
                    }
                }
                break;
            case RED_INVOICE:
                // 红字发票
                //如果是红字发票还需要判断发票的开具状态是否为已开具
                SouIntDepositInvoice souIntDepositInvoice = qlService.readByKey(MqlType.SOU_DEPOSIT_INVOICE_BUYER, records.get(0).get(SouIntDepositInvoice::getFromDepositInvoiceId), SouIntDepositInvoice.class);
                if (ObjectUtil.isEmpty(souIntDepositInvoice)) {
                    throw new IllegalArgumentException("对冲的发票数据不存在。");
                }
                if (!souIntDepositInvoice.getStatus().equals(IntDepositInvoiceStatusEnum.INVOICED.getCode())) {
                    throw new BaseException("申请开票失败：不允许重复开票。");
                }
                //查询最新的一条开票记录
                SouIntDepositInvoice latestRedDepositInvoice = souIntDepositInvoiceService.getLatestData(params);
                if (ObjectUtil.isNotEmpty(latestRedDepositInvoice)) {
                    throw new BaseException("申请开票失败：不允许重复开票。");
                }
                break;
            default:
                break;
        }
    }

    @Transactional
    public void afterSubmit(QlQueryAction qlQueryAction, QlResult qlResult, Map<String, Collection<Record>> stringCollectionMap) {
        log.info("qlQueryAction:"+JSONObject.toJSONString(qlQueryAction));
        log.info("qlResult:"+JSONObject.toJSONString(qlResult));
        log.info("stringCollectionMap:"+JSONObject.toJSONString(stringCollectionMap));
        List<Object> list = (ArrayList<Object>)qlQueryAction.getPayload();
        SouIntDepositInvoice invoice = MeiQl.toValue(list.get(0), SouIntDepositInvoice.class);

        JSONObject result = souIntDepositInvoiceService.createInvoice(invoice);
    }

    private QlResult submit(QlQueryAction queryAction) {
        List<Record> records = PayloadWrapper.of(queryAction.getType(), queryAction.getPayload()).asRecords();
        records.forEach(record -> {
            record.set(SouIntDepositInvoice::getStatus, IntDepositInvoiceStatusEnum.INVOICED.getCode());
            String num = baseClient.seqGen("SEQ_INVOICE_CODE");
            record.set(SouIntDepositInvoice::getInvoiceNo, num);
        });
        return super.save(ProxyQlQueryAction.proxy(queryAction, DefaultAction.SAVE.value(), records));
    }


    @Override
    public QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        QlCondition condition = MeiQl.newCondition();
        condition.eq("vendorId", AppUserUtil.getLoginAppUser().getCompanyId());
        return condition;
    }
}
