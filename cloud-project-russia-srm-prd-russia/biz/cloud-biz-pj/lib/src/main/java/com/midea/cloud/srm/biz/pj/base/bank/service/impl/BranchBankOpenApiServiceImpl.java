package com.midea.cloud.srm.biz.pj.base.bank.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.gson.Gson;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.enums.api.ResultStatus;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.api.interfacelog.service.IInterfaceLogService;
import com.midea.cloud.srm.biz.pj.base.bank.dto.BankOpenPushDto;
import com.midea.cloud.srm.biz.pj.base.bank.dto.BankOpenReturnDto;
import com.midea.cloud.srm.biz.pj.base.bank.service.BranchBankOpenApiService;
import com.midea.cloud.srm.model.base.organization.entity.ErpBranchBank;
import com.midea.cloud.srm.model.pj.api.interfacelog.dto.InterfaceLogDTO;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.common.CommonResponseDto;
import com.midea.cloud.srm.model.pj.siss.dto.SunHonestyReturnDto;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.json.Json;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author GW00311146
 */
@Slf4j
@Service
public class BranchBankOpenApiServiceImpl implements BranchBankOpenApiService {

    public static final String STRING_ZERO = "0";
    public static final String STRING_0000 = "0000";
    @Value("${gwm.prdAppkey}")
    private String appKey;
    @Value("${gwm.prdSecret}")
    private String secret;
    @Value("${gwm.url.branchBankSyncFromOpen-url}")
    private String branchBankSyncFromOpenUrl;

    @Resource
    private IInterfaceLogService interfaceLogService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public List<ErpBranchBank> findBankInfoFromOpen() {


        BankOpenPushDto bankOpenPushDto = new BankOpenPushDto();
        BankOpenPushDto.Cmscloud cmscloud = new BankOpenPushDto.Cmscloud();
        BankOpenPushDto.Cmscloud.Head head = new BankOpenPushDto.Cmscloud.Head();
        BankOpenPushDto.Cmscloud.Body body = new BankOpenPushDto.Cmscloud.Body();
        BankOpenPushDto.Cmscloud.Body.Data data = new BankOpenPushDto.Cmscloud.Body.Data();
        BankOpenPushDto.Cmscloud.Body.Data.ParamSet paramSet = new BankOpenPushDto.Cmscloud.Body.Data.ParamSet();


        head.setTenantId("1");
        head.setTimestamp(String.valueOf(System.currentTimeMillis()));
        head.setSystemNo("srm");
        head.setSystemName("长城慧采云");
        head.setMesgNo("BASE-0117");

        body.setSign("数据签名");

        //开始时间 增量条件 前一天
        paramSet.setStartTime(LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //查询下页标识	第一次查询传1，后面查询时传响应结果中的NextTag，一直到NextTag返回0则代表数据全部返回，每页返回50条数据
        String nextTag = "1";

        //结果最终List
        List<ErpBranchBank> erpBranchBankList = new ArrayList<>();

        while (!STRING_ZERO.equals(nextTag)) {
            paramSet.setNextTag(nextTag);
            data.setParamSet(paramSet);
            body.setData(data);

            int randomNumber = ThreadLocalRandom.current().nextInt(100, 1000);
            head.setRequestNo(LocalDateTime.now().format(FORMATTER) + randomNumber);

            cmscloud.setHead(head);
            cmscloud.setBody(body);
            bankOpenPushDto.setCmscloud(cmscloud);

            String gsonStr = new Gson().toJson(bankOpenPushDto);
            JSONObject pushJson = JSONObject.parseObject(gsonStr);

            InterfaceLogDTO interfaceLog = new InterfaceLogDTO(ApiInfoEnum.BRANCH_BANK_SYN);
            String result = null;
            try {
                OpenClient openClient = new OpenClient(appKey,secret);
                log.info("入参内容======" + pushJson);
                interfaceLog.setServiceInfo(JSONObject.toJSONString(pushJson));
                result = openClient.sendHttpPost(branchBankSyncFromOpenUrl, pushJson.toString(), "application/json");
                log.info("返回内容======" + result);
                interfaceLog.setReturnInfo(result);

            } catch (Exception e) {
                log.info(ApiInfoEnum.BRANCH_BANK_SYN.getServiceName() + "报错:" + e.getMessage());
                interfaceLog.setStatus(ResultStatus.FAIL.toString());
                interfaceLog.setErrorInfo(e.getMessage());
            }finally {
                interfaceLog.setReturnInfo(result);
                interfaceLogService.createInterfaceLog(interfaceLog);
            }

            BankOpenReturnDto returnDto = JSONObject.parseObject(result, BankOpenReturnDto.class);
            assert returnDto != null;
            if (!STRING_0000.equals(returnDto.getCmscloud().getBody().getData().getResultCode())) {
                throw new BaseException(returnDto.getCmscloud().getBody().getData().getResultMsg());
            } else {
                nextTag = returnDto.getCmscloud().getBody().getData().getNextTag();
                returnDto.getCmscloud().getBody().getData().getResultSet().stream()
                        .filter(resultSet -> !resultSet.getCnapsCode().isEmpty())
                        .forEach(r -> {
                            ErpBranchBank erpBranchBank = new ErpBranchBank();
                            erpBranchBank.setBankNum(r.getCnapsCode().substring(0, 3));
                            erpBranchBank.setBankName(r.getDirectName());
                            erpBranchBank.setBranchBankNum(r.getCnapsCode());
                            erpBranchBank.setBranchBankName(r.getBankName());
                            erpBranchBank.setAttr1("Y");

                            erpBranchBankList.add(erpBranchBank);
                        });
            }
        }
        return erpBranchBankList;
    }

}
