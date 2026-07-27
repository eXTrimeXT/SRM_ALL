package com.midea.cloud.srm.feign.pj.util;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <pre>
 *  功能名称 openApi调用的feign拦截器自动带上ClientToken
 * </pre>
 *
 * @author luxc18
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/25 14:11
 *  修改内容:
 * </pre>
 */
@Component
@Slf4j
public class OpenApiInterceptor implements RequestInterceptor {

    @Resource
    private RestTemplate restTemplate;

    private static  ClientToken clientToken = new ClientToken();


    @Value("${iam.user.client.id}")
    private String clientId;
    @Value("${iam.user.client.secret}")
    private String clientSecret;
    @Value("${iam.user.base.url}")
    private String baseUrl;
    private static  final String CLIENT_TOKEN_URL = "/iam/oauth2/token?grant_type=%s&client_id=%s&client_secret=%s";


    public static  final  String GRANT_TYPE = "client_credentials";

    private static final String USER_TOKEN_KEY = "X-Principal";

    private static final String SRM_TOKEN = "Authorization";

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final Lock wlock = rwLock.writeLock();

    private final Lock rLock = rwLock.readLock();

    private final int NUM = 1000;
    private final long NUM2 = 1500L;


    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization","Bearer "+getClientToken());
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                if (name.equalsIgnoreCase(SRM_TOKEN) ||
                        name.equalsIgnoreCase(USER_TOKEN_KEY)) {
                    continue;
                }
                requestTemplate.header(name, request.getHeader(name));
            }
        }
    }



    private String getClientToken(){
        rLock.lock();
        try {
            if(StringUtils.isEmpty(clientToken.getToken())||clientToken.getExpiresTime()<System.currentTimeMillis()/NUM){
                rLock.unlock();
                try{
                    updateClientToken();
                }finally {
                    rLock.lock();
                }
            }
            return clientToken.getToken();
        }finally {
            rLock.unlock();
        }
    }

    private void updateClientToken(){
        wlock.lock();
        try{
            String paramUrl = baseUrl + CLIENT_TOKEN_URL;
            String url = String.format(paramUrl, GRANT_TYPE, clientId, clientSecret);
            String token =  postForObjectByFormData(url,null).get("access_token").toString();
            Long expiresTime =  Long.valueOf(postForObjectByFormData(url,null).get("at_expires_at").toString())-1000;
            clientToken.setToken(token);
            clientToken.setExpiresTime(expiresTime);
        }finally {
            wlock.unlock();
        }

    }

    private Map<String, Object> postForObjectByFormData(String url, HttpEntity requestBody) {
        Map result = null;
        try {
            log.info("IDM请求URL：" + url);
            Long start = System.currentTimeMillis();
            ResponseEntity<Map> dsMap = this.restTemplate.postForEntity(new URI(url), requestBody, Map.class);
            log.info("请求Idm的Post返回值：" + dsMap);
            Long end = System.currentTimeMillis();
            Long diff = end - start;
            if (diff.compareTo(NUM2) == 1) {
                log.info("请求Idm的Post接口耗时：" + (end - start) + " ms");
            }

            if (null != dsMap) {
                result = (Map)dsMap.getBody();
            }
        } catch (RestClientException var8) {
            log.error("向IDM调用postForObjectByFormData时出现RestClientException异常:");
        } catch (URISyntaxException var9) {
            log.error("向IDM调用postForObjectByFormData时出现RestClientException异常:");
        }

        return result;
    }


    @Data
    static class ClientToken{

        private String token;

        private Long expiresTime;


    }
}
