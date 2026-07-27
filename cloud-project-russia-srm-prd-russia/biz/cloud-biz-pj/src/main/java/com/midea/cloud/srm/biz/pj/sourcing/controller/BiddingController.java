package com.midea.cloud.srm.biz.pj.sourcing.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.biz.pj.sourcing.dto.BiddingQueryDTO;
import com.midea.cloud.srm.biz.pj.sourcing.service.IBiddingService;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.pj.sou.bidding.entity.Bidding;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 *  竞价管理 前端控制器
 * </pre>
 *
 * @author yipeng@meiCloud.com
 * @version 1.00.00
 */
@RestController
@RequestMapping("/sou/bidding")
@Api(value = "BiddingController", tags = "竞价管理")
public class BiddingController extends BaseController {

    @Autowired
    private IBiddingService iBiddingService;

    /**
     * 分页查询
     *
     * @param biddingQueryDTO
     * @return
     */
    @PostMapping("/listPage")
    @ApiOperation("分页查询")
    public PageInfo<Bidding> listPage(@RequestBody BiddingQueryDTO biddingQueryDTO) {
        return iBiddingService.listPage(biddingQueryDTO);
    }


}
