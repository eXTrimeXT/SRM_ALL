package com.midea.cloud.srm.supcooperate.ext.requirement.pr.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.enums.chat.GroupTypeEnum;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.api.spec.ql.QlQueryWrapper;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.constant.NumConstant;
import com.midea.cloud.srm.feign.PjProjectBidExtClient;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.organization.entity.Organization;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.base.utils.DictUtil;
import com.midea.cloud.srm.model.pj.hruser.dto.HrUserOrgnizationDto;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementAttach;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.pm.pr.requirement.enums.RequirementApproveStatus;
import com.midea.cloud.srm.model.rbac.role.entity.Role;
import com.midea.cloud.srm.model.rbac.role.entity.RoleUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.supcooperate.enums.SouHandlerRoleType;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementHeadDTO;
import com.midea.cloud.srm.supcooperate.ext.requirement.pr.dto.PurchaseRequirementLineDTO;
import com.midea.cloud.srm.supcooperate.utils.DingTalkSender;
import javafx.beans.binding.ObjectBinding;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Service
public class PurchaseRequirementFlowServiceImpl implements IFlowBusinessCallbackService {

    @Resource
    private QlService qlService;

    @Resource
    private PjProjectExtClient pjProjectExtClient;


    @Autowired
    private PjProjectBidExtClient pjProjectBidExtClient;

//    @Value("${dingtalk.samebu.receiver:GW00045618}")
//    private String chargeUsername;



    @Resource
    private BaseClient baseClient;

    @Resource
    private RbacClient rbacClient;

    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;

    @Value("${bpm.lcxqsqd.processGroupId}")
    private String processGroupId;
    @Value("${bpm.lcxqsqd.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;
    @Resource
    private BaseExtClient baseExtClient;
    private static final String BUSINESS_TYPE = "REQUIREMENT";
    @Autowired
    private RedisUtil redisUtil;




    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                updateSatus(businessId, RequirementApproveStatus.APPROVING);

                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                Record r = new Record();
                r.put(RequirementHead::getRequirementHeadId, businessId);
                r.put("startBpmUsername", loginAppUser.getUsername());
                r.put("startBpmNickname", loginAppUser.getNickname());
                qlService.update("PurchaseRequirementHead", Arrays.asList(r));

                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            updateSatus(businessId, RequirementApproveStatus.APPROVING);
        }
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        Record requirement = qlService.readByKey("PurchaseRequirementHead", businessId, Record.class);
        Assert.notNull(requirement, "采购申请ID不存在");
        Record r = new Record();
        r.put(RequirementHead::getRequirementHeadId, businessId);
        r.put(RequirementHead::getAuditStatus, RequirementApproveStatus.APPROVED);
        r.put(PurchaseRequirementHeadDTO::getExtApproveTime, LocalDateTime.now());
        List<ExtPrSouRequirementGroup> souGroupList = qlService.queryByWrapper(QlWrappers.query(ExtPrSouRequirementGroup.class)
                .eq(ExtPrSouRequirementGroup::getRequirementHeadId, businessId).
                eq(ExtPrSouRequirementGroup::getGroupType, PrSouRequirementGroupTypeEnum.SOU.name()), ExtPrSouRequirementGroup.class);
        log.info("souGroupList===" + JSONObject.toJSONString(souGroupList));
        if (CollectionUtils.isNotEmpty(souGroupList)) {
            ExtPrSouRequirementGroup g = souGroupList.get(0);
            HrUserOrgnizationDto userOrganization = pjProjectExtClient.getHrUserOrgnizationByUsername(g.getUsername());
            log.info("userOrganization===" + JSONObject.toJSONString(userOrganization));
            if (userOrganization != null) {
                //公司
                Organization ouOrganization = userOrganization.getOuOrganization();
                //板块
                Organization buOrganization = userOrganization.getBuOrganization();
                //部门
                Organization departmentOrganization = userOrganization.getDepartmentOrganization();
                if(ObjectUtil.isNotNull(buOrganization)) {
                    r.put(PurchaseRequirementHeadDTO::getBuId, buOrganization.getOrganizationId());
                    r.put(PurchaseRequirementHeadDTO::getBuCode, buOrganization.getOrganizationCode());
                    r.put(PurchaseRequirementHeadDTO::getBuName, buOrganization.getOrganizationName());
                }
                if(ObjectUtil.isNotNull(ouOrganization)) {
                    r.put(PurchaseRequirementHeadDTO::getComId, ouOrganization.getOrganizationId());
                    r.put(PurchaseRequirementHeadDTO::getComCode, ouOrganization.getOrganizationCode());
                    r.put(PurchaseRequirementHeadDTO::getComName, ouOrganization.getOrganizationName());
                }
                if(ObjectUtil.isNotNull(departmentOrganization)) {
                    r.put(PurchaseRequirementHeadDTO::getDepId, departmentOrganization.getOrganizationId());
                    r.put(PurchaseRequirementHeadDTO::getDepCode, departmentOrganization.getOrganizationCode());
                    r.put(PurchaseRequirementHeadDTO::getDepName, departmentOrganization.getOrganizationName());
                }
            }
        }
        log.info("最终的数据===" + JSONObject.toJSONString(r));
        qlService.update("PurchaseRequirementHead", Arrays.asList(r));
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, RequirementApproveStatus.REJECTED);
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, RequirementApproveStatus.WITHDRAW);
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        updateSatus(businessId, RequirementApproveStatus.ABANDONED);
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
        PurchaseRequirementHeadDTO requirement = qlService.readByKey("PurchaseRequirementHead", businessId, PurchaseRequirementHeadDTO.class);
        DictItem ceeaPrTypeDictItem = baseExtClient.getDictItem("application_form_type",requirement.getCeeaPrType());
        List<PurchaseRequirementLineDTO> requirementLines = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementLine")
                .eq(PurchaseRequirementLineDTO::getRequirementHeadId, businessId), PurchaseRequirementLineDTO.class);
        JSONObject processVars = new JSONObject();
        processVars.put("SQDLX",requirement.getCeeaPrType()==null?null:ceeaPrTypeDictItem.getDictItemName());
        BigDecimal zje = new BigDecimal(0);
        if (CollectionUtils.isNotEmpty(requirementLines)) {
            zje = requirementLines.stream().map(s -> Optional.ofNullable(s.getExtPredictAmount()).orElse(new BigDecimal("0"))).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
//        processVars.put("YYZJE",requirement.getTotalBudget()==null?null:requirement.getTotalBudget());
        processVars.put("YYZJE", zje.compareTo(new BigDecimal(0)) == 0 ? null : zje);
        processVars.put("GS",requirement.getOrgName()==null?null:requirement.getOrgName());
        processVars.put("SFJCWZ", "Y".equals(requirement.getCeeaPurchaseType()) ? "是" : "否");

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName() + "-" + requirement.getRequirementHeadNum() + "-" + requirement.getApplyByNickname() + "-" + requirement.getApplyBy());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("---------临采需求申请单getDataPushFlow-----------businessId:{}---param:{}", businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&& com.alibaba.cloud.commons.lang.StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null|| com.alibaba.cloud.commons.lang.StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        PurchaseRequirementHeadDTO requirement = qlService.readByKey("PurchaseRequirementHead", businessId, PurchaseRequirementHeadDTO.class);
        Map<String, Object> mainInfoMap = new HashMap<>(16);
        //申请日期
        mainInfoMap.put("SQRQ", BpmResult.formatLocalDate(requirement.getApplyDate()));
        //申请公司
        mainInfoMap.put("SQGS", requirement.getOrgName());
        //申请部门
        mainInfoMap.put("SQBM", requirement.getCeeaDepartmentName());
        //主控部门
        mainInfoMap.put("ZKBM", getDictName("main_control_department", requirement.getDemandDepartmentCode()));
        //申请单类型
        mainInfoMap.put("SQDLX", getDictName("application_form_type", requirement.getCeeaPrType()));
        //采购申请说明
        mainInfoMap.put("CGSQSM", requirement.getCeeaAppointReason());
        List<Object> itemDataList = new ArrayList<>();
        //临采需求申请单明细
        List<PurchaseRequirementLineDTO> requirementLines = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementLine")
                .eq(PurchaseRequirementLineDTO::getRequirementHeadId, businessId), PurchaseRequirementLineDTO.class);
        for (PurchaseRequirementLineDTO e : requirementLines) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_LCXQSQDWLMX");
            //物资编码
            map.put("WZBM", e.getMaterialCode());
            //物资名称
            map.put("WZMC", e.getMaterialName());
            //规格型号
            map.put("GGXH", e.getExtMaterialModel());
            //品牌
            map.put("PP", e.getBrand());
            //需求数量
            map.put("XQSL", e.getRequirementQuantity());
            //基本计量单位
            map.put("JBJLDW", e.getUnit());
            //本次需求日期
            map.put("BCXQRQ", BpmResult.formatLocalDate(e.getRequirementDate()));
            //用途
            map.put("YT", e.getExtUseTo());
            //使用部门
            map.put("SYBM", e.getExtUseDepartmentName());
            //使用人信息
            map.put("SYRXX", e.getExtUserName());
            //实时库存
            map.put("SSKC", e.getExtActualStock());
            //共享库存数量
            map.put("GXKCSL", e.getExtShareStock());
            //附件
            map.put("FJ", BpmResult.getFileList(addressPath, e.getExtAttachName(), e.getExtAttachId()));
            itemDataList.add(map);
        }
        //临采需求申请单附件
        List<RequirementAttach> attachList = qlService.queryByWrapper(QlWrappers.query("PurchaseRequirementAttach")
                .eq(RequirementAttach::getRequirementHeadId, businessId), RequirementAttach.class);
        for (RequirementAttach e : attachList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_LCXQSQDXGFJSC");
            //附件
            map.put("FJ2", BpmResult.getFileList(addressPath, e.getAttachName(), e.getFileuploadId()));
            //上传人
            map.put("SCR", e.getCreatedFullName());
            //账号
            map.put("ZH", e.getCreatedBy());
            //上传时间
            map.put("SCSJ", BpmResult.sdfDate(e.getCreationDate()));
            itemDataList.add(map);
        }
        String processTitle = "临采需求申请单";
        String mainTable = "BO_EU_LCXQSQD";
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
        //临采定价单审批供应商报价明细
        tableList.add("BO_EU_LCXQSQDWLMX");
        //临采需求申请单附件
        tableList.add("BO_EU_LCXQSQDXGFJSC");
        Map<String,Object> itemFile = new HashMap<>(16);
        itemFile.put("BO_EU_LCXQSQDWLMX", BpmResult.getFileField("FJ"));
        itemFile.put("BO_EU_LCXQSQDXGFJSC", BpmResult.getFileField("FJ2"));
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




    private void updateSatus(Long id, RequirementApproveStatus status) {
        Record requirement = qlService.readByKey("PurchaseRequirementHead", id, Record.class);
        Assert.notNull(requirement, "采购申请ID不存在");

        Record r = new Record();
        r.put(RequirementHead::getRequirementHeadId, id);
        r.put(RequirementHead::getAuditStatus, status);
        qlService.update("PurchaseRequirementHead", Arrays.asList(r));
    }







}
