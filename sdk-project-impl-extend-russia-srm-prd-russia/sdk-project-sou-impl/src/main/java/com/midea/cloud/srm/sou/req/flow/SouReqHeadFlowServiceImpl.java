package com.midea.cloud.srm.sou.req.flow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.DateUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.file.upload.dto.FileuploadDTO;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.hrorganization.SccPjOrganization;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.req.SouReqHead;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.req.enums.SouReqHeadStatusEnum;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * <pre>
 *  寻源需求审批流
 * </pre>
 *
 * @author xiaym13@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2023/10/16 16:10
 *  修改内容:
 * </pre>
 */
@Slf4j
@Service
public class SouReqHeadFlowServiceImpl implements IFlowBusinessCallbackService {

    @Resource(name = "souReqHeadFlowServiceImpl")
    private IFlowBusinessCallbackService souReqHeadFlowServiceImpl;

    @Autowired
    protected QlService qlService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Resource
    private PjSouClient pjSouClient;

    @Value("${bpm.xqxy.processGroupId}")
    private String processGroupId;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        this.updateStatus(businessId, param, SouReqHeadStatusEnum.APPROVING.toString());
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        this.updateStatus(businessId, param, SouReqHeadStatusEnum.APPROVED.toString());
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        this.updateStatus(businessId, param, SouReqHeadStatusEnum.REJECTED.toString());
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        this.updateStatus(businessId, param, SouReqHeadStatusEnum.WITHDRAW.toString());
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {

    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("---------getDataPushFlow-----------businessId:{}---param:{}" ,businessId, param);
        SouReqHead souReqHead = qlService.readByKey(MqlType.SOU_REQ_HEAD_BUYER, businessId, SouReqHead.class);
        Map<String, Object> mainInfoMap = new HashMap<>(50);
        //板块
        mainInfoMap.put("BK", souReqHead.getOrgBuName());
        //公司
        mainInfoMap.put("GS", souReqHead.getOrgName());
        //寻源公示模板
        mainInfoMap.put("XYGSMB", souReqHead.getPubconfigName());
        //需求部门
        mainInfoMap.put("XQBM", souReqHead.getReqDepartment());
        //需求人
        mainInfoMap.put("XQR", souReqHead.getReqUserName());
        //供应商负责人
        mainInfoMap.put("GYSFZR", souReqHead.getResponsibilityUserName());
        //招标负责人
        mainInfoMap.put("ZBFR", souReqHead.getSouPersonUserName());
        //是否前置交流
        mainInfoMap.put("SFQZJL", "Y".equals(souReqHead.getIsPreComm()) ? "是" : "否");
        //单据状态
        mainInfoMap.put("DJZT", getStatusZhName(souReqHead.getStatus()));
        //创建人
        mainInfoMap.put("CJR", souReqHead.getCreatedFullName());
        //创建日期
        mainInfoMap.put("CJRQ", BpmResult.sdfDate(souReqHead.getCreationDate()));
        //最后更新日期
        mainInfoMap.put("ZHGXRQ", BpmResult.sdfDate(souReqHead.getLastUpdateDate()));
        //寻源编号
        mainInfoMap.put("XYBH", souReqHead.getReqHeadNo());
        //项目名称
        mainInfoMap.put("XMMC", souReqHead.getProjectName());
        //公示截止时间
        mainInfoMap.put("GSJZSJ", DateUtil.format(souReqHead.getPublicEndTime(), DateUtil.DATE_FORMAT_19));
        //预算（万元）
        mainInfoMap.put("YS", String.valueOf(souReqHead.getTotalAmountByTenKilo()));
        //品类
        mainInfoMap.put("PL", souReqHead.getCategoryName());
        //规模数量
        mainInfoMap.put("GM", souReqHead.getRequireQuantity());
        //申请单号
        mainInfoMap.put("SQDH", StringUtils.isNotBlank(souReqHead.getRequirementHeadNoList())?souReqHead.getRequirementHeadNoList():souReqHead.getRequirementHeadNo());
        //需求来源
        mainInfoMap.put("XQLY", getSourceFromZhName(souReqHead.getRequireFrom()));
        //项目概括与招标范围
        mainInfoMap.put("XMGK", souReqHead.getProjectScope());
        //供应商资质要求
        mainInfoMap.put("GYSZZ", souReqHead.getVendorQualReq());
        //技术要求
        mainInfoMap.put("JSYQ", souReqHead.getTechnicalReq());
        //业绩要求
        mainInfoMap.put("YJYQ", souReqHead.getPerformanceReq());
        //项目所在地
        mainInfoMap.put("XMSZD", souReqHead.getProjectAddress());
        //报名联系人
        mainInfoMap.put("BMLXR", souReqHead.getContactName());
        //报名联系电话
        mainInfoMap.put("BMLXDH", souReqHead.getPhone());
        //办公电话
        mainInfoMap.put("BBDH", souReqHead.getOfficePhone());
        //需缴纳意向金
        mainInfoMap.put("XJNYXJ", "Y".equals(souReqHead.getIsNeedDeposit()) ? "是" : "否");
        //意向缴纳金额（元）
        mainInfoMap.put("YXJJE", String.valueOf(souReqHead.getDepositAmount()));
        //开户银行
        mainInfoMap.put("KHYH", souReqHead.getBankName());
        //银行联行号
        mainInfoMap.put("YHLHH", souReqHead.getBankNumber());
        //开户账号
        mainInfoMap.put("KHZH", souReqHead.getBankAccount());
        //开户户名
        mainInfoMap.put("KHHM", souReqHead.getBankAccountName());
        List<Object> itemDataList = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        ids.add(businessId);
        List<SceneFile> fileNameList = baseClient.listSceneFileBatch(ids);
        for (SceneFile e : fileNameList) {
            Map<String, Object> map = new HashMap<>(50);
            map.put("__TABLE", "BO_EU_XYXQMXB");
            if (e.getFileuploadId() != null) {
                map.put("FJMC", BpmResult.getFileList(addressPath,e.getFileName(), e.getFileuploadId()));
                map.put("FJMBZC", e.getRemark());
            } else {
                map.put("FJMC", null);
                map.put("FJMBZC", null);
            }
            itemDataList.add(map);
        }
        String processTitle = "寻源公示-"+souReqHead.getProjectName();
        String mainTable = "BO_EU_XYXQ2";
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();
        String createOrgId = null;
        SccPjUser sccPjUser = pjSouClient.getSccUserByPersonnelNo(createUser);
        if (sccPjUser != null && sccPjUser.getGroupId() != null) {
            createOrgId = String.valueOf(sccPjUser.getGroupId());
        }
        if (StringUtils.isBlank(createOrgId)) {
            throw new BaseException("查询不到hr组织id");
        }
        List<String> tableList = new ArrayList<>();
        tableList.add("BO_EU_XYXQMXB");
        Map<String,Object> itemFile = new HashMap<>(50);
        itemFile.put("BO_EU_XYXQMXB", BpmResult.getFileField("FJMC"));
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, mainInfoMap, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile);
        Map<String, String> pageUrlMap = new HashMap<>(50);
        pageUrlMap.put("formUrl", getViewSrmRollBackUrl("sourcingRequireBuyer",businessId ,"寻源需求单据详情"));
        dataPushFlowJsn.put("PROCESSVARS", pageUrlMap);
        log.info(JSON.toJSONString(dataPushFlowJsn));
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    public String getViewSrmRollBackUrl( String funName,Long formId,String formNo){
        String url =  pjSouClient.getViewSrmRollBackUrl(funName,formId,formNo);
        return url;
    }

    public static String getStatusZhName(String str) {
        Map<String, String> map = new HashMap<>(50);
        map.put("DRAFT", "拟定");
        map.put("APPROVING", "审批中");
        map.put("APPROVED", "接受报名中");
        map.put("REJECTED", "已驳回");
        map.put("WITHDRAW", "已撤回");
        map.put("SIGNUP_DONE", "报名截止");
        map.put("CLOSED", "关闭");
        return map.get(str);
    }

    public static String getSourceFromZhName(String str) {
        Map<String, String> map = new HashMap<>(50);
        map.put("YEAR", "年度");
        map.put("MONTH", "月度");
        map.put("WITHOUT_PLAN", "计划外");
        map.put("SPECIAL_SOU", "特殊招标");
        return map.get(str);
    }

    private void updateStatus(Long businessId, String param, String status) {
        qlService.updateByWrapper(QlWrappers.update(MqlType.SOU_REQ_HEAD_BUYER)
                .set(status.equals(SouReqHeadStatusEnum.APPROVED.toString()), SouReqHead::getReleaseDate, new Date())
                .set(SouReqHead::getStatus, status)
                .eq(SouReqHead::getReqHeadId, businessId));
    }
}
