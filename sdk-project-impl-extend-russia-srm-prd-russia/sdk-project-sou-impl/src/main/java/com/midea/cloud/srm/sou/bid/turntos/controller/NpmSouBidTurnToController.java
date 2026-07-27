package com.midea.cloud.srm.sou.bid.turntos.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.sou.bidturns.dto.NpmSouBidTurnRquestParamDto;
import com.midea.cloud.srm.sou.bid.turntos.service.NpmSouBidTurnToService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: panmq
 * @Date: 2024/04/07/ $
 * @Description: 招标负责人转办控制类
 */
@RestController
@Slf4j
@Api("招标负责人转办控制类")
@RequestMapping("/bid/souTurn")
public class NpmSouBidTurnToController extends BaseController {

    @Autowired
    private NpmSouBidTurnToService npmSouBidTurnToService;

    /**
     * 转办责任人
     *
     * @param paramDto
     * @return
     */
    @ApiOperation("招标负责人转办")
    @PostMapping("/turnBidPricipal")
    Long turnBidPricipal(@RequestBody NpmSouBidTurnRquestParamDto paramDto) {
        try {
            return npmSouBidTurnToService.turnBidPricipal(paramDto);
        } catch (Exception e) {
            log.error("turnBidPricipal Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
