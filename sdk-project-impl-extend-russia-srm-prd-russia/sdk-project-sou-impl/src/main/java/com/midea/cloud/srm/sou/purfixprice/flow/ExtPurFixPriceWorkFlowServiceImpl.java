package com.midea.cloud.srm.sou.purfixprice.flow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.midea.cloud.common.enums.YesOrNo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AppUserUtil;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.common.utils.redis.RedisUtil;
import com.midea.cloud.srm.feign.BaseExtClient;
import com.midea.cloud.srm.feign.PjSouClient;
import com.midea.cloud.srm.feign.base.BaseClient;
import com.midea.cloud.srm.feign.client.PjProjectExtClient;
import com.midea.cloud.srm.model.base.dict.dto.DictItemDTO;
import com.midea.cloud.srm.model.base.dict.entity.DictItem;
import com.midea.cloud.srm.model.base.purchase.entity.PurchaseUnit;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceStatusEnum;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceFile;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceHead;
import com.midea.cloud.srm.model.sou.purfixprice.entity.ExtPurFixPriceLine;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sou.constants.NumConstant;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceFileDAO;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceHeadDAO;
import com.midea.cloud.srm.sou.purfixprice.dao.ExtPurFixPriceLineDAO;
import com.midea.cloud.srm.sou.purfixprice.service.ExtPurFixPriceEventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: for srm集采定价单 - 审批流回调
 *
 * @author srm
 * @date 2024-05-18
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtPurFixPriceWorkFlowServiceImpl implements IFlowBusinessCallbackService {

    @Autowired
    private ExtPurFixPriceEventService extPurFixPriceEventService;
    @Autowired
    private ExtPurFixPriceHeadDAO extPurFixPriceHeadDAO;
    @Autowired
    private ExtPurFixPriceFileDAO extPurFixPriceFileDAO;
    @Autowired
    private PjSouClient pjSouClient;
    @Autowired
    private ExtPurFixPriceLineDAO extPurFixPriceLineDAO;
    @Autowired
    private BaseClient baseClient;
    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;
    @Value("${bpm.lcdjd.processGroupId}")
    private String processGroupId;
    @Value("${bpm.lcdjd.processGroupId2}")
    private String processGroupId2;
    @Value("${bpm.zzsc.appId}")
    private String appId;
    @Resource
    private BaseExtClient baseExtClient;

    private static final String BUSINESS_TYPE = "EXT_SOU_PUR_FIX_PRICE";
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        log.info("submitFlow: {}, {}", businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                extPurFixPriceEventService.callbackAfterApprovalSubmit(businessId);

                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                extPurFixPriceHeadDAO.lambdaUpdate()
                        .set(ExtPurFixPriceHead::getStartBpmUsername, loginAppUser.getUsername())
                        .set(ExtPurFixPriceHead::getStartBpmNickname, loginAppUser.getNickname())
                        .eq(ExtPurFixPriceHead::getPurFixPriceHeadId, businessId)
                        .update();
            }
        }else{
            extPurFixPriceEventService.callbackAfterApprovalSubmit(businessId);
        }
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        extPurFixPriceEventService.callbackAfterApprovalPass(businessId);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        extPurFixPriceEventService.callbackAfterApprovalUnPass(businessId, ExtFixPriceStatusEnum.REJECTED.name());
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        extPurFixPriceEventService.callbackAfterApprovalUnPass(businessId, ExtFixPriceStatusEnum.WITHDRAW.name());
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        extPurFixPriceEventService.callbackAfterApprovalUnPass(businessId, ExtFixPriceStatusEnum.ABANDONED.name());
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
        ExtPurFixPriceHead fixPrice = extPurFixPriceHeadDAO.getById(businessId);

        JSONObject processVars = new JSONObject();
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName()+"-"+fixPrice.getSouNo()+"-"+AppUserUtil.getLoginAppUser().getNickname());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(processVars);
        String dataPushFlow = JSONObject.toJSONString(bpmParam);
        log.info("dataPushFlow:"+dataPushFlow);
        return dataPushFlow;
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("---------集采定价单getDataPushFlow-----------businessId:{}---param:{}", businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&& StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null|| StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        Map<String/* dictItemCode */, String/* dictItemName */> areaDictMap = baseClient.getDictItmeMapByDictCode("REGION");

        ExtPurFixPriceHead fixPrice = extPurFixPriceHeadDAO.getById(businessId);
        Map<String, Object> mainInfoMap = new HashMap<>(NumConstant.SIXTEEN);
        // 项目名称
        mainInfoMap.put("XMMC", fixPrice.getDesignProjectName());
        // 轮数
        mainInfoMap.put("LS", fixPrice.getDesignNum());
        // 创建人
        mainInfoMap.put("CJR", fixPrice.getCreatedFullName());
        // 联系方式
        mainInfoMap.put("LXFS", fixPrice.getDesignCreatePhone());
        // 创建时间
        mainInfoMap.put("CJSJ", BpmResult.formatLocalDate(fixPrice.getCreationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
        // 执行时间从
        mainInfoMap.put("ZXSJC", fixPrice.getExecuteTimeFrom() != null ?
                BpmResult.formatLocalDate(fixPrice.getExecuteTimeFrom().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) : null);
        // 执行时间到
        mainInfoMap.put("ZXSJD", fixPrice.getExecuteTimeTo() != null ?
                BpmResult.formatLocalDate(fixPrice.getExecuteTimeTo().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) : null);
        // 项目策划方案 (暂时忽略)
//        mainInfoMap.put("XMCHFA", fixPrice.get);
        // 供货范围
        mainInfoMap.put("GHFW", areaDictMap.get(fixPrice.getDesignArea()));
        // 项目介绍
        mainInfoMap.put("XMJS", fixPrice.getDesignProjIntroduce());
        // 最终询价对比表 (附件)
        {
            List<ExtPurFixPriceFile> fileList = extPurFixPriceFileDAO.lambdaQuery().eq(ExtPurFixPriceFile::getPurFixPriceHeadId, businessId).list();
            List<Map<String, Object>> reList = new ArrayList<>();
            fileList.forEach(e -> {
                Map<String, Object> map = new HashMap<>(NumConstant.SIXTEEN);
                map.put("FILE_PATH_BYMOBILE", "");
                map.put("FILE_NAME", e.getFileName());
                map.put("FILE_PATH", String.format(addressPath + "fileSourceName=%s&fileuploadId=%s", e.getFileName(), e.getFileId()));
                reList.add(map);
            });
            mainInfoMap.put("ZZXJDD", reList);
        }
        List<Object> itemDataList = new ArrayList<>();
        List<ExtPurFixPriceLine> priceLineList = extPurFixPriceLineDAO.list(ExtPurFixPriceLine::getPurFixPriceHeadId, businessId);
        //临采定价单审批供应商报价明细
        Map<String, String> unitMap = baseClient.listAllEnablePurchaseUnit().stream().collect(Collectors.toMap(PurchaseUnit::getUnitCode, PurchaseUnit::getUnitName));
        for (ExtPurFixPriceLine priceLine : priceLineList) {
            Map<String, Object> map = new HashMap<>(NumConstant.SIXTEEN);
            itemDataList.add(map);

            map.put("__TABLE", "BO_EU_DCDJMX");
            // 轮次
            map.put("LC", priceLine.getRound());
            // 物资编码
            map.put("WZBM", priceLine.getItemCode());
            // 物资名称
            map.put("WZMC", priceLine.getItemDesc());
            // 规格型号
            map.put("GGXH", priceLine.getModel());
            // 计量单位
            map.put("JLDW", unitMap.get(priceLine.getUnit()));
            // 数量
            map.put("SL", priceLine.getRequireQuantity().stripTrailingZeros().toPlainString());
            // 供货范围
            map.put("GHFW", areaDictMap.get(priceLine.getArea()));
            // 备注
            map.put("BZ", priceLine.getRemark());
            // 未税单价
            map.put("WSDJ", priceLine.getNotaxPrice().stripTrailingZeros().toPlainString());
            // 质保期
            map.put("ZBQ", priceLine.getExtWarrantyPeriod());
            // 中标供应商
            map.put("ZBGYS", priceLine.getVendorName());
        }

        String processTitle = "集采定价单-" + fixPrice.getDesignProjectCode();
        String mainTable = "BO_EU_JCDCDJ";
        LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
        String createUser = loginAppUser.getUsername();
        String createOrgId = null;
        SccPjUser sccPjUser = pjSouClient.getSccUserByPersonnelNo(createUser);
        if (sccPjUser != null && sccPjUser.getGroupId() != null) {
            createOrgId = String.valueOf(sccPjUser.getGroupId());
        }
        if (StringUtils.isBlank(createOrgId)) {
            throw new BaseException("查询不到hr组织id");
        }
        List<String> tableList = new ArrayList<>();
        //集采采定价单审批供应商报价明细
        tableList.add("BO_EU_DCDJMX");

        ArrayList<String> mainFile = Lists.newArrayList("ZZXJDD");

        Map<String,Object> itemFile = new HashMap<>(NumConstant.SIXTEEN);
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, mainInfoMap, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile, mainFile);
        log.info(JSON.toJSONString(dataPushFlowJsn));
        log.info("dataPushFlowJsn:"+dataPushFlowJsn.toJSONString());
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

}
