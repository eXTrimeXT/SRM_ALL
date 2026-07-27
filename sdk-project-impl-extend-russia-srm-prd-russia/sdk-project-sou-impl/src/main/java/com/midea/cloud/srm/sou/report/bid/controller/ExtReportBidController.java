package com.midea.cloud.srm.sou.report.bid.controller;

import com.github.pagehelper.PageInfo;
import com.midea.cloud.common.exception.BaseException;
import com.midea.cloud.common.utils.BeanMapUtils;
import com.midea.cloud.srm.model.common.BaseController;
import com.midea.cloud.srm.model.sou.report.bid.dto.ScheduleReportQueryDto;
import com.midea.cloud.srm.model.sou.report.bid.dto.SuperviseReportDto;
import com.midea.cloud.srm.model.sou.report.bid.dto.SuperviseReportQueryDto;
import com.midea.cloud.srm.model.sou.report.bid.dto.SuperviseReportToStrDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;
import com.midea.cloud.srm.sou.report.bid.schedule.service.ISccNpmSouScheduleService;
import com.midea.cloud.srm.sou.report.bid.service.ExtReportBidScheduleService;
import com.midea.cloud.srm.sou.report.bid.service.ExtReportBidSuperviseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Api("招标模块报表")
@RestController
@RequestMapping("/extReportBid")
@Slf4j
public class ExtReportBidController extends BaseController {

    @Autowired
    private ExtReportBidScheduleService extReportBidScheduleService;

    @Autowired
    private ExtReportBidSuperviseService extReportBidSuperviseService;

    @Autowired
    private ISccNpmSouScheduleService sccNpmSouScheduleService;

    /**
     * 进度表
     * @param query
     * @return
     */
    @ApiOperation("项目进度报表分页查询")
    @PostMapping("/bidSchedule/timerJobToRefreshScheduleReport")
    public PageInfo<ExtSouProject> timerJobToRefreshScheduleReport(@RequestBody ScheduleReportQueryDto query) {
        try {
            return sccNpmSouScheduleService.timerJobToRefreshReport(query);
        } catch (Exception e) {
            log.error("timerJobToRefreshScheduleReport Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 进度表
     * @param query
     * @return
     */
    @ApiOperation("项目进度报表分页查询")
    @PostMapping("/bidSchedule/listPage")
    public PageInfo<Map<String, Object>> bidScheduleListPage(@RequestBody ScheduleReportQueryDto query) {
        try {
            return extReportBidScheduleService.listPage(BeanMapUtils.beanToMap(query));
        } catch (Exception e) {
            log.error("bidScheduleListPage Exception", e);
            throw new BaseException(e.getMessage());
        }
    }

    /**
     * 上报监察报表
     * @param query
     * @return
     */
    @ApiOperation("上报监察报表分页查询")
    @PostMapping("/bidSupervise/listPage")
    public PageInfo<?> bidSuperviseListPage(@RequestBody SuperviseReportQueryDto query) {
        try {
            PageInfo pageInfo = extReportBidSuperviseService.listPage(BeanMapUtils.beanToMap(query));
            List<SuperviseReportToStrDto> dataList = extReportBidSuperviseService.convertStrList(pageInfo.getList());
            pageInfo.setList(dataList);
            return pageInfo;
        } catch (Exception e) {
            log.error("bidSuperviseListPage Exception", e);
            throw new BaseException(e.getMessage());
        }
    }
}
