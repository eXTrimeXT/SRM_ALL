package com.midea.cloud.srm.mall.api.jd.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.mall.api.AfterSaleApi;
import com.midea.cloud.srm.mall.common.ResultCode;
import com.midea.cloud.srm.mall.config.UriPropertiesConfiguration;
import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.MallResult;
import com.midea.cloud.srm.mall.result.jd.afs.AfsApplyResultDTO;
import com.midea.cloud.srm.mall.utils.ResultUtils;
import com.midea.cloud.srm.mall.utils.SrmHttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 京东商城售后实现处理
 */
@Service("JDAfterSaleServiceInstance")
@Slf4j
public class JDAfterSaleApiImpl implements AfterSaleApi {
    @Autowired
    private UriPropertiesConfiguration uriPropertiesConfiguration;

    @Autowired
    private SrmHttpRequestUtil srmHttpUtil;


    @Override
    public MallResult<ResultCode, CommonResultDTO> createAfsApply(BaseRequestDTO baseRequestDTO) {
        Map params = JSONObject.parseObject(JSON.toJSONString(baseRequestDTO), Map.class);
        AfsApplyResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getCreateAfsApplyUrl(), params, "application/x-www-form-urlencoded", AfsApplyResultDTO.class);
        return ResultUtils.buildResult(resultDTO.getResultCode(), resultDTO);
    }
}
