package com.midea.cloud.srm.sou.brg.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.sou.brg.service.ExtNpmBrgService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * 备注
 * @author huangbf3
 */
@RestController
@Slf4j
@Api("竞价单单号控制器")
@RequestMapping("/sou/api/v1/npmbrg")
public class ExtNpmBrgController extends BaseController {
    @Autowired
    private ExtNpmBrgService extNpmBrgService;

    /**
     * 根据板块生成竞价: /sou/api/v1/npmbrg/
     * 根据板块生成竞价单号
     * @param invbuCode
     * @return
     */
    @GetMapping("/generateSeq")
    public String generateSeq(@RequestParam("invbuCode") String invbuCode) {
        try {
            return extNpmBrgService.generateSeq(invbuCode);
        } catch (Exception e) {
            log.error("generateSeq Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
