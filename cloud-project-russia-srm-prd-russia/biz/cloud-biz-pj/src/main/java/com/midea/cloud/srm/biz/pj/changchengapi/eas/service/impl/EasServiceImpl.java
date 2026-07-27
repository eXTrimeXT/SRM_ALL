package com.midea.cloud.srm.biz.pj.changchengapi.eas.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.dto.LoginResultDto;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.dto.WsContext;
import com.midea.cloud.srm.biz.pj.changchengapi.eas.service.EasService;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.message.SOAPHeaderElement;
import org.springframework.beans.factory.annotation.Value;

import javax.xml.namespace.QName;

/**
 * @author huangbf3
 */
@Slf4j
@org.springframework.stereotype.Service
public class EasServiceImpl implements EasService {
    @Value("${eas.login.user}")
    private String user;
    @Value("${eas.login.password}")
    private String password;
    @Value("${eas.login.slnName}")
    private String slnName;
    @Value("${eas.login.dcName}")
    private String dcName;
    @Value("${eas.login.language}")
    private String language;
    @Value("${eas.login.dbType}")
    private Integer dbType;
    @Value("${eas.login.loginUrl}")
    private String loginUrl;
    @Value("${eas.targetEndpointAddress}")
    private String targetEndpointAddress;
    @Value("${eas.namespace}")
    private String namespace;


    @Override
    public Call getOrgCall(LoginResultDto loginResultDto) {
        Call call = loginResultDto.getCall();
        call.setOperationName("inOrgUnit");
        call.setTargetEndpointAddress(targetEndpointAddress);
        call.setReturnQName(new QName("", "inOrgUnitReturn"));
//调用业务接口
        call.setTimeout(Integer.valueOf(100 * 600000 * 60));
        call.setMaintainSession(true);
//设置登录返回的session在soap头 "http://login.webservice.bos.kingdee.com"是固定的
        SOAPHeaderElement header = new SOAPHeaderElement(namespace, "SessionId", loginResultDto.getWsContext().getSessionId());
        call.addHeader(header);
        return call;
    }

    @Override
    public Call getPersonCall(LoginResultDto loginResultDto) {
        Call call = loginResultDto.getCall();
        call.setOperationName("inPerson");
        call.setTargetEndpointAddress(targetEndpointAddress);
        call.setReturnQName(new QName("", "inPersonReturn"));
//调用业务接口
        call.setTimeout(Integer.valueOf(100 * 600000 * 60));
        call.setMaintainSession(true);
//设置登录返回的session在soap头 "http://login.webservice.bos.kingdee.com"是固定的
        SOAPHeaderElement header = new SOAPHeaderElement(namespace, "SessionId", loginResultDto.getWsContext().getSessionId());
        call.addHeader(header);
        return call;
    }

    @Override
    public LoginResultDto getLoginResultDto() throws Exception {
        Service s=new Service();
        Call call=(Call)s.createCall();
        call.setOperationName("login");
        call.setTargetEndpointAddress(loginUrl);
        call.setReturnType(new QName("urn:client","WSContext"));
        call.setReturnClass(WsContext.class);
        call.setReturnQName(new QName("","loginReturn"));
//超时
        OperationDesc oper = call.getOperation();
        oper.setName("login");
        WsContext wsContext = ((WsContext) call.invoke(new Object[] { user,password,slnName,dcName, language,dbType}));
        if(wsContext.getSessionId() == null){
            throw new Exception("login fail");
        }

        log.info(JSONObject.toJSONString(wsContext));

        log.info(wsContext.getSessionId());
        //清理
        call.clearOperation();

        LoginResultDto loginResultDto = new LoginResultDto().setCall(call).setWsContext(wsContext);
        return loginResultDto;
    }
}