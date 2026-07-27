package com.midea.cloud.srm.sou.req.service.impl;


import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.FileUploadType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.service.impl.BaseServiceImpl;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.meiql.api.component.paging.Page;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.SouExtRbacClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.monitor.enums.YesOrNo;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.rbac.ExtUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.deposit.entity.FinanceCompany;
import com.midea.cloud.srm.model.sou.enums.CaApiAcountServiceEnum;
import com.midea.cloud.srm.model.sou.req.SettleResultDto;
import com.midea.cloud.srm.model.sou.req.SouIntDepositInvoice;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.IntDepositInvoiceStatusEnum;
import com.midea.cloud.srm.model.sou.req.enums.InvoiceTypeEnum;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.req.mapper.SouIntDepositInvoiceMapper;
import com.midea.cloud.srm.sou.req.service.SouIntDepositInvoiceService;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 寻源单意向金开票表
 *
 * @author xiaym13 xiaym13@meicloud.com
 * @since 1.0.0 2023-10-04
 */
@Slf4j
@Service
public class SouIntDepositInvoiceServiceImpl extends BaseServiceImpl<SouIntDepositInvoiceMapper, SouIntDepositInvoice> implements SouIntDepositInvoiceService {

    @Autowired
    protected QlService qlService;
    @Autowired
    protected QlOpenClient qlOpenClient;
    @Autowired
    protected PjProjectExtClient pjProjectExtClient;
    @Autowired
    protected SouExtRbacClient souExtRbacClient;

    @Autowired
    private FileCenterClient fileCenterClient;

    @Value("${bpm.fbsp.addressPath}")
    private String addressPath;

    private static final int NUM200 = 200;
    private static final int NUM500 = 500;
    private static final String CODE = "code";
    private static final String DATA = "data";
    private static final String STR02 = "02";
    private static final String STR04 = "04";
    private static final String STR06 = "06";

    @Override
    public SouIntDepositInvoice getLatestData(SouIntDepositInvoice record) {
        List<SouIntDepositInvoice> souIntDepositInvoiceList = qlService.queryPageByWrapper(QlWrappers.query(MqlType.SOU_DEPOSIT_INVOICE_BUYER)
                        .eq(SouIntDepositInvoice::getReqHeadId, record.getReqHeadId())
                        .eq(ObjectUtils.allNotNull(record.getVendorId()), SouIntDepositInvoice::getVendorId, record.getVendorId())
                        .eq(ObjectUtils.allNotNull(record.getApplyId()), SouIntDepositInvoice::getApplyId, record.getApplyId())
                        .eq(ObjectUtil.isNotEmpty(record.getFromDepositInvoiceId()) && record.getInvoiceType().equals(InvoiceTypeEnum.RED_INVOICE.getCode()), SouIntDepositInvoice::getFromDepositInvoiceId, record.getFromDepositInvoiceId())
                        .eq(SouIntDepositInvoice::getInvoiceType, record.getInvoiceType())
                        .orderByDesc(SouIntDepositInvoice::getInvoiceId), 1L, 1L
                , SouIntDepositInvoice.class).getRecords();
        return ObjectUtil.isEmpty(souIntDepositInvoiceList) ? null : souIntDepositInvoiceList.get(0);
    }

    @Override
    public SouIntDepositInvoice getByFromDepositInvoiceId(Long invoiceId) {
        List<SouIntDepositInvoice> souIntDepositInvoiceList = qlService.queryPageByWrapper(QlWrappers.query(MqlType.SOU_DEPOSIT_INVOICE_BUYER)
                        .eq(SouIntDepositInvoice::getFromDepositInvoiceId, invoiceId)
                        .eq(SouIntDepositInvoice::getInvoiceType, InvoiceTypeEnum.RED_INVOICE.getCode())
                        .orderByDesc(SouIntDepositInvoice::getInvoiceId), 1L, 1L
                , SouIntDepositInvoice.class).getRecords();
        return ObjectUtil.isEmpty(souIntDepositInvoiceList) ? null : souIntDepositInvoiceList.get(0);
    }

    /**
     * 根据红票查询最新的蓝票信息
     * @param redInvoce
     * @return
     */
    private SouIntDepositInvoice queryNewestInvoiceForRed(SouIntDepositInvoice redInvoce) {

        Page<SouIntDepositInvoice> page = qlService.queryPageByWrapper(QlWrappers.query(MqlType.SOU_DEPOSIT_INVOICE_BUYER)
                .eq(SouIntDepositInvoice::getReqHeadId, redInvoce.getReqHeadId())
                .eq(SouIntDepositInvoice::getApplyId, redInvoce.getApplyId())
                .notEq(!Objects.isNull(redInvoce.getInvoiceId()), SouIntDepositInvoice::getInvoiceId, redInvoce.getInvoiceId())
                .orderByDesc(SouIntDepositInvoice::getInvoiceId), 1L, 1L, SouIntDepositInvoice.class);

        if(Objects.isNull(page) || CollectionUtils.isEmpty(page.getRecords())) {
            throw new BaseException("无蓝票信息，不允许发起红票申请");
        }

        SouIntDepositInvoice intDepositInvoice = page.getRecords().get(0);
        if(!InvoiceTypeEnum.INVOICE.getCode().equals(intDepositInvoice.getInvoiceType())) {
            throw new BaseException("最新开票信息为红票申请，不允许连续发起红票申请");
        }
        if(!IntDepositInvoiceStatusEnum.INVOICED.getCode().equals(intDepositInvoice.getStatus())) {
            throw new BaseException("最新开票信息非已开票状态，不允许发起红票申请");
        }
        return intDepositInvoice;
    }

    /**
     * 接口平台https://open.gwm.cn/goodsdetail?group_id=765bd9673d0a41459765f0b2d0031673&tabQueryType=0
     * 发票开具创建
     * @param invoice 寻源单意向金开票表
     * @return 发票开具创建 返回结果
     */
    @Override
    public JSONObject createInvoice(SouIntDepositInvoice invoice) {
        //寻源需求单头表
        SouReqHead souReqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, invoice.getReqHeadId(), SouReqHead.class);
        //SouReqHead souReqHead = qlService.queryByWrapper(QlWrappers.query(MqlType.SOU_REQ_HEAD_BUYER).eq(SouReqHead::getReqHeadId, invoice.getReqHeadId()), SouReqHead.class).get(0);
        //财务-公司信息
        Record financeCompany = qlService.queryByWrapper(QlWrappers.query(MqlType.SCC_SOU_FINANCE_COMPANY).eq("account",souReqHead.getBankAccount().replace(" ","")),Record.class).get(0);
        LoginAppUser user = AppUserUtil.getLoginAppUser();
        //查询供应商
        Record companyInfo = qlOpenClient.read(ContextPath.SUP, MqlType.SUPPLIER, user.getCompanyId(), Record.class);
        /*CompanyInfo companyInfo = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SUPPLIER)
                        .eq(CompanyInfo::getCompanyId, AppUserUtil.getLoginAppUser().getCompanyId())
                , CompanyInfo.class).get(0);*/

        JSONObject param = new JSONObject();

        //请求头
        JSONObject header = new JSONObject();
        header.put("systemCode","SRM");
        header.put("businessNo",invoice.getInvoiceNo());
        header.put("reqSn",invoice.getInvoiceNo());
        header.put("saleType","01");
        header.put("orgCode",financeCompany.getString("companyCode"));
        header.put("deptName","招标部");
        /*header.put("createUserNo",user.getUsername());
        header.put("createUserName",user.getNickname());*/
        ExtUser extUser = souExtRbacClient.getByUserId(souReqHead.getResponsibilityUserId());

        header.put("createUserNo", extUser.getUsername());
        header.put("createUserName", extUser.getNickname());
        header.put("createTime", DateUtil.format(new Date(),DateUtil.DATE_FORMAT_19));
        header.put("settleExplain", "意向金开票申请");
        header.put("needApprove", true);
        header.put("accountUserNo", null);
        header.put("accountUserName", null);

        //红票类型
        Boolean redInvoice = !InvoiceTypeEnum.INVOICE.getCode().equals(invoice.getInvoiceType());
        header.put("redInvoice", redInvoice);

        //行对象
        JSONArray items = new JSONArray();

        //基本信息
        JSONObject baseInfo = new JSONObject();
        baseInfo.put("itemNo",10);

//        baseInfo.put("customerCode",invoice.getVendorCode());
//        baseInfo.put("customerName",invoice.getVendorName());
        //customerCode、customerName到时换成下边三个
        baseInfo.put("partnerType","2");
        baseInfo.put("partnerCode",invoice.getVendorCode());
        baseInfo.put("partnerName",invoice.getVendorName());

        baseInfo.put("profitCenterCode",financeCompany.getString(LambdaUtil.getFieldName(FinanceCompany::getProfitCenterCode)));
        baseInfo.put("profitCenterName",financeCompany.getString(LambdaUtil.getFieldName(FinanceCompany::getProfitCenterName)));
        baseInfo.put("documentExplain",invoice.getVendorName()+"意向金开票");

//        baseInfo.put("partnerClassify", StringUtils.equals(companyInfo.getOverseasRelation(),"PERSONAL")?"03":"01");
        baseInfo.put("partnerClassify", StringUtils.equals(companyInfo.getString("overseasRelation"),"PERSONAL")?"03":"01");
        baseInfo.put("partnerTaxPayerCode",invoice.getTaxPayer());
        baseInfo.put("partnerAddress",invoice.getAddress());
        baseInfo.put("partnerPhone",invoice.getPhone());
        baseInfo.put("partnerBankName",invoice.getBankName());
        baseInfo.put("partnerBankAccount",invoice.getBankAccount());
        baseInfo.put("partnerEmail",invoice.getInvoiceReceiverEmail());

        //合同信息
        JSONObject contractInfo = new JSONObject();
        contractInfo.put("contractNo",null);
        contractInfo.put("contractName",null);
        contractInfo.put("contractAmount",null);
        contractInfo.put("payType",null);
        contractInfo.put("collectionBank",null);
        contractInfo.put("collectionAccount",null);
        contractInfo.put("payBank",null);
        contractInfo.put("payAccount",null);
        contractInfo.put("containTax",true);

        //结算明细列表
        JSONArray settleDetailList = new JSONArray();
        JSONObject settleDetailItem = new JSONObject();
        settleDetailList.add(settleDetailItem);
        settleDetailItem.put("serviceCode", CaApiAcountServiceEnum.EARNEST_INVOICE.getServiceCode());//暂传
//        settleDetailItem.put("serviceCode","FW0907");
        settleDetailItem.put("taxClassifyCode","3049900000000000000");

        JSONObject invoiceInfo = new JSONObject();
        invoiceInfo.put("invoiceType","026");
        invoiceInfo.put("invoiceRemarks",null);
        if(redInvoice) {
            SouIntDepositInvoice oldInvoice = queryNewestInvoiceForRed(invoice);
            //原发票代码
            invoiceInfo.put("oldInvoiceCode",oldInvoice.getInvoiceCode());
            //原发票号码
            invoiceInfo.put("oldInvoiceNo",oldInvoice.getReqInvoiceNo());
            //红字信息表
            invoiceInfo.put("redLetterTab",null);
            //红冲原因
            invoiceInfo.put("reverseReason",invoice.getRedInvoiceReason());

        }
        settleDetailItem.put("invoiceInfo",invoiceInfo);

        JSONArray settleInfoList = new JSONArray();
        JSONObject settleInfoItem = new JSONObject();
        settleInfoItem.put("businessCode",CaApiAcountServiceEnum.EARNEST_INVOICE.getBusinessCode());//暂传
//        settleInfoItem.put("businessCode","FW0907");
        settleInfoItem.put("specsModel",null);
        settleInfoItem.put("settleNumber",1);
        settleInfoItem.put("price",souReqHead.getDepositAmount());
        settleInfoItem.put("unit","项");
        settleInfoItem.put("discountFlag",false);
        settleInfoItem.put("discountAmount",null);
        settleInfoItem.put("remarks",null);
        settleInfoItem.put("zeroTaxRateFlag",null);
        settleInfoItem.put("preferentialPolicy",null);
        settleInfoItem.put("addTaxSpecial",null);

        settleInfoList.add(settleInfoItem);
        settleDetailItem.put("settleInfoList",settleInfoList);

        //成本结转列表
        JSONArray costInfoList = new JSONArray();

        //收款信息列表
        JSONArray collectionInfoList = new JSONArray();

        //附件列表
        JSONArray attachList = new JSONArray();

        //支付凭证传输
        if(!Objects.isNull(invoice.getEntrustPayVoucherFileId())) {
            JSONObject attach = new JSONObject();
            attach.put("attachName", invoice.getEntrustPayVoucherFileName());
            attach.put("attachUrl", String.format(addressPath + "fileSourceName=%s&fileuploadId=%s", invoice.getEntrustPayVoucherFileName(), invoice.getEntrustPayVoucherFileId()));
            attachList.add(attach);
        }

        JSONObject item = new JSONObject();

        item.put("baseInfo",baseInfo);
        item.put("contractInfo",contractInfo);
        item.put("settleDetailList",settleDetailList);
        item.put("costInfoList",costInfoList);
        item.put("collectionInfoList",collectionInfoList);
        item.put("attachList",attachList);

        items.add(item);
        param.put("header",header);
        param.put("items",items);

        log.info("param:{}",param.toJSONString());

        JSONObject result = pjProjectExtClient.createInvoice(param);
        log.info("result:{}",result.toJSONString());
        if(NUM200==result.getIntValue(CODE)&&result.getJSONObject(DATA)!=null
                &&result.getJSONObject("data").containsKey("settleDocumentCode")){
            this.update(Wrappers.lambdaUpdate(SouIntDepositInvoice.class)
                    .set(SouIntDepositInvoice::getSettleDocumentCode,result.getJSONObject("data").get("settleDocumentCode"))
                    .set(SouIntDepositInvoice::getStatus, IntDepositInvoiceStatusEnum.INVOICING.getCode())
                    .set(SouIntDepositInvoice::getCreateInvoiceStatus, YesOrNo.N.name())
                    .eq(SouIntDepositInvoice::getInvoiceId,invoice.getInvoiceId()));
        }else{
            Assert.isTrue(false,"发票开具创建失败:{}",result.getString("msg"));
        }
        return result;
    }

    /**
     * 查询（两个小时）状态为“已提交”的数据，触发查询接口《财务共享-发票结算结果查询》，有结果回写状态，开具成功or开具失败
     * @param invoice 寻源单意向金开票表
     */
    @Override
    public void updateCreateInvoiceStatus(SouIntDepositInvoice invoice) {
        try{
            JSONObject param = new JSONObject();
            param.put("systemCode","SRM");
            param.put("businessNo",invoice.getInvoiceNo());
            param.put("reqSn",invoice.getInvoiceNo());
            param.put("settleDocumentCode",invoice.getSettleDocumentCode());
            JSONObject result = pjProjectExtClient.settleResult(param);
            if(Objects.isNull(result)) {
                return;
            }

            SettleResultDto resultDto = result.toJavaObject(SettleResultDto.class);

            if(Integer.compare(NUM200, ObjectUtils.defaultIfNull(resultDto.getCode(), NUM500)) != 0) {
                log.info(MessageFormat.format("updateCreateInvoiceStatus {0}", "状态码不为200"));
                return;
            }

            if(Objects.isNull(resultDto.getData())) {
                log.info(MessageFormat.format("updateCreateInvoiceStatus {0}", "返回data空"));
                return;
            }

            if(CollectionUtils.isEmpty(resultDto.getData().getItems())) {
                log.info(MessageFormat.format("updateCreateInvoiceStatus {0}", "item明细空"));
                return;
            }

            SettleResultDto.ItemDto itemDto = resultDto.getData().getItems().get(0);
            if(CollectionUtils.isEmpty(itemDto.getInvoiceList())) {
                log.info(MessageFormat.format("updateCreateInvoiceStatus {0}", "发票信息空"));
                return;
            }

            SettleResultDto.DataDto dataDto = resultDto.getData();

            SettleResultDto.InvoiceDto invoiceDto = itemDto.getInvoiceList().get(0);

            //新增 如果被退回 财务单据状态为已废弃 srm改为也显示开票失败
            if (STR06.equals(dataDto.getState())){
                log.info(MessageFormat.format("updateCreateInvoiceStatus {0}", "开票失败"));
                this.update(Wrappers.lambdaUpdate(SouIntDepositInvoice.class)
                        .set(SouIntDepositInvoice::getStatus, IntDepositInvoiceStatusEnum.FAIL_INVOICED.getCode())
                        .set(SouIntDepositInvoice::getApplyInvoiceFailReason, dataDto.getStateExplain())
                        .eq(SouIntDepositInvoice::getInvoiceId,invoice.getInvoiceId()));
            }

            if(STR02.equals(invoiceDto.getStateCode())) {
                log.info(MessageFormat.format("updateCreateInvoiceStatus {0}", "失败"));
                this.update(Wrappers.lambdaUpdate(SouIntDepositInvoice.class)
                        .set(SouIntDepositInvoice::getStatus, IntDepositInvoiceStatusEnum.FAIL_INVOICED.getCode())
                        .set(SouIntDepositInvoice::getApplyInvoiceFailReason, invoiceDto.getStateDesc())
                        .eq(SouIntDepositInvoice::getInvoiceId,invoice.getInvoiceId()));
            } else if(STR04.equals(invoiceDto.getStateCode())) {
                log.info(MessageFormat.format("updateCreateInvoiceStatus {0}", "成功"));
                String invoiceCode = "";
                String reqInvoiceNo = "";
                if(CollectionUtils.isNotEmpty(invoiceDto.getList())) {
                    invoiceCode = invoiceDto.getList().get(0).getInvoiceCode();
                    reqInvoiceNo = invoiceDto.getList().get(0).getInvoiceNo();
                }
                LambdaUpdateWrapper<SouIntDepositInvoice> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(SouIntDepositInvoice::getCreateInvoiceStatus, YesOrNo.Y.name())
                        .set(SouIntDepositInvoice::getInvoiceCode, invoiceCode)
                        .set(SouIntDepositInvoice::getReqInvoiceNo, reqInvoiceNo)
                        .set(SouIntDepositInvoice::getStatus, IntDepositInvoiceStatusEnum.INVOICED.getCode());

                if(CollectionUtils.isNotEmpty(invoiceDto.getList())) {
                    String fileUrl = invoiceDto.getList().get(0).getInvoiceUrl();
                    Fileupload fl = dealFile(fileUrl);
                    updateWrapper.set(SouIntDepositInvoice::getFileUrl, fileUrl)
                            .set(SouIntDepositInvoice::getFileId, fl.getFileuploadId())
                            .set(SouIntDepositInvoice::getFileName, fl.getFileSourceName());
                }

                updateWrapper.eq(SouIntDepositInvoice::getInvoiceId,invoice.getInvoiceId());
                this.update(updateWrapper);
            }

        }catch (Exception e){
            log.info("结算结果更新失败:{}",e.getMessage());
        }
    }

    public Fileupload dealFile(String fileUrl) {
        MockMultipartFile multipartFile;
        String fileType = fileUrl.substring(fileUrl.lastIndexOf(".") + 1).toLowerCase();
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(fileUrl);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            // 获取输入流来读取文件内容
            InputStream inputStream = entity.getContent();
            // 将输入流转为字节数组
            byte[] fileBytes = IOUtils.toByteArray(inputStream);
            inputStream.close(); // 关闭输入流
            // 从响应中获取原始文件名（可能需要解析HTTP响应头）
            //String originalFilename = "filenameFromUrlResponse";
            String originalFilename = UUID.randomUUID().toString();
            // 创建MultipartFile对象
//                        multipartFile =  new MockMultipartFile(originalFilename, originalFilename, "application/octet-stream", fileBytes);
            log.info("附件类型信息---***===" + fileType);
            // 创建MultipartFile对象
            multipartFile =  new MockMultipartFile(originalFilename, originalFilename + "." + fileType, "application/" + fileType, fileBytes);
        } catch (IOException ex) {
            log.info("处理附件===========" + JSONObject.toJSONString(ex));
            throw new BaseException("附件下载失败" + "---" + ex.getMessage());
        }
        String sourceType = "WEB_APP";
        String uploadType = FileUploadType.DEF.name();
        String fileModular = "sup";
        String fileFunction = "vendorBiddingManagement";
        return fileCenterClient.feignClientUpload(multipartFile, sourceType, uploadType, fileModular, fileFunction, fileType);
    }
}
