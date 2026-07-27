package com.midea.cloud.srm.biz.pj.sou.comp.init.flow;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.srm.biz.pj.hruser.service.ISccPjUserService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitQueryService;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouCreateApprovalUnPassDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.pj.supplier.rev.dto.BpmResult;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangbf3
 *【竞价管理-发布审批】
 * **/
@Service
@Slf4j
public class BiddingSubmitFlowServiceImpl implements IFlowBusinessCallbackService {

    @Value("${bpm.JJGL.processGroupId}")
    private String processGroupId;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Autowired
    private SouInitEventService souInitEventService;

    @Autowired
    private SouInitQueryService souInitQueryService;


    @Autowired
    private ISccPjUserService iSccPjUserService;


    /**
     * 提交审批流后的回调
     */
    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        souInitEventService.callbackAfterApprovalSubmit(businessId, SouTypeEnum.comp.name());
    }

    /**
     * 审批通过后的回调
     */
    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        souInitEventService.callbackAfterApprovalPass(businessId, SouTypeEnum.comp.name());
    }

    /**
     * 审批驳回后的回调
     */
    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        souInitEventService.callbackAfterApprovalUnPass(new ApiSouCreateApprovalUnPassDTO(businessId, SouApprovalStatusEnum.REJECTED), SouTypeEnum.comp.name());
    }

    /**
     * 审批撤回后的回调
     */
    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        souInitEventService.callbackAfterApprovalUnPass(new ApiSouCreateApprovalUnPassDTO(businessId, SouApprovalStatusEnum.WITHDRAW), SouTypeEnum.comp.name());
    }

    /**
     * 审批作废后的回调
     */
    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        souInitEventService.callbackAfterApprovalUnPass(new ApiSouCreateApprovalUnPassDTO(businessId, SouApprovalStatusEnum.ABANDONED), SouTypeEnum.comp.name());
    }

   /* =============================================== */

    /**
     * 组装表单数据
     */
    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {

        log.info("===================进入竞价管理-发布审批组装数据方法开始"+businessId);
        /* 1: 查询数据 */
        ApiSouInitProjectInfoVO souVO = souInitQueryService.getProject(businessId, SouTypeEnum.comp.name());
        /*2： 查询物料需求 */
        List<ApiSouItemVO> apiSouItemVOList = souInitQueryService.listRequires(businessId, SouTypeEnum.comp.name());
        /*3： 查询供应商信息 */
        List<ApiSouVendorVO> apiSouVendorVOList = souInitQueryService.listVendors(businessId, SouTypeEnum.comp.name());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        /* 组装主表信息  BO_EU_FBSP */
        Map<String, Object> mainTableData = new HashMap<>(50);
        //竞价单号
        mainTableData.put("JJDH",souVO.getSouNo());
        //报名开始时间
        mainTableData.put("BMKSSJ",(souVO.getSignUpStartTime() == null) ? null : dateFormat.format(souVO.getSignUpStartTime()));
        //报名截止时间
        mainTableData.put("BMJZSJ",(souVO.getSignUpEndTime() == null) ? null : dateFormat.format(souVO.getSignUpEndTime()));
        //竞价开始时间
        mainTableData.put("JJKSSJ",(souVO.getOrderStartTime() == null) ? null : dateFormat.format(souVO.getOrderStartTime()));
        //竞价截止时间
        mainTableData.put("JJJZSJ",(souVO.getOrderEndTime() == null) ? null : dateFormat.format(souVO.getOrderEndTime()));
        //发标时间
        mainTableData.put("FBSJ",(souVO.getPublishTime() == null) ? null : dateFormat.format(souVO.getPublishTime()));
        //创建时间
        mainTableData.put("CJSJ",(souVO.getCreationDate() == null) ? null : dateFormat.format(souVO.getCreationDate()));

        String forwardRuleText = "FORWARD_RULE";
        String reverseRuleText = "REVERSE_RULE";
        if(forwardRuleText.equals(souVO.getSouRules())){
            //竞价规则
            mainTableData.put("JJGZ","正向规则");
        }else if(reverseRuleText.equals(souVO.getSouRules())){
            //竞价规则
            mainTableData.put("JJGZ","反向规则");
        }else{
            //竞价规则
            mainTableData.put("JJGZ","");
        }
        //公开规则
        mainTableData.put("GKGZ",souVO.getPublicRules());
        //每项物资报价次数
        mainTableData.put("MXWZBJCS",souVO.getOrderNum());
        //报价上限%
        mainTableData.put("BJSX",souVO.getQuoteCap());
        //来源推荐供应商清单
        mainTableData.put("LYTJGYSQD","");

        List<Object> itemdata = new ArrayList<>();

        /*需求明细  BO_EU_XQMXZB */
        for (ApiSouItemVO apiSouItemVO : apiSouItemVOList) {
            Map<String, Object> map = new HashMap<>(50);
            map.put("__TABLE", "BO_EU_XQMXZB");
            //物资名称
            map.put("WZMC", apiSouItemVO.getItemDesc());
            //组合
            map.put("ZH", apiSouItemVO.getItemGroup());
            //所属单位
            map.put("SSDW", apiSouItemVO.getAffiliatedUnit());
            //履约保证金
            map.put("LYBZJ", apiSouItemVO.getPerformanceBond());
            //预付款
            map.put("YFK", apiSouItemVO.getAdvanceCharge());
            //月约产量
            map.put("YYCL", apiSouItemVO.getMonthlyProduction());
            //计量单位
            map.put("JLDW", apiSouItemVO.getMeteringUnit());
            //起拍价格
            map.put("QPJG", apiSouItemVO.getStartPrice());
            //梯次价格
            map.put("TCJG", apiSouItemVO.getEchelonPrice());
            itemdata.add(map);
        }

        /* 邀请参与者子表 BO_EU_YQCYZZB */
        for (ApiSouVendorVO apiSouVendorVO : apiSouVendorVOList) {
            Map<String, Object> map = new HashMap<>(50);
            //供应商编码
            map.put("GYSBM", apiSouVendorVO.getVendorCode());
            //供应商名称
            map.put("GYSMC", apiSouVendorVO.getVendorName());
            //联系人
            map.put("LXR", apiSouVendorVO.getLinkmanName());
            //电话
            map.put("DH", apiSouVendorVO.getPhone());
            //邮箱
            map.put("YX", apiSouVendorVO.getEmail());
            map.put("__TABLE", "BO_EU_YQCYZZB");
            itemdata.add(map);
        }

        /* 附件信息 */
        List<SouFile> souFileList = souVO.getSouFileList();
        /*招标资料附件  BO_EU_ZBWJFJ */
        for (SouFile souFile : souFileList) {
            if (SouFileTypeEnum.OUTER.name().equals(souFile.getFileType().name())) {
                Map<String, Object> map = new HashMap<>(50);
               map.put("CKWJ",dealFileList(souFile.getSouDocId()));
               //备注
                map.put("BZ",souFile.getSouRemark());
                map.put("__TABLE", "BO_EU_ZBWJFJ");
                itemdata.add(map);
                /* 申请资料附件 BO_EU_SQZLFJ */
            }else if (SouFileTypeEnum.INNER.name().equals(souFile.getFileType().name())) {
                Map<String, Object> map = new HashMap<>(50);
                map.put("CKWJ",dealFileList(souFile.getSouDocId()));
                //备注
                map.put("BZ",souFile.getSouRemark());
                map.put("__TABLE", "BO_EU_SQZLFJ");
                itemdata.add(map);
            }

        }

        Map<String,Object> itemFile = new HashMap<>(50);
        List<String> fList = new ArrayList<>();
        fList.add("FJSC");
        itemFile.put("BO_EU_FJ", fList);

        /* 其他子表 */
        List<String> itemtable = new ArrayList<>();
        itemtable.add("BO_EU_SQZLFJ");
        itemtable.add("BO_EU_XQMXZB");
        itemtable.add("BO_EU_YQCYZZB");
        itemtable.add("BO_EU_ZBWJFJ");

        String processtitle = "竞价管理_发布审批";
        String maintable = "BO_EU_FBSP";

        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();


        String createOrgId = null;
        SccPjUser sccPjUser = iSccPjUserService.getOne(new LambdaQueryWrapper<SccPjUser>().eq(SccPjUser::getPersonnelNo, createUser));
        if (sccPjUser != null && sccPjUser.getGroupId() != null) {
            createOrgId = String.valueOf(sccPjUser.getGroupId());
        }
        if (StringUtils.isBlank(createOrgId)) {
            throw new RuntimeException("查询不到hr组织id");
        }


        JSONObject dataPushFlowJsn ;
        dataPushFlowJsn = BpmResult.generateBpmJson(processtitle, maintable, mainTableData, processGroupId, appId,
                createOrgId, createUser, itemtable, itemdata, itemFile);
        log.info("===================进入竞价管理_发布审批组装数据方法结束"+dataPushFlowJsn.toString());
        log.info("===========竞价管理_发布审批JSON=============="+dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }


    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }


    public List<Map<String, Object>> dealFileList(Long fileId)  {

        com.midea.cloud.srm.model.file.upload.entity.Fileupload fileupload = new com.midea.cloud.srm.model.file.upload.entity.Fileupload();
        fileupload.setFileuploadId(fileId);
        fileupload.setPageNum(1);
        fileupload.setPageSize(1);
        PageInfo<Fileupload> fileuploads = fileCenterClient.listPage(fileupload,"N");
        List<com.midea.cloud.srm.model.file.upload.entity.Fileupload> list = fileuploads.getList();

        List<Map<String, Object>> fileList = new ArrayList<>();
        list.forEach(e -> {
            Map<String, Object> map = new HashMap<>(50);
            map.put("FILE_PATH_BYMOBILE", "");
            map.put("FILE_NAME", e.getFileSourceName());
            String mes = "fileSourceName="+e.getFileSourceName()+"&fileuploadId="+e.getFileuploadId();
            map.put("FILE_PATH", fileDownloadPath+mes);
            fileList.add(map);
        });
        return fileList;
    }

}
