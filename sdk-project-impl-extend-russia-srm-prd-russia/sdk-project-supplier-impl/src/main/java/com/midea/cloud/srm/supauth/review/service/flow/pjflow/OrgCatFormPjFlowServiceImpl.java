package com.midea.cloud.srm.supauth.review.service.flow.pjflow;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.enums.SupplierControlType;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryFieldWrapper;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.emun.PjSupplierControlType;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.base.systemConfigure.dto.SystemConfigureDTO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.supplierauth.orgcategory.entity.OrgCatForm;
import com.midea.cloud.srm.model.supplierauth.orgcategory.entity.OrgCatFormCategory;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.supauth.orgcategory.service.IOrgCatFormCategoryService;
import com.midea.cloud.srm.supauth.orgcategory.service.IOrgCatFormService;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 合作终止审批流回调类
 * @date: 2023/10/6 20:54
 * @author 100014323
 */
@Slf4j
@Service
public class OrgCatFormPjFlowServiceImpl implements IFlowBusinessCallbackService {


    @Resource(name = "orgCatFormServiceImpl")
    private IFlowBusinessCallbackService orgCatFormServiceImpl;

    @Autowired
    private IOrgCatFormService orgCatFormService;

    @Autowired
    private IOrgCatFormCategoryService orgCatFormCategoryService;

    @Autowired
    private QlService qlService;

    @Autowired
    private QlOpenClient qlOpenClient;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Value("${bpm.zzsx.processGroupId}")
    private String processGroupId;
    @Value("${bpm.zzsx.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;

    @Autowired
    private BaseClient baseClient;
    @Autowired
    private BaseExtClient baseExtClient;

    private static final String BUSINESS_TYPE = "supplierLimitation";

    @Autowired
    private RedisUtil redisUtil;


    private boolean ifPorductAction(Long businessId) {
        OrgCatForm orgCatForm = orgCatFormService.getById(businessId);
        boolean flag = true;
        try {
            SupplierControlType supplierControlType = SupplierControlType.valueOf(orgCatForm.getSupplierControlType());
        } catch (IllegalArgumentException e) {
            flag = false;
        }
        return flag;
    }

    private void submit(Long businessId,String param) throws Exception {
        boolean flag = ifPorductAction(businessId);
        if (flag) {
            orgCatFormServiceImpl.submitFlow(businessId, param);
        } else {
            // 二开处理
            orgCatFormService.update(Wrappers.lambdaUpdate(OrgCatForm.class)
                    .eq(OrgCatForm::getOrgCatFormId, businessId)
                    .set(OrgCatForm::getApproveStatus, ApproveStatusType.SUBMITTED.getValue()));
        }
    }

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& com.alibaba.cloud.commons.lang.StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                submit(businessId, param);

                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                boolean flag = ifPorductAction(businessId);
                if (flag) {

                } else {
                    // 二开处理
                    Record r = new Record();
                    r.put(OrgCatForm::getOrgCatFormId, businessId);
                    r.put("startBpmUsername", loginAppUser.getUsername());
                    r.put("startBpmNickname", loginAppUser.getNickname());
                    qlService.update("OrgCatForm", Arrays.asList(r));
                }
                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            submit(businessId, param);
        }
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        OrgCatForm orgCatForm = orgCatFormService.getById(businessId);

        boolean flag = ifPorductAction(businessId);
        if (flag) {
            orgCatFormServiceImpl.passFlow(businessId, param);
        } else {
            // todo 二开处理
            List<OrgCatFormCategory> selectedRangeList = orgCatFormCategoryService.list(new QueryWrapper<>(new OrgCatFormCategory()
                    .setOrgCatFormId(orgCatForm.getOrgCatFormId())
                    .setControlFlag("1")
                    .setSelected(YesOrNo.YES.getValue())));
            List<OrgCatFormCategory> selectedDetailList = orgCatFormCategoryService.list(new QueryWrapper<>(new OrgCatFormCategory()
                    .setOrgCatFormId(orgCatForm.getOrgCatFormId())
                    .setControlFlag("2")
                    .setSelected(YesOrNo.YES.getValue())));

            if (PjSupplierControlType.POSITION_LIMIT_FLAG.name().equals(orgCatForm.getSupplierControlType())) {
                // 组织受限
                dealWithPositionLimitFlag(businessId, orgCatForm, selectedRangeList);
            } else if (PjSupplierControlType.POSITION_LIMIT_FLAG_REMOVE.name().equals(orgCatForm.getSupplierControlType())) {
                //组织受限解除
                dealWithPositionLimitFlagRemove(businessId, orgCatForm, selectedRangeList);
            } else if (PjSupplierControlType.CATEGORY_LIMIT_FLAG.name().equals(orgCatForm.getSupplierControlType())) {
                //品类受限
                dealWithCategoryLimitFlag(businessId, orgCatForm, selectedDetailList);
            } else if (PjSupplierControlType.CATEGORY_LIMIT_FLAG_REMOVE.name().equals(orgCatForm.getSupplierControlType())) {
                //品类受限解除
                dealWithCategoryLimitFlagRemove(businessId, orgCatForm, selectedDetailList);
            } else if (PjSupplierControlType.TIME_LIMIT_FLAG.name().equals(orgCatForm.getSupplierControlType())) {
                //时间受限
                dealWithTimeLimitFlag(businessId, orgCatForm);
            } else if (PjSupplierControlType.TIME_LIMIT_FLAG_REMOVE.name().equals(orgCatForm.getSupplierControlType())) {
                //时间受限接触
                dealWithTimeLimitFlagRemove(businessId, orgCatForm);
            }
            // 最终都要修改单据状态
            orgCatForm.setApproveStatus(ApproveStatusType.APPROVED.getValue());
            orgCatForm.setStartDate(LocalDate.now());
            orgCatFormService.updateById(orgCatForm);

            if (PjSupplierControlType.POSITION_LIMIT_FLAG.name().equals(orgCatForm.getSupplierControlType())) {
                //预警处理
                remindChief(orgCatForm);
            }
        }
    }

    private void dealWithPositionLimitFlag(Long businessId, OrgCatForm orgCatForm, List<OrgCatFormCategory> selectedRangeList) {
        //供应商品类库的组织状态变成失效，《供应商清单》是否单位受限改成是，供应商异常信息记录一条信息
        if (CollectionUtils.isNotEmpty(selectedRangeList)) {
            List<Long> orgIdList = selectedRangeList.stream().map(OrgCatFormCategory::getOrgId).collect(Collectors.toList());
            Map<Long, OrgCatFormCategory> orgIdMap = selectedRangeList.stream().collect(Collectors.toMap(OrgCatFormCategory::getOrgId, Function.identity(), (k1, k2) -> k2));
            qlService.updateByWrapper(QlWrappers.update(MqlType.ORG_CATEGORY)
                    .eq("companyId", orgCatForm.getVendorId())
                    .in("orgId", orgIdList)
                    .set("pjOrgStatus", Enable.N.name()));

            qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                    .eq("companyId", orgCatForm.getVendorId())
                    .set("positionLimitFlag", Enable.Y.name()));

            List<Record> saveList = new ArrayList<>();
            for (Long key : orgIdMap.keySet()) {
                OrgCatFormCategory catFormCategory = orgIdMap.get(key);
                Record record = new Record();
                record.put("companyId", orgCatForm.getVendorId());
                record.put("businessId", businessId);
                record.put("exceptionType", PjSupplierControlType.POSITION_LIMIT_FLAG);
                record.put("exceptionInfo", catFormCategory.getOrgName());
                record.put("orgOrCategoryId", catFormCategory.getOrgId());
                record.put("orgOrCategoryCode", catFormCategory.getOrgCode());
                saveList.add(record);
            }

            qlService.create(MqlType.NPM_COMPANY_EXCEPTION_INFO, saveList);



        }
    }


    /**
     * 某供应商若被≥2个板块（根据限制单位所属板块）列入限制单位（即控制类型为组织受限），系统给异常名录管理员预警提示
     * @param orgCatForm
     */
    private void remindChief(OrgCatForm orgCatForm) {

        QlQueryWrapper qlQueryWrapper = QlWrappers.query(MqlType.ORGCATFORM, "ocf");
        qlQueryWrapper.select(QlQueryFieldWrapper.field("ocf", OrgCatForm::getSupplierControlType),QlQueryFieldWrapper.field("ocfcd", OrgCatFormCategory::getOrgId));
        qlQueryWrapper.eq(QlQueryFieldWrapper.field("ocf", OrgCatForm::getVendorId), orgCatForm.getVendorId());
        qlQueryWrapper.eq(QlQueryFieldWrapper.field("ocf", OrgCatForm::getApproveStatus), ApproveStatusType.APPROVED.name());
        qlQueryWrapper.in(QlQueryFieldWrapper.field("ocf", OrgCatForm::getSupplierControlType), Lists.newArrayList(PjSupplierControlType.POSITION_LIMIT_FLAG.name(),PjSupplierControlType.POSITION_LIMIT_FLAG_REMOVE.name()));
        qlQueryWrapper.innerJoin(MqlType.ORGCATFORMCATEGORYDETAIL, "ocfcd", oncondition ->
                oncondition.eq(QlQueryFieldWrapper.field("ocf", OrgCatForm::getOrgCatFormId), QlQueryFieldWrapper.field("ocfcd", OrgCatFormCategory::getOrgCatFormId))
                        .eq(QlQueryFieldWrapper.field("ocfcd", OrgCatFormCategory::getSelected), YesOrNo.YES.getValue())
                        .eq(QlQueryFieldWrapper.field("ocfcd", OrgCatFormCategory::getControlFlag), "1")
        );
        List<Record> recordList = qlService.queryByWrapper(qlQueryWrapper, Record.class);

        List<Long> orgList = recordList.stream()
                .filter(record -> Objects.equals(record.get(OrgCatForm::getSupplierControlType), PjSupplierControlType.POSITION_LIMIT_FLAG.name()))
                .map(record -> record.get(OrgCatFormCategory::getOrgId))
                .collect(Collectors.toList());

        List<Long> orgRemoveList = recordList.stream()
                .filter(record -> Objects.equals(record.get(OrgCatForm::getSupplierControlType), PjSupplierControlType.POSITION_LIMIT_FLAG_REMOVE.name()))
                .map(record -> record.get(OrgCatFormCategory::getOrgId))
                .collect(Collectors.toList());

        // 不能使用removeAll, 受限 解除受限 受限 后 组织丢失
        orgRemoveList.forEach(e -> orgList.remove(e));

        log.info("orgCatForm:{},remindChief orgList size:{}", orgCatForm.getOrgCatFormId(), orgList.size());
        Set<String> orgNameSet = orgList.stream()
                .distinct()
                .map(e-> baseExtClient.getBuOrg(e))
                .filter(Objects::nonNull)
                .map(Organization::getOrganizationName)
                .collect(Collectors.toSet());
        log.info("orgCatForm:{},remindChief orgNameSet size:{}", orgCatForm.getOrgCatFormId(),orgNameSet.size());

        //提醒管理员
        int num2 = 2;
        if(orgNameSet.size()<num2){
           return;
        }
        SystemConfigureDTO systemConfigure = baseClient.getSystemConfigure("EXCEPTION_REMIND_LIST");
        String paramValue = systemConfigure.getParamValue();
        List<String> noticeManList = Arrays.asList(paramValue.split(","));
        String formatStr = "%s为%s限制单位供应商，请予以关注处理。";
        pjProjectExtClient.workNotices(String.format(formatStr,orgCatForm.getVendorName(), orgNameSet.stream().collect(Collectors.joining("和"))),noticeManList);
    }



    private void dealWithPositionLimitFlagRemove(Long businessId, OrgCatForm orgCatForm, List<OrgCatFormCategory> selectedRangeList) {
        //供应商品类库的组织状态变成生效,供应商异常信息对应的信息逻辑删除，当供应商异常信息里面的单位限制都删除，《供应商清单》是否单位受限改成否
        if (CollectionUtils.isNotEmpty(selectedRangeList)) {
            List<Long> orgIdList = selectedRangeList.stream().map(OrgCatFormCategory::getOrgId).collect(Collectors.toList());
            qlService.updateByWrapper(QlWrappers.update(MqlType.ORG_CATEGORY)
                    .eq("companyId", orgCatForm.getVendorId())
                    .in("orgId", orgIdList)
                    .set("pjOrgStatus", Enable.Y.name()));

            // 当供应商异常信息里面的单位限制都删除，《供应商清单》是否单位受限改成否
            qlService.updateByWrapper(QlWrappers.update(MqlType.NPM_COMPANY_EXCEPTION_INFO)
                    .eq("companyId", orgCatForm.getVendorId())
                    .eq("exceptionType", PjSupplierControlType.POSITION_LIMIT_FLAG)
                    .in("orgOrCategoryId", orgIdList)
                    .set("deleteFlag", Enable.Y.name())
            );
            List<Record> records = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_COMPANY_EXCEPTION_INFO)
                    .eq("companyId", orgCatForm.getVendorId())
                    .eq("deleteFlag", Enable.N.name())
                    .eq("exceptionType", PjSupplierControlType.POSITION_LIMIT_FLAG), Record.class);

            if (CollectionUtils.isEmpty(records)) {
                qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                        .eq("companyId", orgCatForm.getVendorId())
                        .set("positionLimitFlag", Enable.N.name()));
            }
        }
    }

    private void dealWithCategoryLimitFlag(Long businessId, OrgCatForm orgCatForm, List<OrgCatFormCategory> selectedDetailList) {
        //供应商品类库的品类状态变成失效，《供应商清单》是否品类受限改成是，供应商异常信息记录一条信息
        if (CollectionUtils.isNotEmpty(selectedDetailList)) {
//            List<Long> categoryIdList = selectedDetailList.stream().map(OrgCatFormCategory::getCategoryId).collect(Collectors.toList());
            Map<Long, OrgCatFormCategory> categoryIdMap = selectedDetailList.stream().collect(Collectors.toMap(OrgCatFormCategory::getCategoryId, Function.identity(), (k1, k2) -> k2));
            for (OrgCatFormCategory orgCatFormCategory : selectedDetailList) {
                qlService.updateByWrapper(QlWrappers.update(MqlType.ORG_CATEGORY)
                                .eq("companyId", orgCatForm.getVendorId())
                                .eq("categoryId", orgCatFormCategory.getCategoryId())
                                .eq("orgId", orgCatFormCategory.getOrgId())
                                .set("pjCategoryStatus", Enable.N.name()));
            }

            qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                    .eq("companyId", orgCatForm.getVendorId())
                    .set("categoryLimitFlag", Enable.Y.name()));

            List<Record> saveList = new ArrayList<>();
            for (Long key : categoryIdMap.keySet()) {
                OrgCatFormCategory catFormCategory = categoryIdMap.get(key);
                Record record = new Record();
                record.put("companyId", orgCatForm.getVendorId());
                record.put("businessId", businessId);
                record.put("exceptionType", PjSupplierControlType.CATEGORY_LIMIT_FLAG);
                record.put("exceptionInfo", catFormCategory.getCategoryName());
                record.put("orgOrCategoryId", catFormCategory.getCategoryId());
                record.put("orgOrCategoryCode", catFormCategory.getCategoryCode());
                saveList.add(record);
            }
            qlService.create(MqlType.NPM_COMPANY_EXCEPTION_INFO, saveList);
        }
    }

    private void dealWithCategoryLimitFlagRemove(Long businessId, OrgCatForm orgCatForm, List<OrgCatFormCategory> selectedDetailList) {
        //供应商品类库的品类状态变成生效,供应商异常信息对应的信息逻辑删除，当供应商异常信息里面的品类限制都删除，《供应商清单》是否品类受限改成否
        if (CollectionUtils.isNotEmpty(selectedDetailList)) {
            List<Long> categoryIdList = selectedDetailList.stream().map(OrgCatFormCategory::getCategoryId).collect(Collectors.toList());
            for (OrgCatFormCategory orgCatFormCategory : selectedDetailList) {
                qlService.updateByWrapper(QlWrappers.update(MqlType.ORG_CATEGORY)
                        .eq("companyId", orgCatForm.getVendorId())
                        .eq("categoryId", orgCatFormCategory.getCategoryId())
                        .eq("orgId", orgCatFormCategory.getOrgId())
                        .set("pjCategoryStatus", Enable.N.name()));
            }

            // 当供应商异常信息里面的品类限制都删除，《供应商清单》是否品类受限改成否
            qlService.updateByWrapper(QlWrappers.update(MqlType.NPM_COMPANY_EXCEPTION_INFO)
                    .eq("companyId", orgCatForm.getVendorId())
                    .eq("exceptionType", PjSupplierControlType.CATEGORY_LIMIT_FLAG)
                    .in("orgOrCategoryId", categoryIdList)
                    .set("deleteFlag", Enable.Y.name())
            );
            List<Record> records = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_COMPANY_EXCEPTION_INFO)
                    .eq("companyId", orgCatForm.getVendorId())
                    .eq("deleteFlag", Enable.N.name())
                    .eq("exceptionType", PjSupplierControlType.CATEGORY_LIMIT_FLAG), Record.class);

            if (CollectionUtils.isEmpty(records)) {
                qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                        .eq("companyId", orgCatForm.getVendorId())
                        .set("categoryLimitFlag", Enable.N.name()));
            }
        }
    }


    private void dealWithTimeLimitFlag(Long businessId, OrgCatForm orgCatForm) {
        //供应商清单的是否限制时间为是，回写限制时间，供应商异常信息记录一条信息
        List<Record> orgCatFormRecord = qlService.queryByWrapper(QlWrappers.query(MqlType.ORGCATFORM)
                .eq("orgCatFormId", businessId), Record.class);
        Long companyId = orgCatForm.getVendorId();
        qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                .eq("companyId", companyId)
                .set("timeLimitFlag", Enable.Y.name())
                .set("limitDate", orgCatFormRecord.get(0).get("timeLimitDate"))
        );
        Record record = new Record();
        record.put("companyId", orgCatForm.getVendorId());
        record.put("businessId", businessId);
        record.put("exceptionType", PjSupplierControlType.TIME_LIMIT_FLAG);
        qlService.create(MqlType.NPM_COMPANY_EXCEPTION_INFO, Arrays.asList(record));
    }

    private void dealWithTimeLimitFlagRemove(Long businessId, OrgCatForm orgCatForm) {
        //供应商清单的是否限制时间为否，清空限制时间,供应商异常信息对应的信息逻辑删除
        Long companyId = orgCatForm.getVendorId();
        qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                .eq("companyId", companyId)
                .set("timeLimitFlag", Enable.N.name())
                .set("limitDate", null)
        );
        qlService.updateByWrapper(QlWrappers.update(MqlType.NPM_COMPANY_EXCEPTION_INFO)
                .eq("companyId", orgCatForm.getVendorId())
                .eq("exceptionType", PjSupplierControlType.TIME_LIMIT_FLAG)
                .set("deleteFlag", Enable.Y.name()));
    }


    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        orgCatFormServiceImpl.rejectFlow(businessId, param);
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        orgCatFormServiceImpl.withdrawFlow(businessId, param);
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        orgCatFormServiceImpl.destoryFlow(businessId, param);
    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }

    /**
     * 封装 根据类别启动流程接口参数
     * @param businessId
     * @return
     */
    public String getDataPushFlow(Long businessId){
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);

        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName());
        bpmParam.setProcessGroupId(processGroupId2);
        JSONObject processVars = new JSONObject();
        QueryWrapper<OrgCatForm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ORG_CAT_FORM_ID", businessId);
        Map<String, Object> xzrq = orgCatFormService.getMap(queryWrapper);
        processVars.put("XZRQ", xzrq.get("TIME_LIMIT_DATE") == null ? null : BpmResult.formatDate(xzrq.get("TIME_LIMIT_DATE")));

        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("---------getDataPushFlow-----------businessId:{}---param:{}" ,businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&& com.alibaba.cloud.commons.lang.StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null|| com.alibaba.cloud.commons.lang.StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        //查询附件
        List<RecordDTO> fileList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.ORG_CAT_FORM_FILE).eq(OrgCatForm::getOrgCatFormId, businessId));
        List<Object> itemDataList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(fileList)) {
            fileList.stream().forEach(file -> {

                Map<String, Object> fileItem = new HashMap<>(16);

                List<Map<String, Object>> gyssxfjList = new ArrayList<>(fileList.size());
                Map<String, Object> fileMap = new HashMap<>(2);
                gyssxfjList.add(fileMap);
                fileMap.put("FILE_PATH_BYMOBILE", "");
                fileMap.put("FILE_NAME", file.get("fileName"));
                fileMap.put("FILE_PATH", String.format(addressPath + "fileSourceName=%s&fileuploadId=%s", file.get("fileName"), Objects.toString(file.get("fileId"))));

                fileItem.put("FJMC", gyssxfjList);
                fileItem.put("BZ", file.get("remark"));
                fileItem.put("__TABLE", "BO_EU_GYSSXFJ");
                itemDataList.add(fileItem);
            });
        }

        OrgCatForm orgCatForm = orgCatFormService.getById(businessId);
        QueryWrapper<OrgCatForm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ORG_CAT_FORM_ID", businessId);
        //Map<String, Object> xzrq = orgCatFormService.getMap(queryWrapper);
        Map<String, Object> orgCatFormMap = new HashMap<>(16);
        orgCatFormMap.put("GYSMC", orgCatForm.getVendorName());
        List<DictItem> diList = baseClient.listDictItemByDictCode("SUPPLIER_CONTROL_TYPE2");
        orgCatFormMap.put("KZLX", BpmResult.getDictName(diList, orgCatForm.getSupplierControlType()));
        orgCatFormMap.put("SXRQ", BpmResult.formatLocalDate(orgCatForm.getStartDate()));
        orgCatFormMap.put("KZDH", orgCatForm.getOrgCatFormNumber());
        orgCatFormMap.put("ZT", getStatus(orgCatForm.getApproveStatus()));
        orgCatFormMap.put("CJR", orgCatForm.getCreatedFullName());
        orgCatFormMap.put("CJSJ", BpmResult.sdfDate(orgCatForm.getCreationDate()));
        Record record = qlService.readByKey(MqlType.ORGCATFORM, businessId, Record.class);
        log.info("aaaaaaaaa===" + JSONObject.toJSONString(record));
        //processVars.put("XZRQ", record.get("timeLimitDate") == null ? null : BpmResult.formatDate(record.get("timeLimitDate")));
        orgCatFormMap.put("XZRQ", record.get("timeLimitDate"));
        orgCatFormMap.put("QT", orgCatForm.getOtherExplain());

        List<OrgCatFormCategory> selectedRangeList = orgCatFormCategoryService.list(new QueryWrapper<>(new OrgCatFormCategory()
                .setOrgCatFormId(orgCatForm.getOrgCatFormId())
                .setSelected(YesOrNo.YES.getValue())));
        List<OrgCatFormCategory> kzfw = selectedRangeList.stream().filter(e -> "1".equals(e.getControlFlag())).collect(Collectors.toList());
        List<OrgCatFormCategory> kzmx = selectedRangeList.stream().filter(e -> "2".equals(e.getControlFlag())).collect(Collectors.toList());
        List<String> kzdwList = selectedRangeList.stream().map(s -> s.getOrgName()).distinct().collect(Collectors.toList());
        kzdwList.forEach(e -> {
            Map<String,Object> kzdw = new HashMap<>(16);
            kzdw.put("__TABLE", "BO_EU_KZDW");
            kzdw.put("GS", e);
            itemDataList.add(kzdw);
        });
        for (OrgCatFormCategory e : kzfw) {
            Map<String,Object> kzfwMap = new HashMap<>(16);
            kzfwMap.put("__TABLE", "BO_EU_KZFW");
            kzfwMap.put("PL", e.getCategoryName());
            itemDataList.add(kzfwMap);
        }
        for (OrgCatFormCategory e : kzmx) {
            Map<String,Object> kzmxMap = new HashMap<>(16);
            kzmxMap.put("__TABLE", "BO_EU_KZMX");
            kzmxMap.put("YWST", e.getOrgName());
            kzmxMap.put("PLX", e.getCategoryName());
            itemDataList.add(kzmxMap);
        }
        String processTitle = "供应商受限-"+orgCatForm.getVendorName();
        String mainTable = "BO_EU_GYSSX";
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
        tableList.add("BO_EU_KZDW");
        tableList.add("BO_EU_KZFW");
        tableList.add("BO_EU_KZMX");
        tableList.add("BO_EU_GYSSXFJ");
        Map<String,Object> itemFile = new HashMap<>(16);
        itemFile.put("BO_EU_GYSSXFJ", new HashSet<>(Arrays.asList("FJMC")));
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, orgCatFormMap, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile);
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    public static String getStatus(String code) {
        Map<String, String> map = new HashMap<>(16);
        map.put("DRAFT", "拟定");
        map.put("SUBMITTED", "已提交");
        map.put("REJECTED", "已驳回");
        map.put("APPROVED", "已审批");
        map.put("ABANDONED", "已废弃");
        map.put("WITHDRAW", "已撤回");
        return map.get(code);
    }

}

