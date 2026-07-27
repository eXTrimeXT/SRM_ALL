package com.midea.cloud.srm.sou.report.bid.mapper;

import com.midea.cloud.srm.model.sou.report.bid.dto.ScheduleReportQueryDto;
import com.midea.cloud.srm.model.sou.report.souschedules.dto.SccNpmSouScheduleReportDto;
import com.midea.cloud.srm.model.sou.sourcing.entity.ExtSouProject;

import java.util.List;

/**
 * @Author: panmq
 * @Date: 2024/03/11/ $
 * @Description: 招标项目进度-mapper接口定义
 */
public interface ExtReportBidScheduleMapper {

    /**
     * 查询底表
     * @param queryDto
     * @return
     */
    List<SccNpmSouScheduleReportDto> listProjectPage(ScheduleReportQueryDto queryDto);

    /**
     * 查询最近有变化的招标单
     * @param queryDto
     * @return
     */
    List<ExtSouProject> listProjectAsChangeRecently(ScheduleReportQueryDto queryDto);
}
