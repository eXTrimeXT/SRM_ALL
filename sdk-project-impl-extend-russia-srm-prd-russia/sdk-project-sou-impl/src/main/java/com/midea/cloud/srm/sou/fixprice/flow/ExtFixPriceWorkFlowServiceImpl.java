package com.midea.cloud.srm.sou.fixprice.flow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import com.midea.cloud.srm.model.pj.changchengapi.bpm.BpmStartProcessParam;
import com.midea.cloud.srm.model.pj.changchengapi.bpm.entity.BpmNewFlag;
import com.midea.cloud.srm.model.pj.flow.process.entity.FlowInstanceRecord;
import com.midea.cloud.srm.model.pj.hruser.entity.SccPjUser;
import com.midea.cloud.srm.model.rbac.user.entity.LoginAppUser;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceFile;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceHead;
import com.midea.cloud.srm.model.sou.fixprice.entity.ExtFixPriceLine;
import com.midea.cloud.srm.model.sou.fixprice.enums.ExtFixPriceStatusEnum;
import com.midea.cloud.srm.model.sou.fixprice.vo.ExtFixPriceInqOrderItemVO;
import com.midea.cloud.srm.model.supplier.bpm.BpmResult;
import com.midea.cloud.srm.model.workflow.service.IFlowBusinessCallbackService;
import com.midea.cloud.srm.sou.constants.NumConstant;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceFileDAO;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceHeadDAO;
import com.midea.cloud.srm.sou.fixprice.dao.ExtFixPriceLineDAO;
import com.midea.cloud.srm.sou.fixprice.service.ExtFixPriceEventService;
import com.midea.cloud.srm.sou.fixprice.service.ExtFixPriceQueryService;
import com.midea.cloud.srm.sou.meiql.inspect.enums.InspectStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 长城 - 定价单 - 审批流回调
 * PS: EXT_SOU_FIX_PRICE
 * @author huangbf3
 */
@Service
@Slf4j
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtFixPriceWorkFlowServiceImpl implements IFlowBusinessCallbackService {

    @Resource
    private ExtFixPriceEventService extFixPriceEventService;

    @Resource
    private ExtFixPriceHeadDAO extFixPriceHeadDAO;

    @Resource
    private ExtFixPriceFileDAO extFixPriceFileDAO;

    @Resource
    private PjSouClient pjSouClient;

    @Resource
    private ExtFixPriceLineDAO extFixPriceLineDAO;

    @Resource
    private BaseClient baseClient;

    @Resource
    private BaseExtClient baseExtClient;

    @Resource
    private PjProjectExtClient pjProjectExtClient;

    @Resource
    private ExtFixPriceQueryService extFixPriceQueryService;

    @Value("${bpm.zzsc.addressPath}")
    private String addressPath;

    @Value("${bpm.lcdjd.processGroupId}")
    private String processGroupId;

    @Value("${bpm.xbjdjsp.processGroupId2}")
    private String processGroupId2;

    @Value("${bpm.zzsc.appId}")
    private String appId;
    private static final String BUSINESS_TYPE = "EXT_SOU_FIX_PRICE";

    @Resource
    private RedisUtil redisUtil;
    @Override
    public void submitFlow(Long businessId, String param) throws Exception {
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        if(dictItem!=null&&StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())) {
            String submitFlowFlag = redisUtil.get(businessId+BUSINESS_TYPE+"submitFlow");
            if(StringUtils.equals(submitFlowFlag,YesOrNo.YES.getValue())){
                extFixPriceEventService.callbackAfterApprovalSubmit(businessId);

                LoginAppUser loginAppUser = AppUserUtil.getLoginAppUser();
                extFixPriceHeadDAO.lambdaUpdate()
                        .set(ExtFixPriceHead::getStartBpmUsername, loginAppUser.getUsername())
                        .set(ExtFixPriceHead::getStartBpmNickname, loginAppUser.getNickname())
                        .eq(ExtFixPriceHead::getFixPriceHeadId, businessId)
                        .update();

                pjProjectExtClient.saveOrUpdateBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
            }
        }else{
            extFixPriceEventService.callbackAfterApprovalSubmit(businessId);
        }
    }

    @Override
    public void passFlow(Long businessId, String param) throws Exception {
        extFixPriceEventService.callbackAfterApprovalPass(businessId);
    }

    @Override
    public void rejectFlow(Long businessId, String param) throws Exception {
        extFixPriceEventService.callbackAfterApprovalUnPass(businessId, ExtFixPriceStatusEnum.REJECTED.name());
    }

    @Override
    public void withdrawFlow(Long businessId, String param) throws Exception {
        extFixPriceEventService.callbackAfterApprovalUnPass(businessId, ExtFixPriceStatusEnum.WITHDRAW.name());
    }

    @Override
    public void destoryFlow(Long businessId, String param) throws Exception {
        extFixPriceEventService.callbackAfterApprovalUnPass(businessId, ExtFixPriceStatusEnum.ABANDONED.name());
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
        ExtFixPriceHead fixPrice = extFixPriceHeadDAO.getById(businessId);
        JSONObject processVars = new JSONObject();
        processVars.put("SFDC",StringUtils.equals(YesOrNo.YES.getValue(),fixPrice.getDc())?"是":"否");
        processVars.put("GS", fixPrice.getCreateUserOrgOuName());
        processVars.put("JSHJ", fixPrice.getTotalTaxPrice());

        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmStartProcessParam bpmParam = new BpmStartProcessParam();
        bpmParam.setProcessTitle(dictItem.getDictItemName() + "-" + fixPrice.getFixPriceNo() + "-" + fixPrice.getCreatedFullName() + "-" +fixPrice.getCreatedBy());
        bpmParam.setProcessGroupId(processGroupId2);
        bpmParam.setProcessVars(processVars);
        return JSONObject.toJSONString(bpmParam);
    }

    @Override
    public String getDataPushFlow(Long businessId, String param) throws Exception {
        log.info("---------临采定价单getDataPushFlow-----------businessId:{}---param:{}", businessId, param);
        DictItem dictItem = baseExtClient.getDictItem("BPM2_ZBUSINESS_TYPE",BUSINESS_TYPE);
        BpmNewFlag bpmNewFlag = pjProjectExtClient.getBpmNewFlag(new BpmNewFlag().setBusinessId(businessId).setBussinessType(BUSINESS_TYPE));
        if(dictItem!=null&& com.alibaba.cloud.commons.lang.StringUtils.equals(dictItem.getItemDescription(), YesOrNo.YES.getValue())
                &&(bpmNewFlag==null|| com.alibaba.cloud.commons.lang.StringUtils.equals(YesOrNo.YES.getValue(),bpmNewFlag.getNewBpmFlag()))){
            return getDataPushFlow(businessId);
        }
        ExtFixPriceHead fixPrice = extFixPriceHeadDAO.getById(businessId);
        Map<String, Object> mainInfoMap = new HashMap<>(16);
        //定价单号
        mainInfoMap.put("DJDH", fixPrice.getFixPriceNo());
        //申请日期
        mainInfoMap.put("SQRQ", BpmResult.formatLocalDate(fixPrice.getFixPriceDate()));
        //总金额
        mainInfoMap.put("ZJE", fixPrice.getTotalNotaxPrice());
        //价税合计
        mainInfoMap.put("JSHJ", fixPrice.getTotalTaxPrice());
        //附件
        List<ExtFixPriceFile> fileList = extFixPriceFileDAO.lambdaQuery().eq(ExtFixPriceFile::getFixPriceHeadId, businessId).list();
        List<Map<String, Object>> reList = new ArrayList<>();
        fileList.forEach(e -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("FILE_PATH_BYMOBILE", "");
            map.put("FILE_NAME", e.getFileName());
            map.put("FILE_PATH", String.format(addressPath + "fileSourceName=%s&fileuploadId=%s", e.getFileName(), e.getFileId()));
            reList.add(map);
        });
        mainInfoMap.put("FJ", reList);
        //备注
        mainInfoMap.put("DJSM", fixPrice.getRemark());
        //是否代采
        mainInfoMap.put("SFDC", BpmResult.dealYesOrNo(fixPrice.getDc()));
        List<Object> itemDataList = new ArrayList<>();
        List<ExtFixPriceLine> priceLineList = extFixPriceLineDAO.list(ExtFixPriceLine::getFixPriceHeadId, businessId);
        //临采定价单审批供应商报价明细
        for (ExtFixPriceLine a : priceLineList) {
            List<ExtFixPriceInqOrderItemVO> mxList = extFixPriceQueryService.listSouInqOrderItemsForPriceLine(Long.parseLong(a.getSourceFromLineId()));
            for (ExtFixPriceInqOrderItemVO b : mxList) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("__TABLE", "BO_EU_LCDJDSPGYSBJMX");
                //供应商编码
                map.put("GYSBM", b.getVendorCode());
                //供应商名称
                map.put("GYSMC", b.getVendorName());
                //未税单价
                map.put("WSDJ", b.getStandardNotaxPrice());
                //报价时间
                map.put("BJSJ", BpmResult.sdfDate(b.getSubmitTime()));
                //税率（%）
                map.put("SLV", b.getTaxRate());
                //到货周期（自然日）
                map.put("DHZQ", b.getExtLeadTime());
                //备注
                map.put("BZ", b.getOrderRemark());
                //报价有效期（自然日）
                map.put("BJYXQ", b.getPriceActiveDay());
                //质保期（自然日）
                map.put("ZBQ", b.getExtWarrantyPeriod());
                //预付款说明
                map.put("YFJSM", b.getAdvancePaymentRemark());
                //报价人
                map.put("BJR", b.getExtOrderByNickname());
                itemDataList.add(map);
            }
        }
        //临采定价单审批物资明细
        priceLineList.forEach(e -> {
            Map<String, Object> map = new HashMap<>(16);
            map.put("__TABLE", "BO_EU_LCDJDSPWZMX");
            //物资编码
            map.put("WZBM", e.getItemCode());
            //物资名称
            map.put("WZMC", e.getItemDesc());
            //规格型号
            map.put("GGXH", e.getExtMaterialModel());
            //基本计量单位
            map.put("JBJLDW", e.getUnit());
            //数量
            map.put("SL", e.getQuantity());
            //供应商名称
            map.put("GYSMC", e.getVendorName());
            //未税单价
            map.put("WSDJ", e.getNotaxPrice());
            //税率（%）
            map.put("SLV", e.getTaxRate());
            //发票类型
            map.put("FPLX", getDictName("EXT_SOU_INQ_ORDER_INVOICE_TYPE", e.getInvoiceType()));//todo字典
            //近期最低价格
            map.put("JQZDJG", e.getLatestMinNotaxPrice());
            //浮动比例
            map.put("FDBJ", e.getPriceFloatScale());
            //近期最低供应商
            map.put("JQZDGYS", e.getLatestMinVendorName());
            //近期最低品牌
            map.put("JQZDPP", e.getLatestMinBrand());
            //修改中标原因
            map.put("XGZBYY", e.getExtWinReason());
            //到货周期（自然日）
            map.put("DHZQ", e.getExtLeadTime());
            //采购员
            map.put("CGY", e.getBuyerNickname());
            //申请类型
            map.put("SQLX", getDictName("application_form_type", e.getApplyType()));
            //质保期（自然日）
            map.put("ZBQ", e.getExtWarrantyPeriod());
            //询价单号
            map.put("XJDH", e.getSourceFromNo());
            //付款方式
            map.put("FKFS", getDictName("JC_PAYMENT_WAY", e.getPaymentMethod()));
            //付款条件
            map.put("FKTJ", getDictName("PAYMENT_PROVISION", e.getPaymentTerm()));
            //是否签订合同
            map.put("SFQDHT", BpmResult.dealYesOrNo(String.valueOf(e.getHasSignedContract())));
            itemDataList.add(map);
        });

        String processTitle = "临采定价单-" + fixPrice.getFixPriceNo();
        String mainTable = "BO_EU_LCDJDSP";
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
        //临采定价单审批供应商报价明细
        tableList.add("BO_EU_LCDJDSPGYSBJMX");
        //临采定价单审批物资明细
        tableList.add("BO_EU_LCDJDSPWZMX");

        ArrayList<String> mainFile = Lists.newArrayList("FJ");

        Map<String,Object> itemFile = new HashMap<>(16);
        JSONObject dataPushFlowJsn = BpmResult.generateBpmJson(processTitle, mainTable, mainInfoMap, processGroupId, appId,
                createOrgId, createUser, tableList, itemDataList, itemFile, mainFile);
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

}
