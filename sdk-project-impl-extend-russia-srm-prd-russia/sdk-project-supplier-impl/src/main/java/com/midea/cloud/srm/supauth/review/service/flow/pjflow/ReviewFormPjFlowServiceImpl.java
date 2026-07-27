package com.midea.cloud.srm.supauth.review.service.flow.pjflow;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.emun.PjCompanyStatusEmun;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.file.upload.dto.FileuploadDTO;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supplier.bpm.*;
import com.midea.cloud.srm.model.supplierauth.entry.entity.FileRecord;
import com.midea.cloud.srm.model.supplierauth.review.entity.*;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.sup.ext.pjreviewform.repo.PjReviewFormRepository;
import com.midea.cloud.srm.sup.meiql.dto.ReviewFormWrapDTO;
import com.midea.cloud.srm.supauth.review.service.IReviewFormService;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description:
 * @date: 2023/7/31 20:54
 * @author 100014323
 */
@Slf4j
@Service
public class ReviewFormPjFlowServiceImpl implements IFlowBusinessCallbackService {

    @Resource(name = "reviewFormFlowServiceImpl")
    private IFlowBusinessCallbackService reviewFormFlowServiceImpl;

    @Autowired
    private IReviewFormService reviewFormService;

    @Autowired
    private QlService qlService;

    @Autowired
    QlOpenClient qlOpenClient;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Value("${bpm.zzsc.processGroupId}")
    private String processGroupId;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private BaseClient baseClient;

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        reviewFormFlowServiceImpl.submitFlow(businessId, param);
    }
    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        reviewFormFlowServiceImpl.passFlow(businessId, param);
        //ReviewForm reviewForm = reviewFormService.getById(businessId);
        Record reviewForm = qlService.readByKey("ReviewForm", businessId, Record.class);
        //后置 供应商状态是呆滞和无效，需要转成准供应商
        Long companyId = reviewForm.getLong("vendorId");
        qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                .eq("companyId", companyId)
                .in("pjCompanyStatus", Arrays.asList(PjCompanyStatusEmun.SLUGGISH_SUPPLIER.name(), PjCompanyStatusEmun.INVALID_SUPPLIER.name()))
                .set("pjCompanyStatus", PjCompanyStatusEmun.QUASI_SUPPLIER.name()));
        qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                .eq("companyId", companyId)
                .set("extUseType", reviewForm.getString("extUseType")));
    }
    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        reviewFormFlowServiceImpl.rejectFlow(businessId, param);
    }
    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        reviewFormFlowServiceImpl.withdrawFlow(businessId, param);
    }
    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        reviewFormFlowServiceImpl.destoryFlow(businessId, param);
    }
    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return reviewFormFlowServiceImpl.getVariableFlow(businessId, param);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("---------getDataPushFlow-----------");
        log.info("businessId:" + businessId);
        log.info("param:" + param);
        //供应商资质审查
        ReviewFormWrapDTO reviewForm = qlService.readByKey("ReviewForm", businessId, ReviewFormWrapDTO.class);
        Map<String, Object> bpmReviewFormDto = new HashMap<>(16);
        List<DictItem> diList = baseClient.listDictItemByDictCode("QUA_REVIEW_TYPE");
        bpmReviewFormDto.put("ZZSCLX", BpmResult.getDictName(diList, reviewForm.getQuaReviewType()));
        bpmReviewFormDto.put("GYSMC",reviewForm.getVendorName());
        bpmReviewFormDto.put("ZZSCDH",reviewForm.getReviewFormNumber());
        bpmReviewFormDto.put("SHZT",getStatusZhName(reviewForm.getApproveStatus()));
        bpmReviewFormDto.put("CJR", reviewForm.getCreatedFullName());
        bpmReviewFormDto.put("BM",reviewForm.getCeeaDeptName());
        bpmReviewFormDto.put("CJSJ", BpmResult.sdfDate(reviewForm.getCreationDate()));
        bpmReviewFormDto.put("SFZB", BpmResult.dealYesOrNo(reviewForm.getIfSiteForm()));
        bpmReviewFormDto.put("DJSM",reviewForm.getReviewExplain());
        Assert.notNull(bpmReviewFormDto,"参数不能为空");
        Long formId = reviewForm.getReviewFormId();
        List<Object> itemDataList = new ArrayList<>();
        //银行信息
        List<BankJournal> backJournalInfoList = qlService.queryByWrapper(QlWrappers.query(MqlType.BANK_JOURNAL).
                eq(BankJournal::getFormId, formId), BankJournal.class);
        for (BankJournal e : backJournalInfoList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("YHDM", e.getBankCode());
            map.put("YHMC", e.getBankName());
            map.put("KHHMC", e.getOpeningBank());
            map.put("FHBM", e.getUnionCode());
            map.put("ZHMC", e.getBankAccountName());
            map.put("YHZH", e.getBankAccount());
            map.put("BZ", e.getCurrencyCode());
            map.put("SFZZH", e.getCeeaMainAccount());
            map.put("QY", e.getCeeaEnabled());
            map.put("__TABLE", "BO_EU_YHXX");
            itemDataList.add(map);
        }
        //引入品类和组织
        List<OrgJournal> orgJournalList = qlService.queryByWrapper(QlWrappers.query(MqlType.ORGJOURNAL).
                eq(OrgJournal::getFormId, formId), OrgJournal.class);
        orgJournalList.forEach(e ->  {
            Map<String, Object> map = new HashMap<>(16);
            map.put("YRGS", e.getOrgName());
            map.put("__TABLE", "BO_EU_YRPLHZZ");
            itemDataList.add(map);
        });
        //引入品类和组织2
        List<CateJournal> cateJournalList = qlService.query(MqlType.CATE_JOURNAL_COMPANY, MeiQl.newCondition()
                .eq(CateJournal::getFormId, formId), CateJournal.class);
        cateJournalList.forEach(e -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("YRPL" ,String.valueOf(e.getCategoryName()));
            map.put("__TABLE", "BO_EU_YRPLHZZ2");
            itemDataList.add(map);
        });
        //资质审查原因
        List<ReviewFormExp> reviewFormExpList = qlService.queryByWrapper(QlWrappers.query(MqlType.REVIEW_FORM_EXP).
                eq(ReviewFormExp::getReviewFormId, formId), ReviewFormExp.class);
        reviewFormExpList.forEach(e -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("YY",e.getReviewReason());
            map.put("YYMS",e.getReasonExplain());
            map.put("__TABLE", "BO_EU_ZZSCYY");
            itemDataList.add(map);
        });
        //附件
        List<FileRecord> fileRecordList = qlService.queryByWrapper(QlWrappers.query(MqlType.FILERECORD).eq(FileRecord::getFormId, formId), FileRecord.class);
        Set<String> fList = new HashSet<>();
        extracted(itemDataList, fileRecordList, fList);
        String processTitle = reviewForm.getVendorName();
        String mainTable = "BO_EU_GYSZZSC";
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();
        String createOrgId = null;
        SccPjUser sccPjUser = pjProjectExtClient.getSccUserByPersonnelNo(createUser);
        if (sccPjUser != null && sccPjUser.getGroupId() != null) {
            createOrgId = String.valueOf(sccPjUser.getGroupId());
        }
        if (StringUtils.isBlank(createOrgId)) {
            throw new BaseException("查询不到hr组织id");
        }
        List<String> tableList = new ArrayList<>();
        tableList.add("BO_EU_YHXX");
        tableList.add("BO_EU_YRPLHZZ");
        tableList.add("BO_EU_YRPLHZZ2");
        tableList.add("BO_EU_ZZSCYY");
        tableList.add("BO_EU_FJ");
        Map<String,Object> itemFile = new HashMap<>(16);
        itemFile.put("BO_EU_FJ", fList);
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, bpmReviewFormDto, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile);
        log.info(JSON.toJSONString(dataPushFlowJsn));
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    private void extracted(List<Object> itemDataList, List<FileRecord> fileRecordList, Set<String> fList) {
        for (FileRecord e : fileRecordList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("FJMC", e.getFileName());
            List<Map<String, Object>> file = new ArrayList<>();
            List<Long> ll = new ArrayList<>();
            ll.add(e.getFileId());
            List<Fileupload> fileList = fileCenterClient.find(ll);
            for (Fileupload fileUpload : fileList) {
                Map<String, Object> fileMap = new HashMap<>(16);
                fileMap.put("FILE_PATH_BYMOBILE", "");
                fileMap.put("FILE_NAME", fileUpload.getFileSourceName());
                fileMap.put("FILE_PATH", String.format(addressPath + "fileSourceName=%s&fileuploadId=%s", fileUpload.getFileSourceName(), fileUpload.getFileuploadId()));
                file.add(fileMap);
            }
            fList.add("FJSC");
            map.put("FJSC", file);
            map.put("BZ", e.getRemark());
            map.put("__TABLE", "BO_EU_FJ");
            itemDataList.add(map);
        }
    }

    public static String getStatusZhName(String str) {
        Map<String, String> map = new HashMap<>(16);
        map.put("DRAFT", "拟定");
        map.put("SUBMITTED", "待审批");
        map.put("APPROVED", "已通过");
        map.put("REJECTED", "已驳回");
        map.put("WITHDRAW", "已撤回");
        map.put("ABANDONED", "已废弃");
        return map.get(str);
    }

}
