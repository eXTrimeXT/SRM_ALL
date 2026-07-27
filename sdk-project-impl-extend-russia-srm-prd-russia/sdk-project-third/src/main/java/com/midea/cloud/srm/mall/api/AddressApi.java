package com.midea.cloud.srm.mall.api;

import com.midea.cloud.srm.mall.common.ResultCode;
import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.MallResult;

public interface AddressApi {

    /**
     * 地址详情转换京东地址编码
     * @param addressRequestDTO 地址请求参数
     * @return 转换
     */
    MallResult<ResultCode, CommonResultDTO> getAddressFromAddress(BaseRequestDTO addressRequestDTO);


}