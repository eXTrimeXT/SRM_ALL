package com.midea.cloud.srm.mall.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.mall.common.AccessTokenManager;
import com.midea.cloud.srm.mall.config.UriPropertiesConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SrmHttpRequestUtil {

    @Autowired
    private RestTemplate srmRestTemplate;
    @Autowired
    private AccessTokenManager accessTokenManager;
    @Autowired
    private UriPropertiesConfiguration uriPropertiesConfiguration;
    @SuppressWarnings("unchecked")
    public <T, R> R postEntity(String url, T params,  String contentType, Class<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", StringUtils.defaultString(contentType,
                MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        // 目前京东api的Content-Type都是application/x-www-form-urlencoded, 统一转MultiValueMap才能传参过去
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (params instanceof MultiValueMap) {
            body = (MultiValueMap<String, Object>) params;
        } else if (params instanceof Map) {
            // Map转为MultiValueMap
            body.setAll((Map) params);
        } else {
            // 兼容上游直接传pojo，要将其转为Map，再转MultiValueMap传过去
            body.setAll(convertPojo2Map(params));
        }
        if (!url.equals(uriPropertiesConfiguration.getAccessTokenUrl()) && !url.equals(uriPropertiesConfiguration.getRefreshTokenUrl())) {
            //拼接token参数
            body.setAll(Collections.singletonMap("token", accessTokenManager.getAccessToken()));
        }
        log.info("requestBody is：{}", body);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Object> responseEntity = srmRestTemplate.postForEntity(url, httpEntity, Object.class);
        Object objectBody=responseEntity.getBody();
        R r=JSONObject.parseObject(objectBody.toString(),responseType);
        HttpStatus statusCode = responseEntity.getStatusCode();
        if (HttpStatus.OK.equals(statusCode)) {
            return r;
        }

        String reasonPhrase = responseEntity.getStatusCode().getReasonPhrase();
        log.error("statusCode != 200 url={} returnCode={} reasonPhrase={}",
                url, statusCode.value(), reasonPhrase);
        throw new RuntimeException("调用http接口返回非200状态码" + reasonPhrase);
    }

    // content-type为MediaType.APPLICATION_JSON的请求，调此方法
    public <T, R> R postEntityForJson(String url, T body, Class<R> responseType) {
        HttpHeaders headers = new HttpHeaders();
        // application/json
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<T> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<R> responseEntity = srmRestTemplate.postForEntity(url, httpEntity, responseType);
        HttpStatus statusCode = responseEntity.getStatusCode();
        if (HttpStatus.OK.equals(statusCode)) {
            return responseEntity.getBody();
        }

        String reasonPhrase = responseEntity.getStatusCode().getReasonPhrase();
        log.error("statusCode != 200 url={} returnCode={} reasonPhrase={}", url, statusCode.value(), reasonPhrase);
        throw new RuntimeException("postEntityForJson调用http接口返回非200状态码" + reasonPhrase);
    }

    public <T, R> R getEntity(String url, Class<R> responseType) {
        ResponseEntity<R> responseEntity = srmRestTemplate.getForEntity(url, responseType);
        if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            return responseEntity.getBody();
        }
        String reasonPhrase = responseEntity.getStatusCode().getReasonPhrase();
        log.error("statusCode != 200 url={} code={} reasonPhrase={}",
                url, responseEntity.getStatusCode().value(), reasonPhrase);
        throw new RuntimeException("调用http接口返回非200状态码" + reasonPhrase);
    }

    private <T> Map<String, Object> convertPojo2Map(T params) {
        String jsonStr = JSON.toJSONString(params);
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        return new HashMap<>(jsonObject);
    }

}
