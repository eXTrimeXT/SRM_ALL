package com.midea.cloud.srm.sup.ext.pjsupplier.repo;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.midea.cloud.common.enums.SupplierDataSourceType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Payload;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryAction;
import com.midea.cloud.meiql.api.spec.result.QlResult;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.meiql.core.repository.jooq.ProxyRepository;
import com.midea.cloud.meiql.core.repository.jooq.support.PayloadWrapper;
import com.midea.cloud.meiql.core.repository.jooq.support.QueryParam;
import com.midea.cloud.srm.emun.PjSupplierExceptionTypeEmun;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.RbacExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.common.enums.OperatingBusinessTypeEnum;
import com.midea.cloud.srm.model.common.enums.OperatingEnum;
import com.midea.cloud.srm.model.pm.pr.division.entity.DivisionCategory;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sup.association.dto.ApiExtSupAssociationDTO;
import com.midea.cloud.srm.model.sup.association.entity.ExtSupAssociation;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.ContactInfo;
import com.midea.cloud.srm.model.supplier.invite.entity.InviteVendor;
import com.midea.cloud.srm.model.supplierauth.review.entity.CateJournal;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sup.association.service.ExtSupAssociationEventService;
import com.midea.cloud.srm.sup.ext.pjsupplier.service.PjSupplierService;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 供应商住数据二开自定义action
 *
 * @author LUXC18
 * @date 2023/9/28 17:42
 */
@Slf4j
@Component
public class PjCompanyInfoRepository extends ProxyRepository {
    @Autowired
    private QlOpenClient qlOpenClient;
    @Autowired
    private QlService qlService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    private RbacExtClient rbacExtClient;

    @Autowired
    private ExtSupAssociationEventService extSouInitEventService;

    @Autowired
    private PjSupplierService pjSupplierService;

    @Autowired
    private PjProjectExtClient pjProjectExtClient;

    private final String POTENTIAL_SUPPLIERS = "potentialSuppliers";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${sup.company.bidDataMsgUrl:http://10.246.0.214:16666/search/upload}")
    private String bidDataMsg;

    public PjCompanyInfoRepository() {
        this.register("queryPotentialSupplier", this::queryPotentialSupplier, true, "查询潜在供应商");
        this.register("saveExceptionInfo", this::saveExceptionInfo, true, "异常登记");
        this.registerAfter("vendorSubmit", this::afterVendorSubmit);
        this.registerAfter("queryPotentialSupplier", this::afterQueryPotentialSupplier);
        this.registerAfter("approve", this::afterApprove);
        this.registerAfter("greenSubmit", this::afterGreenSubmit);
        this.registerBefore("reject", this::beforeReject);
        this.registerBefore("greenSubmit", this::beforeGreenSubmit);
    }

    private void beforeReject(QlQueryAction qlQueryAction, Payload payload) {
        log.info("执行了beforeReject方法qlQueryAction" + JSONObject.toJSONString(qlQueryAction));
        log.info("执行了beforeReject方法payload" + JSONObject.toJSONString(payload));
        List<Record> recs = PayloadWrapper.of(qlQueryAction.getType(), qlQueryAction.getPayload()).asRecords();
        qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                .eq("companyId",recs.get(0).getLong("companyId"))
                .set("extRejectAttribute1", recs.get(0).getString("extRejectAttribute1"))
                .set("extRejectAttribute2", recs.get(0).getString("extRejectAttribute2"))
                .set("extRejectAttribute3", recs.get(0).getString("extRejectAttribute3"))
                .set("extRejectAttribute4", recs.get(0).getString("extRejectAttribute4"))
                .set("extRejectAttribute5", recs.get(0).getString("extRejectAttribute5"))
                .set("extRejectAttribute6", recs.get(0).getString("extRejectAttribute6"))
                .set("extRejectAttribute7", recs.get(0).getString("extRejectAttribute7"))
                .set("extRejectAttribute8", recs.get(0).getString("extRejectAttribute8"))
                .set("extRejectAttribute9", recs.get(0).getString("extRejectAttribute9"))
                .set("extRejectAttribute10", recs.get(0).getString("extRejectAttribute10"))
                .set("extRejectAttribute11", recs.get(0).getString("extRejectAttribute11"))
                .set("extRejectAttribute12", recs.get(0).getString("extRejectAttribute12"))
                .set("extRejectAttribute13", recs.get(0).getString("extRejectAttribute13"))
                .set("extRejectAttribute14", recs.get(0).getString("extRejectAttribute14"))
                .set("extRejectAttribute15", recs.get(0).getString("extRejectAttribute15"))
                .set("extRejectAttribute16", recs.get(0).getString("extRejectAttribute16"))
                .set("extRejectAttribute17", recs.get(0).getString("extRejectAttribute17"))
                .set("extRejectAttribute18", recs.get(0).getString("extRejectAttribute18"))
                .set("extRejectAttribute19", recs.get(0).getString("extRejectAttribute19"))
                .set("extRejectAttribute20", recs.get(0).getString("extRejectAttribute20"))
        );
    }

    private void beforeGreenSubmit(QlQueryAction qlQueryAction, Payload payload) {
        log.info("执行了beforeGreenSubmit方法qlQueryAction" + JSONObject.toJSONString(qlQueryAction));
        log.info("执行了beforeGreenSubmit方法payload" + JSONObject.toJSONString(payload));
        List<Record> recs = PayloadWrapper.of(qlQueryAction.getType(), qlQueryAction.getPayload()).asRecords();
        Record record = recs.get(0);
        record.put("dataSources", SupplierDataSourceType.MANUALLY_CREATE.name());
        log.info("执行了beforeGreenSubmit方法");
    }

    /**
     * 后置调用mdm编码获取
     *
     * @param qlQueryAction
     * @param qlResult
     * @param stringCollectionMap
     */
    private void afterApprove(QlQueryAction qlQueryAction, QlResult qlResult, Map<String, Collection<Record>> stringCollectionMap) {
        // 调获取mdm编码
        Long companyId = (Long) qlResult.getRecords().get(0);
        try {
            pjSupplierService.getMdmCodeByCompanyId(companyId);
        } catch (Exception e) {
            log.error("采购商审批后置失败:"+e);
            log.error("采购商审批后置失败:"+e.getMessage());
            // 把操作成功日志删了
            qlOpenClient.delete(ContextPath.BASE,QlOpenWrappers.update("base_operating_log")
                    .eq("businessId",companyId)
                    .eq("businessType", OperatingBusinessTypeEnum.COMPANY_REGISTER.name())
                    .eq("operation", OperatingEnum.PASS.name())
            );
            throw new BaseException(e.getMessage());
        }
        //供应商注册 审核此时 供应商下只有一个用户
        User user = rbacClient.queryByCompanyId(companyId);
        user.setCompanyName(user.getCeeaCompany());
        rbacExtClient.pushVendorSiss(Lists.newArrayList(user));
    }

    /**
     * 后置补齐联系人信息
     *
     * @param qlQueryAction
     * @param qlResult
     */
    private void afterQueryPotentialSupplier(QlQueryAction qlQueryAction, QlResult qlResult, Map<String, Collection<Record>> repoData) {

        Collection<Record> lineRecordList = repoData.get(MqlType.SUPPLIER);
        if (CollectionUtils.isNotEmpty(lineRecordList)) {
            List<Long> companyIdList = lineRecordList.stream().map(item -> item.getLong("companyId")).collect(Collectors.toList());

            List<ContactInfo> contactInfos = qlService.queryByWrapper(QlWrappers.query(MqlType.CONTACTINFO)
                    .in(ContactInfo::getCompanyId, companyIdList)
                    .eq(ContactInfo::getCeeaDefaultContact, Enable.Y.name()), ContactInfo.class);
            if (CollectionUtils.isNotEmpty(contactInfos)) {
                Map<Long, ContactInfo> idContactInfoMap = contactInfos.stream().collect(Collectors.toMap(ContactInfo::getCompanyId, Function.identity(), (k1, k2) -> k2));

                for (Record record : lineRecordList) {
                    Long companyId = record.getLong("companyId");
                    if (idContactInfoMap.containsKey(companyId)) {
                        record.put("contactName", idContactInfoMap.get(companyId).getContactName());
                        record.put("ceeaContactMethod", idContactInfoMap.get(companyId).getCeeaContactMethod());
                    }
                }
            }
        }
    }

    private void afterGreenSubmit(QlQueryAction qlQueryAction, QlResult qlResult, Map<String, Collection<Record>> repoRecords) {
        log.info("绿色通道提交afterVendorSubmit，二开后置逻辑");
        log.info("绿色通道提交qlQueryAction:" + JSONObject.toJSONString(qlQueryAction));
        log.info("绿色通道提交qlResult:" + JSONObject.toJSONString(qlResult));
        log.info("绿色通道提交repoRecords:" + JSONObject.toJSONString(repoRecords));
        Long companyId = (Long) qlResult.getRecords().get(0);
        try {
            CompletableFuture.runAsync(() -> {
                String gscpStatus = pjProjectExtClient.importScreening(companyId);
                qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                        .set("gscpStatus", gscpStatus)
                        .eq("companyId", companyId));
            });
        } catch (BaseException e) {
            throw new BaseException("获取GSCP接口失败,请重试");
        }
    }

    private void afterVendorSubmit(QlQueryAction qlQueryAction, QlResult qlResult, Map<String, Collection<Record>> repoRecords) {
        log.info("供应商提交afterVendorSubmit，二开后置逻辑");
        log.info("qlQueryAction:" + JSONObject.toJSONString(qlQueryAction));
        log.info("qlResult:" + JSONObject.toJSONString(qlResult));
        log.info("repoRecords:" + JSONObject.toJSONString(repoRecords));

        Long companyId = (Long) qlResult.getRecords().get(0);
        try {
            CompletableFuture.runAsync(() -> {
                String gscpStatus = pjProjectExtClient.importScreening(companyId);
                qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                        .set("gscpStatus", gscpStatus)
                        .eq("companyId", companyId));
            });
        } catch (BaseException e) {
            throw new BaseException("获取GSCP接口失败,请重试");
        }

        // 后置提交时校验联系人的电话或邮箱和目前其他供应商的联系人是否一致，
        //  如果一致，则在 《关联供应商》记录下数据，关联关系备注记录“有一样的联系人xxx(电话或邮箱)”
        //如果联系人电话一致，信息保存为：联系人电话一样：具体相同的电话
        //如果联系人邮箱一致，信息保存为：联系人邮箱一样：具体相同的邮箱
        //如果联系人名字一致，信息保存为：联系人名字一样：具体名字
        // 如果存在 A和B关联，不需要插入B和A关联
        List<Record> recs = PayloadWrapper.of(qlQueryAction.getType(), qlQueryAction.getPayload()).asRecords();
        Record record = recs.get(0);
        String companyName = record.getString("companyName");
        String lcCode = record.getString("lcCode");
        List<Record> contactInfos = record.getSubRecords("contactInfos");
        if (CollectionUtils.isNotEmpty(contactInfos)) {
            List<String> emailList = contactInfos.stream().filter(item -> StringUtils.isNotEmpty(item.getString("email")))
                    .map(item -> item.getString("email")).collect(Collectors.toList());
            List<String> ceeaContactMethodList = contactInfos.stream().filter(item -> StringUtils.isNotEmpty(item.getString("ceeaContactMethod")))
                    .map(item -> item.getString("ceeaContactMethod")).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(emailList)) {
                // 相同邮箱的供应商
                List<Record> records = qlService.queryByWrapper(QlWrappers.query(MqlType.CONTACTINFO)
                        .in("email", emailList)
                        .notEq("companyId", companyId)
                        .orderByAsc("creationDate"), Record.class);
                if (CollectionUtils.isNotEmpty(records)) {
                    List<Long> emailExistCompanyIdList = records.stream().map(item -> item.getLong("companyId")).collect(Collectors.toList());
                    String emailExist = records.get(0).getString("email");
                    //组装数据保存
                    try {
                        emailExist = "联系人邮箱一样：" + emailExist;
                        saveRelationCompanyInfo(emailExistCompanyIdList, companyId, companyName,lcCode, emailExist);
                    } catch (Exception e) {
                        log.error("保存关联供应商信息失败" + e);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(ceeaContactMethodList)) {
                // 相同邮箱的供应商
                List<Record> records = qlService.queryByWrapper(QlWrappers.query(MqlType.CONTACTINFO)
                        .in("ceeaContactMethod", ceeaContactMethodList)
                        .notEq("companyId", companyId)
                        .orderByAsc("creationDate"), Record.class);
                if (CollectionUtils.isNotEmpty(records)) {
                    List<Long> ceeaContactMethodExistCompanyIdList = records.stream().map(item -> item.getLong("companyId")).collect(Collectors.toList());
                    String ceeaContactMethodExist = records.get(0).getString("ceeaContactMethod");
                    //组装数据保存
                    try {
                        ceeaContactMethodExist = "联系人电话一样：" + ceeaContactMethodExist;
                        saveRelationCompanyInfo(ceeaContactMethodExistCompanyIdList, companyId, companyName,lcCode, ceeaContactMethodExist);
                    } catch (Exception e) {
                        log.error("保存关联供应商信息失败" + e);
                    }
                }
            }
        }
        //  供应商提交后，如果是邀请供应商，则给邀请人发送一条钉钉通知；
        pjSupplierService.sendDingDingMsg(companyId);
        //调用大数据接口
        sendBigDataMsg(companyName, lcCode);
    }

    public void sendBigDataMsg(String companyName, String lcCode) {
        CompletableFuture.runAsync(() -> {
            try {
                if (StringUtils.isEmpty(lcCode)) {
                    return;
                }
                String url = bidDataMsg + "?company=" + companyName + "&regno=" + lcCode;
                log.info("大数据接口url:" + url);
                ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
                log.info("大数据接口返回:" + JSONObject.toJSONString(forEntity));
            } catch (BaseException e) {
                log.error("company调用大数据接口失败:" + e);
                log.error("company调用大数据接口失败:" + e.getMessage());
            }
        });
    }

    /**
     * 保存关联供应商信息
     *
     * @param companyId
     * @param companyName
     * @param existStr
     */

    private void saveRelationCompanyInfo(List<Long> emailExistCompanyIdList, Long companyId, String companyName,String lcCode, String existStr) {
        List<Record> recordList = qlService.query(MqlType.SUPPLIER, MeiQl.newCondition().in("companyId", emailExistCompanyIdList), Record.class);
        // 根据B的供应商ID,先删后新增
        qlService.deleteByWrapper(QlWrappers.update(com.midea.cloud.srm.model.sou.req.constants.MqlType.SOU_RELATION_SUP_BUYER)
                .eq("vendorIdB",companyId));
        List<Record> filterList = filterExistsSupAssociation(recordList,companyId);
        for (Record existCompanyRecord : filterList) {
            ApiExtSupAssociationDTO param = new ApiExtSupAssociationDTO();
            param.setVendorIdA(existCompanyRecord.getLong("companyId"));
            param.setVendorCodeA(existCompanyRecord.getString("companyCode"));
            param.setVendorNameA(existCompanyRecord.getString("companyName"));
            param.setSocialCreditCodeA(existCompanyRecord.getString("lcCode"));
            param.setVendorIdB(companyId);
            param.setVendorNameB(companyName);
            param.setVendorNameB(companyName);
            param.setSocialCreditCodeB(lcCode);
            param.setAssociationType("STRONG_RELATION");
            param.setAssociationRemark(existStr);
            extSouInitEventService.editProject(param);
        }
    }

    private List<Record> filterExistsSupAssociation(List<Record> recordList, Long companyId) {
        //如果存在 A和B关联，不需要插入B和A关联
        List<Long> comapnyList = recordList.stream().map(s -> s.getLong("companyId")).collect(Collectors.toList());
        List<ExtSupAssociation> associations = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(com.midea.cloud.srm.model.sou.req.constants.MqlType.SOU_RELATION_SUP_BUYER)
                        .select(ExtSupAssociation::getVendorIdA, ExtSupAssociation::getVendorIdB)
                .in(ExtSupAssociation::getVendorIdB, comapnyList)
                .eq(ExtSupAssociation::getVendorIdA, companyId)
                , ExtSupAssociation.class);
        Set<Long> existSet = associations.stream().map(ExtSupAssociation::getVendorIdB).collect(Collectors.toSet());
        return recordList.stream().filter(s -> !existSet.contains(s.getLong("companyId"))).collect(Collectors.toList());
    }


    /**
     * 查询潜在供应商
     *
     * @return
     */
    private QlResult queryPotentialSupplier(QlQueryAction qlQueryAction) {
        QueryParam queryParam = PayloadWrapper.of(qlQueryAction.getType(), qlQueryAction.getPayload()).asQuery();

        if (queryParam.getFilter().containsKey(POTENTIAL_SUPPLIERS)) {
            //服务范围里面对应的二级品类和该登录人员的品类分工的品类对应的二级品类有交集
            LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();

            List<Long> companyIdList = new ArrayList<>();
            // 如果供应商邀请供应商的,邀请人可以查看
            List<InviteVendor> inviteVendors = qlService.queryByWrapper(QlWrappers.query(InviteVendor.class)
                    .eq(InviteVendor::getCreatedId, loginAppUser.getUserId()), InviteVendor.class);
            if (CollectionUtils.isNotEmpty(inviteVendors)) {
                List<String> companyNameList = inviteVendors.stream().map(InviteVendor::getVendorName).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(companyNameList)) {
                    List<CompanyInfo> companyInfos = qlService.queryByWrapper(QlWrappers.query(CompanyInfo.class)
                            .select(CompanyInfo::getCompanyId)
                            .in(CompanyInfo::getCompanyName, companyNameList), CompanyInfo.class);
                    if (CollectionUtils.isNotEmpty(companyInfos)) {
                        companyIdList.addAll(companyInfos.stream().map(CompanyInfo::getCompanyId).collect(Collectors.toList()));
                    }
                }
            }
            List<DivisionCategory> divisionCategoryList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query(DivisionCategory.class)
                    .eq(DivisionCategory::getPersonInChargeUserId, loginAppUser.getUserId()), DivisionCategory.class);
            // 没权限查看
            if (CollectionUtils.isEmpty(divisionCategoryList)) {
                log.info("---没有品类分工权限");
            } else {
                // fullName对比,前2个一样则可以查看
                List<Long> categoryIdList = divisionCategoryList.stream().map(DivisionCategory::getCategoryId).collect(Collectors.toList());
                Map<String, String> idNameMap = baseClient.queryCategoryFullNameByLevelIds(categoryIdList);

                List<String> partNameList = new ArrayList<>();
                for (String value : idNameMap.values()) {
                    String[] names = value.split("-");
                    if (names.length >= 3) {
                        String levelTwoName = names[0] + "-" + names[1];
                        partNameList.add(levelTwoName);
                    }
                }
                // 前端传入供应商品类信息,含前2级的全路径名
                List<CateJournal> cateJournalList = qlService.query(MqlType.CATE_JOURNAL_COMPANY, MeiQl.newCondition()
                        .in("categoryFullName", partNameList), CateJournal.class);

                // 没权限查看
                if (CollectionUtils.isEmpty(cateJournalList)) {
                    log.info("---没有对应品类的供应商");
                } else {
                    companyIdList.addAll(cateJournalList.stream().map(CateJournal::getFormId).collect(Collectors.toList()));
                }
            }
            if (CollectionUtils.isEmpty(companyIdList)) {
                log.info("---完全没权限查看");
                return new QlResult();
            }

            queryParam.getFilter().setValue("companyId", "in", companyIdList);
            qlQueryAction.setPayload(queryParam);
            QlResult qlResult = this.query(qlQueryAction);
            return qlResult;
        } else {
            QlResult qlResult = this.query(qlQueryAction);
            return qlResult;
        }

    }

    private QlResult saveExceptionInfo(QlQueryAction qlQueryAction) {
        List<Record> recs = PayloadWrapper.of(qlQueryAction.getType(), qlQueryAction.getPayload()).asRecords();
        AssertUtils.notEmpty(recs, "参数不能为空");
        Record record = recs.get(0);
        Long companyId = record.getLong("companyId");
        Assert.isTrue(companyId != null, "供应商id不能为空");
        //npmCompanyExceptionInfos
        List<Map<String, Object>> npmCompanyExceptionInfos = (List<Map<String, Object>>) recs.get(0).get("npmCompanyExceptionInfos");
        for (Map<String, Object> npmCompanyExceptionInfo : npmCompanyExceptionInfos) {
            npmCompanyExceptionInfo.put("exceptionType", PjSupplierExceptionTypeEmun.KEY_SUPERVISION_FLAG.name());
        }
        return super.save(noProxy(qlQueryAction));
    }

}
