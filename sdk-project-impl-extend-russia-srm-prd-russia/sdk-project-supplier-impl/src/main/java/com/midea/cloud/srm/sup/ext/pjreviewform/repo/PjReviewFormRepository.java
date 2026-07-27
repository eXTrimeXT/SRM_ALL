package com.midea.cloud.srm.sup.ext.pjreviewform.repo;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.MainType;
import com.midea.cloud.common.enums.OrgCateBillType;
import com.midea.cloud.common.enums.RoleType;
import com.midea.cloud.common.enums.review.FormType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.BeanCopyUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.meiql.api.action.DefaultAction;
import com.midea.cloud.meiql.api.service.QlCondition;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.ProxyQlQueryAction;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.CrudRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.common.enums.CategoryStatus;
import com.midea.cloud.srm.model.common.enums.CompanyStatusEnum;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sup.orgcategory.entity.PjOrgCategory;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.FinanceInfoForReviewForm;
import com.midea.cloud.srm.model.supplier.info.entity.OrgCategory;
import com.midea.cloud.srm.model.supplierauth.entry.entity.*;
import com.midea.cloud.srm.model.supplierauth.review.entity.CateJournal;
import com.midea.cloud.srm.model.supplierauth.review.entity.OrgJournal;
import com.midea.cloud.srm.model.supplierauth.review.entity.ReviewForm;
import com.midea.cloud.srm.sup.info.service.ICompanyInfoService;
import com.midea.cloud.srm.sup.info.service.IOrgCategoryService;
import com.midea.cloud.srm.sup.meiql.dto.ReviewFormWrapDTO;
import com.midea.cloud.srm.supauth.entry.service.*;
import com.midea.cloud.srm.supauth.review.service.ICateJournalService;
import com.midea.cloud.srm.supauth.review.service.IOrgJournalService;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 资质审查二开自定义action
 *
 * @author LUXC18
 * @date 2023/9/28 17:42
 */
@Slf4j
@Component
public class PjReviewFormRepository extends CrudRepository {

    @Autowired
    private IOrgCategoryService orgCategoryService;

    @Autowired
    private QlService qlService;

    @Autowired
    private RbacClient rbacClient;

    @Resource
    private ICompanyInfoService companyInfoService;

    @Autowired
    private IEntryConfigJudgeUtilService iEntryConfigJudgeUtilService;

    @Autowired
    private IEntryConfigRecordService iEntryConfigRecordService;

    @Autowired
    private IEntryConfigRecordNodeService entryConfigRecordNodeService;

    @Autowired
    private IEntryConfigNodeService entryConfigNodeService;

    @Autowired
    private IEntryRelationRecordService iEntryRelationRecordService;

    @Autowired
    private ICateJournalService cateJournalService;

    @Autowired
    private IOrgJournalService orgJournalService;

    @Autowired
    private IEntryConfigCheckSPIService entryConfigCheckSPIService;

    public PjReviewFormRepository() {
        this.register("saveWithoutFlow", this::saveWithoutFlow, true, "不触发审批流暂存");
        this.register("submitWithoutFlow", this::submitWithoutFlow, true, "不触发审批流提交");
    }

    /**
     * 不触发审批流暂存
     * @param qlQueryAction
     * @return
     */
    private QlResult saveWithoutFlow(QlQueryAction qlQueryAction) {
        List<Record> recs = PayloadWrapper.of(qlQueryAction.getType(), qlQueryAction.getPayload()).asRecords();
        recs.get(0).put("financeInfoList",new ArrayList<>());
        QlQueryAction queryAction = ProxyQlQueryAction.proxy(qlQueryAction, DefaultAction.SAVE.value(),recs);
        queryAction.setDoValidate(false);
        QlResult qlResult = super.doSave(queryAction, recs);
        return qlResult;
    }

    /**
     * 校验供应商品类
     * @param reviewFormWrap
     */
    private void checkOrgCategory(ReviewFormWrapDTO reviewFormWrap) {

        List<OrgJournal> orgJournals = reviewFormWrap.getOrgJournals();
        List<CateJournal> cateJournals = reviewFormWrap.getCateJournals();

        Map<Long, String> orgMap = orgJournals.stream().filter(k -> ObjectUtils.allNotNull(k, k.getOrgId())).collect(Collectors.toMap(k -> k.getOrgId(), v -> v.getOrgName(), (k1, k2) -> k2));
        Map<Long, String> categoryMap = cateJournals.stream().filter(k -> ObjectUtils.allNotNull(k, k.getCategoryId())).collect(Collectors.toMap(k -> k.getCategoryId(), v -> v.getCategoryName(), (k1, k2) -> k2));

        if(MapUtils.isNotEmpty(categoryMap) && MapUtils.isNotEmpty(orgMap)) {
            List<Record> orgCategories = this.qlService.queryByWrapper(QlWrappers.query(MqlType.ORGCATEGORY)
                    .eq(PjOrgCategory::getCompanyId, reviewFormWrap.getVendorId())
                    .in(PjOrgCategory::getOrgId, new ArrayList<>(orgMap.keySet()))
                    .in(PjOrgCategory::getCategoryId, new ArrayList<>(categoryMap.keySet()))
                    .eq(PjOrgCategory::getServiceStatus, CategoryStatus.QUALIFIED.name()),Record.class);

            if(CollectionUtils.isNotEmpty(orgCategories)) {

                List<String> errorList = orgCategories.stream().map(s -> MessageFormat.format("{0}-{1}", orgMap.get(s.get(OrgCategory::getOrgId)), categoryMap.get(s.get(OrgCategory::getCategoryId)))).distinct().collect(Collectors.toList());
                throw new BaseException(StringUtils.join(errorList.stream().collect(Collectors.joining(SrmConstant.SIG_3)), " 已经存在品类状态合格的数据"));
            }
        }
    }

    /**
     * 保存方法
     * @param queryAction
     * @param recs
     * @return
     */
    @Override
    protected QlResult doSave(QlQueryAction queryAction, List<Record> recs) {
        String type = queryAction.getType();
        ReviewFormWrapDTO reviewFormWrapDTO = BeanCopyUtil.convertWithExtensions(recs.get(0), ReviewFormWrapDTO.class);

        checkOrgCategory(reviewFormWrapDTO);

        List<OrgJournal> orgJournals = reviewFormWrapDTO.getOrgJournals();
        List<CateJournal> cateJournals = reviewFormWrapDTO.getCateJournals();
        List<FinanceInfoForReviewForm> financeInfoList = reviewFormWrapDTO.getFinanceInfoList();

        List<Map<String, Object>> financeInfoRecords = (List<Map<String, Object>>) recs.get(0).get("financeInfoList");
        List<Map<String, Object>> cateJournalsRecords = (List<Map<String, Object>>) recs.get(0).get("cateJournals");
        List<Map<String, Object>> orgJournalsRecords = (List<Map<String, Object>>) recs.get(0).get("orgJournals");
        for (Map<String, Object> financeInfoRecord : financeInfoRecords) {
            financeInfoRecord.put("companyId",reviewFormWrapDTO.getVendorId());
            financeInfoRecord.put("companyCode",reviewFormWrapDTO.getVendorCode());
            financeInfoRecord.put("companyName",reviewFormWrapDTO.getVendorName());
        }
        for (Map<String, Object> cateJournalsRecord : cateJournalsRecords) {
            cateJournalsRecord.put("formType", FormType.REVIEW_FORM.name());
        }
        for (Map<String, Object> orgJournalsRecord : orgJournalsRecords) {
            orgJournalsRecord.put("formType", FormType.REVIEW_FORM.name());
        }
        // 3.检查准入流程对应品类是否正确
        checkEntryConfig(reviewFormWrapDTO, cateJournals);
        QlResult qlResult = super.doSave(queryAction, recs);
        List<Long> reviewFormIds = (List<Long>) qlResult.getRecords();
        reviewFormWrapDTO.setReviewFormId(reviewFormIds.get(0));
        // 1.更新供应商为非潜在
        CompanyInfo companyInfo = companyInfoService.getById(reviewFormWrapDTO.getVendorId());
        if (companyInfo != null) {
            companyInfo.setPotentialFlag(Enable.N.name());
            companyInfoService.updateById(companyInfo);
        }
        // 2.生成准入流程记录
        this.saveEntryConfigRecord(reviewFormWrapDTO, orgJournals, cateJournals);
        // 3.保存即生成品类数据
        iEntryConfigJudgeUtilService.generateOrgCategorys(orgJournals, cateJournals, reviewFormWrapDTO.getReviewFormId(), null, OrgCateBillType.REVIEW_FORM.getValue());
        // 4. 如果引入的供应商+组织,已有供应商状态合格,则供应商状态修改为合格 23-07-26
        List<Long> orgIdList = orgJournals.stream().map(OrgJournal::getOrgId).collect(Collectors.toList());
        List<OrgCategory> orgCategoryList = orgCategoryService.list(Wrappers.lambdaQuery(OrgCategory.class)
                .eq(OrgCategory::getCompanyId, reviewFormWrapDTO.getVendorId())
                .in(OrgCategory::getOrgId, orgIdList));
        if (CollectionUtils.isNotEmpty(orgCategoryList)) {
            Map<CompanyStatusEnum, List<OrgCategory>> groupMap = orgCategoryList.stream().collect(Collectors.groupingBy(OrgCategory::getCompanyStatus));
            if (groupMap.containsKey(CompanyStatusEnum.QUALIFIED)) {
                List<Long> idList = orgCategoryList.stream().map(OrgCategory::getOrgCategoryId).collect(Collectors.toList());
                orgCategoryService.update(Wrappers.lambdaUpdate(OrgCategory.class)
                        .set(OrgCategory::getCompanyStatus, CompanyStatusEnum.QUALIFIED)
                        .in(OrgCategory::getOrgCategoryId, idList));
            }
        }
        return qlResult;
    }

    /**
     * 不触发审批流提交
     *
     * @return
     */
    private QlResult submitWithoutFlow(QlQueryAction qlQueryAction) {
        List<Record> recs = PayloadWrapper.of(qlQueryAction.getType(), qlQueryAction.getPayload()).asRecords();
        //  以供应商+品类+组织维度直接生成供应商品类库数据，品类状态为验证中（如果已经存在数据，合格不改状态)
        ReviewFormWrapDTO reviewFormWrapDTO = BeanCopyUtil.convertWithExtensions(recs.get(0), ReviewFormWrapDTO.class);
        List<OrgJournal> orgJournals = reviewFormWrapDTO.getOrgJournals();
        List<CateJournal> cateJournals = reviewFormWrapDTO.getCateJournals();

        Assert.isTrue(Enable.Y.name().equals(reviewFormWrapDTO.getIfSiteForm()), "是否招标为是时才可执行");
        Assert.isTrue(CollectionUtils.isNotEmpty(orgJournals), "组织信息不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cateJournals), "品类信息不能为空");

        List<Long> orgIdList = orgJournals.stream().map(OrgJournal::getOrgId).collect(Collectors.toList());
        List<Long> categoryIdList = cateJournals.stream().map(CateJournal::getCategoryId).collect(Collectors.toList());

        List<OrgCategory> orgCategoryList = new ArrayList<>();

        List<Record> orgCategories = this.qlService.queryByWrapper(QlWrappers.query(MqlType.ORGCATEGORY)
                .eq(PjOrgCategory::getCompanyId, reviewFormWrapDTO.getVendorId())
                .in(PjOrgCategory::getOrgId, orgIdList)
                .in(PjOrgCategory::getCategoryId, categoryIdList)
                .eq(PjOrgCategory::getPjCategoryStatus,Enable.N.name()),Record.class);

        Assert.isTrue(CollectionUtils.isEmpty(orgCategories),"已存在品类状态为失效的品类关系数据,请检查");

        List<OrgCategory> dbOrgCategoryList = orgCategoryService.list(Wrappers.lambdaQuery(OrgCategory.class)
                .eq(OrgCategory::getCompanyId, reviewFormWrapDTO.getVendorId())
                .in(OrgCategory::getOrgId, orgIdList)
                .in(OrgCategory::getCategoryId, categoryIdList));

        if (CollectionUtils.isNotEmpty(dbOrgCategoryList)) {
            Map<String, OrgCategory> dbMap = dbOrgCategoryList.stream().collect(Collectors.toMap(item -> item.getCompanyId() + "-" + item.getOrgId() + "-" + item.getCategoryId(), Function.identity(), (k1, k2) -> k2));
            for (OrgJournal orgJournal : orgJournals) {
                for (CateJournal cateJournal : cateJournals) {
                    String key = reviewFormWrapDTO.getVendorId() + "-" + orgJournal.getOrgId() + "-" + cateJournal.getCategoryId();
                    if (dbMap.containsKey(key)) {
                        OrgCategory orgCategory = dbMap.get(key);
                        orgCategory.setServiceStatus(CategoryStatus.VERIFY);
                        orgCategory.setCompanyStatus(CompanyStatusEnum.IMPORTING);
                        orgCategoryList.add(orgCategory);
                    } else {
                        OrgCategory orgCategory = buildOrgCategory(reviewFormWrapDTO, orgJournal, cateJournal);
                        orgCategoryList.add(orgCategory);
                    }
                }
            }

        } else {
            for (OrgJournal orgJournal : orgJournals) {
                for (CateJournal cateJournal : cateJournals) {
                    OrgCategory orgCategory = buildOrgCategory(reviewFormWrapDTO, orgJournal, cateJournal);
                    orgCategoryList.add(orgCategory);
                }
            }
        }
        // 保存/更新品类关系表
        if (CollectionUtils.isNotEmpty(orgCategoryList)) {
            orgCategoryService.saveOrUpdateBatch(orgCategoryList);
        }
        recs.get(0).put("financeInfoList",new ArrayList<>());
        QlQueryAction queryAction = ProxyQlQueryAction.proxy(qlQueryAction, DefaultAction.SAVE.value(),recs);
        queryAction.setDoValidate(false);
        QlResult qlResult = super.doSave(queryAction, recs);
        // 用户角色主账号权限授权
        User user = rbacClient.getUserByParmForAnon(new User()
                .setCompanyId(reviewFormWrapDTO.getVendorId())
                .setMainType(MainType.Y.name()));
        /*if(user == null || user.getUserId() == null){
            Assert.isTrue(false,"请维护该供应商主账号");
        }*/
        if(user != null && user.getUserId() != null){
            Long userId = user.getUserId();
            rbacClient.modifyRoleByUserIdForAnon(userId, RoleType.SUPPLIER_INIT.name());
        }
        return qlResult;
    }

    private OrgCategory buildOrgCategory(ReviewFormWrapDTO reviewFormWrapDTO, OrgJournal orgJournal, CateJournal cateJournal) {
        OrgCategory orgCategory = new OrgCategory();
        orgCategory.setOrgCategoryId(IdGenrator.generate());
        orgCategory.setCompanyId(reviewFormWrapDTO.getVendorId());
        orgCategory.setOrgId(orgJournal.getOrgId());
        orgCategory.setOrgCode(orgJournal.getOrgCode());
        orgCategory.setOrgName(orgJournal.getOrgName());
        orgCategory.setCategoryId(cateJournal.getCategoryId());
        orgCategory.setCategoryCode(cateJournal.getCategoryCode());
        orgCategory.setCategoryName(cateJournal.getCategoryName());
        orgCategory.setServiceStatus(CategoryStatus.VERIFY);
        orgCategory.setCompanyStatus(CompanyStatusEnum.IMPORTING);
        return orgCategory;

    }


    @Override
    protected void afterCreate(QlQueryAction queryAction, Collection<Record> records) {
        super.afterCreate(queryAction, records);
    }

    @Override
    public QlCondition beforeRead(QlQueryAction queryAction, Collection keys) {
        return null;
    }

    @Override
    public QlCondition beforeQuery(QlQueryAction queryAction, QueryParam payload) {
        return null;
    }

    /**
     * 删除，要判断状态为
     *
     * @param records
     */
    @Override
    protected void beforeDelete(QlQueryAction queryAction, Collection<Record> records) {
        // 获取要删除的所有id列表
        List keys = records.stream().map(r -> r.get("reviewFormId")).collect(Collectors.toList());

        List<ReviewFormWrapDTO> reviewFormDtoList = qlService.readByKeys(queryAction.getType(), keys, ReviewFormWrapDTO.class);
        for (ReviewFormWrapDTO item : reviewFormDtoList) {
            // 删除品类关系
            Long reviewFormId = item.getReviewFormId();
            List<CateJournal> cateJournalList = cateJournalService.list(new QueryWrapper<>(new CateJournal().setFormId(reviewFormId).setFormType(FormType.REVIEW_FORM.name())));
            List<OrgJournal> orgJournalList = orgJournalService.list(new QueryWrapper<>(new OrgJournal().setFormId(reviewFormId).setFormType(FormType.REVIEW_FORM.name())));
            Long vendorId = item.getVendorId();
            if (vendorId != null && CollectionUtils.isNotEmpty(orgJournalList) && CollectionUtils.isNotEmpty(cateJournalList)) {
                List<Long> orgIdList = orgJournalList.stream().map(OrgJournal::getOrgId).collect(Collectors.toList());
                List<Long> categoryIdList = cateJournalList.stream().map(CateJournal::getCategoryId).collect(Collectors.toList());
                orgCategoryService.remove(Wrappers.lambdaQuery(OrgCategory.class)
                        .eq(OrgCategory::getCompanyId, vendorId)
                        .eq(OrgCategory::getServiceStatus, CategoryStatus.VERIFY)
                        .in(OrgCategory::getOrgId, orgIdList)
                        .in(OrgCategory::getCategoryId, categoryIdList));
            }
        }
    }

    private void checkEntryConfig(ReviewFormWrapDTO reviewFormWrapDTO, List<CateJournal> cateJournals) {
        EntryConfig entryConfig = null;
        ReviewForm reviewForm = new ReviewForm();
        BeanCopyUtil.copyProperties(reviewForm,reviewFormWrapDTO);
        for (CateJournal cateJournal : cateJournals) {
            // 排除delete标识的
            if (cateJournal.getCategoryId() == null) {
                log.info("资质审查-品类信息id为空");
                continue;
            }
            //根据资质审查类型和品类ID获取准入配置
            entryConfig = entryConfigCheckSPIService.getEntryConfigByReviewFormDTO(reviewForm,cateJournal);
//            entryConfig = iEntryConfigService.getEntryConfigByTypeAndCategoryId(reviewFormWrapDTO.getQuaReviewType(), cateJournal.getCategoryId());
            AssertUtils.notNull(entryConfig.getEntryConfigId(), LocaleHandler.getLocaleMsg("尚未配置准入流程"), cateJournal.getCategoryName());
            for (CateJournal journal : cateJournals) {
                // 排除delete标识的
                if (journal.getCategoryId() == null) {
                    log.info("资质审查-品类信息id为空");
                    continue;
                }
                //根据资质审查类型和品类ID获取准入配置,逐一比对,查询是否有不一致的流程配置
                EntryConfig config = entryConfigCheckSPIService.getEntryConfigByReviewFormDTO(reviewForm,journal);
//                EntryConfig config = iEntryConfigService.getEntryConfigByTypeAndCategoryId(reviewFormWrapDTO.getQuaReviewType(), journal.getCategoryId());
                AssertUtils.notNull(config.getEntryConfigId(), LocaleHandler.getLocaleMsg("尚未配置准入流程"), journal.getCategoryName());
                if (entryConfig.getEntryConfigId().compareTo(config.getEntryConfigId()) != 0) {
                    throw new BaseException(LocaleHandler.getLocaleMsg("引入品类的准入流程配置不一致,请检查!"));
                }
            }
        }
    }

    private void saveEntryConfigRecord(ReviewFormWrapDTO reviewFormWrapDTO, List<OrgJournal> orgJournals, List<CateJournal> cateJournals) {
        Long reviewFormId = reviewFormWrapDTO.getReviewFormId();
        ReviewForm reviewForm = new ReviewForm();
        BeanCopyUtil.copyProperties(reviewForm,reviewFormWrapDTO);
        EntryConfig entryConfig = entryConfigCheckSPIService.getEntryConfigByReviewFormDTO(reviewForm,cateJournals.get(0));
//        EntryConfig entryConfig = iEntryConfigService.getEntryConfigByTypeAndCategoryId(reviewFormWrapDTO.getQuaReviewType(), cateJournals.get(0).getCategoryId());
        EntryConfigRecord entryConfigRecord = this.iEntryConfigRecordService.selectFirst(Wrappers.lambdaQuery(EntryConfigRecord.class)
                .eq(EntryConfigRecord::getReviewFormId, reviewFormId));
        if (null == entryConfigRecord) {
            entryConfigRecord = new EntryConfigRecord();
            BeanUtils.copyProperties(entryConfig, entryConfigRecord);
            entryConfigRecord.setReviewFormId(reviewFormId);
            entryConfigRecord.setRecordId(IdGenrator.generate());
            iEntryConfigRecordService.save(entryConfigRecord);
        }
        // 快照-明细表
        List<EntryConfigNode> entryConfigNodeList = entryConfigNodeService.list(EntryConfigNode::getEntryConfigId, entryConfig.getEntryConfigId());
        if (CollectionUtils.isNotEmpty(entryConfigNodeList)) {
            List<EntryConfigRecordNode> entryConfigRecordNodeList = BeanCopyUtil.copyListProperties(entryConfigNodeList, EntryConfigRecordNode.class);
            for (EntryConfigRecordNode entryConfigRecordNode : entryConfigRecordNodeList) {
                entryConfigRecordNode.setEntryConfigRecordNodeId(IdGenrator.generate());
                entryConfigRecordNode.setRecordId(entryConfigRecord.getRecordId());
            }
            entryConfigRecordNodeService.saveOrUpdate(entryConfigRecord.getRecordId(), entryConfigRecordNodeList, EntryConfigRecordNode::getRecordId);
        }
        // 记录中间头表
        //删除准入组织和品类行信息
        iEntryRelationRecordService.remove(Wrappers.lambdaQuery(EntryRelationRecord.class)
                .eq(EntryRelationRecord::getRecordId, entryConfigRecord.getRecordId()));

        List<EntryRelationRecord> list = new ArrayList<EntryRelationRecord>();

        if (null != orgJournals && null != cateJournals) {
            for (OrgJournal org : orgJournals) {
                for (CateJournal cate : cateJournals) {
                    if(Objects.isNull(cate.getCategoryId())) {
                        continue;
                    }
                    EntryRelationRecord relation = new EntryRelationRecord();
                    relation.setReviewFormId(reviewFormId);
                    relation.setCompanyId(reviewFormWrapDTO.getVendorId());
                    relation.setCompanyName(reviewFormWrapDTO.getVendorName());
                    relation.setCategoryId(cate.getCategoryId());
                    relation.setCategoryCode(cate.getCategoryCode());
                    relation.setCategoryName(cate.getCategoryName());
                    relation.setOrganizationId(org.getOrgId());
                    relation.setOrganizationCode(org.getOrgCode());
                    relation.setOrganizationName(org.getOrgName());
                    relation.setRecordId(entryConfigRecord.getRecordId());
                    relation.setRelationId(IdGenrator.generate());
                    list.add(relation);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(list)) {
            iEntryRelationRecordService.saveBatch(list);
        }
    }

}
