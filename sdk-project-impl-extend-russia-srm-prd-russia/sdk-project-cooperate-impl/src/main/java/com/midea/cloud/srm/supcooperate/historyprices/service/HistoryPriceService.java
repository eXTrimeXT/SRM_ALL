package com.midea.cloud.srm.supcooperate.historyprices.service;

import com.midea.cloud.srm.model.supcooperate.historyprices.dto.HistoryPriceApiRequestDto;
import com.midea.cloud.srm.model.supcooperate.historyprices.dto.HistoryPriceApiResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

/**
 * @Author: panmq
 * @Date: 2024/03/28/ $
 * @Description: 历史价格接口
 */
@Api("历史价格接口")
public interface HistoryPriceService {

    /**
     * receiveHistoryPrice
     * @param requestDto
     * @return
     */
    @ApiOperation("接收历史价格")
    HistoryPriceApiResponseDto receiveHistoryPrice(HistoryPriceApiRequestDto requestDto);
}
