package com.midea.cloud.srm.biz.pj.sou.comp.init.flow;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmNewFlagService;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmService;
import com.midea.cloud.srm.biz.pj.hruser.service.ISccPjUserService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.dao.SouProjectDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao.SouOrderResultDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao.SouSelectFileDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.service.SouSelectQueryService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.pj.base.BaseExtClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.file.upload.entity.Fileupload;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectResultVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouSelectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderResult;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouProject;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouSelectFile;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.pj.supplier.rev.dto.BpmResult;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author huangbf3
 *提交定商定价结果审批 【编制定标结果】
 * **/
@Service
@Slf4j
public class BiddingResultFlowServiceImpl implements IFlowBusinessCallbackService {

    @Value("${bpm.BZDBJG.processGroupId}")
    private String processGroupId;
    @Value("${bpm.BZDBJG.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Value("${bpm.GYSHMD.fileDownloadPath}")
    private String fileDownloadPath;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Autowired
    private SouSelectQueryService souSelectQueryService;

    @Autowired
    private SouSelectFileDAOImpl souSelectFileDao;

    @Autowired
    private SouOrderResultDAOImpl souOrderResultDao;

    @Autowired
    private ISccPjUserService iSccPjUserService;
    @Autowired
    private IBpmNewFlagService iBpmNewFlagService;
    @Autowired
    private BaseExtClient baseExtClient;
    @Autowired
    private IBpmService iBpmService;
    private static final String BUSINESS_TYPE = "BIDDING_RESULT";
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SouProjectDAOImpl souProjectDao;

    /**
     * 提交审批流后的回调
     */
    @Override

    public void submitFlow(Long businessId, String param) throws Exception {
        /* 点击提交 -改竞价评选表 已审核  【评选中的 定价审批中的状态 改为已提交】 */

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {

            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                FlowInstanceRecord record = iBpmService.getLastFlowInstanceRecord(new FlowInstanceRecord().setTemplateCode(BUSINESS_TYPE).setBusinessId(businessId));
                if(record!=null&&StringUtils.isNotBlank(record.getInstanceId())){
                    LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                    ApiSouSelectQueryDTO apiSouSelectQueryDto = new ApiSouSelectQueryDTO();
                    apiSouSelectQueryDto.setProjectId(businessId);
                    List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listOrderResult(apiSouSelectQueryDto, SouTypeEnum.comp.name());
                    for(int i = 0 ; i < souVOList.size(); i++){
                        souOrderResultDao.lambdaUpdate()
                                //状态
                                .set(SouOrderResult::getResultStatus, SouApprovalStatusEnum.SUBMITTED)
                                .set(SouOrderResult::getStartBpmUsername, loginAppUser.getUsername())
                                .set(SouOrderResult::getStartBpmNickname, loginAppUser.getNickname())
                                .eq(SouOrderResult::getOrderResultId, souVOList.get(i).getOrderResultId())
                                .update();
                    }
                    BpmNewFlag bpmNewFlag = new BpmNewFlag();
                    bpmNewFlag.setBusinessId(businessId);
                    bpmNewFlag.setBussinessType(BUSINESS_TYPE);
                    BpmNewFlag dbBpmNewFlag = iBpmNewFlagService.lambdaQuery()
                            .eq(BpmNewFlag::getBusinessId,businessId)
                            .eq(io.seata.common.util.StringUtils.isNotBlank(bpmNewFlag.getBussinessType()),BpmNewFlag::getBussinessType,bpmNewFlag.getBussinessType())
                            .one();
                    if(dbBpmNewFlag==null){
                        bpmNewFlag.setBpmNewFlagId(IdGenrator.generate());
                        bpmNewFlag.setNewBpmFlag(YesOrNo.YES.getValue());
                        iBpmNewFlagService.save(bpmNewFlag);
                    }
                }
            }
        }else{
            ApiSouSelectQueryDTO apiSouSelectQueryDto = new ApiSouSelectQueryDTO();
            apiSouSelectQueryDto.setProjectId(businessId);
            List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listOrderResult(apiSouSelectQueryDto, SouTypeEnum.comp.name());
            for(int i = 0 ; i < souVOList.size(); i++){
                souOrderResultDao.lambdaUpdate()
                        //状态
                        .set(SouOrderResult::getResultStatus, SouApprovalStatusEnum.SUBMITTED)
                        .eq(SouOrderResult::getOrderResultId, souVOList.get(i).getOrderResultId())
                        .update();
            }
        }
    }

    /**
     * 比较两个非空整数相等
     * @param one
     * @param two
     * @return
     */
    protected Boolean eqLong(Long one, Long two) {
        if(ObjectUtils.anyNull(one, two)) {
            return false;
        }
        return one.compareTo(two) == 0;
    }

    /**
     * 审批通过后的回调
     */
    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        log.info("===============提交定商定价结果审批返回的数据==============="+param);
        /* 通过 -【评选中的 定价审批中的状态 改为已审批】 将回传的数据插入编制定标结果 */

        List<SouOrderResult> orderResultList = souOrderResultDao.lambdaQuery().eq(SouOrderResult::getProjectId, businessId).list();
        if(CollectionUtils.isNotEmpty(orderResultList)) {
            orderResultList.stream().forEach(souOrderResult -> {
                souOrderResult.setResultStatus(SouApprovalStatusEnum.APPROVED);

                if(eqLong(souOrderResult.getWinVendorId(), souOrderResult.getMaxVendorId())) {
                    souOrderResult.setWinVendorPrice(souOrderResult.getMaxPrice());
                } else if(eqLong(souOrderResult.getWinVendorId(), souOrderResult.getSecondVendorId())) {
                    souOrderResult.setWinVendorPrice(souOrderResult.getSecondPrice());
                } else if(eqLong(souOrderResult.getWinVendorId(), souOrderResult.getThirdVendorId())) {
                    souOrderResult.setWinVendorPrice(souOrderResult.getThirdPrice());
                }

                souOrderResult.setMonthTotalAmount(ObjectUtils.defaultIfNull(souOrderResult.getWinVendorPrice(), BigDecimal.ZERO).multiply(ObjectUtils.defaultIfNull(souOrderResult.getMonthlyProduction(), BigDecimal.ZERO)));

            });

            souOrderResultDao.updateBatchById(orderResultList);
        }
    }

    /**
     * 审批驳回后的回调
     */
    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        ApiSouSelectQueryDTO apiSouSelectQueryDto = new ApiSouSelectQueryDTO();
        apiSouSelectQueryDto.setProjectId(businessId);
        List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listOrderResult(apiSouSelectQueryDto, SouTypeEnum.comp.name());
        for(int i = 0 ; i < souVOList.size(); i++){
            souOrderResultDao.lambdaUpdate()
                    //状态
                    .set(SouOrderResult::getResultStatus, SouApprovalStatusEnum.REJECTED)
                    .eq(SouOrderResult::getOrderResultId, souVOList.get(i).getOrderResultId())
                    .update();
        }

    }

    /**
     * 审批撤回后的回调
     */
    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        ApiSouSelectQueryDTO apiSouSelectQueryDto = new ApiSouSelectQueryDTO();
        apiSouSelectQueryDto.setProjectId(businessId);
        List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listOrderResult(apiSouSelectQueryDto, SouTypeEnum.comp.name());
        for(int i = 0 ; i < souVOList.size(); i++){
            souOrderResultDao.lambdaUpdate()
                    //状态
                    .set(SouOrderResult::getResultStatus, SouApprovalStatusEnum.WITHDRAW)
                    .eq(SouOrderResult::getOrderResultId, souVOList.get(i).getOrderResultId())
                    .update();
        }

    }

    /**
     * 审批作废后的回调
     */
    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        ApiSouSelectQueryDTO apiSouSelectQueryDto = new ApiSouSelectQueryDTO();
        apiSouSelectQueryDto.setProjectId(businessId);
        List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listOrderResult(apiSouSelectQueryDto, SouTypeEnum.comp.name());
        for(int i = 0 ; i < souVOList.size(); i++){
            souOrderResultDao.lambdaUpdate()
                    //状态
                    .set(SouOrderResult::getResultStatus, SouApprovalStatusEnum.ABANDONED)
                    .eq(SouOrderResult::getOrderResultId, souVOList.get(i).getOrderResultId())
                    .update();
        }
    }

    @Override
    public String getVariableFlow(Long businessId, String param) throws Exception {
        return null;
    }


    /* =============================================== */


    /**
     * 封装 根据类别启动流程接口参数
     * @param businessId
     * @return
     */
    public String getDataPushFlow(Long businessId){
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(new JSONObject());
        return JSONObject.toJSONString(bpmParam);
    }
    /**
     * 组装表单数据
     * @param businessId
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("===================进入提交定商定价结果审批【编制定标结果】组装数据方法开始"+businessId);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = iBpmNewFlagService.getOne(Wrappers.lambdaQuery(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE)));
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null||StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        /* 数据为评选中的定价审批列表  listOrderResult 根据project_id 查询 */
        ApiSouSelectQueryDTO apiSouSelectQueryDto = new ApiSouSelectQueryDTO();
        apiSouSelectQueryDto.setProjectId(businessId);
        List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listOrderResult(apiSouSelectQueryDto, SouTypeEnum.comp.name());

        LambdaQueryWrapper<SouSelectFile> queryFileWrapper = new LambdaQueryWrapper<>();
        queryFileWrapper.eq(SouSelectFile::getProjectId, businessId);
        List<SouSelectFile> souFileList = souSelectFileDao.list(queryFileWrapper);

        //组装主表信息  BO_EU_TJDSDJJGSP
        Map<String, Object> mainTableData = new HashMap<>(10);
        if(souFileList.size()>0){
           Long fileUploadId = souFileList.get(0).getSelectDocId();
           //附件上传
            mainTableData.put("FJSC",dealFileList(fileUploadId));
        }else{
            //附件上传
            mainTableData.put("FJSC","");
        }

        List<Object> itemdata = new ArrayList<>();

        //提交定商定价结果审批子表  BO_EU_TJDSDJJGSPZB
        for (ApiSouSelectResultVO apiSouSelectResultVO : souVOList) {
            Map<String, Object> map = new HashMap<>(10);
            map.put("__TABLE", "BO_EU_TJDSDJJGSPZB");
            //竞价单号
            map.put("PROJICTID",apiSouSelectResultVO.getProjectId());
            //物资ID
            map.put("WZMCID",apiSouSelectResultVO.getSouItemId());
            //物资名称
            map.put("WZMC", apiSouSelectResultVO.getItemDesc());
            //所属单位
            map.put("SSDW", apiSouSelectResultVO.getAffiliatedUnit());
            //状态
            map.put("ZT",  apiSouSelectResultVO.getResultStatus());
            //月产量
            map.put("YCL", apiSouSelectResultVO.getMonthlyProduction());
            //名称 【第一高】
            map.put("MC1", apiSouSelectResultVO.getMaxVendorName());
            //第一高供应商id
            map.put("MC1ID",apiSouSelectResultVO.getMaxVendorId());
            //单价（元）
            map.put("DJ1", apiSouSelectResultVO.getMaxPrice());
            //名称 【第二稿】
            map.put("MC2", apiSouSelectResultVO.getSecondVendorName());
            //第二高供应商id
            map.put("MC2ID",apiSouSelectResultVO.getSecondVendorId());
            //单价（元）
            map.put("DJ2", apiSouSelectResultVO.getSecondPrice());
            //名称 【第三高】
            map.put("MC3", apiSouSelectResultVO.getThirdVendorName());
            //第三高供应商id
            map.put("MC3ID",apiSouSelectResultVO.getThirdVendorId());
            //单价（元）
            map.put("DJ3", apiSouSelectResultVO.getThirdPrice());
            //名称 [上期中标供应商]
            map.put("MC4", apiSouSelectResultVO.getPeriodVendorName());
            //上期中标供应商id
            map.put("MC4ID",apiSouSelectResultVO.getPeriodVendorId());
            //单价（元）
            map.put("DJ4", apiSouSelectResultVO.getPeriodPrice());
            //价格差异率
            map.put("JGCYL", apiSouSelectResultVO.getDifferenceRate());
            //备注
            map.put("BZ", apiSouSelectResultVO.getOrderRemark());
            //中标供应商ID
            map.put("VMDID","");
            //中标供应商名称
            map.put("VMDNAME","");
//            中标原因
            map.put("VMRESON","");
//            是否流标(Y/N)
            map.put("ISFOR","");
//            流标原因
            map.put("ISRESON","");
            itemdata.add(map);
        }
        SouProject souProject = souProjectDao.getById(businessId);

        String processtitle = "定商定价审批-"+souProject.getSouName();
        String maintable = "BO_EU_TJDSDJJGSP";

        ArrayList<String> mainFile = Lists.newArrayList("FJSC");


        Map<String,Object> itemFile = new HashMap<>(10);
        List<String> fList = new ArrayList<>();
        fList.add("FJSC");
        itemFile.put("BO_EU_FJ", fList);

        /* 其他子表 */
        List<String> itemtable = new ArrayList<>();
        itemtable.add("BO_EU_TJDSDJJGSPZB");


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
                createOrgId, createUser, itemtable, itemdata, itemFile, mainFile);
        log.info("===================进入竞价管理_提交定商定价结果审批组装数据方法结束"+dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);
    }

    public List<Map<String, Object>> dealFileList(Long fileId)  {

        Fileupload fileupload = new Fileupload();
        fileupload.setFileuploadId(fileId);
        fileupload.setPageNum(1);
        fileupload.setPageSize(1);
        PageInfo<Fileupload> fileuploads = fileCenterClient.listPage(fileupload,"N");
        List<Fileupload> list = fileuploads.getList();

        List<Map<String, Object>> fileList = new ArrayList<>();
        list.forEach(e -> {
            Map<String, Object> map = new HashMap<>(10);
            map.put("FILE_PATH_BYMOBILE", "");
            map.put("FILE_NAME", e.getFileSourceName());
            String mes = "fileSourceName="+e.getFileSourceName()+"&fileuploadId="+e.getFileuploadId();
            map.put("FILE_PATH", "");
            fileList.add(map);
        });
        return fileList;
    }
}
