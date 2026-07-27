package com.midea.cloud.srm.sou.meiql.inspect.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sou.meiql.inspect.dto.Inspect;
import com.midea.cloud.srm.sou.meiql.inspect.dto.InspectAttach;
import com.midea.cloud.srm.sou.meiql.inspect.dto.InspectVendor;
import com.midea.cloud.srm.sou.meiql.inspect.enums.InspectStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 备注
 * @author huangbf3
 */
@Service
@Slf4j
public class InspectReportFlowServiceImpl implements IFlowBusinessCallbackService {


    @Autowired
    private QlService qlService;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Value("${bpm.KCBG.processGroupId}")
    private String processGroupId;
    @Value("${bpm.KCBG.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;

    @Resource
    private PjProjectExtClient pjProjectExtClient;
    @Resource
    private BaseExtClient baseExtClient;

    private static final String BUSINESS_TYPE = "INSPECT_REPORT";
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                updateSatus(businessId, InspectStatusEnum.REPORT_APPROVING);

                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                Record r = new Record();
                r.put(Inspect::getInspectId, businessId);
                r.put(Inspect::getStartBpmReportUsername, loginAppUser.getUsername());
                r.put(Inspect::getStartBpmReportNickname, loginAppUser.getNickname());
                qlService.update("Inspect", Arrays.asList(r));
                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            updateSatus(businessId, InspectStatusEnum.REPORT_APPROVING);
        }
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, InspectStatusEnum.REPORT_APPROVED);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, InspectStatusEnum.REPORT_REJECTED);
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, InspectStatusEnum.REPORT_WITHDRAW);
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, InspectStatusEnum.ABANDON);
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
        Inspect inspect = qlService.readByKey("Inspect",businessId, Inspect.class);

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+inspect.getBidingName());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(new JSONObject());
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        /**
         * 查询 考察报告 -   businessid  就是 inspectId
         * 考察报告表             scc_npm_inspect  根据 inspectId 查询
         * 考察单位      scc_npm_inspect_vendor   根据 inspectId 查询
         * 附件         scc_npm_inspect_attach
         */
        log.info("===================进入考察报告装数据方法开始"+businessId);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null||StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        Inspect inspect = qlService.readByKey("Inspect",businessId, Inspect.class);

        List<InspectVendor> inspectVendorList = qlService.queryByWrapper(QlWrappers.query(InspectVendor.class).
                eq(InspectVendor::getInspectId, businessId), InspectVendor.class);

        List<InspectAttach> inspectattachlist = qlService.queryByWrapper(QlWrappers.query(InspectAttach.class).
                eq(InspectAttach::getInspectId, businessId), InspectAttach.class);

        // 组装主表信息
        Map<String, Object> mainTableData = new HashMap<>(50);
        // 项目名称
        mainTableData.put("XMMC",inspect.getBidingName());
        // 【】招标单号 是否对应 招标项目编码
        mainTableData.put("ZBDH",inspect.getBidingNum());
        // 招标负责人
        mainTableData.put("ZBFZR",inspect.getBidingHead());
        // 出行方式
        mainTableData.put("CXFS",inspect.getComeType());
        // 公司名称
        mainTableData.put("GSMC",inspect.getOrgName());
        // 申请部门
        mainTableData.put("SQBM",inspect.getDepartmentName());
        // 招标部是否参加
        mainTableData.put("ZBBSFCJ",inspect.getBidingDepartmentFlag());
        // 申请单据号
        mainTableData.put("SQDJH",inspect.getInspectNum());
        // 考察报告单据号
        mainTableData.put("KCBGDJH",inspect.getReportNum());
        // 拟参加人员
        mainTableData.put("NCJRY",inspect.getComment());
        // 综合意见
        mainTableData.put("ZHYJ",inspect.getComprehensiveEvaluation());
        //  评标组长意见
        mainTableData.put("PBZZYJ",inspect.getLeaderEvaluation());

        // 考察单位信息
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Object> itemdata = new ArrayList<>();
        for(int i = 0 ; i < inspectVendorList.size(); i ++){
            Map<String, Object> map = new HashMap<>(50);
            //供应商名称
            map.put("GYSMC",inspectVendorList.get(i).getVendorName());
            // 地点
            map.put("DD",inspectVendorList.get(i).getInspectAddress());
            // 考察原因
            map.put("KCYY",inspectVendorList.get(i).getInspectCause());

            if(inspectVendorList.get(i).getInspectTime() != null){
                // 考察时间
                map.put("KCSJ",simpleDateFormat.format(inspectVendorList.get(i).getInspectTime()));
            }
            // 考察内容
            map.put("KCNR",inspectVendorList.get(i).getInspectContent());
            // 现场管理评价
            map.put("XCGLPJ",inspectVendorList.get(i).getManageEvaluate());
            // 主要生产设备评价
            map.put("ZYSCSBPL",inspectVendorList.get(i).getDeviceEvaluate());
            // 人员情况
            map.put("RYZK",inspectVendorList.get(i).getStaffEvaluate());
            // 业绩情况
            map.put("YJQK",inspectVendorList.get(i).getPerformanceEvaluate());
            // 综合意见
            mainTableData.put("ZHYJ",inspectVendorList.get(i).getComprehensiveEvaluation());
            // 其他方面
            map.put("QTFM",inspectVendorList.get(i).getComment());
            map.put("__TABLE", "BO_EU_KCDWPJ");
            itemdata.add(map);
        }

        // 附件信息
        if (CollectionUtils.isNotEmpty(inspectattachlist)) {
            inspectattachlist.forEach(e -> {
                Map<String, Object> map = new HashMap<>(50);
                List<Map<String, Object>> file = new ArrayList<>();
                Map<String, Object> fileMap = new HashMap<>(50);

                Fileupload fileupload = new Fileupload();
                fileupload.setFileuploadId(e.getAttachId());
                fileupload.setPageNum(1);
                fileupload.setPageSize(1);

                PageInfo<Fileupload> fileuploads = fileCenterClient.listPage(fileupload,"N");
                fileuploads.getList().forEach(m -> {
                    fileMap.put("FILE_PATH_BYMOBILE", "");
                    fileMap.put("FILE_NAME", m.getFileSourceName());
                    String mes = "fileSourceName="+m.getFileSourceName()+"&fileuploadId="+m.getFileuploadId();
                    fileMap.put("FILE_PATH", fileDownloadPath+mes);
                    file.add(fileMap);
                });

                map.put("FJMC",file);
                // 上传人
                map.put("SCR",e.getCreatedBy());
                // 上传时间
                map.put("SCSJ",e.getCreationDate());
                map.put("__TABLE", "BO_EU_KCBBFJSCZB");
                itemdata.add(map);
            });
        }

        Map<String,Object> itemFile = new HashMap<>(50);
        List<String> fList = new ArrayList<>();
        fList.add("FJMC");
        itemFile.put("BO_EU_KCBBFJSCZB", fList);

        // 其他子表
        List<String> itemtable = new ArrayList<>();
        itemtable.add("BO_EU_KCDWPJ");
        itemtable.add("BO_EU_KCBBFJSCZB");

        String processtitle = "考察报告";
        String maintable = "BO_EU_KCBG";

        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();


        String createOrgId = null;
        SccPjUser sccPjUser = pjProjectExtClient.getSccUserByPersonnelNo(createUser);
        if (sccPjUser != null && sccPjUser.getGroupId() != null) {
            createOrgId = String.valueOf(sccPjUser.getGroupId());
        }
        if (StringUtils.isBlank(createOrgId)) {
            throw new RuntimeException("查询不到hr组织id");
        }

        JSONObject dataPushFlowJsn ;
        dataPushFlowJsn = BpmResult.generateBpmJson(processtitle, maintable, mainTableData, processGroupId, appId,
                createOrgId, createUser, itemtable, itemdata, itemFile);
        log.info("===================进入考察报告组装数据方法结束"+dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    private void updateSatus(Long id, InspectStatusEnum status){
        Record recruit = qlService.readByKey("Inspect",id,Record.class);
        Assert.notNull(recruit, "单据ID不存在");

        Record r = new Record();
        r.put(Inspect::getInspectId, id);
        r.put(Inspect::getInspectStatus, status.getCode());
        qlService.update("Inspect", Arrays.asList(r));
    }
}
