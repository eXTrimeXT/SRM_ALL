package com.midea.cloud.srm.sou.inq.ext.controller;

import cn.hutool.core.util.ObjectUtil;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.AssertUtils;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqCloseItemParams;
import com.midea.cloud.srm.model.extapi.sou.inq.dto.ExtInqSouSelectQueryDTO;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouSelectOrderItemVendorVO;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouSelectQueryVO;
import com.midea.cloud.srm.model.extapi.sou.inq.vo.ExtInqSouSelectionManagementVO;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouSelectEventService;
import com.midea.cloud.srm.sou.inq.ext.service.ExtInqSouSelectQueryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 长城 - 询比价 - 评选
 * @author huangbf3
 */
@Slf4j
@RestController
@RequestMapping("/npm/buyer/inq/select")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public class ExtWebInqSouSelectForBuyerController {

    @Autowired
    private ExtInqSouSelectQueryService extInqSouSelectQueryService;
    @Autowired
    private ExtInqSouSelectEventService extInqSouSelectEventService;

    /**
     * 查询询比价管理界面信息
     */
    @ApiOperation("查询询比价管理界面信息")
    @GetMapping("/getInqSelectManagementInfo/{projectId}")
    public ExtInqSouSelectionManagementVO getInqSelectManagementInfo(@PathVariable("projectId") Long projectId) {
        return extInqSouSelectQueryService.getInqSelectManagementInfo(projectId);
    }

    @ApiOperation("评选列表查询")
    @PostMapping("/queryItemSelectInfo")
    public ExtInqSouSelectQueryVO queryItemSelectInfo(@RequestBody ExtInqSouSelectQueryDTO queryParam) {
        return extInqSouSelectQueryService.queryItemSelectInfo(queryParam);
    }

    @ApiOperation("评选物料轮次供应商报价明细")
    @GetMapping("/queryOrderItemVendors")
    public List<ExtInqSouSelectOrderItemVendorVO> queryOrderItemVendors(Long souItemId, Integer round) {
        AssertUtils.notNull(souItemId, "缺少souItemId参数");
        AssertUtils.notNull(round, "缺少round参数");
        return extInqSouSelectQueryService.queryOrderItemVendors(souItemId, round);
    }

    @ApiOperation("评选列表信息导出")
    @PostMapping("/queryItemSelectInfo/downloadExcel")
    public void downLoadExcelForItemSelectInfo(@RequestBody ExtInqSouSelectQueryDTO queryParam, HttpServletResponse response) throws IOException {
        try {
            extInqSouSelectQueryService.downLoadExcelForItemSelectInfo(queryParam, response);
        } catch (Exception e) {
            log.error("downLoadExcelForItemSelectInfo Exception", e);
            throw new IOException(e);
        }
    }

    @ApiOperation("总价比价")
    @PostMapping("/totalPriceCompare/{projectId}")
    public void totalPriceCompare(@PathVariable("projectId") Long projectId) {
        extInqSouSelectEventService.totalPriceCompare(projectId);
    }

    @ApiOperation("结束询价")
    @PostMapping("/finishSou/{projectId}")
    public void finishSou(@PathVariable("projectId") Long projectId) {
        extInqSouSelectEventService.finishSou(projectId);
    }

    @ApiOperation("取消物料需求")
    @PostMapping("/closeSouItems")
    public void closeSouItems(@RequestBody Set<Long> souItemIds) {
        extInqSouSelectEventService.closeSouItems(souItemIds);
    }

    @ApiOperation("拟定时取消物料需求")
    @PostMapping("/draft/closeSouItems")
    public void draftCloseSouItems(@RequestBody Set<Long> souItemIds) {
        extInqSouSelectEventService.draftCloseSouItems(souItemIds);
    }

    @ApiOperation("关闭物料需求")
    @PostMapping("/closeSouItemsWithReason")
    public void closeSouItemsWithReason(@RequestBody ExtInqCloseItemParams params) {
        if(ObjectUtil.isNull(params.getSouItemId())){
            throw new BaseException("物料需求ID不能为空");
        }
        extInqSouSelectEventService.closeSouItems(params);
    }

}
