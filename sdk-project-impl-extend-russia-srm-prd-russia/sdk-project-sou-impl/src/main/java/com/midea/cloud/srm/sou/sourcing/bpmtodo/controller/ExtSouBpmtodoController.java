package com.midea.cloud.srm.sou.sourcing.bpmtodo.controller;

import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.sou.bpmtodo.dto.SouBpmtodoParam;
import com.midea.cloud.srm.model.sou.bpmtodo.dto.SouBpmtodoResponse;
import com.midea.cloud.srm.sou.sourcing.bpmtodo.service.ExtSouBpmtodoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author srm
 * @Description: for srm
 * @date 2024/5/31
 */
@RestController
@Slf4j
@Api("招标流程手机待办-控制类")
@RequestMapping("/bid/bpmtodo")
public class ExtSouBpmtodoController extends BaseController {

    @Autowired
    private ExtSouBpmtodoService extSouBpmtodoService;

    /**
     * 招标流程手机待办-商务标信息查询接口
     * @param param
     * @return
     */
    @PostMapping("/queryBusTodoInfo")
    public SouBpmtodoResponse queryBusTodoInfo(@RequestBody SouBpmtodoParam param) {
        try {
            return extSouBpmtodoService.queryBusTodoInfo(param);
        } catch (Exception e) {
            log.error("queryBusTodoInfo Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
