package com.midea.cloud.srm.mall.api;

import com.midea.cloud.srm.mall.common.ResultCode;
import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.MallResult;

public interface OrderApi {

    /**
     * 提交订单
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> submitOrder(BaseRequestDTO baseRequestDTO);

    /**
     * 查询订单详情
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> queryOrderDetail(BaseRequestDTO baseRequestDTO);

    /**
     * 查询配送信息
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> queryOrderTrack(BaseRequestDTO baseRequestDTO);

    /**
     * 确认收货
     *
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> confirmReceived(BaseRequestDTO baseRequestDTO);

    /**
     * 批量确认收货
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> batchConfirmReceived(BaseRequestDTO baseRequestDTO);

    /**
     * 查询配送预计送达时间
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> getPromiseTips(BaseRequestDTO baseRequestDTO);

    /**
     * 查询配送预计送达时间
     *
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> getMessage(BaseRequestDTO baseRequestDTO);

    /**
     * 查询配送预计送达时间
     *
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> delMessage(BaseRequestDTO baseRequestDTO);

    /**
     * 更新采购单号（跟京东沟通过，现在这接口用来推送送货单给京东那边）
     *
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> saveOrUpdatePoNo(BaseRequestDTO baseRequestDTO);
}
