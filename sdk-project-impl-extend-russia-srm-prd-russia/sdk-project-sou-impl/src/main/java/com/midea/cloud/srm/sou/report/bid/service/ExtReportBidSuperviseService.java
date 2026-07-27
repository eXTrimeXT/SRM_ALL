package com.midea.cloud.srm.sou.report.bid.service;

import com.midea.cloud.srm.model.sou.report.bid.dto.SuperviseReportDto;
import com.midea.cloud.srm.model.sou.report.bid.dto.SuperviseReportToStrDto;
import com.midea.cloud.srm.sou.report.service.ExtReportService;
import io.swagger.annotations.Api;

import java.util.List;

/**
 * @Description: for srm
 *
 * @author srm
 * @date 2024-05-20
 */
@Api("招标报表-上报监察报表")
public interface ExtReportBidSuperviseService <T> extends ExtReportService<T> {

    /**
     * 属性值替换成字符串
     * @param dataList
     * @return
     */
    List<SuperviseReportToStrDto> convertStrList(List<SuperviseReportDto> dataList);
}
