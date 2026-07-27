package com.midea.cloud.srm.sou.meiql.answer.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.sou.answer.dto.ReplayDTO;
import com.midea.cloud.srm.model.sou.answer.dto.SignReplayDTO;
import com.midea.cloud.srm.model.sou.openapi.sourcing.dto.order.ApiExtSouSignEditDto;
import com.midea.cloud.srm.model.sou.sourcing.enums.SouTypeEnum;
import com.midea.cloud.srm.sou.meiql.answer.service.AnswerSignService;
import com.midea.cloud.srm.sou.meiql.answer.util.SignUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 备注
 * @author huangbf3
 */
@Api("质疑澄清-签署")
@RestController
@Slf4j
@RequestMapping("/sou/answer")
public class AnswerSignController {
    @Autowired
    private AnswerSignService answerSignService;

    /**
     * 推送澄清回复附件电子签章数据
     * @param replayDTO
     * @return
     */
    @ApiOperation("推送澄清回复附件电子签章数据")
    @PostMapping("/pushSgin")
    SignReplayDTO pushSgin(@RequestBody ReplayDTO replayDTO) {
        try {
            return answerSignService.sign(replayDTO);
        } catch (Exception e) {
            log.error("bid vendor pushSgin Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
