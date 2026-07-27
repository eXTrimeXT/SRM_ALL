package com.midea.cloud.srm.supcooperate.historyprices.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.supcooperate.historyprices.dto.HistoryPriceApiRequestDto;
import com.midea.cloud.srm.model.supcooperate.historyprices.dto.HistoryPriceApiResponseDto;
import com.midea.cloud.srm.supcooperate.historyprices.service.HistoryPriceService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: panmq
 * @Date: 2024/03/28/ $
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/historyPrice/api")
public class HistoryPriceController extends BaseController {

    @Autowired
    private HistoryPriceService historyPriceService;


    @ApiOperation("接收历史价格")
    @PostMapping("/sendToSrm")
    HistoryPriceApiResponseDto receiveHistoryPrice(@RequestBody HistoryPriceApiRequestDto requestDto) {
        try {
            return historyPriceService.receiveHistoryPrice(requestDto);
        } catch (Exception e) {
            log.error("sendToSrm receiveHistoryPrice Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
