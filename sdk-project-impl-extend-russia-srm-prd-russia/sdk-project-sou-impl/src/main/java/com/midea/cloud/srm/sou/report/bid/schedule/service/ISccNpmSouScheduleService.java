package com.midea.cloud.srm.sou.report.bid.schedule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.midea.cloud.srm.model.sou.report.bid.dto.ScheduleReportQueryDto;
import com.midea.cloud.srm.model.sou.report.souschedules.entity.SccNpmSouSchedule;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/03/12/ $
 * @Description: 招标项目进度报表接口
 */
public interface ISccNpmSouScheduleService extends IService<SccNpmSouSchedule> {

    /**
     * 分页查询近期有变化的招标项目
     * @param queryDto
     * @return
     */
    PageInfo<ExtSouProject> listPageAsChangeRecently(ScheduleReportQueryDto queryDto);

    /**
     * 生成报表数据
     * @param projectList
     * @return
     */
    List<SccNpmSouSchedule> generateScheduleReportData(List<ExtSouProject> projectList);

    /**
     * 保存或者更新报表数据
     * @param scheduleList
     * @return
     */
    List<SccNpmSouSchedule> saveOrUpdate(List<SccNpmSouSchedule> scheduleList);

    /**
     * 定时刷新进度报表任务接口
     * @param queryDto
     * @return
     */
    PageInfo<ExtSouProject> timerJobToRefreshReport(ScheduleReportQueryDto queryDto);
}
