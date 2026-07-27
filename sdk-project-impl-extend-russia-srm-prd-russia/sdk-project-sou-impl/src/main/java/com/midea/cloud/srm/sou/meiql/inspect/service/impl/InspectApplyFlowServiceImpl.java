package com.midea.cloud.srm.sou.meiql.inspect.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sou.meiql.inspect.dto.Inspect;
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
public class InspectApplyFlowServiceImpl implements IFlowBusinessCallbackService {


    @Autowired
    private QlService qlService;
    @Autowired
    private BaseClient baseClient;


    @Value("${bpm.KCSQ.processGroupId}")
    private String processGroupId;
    @Value("${bpm.KCSQ.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Resource
    private BaseExtClient baseExtClient;
    private static final String BUSINESS_TYPE = "INSPECT_APPLY";
    @Resource
    private RedisUtil redisUtil;

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                updateSatus(businessId, InspectStatusEnum.APPLY_APPROVING);

                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                Record r = new Record();
                r.put(Inspect::getInspectId, businessId);
                r.put(Inspect::getStartBpmUsername, loginAppUser.getUsername());
                r.put(Inspect::getStartBpmNickname, loginAppUser.getNickname());
                qlService.update("Inspect", Arrays.asList(r));

                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            updateSatus(businessId, InspectStatusEnum.APPLY_APPROVING);
        }
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, InspectStatusEnum.APPLY_APPROVED);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, InspectStatusEnum.APPLY_REJECTED);
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, InspectStatusEnum.APPLY_WITHDRAW);
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
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
            &&(bpmNewFlag==null||StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }

        /**
         * 查询 考察申请 -   businessid  就是 inspectId
         * 考察申请表             scc_npm_inspect  根据 inspectId 查询
         * 考察单位      scc_npm_inspect_vendor   根据 inspectId 查询
         */
        log.info("===================进入考察申请装数据方法开始"+businessId);
        Inspect inspect = qlService.readByKey("Inspect",businessId, Inspect.class);

        List<InspectVendor> inspectVendorList = qlService.queryByWrapper(QlWrappers.query(InspectVendor.class).
                eq(InspectVendor::getInspectId, businessId), InspectVendor.class);
        // 项目名称
        String xmmc = inspect.getBidingName();
        // 【】招标单号 是否对应 招标项目编码
        String zbdh = inspect.getBidingNum();
        // 招标负责人
        String zbfzr = inspect.getBidingHead();
        // 出行方式
        String cxfs = inspect.getComeType();
        // 公司名称
        String gsmc = inspect.getOrgName();
        // 申请部门
        String sqbm = inspect.getDepartmentName();
        // 申请单据号
        String sqdjh = inspect.getInspectNum().toString();
        // 招标部是否参加
        String zbbsfcj = inspect.getBidingDepartmentFlag();
        // 拟参加人员
        String ncjry = inspect.getComment();

        String processtitle = "考察申请";
        String maintable = "BO_EU_KCSQ";

        Map<String, Object> mainTableData = new HashMap<>(50);
        mainTableData.put("XMMC",xmmc);
        mainTableData.put("ZBDH",zbdh);
        mainTableData.put("ZBFZR",zbfzr);
        mainTableData.put("CXFS",cxfs);
        mainTableData.put("GSMC",gsmc);
        mainTableData.put("SQBM",sqbm);
        mainTableData.put("SQDJH",sqdjh);
        mainTableData.put("ZBBSFCJ",zbbsfcj);
        mainTableData.put("NCJRY",ncjry);

        String processgroupid = processGroupId;
        String appid = appId;

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

        List<String> itemtable = new ArrayList<>();
        itemtable.add("BO_EU_KCDWZB");

        List<Object> itemdata = new ArrayList<>();
        for(int i = 0 ; i < inspectVendorList.size(); i++){
            Map<String, Object> map = new HashMap<>(50);
            //供应商名称
            map.put("GYSMC",inspectVendorList.get(i).getVendorName());
            // 地点
            map.put("DD",inspectVendorList.get(i).getInspectAddress());
            // 考察原因
            map.put("KCYY",inspectVendorList.get(i).getInspectCause());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if(inspectVendorList.get(i).getInspectTime() != null){
                // 考察时间
                map.put("KCSJ",simpleDateFormat.format(inspectVendorList.get(i).getInspectTime()));
            }
            // 考察内容
            map.put("KCNR",inspectVendorList.get(i).getInspectContent());

            map.put("__TABLE", "BO_EU_KCDWZB");
            itemdata.add(map);
        }


        Map<String,Object> itemFile = new HashMap<>(50);

        JSONObject dataPushFlowJsn ;
        dataPushFlowJsn = BpmResult.generateBpmJson(processtitle, maintable, mainTableData, processgroupid, appid,
                createOrgId, createUser, itemtable, itemdata, itemFile);
        log.info("===================进入考察申请组装数据方法结束"+dataPushFlowJsn.toString());
        log.info("===========考察申请JSON=============="+dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);


    }

    private void updateSatus(Long id, InspectStatusEnum status){
        Record recruit = qlService.readByKey("Inspect",id,Record.class);
        Assert.notNull(recruit, "单据ID不存在");

//        if (InspectStatusEnum.APPLY_APPROVING == status && !InspectStatusEnum.DRAFT.getCode().equals(recruit.get(Inspect::getStatus))) {
//            throw new BaseException("当前状态不能提交审批");
//        }
//        if (InspectStatusEnum.ABANDON == status && InspectStatusEnum.REPORT_APPROVED.getCode().equals(recruit.get(Inspect::getStatus))) {
//            throw new BaseException("当前状态不能操作废弃");
//        }

        Record r = new Record();
        r.put(Inspect::getInspectId, id);
        r.put(Inspect::getInspectStatus, status.getCode());
        qlService.update("Inspect", Arrays.asList(r));
    }
}
