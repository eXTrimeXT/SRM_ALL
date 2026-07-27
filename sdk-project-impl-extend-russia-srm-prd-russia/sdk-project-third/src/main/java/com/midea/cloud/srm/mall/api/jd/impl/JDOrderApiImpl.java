package com.midea.cloud.srm.mall.api.jd.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.mall.api.OrderApi;
import com.midea.cloud.srm.mall.common.ResultCode;
import com.midea.cloud.srm.mall.config.UriPropertiesConfiguration;
import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import com.midea.cloud.srm.mall.request.jd.order.*;
import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.MallResult;
import com.midea.cloud.srm.mall.result.jd.Order.*;
import com.midea.cloud.srm.mall.result.jd.common.DelMessageResultDTO;
import com.midea.cloud.srm.mall.result.jd.common.GetMessageResultDTO;
import com.midea.cloud.srm.mall.utils.ResultUtils;
import com.midea.cloud.srm.mall.utils.SrmHttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 京东商城订单实现处理
 */
@Service("JDOrderServiceInstance")
@Slf4j
public class JDOrderApiImpl implements OrderApi {
    @Autowired
    private UriPropertiesConfiguration uriPropertiesConfiguration;

    @Autowired
    private SrmHttpRequestUtil srmHttpUtil;

    @Override
    public MallResult<ResultCode, CommonResultDTO> submitOrder(BaseRequestDTO baseRequestDTO) {
        JDOrderSubmitRequestDTO orderSubmitRequestDTO = (JDOrderSubmitRequestDTO) baseRequestDTO;
        try {
            OrderSubmitResultDTO orderSubmitResultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getSubmitOrder(), orderSubmitRequestDTO,
                    "application/x-www-form-urlencoded", OrderSubmitResultDTO.class);

            return ResultUtils.buildResult(orderSubmitResultDTO.getResultCode(), orderSubmitResultDTO);
        } catch (Exception e) {
            log.error("订单提交接口异常【京东】",e);
            throw new BaseException("订单提交接口异常【京东】");
        }
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> queryOrderDetail(BaseRequestDTO baseRequestDTO) {
        JDOrderDetailRequestDTO jdOrderDetailRequestDTO = (JDOrderDetailRequestDTO) baseRequestDTO;
        Map params = JSONObject.parseObject(JSON.toJSONString(jdOrderDetailRequestDTO), Map.class);
        OrderDetailResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getSelectJdOrderUrl(), params,
                "application/x-www-form-urlencoded", OrderDetailResultDTO.class);
        return ResultUtils.buildResult(resultDTO.getResultCode(), resultDTO);
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> queryOrderTrack(BaseRequestDTO baseRequestDTO) {
            JDDeliveryRequestDTO jdDeliveryRequestDTO = (JDDeliveryRequestDTO) baseRequestDTO;
        Map params = JSONObject.parseObject(JSON.toJSONString(jdDeliveryRequestDTO), Map.class);
        DeliveryResultDTO deliveryResultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getOrderTrackUrl(), params,
                    "application/x-www-form-urlencoded", DeliveryResultDTO.class);
            return ResultUtils.buildResult(deliveryResultDTO.getResultCode(), deliveryResultDTO);
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> confirmReceived(BaseRequestDTO baseRequestDTO) {
        try {
            ReceiveConfirmRequestDTO receiveConfirmRequestDTO = (ReceiveConfirmRequestDTO) baseRequestDTO;
            ReceiveConfirmResultDTO confirmResultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getConfirmReceivedUrl(), receiveConfirmRequestDTO,
                    "application/x-www-form-urlencoded", ReceiveConfirmResultDTO.class);
            return ResultUtils.buildResult(confirmResultDTO.getResultCode(), confirmResultDTO);
        } catch (Exception e) {
            log.error("订单确认异常【京东】", e);
            throw new BaseException("订单确认异常【京东】");
        }
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> batchConfirmReceived(BaseRequestDTO baseRequestDTO) {
        try {
            BatchReceiveConfirmRequestDTO receiveConfirmRequestDTO = (BatchReceiveConfirmRequestDTO) baseRequestDTO;
            BatchReceiveConfirmResultDTO confirmResultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getBatchConfirmReceivedUrl(), receiveConfirmRequestDTO,
                    "application/x-www-form-urlencoded", BatchReceiveConfirmResultDTO.class);
            return ResultUtils.buildResult(confirmResultDTO.getResultCode(), confirmResultDTO);
        } catch (Exception e) {
            log.error("订单批量确认异常【京东】",e);
            throw new BaseException("订单批量确认异常【京东】");
        }
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> getPromiseTips(BaseRequestDTO baseRequestDTO) {
        try {
            JDPromiseTisRequestDTO promiseTisRequestDTO = (JDPromiseTisRequestDTO) baseRequestDTO;
            PromiseTisResultDTO promiseTisResultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getBatchConfirmReceivedUrl(), promiseTisRequestDTO,
                    "application/x-www-form-urlencoded", PromiseTisResultDTO.class);
            return ResultUtils.buildResult(promiseTisResultDTO.getResultCode(), promiseTisResultDTO);
        } catch (Exception e) {
            log.error("查询配送预计送达时间异常【京东】",e);
            throw new BaseException("查询配送预计送达时间异常【京东】");
        }
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> getMessage(BaseRequestDTO baseRequestDTO) {
        GetMessageRequestDTO getMessageRequestDTO = (GetMessageRequestDTO) baseRequestDTO;
        Map params = JSONObject.parseObject(JSON.toJSONString(getMessageRequestDTO), Map.class);
        GetMessageResultDTO getMessageResultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getGetMessageUrl(), params,
                "application/x-www-form-urlencoded", GetMessageResultDTO.class);
        return ResultUtils.buildResult(getMessageResultDTO.getResultCode(), getMessageResultDTO);
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> delMessage(BaseRequestDTO baseRequestDTO) {
        DelMessageResultDTO delMessageResultDTO = new DelMessageResultDTO();
        delMessageResultDTO.setSuccess(true);
        delMessageResultDTO.setResult(true);
        DelMessageRequestDTO delMessageRequestDTO = (DelMessageRequestDTO) baseRequestDTO;
        Assert.isTrue(ObjectUtil.isNotEmpty(delMessageRequestDTO.getId()), "入参不能为空");
        //由于最大只支持100个
        List<String> subOrderIds = Arrays.asList(delMessageRequestDTO.getId().split(","));
        int batchSize = 100;
        for (int i = 0; i < subOrderIds.size(); i += batchSize) {
            List<String> batch = subOrderIds.subList(i, Math.min(i + batchSize, subOrderIds.size()));
            DelMessageRequestDTO batchDelMessageRequestDTO = new DelMessageRequestDTO();
            batchDelMessageRequestDTO.setMallType(delMessageRequestDTO.getMallType());
            batchDelMessageRequestDTO.setId(String.join(",", batch));
            Map params = JSONObject.parseObject(JSON.toJSONString(batchDelMessageRequestDTO), Map.class);
            DelMessageResultDTO batchDelMessageResultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getDelMessageUrl(), params,
                    "application/x-www-form-urlencoded", DelMessageResultDTO.class);
            if (!batchDelMessageResultDTO.isSuccess()) {
                delMessageResultDTO.setSuccess(false);
                delMessageResultDTO.setResultMessage(delMessageResultDTO.getResultMessage() + batchDelMessageResultDTO.getResultMessage() + "(" + batchDelMessageRequestDTO.getId() + ");");
                delMessageResultDTO.setResult(false);
                log.error("批量删除推送消息失败,消息体" + batchDelMessageRequestDTO.getId());
                log.error("批量删除推送消息失败：" + batchDelMessageResultDTO.getResultMessage());
            }
        }
        return ResultUtils.buildResult(delMessageResultDTO.getResultCode(), delMessageResultDTO);
    }

    @Override
    public MallResult<ResultCode, CommonResultDTO> saveOrUpdatePoNo(BaseRequestDTO baseRequestDTO) {
        UpdatePoNoRequestDTO getMessageRequestDTO = (UpdatePoNoRequestDTO) baseRequestDTO;
        Map params = JSONObject.parseObject(JSON.toJSONString(getMessageRequestDTO), Map.class);
        UpdatePoNoResultDTO resultDTO = srmHttpUtil.postEntity(uriPropertiesConfiguration.getSaveOrUpdatePoNoUrl(), params,
                "application/x-www-form-urlencoded", UpdatePoNoResultDTO.class);
        return ResultUtils.buildResult(resultDTO.getResultCode(), resultDTO);
    }
}
