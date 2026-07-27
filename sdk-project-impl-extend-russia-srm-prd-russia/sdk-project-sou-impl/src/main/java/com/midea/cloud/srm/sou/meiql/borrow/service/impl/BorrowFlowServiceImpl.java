package com.midea.cloud.srm.sou.meiql.borrow.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sou.meiql.borrow.dto.Borrow;
import com.midea.cloud.srm.sou.meiql.borrow.dto.BorrowAttach;
import com.midea.cloud.srm.sou.meiql.borrow.enums.BorrowStatusEnum;
import com.midea.cloud.srm.sou.meiql.inspect.enums.InspectStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Service
public class BorrowFlowServiceImpl implements IFlowBusinessCallbackService {


    @Autowired
    private QlService qlService;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Value("${bpm.jysqsp.processGroupId}")
    private String processGroupId;
    @Value("${bpm.jysqsp.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Resource
    private BaseClient baseClient;
    @Resource
    private BaseExtClient baseExtClient;

    private static final String BUSINESS_TYPE = "BORROW";
    @Autowired
    private RedisUtil redisUtil;

    private static final String NULL_STR = "null";


    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& com.alibaba.cloud.commons.lang.StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                updateSatus(businessId, BorrowStatusEnum.APPROVING);
                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();

                Record r = new Record();
                r.put(Borrow::getBorrowId, businessId);
                r.put(Borrow::getStartBpmUsername, loginAppUser.getUsername());
                r.put(Borrow::getStartBpmNickname, loginAppUser.getNickname());
                qlService.update("Borrow", Arrays.asList(r));

                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            updateSatus(businessId, BorrowStatusEnum.APPROVING);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        log.info("borrow pass flow param: {}", param);
        if (StringUtils.isNotBlank(param)) {
            cn.hutool.json.JSONObject jsonObj = JSONUtil.parseObj(param);
            String data = (String) jsonObj.get("createdBy");
            if (StringUtils.isNotBlank(data)&&!StringUtils.equals(data,NULL_STR)) {
                data = data.replace("\\", "");
                cn.hutool.json.JSONObject fileJson = JSONUtil.parseObj(data);
                List<Map<String, Object>> files = (List<Map<String, Object>>) fileJson.get("FJSC");
                if (CollectionUtils.isNotEmpty(files)) {
                    List<BorrowAttach> attaches = files.stream().map(e -> {
                        BorrowAttach attach = new BorrowAttach();
                        attach.setBorrowId(businessId);
                        attach.setAttachName((String) e.get("fileName"));
                        attach.setAttachPath((String) e.get("filePath"));
                        return attach;
                    }).collect(Collectors.toList());
                    qlService.create("BorrowAttach", attaches);
                }
            }
        }

        updateSatus(businessId, BorrowStatusEnum.APPROVED);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, BorrowStatusEnum.REJECTED);
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, BorrowStatusEnum.WITHDRAW);
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, BorrowStatusEnum.ABANDON);
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
        Record borrow = qlService.readByKey("Borrow", businessId, Record.class);
        JSONObject processVars = new JSONObject();
        processVars.put("WISO",StringUtils.equals(borrow.get(Borrow::getPriceFlag),YesOrNo.YES.getValue())?"是":"否");

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+borrow.getString("bidingName"));
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("---------getDataPushFlow-----------businessId:{}---param:{}" ,businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null|| com.alibaba.cloud.commons.lang.StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        Record borrow = qlService.readByKey("Borrow", businessId, Record.class);
        Assert.isTrue(StringUtils.isNotBlank(borrow.get(Borrow::getPriceFlag)),"是否借阅外单位资料为空");
        Map<String, Object> mainInfoMap = new HashMap<>(50);
        //招标单号
        mainInfoMap.put("ZBDH", borrow.get(Borrow::getBorrowNum));
        //申请人
        mainInfoMap.put("SQR", borrow.get(Borrow::getApplyUserName));
        //申请人联系方式
        mainInfoMap.put("SQRLXFS", borrow.get(Borrow::getApplyContacts));
        //项目名称
        mainInfoMap.put("XMMC", borrow.get(Borrow::getBidingName));
        //申请单位
        mainInfoMap.put("SQDW", borrow.get("orgName"));
        //申请部门
        mainInfoMap.put("SQBM", borrow.get(Borrow::getDepartmentName));
        //借阅资料类型
        mainInfoMap.put("JYZLLX", borrow.get(Borrow::getBorrowType));
        //是否涉及报价
        mainInfoMap.put("SFSJBJ", "Y".equals(borrow.get(Borrow::getPriceFlag)) ? "是" : "否");
        //使用方式
        mainInfoMap.put("SYFS", getDictName("BORROW_USETYPE", borrow.get(Borrow::getUseType)));
        //申请使用原因
        mainInfoMap.put("SQSYYY", borrow.get(Borrow::getBorrowCause));
        //是否借阅外单位资料（Y是N否）
        mainInfoMap.put("WISO", "Y".equals(borrow.get(Borrow::getPriceFlag)) ? "是" : "否");
        //单据状态
        mainInfoMap.put("DJZT", getStatus(borrow.get(Borrow::getStatus)));
        //被借阅单位总经理
        mainInfoMap.put("BJYDWZJL", borrow.get(Borrow::getManagerCode));
        List<Object> itemDataList = new ArrayList<>();
        String processTitle = "借阅申请审批";
        String mainTable = "BO_EU_JYSQSP";
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
        Map<String,Object> itemFile = new HashMap<>(50);
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, mainInfoMap, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile);
        log.info(JSON.toJSONString(dataPushFlowJsn));
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    public static String getStatus(String str) {
        Map<String, String> map = new HashMap<>(50);
        map.put("DRAFT","拟定");
        map.put("APPROVING","审批中");
        map.put("APPROVED","已审批");
        map.put("REJECTED","已驳回");
        map.put("ABANDON","已废弃");
        map.put("WITHDRAW","已撤回");
        return map.get(str);
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

    private void updateSatus(Long id, BorrowStatusEnum status){
        Record borrow = qlService.readByKey("Borrow",id,Record.class);
        Assert.notNull(borrow, "借阅申请ID不存在");

        Record r = new Record();
        r.put(Borrow::getBorrowId, id);
        r.put(Borrow::getStatus, status.getCode());
        qlService.update("Borrow", Arrays.asList(r));
    }
}
