package com.midea.cloud.srm.sou.bid.init.flow;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ApiExtSouProjectInfoDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.init.ExtSouProjectDto;
import com.midea.cloud.srm.model.sou.req.BidDataSubmit;
import com.midea.cloud.srm.model.sou.req.BidDataSubmitEvaluator;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouFile;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouGroup;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouPlan;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementAttach;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementHead;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementVendor;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.enums.PrSouRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.vo.ExtPrSouRequirementHeadVO;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenQueryWrapper;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.sourcing.init.service.ExtSouInitQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 招标 - 立项审批流回调定义
 * PS: 审批流编码  MQL_PR_SOU_REQUIREMENT_INIT
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/07
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouBidFlowServiceImpl implements IFlowBusinessCallbackService {

    @Value("${bpm.ZBJH.processGroupId}")
    private String processGroupId;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;

    @Autowired
    private QlService qlService;

    @Autowired
    QlOpenClient qlOpenClient;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    public FileCenterClient fileCenterClient;
    @Autowired
    private ExtSouInitQueryService extSouInitQueryService;


    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("getVariableFlow: {}, {}", businessId, param);
        //流程编码 SOU_BID_INIT
        ApiExtSouProjectInfoDTO projectInfo = extSouInitQueryService.getProjectInfo(businessId);
        //招标基本信息
        ExtSouProjectDto project = projectInfo.getProject();
        //寻源单名称
        String souName = project.getSouName();
        String souNo = project.getSouNo();
        //板块
        String extOrgBuName = project.getExtOrgBuName();
        Long extOrgBuId = project.getExtOrgBuId();
        //公司名称
        String extOrgOuName = project.getExtOrgOuName();
        Long extOrgOuId = project.getExtOrgOuId();
        //招标流程
        String extSouProcess = project.getExtSouProcess();
        //收标方式
        String extSouMode = project.getExtSouMode();
        //招标方式
        String publishScope = project.getPublishScope();
        //评选方式(低价/高价/综合)【评分规则】
        String scoreRuleType = project.getScoreRuleType();
        //投资编号
        String extInvestNo = project.getExtInvestNo();
        //品类
        String extCategoryName = project.getExtCategoryName();
        //编码
        Long extCategoryId = project.getExtCategoryId();
        //技术负责人
        String extTechPrincipal = project.getExtTechPrincipal();
        //预算（万元）
        BigDecimal extBudget = project.getExtBudget();
        //招标类型
        String orderType = project.getOrderType();
        //规模数量
        String extScaleQuantity = project.getExtScaleQuantity();
        //申请人
        String extApplicant = project.getExtApplicant();
        //申请部门
        String extApplicantDepart = project.getExtApplicantDepart();
        //是否指定评标人
        String extAssignEvaluator = project.getExtAssignEvaluator();
        //评标总人数
        Integer extBidEvaluatorNum = project.getExtBidEvaluatorNum();
        //要求高级专家人数
        Integer extAskSeniorExpertNum = project.getExtAskSeniorExpertNum();
        //合并申请单号
        String applicantNo = project.getApplicantNo();
        //bpm是否招标
        String sfzb = "";
        // bpm生育时间
        String sysj = "";
        // bpm需求类型--
        String demandType = "";
        // bpm申请公司--srm公司编码
        String orgCode = "";
        // bpm申请部门 -- srm部门编码
        String ceeaDepartmentId = "";
        //bpm申请日期 --
        String applyDate = "";
        // bpm技术负责人 -- srm技术负责人账号
        String techUsername = "";
        // bpm技术负责人联系方式
        String techPhone = "";
        // bpm所属品类 -- srm品类编码
        String categoryCode = "";

        String processtitle = "创建招标单";
        String maintable = "BO_CJZBD";
        Map<String, Object> mainTableData = new HashMap<>(50);
        mainTableData.put("SFZB", sfzb);
        mainTableData.put("SQDH", souNo);
        mainTableData.put("XQLX", demandType);
        mainTableData.put("SQMC", souName);
        mainTableData.put("SQGS", orgCode);
        mainTableData.put("SQBM", ceeaDepartmentId);
        mainTableData.put("SQRQ", applyDate);
        mainTableData.put("JSFZR", techUsername);
        mainTableData.put("JSFZRLXFS", techPhone);
        mainTableData.put("BKMC", extOrgBuName);
        mainTableData.put("BKID", extOrgBuId);
        mainTableData.put("GSMC", extOrgOuName);
        mainTableData.put("GSID", extOrgOuId);
        mainTableData.put("SSPL", categoryCode);
        mainTableData.put("ZBLC", extSouProcess);
        mainTableData.put("SBFS", extSouMode);
        mainTableData.put("ZBFS", publishScope);
        mainTableData.put("PXFS", scoreRuleType);
        mainTableData.put("TZBH", extInvestNo);
        mainTableData.put("PL", extCategoryName);
        mainTableData.put("BM", extCategoryId);
        mainTableData.put("JSFZR", extTechPrincipal);
        mainTableData.put("YSJE", extBudget);
        mainTableData.put("ZBLX", orderType);
        mainTableData.put("GMSL", extScaleQuantity);
        mainTableData.put("SQR", extApplicant);
        mainTableData.put("SQBM", extApplicantDepart);
        mainTableData.put("SFZDPXR", extAssignEvaluator);
        mainTableData.put("SYSJ", sysj);
        mainTableData.put("PXZRS", extBidEvaluatorNum);
        mainTableData.put("YQYJRS", extAskSeniorExpertNum);
        mainTableData.put("HBSQDH", applicantNo);

        String processgroupid = processGroupId;
        String appid = appId;

        //createorgid 使用创建人的HR组织ID,先写死测试，这里后续要改
        String createorgid = "10000520";

        String createuser = project.getCreatedId().toString();

        List<String> itemtable = new ArrayList<>();
        itemtable.add("BO_CJZBD");

        JSONObject dataPushFlowJsn;
        dataPushFlowJsn = BpmResult.generateBpmJson(processtitle, maintable, mainTableData, processgroupid, appid,
                createorgid, createuser, itemtable, null, null);
        log.info("===========招标单JSON==============" + dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }


    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        log.info("submitFlow: {}, {}", businessId, param);
        //mqlPrRequirementInitEventService.callbackAfterApprovalSubmit(businessId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passFlow(Long businessId, String param) throws Exception {
        log.info("passFlow: {}, {}", businessId, param);
        //mqlPrRequirementInitEventService.callbackAfterApprovalPass(businessId);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        log.info("rejectFlow: {}, {}", businessId, param);
        /*mqlPrRequirementInitEventService.callbackAfterApprovalUnPass(
                new MqlPrRequirementApprovalUnPassDTO(businessId, RequirementApproveStatus.REJECTED));*/
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        log.info("withdrawFlow: {}, {}", businessId, param);
        /*mqlPrRequirementInitEventService.callbackAfterApprovalUnPass(
                new MqlPrRequirementApprovalUnPassDTO(businessId, RequirementApproveStatus.WITHDRAW));*/
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        log.info("destoryFlow: {}, {}", businessId, param);
        /*mqlPrRequirementInitEventService.callbackAfterApprovalUnPass(
                new MqlPrRequirementApprovalUnPassDTO(businessId, RequirementApproveStatus.ABANDONED));*/
    }


    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        log.info("getDataPushFlow: {}, {}", businessId, param);
        return null;
    }

}
