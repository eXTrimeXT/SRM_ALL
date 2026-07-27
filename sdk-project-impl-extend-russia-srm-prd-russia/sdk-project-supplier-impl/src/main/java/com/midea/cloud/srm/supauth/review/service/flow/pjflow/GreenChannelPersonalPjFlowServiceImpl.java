package com.midea.cloud.srm.supauth.review.service.flow.pjflow;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.ImmutableList;
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
import com.midea.cloud.srm.model.supplier.info.entity.*;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sup.ext.pjsupplier.service.PjSupplierService;
import com.midea.cloud.srm.sup.info.service.ICompanyInfoService;
import com.midea.cloud.srm.utils.MqlType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
public class GreenChannelPersonalPjFlowServiceImpl implements IFlowBusinessCallbackService {

    @Resource(name = "greenChannelFlowServiceImpl")
    private IFlowBusinessCallbackService greenChannelFlowServiceImpl;

    @Value("${bpm.lstdgr.processGroupId}")
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
        QueryWrapper<CompanyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("COMPANY_ID", businessId);
        Map<String, Object> ciMap = companyInfoService.getMap(queryWrapper);
        User user = rbacClient.queryByCompanyId(companyInfo.getCompanyId());
        Map<String, Object> companyInfoMap = new HashMap<>(16);
        companyInfoMap.put("YHM", user.getUsername());
        companyInfoMap.put("XM", user.getNickname());
        companyInfoMap.put("YX", user.getEmail());
        companyInfoMap.put("LXDH", user.getPhone());
        companyInfoMap.put("SYLSTDYJGYSDYYXS", companyInfo.getGreenChannelReason());
        companyInfoMap.put("QYFL", "个人");
        companyInfoMap.put("QYMC", companyInfo.getCompanyName());
        companyInfoMap.put("GEJC", companyInfo.getCompanyShortName());
        //身份证正面
        Object businessLicenseFileId = ciMap.get("BUSINESS_LICENSE_FILE_ID");
        companyInfoMap.put("SCSFZZM", businessLicenseFileId);
        //身份证反面
        Object extIdCardOppositeFileId = ciMap.get("EXT_ID_CARD_OPPOSITE_FILE_ID");
        companyInfoMap.put("SCSFZFM", extIdCardOppositeFileId);
        companyInfoMap.put("SFZHM", companyInfo.getIdNumber());
        companyInfoMap.put("SFZYXQ", null);
        companyInfoMap.put("XB", ciMap.get("EXT_SEX"));
        companyInfoMap.put("ZYFW", companyInfo.getBusinessScope());
        companyInfoMap.put("GJDQ", companyInfo.getCompanyCountry());
        companyInfoMap.put("S", companyInfo.getCompanyProvince());
        companyInfoMap.put("CS", companyInfo.getCompanyCity());
        companyInfoMap.put("QYBGXXDZ", companyInfo.getCompanyAddress());
        companyInfoMap.put("SSPL", null);
        companyInfoMap.put("LXR", null);
        List<Object> itemDataList = new ArrayList<>();
        //银行
        List<BankInfo> bankInfoList = qlService.queryByWrapper(QlWrappers.query(MqlType.BANKINFO).
                eq(BankInfo::getCompanyId, businessId), BankInfo.class);
        bankInfoList.forEach(e -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_GYSLSTDGRYHZB");
            map.put("YHDM", e.getBankCode());
            map.put("YHMC", e.getBankName());
            map.put("KHHMC", e.getOpeningBank());
            map.put("FHBM", e.getUnionCode());
            map.put("ZHMC", e.getBankAccountName());
            map.put("YHZH", e.getBankAccount());
            map.put("BZ", e.getCurrencyName());
            map.put("SFZZH", e.getCeeaMainAccount());
            map.put("QY", e.getCeeaEnabled());
            itemDataList.add(map);
        });
        //服务范围scc_npm_sercice_custom
        List<Record> fwfwList = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_SERCICE_CUSTOM)
                .eq("companyId", businessId), Record.class);
        for (Record e : fwfwList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_GYSLSTDGRFWFWZB");
            map.put("YJE", e.getString("performanceAmount"));
            map.put("ZYKH", e.getString("mainCustom"));
            map.put("XYYJ", BpmResult.getFileList(addressPath, e.getString("fileName"), e.getLong("fileId")));
            itemDataList.add(map);
        }
        //联系人信息
        List<ContactInfo> lxrxxList = qlService.queryByWrapper(QlWrappers.query(MqlType.CONTACTINFO).
                eq(ContactInfo::getCompanyId, businessId), ContactInfo.class);
        lxrxxList.forEach(e -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_GYSLSTDGRLXRZB");
            map.put("XM", e.getContactName());
            map.put("B", e.getCeeaGender());
            map.put("LXFS", e.getCeeaContactMethod());
            map.put("YS", e.getEmail());
            map.put("MRLXR", e.getCeeaDefaultContact());
            map.put("BZ", e.getCeeaComments());
            itemDataList.add(map);
        });
        //引入品类和组织
        List<Record> yrplhzzList = qlService.queryByWrapper(QlWrappers.query(MqlType.ORGCATEGORY).eq("companyId", businessId), Record.class);
        yrplhzzList.forEach(e -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_GYSLSTDGRYRZB");
            map.put("YRPL", e.getString("categoryName"));
            map.put("YRZZ", e.getString("orgName"));
            itemDataList.add(map);
        });
        //认证信息
        try {
            Record record = qlService.readByKey(MqlType.SUPPLIER, businessId, Record.class);
            if (record != null && record.get(SUNSHINE_FILE_ID) != null) {
                String[] sunFileIds = record.get(SUNSHINE_FILE_ID).toString().split(",");
                String[] sunFileName = record.get("sunshineFileName").toString().split(",");
                for (int i = 0; i < sunFileIds.length; i++) {
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("__TABLE", "BO_EU_GYSLSTDGRRZXX");
                    map.put("YGXY", BpmResult.getFileList(addressPath, sunFileName[i], Long.valueOf(sunFileIds[i])));
                    itemDataList.add(map);
                }
            }
        } catch (Exception e) {
            log.info("没有阳光协议附件信息");
        }
        String processTitle = "供应商绿色通道(个人)";
        String mainTable = "BO_EU_GYSLSTDGR";
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
        tableList.add("BO_EU_GYSLSTDGRYHZB");
        tableList.add("BO_EU_GYSLSTDGRFWFWZB");
        tableList.add("BO_EU_GYSLSTDGRLXRZB");
        tableList.add("BO_EU_GYSLSTDGRYRZB");

        ArrayList<String> mainFile = Lists.newArrayList("SCSFZZM","SCSFZFM");

        Map<String,Object> itemFile = new HashMap<>(16);
        itemFile.put("BO_EU_GYSLSTDGR", BpmResult.getFileField("SCSFZZM,SCSFZFM,YGXY"));
        itemFile.put("BO_EU_GYSLSTDGRFWFWZB", BpmResult.getFileField("XYYJ"));
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, companyInfoMap, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile, mainFile);
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    public List<Map<String, Object>> dealFileList(Long fileId) throws Exception {
        FileuploadDTO fileuploadDTO = new FileuploadDTO();
        fileuploadDTO.setFileId(fileId);
        List<Fileupload> list = fileCenterClient.uploadThirdBatch(fileuploadDTO);
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
