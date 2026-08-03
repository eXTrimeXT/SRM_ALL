package com.midea.cloud.srm.biz.pj.screen.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.enums.api.ResultStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogService;
import com.midea.cloud.srm.biz.pj.changchengapi.companybigdata.CompanyBigDataService;
import com.midea.cloud.srm.biz.pj.screen.AuthenticationScreenService;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.changchengapi.dto.CompanyAQCApiDTO;
import com.midea.cloud.srm.model.pj.sup.company.entity.AuthenticationScreen;
import com.midea.cloud.srm.ql.open.v1.client.QlOpenClient;
import com.midea.cloud.srm.ql.open.v1.client.enums.ContextPath;
import com.midea.cloud.srm.ql.open.v1.client.wrapper.QlOpenWrappers;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class IAuthenticationScreenServiceImpl implements AuthenticationScreenService {

    @Autowired
    QlOpenClient qlOpenClient;

    @Autowired
    private CompanyBigDataService companyBigDataService;

    @Value("${gwm.url.findToken-url}")
    private String findTokenUrl;

    @Value("${gwm.url.findTokenUsername}")
    private String findTokenUsername;

    @Value("${gwm.url.findTokenPassword}")
    private String findTokenPassword;

    @Value("${gwm.url.import-screening}")
    private String importScreening;

    @Value("${gwm.url.import-screening-accountId}")
    private int accountId;

    @Value("${gwm.url.import-screening-datasetId}")
    private int datasetId;

    @Value("${gwm.url.import-screening-profileId}")
    private String profileId;

    @Value("${ipaas.flow.api}")
    private String ipaasApi;

    @Value("${ipaas.flow.gscp_token_url}")
    private String ipaasGscpToken;

    @Value("${ipaas.flow.gscp_import_screening_url}")
    private String ipaasImportScreening;

    private static final int SUCCESS_VALUE_INT = 200;

    private static final String SUCCESS_VALUE_STR = "200";
    private static final String SUCCESS_CODE = "code";

    @Autowired
    private IInterfaceLogService interfaceLogService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取令牌接口
     * 20230924
     * **/
    @Override
    public Map<String,String> findToken(){

        Map<String,String> tokenResult = restTemplate.postForObject(StringUtils.joinWith("", ipaasApi, ipaasGscpToken, "?grant_type=client_credentials&scope=INTERNAL"), new HashMap<>(15), Map.class);

        return tokenResult;

    }

    private String formateFieldValue(String fieldValue) {
        if(StringUtils.isBlank(fieldValue)) {
            return "";
        }
        return fieldValue;
    }

    /**
     * 供应商风险信息接口
     * 20230925
     * **/
    @Override
    public String importScreening(Long companyId) {
        //根据供应商ID查询相关信息
        Assert.isTrue(companyId!=null,"供应商ID不能为空");
        StopWatch stopWatch = new StopWatch("GSCP接口处理-companyId@"+companyId);
        stopWatch.start("请求供应商内部微服务接口");
        // AuthenticationScreen authenticationScreen = qlOpenClient.read(ContextPath.SUP,"CompanyInfo",companyId, AuthenticationScreen.class);
        List<AuthenticationScreen> authenticationScreens = qlOpenClient.query(ContextPath.SUP,
                QlOpenWrappers.query("CompanyInfo").eq(AuthenticationScreen::getCompanyId, companyId), AuthenticationScreen.class);
        if(CollectionUtils.isEmpty(authenticationScreens)) {
            throw new BaseException("获取供应商信息失败");
        }
        AuthenticationScreen authenticationScreen = authenticationScreens.get(0);
        stopWatch.stop();
        stopWatch.start("获取GSCP请求Token");
        Map<String,String> tokerMap = findToken();
        stopWatch.stop();
        String accessToken = "";
        String gwlStatus = "";
        accessToken = tokerMap.get("access_token");
        if(StringUtils.isNotBlank(accessToken)){

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("profileId",profileId);
            jsonObject.put("accountId",accountId);
            jsonObject.put("datasetId",datasetId);
            jsonObject.put("correlationId",String.valueOf(System.currentTimeMillis()));

            JSONArray inputRecord = new JSONArray();

            authenticationScreen.getLcCode();
            authenticationScreen.getIdNumber();

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("fieldName","唯一编号（证件号）");
            jsonObject1.put("fieldValue",formateFieldValue((StringUtils.isEmpty(authenticationScreen.getLcCode())) ? authenticationScreen.getIdNumber() : authenticationScreen.getLcCode()));
            inputRecord.add(jsonObject1);

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("fieldName","公司全称");
            jsonObject3.put("fieldValue",formateFieldValue(authenticationScreen.getCompanyName()));
            inputRecord.add(jsonObject3);

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("fieldName","英文全称");
            jsonObject4.put("fieldValue","");
            inputRecord.add(jsonObject4);

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("fieldName","注册国家代码");
            jsonObject5.put("fieldValue",formateFieldValue(authenticationScreen.getCompanyCountry()));
            inputRecord.add(jsonObject5);

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("fieldName","注册地址");
            jsonObject6.put("fieldValue",formateFieldValue(authenticationScreen.getCompanyAddress()));
            inputRecord.add(jsonObject6);

            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("fieldName","法定代表人/负责人姓名");
            jsonObject7.put("fieldValue",formateFieldValue(authenticationScreen.getLegalPerson()));
            inputRecord.add(jsonObject7);


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("fieldName","集团英文名称");
            jsonObject2.put("fieldValue","");
            inputRecord.add(jsonObject2);


            JSONObject jsonObject8 = new JSONObject();
            jsonObject8.put("fieldName","企业证件类型1");
            jsonObject8.put("fieldValue","");
            inputRecord.add(jsonObject8);

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("fieldName","证件号码1");
            jsonObject9.put("fieldValue","");
            inputRecord.add(jsonObject9);

            JSONObject jsonObject10 = new JSONObject();
            jsonObject10.put("fieldName","企业证件类型2");
            jsonObject10.put("fieldValue","");
            inputRecord.add(jsonObject10);

            JSONObject jsonObject11 = new JSONObject();
            jsonObject11.put("fieldName","证件号码2");
            jsonObject11.put("fieldValue","");
            inputRecord.add(jsonObject11);

            JSONObject jsonObject12 = new JSONObject();
            jsonObject12.put("fieldName","企业证件类型3");
            jsonObject12.put("fieldValue","");
            inputRecord.add(jsonObject12);

            JSONObject jsonObject13 = new JSONObject();
            jsonObject13.put("fieldName","证件号码3");
            jsonObject13.put("fieldValue","");
            inputRecord.add(jsonObject13);

            JSONObject jsonObject14 = new JSONObject();
            jsonObject14.put("fieldName","法定代表人/负责人证件类型");
            jsonObject14.put("fieldValue","");
            inputRecord.add(jsonObject14);

            JSONObject jsonObject15 = new JSONObject();
            jsonObject15.put("fieldName","法定代表人/负责人证件号码");
            jsonObject15.put("fieldValue","");
            inputRecord.add(jsonObject15);

            JSONObject jsonObject16 = new JSONObject();
            jsonObject16.put("fieldName","代理人姓名");
            jsonObject16.put("fieldValue","");
            inputRecord.add(jsonObject16);

            JSONObject jsonObject17 = new JSONObject();
            jsonObject17.put("fieldName","代理人证件类型");
            jsonObject17.put("fieldValue","");
            inputRecord.add(jsonObject17);

            JSONObject jsonObject18 = new JSONObject();
            jsonObject18.put("fieldName","代理人证件号码");
            jsonObject18.put("fieldValue","");
            inputRecord.add(jsonObject18);

            JSONObject jsonObject19 = new JSONObject();
            jsonObject19.put("fieldName","所属行业(大类)");
            jsonObject19.put("fieldValue","");
            inputRecord.add(jsonObject19);

            JSONObject jsonObject20 = new JSONObject();
            jsonObject20.put("fieldName","境内/境外");
            jsonObject20.put("fieldValue","");
            inputRecord.add(jsonObject20);

            String aqcStr= "";
            if(ObjectUtil.isNotEmpty(authenticationScreen.getCompanyName())) {
                stopWatch.start("请求大数据爬虫接口");
                aqcStr = getAqcStr(authenticationScreen.getCompanyName());
                stopWatch.stop();
            }
            String gd1 = "";
            String gd2 = "";
            if (StringUtils.isNotBlank(aqcStr)) {
                String[] aqcStrArray = aqcStr.split("\\,");
                if (aqcStrArray.length > 1) {
                    gd1 = aqcStrArray[0];
                    gd2 = aqcStrArray[1];
                } else {
                    gd1 = aqcStrArray[0];
                }
            }
            JSONObject jsonObject21 = new JSONObject();
            jsonObject21.put("fieldName","股东姓名 1");
            jsonObject21.put("fieldValue",formateFieldValue(gd1));
            inputRecord.add(jsonObject21);

            JSONObject jsonObject22 = new JSONObject();
            jsonObject22.put("fieldName","股东国籍 1");
            jsonObject22.put("fieldValue","");
            inputRecord.add(jsonObject22);

            JSONObject jsonObject23 = new JSONObject();
            jsonObject23.put("fieldName","股东姓名 2");
            jsonObject23.put("fieldValue",formateFieldValue(gd2));
            inputRecord.add(jsonObject23);

            JSONObject jsonObject24 = new JSONObject();
            jsonObject24.put("fieldName","股东国籍 2");
            jsonObject24.put("fieldValue","");
            inputRecord.add(jsonObject24);

            jsonObject.put("inputRecord",inputRecord);

            JSONObject configObject = new JSONObject();
            configObject.put("storeInput","Y");
            configObject.put("responseType","SIMPLE");
            configObject.put("entityDetails","Y");

            jsonObject.put("config",configObject);

            log.info("供应商风险信息GSCP请求参数:"+JSONObject.toJSONString(jsonObject));
            stopWatch.start("请求GSCP接口");
            JSONObject responseSrc = restTemplate.postForObject(StringUtils.joinWith("", ipaasApi, ipaasImportScreening, "?access_token=", accessToken), jsonObject, JSONObject.class);
            stopWatch.stop();

            JSONArray subCaseDetails = responseSrc.getJSONArray("subCaseDetails");
            // 解析返回内容时只需要解析subCaseDetails 中 subcase 为GWL子案例状态
            if(!Objects.isNull(subCaseDetails) && !subCaseDetails.isEmpty()) {
                for (int i = 0; i < subCaseDetails.size(); i++) {
                    String subcase = subCaseDetails.getJSONObject(i).get("subcase").toString();
                    String status = subCaseDetails.getJSONObject(i).get("status").toString();
                    if("GWL".equals(subcase)) {
                        gwlStatus = status;
                    }
                    break;
                }
            }
        }else{
            throw new BaseException("调用鉴权接口获取令牌失败"+tokerMap.get("code"));
        }
        log.info(stopWatch.prettyPrint());
        return gwlStatus;
    }

    private InterfaceLogDTO createInterfaceLogDTO(JSONObject jsonObject,Date beginDate,Date endDate,String returnInfo,Long dealTime,Long billId,String status,String errMsg) {
        InterfaceLogDTO logDTO = new InterfaceLogDTO(ApiInfoEnum.IMPORT_SCREENING,JsonUtil.entityToJsonStr(jsonObject));
        logDTO.setCreationDateBegin(beginDate);
        logDTO.setCreationDateEnd(endDate);
        logDTO.setReturnInfo(returnInfo);
        logDTO.setDealTime(dealTime);
        logDTO.setBillId(String.valueOf(billId));
        logDTO.setStatus(status);
        logDTO.setErrorInfo(errMsg);
        return logDTO;
    }

    public String getAqcStr(String companyName){
        String str = "";
        int maxSize = 2;
        CompletableFuture<String> future = CompletableFuture.supplyAsync(()->{
            String str1 = "";
/*            List<CompanyAQCApiDTO> companyAQCApiDTOS = Arrays.asList(makeCompanyAQCApiDTO());*/
            List<CompanyAQCApiDTO> companyAQCApiDTOS = companyBigDataService.findAqcByNames(Collections.singletonList(companyName));
            if(ObjectUtil.isNotEmpty(companyAQCApiDTOS)){
                CompanyAQCApiDTO.ItemData data = companyAQCApiDTOS.get(0).getData();
                if(ObjectUtil.isNotEmpty(data)){
                    List<String> shareholdersData = data.getShareholdersData();
                    if(CollUtil.isNotEmpty(shareholdersData)){
                        if(shareholdersData.size()>maxSize){
                            shareholdersData = shareholdersData.subList(0,maxSize);
                        }
                        str1 = String.join(",", shareholdersData);
                    }
                }
            }
            return str1;
        });
        try {
            str = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return str;
    }

    private CompanyAQCApiDTO makeCompanyAQCApiDTO(){
        CompanyAQCApiDTO companyAQCApiDTO = new CompanyAQCApiDTO();
        companyAQCApiDTO.setCompany("广东世纪达投资集团有限公司");
        CompanyAQCApiDTO.ItemData data =  new CompanyAQCApiDTO.ItemData();
        data.setShareholdersData(Arrays.asList("广东世纪达投资集团有限公司", "韦细华", "麦志基", "区荣大"));
        companyAQCApiDTO.setData(data);
        return companyAQCApiDTO;
    }

}
