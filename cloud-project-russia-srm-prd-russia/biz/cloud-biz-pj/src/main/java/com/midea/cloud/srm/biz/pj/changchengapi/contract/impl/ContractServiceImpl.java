package com.midea.cloud.srm.biz.pj.changchengapi.contract.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gwm.open.sdk.OpenClient;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.biz.pj.changchengapi.contract.ContractService;
import com.midea.cloud.srm.biz.pj.common.PjInterfaceLogUtils;
import com.midea.cloud.srm.model.common.enums.ProcessStatusEnum;
import com.midea.cloud.srm.model.pj.api.interfacelog.enums.ApiInfoEnum;
import com.midea.cloud.srm.model.pj.contract.dto.CreateContractReturnDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huangbf3
 */
@Slf4j
@Service
public class ContractServiceImpl implements ContractService {

    @Value("${gwm.url.createContract-url}")
    private String createContractUrl;

    @Value("${gwm.appkey}")
    private String appkey;

    @Value("${gwm.secret}")
    private String secret;

    @ApiModelProperty("源系统应用")
    @Value("${gwm.bpm.src-system}")
    private String srcSystem;

    @Value("${gwm.bpm.api-username}")
    private String apUsername;


    @Override
    public CreateContractReturnDTO createContract(JSONObject requestJsn) {
        String msg = ProcessStatusEnum.COMPLETED.getName();
        try {
            log.info("======进入调长城开放平台创建已签署合同接口======="+requestJsn.toString());
            Map<String,String> headers = new HashMap<>(50);
            headers.put("SRC-SYSTEM",srcSystem);
            headers.put("USERID",apUsername);
            OpenClient openClient = new OpenClient(appkey,secret);
            String result = openClient.sendHttpPost(createContractUrl,requestJsn.toString(),"application/json",headers);
            msg = result;
            log.info("======进入调长城开放平台创建已签署合同接口请求参数:"+requestJsn.toString());
            log.info("======进入调长城开放平台创建已签署合同接口返回结果:"+result);
            CreateContractReturnDTO createContractResult = JSONObject.parseObject(result,CreateContractReturnDTO.class);
            return createContractResult;
        } catch (Exception e) {
            log.error("createContract Exception", e);
            msg = "接口异常" + e.getMessage();
            throw new BaseException(e.getMessage());
        } finally {
            PjInterfaceLogUtils.sendInterfaceLog(ApiInfoEnum.CONTRAC_FILING, JSON.toJSONString(requestJsn), msg);
        }

    }
}
