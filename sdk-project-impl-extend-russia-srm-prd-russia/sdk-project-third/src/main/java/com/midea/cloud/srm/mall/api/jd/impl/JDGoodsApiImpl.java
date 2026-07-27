package com.midea.cloud.srm.mall.api.jd.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.srm.mall.api.GoodsApi;
import com.midea.cloud.srm.mall.common.ResultCode;
import com.midea.cloud.srm.mall.config.UriPropertiesConfiguration;
import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import com.midea.cloud.srm.mall.request.jd.goods.QuerySkuDetailRequestDTO;
import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.MallResult;
import com.midea.cloud.srm.mall.result.jd.goods.*;
import com.midea.cloud.srm.mall.utils.ResultUtils;
import com.midea.cloud.srm.mall.utils.SrmHttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 京东商城商品实现处理
 */
@Service("JDGoodsServiceInstance")
@Slf4j
public class JDGoodsApiImpl implements GoodsApi {
    @Autowired
    private UriPropertiesConfiguration uriPropertiesConfiguration;
    @Autowired
    private SrmHttpRequestUtil srmHttpUtil;

    @Override
    public MallResult<ResultCode, CommonResultDTO> querySkuDetail(BaseRequestDTO baseRequestDTO) {
        QuerySkuDetailRequestDTO querySkuDetailRequestDTO = (QuerySkuDetailRequestDTO) baseRequestDTO;
        Map params = JSONObject.parseObject(JSON.toJSONString(querySkuDetailRequestDTO), Map.class);
        log.info("京东商城请求==>params:"+params.toString());
        SkuDetailResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getProductDetailUrl(), params,
                "application/x-www-form-urlencoded", SkuDetailResultDTO.class);
        log.info("京东商城请求==>resultDTO:"+resultDTO);
        return ResultUtils.buildResult(resultDTO.getResultCode(), resultDTO);
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> querySkuImage(BaseRequestDTO baseRequestDTO) {
        Map params = JSONObject.parseObject(JSON.toJSONString(baseRequestDTO), Map.class);
        log.info("京东商城请求==>params:"+params.toString());
        SkuImgResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getSkuImageUrl(), params,
                "application/x-www-form-urlencoded", SkuImgResultDTO.class);
        log.info("京东商城请求==>resultDTO:"+resultDTO);
        return ResultUtils.buildResult(resultDTO.getResultCode(), resultDTO);
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> getProductPageNum(BaseRequestDTO baseRequestDTO) {
        Map params = JSONObject.parseObject(JSON.toJSONString(baseRequestDTO), Map.class);
        log.info("京东商城请求==>params:"+params.toString());
        ProductPageNumResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getGetPageNumUrl(), params,
                "application/x-www-form-urlencoded", ProductPageNumResultDTO.class);
        log.info("京东商城请求==>resultDTO:"+resultDTO);
        return ResultUtils.buildResult(resultDTO.getResultCode(), resultDTO);
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> querySkuByPage(BaseRequestDTO baseRequestDTO) {
        Map params = JSONObject.parseObject(JSON.toJSONString(baseRequestDTO), Map.class);
        log.info("京东商城请求==>params:"+params.toString());
        QuerySkuResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getQuerySkuUrl(), params,
                "application/x-www-form-urlencoded", QuerySkuResultDTO.class);
        log.info("京东商城请求==>resultDTO:"+resultDTO);
        return ResultUtils.buildResult(resultDTO.getResultCode(), resultDTO);
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> getSkuState(BaseRequestDTO baseRequestDTO) {
        return null;
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> getProductStockById(BaseRequestDTO baseRequestDTO) {
        Map params = JSONObject.parseObject(JSON.toJSONString(baseRequestDTO), Map.class);
        log.info("京东商城请求==>params:"+params.toString());
        ProductStockResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getGetPageNumUrl(), params,
                "application/x-www-form-urlencoded", ProductStockResultDTO.class);
        log.info("京东商城请求==>resultDTO:"+resultDTO);
        return ResultUtils.buildResult(resultDTO.getResultCode(), resultDTO);
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> totalCheckNew(BaseRequestDTO baseRequestDTO) {
        Map params = JSONObject.parseObject(JSON.toJSONString(baseRequestDTO), Map.class);
        log.info("京东商城请求==>params:"+params.toString());
        TotalCheckNewResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getTotalCheckNewUrl(), params,
                "application/x-www-form-urlencoded", TotalCheckNewResultDTO.class);
        log.info("京东商城请求==>resultDTO:"+resultDTO);
        return ResultUtils.buildResult(resultDTO.getResultCode(), resultDTO);
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> getSellPrice(BaseRequestDTO baseRequestDTO) {
        Map params = JSONObject.parseObject(JSON.toJSONString(baseRequestDTO), Map.class);
        log.info("京东商城请求==>params:" + params.toString());
        SellPriceResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getGetSellPriceUrl(), params,
                "application/x-www-form-urlencoded", SellPriceResultDTO.class);
        log.info("京东商城请求==>resultDTO:" + resultDTO);
        return ResultUtils.buildResult(resultDTO.getResultCode(), resultDTO);
    }

}
