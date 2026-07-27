package com.midea.cloud.srm.perf.ordercheck.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.utils.PageUtil;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.perf.ordercheck.dto.PerfScoreItemsOrderCheckDTO;
import com.midea.cloud.srm.model.perf.ordercheck.dto.PerfScoreItemsOrderCheckQueryDTO;
import com.midea.cloud.srm.model.perf.scoreproject.entity.PerfScoreItemsOrderCheck;
import com.midea.cloud.srm.perf.scoreproject.service.IPerfScoreItemsOrderCheckService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 * 订单化绩效复核 前端控制器</pre>
 * @author huangbf3
 *
 * <pre>
 */
@Api(value = "PerfScoreItemsOrderCheckController", tags = {"订单化绩效复核"})
@RestController
@RequestMapping("/pj/scoreItemsOrderCheck")
@Slf4j
public class PerfScoreItemsOrderCheckController extends BaseController {

    @Autowired
    private IPerfScoreItemsOrderCheckService perfScoreItemsOrderCheckService;

    @ApiOperation(value = "批量新增订单化绩效复核", notes = "批量新增订单化绩效复核")
    @PostMapping("/batchSaveOrderCheckList")
    public void batchSaveOrderCheckList(@RequestBody List<PerfScoreItemsOrderCheckDTO> perfScoreItemsOrderCheckDTOList) {
        perfScoreItemsOrderCheckService.batchSaveOrderCheckList(perfScoreItemsOrderCheckDTOList);
    }

    @ApiOperation(value = "列表查询", notes = "列表查询", httpMethod = "POST")
    @PostMapping("/listPage")
    public PageInfo<PerfScoreItemsOrderCheck> listPage(@RequestBody PerfScoreItemsOrderCheckQueryDTO queryDTO) {
        PageUtil.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        return new PageInfo<PerfScoreItemsOrderCheck>(perfScoreItemsOrderCheckService.listPage(queryDTO));
    }

    @ApiOperation(value = "查询详情", notes = "查询详情")
    @GetMapping("/getDetailById")
    public PerfScoreItemsOrderCheckDTO getDetailById(@RequestParam Long orderCheckId) {
        return perfScoreItemsOrderCheckService.getDetailById(orderCheckId);
    }

    @ApiOperation(value = "复核驳回", notes = "复核驳回")
    @PostMapping("/reject")
    public void reject(@RequestBody PerfScoreItemsOrderCheckDTO dto) {
        perfScoreItemsOrderCheckService.reject(dto);
    }

    @ApiOperation(value = "复核计算得分", notes = "复核计算得分")
    @GetMapping("/calcScore")
    public void calcScore(@RequestParam Long checkId) {
        perfScoreItemsOrderCheckService.calcScore(checkId);
    }

}
