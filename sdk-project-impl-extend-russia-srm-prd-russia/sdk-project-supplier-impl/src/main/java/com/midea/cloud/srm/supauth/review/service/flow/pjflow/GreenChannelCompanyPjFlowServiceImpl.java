package com.midea.cloud.srm.supauth.review.service.flow.pjflow;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.common.enums.Enable;
import com.midea.cloud.srm.model.file.upload.dto.FileuploadDTO;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.supplier.bpm.dto.ContactInfoDto;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.supplier.info.entity.SiteInfo;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sup.ext.pjsupplier.repo.PjCompanyInfoRepository;
import com.midea.cloud.srm.sup.ext.pjsupplier.service.PjSupplierService;
import com.midea.cloud.srm.sup.info.service.ICompanyInfoService;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
/**
 * @author luxc18
 */
@Slf4j
@Service
public class GreenChannelCompanyPjFlowServiceImpl implements IFlowBusinessCallbackService {

    @Resource(name = "greenChannelFlowServiceImpl")
    private IFlowBusinessCallbackService greenChannelFlowServiceImpl;

    @Value("${bpm.lstdgs.processGroupId}")
    private String processGroupId;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;

    @Autowired
    private ICompanyInfoService companyInfoService;

    @Autowired
    private QlService qlService;

    @Autowired
    private RbacClient rbacClient;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Autowired
    private PjSupplierService pjSupplierService;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    private final String SUNSHINE_FILE_ID = "sunshineFileId";

    @Autowired
    private PjCompanyInfoRepository pjCompanyInfoRepository;

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        greenChannelFlowServiceImpl.submitFlow(businessId,param);
    }

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        greenChannelFlowServiceImpl.passFlow(businessId,param);
        pjSupplierService.getMdmCodeByCompanyId(businessId);
        //    23-11-21 更新信息是否完善标识    infoCompleteFlag
        qlService.updateByWrapper(QlWrappers.update(MqlType.SUPPLIER)
                .set("infoCompleteFlag", Enable.Y.name())
                .eq("companyId", businessId));
        CompanyInfo companyInfo = companyInfoService.getById(businessId);
        pjCompanyInfoRepository.sendBigDataMsg(companyInfo.getCompanyName(),companyInfo.getLcCode());
    }

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        greenChannelFlowServiceImpl.rejectFlow(businessId,param);
    }

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        greenChannelFlowServiceImpl.withdrawFlow(businessId,param);
    }

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        greenChannelFlowServiceImpl.destoryFlow(businessId,param);
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
        log.info("---------getDataPushFlow-----------businessId:{}---param:{}" ,businessId, param);
        CompanyInfo companyInfo = companyInfoService.getById(businessId);
        User user = rbacClient.queryByCompanyId(companyInfo.getCompanyId());
        Map<String, Object> companyInfoMap = new HashMap<>(16);
        List<Object> itemDataList = getObjects(businessId, companyInfo, user, companyInfoMap);
        //近三年三大报表附件scc_npm_finance_report
        List<Record> bbfjList = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_FINANCE_REPORT).eq("companyId", businessId), Record.class);
        for (Record e : bbfjList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_JSMSDBBFJ");
            map.put("FJMC", e.getString("year"));
            map.put("SCR", e.getString("remark"));
            map.put("SCSJ", dealFileList(e.getLong("fileId")));
            itemDataList.add(map);
        }
        //公司规模scc_sup_company_size
        List<Record> gsgmList = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_COMPANY_SIZE)
                .eq("companyId", businessId), Record.class);
        gsgmList.forEach(e -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_GSGM");
            //总人数
            map.put("ZRS", e.getString("employee"));
            //社保人数
            map.put("SBRS", e.getString(""));
            //管理人数
            map.put("GLRY", e.getString("manager"));
            //研发人数
            map.put("YFRY", e.getString(""));
            //生产人员
            map.put("SCRY", e.getString("production"));
            //本科学历及以上
            map.put("BKXLJYS", e.getString(""));
            itemDataList.add(map);
        });
        //服务范围scc_npm_sercice_custom
        List<Record> fwfwList = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_SERCICE_CUSTOM).eq("companyId", businessId), Record.class);
        for (Record e : fwfwList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_FWFWZB");
            map.put("YJE", e.getString("performanceAmount"));
            map.put("ZYKH", e.getString("mainCustom"));
            map.put("XYYJ", dealFileList(e.getLong("fileId")));
            itemDataList.add(map);
        }
        //联系人信息
        List<ContactInfoDto> lxrxxList = qlService.queryByWrapper(QlWrappers.query(MqlType.CONTACTINFO).
                eq(ContactInfoDto::getCompanyId, businessId), ContactInfoDto.class);
        for (ContactInfoDto e : lxrxxList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_LXRXX");
            map.put("XM", e.getContactName());
            map.put("XB", e.getCeeaGender());
            map.put("BM", e.getCeeaDeptName());
            map.put("ZW", e.getPosition());
            map.put("LXFS", e.getCeeaContactMethod());
            map.put("YX", e.getEmail());
            map.put("MRLXR", e.getCeeaDefaultContact());
            map.put("SBZM", dealFileList(e.getSocialSecurityCertificateFileId()));
            map.put("BZ", e.getCeeaComments());
            itemDataList.add(map);
        }
        //引入品类和组织
        List<Record> yrplhzzList = qlService.queryByWrapper(QlWrappers.query(MqlType.ORGCATEGORY).eq("companyId", businessId), Record.class);
        yrplhzzList.forEach(e -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_YRPLHZZZB");
            map.put("YRPL", e.getString("categoryName"));
            map.put("YRZZ", e.getString("orgName"));
            itemDataList.add(map);
        });
        //认证信息
        try {
            Record record = qlService.readByKey(MqlType.SUPPLIER, businessId, Record.class);
            if (record != null && record.get(SUNSHINE_FILE_ID) != null) {
                String[] sunFileIds = record.get(SUNSHINE_FILE_ID).toString().split(",");
                for (String e : sunFileIds) {
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("__TABLE", "BO_EU_GYSLSTDGSRZXX");
                    map.put("YGXY", dealFileList(Long.valueOf(e)));
                    itemDataList.add(map);
                }
            }
        } catch (Exception e) {
            log.info("没有阳光协议附件信息");
        }
        String processTitle = "供应商绿色通道(公司)";
        String mainTable = "BO_EU_GYSLSTDGS";
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
        tableList.add("BO_EU_DZXX");
        tableList.add("BO_EU_JSMSDBBFJ");
        tableList.add("BO_EU_GSGM");
        tableList.add("BO_EU_FWFWZB");
        tableList.add("BO_EU_LXRXX");
        tableList.add("BO_EU_YRPLHZZZB");

        ArrayList<String> mainFile = Lists.newArrayList("YYZZFJSC");


        Map<String,Object> itemFile = new HashMap<>(16);
        itemFile.put("BO_EU_GYSLSTDGS", BpmResult.getFileField("YYZZFJSC"));
        if (CollectionUtils.isNotEmpty(bbfjList)) {
            itemFile.put("BO_EU_JSMSDBBFJ", BpmResult.getFileField("SCSJ"));
        }
        itemFile.put("BO_EU_FWFWZB", BpmResult.getFileField("XYYJ"));
        itemFile.put("BO_EU_LXRXX", BpmResult.getFileField("SBZM"));
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, companyInfoMap, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile, mainFile);
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    /**
     * 组装数据
     * @param businessId 参数
     * @param companyInfo 参数
     * @param user 参数
     * @param companyInfoMap 参数
     * @return 返回
     * @throws Exception 报错
     */
    @NotNull
    private List<Object> getObjects(Long businessId, CompanyInfo companyInfo, User user, Map<String, Object> companyInfoMap) throws Exception {
        companyInfoMap.put("YHM", user.getUsername());
        companyInfoMap.put("XM", user.getNickname());
        companyInfoMap.put("YX", user.getEmail());
        companyInfoMap.put("LXDH", user.getPhone());
        companyInfoMap.put("GW", user.getCeeaJobcodeDescr());
        companyInfoMap.put("LSTD", companyInfo.getGreenChannelReason());
        companyInfoMap.put("JNWGX", companyInfo.getOverseasRelation());
        companyInfoMap.put("QYXZ", companyInfo.getCompanyType());
        companyInfoMap.put("GYSLX", companyInfo.getSupplierType());
        //营业执照
        companyInfoMap.put("YYZZFJSC", dealFileList(Long.valueOf(companyInfo.getBusinessLicenseFileId())));
        companyInfoMap.put("QYMC", companyInfo.getCompanyName());
        companyInfoMap.put("FDDBR", companyInfo.getLegalPerson());
        companyInfoMap.put("TISHXYDM", companyInfo.getLcCode());
        companyInfoMap.put("ZCZB", companyInfo.getRegisteredCapital());
        companyInfoMap.put("BZ", companyInfo.getRegistCurrency());
        companyInfoMap.put("CLSJ", companyInfo.getCompanyCreationDate());
        companyInfoMap.put("YYQX", companyInfo.getBusinessEndDate());
        companyInfoMap.put("QYJCX", companyInfo.getCompanyShortName());
        companyInfoMap.put("QYYMWMC", companyInfo.getCompanyEnName());
        companyInfoMap.put("YYFW", companyInfo.getBusinessScope());
        companyInfoMap.put("SYMS", companyInfo.getCeeaBusinessModel());
        companyInfoMap.put("SFSS", companyInfo.getCeeaIfListed());
        companyInfoMap.put("SSSJ", companyInfo.getCeeaListedTime());
        companyInfoMap.put("GJDQ", companyInfo.getCompanyCountry());
        companyInfoMap.put("S", companyInfo.getCompanyProvince());
        companyInfoMap.put("CS", companyInfo.getCompanyCity());
        companyInfoMap.put("QYBGXXDZ", companyInfo.getCompanyAddress());
        companyInfoMap.put("SFYJTGS", companyInfo.getCeeaHasParentCompany());
        List<Object> itemDataList = new ArrayList<>();
        //地址信息
        List<SiteInfo> addressList = qlService.queryByWrapper(QlWrappers.query(MqlType.SITEINFO).
                eq(SiteInfo::getCompanyId, businessId), SiteInfo.class);
        addressList.forEach(e -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_DZXX");
            map.put("GJDQ", e.getCountry());
            map.put("SF", e.getProvince());
            map.put("CS", e.getCity());
            map.put("XXDZ", e.getAddressDetail());
            map.put("YZBM", e.getPostCode());
            map.put("DZBZ", e.getSiteComment());
            map.put("QY", e.getEnabledFlag());
            itemDataList.add(map);
        });
        return itemDataList;
    }

    public List<Map<String, Object>> dealFileList(Long fileId) throws Exception {
        List<Long> ll = new ArrayList<>();
        ll.add(fileId);
        List<Fileupload> list = fileCenterClient.find(ll);
        List<Map<String, Object>> fileList = new ArrayList<>();
        list.forEach(e -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("FILE_PATH_BYMOBILE", "");
            map.put("FILE_NAME", e.getFileSourceName());
            map.put("FILE_PATH", String.format(addressPath + "fileSourceName=%s&fileuploadId=%s", e.getFileSourceName(), e.getFileuploadId()));
            fileList.add(map);
        });
        return fileList;
    }

}
