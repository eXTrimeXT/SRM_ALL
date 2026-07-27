package com.midea.cloud.srm.biz.pj.sou.comp.init.flow;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitQueryService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.file.upload.dto.FileuploadDTO;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.init.ApiSouCreateApprovalUnPassDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouItemVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouVendorVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouFileTypeEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.pj.supplier.rev.dto.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 竞价 - 立项审批
 *
 * @author zhangwk12@midea.com
 * @since 2022/09/27
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class CompSouInitWorkFlowServiceImpl implements IFlowBusinessCallbackService {


    @Value("${bpm.fbsp.processGroupId}")
    private String processGroupId;

    @Value("${bpm.fbsp.appId}")
    private String appId;

    @Value("${bpm.fbsp.addressPath}")
    private String addressPath;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Autowired
    private SouInitEventService souInitEventService;

    @Autowired
    private SouInitQueryService souInitQueryService;

    /**
     * 提交审批流后的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void submitFlow(Long projectId, String param) {
        souInitEventService.callbackAfterApprovalSubmit(projectId, SouTypeEnum.comp.name());

    }

    /**
     * 审批通过后的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void passFlow(Long projectId, String param) {
        souInitEventService.callbackAfterApprovalPass(projectId, SouTypeEnum.comp.name());
    }

    /**
     * 审批驳回后的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void rejectFlow(Long projectId, String param) {
        souInitEventService.callbackAfterApprovalUnPass(new ApiSouCreateApprovalUnPassDTO(projectId, SouApprovalStatusEnum.REJECTED), SouTypeEnum.comp.name());
    }

    /**
     * 审批撤回后的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void withdrawFlow(Long projectId, String param) {
        souInitEventService.callbackAfterApprovalUnPass(new ApiSouCreateApprovalUnPassDTO(projectId, SouApprovalStatusEnum.WITHDRAW), SouTypeEnum.comp.name());
    }

    /**
     * 审批作废后的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void destoryFlow(Long projectId, String param) {
        souInitEventService.callbackAfterApprovalUnPass(new ApiSouCreateApprovalUnPassDTO(projectId, SouApprovalStatusEnum.ABANDONED), SouTypeEnum.comp.name());
    }

    @Override
    @Nullable
    public String getVariableFlow(Long projectId, String param) {
        return null;
    }

    @Override
    @Nullable
    public String getDataPushFlow(Long projectId, String param) throws Exception {

        log.info("---------getDataPushFlow-----------projectId:{}---param:{}", projectId, param);

        /* 1: 查询数据 */
        ApiSouInitProjectInfoVO souVO = souInitQueryService.getProject(projectId, SouTypeEnum.comp.name());

        /*2： 查询物料需求 */
        List<ApiSouItemVO> itemList = souInitQueryService.listRequires(projectId, SouTypeEnum.comp.name());

        /*3： 查询供应商信息 */
        List<ApiSouVendorVO> vendorList = souInitQueryService.listVendors(projectId, SouTypeEnum.comp.name());

        /*------------ */
        String jjdh = souVO.getSouNo();
        Date bmkssj = souVO.getSignUpStartTime();
        Date bmjzsj = souVO.getSignUpEndTime();
        Date jjkssj = souVO.getOrderStartTime();
        Date jjjzsj = souVO.getOrderEndTime();
        Date fbsj = souVO.getPublishTime();
        Date cjsj = souVO.getCreationDate();
        //竞价规则
        String jjgz = souVO.getSouRules();
        //公开规则
        String gkgz = souVO.getPublicRules();
//        每项物资报价次数
        Integer mxwzbjcs = souVO.getOrderNum();
//        报价上限
        Integer bjsx = souVO.getQuoteCap();
        /*------------ */
        Map<String, Object> projectInfoMap = new HashMap<>(50);
        projectInfoMap.put("JJDH", jjdh);
        projectInfoMap.put("BMKSSJ", bmkssj);
        projectInfoMap.put("BMJZSJ", bmjzsj);
        projectInfoMap.put("JJKSSJ", jjkssj);
        projectInfoMap.put("JJJZSJ", jjjzsj);
        projectInfoMap.put("FBSJ", fbsj);
        projectInfoMap.put("CJSJ", cjsj);
        projectInfoMap.put("JJGZ", jjgz);
        projectInfoMap.put("GKGZ", gkgz);
        projectInfoMap.put("MXWZBJCS", mxwzbjcs);
        projectInfoMap.put("BJSX", bjsx);
        /*LYTJGYSQD	来源推荐供应商清单 */
        List<Object> itemDataList = new ArrayList<>();
        /*物料需求 */
        for (ApiSouItemVO apiSouItemVO : itemList) {
            Map<String, Object> map = new HashMap<>(50);
            /*需求明细 */
            map.put("__TABLE", "BO_EU_XQMXZB");
//            物资名称
            map.put("WZMC", apiSouItemVO.getItemDesc());
//            组合
            map.put("ZH", apiSouItemVO.getItemGroup());
//            所属单位
            map.put("SSDW", apiSouItemVO.getAffiliatedUnit());
//            履约保证金
            map.put("LYBZJ", apiSouItemVO.getPerformanceBond());
//            预付款
            map.put("YFK", apiSouItemVO.getAdvanceCharge());
//            月约产量
            map.put("YYCL", apiSouItemVO.getMonthlyProduction());
//            计量单位
            map.put("JLDW", apiSouItemVO.getMeteringUnit());
//            起拍价格
            map.put("QPJG", apiSouItemVO.getStartPrice());
//            梯次价格
            map.put("TCJG", apiSouItemVO.getEchelonPrice());
            itemDataList.add(map);
        }

        /*招标文件附件 */
        List<SouFile> souFileList = souVO.getSouFileList();
        for (SouFile souFile : souFileList) {
            if (SouFileTypeEnum.OUTER.name().equals(souFile.getFileType().name())) {
                Map<String, Object> map = new HashMap<>(50);
                /*招标附件 */
                map.put("__TABLE", "BO_EU_ZBWJFJ");
                map.put("CKWJ", souFile.getSouFileName());
                map.put("BZ", souFile.getSouRemark());
                map.put("SCSJ", dealFileList(souFile.getSouFileId()));
                itemDataList.add(map);
            }
        }

        /*邀请供应商 */
        for (ApiSouVendorVO apiSouVendorVO : vendorList) {
            Map<String, Object> map = new HashMap<>(50);
            /*供应商明细 */
            map.put("__TABLE", "BO_EU_YQCYZZB");
//            供应商编码
            map.put("GYSBM", apiSouVendorVO.getVendorCode());
//            供应商名称
            map.put("GYSMC", apiSouVendorVO.getVendorName());
//            联系人
            map.put("LXR", apiSouVendorVO.getLinkmanName());
//            电话
            map.put("DH", apiSouVendorVO.getPhone());
//            邮箱
            map.put("YX", apiSouVendorVO.getEmail());
            itemDataList.add(map);
        }
        String processTitle = "竞价发布立项审核";
        String mainTable = "BO_EU_FBSP";
        String createOrgId = String.valueOf(projectId);
        String createUser = "GW00244106";
        List<String> tableList = new ArrayList<>();
        tableList.add("BO_EU_XQMXZB");
        tableList.add("BO_EU_ZBWJFJ");
        tableList.add("BO_EU_YQCYZZB");
        Map<String,Object> itemFile = new HashMap<>(50);
        if (CollectionUtils.isNotEmpty(souFileList)) {
            itemFile.put("BO_EU_ZBWJFJ", BpmResult.getFileField("CKWJ"));
        }
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, projectInfoMap, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile);
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    public List<Map<String, Object>> dealFileList(Long fileId) throws Exception {
        FileuploadDTO fileuploadDTO = new FileuploadDTO();
        fileuploadDTO.setFileId(fileId);
        List<Fileupload> list = fileCenterClient.uploadThirdBatch(fileuploadDTO);
        List<Map<String, Object>> fileList = new ArrayList<>();
        list.forEach(e -> {
            Map<String, Object> map = new HashMap<>(50);
            map.put("FILE_PATH_BYMOBILE", "");
            map.put("FILE_NAME", e.getFileSourceName());
            map.put("FILE_PATH", String.format(addressPath + "fileSourceName=%s&fileuploadId=%s", e.getFileSourceName(), e.getFileuploadId()));
            fileList.add(map);
        });
        return fileList;
    }

}
