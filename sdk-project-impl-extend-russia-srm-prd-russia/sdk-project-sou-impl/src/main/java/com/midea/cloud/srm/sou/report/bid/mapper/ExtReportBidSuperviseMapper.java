package com.midea.cloud.srm.sou.report.bid.mapper;

import com.midea.cloud.srm.model.sou.report.bid.dto.SuperviseReportDto;
import com.midea.cloud.srm.model.sou.report.bid.dto.SuperviseReportQueryDto;

import java.util.List;
import java.util.Map;

/**
 * @Description: for srm 上报监察报表Mapper
 *
 * @author srm
 * @date 2024-05-20
 */
public interface ExtReportBidSuperviseMapper {

    /**
     * listSuperviseReport
     * @param params
     * @return
     */
    List<SuperviseReportDto> listSuperviseReport(SuperviseReportQueryDto params);

}
