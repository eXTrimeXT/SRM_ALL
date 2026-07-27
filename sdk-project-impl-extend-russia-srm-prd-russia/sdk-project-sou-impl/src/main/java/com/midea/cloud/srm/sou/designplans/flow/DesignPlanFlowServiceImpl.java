package com.midea.cloud.srm.sou.designplans.flow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.designplans.entity.*;
import com.midea.cloud.srm.model.sou.designplans.enums.DesignPlanEnums;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sou.constants.NumConstant;
import com.midea.cloud.srm.sou.designplans.mapper.*;
import com.midea.cloud.srm.sou.designplans.service.DesignPlanService;
import com.midea.cloud.srm.sou.meiql.inspect.enums.InspectStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ex_liuxy46
 */
@Slf4j
@Service
public class DesignPlanFlowServiceImpl implements IFlowBusinessCallbackService {
    @Resource
    private DesignPlanService designPlanService;

    @Resource
    private PjSouClient pjSouClient;

    @Resource
    private BaseClient baseClient;

    @Resource
    private DemandWorkMapper demandWorkMapper;

    @Resource
    private DemandAnalysisMapper demandAnalysisMapper;

    @Resource
    private DemandStrategyMapper demandStrategyMapper;

    @Resource
    private DemandOtherMapper demandOtherMapper;

    @Resource
    private DemandSettingMapper demandSettingMapper;

    @Value("${bpm.tbchfa.processGroupId}")
    private String processGroupId;
    @Value("${bpm.tbchfa.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;
    @Resource
    private BaseExtClient baseExtClient;
    @Resource
    private PjProjectExtClient pjProjectExtClient;
    private static final String BUSINESS_TYPE = "designPlan";
    @Resource
    private RedisUtil redisUtil;


    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                this.updateStatus(businessId, DesignPlanEnums.APPROVING.getCode());

                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                SccSouChDesignPlan souChDesignPlan = new SccSouChDesignPlan();
                souChDesignPlan.setDesignId(businessId);
                souChDesignPlan.setStartBpmUsername(loginAppUser.getUsername());
                souChDesignPlan.setStartBpmNickname(loginAppUser.getNickname());
                designPlanService.updateById(souChDesignPlan);

                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            this.updateStatus(businessId, DesignPlanEnums.APPROVING.getCode());
        }
    }

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        this.updateStatus(businessId, DesignPlanEnums.APPROVED.getCode());
        //发起询价
    }

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        this.updateStatus(businessId, DesignPlanEnums.REJECTED.getCode());
    }

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        this.updateStatus(businessId, DesignPlanEnums.WITHDRAW.getCode());
    }

    /**
     * @param businessId
     * @param param
     * @throws Exception
     */
    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
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
     * 封装 根据类别启动流程接口参数
     * @param businessId
     * @return
     */
    public String getDataPushFlow(Long businessId){
        SccSouChDesignPlan designPlan = designPlanService.getById(businessId);

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+designPlan.getProjectCode()+"-"+AppUserUtil.getLoginAppUser().getNickname());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(new JSONObject());
        return JSONObject.toJSONString(bpmParam);
    }

    /**
     * @param businessId
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("提报策划方案getDataPushFlow: {}, {}", businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&& com.alibaba.cloud.commons.lang.StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null|| com.alibaba.cloud.commons.lang.StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        SccSouChDesignPlan designPlan = designPlanService.getById(businessId);
        Map<String, Object> mainInfoMap = new HashMap<>(16);
        //项目编号
        mainInfoMap.put("XMBH", designPlan.getProjectCode());
        //项目名称
        mainInfoMap.put("XMMC", designPlan.getProjectName());
        //轮数
        mainInfoMap.put("LS", designPlan.getNum());
        //项目金额（万元）
        mainInfoMap.put("XMJE", designPlan.getProjMoney());
        //供货区域
        mainInfoMap.put("GHQY", getDictName("REGION", designPlan.getArea()));
        //创建人
        mainInfoMap.put("CJR", designPlan.getCreatedFullName());
        //项目介绍
        mainInfoMap.put("XMJS", designPlan.getProjIntroduce());
        //定价思路
        mainInfoMap.put("DJSL", designPlan.getPricingIdeas());
        List<Object> itemDataList = new ArrayList<>();
        //工作日程
        LambdaQueryWrapper<SccSouChDemandWork> qwDemandWork = new LambdaQueryWrapper<>();
        qwDemandWork.eq(SccSouChDemandWork::getDesignId, businessId);
        List<SccSouChDemandWork> demandWorkList = demandWorkMapper.selectList(qwDemandWork);
        for (SccSouChDemandWork e : demandWorkList) {
            Map<String, Object> map = new HashMap<>(NumConstant.SIXTEEN);
            map.put("__TABLE", "BO_EU_GZRC");
            map.put("SJTJ", e.getDataSta());
            map.put("XQFX", e.getReqSta());
            map.put("GFZYKF", e.getSupDev());
            map.put("CHFABX", e.getPlanWrite());
            map.put("XBJHJ", e.getInqPro());
            map.put("DCSQ", e.getFacApl());
            map.put("FAQP", e.getProSign());
            map.put("HTQS", e.getConSign());
            itemDataList.add(map);
        }
        LambdaQueryWrapper<SccSouChDemandAnalysis> qwDemandAnalysis = new LambdaQueryWrapper<>();
        qwDemandAnalysis.eq(SccSouChDemandAnalysis::getDesignId, businessId);
        List<SccSouChDemandAnalysis> demandAnalysisList = demandAnalysisMapper.selectList(qwDemandAnalysis);
        int one = 1;
        int two = 2;
        int three = 3;
        //按使用单位金额分析
        List<SccSouChDemandAnalysis> dwAnalysisList = demandAnalysisList.stream().filter(e -> e.getType() == one).collect(Collectors.toList());
        for (SccSouChDemandAnalysis e : dwAnalysisList) {
            Map<String, Object> map = new HashMap<>(NumConstant.SIXTEEN);
            map.put("__TABLE", "BO_EU_ASYDWJEFX");
            map.put("SQDW", e.getOrganizationName());
            map.put("JE", e.getMatMoney());
            map.put("ZB", e.getMatRate());
            itemDataList.add(map);
        }
        //按供方采购金额分析
        List<SccSouChDemandAnalysis> cgAnalysisList = demandAnalysisList.stream().filter(e -> e.getType() == two).collect(Collectors.toList());
        for (SccSouChDemandAnalysis e : cgAnalysisList) {
            Map<String, Object> map = new HashMap<>(NumConstant.SIXTEEN);
            map.put("__TABLE", "BO_EU_AGFCGJEFX");
            map.put("GYSMC", e.getVendorName());
            map.put("JE2", e.getMatMoney());
            map.put("ZB2", e.getMatRate());
            itemDataList.add(map);
        }
        //按物资品类分析
        List<SccSouChDemandAnalysis> wzAnalysisList = demandAnalysisList.stream().filter(e -> e.getType() == three).collect(Collectors.toList());
        for (SccSouChDemandAnalysis e : wzAnalysisList) {
            Map<String, Object> map = new HashMap<>(NumConstant.SIXTEEN);
            map.put("__TABLE", "BO_EU_AWZPLFX");
            map.put("WZMC", e.getMaterialName());
            map.put("JE3", e.getMatMoney());
            map.put("ZB3", e.getMatRate());
            itemDataList.add(map);
        }
        //汽柴油
        LambdaQueryWrapper<SccSouChDemandStrategy> qwDemandStrategy = new LambdaQueryWrapper<>();
        qwDemandStrategy.eq(SccSouChDemandStrategy::getDesignId, businessId);
        List<SccSouChDemandStrategy> demandStrategyList = demandStrategyMapper.selectList(qwDemandStrategy);
        for (SccSouChDemandStrategy e : demandStrategyList) {
            Map<String, Object> map = new HashMap<>(NumConstant.SIXTEEN);
            map.put("__TABLE", "BO_EU_QCY");
            map.put("ZBQY", e.getBidArea());
            map.put("YPXH", e.getYpType());
            map.put("CGL", e.getBuyNum());
            map.put("CGE", e.getBuyMoney());
            map.put("XXZC", e.getCurPol());
            map.put("NXJGF", e.getSelSup());
            map.put("ZBCL", e.getBidStr());
            map.put("MBSD", e.getTarSet());
            itemDataList.add(map);
        }
        //其他
        LambdaQueryWrapper<SccSouChDemandOther> qwDemandOther = new LambdaQueryWrapper<>();
        qwDemandOther.eq(SccSouChDemandOther::getDesignId, businessId);
        List<SccSouChDemandOther> demandOtherList = demandOtherMapper.selectList(qwDemandOther);
        for (SccSouChDemandOther e : demandOtherList) {
            Map<String, Object> map = new HashMap<>(NumConstant.SIXTEEN);
            map.put("__TABLE", "BO_EU_QTMXB");
            map.put("ZBQY2", e.getBidArea());
            map.put("GYS", e.getSup());
            map.put("NCGE", e.getProMon());
            map.put("JEZB", e.getAmoPro());
            map.put("XXZC2", e.getCurPol());
            map.put("NXJGF2", e.getSelSup());
            map.put("ZBCL2", e.getBidStr());
            map.put("MBSD2", e.getTarSet());
            itemDataList.add(map);
        }
        //招标供方履历
        LambdaQueryWrapper<SccSouChDemandSetting> qwDemandSetting = new LambdaQueryWrapper<>();
        qwDemandSetting.eq(SccSouChDemandSetting::getDesignId, businessId);
        List<SccSouChDemandSetting> demandSettingList = demandSettingMapper.selectList(qwDemandSetting);
        for (SccSouChDemandSetting e : demandSettingList) {
            Map<String, Object> map = new HashMap<>(NumConstant.SIXTEEN);
            map.put("__TABLE", "BO_EU_ZBGFLL");
            map.put("GFMC", e.getSupName());
            map.put("JYXZ", e.getNature());
            map.put("PL", e.getCategoryName());
            map.put("HYKH", e.getCustomers());
            map.put("SFXYJ", BpmResult.dealYesOrNo(e.getIsNew()));
            map.put("BZ", e.getRemark());
            itemDataList.add(map);
        }
        String processTitle = "策划方案申请-" + designPlan.getProjectName();
        String mainTable = "BO_EU_CHFASQ";
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
        //工作日程
        tableList.add("BO_EU_GZRC");
        //按使用单位金额分析
        tableList.add("BO_EU_ASYDWJEFX");
        //按供方采购金额分析
        tableList.add("BO_EU_AGFCGJEFX");
        //按物资品类分析
        tableList.add("BO_EU_AWZPLFX");
        //汽柴油
        tableList.add("BO_EU_QCY");
        //其他
        tableList.add("BO_EU_QT");
        //招标供方履历
        tableList.add("BO_EU_ZBGFLL");
        Map<String, Object> itemFile = new HashMap<>(NumConstant.TWO);
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

    private void updateStatus(Long businessId, String status) {
        SccSouChDesignPlan souChDesignPlan = new SccSouChDesignPlan();
        souChDesignPlan.setDesignId(businessId);
        souChDesignPlan.setStatus(status);
        designPlanService.updateById(souChDesignPlan);
    }
}
