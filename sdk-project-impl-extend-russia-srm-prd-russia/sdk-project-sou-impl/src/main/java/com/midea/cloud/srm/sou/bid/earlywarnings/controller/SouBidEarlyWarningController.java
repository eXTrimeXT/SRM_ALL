package com.midea.cloud.srm.sou.bid.earlywarnings.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.sou.bid.earlywarnings.service.SouBidEarlyWarningTimingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@RestController
@Slf4j
@RequestMapping("/earlyWarning")
public class SouBidEarlyWarningController extends BaseController {

    @Autowired
    private SouBidEarlyWarningTimingService souBidEarlyWarningTimingService;

    @PostMapping("/soubid/doWaring")
    public String doWaring(@RequestBody Map<String, Object> params) {
        try {
            return souBidEarlyWarningTimingService.doWaring();
        } catch (Exception e) {
            log.error("soubid doWaring Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
