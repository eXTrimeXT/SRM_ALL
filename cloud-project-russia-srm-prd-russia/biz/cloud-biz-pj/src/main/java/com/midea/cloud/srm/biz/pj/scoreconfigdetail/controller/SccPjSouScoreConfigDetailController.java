package com.midea.cloud.srm.biz.pj.scoreconfigdetail.controller;

import com.midea.cloud.srm.biz.pj.scoreconfigdetail.service.ISccPjSouScoreConfigDetailService;
import com.midea.cloud.srm.model.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description
 * @author panmq
 * @date 2023-09-21
 */
@RestController
@Slf4j
@RequestMapping("/sou/scoreConfig")
public class SccPjSouScoreConfigDetailController extends BaseController {

   @Autowired
    private ISccPjSouScoreConfigDetailService iSccPjSouScoreConfigDetailService;

}

