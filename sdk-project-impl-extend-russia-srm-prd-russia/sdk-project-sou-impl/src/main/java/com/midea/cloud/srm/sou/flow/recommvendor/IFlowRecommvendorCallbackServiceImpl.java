package com.midea.cloud.srm.sou.flow.recommvendor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.MeiQl;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.constant.SrmConstant;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pm.pr.requirement.entity.RequirementHead;
import com.midea.cloud.srm.model.ql.dto.RecordDTO;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.enums.ExtPrRequirementGroupTypeEnum;
import com.midea.cloud.srm.model.sou.enums.SouRecommvendorStatusEnum;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorDto;
import com.midea.cloud.srm.model.sou.recommvendor.dto.RecommvendorProjectDto;
import com.midea.cloud.srm.model.sou.recommvendor.enums.RecommType;
import com.midea.cloud.srm.model.sou.req.constants.MqlType;
import com.midea.cloud.srm.model.sou.sourcing.entity.*;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.supcooperate.ext.requirement.souplan.entity.ExtPrSouRequirementGroup;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.supplier.info.entity.CompanyInfo;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import com.midea.cloud.srm.sou.bid.init.service.impl.ExtBidSouProjectApproveCallbackImpl;
import com.midea.cloud.srm.sou.common.ExtSouBidComponent;
import com.midea.cloud.srm.sou.constants.SouConstant;
import com.midea.cloud.srm.sou.meiql.borrow.dto.Borrow;
import com.midea.cloud.srm.sou.meiql.borrow.enums.BorrowStatusEnum;
import com.midea.cloud.srm.sou.recommvendor.service.ExtSouRecommVendorService;
import com.midea.cloud.srm.sou.sourcing.init.mapper.ExtSouRecommVendorMapper;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouDemandService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouFileService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouProjectService;
import com.midea.cloud.srm.sou.sourcing.init.service.IExtSouVendorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * 备注
 * @author huangbf3
 */
@Slf4j
@Service
public class IFlowRecommvendorCallbackServiceImpl implements IFlowBusinessCallbackService {

    @Autowired
    private QlService qlService;

    @Autowired
    private IExtSouProjectService projectService;

    @Autowired
    private ExtSouRecommVendorMapper extSouRecommVendorMapper;

    @Autowired
    private IExtSouFileService souFileService;

    @Autowired
    private IExtSouVendorService vendorService;

    @Autowired
    private ExtBidSouProjectApproveCallbackImpl projectApproveCallback;

    @Autowired
    private IExtSouDemandService demandService;

    @Value("${bpm.tjgys.processGroupId}")
    private String processGroupId;
    @Value("${bpm.tjgys.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;

    @Resource
    private PjSouClient pjSouClient;

    @Resource
    private BaseClient baseClient;

    @Autowired
    private QlOpenClient qlOpenClient;
    @Resource
    private BaseExtClient baseExtClient;

    private static final String BUSINESS_TYPE = "RCOMMVENDOR";
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& com.alibaba.cloud.commons.lang.StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();

                RecommvendorProjectDto projectDto = qlService.readByKey(RecommType.RecommvendorProject.name(), businessId, RecommvendorProjectDto.class);
                //更新为审批中
                projectDto.setProjectStatus(SouRecommvendorStatusEnum.APPROVING.getCode());
                projectDto.setStartBpmUsername(loginAppUser.getUsername());
                projectDto.setStartBpmNickname(loginAppUser.getNickname());

                qlService.update(RecommType.RecommvendorProject.name(), Collections.singletonList(projectDto));
            }
        }else{
            RecommvendorProjectDto projectDto = qlService.readByKey(RecommType.RecommvendorProject.name(), businessId, RecommvendorProjectDto.class);
            //更新为审批中
            projectDto.setProjectStatus(SouRecommvendorStatusEnum.APPROVING.getCode());

            qlService.update(RecommType.RecommvendorProject.name(), Collections.singletonList(projectDto));

        }

    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        RecommvendorProjectDto projectDto = qlService.readByKey(RecommType.RecommvendorProject.name(), businessId, RecommvendorProjectDto.class);
        projectDto.setProjectStatus(SouRecommvendorStatusEnum.APPROVED.getCode());
        qlService.update(RecommType.RecommvendorProject.name(), Collections.singletonList(projectDto));

        //查询推荐供应商列表
        List<RecommvendorDto> recommvendorDtoList = qlService.query(RecommType.Recommvendor.name(), MeiQl.newCondition().eq(RecommvendorDto::getProjectId, businessId), RecommvendorDto.class);

        //追加供应商推荐单的供应商数据
        //查询合并申请单号
        List<ExtSouDemand> demandList = demandService.lambdaQuery().eq(ExtSouDemand::getProjectId, projectDto.getProjectId()).eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO).list();
        if(CollectionUtils.isNotEmpty(demandList)) {
            //反查招标单据ID
            List<ExtSouDemand> bidDemandList = demandService.lambdaQuery().in(ExtSouDemand::getApplicantNo, demandList.stream().map(ExtSouDemand::getApplicantNo).collect(Collectors.toList())).ne(ExtSouDemand::getProjectId, projectDto.getProjectId()).eq(ExtSouDemand::getStatus, SrmConstant.NUM_ZERO).list();
            if(CollectionUtils.isNotEmpty(bidDemandList)) {
                //查询招标单据
                List<ExtSouProject> souProjectList = projectService.lambdaQuery().in(ExtSouProject::getProjectId, bidDemandList.stream().map(d -> d.getProjectId()).distinct().collect(Collectors.toList()))
                        .eq(ExtSouProject::getSouType, SouTypeEnum.bid.name()).list();
                if(CollectionUtils.isNotEmpty(souProjectList)) {
                    souProjectList.stream().forEach(project -> {
                        //追加供应商推荐单供应商
                        projectApproveCallback.recommvendoPass(project, recommvendorDtoList);
                    });
                }
            }
        }

        //把资质和品牌反写
        List<RecordDTO> companyInfoList = qlOpenClient.query(ContextPath.SUP, QlOpenWrappers.query(MqlType.SUPPLIER)
                .in(CompanyInfo::getCompanyId, recommvendorDtoList.stream().map(v -> v.getVendorId()).distinct().collect(Collectors.toList())), RecordDTO.class);

        Map<Long, RecommvendorDto> recommvendorDtoMap = recommvendorDtoList.stream().collect(Collectors.toMap(v -> v.getVendorId(), Function.identity(), (k1, k2)->k2));

        List<RecordDTO> updateCompanyInfoList = new ArrayList<>();
        companyInfoList.stream().forEach(companyInfo -> {
            RecommvendorDto recommvendorDto = recommvendorDtoMap.get(companyInfo.get(CompanyInfo::getCompanyId));
            if(Objects.isNull(recommvendorDto)) {
                return;
            }
            Boolean saveFlag = false;
            //资质
            if(StringUtils.isNotBlank(recommvendorDto.getExtAptitude())) {
                String pjQualifications = ObjectUtils.defaultIfNull(companyInfo.getString("pjQualifications"), "");
                List<String> pjQualificationsList = new ArrayList<>();
                if(StringUtils.isNotBlank(pjQualifications)) {
                    pjQualificationsList = new ArrayList<>(Arrays.asList(pjQualifications.split(";")));
                }
                if(!pjQualificationsList.contains(recommvendorDto.getExtAptitude())) {
                    pjQualificationsList.add(recommvendorDto.getExtAptitude());
                    companyInfo.put("pjQualifications", pjQualificationsList.stream().collect(Collectors.joining(";")));
                    saveFlag = true;
                }
            }

            //品牌
            if(StringUtils.isNotBlank(recommvendorDto.getExtBrand())) {
                String ceeaAgentBrand = ObjectUtils.defaultIfNull(companyInfo.getString("ceeaAgentBrand"), "");
                List<String> ceeaAgentBrandList = new ArrayList<>();
                if(StringUtils.isNotBlank(ceeaAgentBrand)) {
                    ceeaAgentBrandList = new ArrayList<>(Arrays.asList(ceeaAgentBrand.split(";")));
                }
                if(!ceeaAgentBrandList.contains(recommvendorDto.getExtBrand())) {
                    ceeaAgentBrandList.add(recommvendorDto.getExtBrand());
                    companyInfo.put("ceeaAgentBrand", ceeaAgentBrandList.stream().collect(Collectors.joining(";")));
                    saveFlag = true;
                }
            }

            if(saveFlag) {
                updateCompanyInfoList.add(companyInfo);
            }

        });

        if(CollectionUtils.isNotEmpty(updateCompanyInfoList)) {
            qlOpenClient.update(ContextPath.SUP, MqlType.SUPPLIER, updateCompanyInfoList);
        }
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        RecommvendorProjectDto projectDto = qlService.readByKey(RecommType.RecommvendorProject.name(), businessId, RecommvendorProjectDto.class);
        //更新为驳回
        projectDto.setProjectStatus(SouRecommvendorStatusEnum.REJECT.getCode());

        qlService.update(RecommType.RecommvendorProject.name(), Collections.singletonList(projectDto));
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        RecommvendorProjectDto projectDto = qlService.readByKey(RecommType.RecommvendorProject.name(), businessId, RecommvendorProjectDto.class);
        //更新为撤回
        projectDto.setProjectStatus(SouRecommvendorStatusEnum.WITHDRAW.getCode());

        qlService.update(RecommType.RecommvendorProject.name(), Collections.singletonList(projectDto));
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        RecommvendorProjectDto projectDto = qlService.readByKey(RecommType.RecommvendorProject.name(), businessId, RecommvendorProjectDto.class);
        //更新为作废
        projectDto.setProjectStatus(SouRecommvendorStatusEnum.ABANDON.getCode());

        qlService.update(RecommType.RecommvendorProject.name(), Collections.singletonList(projectDto));
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
        ExtSouProject souProject = projectService.getById(businessId);

        JSONObject processVars = new JSONObject();

        //招标负责人
        processVars.put("ZBFZR", getSouPrincipal(businessId));
        String ifBid = YesOrNo.NO.getName();
        Boolean bidFlag = ExtSouBidComponent.getInstance().isBid(souProject.getExtCategoryId());
        if(bidFlag) {
            ifBid = YesOrNo.YES.getName();
        }
        processVars.put("SFSYZBFW", ifBid);
        processVars.put("YS", ObjectUtils.allNotNull(souProject.getExtBudget()) ? souProject.getExtBudget().stripTrailingZeros().toPlainString() : "");

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+souProject.getSouName());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("getDataPushFlow: {}, {}", businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjSouClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&& com.alibaba.cloud.commons.lang.StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null|| com.alibaba.cloud.commons.lang.StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        ExtSouProject souProject = projectService.getById(businessId);
        LambdaQueryWrapper<ExtSouRecommendedVendor> qw = new LambdaQueryWrapper<>();
        qw.eq(ExtSouRecommendedVendor::getProjectId, businessId);
        List<ExtSouRecommendedVendor> rvList = extSouRecommVendorMapper.selectList(qw);
        ExtSouRecommendedVendor recommendedVendor = rvList != null && rvList.size() > 0 ? rvList.get(0) : new ExtSouRecommendedVendor();
        Map<String, Object> mainInfoMap = new HashMap<>(16);
        //推荐供应商单号
        mainInfoMap.put("TJGYSDH", souProject.getExtRecommendNo());
        //板块
        mainInfoMap.put("BK", souProject.getExtOrgBuName());
        //公司
        mainInfoMap.put("GS", souProject.getExtOrgOuName());
        //需求部门
        mainInfoMap.put("XQBM", souProject.getExtApplicantDepart());
        //寻源单号
        mainInfoMap.put("XYDH", recommendedVendor.getSouRequirementNo());
        //创建人
        mainInfoMap.put("CJR", souProject.getCreatedFullName());
        //创建日期
        mainInfoMap.put("CJSJ", BpmResult.sdfDate(souProject.getCreationDate()));
        //最后更新时间
        mainInfoMap.put("ZHGXSJ", BpmResult.sdfDate(souProject.getLastUpdateDate()));
        //技术负责人
        mainInfoMap.put("JSFZR", souProject.getExtTechPrincipal());
        //联系电话
        mainInfoMap.put("LXDH", souProject.getTel());
        //单据状态
        mainInfoMap.put("DJZT", getDictName("SOU_RECOMMVENDOR_STATUS", souProject.getProjectStatus()));
        //招标负责人
        mainInfoMap.put("ZBFZR", getSouPrincipal(businessId));
        //申请单号
        mainInfoMap.put("SQDH", souProject.getSourceFromNo());
        //项目名称
        mainInfoMap.put("XMMC", souProject.getSouName());
        //需求来源
        mainInfoMap.put("XQLY", getDictName("PR_SOU_REQUIREMENT_FROM", souProject.getSourceFromType()));
        //预算（万元）
        mainInfoMap.put("YS", souProject.getExtBudget());
        //品类
        mainInfoMap.put("PL", souProject.getExtCategoryName());
        //规模数量
        mainInfoMap.put("GMSL", souProject.getExtScaleQuantity());
        //投标意向金（元）
        mainInfoMap.put("TBYXJ", souProject.getExtEarnestAmount());
        //推荐供应商类型
        mainInfoMap.put("TJGYSLX", tjType(recommendedVendor.getRcommendType()));
        //是否公示
        mainInfoMap.put("SFGS", yesOrNo(recommendedVendor.getPublishFlag()));
        //项目概述及招标范围
        mainInfoMap.put("XMGSJZBFW", recommendedVendor.getProjectRemark());
        //供应商资质要求
        mainInfoMap.put("GYSZZYQ", recommendedVendor.getVendorFlairAdjure());
        //供应商业绩要求
        mainInfoMap.put("GYSYJYQ", recommendedVendor.getVendorBizAdjure());
        //前期招标情况
        mainInfoMap.put("QQZBQK", recommendedVendor.getPreInviteTenders());
        //备注
        mainInfoMap.put("BZ", souProject.getRemark());
        //供应商风险链接
        mainInfoMap.put("GYSFXLJ", getViewSrmRollBackUrl("vendorRisk", businessId ,"供应商推荐单-查看供应商风险"));
        //追加供应商原因
        mainInfoMap.put("ZJGYSYY", recommendedVendor.getAddVendorReason());
        List<Object> itemDataList = new ArrayList<>();
        //附件信息子表
        LambdaQueryWrapper<ExtSouFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExtSouFile::getProjectId, businessId);
        List<ExtSouFile> bidAttachmentList = souFileService.list(queryWrapper);
        for (ExtSouFile e : bidAttachmentList) {
            Map<String, Object> map = new HashMap<>(16);
            List<Map<String, Object>> file = new ArrayList<>();
            Map<String, Object> fileMap = new HashMap<>(50);
            fileMap.put("FILE_PATH_BYMOBILE", "");
            fileMap.put("FILE_NAME", e.getSouFileName());
            fileMap.put("FILE_PATH", String.format(addressPath + "fileSourceName=%s&fileuploadId=%s", e.getSouFileName(), e.getSouDocId()));
            file.add(fileMap);
            map.put("__TABLE", "BO_EU_FJXXZB");
            map.put("SJMC", file);
            map.put("BZ", e.getSouRemark());
            itemDataList.add(map);
        }
        //推荐供应商列表子表
        LambdaQueryWrapper<ExtSouVendor> querySouVendorWrapper = new LambdaQueryWrapper<>();
        querySouVendorWrapper.eq(ExtSouVendor::getProjectId, businessId);
        querySouVendorWrapper.orderByAsc(ExtSouVendor::getSortIndex);
        List<ExtSouVendor> souVendorList = vendorService.list(querySouVendorWrapper);
        extracted(itemDataList, souVendorList);
        String processTitle = "供应商推荐-"+souProject.getSouName();
        String mainTable = "BO_EU_SRMTJGYS";
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();
//        String createUser = "GW00244106";
        String createOrgId = null;
//        createOrgId = "10002247";
        SccPjUser sccPjUser = pjSouClient.getSccUserByPersonnelNo(createUser);
        if (sccPjUser != null && sccPjUser.getGroupId() != null) {
            createOrgId = String.valueOf(sccPjUser.getGroupId());
        }
        if (StringUtils.isBlank(createOrgId)) {
            throw new BaseException("查询不到hr组织id");
        }
        List<String> tableList = new ArrayList<>();
        //附件信息子表
        tableList.add("BO_EU_FJXXZB");
        //推荐供应商列表子表
        tableList.add("BO_EU_TJGYSLBZB");
        Map<String,Object> itemFile = new HashMap<>(50);
        itemFile.put("BO_EU_FJXXZB", BpmResult.getFileField("SJMC"));
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, mainInfoMap, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile);
        log.info(JSON.toJSONString(dataPushFlowJsn));
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    public String getSouPrincipal(Long projectId){
        List<RecordDTO> souDemandList = qlService.queryByWrapper(QlWrappers.query(MqlType.NPM_SOU_DEMAND).eq(ExtSouDemand::getProjectId,projectId), RecordDTO.class);
        if(CollectionUtils.isEmpty(souDemandList)) {
            return "";
        }
        List<String> applicantNoList = souDemandList.stream().map(record -> record.get(ExtSouDemand::getApplicantNo)).collect(Collectors.toList());
        List<RequirementHead> requirementHeadList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("PurchaseRequirementHead")
                .in(RequirementHead::getRequirementHeadNum, applicantNoList), RequirementHead.class);
        if(CollectionUtils.isEmpty(requirementHeadList)) {
            return "";
        }
        List<Long> requirementHeadIdList =  requirementHeadList.stream().map(r -> r.getRequirementHeadId()).collect(Collectors.toList());
        List<ExtPrSouRequirementGroup> groupList = qlOpenClient.query(ContextPath.SUP_CE, QlOpenWrappers.query("ExtPrSouRequirementGroup")
                .in(ExtPrSouRequirementGroup::getRequirementHeadId, requirementHeadIdList)
                .eq(ExtPrSouRequirementGroup::getGroupType, ExtPrRequirementGroupTypeEnum.SOU.getCode()), ExtPrSouRequirementGroup.class);
        if(CollectionUtils.isEmpty(groupList)) {
            return "";
        }
        return groupList.stream().map(s -> s.getUsername()).distinct().collect(Collectors.joining(" "));
    }

    public String getViewSrmRollBackUrl( String funName,Long formId,String formNo){
        String url =  pjSouClient.getViewSrmRollBackUrl(funName,formId,formNo);
        return url;
    }

    /**
     * 拆分
     * @param itemDataList 参数
     * @param souVendorList 参数
     */
    private void extracted(List<Object> itemDataList, List<ExtSouVendor> souVendorList) {
        for (ExtSouVendor e : souVendorList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_TJGYSLBZB");
            //供应商名称
            map.put("GYSMC", e.getVendorName());
            //报名联系人
            map.put("BMLXR", e.getLinkmanName());
            //报名联系电话
            map.put("BMLXDH", e.getPhone());
            //邮箱
            map.put("YX", e.getEmail());
            //注册资金
            map.put("ZCZJ", e.getExtRegisterFund());
            //成立时间
            map.put("CLSJ", BpmResult.sdfDate(e.getExtFounded()));
            //GSCP
            map.put("GSCP", e.getExtGscp());
            //是否重点关注
            map.put("SGZDGZ", yesOrNo(e.getExtIsMainPoint()));
            //公司地址
            map.put("GSDZ", e.getExtCompanyAddr());
            //资质
            map.put("ZZ", e.getExtAptitude());
            //品牌
            map.put("PP", e.getExtBrand());
            //供应商属性
            StringBuilder att = new StringBuilder();
            String[] attr = e.getExtVendorAttr().split(";");
            for (String va : attr) {
                String s = getDictName("SOU_RECOMM_VENDOR_NATRUE", va);
                att.append(s);
            }
            map.put("GYSSX", att);
            //是否新供应商
            map.put("SFXGYS", yesOrNo(e.getExtIsNewVendor()));
            //备注
            map.put("BZ", e.getExtRemark());
            //是否失信
            map.put("SFSX", yesOrNo(e.getExtIsDishonesty()));
            //是否经营异常
            map.put("SFJYYC", yesOrNo(e.getExtIsBizAnomaly()));
            //法人
            map.put("FR", e.getExtLegal());
            //主要人员
            map.put("ZTRY", e.getExtMainPeople());
            //主要股东
            map.put("ZYGD", e.getExtStockholder());
            //是否追加供应商
            map.put("SFZJGYS", yesOrNo(e.getExtIsAddVendor()));
            //行号
            map.put("HH", e.getSortIndex());
            itemDataList.add(map);
        }
    }

    public static String sx(Boolean sx) {
        if (sx == null) {
            return "";
        } else if (sx) {
            return "是";
        } else {
            return "否";
        }
    }

    public String getDictName(String dictCode, String va) {
        //字典 推荐供应商状态SOU_RECOMMVENDOR_STATUS
        List<DictItemDTO> gyqyList = baseClient.listAllByDictCode(dictCode);
        for (DictItemDTO e : gyqyList) {
            if (e.getDictItemCode().equals(va)) {
                return e.getDictItemName();
            }
        }
        return null;
    }

    public static String tjType(String str) {
        String add = "ADD";
        String recomm = "RECOMM";
        if (add.equals(str)) {
            return "追加供应商";
        } else if (recomm.equals(str)) {
            return "推荐供应商";
        } else {
            return null;
        }
    }

    public static String yesOrNo(String str) {
        return "Y".equals(str) ? "是" : "否";
    }
}
