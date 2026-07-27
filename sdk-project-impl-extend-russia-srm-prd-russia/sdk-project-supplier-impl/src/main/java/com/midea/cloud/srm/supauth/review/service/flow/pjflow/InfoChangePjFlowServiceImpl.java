package com.midea.cloud.srm.supauth.review.service.flow.pjflow;

import com.esotericsoftware.minlog.Log;
import com.midea.cloud.common.enums.review.FormType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.mdm.dto.MdmResponseDto;
import com.midea.cloud.srm.model.pj.supplier.entry.dto.InternalSupplierQuery;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.utils.MqlType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @description: 供应商信息变更审批流回调类
 * @date: 2023/10/6 20:54
 * @author 100014323
 */
@Service
public class InfoChangePjFlowServiceImpl implements IFlowBusinessCallbackService {
    @Resource(name = "infoChangeFlowServiceImpl")
    private IFlowBusinessCallbackService infoChangeFlowServiceImpl;

    @Autowired
    private QlService qlService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        infoChangeFlowServiceImpl.submitFlow(businessId, param);
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        infoChangeFlowServiceImpl.passFlow(businessId, param);

        // 二开字段
        List<Record> companyInfoChangeRecords = qlService.query(MqlType.COMPANY_INFO_CHANGE, MeiQl.newCondition()
                .eq("changeId", businessId), Record.class);
        Record companyInfoChangeRecord = companyInfoChangeRecords.get(0);
        Long companyId = companyInfoChangeRecord.getLong("companyId");

        //后置，处理其他二开字段的回写
        try {
            //供应商主信息
            pjCompanyInfoChange2CompanyInfo(companyId, companyInfoChangeRecord);
            //联系人信息
            pjContactInfoChange2ContactInfo(businessId);
            //附件信息
            pjAttachChange2Attach(businessId);

            // 新表
            //三大财务报表附件
            pjOtherChange2Info(MqlType.NPM_FINANCE_REPORT_CHANGE, MqlType.NPM_FINANCE_REPORT, businessId, companyId);
            //公司规模
            pjOtherChange2Info(MqlType.NPM_COMPANY_SIZE_CHANGE, MqlType.NPM_COMPANY_SIZE, businessId, companyId);
            //品类信息,服务明细
            pjCategoryChange2Category(businessId, companyId);
            //    23-11-21 更新信息是否完善标识    infoCompleteFlag
            qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                    .set("infoCompleteFlag", Enable.Y.name())
                    .eq("companyId", companyId));
        } catch (Exception e) {
            Log.error("二开字段回写失败:" + e);
        }
        CompletableFuture.runAsync(()->{
            try {
                Thread.sleep(2000L);
                CompanyInfo companyInfoParam = new CompanyInfo();
                companyInfoParam.putX("accountGroup", companyInfoChangeRecord.getString("accountGroup"));
                MdmResponseDto mdmResponseDto = pjProjectExtClient.sendCompanyInfoToMdm(companyInfoParam.setCompanyId(companyId));
            } catch (Exception e) {
                Log.error("修改供应商同步MDM失败:" + e);
            }
        });


    }

    private void pjCompanyInfoChange2CompanyInfo(Long companyId, Record companyInfoChangeRecord) {
        qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                .eq("companyId", companyId)
                .set("sunshineFileId", companyInfoChangeRecord.getString("sunshineFileId"))
                .set("sunshineFileName", companyInfoChangeRecord.getString("sunshineFileName"))
                .set("groupCountry", companyInfoChangeRecord.getString("groupCountry"))
                .set("totalAssets", companyInfoChangeRecord.getBigDecimal("totalAssets"))
                .set("currentAssets", companyInfoChangeRecord.getBigDecimal("currentAssets"))
                .set("fixedAssets", companyInfoChangeRecord.getBigDecimal("fixedAssets"))
                .set("avgAnnualOutput", companyInfoChangeRecord.getBigDecimal("avgAnnualOutput"))
                .set("avgAnnualProfit", companyInfoChangeRecord.getBigDecimal("avgAnnualProfit"))
                .set("focusFlag", companyInfoChangeRecord.getString("focusFlag"))
                .set("positionLimitFlag", companyInfoChangeRecord.getString("positionLimitFlag"))
                .set("categoryLimitFlag", companyInfoChangeRecord.getString("categoryLimitFlag"))
                .set("timeLimitFlag", companyInfoChangeRecord.getString("timeLimitFlag"))
                .set("limitDate", companyInfoChangeRecord.getDate("limitDate"))
                .set("contractVerification", companyInfoChangeRecord.getString("contractVerification"))
                .set("biddingFlag", companyInfoChangeRecord.getString("biddingFlag"))
                .set("keySupervisionFlag", companyInfoChangeRecord.getString("keySupervisionFlag"))
                .set("accountGroup", companyInfoChangeRecord.getString("accountGroup"))
                .set("partner", companyInfoChangeRecord.getString("partner"))
                .set("gscpStatus", companyInfoChangeRecord.getString("gscpStatus"))
                .set("pjCompanyStatus", companyInfoChangeRecord.getString("pjCompanyStatus"))
                .set("extSex", companyInfoChangeRecord.getString("extSex"))
                .set("extIdCardOppositeFileId", companyInfoChangeRecord.getString("extIdCardOppositeFileId"))
                .set("extIdCardOppositeFileName", companyInfoChangeRecord.getString("extIdCardOppositeFileName"))
        );
    }

    private void pjContactInfoChange2ContactInfo(Long businessId) {
        List<Record> contactInfoChangeRecods = qlService.query(MqlType.CONTACT_INFO_CHANGE, MeiQl.newCondition()
                .eq("changeId", businessId), Record.class);
        if (CollectionUtils.isNotEmpty(contactInfoChangeRecods)) {
            for (Record contactInfoChangeRecod : contactInfoChangeRecods) {
                qlService.updateByWrapper(QlWrappers.update(MqlType.CONTACTINFO)
                        .eq("contactInfoId", contactInfoChangeRecod.getLong("contactInfoId"))
                        .set("socialSecurityCertificateFileId", contactInfoChangeRecod.getLong("socialSecurityCertificateFileId"))
                        .set("socialSecurityCertificateFileName", contactInfoChangeRecod.getString("socialSecurityCertificateFileName")));
            }
        }
    }

    private void pjAttachChange2Attach(Long businessId) {
        List<Record> recordList = qlService.query(MqlType.MANAGEMENT_ATTACH_CHANGE, MeiQl.newCondition()
                .eq("changeId", businessId), Record.class);
        if (CollectionUtils.isNotEmpty(recordList)) {
            for (Record record : recordList) {
                qlService.updateByWrapper(QlWrappers.update(MqlType.MANAGEMENT_ATTACH)
                        .eq("fileuploadId", record.getLong("fileuploadId"))
                        .set("categoryId", record.getLong("categoryId"))
                        .set("categoryCode", record.getString("categoryCode"))
                        .set("categoryName", record.getString("categoryName")));
            }
        }
    }

    /**
     * 变更表回写到供应商主表
     *
     * @param changeType
     * @param infoType
     * @param businessId
     * @param companyId
     */
    private void pjOtherChange2Info(String changeType, String infoType, Long businessId, Long companyId) {
        List<Record> recordList = qlService.query(changeType, MeiQl.newCondition()
                .eq("changeId", businessId), Record.class);
        // 删除原来的
        qlService.deleteByWrapper(QlWrappers.update(infoType).eq("companyId", companyId));
        if (CollectionUtils.isNotEmpty(recordList)) {
            for (Record record : recordList) {
                record.put("companyId", companyId);
                record.remove("financeReportId");
                record.remove("companySizeId");
            }
            // 回写主数据
            qlService.create(infoType, recordList);
        }
    }

    private void pjCategoryChange2Category(Long businessId, Long companyId) {
        // 1. 品类信息
        List<Record> recordList = qlService.query(MqlType.CATE_JOURNAL_COMPANY_CHANGE, MeiQl.newCondition()
                .eq("formId", businessId), Record.class);
        // 删除原来的
        List<Record> originalRecords = qlService.query(MqlType.CATE_JOURNAL_COMPANY, MeiQl.newCondition().eq("formId", companyId), Record.class);
        qlService.deleteByWrapper(QlWrappers.update(MqlType.CATE_JOURNAL_COMPANY).eq("formId", companyId));
        if (CollectionUtils.isNotEmpty(recordList)) {
            List<Long> idList = new ArrayList<>();
            for (Record record : recordList) {
                idList.add(record.getLong("categoryJournalChangeId"));
                record.put("formId", companyId);
                record.put("formType", FormType.SUPPLIER_REGISTRATION.name());
                record.put("categoryJournalId", record.getLong("categoryJournalChangeId"));
            }
            // 回写主数据
            qlService.create(MqlType.CATE_JOURNAL_COMPANY, recordList);
            //2. 回写服务明细
            List<Record> deatailRecordList = qlService.query(MqlType.NPM_SERCICE_CUSTOM_CHANGE, MeiQl.newCondition()
                    .in("categoryJournalChangeId", idList), Record.class);
            // 删除原来的
            if (CollectionUtils.isNotEmpty(originalRecords)) {
                List<Long> origianlIdList = originalRecords.stream().map(item -> item.getLong("categoryJournalId")).collect(Collectors.toList());
                qlService.deleteByWrapper(QlWrappers.update(MqlType.NPM_SERCICE_CUSTOM).in("categoryJournalId", origianlIdList));
            }
            if (CollectionUtils.isNotEmpty(deatailRecordList)) {
                for (Record record : deatailRecordList) {
                    record.put("categoryJournalId", record.getLong("categoryJournalChangeId"));
                    record.remove("serciceCustomId");
                }
                qlService.save(MqlType.NPM_SERCICE_CUSTOM, deatailRecordList);
            }
        }
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        infoChangeFlowServiceImpl.rejectFlow(businessId, param);
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        infoChangeFlowServiceImpl.withdrawFlow(businessId, param);
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        infoChangeFlowServiceImpl.destoryFlow(businessId, param);
    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        return null;
    }
}