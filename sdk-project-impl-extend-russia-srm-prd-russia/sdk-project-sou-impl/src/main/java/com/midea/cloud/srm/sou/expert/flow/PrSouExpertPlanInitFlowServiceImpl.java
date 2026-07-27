package com.midea.cloud.srm.sou.expert.flow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.rbac.RbacClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.rbac.user.entity.User;
import com.midea.cloud.srm.model.sou.expert.dto.ExtSouExpertApplyUnPassDTO;
import com.midea.cloud.srm.model.sou.expert.entity.*;
import com.midea.cloud.srm.model.sou.expert.enums.ExtSouExpertApplyStatusEnum;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sou.expert.mapper.ExtSouExpertCategoryRelationMapper;
import com.midea.cloud.srm.sou.expert.mapper.ExtSouExpertWorkMapper;
import com.midea.cloud.srm.sou.expert.mapper.ExtSouExpertWorkRelationMapper;
import com.midea.cloud.srm.sou.expert.service.ExtSouExpertEventService;
import com.midea.cloud.srm.sou.meiql.borrow.dto.Borrow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 专家库 - 专家申请 - 审批流回调定义
 * PS: 审批流编码  MQL_SOU_EXPERT_APPLY_INIT
 *
 * @author zhangwk12@meicloud.com
 * @since 2023/10/09
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class PrSouExpertPlanInitFlowServiceImpl implements IFlowBusinessCallbackService {

    @Autowired
    private ExtSouExpertEventService extSouExpertEventService;

    @Resource
    private ExtSouExpertCategoryRelationMapper extSouExpertCategoryRelationMapper;

    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;

    @Autowired
    private QlService qlService;

    @Value("${bpm.zjsq.processGroupId}")
    private String processGroupId;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Autowired
    private RbacClient rbacClient;

    @Resource
    private PjSouClient pjSouClient;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Resource
    private ExtSouExpertWorkRelationMapper workRelationMapper;

    @Resource
    private ExtSouExpertWorkMapper extSouExpertWorkMapper;

    @Override
    public void submitFlow(Long businessId, String param) {
        log.info("submitFlow: {}, {}", businessId, param);
        extSouExpertEventService.callbackAfterApplyApprovalSubmit(businessId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passFlow(Long businessId, String param) {
        log.info("passFlow: {}, {}", businessId, param);
        extSouExpertEventService.callbackAfterApplyApprovalPass(businessId);
    }

    @Override
    public void rejectFlow(Long businessId, String param) {
        log.info("rejectFlow: {}, {}", businessId, param);
        extSouExpertEventService.callbackAfterApplyApprovalUnPass(
                new ExtSouExpertApplyUnPassDTO(businessId, ExtSouExpertApplyStatusEnum.REJECTED));
    }

    @Override
    public void withdrawFlow(Long businessId, String param) {
        log.info("withdrawFlow: {}, {}", businessId, param);
        extSouExpertEventService.callbackAfterApplyApprovalUnPass(
                new ExtSouExpertApplyUnPassDTO(businessId, ExtSouExpertApplyStatusEnum.WITHDRAW));
    }

    @Override
    public void destoryFlow(Long businessId, String param) {
        log.info("destoryFlow: {}, {}", businessId, param);
        extSouExpertEventService.callbackAfterApplyApprovalUnPass(
                new ExtSouExpertApplyUnPassDTO(businessId, ExtSouExpertApplyStatusEnum.ABANDONED));
    }

    @Nullable
    @Override
    public String getVariableFlow(Long businessId, String param) {
        log.info("getVariableFlow: {}, {}", businessId, param);
        return null;
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) {
        log.info("getDataPushFlow: {}, {}", businessId, param);
        ExtSouExpertApply expertApply = qlService.readByKey(ExtSouExpertApply.class.getSimpleName(), businessId, ExtSouExpertApply.class);
        //获取申请人
        User user = rbacClient.getUserByIdAnon(expertApply.getApplyById());
        //获取学历
        List<ExtSouExpertEducation> educationList = qlService.queryByWrapper(QlWrappers.query(ExtSouExpertEducation.class)
                .eq(ExtSouExpertEducation::getExpertApplyId, expertApply.getExpertApplyId()), ExtSouExpertEducation.class);
        Map<String, Object> mainInfoMap = new HashMap<>(16);
        //数据来源
        mainInfoMap.put("SJLY", getSor(expertApply.getApplyFromType()));
        //申请编号
        mainInfoMap.put("SQBH", expertApply.getExpertApplyNo());
        //申请人
        mainInfoMap.put("SQR", expertApply.getApplyBy());
        //单据状态
        mainInfoMap.put("DJZT", getDstatus(expertApply.getApplyStatus()));
        //工号
        mainInfoMap.put("GH", user.getUsername());
        //姓名
        mainInfoMap.put("XM", user.getNickname());
        //最高学历
        mainInfoMap.put("ZGXL", getDictName("EXT_SOU_EXPERT_EDUCATION" , expertApply.getHighestDegree()));
        //毕业时间
        mainInfoMap.put("BYSJ", CollectionUtils.isNotEmpty(educationList) ? educationList.get(0).getStudyDateTo() : null);
        //性别
        mainInfoMap.put("XB", sex(expertApply.getSex()));
        //所属单位
        mainInfoMap.put("SSDW", expertApply.getOrgOuName());
        //部门/科室
        mainInfoMap.put("BMKS", expertApply.getDepartmentName());
        //职务
        mainInfoMap.put("ZW", expertApply.getJob());
        //序列等级
        mainInfoMap.put("XLDJ", expertApply.getJobRank());
        //在职状态
        mainInfoMap.put("ZZZT", jobStatus(expertApply.getJobStatus()));
        //手机号码
        mainInfoMap.put("SJHM", expertApply.getPhone());
        //入厂时间
        mainInfoMap.put("RCSJ", expertApply.getHireDate());
        //毕业院校
        mainInfoMap.put("BYYX", CollectionUtils.isNotEmpty(educationList) ? educationList.get(0).getStudyCollege() : null);
        //所学专业
        mainInfoMap.put("SXZY", CollectionUtils.isNotEmpty(educationList) ? educationList.get(0).getMajor() : null);
        //申报等级
        mainInfoMap.put("SBDJ", applyLevel(expertApply.getApplyLevel()));
        //适用品类scc_npm_sou_expert_category
        List<ExtSouExpertCategoryRelation> list = extSouExpertCategoryRelationMapper.selectList(new LambdaQueryWrapper<ExtSouExpertCategoryRelation>().eq(ExtSouExpertCategoryRelation::getExpertApplyId, businessId));
        String plStr = list.stream().map(ExtSouExpertCategoryRelation::getCategoryName).collect(Collectors.joining(","));
        //适用品类
        mainInfoMap.put("SYPL", plStr);
        List<Object> itemDataList = new ArrayList<>();
        //适用评分组织子表
        List<ExtSouExpertOrgRelation> pf = qlService.queryByWrapper(QlWrappers.query(ExtSouExpertOrgRelation.class)
                .eq(ExtSouExpertOrgRelation::getExpertApplyId, expertApply.getExpertApplyId()), ExtSouExpertOrgRelation.class);
        for (ExtSouExpertOrgRelation s : pf) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_SYPFZZZB");
            map.put("ZZMC", s.getOrgName());
            itemDataList.add(map);
        }
        //亲属工作单位 scc_npm_sou_expert_work_relate
        LambdaQueryWrapper<ExtSouExpertWorkRelation> workRelationWrapper = new LambdaQueryWrapper<>();
        workRelationWrapper.eq(ExtSouExpertWorkRelation::getExpertApplyId, businessId);
        List<ExtSouExpertWorkRelation> workRelationList = workRelationMapper.selectList(workRelationWrapper);
        for (ExtSouExpertWorkRelation e : workRelationList) {
            Map<String, Object> map = new HashMap<>(50);
            map.put("__TABLE", "BO_EU_QSGZDW");
            map.put("GZDWMC", e.getWorkUnit());
            map.put("GZSJ", e.getRelativeType());
            itemDataList.add(map);
        }
        //工作履历子表  专家申请工作经历  scc_npm_sou_expert_work
        LambdaQueryWrapper<ExtSouExpertWork> expertWorkWrapper = new LambdaQueryWrapper<>();
        expertWorkWrapper.eq(ExtSouExpertWork::getExpertApplyId, businessId);
        List<ExtSouExpertWork> expertWorkList = extSouExpertWorkMapper.selectList(expertWorkWrapper);
        for (ExtSouExpertWork e : expertWorkList) {
            Map<String, Object> map = new HashMap<>(50);
            map.put("__TABLE", "BO_EU_GZLLZB");
            map.put("GZDWMC", e.getWorkUnit());
            map.put("GZSJ", BpmResult.formatLocalDate(e.getEntryDate()) + "-" + BpmResult.formatLocalDate(e.getQuitDate()));
            itemDataList.add(map);
        }
        //附件上传子表
        List<SceneFile> expertAttachList = baseClient.listSceneFileBatch(Collections.singletonList(businessId));
        for (SceneFile e : expertAttachList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_FJSCZB");
            map.put("FJMC", BpmResult.getFileList(addressPath, e.getFileName(), e.getFileuploadId()));
            map.put("BZ", e.getRemark());
            itemDataList.add(map);
        }
        String processTitle = "专家注册申请" + "-" + user.getNickname();
        String mainTable = "BO_EU_ZJSQ";
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
        //适用评分组织子表
        tableList.add("BO_EU_SYPFZZZB");
        //亲属工作单位
        tableList.add("BO_EU_QSGZDW");
        //工作履历子表
        tableList.add("BO_EU_GZLLZB");
        //附件上传子表
        tableList.add("BO_EU_FJSCZB");
        Map<String,Object> itemFile = new HashMap<>(50);
        itemFile.put("BO_EU_FJSCZB", BpmResult.getFileField("FJMC"));
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

    public static String getSor(String str) {
        Map<String, String> map = new HashMap<>(50);
        map.put("INDEPENDENT", "自主申请");
        map.put("GREEN_CHANNEL", "绿色通道");
        map.put("UPGRADE", "升级申请");
        map.put("CHANGE", "变更申请");
        return map.get(str);
    }

    public static String getDstatus(String str) {
        Map<String, String> map = new HashMap<>(50);
        map.put("DRAFT", "拟定");
        map.put("SUBMITTED", "已提交");
        map.put("APPROVING", "审批中");
        map.put("REJECTED", "已驳回");
        map.put("APPROVED", "已审批");
        map.put("ABANDONED", "已废弃");
        map.put("WITHDRAW", "已撤回");
        return map.get(str);
    }

    public static String sex(String str) {
        Map<String, String> map = new HashMap<>(50);
        map.put("1", "男");
        map.put("2", "女");
        return map.get(str);
    }

    public static String jobStatus(String str) {
        Map<String, String> map = new HashMap<>(50);
        map.put("1", "在职");
        map.put("2", "离职");
        return map.get(str);
    }

    public static String applyLevel(String str) {
        Map<String, String> map = new HashMap<>(50);
        map.put("NORMAL", "普通");
        map.put("SENIOR", "高级");
        return map.get(str);
    }
}
