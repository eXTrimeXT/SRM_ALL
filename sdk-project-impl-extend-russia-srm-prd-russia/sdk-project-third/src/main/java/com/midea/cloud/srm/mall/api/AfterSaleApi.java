package com.midea.cloud.srm.mall.api;

import com.midea.cloud.srm.mall.common.ResultCode;
import com.midea.cloud.srm.mall.request.base.BaseRequestDTO;
import com.midea.cloud.srm.mall.result.CommonResultDTO;
import com.midea.cloud.srm.mall.result.MallResult;

/**
 * 售后
 */
public interface AfterSaleApi {

    /**
     * 售后申请
     *
     * @param baseRequestDTO
     * @return
     */
    MallResult<ResultCode, CommonResultDTO> createAfsApply(BaseRequestDTO baseRequestDTO);
}
