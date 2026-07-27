package com.midea.cloud.srm.sup.black.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.ApproveStatusType;
import com.midea.cloud.common.enums.CatalogStatusType;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.black.entity.Black;
import com.midea.cloud.srm.model.base.black.entity.BlackCompany;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.OrgCategory;
import com.midea.cloud.srm.model.supplierauth.purchasecatalog.entity.PurchaseCatalog;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sup.black.service.BlackCompanyService;
import com.midea.cloud.srm.sup.black.service.BlackService;
import com.midea.cloud.srm.sup.info.service.ICompanyInfoService;
import com.midea.cloud.srm.sup.info.service.IOrgCategoryService;
import com.midea.cloud.srm.sup.meiql.dto.black.BlackRescindCompanyDTO;
import com.midea.cloud.srm.sup.meiql.dto.black.BlackRescindDTO;
import com.midea.cloud.srm.supauth.purchasecatalog.service.IPurchaseCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 黑名单解除
 * @date: 2023/12/29 16:19
 * @author ex_liuxy46
 */
@Slf4j
@Service
public class PjBlackRescindFlowServiceImpl implements IFlowBusinessCallbackService {

    @Resource
    protected QlService qlService;

    @Resource
    private ICompanyInfoService companyInfoService;

    @Resource
    private IOrgCategoryService orgCategoryService;

    @Resource
    private IPurchaseCatalogService purchaseCatalogService;

    @Resource
    private BlackCompanyService blackCompanyService;

    @Resource
    private BlackService blackService;

    @Resource
    private BaseClient baseClient;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Value("${bpm.hmdjc.processGroupId}")
    private String processGroupId;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;

    private static final String BLACK_RESCIND = "BlackRescind";
    private static final String BLACK_RESCIND_COMPANY = "BlackRescindCompany";

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        updateStatus(businessId, ApproveStatusType.SUBMITTED.name());
    }

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        QlQueryWrapper queryWrapper = QlWrappers.query(BLACK_RESCIND_COMPANY);
        queryWrapper.eq(BlackRescindCompanyDTO::getRescindId, businessId);
        List<BlackRescindCompanyDTO> blackRescindCompanyDTOList = qlService.queryByWrapper(queryWrapper, BlackRescindCompanyDTO.class);
        log.info("要解除的供应商列表:" + JSONObject.toJSONString(blackRescindCompanyDTOList));
        if (CollectionUtils.isNotEmpty(blackRescindCompanyDTOList)) {
            List<Long> companyIdList = blackRescindCompanyDTOList.stream().map(BlackRescindCompanyDTO::getCompanyId).collect(Collectors.toList());
            // 更新品类关系状态,货源清单信息
            updateInfo(companyIdList);
            // 更新黑名单中,最近一条的失效时间
            updateBlackInfo(companyIdList);
        }
        updateStatus(businessId, ApproveStatusType.APPROVED.name());
        // 更新明细的字段
        qlService.updateByWrapper(QlWrappers.update(BLACK_RESCIND_COMPANY)
                .set(BlackRescindCompanyDTO::getEndDate, DateUtil.getStartTimeOfDate(new Date()))
                .eq(BlackRescindCompanyDTO::getRescindId, businessId));
    }
    private void updateBlackInfo(List<Long> companyIdList) {
        List<BlackCompany> blackCompanyList = blackCompanyService.list(Wrappers.lambdaQuery(BlackCompany.class)
                .in(BlackCompany::getCompanyId, companyIdList)
                .orderByDesc(BlackCompany::getLastUpdateDate));
        if (CollectionUtils.isNotEmpty(blackCompanyList)) {
            Map<Long, Long> blackCompanyMap = blackCompanyList.stream().collect(Collectors.toMap(BlackCompany::getCompanyId, BlackCompany::getBlackId, (k1, k2) -> k1));
            List<Long> blackIdList = new ArrayList<>();
            for (Long key : blackCompanyMap.keySet()) {
                if (blackCompanyMap.get(key) != null) {
                    blackIdList.add(blackCompanyMap.get(key));
                }
            }
            log.info("黑名单解除要更新的黑名单明细信息:----" + JSONObject.toJSONString(blackIdList));
            if (CollectionUtils.isNotEmpty(blackIdList)) {
                blackService.update(Wrappers.lambdaUpdate(Black.class)
                        .set(Black::getEndDate, DateUtil.getStartTimeOfDate(new Date()))
                        .in(Black::getBlackId, blackIdList));
            }
        }
    }

    public void updateInfo(List<Long> companyIdList){
        if (CollectionUtils.isNotEmpty(companyIdList)) {
            // 供应商信息更新为非黑名单
            companyInfoService.update(Wrappers.lambdaUpdate(CompanyInfo.class)
                    .set(CompanyInfo::getIsBacklist, Enable.N.name())
                    .set(CompanyInfo::getQuitFlag, Enable.N.name())
                    .in(CompanyInfo::getCompanyId, companyIdList));
            // 品类关系状态回写上一个状态
            List<OrgCategory> list = orgCategoryService.list(Wrappers.lambdaQuery(OrgCategory.class)
                    .in(OrgCategory::getCompanyId, companyIdList));
            if (CollectionUtils.isNotEmpty(list)) {
                for (OrgCategory orgCategory : list) {
                    orgCategory.setCompanyStatus(orgCategory.getOldCompanyStatus() != null ? orgCategory.getOldCompanyStatus() : orgCategory.getCompanyStatus());
                    orgCategory.setServiceStatus(orgCategory.getLastServiceStatus() != null ? orgCategory.getLastServiceStatus() : orgCategory.getServiceStatus());
                    orgCategory.setWarningStatus(orgCategory.getLastWarningStatus() != null ? orgCategory.getLastWarningStatus() : orgCategory.getWarningStatus());
                }
            }
            orgCategoryService.updateBatchById(list);
            // 更新货源清单状态
            purchaseCatalogService.update(Wrappers.lambdaUpdate(PurchaseCatalog.class)
                    .set(PurchaseCatalog::getCatalogStatus, CatalogStatusType.VALID.name())
                    .in(PurchaseCatalog::getVendorId,companyIdList));
        }
    }

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        updateStatus(businessId, ApproveStatusType.REJECTED.name());
    }

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        updateStatus(businessId, ApproveStatusType.WITHDRAW.name());
    }

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        updateStatus(businessId, ApproveStatusType.ABANDONED.name());
    }

    /**
     * @param businessId
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }

    /**
     * @param businessId
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("---------黑名单解除getDataPushFlow-----------businessId:{}---param:{}", businessId, param);
        int i2 = 2;
        int i16 = 16;
        Record br = qlService.readByKey(BLACK_RESCIND, businessId, Record.class);
        Map<String, Object> mainInfoMap = new HashMap<>(i16);
        //单据名称
        mainInfoMap.put("DJMC", br.get("rescindName"));
        //单据状态
        mainInfoMap.put("DJZT", br.get("approveStatus"));
        //创建人
        mainInfoMap.put("CJR", br.get("createdFullName"));
        //创建时间
        mainInfoMap.put("CJSJ", BpmResult.sdfDate(br.getDate("creationDate")));
        //简述
        mainInfoMap.put("JS", br.get("rescindContent"));
        List<Object> itemDataList = new ArrayList<>();
        //解除供应商范围
        List<Record> fwList = qlService.queryByWrapper(QlWrappers.query(BLACK_RESCIND_COMPANY).eq("rescindId", businessId), Record.class);
        for (Record e : fwList) {
            Map<String, Object> map = new HashMap<>(i16);
            map.put("__TABLE", "BO_EU_JCGYSFW");
            //供应商名称
            map.put("GYSMC", e.get("companyName"));
            //供应商编码
            map.put("GYSBM", e.get("companyCode"));
            //统一社会信用代码
            map.put("TYSHXYDM", e.get("lcCode"));
            //法人代表
            map.put("FRDB", e.get("legalPerson"));
            //供应商类型
            map.put("GYSLX", getDictName("SUPPLIER_TYPE", e.getString("supplierType")));
            //企业性质
            map.put("QYXZ", e.get("companyType"));
            //经办人
            map.put("JBR", e.get("agent"));
            //黑名单截止日期
            map.put("HMDJZRQ", BpmResult.sdfDate(e.getDate("endDate")));
            itemDataList.add(map);
        }
        //附件
        /*List<Long> ids = new ArrayList<>();
        ids.add(businessId);
        List<SceneFile> fileList = baseClient.listSceneFileBatch(ids);
        for (SceneFile e : fileList) {
            Map<String, Object> map = new HashMap<>(i16);
            map.put("__TABLE", null);
            map.put("", e);
            itemDataList.add(map);
        }*/
        String processTitle = "黑名单解除-" + br.get("rescindName");
        String mainTable = "BO_EU_HMDJCJBXX";
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
        //解除供应商范围
        tableList.add("BO_EU_JCGYSFW");
        Map<String,Object> itemFile = new HashMap<>(i2);
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, mainInfoMap, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile);
        log.info(JSON.toJSONString(dataPushFlowJsn));
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    public String getDictName(String dictCode, String va) {
        List<DictItemDTO> gyqyList = baseClient.listAllByDictCode(dictCode);
        for (DictItemDTO e : gyqyList) {
            if (e.getDictItemCode().equals(va)) {
                return e.getDictItemName();
            }
        }
        return null;
    }

    public void updateStatus(Long businessId, String status) {
        Record borrow = qlService.readByKey(BLACK_RESCIND, businessId ,Record.class);
        Assert.notNull(borrow, "黑名单解除信息不存在");
        qlService.updateByWrapper(QlWrappers.update(BLACK_RESCIND)
                .set(BlackRescindDTO::getApproveStatus, status)
                .eq(BlackRescindDTO::getRescindId, businessId));
    }
}
