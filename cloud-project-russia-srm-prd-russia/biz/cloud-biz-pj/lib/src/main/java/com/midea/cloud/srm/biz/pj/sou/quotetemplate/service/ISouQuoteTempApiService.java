package com.midea.cloud.srm.biz.pj.sou.quotetemplate.service;

import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempApiEditDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.dto.SouQuoteTempApiQueryDTO;
import com.midea.cloud.srm.model.bid.quotetemplate.entity.SouQuoteTempApi;

import java.util.List;
import java.util.Map;

/**
 * 寻源-报价模板-api列表
 *
 * @author zhangwk12@meicloud.com
 * @since 2022/08/17
 */
public interface ISouQuoteTempApiService {

    /**
     * 列表查询api
     * @param queryParams
     * @return
     */
    List<SouQuoteTempApi> listApis(SouQuoteTempApiQueryDTO queryParams);

    /**
     *
     * api列表查询
     * @param apiId {@link SouQuoteTempApi#getApiId}
     * @return
     */
    SouQuoteTempApi getApi(long apiId);

    /**
     *
     * 编辑/提交api
     * @param param api信息
     * @param isTempSave true-暂存/false-提交
     * @return
     */
    long/* apiId */ editApi(SouQuoteTempApiEditDTO param, boolean isTempSave);

    /**
     * 执行API
     * @param apiId
     * @param params
     * @param needValid
     * @return
     */
    Object executeApi(long apiId, Map<String/* argName */, Object> params, boolean needValid);

    /**
     * 执行API
     * @param apiName
     * @param params
     * @param needValid
     * @return
     */
    Object executeApi(String apiName, Map<String/* argName */, Object> params, boolean needValid);

}
