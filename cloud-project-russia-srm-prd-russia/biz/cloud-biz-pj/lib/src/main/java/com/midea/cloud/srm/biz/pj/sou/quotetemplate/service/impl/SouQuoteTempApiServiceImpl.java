package com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.page.PageMethod;
import com.midea.cloud.common.constants.OAuthConstant;
import com.midea.cloud.common.constants.RedisKey;
import com.midea.cloud.common.result.ResultCode;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.common.utils.JsonUtil;
import com.midea.cloud.component.aop.lock.SyncLock;
import com.midea.cloud.component.context.container.SpringContextHolder;
import com.midea.cloud.component.context.i18n.LocaleHandler;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.dao.SouQuoteTempApiRepositoryImpl;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.judge.SouQuoteTempApiJudge;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.service.ISouQuoteTempApiService;
import com.midea.cloud.srm.biz.pj.sou.quotetemplate.validator.api.SouQuoteTempApiValidator;
import com.midea.cloud.srm.model.bid.quotetemplate.client.SouQuoteTempApiClient;
import com.midea.cloud.srm.model.bid.quotetemplate.client.SouQuoteTempApiClientDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempApiEditDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempApiQueryDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempApi;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempApiStatusEnum;
import com.midea.cloud.srm.model.bid.quotetemplate.enums.SouQuoteTempApiTypeEnum;
import com.midea.cloud.srm.model.common.enums.Enable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

/**
 * 寻源-报价模板-api列表
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/08/17
 */
@Service
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
public class SouQuoteTempApiServiceImpl implements ISouQuoteTempApiService {

    @Autowired
    private SouQuoteTempApiJudge souQuoteTempApiJudge;
    @Autowired
    private SouQuoteTempApiRepositoryImpl souQuoteTempApiRepository;
    @Autowired
    private SouQuoteTempApiValidator souQuoteTempApiValidator;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 采购商端: 列表查询api
     */
    @Override
    public List<SouQuoteTempApi> listApis(SouQuoteTempApiQueryDTO queryParams) {
        // 1: 入参格式化
        queryParams.formatParams();
        // 2: 数据查询
        if (queryParams.getPageNum() != null && queryParams.getPageSize() != null) {
            PageMethod.startPage(queryParams.getPageNum(), queryParams.getPageSize());
        }
        return souQuoteTempApiRepository.lambdaQuery()
                // api名称
                .like(queryParams.getApiName() != null, SouQuoteTempApi::getApiName, queryParams.getApiName())
                // api状态
                .eq(queryParams.getApiStatus() != null, SouQuoteTempApi::getApiStatus, queryParams.getApiStatus())
                .orderByDesc(SouQuoteTempApi::getApiId)
                .list();
    }

    /**
     * api列表查询
     * @param apiId {@link SouQuoteTempApi#getApiId}
     */
    @Override
    public SouQuoteTempApi getApi(long apiId) {
        // 1: 数据查询
        return souQuoteTempApiRepository.getById(apiId);
    }

    /**
     * 编辑/提交api
     * @param param api信息
     * @param isTempSave true-暂存/false-提交
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @SyncLock(
            moduleName = RedisKey.SOU.QuoteTemp.SYNC_LOCK_BUYER,
            keyBySpel = "#param.api.apiId",
            condition = "#param.api != null and #param.api.apiId != null ? true : false")
    public long/* apiId */ editApi(SouQuoteTempApiEditDTO param, boolean isTempSave) {
        AssertUtils.notNull(param.getApi(), "缺少api信息");
        // 1: 校验操作条件/权限
        souQuoteTempApiJudge.judgeEditApiAuth(param.getApi().getApiId());
        // 2: 入参校验及数据转换
        SouQuoteTempApi entity = souQuoteTempApiValidator.formatValidateAndConvert(param.getApi(), param.isTempSave());
        // 3: 保存数据
        souQuoteTempApiRepository.saveOrUpdate(entity);

        return entity.getApiId();
    }

    /**
     * 测试api
     */
    @Override
    public Object executeApi(long apiId, Map<String/* argName */, Object> params, boolean needValid) {
        // 1: 校验操作条件/权限
        SouQuoteTempApi api = souQuoteTempApiRepository.getById(apiId);
        AssertUtils.notNull(api, LocaleHandler.getLocaleMsg("自定义函数[{0}]不存在"), apiId);
        AssertUtils.isTrue(SouQuoteTempApiStatusEnum.VALID.equals(api.getApiStatus()), LocaleHandler.getLocaleMsg("自定义函数[{0}]不是生效状态"), api.getApiName());
        // 2: 入参处理及执行
        return this.executeApi(api, params, needValid);
    }

    /**
     * 执行API
     */
    @Override
    public Object executeApi(String apiName, Map<String/* argName */, Object> params, boolean needValid) {
        // 1: 查询数据
        SouQuoteTempApi api = souQuoteTempApiRepository.lambdaQuery()
                .eq(SouQuoteTempApi::getApiName, apiName)
                .one();
        AssertUtils.notNull(api, LocaleHandler.getLocaleMsg("自定义函数[{0}]不存在"), apiName);
        AssertUtils.isTrue(SouQuoteTempApiStatusEnum.VALID.equals(api.getApiStatus()), LocaleHandler.getLocaleMsg("自定义函数[{0}]不是生效状态"), api.getApiName());
        // 2: 入参处理及执行
        return this.executeApi(api, params, needValid);
    }

    private Object executeApi(SouQuoteTempApi api, Map<String/* argName */, Object> params, boolean needValid) {
        // 1: 数据校验与转化
        Map<String/* argName */, Object> apiData;
        if (needValid) {
            if (api.getApiDetails() != null) {
                apiData = new HashMap<>(api.getApiDetails().size());
                api.getApiDetails().forEach(detail -> {
                    Object param = params.get(detail.getArgName());
                    // 是否必填
                    if (param == null) {
                        AssertUtils.isFalse(Enable.Y.equals(detail.getRequired()), LocaleHandler.getLocaleMsg("参数[{0}]必填"), detail.getArgName());
                    }
                    apiData.put(detail.getArgName(), param);
                });
            } else {
                apiData = Collections.emptyMap();
            }
        } else {
            apiData = new HashMap<>(params);
        }
        // 2: 访问处理
        final Object result;
        if (SouQuoteTempApiTypeEnum.URL.equals(api.getApiType())) {
            // URL访问
            result = this.postHttp(api.getApiUrl(), apiData);
        } else if (SouQuoteTempApiTypeEnum.SERVICE.equals(api.getApiType())) {
            // Service访问
            try {
                Class clazz = Class.forName(api.getApiClient());
                SouQuoteTempApiClient client = (SouQuoteTempApiClient) SpringContextHolder.getApplicationContext().getBean(clazz);
                SouQuoteTempApiClientDTO param = new SouQuoteTempApiClientDTO();
                {
                    param.setServiceBean(api.getApiService());
                    param.setParam(params);
                }
                result = client.apiCallback(param);
            } catch (ClassNotFoundException | BeansException e) {
                throw new IllegalArgumentException("报价模板API定义错误: 找不到" + api.getApiClient());
            }
        } else {
            throw new IllegalArgumentException("报价模板API定义错误: 暂不支持的API类型" + api.getApiType());
        }
        if (result == null) {
            return BigDecimal.ZERO;
        } else if (result instanceof BigDecimal) {
            return result;
        } else if (result instanceof Collection) {
            List<BigDecimal> list = new ArrayList<>(((Collection<?>)result).size());
            ((Collection<?>) result).forEach(e -> {
                if (e instanceof BigDecimal) {
                    list.add((BigDecimal) e);
                } else {
                    try {
                        list.add(new BigDecimal(e.toString()));
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("api计算返回值非法:" + JSON.toJSONString(result));
                    }
                }
            });
            return list;
        } else {
            try {
                return new BigDecimal(result.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("api计算返回值非法:" + JSON.toJSONString(result));
            }
        }
    }

    @Nullable
    private BigDecimal postHttp(String url, Map<String/* argName */, Object> apiData) {
        HttpHeaders requestHeaders = new HttpHeaders();
        // body
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication instanceof OAuth2Authentication) {
                OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
                String accessToken = details.getTokenValue();
                requestHeaders.add(OAuthConstant.AUTHORIZATION, OAuth2AccessToken.BEARER_TYPE + " " + accessToken);
            }
        }
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(apiData, requestHeaders);
        String result = restTemplate.postForObject(url, requestEntity, String.class);
        Map<String, Object> map = JsonUtil.parseJsonStrToMap(result);
        String codeText = "code";
        if (!(map.get(codeText).equals(ResultCode.SUCCESS.getCode()))) {
            throw new IllegalArgumentException("接口访问报错:" + JSON.toJSONString(map));
        }
        Object obj = map.get("data");
        if (obj == null) { return null; }
        if (obj instanceof JSONObject || obj instanceof JSONArray) {
            throw new IllegalArgumentException("接口返回的数据格式错误，不是单一的数字:" + obj);
        } else {
            try {
                return new BigDecimal(obj.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("接口返回的数据格式错误，不是数字:" + obj);
            }
        }
    }

}
