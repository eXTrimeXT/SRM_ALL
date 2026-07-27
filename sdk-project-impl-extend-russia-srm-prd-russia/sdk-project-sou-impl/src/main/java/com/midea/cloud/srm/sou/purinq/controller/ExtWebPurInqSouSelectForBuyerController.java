package com.midea.cloud.srm.sou.purinq.controller;

import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.extapi.sou.purinq.dto.select.ExtPurInqSouSelectQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.select.ExtPurInqSouSelectOrderItemVendorVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.select.ExtPurInqSouSelectQueryVO;
import com.midea.cloud.srm.model.extapi.sou.purinq.vo.select.ExtPurInqSouSelectionManagementVO;
import com.midea.cloud.srm.sou.purinq.service.ExtPurInqSouSelectEventService;
import com.midea.cloud.srm.sou.purinq.service.ExtPurInqSouSelectQueryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 集采询比价 - 评选
 * @author 100014337
 */
@RestController
@RequestMapping("/npm/buyer/ext_pur_inq/select")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtWebPurInqSouSelectForBuyerController {

    @Autowired
    private ExtPurInqSouSelectQueryService extPurInqSouSelectQueryService;
    @Autowired
    private ExtPurInqSouSelectEventService extPurInqSouSelectEventService;

    /**
     * 查询询比价管理界面信息
     */
    @ApiOperation("查询询比价管理界面信息")
    @GetMapping("/getInqSelectManagementInfo/{projectId}")
    public ExtPurInqSouSelectionManagementVO getInqSelectManagementInfo(@PathVariable("projectId") Long projectId) {
        return extPurInqSouSelectQueryService.getInqSelectManagementInfo(projectId);
    }

    @ApiOperation("评选列表查询")
    @PostMapping("/queryItemSelectInfo")
    public ExtPurInqSouSelectQueryVO queryItemSelectInfo(@RequestBody ExtPurInqSouSelectQueryDTO queryParam) {
        return extPurInqSouSelectQueryService.queryItemSelectInfo(queryParam);
    }

    @ApiOperation("评选物料轮次供应商报价明细")
    @GetMapping("/queryOrderItemVendors")
    public List<ExtPurInqSouSelectOrderItemVendorVO> queryOrderItemVendors(Long souItemId, Integer round) {
        AssertUtils.notNull(souItemId, "缺少souItemId参数");
        AssertUtils.notNull(round, "缺少round参数");
        return extPurInqSouSelectQueryService.queryOrderItemVendors(souItemId, round);
    }

    @ApiOperation("评选列表信息导出")
    @PostMapping("/queryItemSelectInfo/downloadExcel")
    public void downLoadExcelForItemSelectInfo(@RequestBody ExtPurInqSouSelectQueryDTO queryParam, HttpServletResponse response) throws IOException {
        extPurInqSouSelectQueryService.downLoadExcelForItemSelectInfo(queryParam, response);
    }

    @ApiOperation("总价比价")
    @PostMapping("/totalPriceCompare/{projectId}")
    public void totalPriceCompare(@PathVariable("projectId") Long projectId) {
        extPurInqSouSelectEventService.totalPriceCompare(projectId);
    }

    @ApiOperation("结束询价")
    @PostMapping("/finishSou/{projectId}")
    public void finishSou(@PathVariable("projectId") Long projectId) {
        extPurInqSouSelectEventService.finishSou(projectId);
    }

}
