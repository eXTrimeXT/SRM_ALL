package com.midea.cloud.srm.biz.pj.biddemand.controller;

import com.midea.cloud.srm.biz.pj.biddemand.service.ISccPjBidDemandService;
import com.midea.cloud.srm.model.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description scc_pj_bid_demand
 * @author panmq
 * @date 2023-09-25
 */
@RestController
@Slf4j
@RequestMapping("/bid/demand")
public class SccPjBidDemandController extends BaseController {

   @Autowired
    private ISccPjBidDemandService iSccPjBidDemandService;

}

