package com.midea.cloud.srm.mall.api.jd.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.mall.api.AddressApi;
import com.midea.cloud.srm.mall.common.ResultCode;
import com.midea.cloud.srm.mall.config.UriPropertiesConfiguration;
import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.MallResult;
import com.midea.cloud.srm.mall.result.jd.common.AddressResultDTO;
import com.midea.cloud.srm.mall.utils.ResultUtils;
import com.midea.cloud.srm.mall.utils.SrmHttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 京东商城地址实现处理
 */
@Service("JDAddressServiceInstance")
@Slf4j
public class JDAddressApiImpl implements AddressApi {
    @Autowired
    private UriPropertiesConfiguration uriPropertiesConfiguration;
    @Autowired
    private SrmHttpRequestUtil srmHttpUtil;
    @Override
    public MallResult<ResultCode, CommonResultDTO> getAddressFromAddress(BaseRequestDTO baseRequestDTO) {
        Map params = JSONObject.parseObject(JSON.toJSONString(baseRequestDTO), Map.class);
        AddressResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getGetJDAddressFromAddressUrl(), params, "application/x-www-form-urlencoded", AddressResultDTO.class);
        return ResultUtils.buildResult(resultDTO.getResultCode(), resultDTO);
    }



}
