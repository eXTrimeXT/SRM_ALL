package com.midea.cloud.srm.biz.pj.bidgroup.controller;

import com.midea.cloud.srm.biz.pj.bidgroup.service.ISccPjBidGroupService;
import com.midea.cloud.srm.model.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description scc_pj_bid_group
 * @author panmq
 * @date 2023-09-25
 */
@RestController
@Slf4j
@RequestMapping("/bid/group")
public class SccPjBidGroupController extends BaseController {

   @Autowired
    private ISccPjBidGroupService iSccPjBidGroupService;

}

