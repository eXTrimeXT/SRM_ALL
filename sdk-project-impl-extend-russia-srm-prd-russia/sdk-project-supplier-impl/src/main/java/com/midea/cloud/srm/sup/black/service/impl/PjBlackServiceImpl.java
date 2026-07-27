package com.midea.cloud.srm.sup.black.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.meiql.api.service.QlService;
import com.midea.cloud.meiql.api.spec.pojo.Record;
import com.midea.cloud.meiql.core.core.QlWrappers;
import com.midea.cloud.srm.feign.PjProjectExtClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.model.base.black.dto.BlackDto;
import com.midea.cloud.srm.model.base.black.entity.BlackCompany;
import com.midea.cloud.srm.model.base.scene.entity.SceneFile;
import com.midea.cloud.srm.model.base.scene.entity.SceneFileDetail;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @date: 2023/10/17 09:36
 * 供应商黑名单
 * @author 100014323
 */
@Slf4j
@Service
public class PjBlackServiceImpl implements IFlowBusinessCallbackService {


    @Resource(name = "blackServiceImpl")
    private IFlowBusinessCallbackService blackServiceImpl;

    @Autowired
    private QlService qlService;

    @Autowired
    QlOpenClient qlOpenClient;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    public FileCenterClient fileCenterClient;


    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Value("${bpm.GYSHMD.processGroupId}")
    private String processGroupId;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;


    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {

        /**
         * 查询供应商黑名单信息 businessId 为 black 主键 BLACK_ID
         * 黑名单 meicloud_usrm_cloud_biz_supplier.scc_base_black  黑名单新增编码、创建人、创建时间
         * 黑名单明细 meicloud_usrm_cloud_biz_supplier.scc_base_black_company 供应商名称、统一社会信用代码、法人代表、股东、原因
         * 场景附件信息 meicloud_usrm_cloud_biz_base.scc_base_scene_file  平台id(存放平台)、附件名称
         * scc_flow_instance_record 根据业务单据查询该表，有记录则是重新提交，没有则是第一次提交
         */
        log.info("===================进入黑名单装数据方法开始"+businessId);
        // 根据黑名单id查询黑名单表 scc_base_black
        BlackDto blackDto = qlService.readByKey("Black",businessId,BlackDto.class);
        // 根据黑名单id查询黑名单供应商信息
        List<Record> blackCompanyList = qlService.queryByWrapper(QlWrappers.query(BlackCompany.class).
                eq(BlackCompany::getBlackId, blackDto.getBlackId()), Record.class);

        // 根据businessId查询关联附件
        SceneFile sceneFileParam = (new SceneFile()).setBusinessId(businessId);
        List<SceneFile> sceneFilelist = baseClient.listSceneFile(sceneFileParam);

        Map<String, Object> bpmBlackmDto = new HashMap<>(16);
        bpmBlackmDto.put("HMDXZBH", blackDto.getBlackCode());
        bpmBlackmDto.put("CJR",blackDto.getCreatedFullName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        if(blackDto.getCreationDate() != null){
            bpmBlackmDto.put("CJSJ",simpleDateFormat.format(blackDto.getCreationDate()));
        }


        // 根据黑名单id查询黑名单供应商信息组装
        List<Object> itemdata = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(blackCompanyList)) {
            blackCompanyList.forEach(e -> {
                Map<String, Object> map = new HashMap<>(16);
                map.put("GYSMC",e.get(BlackCompany::getCompanyName));
                map.put("TYSHXYDM",e.get(BlackCompany::getSocialCreditCode));
                map.put("FRDB",e.get(BlackCompany::getLegalPerson));
                // 股东
                map.put("GD",e.get("shareholder"));
                // 原因
                map.put("YY",e.get("reason"));
                map.put("__TABLE", "BO_EU_GYSHMDMXZB1");
                itemdata.add(map);
            });
        }


         // 附件信息组装
        if (CollectionUtils.isNotEmpty(sceneFilelist)) {
            sceneFilelist.forEach(e -> {
                List<SceneFileDetail> list =   e.getSceneFileDetailList();
                for(int i = 0 ; i< list.size(); i++){
                    Map<String, Object> map = new HashMap<>(16);
                    List<Map<String, Object>> file = new ArrayList<>();
                    Fileupload fileupload = new Fileupload();
                    fileupload.setFileuploadId(list.get(i).getFileuploadId());
                    fileupload.setPageNum(1);
                    fileupload.setPageSize(1);
                    PageInfo<Fileupload> fileuploads = fileCenterClient.listPage(fileupload,"N");
                    List<Fileupload> fileList = fileuploads.getList();
                    for (Fileupload fileUpload : fileList) {
                        Map<String, Object> fileMap = new HashMap<>(16);
                        fileMap.put("FILE_PATH_BYMOBILE", "");
                        fileMap.put("FILE_NAME", fileUpload.getFileSourceName());
                        String mes = "fileSourceName="+fileUpload.getFileSourceName()+"&fileuploadId="+fileUpload.getFileuploadId();
                        fileMap.put("FILE_PATH", fileDownloadPath+mes);
                        file.add(fileMap);
                    }
                    map.put("FJSC",file);
                    map.put("FJMC",e.getFileName());
                    map.put("__TABLE", "BO_EU_GYSHMDMXZB2");
                    itemdata.add(map);
                }
            });

        }

        // 子表附件需要在这里维护，表名和附件字段
        Map<String,Object> itemFile = new HashMap<>(16);
        List<String> fList = new ArrayList<>();
        fList.add("FJSC");
        itemFile.put("BO_EU_GYSHMDMXZB2", fList);

        String processtitle = "供应商黑名单-"+blackCompanyList.get(0).getString("companyName");
        String maintable = "BO_EU_GYSHMDMX";

        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createuser = loginAppUser.getUsername();

        String createorgid = null;
        SccPjUser sccPjUser = pjProjectExtClient.getSccUserByPersonnelNo(createuser);
        if (sccPjUser != null && sccPjUser.getGroupId() != null) {
            createorgid = String.valueOf(sccPjUser.getGroupId());
        }
        if (StringUtils.isBlank(createorgid)) {
            throw new RuntimeException("查询不到hr组织id");
        }


        // 黑名单其他子表
        List<String> itemtable = new ArrayList<>();
        // 黑名单明细
        itemtable.add("BO_EU_GYSHMDMXZB1");
        // 供应商黑名单明细
        itemtable.add("BO_EU_GYSHMDMXZB2");

        JSONObject dataPushFlowJsn ;
        dataPushFlowJsn = BpmResult.generateBpmJson(processtitle, maintable, bpmBlackmDto, processGroupId, appId,
                createorgid, createuser, itemtable, itemdata, itemFile);
        log.info("===================进入黑名单装数据方法结束"+dataPushFlowJsn.toString());
        log.info("===========供应商黑名单JSON组装=============="+dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }




    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        blackServiceImpl.submitFlow(businessId,param);
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        blackServiceImpl.passFlow(businessId,param);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        blackServiceImpl.rejectFlow(businessId,param);
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        blackServiceImpl.withdrawFlow(businessId,param);
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        blackServiceImpl.destoryFlow(businessId,param);
    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return blackServiceImpl.getVariableFlow(businessId,param);

    }




}
