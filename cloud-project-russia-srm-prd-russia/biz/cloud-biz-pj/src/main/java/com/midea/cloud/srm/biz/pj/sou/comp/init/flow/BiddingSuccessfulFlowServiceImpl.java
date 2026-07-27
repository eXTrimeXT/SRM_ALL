package com.midea.cloud.srm.biz.pj.sou.comp.init.flow;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.IdGenrator;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmNewFlagService;
import com.midea.cloud.srm.biz.pj.changchengapi.bpm.service.IBpmService;
import com.midea.cloud.srm.biz.pj.hruser.service.ISccPjUserService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitEventService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.init.service.SouInitQueryService;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao.SouOrderResultDAOImpl;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.dao.SouSelectMapper;
import com.midea.cloud.srm.biz.pj.sou.sourcing.select.service.SouSelectQueryService;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.file.FileCenterClient;
import com.midea.cloud.srm.feign.pj.base.BaseExtClient;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.ApiSouSelectResultVO;
import com.midea.cloud.srm.model.pj.sou.openapi.comp.vo.select.BiddingSuccessResltVO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.dto.select.ApiSouSelectQueryDTO;
import com.midea.cloud.srm.model.pj.sou.openapi.sourcing.vo.init.ApiSouInitProjectInfoVO;
import com.midea.cloud.srm.model.pj.sou.sourcing.entity.SouOrderResult;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouApprovalStatusEnum;
import com.midea.cloud.srm.model.pj.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.model.pj.supplier.rev.dto.BpmResult;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangbf3
 *中标通知审批 【中标通知】
 * **/
@Service
@Slf4j
public class BiddingSuccessfulFlowServiceImpl implements IFlowBusinessCallbackService {

    @Value("${bpm.ZBTZSP.processGroupId}")
    private String processGroupId;
    @Value("${bpm.ZBTZSP.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;

    @Autowired
    public FileCenterClient fileCenterClient;

    @Autowired
    private SouInitQueryService souInitQueryService;

    @Autowired
    private SouSelectQueryService souSelectQueryService;

    @Autowired
    private SouOrderResultDAOImpl souOrderResultDao;

    @Autowired
    private SouSelectMapper souSelectMapper;

    @Autowired
    private ISccPjUserService iSccPjUserService;

    @Autowired
    private BaseExtClient baseExtClient;

    @Autowired
    private IBpmService iBpmService;

    @Autowired
    private IBpmNewFlagService iBpmNewFlagService;
    private static final String BUSINESS_TYPE = "BIDDING_SUCCESS";
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 提交审批流后的回调
     */
    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())) {
                FlowInstanceRecord record = iBpmService.getLastFlowInstanceRecord(new FlowInstanceRecord().setTemplateCode(BUSINESS_TYPE).setBusinessId(businessId));
                if(record!=null&&StringUtils.isNotBlank(record.getInstanceId())){
                    /* 点击提交 */
                    LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                    ApiSouSelectQueryDTO apiSouSelectQueryDto = new ApiSouSelectQueryDTO();
                    apiSouSelectQueryDto.setProjectId(businessId);
                    List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listWinNotice(apiSouSelectQueryDto, SouTypeEnum.comp.name());
                    for(int i = 0 ; i < souVOList.size(); i++){
                        souOrderResultDao.lambdaUpdate()
                                //状态
                                .set(SouOrderResult::getWinNoticeStatus, SouApprovalStatusEnum.SUBMITTED)
                                .eq(SouOrderResult::getOrderResultId, souVOList.get(i).getOrderResultId())
                                .set(SouOrderResult::getStartBpmNoticeUsername, loginAppUser.getUsername())
                                .set(SouOrderResult::getStartBpmNoticeNickname, loginAppUser.getNickname())
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
            /* 点击提交 */
            ApiSouSelectQueryDTO apiSouSelectQueryDto = new ApiSouSelectQueryDTO();
            apiSouSelectQueryDto.setProjectId(businessId);
            List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listWinNotice(apiSouSelectQueryDto, SouTypeEnum.comp.name());
            for(int i = 0 ; i < souVOList.size(); i++){
                souOrderResultDao.lambdaUpdate()
                        //状态
                        .set(SouOrderResult::getWinNoticeStatus, SouApprovalStatusEnum.SUBMITTED)
                        .eq(SouOrderResult::getOrderResultId, souVOList.get(i).getOrderResultId())
                        .update();
            }
        }
    }

    /**
     * 审批通过后的回调
     */
    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        ApiSouSelectQueryDTO apiSouSelectQueryDto = new ApiSouSelectQueryDTO();
        apiSouSelectQueryDto.setProjectId(businessId);
        List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listWinNotice(apiSouSelectQueryDto, SouTypeEnum.comp.name());
        for(int i = 0 ; i < souVOList.size(); i++){
            souOrderResultDao.lambdaUpdate()
                    //状态
                    .set(SouOrderResult::getWinNoticeStatus, SouApprovalStatusEnum.APPROVED)
                    .eq(SouOrderResult::getOrderResultId, souVOList.get(i).getOrderResultId())
                    .update();
        }


    }

    /**
     * 审批驳回后的回调
     */
    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {

        ApiSouSelectQueryDTO apiSouSelectQueryDto = new ApiSouSelectQueryDTO();
        apiSouSelectQueryDto.setProjectId(businessId);
        List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listWinNotice(apiSouSelectQueryDto, SouTypeEnum.comp.name());
        for(int i = 0 ; i < souVOList.size(); i++){
            souOrderResultDao.lambdaUpdate()
                    //状态
                    .set(SouOrderResult::getWinNoticeStatus, SouApprovalStatusEnum.REJECTED)
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
        List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listWinNotice(apiSouSelectQueryDto, SouTypeEnum.comp.name());
        for(int i = 0 ; i < souVOList.size(); i++){
            souOrderResultDao.lambdaUpdate()
                    //状态
                    .set(SouOrderResult::getWinNoticeStatus, SouApprovalStatusEnum.WITHDRAW)
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
        List<ApiSouSelectResultVO> souVOList = souSelectQueryService.listWinNotice(apiSouSelectQueryDto, SouTypeEnum.comp.name());
        for(int i = 0 ; i < souVOList.size(); i++){
            souOrderResultDao.lambdaUpdate()
                    //状态
                    .set(SouOrderResult::getWinNoticeStatus, SouApprovalStatusEnum.ABANDONED)
                    .eq(SouOrderResult::getOrderResultId, souVOList.get(i).getOrderResultId())
                    .update();
        }

    }

    /* =============================================== */

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
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(new JSONObject());
        return JSONObject.toJSONString(bpmParam);
    }

    /**
     * 组装表单数据
     */
    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {

        log.info("===================进入提交定商定价结果审批【中标通知审批】组装数据方法开始"+businessId);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = iBpmNewFlagService.getOne(Wrappers.lambdaQuery(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE)));
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null||StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        ApiSouSelectQueryDTO apiSouSelectQueryDto = new ApiSouSelectQueryDTO();
        apiSouSelectQueryDto.setProjectId(businessId);

        ApiSouInitProjectInfoVO souVO = souInitQueryService.getProject(businessId, SouTypeEnum.comp.name());
        List<BiddingSuccessResltVO> biddingSuccessResltList = souSelectMapper.biddingSuccessResltList(apiSouSelectQueryDto);

        //组装主表信息  BO_EU_ZBTZSP  中标通知审批
        Map<String, Object> mainTableData = new HashMap<>(50);
        //申请人
        mainTableData.put("SQR",souVO.getCreatedFullName());
        //申请日期
        mainTableData.put("SQRQ",souVO.getCreationDate());
        //备注
        mainTableData.put("BZ","");




        /* 组装子表信息  BO_EU_ZBTZSPZB  中标通知审批子表 */
        List<Object> itemdata = new ArrayList<>();
        for(int i = 0 ; i < biddingSuccessResltList.size(); i ++){
            Map<String, Object> map = new HashMap<>(50);
            BiddingSuccessResltVO biddingSuccessResltVO = biddingSuccessResltList.get(i);
            //单位名称  AFFILIATED_UNIT
            map.put("DWMC", biddingSuccessResltVO.getAffiliatedUnit());
            //项目编号  sou_no
            map.put("XMBH", biddingSuccessResltVO.getSouNo());
            //中标单位   WIN_VENDOR_NAME
            map.put("ZBDW", biddingSuccessResltVO.getWinVendorName());
            //联系人   linkman_name
            map.put("LXR", biddingSuccessResltVO.getLinkmanName());
            //联系方式  phone
            map.put("LXFS", biddingSuccessResltVO.getPhone());
            //中标物资名称  item_desc
            map.put("ZBWZMC", biddingSuccessResltVO.getItemDesc());
            //中标价格（元） win_vendor_price
            map.put("ZBJG", biddingSuccessResltVO.getWinVendorPrice());
            //计量单位  METERING_UNIT
            map.put("JLDW", biddingSuccessResltVO.getMeteringUnit());
            //备注
            map.put("BZ", biddingSuccessResltVO.getWinNoticeRemark());
            map.put("__TABLE", "BO_EU_ZBTZSPZB");
            itemdata.add(map);
        }


        Map<String,Object> itemFile = new HashMap<>(50);
        List<String> fList = new ArrayList<>();
        fList.add("FJSC");
        itemFile.put("BO_EU_FJ", fList);

        /* 其他子表 */
        List<String> itemtable = new ArrayList<>();
        itemtable.add("BO_EU_ZBTZSPZB");

        String processtitle = "竞价管理_中标通知审批";
        String maintable = "BO_EU_ZBTZSP";

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
        log.info("===================进入竞价管理_中标通知审批组装数据方法结束"+dataPushFlowJsn.toString());
        log.info("===========竞价管理_中标通知审批JSON=============="+dataPushFlowJsn.toString());
        return JsonUtil.entityToJsonStr(dataPushFlowJsn);

    }
}
