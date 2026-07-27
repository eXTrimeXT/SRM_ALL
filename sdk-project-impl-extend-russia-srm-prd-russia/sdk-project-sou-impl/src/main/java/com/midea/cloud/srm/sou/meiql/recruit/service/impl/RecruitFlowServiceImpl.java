package com.midea.cloud.srm.sou.meiql.recruit.service.impl;

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
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sou.meiql.recruit.dto.Recruit;
import com.midea.cloud.srm.sou.meiql.recruit.dto.RecruitContent;
import com.midea.cloud.srm.sou.meiql.recruit.enums.RecruitStatusEnum;
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
public class RecruitFlowServiceImpl implements IFlowBusinessCallbackService {

    @Value("${bpm.ZMSP.processGroupId}")
    private String processGroupId;
    @Value("${bpm.ZMSP.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Autowired
    private QlService qlService;

    @Resource
    private PjProjectExtClient pjProjectExtClient;
    @Resource
    private BaseExtClient baseExtClient;
    private static final String BUSINESS_TYPE = "RECRUIT";
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                updateSatus(businessId, RecruitStatusEnum.APPROVING);
                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();

                Record r = new Record();
                r.put(Recruit::getRecruitId, businessId);
                r.put(Recruit::getStartBpmUsername, loginAppUser.getUsername());
                r.put(Recruit::getStartBpmNickname, loginAppUser.getNickname());
                qlService.update("Recruit", Arrays.asList(r));

                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            updateSatus(businessId, RecruitStatusEnum.APPROVING);
        }
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, RecruitStatusEnum.APPROVED);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, RecruitStatusEnum.REJECTED);
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, RecruitStatusEnum.WITHDRAW);
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, RecruitStatusEnum.ABANDON);
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
        Recruit recruit = qlService.readByKey("Recruit",businessId, Recruit.class);

        JSONObject processVars = new JSONObject();
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+recruit.getName());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        /**
         * 查询 招募审批 -   businessid  就是 RECRUIT_ID
         * 招募审批表             scc_npm_recruit  根据 RECRUIT_ID 查询
         * 招募内容表      scc_npm_recruit_content   根据 RECRUIT_ID 查询
         */
        log.info("===================进入招募审批装数据方法开始"+businessId);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null|| com.alibaba.cloud.commons.lang.StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        Recruit recruit = qlService.readByKey("Recruit",businessId, Recruit.class);

        List<RecruitContent> recruitContentList = qlService.queryByWrapper(QlWrappers.query(RecruitContent.class).
                eq(RecruitContent::getRecruitId, businessId), RecruitContent.class);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        // 招募民初
        String zmmc = recruit.getName();
        // 标题
        String fbt = recruit.getTitle();
        // 截止时间
        String jzsj = null;
        //创建时间
        String cjrq = null ;
        if(recruit.getDeadlineTime() != null){
            jzsj = simpleDateFormat.format(recruit.getDeadlineTime());
        }

        if(recruit.getCreationDate() != null){
            cjrq = simpleDateFormat.format(recruit.getDeadlineTime());
        }
// 品类名称
        String pl = recruit.getCategoryName();
        // 状态
        String zt = getStatus(recruit.getStatus());
        // 创建人
        String cjr = recruit.getCreatedFullName();
        // 发布时间
        String fbrq = (recruit.getPublishTime() == null) ? null : recruit.getPublishTime().toString();
        String zmnr = "";
        if(recruitContentList.size()>0){
            zmnr = recruitContentList.get(0).getContent();
        }

        String processtitle = "招募审批";
        String maintable = "BO_EU_ZMSP";

        Map<String, Object> mainTableData = new HashMap<>(50);
        mainTableData.put("ZMMC",zmmc);
        mainTableData.put("FBT",fbt);
        mainTableData.put("JZSJ",jzsj);
        mainTableData.put("PL",pl);
        mainTableData.put("ZT",zt);
        mainTableData.put("CJR",cjr);
        mainTableData.put("CJSJ",cjrq);
        mainTableData.put("FBRQ",fbrq);
        mainTableData.put("ZMNR",zmnr);

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

        List<Object> itemdata = new ArrayList<>();

        Map<String,Object> itemFile = new HashMap<>(50);


        JSONObject dataPushFlowJsn ;
        dataPushFlowJsn = BpmResult.generateBpmJson(processtitle, maintable, mainTableData, processgroupid, appid,
                createOrgId, createUser, itemtable, itemdata, itemFile);
        log.info("===================进入招募审批组装数据方法结束"+dataPushFlowJsn.toString());
        log.info("===========招募审批JSON=============="+dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);

    }

    private void updateSatus(Long id, RecruitStatusEnum status){
        Record recruit = qlService.readByKey("Recruit",id,Record.class);
        Assert.notNull(recruit, "单据ID不存在");

        Record r = new Record();
        r.put(Recruit::getRecruitId, id);
        r.put(Recruit::getStatus, status.getCode());
        if (RecruitStatusEnum.APPROVED == status) {
            r.put(Recruit::getPublishTime, new Date());
        }


        qlService.update("Recruit", Arrays.asList(r));
    }


    public String getStatus(String type){
        String approving = "APPROVING";
        String approved = "APPROVED";
        String draft = "DRAFT";
        String rejected = "REJECTED";
        String withdraw = "WITHDRAW";
        String abandon = "ABANDON";
        if(draft.equals(type)){
            return "拟定" ;
        }else if(approving.equals(type)){
            return "审批中" ;
        }else if(approved.equals(type)){
            return "已审批" ;
        }else if(rejected.equals(type)){
            return "已驳回" ;
        }else if(abandon.equals(type)){
            return "已废弃" ;
        }else if(withdraw.equals(type)){
            return "已撤回" ;
        }else {
            return type ;
        }
    }


}
